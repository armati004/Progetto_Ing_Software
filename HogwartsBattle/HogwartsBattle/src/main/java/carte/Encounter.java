package carte;

import java.util.ArrayList;
import java.util.List;

import gestoreEffetti.Effetto;
import gioco.Giocatore;
import gioco.StatoDiGioco;

/**
 * Encounter - Carte obiettivo (Pack 1-4)
 * 
 * Funzionamento:
 * - Ogni anno ha 3 Encounter
 * - Un Encounter è attivo alla volta
 * - Hanno effetto continuo negativo
 * - Hanno condizione di completamento
 * - Quando completato, danno ricompensa (reward)
 */
public class Encounter {
    
    private String nome;
    private String id;
    private int pack;
    private int ordine;
    
    // Malvagi associati (quando uno è rivelato, attiva questo Encounter)
    private List<String> malvagiAssociati;
    
    // Effetto continuo (negativo) dell'Encounter
    private String descrizioneEffetto;
    private List<Effetto> effettiContinui;
    
    // Condizione di completamento
    private String descrizioneCompletamento;
    private String tipoCondizione;  // Es: "GIOCA_CARTE_TIPO", "ACQUISTA_INFLUENZA"
    private int valoreRichiesto;     // Es: 4 carte, 8 influenza
    private int valoreCarta;         // Per GIOCA_CARTE_VALORE: valore minimo carta
    private String tipoCartaRichiesto; // Per GIOCA_CARTE_TIPO: "OGGETTO", "INCANTESIMO", ecc.
    
    // Ricompensa quando completato
    private String descrizioneRicompensa;
    private List<Effetto> reward;
    
    // Stato
    private boolean completato;
    
    // Immagine
    private String pathImg;
    
    public Encounter() {
        this.malvagiAssociati = new ArrayList<>();
        this.effettiContinui = new ArrayList<>();
        this.reward = new ArrayList<>();
        this.completato = false;
    }
    
    /**
     * Applica l'effetto continuo dell'Encounter.
     * Chiamato durante la fase appropriata del turno.
     */
    public void applicaEffettoContinuo(StatoDiGioco stato, Giocatore giocatore) {
        if (completato) {
            return;
        }
        
        if (effettiContinui == null || effettiContinui.isEmpty()) {
            return;
        }
        
        System.out.println("🎯 Encounter attivo: " + nome);
        
        for (Effetto effetto : effettiContinui) {
            gestoreEffetti.EsecutoreEffetti.eseguiEffetto(effetto, stato, giocatore);
        }
    }
    
    /**
     * Applica la ricompensa quando l'Encounter viene completato.
     */
    public void applicaRicompensa(StatoDiGioco stato, Giocatore giocatore) {
        if (!completato) {
            return;
        }
        
        if (reward == null || reward.isEmpty()) {
            return;
        }
        
        System.out.println("🎉 Ricompensa Encounter: " + nome);
        
        for (Effetto effetto : reward) {
            gestoreEffetti.EsecutoreEffetti.eseguiEffetto(effetto, stato, giocatore);
        }
    }
    
    // Getters e Setters
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
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
    
    public String getDescrizioneEffetto() {
        return descrizioneEffetto;
    }
    
    public void setDescrizioneEffetto(String descrizioneEffetto) {
        this.descrizioneEffetto = descrizioneEffetto;
    }
    
    public List<Effetto> getEffettiContinui() {
        return effettiContinui;
    }
    
    public void setEffettiContinui(List<Effetto> effettiContinui) {
        this.effettiContinui = effettiContinui;
    }
    
    public String getDescrizioneCompletamento() {
        return descrizioneCompletamento;
    }
    
    public void setDescrizioneCompletamento(String descrizioneCompletamento) {
        this.descrizioneCompletamento = descrizioneCompletamento;
    }
    
    public String getTipoCondizione() {
        return tipoCondizione;
    }
    
    public void setTipoCondizione(String tipoCondizione) {
        this.tipoCondizione = tipoCondizione;
    }
    
    public int getValoreRichiesto() {
        return valoreRichiesto;
    }
    
    public void setValoreRichiesto(int valoreRichiesto) {
        this.valoreRichiesto = valoreRichiesto;
    }
    
    public int getValoreCarta() {
        return valoreCarta;
    }
    
    public void setValoreCarta(int valoreCarta) {
        this.valoreCarta = valoreCarta;
    }
    
    public String getTipoCartaRichiesto() {
        return tipoCartaRichiesto;
    }
    
    public void setTipoCartaRichiesto(String tipoCartaRichiesto) {
        this.tipoCartaRichiesto = tipoCartaRichiesto;
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
    
    public String getPathImg() {
        return pathImg;
    }
    
    public void setPathImg(String pathImg) {
        this.pathImg = pathImg;
    }
    
    @Override
    public String toString() {
        return "🎯 " + nome + " - " + 
               (completato ? "✓ Completato" : descrizioneCompletamento);
    }
}
