package gestoreEffetti;

/**
 * Enum che rappresenta la durata di un effetto nel gioco.
 */
public enum DurataEffetto {
    /**
     * Effetto istantaneo - Si applica immediatamente e termina
     */
    ISTANTANEO,
    
    /**
     * Effetto temporaneo - Dura fino alla fine del turno
     */
    TEMPORANEO,
    
    /**
     * Effetto continuo - Rimane attivo fino a quando la carta è in gioco
     */
    CONTINUO
}
