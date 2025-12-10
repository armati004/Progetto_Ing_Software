package data;

import carte.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Factory statica che carica tutte le definizioni delle carte dai JSON
 * e crea nuove istanze su richiesta.
 */
public class CardFactory {
	// Cache di tutte le definizioni di carte (mappa ID -> Dati Carta)
    private static Map<String, Carta> registroCarte = new HashMap<>();
    private static Boolean inizializzata = false;
    
    public static void inizializza() {
    	if(inizializzata == true) {
    		return;
    	}
    	
    	caricaCarte("alleato.json");
    	caricaCarte("incantesimo.json");
    	caricaCarte("oggetto.json");
    	caricaCarte("arti_oscure.json");
    	caricaCarte("starterPack.json");
        
        System.out.println("CardFactory inizializzata. Carte caricate: " + registroCarte.size());
        inizializzata = true;
    }
    
    private static void caricaCarte(String nomeFile) {
    	try (Reader reader = new InputStreamReader(CardFactory.class.getClassLoader().getResourceAsStream("json/" + nomeFile))){
    		Gson gson = new Gson();
            // Il JSON è una mappa "gioco1": [lista], "gioco2": [lista]...
            // Noi vogliamo appiattire tutto in un unico registro.
            Type type = new TypeToken<Map<String, List<Carta>>>(){}.getType();
            Map<String, List<Carta>> data = gson.fromJson(reader, type);
            
            if (data != null) {
                for (List<Carta> list : data.values()) {
                    for (Carta datiCarta : list) {
                        // Mettiamo nella mappa usando l'ID come chiave
                        registroCarte.put(datiCarta.getId(), datiCarta);
                    }
                }
            }
    	} catch (Exception e) {
    		System.err.println("Errore nel caricamento di " + nomeFile + ": " + e.getMessage());
			e.printStackTrace();
		}
    }
    
    /**
     * Crea una nuova istanza di una carta dato il suo ID.
     * @param cardId L'ID univoco della carta (es. "harry1", "incendio1")
     * @return Una nuova istanza di Card (AllyCard, SpellCard, ecc.)
     */
    public static Carta creaCarta(String idCarta) {
    	if(inizializzata == false) {
    		inizializza();
    	}
    	
    	Carta data = registroCarte.get(idCarta);
    	if(data == null) {
    		throw new IllegalArgumentException("Carta non trovata con ID: " + idCarta);
    	}
    	
    	switch(data.getClasse().toLowerCase()) {
    	case "alleato":
    		return new Alleato(data.getNome(), data.getId(), data.getClasse(), data.getDescrizione(), data.getCosto(), data.getPathImmagine(), data.getEffetti(), data.getTriggers());
    	case "incantesimo":
    		return new Incantesimo(data.getNome(), data.getId(), data.getClasse(), data.getDescrizione(), data.getCosto(), data.getPathImmagine(), data.getEffetti(), data.getTriggers());
    	case "oggetto":
    		return new Oggetto(data.getNome(), data.getId(), data.getClasse(), data.getDescrizione(), data.getCosto(), data.getPathImmagine(), data.getEffetti(), data.getTriggers());
    	case "artioscure":
    		return new ArteOscura(data.getNome(), data.getId(), data.getClasse(), data.getDescrizione(), data.getCosto(), data.getPathImmagine(), data.getEffetti(), data.getTriggers());
    	default:
    		throw new IllegalArgumentException("Tipo carta sconosciuto: " + data.getClasse());
    	}
    }
}
