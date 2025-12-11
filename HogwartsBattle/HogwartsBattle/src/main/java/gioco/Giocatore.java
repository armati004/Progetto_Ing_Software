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
	
	private void scartaCarta(Mazzo mazzo, Carta carta) {
		mazzo.remove(carta);
	}

	
	private void giocaCarta(StatoDiGioco stato, Carta carta) {
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
	private List<Carta> cercaNelMazzo(String tipo) {	
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
	


    
}
