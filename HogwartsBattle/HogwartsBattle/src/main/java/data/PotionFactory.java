package carte;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gestoreEffetti.Effetto;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Factory per creare oggetti Pozione da file JSON.
 * Gestisce il caricamento delle pozioni per Pack 2, 3 e 4.
 */
public class PotionFactory {
    
    private static final String POTION_JSON_PATH = "pozione.json";
    private static JsonObject potionData;
    
    static {
        loadPotionData();
    }
    
    /**
     * Carica i dati delle pozioni dal file JSON.
     */
    private static void loadPotionData() {
        try (FileReader reader = new FileReader(POTION_JSON_PATH)) {
            Gson gson = new Gson();
            potionData = gson.fromJson(reader, JsonObject.class);
        } catch (IOException e) {
            System.err.println("Errore nel caricamento di " + POTION_JSON_PATH + ": " + e.getMessage());
            potionData = new JsonObject();
        }
    }
    
    /**
     * Ottiene una pozione specifica tramite ID.
     */
    public static Pozione getPotionById(String id) {
        if (potionData == null) {
            loadPotionData();
        }
        
        // Cerca in tutti i pack
        for (String packKey : potionData.keySet()) {
            JsonArray potions = potionData.getAsJsonArray(packKey);
            for (JsonElement element : potions) {
                JsonObject potionObj = element.getAsJsonObject();
                if (potionObj.get("id").getAsString().equals(id)) {
                    return parsePozione(potionObj);
                }
            }
        }
        
        System.err.println("Pozione non trovata: " + id);
        return null;
    }
    
    /**
     * Ottiene tutte le pozioni di un pack specifico.
     */
    public static List<Pozione> getPotionsForPack(int pack) {
        if (potionData == null) {
            loadPotionData();
        }
        
        List<Pozione> pozioni = new ArrayList<>();
        String packKey = "pack" + pack;
        
        if (potionData.has(packKey)) {
            JsonArray potions = potionData.getAsJsonArray(packKey);
            for (JsonElement element : potions) {
                Pozione pozione = parsePozione(element.getAsJsonObject());
                if (pozione != null) {
                    pozioni.add(pozione);
                }
            }
        }
        
        return pozioni;
    }
    
    /**
     * Ottiene tutte le pozioni disponibili.
     */
    public static List<Pozione> getAllPotions() {
        if (potionData == null) {
            loadPotionData();
        }
        
        List<Pozione> allPotions = new ArrayList<>();
        
        for (String packKey : potionData.keySet()) {
            JsonArray potions = potionData.getAsJsonArray(packKey);
            for (JsonElement element : potions) {
                Pozione pozione = parsePozione(element.getAsJsonObject());
                if (pozione != null) {
                    allPotions.add(pozione);
                }
            }
        }
        
        return allPotions;
    }
    
    /**
     * Parse un oggetto JSON in una Pozione.
     */
    private static Pozione parsePozione(JsonObject json) {
        try {
            String nome = json.get("nome").getAsString();
            String id = json.get("id").getAsString();
            String classe = "Pozione";
            int pack = json.get("pack").getAsInt();
            
            // Path immagine
            String pathImmagine = json.has("path-img") ? 
                                  json.get("path-img").getAsString() : 
                                  "/Immagini_carte/Pozioni/Pack" + pack + "/" + nome + ".png";
            
            // Ingredienti richiesti
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
            
            // Descrizioni effetti
            String descrizioneEffettoNormale = json.has("descrizioneEffettoNormale") ? 
                                               json.get("descrizioneEffettoNormale").getAsString() : "";
            String descrizioneEffettoBanish = json.has("descrizioneEffettoBanish") ? 
                                              json.get("descrizioneEffettoBanish").getAsString() : "";
            
            // Effetti normali
            List<Effetto> effettoNormale = new ArrayList<>();
            if (json.has("effettoNormale")) {
                JsonArray effettiArray = json.getAsJsonArray("effettoNormale");
                effettoNormale = CardFactory.parseEffetti(effettiArray);
            }
            
            // Effetti banish
            List<Effetto> effettoBanish = new ArrayList<>();
            if (json.has("effettoBanish")) {
                JsonArray effettiArray = json.getAsJsonArray("effettoBanish");
                effettoBanish = CardFactory.parseEffetti(effettiArray);
            }
            
            // Flag Dark Arts Potion (per Pack 3+)
            boolean isDarkArtsPotion = json.has("isDarkArtsPotion") && 
                                      json.get("isDarkArtsPotion").getAsBoolean();
            
            // Crea la pozione
            return new Pozione(
                nome,
                id,
                classe,
                descrizioneEffettoNormale + " | BANISH: " + descrizioneEffettoBanish,
                0, // costo
                pathImmagine,
                pack,
                ingredientiRichiesti,
                descrizioneEffettoNormale,
                effettoNormale,
                descrizioneEffettoBanish,
                effettoBanish,
                isDarkArtsPotion
            );
            
        } catch (Exception e) {
            System.err.println("Errore nel parsing della pozione: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Reload dei dati (utile per testing o hot-reload).
     */
    public static void reload() {
        loadPotionData();
    }
}
