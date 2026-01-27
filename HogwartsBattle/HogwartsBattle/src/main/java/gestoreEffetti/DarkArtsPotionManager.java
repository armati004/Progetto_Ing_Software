package gioco;

import carte.DarkArtsPotion;
import carte.Ingrediente;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gestisce le Dark Arts Potions del Pack 3.
 * Le Dark Arts Potions sono carte Arti Oscure che:
 * - Vengono posizionate face up davanti all'eroe che le pesca
 * - Hanno effetto ongoing ogni turno
 * - Possono essere "brewed" aggiungendo ingredienti
 * - Vengono rimosse quando completate
 */
public class DarkArtsPotionManager {
    private StatoDiGioco stato;
    
    // Map: Giocatore -> Lista di Dark Arts Potions davanti a lui
    private Map<Giocatore, List<DarkArtsPotion>> darkArtsPotionsPerGiocatore;
    
    public DarkArtsPotionManager(StatoDiGioco stato) {
        this.stato = stato;
        this.darkArtsPotionsPerGiocatore = new HashMap<>();
    }
    
    /**
     * Aggiunge una Dark Arts Potion davanti a un giocatore.
     * Viene chiamato quando viene pescata come evento Arti Oscure.
     */
    public void aggiungiDarkArtsPotion(Giocatore giocatore, DarkArtsPotion pozione) {
        if (!darkArtsPotionsPerGiocatore.containsKey(giocatore)) {
            darkArtsPotionsPerGiocatore.put(giocatore, new ArrayList<>());
        }
        
        pozione.setProprietario(giocatore);
        darkArtsPotionsPerGiocatore.get(giocatore).add(pozione);
        
        System.out.println("☠️ Dark Arts Potion posizionata davanti a " + giocatore.getEroe().getNome() + ": " + pozione.getNome());
        System.out.println("   Ingredienti richiesti per rimuoverla: " + pozione.getIngredientiMancanti());
    }
    
    /**
     * Risolve gli effetti ongoing di tutte le Dark Arts Potions davanti a un giocatore.
     * Viene chiamato all'inizio del turno del giocatore (prima di giocare carte).
     */
    public void risolviEffettiOngoing(Giocatore giocatore) {
        List<DarkArtsPotion> pozioni = darkArtsPotionsPerGiocatore.get(giocatore);
        
        if (pozioni == null || pozioni.isEmpty()) {
            return;
        }
        
        System.out.println("\n☠️ Risolvo Dark Arts Potions davanti a " + giocatore.getEroe().getNome());
        
        for (DarkArtsPotion pozione : pozioni) {
            if (!pozione.isCompletata()) {
                System.out.println("   → " + pozione.getNome());
                pozione.applicaEffetto(stato, giocatore);
            }
        }
    }
    
    /**
     * Aggiunge un ingrediente a una Dark Arts Potion.
     * Qualsiasi giocatore può farlo.
     */
    public boolean aggiungiIngrediente(DarkArtsPotion pozione, Ingrediente ingrediente) {
        boolean successo = pozione.aggiungiIngrediente(ingrediente);
        
        if (successo && pozione.isCompletata()) {
            // Rimuovi la pozione completata
            rimuoviDarkArtsPotion(pozione);
        }
        
        return successo;
    }
    
    /**
     * Rimuove una Dark Arts Potion completata.
     */
    private void rimuoviDarkArtsPotion(DarkArtsPotion pozione) {
        Giocatore proprietario = pozione.getProprietario();
        
        if (proprietario != null && darkArtsPotionsPerGiocatore.containsKey(proprietario)) {
            darkArtsPotionsPerGiocatore.get(proprietario).remove(pozione);
            System.out.println("✅ Dark Arts Potion rimossa da " + proprietario.getEroe().getNome());
        }
    }
    
    /**
     * Ottiene tutte le Dark Arts Potions davanti a un giocatore.
     */
    public List<DarkArtsPotion> getDarkArtsPotions(Giocatore giocatore) {
        return darkArtsPotionsPerGiocatore.getOrDefault(giocatore, new ArrayList<>());
    }
    
    /**
     * Ottiene tutte le Dark Arts Potions attive nel gioco.
     */
    public List<DarkArtsPotion> getTutteDarkArtsPotions() {
        List<DarkArtsPotion> tutte = new ArrayList<>();
        for (List<DarkArtsPotion> lista : darkArtsPotionsPerGiocatore.values()) {
            tutte.addAll(lista);
        }
        return tutte;
    }
    
    /**
     * Conta quante Dark Arts Potions face up ci sono nel gioco.
     * Usato per condizioni di Encounter.
     */
    public int contaDarkArtsPotionsFaceUp() {
        int count = 0;
        for (List<DarkArtsPotion> lista : darkArtsPotionsPerGiocatore.values()) {
            for (DarkArtsPotion pozione : lista) {
                if (!pozione.isCompletata()) {
                    count++;
                }
            }
        }
        return count;
    }
    
    /**
     * Verifica se un giocatore ha almeno una Dark Arts Potion davanti.
     */
    public boolean hasDarkArtsPotions(Giocatore giocatore) {
        List<DarkArtsPotion> pozioni = darkArtsPotionsPerGiocatore.get(giocatore);
        return pozioni != null && !pozioni.isEmpty() && 
               pozioni.stream().anyMatch(p -> !p.isCompletata());
    }
}
