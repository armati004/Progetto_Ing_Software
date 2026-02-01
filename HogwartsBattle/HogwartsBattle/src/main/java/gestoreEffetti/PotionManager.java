package gestoreEffetti;

import carte.*;
import gioco.Giocatore;
import gioco.StatoDiGioco;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Gestisce il sistema completo delle pozioni:
 * - Potion Board con 2 scaffali di pozioni disponibili
 * - Gestione ingredienti
 * - Brewing (completamento pozioni)
 * - Requisiti per accedere agli scaffali
 * 
 * Sistema Pozioni - Pack 2+
 */
public class PotionManager {
    private StatoDiGioco stato;
    
    // Potion Board - Mazzo e pozioni visibili
    private List<Pozione> mazzoPozioni;
    private List<Pozione> scaffaleA; // Primo scaffale di pozioni
    private List<Pozione> scaffaleB; // Secondo scaffale di pozioni
    
    // Requisiti scaffali
    private String latoCorrente; // "A" o "B"
    
    /**
     * Costruttore del manager delle pozioni.
     * 
     * @param stato Stato di gioco corrente
     */
    public PotionManager(StatoDiGioco stato) {
        this.stato = stato;
        this.mazzoPozioni = new ArrayList<>();
        this.scaffaleA = new ArrayList<>();
        this.scaffaleB = new ArrayList<>();
        this.latoCorrente = "A"; // Default
    }
    
    /**
     * Inizializza la Potion Board distribuendo le pozioni tra gli scaffali.
     */
    public void inizializza() {
        // Mescola il mazzo pozioni
        Collections.shuffle(mazzoPozioni);
        
        // Metti fino a 3 pozioni nello scaffale A
        while (scaffaleA.size() < 3 && !mazzoPozioni.isEmpty()) {
            scaffaleA.add(mazzoPozioni.remove(0));
        }
        
        // Metti fino a 3 pozioni nello scaffale B
        while (scaffaleB.size() < 3 && !mazzoPozioni.isEmpty()) {
            scaffaleB.add(mazzoPozioni.remove(0));
        }
        
        System.out.println("🧪 Potion Board inizializzata!");
        System.out.println("   Lato requisiti: " + latoCorrente);
        System.out.println("   Scaffale A: " + scaffaleA.size() + " pozioni");
        System.out.println("   Scaffale B: " + scaffaleB.size() + " pozioni");
    }
    
    /**
     * Aggiunge un ingrediente al pool del giocatore specificato.
     * 
     * @param giocatore Giocatore che riceve l'ingrediente
     * @param tipoIngrediente Tipo di ingrediente da aggiungere
     */
    public void aggiungiIngrediente(Giocatore giocatore, String tipoIngrediente) {
        if (giocatore == null || tipoIngrediente == null) {
            System.out.println("⚠️ Giocatore o tipo ingrediente null");
            return;
        }
        
        // Aggiungi l'ingrediente al giocatore
        giocatore.aggiungiIngrediente(tipoIngrediente, 1);
        System.out.println("✓ Ingrediente " + tipoIngrediente + " aggiunto a " + giocatore.getNome());
    }
    
    /**
     * Brew (completa e gioca) una pozione.
     * Il giocatore deve avere tutti gli ingredienti necessari.
     * 
     * @param giocatore Giocatore che vuole brewre la pozione
     * @param pozione Pozione da completare
     * @param usaBanish true se si vuole usare l'effetto banish
     * @return true se il brewing è avvenuto con successo
     */
    public boolean brewPozione(Giocatore giocatore, Pozione pozione, boolean usaBanish) {
        if (giocatore == null || pozione == null) {
            System.out.println("⚠️ Giocatore o pozione null");
            return false;
        }
        
        // Verifica se il giocatore ha gli ingredienti necessari
        List<String> ingredientiRichiesti = new ArrayList<>();
        for (TipoIngrediente tipo : pozione.getIngredientiRichiesti()) {
            ingredientiRichiesti.add(tipo.name());
        }
        
        // Conta ingredienti necessari
        java.util.Map<String, Integer> ingredientiNecessari = new java.util.HashMap<>();
        for (String ing : ingredientiRichiesti) {
            ingredientiNecessari.put(ing, ingredientiNecessari.getOrDefault(ing, 0) + 1);
        }
        
        // Verifica disponibilità
        for (java.util.Map.Entry<String, Integer> entry : ingredientiNecessari.entrySet()) {
            int disponibili = giocatore.getIngredienti().getOrDefault(entry.getKey(), 0);
            if (disponibili < entry.getValue()) {
                System.out.println("⚠️ Ingredienti insufficienti per " + pozione.getNome());
                return false;
            }
        }
        
        // Consuma gli ingredienti
        for (java.util.Map.Entry<String, Integer> entry : ingredientiNecessari.entrySet()) {
            giocatore.rimuoviIngrediente(entry.getKey(), entry.getValue());
        }
        
        System.out.println("🧪 " + giocatore.getNome() + " sta preparando: " + pozione.getNome());
        
        if (usaBanish) {
            System.out.println("   Usando effetto BANISH");
            // Con banish, applica effetto ma NON aggiunge al mazzo
            List<Effetto> effetti = pozione.getEffettoBanish();
            if (effetti != null && stato != null) {
                for (Effetto effetto : effetti) {
                    EsecutoreEffetti.eseguiEffetto(effetto, stato, giocatore);
                }
            }
        } else {
            System.out.println("   Usando effetto NORMALE");
            // Applica effetto normale e aggiungi al mazzo
            List<Effetto> effetti = pozione.getEffettoNormale();
            if (effetti != null && stato != null) {
                for (Effetto effetto : effetti) {
                    EsecutoreEffetti.eseguiEffetto(effetto, stato, giocatore);
                }
            }
            // Aggiungi pozione al mazzo del giocatore
            giocatore.getMazzo().aggiungiCarta(pozione);
        }
        
        // Rimuovi pozione dallo scaffale
        scaffaleA.remove(pozione);
        scaffaleB.remove(pozione);
        
        // Incrementa contatore
        giocatore.incrementaPozioniBrewate();
        
        // Riempi scaffali se necessario
        riempiScaffali();
        
        return true;
    }
    
    /**
     * Riempie gli scaffali con pozioni dal mazzo.
     */
    private void riempiScaffali() {
        // Riempi scaffale A
        while (scaffaleA.size() < 3 && !mazzoPozioni.isEmpty()) {
            scaffaleA.add(mazzoPozioni.remove(0));
        }
        
        // Riempi scaffale B
        while (scaffaleB.size() < 3 && !mazzoPozioni.isEmpty()) {
            scaffaleB.add(mazzoPozioni.remove(0));
        }
    }
    
    /**
     * Aggiunge una pozione al mazzo.
     * 
     * @param pozione Pozione da aggiungere
     */
    public void aggiungiPozioneAlMazzo(Pozione pozione) {
        if (pozione != null) {
            mazzoPozioni.add(pozione);
            System.out.println("✓ Pozione aggiunta al mazzo: " + pozione.getNome());
        }
    }
    
    /**
     * Ruota lo scaffale (cambia tra lato A e lato B).
     * Questo cambia i requisiti per accedere agli scaffali.
     */
    public void ruotaScaffale() {
        latoCorrente = latoCorrente.equals("A") ? "B" : "A";
        System.out.println("🔄 Scaffale ruotato al lato: " + latoCorrente);
    }
    
    /**
     * Ottiene lo scaffale corrente in base al lato.
     * 
     * @return Lista delle pozioni nello scaffale corrente
     */
    public List<Pozione> getScaffaleCorrente() {
        return latoCorrente.equals("A") ? scaffaleA : scaffaleB;
    }
    
    // =============== GETTERS E SETTERS ===============
    
    /**
     * Ottiene lo scaffale A.
     * 
     * @return Lista delle pozioni nello scaffale A
     */
    public List<Pozione> getScaffaleA() {
        return scaffaleA;
    }
    
    /**
     * Ottiene lo scaffale B.
     * 
     * @return Lista delle pozioni nello scaffale B
     */
    public List<Pozione> getScaffaleB() {
        return scaffaleB;
    }
    
    public String getLatoCorrente() {
        return latoCorrente;
    }
    
    public void setLatoCorrente(String lato) {
        if (lato.equals("A") || lato.equals("B")) {
            this.latoCorrente = lato;
        }
    }
    
    public List<Pozione> getMazzoPozioni() {
        return mazzoPozioni;
    }
}
