package data;

import com.google.gson.Gson;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Gestisce il caricamento della configurazione di un anno specifico.
 * 
 * AGGIORNATO per espansione Charms & Potions:
 * - Caricamento Pozioni (Pack 2+)
 * - Caricamento Dark Arts Potions (Pack 3+)
 * - Caricamento Competenze (Pack 2+)
 * - Caricamento Horcrux (Gioco 7)
 * - Configurazione lato scaffali pozioni
 */
public class GameLoader {

	public GameConfig caricaConfigurazione(int annoTarget) {
		System.out.println("--- INIZIO CARICAMENTO ANNO " + annoTarget + " ---");
		GameConfig config = new GameConfig();
		config.setAnno(annoTarget);

		for (int i = 1; i <= annoTarget; i++) {
			Anno anno = caricaDatiAnno(i);

			if (anno != null) {
				System.out.println("Trovati dati per Anno " + i);

				// --- AGGIUNTA SICURA (Gestione null) ---

				// 1. Carte Negozio (Hogwarts)
				if (anno.getNuoveCarteNegozio() != null && !anno.getNuoveCarteNegozio().isEmpty()) {
					config.getCarteNegozioId().addAll(anno.getNuoveCarteNegozio());
					System.out.println("  > Aggiunte " + anno.getNuoveCarteNegozio().size() + " carte negozio.");
				}

				// 2. Malvagi
				if (anno.getNuoviMalvagi() != null && !anno.getNuoviMalvagi().isEmpty()) {
					config.getMalvagiId().addAll(anno.getNuoviMalvagi());
					System.out.println("  > Aggiunti " + anno.getNuoviMalvagi().size() + " malvagi.");
				}

				// 3. Arti Oscure
				if (anno.getNuoveArtiOscure() != null && !anno.getNuoveArtiOscure().isEmpty()) {
					config.getArtiOscureId().addAll(anno.getNuoveArtiOscure());
					System.out.println("  > Aggiunte " + anno.getNuoveArtiOscure().size() + " arti oscure.");
				}

				// 4. Encounter (Pack 1, 2, 3, 4)
				if (anno.getEncounter() != null && !anno.getEncounter().isEmpty()) {
					config.getEncounterId().addAll(anno.getEncounter());
					System.out.println("  > Aggiunti " + anno.getEncounter().size() + " encounter.");
				}

				// 5. Pozioni (Pack 2+)
				if (anno.getPozioni() != null && !anno.getPozioni().isEmpty()) {
					config.getPozioniId().addAll(anno.getPozioni());
					System.out.println("  > Aggiunte " + anno.getPozioni().size() + " pozioni.");
				}

				// 6. Dark Arts Potions (Pack 3+)
				if (anno.getDarkArtsPozioni() != null && !anno.getDarkArtsPozioni().isEmpty()) {
					config.getDarkArtsPotionId().addAll(anno.getDarkArtsPozioni());
					System.out.println("  > Aggiunte " + anno.getDarkArtsPozioni().size() + " Dark Arts Potions.");
				}

				// 7. Lato Scaffali Pozioni
				if (anno.getPotionShelfSide() != null) {
					config.setPotionShelfSide(anno.getPotionShelfSide());
					System.out.println("  > Lato scaffali pozioni: " + anno.getPotionShelfSide());
				}

				// 8. Competenze (Pack 2+)
				if (anno.getAggiuntaCompetenze() != null && !anno.getAggiuntaCompetenze().isEmpty()) {
					config.getCompetenzeId().addAll(anno.getAggiuntaCompetenze());
					System.out.println("  > Aggiunte " + anno.getAggiuntaCompetenze().size() + " competenze.");
				}

				// 9. Horcrux (Gioco 7)
				if (anno.getAggiuntaHorcrux() != null && !anno.getAggiuntaHorcrux().isEmpty()) {
					config.getHorcruxId().addAll(anno.getAggiuntaHorcrux());
					System.out.println("  > Aggiunti " + anno.getAggiuntaHorcrux().size() + " Horcrux.");
				}

				// 10. Luoghi (Sovrascrive i precedenti, come da logica del gioco)
				if (anno.getLuoghi() != null && !anno.getLuoghi().isEmpty()) {
					config.setLuoghiId(anno.getLuoghi());
					System.out.println("  > Impostati luoghi dell'anno " + i);
				}

				// 11. Meccaniche
				if (anno.getMeccanica() != null) {
					config.aggiornaMeccaniche(anno.getMeccanica());
				}
			}
		}

		System.out.println("--- CONFIGURAZIONE COMPLETATA ---");
		System.out.println("Totale Malvagi nel mazzo: " + config.getMalvagiId().size());
		System.out.println("Totale Encounter: " + config.getEncounterId().size());
		System.out.println("Totale Pozioni: " + config.getPozioniId().size());
		System.out.println("Totale Dark Arts Potions: " + config.getDarkArtsPotionId().size());
		
		return config;
	}

	private Anno caricaDatiAnno(int anno) {
		String path = "json/livelli/gioco" + anno + ".json";

		// 1. Otteniamo lo stream separatamente per controllare se è null
		InputStream is = getClass().getClassLoader().getResourceAsStream(path);

		if (is == null) {
			// Questo accade se il file non esiste o il percorso è sbagliato
			// System.err.println("ATTENZIONE: File non trovato: " + path);
			return null;
		}

		try (Reader reader = new InputStreamReader(is)) {
			Gson gson = new Gson();
			Anno datiAnno = gson.fromJson(reader, Anno.class);

			if (datiAnno == null) {
				System.err.println("ERRORE: Il file " + path + " è vuoto o malformato.");
			}
			return datiAnno;

		} catch (Exception e) {
			System.err.println("Eccezione durante la lettura di " + path);
			e.printStackTrace();
			return null;
		}
	}
}
