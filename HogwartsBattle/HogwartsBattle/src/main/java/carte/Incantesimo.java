package carte;

import java.util.List;
import gestoreEffetti.Effetto;
import gestoreEffetti.Trigger;

/**
 * Classe Incantesimo - Rappresenta le carte di tipo Spell.
 * Estende Carta per integrarsi nel sistema esistente.
 */
public class Incantesimo extends Carta {

    /**
     * Costruttore per creare un Incantesimo.
     * 
     * @param nome Nome dell'incantesimo
     * @param id ID univoco
     * @param classe Tipo di carta ("Incantesimo")
     * @param descrizione Descrizione testuale dell'effetto
     * @param costo Costo in influenza per acquistare
     * @param pathImmagine Path dell'immagine
     * @param effetti Lista degli effetti applicati
     * @param triggers Lista dei trigger associati
     */
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
