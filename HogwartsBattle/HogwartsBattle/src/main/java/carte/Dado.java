package carte;

import java.util.List;
import java.util.Random;

import com.google.gson.annotations.SerializedName;

import gioco.StatoDiGioco;
import gioco.Giocatore;
import gestoreEffetti.Effetto;
import gestoreEffetti.EsecutoreEffetti;

/**
 * Rappresenta un dado del gioco Hogwarts Battle.
 * Il dado ha 6 facce, ognuna con un effetto diverso.
 * Quando viene tirato, sceglie casualmente una delle 6 facce.
 */
public class Dado {

    private String nome;
    private String id;
    
    @SerializedName(value = "pathImg", alternate = {"path-img"})
    private String pathImg;
    
    // ⭐ NUOVO: Lista di 6 effetti (le facce del dado)
    @SerializedName(value = "opzioni", alternate = {"options", "facce"})
    private List<Effetto> opzioni;

    private transient Random random = new Random();

    // ⭐ NUOVO COSTRUTTORE
    public Dado(String nome, String id, String pathImg, List<Effetto> opzioni) {
        this.nome = nome;
        this.id = id;
        this.pathImg = pathImg;
        this.opzioni = opzioni;
        
        // Validazione
        if (opzioni == null || opzioni.size() != 6) {
            throw new IllegalArgumentException(
                "Un dado deve avere esattamente 6 opzioni (facce). " +
                "Trovate: " + (opzioni != null ? opzioni.size() : 0)
            );
        }
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

    public List<Effetto> getOpzioni() {
        return opzioni;
    }

    /**
     * Tira il dado: sceglie casualmente una delle 6 facce ed esegue l'effetto
     * 
     * @param stato Stato di gioco
     * @param attivo Giocatore attivo
     * @return L'effetto selezionato (utile per Horcrux)
     */
    public Effetto tiraDado(StatoDiGioco stato, Giocatore attivo) {
        if (opzioni == null || opzioni.size() != 6) {
            System.err.println("❌ Dado " + nome + " non ha 6 opzioni valide!");
            return null;
        }

        // Genera numero casuale 0-5 (6 facce)
        int indiceFaccia = random.nextInt(6);
        Effetto effettoSelezionato = opzioni.get(indiceFaccia);

        System.out.println("🎲 " + nome + " - Faccia " + (indiceFaccia + 1) + ": " + 
                         effettoSelezionato.getType());

        // Esegui effetto
        EsecutoreEffetti.eseguiEffetto(effettoSelezionato, stato, attivo);

        return effettoSelezionato;
    }
}