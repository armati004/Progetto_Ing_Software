package carte;
import java.util.List;

import gioco.Giocatore;
import gioco.StatoDiGioco;
import gestoreEffetti.Effetto;
import gestoreEffetti.EsecutoreEffetti;
import gestoreEffetti.Trigger;

/**
 * Enum per definire come un Malvagio può essere sconfitto.
 * NOVITÀ ESPANSIONE: I malvagi possono essere sconfitti con Attacco, Influenza o entrambi.
 */
enum TipoSconfittaMalvagio {
    SOLO_ATTACCO,           // Sconfitto solo con segnalini attacco (default, gioco base)
    SOLO_INFLUENZA,         // Sconfitto solo con segnalini influenza (espansione)
    ATTACCO_O_INFLUENZA     // Sconfitto con attacco O influenza, chi arriva prima (espansione)
}

/**
 * Classe Malvagio - Rappresenta un Villain o Creature.
 * 
 * NOVITÀ ESPANSIONE CHARMS & POTIONS:
 * - Può essere sconfitto con Influenza invece che Attacco
 * - Supporto per Inferi (Villain-Creature ibrido)
 * - Supporto per multiple Lord Voldemort cards
 */
public class Malvagio extends Carta {
	
	private final int vita;
	private int danno;              // Segnalini attacco assegnati
	private int influenza;          // NUOVO: Segnalini influenza assegnati
    private List<Effetto> reward;
    private TipoSconfittaMalvagio tipoSconfitta; // NUOVO: Come viene sconfitto
    
    // NUOVO: Pack 3-4 - Carte speciali
    private boolean isCreature;        // true per Inferi (Villain-Creature)
    private boolean isLordVoldemort;   // true per carte Lord Voldemort
    private boolean isFinalBoss;       // true per Voldemort Pack 4 finale
    
	
    /**
     * Costruttore base - Malvagio sconfitto solo con Attacco (comportamento default).
     */
    public Malvagio(String nome, String id, String classe, String descrizione, int costo, String pathImmagine,
			List<Effetto> effetti, List<Trigger> triggers, List<Effetto> reward, int vita) {
		super(nome, id, classe, descrizione, costo, pathImmagine, effetti, triggers);
		this.reward = reward;
		this.vita = vita;
		this.danno = 0;
		this.influenza = 0;
		this.tipoSconfitta = TipoSconfittaMalvagio.SOLO_ATTACCO; // Default gioco base
		this.isCreature = false;
		this.isLordVoldemort = false;
		this.isFinalBoss = false;
	}
	
	/**
	 * Costruttore con tipo sconfitta personalizzato.
	 * Usato per malvagi dell'espansione che possono essere sconfitti con Influenza.
	 */
	public Malvagio(String nome, String id, String classe, String descrizione, int costo, String pathImmagine,
			List<Effetto> effetti, List<Trigger> triggers, List<Effetto> reward, int vita, 
			TipoSconfittaMalvagio tipoSconfitta) {
		this(nome, id, classe, descrizione, costo, pathImmagine, effetti, triggers, reward, vita);
		this.tipoSconfitta = tipoSconfitta;
	}
	
	/**
	 * Aggiunge segnalini attacco al malvagio.
	 */
	public void aggiungiAttacco(int quantita) {
		this.danno += quantita;
		System.out.println("→ Assegnati " + quantita + " ⚔️ a " + getNome() + " (" + danno + "/" + vita + ")");
	}
	
	/**
	 * Aggiunge segnalini influenza al malvagio.
	 * NUOVO per espansione.
	 */
	public void aggiungiInfluenza(int quantita) {
		this.influenza += quantita;
		System.out.println("→ Assegnati " + quantita + " 💰 a " + getNome() + " (" + influenza + "/" + vita + ")");
	}
	
	/**
	 * Rimuove segnalini attacco dal malvagio.
	 * Usato per effetti di guarigione del malvagio.
	 */
	public void rimuoviAttacco(int quantita) {
		this.danno = Math.max(0, this.danno - quantita);
		System.out.println("→ Rimossi " + quantita + " ⚔️ da " + getNome() + " (" + danno + "/" + vita + ")");
	}
	
	/**
	 * Rimuove segnalini influenza dal malvagio.
	 */
	public void rimuoviInfluenza(int quantita) {
		this.influenza = Math.max(0, this.influenza - quantita);
		System.out.println("→ Rimossi " + quantita + " 💰 da " + getNome() + " (" + influenza + "/" + vita + ")");
	}
    
	/**
	 * Applica la ricompensa quando il malvagio viene sconfitto.
	 */
    public void defeat(StatoDiGioco stato, Giocatore g) {
        System.out.println("✅ Sconfitto " + this.getNome() + "!");
        
        // Applica tutti gli effetti della ricompensa
        for(Effetto e : this.getReward()) {
        	EsecutoreEffetti.eseguiEffetto(e, stato, g);
        }
    }
    
    /**
     * Verifica se il malvagio è sconfitto.
     * NUOVO: Considera anche i segnalini influenza in base al tipo sconfitta.
     */
	public boolean isSconfitto() {
		switch (tipoSconfitta) {
			case SOLO_ATTACCO:
				return danno >= vita;
				
			case SOLO_INFLUENZA:
				return influenza >= vita;
				
			case ATTACCO_O_INFLUENZA:
				return danno >= vita || influenza >= vita;
				
			default:
				return danno >= vita;
		}
	}
	
	/**
	 * Verifica se il malvagio può essere attaccato con segnalini attacco.
	 */
	public boolean accettaAttacco() {
		return tipoSconfitta == TipoSconfittaMalvagio.SOLO_ATTACCO || 
		       tipoSconfitta == TipoSconfittaMalvagio.ATTACCO_O_INFLUENZA;
	}
	
	/**
	 * Verifica se il malvagio può essere attaccato con segnalini influenza.
	 */
	public boolean accettaInfluenza() {
		return tipoSconfitta == TipoSconfittaMalvagio.SOLO_INFLUENZA || 
		       tipoSconfitta == TipoSconfittaMalvagio.ATTACCO_O_INFLUENZA;
	}
	
	/**
	 * Verifica se questo malvagio è un Villain.
	 * Per Inferi (Villain-Creature), ritorna sempre true.
	 */
	public boolean isVillain() {
		return true; // Tutti i malvagi sono villain, anche gli Inferi
	}
	
	/**
	 * Verifica se questo malvagio è una Creature.
	 * Solo gli Inferi (Pack 3) sono Creature.
	 */
	public boolean isCreature() {
		return isCreature;
	}
	
	/**
	 * Ottiene una descrizione del progresso verso la sconfitta.
	 */
	public String getProgressoSconfitta() {
		switch (tipoSconfitta) {
			case SOLO_ATTACCO:
				return "⚔️ " + danno + "/" + vita;
				
			case SOLO_INFLUENZA:
				return "💰 " + influenza + "/" + vita;
				
			case ATTACCO_O_INFLUENZA:
				return "⚔️ " + danno + "/" + vita + " OR 💰 " + influenza + "/" + vita;
				
			default:
				return "⚔️ " + danno + "/" + vita;
		}
	}

    // =============== GETTERS E SETTERS ===============
    
    public int getDanno() {
		return danno;
	}

	public void setDanno(int danno) {
		this.danno = danno;
	}
	
	public int getInfluenza() {
		return influenza;
	}
	
	public void setInfluenza(int influenza) {
		this.influenza = influenza;
	}

	public int getVita() {
		return vita;
	}

	public List<Effetto> getReward() {
		return reward;
	}
	
	public TipoSconfittaMalvagio getTipoSconfitta() {
		return tipoSconfitta;
	}
	
	public void setTipoSconfitta(TipoSconfittaMalvagio tipoSconfitta) {
		this.tipoSconfitta = tipoSconfitta;
	}
	
	public void setIsCreature(boolean isCreature) {
		this.isCreature = isCreature;
	}
	
	public boolean isLordVoldemort() {
		return isLordVoldemort;
	}
	
	public void setIsLordVoldemort(boolean isLordVoldemort) {
		this.isLordVoldemort = isLordVoldemort;
	}
	
	public boolean isFinalBoss() {
		return isFinalBoss;
	}
	
	public void setIsFinalBoss(boolean isFinalBoss) {
		this.isFinalBoss = isFinalBoss;
	}
	
	@Override
	public String toString() {
		return getNome() + " " + getProgressoSconfitta() + 
		       (isCreature ? " [Villain-Creature]" : "") +
		       (isLordVoldemort ? " [LORD VOLDEMORT]" : "");
	}
}
