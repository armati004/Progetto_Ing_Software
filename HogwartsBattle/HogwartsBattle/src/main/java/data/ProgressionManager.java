package data;

import gioco.Giocatore;
import gioco.StatoDiGioco;
import carte.Carta;
import carte.Eroe;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
            }
            
            // ⭐ NUOVO: Ricarica carte acquisite
            if (playerData.getCarteNelMazzo() != null && !playerData.getCarteNelMazzo().isEmpty()) {
                // Rimuovi carte starter pack (già aggiunte da Giocatore costruttore)
                giocatore.getMazzo().getCarte().clear();
                giocatore.getScarti().getCarte().clear();
                
                // Aggiungi carte salvate
                for (String cartaID : playerData.getCarteNelMazzo()) {
                    try {
                        Carta carta = CardFactory.creaCarta(cartaID);
                        giocatore.getMazzo().aggiungiCarta(carta);
                    } catch (Exception e) {
                        System.err.println("  ⚠️ Errore caricamento carta: " + cartaID);
                    }
                }
                
                System.out.println("  ✓ " + playerData.getNomeEroe() + " - " + 
                                 playerData.getCarteNelMazzo().size() + " carte caricate");
            } else {
                System.out.println("  ✓ " + playerData.getNomeEroe() + " - carte starter pack");
            }
            
            giocatori.add(giocatore);
        }
        
        return giocatori;
    }

    // ============================================
    // MODIFICA 4: ProgressionManager.preparaGiocatoriProssimoAnno()
    // ============================================

    public static List<Giocatore> preparaGiocatoriProssimoAnno(
    	    List<Giocatore> giocatoriPrecedenti, 
    	    int prossimoAnno,
    	    StatoDiGioco stato) {  // ⭐ NUOVO parametro
    	    
    	    List<Giocatore> nuoviGiocatori = new ArrayList<>();
    	    
    	    // ⭐ NUOVO: Raccogli tutte le carte dai giocatori precedenti
    	    Set<String> carteGiocatori = new HashSet<>();
    	    for (Giocatore vecchioG : giocatoriPrecedenti) {
    	        // Carte nel mazzo
    	        for (Carta c : vecchioG.getMazzo().getCarte()) {
    	            carteGiocatori.add(c.getId());
    	        }
    	        // Carte negli scarti
    	        for (Carta c : vecchioG.getScarti().getCarte()) {
    	            carteGiocatori.add(c.getId());
    	        }
    	        // Carte in mano
    	        for (Carta c : vecchioG.getMano()) {
    	            carteGiocatori.add(c.getId());
    	        }
    	    }
    	    
    	    // ⭐ NUOVO: Aggiorna set carte acquisite nello stato
    	    stato.setCarteAcquisiteDaiGiocatori(carteGiocatori);
    	    
    	    System.out.println("📌 Carte nei mazzi giocatori: " + carteGiocatori.size());
    	    
    	 // Crea nuovi giocatori per il prossimo anno
    	    for (Giocatore vecchioG : giocatoriPrecedenti) {
    	        // Crea nuovo eroe per il prossimo anno
    	        String nomeEroe = vecchioG.getEroe().getNome();
    	        Eroe nuovoEroe = HeroFactory.creaEroe(nomeEroe, prossimoAnno);
    	        Giocatore nuovoGiocatore = new Giocatore(nuovoEroe);
    	        
    	        // Mantieni la competenza se presente
    	        if (vecchioG.getCompetenza() != null) {
    	            nuovoGiocatore.setCompetenza(vecchioG.getCompetenza());
    	        }
    	        
    	        // Trasferisci le carte dal vecchio mazzo
    	        nuovoGiocatore.getMazzo().getCarte().clear();
    	        nuovoGiocatore.getScarti().getCarte().clear();
    	        
    	        // Aggiungi tutte le carte del vecchio giocatore
    	        for (Carta c : vecchioG.getMazzo().getCarte()) {
    	            nuovoGiocatore.getMazzo().aggiungiCarta(c);
    	        }
    	        for (Carta c : vecchioG.getScarti().getCarte()) {
    	            nuovoGiocatore.getMazzo().aggiungiCarta(c);
    	        }
    	        for (Carta c : vecchioG.getMano()) {
    	            nuovoGiocatore.getMazzo().aggiungiCarta(c);
    	        }
    	        
    	        nuoviGiocatori.add(nuovoGiocatore);
    	        
    	        System.out.println("✅ " + nomeEroe + " pronto per anno " + prossimoAnno + 
    	                         " (carte: " + nuovoGiocatore.getMazzo().getCarte().size() + ")");
    	    }

    	    return nuoviGiocatori;
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