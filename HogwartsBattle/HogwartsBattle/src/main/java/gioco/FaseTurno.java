package gioco;

/**
 * Enum che rappresenta le fasi del turno.
 * 
 * Pack 2+ aggiunge RACCOLTA_INGREDIENTI dopo GIOCA_CARTE.
 */
public enum FaseTurno {
    ARTI_OSCURE, 
    MALVAGI, 
    HORCRUX, 
    GIOCA_CARTE, 
    RACCOLTA_INGREDIENTI,  // ⭐ NUOVA - Pack 2+
    ATTACCA, 
    ACQUISTA_CARTE, 
    FINE_TURNO
}
