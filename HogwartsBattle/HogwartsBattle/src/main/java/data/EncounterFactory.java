package data;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import carte.Encounter;
import gestoreEffetti.TipoCondizioneEncounter;
import gestoreEffetti.Effetto;
import gestoreEffetti.Trigger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Factory per creare Encounter da file JSON.
 * Gestisce il caricamento degli Encounter per Pack 1-4.
 * 
 * Sistema Encounter - Pack 1-4
 */
public class EncounterFactory {
    private static Map<String, Encounter> encounterCache = new HashMap<>();
    
    /**
     * Carica tutti gli encounter dal file JSON.
     */
    public static void caricaEncounter() {
        String path = "json/encounter.json";
        
        try (InputStream is = EncounterFactory.class.getClassLoader().getResourceAsStream(path);
             Reader reader = new InputStreamReader(is)) {
            
            Gson gson = new Gson();
            JsonObject root = gson.fromJson(reader, JsonObject.class);
            
            // Carica tutti gli encounter divisi per pack
            for (String packKey : root.keySet()) {
                JsonArray encounterArray = root.getAsJsonArray(packKey);
                
                for (JsonElement element : encounterArray) {
                    JsonObject encJson = element.getAsJsonObject();
                    Encounter enc = parseEncounter(encJson);
                    if (enc != null) {
                        encounterCache.put(enc.getId(), enc);
                    }
                }
            }
            
            System.out.println("✅ Caricati " + encounterCache.size() + " encounter");
            
        } catch (Exception e) {
            System.err.println("❌ Errore nel caricamento degli encounter: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Parsa un Encounter dal JSON.
     * 
     * @param json Oggetto JSON da parsare
     * @return Encounter creato o null in caso di errore
     */
    private static Encounter parseEncounter(JsonObject json) {
        try {
            Gson gson = new Gson();
            
            String nome = json.get("nome").getAsString();
            String id = json.get("id").getAsString();
            int pack = json.get("pack").getAsInt();
            int ordine = json.get("ordine").getAsInt();
            
            // Malvagi associati
            List<String> malvagiAssociati = new ArrayList<>();
            if (json.has("malvagiAssociati")) {
                JsonArray malvagiArray = json.getAsJsonArray("malvagiAssociati");
                for (JsonElement elem : malvagiArray) {
                    malvagiAssociati.add(elem.getAsString());
                }
            }
            
            String descrizioneEffetto = json.get("descrizioneEffetto").getAsString();
            
            // Effetti continui
            List<Effetto> effettiContinui = new ArrayList<>();
            if (json.has("effettiContinui")) {
                JsonArray effettiArray = json.getAsJsonArray("effettiContinui");
                Type effettiType = new TypeToken<List<Effetto>>(){}.getType();
                effettiContinui = gson.fromJson(effettiArray, effettiType);
            }
            
            String descrizioneCompletamento = json.get("descrizioneCompletamento").getAsString();
            String tipoCondizioneStr = json.get("tipoCondizione").getAsString();
            TipoCondizioneEncounter tipoCondizione = TipoCondizioneEncounter.valueOf(tipoCondizioneStr);
            int valoreRichiesto = json.get("valoreRichiesto").getAsInt();
            
            String descrizioneRicompensa = json.get("descrizioneRicompensa").getAsString();
            
            // Reward
            List<Effetto> reward = new ArrayList<>();
            if (json.has("reward")) {
                JsonArray rewardArray = json.getAsJsonArray("reward");
                Type rewardType = new TypeToken<List<Effetto>>(){}.getType();
                reward = gson.fromJson(rewardArray, rewardType);
            }
            
            String pathImg = json.has("pathImg") ? json.get("pathImg").getAsString() : "";
            
            // Crea l'Encounter usando il costruttore di Carta
            Encounter encounter = new Encounter(
                nome, id, "Encounter", descrizioneEffetto, 0, pathImg,
                effettiContinui, new ArrayList<Trigger>(),
                pack, ordine, malvagiAssociati,
                descrizioneCompletamento, tipoCondizione, valoreRichiesto,
                descrizioneRicompensa, reward
            );
            
            return encounter;
            
        } catch (Exception e) {
            System.err.println("❌ Errore nel parsing dell'Encounter: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Ottiene un Encounter specifico dal suo ID.
     * 
     * @param id ID dell'encounter da cercare
     * @return Copia dell'Encounter trovato o null se non esiste
     */
    public static Encounter getEncounterById(String id) {
        if (encounterCache.isEmpty()) {
            caricaEncounter();
        }
        
        Encounter original = encounterCache.get(id);
        if (original == null) {
            System.err.println("❌ Encounter con ID " + id + " non trovato!");
            return null;
        }
        
        // Restituisce una copia per evitare modifiche all'originale
        return copiaEncounter(original);
    }
    
    /**
     * Carica gli encounter per un pack specifico.
     * 
     * @param pack Numero del pack (1, 2, 3, 4)
     * @return Lista di Encounter del pack, ordinati per ordine
     */
    public static List<Encounter> getEncounterPerPack(int pack) {
        if (encounterCache.isEmpty()) {
            caricaEncounter();
        }
        
        List<Encounter> result = new ArrayList<>();
        for (Encounter enc : encounterCache.values()) {
            if (enc.getPack() == pack) {
                result.add(copiaEncounter(enc));
            }
        }
        
        // Ordina per ordine (1 of 3, 2 of 3, 3 of 3)
        result.sort((e1, e2) -> Integer.compare(e1.getOrdine(), e2.getOrdine()));
        
        return result;
    }
    
    /**
     * Crea una copia di un Encounter per evitare modifiche all'originale.
     * 
     * @param original Encounter originale da copiare
     * @return Nuova istanza di Encounter
     */
    private static Encounter copiaEncounter(Encounter original) {
        Encounter copia = new Encounter(
            original.getNome(),
            original.getId(),
            original.getClasse(),
            original.getDescrizione(),
            original.getCosto(),
            original.getPathImmagine(),
            new ArrayList<>(original.getEffetti()),
            new ArrayList<>(original.getTriggers()),
            original.getPack(),
            original.getOrdine(),
            new ArrayList<>(original.getMalvagiAssociati()),
            original.getDescrizioneCompletamento(),
            original.getTipoCondizione(),
            original.getValoreRichiesto(),
            original.getDescrizioneRicompensa(),
            new ArrayList<>(original.getReward())
        );
        
        // Reset stato
        copia.setProgressoAttuale(0);
        copia.setCompletato(false);
        copia.setRewardUsata(false);
        
        return copia;
    }
}
