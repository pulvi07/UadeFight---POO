package uadefight.modelo.enemigos;

import uadefight.modelo.Personaje;

import java.io.Serializable;

// esto es como una "cajita" q devuelve el enemigo cuando decide q hacer.
// guarda a quien le va a pegar y si va a usar el especial o no.
// asi el enemigo solo decide y despues el controlador se encarga de aplicarlo
public class DecisionEnemigo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Personaje objetivo;
    private boolean especial;

    public DecisionEnemigo(Personaje objetivo, boolean especial) {
        this.objetivo = objetivo;
        this.especial = especial;
    }

    public Personaje getObjetivo() { return objetivo; }
    public boolean isEspecial() { return especial; }
}
