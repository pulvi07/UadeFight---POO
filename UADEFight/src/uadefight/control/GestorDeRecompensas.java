package uadefight.control;

import uadefight.modelo.Batalla;
import uadefight.modelo.EstadoBatalla;
import uadefight.modelo.Personaje;
import uadefight.modelo.RecompensaBatalla;
import uadefight.modelo.RecompensaPersonaje;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// se ocupa de todo lo q pasa dsp de ganar: repartir la experiencia entre los
// personajes vivos y elegir el MVP. antes esto tambien estaba dentro del
// ControladorBatalla; se separo para q el reparto de premios sea su propia
// responsabilidad
public class GestorDeRecompensas implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Batalla batalla;

    public GestorDeRecompensas(Batalla batalla) {
        this.batalla = batalla;
    }

    // al ganar, reparte la xp entre los personajes q quedaron vivos (en partes
    // iguales). devuelve un objeto de resultado (cuanto gano cada uno y cuantos
    // niveles subio) sin texto de pantalla: la vista decide como mostrarlo
    public RecompensaBatalla repartirRecompensa() {
        if (batalla.getEstado() != EstadoBatalla.VICTORIA) return RecompensaBatalla.vacia();
        List<Personaje> vivos = batalla.getParty().getPersonajesVivos();
        if (vivos.isEmpty()) return RecompensaBatalla.vacia();
        int porCabeza = batalla.calcularXpRecompensa() / vivos.size();
        List<RecompensaPersonaje> detalles = new ArrayList<>();
        for (Personaje p : vivos) {
            int niveles = p.ganarExperiencia(porCabeza);
            detalles.add(new RecompensaPersonaje(p, porCabeza, niveles));
        }
        return new RecompensaBatalla(porCabeza, detalles);
    }

    // el MVP para la pantalla final. la eleccion la hace la Batalla segun el aporte
    // real de cada uno (daño + curacion), no solo el ataque
    public Personaje calcularMVP() {
        return batalla.calcularMVP();
    }
}
