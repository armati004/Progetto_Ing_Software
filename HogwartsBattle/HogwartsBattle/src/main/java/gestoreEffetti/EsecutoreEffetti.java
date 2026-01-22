package gestoreEffetti;

import java.util.List;

import carte.Carta;
import carte.Luogo;
import carte.Malvagio;
import gioco.Giocatore;
import gioco.StatoDiGioco;

public class EsecutoreEffetti {

    public static void eseguiEffetto(Effetto effetto, StatoDiGioco stato, Giocatore bersaglio) {
        if (effetto == null) return;
        
        // Se il target è "TUTTI_GLI_EROI", chiamiamo ricorsivamente l'esecutore per ogni giocatore
        if (effetto.getTarget() == BersaglioEffetto.TUTTI_GLI_EROI) {
            for (Giocatore g : stato.getGiocatori()) {
                eseguiEffettoSingolo(effetto, stato, g);
            }
        } else {
            // Esecuzione standard sul bersaglio specifico
            eseguiEffettoSingolo(effetto, stato, bersaglio);
        }
    }

    private static void eseguiEffettoSingolo(Effetto effetto, StatoDiGioco stato, Giocatore bersaglio) {
        int qta = (effetto.getQta() != null) ? effetto.getQta() : 0;

        switch(effetto.getType()) {
            // --- GESTIONE VITA ---
            case GUADAGNARE_VITA:
                bersaglio.aggiungiSalute(qta);
                System.out.println(bersaglio.getEroe().getNome() + " guadagna " + qta + " vita.");
                break;
                
            case PERDERE_VITA:
                bersaglio.rimuoviSalute(qta);
                System.out.println(bersaglio.getEroe().getNome() + " perde " + qta + " vita.");
                break;

            // --- GESTIONE RISORSE ---
            case GUADAGNARE_INFLUENZA:
                bersaglio.aggiungiGettone(qta);
                System.out.println("Guadagnati " + qta + " gettoni influenza.");
                break;
                
            case GUADAGNARE_ATTACCO:
                bersaglio.aggiungiAttacco(qta);
                System.out.println("Guadagnati " + qta + " segnalini attacco.");
                break;
                
            case RIMUOVERE_ATTACCO:
                bersaglio.rimuoviAttacco(qta);
                break;


            case PESCARE_CARTA:
                for (int i = 0; i < qta; i++) {
                    bersaglio.getMazzo().pesca(bersaglio.getMano(), bersaglio.getScarti());
                }
                System.out.println("Pescate " + qta + " carte.");
                break;
                
            case SCARTARE_CARTA:
                for(int i=0; i<qta && !bersaglio.getMano().isEmpty(); i++) {

                    Carta daScartare = bersaglio.getMano().get(0); 
                    
                    bersaglio.getMano().remove(0);
                    bersaglio.getScarti().aggiungiCarta(daScartare);
                    System.out.println("Scartata: " + daScartare.getNome());
                    

                    stato.getGestoreTrigger().attivaTrigger(
                        TipoTrigger.AUTO_SCARTO, 
                        stato, 
                        bersaglio,  
                        daScartare    
                    );
                }
                break;


            case AGGIUNGERE_MARCHIO_NERO:
                Luogo luogoCorrente = stato.getCurrentLocation();
                if (luogoCorrente != null) {
                    //Aggiorna la logica del Luogo
                    int attuali = luogoCorrente.getNumeroMarchiNeri();
                    luogoCorrente.setNumeroMarchiNeri(attuali + qta);
                    System.out.println("Aggiunto Marchio Nero! Totale: " + (attuali + qta));
                    
                    //Attiva i Trigger (es. Draco Malfoy)
                    stato.getGestoreTrigger().attivaTrigger(TipoTrigger.AGGIUNTA_MARCHIO_NERO, stato, bersaglio);

                    //Controllo Conquista Luogo
                    if (luogoCorrente.getNumeroMarchiNeri() >= luogoCorrente.getMarchiNeriMax()) {
                        System.out.println("ATTENZIONE: Il luogo è stato conquistato!");
                    }
                }
                break;
                
            case RIMUOVERE_MARCHIO_NERO:
                Luogo luogo = stato.getCurrentLocation();
                if (luogo != null) {
                    int attuali = luogo.getNumeroMarchiNeri();
                    int nuovi = Math.max(0, attuali - qta); // Non scendere sotto zero
                    luogo.setNumeroMarchiNeri(nuovi);
                    System.out.println("Rimosso Marchio Nero. Totale: " + nuovi);
                }
                break;
                
            case CURARE_MALVAGI:
                for(Malvagio m : stato.getMalvagiAttivi()) {
                    // m.guarisci(qta); // Implementare in Malvagio se necessario
                }
                break;

            case ACQUISTO_CARTA:
                break;
                
            default:
                break;
        }
    }
    
    // Metodi stub per logiche future
    public static void scartaAlleato(Effetto effetto, StatoDiGioco stato, Giocatore attivo) {}
    public static void bloccaAbilitaMalvagio(Effetto effetto, StatoDiGioco stato, Giocatore giocatore) {}
    public static void cercaIncantesimo(Effetto effetto, StatoDiGioco stato, Giocatore bersaglio) {}
}