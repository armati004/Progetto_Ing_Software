package gestoreEffetti;

import gioco.Giocatore;
import gioco.StatoDiGioco;

public class EsecutoreEffetti {
	public static void eseguiEffetto(Effetto effetto, StatoDiGioco stato, Giocatore giocatore) {
		switch(effetto.getType()) {
		case AGGIUNGERE_MARCHIO_NERO:
			aggiungereMArchioNero();
			break;
		case ALLEATO_IN_MAZZO:
			break;
		case ALLEATO_IN_MANO:
			break;
		case CERCA_ALLEATO:
			break;
		case CERCA_INCANTESIMO:
			break;
		case CERCA_OGGETTO:
			break;
		case COPIA_EFFETTO:
			break;
		case DADO_CORVONERO:
			break;
		case DADO_GRIFONDORO:
			break;
		case DADO_MALVAGIO:
			break;
		case DADO_SERPEVERDE:
			break;
		case DADO_TASSOROSSO:
			break;
		case GUADAGNARE_ATTACCO:
			break;
		case GUADAGNARE_INFLUENZA:
			break;
		case GUADAGNARE_VITA:
			break;
		case INCANTESIMO_IN_MAZZO:
			break;
		case OGGETTO_IN_MAZZO:
			break;
		case NON_ATTACCARE_VOLDY:
			break;
		case SCELTA:
			break;
		case ACQUISTO_CARTA:
			break;
		case NON_GUADAGNARE_ATTACCHI:
			break;
		case NON_GUADAGNARE_GETTONI:
			break;
		case NON_GUADAGNARE_VITA:
			break;
		case NON_PESCARE_CARTE:
			break;
		case NON_RIMUOVERE_MARCHI:
			break;
		case PERDERE_VITA:
			break;
		case PESCARE_CARTA:
			break;
		case RIMUOVERE_ATTACCO:
			break;
		case RIMUOVERE_MARCHIO_NERO:
			break;
		case RITIRA_DADO:
			break;
		case RIVELA_CARTA:
			break;
		case RIVELA_NUOVO_EVENTO:
			break;
		case SCARTARE_CARTA:
			break;
		case SCARTA_ALLEATO:
			break;
		case SCARTA_GETTONI:
			break;
		case SCARTA_INCANTESIMO:
			break;
		case SCARTA_OGGETTO:
			break;
		case SCONTO_ACQUISTO_CASATA:
			break;
		case VITTORIA:
			break;
		}
	}
	
	public static void gestisciOpzioni(Effetto effetto, StatoDiGioco stato, Giocatore attivo) {
		
	}
	
	public static void aggiungereMArchioNero () {
		//...
	}
}

// Lista di funzioni che vengono richiamate sopra

	