package carte;

import java.util.ArrayList;
import java.util.Collections;
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
        if(!carte.isEmpty()) {
        	Carta carta = carte.get(0);
            carte.remove(0);
            return carta;
        }
        else {
        	return null;
        }
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
    
    /**
     * Pesca una carta dal mazzo e la mette nella mano del giocatore.
     * Se il mazzo è vuoto, rimescola gli scarti per formare un nuovo mazzo.
     */
    public void pesca(List<Carta> mano, Mazzo mazzoScarti) {
        // 1. Se il mazzo è vuoto, proviamo a rimescolare gli scarti
        if (this.getCarte().isEmpty()) {
            if (mazzoScarti.getCarte().isEmpty()) {
                return; // Non ci sono carte né nel mazzo né negli scarti
            }
            
            System.out.println("Il mazzo è finito! Rimescolo gli scarti...");
            // Sposta tutte le carte dagli scarti al mazzo
            this.getCarte().addAll(mazzoScarti.getCarte());
            mazzoScarti.getCarte().clear();
            
            // Mischia il nuovo mazzo
            Collections.shuffle(this.getCarte());
        }

        // 2. Ora pesca la carta
        if (!this.getCarte().isEmpty()) {
            // Rimuove la prima carta del mazzo (cima)
            Carta cartaPescata = this.getCarte().remove(0);
            // Aggiunge alla mano
            mano.add(cartaPescata);
        }
    }
    
}
