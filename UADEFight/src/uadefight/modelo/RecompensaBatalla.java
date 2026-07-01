package uadefight.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// el resultado del reparto de recompensa al ganar: cuanta xp le toco a cada uno y
// el detalle por personaje. es el resultado del JUEGO, sin texto de interfaz: la
// vista decide como mostrarlo. antes esto era una List<String> ya formateada, q
// mezclaba el resultado con el texto de pantalla
public class RecompensaBatalla implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int xpPorPersonaje;
    private final List<RecompensaPersonaje> detalles;

    public RecompensaBatalla(int xpPorPersonaje, List<RecompensaPersonaje> detalles) {
        this.xpPorPersonaje = xpPorPersonaje;
        this.detalles = new ArrayList<>(detalles);
    }

    // para cuando no hubo reparto (perdiste, o no quedo nadie vivo para repartir)
    public static RecompensaBatalla vacia() {
        return new RecompensaBatalla(0, new ArrayList<>());
    }

    public int getXpPorPersonaje() { return xpPorPersonaje; }

    public List<RecompensaPersonaje> getDetalles() {
        return Collections.unmodifiableList(detalles);
    }

    // ¿se repartio algo? (lo usa la vista en vez del viejo lista.isEmpty())
    public boolean huboReparto() { return !detalles.isEmpty(); }
}
