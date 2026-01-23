package grafica.managers;

import carte.Competenza;
import carte.Eroe;
import data.HeroFactory;
import data.ProficiencyFactory;
import gioco.Giocatore;

import java.util.ArrayList;
import java.util.List;

/**
 * GameFlowManager - Gestisce il flusso tra i giochi e l'evoluzione degli eroi.
 * 
 * Responsabilità:
 * - Memorizza gli eroi scelti al gioco 1
 * - Gestisce l'evoluzione degli eroi ai giochi 3 e 7
 * - Gestisce la selezione competenze al gioco 6
 * - Mantiene le competenze selezionate per i giochi successivi
 */
public class GameFlowManager {
    
    private static GameFlowManager instance;
    
    // Stato persistente tra i giochi
    private List<String> eroiScelti;           // Nomi degli eroi scelti al gioco 1
    private List<String> competenzeScelte;      // ID competenze scelte al gioco 6
    private int annoCorrente;
    
    /**
     * Singleton
     */
    public static GameFlowManager getInstance() {
        if (instance == null) {
            instance = new GameFlowManager();
        }
        return instance;
    }
    
    /**
     * Costruttore privato
     */
    private GameFlowManager() {
        this.eroiScelti = new ArrayList<>();
        this.competenzeScelte = new ArrayList<>();
        this.annoCorrente = 1;
    }
    
    /**
     * Resetta tutto (per nuova campagna)
     */
    public void reset() {
        eroiScelti.clear();
        competenzeScelte.clear();
        annoCorrente = 1;
        System.out.println("🔄 GameFlowManager resettato");
    }
    
    /**
     * Verifica se gli eroi sono già stati scelti
     */
    public boolean sonoStatiSceltiGliEroi() {
        return !eroiScelti.isEmpty();
    }
    
    /**
     * Verifica se le competenze sono già state scelte
     */
    public boolean sonoStateScelteleCompetenze() {
        return !competenzeScelte.isEmpty();
    }
    
    /**
     * Imposta gli eroi scelti (chiamato dalla schermata selezione al gioco 1)
     */
    public void setEroiScelti(List<String> eroi) {
        this.eroiScelti = new ArrayList<>(eroi);
        System.out.println("✓ Eroi memorizzati: " + eroiScelti);
    }
    
    /**
     * Imposta le competenze scelte (chiamato dalla schermata selezione al gioco 6)
     */
    public void setCompetenzeScelte(List<String> competenze) {
        this.competenzeScelte = new ArrayList<>(competenze);
        System.out.println("✓ Competenze memorizzate: " + competenzeScelte);
    }
    
    /**
     * Ottiene gli eroi scelti
     */
    public List<String> getEroiScelti() {
        return new ArrayList<>(eroiScelti);
    }
    
    /**
     * Ottiene le competenze scelte
     */
    public List<String> getCompetenzeScelte() {
        return new ArrayList<>(competenzeScelte);
    }
    
    /**
     * Imposta l'anno corrente
     */
    public void setAnnoCorrente(int anno) {
        this.annoCorrente = anno;
    }
    
    /**
     * Ottiene l'anno corrente
     */
    public int getAnnoCorrente() {
        return annoCorrente;
    }
    
    /**
     * Crea i giocatori per l'anno corrente.
     * Gestisce automaticamente l'evoluzione degli eroi.
     * 
     * @param anno Anno del gioco
     * @return Lista di giocatori con eroi e competenze appropriate
     */
    public List<Giocatore> creaGiocatoriPerAnno(int anno) {
        if (eroiScelti.isEmpty()) {
            throw new IllegalStateException("Eroi non ancora selezionati!");
        }
        
        List<Giocatore> giocatori = new ArrayList<>();
        
        System.out.println("\n👥 Creazione giocatori per Anno " + anno + "...");
        
        // Determina se c'è evoluzione
        boolean evoluzione = (anno == 3 || anno == 7);
        if (evoluzione) {
            System.out.println("⚡ EVOLUZIONE EROI!");
        }
        
        for (int i = 0; i < eroiScelti.size(); i++) {
            String nomeEroe = eroiScelti.get(i);
            
            // Crea l'eroe con l'anno corrente (HeroFactory gestisce l'evoluzione)
            Eroe eroe = HeroFactory.creaEroe(nomeEroe, anno);
            Giocatore giocatore = new Giocatore(eroe);
            
            // Aggiungi competenza se anno 6+ e sono state scelte
            if (anno >= 6 && i < competenzeScelte.size()) {
                try {
                    String idCompetenza = competenzeScelte.get(i);
                    Competenza competenza = ProficiencyFactory.creaCompetenza(idCompetenza);
                    giocatore.setCompetenza(competenza);
                    
                    if (evoluzione) {
                        System.out.println("  ⚡ " + nomeEroe + " (EVOLUTO) con " + competenza.getNome());
                    } else {
                        System.out.println("  ✓ " + nomeEroe + " con " + competenza.getNome());
                    }
                } catch (Exception e) {
                    System.err.println("  ⚠ Errore caricamento competenza per " + nomeEroe);
                }
            } else {
                if (evoluzione) {
                    System.out.println("  ⚡ " + nomeEroe + " (EVOLUTO)");
                } else {
                    System.out.println("  ✓ " + nomeEroe);
                }
            }
            
            giocatori.add(giocatore);
        }
        
        return giocatori;
    }
    
    /**
     * Determina se serve mostrare la selezione eroi
     * (solo al gioco 1 se non già selezionati)
     */
    public boolean serveSelezioneEroi(int anno) {
        return anno == 1 && !sonoStatiSceltiGliEroi();
    }
    
    /**
     * Determina se serve mostrare la selezione competenze
     * (solo al gioco 6 se non già selezionate)
     */
    public boolean serveSelezioneCompetenze(int anno) {
        return anno == 6 && !sonoStateScelteleCompetenze();
    }
    
    /**
     * Mostra un messaggio di evoluzione se applicabile
     */
    public String getMessaggioEvoluzione(int anno) {
        switch (anno) {
            case 3:
                return "🌟 ANNO 3: Gli eroi si sono evoluti e hanno ottenuto nuove abilità!";
            case 7:
                return "⚡ ANNO 7: Gli eroi hanno raggiunto il loro massimo potenziale!";
            default:
                return null;
        }
    }
    
    /**
     * Ottiene tutte le competenze disponibili per il gioco 6
     */
    public static List<String> getCompetenzeDisponibili() {
        List<String> competenze = new ArrayList<>();
        
        // Tutte le competenze sono disponibili al gioco 6
        competenze.add("difesaControLeArtiOscure6");
        competenze.add("trasformazione6");
        competenze.add("incantesimi6");
        competenze.add("pozioni6");
        competenze.add("erbologia6");
        competenze.add("divinazione6");
        competenze.add("artimanzia6");
        competenze.add("curaDellCreatureMagiche6");
        competenze.add("storiaDellaMagia6");
        competenze.add("lezioniDiVolo6");
        
        return competenze;
    }
    
    /**
     * Stampa lo stato corrente per debug
     */
    public void stampaStato() {
        System.out.println("\n📊 STATO GAME FLOW MANAGER:");
        System.out.println("  Anno: " + annoCorrente);
        System.out.println("  Eroi scelti: " + (sonoStatiSceltiGliEroi() ? eroiScelti : "Nessuno"));
        System.out.println("  Competenze scelte: " + (sonoStateScelteleCompetenze() ? competenzeScelte : "Nessuna"));
        System.out.println("  Serve selezione eroi: " + serveSelezioneEroi(annoCorrente));
        System.out.println("  Serve selezione competenze: " + serveSelezioneCompetenze(annoCorrente));
    }
}