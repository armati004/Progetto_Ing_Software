package carte;

import java.util.ArrayList;
import java.util.List;

import data.CardFactory;
import data.StarterPackLoader;

public class Mazzo {
    private List<Carta> carte = new ArrayList<>();
    
    public Mazzo() {
        // Non serve più nulla!
    }
    
    public List<Carta> inizializzaMazzo(String nomeEroe) {
    	List<String> idStarterPack = StarterPackLoader.getDeckPerEroe(nomeEroe);
    	
    	for(String id : idStarterPack) {
    		carte.add(CardFactory.creaCarta(id));
    	}
    	
    	return carte;
    }

    public void aggiungiCarta(Carta carta) {
        carte.add(carta);
    }

    public Carta pescaCarta() {
        Carta carta = carte.get(0);
        carte.remove(0);
        return carta;
    }

    public int size() {
        return carte.size();
    }

    public List<Carta> getCarte() {
        return carte;
    }

    public Carta get(int index) {
        return carte.get(index);
    }
}
