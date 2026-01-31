package gioco;

import java.util.List;

import carte.Carta;
import carte.Malvagio;

public class InputController {
	private StatoDiGioco stato;
	
	public InputController(StatoDiGioco stato) {
		this.stato = stato;
	}
	
	public String processaComando(String input) {
	    if (input == null || input.trim().isEmpty()) return "Comando vuoto.";

	    String[] parts = input.trim().split(" ");
	    String command = parts[0].toLowerCase();

	    // --- CORREZIONE 1: Controllo di Sicurezza sul Giocatore ---
	    // Verifichiamo che l'indice del giocatore corrente sia valido per la lista attuale
	    int indiceGiocatore = stato.getGiocatoreCorrente();
	    if (stato.getGiocatori() == null || stato.getGiocatori().isEmpty()) {
	        return "ERRORE GRAVE: Nessun giocatore presente nel GameState.";
	    }
	    if (indiceGiocatore < 0 || indiceGiocatore >= stato.getGiocatori().size()) {
	        return "ERRORE GRAVE: Indice giocatore corrente (" + indiceGiocatore + ") fuori dai limiti (Giocatori: " + stato.getGiocatori().size() + ")";
	    }

	    // Ora siamo sicuri che questa riga non lancerà eccezioni
	    Giocatore player = stato.getGiocatori().get(indiceGiocatore);

	    try {
	        switch (command) {
	            case "gioca":
	                // Passiamo 'parts' per controllare se c'è l'argomento
	                return handlePlayCard(parts, player);
	            case "attacca":
	                return handleAttack(parts, player); // Assicurati di avere controlli simili qui
	            case "compra":
	                return handleBuy(parts, player);    // E qui
	            case "next":
	                return handleNextPhase();
	            case "debug_add_res":
	                player.setAttacco(5);
	                player.setGettone(5);
	                return "Risorse aggiunte (Cheat).";
	            default:
	                return "Comando non riconosciuto: " + command;
	        }
	    } catch (NumberFormatException e) {
	        return "Errore: Devi inserire un numero valido dopo il comando.";
	    } catch (IndexOutOfBoundsException e) {
	        // Stampiamo lo stack trace per capire DOVE succede l'errore se capita ancora
	        e.printStackTrace(); 
	        return "Errore: Indice non valido (Vedi console per dettagli).";
	    } catch (Exception e) {
	        e.printStackTrace();
	        return "Errore generico: " + e.getMessage();
	    }
	}

	private String handlePlayCard(String[] parts, Giocatore player) {
	    if (stato.getFaseCorrente() != FaseTurno.GIOCA_CARTE) 
	        return "Non puoi giocare carte ora. Fase attuale: " + stato.getFaseCorrente();
	    
	    // --- CORREZIONE 2: Controllo Input Utente ---
	    if (parts.length < 2) {
	        return "Comando incompleto. Sintassi corretta: 'gioca <numero_carta>'";
	    }

	    try {
	        // Sottraiamo 1 per l'indice array (0-based)
	        int index = Integer.parseInt(parts[1]) - 1;
	        
	        // Controllo range dell'array
	        if (index < 0 || index >= player.getMano().size()) {
	            return "Indice carta non valido. Scegli un numero tra 1 e " + player.getMano().size();
	        }

	        Carta carta = player.getMano().get(index);
	        player.giocaCarta(stato, carta);
	        return "Hai giocato: " + carta.getNome();
	        
	    } catch (NumberFormatException e) {
	        return "Devi inserire un numero valido. Es: 'gioca 1'";
	    }
	}

    private String handleAttack(String[] parts, Giocatore player) {
        if (stato.getFaseCorrente() != FaseTurno.ATTACCA) 
            return "Devi essere nella fase di attacco.";

        int index = Integer.parseInt(parts[1]);
        List<Malvagio> villains = stato.getMalvagiAttivi();
        
        if (index < 0 || index >= villains.size()) return "Indice malvagio non valido.";
        
        Malvagio v = villains.get(index);
        
        if (player.getAttacco() < 1) return "Non hai abbastanza Attacco.";
        //if (!v.canTakeDamage(stato)) return v.getNome() + " è immune!";

        player.setAttacco(player.getAttacco() - 1);
        v.setDanno(v.getDanno() + 1);
        
        String result = "Colpito " + v.getNome() + ". Vita rimanente: " + v.getDanno();
        if (v.getDanno() >= v.getVita()) {
            stato.sconfiggiMalvagio(v);
            result += " (SCONFITTO!)";
        }
        return result;
    }

    private String handleBuy(String[] parts, Giocatore player) {
        if (stato.getFaseCorrente() != FaseTurno.ACQUISTA_CARTE) 
            return "Devi essere nella fase di acquisto.";

        int index = Integer.parseInt(parts[1]);
        List<Carta> market = stato.getMercato();
        
        if (index < 0 || index >= market.size()) return "Indice mercato non valido.";
        
        Carta c = market.get(index);
        
        if (player.getGettone() >= c.getCosto()) {
            player.acquistaCarta(stato.getMazzoNegozio(),c);
            stato.rimpiazzaCartaMercato(index);
            return "Hai comprato: " + c.getNome();
        } else {
            return "Influenza insufficiente. Costo: " + c.getCosto() + ", Hai: " + player.getGettone();
        }
    }

    private String handleNextPhase() {
        FaseTurno current = stato.getFaseCorrente();
        
        if (current == FaseTurno.GIOCA_CARTE) {
            stato.setFaseCorrente(FaseTurno.ATTACCA);
            return "Passato alla fase ATTACCO.";
        } else if (current == FaseTurno.ATTACCA) {
            stato.setFaseCorrente(FaseTurno.ACQUISTA_CARTE);
            return "Passato alla fase ACQUISTO.";
        } else if (current == FaseTurno.ACQUISTA_CARTE) {
            stato.fineTurno();
            stato.setFaseCorrente(FaseTurno.ARTI_OSCURE); // O gestione automatica successiva
            return "Turno finito. Tocca a: " + stato.getGiocatori().get(stato.getGiocatoreCorrente()).getEroe().getNome();
        }
        return "Impossibile cambiare fase manualmente ora.";
    }
}
