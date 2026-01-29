package carte;

/**
 * Rappresenta un token fisico di ingrediente sulla Potion Board.
 * Gli ingredienti vengono raccolti dagli scaffali e aggiunti alle pozioni.
 */
public class Ingrediente {
    private TipoIngrediente tipo;
    private boolean usato; // True se già aggiunto a una pozione
    
    public Ingrediente(TipoIngrediente tipo) {
        this.tipo = tipo;
        this.usato = false;
    }
    
    /**
     * Verifica se questo ingrediente può soddisfare un requisito.
     * WILD può sostituire qualsiasi ingrediente.
     */
    public boolean soddisfaRequisito(TipoIngrediente richiesto) {
        if (usato) {
            return false;
        }
        return this.tipo == richiesto || this.tipo == TipoIngrediente.WILD;
    }
    
    public TipoIngrediente getTipo() {
        return tipo;
    }
    
    public void setTipo(TipoIngrediente tipo) {
        this.tipo = tipo;
    }
    
    public boolean isUsato() {
        return usato;
    }
    
    public void setUsato(boolean usato) {
        this.usato = usato;
    }
    
    @Override
    public String toString() {
        return tipo.toString() + (usato ? " (usato)" : "");
    }
}
