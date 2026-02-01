package gestoreEffetti;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * Trigger - Rappresenta un trigger che attiva effetti in risposta a eventi
 * VERSIONE CORRETTA per deserializzazione Gson
 */
public class Trigger {
	private TipoTrigger type;
	private List<Effetto> effectToExecute;
	private BersaglioEffetto target;
	
	// FIX: Supporta sia "qta-quorum" che "qtaQuorum" nei JSON
	@SerializedName(value = "qta-quorum", alternate = {"qtaQuorum"})
	private Integer qtaQuorum;
	
	private Boolean attivato1Volta;
	private DurataEffetto durata;
	
	// ⭐ Costruttore vuoto necessario per Gson
	public Trigger() {
	}
	
	// Costruttore completo per creazione manuale
	public Trigger(TipoTrigger type, BersaglioEffetto target, Integer qtaQuorum, Boolean attivato1Volta, List<Effetto> effectToExecute, DurataEffetto durata) {
		this.type = type;
		this.target = target;
		this.qtaQuorum = qtaQuorum;
		this.attivato1Volta = attivato1Volta;
		this.effectToExecute = effectToExecute;
		this.durata = durata;
	}

	public TipoTrigger getType() {
		return type;
	}

	public List<Effetto> getEffectToExecute() {
		return effectToExecute;
	}

	public void setEffectToExecute(List<Effetto> effectToExecute) {
		this.effectToExecute = effectToExecute;
	}

	public BersaglioEffetto getTarget() {
		return target;
	}

	public Integer getQtaQuorum() {
		return qtaQuorum;
	}

	public Boolean getAttivato1Volta() {
		return attivato1Volta;
	}

	public void setAttivato1Volta(Boolean attivato1Volta) {
		this.attivato1Volta = attivato1Volta;
	}

	public DurataEffetto getDurata() {
		return durata;
	}
	
	public void setType(TipoTrigger type) {
		this.type = type;
	}
	
	public void setTarget(BersaglioEffetto target) {
		this.target = target;
	}
	
	public void setQtaQuorum(Integer qtaQuorum) {
		this.qtaQuorum = qtaQuorum;
	}
	
	public void setDurata(DurataEffetto durata) {
		this.durata = durata;
	}
}