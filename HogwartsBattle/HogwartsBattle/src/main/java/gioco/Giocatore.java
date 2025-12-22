package gioco;

import java.util.ArrayList;
import java.util.List;

import carte.Carta;
import carte.Eroe;
import carte.Mazzo;

public class Giocatore {
    private Eroe  eroe;
    private int salute;
    private final int saluteMax = 10;
    private Mazzo mazzo;
    private Mazzo scarti;
    private List<Carta> mano;
    private int gettone;
    private int attacco;
	
    
    
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
    }
    
    public void scartaCarta(/*Mazzo mazzo,*/ Carta carta) {
		this.getScarti().getCarte().add(carta);
		this.getMano().remove(carta);
	}
	
	public void giocaCarta(StatoDiGioco stato, Carta carta) {
	    // Verifica che la carta sia nella mano
	    if (!mano.contains(carta)) {
	        System.out.println("Carta non trovata nella mano!");
	        return;
	    }
	    // Rimuovi dalla mano
	    mano.remove(carta);
	    // Sposta carta negli scarti
	    scarti.aggiungiCarta(carta);
	    
	    carta.applicaEffetto(stato, this);
	    
	    for (int i = 0; i < 5; i++) {
	        Carta pescata = mazzo.pescaCarta();
	        if (pescata != null) {
	            mano.add(pescata);
	        }
	    }
	    System.out.println("Giocata: " + carta.getNome() + " | Mano: " + mano.size());
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
	
	//Aggiungere un metodo per comprare le carte
	

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
	
}
