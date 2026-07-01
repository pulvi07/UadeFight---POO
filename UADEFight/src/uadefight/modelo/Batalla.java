package uadefight.modelo;

import uadefight.modelo.enemigos.Enemigo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// la Batalla es como el "contexto" de la pelea: guarda quienes pelean (la party
// y los enemigos), en q estado esta (en curso, victoria, etc) y va llevando las
// estadisticas (turnos, daño total, items usados...) para mostrarlas al final.
// no decide nada por su cuenta, eso lo hace el controlador. ella solo guarda datos
public class Batalla implements Serializable {

    private static final long serialVersionUID = 1L;

    private Party party;
    private List<Enemigo> enemigos;
    private EstadoBatalla estado;

    // Estadisticas que se muestran en la pantalla de resumen
    private int turnosJugados;
    private int danioTotalInfligido;
    private int itemsUsados;
    private int habilidadesUsadas;

    // aporte de cada entidad (daño y curacion q hizo) para elegir el MVP. lo llevo
    // por Entidad para no castear al registrar; despues solo consulto el de los
    // personajes de la party
    private Map<Entidad, AporteCombate> aportes;

    public Batalla(Party party, List<Enemigo> enemigos) {
        this.party = party;
        this.enemigos = new ArrayList<>(enemigos);
        this.estado = EstadoBatalla.EN_CURSO;
        this.turnosJugados = 0;
        this.danioTotalInfligido = 0;
        this.itemsUsados = 0;
        this.habilidadesUsadas = 0;
        this.aportes = new HashMap<>();
    }

    public Party getParty() { return party; }
    public List<Enemigo> getEnemigos() { return enemigos; }

    // me devuelve solo los enemigos q todavia estan vivos, asi se a quienes
    // puedo atacar o cuales mostrar en pantalla
    public List<Enemigo> obtenerEnemigosActivos() {
        List<Enemigo> activos = new ArrayList<>();
        for (Enemigo e : enemigos) {
            if (e.estaViva()) activos.add(e);
        }
        return activos;
    }
    public EstadoBatalla getEstado() { return estado; }
    public int getTurnosJugados() { return turnosJugados; }
    public int getDanioTotalInfligido() { return danioTotalInfligido; }
    public int getItemsUsados() { return itemsUsados; }
    public int getHabilidadesUsadas() { return habilidadesUsadas; }

    public void incrementarTurnos() { turnosJugados++; }
    public void sumarDanio(int d) { if (d > 0) danioTotalInfligido += d; }
    public void contarItemUsado() { itemsUsados++; }
    public void contarHabilidadUsada() { habilidadesUsadas++; }

    // si vengo de una partida vieja guardada, el mapa puede venir en null: lo
    // inicializo antes de tocarlo asi no explota
    private Map<Entidad, AporteCombate> aportes() {
        if (aportes == null) aportes = new HashMap<>();
        return aportes;
    }

    // le anoto a quien lo hizo el daño q hizo (ataques y habilidades ofensivas)
    public void registrarDanioDe(Entidad e, int danio) {
        if (e != null) aportes().computeIfAbsent(e, k -> new AporteCombate()).sumarDanio(danio);
    }

    // le anoto a quien la hizo la vida q curo (habilidades de curacion)
    public void registrarCuracionDe(Entidad e, int curacion) {
        if (e != null) aportes().computeIfAbsent(e, k -> new AporteCombate()).sumarCuracion(curacion);
    }

    // el aporte de un personaje (nunca null: si no hizo nada, devuelve uno en cero)
    public AporteCombate getAporte(Personaje p) {
        return aportes().getOrDefault(p, new AporteCombate());
    }

    // el MVP de verdad: el personaje q mas aporto (daño + curacion). si nadie hizo
    // nada, cae al de mayor ataque total como desempate. mira a toda la party (vivos
    // y caidos) porque el aporte vale igual aunque despues lo hayan derrotado
    public Personaje calcularMVP() {
        Personaje mvp = null;
        int mejorPuntaje = -1;
        for (Personaje p : party.getPersonajes()) {
            int puntaje = getAporte(p).puntaje();
            if (puntaje > mejorPuntaje
                    || (puntaje == mejorPuntaje && mvp != null && p.getAtaqueTotal() > mvp.getAtaqueTotal())) {
                mejorPuntaje = puntaje;
                mvp = p;
            }
        }
        return mvp;
    }

    public void pausar() { if (estado == EstadoBatalla.EN_CURSO) estado = EstadoBatalla.PAUSADA; }
    public void reanudar() { if (estado == EstadoBatalla.PAUSADA) estado = EstadoBatalla.EN_CURSO; }

    // se fija como va la pelea y actualiza el estado. si murieron todos los
    // enemigos ganamos, si murio toda la party perdimos, y sino seguimos jugando.
    // lo llamo antes de cada turno para saber si hay q cortar
    public EstadoBatalla verificarFin() {
        boolean enemigosVivos = false;
        for (Enemigo e : enemigos) {
            if (e.estaViva()) { enemigosVivos = true; break; }
        }
        if (!enemigosVivos) {
            estado = EstadoBatalla.VICTORIA;
        } else if (!party.estaViva()) {
            estado = EstadoBatalla.DERROTA;
        } else if (estado != EstadoBatalla.PAUSADA) {
            estado = EstadoBatalla.EN_CURSO;
        }
        return estado;
    }

    // junta a todos los q estan en la pelea (party + enemigos), vivos o muertos.
    // lo usa el controlador de turnos para armar la lista de quien juega
    public List<Entidad> obtenerParticipantes() {
        List<Entidad> participantes = new ArrayList<>();
        participantes.addAll(party.getPersonajes());
        participantes.addAll(enemigos);
        return participantes;
    }

    // lo mismo q arriba pero filtrando los q ya estan muertos
    public List<Entidad> obtenerParticipantesVivos() {
        List<Entidad> vivos = new ArrayList<>();
        for (Entidad e : obtenerParticipantes()) {
            if (e.estaViva()) vivos.add(e);
        }
        return vivos;
    }

    // suma toda la xp q dan los enemigos, q despues se reparte entre la party al ganar
    public int calcularXpRecompensa() {
        int total = 0;
        for (Enemigo e : enemigos) total += e.getXpQueOtorga();
        return total;
    }
}
