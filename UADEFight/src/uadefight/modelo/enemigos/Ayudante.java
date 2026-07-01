package uadefight.modelo.enemigos;

// el Ayudante es mas debilucho pero mas rapido.
// su especial pega un poco mas (x1.4).
// en el codigo "Javier Valda" es un objeto de esta clase
public class Ayudante extends Enemigo {

    private static final long serialVersionUID = 1L;

    public Ayudante(String nombre, int vidaMaxima, int ataque, int defensa,
                    int velocidad, int xpQueOtorga) {
        super(nombre, vidaMaxima, ataque, defensa, velocidad, xpQueOtorga, /*especial%*/ 15);
    }

    // nombre del especial del ayudante q se muestra en el log
    @Override
    public String getNombreEspecial() { return "Cachar copiandose"; }

    // pega 1.4 veces lo normal, bastante menos q el profe
    @Override
    public double getMultiplicadorEspecial() { return 1.4; }
}
