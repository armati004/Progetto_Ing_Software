package carte;

import java.util.List;
import com.google.gson.annotations.SerializedName;
import gioco.Giocatore;
import gioco.StatoDiGioco;
import gestoreEffetti.DurataEffetto;
import gestoreEffetti.Effetto;
import gestoreEffetti.EsecutoreEffetti;
import gestoreEffetti.Trigger;

public class Luogo extends Carta {
    private int numero;
    private int nDarkEvents;
    private int marchiNeriMax;
    private Object effettoEntrata; // Puoi tipizzare con una classe EffettoEntrata se hai già una struttura

    public Luogo(
        String nome,
        String id,
        String classe,
        String descrizione,
        int costo,
        String pathImmagine,
        List<Effetto> effetti,
        List<Trigger> triggers,
        int numero,
        int nDarkEvents,
        int marchiNeriMax,
        Object effettoEntrata
    ) {
        super(nome, id, classe, descrizione, costo, pathImmagine, effetti, triggers);
        this.numero = numero;
        this.nDarkEvents = nDarkEvents;
        this.marchiNeriMax = marchiNeriMax;
        this.effettoEntrata = effettoEntrata;
    }

    public int getNumero() {
        return numero;
    }

    public int getNDarkEvents() {
        return nDarkEvents;
    }

    public int getMarchiNeriMax() {
        return marchiNeriMax;
    }

    public Object getEffettoEntrata() {
        return effettoEntrata;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public void setNDarkEvents(int nDarkEvents) {
        this.nDarkEvents = nDarkEvents;
    }

    public void setMarchiNeriMax(int marchiNeriMax) {
        this.marchiNeriMax = marchiNeriMax;
    }

    public void setEffettoEntrata(Object effettoEntrata) {
        this.effettoEntrata = effettoEntrata;
    }

    // Puoi aggiungere metodi specifici per attivare effettoEntrata qui
}
