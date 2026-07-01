package uadefight.control;

import uadefight.modelo.Batalla;
import uadefight.modelo.Entidad;
import uadefight.modelo.Personaje;
import uadefight.modelo.enemigos.Enemigo;
import uadefight.modelo.RecompensaBatalla;
import uadefight.modelo.habilidades.Habilidad;
import uadefight.modelo.items.Item;

import java.io.Serializable;
import java.util.List;

// coordinador de la pelea. ya no hace todo el mismo: delega en clases
// especializadas (GestorDeAcciones para resolver las acciones, GestorDeRecompensas
// para el reparto de xp y el MVP, BitacoraCombate para el log). su unico trabajo
// propio es llevar el orden de los turnos y dlre a la pantalla un unico punto
// de entrada, repartiendo cada pedido al gestor q corresponde.
public class ControladorBatalla implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Batalla batalla;
    private final ControladorTurnos controladorTurnos;
    private final BitacoraCombate bitacora;
    private final GestorDeAcciones acciones;
    private final GestorDeRecompensas recompensas;

    public ControladorBatalla(Batalla batalla) {
        this.batalla = batalla;
        this.controladorTurnos = new ControladorTurnos();
        this.bitacora = new BitacoraCombate();
        this.acciones = new GestorDeAcciones(batalla, bitacora);
        this.recompensas = new GestorDeRecompensas(batalla);
        this.controladorTurnos.calcularOrdenTurnos(batalla.obtenerParticipantesVivos());
    }

    public Batalla getBatalla() { return batalla; }
    public ControladorTurnos getControladorTurnos() { return controladorTurnos; }
    public List<String> getLog() { return bitacora.getLineas(); }

    // le pide al controlador de turnos a quien le toca ahora. cuando arranca el
    // turno de alguien, le saco la defensa q haya puesto antes (dura un solo turno)
    // y sumo 1 al contador de turnos jugados
    public Entidad avanzarAlSiguienteTurno() {
        Entidad e = controladorTurnos.siguienteTurno();
        if (e != null) {
            e.desactivarDefensa();
            batalla.incrementarTurnos();
        }
        return e;
    }


    public int ejecutarAtaque(Entidad atacante, Entidad objetivo) {
        return acciones.ejecutarAtaque(atacante, objetivo);
    }

    public void ejecutarDefensa(Entidad entidad) {
        acciones.ejecutarDefensa(entidad);
    }

    public boolean ejecutarHabilidad(Personaje usuario, Habilidad habilidad, Entidad objetivo) {
        return acciones.ejecutarHabilidad(usuario, habilidad, objetivo);
    }

    public boolean ejecutarUsoItem(Personaje usuario, Item item, Entidad objetivo) {
        return acciones.ejecutarUsoItem(usuario, item, objetivo);
    }

    public void ejecutarTurnoEnemigo(Enemigo enemigo) {
        acciones.ejecutarTurnoEnemigo(enemigo);
    }


    public RecompensaBatalla repartirRecompensa() {
        return recompensas.repartirRecompensa();
    }

    public Personaje calcularMVP() {
        return recompensas.calcularMVP();
    }
}
