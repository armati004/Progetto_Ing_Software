package data;

import java.util.ArrayList;
import java.util.List;

public class Anno {
	private int anno;
	private List<String> nuoveCarteNegozio = new ArrayList<>();
	private List<String> nuoviMalvagi = new ArrayList<>();
	private List<String> nuoveArtiOscure = new ArrayList<>();
	private List<String> luoghi = new ArrayList<>();
	private Meccanica meccanica;
	
	
	public int getAnno() {
		return anno;
	}

	public void setAnno(int anno) {
		this.anno = anno;
	}

	public List<String> getNuoveCarteNegozio() {
		return nuoveCarteNegozio;
	}
	
	public void setNuoveCarteNegozio(List<String> nuoveCarteNegozio) {
		this.nuoveCarteNegozio = nuoveCarteNegozio;
	}
	
	public List<String> getNuoviMalvagi() {
		return nuoviMalvagi;
	}
	
	public void setNuoviMalvagi(List<String> nuoviMalvagi) {
		this.nuoviMalvagi = nuoviMalvagi;
	}

	public List<String> getNuoveArtiOscure() {
		return nuoveArtiOscure;
	}

	public void setNuoveArtiOscure(List<String> nuoveArtiOscure) {
		this.nuoveArtiOscure = nuoveArtiOscure;
	}

	public List<String> getLuoghi() {
		return luoghi;
	}

	public void setLuoghi(List<String> luoghi) {
		this.luoghi = luoghi;
	}

	public Meccanica getMeccanica() {
		return meccanica;
	}
	
	
}
