package carte;

/**
 * Enum che definisce i vari tipi di condizioni per completare un Encounter.
 */
public enum TipoCondizioneEncounter {
    /**
     * Giocare un certo numero di carte di un tipo specifico in un turno
     * Es: "Play 4 Items in one turn"
     */
    GIOCA_CARTE_TIPO,
    
    /**
     * Acquisire una certa quantità di influenza in un turno
     * Es: "Acquire 8 influence in one turn"
     */
    ACQUISTA_INFLUENZA,
    
    /**
     * Giocare carte con un valore specifico
     * Es: "Play 3 cards with a value of 4 or more in one turn"
     */
    GIOCA_CARTE_VALORE,
    
    /**
     * Acquisire una carta con un valore minimo
     * Es: "Acquire a card with a value of 6 or more"
     */
    ACQUISTA_CARTA_VALORE,
    
    /**
     * Assegnare un certo numero di segnalini attacco a un malvagio/encounter
     * Es: "Assign 6 attack to this card in one turn"
     */
    ASSEGNA_ATTACCO,
    
    /**
     * Risolvere un certo numero di eventi Arti Oscure in un turno
     * Es: "Resolve 5 Dark Arts events in one turn"
     */
    RISOLVI_EVENTI_ARTI_OSCURE,
    
    /**
     * Bannare pozioni
     * Es: "Banish 2 potions in one turn"
     */
    BANISH_POZIONI,
    
    /**
     * Acquisire un certo numero di carte in un turno
     * Es: "Acquire 3 cards in one turn"
     */
    ACQUISTA_CARTE,
    
    /**
     * Giocare carte con valori dispari
     * Es: "Play 3 cards with an odd value"
     */
    GIOCA_CARTE_DISPARI,
    
    /**
     * Giocare carte con valori pari
     * Es: "Play 3 cards with an even value"
     */
    GIOCA_CARTE_PARI,
    
    /**
     * Rimuovere segnalini controllo dalla locazione
     * Es: "Remove 2 control tokens in one turn"
     */
    RIMUOVI_CONTROLLO,
    
    /**
     * Essere storditi un certo numero di volte
     * Es: "Heroes must be stunned 3 times"
     */
    ESSERE_STORDITI,
    
    /**
     * Lanciare il dado con un risultato specifico
     * Es: "Roll flame, cauldron, or lightning"
     */
    LANCIA_DADO,
    
    /**
     * Condizione custom da verificare manualmente
     */
    CUSTOM
}
