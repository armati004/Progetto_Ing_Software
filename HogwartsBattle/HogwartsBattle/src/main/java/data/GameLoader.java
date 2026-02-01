package data;

import com.google.gson.Gson;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

/**
 * Gestisce il caricamento della configurazione di un anno specifico.
 */
public class GameLoader {

	public GameConfig caricaConfigurazione(int annoTarget, List<String> carteNegozioSalvate) {
	    System.out.println("--- INIZIO CARICAMENTO ANNO " + annoTarget + " ---");
	    GameConfig config = new GameConfig();
	    config.setAnno(annoTarget);
	    Anno annoCorrente = caricaDatiAnno(annoTarget);
	    
	    if (carteNegozioSalvate != null && !carteNegozioSalvate.isEmpty()) {
	        System.out.println("📦 Usando " + carteNegozioSalvate.size() + " carte negozio da salvataggio");
	        config.getCarteNegozioId().addAll(carteNegozioSalvate);
	        
	        if (annoCorrente != null && annoCorrente.getNuoveCarteNegozio() != null) {
	            int carteAggiunte = 0;
	            for (String cartaId : annoCorrente.getNuoveCarteNegozio()) {
	                if (!carteNegozioSalvate.contains(cartaId)) {  // ✅ Evita duplicati
	                    config.getCarteNegozioId().add(cartaId);
	                    carteAggiunte++;
	                }
	            }
	            System.out.println("  ➕ Aggiunte " + carteAggiunte + " carte nuove anno " + annoTarget);
	        }
	    } else {
	    	if (annoCorrente != null && annoCorrente.getNuoveCarteNegozio() != null) {
	            int carteAggiunte = 0;
	            for (String cartaId : annoCorrente.getNuoveCarteNegozio()) {
	            	config.getCarteNegozioId().add(cartaId);
                    carteAggiunte++;
	            }
	            System.out.println("  ➕ Aggiunte " + carteAggiunte + " carte nuove anno " + annoTarget);
	        }
	    }

	    for (int i = 1; i <= annoTarget; i++) {
	        Anno anno = caricaDatiAnno(i);

	        if (anno != null) {
	            System.out.println("Trovati dati per Anno " + i);

	            // 1. Carte Negozio
	            /*if (anno.getNuoveCarteNegozio() != null && !anno.getNuoveCarteNegozio().isEmpty()) {
	                config.getCarteNegozioId().addAll(anno.getNuoveCarteNegozio());
	                System.out.println("  > Aggiunte " + anno.getNuoveCarteNegozio().size() + " carte negozio.");
	            }*/

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
	            
	            // ⭐ 4. HORCRUX (NUOVO)
	            if (anno.getNuoviHorcrux() != null && !anno.getNuoviHorcrux().isEmpty()) {
	                config.getHorcruxId().addAll(anno.getNuoviHorcrux());
	                System.out.println("  > Aggiunti " + anno.getNuoviHorcrux().size() + " horcrux.");
	            }

	            // 5. Luoghi
	            if (anno.getLuoghi() != null && !anno.getLuoghi().isEmpty()) {
	                config.setLuoghiId(anno.getLuoghi());
	                System.out.println("  > Impostati luoghi dell'anno " + i);
	            }

	            // 6. Meccaniche
	            if (anno.getMeccanica() != null) {
	                config.aggiornaMeccaniche(anno.getMeccanica());
	            }
	        }
	    }

	    System.out.println("--- CONFIGURAZIONE COMPLETATA ---");
	    System.out.println("Totale Malvagi nel mazzo: " + config.getMalvagiId().size());
	    System.out.println("Totale Horcrux nel mazzo: " + config.getHorcruxId().size());
	    return config;
	}
	
	public GameConfig caricaConfigurazione(int annoTarget) {
	    return caricaConfigurazione(annoTarget, null);  // Caricamento normale
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