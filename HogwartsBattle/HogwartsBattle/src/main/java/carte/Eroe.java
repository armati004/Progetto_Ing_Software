package carte;

import java.util.List;
import gestoreEffetti.Effetto;
import gestoreEffetti.Trigger;
import gioco.Giocatore;
import gioco.StatoDiGioco;

public class Eroe extends Carta {

    public Eroe(String nome, String id, String classe, String descrizione, int costo,
                String pathImmagine, List<Effetto> effetti, List<Trigger> triggers) {

        super(nome, id, classe, descrizione, costo, pathImmagine, effetti, triggers);
    }

    @Override
    public void applicaEffetto(StatoDiGioco stato, Giocatore attivo) {
        // Eventuali regole speciali degli eroi
        super.applicaEffetto(stato, attivo);
    }

	public int getVita() {
		return 0;
	}
}
