package uadefight.modelo.items;

import uadefight.modelo.Entidad;

import java.io.Serializable;

// clase base de todos los items. la hago abstracta xq cada item se usa distinto.
// hay dos familias: los q se consumen (la pocion, q desaparece al usarla) y los
// equipables (arma/armadura, q quedan puestos). por eso cada item dice si es
// consumible y como aplica su efecto. el "valor" lo guarda cada subclase con su
// propio nombre asi no se confunde (valorCuracion, ataqueExtra, etc)
public abstract class Item implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nombre;
    private String descripcion;

    protected Item(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }

    // hace lo q tenga q hacer el item sobre el objetivo (curar, equiparse, etc).
    // cada subclase lo implementa a su manera
    public abstract void aplicarEfecto(Entidad objetivo);

    // true si el item se gasta al usarlo (las pociones), false si queda (equipables)
    public abstract boolean esConsumible();

    // chequea si tiene sentido usar este item sobre ese objetivo
    public abstract boolean puedeUsarseSobre(Entidad objetivo);

    @Override
    public String toString() { return nombre; }
}
