package data;

import com.google.gson.annotations.SerializedName;

public class Meccanica {
	@SerializedName("usoDadi")
	private Boolean contieneDadi;
	@SerializedName("usoCompetenze")
	private Boolean contieneCompetenze;
	@SerializedName("usoHorcrux")
	private Boolean contieneHorcrux;
	@SerializedName("usoEncounter")
	private Boolean contieneEncounter;
	@SerializedName("usoPozioni")
	private Boolean contienePozioni;
	@SerializedName("usoDarkArtsPozioni")
	private Boolean contieneDarkArtsPozioni;
	private String versioneEroi;
	private Boolean upgradeEroi;
	
	public Boolean getContieneDadi() {
		return contieneDadi;
	}

	public Boolean getContieneCompetenze() {
		return contieneCompetenze;
	}

	public Boolean getContieneHorcrux() {
		return contieneHorcrux;
	}

	public Boolean getContieneEncounter() {
		return contieneEncounter != null ? contieneEncounter : false;
	}

	public Boolean getContienePozioni() {
		return contienePozioni != null ? contienePozioni : false;
	}

	public Boolean getContieneDarkArtsPozioni() {
		return contieneDarkArtsPozioni != null ? contieneDarkArtsPozioni : false;
	}

	public String getVersioneEroi() {
		return versioneEroi;
	}

	public void setVersioneEroi(String versioneEroi) {
		this.versioneEroi = versioneEroi;
	}

	public Boolean getUpgradeEroi() {
		return upgradeEroi != null ? upgradeEroi : false;
	}

	public void setUpgradeEroi(Boolean upgradeEroi) {
		this.upgradeEroi = upgradeEroi;
	}
	
	
}
