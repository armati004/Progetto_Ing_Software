package carte;

import java.util.ArrayList;
import java.util.List;

import gestoreEffetti.Effetto;
import gestoreEffetti.Trigger;
import gioco.Giocatore;
import gioco.StatoDiGioco;

/**
 * Pozione - Carte che si brewano (Pack 2+)
 */
public class Pozione extends Carta {
    
    private List<String> ingredientiRichiesti;
    private List<Effetto> effetti;
    private List<Effetto> effettiBanish;
    private int pack;
    
    public Pozione() {
        super();
        this.ingredientiRichiesti = new ArrayList<>();
        this.effetti = new ArrayList<>();
        this.effettiBanish = new ArrayList<>();
        this.pack = 2;
    }
    
    public Pozione(String nome, String id, String classe, String descrizione, 
                  int costo, String pathImmagine, List<Effetto> effetti, 
                  List<Trigger> triggers) {
        super(nome, id, classe, descrizione, costo, pathImmagine, effetti, triggers);
        this.ingredientiRichiesti = new ArrayList<>();
        this.effetti = effetti != null ? effetti : new ArrayList<>();
        this.effettiBanish = new ArrayList<>();
        this.pack = 2;
    }
    
    @Override
    public void applicaEffetto(StatoDiGioco stato, Giocatore giocatore) {
        if (effetti == null || effetti.isEmpty()) {
            return;
        }
        
        for (Effetto effetto : effetti) {
            gestoreEffetti.EsecutoreEffetti.eseguiEffetto(effetto, stato, giocatore);
        }
    }
    
    public void applicaEffettoBanish(StatoDiGioco stato, Giocatore giocatore) {
        if (effettiBanish == null || effettiBanish.isEmpty()) {
            applicaEffetto(stato, giocatore);
            return;
        }
        
        for (Effetto effetto : effettiBanish) {
            gestoreEffetti.EsecutoreEffetti.eseguiEffetto(effetto, stato, giocatore);
        }
    }
    
    public List<String> getIngredientiRichiesti() {
        return ingredientiRichiesti;
    }
    
    public void setIngredientiRichiesti(List<String> ingredientiRichiesti) {
        this.ingredientiRichiesti = ingredientiRichiesti;
    }
    
    @Override
    public List<Effetto> getEffetti() {
        return effetti;
    }
    
    @Override
    public void setEffetti(List<Effetto> effetti) {
        this.effetti = effetti;
    }
    
    public List<Effetto> getEffettiBanish() {
        return effettiBanish;
    }
    
    public void setEffettiBanish(List<Effetto> effettiBanish) {
        this.effettiBanish = effettiBanish;
    }
    
    public int getPack() {
        return pack;
    }
    
    public void setPack(int pack) {
        this.pack = pack;
    }
}
