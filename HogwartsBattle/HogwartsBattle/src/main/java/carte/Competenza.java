package carte;

import java.util.List;
import gestoreEffetti.Effetto;
import gestoreEffetti.Trigger;

public class Competenza extends Carta {
    private String tipo;              // Es: "Competenza"
    private boolean attivabile;       // True se si può attivare manualmente
    private List<Effetto> effetti;    // Effetti attivabili (se presenti, esempio Incanti, Trasfigurazione, Volo)
    private List<Trigger> triggers;   // Effetti passivi/triggerati
    private String commento;          // Descrizione extra/commento

    public Competenza(
        String nome,
        String id,
        String tipo,
        String descrizione,
        String pathImmagine,
        boolean attivabile,
        List<Effetto> effetti,
        List<Trigger> triggers,
        String commento
    ) {
        super(nome, id, tipo, descrizione, 0, pathImmagine, effetti, triggers);
        this.tipo = tipo;
        this.attivabile = attivabile;
        this.effetti = effetti;
        this.triggers = triggers;
        this.commento = commento;
    }

    public String getTipo() { return tipo; }
    public boolean isAttivabile() { return attivabile; }
    @Override
    public List<Effetto> getEffetti() { return effetti; }
    @Override
    public void setEffetti(List<Effetto> effetti) { this.effetti = effetti; }
    @Override
    public List<Trigger> getTriggers() { return triggers; }
    @Override
    public void setTriggers(List<Trigger> triggers) { this.triggers = triggers; }
    public String getCommento() { return commento; }
    public void setCommento(String commento) { this.commento = commento; }
}
