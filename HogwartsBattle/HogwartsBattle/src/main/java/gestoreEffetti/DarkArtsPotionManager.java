package gestoreEffetti;

import java.util.List;
import carte.DarkArtsPotion;
import gioco.Giocatore;
import gioco.StatoDiGioco;

public class DarkArtsPotionManager {
    
    private StatoDiGioco stato;
    
    public DarkArtsPotionManager(StatoDiGioco stato) {
        this.stato = stato;
    }
    
    public void assegnaPozioneAlGiocatore(DarkArtsPotion potion, Giocatore giocatore) {
        giocatore.aggiungiDarkArtsPotion(potion);
    }
    
    public void risolviEffettiOngoing(Giocatore giocatore) {
        List<DarkArtsPotion> potions = giocatore.getDarkArtsPotionsAttive();
        
        if (potions.isEmpty()) {
            return;
        }
        
        for (DarkArtsPotion potion : potions) {
            potion.applicaEffettoOngoing(stato, giocatore);
        }
    }
    
    public void rimuoviTuttePozioni(Giocatore giocatore) {
        giocatore.rimuoviTutteDarkArtsPotions();
    }
    
    public boolean puoBrew(Giocatore giocatore) {
        return true;
    }
    
    public boolean puoGiocareAlleati(Giocatore giocatore) {
        for (DarkArtsPotion potion : giocatore.getDarkArtsPotionsAttive()) {
            if (potion.bloccaAlleati()) {
                return false;
            }
        }
        return true;
    }
}
