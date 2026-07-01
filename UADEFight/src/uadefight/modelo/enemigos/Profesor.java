package uadefight.modelo.enemigos;

// el Profesor es el enemigo "jefe" de la pelea, el q mas pega.
// cosas propias: su especial se llama "Parcial Sorpresa" y pega bastante mas
// fuerte (x1.8). en el codigo "Godio" es un objeto de esta clase
public class Profesor extends Enemigo {

    private static final long serialVersionUID = 1L;

    public Profesor(String nombre, int vidaMaxima, int ataque, int defensa,
                    int velocidad, int xpQueOtorga) {
        super(nombre, vidaMaxima, ataque, defensa, velocidad, xpQueOtorga, /*especial%*/ 25);
    }

    // nombre q sale en el log cuando el profe tira el especial
    @Override
    public String getNombreEspecial() { return "Parcial Sorpresa"; }

    // el parcial sorpresa pega 1.8 veces lo normal, por eso asusta
    @Override
    public double getMultiplicadorEspecial() { return 1.8; }
}
