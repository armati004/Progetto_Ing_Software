package gioco;

import java.util.ArrayList;
import java.util.List;

import carte.Carta;
import carte.Competenza;
import carte.Eroe;
import carte.Mazzo;
import gestoreEffetti.TipoTrigger;
import gestoreEffetti.Trigger;

public class Giocatore {
    private Eroe  eroe;
    private int salute;
    private final int saluteMax = 10;
    private Mazzo mazzo;	//dove si pescano le carte
    private Mazzo scarti;	//dove si mettono le carte dopo averle giocate
    private List<Carta> mano;
    private int gettone;
    private int attacco;
    private Competenza competenza;
    private int alleatiGiocati;
    private int incantesimiGiocati;
    private int oggettiGiocati;

	private SelettoreCarta selettoreCarta;
	private List<Carta> carteAcquistateQuestoTurno = new ArrayList<>();

    public Giocatore(Eroe eroe) {
    	this.eroe = eroe;
    	this.salute = saluteMax;
    	this.mazzo = new Mazzo();
    	this.scarti = new Mazzo();
    	this.mano = new ArrayList<>();
    	this.gettone = 0;
    	this.attacco = 0;
    	this.competenza = null;
    	this.setAlleatiGiocati(0);
    	this.setIncantesimiGiocati(0);
    	this.setOggettiGiocati(0);
    }
    
    public void scartaCarta(Carta carta) {
		this.getScarti().getCarte().add(carta);
		this.getMano().remove(carta);
	}
    
    public void inizializzaMano() {
    	for(int i = 0; i < 5; i++) {
    		Carta cartaPescata = mazzo.pescaCarta();
    		if(cartaPescata != null) {
    			mano.add(cartaPescata);
    		}
    	}
    }
	
    public void giocaCarta(StatoDiGioco stato, Carta carta) {
        if (!mano.contains(carta)) {
            System.out.println("ERRORE: La carta " + carta.getNome() + " non è nella mano!");
            return;
        }

        // Rimuovi trigger "in mano" PRIMA di giocare
        rimuoviTriggersCartaDaMano(stato, carta);

        carta.applicaEffetto(stato, this);

        if (carta.getClasse().equalsIgnoreCase("Alleato")) {
            alleatiGiocati++;
        }
        else if(carta.getClasse().equalsIgnoreCase("Incantesimo")) {
        	incantesimiGiocati++;
        }
        else if(carta.getClasse().equalsIgnoreCase("Oggetto")) {
        	oggettiGiocati++;
        }
        
        if(incantesimiGiocati >= 4 && eroe.getNome().contains("Hermione") && eroe.getTriggers() != null) {
        	if(eroe.getTriggers().get(0).getAttivato1Volta() == false) {
        		stato.getGestoreTrigger().attivaTrigger(TipoTrigger.INCANTESIMI_GIOCATI, stato, this);
        		eroe.getTriggers().get(0).setAttivato1Volta(true);
        	}
        }
        
        if(incantesimiGiocati >= 1 && alleatiGiocati >= 1 && oggettiGiocati >= 1) {
        	stato.getGestoreTrigger().attivaTrigger(TipoTrigger.GIOCA_TUTTO, stato, this);
        	stato.getGestoreTrigger().attivaTrigger(TipoTrigger.TUTTE_TIPOLOGIE, stato, this);
        }
        
        if(alleatiGiocati >= 2) {
        	stato.getGestoreTrigger().attivaTrigger(TipoTrigger.GIOCA_ALLEATO, stato, this);
        }
        
        if(oggettiGiocati >= 1) {
        	stato.getGestoreTrigger().attivaTrigger(TipoTrigger.GIOCA_OGGETTO, stato, this);
        }
        
        if(incantesimiGiocati >= 1) {
        	stato.getGestoreTrigger().attivaTrigger(TipoTrigger.GIOCA_INCANTESIMO, stato, this);
        }
        
        mano.remove(carta);
        scarti.aggiungiCarta(carta);

        System.out.println("Giocata: " + carta.getNome());
    }
	
	//cerca nel mazzo degli scarti un tipo di carta (oggetto e incantesimo e alleato)
	public List<Carta> cercaNelMazzo(String tipo) {	
	    List<Carta> carteCorrispondenti = new ArrayList<>();
	    
	    for (int i = 0; i < mazzo.getCarte().size(); i++) { 
	        Carta carta = mazzo.getCarte().get(i);
	        if (carta.getClasse().equalsIgnoreCase(tipo)) {
	            carteCorrispondenti.add(carta);
		        System.out.println("carta: " + carta.getNome() + ", descrizione:" + carta.getDescrizione());
	        }
	    }
		return carteCorrispondenti;
		//una volta ritornate dovrà poi scegliere quale vuole aggiungere al mazzo
	}
	
	public void acquistaCarta(List<Carta> mercato, Carta carta, StatoDiGioco stato) {
	    this.setGettone(gettone - carta.getCosto());
	    mercato.remove(carta);
	    
	    // Segna carta come acquisita permanentemente
	    stato.segnaCartaAcquisita(carta.getId());
	    
	    // Traccia carta acquistata questo turno
	    carteAcquistateQuestoTurno.add(carta);
	    
	    System.out.println("Acquistata: " + carta.getNome());
	}

	// ============================================
	// AGGIUNGERE A GIOCATORE.JAVA
	// ============================================

	/**
	 * Pesca una carta dal mazzo.
	 * Se il mazzo è vuoto, rimescola gli scarti nel mazzo.
	 * 
	 * @return true se pescata con successo, false se impossibile pescare
	 */
	public boolean pescaCarta() {
	    // Se il mazzo è vuoto, rimescola gli scarti
	    if (mazzo.isEmpty()) {
	        System.out.println("Mazzo vuoto! Rimescolo scarti...");
	        
	        // Se anche gli scarti sono vuoti, non si può pescare
	        if (scarti.isEmpty()) {
	            System.out.println("Nessuna carta da pescare (mazzo e scarti vuoti)");
	            return false;
	        }
	        
	        // Rimescola: sposta tutte le carte dagli scarti al mazzo
	        while (!scarti.isEmpty()) {
	            Carta carta = scarti.pescaCarta();  // Prende dalla cima degli scarti
	            if (carta != null) {
	                mazzo.aggiungiCarta(carta);
	            }
	        }
	        
	        // Mescola il mazzo
	        mazzo.mescola();
	        
	        System.out.println("Mazzo rimescolato: " + mazzo.size() + " carte");
	    }
	    
	    // Pesca carta
	    Carta carta = mazzo.pescaCarta();
	    if (carta != null) {
	        mano.add(carta);
	        return true;
	    }
	    
	    return false;
	}
	
	/**
	 * Registra i trigger delle carte nella mano
	 */
	public void registraTriggersInMano(StatoDiGioco stato) {
	    for (Carta carta : mano) {
	        if (carta.getTriggers() != null && !carta.getTriggers().isEmpty()) {
	            for (Trigger trigger : carta.getTriggers()) {
	                // Solo trigger che funzionano "in mano" (es. RICEVI_DANNO per Mantello)
	                if (trigger.getType() == gestoreEffetti.TipoTrigger.RICEVI_DANNO || trigger.getType() == gestoreEffetti.TipoTrigger.AUTO_SCARTO) {
	                    stato.getGestoreTrigger().registraTrigger(
	                        trigger.getType(), 
	                        trigger.getEffectToExecute(), 
	                        carta, 
	                        trigger.getDurata()
	                    );
	                }
	            }
	        }
	    }
	}

	/**
	 * Rimuove i trigger di una carta specifica
	 */
	public void rimuoviTriggersCartaDaMano(StatoDiGioco stato, Carta carta) {
	    if (carta.getTriggers() != null && !carta.getTriggers().isEmpty()) {
	        stato.getGestoreTrigger().rimuoviTrigger(carta);
	    }
	}

	public Eroe getEroe() {
		return eroe;
	}
	public void setEroe(Eroe eroe) {
		this.eroe = eroe;
	}
	public int getSalute() {
		return salute;
	}
	public void setSalute(int salute) {
		this.salute = salute;
	}
	public int getSaluteMax() {
		return saluteMax;
	}
	public Mazzo getMazzo() {
		return mazzo;
	}
	public Mazzo getScarti() {
		return scarti;
	}
	public List<Carta> getMano() {
		return mano;
	}
	public void setMano(List<Carta> mano) {
		this.mano = mano;
	}
	public int getGettone() {
		return gettone;
	}
	public void setGettone(int gettone) {
		this.gettone = gettone;
	}
	public int getAttacco() {
		return attacco;
	}
	public void setAttacco(int attacco) {
		this.attacco = attacco;
	}
	
	public Carta scegliCarta(List<Carta> mazzo) {
		return selettoreCarta.selezionaCarta(mazzo);
	}

	public Competenza getCompetenza() {
		return competenza;
	}

	public void setCompetenza(Competenza competenza) {
		this.competenza = competenza;
	}
	
	public int getAlleatiGiocati() {
		return alleatiGiocati;
	}

	public void setAlleatiGiocati(int alleatiGiocati) {
		this.alleatiGiocati = alleatiGiocati;
	}

	public int getIncantesimiGiocati() {
		return incantesimiGiocati;
	}

	public void setIncantesimiGiocati(int incantesimiGiocati) {
		this.incantesimiGiocati = incantesimiGiocati;
	}

	public int getOggettiGiocati() {
		return oggettiGiocati;
	}

	public void setOggettiGiocati(int oggettiGiocati) {
		this.oggettiGiocati = oggettiGiocati;
	}

	public List<Carta> getCarteAcquistateQuestoTurno() {
	    return carteAcquistateQuestoTurno;
	}

	public void resetCarteAcquistate() {
	    carteAcquistateQuestoTurno.clear();
	}

}
