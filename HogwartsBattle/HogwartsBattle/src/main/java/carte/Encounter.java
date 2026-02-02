package carte;

import java.util.ArrayList;
import java.util.List;

import gestoreEffetti.Effetto;
import gestoreEffetti.TipoCondizioneEncounter;
import gestoreEffetti.Trigger;
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
    private String classe;
    private String descrizione;
    private int pack;
    private int ordine;
    private int costo;
    
    // Malvagi associati (quando uno è rivelato, attiva questo Encounter)
    private List<String> malvagiAssociati;
    
    // Effetto continuo (negativo) dell'Encounter
    private String descrizioneEffetto;
    private List<Effetto> effettiContinui;
    private List<Trigger> triggers;
    
    // Condizione di completamento
    private String descrizioneCompletamento;
    private TipoCondizioneEncounter tipoCondizione;
    private int valoreRichiesto;
    private int valoreCarta;
    private String tipoCartaRichiesto;
    
    // Ricompensa quando completato
    private String descrizioneRicompensa;
    private List<Effetto> reward;
    
    // Stato
    private boolean completato;
    private int progressoAttuale;
    private boolean rewardUsata;
    
    // Immagine
    private String pathImmagine;
    
    /**
     * Costruttore vuoto.
     */
    public Encounter() {
        this.malvagiAssociati = new ArrayList<>();
        this.effettiContinui = new ArrayList<>();
        this.triggers = new ArrayList<>();
        this.reward = new ArrayList<>();
        this.completato = false;
        this.progressoAttuale = 0;
        this.rewardUsata = false;
    }
    
    /**
     * Costruttore completo.
     */
    public Encounter(String nome, String id, String classe, String descrizioneEffetto, 
                     int costo, String pathImmagine, List<Effetto> effettiContinui, 
                     List<Trigger> triggers, int pack, int ordine, 
                     List<String> malvagiAssociati, String descrizioneCompletamento, 
                     TipoCondizioneEncounter tipoCondizione, int valoreRichiesto, 
                     String descrizioneRicompensa, List<Effetto> reward) {
        
        this.nome = nome;
        this.id = id;
        this.classe = classe;
        this.descrizione = descrizioneEffetto;
        this.descrizioneEffetto = descrizioneEffetto;
        this.costo = costo;
        this.pathImmagine = pathImmagine;
        this.effettiContinui = effettiContinui != null ? effettiContinui : new ArrayList<>();
        this.triggers = triggers != null ? triggers : new ArrayList<>();
        this.pack = pack;
        this.ordine = ordine;
        this.malvagiAssociati = malvagiAssociati != null ? malvagiAssociati : new ArrayList<>();
        this.descrizioneCompletamento = descrizioneCompletamento;
        this.tipoCondizione = tipoCondizione;
        this.valoreRichiesto = valoreRichiesto;
        this.descrizioneRicompensa = descrizioneRicompensa;
        this.reward = reward != null ? reward : new ArrayList<>();
        this.completato = false;
        this.progressoAttuale = 0;
        this.rewardUsata = false;
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
        if (!completato || rewardUsata) {
            return;
        }
        
        if (reward == null || reward.isEmpty()) {
            return;
        }
        
        System.out.println("🎉 Ricompensa Encounter: " + nome);
        
        for (Effetto effetto : reward) {
            gestoreEffetti.EsecutoreEffetti.eseguiEffetto(effetto, stato, giocatore);
        }
        
        rewardUsata = true;
    }
    
    // ============================================================================
    // GETTERS E SETTERS
    // ============================================================================
    
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
    
    public String getClasse() {
        return classe;
    }
    
    public void setClasse(String classe) {
        this.classe = classe;
    }
    
    public String getDescrizione() {
        return descrizione;
    }
    
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
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
    
    public int getCosto() {
        return costo;
    }
    
    public void setCosto(int costo) {
        this.costo = costo;
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
    
    public List<Effetto> getEffetti() {
        return effettiContinui;
    }
    
    public void setEffetti(List<Effetto> effetti) {
        this.effettiContinui = effetti;
    }
    
    public List<Trigger> getTriggers() {
        return triggers;
    }
    
    public void setTriggers(List<Trigger> triggers) {
        this.triggers = triggers;
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
    
    public int getProgressoAttuale() {
        return progressoAttuale;
    }
    
    public void setProgressoAttuale(int progressoAttuale) {
        this.progressoAttuale = progressoAttuale;
    }
    
    public boolean isRewardUsata() {
        return rewardUsata;
    }
    
    public void setRewardUsata(boolean rewardUsata) {
        this.rewardUsata = rewardUsata;
    }
    
    public void setRewardUsata() {
        this.rewardUsata = true;
    }
    
    public String getPathImmagine() {
        return pathImmagine;
    }
    
    public void setPathImmagine(String pathImmagine) {
        this.pathImmagine = pathImmagine;
    }
    
    @Override
    public String toString() {
        return "🎯 " + nome + " - " + 
               (completato ? "✓ Completato" : descrizioneCompletamento);
    }

	public void applicaReward(StatoDiGioco stato, Giocatore giocatore) {
		// TODO Auto-generated method stub
		
	}

	public void setTipoCondizione(String tipoCondizione2) {
		// TODO Auto-generated method stub
		
	}
}
