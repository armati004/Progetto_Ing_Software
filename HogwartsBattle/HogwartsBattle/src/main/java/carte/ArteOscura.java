package carte;

import java.util.List;
import gestoreEffetti.Effetto;
import gestoreEffetti.Trigger;

public class ArteOscura extends Carta {

    public CartaArteOscure(
        String nome,
        String id,
        String classe,
        String descrizione,
        int costo,
        String pathImmagine,
        List<Effetto> effetti,
        List<Trigger> triggers
    ) {
        super(nome, id, classe, descrizione, costo, pathImmagine, effetti, triggers);
    }
}
