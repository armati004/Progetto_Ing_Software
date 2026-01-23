package carte;

import java.util.List;
import gestoreEffetti.Effetto;
import gestoreEffetti.Trigger;

public class Incantesimo extends Carta {

    public Incantesimo(String nome, String id, String classe, String descrizione, int costo,
            String pathImmagine, List<Effetto> effetti, List<Trigger> triggers) {
    	super(nome, id, classe, descrizione, costo, pathImmagine, effetti, triggers);
    }

    
    @Override
    public String toString() {
        return "Incantesimo{" +
                "nome='" + getNome() + '\'' +
                ", id='" + getId() + '\'' +
                ", classe='" + getClasse() + '\'' +
                ", descrizione='" + getDescrizione() + '\'' +
                ", costo=" + getCosto() +
                ", pathImg='" + getPathImmagine() + '\'' +
                ", effetti=" + getEffetti() +
                ", triggers=" + getTriggers() +
                '}';
    }
}
