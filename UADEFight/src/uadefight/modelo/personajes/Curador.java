package uadefight.modelo.personajes;

import uadefight.modelo.CrecimientoNivel;
import uadefight.modelo.Personaje;
import uadefight.modelo.habilidades.HabilidadCuracion;

// el Curador pega poquito pero es el q mantiene viva a la party curando.
// es el soporte del equipo, lo cuidas para q no se quede sin mana. es Pulvi
public class Curador extends Personaje {

    private static final long serialVersionUID = 1L;

    public Curador(String nombre) {
        super(nombre, /*vida*/ 70, /*mana*/ 70, /*ataque*/ 12, /*defensa*/ 10, /*velocidad*/ 11);
        agregarHabilidad(new HabilidadCuracion("Curacion Mayor", "Restaura HP a un companiero",
                18, 35));
        agregarHabilidad(new HabilidadCuracion("Besito", "Beso de sanacion",
                10, 18));
    }

    // al curador le subo mas q nada defensa (para q aguante) y un poco de todo.
    // el ataque casi no, total no esta para pegar sino para curar
    @Override
    protected CrecimientoNivel getCrecimientoPorNivel() {
        return new CrecimientoNivel(/*vida*/ 7, /*mana*/ 0, /*ataque*/ 2, /*defensa*/ 3, /*velocidad*/ 1);
    }

    @Override
    public String getClasePersonaje() { return "Curador"; }
}
