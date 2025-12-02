package carte;

import java.util.List;
import gestoreEffetti.Effetto;
import gestoreEffetti.Trigger;

public class CartaArteOscure extends Carta {
    private String pathImg;
    private List<Trigger> triggers;

    // il costo e la descrizione sono spesso nulli nelle arti oscure (puoi gestirli con default)
    public CartaArteOscure(
        String nome,
        String id,
        String classe, // quasi sempre "ArtiOscure"
        String pathImg,
        List<Effetto> effetti, // normalmente vuota, la logica passa dai trigger
        List<Trigger> triggers
    ) {
        // Passa null o valori di default per descrizione e costo
        super(nome, id, classe, null, 0, pathImg, effetti, triggers);
        this.pathImg = pathImg;
        this.triggers = triggers;
    }

    public String getPathImg() {
        return pathImg;
    }

    public void setPathImg(String pathImg) {
        this.pathImg = pathImg;
    }

    @Override
    public List<Trigger> getTriggers() {
        return triggers;
    }

    @Override
    public void setTriggers(List<Trigger> triggers) {
        this.triggers = triggers;
    }

    // Se vuoi gestire la logica specifica delle Arti Oscure, aggiungi qui i tuoi metodi
}
