package data;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Gestisce il caricamento della configurazione di un anno specifico.
 * Aggrega i dati cumulativi (es. Anno 3 include carte di Anno 1 e 2).
 */
public class GameLoader {
	/**
	 * Carica tutto il necessario per iniziare una partita in un anno specifico.
	 * @param annoTarget L'anno da caricare (1-7)
	 * @return Un oggetto GameConfig pronto per inizializzare il GameState
	 */
	public GameConfig caricaConfigurazione(int annoTarget) {
		GameConfig config = new GameConfig();
		config.setAnno(annoTarget);
		
		// Carica sequenzialmente tutti gli anni fino al target
        // (Perché nel gioco annoTarget si usano anche i nemici dei giochi precedenti)
		for(int i = 1; i <= annoTarget; i++) {
			Anno anno = caricaDatiAnno(i);
			
			if(anno != null) {
				config.getCarteNegozioId().addAll(anno.getNuoveCarteNegozio());
				config.getMalvagiId().addAll(anno.getNuoviMalvagi());
				config.getArtiOscureId().addAll(anno.getNuoveArtiOscure());
				
				if(anno.getLuoghi() != null && !anno.getLuoghi().isEmpty()) {
					config.setLuoghiId(anno.getLuoghi());
				}
				if(anno.getMeccanica() != null) {
					config.aggiornaMeccaniche(anno.getMeccanica());
				}
			}
		}
		
		return config;
	}
	
	private Anno caricaDatiAnno(int anno) {
		String nomeFile = "anno" + anno + ".json";
		try (Reader reader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream("json/" + nomeFile))) {
			Gson gson = new Gson();
			return gson.fromJson(reader, Anno.class);
		} catch (IOException e) {
			System.err.println("Impossibile caricare " + nomeFile + " (forse non esiste ancora?)");
            return null;
		}
	}
}
