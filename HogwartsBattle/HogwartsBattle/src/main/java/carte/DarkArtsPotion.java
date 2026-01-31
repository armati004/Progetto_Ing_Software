package carte;

import java.util.ArrayList;
import java.util.List;

import gestoreEffetti.Effetto;
import gestoreEffetti.Trigger;
import gioco.Giocatore;
import gioco.StatoDiGioco;

/**
 * DarkArtsPotion - Carte Arti Oscure speciali (Pack 3+)
 * 
 * Differenze dalle Arti Oscure normali:
 * - Hanno effetti ONGOING (si risolvono ogni turno del giocatore)
 * - Rimangono davanti al giocatore finché non vengono rimosse
 * - Possono bloccare certe azioni (es. giocare alleati)
 */
public class DarkArtsPotion extends ArteOscura {
    
    // Effetti che si risolvono ogni turno
    private List<Effetto> effettiOngoing;
    
    // Flag per bloccare azioni specifiche
    private boolean bloccaAlleati;
    private boolean bloccaIncantesimi;
    private boolean bloccaOggetti;
    
    // Giocatore che ha questa pozione
    private Giocatore proprietario;
    
    public DarkArtsPotion() {
        super();
        this.effettiOngoing = new ArrayList<>();
        this.bloccaAlleati = false;
        this.bloccaIncantesimi = false;
        this.bloccaOggetti = false;
    }
    
    public DarkArtsPotion(String nome, String id, String classe, String descrizione, 
                         int costo, String pathImmagine, List<Effetto> effetti, 
                         List<Trigger> triggers) {
        super(nome, id, classe, descrizione, costo, pathImmagine, effetti, triggers);
        this.effettiOngoing = new ArrayList<>();
        this.bloccaAlleati = false;
        this.bloccaIncantesimi = false;
        this.bloccaOggetti = false;
    }
    
    /**
     * Applica l'effetto ONGOING della Dark Arts Potion.
     * Chiamato all'inizio di ogni turno del proprietario.
     */
    public void applicaEffettoOngoing(StatoDiGioco stato, Giocatore giocatore) {
        if (effettiOngoing == null || effettiOngoing.isEmpty()) {
            return;
        }
        
        System.out.println("☠️ Dark Arts Potion ongoing: " + getNome());
        
        for (Effetto effetto : effettiOngoing) {
            gestoreEffetti.EsecutoreEffetti.eseguiEffetto(effetto, stato, giocatore);
        }
    }
    
    /**
     * Applica l'effetto normale quando viene pescata.
     */
    @Override
    public void applicaEffetto(StatoDiGioco stato, Giocatore giocatore) {
        // L'effetto normale è stato già applicato quando è stata pescata
        // Qui non facciamo nulla perché l'effetto ongoing viene gestito separatamente
        System.out.println("☠️ " + giocatore.getEroe().getNome() + " riceve Dark Arts Potion: " + getNome());
    }
    
    // Getters e Setters
    
    public List<Effetto> getEffettiOngoing() {
        return effettiOngoing;
    }
    
    public void setEffettiOngoing(List<Effetto> effettiOngoing) {
        this.effettiOngoing = effettiOngoing;
    }
    
    public boolean bloccaAlleati() {
        return bloccaAlleati;
    }
    
    public void setBloccaAlleati(boolean bloccaAlleati) {
        this.bloccaAlleati = bloccaAlleati;
    }
    
    public boolean bloccaIncantesimi() {
        return bloccaIncantesimi;
    }
    
    public void setBloccaIncantesimi(boolean bloccaIncantesimi) {
        this.bloccaIncantesimi = bloccaIncantesimi;
    }
    
    public boolean bloccaOggetti() {
        return bloccaOggetti;
    }
    
    public void setBloccaOggetti(boolean bloccaOggetti) {
        this.bloccaOggetti = bloccaOggetti;
    }
    
    public Giocatore getProprietario() {
        return proprietario;
    }
    
    public void setProprietario(Giocatore proprietario) {
        this.proprietario = proprietario;
    }
    
    @Override
    public String toString() {
        return "☠️ Dark Arts Potion: " + getNome() + 
               (bloccaAlleati ? " [Blocca Alleati]" : "") +
               (bloccaIncantesimi ? " [Blocca Incantesimi]" : "") +
               (bloccaOggetti ? " [Blocca Oggetti]" : "");
    }

	public void setDescrizione(String string) {
		// TODO Auto-generated method stub
		
	}

	public void setNome(String nome) {
		// TODO Auto-generated method stub
		
	}
}
