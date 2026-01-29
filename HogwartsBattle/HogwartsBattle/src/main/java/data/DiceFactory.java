package data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import carte.Dado;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Factory per la creazione dei Dadi dal JSON.
 * I dadi sono usati nell'espansione Monster Box of Monsters.
 */
public class DiceFactory {
    private static Map<String, Dado> registroDadi = new HashMap<>();
    private static Boolean inizializzata = false;
    
    /**
     * Inizializza la factory caricando tutti i dadi dai JSON.
     */
    public static void inizializza() {
        if (inizializzata) {
            return;
        }
        
        caricaDadi("dado.json");
        
        System.out.println("DiceFactory inizializzata. Dadi caricati: " + registroDadi.size());
        inizializzata = true;
    }
    
    /**
     * Carica i dadi da un file JSON.
     */
    private static void caricaDadi(String nomeFile) {
        try (Reader reader = new InputStreamReader(
                DiceFactory.class.getClassLoader().getResourceAsStream("json/" + nomeFile))) {
            
            if (reader == null) {
                System.err.println("⚠️ File non trovato: json/" + nomeFile);
                return;
            }
            
            Gson gson = new Gson();
            
            // Il JSON potrebbe essere strutturato come: {"dadi": [...]}
            // O direttamente come array: [...]
            // Assumiamo struttura simile alle carte
            Type type = new TypeToken<Map<String, List<Dado>>>(){}.getType();
            Map<String, List<Dado>> data = gson.fromJson(reader, type);
            
            if (data != null) {
                for (List<Dado> list : data.values()) {
                    for (Dado datiDado : list) {
                        registroDadi.put(datiDado.getId(), datiDado);
                        
                        if (System.getProperty("debug.loading") != null) {
                            System.out.println("  Caricato dado: " + datiDado.getNome() + 
                                             " (ID: " + datiDado.getId() + ")");
                        }
                    }
                }
            } else {
                System.err.println("⚠️ Nessun dato trovato in " + nomeFile);
            }
            
        } catch (IOException e) {
            System.err.println("❌ Errore nel caricamento di " + nomeFile);
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ Errore generico nel parsing di " + nomeFile);
            e.printStackTrace();
        }
    }
    
    /**
     * Crea un nuovo Dado dato il suo ID.
     * 
     * @param idDado L'ID univoco del dado (es. "grifondoro", "serpeverde")
     * @return Una nuova istanza di Dado
     * @throws IllegalArgumentException se il dado non viene trovato
     */
    public static Dado creaDado(String idDado) {
        if (!inizializzata) {
            inizializza();
        }
        
        Dado data = registroDadi.get(idDado);
        
        if (data == null) {
            throw new IllegalArgumentException("Dado non trovato con ID: " + idDado);
        }
        
        // Crea una nuova istanza con tutti i dati
        return new Dado(
            data.getNome(),
            data.getId(),
            data.getPathImg(),
            data.getTriggers()
        );
    }
    
    /**
     * Crea tutti i 4 dadi delle casate di Hogwarts.
     * 
     * @return Mappa con i dadi delle 4 casate (chiave: nome casata lowercase)
     */
    public static Map<String, Dado> creaDadiCasate() {
        if (!inizializzata) {
            inizializza();
        }
        
        Map<String, Dado> dadiCasate = new HashMap<>();
        
        String[] casate = {"grifondoro", "serpeverde", "corvonero", "tassorosso"};
        
        for (String casata : casate) {
            try {
                Dado dado = creaDado(casata);
                dadiCasate.put(casata.toLowerCase(), dado);
            } catch (IllegalArgumentException e) {
                System.err.println("⚠️ Dado della casata " + casata + " non trovato");
            }
        }
        
        return dadiCasate;
    }
    
    /**
     * Verifica se un dado esiste.
     */
    public static boolean esisteDado(String idDado) {
        if (!inizializzata) {
            inizializza();
        }
        return registroDadi.containsKey(idDado);
    }
    
    /**
     * Restituisce il numero totale di dadi caricati.
     */
    public static int getNumeroDadiCaricati() {
        if (!inizializzata) {
            inizializza();
        }
        return registroDadi.size();
    }
    
    /**
     * Ottiene tutti i dadi disponibili.
     */
    public static Map<String, Dado> getTuttiIDadi() {
        if (!inizializzata) {
            inizializza();
        }
        return new HashMap<>(registroDadi);
    }
}