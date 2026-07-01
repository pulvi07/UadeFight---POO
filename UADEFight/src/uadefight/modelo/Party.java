package uadefight.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// la Party es el grupo de personajes q arma el jugador para pelear.
// adentro tiene la lista de personajes (entre 2 y 4) y el inventario q comparten
// todos. tiene metodos para agregar/sacar gente y para saber si queda alguien vivo
public class Party implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final int TAMANIO_MAXIMO = 4;
    private static final int TAMANIO_MINIMO = 2;

    private List<Personaje> personajes;
    private Inventario inventario;

    public Party() {
        this.personajes = new ArrayList<>();
        this.inventario = new Inventario();
    }

    // agrega un personaje si hay lugar (max 4) y si no estaba ya.
    // devuelve false si no se pudo (lleno, null o repetido)
    public boolean agregarPersonaje(Personaje p) {
        if (p == null || personajes.size() >= TAMANIO_MAXIMO) return false;
        if (personajes.contains(p)) return false;
        personajes.add(p);
        return true;
    }

    public boolean eliminarPersonaje(Personaje p) {
        return personajes.remove(p);
    }

    public List<Personaje> getPersonajes() {
        return Collections.unmodifiableList(personajes);
    }

    public List<Personaje> getPersonajesVivos() {
        List<Personaje> vivos = new ArrayList<>();
        for (Personaje p : personajes) {
            if (p.estaViva()) vivos.add(p);
        }
        return vivos;
    }

    // la party sigue "viva" mientras quede aunq sea un personaje en pie.
    // si no queda ninguno, perdimos
    public boolean estaViva() {
        return !getPersonajesVivos().isEmpty();
    }

    public Inventario getInventario() { return inventario; }

    public int getTamanio() { return personajes.size(); }
    public static int getTamanioMaximo() { return TAMANIO_MAXIMO; }
    public static int getTamanioMinimo() { return TAMANIO_MINIMO; }

    // una party sirve para pelear solo si tiene al menos 2 personajes
    public boolean esValida() { return personajes.size() >= TAMANIO_MINIMO; }
}
