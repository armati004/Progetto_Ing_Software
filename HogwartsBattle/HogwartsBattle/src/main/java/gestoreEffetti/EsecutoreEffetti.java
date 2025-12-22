package gestoreEffetti;

import carte.Alleato;
import carte.Carta;
import gioco.Giocatore;
import gioco.StatoDiGioco;

public class EsecutoreEffetti {
	public static void eseguiEffetto(Effetto effetto, StatoDiGioco stato, Giocatore giocatore) {
		switch(effetto.getType()) {
		case AGGIUNGERE_MARCHIO_NERO:
			aggiungereMarchioNero(effetto, stato, giocatore); 
			break;
		case ALLEATO_IN_MAZZO:
			break;
		case ALLEATO_IN_MANO:
			verificaAlleatoInMano(effetto, stato, giocatore);
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
			guadagnareAttacco(effetto, stato, giocatore);
			break;
		case GUADAGNARE_INFLUENZA:
			guadagnareInfluenza(effetto, stato, giocatore, effetto.getQta());
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
		case SCELTA_MULTIPLA:
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
			perdereVita(effetto, stato, giocatore, effetto.getQta());
			break;
		case PESCARE_CARTA:
			pescaCarta(effetto, stato, giocatore, effetto.getQta());
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
		case SCARTA_INFLUENZA:
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
	
	public static void aggiungereMarchioNero(Effetto effetto, StatoDiGioco stato, Giocatore attivo) {
		
	}	
	
	public static void verificaAlleatoInMano(Effetto effetto, StatoDiGioco stato, Giocatore attivo) {
		int contatore = 0;
		
		for(Carta c : attivo.getMano()) {
			if(c instanceof Alleato) {
				contatore++;
			}
		}
		
		if(contatore > 0) {
			
		}
		else {
			return;
		}
	}

	public static void guadagnareAttacco (Effetto effetto, StatoDiGioco stato, Giocatore attivo) {
		//regola attiva funziona che se l'effetto ritorna true significa che c'è un blocco sull'effetto quindi non si puo fare
		//se l'effetto ritorna flase significa che non c'è un blocco sull'effetto quindi si puo fare
		if(stato.getGestoreEffetti().regolaAttiva(TipoEffetto.NON_GUADAGNARE_ATTACCHI) == false) { 
		attivo.setAttacco(attivo.getAttacco() + effetto.getQta());
		} else {
			System.out.println("C'è un blocco su questo effetto, NON SI PUO FARE!");
			return;
		}
		
	}
	public static void guadagnareSalute (Effetto effetto, StatoDiGioco stato, Giocatore attivo, int valore) {
		attivo.setSalute(attivo.getSalute() + valore);
	}
	public static void guadagnareInfluenza (Effetto effetto, StatoDiGioco stato, Giocatore attivo, int valore) {
		attivo.setGettone(attivo.getGettone() + valore);
	}
	public static void pescaCarta(Effetto effetto, StatoDiGioco stato, Giocatore attivo, int qta) {
		for(int i = 0; i < qta; i++) {
			attivo.getMazzo().pescaCarta();
		}
	}
	public static void perdereVita (Effetto effetto, StatoDiGioco stato, Giocatore attivo, int valore) {
		attivo.setSalute(attivo.getSalute() - valore);
	}

}
