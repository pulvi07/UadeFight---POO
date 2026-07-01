package uadefight.modelo.personajes;

import uadefight.modelo.CrecimientoNivel;
import uadefight.modelo.Personaje;
import uadefight.modelo.habilidades.HabilidadDanio;

// el Guerrero es el tanque: mucha vida y ataque pero casi nada de mana.
// lo pones adelante para q aguante los golpes. en el juego es Chamo 
// (habria q ver de ajustar, me di cuenta q pega una BANDA)
public class Guerrero extends Personaje {

    private static final long serialVersionUID = 1L;

    public Guerrero(String nombre) {
        // los numeros son sus stats base de nivel 1 (vida, mana, ataque, def, vel)
        super(nombre, /*vida*/ 95, /*mana*/ 30, /*ataque*/ 22, /*defensa*/ 15, /*velocidad*/ 10);
        // sus 2 habilidades especiales, las dos hacen daño. el numero del medio
        // es lo q gastan de mana y el otro cuanto pegan
        agregarHabilidad(new HabilidadDanio("Espadaso", "Un corte con espada de alto danio",
                10, 35));
        agregarHabilidad(new HabilidadDanio("Punio fuerte", "Pega con el punio lo mas fuerte posible",
                15, 12));
    }

    // al guerrero le subo sobre todo vida y ataque q es para lo q sirve. la vida y
    // el mana se rellenan solos al subir (lo hace subirNivel())
    @Override
    protected CrecimientoNivel getCrecimientoPorNivel() {
        return new CrecimientoNivel(/*vida*/ 12, /*mana*/ 0, /*ataque*/ 4, /*defensa*/ 3, /*velocidad*/ 1);
    }

    @Override
    public String getClasePersonaje() { return "Guerrero"; }
}
