package data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import carte.Eroe;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class HeroFactory {
	private static Map<String, List<Eroe>> dataBaseEroi;
	private static Boolean inizializzata = false;
	
	public static void inizializza() {
		if(inizializzata) {
			return;
		}
		
		try(Reader reader = new InputStreamReader(
				HeroFactory.class.getClassLoader().getResourceAsStream("json/eroe.json"))){
			Gson gson = new Gson();
			Type type = new TypeToken<Map<String, List<Eroe>>>(){}.getType();
			dataBaseEroi = gson.fromJson(reader, type);
			
			inizializzata = true;
			System.out.println("HeroFactory inizializzata con successo.");
		} catch (IOException e) {
			System.err.println("Errore caricamento eroe.json: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
     * Crea l'Eroe corretto in base al nome scelto e all'anno di gioco corrente.
     * Gestisce automaticamente l'evoluzione delle abilità.
     * * @param heroName Il nome dell'eroe (es. "Harry Potter")
     * @param currentYear L'anno corrente della partita (1-7)
     * @return L'oggetto Hero configurato per quell'anno
     */
	public static Eroe creaEroe(String nomeEroe, int annoCorrente) {
		if(!inizializzata) {
			inizializza();
		}
		
		String versionKey = getVersionKeyForYear(annoCorrente);
		
		List<Eroe> eroi = dataBaseEroi.get(versionKey);
		
		if(eroi == null) {
			throw new RuntimeException("Nessun dato trovato per la versione: " + nomeEroe + " (Anno " + annoCorrente + ")");
		}
		
		for(Eroe data : eroi) {
			if(data.getNome().equalsIgnoreCase(nomeEroe)) {
				return new Eroe(data.getNome(), data.getId(), data.getClasse(), data.getDescrizione(), data.getCosto(), data.getPathImmagine(), data.getEffetti(), data.getTriggers());
			}
		}
		
		throw new IllegalArgumentException("Eroe non trovato: " + nomeEroe + " (Anno " + annoCorrente + ")");
	}
	
	/**
     * Logica di Evoluzione:
     * - Anni 1-2: Usa profili "gioco1" (senza abilità attive)
     * - Anni 3-6: Usa profili "gioco3" (abilità intermedie)
     * - Anno 7: Usa profili "gioco7" (abilità finali)
     */
    private static String getVersionKeyForYear(int year) {
        if (year >= 7) {
            return "gioco7";
        } else if (year >= 3) {
            return "gioco3";
        } else {
            return "gioco1";
        }
    }
}
