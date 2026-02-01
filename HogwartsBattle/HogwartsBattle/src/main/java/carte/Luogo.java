package carte;

import java.util.List;
import gestoreEffetti.Effetto;
import gestoreEffetti.Trigger;

public class Luogo extends Carta {
    private int numeroMarchiNeri;
    private int nDarkEvents;
    private int marchiNeriMax;
    private Effetto effettoEntrata; // Puoi tipizzare con una classe EffettoEntrata se hai già una struttura

    public Luogo(
        String nome,
        String id,
        String classe,
        String descrizione,
        int costo,
        String pathImmagine,
        List<Effetto> effetti,
        List<Trigger> triggers,
        int numeroMarchiNeri,
        int nDarkEvents,
        int marchiNeriMax,
        Effetto effettoEntrata
    ) {
        super(nome, id, classe, descrizione, costo, pathImmagine, effetti, triggers);
        this.numeroMarchiNeri = 0;
        this.nDarkEvents = nDarkEvents;
        this.marchiNeriMax = marchiNeriMax;
        this.effettoEntrata = effettoEntrata;
    }

    public int getNumeroMarchiNeri() {
        return numeroMarchiNeri;
    }

    public int getNDarkEvents() {
        return nDarkEvents;
    }

    public int getMarchiNeriMax() {
        return marchiNeriMax;
    }

    public Effetto getEffettoEntrata() {
        return effettoEntrata;
    }

    public void setNumeroMarchiNeri(int numeroMarchiNeri) {
        this.numeroMarchiNeri = numeroMarchiNeri;
    }

    public void setNDarkEvents(int nDarkEvents) {
        this.nDarkEvents = nDarkEvents;
    }

    public void setMarchiNeriMax(int marchiNeriMax) {
        this.marchiNeriMax = marchiNeriMax;
    }

    public void setEffettoEntrata(Effetto effettoEntrata) {
        this.effettoEntrata = effettoEntrata;
    }

	public boolean aggiungiMarchioNero(Integer marchi) {
		this.setNumeroMarchiNeri(this.getNumeroMarchiNeri() + marchi);
		if(this.getNumeroMarchiNeri() >= this.getMarchiNeriMax()) {
			return true;
		}
		else {
			return false;
		}
	}

	public void rimuoviMarchioNero(Integer qta) {
		if(this.getNumeroMarchiNeri() > 0) {
			this.setNumeroMarchiNeri(this.getNumeroMarchiNeri() - qta);
		}
	}

    
}
