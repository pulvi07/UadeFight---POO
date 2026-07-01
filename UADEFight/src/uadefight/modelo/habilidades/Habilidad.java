package uadefight.modelo.habilidades;

import uadefight.modelo.Entidad;
import uadefight.modelo.Personaje;

import java.io.Serializable;

// una Habilidad es un ataque/curacion especial de un personaje. guarda sus datos
// comunes (nombre, descripcion, costo de mana y valor del efecto) pero el "que
// hace" ya no se decide afuera con un tipo: cada subclase (HabilidadDanio,
// HabilidadCuracion) sabe aplicarse sola. asi el controlador no tiene q preguntar
// de q tipo es, simplemente le pide a la habilidad q se aplique.
public abstract class Habilidad implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nombre;
    private String descripcion;
    private int costoMana;
    private int valorEfecto;

    protected Habilidad(String nombre, String descripcion, int costoMana, int valorEfecto) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.costoMana = costoMana;
        this.valorEfecto = valorEfecto;
    }

    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public int getCostoMana() { return costoMana; }
    public int getValorEfecto() { return valorEfecto; }

    // chequea si el personaje puede tirar esta habilidad: tiene q estar vivo y
    // tener el mana suficiente, sino no puede
    public boolean puedeUsarsePor(Personaje usuario) {
        return usuario != null && usuario.estaViva() && usuario.getMana() >= costoMana;
    }

    // calcula cuanto pega o cura, con un toque de random asi no es siempre igual.
    // lo usan las subclases cuando se aplican
    protected int calcularEfecto() {
        double factor = 0.9 + Math.random() * 0.2;
        return Math.max(1, (int) Math.round(valorEfecto * factor));
    }

    // cada habilidad sabe sobre quien tiene sentido usarla (un enemigo si es de
    // daño, un aliado si es curacion)
    public abstract boolean objetivoValido(Entidad objetivo);

    // para la GUI: si apunta a un aliado hay q elegir companiero, sino enemigo
    public abstract boolean apuntaAAliado();

    // etiqueta corta para mostrar en las pantallas ("dano" / "cura")
    public abstract String etiquetaTipo();

    // aca esta el comportamiento propio de cada habilidad: aplica su efecto sobre
    // el objetivo y devuelve un resultado con lo q paso (mensaje + daño infligido)
    // asi el controlador no necesita volver a preguntar de q tipo era
    public abstract ResultadoHabilidad aplicar(Personaje usuario, Entidad objetivo);

    @Override
    public String toString() {
        return nombre + " (" + costoMana + " MP)";
    }
}
