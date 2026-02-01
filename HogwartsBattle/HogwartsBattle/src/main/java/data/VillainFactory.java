package data;

import carte.*;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import carte.TipoSconfittaMalvagio1;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Factory per creare malvagi da JSON.
 * AGGIORNATO per espansione Charms & Potions:
 * - Supporto TipoSconfittaMalvagio (Attacco, Influenza, o entrambi)
 * - Supporto Inferi (Villain-Creature)
 * - Supporto Lord Voldemort multipli
 */
public class VillainFactory {
	private static Map<String, JsonObject> registroMalvagio = new HashMap<>();
    private static Boolean inizializzata = false;
    
    public static void inizializza() {
    	if(inizializzata == true) {
    		return;
    	}
    	
    	caricaMalvagi("malvagio.json");
    	inizializzata = true;
    }
    
    private static void caricaMalvagi(String nomeFile) {
    	try (Reader reader = new InputStreamReader(
                VillainFactory.class.getClassLoader().getResourceAsStream("json/" + nomeFile))){
    		
    		JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            
            // Per ogni gioco (gioco1, gioco2, ..., gioco7)
            for (String giocoKey : root.keySet()) {
                com.google.gson.JsonArray malvagiArray = root.getAsJsonArray(giocoKey);
                
                // Per ogni malvagio nell'array
                for (int i = 0; i < malvagiArray.size(); i++) {
                    JsonObject malvagioObj = malvagiArray.get(i).getAsJsonObject();
                    String id = malvagioObj.get("id").getAsString();
                    registroMalvagio.put(id, malvagioObj);
                }
            }
            
    	} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public static Malvagio creaMalvagio(String idMalvagio) {
    	if(inizializzata == false) {
    		inizializza();
    	}
    	
    	JsonObject data = registroMalvagio.get(idMalvagio);
    	
    	if(data == null) {
    		throw new IllegalArgumentException("Malvagio non trovato: " + idMalvagio);
    	}
    	
    	return parseMalvagio(data);
    }
    
    /**
     * Parse un JsonObject in un oggetto Malvagio.
     * Supporta tutti i nuovi campi dell'espansione.
     */
    private static Malvagio parseMalvagio(JsonObject json) {
        String nome = json.get("nome").getAsString();
        String id = json.get("id").getAsString();
        String classe = json.get("class").getAsString();
        String descrizione = json.has("Descrizione") ? json.get("Descrizione").getAsString() : "";
        int costo = json.has("costo") ? json.get("costo").getAsInt() : 0;
        String pathImmagine = json.has("path-img") ? json.get("path-img").getAsString() : "";
        int vita = json.get("vita").getAsInt();
        
        // Parse effetti e triggers usando CardFactory
        List<gestoreEffetti.Effetto> effetti = CardFactory.parseEffetti(
            json.has("effetti") ? json.getAsJsonArray("effetti") : new com.google.gson.JsonArray()
        );
        
        List<gestoreEffetti.Trigger> triggers = CardFactory.parseTriggers(
            json.has("triggers") ? json.getAsJsonArray("triggers") : new com.google.gson.JsonArray()
        );
        
        List<gestoreEffetti.Effetto> reward = CardFactory.parseEffetti(
            json.has("reward") ? json.getAsJsonArray("reward") : new com.google.gson.JsonArray()
        );
        
        // NUOVO: Parse tipo sconfitta (default = SOLO_ATTACCO per compatibilità)
        TipoSconfittaMalvagio tipoSconfitta = TipoSconfittaMalvagio.SOLO_ATTACCO;
        if (json.has("tipoSconfitta")) {
            try {
                tipoSconfitta = TipoSconfittaMalvagio.valueOf(json.get("tipoSconfitta").getAsString());
            } catch (IllegalArgumentException e) {
                System.err.println("TipoSconfitta non riconosciuto per " + id + ", uso default SOLO_ATTACCO");
            }
        }
        
        // Crea il malvagio con tipo sconfitta
        Malvagio malvagio = new Malvagio(nome, id, classe, descrizione, costo, pathImmagine, 
                                         effetti, triggers, reward, vita, tipoSconfitta);
        
        // NUOVO: Campi Pack 3-4
        if (json.has("isCreature") && json.get("isCreature").getAsBoolean()) {
            malvagio.setIsCreature(true);
        }
        
        if (json.has("isLordVoldemort") && json.get("isLordVoldemort").getAsBoolean()) {
            malvagio.setIsLordVoldemort(true);
        }
        
        if (json.has("isFinalBoss") && json.get("isFinalBoss").getAsBoolean()) {
            malvagio.setIsFinalBoss(true);
        }
        
        return malvagio;
    }
    
    /**
     * Reload dei dati (utile per testing).
     */
    public static void reload() {
        registroMalvagio.clear();
        inizializzata = false;
        inizializza();
    }
}
