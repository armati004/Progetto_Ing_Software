package carte;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gestoreEffetti.Effetto;
import gestoreEffetti.Trigger;

/**
 * Rappresenta una carta Pozione.
 * Le Pozioni richiedono ingredienti per essere completate (brewed).
 * Una volta completate possono essere giocate con effetto normale o banish.
 * 
 * Estende Carta per integrarsi nel sistema esistente.
 */
public class Pozione extends Carta {
    private int pack; // Pack di appartenenza (2, 3, 4)
    
    // Ingredienti richiesti per completare
    private List<TipoIngrediente> ingredientiRichiesti;
    
    // Ingredienti attualmente aggiunti
    private Map<TipoIngrediente, Integer> ingredientiAttuali;
    
    // Effetti della pozione
    private String descrizioneEffettoNormale; // Primo effetto (carta resta nel deck)
    private String descrizioneEffettoBanish;  // Secondo effetto (carta viene bannata)
    private List<Effetto> effettoNormale;
    private List<Effetto> effettoBanish;
    
    // Stato
    private boolean completata;
    private boolean darkArtsPotion; // Per Pack 3 - Pozioni Arti Oscure con effetto ongoing
    
    /**
     * Costruttore completo per Pozione.
     */
    public Pozione(String nome, String id, String classe, String descrizione, int costo, String pathImmagine,
                   List<Effetto> effetti, List<Trigger> triggers, int pack,
                   List<TipoIngrediente> ingredientiRichiesti,
                   String descrizioneEffettoNormale, List<Effetto> effettoNormale,
                   String descrizioneEffettoBanish, List<Effetto> effettoBanish,
                   boolean darkArtsPotion) {
        
        super(nome, id, classe, descrizione, costo, pathImmagine, effetti, triggers);
        
        this.pack = pack;
        this.ingredientiRichiesti = ingredientiRichiesti != null ? ingredientiRichiesti : new ArrayList<>();
        this.ingredientiAttuali = new HashMap<>();
        this.descrizioneEffettoNormale = descrizioneEffettoNormale;
        this.effettoNormale = effettoNormale != null ? effettoNormale : new ArrayList<>();
        this.descrizioneEffettoBanish = descrizioneEffettoBanish;
        this.effettoBanish = effettoBanish != null ? effettoBanish : new ArrayList<>();
        this.completata = false;
        this.darkArtsPotion = darkArtsPotion;
    }
    
    /**
     * Aggiunge un ingrediente alla pozione.
     * @return true se l'ingrediente è stato aggiunto con successo
     */
    public boolean aggiungiIngrediente(Ingrediente ingrediente) {
        if (completata) {
            System.out.println("⚠️ La pozione è già completata!");
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
                    
                    System.out.println("✓ Aggiunto " + ingrediente.getTipo() + " a " + getNome());
                    
                    // Verifica se la pozione è completa
                    if (verificaCompletamento()) {
                        completata = true;
                        System.out.println("🧪 " + getNome() + " è stata completata!");
                    }
                    
                    return true;
                }
            }
        }
        
        System.out.println("⚠️ Questo ingrediente non serve per questa pozione!");
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
    
    // Getters e Setters
    
    public int getPack() {
        return pack;
    }
    
    public void setPack(int pack) {
        this.pack = pack;
    }
    
    public List<TipoIngrediente> getIngredientiRichiesti() {
        return ingredientiRichiesti;
    }
    
    public void setIngredientiRichiesti(List<TipoIngrediente> ingredientiRichiesti) {
        this.ingredientiRichiesti = ingredientiRichiesti;
    }
    
    public Map<TipoIngrediente, Integer> getIngredientiAttuali() {
        return ingredientiAttuali;
    }
    
    public String getDescrizioneEffettoNormale() {
        return descrizioneEffettoNormale;
    }
    
    public void setDescrizioneEffettoNormale(String descrizioneEffettoNormale) {
        this.descrizioneEffettoNormale = descrizioneEffettoNormale;
    }
    
    public List<Effetto> getEffettoNormale() {
        return effettoNormale;
    }
    
    public void setEffettoNormale(List<Effetto> effettoNormale) {
        this.effettoNormale = effettoNormale;
    }
    
    public String getDescrizioneEffettoBanish() {
        return descrizioneEffettoBanish;
    }
    
    public void setDescrizioneEffettoBanish(String descrizioneEffettoBanish) {
        this.descrizioneEffettoBanish = descrizioneEffettoBanish;
    }
    
    public List<Effetto> getEffettoBanish() {
        return effettoBanish;
    }
    
    public void setEffettoBanish(List<Effetto> effettoBanish) {
        this.effettoBanish = effettoBanish;
    }
    
    public boolean isCompletata() {
        return completata;
    }
    
    public void setCompletata(boolean completata) {
        this.completata = completata;
    }
    
    public boolean isDarkArtsPotion() {
        return darkArtsPotion;
    }
    
    public void setDarkArtsPotion(boolean darkArtsPotion) {
        this.darkArtsPotion = darkArtsPotion;
    }
    
    @Override
    public String toString() {
        return getNome() + " - " + (completata ? "✓ Completa" : "Mancano: " + getIngredientiMancanti());
    }
}
