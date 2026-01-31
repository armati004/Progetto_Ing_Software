package gioco;

import carte.*;
import gestoreEffetti.EsecutoreEffetti;
import gioco.StatoDiGioco;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Gestisce il sistema completo delle pozioni:
 * - Potion Board con 2 pozioni disponibili
 * - 3 scaffali con ingredienti
 * - Requisiti per accedere agli scaffali
 * - Brewing (completamento pozioni)
 * - Riciclo ingredienti
 */
public class PotionManager {
    private StatoDiGioco stato;
    
    // Potion Board - Mazzo e pozioni visibili
    private List<Pozione> mazzoPozioni;
    private List<Pozione> pozioniDisponibili; // 2 pozioni visibili sulla board
    
    // Pozioni in progress (condivise tra tutti i giocatori)
    private List<Pozione> pozioniInProgress;
    
    // Ingredienti - Pool e scaffali
    private List<Ingrediente> poolIngredienti; // Ingredienti non ancora rivelati
    private List<List<Ingrediente>> scaffali; // 3 scaffali con max 3 ingredienti ciascuno
    private List<Ingrediente> ingredientiUsati; // Area ingredienti usati (per riciclo)
    
    // Requisiti scaffali
    private List<PotionShelfRequirement> requisitiScaffaliLatoA;
    private List<PotionShelfRequirement> requisitiScaffaliLatoB;
    private String latoCorrente; // "A" o "B"
    
    public PotionManager(StatoDiGioco stato) {
        this.stato = stato;
        this.mazzoPozioni = new ArrayList<>();
        this.pozioniDisponibili = new ArrayList<>();
        this.pozioniInProgress = new ArrayList<>();
        this.poolIngredienti = new ArrayList<>();
        this.scaffali = new ArrayList<>();
        this.ingredientiUsati = new ArrayList<>();
        this.requisitiScaffaliLatoA = new ArrayList<>();
        this.requisitiScaffaliLatoB = new ArrayList<>();
        this.latoCorrente = "A"; // Default
        
        // Inizializza 3 scaffali vuoti
        for (int i = 0; i < 3; i++) {
            scaffali.add(new ArrayList<>());
        }
        
        inizializzaRequisiti();
    }
    
    /**
     * Inizializza i requisiti per i 3 scaffali (lato A e B).
     */
    private void inizializzaRequisiti() {
        // LATO A - Più facile
        requisitiScaffaliLatoA.add(new PotionShelfRequirement(1, "A", 
            TipoRequisitoScaffale.ACQUISTA_CARTA_VALORE, 4, "Acquire a card of 4 or more"));
        requisitiScaffaliLatoA.add(new PotionShelfRequirement(2, "A", 
            TipoRequisitoScaffale.GIOCA_ITEMS, 2, "Play 2 Items"));
        requisitiScaffaliLatoA.add(new PotionShelfRequirement(3, "A", 
            TipoRequisitoScaffale.SCARTA_CARTA, 1, "Discard a card"));
        
        // LATO B - Più difficile
        requisitiScaffaliLatoB.add(new PotionShelfRequirement(1, "B", 
            TipoRequisitoScaffale.EROE_STORDITO, 1, "A Hero is Stunned"));
        requisitiScaffaliLatoB.add(new PotionShelfRequirement(2, "B", 
            TipoRequisitoScaffale.CURA_ALTRO_EROE, 1, "Heal another Hero"));
        requisitiScaffaliLatoB.add(new PotionShelfRequirement(3, "B", 
            TipoRequisitoScaffale.ASSEGNA_ATTACCO, 3, "Assign 3 or more attack"));
    }
    
    /**
     * Inizializza la Potion Board.
     */
    public void inizializza(List<Pozione> pozioni, String lato) {
        // Carica le pozioni
        mazzoPozioni.addAll(pozioni);
        Collections.shuffle(mazzoPozioni);
        
        // Rivela 2 pozioni
        if (mazzoPozioni.size() >= 2) {
            pozioniDisponibili.add(mazzoPozioni.remove(0));
            pozioniDisponibili.add(mazzoPozioni.remove(0));
        }
        
        // Crea pool di ingredienti (3 set completi = 18 token totali)
        for (int set = 0; set < 3; set++) {
            poolIngredienti.add(new Ingrediente(TipoIngrediente.BICORN_HORN));
            poolIngredienti.add(new Ingrediente(TipoIngrediente.MANDRAKE_LEAF));
            poolIngredienti.add(new Ingrediente(TipoIngrediente.FLOBBER_WORM));
            poolIngredienti.add(new Ingrediente(TipoIngrediente.HELLEBORE));
            poolIngredienti.add(new Ingrediente(TipoIngrediente.LACEWING_FLY));
            poolIngredienti.add(new Ingrediente(TipoIngrediente.WILD));
        }
        
        Collections.shuffle(poolIngredienti);
        
        // Imposta lato requisiti
        this.latoCorrente = lato;
        
        // Riempi scaffali inizialmente
        riempiScaffali();
        
        System.out.println("🧪 Potion Board inizializzata!");
        System.out.println("   Lato requisiti: " + latoCorrente);
        System.out.println("   Pozioni disponibili: " + pozioniDisponibili.size());
        System.out.println("   Pool ingredienti: " + poolIngredienti.size());
    }
    
    /**
     * Riempie gli scaffali con ingredienti dal pool.
     * Gli ingredienti scorrono verso il basso per riempire spazi vuoti.
     */
    private void riempiScaffali() {
        for (int i = 0; i < 3; i++) {
            List<Ingrediente> scaffale = scaffali.get(i);
            
            // Riempi fino a 3 ingredienti per scaffale
            while (scaffale.size() < 3 && !poolIngredienti.isEmpty()) {
                scaffale.add(poolIngredienti.remove(0));
            }
        }
    }
    
    /**
     * Fase 4 del turno: Gather Potion Ingredients.
     * Permette al giocatore di prendere 1 ingrediente da ogni scaffale se soddisfa i requisiti.
     */
    public void faseRaccoltaIngredienti() {
        System.out.println("\n🧪 === FASE RACCOLTA INGREDIENTI ===");
        System.out.println("Puoi prendere 1 ingrediente da ogni scaffale se soddisfi i requisiti:");
        System.out.println("(Controlla dall'alto verso il basso)");
        
        List<PotionShelfRequirement> requisiti = latoCorrente.equals("A") ? 
            requisitiScaffaliLatoA : requisitiScaffaliLatoB;
        
        for (int i = 0; i < 3; i++) {
            PotionShelfRequirement requisito = requisiti.get(i);
            List<Ingrediente> scaffale = scaffali.get(i);
            
            System.out.println("\n📦 Scaffale " + (i + 1) + ": " + requisito.getDescrizione());
            System.out.println("   Ingredienti disponibili: " + scaffale);
            
            if (verificaRequisitoScaffale(requisito)) {
                System.out.println("   ✓ Requisito soddisfatto! Puoi prendere 1 ingrediente.");
            } else {
                System.out.println("   ✗ Requisito NON soddisfatto");
            }
        }
        
        System.out.println("\n💡 Usa l'UI per selezionare gli ingredienti da prendere");
    }
    
    /**
     * Verifica se il requisito di uno scaffale è soddisfatto.
     */
    public boolean verificaRequisitoScaffale(PotionShelfRequirement requisito) {
        switch (requisito.getTipo()) {
            case ACQUISTA_CARTA_VALORE:
                return verificaAcquistoCartaValore(requisito.getValore());
                
            case GIOCA_ITEMS:
                return verificaItemsGiocati(requisito.getValore());
                
            case SCARTA_CARTA:
                // Deve essere fatto manualmente dall'UI
                return true;
                
            case CURA_ALTRO_EROE:
                return verificaCuraAltroEroe();
                
            case EROE_STORDITO:
                return verificaEroeStordito();
                
            case ASSEGNA_ATTACCO:
                return stato.getAttaccoAssegnatoQuestoTurno() >= requisito.getValore();
                
            case GIOCA_INCANTESIMI:
                return verificaIncantesimiGiocati(requisito.getValore());
                
            case ACQUISTA_INFLUENZA:
                return stato.getInfluenzaSpesaQuestoTurno() >= requisito.getValore();
                
            case NESSUNO:
                return true;
                
            default:
                return false;
        }
    }
    
    /**
     * Prende un ingrediente da uno scaffale (chiamato dall'UI).
     */
    public Ingrediente prendiIngrediente(int scaffaleIndex, int ingredienteIndex) {
        if (scaffaleIndex < 0 || scaffaleIndex >= 3) {
            System.out.println("⚠️ Indice scaffale non valido");
            return null;
        }
        
        List<Ingrediente> scaffale = scaffali.get(scaffaleIndex);
        if (ingredienteIndex < 0 || ingredienteIndex >= scaffale.size()) {
            System.out.println("⚠️ Indice ingrediente non valido");
            return null;
        }
        
        Ingrediente ingrediente = scaffale.remove(ingredienteIndex);
        System.out.println("✓ Preso: " + ingrediente.getTipo());
        
        return ingrediente;
    }
    
    /**
     * Aggiunge un ingrediente a una pozione.
     */
    public boolean aggiungiIngredienteAPozione(Ingrediente ingrediente, Pozione pozione) {
        boolean successo = pozione.aggiungiIngrediente(ingrediente);
        
        if (successo && pozione.isCompletata()) {
            // Pozione completata!
            System.out.println("🎉 Pozione completata: " + pozione.getNome());
            System.out.println("   Puoi giocarla subito (solo effetto banish) o metterla negli scarti");
        }
        
        return successo;
    }
    
    /**
     * Scarta un ingrediente raccolto (invece di aggiungerlo a una pozione).
     */
    public void scartaIngrediente(Ingrediente ingrediente) {
        ingrediente.setUsato(true);
        ingredientiUsati.add(ingrediente);
        System.out.println("🗑️ Ingrediente scartato: " + ingrediente.getTipo());
    }
    
    /**
     * Fine fase raccolta: riempie gli scaffali.
     */
    public void fineFaseRaccolta() {
        // Riempi scaffali
        riempiScaffali();
        
        // Se pool vuoto, ricicla ingredienti usati
        if (poolIngredienti.isEmpty() && !ingredientiUsati.isEmpty()) {
            System.out.println("♻️ Riciclo ingredienti usati...");
            for (Ingrediente ing : ingredientiUsati) {
                ing.setUsato(false);
            }
            poolIngredienti.addAll(ingredientiUsati);
            ingredientiUsati.clear();
            Collections.shuffle(poolIngredienti);
        }
    }
    
    /**
     * Quando una pozione è completata, la rimuove dalla board e ne rivela una nuova.
     */
    public void rimuoviPozioneCompletata(Pozione pozione) {
        if (pozioniDisponibili.remove(pozione)) {
            // Rivela nuova pozione
            if (!mazzoPozioni.isEmpty()) {
                pozioniDisponibili.add(mazzoPozioni.remove(0));
                System.out.println("🆕 Nuova pozione disponibile: " + 
                    pozioniDisponibili.get(pozioniDisponibili.size() - 1).getNome());
            }
        }
    }
    
    // ========== METODI DI VERIFICA REQUISITI ==========
    
    private boolean verificaAcquistoCartaValore(int valoreMinimo) {
        List<Carta> acquistate = stato.getCarteAcquistateQuestoTurno();
        if (acquistate != null) {
            for (Carta carta : acquistate) {
                if (carta.getCosto() >= valoreMinimo) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean verificaItemsGiocati(int numero) {
        List<Carta> giocate = stato.getCarteGiocateQuestoTurno();
        if (giocate != null) {
            int count = 0;
            for (Carta carta : giocate) {
                if (carta instanceof Oggetto) {
                    count++;
                }
            }
            return count >= numero;
        }
        return false;
    }
    
    private boolean verificaCuraAltroEroe() {
        // TODO: Implementare tracciamento cure nel StatoDiGioco
        return false;
    }
    
    private boolean verificaEroeStordito() {
        // TODO: Implementare tracciamento stordimenti nel StatoDiGioco
        return false;
    }
    
    private boolean verificaIncantesimiGiocati(int numero) {
        List<Carta> giocate = stato.getCarteGiocateQuestoTurno();
        if (giocate != null) {
            int count = 0;
            for (Carta carta : giocate) {
                if (carta instanceof Incantesimo) {
                    count++;
                }
            }
            return count >= numero;
        }
        return false;
    }
    
    // ========== GETTERS ==========
    
    public List<Pozione> getPozioniDisponibili() {
        return pozioniDisponibili;
    }
    
    public List<Pozione> getPozioniInProgress() {
        return pozioniInProgress;
    }
    
    public List<List<Ingrediente>> getScaffali() {
        return scaffali;
    }
    
    public List<PotionShelfRequirement> getRequisitiScaffaliCorrente() {
        return latoCorrente.equals("A") ? requisitiScaffaliLatoA : requisitiScaffaliLatoB;
    }
    
    public String getLatoCorrente() {
        return latoCorrente;
    }
    
    public void setLatoCorrente(String lato) {
        this.latoCorrente = lato;
    }
}
