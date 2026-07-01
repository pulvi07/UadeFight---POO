package uadefight.modelo.habilidades;

import uadefight.modelo.Entidad;
import uadefight.modelo.Personaje;

// una habilidad ofensiva: le saca vida al objetivo.
public class HabilidadDanio extends Habilidad {

    private static final long serialVersionUID = 1L;

    public HabilidadDanio(String nombre, String descripcion, int costoMana, int valorEfecto) {
        super(nombre, descripcion, costoMana, valorEfecto);
    }

    @Override
    public boolean objetivoValido(Entidad objetivo) {
        return objetivo != null;
    }

    @Override
    public boolean apuntaAAliado() { return false; }

    @Override
    public String etiquetaTipo() { return "dano"; }

    @Override
    public ResultadoHabilidad aplicar(Personaje usuario, Entidad objetivo) {
        int efectivo = objetivo.recibirDanio(calcularEfecto());
        String mensaje = "> " + usuario.getNombre() + " usa " + getNombre()
                + " sobre " + objetivo.getNombre() + " (-" + efectivo + " HP)";
        return new ResultadoHabilidad(mensaje, efectivo, 0);
    }
}
