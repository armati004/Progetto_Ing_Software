package carte;

import java.util.ArrayList;
import java.util.List;

public class Mazzo {
    private List<Carta> carte = new ArrayList<>();
    
    public Mazzo() {
        // Non serve più nulla!
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
