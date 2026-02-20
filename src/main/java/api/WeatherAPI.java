package api;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

public class WeatherAPI {

    // Ta clé API de weatherapi.com
    private static final String API_KEY = "2ef71e869a794c86ac3225754262002";
    private static final String BASE_URL = "http://api.weatherapi.com/v1";

    /**
     * Récupère la météo pour une date et une ville
     */
    public static String getWeatherForDate(String date, String city) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/forecast.json?key=" + API_KEY +
                            "&q=" + city + "&dt=" + date + "&lang=fr"))
                    .build();

            System.out.println("📡 Appel API: " + request.uri());

            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
                JsonObject forecast = json.getAsJsonObject("forecast");
                JsonArray forecastday = forecast.getAsJsonArray("forecastday");
                JsonObject day = forecastday.get(0).getAsJsonObject().getAsJsonObject("day");
                JsonObject condition = day.getAsJsonObject("condition");

                String conditionText = condition.get("text").getAsString();
                double temp = day.get("avgtemp_c").getAsDouble();

                return conditionText + " - " + temp + "°C";
            } else {
                return "Erreur API (" + response.statusCode() + ")";
            }

        } catch (Exception e) {
            System.err.println("❌ Erreur API: " + e.getMessage());
            return getMockWeather(date); // Fallback
        }
    }

    /**
     * Version simulée (fallback)
     */
    public static String getMockWeather(String date) {
        try {
            int day = Integer.parseInt(date.substring(8));
            if (day % 3 == 0) return "☀️ Ensoleillé - 22°C";
            if (day % 3 == 1) return "☁️ Nuageux - 18°C";
            return "🌧️ Pluvieux - 15°C";
        } catch (Exception e) {
            return "☀️ Ensoleillé - 22°C";
        }
    }

    /**
     * Version avec ville par défaut (Tunis)
     */
    public static String getWeatherForDate(String date) {
        return getWeatherForDate(date, "Tunis");
    }
}