package service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ExchangeRateService {

    private static final String API_KEY = "f25aa63044239735a3f9bfa9"; // Remplace par ta vraie clé
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/";

    private Map<String, Double> tauxCache = new HashMap<>();
    private long dernierCacheTime = 0;
    private static final long CACHE_DURATION = 3600000; // 1 heure


    public double getTaux(String de, String vers) throws Exception {
        if (tauxCache.isEmpty() || System.currentTimeMillis() - dernierCacheTime > CACHE_DURATION) {
            chargerTaux(de);
        }

        Double taux = tauxCache.get(vers);
        if (taux == null) {
            throw new Exception("Devise non supportée: " + vers);
        }
        return taux;
    }


    public double convertir(double montant, String de, String vers) throws Exception {
        double taux = getTaux(de, vers);
        return montant * taux;
    }


    public Map<String, Double> convertirToutesDevises(double montantDT) throws Exception {
        Map<String, Double> resultats = new HashMap<>();

        double tauxTNDUSD = getTaux("TND", "USD");
        double usd = montantDT * tauxTNDUSD;
        resultats.put("USD", usd);

        double tauxUSDEUR = getTaux("USD", "EUR");
        double eur = usd * tauxUSDEUR;
        resultats.put("EUR", eur);

        return resultats;
    }

    private void chargerTaux(String baseDevise) throws Exception {
        String url_str = BASE_URL + baseDevise;

        try {
            URL url = new URL(url_str);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.connect();

            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
            JsonObject jsonobj = root.getAsJsonObject();

            String result = jsonobj.get("result").getAsString();
            if (!"success".equals(result)) {
                throw new Exception("Erreur API: " + result);
            }

            JsonObject rates = jsonobj.getAsJsonObject("conversion_rates");

            tauxCache.clear();
            for (String key : rates.keySet()) {
                tauxCache.put(key, rates.get(key).getAsDouble());
            }

            dernierCacheTime = System.currentTimeMillis();

        } catch (Exception e) {
            throw new Exception("Erreur lors du chargement des taux: " + e.getMessage());
        }
    }
}