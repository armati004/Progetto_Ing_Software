package data;

import java.util.List;

public class PlayerSaveData {
    
    private String nomeEroe;
    private String idCompetenza;
    
    private List<String> carteNelMazzo;
    
    public PlayerSaveData() {
    }
    
    public PlayerSaveData(String nomeEroe, String idCompetenza, List<String> carteNelMazzo) {
        this.nomeEroe = nomeEroe;
        this.idCompetenza = idCompetenza;
        this.carteNelMazzo = carteNelMazzo;
    }
    
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
    
    public List<String> getCarteNelMazzo() {
        return carteNelMazzo;
    }
    
    public void setCarteNelMazzo(List<String> carteNelMazzo) {
        this.carteNelMazzo = carteNelMazzo;
    }
    
    @Override
    public String toString() {
        return "PlayerSaveData{" +
               "eroe='" + nomeEroe + '\'' +
               (idCompetenza != null ? ", competenza='" + idCompetenza + '\'' : "") +
               ", carte=" + (carteNelMazzo != null ? carteNelMazzo.size() : 0) +
               '}';
    }
}