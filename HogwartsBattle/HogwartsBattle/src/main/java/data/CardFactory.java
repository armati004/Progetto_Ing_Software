package data;

import carte.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardFactory {
	// Cache di tutte le definizioni di carte
	private static Map<String, Carta> registroCarte = new HashMap<>();
	private static boolean inizializzata = false;

	public static void inizializza() {
		if (inizializzata)
			return;

		// Carica tutti i file JSON
		caricaCarte("/json/alleato.json");
		caricaCarte("/json/incantesimo.json");
		caricaCarte("/json/oggetto.json");
		caricaCarte("/json/arti_oscure.json");
		caricaCarte("/json/starter_pack.json"); // Fondamentale per le definizioni base

		System.out.println("CardFactory inizializzata. Definizioni caricate: " + registroCarte.size());
		inizializzata = true;
	}

	private static void caricaCarte(String pathFile) {
		// Nota: Assicurati che il path inizi con "/" se usi getResourceAsStream su root
		if (!pathFile.startsWith("/"))
			pathFile = "/" + pathFile;

		try (Reader reader = new InputStreamReader(CardFactory.class.getResourceAsStream(pathFile))) {
			Gson gson = new Gson();
			Type type = new TypeToken<Map<String, List<Carta>>>() {
			}.getType();

			// Parsa la mappa: chiave (es. "gioco1", "harry") -> lista di carte
			Map<String, List<Carta>> data = gson.fromJson(reader, type);

			if (data != null) {
				for (List<Carta> lista : data.values()) {
					for (Carta c : lista) {
						// REGISTRA LA CARTA NELLA MEMORIA GLOBALE
						if (c.getId() != null) {
							registroCarte.put(c.getId(), c);
						}
					}
				}
			}
		} catch (Exception e) {
			System.err.println("Errore caricamento " + pathFile + ": " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static Carta creaCarta(String idCarta) {
		if (!inizializzata)
			inizializza();

		Carta data = registroCarte.get(idCarta);
		if (data == null) {
			System.err.println("ERRORE: Carta ID " + idCarta + " non trovata.");
			return null;
		}

		// Nel JSON il campo sia "class": "ArtiOscure"
		
		String tipo = (data.getClasse() != null) ? data.getClasse().toLowerCase() : "sconosciuto";

		switch (tipo) {
		case "alleato":
			return new Alleato(data.getNome(), data.getId(), "Alleato", data.getDescrizione(), data.getCosto(),
					data.getPathImmagine(), data.getEffetti(), data.getTriggers());
		case "incantesimo":
			return new Incantesimo(data.getNome(), data.getId(), "Incantesimo", data.getDescrizione(), data.getCosto(),
					data.getPathImmagine(), data.getEffetti(), data.getTriggers());
		case "oggetto":
			return new Oggetto(data.getNome(), data.getId(), "Oggetto", data.getDescrizione(), data.getCosto(),
					data.getPathImmagine(), data.getEffetti(), data.getTriggers());

		case "artioscure":
			return new ArteOscura(data.getNome(), data.getId(), "ArtiOscure", data.getDescrizione(), data.getCosto(),
					data.getPathImmagine(), data.getEffetti(), data.getTriggers());
		case "arti_oscure":
			return new ArteOscura(data.getNome(), data.getId(), "ArtiOscure", data.getDescrizione(), data.getCosto(),
					data.getPathImmagine(), data.getEffetti(), data.getTriggers());
		default:
			System.err.println("Attenzione: Tipo '" + tipo + "' non riconosciuto per la carta " + idCarta
					+ ". Ritorno carta generica.");
			return data;
		}
	}

	public static boolean isInizializzata() {
		return inizializzata;
	}
}