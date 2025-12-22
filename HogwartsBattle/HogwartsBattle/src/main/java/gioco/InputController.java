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
        Giocatore player = stato.getGiocatori().get(stato.getGiocatoreCorrente());

        try {
            switch (command) {
                case "gioca":
                    return handlePlayCard(parts, player);
                case "attacca":
                    return handleAttack(parts, player);
                case "compra":
                    return handleBuy(parts, player);
                case "next":
                    return handleNextPhase();
                case "debug_add_res": // Comando cheat per i test
                    player.setAttacco(5);
                    player.setGettone(5);
                    return "Risorse aggiunte (Cheat).";
                default:
                    return "Comando non riconosciuto: " + command;
            }
        } catch (NumberFormatException e) {
            return "Errore: Devi inserire un numero valido dopo il comando.";
        } catch (IndexOutOfBoundsException e) {
            return "Errore: Indice non valido.";
        } catch (Exception e) {
            return "Errore generico: " + e.getMessage();
        }
	}
	
	private String handlePlayCard(String[] parts, Giocatore player) {
        if (stato.getFaseCorrente() != FaseTurno.GIOCA_CARTE) 
            return "Non puoi giocare carte ora. Fase attuale: " + stato.getFaseCorrente();
        
        int index = Integer.parseInt(parts[1]);
        if (index < 0 || index >= player.getMano().size()) return "Indice carta non valido.";

        Carta Carta = player.getMano().get(index);
        player.giocaCarta(stato, Carta);
        return "Hai giocato: " + Carta.getNome();
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
            player.compraCarta(c);
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
