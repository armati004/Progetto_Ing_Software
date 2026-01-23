package gestoreEffetti;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Trigger {
	private final TipoTrigger type;
	private List<Effetto> effectToExecute;
	private final BersaglioEffetto target;
	@SerializedName("qta-quorum")
	private final Integer qtaQuorum;
	private Boolean attivato1Volta;
	private DurataEffetto durata;
	
	public Trigger(TipoTrigger type, BersaglioEffetto target, Integer qtaQuorum, Boolean attivato1Volta, List<Effetto> effectToExecute, DurataEffetto durata) {
		this.type = type;
		this.target = target;
		this.qtaQuorum = qtaQuorum;
		this.setAttivato1Volta(attivato1Volta);
		this.setEffectToExecute(effectToExecute);
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
}
