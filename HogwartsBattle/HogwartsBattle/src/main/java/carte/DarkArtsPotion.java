package carte;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gestoreEffetti.Effetto;
import gestoreEffetti.Trigger;
import gioco.Giocatore;
import gioco.StatoDiGioco;

/**
 * Rappresenta una Dark Arts Potion del Pack 3.
 * Queste sono carte Arti Oscure speciali che:
 * 1. Vengono pescate come eventi Arti Oscure
 * 2. Vanno posizionate face up davanti all'eroe che le pesca
 * 3. Hanno effetto ONGOING (si risolvono ogni turno)
 * 4. Possono essere "brewed" aggiungendo ingredienti
 * 5. Quando completate, vengono rimosse
 * 
 * Estende ArteOscura per integrarsi nel mazzo Arti Oscure.
 */
public class DarkArtsPotion extends ArteOscura {
    // Ingredienti richiesti per completare (brewre) la pozione
    private List<TipoIngrediente> ingredientiRichiesti;
    
    // Ingredienti attualmente aggiunti
    private Map<TipoIngrediente, Integer> ingredientiAttuali;
    
    // Giocatore che ha questa pozione davanti
    private Giocatore proprietario;
    
    // Stato
    private boolean completata;
    private boolean bloccaAlleati;
    
    /**
     * Costruttore di default senza argomenti.
     * Utile per i test.
     */
    public DarkArtsPotion() {
        super("Dark Arts Potion", "DAP_DEFAULT", "ArteOscura", 
              "Dark Arts Potion di default", 0, "", 
              new ArrayList<>(), new ArrayList<>());
        
        this.ingredientiRichiesti = new ArrayList<>();
        this.ingredientiAttuali = new HashMap<>();
        this.completata = false;
        this.bloccaAlleati = false;
    }
    
    /**
     * Costruttore completo per Dark Arts Potion.
     */
    public DarkArtsPotion(String nome, String id, String classe, String descrizione, 
                         int costo, String pathImmagine, List<Effetto> effetti, 
                         List<Trigger> triggers, List<TipoIngrediente> ingredientiRichiesti) {
        
        super(nome, id, classe, descrizione, costo, pathImmagine, effetti, triggers);
        
        this.ingredientiRichiesti = ingredientiRichiesti != null ? ingredientiRichiesti : new ArrayList<>();
        this.ingredientiAttuali = new HashMap<>();
        this.completata = false;
        this.bloccaAlleati = false;
    }
    
    /**
     * Applica l'effetto ongoing della Dark Arts Potion.
     * Viene chiamato all'inizio di ogni turno del proprietario.
     */
    @Override
    public void applicaEffetto(StatoDiGioco stato, Giocatore attivo) {
        if (completata) {
            return;
        }
        
        System.out.println("☠️ Dark Arts Potion ongoing: " + getNome());
        
        // Applica gli effetti ongoing
        super.applicaEffetto(stato, attivo);
    }
    
    /**
     * Aggiunge un ingrediente alla Dark Arts Potion.
     * Qualsiasi eroe può farlo, non solo il proprietario.
     */
    public boolean aggiungiIngrediente(Ingrediente ingrediente) {
        if (completata) {
            System.out.println("⚠️ Questa Dark Arts Potion è già completata!");
            return false;
        }
        
        if (ingrediente == null) {
            System.out.println("⚠️ Ingrediente null!");
            return false;
        }
        
        // Trova quale tipo di ingrediente manca
        for (TipoIngrediente richiesto : ingredientiRichiesti) {
            int richiesti = contaIngredientiRichiesti(richiesto);
            int attuali = ingredientiAttuali.getOrDefault(richiesto, 0);
            
            if (attuali < richiesti) {
                // Verifica se l'ingrediente fornito può soddisfare questo requisito
                if (ingrediente.soddisfaRequisito(richiesto)) {
                    ingredientiAttuali.put(richiesto, attuali + 1);
                    ingrediente.setUsato(true);
                    
                    System.out.println("✓ Aggiunto " + ingrediente.getTipo() + " a Dark Arts Potion: " + getNome());
                    
                    // Verifica se la pozione è completa
                    if (verificaCompletamento()) {
                        completata = true;
                        System.out.println("✅ Dark Arts Potion completata e rimossa: " + getNome());
                    }
                    
                    return true;
                }
            }
        }
        
        System.out.println("⚠️ Questo ingrediente non serve per questa Dark Arts Potion!");
        return false;
    }
    
    /**
     * Conta quanti ingredienti di un certo tipo sono richiesti.
     */
    private int contaIngredientiRichiesti(TipoIngrediente tipo) {
        int count = 0;
        for (TipoIngrediente richiesto : ingredientiRichiesti) {
            if (richiesto == tipo) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Verifica se tutti gli ingredienti richiesti sono stati aggiunti.
     */
    private boolean verificaCompletamento() {
        for (TipoIngrediente richiesto : ingredientiRichiesti) {
            int richiesti = contaIngredientiRichiesti(richiesto);
            int attuali = ingredientiAttuali.getOrDefault(richiesto, 0);
            
            if (attuali < richiesti) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Ottiene una rappresentazione testuale degli ingredienti mancanti.
     */
    public String getIngredientiMancanti() {
        StringBuilder sb = new StringBuilder();
        Map<TipoIngrediente, Integer> mancanti = new HashMap<>();
        
        for (TipoIngrediente richiesto : ingredientiRichiesti) {
            int richiesti = contaIngredientiRichiesti(richiesto);
            int attuali = ingredientiAttuali.getOrDefault(richiesto, 0);
            int mancante = richiesti - attuali;
            
            if (mancante > 0) {
                mancanti.put(richiesto, mancante);
            }
        }
        
        if (mancanti.isEmpty()) {
            return "Completa!";
        }
        
        for (Map.Entry<TipoIngrediente, Integer> entry : mancanti.entrySet()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(entry.getValue()).append("x ").append(entry.getKey().getSimbolo());
        }
        
        return sb.toString();
    }
    
    // ============================================================================
    // GETTERS E SETTERS
    // ============================================================================
    
    public List<TipoIngrediente> getIngredientiRichiesti() {
        return ingredientiRichiesti;
    }
    
    public void setIngredientiRichiesti(List<TipoIngrediente> ingredientiRichiesti) {
        this.ingredientiRichiesti = ingredientiRichiesti;
    }
    
    public Map<TipoIngrediente, Integer> getIngredientiAttuali() {
        return ingredientiAttuali;
    }
    
    public Giocatore getProprietario() {
        return proprietario;
    }
    
    public void setProprietario(Giocatore proprietario) {
        this.proprietario = proprietario;
    }
    
    public boolean isCompletata() {
        return completata;
    }
    
    public void setCompletata(boolean completata) {
        this.completata = completata;
    }
    
    public boolean isBloccaAlleati() {
        return bloccaAlleati;
    }
    
    public void setBloccaAlleati(boolean bloccaAlleati) {
        this.bloccaAlleati = bloccaAlleati;
    }
    
    public void setEffettiOngoing(List<Effetto> effetti) {
        if (effetti != null) {
            this.setEffetti(effetti);
        }
    }
    
    @Override
    public String toString() {
        return "☠️ " + getNome() + " - " + 
               (completata ? "✓ Completa" : "Mancano: " + getIngredientiMancanti());
    }
}
