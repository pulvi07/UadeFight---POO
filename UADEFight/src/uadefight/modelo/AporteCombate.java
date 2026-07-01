package uadefight.modelo;

import java.io.Serializable;

// lleva la cuenta de lo q aporto un personaje en la pelea: cuanto daño hizo y
// cuanta vida curo. con esto se elige el MVP de verdad (el q mas aporto), en vez
// de mirar solo quien pega mas fuerte. asi un curador q sano un monton tambien
// puede ser el mas valioso aunque no haga daño
public class AporteCombate implements Serializable {

    private static final long serialVersionUID = 1L;

    private int danioHecho;
    private int curacionHecha;

    public void sumarDanio(int cantidad) {
        if (cantidad > 0) danioHecho += cantidad;
    }

    public void sumarCuracion(int cantidad) {
        if (cantidad > 0) curacionHecha += cantidad;
    }

    public int getDanioHecho() { return danioHecho; }
    public int getCuracionHecha() { return curacionHecha; }

    // el puntaje q decide el MVP. el daño y la curacion valen lo mismo (1 HP es
    // 1 HP), asi el q cura no queda en desventaja frente al q pega
    public int puntaje() {
        return danioHecho + curacionHecha;
    }
}
