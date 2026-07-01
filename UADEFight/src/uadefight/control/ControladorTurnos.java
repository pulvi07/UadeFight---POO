package uadefight.control;

import uadefight.modelo.Entidad;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// se encarga de decidir quien juega y en q orden. basicamente tiene una lista
//  con todos los q pelean, ordenada por velocidad, y un indice q va
// marcando a quien le toca. cuando la cola se termina, la rearma con los vivos.
// no hace falta una clase Turno aparte, con la lista y el indice alcanza
public class ControladorTurnos implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Entidad> colaTurnos;
    private int indiceActual;

    public ControladorTurnos() {
        this.colaTurnos = new ArrayList<>();
        this.indiceActual = -1;
    }

    // arma la cola de la ronda: agarra a todos los q pelean y los ordena de mas
    // rapido a mas lento. asi el de mayor velocidad juega primero
    public void calcularOrdenTurnos(List<Entidad> participantes) {
        colaTurnos = new ArrayList<>(participantes);
        colaTurnos.sort(Comparator.comparingInt(Entidad::getVelocidad).reversed());
        indiceActual = -1;
    }

    // pasa al q sigue y lo devuelve. si el q sigue esta muerto lo saltea (se llama
    // a si mismo de nuevo). cuando llega al final de la cola la rearma con los q
    // siguen vivos para empezar otra ronda. si no quedo nadie vivo devuelve null
    public Entidad siguienteTurno() {
        if (colaTurnos.isEmpty()) return null;

        // Si ya recorrimos la cola, hay que recalcular con los vivos
        if (indiceActual + 1 >= colaTurnos.size()) {
            List<Entidad> vivos = new ArrayList<>();
            for (Entidad e : colaTurnos) if (e.estaViva()) vivos.add(e);
            if (vivos.isEmpty()) return null;
            calcularOrdenTurnos(vivos);
        }

        indiceActual++;
        Entidad actual = colaTurnos.get(indiceActual);
        if (!actual.estaViva()) {
            return siguienteTurno();
        }
        return actual;
    }

    public Entidad getEntidadActual() {
        if (indiceActual < 0 || indiceActual >= colaTurnos.size()) return null;
        return colaTurnos.get(indiceActual);
    }

    // mete a alguien nuevo a la cola sin romper el turno actual (lo agrega al
    // final). en la proxima ronda ya se va a ordenar bien por velocidad
    public void agregarParticipante(Entidad e) {
        if (e == null || colaTurnos.contains(e)) return;
        colaTurnos.add(e);
    }

    public List<Entidad> getColaTurnos() { return Collections.unmodifiableList(colaTurnos); }
}
