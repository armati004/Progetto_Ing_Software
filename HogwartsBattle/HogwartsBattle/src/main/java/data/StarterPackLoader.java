package data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import carte.Carta;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StarterPackLoader {
    // Mappa: "harry" -> ["edvige0", "firebolt0", ...]
    private static Map<String, List<String>> starterDeckMap = new HashMap<>();
    private static boolean inizializzato = false;

    public static void inizializza() {
        if (inizializzato) return;

        // Assicurati che il path sia corretto per le risorse
        String path = "/json/starter_pack.json";
        
        try (Reader reader = new InputStreamReader(StarterPackLoader.class.getResourceAsStream(path))) {
            if (reader == null) {
                System.err.println("CRITICO: starter_pack.json non trovato in " + path);
                return;
            }

            Gson gson = new Gson();
            // Leggiamo la struttura { "harry": [carta1, carta2], "ron": [...] }
            Type type = new TypeToken<Map<String, List<Carta>>>(){}.getType();
            Map<String, List<Carta>> data = gson.fromJson(reader, type);
            
            if(data != null) {
                for(Map.Entry<String, List<Carta>> entry : data.entrySet()) {
                    String nomeEroe = entry.getKey().toLowerCase(); // "harry"
                    List<String> ids = new ArrayList<>();
                    
                    // Salviamo solo gli ID
                    for(Carta c : entry.getValue()) {
                        if (c.getId() != null) {
                            ids.add(c.getId());
                        }
                    }
                    starterDeckMap.put(nomeEroe, ids);
                    System.out.println("StarterPackLoader: Caricati " + ids.size() + " ID per " + nomeEroe);
                }
            }
            inizializzato = true;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * QUESTO È IL METODO CHE MANCAVA!
     * Crea una lista di oggetti Carta reali usando CardFactory
     */
    public static List<Carta> creaMazzoPerEroe(String nomeEroe) {
        if (!inizializzato) inizializza();
        
        List<Carta> mazzo = new ArrayList<>();
        
        // Esempio: "Harry Potter" -> cerca chiave "harry"
        String chiave = nomeEroe.toLowerCase().split(" ")[0]; 
        
        List<String> ids = starterDeckMap.get(chiave);
        
        if (ids == null) {
            System.err.println("ERRORE: Nessuno starter pack trovato per la chiave '" + chiave + "' (Eroe: " + nomeEroe + ")");
            return mazzo; // Ritorna lista vuota
        }
        
        for (String id : ids) {
            // Usa CardFactory per creare l'oggetto vero
        	if(id.contains("Alohomora")) {
        		for(int i = 0; i < 7; i++) {
        			Carta c = CardFactory.creaCarta(id);
                    if (c != null) {
                        mazzo.add(c);
                    }
        		}
        	}
            Carta c = CardFactory.creaCarta(id);
            if (c != null) {
                mazzo.add(c);
            }
        }
        
        return mazzo;
    }
    
    /**
     * Restituisce la lista degli ID delle carte (es. "edvige0", "firebolt0") per un dato eroe.
     */
    public static List<String> getIdsStarterPack(String nomeEroe) {
        if (starterDeckMap.isEmpty()) {
            inizializza();
        }
        
        // 1. Pulisce il nome (es. "Harry Potter" -> "harry") per matchare la chiave nel JSON
        String chiave = nomeEroe.toLowerCase().split(" ")[0];
        
        // 2. Ritorna la lista o una lista vuota se non trova nulla (per evitare NullPointerException)
        return starterDeckMap.getOrDefault(chiave, new ArrayList<>());
    }
}