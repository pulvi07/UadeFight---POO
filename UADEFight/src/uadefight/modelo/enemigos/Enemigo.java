package uadefight.modelo.enemigos;

import uadefight.modelo.Entidad;
import uadefight.modelo.Personaje;

import java.util.List;
import java.util.Random;

public class Enemigo extends Entidad {

    private static final long serialVersionUID = 1L;
    private static final Random RNG = new Random();

    private int xpQueOtorga;
    private int probabilidadEspecial; // 0..100

    public Enemigo(String nombre, int vidaMaxima, int ataque, int defensa,
                   int velocidad, int xpQueOtorga, int probabilidadEspecial) {
        super(nombre, vidaMaxima, ataque, defensa, velocidad);
        this.xpQueOtorga = xpQueOtorga;
        this.probabilidadEspecial = probabilidadEspecial;
    }

    public int getXpQueOtorga() { return xpQueOtorga; }
    public int getProbabilidadEspecial() { return probabilidadEspecial; }

    // aca el enemigo "piensa" q va a hacer en su turno: elige a quien pegarle y
    // tira un random para ver si usa el ataque especial o uno normal. El
    // enemigo NO pega, solo decide. el q aplica el golpe es el controlador.
    public DecisionEnemigo decidirAccion(List<Personaje> objetivosVivos) {
        if (objetivosVivos == null || objetivosVivos.isEmpty()) return null;
        Personaje objetivo = elegirObjetivo(objetivosVivos);
        boolean especial = RNG.nextInt(100) < probabilidadEspecial;
        return new DecisionEnemigo(objetivo, especial);
    }

    // por defecto le pega a cualquiera de la party al azar. cambie lo de q le pegue 
    // al que menos vida tiene, el profe dijo q era mejor asi
    protected Personaje elegirObjetivo(List<Personaje> vivos) {
        return vivos.get(RNG.nextInt(vivos.size()));
    }

    // este es el daño q pega el enemigo. lo hago depender del nivel PROMEDIO de
    // la party asi la pelea no se hace facil cuando suben de nivel, y aparte
    // le sumo una "ventaja" xq los enemigos son menos q nosotros, entonces
    // pegan mas fuerte para compensar. el factor random es para q varie un toque
    public int calcularDanioContraParty(int nivelPromedioParty, double ventaja) {
        double factor = 0.85 + RNG.nextDouble() * 0.30;
        double porNivel = 1 + (Math.max(1, nivelPromedioParty) - 1) * 0.12;
        return Math.max(1, (int) Math.round(getAtaqueTotal() * factor * porNivel * ventaja));
    }

    // cuanto multiplica el daño cuando el enemigo tira el especial.
    // las subclases lo pisan para q cada uno pegue distinto
    public double getMultiplicadorEspecial() { return 1.6; }

    // nombre del ataque especial q se muestra en el log. medio generico aca,
    // cada enemigo le pone el suyo
    public String getNombreEspecial() { return "Ataque Sorpresa"; }
}
