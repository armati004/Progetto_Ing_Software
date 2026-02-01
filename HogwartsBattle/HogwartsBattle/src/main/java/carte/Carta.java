package carte;

import java.util.List;
import com.google.gson.annotations.SerializedName;

import gioco.Giocatore;
import gioco.StatoDiGioco;
import gestoreEffetti.DurataEffetto;
import gestoreEffetti.Effetto;
import gestoreEffetti.EsecutoreEffetti;
import gestoreEffetti.Trigger;

public class Carta {
	private final String nome;
	private final String id;
	@SerializedName("class")
	protected
	final String classe;
	private final String descrizione;
	protected final int costo;
	@SerializedName(value = "pathImmagine", alternate = {"path-img"})
	private final String pathImmagine;
	private List<Effetto> effetti;
	private List<Trigger> triggers;
	
	public Carta(String nome, String id, String classe, String descrizione, int costo,String pathImmagine, 
			List<Effetto> effetti, List<Trigger> triggers) {
		this.nome = nome;
		this.id = id;
		this.classe = classe;
		this.descrizione = descrizione;
		this.costo = costo;
		this.pathImmagine = pathImmagine;
		this.setEffetti(effetti);
		this.setTriggers(triggers);
	}
	
	public void applicaEffetto(StatoDiGioco stato, Giocatore attivo) {
		if(this.getEffetti() != null && !(this.getEffetti().isEmpty())) {
			for(Effetto effetto : this.getEffetti()) {
				if(effetto.getDurata() == DurataEffetto.ISTANTANEO) {
					EsecutoreEffetti.eseguiEffetto(effetto, stato, attivo, this);
				}
				else if(effetto.getDurata() == DurataEffetto.TEMPORANEO) {
					stato.getGestoreEffetti().aggiungiEffettoTemporaneo(effetto.getType(), this);
				}
				else if(effetto.getDurata() == DurataEffetto.CONTINUO) {
					stato.getGestoreEffetti().aggiungiEffetto(effetto.getType(), this);
				}
			}
		}
		
		if(this.getTriggers() != null && !(this.getTriggers().isEmpty())) {
			for(Trigger trigger : this.getTriggers()) {
				stato.getGestoreTrigger().registraTrigger(trigger.getType(), trigger.getEffectToExecute(), this, trigger.getDurata());
			}
		}
	}
	
	public String getNome() {
		return nome;
	}

	public String getId() {
		return id;
	}

	public String getClasse() {
		return classe;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public int getCosto() {
		return costo;
	}

	public String getPathImmagine() {
		return pathImmagine;
	}

	public List<Effetto> getEffetti() {
		return effetti;
	}

	public void setEffetti(List<Effetto> effetti) {
		this.effetti = effetti;
	}

	public List<Trigger> getTriggers() {
		return triggers;
	}

	public void setTriggers(List<Trigger> triggers) {
		this.triggers = triggers;
	}

	public Object getTriggersInMano() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setNome(String nome2) {
		// TODO Auto-generated method stub
		
	}

	public Carta getCartaOrigine() {
		// TODO Auto-generated method stub
		return null;
	}
}
