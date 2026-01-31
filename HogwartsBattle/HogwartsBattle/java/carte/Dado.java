package carte;

import java.util.List;
import java.util.Random;

import gioco.StatoDiGioco;
import gioco.Giocatore;
import gestoreEffetti.Trigger;
import gestoreEffetti.Effetto;
import gestoreEffetti.EsecutoreEffetti;

/**
 * Rappresenta un dado del gioco Hogwarts Battle:
 * - ha nome, id, immagine
 * - ha solo trigger (tipicamente DADO_TIRATO)
 * - quando viene tirato deve eseguire un effetto RANDOM con 6 opzioni.
 */
public class Dado {

    private String nome;
    private String id;
    private String pathImg;
    private List<Trigger> triggers;

    private final Random random = new Random();

    public Dado(String nome, String id, String pathImg, List<Trigger> triggers) {
        this.nome = nome;
        this.id = id;
        this.pathImg = pathImg;
        this.triggers = triggers;
    }

    public String getNome() {
        return nome;
    }

    public String getId() {
        return id;
    }

    public String getPathImg() {
        return pathImg;
    }

    public List<Trigger> getTriggers() {
        return triggers;
    }

    public void tiraDado(StatoDiGioco stato, Giocatore attivo, List<Effetto> opzioniEffetti) {
        if (opzioniEffetti == null || opzioniEffetti.size() < 6) {
            throw new IllegalArgumentException("Deve essere fornita una lista di almeno 6 effetti per il dado.");
        }

        int indiceEffetto = random.nextInt(6); // Genera un numero casuale tra 0 e 5
        Effetto effettoSelezionato = opzioniEffetti.get(indiceEffetto);

        EsecutoreEffetti.eseguiEffetto(effettoSelezionato, stato, attivo);
    }



}
