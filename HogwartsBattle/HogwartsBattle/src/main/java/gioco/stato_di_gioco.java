package gioco;
import java.util.*;
import java.util.Collections;

enum TipoCarta { INCANTESIMO, ALLEATO, OGGETTO }
public class StatoGioco {
    private int livelloGioco;
    private boolean perdita;
    private boolean vittoria;
    private List<Giocatore> giocatori;
    private int indiceGiocatoreCorrente;
    
    private List<Luogo> luoghi;
    private int indiceLuogoCorrente;
    
    private List<Malvagio> mazzoMalvagi;
    private List<Malvagio> malvagiAttivi;
    
    private List<ArteOscura> mazzoArtiOscure; 
    private List<ArteOscura> scartiArtiOscure;
    
    private List<Carta> mazzoHogwarts; // Negozi
    private List<Carta> mercato; 

    public StatoGioco(int livelloGioco, String[] nomiEroi) {
        this.livelloGioco = livelloGioco;
        this.perdita = false;
        this.vittoria = false;
        this.giocatori = new ArrayList<>();
        for (String nome : nomiEroi) {
            Giocatore g = new Giocatore(nome);
            configuraMazzoIniziale(g); 
            this.giocatori.add(g);
        }
        this.indiceGiocatoreCorrente = 0;
        
        caricaLuoghi();
        caricaMalvagi();
        caricaCarteHogwarts();
        caricaArtiOscure();
        
        this.malvagiAttivi = new ArrayList<>();
        generaMalvagi();
        
        this.mercato = new ArrayList<>();
        rifornisciMercato();
        for(Giocatore g : giocatori) {
            g.pescaCarte(5);
        }
    }
    
    public Giocatore getGiocatoreCorrente() {
        return giocatori.get(indiceGiocatoreCorrente);
    }
    
    public void inizioTurno() {
        if (perdita || vittoria) return;
        
        Giocatore corrente = getGiocatoreCorrente();
        System.out.println("--- Inizio turno: " + corrente.nome + " ---");
        corrente.influenzaCorrente = 0;
        corrente.attaccoCorrente = 0;

        if (corrente.isStordito) {
            System.out.println(corrente.nome + " è stordito e salta l'estrazione Arti Oscure.");
            corrente.isStordito = false;
            corrente.vita -= 1;
            controllaStordimento(corrente);
            return;
        }

        for(int i = 0; i < 1; i++) {
            if(mazzoArtiOscure.isEmpty()) rimescolaArtiOscure();
            if (mazzoArtiOscure.isEmpty()) break; 
            
            ArteOscura arteOscura = mazzoArtiOscure.remove(0);
            scartiArtiOscure.add(arteOscura);
            risolviEffettoArteOscura(arteOscura);
        }
        
        System.out.println("Malvagi attivi:");
        for(Malvagio m : malvagiAttivi) {
            System.out.println("- " + m.nome + " (Vita: " + m.vitaCorrente + ")"); 
        }
    }
    
    public void giocaCarta(int indiceMano) {
        Giocatore g = getGiocatoreCorrente();
        if(indiceMano < 0 || indiceMano >= g.mano.size()) return;
        
        Carta carta = g.mano.remove(indiceMano);
        g.scarti.add(carta);
        
        System.out.println(g.nome + " gioca " + carta.nome);
        g.attaccoCorrente += carta.attaccoBonus;
        g.influenzaCorrente += carta.influenzaBonus; 
        if(carta.tipo == TipoCarta.INCANTESIMO) {
            
        }
        
        System.out.println("Attacco: " + g.attaccoCorrente + ", Influenza: " + g.influenzaCorrente);
    }
    
    public void assegnaDannoAMalvagio(int indiceMalvagio, int quantita) {
        Giocatore g = getGiocatoreCorrente();
        if (indiceMalvagio < 0 || indiceMalvagio >= malvagiAttivi.size()) return;

        if (g.attaccoCorrente < quantita) {
            System.out.println("Attacco insufficiente! Hai solo " + g.attaccoCorrente);
            return;
        }
        
        Malvagio target = malvagiAttivi.get(indiceMalvagio);
        target.vitaCorrente -= quantita;
        g.attaccoCorrente -= quantita;
        
        System.out.println(target.nome + " subisce " + quantita + " danni. Vita rimanente: " + target.vitaCorrente);
        
        if (target.èSconfitto()) {
            gestisciSconfittaMalvagio(target);
        }
    }
    
    public void compraCarta(int indiceMercato) {
        Giocatore g = getGiocatoreCorrente();
        if(indiceMercato < 0 || indiceMercato >= mercato.size()) return;
        
        Carta daComprare = mercato.get(indiceMercato);
        if (g.influenzaCorrente >= daComprare.costo) {
            g.influenzaCorrente -= daComprare.costo;
            g.scarti.add(daComprare);
            mercato.remove(indiceMercato);
            rifornisciMercato();
            System.out.println(g.nome + " ha acquistato: " + daComprare.nome + ". Influenza rimanente: " + g.influenzaCorrente);
        } else {
            System.out.println("Influenza insufficiente. Costo: " + daComprare.costo);
        }
    }
        
    public void fineTurno() {
        Giocatore g = getGiocatoreCorrente();

        g.scarti.addAll(g.mano);
        g.mano.clear();
        g.attaccoCorrente = 0;
        g.influenzaCorrente = 0;
        g.pescaCarte(5);
        Luogo luogoCorrente = luoghi.get(indiceLuogoCorrente);
        if (luogoCorrente.èSopraffatto()) {
            System.out.println("--- LUOGO PERSO: " + luogoCorrente.nome + " ---");
            indiceLuogoCorrente++;
            if (indiceLuogoCorrente >= luoghi.size()) {
                perdita = true;
                System.out.println("GAME OVER - I Villain hanno vinto.");
                return;
            }
        }
        indiceGiocatoreCorrente = (indiceGiocatoreCorrente + 1) % giocatori.size();
        System.out.println("--- Fine Turno. Prossimo Giocatore: " + getGiocatoreCorrente().nome + " ---");
    }

    private void gestisciSconfittaMalvagio(Malvagio m) {
        System.out.println(m.nome + " sconfitto!");
        malvagiAttivi.remove(m);
        
        if (malvagiAttivi.isEmpty() && mazzoMalvagi.isEmpty()) {
            vittoria = true;
            System.out.println("VITTORIA! Hogwarts è salva.");
        } else {
            generaMalvagi();
        }
    }
    
    private void generaMalvagi() {
        int maxAttivi = (livelloGioco >= 3) ? 2 : 1;
        while (malvagiAttivi.size() < maxAttivi && !mazzoMalvagi.isEmpty()) {
            Malvagio nuovoMalvagio = mazzoMalvagi.remove(0);
            malvagiAttivi.add(nuovoMalvagio);
            System.out.println("Generato nuovo Malvagio: " + nuovoMalvagio.nome);
        }
    }
    
    private void rifornisciMercato() {
        while(mercato.size() < 6 && !mazzoHogwarts.isEmpty()) {
            mercato.add(mazzoHogwarts.remove(0));
        } 
    }
    
    private void risolviEffettoArteOscura(ArteOscura arteOscura) {
        System.out.println("Evento Arti Oscure: " + arteOscura.nome);
        
        Giocatore g = getGiocatoreCorrente();
        
        if (arteOscura.nome.equals("Petrificus")) {
            g.vita -= 1; 
            System.out.println(g.nome + " perde 1 vita. Vita: " + g.vita);
            controllaStordimento(g);
        }
        else if (arteOscura.nome.equals("Morsmordre")) {
            aggiungiSegnalinoMarchioOscuro();
        }
    }
    
    private void aggiungiSegnalinoMarchioOscuro() {
        Luogo loc = luoghi.get(indiceLuogoCorrente);
        loc.influenzaOscuraCorrente++;
        System.out.println("Aggiunto segnalino al luogo " + loc.nome + ". Totale: " + loc.influenzaOscuraCorrente + "/" + loc.influenzaOscuraMassima);
    }
    
    private void controllaStordimento(Giocatore g) {
        if (g.vita <= 0 && !g.isStordito) {
            g.isStordito = true;
            g.vita = 0; 
            aggiungiSegnalinoMarchioOscuro();
            System.out.println(g.nome + " è STORDITO! (Vita azzerata, aggiunto Segnalino Oscuro)");
        }
    }

   
    private void configuraMazzoIniziale(Giocatore g) {
        for(int i=0; i<7; i++) g.mazzo.add(new Carta("Alohomora", 0, TipoCarta.INCANTESIMO, 0, 1));
        for(int i=0; i<3; i++) g.mazzo.add(new Carta("Alleato Base", 0, TipoCarta.ALLEATO, 1, 0));
        Collections.shuffle(g.mazzo);
    }
    
    private void caricaLuoghi() {
        luoghi = new ArrayList<>();
        luoghi.add(new Luogo("Diagon Alley", 4));
        luoghi.add(new Luogo("Specchio delle Brame", 5));
    }
    
    private void caricaMalvagi() {
        mazzoMalvagi = new ArrayList<>();
        mazzoMalvagi.add(new Malvagio("Crabbe & Goyle", 5));
        mazzoMalvagi.add(new Malvagio("Draco Malfoy", 6));
        mazzoMalvagi.add(new Malvagio("Basilisco", 8));
        mazzoMalvagi.add(new Malvagio("Lucius Malfoy", 7));
        mazzoMalvagi.add(new Malvagio("Tom Riddle", 6));
        mazzoMalvagi.add(new Malvagio("Dissennatore", 8));
        Collections.shuffle(mazzoMalvagi);
    }
    
    private void caricaCarteHogwarts() {
        mazzoHogwarts = new ArrayList<>();
        for(int i=0; i<20; i++) mazzoHogwarts.add(new Carta("Incendio", 2 + (i%4), TipoCarta.INCANTESIMO, 1, 1));
        Collections.shuffle(mazzoHogwarts);
    }
    
    private void caricaArtiOscure() {
        mazzoArtiOscure = new ArrayList<>();
        scartiArtiOscure = new ArrayList<>();
        mazzoArtiOscure.add(new ArteOscura("Petrificus"));
        mazzoArtiOscure.add(new ArteOscura("Morsmordre"));
        mazzoArtiOscure.add(new ArteOscura("Expulso"));
        Collections.shuffle(mazzoArtiOscure);
    }
    
    private void rimescolaArtiOscure() {
        mazzoArtiOscure.addAll(scartiArtiOscure);
        scartiArtiOscure.clear();
        Collections.shuffle(mazzoArtiOscure);
        System.out.println("Mazzo Arti Oscure rimescolato.");
    }
    
    public static void main(String[] args) { 
        String[] eroi = {"Harry", "Hermione", "Ron", "Neville"};
        StatoGioco gioco = new StatoGioco(1, eroi);
        
        gioco.inizioTurno(); 
        gioco.giocaCarta(0); 
        gioco.compraCarta(0);
        
        if (!gioco.malvagiAttivi.isEmpty()) {
            gioco.getGiocatoreCorrente().attaccoCorrente += 10;
            gioco.assegnaDannoAMalvagio(0, 10);
        }
        
        gioco.fineTurno();   
    }
}