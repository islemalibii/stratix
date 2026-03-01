package api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.util.Random;

public class QuoteAPI {

    private static final String API_URL = "https://type.fit/api/quotes";
    private static JsonArray quotes = null;
    private static long lastFetch = 0;
    private static final long CACHE_DURATION = 3600000; // 1 heure

    /**
     * Récupère une citation aléatoire
     */
    public static String getRandomQuote() {
        try {
            // Rafraîchir le cache toutes les heures
            if (quotes == null || System.currentTimeMillis() - lastFetch > CACHE_DURATION) {
                fetchQuotes();
            }

            if (quotes != null && quotes.size() > 0) {
                Random rand = new Random();
                JsonObject quote = quotes.get(rand.nextInt(quotes.size())).getAsJsonObject();

                String text = quote.get("text").getAsString();
                String author = quote.has("author") && !quote.get("author").isJsonNull()
                        ? quote.get("author").getAsString()
                        : "Inconnu";

                return String.format("“%s” — %s", text, author);
            }

        } catch (Exception e) {
            System.err.println("❌ Erreur API Citations: " + e.getMessage());
        }

        // Citations par défaut en cas d'erreur
        return getFallbackQuote();
    }

    /**
     * Récupère une citation par catégorie (travail, succès, équipe)
     */
    public static String getQuoteByCategory(String category) {
        // Catégories simulées car l'API ne les supporte pas
        String[][] categorizedQuotes = {
                // Travail
                {
                        "“Choisissez un travail que vous aimez et vous n'aurez pas à travailler un seul jour de votre vie.” — Confucius",
                        "“Le travail éloigne de nous trois grands maux : l'ennui, le vice et le besoin.” — Voltaire",
                        "“Le succès c'est d'aller d'échec en échec sans perdre son enthousiasme.” — Winston Churchill"
                },
                // Équipe
                {
                        "“Seul on va plus vite, ensemble on va plus loin.” — Proverbe africain",
                        "“Le travail d'équipe est la capacité de travailler ensemble vers une vision commune.” — Andrew Carnegie",
                        "“L'union fait la force.” — Ésope"
                },
                // Succès
                {
                        "“Le succès n'est pas la clé du bonheur. Le bonheur est la clé du succès.” — Albert Schweitzer",
                        "“Le succès, c'est se promener de échecs en échecs tout en restant motivé.” — Winston Churchill",
                        "“80% du succès est de se montrer.” — Woody Allen"
                }
        };

        int index = 0;
        if ("travail".equalsIgnoreCase(category)) index = 0;
        else if ("equipe".equalsIgnoreCase(category) || "équipe".equalsIgnoreCase(category)) index = 1;
        else if ("succes".equalsIgnoreCase(category) || "succès".equalsIgnoreCase(category)) index = 2;

        Random rand = new Random();
        return categorizedQuotes[index][rand.nextInt(categorizedQuotes[index].length)];
    }

    private static void fetchQuotes() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("User-Agent", "Stratix-App")
                    .build();

            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                quotes = JsonParser.parseString(response.body()).getAsJsonArray();
                lastFetch = System.currentTimeMillis();
                System.out.println("✅ Citations chargées: " + quotes.size());
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur chargement citations: " + e.getMessage());
        }
    }

    private static String getFallbackQuote() {
        String[] fallback = {
                "“Le succès c'est d'aller d'échec en échec sans perdre son enthousiasme.” — Winston Churchill",
                "“Le travail d'équipe est la capacité de travailler ensemble vers une vision commune.” — Andrew Carnegie",
                "“La qualité n'est jamais un accident, c'est toujours le résultat d'un effort intelligent.” — John Ruskin",
                "“Le secret pour arriver est de commencer.” — Mark Twain",
                "“Ne rêvez pas votre vie, vivez vos rêves.” — Inconnu"
        };
        return fallback[new Random().nextInt(fallback.length)];
    }

    /**
     * Rafraîchit manuellement les citations
     */
    public static void refreshQuotes() {
        quotes = null;
        fetchQuotes();
    }
}