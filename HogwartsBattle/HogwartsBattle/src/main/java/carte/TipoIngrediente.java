package carte;

/**
 * Enum che rappresenta i vari tipi di ingredienti per le Pozioni.
 * Ogni ingrediente è un token fisico sulla Potion Board.
 */
public enum TipoIngrediente {
    /**
     * Corno di Bicorno
     */
    BICORN_HORN("Bicorn Horn", "🦄"),
    
    /**
     * Foglia di Mandragola
     */
    MANDRAKE_LEAF("Mandrake Leaf", "🌿"),
    
    /**
     * Verme Flobber
     */
    FLOBBER_WORM("Flobber Worm", "🪱"),
    
    /**
     * Elleboro
     */
    HELLEBORE("Hellebore", "🌺"),
    
    /**
     * Mosca Lacewing
     */
    LACEWING_FLY("Lacewing Fly", "🦟"),
    
    /**
     * Wild - Ingrediente jolly che può sostituire qualsiasi altro ingrediente
     */
    WILD("Wild", "⭐");
    
    private final String nome;
    private final String simbolo;
    
    TipoIngrediente(String nome, String simbolo) {
        this.nome = nome;
        this.simbolo = simbolo;
    }
    
    public String getNome() {
        return nome;
    }
    
    public String getSimbolo() {
        return simbolo;
    }
    
    @Override
    public String toString() {
        return simbolo + " " + nome;
    }

	public static void main(String[] args) {
		HeroFactory.creaEroe("Hermione Granger", 6)
	}
}
