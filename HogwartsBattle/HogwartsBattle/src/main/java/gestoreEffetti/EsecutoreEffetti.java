package gestoreEffetti;

import java.util.ArrayList;
import java.util.List;

import carte.Alleato;
import carte.Carta;
import carte.Luogo;
import carte.Malvagio;
import gioco.Giocatore;
import gioco.StatoDiGioco;

public class EsecutoreEffetti {
	public static void eseguiEffetto(Effetto effetto, StatoDiGioco stato, Giocatore bersaglio) {
		
		switch(effetto.getType()) {
		case AGGIUNGERE_MARCHIO_NERO:
			aggiungereMarchioNero(effetto, stato, bersaglio); 
			break;
		case ALLEATO_IN_MAZZO:
			break;
		case ALLEATO_IN_MANO:
			verificaAlleatoInMano(effetto, stato, bersaglio);
			break;
		case BLOCCA_ABILITA_MALVAGIO:
			bloccaAbilitaMalvagio(effetto, stato, bersaglio);
			break;
		case CERCA_ALLEATO:
			break;
		case CERCA_INCANTESIMO:
			cercaIncantesimo(effetto, stato, bersaglio);
			break;
		case CERCA_OGGETTO:
			break;
		case COPIA_EFFETTO:
			copiaEffetto(effetto, stato, bersaglio);
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
			guadagnareAttacco(effetto, stato, bersaglio);
			break;
		case GUADAGNARE_INFLUENZA:
			guadagnareInfluenza(effetto, stato, bersaglio, effetto.getQta());
			break;
		case GUADAGNARE_VITA:
			guadagnareSalute(effetto, stato, bersaglio, effetto.getQta());
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
			perdereVita(effetto, stato, bersaglio, effetto.getQta());
			break;
		case PESCARE_CARTA:
			pescaCarta(effetto, stato, bersaglio, effetto.getQta());
			break;
		case RIMUOVERE_ATTACCO:
			break;
		case RIMUOVERE_MARCHIO_NERO:
			rimuovereMarchioNero(effetto, stato, stato.getCurrentLocation(), effetto.getQta());
			break;
		case RITIRA_DADO:
			break;
		case RIVELA_CARTA:
			break;
		case RIVELA_NUOVO_EVENTO:
			break;
		case SCARTARE_CARTA:
			scartaCarta(effetto, stato, bersaglio, effetto.getQta());
			break;
		case SCARTA_ALLEATO:
			scartaAlleato(effetto, stato, bersaglio);
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
	
	public static void aggiungereMarchioNero(Effetto effetto, StatoDiGioco stato, Giocatore bersaglio) {
		stato.getCurrentLocation().setNumeroMarchiNeri(stato.getCurrentLocation().getNumeroMarchiNeri() + effetto.getQta());
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
	        // 1. CATTURA la carta restituita dal metodo
	        Carta cartaPescata = attivo.getMazzo().pescaCarta();
	        
	        // 2. AGGIUNGILA alla mano (se il mazzo non era vuoto)
	        if (cartaPescata != null) {
	            attivo.getMano().add(cartaPescata);
	        }
	    }
	    // Debug utile per verificare che funzioni
	    // System.out.println("DEBUG: Pescate " + qta + " carte. Mano attuale: " + attivo.getMano().size());
	}
	public static void perdereVita (Effetto effetto, StatoDiGioco stato, Giocatore attivo, int valore) {
		attivo.setSalute(attivo.getSalute() - valore);
	}

	// i seguenti metodi vanno testati
	public static void rimuovereMarchioNero (Effetto effetto, StatoDiGioco stato, Luogo luogo, int qta) {
		int numeroLuogo = luogo.getNumeroMarchiNeri();
		if(luogo.getNumeroMarchiNeri() - qta < 0) {
			luogo.setNumeroMarchiNeri(0);
			return;
		}
		else{
			luogo.setNumeroMarchiNeri(numeroLuogo - qta);
			return;
		}
	}

	//attualmente non implementa il caso che debbano venire scartate più carte dallo stesso effetto
	//scarta una carta a scelta del giocatore dalla sua mano
	public static void scartaCarta(Effetto effetto, StatoDiGioco stato, Giocatore attivo, int qta) {
		for(int i = 0; i < qta; i++) {
			if(attivo.getMano().isEmpty()) {
				return;
			}
			Carta sceltaCarta = attivo.scegliCarta(attivo.getMano());
			if (attivo.getMano().contains(sceltaCarta)) {
				attivo.getMano().remove(sceltaCarta);
				attivo.getScarti().aggiungiCarta(sceltaCarta);
			}
			else {
				throw new IllegalArgumentException("La carta selezionata non è nella mano del giocatore.");
			}
		}	
	}

	public static void scartaCartaForzata(Giocatore attivo, Carta carta) {
		if (attivo.getMano().contains(carta)) {
			attivo.getMano().remove(carta);
			attivo.getScarti().aggiungiCarta(carta);
		} else {
			throw new IllegalArgumentException("La carta selezionata non è nella mano del giocatore.");
		}
	}

	//copia l'effetto di un alleato giocato in questo turno
	public static void copiaEffetto (Effetto effetto, StatoDiGioco stato, Giocatore bersaglio) {
		List<Carta> alleatiGiocati = stato.getAlleatiGiocatiInQuestoTurno();
		if(alleatiGiocati.isEmpty()) {
			return;
		}
		else {

			Carta alleatoScelto = bersaglio.scegliCarta(alleatiGiocati);
			if(alleatoScelto != null) {
				for(Effetto e : alleatoScelto.getEffetti()) {
					EsecutoreEffetti.eseguiEffetto(e, stato, bersaglio);
				}
			}
		}
	}

	public static void cercaIncantesimo(Effetto effetto, StatoDiGioco stato, Giocatore attivo) {
		List<Carta> incantesimiInScarto = new ArrayList<Carta>();
		for (Carta c : attivo.getScarti().getCarte()) {
			if (c.getClasse().equals("Incantesimo")) {
				incantesimiInScarto.add(c);
			}
		}
		if(incantesimiInScarto.isEmpty()) {
			return;
		}
		else {
			Carta incantesimoScelto = attivo.scegliCarta(incantesimiInScarto);
			if(incantesimoScelto != null) {
				attivo.getMano().add(incantesimoScelto);
				attivo.getScarti().getCarte().remove(incantesimoScelto);
			}
		}
	}

	public static void scartaAlleato(Effetto effetto, StatoDiGioco stato, Giocatore attivo) {
		List<Carta> alleatiInMano = new ArrayList<Carta>();
		for (Carta c : attivo.getMano()) {
			if (c.getClasse().equals("Alleato")) {
				alleatiInMano.add(c);
			}
		}
		if(alleatiInMano.isEmpty()) {
			return;
		}
		else {
			Carta alleatoScelto = attivo.scegliCarta(alleatiInMano);
			if(alleatoScelto != null) {
				attivo.getMano().remove(alleatoScelto);
				attivo.getScarti().aggiungiCarta(alleatoScelto);
			}
		}
	}

	public static void bloccaAbilitaMalvagio (Effetto effetto, StatoDiGioco stato, Giocatore giocatore) {
		List<Malvagio> malvagiAttivi = stato.getMalvagiAttivi();
		if (malvagiAttivi.isEmpty()) {
			return;
		}
		else if (malvagiAttivi.size() == 1) {
			Malvagio malvagioSingolo = malvagiAttivi.get(0);
			malvagioSingolo.setAbilitaBloccata(true);
		}
		else {
			Malvagio malvagioScelto = (Malvagio) giocatore.scegliCarta(new ArrayList<Carta>(malvagiAttivi));
			if(malvagioScelto != null) {
				malvagioScelto.setAbilitaBloccata(true);
				stato.getMalvagiBloccati().put(malvagioScelto, giocatore);
			}
		}
	}

	public static void rivelaCarta (Effetto effetto, StatoDiGioco stato, Giocatore bersaglio) {
		Carta primaCarta = bersaglio.getMazzo().get(0);
		if (primaCarta.getCosto() > 0){
			scartaCartaForzata(bersaglio, primaCarta);
			aggiungereMarchioNero(effetto, stato, bersaglio);
		}
	}
}
