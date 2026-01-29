package carte;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import data.CardFactory;
import data.StarterPackLoader;

public class Mazzo {
    private List<Carta> carte = new ArrayList<>();
    
    public Mazzo() {
        // Non serve più nulla!
    }
    
    public List<Carta> inizializzaMazzo(String nomeEroe) {
        // 1. Svuota il mazzo attuale per sicurezza
        this.carte.clear(); 

        // 2. Chiede al Loader la lista degli ID (Stringhe)
        List<String> idStarterPack = StarterPackLoader.getIdsStarterPack(nomeEroe);
        
        // 3. Converte ogni ID in una Carta vera e la aggiunge
        for(String id : idStarterPack) {
        	if(id.contains("Alohomora")) {
        		for(int i = 0; i < 7; i++) {
        			Carta c = CardFactory.creaCarta(id);
                    if (c != null) {
                        this.carte.add(c);
                    }
        		}
        	}
        	else {
        		Carta c = CardFactory.creaCarta(id);
                if (c != null) {
                    this.carte.add(c);
                } else {
                    System.err.println("⚠️ Attenzione: Carta con ID '" + id + "' non trovata nella Factory.");
                }
        	}
        }
        
        System.out.println("✅ Mazzo inizializzato per " + nomeEroe + " con " + this.carte.size() + " carte.");
        return this.carte;
    }

    public void aggiungiCarta(Carta carta) {
        carte.add(carta);
    }

    public Carta pescaCarta() {
        if(!carte.isEmpty()) {
        	Carta carta = carte.get(0);
            carte.remove(0);
            return carta;
        }
        else {
        	return null;
        }
    }
    /**
     * Mescola casualmente le carte nel mazzo
     */
    public void mescola() {
        Collections.shuffle(carte);
    }

    /**
     * Verifica se il mazzo è vuoto
     */
    public boolean isEmpty() {
        return carte.isEmpty();
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
