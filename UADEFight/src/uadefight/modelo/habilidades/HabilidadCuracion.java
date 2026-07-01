package uadefight.modelo.habilidades;

import uadefight.modelo.Entidad;
import uadefight.modelo.Personaje;

// una habilidad de soporte: le devuelve vida a un companiero. solo tiene sentido
// usarla sobre un Personaje (no curamos enemigos), y eso lo valida ella misma
public class HabilidadCuracion extends Habilidad {

    private static final long serialVersionUID = 1L;

    public HabilidadCuracion(String nombre, String descripcion, int costoMana, int valorEfecto) {
        super(nombre, descripcion, costoMana, valorEfecto);
    }

    @Override
    public boolean objetivoValido(Entidad objetivo) {
        return objetivo != null && objetivo.esPersonaje();
    }

    @Override
    public boolean apuntaAAliado() { return true; }

    @Override
    public String etiquetaTipo() { return "cura"; }

    @Override
    public ResultadoHabilidad aplicar(Personaje usuario, Entidad objetivo) {
        int curado = objetivo.recibirCuracion(calcularEfecto());
        String mensaje = "> " + usuario.getNombre() + " cura a " + objetivo.getNombre()
                + " (+" + curado + " HP)";
        return new ResultadoHabilidad(mensaje, 0, curado);
    }
}
