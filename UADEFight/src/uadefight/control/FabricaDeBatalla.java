package uadefight.control;

import uadefight.modelo.Batalla;
import uadefight.modelo.Party;
import uadefight.modelo.Personaje;
import uadefight.modelo.enemigos.Ayudante;
import uadefight.modelo.enemigos.Enemigo;
import uadefight.modelo.enemigos.Profesor;

import java.util.ArrayList;
import java.util.List;

// arma la batalla: decide q enemigos aparecen y con cuanta vida, escalados a la
// party. antes esto lo hacia la PantallaBatalla, q es la vista: mezclaba lo visual
// con la configuracion del combate. aca queda separado, la pantalla solo pide
// "creame la batalla para esta party" y no sabe nada de los enemigos concretos.
public class FabricaDeBatalla {

    // arma la batalla completa para una party: crea sus enemigos escalados y los
    // mete en una Batalla nueva lista para pelear
    public Batalla crearBatalla(Party party) {
        return new Batalla(party, crearEnemigos(party));
    }

    // a partir de este nivel promedio la party ya avanzo lo suficiente (+2 sobre el
    // nivel inicial 1) como para sumar un tercer enemigo a la pelea
    private static final int NIVEL_PARA_TERCER_ENEMIGO = 3;

    // los enemigos de la pelea. base: Godio (Profesor) y Javier Valda (Ayudante).
    // la vida sale de una base por enemigo escalada segun la party. cuando la party
    // ya subio (nivel promedio +2) sumo un tercer enemigo asi la pelea acompaña la
    // progresion y no queda siempre igual
    private List<Enemigo> crearEnemigos(Party party) {
        List<Enemigo> enemigos = new ArrayList<>();
        enemigos.add(new Profesor("Godio", vidaEnemigo(120, party), 18, 8, 9, 320));
        enemigos.add(new Ayudante("Javier Valda", vidaEnemigo(70, party), 11, 5, 13, 120));
        if (nivelPromedio(party) >= NIVEL_PARA_TERCER_ENEMIGO) {
            enemigos.add(new Ayudante("Ayudante de Catedra", vidaEnemigo(80, party), 13, 6, 14, 150));
        }
        return enemigos;
    }

    // calcula la vida de un enemigo a partir de una "base" suya, escalada por:
    //  - el nivel promedio de la party: +20% de vida por cada nivel arriba del 1
    //  - cuantos son en la party: con 2 queda igual (x1), con 4 se duplica (x2)
    // asi cuanto mas fuerte/numerosa la party, mas aguantan los enemigos
    private int vidaEnemigo(int base, Party party) {
        int cantidad = Math.max(1, party.getPersonajes().size());
        double porNivel = 1 + (nivelPromedio(party) - 1) * 0.20;
        double porCantidad = cantidad / 2.0;
        return Math.max(1, (int) Math.round(base * porNivel * porCantidad));
    }

    // nivel promedio (redondeado) de la party. me sirve para escalar la vida enemiga
    private int nivelPromedio(Party party) {
        List<Personaje> ps = party.getPersonajes();
        if (ps.isEmpty()) return 1;
        int suma = 0;
        for (Personaje p : ps) suma += p.getNivel();
        return Math.max(1, Math.round(suma / (float) ps.size()));
    }
}
