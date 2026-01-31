package data;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Anno {
	@SerializedName("anno")
	private int anno;

	// Mappiamo "meccaniche" dal JSON
	@SerializedName("meccaniche")
	private Meccanica meccanica;

	// Mappiamo "luoghi"
	@SerializedName("luoghi")
	private List<String> luoghi = new ArrayList<>();

	// --- CORREZIONI CHIAVE QUI SOTTO ---

	// Nel JSON è "aggiunteMazzoNegozio"
	@SerializedName(value = "aggiunteMazzoNegozio", alternate = { "nuoveCarteNegozio", "hogwartsDeckToAdd" })
	private List<String> nuoveCarteNegozio = new ArrayList<>();

	// Nel JSON è "aggiuntaMalvagi"
	@SerializedName(value = "aggiuntaMalvagi", alternate = { "nuoviMalvagi", "villainsToAdd" })
	private List<String> nuoviMalvagi = new ArrayList<>();

	// Nel JSON è "aggiuntaArtiOscure"
	@SerializedName(value = "aggiuntaArtiOscure", alternate = { "nuoveArtiOscure", "darkArtsToAdd" })
	private List<String> nuoveArtiOscure = new ArrayList<>();
	
	@SerializedName(value = "aggiuntaHorcrux", alternate = {"nuoviHorcrux", "horcruxToAdd"})
	private List<String> nuoviHorcrux = new ArrayList<>();

	// Getters
	public int getAnno() {
		return anno;
	}

	public Meccanica getMeccanica() {
		return meccanica;
	}

	public List<String> getLuoghi() {
		return luoghi;
	}

	public List<String> getNuoveCarteNegozio() {
		return nuoveCarteNegozio;
	}

	public List<String> getNuoviMalvagi() {
		return nuoviMalvagi;
	}

	public List<String> getNuoveArtiOscure() {
		return nuoveArtiOscure;
	}
	
	public List<String> getNuoviHorcrux() {
	    return nuoviHorcrux;
	}

}
