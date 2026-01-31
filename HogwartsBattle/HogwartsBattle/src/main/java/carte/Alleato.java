package carte;

import java.util.List;

import gioco.Giocatore;
import gioco.StatoDiGioco;
import gestoreEffetti.Effetto;
import gestoreEffetti.TipoEffetto;
import gestoreEffetti.Trigger;
import gestoreEffetti.EsecutoreEffetti;

public class Alleato extends Carta {

    public Alleato(String nome, String id, String classe, String descrizione, int costo,
                   String pathImmagine, List<Effetto> effetti, List<Trigger> triggers) {
        super(nome, id, classe, descrizione, costo, pathImmagine, effetti, triggers);
    }

    /*@Override
    public void applicaEffetto(StatoDiGioco stato, Giocatore attivo) {

        for (Effetto e : getEffetti()) {

            if (e.getType() == TipoEffetto.SCELTA) {
                gestisciScelta(e, stato, attivo);
            } else {
                EsecutoreEffetti.eseguiEffetto(e, stato, attivo);
            }
        }
    }

    private void gestisciScelta(Effetto effetto, StatoDiGioco stato, Giocatore attivo) {
        // Logica per gestire l'effetto di scelta
        // Ad esempio, presentare le opzioni al giocatore e applicare l'effetto scelto
        List<Effetto> opzioni = effetto.getOpzioni();
        if (opzioni != null && !opzioni.isEmpty()) {
            // Per semplicità, scegliamo sempre la prima opzione in questo esempio
            Effetto scelta = opzioni.get(0);
            EsecutoreEffetti.eseguiEffetto(scelta, stato, attivo);
        }
    }*/

    
}
