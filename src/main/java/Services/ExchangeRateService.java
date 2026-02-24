package Services;

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

    private static final String API_KEY = "f25aa63044239735a3f9bfa9";
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/";

    private Map<String, Double> tauxCache = new HashMap<>();
    private String baseDeviseActuelle = "";
    private long dernierCacheTime = 0;
    private static final long CACHE_DURATION = 3600000; // 1 heure en millisecondes


    public double getTaux(String de, String vers) throws Exception {
        // On recharge si le cache est vide, expiré, ou si la devise de base a changé
        if (tauxCache.isEmpty()
                || !de.equals(baseDeviseActuelle)
                || (System.currentTimeMillis() - dernierCacheTime > CACHE_DURATION)) {
            chargerTaux(de);
        }

        Double taux = tauxCache.get(vers);
        if (taux == null) {
            throw new Exception("Devise non supportée : " + vers);
        }
        return taux;
    }


    public double convertir(double montant, String de, String vers) throws Exception {
        double taux = getTaux(de, vers);
        return montant * taux;
    }

    public Map<String, Double> convertirToutesDevises(double montantDT) throws Exception {
        Map<String, Double> resultats = new HashMap<>();

        // Conversion directe TND -> USD
        double usd = convertir(montantDT, "TND", "USD");
        resultats.put("USD", usd);

        // Conversion directe TND -> EUR (plus précis que de passer par l'USD)
        double eur = convertir(montantDT, "TND", "EUR");
        resultats.put("EUR", eur);

        return resultats;
    }


    private void chargerTaux(String baseDevise) throws Exception {
        String url_str = BASE_URL + baseDevise;

        try {
            URL url = new URL(url_str);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.setRequestMethod("GET");
            request.connect();

            int responseCode = request.getResponseCode();
            if (responseCode != 200) {
                throw new Exception("Erreur HTTP : " + responseCode);
            }

            JsonElement root = JsonParser.parseReader(new InputStreamReader((InputStream) request.getContent()));
            JsonObject jsonobj = root.getAsJsonObject();

            String result = jsonobj.get("result").getAsString();
            if (!"success".equals(result)) {
                throw new Exception("Erreur API : " + result);
            }

            JsonObject rates = jsonobj.getAsJsonObject("conversion_rates");

            // Mise à jour du cache
            tauxCache.clear();
            for (String key : rates.keySet()) {
                tauxCache.put(key, rates.get(key).getAsDouble());
            }

            this.baseDeviseActuelle = baseDevise;
            this.dernierCacheTime = System.currentTimeMillis();

        } catch (Exception e) {
            throw new Exception("Impossible de contacter le service de change : " + e.getMessage());
        }
    }
}