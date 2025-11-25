package carte;

import java.util.List;
import gestoreEffetti.Effetto;
import gestoreEffetti.Trigger;

public class Incantesimo extends Carta {
    private String pathImg;

    public Incantesimo(
        String nome,
        String id,
        String classe,          // "Incantesimo"
        String descrizione,     // opzionale, può essere null
        int costo,              // opzionale, per alcune carte può essere 0
        String pathImg,
        List<Effetto> effetti,
        List<Trigger> triggers
    ) {
        super(nome, id, classe, descrizione, costo, pathImg, effetti, triggers);
        this.pathImg = pathImg;
    }

    public String getPathImg() { return pathImg; }
    public void setPathImg(String pathImg) { this.pathImg = pathImg; }

    @Override
    public String toString() {
        return "Incantesimo{" +
                "nome='" + getNome() + '\'' +
                ", id='" + getId() + '\'' +
                ", classe='" + getClasse() + '\'' +
                ", descrizione='" + getDescrizione() + '\'' +
                ", costo=" + getCosto() +
                ", pathImg='" + pathImg + '\'' +
                ", effetti=" + getEffetti() +
                ", triggers=" + getTriggers() +
                '}';
    }
}
