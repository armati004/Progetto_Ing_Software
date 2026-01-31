package data;

import com.google.gson.annotations.SerializedName;

public class Meccanica {
	@SerializedName("usoDadi")
	private Boolean contieneDadi;
	@SerializedName("usoCompetenze")
	private Boolean contieneCompetenze;
	@SerializedName("usoHorcrux")
	private Boolean contieneHorcrux;
	private String versioneEroi;
	
	public Boolean getContieneDadi() {
		return contieneDadi;
	}

	public Boolean getContieneCompetenze() {
		return contieneCompetenze;
	}

	public Boolean getContieneHorcrux() {
		return contieneHorcrux;
	}

	public String getVersioneEroi() {
		return versioneEroi;
	}

	public void setVersioneEroi(String versioneEroi) {
		this.versioneEroi = versioneEroi;
	}
	
	
}
