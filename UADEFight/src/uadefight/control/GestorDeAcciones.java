package uadefight.control;

import uadefight.modelo.Batalla;
import uadefight.modelo.Entidad;
import uadefight.modelo.Inventario;
import uadefight.modelo.Personaje;
import uadefight.modelo.enemigos.DecisionEnemigo;
import uadefight.modelo.enemigos.Enemigo;
import uadefight.modelo.habilidades.Habilidad;
import uadefight.modelo.habilidades.ResultadoHabilidad;
import uadefight.modelo.items.Item;

import java.io.Serializable;
import java.util.List;

// se encarga de resolver las acciones del combate: ataque normal,
// defensa, habilidades, items y el turno de los enemigos. valida cada accion,
// aplica su efecto sobre la batalla y va escribiendo en la bitacora. antes todo
// esto estaba dentro del ControladorBatalla; se separo para q cada clase tenga
// una sola responsabilidad como pidio el porfe
public class GestorDeAcciones implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Batalla batalla;
    private final BitacoraCombate bitacora;

    public GestorDeAcciones(Batalla batalla, BitacoraCombate bitacora) {
        this.batalla = batalla;
        this.bitacora = bitacora;
    }

    // ataque normal: le pido el daño al atacante, se lo aplico al objetivo y
    // devuelvo cuanto le saque de verdad. si el q pega es un personaje, sumo ese
    // daño a las estadisticas de la batalla. tambien escribo en la bitacora
    public int ejecutarAtaque(Entidad atacante, Entidad objetivo) {
        if (atacante == null || objetivo == null) return 0;
        if (!atacante.estaViva() || !objetivo.estaViva()) return 0;
        int danioBase = atacante.calcularDanioAtaque();
        int efectivo = objetivo.recibirDanio(danioBase);
        if (atacante.esPersonaje()) {
            batalla.sumarDanio(efectivo);
            batalla.registrarDanioDe(atacante, efectivo);
        }
        bitacora.registrar("> " + atacante.getNombre() + " ataca a " + objetivo.getNombre()
                + " (-" + efectivo + " HP)");
        if (!objetivo.estaViva()) {
            bitacora.registrar("  " + objetivo.getNombre() + " fue derrotado!");
        }
        return efectivo;
    }

    // el personaje se pone en guardia: el proximo golpe q reciba le va a pegar
    // la mitad. dura solo hasta su proximo turno
    public void ejecutarDefensa(Entidad entidad) {
        if (entidad == null || !entidad.estaViva()) return;
        entidad.activarDefensa();
        bitacora.registrar("> " + entidad.getNombre() + " se pone en defensa");
    }

    // usa una habilidad. valido todo (q exista, q tenga mana, q el objetivo sea
    // valido para esa habilidad) y le descuento el mana. el efecto en si lo aplica
    // la propia habilidad: yo solo uso lo q me devuelve para el log y las stats,
    // sin preguntar de q tipo era. devuelve true si se pudo usar
    public boolean ejecutarHabilidad(Personaje usuario, Habilidad habilidad, Entidad objetivo) {
        if (usuario == null || habilidad == null || objetivo == null) return false;
        if (!usuario.estaViva()) return false;
        if (!habilidad.puedeUsarsePor(usuario)) {
            bitacora.registrar("> " + usuario.getNombre() + " no tiene mana para " + habilidad.getNombre());
            return false;
        }
        if (!habilidad.objetivoValido(objetivo)) {
            return false;
        }
        usuario.consumirMana(habilidad.getCostoMana());
        ResultadoHabilidad resultado = habilidad.aplicar(usuario, objetivo);
        if (resultado.getDanioInfligido() > 0) {
            batalla.sumarDanio(resultado.getDanioInfligido());
            batalla.registrarDanioDe(usuario, resultado.getDanioInfligido());
        }
        if (resultado.getCuracionRealizada() > 0) {
            batalla.registrarCuracionDe(usuario, resultado.getCuracionRealizada());
        }
        bitacora.registrar(resultado.getMensaje());
        if (!objetivo.estaViva()) {
            bitacora.registrar("  " + objetivo.getNombre() + " fue derrotado!");
        }
        batalla.contarHabilidadUsada();
        return true;
    }

    // usa un item del inventario. chequeo q el item este en el inventario y q se
    // pueda usar ahi, lo aplico, y lo borro
    public boolean ejecutarUsoItem(Personaje usuario, Item item, Entidad objetivo) {
        if (usuario == null || item == null || objetivo == null) return false;
        if (!usuario.estaViva()) return false;
        Inventario inv = batalla.getParty().getInventario();
        if (!inv.contieneItem(item)) {
            bitacora.registrar("> El item " + item.getNombre() + " no esta en el inventario");
            return false;
        }
        if (!item.puedeUsarseSobre(objetivo)) {
            bitacora.registrar("> " + item.getNombre() + " no se puede usar ahi");
            return false;
        }
        item.aplicarEfecto(objetivo);
        bitacora.registrar("> " + usuario.getNombre() + " usa " + item.getNombre()
                + " sobre " + objetivo.getNombre());
        // saco el item del inventario una vez usado: la pocion xq se gasta, y el
        // equipable xq ya quedo puesto y no tiene sentido volver a equiparlo.
        inv.eliminarItem(item);
        batalla.contarItemUsado();
        return true;
    }

    // resuelve el turno de un enemigo. le pregunto al enemigo q quiere hacer
    // (decidirAccion) y aplico el golpe al personaje q eligio. si decidio usar el
    // especial, le multiplico el daño. el daño base lo saco segun el nivel de la
    // party y la ventaja por ser menos enemigos
    public void ejecutarTurnoEnemigo(Enemigo enemigo) {
        if (enemigo == null || !enemigo.estaViva()) return;
        List<Personaje> vivos = batalla.getParty().getPersonajesVivos();
        DecisionEnemigo dec = enemigo.decidirAccion(vivos);
        if (dec == null) return;
        Personaje objetivo = dec.getObjetivo();
        int danioBase = enemigo.calcularDanioContraParty(nivelPromedioParty(), ventajaPorCantidad());
        if (dec.isEspecial()) {
            danioBase = (int) Math.round(danioBase * enemigo.getMultiplicadorEspecial());
            bitacora.registrar("> " + enemigo.getNombre() + " lanza un " + enemigo.getNombreEspecial() + "!");
        }
        int efectivo = objetivo.recibirDanio(danioBase);
        bitacora.registrar("  " + objetivo.getNombre() + " pierde " + efectivo + " HP");
        if (!objetivo.estaViva()) {
            bitacora.registrar("  " + objetivo.getNombre() + " fue derrotado!");
        }
    }

    // saco el nivel promedio de la party (sumo todos los niveles y divido).
    // con esto los enemigos pegan mas fuerte si la party esta mas alta de nivel
    private int nivelPromedioParty() {
        List<Personaje> ps = batalla.getParty().getPersonajes();
        if (ps.isEmpty()) return 1;
        int suma = 0;
        for (Personaje p : ps) suma += p.getNivel();
        return Math.max(1, Math.round(suma / (float) ps.size()));
    }

    // como los enemigos son menos q nosotros, les doy una ventaja de daño:
    // divido cuantos personajes vivos hay por cuantos enemigos quedan. si somos
    // 4 contra 2, pegan x2. nunca baja de 1.2 asi siempre pegan mas q un personaje
    private double ventajaPorCantidad() {
        int party = batalla.getParty().getPersonajesVivos().size();
        int enemigos = batalla.obtenerEnemigosActivos().size();
        if (enemigos <= 0) return 1.2;
        return Math.max(1.2, party / (double) enemigos);
    }
}
