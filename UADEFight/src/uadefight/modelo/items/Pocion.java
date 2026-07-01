package uadefight.modelo.items;

import uadefight.modelo.Entidad;

// la pocion cura vida y se gasta apenas la usas (es consumible).
// solo se puede usar en aliados vivos, no tiene sentido curar a un enemigo jeje 
public class Pocion extends Item {

    private static final long serialVersionUID = 1L;

    private int valorCuracion;

    public Pocion(String nombre, int valorCuracion) {
        super(nombre, "Restaura " + valorCuracion + " HP");
        this.valorCuracion = valorCuracion;
    }

    public int getValorCuracion() { return valorCuracion; }

    // le devuelve vida al objetivo (siempre q exista y este vivo)
    @Override
    public void aplicarEfecto(Entidad objetivo) {
        if (objetivo != null && objetivo.estaViva()) {
            objetivo.recibirCuracion(valorCuracion);
        }
    }

    // la pocion se gasta, por eso va true
    @Override
    public boolean esConsumible() { return true; }

    // solo se la puede tomar un personaje vivo de la party
    @Override
    public boolean puedeUsarseSobre(Entidad objetivo) {
        return objetivo.esPersonaje() && objetivo.estaViva();
    }
}
