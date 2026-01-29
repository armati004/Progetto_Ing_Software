package gioco;

import carte.ArteOscura;
import carte.Malvagio;
import gestoreEffetti.EsecutoreEffetti;
import gestoreEffetti.TipoEffetto;
import gestoreEffetti.TipoTrigger;
import carte.Horcrux;

import java.util.Collections;
import java.util.List;

/**
 * TurnManager - Gestisce il flusso delle fasi del turno
 * VERSIONE CORRETTA con applicazione effetti malvagi
 */
public class TurnManager {
    
    private StatoDiGioco stato;
    
    public TurnManager(StatoDiGioco stato) {
        this.stato = stato;
    }
    
    public void iniziaTurno() {
        Giocatore giocatore = stato.getGiocatori().get(stato.getGiocatoreCorrente());
        giocatore.registraTriggersInMano(stato);
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🎯 TURNO DI: " + giocatore.getEroe().getNome());
        System.out.println("=".repeat(60));
        
        stato.resetAttacchi();
        
        stato.setFaseCorrente(FaseTurno.ARTI_OSCURE);
        
        eseguiFaseAutomatica();
    }
    
    /**
     * Esegue la fase corrente automaticamente
     */
    public void eseguiFaseAutomatica() {
        FaseTurno fase = stato.getFaseCorrente();
        
        switch (fase) {
            case ARTI_OSCURE:
                eseguiFaseArtiOscure();
                break;
            case MALVAGI:
                eseguiFaseMalvagi();
                break;
            case HORCRUX:
                eseguiFaseHorcrux();
                break;
            case FINE_TURNO:
                eseguiFineTurno();
                break;
            default:
                System.out.println("⏸️ Fase " + fase + " - In attesa di input");
                break;
        }
        
        // ⭐ Aggiorna UI dopo ogni fase automatica
        aggiornaUI();
    }
    
    /**
     * Aggiorna l'interfaccia grafica
     */
    private void aggiornaUI() {
        try {
            if (grafica.GameController.getInstance() != null && 
                grafica.GameController.getInstance().getGameUI() != null) {
                
                javafx.application.Platform.runLater(() -> {
                    grafica.GameController.getInstance().getGameUI().aggiorna();
                });
            }
        } catch (Exception e) {
            System.err.println("⚠️ Errore aggiornamento UI: " + e.getMessage());
        }
    }
    
    /**
     * FASE 1: Rivela e applica carta Arti Oscure
     */
    private void eseguiFaseArtiOscure() {
        System.out.println("\n🌑 === FASE ARTI OSCURE ===");
        
        ArteOscura arteOscura = stato.pescaArteOscura();
        
        if (arteOscura != null) {
            System.out.println("📜 Rivelata: " + arteOscura.getNome());
            System.out.println("   " + arteOscura.getDescrizione());
            
            Giocatore giocatoreAttivo = stato.getGiocatori().get(stato.getGiocatoreCorrente());
            arteOscura.applicaEffetto(stato, giocatoreAttivo);
            
            if(arteOscura.getNome().contains("Morsmordre")) {
				stato.getGestoreTrigger().attivaTrigger(TipoTrigger.RIVELA_MORSMORDRE_O_MALVAGIO, stato, giocatoreAttivo);
			}
            
            // ⭐ IMPORTANTE: Aggiorna l'ultima carta giocata
           // stato.setUltimaArteOscuraGiocata(arteOscura);
            
            System.out.println("✓ Effetto Arti Oscure applicato");
        } else {
            System.out.println("⚠️ Nessuna carta Arti Oscure disponibile");
        }
        
        prossimaFase();
    }
    
    /**
     * FASE 2: Applica effetti dei malvagi attivi
     * ⭐ FIX: Ora applica VERAMENTE gli effetti
     */
    private void eseguiFaseMalvagi() {
        System.out.println("\n👹 === FASE MALVAGI ===");

        List<Malvagio> malvagi = stato.getMalvagiAttivi();

        if (malvagi.isEmpty()) {
            System.out.println("✓ Nessun malvagio attivo");
            if(!stato.getMazzoMalvagi().isEmpty()) {
            	stato.addMalvagioAttivo();
            	Giocatore giocatoreAttivo = stato.getGiocatori().get(stato.getGiocatoreCorrente());

                for (Malvagio malvagio : malvagi) {
                    System.out.println("  • " + malvagio.getNome() +
                                     " (" + malvagio.getDanno() + "⚔️/" + malvagio.getVita() + " ❤️)");

                    // ⭐ FIX: Applica l'effetto del malvagio
                    if(!malvagio.getBloccoAbilita()) {
                    	malvagio.applicaEffetto(stato, giocatoreAttivo);
                    }
                    else if(malvagio.getGiocatoreBloccante().equals(giocatoreAttivo)) {
                    	malvagio.setBloccoAbilita(false);
                    	malvagio.setGiocatoreBloccante(null);
                    	malvagio.applicaEffetto(stato, giocatoreAttivo);
                    }
                }
            }
        } else {
            System.out.println("Malvagi attivi: " + malvagi.size());

            Giocatore giocatoreAttivo = stato.getGiocatori().get(stato.getGiocatoreCorrente());

            for (Malvagio malvagio : malvagi) {
                System.out.println("  • " + malvagio.getNome() +
                                 " (" + malvagio.getDanno() + "⚔️/" + malvagio.getVita() + " ❤️)");

                // ⭐ FIX: Applica l'effetto del malvagio
                if(!malvagio.getBloccoAbilita()) {
                	malvagio.applicaEffetto(stato, giocatoreAttivo);
                }
                else if(malvagio.getGiocatoreBloccante().equals(giocatoreAttivo)) {
                	malvagio.setBloccoAbilita(false);
                	malvagio.setGiocatoreBloccante(null);
                	malvagio.applicaEffetto(stato, giocatoreAttivo);
                }
            }
        }

        prossimaFase();
    }
    
    /**
     * FASE 3: Applica effetti horcrux
     */
    private void eseguiFaseHorcrux() {
        System.out.println("\n💀 === FASE HORCRUX ===");
        
        if (!stato.isHasHorcruxes()) {
            System.out.println("⏭️ Anno senza Horcrux, fase saltata");
            prossimaFase();
            return;
        }
        
        List<Horcrux> horcruxAttivi = stato.getHorcruxAttivi();
        
        if (horcruxAttivi.isEmpty()) {
            System.out.println("✓ Nessun Horcrux attivo");
        } else {
            System.out.println("Horcrux attivi: " + horcruxAttivi.size());
            
            Giocatore giocatoreAttivo = stato.getGiocatori().get(stato.getGiocatoreCorrente());
            
            for (Horcrux horcrux : horcruxAttivi) {
                System.out.println("  • " + horcrux.getNome());
                horcrux.applicaEffetto(stato, giocatoreAttivo);
            }
        }
        
        prossimaFase();
    }
    
    /**
     * FASE 7: Fine turno
     */
    private void eseguiFineTurno() {
        System.out.println("\n🔄 === FINE TURNO ===");
        
        Giocatore giocatore = stato.getGiocatori().get(stato.getGiocatoreCorrente());
        
        stato.getGestoreEffetti().fineTurno();
        stato.getGestoreTrigger().rimuoviTriggerFineTurno();
        
        for(Malvagio m : stato.getMalvagiAttivi()) {
    		m.setAttaccoassegnato(false);
    	}
        
        if (stato.isVittoriaPendente()) {
            System.out.println("🎊 Turno concluso! Mostra schermata vittoria...");
            
            stato.setVictory(true);
            stato.setGameOver(true);
            
            javafx.application.Platform.runLater(() -> {
                if (grafica.GameController.getInstance() != null) {
                    grafica.GameController.getInstance().onVittoria();
                }
            });
        }else {
        	// 1. Scarta mano
            int carteScartatate = giocatore.getMano().size();
            while (!giocatore.getMano().isEmpty()) {
                giocatore.scartaCarta(giocatore.getMano().get(0));
            }
            System.out.println("  📤 Scartate " + carteScartatate + " carte");
            
            // 2. Ripristina segnalini
            giocatore.setAttacco(0);
            giocatore.setGettone(0);
            giocatore.setAlleatiGiocati(0);
            giocatore.setIncantesimiGiocati(0);
            giocatore.setOggettiGiocati(0);
            System.out.println("  🔄 Segnalini ripristinati");
            
            // 3. Pesca 5 carte
            if (giocatore.getMazzo().getCarte().isEmpty()) {
                giocatore.getMazzo().getCarte().addAll(giocatore.getScarti().getCarte());
                giocatore.getScarti().getCarte().clear();
                Collections.shuffle(giocatore.getMazzo().getCarte());
            }
            
            int carteDaPescare = Math.min(5, giocatore.getMazzo().getCarte().size());
            for (int i = 0; i < carteDaPescare; i++) {
                giocatore.pescaCarta();
            }
            
            System.out.println("  🃏 Pescate " + carteDaPescare + " carte");
            
            // 4. Prossimo giocatore
            int prossimoGiocatore = (stato.getGiocatoreCorrente() + 1) % stato.getGiocatori().size();
            stato.setGiocatoreCorrente(prossimoGiocatore);
            
            System.out.println("\n✓ Turno completato");
            
            iniziaTurno();
        }
    }
    
    /**
     * Avanza alla fase successiva
     */
    public void prossimaFase() {
        FaseTurno faseCorrente = stato.getFaseCorrente();
        FaseTurno prossimaFase = null;
        
        switch (faseCorrente) {
            case ARTI_OSCURE:
                prossimaFase = FaseTurno.MALVAGI;
                break;
            case MALVAGI:
                prossimaFase = stato.isHasHorcruxes() ? FaseTurno.HORCRUX : FaseTurno.GIOCA_CARTE;
                break;
            case HORCRUX:
                prossimaFase = FaseTurno.GIOCA_CARTE;
                break;
            case GIOCA_CARTE:
                prossimaFase = FaseTurno.ATTACCA;
                break;
            case ATTACCA:
                prossimaFase = FaseTurno.ACQUISTA_CARTE;
                break;
            case ACQUISTA_CARTE:
                prossimaFase = FaseTurno.FINE_TURNO;
                break;
            case FINE_TURNO:
                return;
        }
        
        if (prossimaFase != null) {
            stato.setFaseCorrente(prossimaFase);
            System.out.println("\n➡️ Fase successiva: " + getNomeFase(prossimaFase));
            
            if (isFaseAutomatica(prossimaFase)) {
                eseguiFaseAutomatica();
            }
        }
    }
    
    private boolean isFaseAutomatica(FaseTurno fase) {
        return fase == FaseTurno.ARTI_OSCURE || 
               fase == FaseTurno.MALVAGI || 
               fase == FaseTurno.HORCRUX ||
               fase == FaseTurno.FINE_TURNO;
    }
    
    private String getNomeFase(FaseTurno fase) {
        switch (fase) {
            case ARTI_OSCURE: return "Arti Oscure";
            case MALVAGI: return "Malvagi";
            case HORCRUX: return "Horcrux";
            case GIOCA_CARTE: return "Gioca Carte";
            case ATTACCA: return "Attacca";
            case ACQUISTA_CARTE: return "Acquista Carte";
            case FINE_TURNO: return "Fine Turno";
            default: return fase.toString();
        }
    }
    
    public StatoDiGioco getStato() {
        return stato;
    }
}