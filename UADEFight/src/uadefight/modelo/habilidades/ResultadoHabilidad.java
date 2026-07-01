package uadefight.modelo.habilidades;

import java.io.Serializable;

// lo q devuelve una habilidad cuando se aplica. lleva el mensaje listo para el
// log y cuanto daño/curacion hizo, asi el controlador puede sumar las
// estadisticas de la batalla y anotar el aporte del personaje sin tener q saber de
// q tipo era la habilidad
public class ResultadoHabilidad implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String mensaje;
    private final int danioInfligido;
    private final int curacionRealizada;

    public ResultadoHabilidad(String mensaje, int danioInfligido, int curacionRealizada) {
        this.mensaje = mensaje;
        this.danioInfligido = danioInfligido;
        this.curacionRealizada = curacionRealizada;
    }

    public String getMensaje() { return mensaje; }
    public int getDanioInfligido() { return danioInfligido; }
    public int getCuracionRealizada() { return curacionRealizada; }
}
