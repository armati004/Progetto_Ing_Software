package gioco;

/**
 * Enum che rappresenta le fasi del turno di gioco.
 * 
 * NOVITÀ Pack 2+: Aggiunta fase RACCOLTA_INGREDIENTI dopo GIOCA_CARTE.
 */
public enum FaseTurno {
    /**
     * Fase Arti Oscure - Si pesca e risolve una carta Arti Oscure
     */
    ARTI_OSCURE,
    
    /**
     * Fase Malvagi - Si applicano gli effetti dei malvagi attivi
     */
    MALVAGI,
    
    /**
     * Fase Horcrux (Anno 7) - Si applicano gli effetti degli Horcrux attivi
     */
    HORCRUX,
    
    /**
     * Fase Gioca Carte - Il giocatore può giocare carte dalla mano
     */
    GIOCA_CARTE,
    
    /**
     * Fase Raccolta Ingredienti (Pack 2+) - Il giocatore raccoglie ingredienti dagli scaffali
     */
    RACCOLTA_INGREDIENTI,
    
    /**
     * Fase Attacca - Il giocatore assegna i segnalini attacco ai malvagi
     */
    ATTACCA,
    
    /**
     * Fase Acquista Carte - Il giocatore può acquistare carte dal mercato
     */
    ACQUISTA_CARTE,
    
    /**
     * Fase Fine Turno - Si scartano carte, si ripristinano risorse e si pesca
     */
    FINE_TURNO
}
