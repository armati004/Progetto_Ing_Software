package data;

import java.util.List;

/**
 * Classe che rappresenta i dati salvati di una partita Include solo le
 * informazioni essenziali per ripristinare lo stato
 */
public class GameSaveData {

	// Informazioni progressione
	private int annoCorrente;
	private int numeroGiocatori;
	private boolean vittoriaUltimaPartita;

	// Informazioni giocatori
	private List<PlayerSaveData> giocatori;

	// Timestamp salvataggio
	private long timestamp;
	private String dataOra; // Formato leggibile

	// Metadati
	private String nomePartita;
	private int giocatoreCorrente;
	private List<String> carteNegozioRimaste;

	/**
	 * Costruttore vuoto per Gson
	 */
	public GameSaveData() {
	}

	/**
	 * Costruttore completo
	 */
	public GameSaveData(int annoCorrente, int numeroGiocatori, List<PlayerSaveData> giocatori, int giocatoreCorrente,
			boolean vittoriaUltimaPartita, String nomePartita, List<String> carteAcquisite) {
		this.annoCorrente = annoCorrente;
		this.numeroGiocatori = numeroGiocatori;
		this.giocatori = giocatori;
		this.giocatoreCorrente = giocatoreCorrente;
		this.vittoriaUltimaPartita = vittoriaUltimaPartita;
		this.nomePartita = nomePartita;
		this.carteNegozioRimaste = carteAcquisite;
		this.timestamp = System.currentTimeMillis();
		this.dataOra = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(timestamp));
	}

	// Getters e Setters

	public int getAnnoCorrente() {
		return annoCorrente;
	}

	public void setAnnoCorrente(int annoCorrente) {
		this.annoCorrente = annoCorrente;
	}

	public int getNumeroGiocatori() {
		return numeroGiocatori;
	}

	public void setNumeroGiocatori(int numeroGiocatori) {
		this.numeroGiocatori = numeroGiocatori;
	}

	public List<PlayerSaveData> getGiocatori() {
		return giocatori;
	}

	public void setGiocatori(List<PlayerSaveData> giocatori) {
		this.giocatori = giocatori;
	}

	public List<String> getCarteNegozioRimaste() {
	    return carteNegozioRimaste;
	}

	public void setCarteNegozioRimaste(List<String> carteNegozioRimaste) {
	    this.carteNegozioRimaste = carteNegozioRimaste;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getDataOra() {
		return dataOra;
	}

	public void setDataOra(String dataOra) {
		this.dataOra = dataOra;
	}

	public String getNomePartita() {
		return nomePartita;
	}

	public void setNomePartita(String nomePartita) {
		this.nomePartita = nomePartita;
	}

	public int getGiocatoreCorrente() {
		return giocatoreCorrente;
	}

	public void setGiocatoreCorrente(int giocatoreCorrente) {
		this.giocatoreCorrente = giocatoreCorrente;
	}

	public boolean isVittoriaUltimaPartita() {
		return vittoriaUltimaPartita;
	}

	public void setVittoriaUltimaPartita(boolean vittoriaUltimaPartita) {
		this.vittoriaUltimaPartita = vittoriaUltimaPartita;
	}

	@Override
	public String toString() {
		return "GameSaveData{" + "anno=" + annoCorrente + ", giocatori=" + numeroGiocatori + ", nome='" + nomePartita
				+ '\'' + ", data=" + dataOra + '}';
	}
}