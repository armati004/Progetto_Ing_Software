package carte;

/**
 * Rappresenta un token fisico di ingrediente sulla Potion Board.
 * Gli ingredienti vengono raccolti dagli scaffali e aggiunti alle pozioni.
 * 
 * Sistema Pozioni - Pack 2+
 */
public class Ingrediente {
    private TipoIngrediente tipo;
    private boolean usato; // True se già aggiunto a una pozione
    
    /**
     * Costruttore per creare un ingrediente.
     * 
     * @param tipo Tipo di ingrediente (BICORN_HORN, MANDRAKE_LEAF, etc.)
     */
    public Ingrediente(TipoIngrediente tipo) {
        this.tipo = tipo;
        this.usato = false;
    }
    
    /**
     * Verifica se questo ingrediente può soddisfare un requisito.
     * WILD può sostituire qualsiasi ingrediente.
     * 
     * @param richiesto Tipo di ingrediente richiesto
     * @return true se questo ingrediente soddisfa il requisito
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
