package carte;

import java.util.ArrayList;
import java.util.List;

import gestoreEffetti.Effetto;
import gestoreEffetti.Trigger;

/**
 * Rappresenta una carta Encounter (Incontro) dell'espansione Charms & Potions.
 * Gli Encounter sono sfide progressive che gli eroi devono completare
 * accumulando progressi attraverso azioni specifiche.
 * 
 * Estende Carta per integrarsi nel sistema esistente.
 */
public class Encounter extends Carta {
    private int pack; // Pack di appartenenza (1, 2, 3, 4)
    private int ordine; // 1 of 3, 2 of 3, 3 of 3
    private List<String> malvagiAssociati; // Malvagi che devono essere mescolati nel mazzo
    
    // Condizioni per completare
    private String descrizioneCompletamento;
    private TipoCondizioneEncounter tipoCondizione;
    private int valoreRichiesto; // Es: 4 Items, 8 influenza, ecc.
    private int progressoAttuale;
    
    // Ricompensa
    private String descrizioneRicompensa;
    private List<Effetto> reward;
    private boolean completato;
    private boolean rewardUsata; // "Once per game"

    /**
     * Costruttore completo per Encounter.
     */
    public Encounter(String nome, String id, String classe, String descrizione, int costo, String pathImmagine,
                    List<Effetto> effetti, List<Trigger> triggers, 
                    int pack, int ordine, List<String> malvagiAssociati,
                    String descrizioneCompletamento, TipoCondizioneEncounter tipoCondizione, 
                    int valoreRichiesto, String descrizioneRicompensa, List<Effetto> reward) {
        
        super(nome, id, classe, descrizione, costo, pathImmagine, effetti, triggers);
        
        this.pack = pack;
        this.ordine = ordine;
        this.malvagiAssociati = malvagiAssociati != null ? malvagiAssociati : new ArrayList<>();
        this.descrizioneCompletamento = descrizioneCompletamento;
        this.tipoCondizione = tipoCondizione;
        this.valoreRichiesto = valoreRichiesto;
        this.progressoAttuale = 0;
        this.descrizioneRicompensa = descrizioneRicompensa;
        this.reward = reward != null ? reward : new ArrayList<>();
        this.completato = false;
        this.rewardUsata = false;
    }

    // Getters e Setters
    public int getPack() {
        return pack;
    }

    public void setPack(int pack) {
        this.pack = pack;
    }

    public int getOrdine() {
        return ordine;
    }

    public void setOrdine(int ordine) {
        this.ordine = ordine;
    }

    public List<String> getMalvagiAssociati() {
        return malvagiAssociati;
    }

    public void setMalvagiAssociati(List<String> malvagiAssociati) {
        this.malvagiAssociati = malvagiAssociati;
    }

    public String getDescrizioneCompletamento() {
        return descrizioneCompletamento;
    }

    public void setDescrizioneCompletamento(String descrizioneCompletamento) {
        this.descrizioneCompletamento = descrizioneCompletamento;
    }

    public TipoCondizioneEncounter getTipoCondizione() {
        return tipoCondizione;
    }

    public void setTipoCondizione(TipoCondizioneEncounter tipoCondizione) {
        this.tipoCondizione = tipoCondizione;
    }

    public int getValoreRichiesto() {
        return valoreRichiesto;
    }

    public void setValoreRichiesto(int valoreRichiesto) {
        this.valoreRichiesto = valoreRichiesto;
    }

    public int getProgressoAttuale() {
        return progressoAttuale;
    }

    public void setProgressoAttuale(int progressoAttuale) {
        this.progressoAttuale = progressoAttuale;
    }

    public void aggiungiProgresso(int quantita) {
        this.progressoAttuale += quantita;
        if (this.progressoAttuale >= this.valoreRichiesto) {
            this.completato = true;
        }
    }

    public String getDescrizioneRicompensa() {
        return descrizioneRicompensa;
    }

    public void setDescrizioneRicompensa(String descrizioneRicompensa) {
        this.descrizioneRicompensa = descrizioneRicompensa;
    }

    public List<Effetto> getReward() {
        return reward;
    }

    public void setReward(List<Effetto> reward) {
        this.reward = reward;
    }

    public boolean isCompletato() {
        return completato;
    }

    public void setCompletato(boolean completato) {
        this.completato = completato;
    }

    public boolean isRewardUsata() {
        return rewardUsata;
    }

    public void setRewardUsata(boolean rewardUsata) {
        this.rewardUsata = rewardUsata;
    }

    @Override
    public String toString() {
        return getNome() + " (" + ordine + " of 3) - " + 
               (completato ? "COMPLETATO" : progressoAttuale + "/" + valoreRichiesto);
    }
}
