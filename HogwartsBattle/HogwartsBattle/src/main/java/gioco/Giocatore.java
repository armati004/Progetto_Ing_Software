package gioco;

import java.util.List;

import carte.Carta;
import carte.Eroe;
import carte.Mazzo;

public class Giocatore {
    private Eroe eroe;
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
    
    
}
