package data;

/**
 * Classe che rappresenta i dati salvati di un singolo giocatore
 */
public class PlayerSaveData {
    
    private String nomeEroe;
    private String idCompetenza; // null se non ha competenza (anni < 6)
    
    /**
     * Costruttore vuoto per Gson
     */
    public PlayerSaveData() {
    }
    
    /**
     * Costruttore
     */
    public PlayerSaveData(String nomeEroe, String idCompetenza) {
        this.nomeEroe = nomeEroe;
        this.idCompetenza = idCompetenza;
    }
    
    // Getters e Setters
    
    public String getNomeEroe() {
        return nomeEroe;
    }
    
    public void setNomeEroe(String nomeEroe) {
        this.nomeEroe = nomeEroe;
    }
    
    public String getIdCompetenza() {
        return idCompetenza;
    }
    
    public void setIdCompetenza(String idCompetenza) {
        this.idCompetenza = idCompetenza;
    }
    
    @Override
    public String toString() {
        return "PlayerSaveData{" +
               "eroe='" + nomeEroe + '\'' +
               (idCompetenza != null ? ", competenza='" + idCompetenza + '\'' : "") +
               '}';
    }
}