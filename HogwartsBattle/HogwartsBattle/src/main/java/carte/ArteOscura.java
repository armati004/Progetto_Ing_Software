package carte;

import java.util.List;
import gestoreEffetti.Effetto;
import gestoreEffetti.Trigger;

public class ArteOscura extends Carta {
   // private String pathImg;
   // private List<Trigger> triggers;

    // il costo e la descrizione sono spesso nulli nelle arti oscure (puoi gestirli con default)
    public ArteOscura(String nome, String id, String classe, String descrizione,
            String pathImmagine, List<Effetto> effetti, List<Trigger> triggers){
        // Passa null o valori di default per descrizione e costo
        super(nome, id, classe, descrizione, 0, pathImmagine, effetti, triggers);
        //this.pathImg = pathImg;
        //this.triggers = triggers;
    }

    /*public String getPathImg() {
        return pathImg;
    }

    public void setPathImg(String pathImg) {
        this.pathImg = pathImg;
    }*/

   /* @Override
    public List<Trigger> getTriggers() {
        return triggers;
    }

    @Override
    public void setTriggers(List<Trigger> triggers) {
        this.triggers = triggers;
    }*/

    // Se vuoi gestire la logica specifica delle Arti Oscure, aggiungi qui i tuoi metodi
}
