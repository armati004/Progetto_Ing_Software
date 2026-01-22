package gestoreEffetti;

import java.util.List;

import carte.Carta;

public enum BersaglioEffetto {
	EROE_ATTIVO, SE_STESSO, TUTTI_GLI_EROI, EROI_NON_ATTIVI,EROE_SCELTO, EROE_ATTIVANTE, ALLEATO_GIOCATO, TUTTI_I_MALVAGI, LUOGO, SCELTA_MULTIPLA, MALVAGIO_SCELTO;


    
    Carta scegliCarta(List<Carta> opzioni) {
        if (opzioni == null || opzioni.isEmpty()) {
            return null;
        }
        System.out.println("[AUTO-SCELTA] Selezionata automaticamente: " + opzioni.get(0).getNome());
        return opzioni.get(0);
    }
}
