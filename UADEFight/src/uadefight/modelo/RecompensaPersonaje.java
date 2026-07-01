package uadefight.modelo;

import java.io.Serializable;

// lo q le toco a UN personaje al repartir la recompensa: cuanta xp gano y cuantos
// niveles subio. son datos puros, sin texto de pantalla: la vista arma el mensaje
// como quiera (antes esto venia ya convertido en un String)
public class RecompensaPersonaje implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Personaje personaje;
    private final int xpGanada;
    private final int nivelesSubidos;

    public RecompensaPersonaje(Personaje personaje, int xpGanada, int nivelesSubidos) {
        this.personaje = personaje;
        this.xpGanada = xpGanada;
        this.nivelesSubidos = nivelesSubidos;
    }

    public Personaje getPersonaje() { return personaje; }
    public int getXpGanada() { return xpGanada; }
    public int getNivelesSubidos() { return nivelesSubidos; }

    public boolean subioDeNivel() { return nivelesSubidos > 0; }
}
