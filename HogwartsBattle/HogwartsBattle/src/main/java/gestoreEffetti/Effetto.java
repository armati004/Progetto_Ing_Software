package gestoreEffetti;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * Effetto - Rappresenta un effetto che può essere applicato nel gioco
 * VERSIONE CORRETTA per deserializzazione Gson
 */
public class Effetto {
	private TipoEffetto type;
	private Integer qta;
	private BersaglioEffetto target;
	private DurataEffetto durata;
	
	@SerializedName(value = "qta-target", alternate = {"qtaTarget", "targetCount"})
	private Integer qtaTarget;
	
	// ⭐ FIX: Supporta sia "opzioni" che "options" nei JSON
	@SerializedName(value = "opzioni", alternate = {"options"})
	private List<Effetto> opzioni;
	
	private Effetto ripetuto;
	
	// ⭐ Costruttore vuoto necessario per Gson
	public Effetto() {
	}
	
	// Costruttore completo per creazione manuale
	public Effetto(TipoEffetto type, Integer qta, BersaglioEffetto target, DurataEffetto durata, Integer qtaTarget, List<Effetto> opzioni, Effetto ripetuto) {
		this.type = type;
		this.qta = qta;
		this.target = target;
		this.durata = durata;
		this.qtaTarget = qtaTarget;
		this.opzioni = opzioni;
		this.ripetuto = ripetuto;
	}

	public TipoEffetto getType() {
		return type;
	}

	public Integer getQta() {
		return qta;
	}

	public BersaglioEffetto getTarget() {
		return target;
	}

	public DurataEffetto getDurata() {
		return durata;
	}
	
	public Integer getQtaTarget() {
		return qtaTarget;
	}

	public List<Effetto> getOpzioni() {
		return opzioni;
	}

	public void setOpzioni(List<Effetto> opzioni) {
		this.opzioni = opzioni;
	}

	public Effetto getRipetuto() {
		return ripetuto;
	}

	public void setTarget(BersaglioEffetto target) {
		this.target = target;
	}
	
	public void setType(TipoEffetto type) {
		this.type = type;
	}
	
	public void setQta(Integer qta) {
		this.qta = qta;
	}
	
	public void setDurata(DurataEffetto durata) {
		this.durata = durata;
	}
	
	public void setQtaTarget(Integer qtaTarget) {
		this.qtaTarget = qtaTarget;
	}
	
	public void setRipetuto(Effetto ripetuto) {
		this.ripetuto = ripetuto;
	}
}