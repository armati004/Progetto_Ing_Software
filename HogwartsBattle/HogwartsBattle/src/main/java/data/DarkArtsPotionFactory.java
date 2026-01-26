package carte;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gestoreEffetti.Effetto;
import gestoreEffetti.Trigger;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Factory per creare oggetti DarkArtsPotion da file JSON.
 * Gestisce il caricamento delle Dark Arts Potions per Pack 3 e 4.
 */
public class DarkArtsPotionFactory {
    
    private static final String DARK_ARTS_POTION_JSON_PATH = "dark_arts_potion.json";
    private static JsonObject darkArtsPotionData;
    
    static {
        loadDarkArtsPotionData();
    }
    
    /**
     * Carica i dati delle Dark Arts Potions dal file JSON.
     */
    private static void loadDarkArtsPotionData() {
        try (FileReader reader = new FileReader(DARK_ARTS_POTION_JSON_PATH)) {
            Gson gson = new Gson();
            darkArtsPotionData = gson.fromJson(reader, JsonObject.class);
        } catch (IOException e) {
            System.err.println("Errore nel caricamento di " + DARK_ARTS_POTION_JSON_PATH + ": " + e.getMessage());
            darkArtsPotionData = new JsonObject();
        }
    }
    
    /**
     * Ottiene una Dark Arts Potion specifica tramite ID.
     */
    public static DarkArtsPotion getDarkArtsPotionById(String id) {
        if (darkArtsPotionData == null) {
            loadDarkArtsPotionData();
        }
        
        // Cerca in tutti i pack
        for (String packKey : darkArtsPotionData.keySet()) {
            JsonArray potions = darkArtsPotionData.getAsJsonArray(packKey);
            for (JsonElement element : potions) {
                JsonObject potionObj = element.getAsJsonObject();
                if (potionObj.get("id").getAsString().equals(id)) {
                    return parseDarkArtsPotion(potionObj);
                }
            }
        }
        
        System.err.println("Dark Arts Potion non trovata: " + id);
        return null;
    }
    
    /**
     * Ottiene tutte le Dark Arts Potions di un pack specifico.
     */
    public static List<DarkArtsPotion> getDarkArtsPotionsForPack(int pack) {
        if (darkArtsPotionData == null) {
            loadDarkArtsPotionData();
        }
        
        List<DarkArtsPotion> pozioni = new ArrayList<>();
        String packKey = "pack" + pack;
        
        if (darkArtsPotionData.has(packKey)) {
            JsonArray potions = darkArtsPotionData.getAsJsonArray(packKey);
            for (JsonElement element : potions) {
                DarkArtsPotion pozione = parseDarkArtsPotion(element.getAsJsonObject());
                if (pozione != null) {
                    pozioni.add(pozione);
                }
            }
        }
        
        return pozioni;
    }
    
    /**
     * Ottiene tutte le Dark Arts Potions disponibili.
     */
    public static List<DarkArtsPotion> getAllDarkArtsPotions() {
        if (darkArtsPotionData == null) {
            loadDarkArtsPotionData();
        }
        
        List<DarkArtsPotion> allPotions = new ArrayList<>();
        
        for (String packKey : darkArtsPotionData.keySet()) {
            JsonArray potions = darkArtsPotionData.getAsJsonArray(packKey);
            for (JsonElement element : potions) {
                DarkArtsPotion pozione = parseDarkArtsPotion(element.getAsJsonObject());
                if (pozione != null) {
                    allPotions.add(pozione);
                }
            }
        }
        
        return allPotions;
    }
    
    /**
     * Parse un oggetto JSON in una DarkArtsPotion.
     */
    private static DarkArtsPotion parseDarkArtsPotion(JsonObject json) {
        try {
            String nome = json.get("nome").getAsString();
            String id = json.get("id").getAsString();
            String classe = "DarkArtsPotion";
            String descrizione = json.get("descrizione").getAsString();
            int pack = json.has("pack") ? json.get("pack").getAsInt() : 3;
            
            // Path immagine
            String pathImmagine = json.has("path-img") ? 
                                  json.get("path-img").getAsString() : 
                                  "/Immagini_carte/DarkArtsPotions/Pack" + pack + "/" + nome + ".png";
            
            // Ingredienti richiesti per completare (brewre) la pozione
            List<TipoIngrediente> ingredientiRichiesti = new ArrayList<>();
            if (json.has("ingredientiRichiesti")) {
                JsonArray ingredientiArray = json.getAsJsonArray("ingredientiRichiesti");
                for (JsonElement ing : ingredientiArray) {
                    try {
                        TipoIngrediente tipo = TipoIngrediente.valueOf(ing.getAsString());
                        ingredientiRichiesti.add(tipo);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Ingrediente non riconosciuto: " + ing.getAsString());
                    }
                }
            }
            
            // Effetti ongoing (si risolvono ogni turno)
            List<Effetto> effetti = new ArrayList<>();
            if (json.has("effetti")) {
                JsonArray effettiArray = json.getAsJsonArray("effetti");
                effetti = CardFactory.parseEffetti(effettiArray);
            }
            
            // Triggers (opzionali)
            List<Trigger> triggers = new ArrayList<>();
            if (json.has("triggers")) {
                JsonArray triggersArray = json.getAsJsonArray("triggers");
                triggers = CardFactory.parseTriggers(triggersArray);
            }
            
            // Crea la Dark Arts Potion
            return new DarkArtsPotion(
                nome,
                id,
                classe,
                descrizione,
                0, // costo
                pathImmagine,
                effetti,
                triggers,
                ingredientiRichiesti
            );
            
        } catch (Exception e) {
            System.err.println("Errore nel parsing della Dark Arts Potion: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Reload dei dati (utile per testing o hot-reload).
     */
    public static void reload() {
        loadDarkArtsPotionData();
    }
}
