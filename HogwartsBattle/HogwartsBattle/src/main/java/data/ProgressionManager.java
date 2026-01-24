package data;

import gioco.Giocatore;
import gioco.StatoDiGioco;
import carte.Eroe;

import java.util.ArrayList;
import java.util.List;

/**
 * Manager per la progressione attraverso gli anni/livelli
 */
public class ProgressionManager {
    
    private static final int MAX_ANNO = 7;
    private static final int MIN_ANNO = 1;
    
    /**
     * Verifica se è possibile avanzare all'anno successivo
     * 
     * @param annoCorrente Anno corrente
     * @return true se può avanzare
     */
    public static boolean puoAvanzareAnno(int annoCorrente) {
        return annoCorrente < MAX_ANNO;
    }
    
    /**
     * Calcola il prossimo anno
     * 
     * @param annoCorrente Anno corrente
     * @return Prossimo anno, o stesso anno se già al massimo
     */
    public static int calcolaProssimoAnno(int annoCorrente) {
        if (puoAvanzareAnno(annoCorrente)) {
            return annoCorrente + 1;
        }
        return annoCorrente;
    }
    
    /**
     * Prepara i giocatori per il prossimo anno
     * Mantiene gli stessi eroi e competenze (se presenti)
     * 
     * @param giocatoriPrecedenti Lista giocatori dell'anno precedente
     * @param prossimoAnno Prossimo anno
     * @return Nuova lista giocatori per il prossimo anno
     */
    public static List<Giocatore> preparaGiocatoriProssimoAnno(List<Giocatore> giocatoriPrecedenti, int prossimoAnno) {
        List<Giocatore> nuoviGiocatori = new ArrayList<>();
        
        System.out.println("\n🎓 ========================================");
        System.out.println("🎓 AVANZAMENTO ANNO " + prossimoAnno);
        System.out.println("🎓 ========================================");
        
        for (Giocatore vecchioG : giocatoriPrecedenti) {
            // Ricrea l'eroe per il nuovo anno
            String nomeEroe = vecchioG.getEroe().getNome();
            Eroe nuovoEroe = HeroFactory.creaEroe(nomeEroe, prossimoAnno);
            
            // Crea nuovo giocatore
            Giocatore nuovoG = new Giocatore(nuovoEroe);
            
            // Se aveva una competenza, mantienila
            if (vecchioG.getCompetenza() != null && prossimoAnno >= 6) {
                nuovoG.setCompetenza(vecchioG.getCompetenza());
                System.out.println("  📚 " + nomeEroe + " mantiene: " + 
                                 vecchioG.getCompetenza().getNome());
            } else {
                System.out.println("  🎓 " + nomeEroe + " avanza all'anno " + prossimoAnno);
            }
            
            nuoviGiocatori.add(nuovoG);
        }
        
        System.out.println("🎓 ========================================\n");
        
        return nuoviGiocatori;
    }
    
    /**
     * Ricrea i giocatori da un salvataggio
     * 
     * @param saveData Dati del salvataggio
     * @param anno Anno per cui creare gli eroi
     * @return Lista giocatori ricreati
     */
    public static List<Giocatore> ricreaGiocatoriDaSalvataggio(GameSaveData saveData, int anno) {
        List<Giocatore> giocatori = new ArrayList<>();
        
        System.out.println("\n📂 Ricostruzione giocatori dall'anno " + anno);
        
        for (PlayerSaveData playerData : saveData.getGiocatori()) {
            // Crea eroe
            Eroe eroe = HeroFactory.creaEroe(playerData.getNomeEroe(), anno);
            Giocatore giocatore = new Giocatore(eroe);
            
            // Aggiungi competenza se presente
            if (playerData.getIdCompetenza() != null) {
                try {
                    carte.Competenza comp = ProficiencyFactory.creaCompetenza(playerData.getIdCompetenza());
                    giocatore.setCompetenza(comp);
                    System.out.println("  ✓ " + playerData.getNomeEroe() + " + " + comp.getNome());
                } catch (Exception e) {
                    System.err.println("  ⚠️ Errore caricamento competenza: " + playerData.getIdCompetenza());
                }
            } else {
                System.out.println("  ✓ " + playerData.getNomeEroe());
            }
            
            giocatori.add(giocatore);
        }
        
        return giocatori;
    }
    
    /**
     * Gestisce la vittoria e l'avanzamento automatico
     * 
     * @param stato Stato di gioco corrente
     * @return Prossimo anno se vinto, -1 se non vinto
     */
    public static int gestisciVittoria(StatoDiGioco stato) {
        if (!stato.isVictory()) {
            return -1;
        }
        
        int annoCorrente = stato.getAnnoCorrente();
        
        System.out.println("\n🎉 ========================================");
        System.out.println("🎉 ANNO " + annoCorrente + " COMPLETATO!");
        System.out.println("🎉 ========================================");
        
        if (annoCorrente >= MAX_ANNO) {
            System.out.println("🏆 ========================================");
            System.out.println("🏆 GIOCO COMPLETATO!");
            System.out.println("🏆 Avete completato tutti e 7 gli anni!");
            System.out.println("🏆 ========================================");
            return MAX_ANNO; // Gioco finito
        } else {
            int prossimoAnno = annoCorrente + 1;
            System.out.println("📖 Preparatevi per l'Anno " + prossimoAnno + "...");
            System.out.println("🎉 ========================================\n");
            return prossimoAnno;
        }
    }
    
    /**
     * Verifica se il gioco è completamente finito (anno 7 vinto)
     */
    public static boolean giocoCompletato(StatoDiGioco stato) {
        return stato.isVictory() && stato.getAnnoCorrente() >= MAX_ANNO;
    }
    
    /**
     * Ottiene messaggio di progresso
     */
    public static String getMessaggioProgresso(int annoCorrente) {
        double progresso = (annoCorrente / (double) MAX_ANNO) * 100;
        return String.format("Anno %d/%d (%.0f%% completato)", annoCorrente, MAX_ANNO, progresso);
    }
    
    /**
     * Salva progresso dopo una vittoria
     */
    public static boolean salvaProgressoVittoria(StatoDiGioco stato) {
        if (!stato.isVictory()) {
            System.out.println("⚠️ Non puoi salvare il progresso: partita non vinta");
            return false;
        }
        
        // Salva con nome specifico per il progresso
        String nomeSalvataggio = "progresso_anno_" + stato.getAnnoCorrente();
        return SaveManager.salvaPartita(stato, nomeSalvataggio);
    }
    
    /**
     * Carica l'ultimo progresso salvato
     */
    public static GameSaveData caricaUltimoProgresso() {
        List<String> salvataggi = SaveManager.listaSalvataggi();
        
        // Cerca il salvataggio con anno più alto
        GameSaveData ultimoProgresso = null;
        int annoMassimo = 0;
        
        for (String nome : salvataggi) {
            if (nome.startsWith("progresso_anno_")) {
                GameSaveData save = SaveManager.caricaPartita(nome);
                if (save != null && save.getAnnoCorrente() > annoMassimo) {
                    annoMassimo = save.getAnnoCorrente();
                    ultimoProgresso = save;
                }
            }
        }
        
        return ultimoProgresso;
    }
}