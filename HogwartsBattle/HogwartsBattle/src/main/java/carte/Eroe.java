package carte;

import java.util.List;
import gestoreEffetti.Trigger;
public class Eroe {

    private String nome;
    private String id;

    // "class" è una parola riservata in Java → uso un altro nome e @JsonProperty
    @com.fasterxml.jackson.annotation.JsonProperty("class")
    private String classe;

    private String descrizione;

    @com.fasterxml.jackson.annotation.JsonProperty("path-img")
    private String pathImg;

    private List<Trigger> triggers;

    // Getter e Setter
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getClasse() { return classe; }
    public void setClasse(String classe) { this.classe = classe; }

    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }

    public String getPathImg() { return pathImg; }
    public void setPathImg(String pathImg) { this.pathImg = pathImg; }

    public List<Trigger> getTriggers() { return triggers; }
    public void setTriggers(List<Trigger> triggers) { this.triggers = triggers; }
}



