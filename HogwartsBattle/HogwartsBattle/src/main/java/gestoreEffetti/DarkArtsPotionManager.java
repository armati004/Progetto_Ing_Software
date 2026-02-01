package gestoreEffetti;

import carte.DarkArtsPotion;
import carte.Ingrediente;
import gioco.Giocatore;
import gioco.StatoDiGioco;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gestisce le Dark Arts Potions del Pack 3.
 * Le Dark Arts Potions sono carte Arti Oscure che:
 * - Vengono posizionate face up davanti all'eroe che le pesca
 * - Hanno effetto ongoing ogni turno
 * - Possono essere "brewed" aggiungendo ingredienti per rimuoverle
 * - Bloccano alcune abilità finché sono attive
 * 
 * Sistema Dark Arts Potions - Pack 3+
 */
public class DarkArtsPotionManager {
    private StatoDiGioco stato;
    
    // Map: Giocatore -> Lista di Dark Arts Potions davanti a lui
    private Map<Giocatore, List<DarkArtsPotion>> darkArtsPotionsPerGiocatore;
    
    /**
     * Costruttore del manager delle Dark Arts Potions.
     * 
     * @param stato Stato di gioco corrente
     */
    public DarkArtsPotionManager(StatoDiGioco stato) {
        this.stato = stato;
        this.darkArtsPotionsPerGiocatore = new HashMap<>();
    }
    
    /**
     * Aggiunge una Dark Arts Potion davanti a un giocatore.
     * Viene chiamato quando viene pescata come evento Arti Oscure.
     * 
     * @param giocatore Giocatore che riceve la Dark Arts Potion
     * @param pozione Dark Arts Potion da aggiungere
     */
    public void aggiungiDarkArtsPotion(Giocatore giocatore, DarkArtsPotion pozione) {
        if (!darkArtsPotionsPerGiocatore.containsKey(giocatore)) {
            darkArtsPotionsPerGiocatore.put(giocatore, new ArrayList<>());
        }
        
        pozione.setProprietario(giocatore);
        darkArtsPotionsPerGiocatore.get(giocatore).add(pozione);
        
        System.out.println("☠️ Dark Arts Potion posizionata davanti a " + 
                          giocatore.getEroe().getNome() + ": " + pozione.getNome());
        System.out.println("   Ingredienti richiesti per rimuoverla: " + 
                          pozione.getIngredientiMancanti());
    }
    
    /**
     * Risolve gli effetti ongoing di tutte le Dark Arts Potions davanti a un giocatore.
     * Viene chiamato all'inizio del turno del giocatore (prima di giocare carte).
     * 
     * @param giocatore Giocatore di cui risolvere le Dark Arts Potions
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
     * Qualsiasi giocatore può farlo durante la fase apposita.
     * 
     * @param pozione Dark Arts Potion a cui aggiungere l'ingrediente
     * @param ingrediente Ingrediente da aggiungere
     * @return true se l'ingrediente è stato aggiunto con successo
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
     * 
     * @param pozione Dark Arts Potion da rimuovere
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
     * 
     * @param giocatore Giocatore di cui ottenere le pozioni
     * @return Lista di Dark Arts Potions (può essere vuota)
     */
    public List<DarkArtsPotion> getDarkArtsPotions(Giocatore giocatore) {
        return darkArtsPotionsPerGiocatore.getOrDefault(giocatore, new ArrayList<>());
    }
    
    /**
     * Ottiene tutte le Dark Arts Potions attive nel gioco.
     * 
     * @return Lista di tutte le Dark Arts Potions
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
     * 
     * @return Numero totale di Dark Arts Potions non completate
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
     * 
     * @param giocatore Giocatore da verificare
     * @return true se il giocatore ha Dark Arts Potions attive
     */
    public boolean hasDarkArtsPotions(Giocatore giocatore) {
        List<DarkArtsPotion> pozioni = darkArtsPotionsPerGiocatore.get(giocatore);
        return pozioni != null && !pozioni.isEmpty() && 
               pozioni.stream().anyMatch(p -> !p.isCompletata());
    }

    /**
     * Assegna una pozione a un giocatore e la registra nel manager.
     * 
     * @param potion Dark Arts Potion da assegnare
     * @param giocatore Giocatore che riceve la pozione
     */
    public void assegnaPozioneAlGiocatore(DarkArtsPotion potion, Giocatore giocatore) {
        giocatore.aggiungiDarkArtsPotion(potion);
        potion.setProprietario(giocatore);
        
        if (!darkArtsPotionsPerGiocatore.containsKey(giocatore)) {
            darkArtsPotionsPerGiocatore.put(giocatore, new ArrayList<>());
        }
        darkArtsPotionsPerGiocatore.get(giocatore).add(potion);
    }

    /**
     * Rimuove tutte le pozioni da un giocatore.
     * Usato per effetti speciali o reset.
     * 
     * @param giocatore Giocatore da cui rimuovere le pozioni
     */
    public void rimuoviTuttePozioni(Giocatore giocatore) {
        giocatore.rimuoviTutteDarkArtsPotions();
        darkArtsPotionsPerGiocatore.remove(giocatore);
    }

    /**
     * Verifica se un giocatore può giocare carte Alleato.
     * Alcune Dark Arts Potions bloccano la possibilità di giocare Alleati.
     * 
     * @param giocatore Giocatore da verificare
     * @return true se il giocatore può giocare Alleati
     */
    public boolean puoGiocareAlleati(Giocatore giocatore) {
        List<DarkArtsPotion> pozioni = darkArtsPotionsPerGiocatore.get(giocatore);
        if (pozioni == null || pozioni.isEmpty()) {
            return true;
        }
        
        for (DarkArtsPotion pozione : pozioni) {
            if (pozione.isBloccaAlleati()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Verifica se un giocatore può completare (brew) pozioni.
     * Alcune Dark Arts Potions potrebbero bloccare questa abilità.
     * 
     * @param giocatore Giocatore da verificare
     * @return true se il giocatore può brewre pozioni
     */
    public boolean puoBrew(Giocatore giocatore) {
        // Implementazione base: sempre true
        // Può essere esteso per gestire blocchi specifici
        return true;
    }
}
