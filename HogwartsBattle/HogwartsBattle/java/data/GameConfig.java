package data;

import java.util.ArrayList;
import java.util.List;

import carte.Luogo;

public class GameConfig {
	private int anno;
	private List<String> carteNegozioId = new ArrayList<>();
	private List<String> malvagiId = new ArrayList<>();
	private List<String> artiOscureId = new ArrayList<>();
	private List<String> luoghiId = new ArrayList<>();
	
	private Boolean contieneDadi = false;
	private Boolean contieneCompetenze = false;
	private Boolean contieneHorcrux = false;
	
	public void aggiornaMeccaniche(Meccanica m) {
		if(m.getContieneDadi() == true) {
			this.contieneDadi = true;
		}
		if(m.getContieneCompetenze() == true) {
			this.contieneCompetenze = true;
		}
		if(m.getContieneHorcrux() == true) {
			this.contieneHorcrux = true;
		}
	}
	
	/**
	 * Crea la lista di luoghi per un anno specifico.
	 * I luoghi sono già ordinati nel JSON.
	 */
	public static List<Luogo> caricaLuoghiPerAnno(List<String> idLuoghi) {
	    List<Luogo> luoghi = new ArrayList<>();
	    
	    for (String id : idLuoghi) {
	        try {
	            Luogo luogo = LocationFactory.creaLuogo(id);
	            
	            luoghi.add(luogo);
	            
	        } catch (IllegalArgumentException e) {
	            System.err.println("⚠️ Luogo non trovato: " + id);
	        }
	    }
	    
	    return luoghi;
	}
	
	public int getAnno() {
		return anno;
	}

	public void setAnno(int anno) {
		this.anno = anno;
	}

	public List<String> getCarteNegozioId() {
		return carteNegozioId;
	}

	public List<String> getMalvagiId() {
		return malvagiId;
	}

	public List<String> getArtiOscureId() {
		return artiOscureId;
	}

	public List<String> getLuoghiId() {
		return luoghiId;
	}

	public void setLuoghiId(List<String> luoghiId) {
		this.luoghiId = luoghiId;
	}

	public Boolean getContieneDadi() {
		return contieneDadi;
	}

	public Boolean getContieneCompetenze() {
		return contieneCompetenze;
	}

	public Boolean getContieneHorcrux() {
		return contieneHorcrux;
	}

}
