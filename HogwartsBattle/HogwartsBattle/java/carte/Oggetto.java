package carte;

import java.util.List;

import gioco.Giocatore;
import gioco.StatoDiGioco;
import gestoreEffetti.Effetto;
import gestoreEffetti.Trigger;
import gestoreEffetti.EsecutoreEffetti;
import gestoreEffetti.TipoEffetto; 
import gestoreEffetti.BersaglioEffetto;
import gestoreEffetti.DurataEffetto;

/**
 * Oggetto = tipo di carta che estende Carta e delega gli effetti all'EsecutoreEffetti.
 * Gestisce localmente le scelte (SCELTA e SCELTA_MULTIPLA) che richiedono input utente.
 */
public class Oggetto extends Carta {

    public Oggetto(String nome, String id, String classe, String descrizione, int costo,
                  String pathImmagine, List<Effetto> effetti, List<Trigger> triggers) {
        super(nome, id, classe, descrizione, costo, pathImmagine, effetti, triggers);
    }


}

