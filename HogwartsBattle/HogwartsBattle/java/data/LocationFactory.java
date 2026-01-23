package data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import carte.Luogo;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Factory per la creazione dei Luoghi dal JSON. Gestisce il caricamento e la
 * creazione delle istanze.
 */
public class LocationFactory {
	private static Map<String, Luogo> registroLuoghi = new HashMap<>();
	private static Boolean inizializzata = false;

	/**
	 * Inizializza la factory caricando tutti i luoghi dai JSON.
	 */
	public static void inizializza() {
		if (inizializzata) {
			return;
		}

		caricaLuoghi("luoghi.json");

		System.out.println("LocationFactory inizializzata. Luoghi caricati: " + registroLuoghi.size());
		inizializzata = true;
	}

	/**
	 * Carica i luoghi da un file JSON.
	 */
	private static void caricaLuoghi(String nomeFile) {
		try (Reader reader = new InputStreamReader(
				LocationFactory.class.getClassLoader().getResourceAsStream("json/" + nomeFile))) {

			if (reader == null) {
				System.err.println("⚠️ File non trovato: json/" + nomeFile);
				return;
			}

			Gson gson = new Gson();

			// Il JSON è strutturato come: {"gioco1": [...], "gioco2": [...]}
			Type type = new TypeToken<Map<String, List<Luogo>>>() {
			}.getType();
			Map<String, List<Luogo>> data = gson.fromJson(reader, type);

			if (data != null) {
				for (List<Luogo> list : data.values()) {
					for (Luogo datiLuogo : list) {
						registroLuoghi.put(datiLuogo.getId(), datiLuogo);

						if (System.getProperty("debug.loading") != null) {
							System.out.println(
									"  Caricato luogo: " + datiLuogo.getNome() + " (ID: " + datiLuogo.getId() + ")");
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
	 * Crea un nuovo Luogo dato il suo ID.
	 * 
	 * @param idLuogo L'ID univoco del luogo (es. "diagon_alley_1")
	 * @return Una nuova istanza di Luogo
	 * @throws IllegalArgumentException se il luogo non viene trovato
	 */
	public static Luogo creaLuogo(String idLuogo) {
		if (!inizializzata) {
			inizializza();
		}

		Luogo data = registroLuoghi.get(idLuogo);

		if (data == null) {
			throw new IllegalArgumentException("Luogo non trovato con ID: " + idLuogo);
		}

		// Crea una nuova istanza con tutti i dati
		return new Luogo(data.getNome(), data.getId(), data.getClasse(), data.getDescrizione(), data.getCosto(),
				data.getPathImmagine(), data.getEffetti(), data.getTriggers(), data.getNumeroMarchiNeri(),
				data.getNDarkEvents(), data.getMarchiNeriMax(), data.getEffettoEntrata()); // Determina il numero giocatori dalla versione;
	}

	/**
	 * Determina il numero di giocatori supportato dal luogo. Alcuni luoghi hanno
	 * versioni diverse per 2-3 o 4-5 giocatori.
	 */
	private static int determinePlayerCount(String idLuogo) {
		// Se l'ID contiene indicatori di player count, estraili
		// Altrimenti usa il default (qualsiasi numero)
		if (idLuogo.contains("_2-3")) {
			return 3; // Max 3 giocatori
		} else if (idLuogo.contains("_4-5")) {
			return 5; // Max 5 giocatori
		}
		return 5; // Default: supporta tutti i player count
	}

	/**
	 * Ottiene tutti i luoghi di un anno specifico.
	 * 
	 * @param anno L'anno del gioco (1-7)
	 * @return Lista degli ID dei luoghi per quell'anno
	 */
	public static List<String> getLuoghiPerAnno(int anno) {
		String chiaveAnno = "gioco" + anno;

		// Questo richiederebbe di mantenere una mappa separata
		// Per ora restituiamo una lista vuota
		// TODO: Implementare se necessario
		return List.of();
	}

	/**
	 * Verifica se un luogo esiste.
	 */
	public static boolean esisteLuogo(String idLuogo) {
		if (!inizializzata) {
			inizializza();
		}
		return registroLuoghi.containsKey(idLuogo);
	}

	/**
	 * Restituisce il numero totale di luoghi caricati.
	 */
	public static int getNumeroLuoghiCaricati() {
		if (!inizializzata) {
			inizializza();
		}
		return registroLuoghi.size();
	}
}