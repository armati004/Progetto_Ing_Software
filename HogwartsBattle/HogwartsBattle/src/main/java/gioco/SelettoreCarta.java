package gioco;
import java.util.List;
import carte.Carta;

public interface SelettoreCarta {
    Carta selezionaCarta(List<Carta> mazzo);
}
