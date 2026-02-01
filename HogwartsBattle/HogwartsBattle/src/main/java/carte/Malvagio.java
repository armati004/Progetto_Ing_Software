package carte;
import java.util.List;

import gioco.Giocatore;
import gioco.StatoDiGioco;
import gestoreEffetti.Effetto;
import gestoreEffetti.EsecutoreEffetti;
import gestoreEffetti.Trigger;

public class Malvagio extends Carta{
	
	private final int vita;
	private int danno;
    private List<Effetto> reward;
    private Boolean bloccoAbilita;
    private Giocatore giocatoreBloccante;
    private Boolean attaccoassegnato;
    
	
    public Malvagio(String nome, String id, String classe, String descrizione, int costo, String pathImmagine,
			List<Effetto> effetti, List<Trigger> triggers, List<Effetto> reward, int vita) {
		super(nome, id, classe, descrizione, costo, pathImmagine, effetti, triggers);
		this.reward = reward;
		this.vita = vita;
		this.danno = 0;
		this.setBloccoAbilita(false);
		this.giocatoreBloccante = null;
		this.setAttaccoassegnato(false);
	}

    public void defeat(StatoDiGioco stato, Giocatore g) {
        System.out.println("Sconfitto " + this.getNome());
        for(Effetto e : this.getReward()) {
        	EsecutoreEffetti.eseguiEffetto(e, stato, g, this);
        }
    }
    
    public int getDanno() {
		return danno;
	}

	public void setDanno(int danno) {
		this.danno = danno;
	}

	public int getVita() {
		return vita;
	}

	public List<Effetto> getReward() {
		return reward;
	}

	public boolean isSconfitto() {
		if(this.getDanno() >= this.getVita()) {
			return true;
		}
		else {
			return false;
		}
	}

	public Boolean getBloccoAbilita() {
		return bloccoAbilita;
	}

	public void setBloccoAbilita(Boolean bloccoAbilita) {
		this.bloccoAbilita = bloccoAbilita;
	}

	public Giocatore getGiocatoreBloccante() {
		return giocatoreBloccante;
	}

	public void setGiocatoreBloccante(Giocatore giocatoreBloccante) {
		this.giocatoreBloccante = giocatoreBloccante;
	}

	public Boolean getAttaccoassegnato() {
		return attaccoassegnato;
	}

	public void setAttaccoassegnato(Boolean attaccoassegnato) {
		this.attaccoassegnato = attaccoassegnato;
	}

}