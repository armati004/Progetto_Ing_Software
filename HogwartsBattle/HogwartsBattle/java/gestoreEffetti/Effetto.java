package gestoreEffetti;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Effetto {
	private final TipoEffetto type;
	private final Integer qta;
	private final BersaglioEffetto target;
	private final DurataEffetto durata;
	@SerializedName("qta-target")
	private final Integer qtaTarget;
	private List<Effetto> opzioni;
	private final Effetto ripetuto;
	
	public Effetto(TipoEffetto type, Integer qta, BersaglioEffetto target, DurataEffetto durata, Integer qtaTarget, List<Effetto> opzioni,
			Effetto ripetuto) {
		this.type = type;
		this.qta = qta;
		this.target = target;
		this.durata = durata;
		this.qtaTarget = qtaTarget;
		this.setOpzioni(opzioni);
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
}
