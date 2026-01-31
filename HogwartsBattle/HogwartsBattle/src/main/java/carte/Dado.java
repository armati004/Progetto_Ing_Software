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
     * Tira il dado e applica un effetto casuale tra le 6 opzioni
     * 
     * @param stato Stato di gioco
     * @param attivo Giocatore attivo
     * @param opzioniEffetti Lista di 6 effetti possibili (uno per faccia del dado)
     * @return L'effetto che è stato eseguito  // ✅ Ora restituisce l'effetto
     */
    public Effetto tiraDado(StatoDiGioco stato, Giocatore attivo, List<Effetto> opzioniEffetti) {
        if (opzioniEffetti == null || opzioniEffetti.size() < 6) {
            throw new IllegalArgumentException("Deve essere fornita una lista di almeno 6 effetti per il dado.");
        }

        int indiceEffetto = random.nextInt(6);
        Effetto effettoSelezionato = opzioniEffetti.get(indiceEffetto);

        // ✅ Log migliorato
        System.out.println("🎲 " + nome + " - Faccia " + (indiceEffetto + 1) + ": " + 
                         effettoSelezionato.getType());

        EsecutoreEffetti.eseguiEffetto(effettoSelezionato, stato, attivo, null);
        
        return effettoSelezionato;  // ✅ Restituisce l'effetto eseguito
    }
}