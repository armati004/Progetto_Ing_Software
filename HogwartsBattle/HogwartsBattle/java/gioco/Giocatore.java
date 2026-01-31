package gioco;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import carte.Carta;
import carte.Competenza;
import carte.Eroe;
import carte.Mazzo;

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

	private SelettoreCarta selettoreCarta;
	

    
    
    /*public Giocatore(Eroe eroe, int salute, Mazzo mazzo, Mazzo scarti, List<Carta> mano, int gettone, int attacco) {
		this.eroe = eroe;
		this.salute = saluteMax;
		this.mazzo = mazzo;
		this.scarti = scarti;
		this.mano = mano;
		this.gettone = gettone;
		this.attacco = attacco;
	}*/
    
    public Giocatore(Eroe eroe) {
    	this.eroe = eroe;
    	this.salute = saluteMax;
    	this.mazzo = new Mazzo();
    	this.mazzo.inizializzaMazzo(eroe.getNome());
    	this.scarti = new Mazzo();
    	this.mano = new ArrayList<>();
    	this.gettone = 0;
    	this.attacco = 0;
    	this.competenza = null;
    }
    
    public void scartaCarta(/*Mazzo mazzo,*/ Carta carta) {
		this.getScarti().getCarte().add(carta);
		this.getMano().remove(carta);
	}
	
    public void giocaCarta(StatoDiGioco stato, Carta carta) {
        // Verifica difensiva
        if (!mano.contains(carta)) {
            System.out.println("ERRORE: La carta " + carta.getNome() + " non è nella mano!");
            return;
        }

        // 1. Applica l'effetto PRIMA di spostarla (a volte conta l'ordine, ma è convenzione)
        //    oppure DOPO, dipende se l'effetto richiede che la carta sia in gioco.
        //    Solitamente: La giochi -> Applichi effetto -> Va negli scarti.
        carta.applicaEffetto(stato, this);

		// Aggiungi alla lista degli alleati giocati in questo turno, se applicabile
		// ai fini di effetti che dipendono da questo (come Pozione polisucco)
		if (carta.getClasse().equals("Alleato")) {
			stato.getAlleatiGiocatiInQuestoTurno().add((carte.Alleato) carta);
		}
        // 2. Rimuovi dalla mano
        mano.remove(carta);

        // 3. Sposta carta negli scarti (o in una pila "carte giocate" se le vuoi tenere visibili fino a fine turno)
        scarti.aggiungiCarta(carta);

        System.out.println("Giocata: " + carta.getNome());
        
        // NOTA: NON pescare qui! La pesca avviene nel metodo cleanupTurn/fineTurno del GameState.
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
		//una volta ritornate dovra poi scegliere quale vuole aggiungere al mazzo
	}
	
	public void acquistaCarta(List<Carta> mercato, Carta carta) {
	    this.setGettone(gettone - carta.getCosto());
	    scarti.aggiungiCarta(carta);
	    mercato.remove(carta);
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

}
