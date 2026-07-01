package uadefight.modelo.personajes;

import uadefight.modelo.CrecimientoNivel;
import uadefight.modelo.Personaje;
import uadefight.modelo.habilidades.HabilidadDanio;

// el Arquero es el mas rapido de todos, por eso casi siempre juega primero.
// ataque mas o menos y defensa floja. su gracia es la velocidad. es Toti
public class Arquero extends Personaje {

    private static final long serialVersionUID = 1L;

    public Arquero(String nombre) {
        super(nombre, /*vida*/ 75, /*mana*/ 45, /*ataque*/ 18, /*defensa*/ 9, /*velocidad*/ 18);
        agregarHabilidad(new HabilidadDanio("Flecha Perforante", "Atraviesa al enemigo",
                14, 32));
        agregarHabilidad(new HabilidadDanio("Lluvia de Flechas", "Multiples flechas",
                18, 26));
    }

    // lo importante aca: el arquero gana +3 de velocidad por nivel (el resto poco),
    // asi se mantiene siempre como el q juega primero en la ronda
    @Override
    protected CrecimientoNivel getCrecimientoPorNivel() {
        return new CrecimientoNivel(/*vida*/ 7, /*mana*/ 0, /*ataque*/ 3, /*defensa*/ 1, /*velocidad*/ 3);
    }

    @Override
    public String getClasePersonaje() { return "Arquero"; }
}
