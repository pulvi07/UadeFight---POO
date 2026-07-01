package uadefight.modelo;

import java.io.Serializable;

// cuanto sube cada stat cuando un personaje pasa de nivel. cada clase define el
// suyo (el guerrero mucha vida y ataque, el arquero velocidad, etc). dice "cuanto". el "como" aplicarlo lo maneja la
// Entidad/Personaje, asi las subclases no andan tocando los stats una por una
public class CrecimientoNivel implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int vida;
    private final int mana;
    private final int ataque;
    private final int defensa;
    private final int velocidad;

    public CrecimientoNivel(int vida, int mana, int ataque, int defensa, int velocidad) {
        this.vida = vida;
        this.mana = mana;
        this.ataque = ataque;
        this.defensa = defensa;
        this.velocidad = velocidad;
    }

    public int getVida() { return vida; }
    public int getMana() { return mana; }
    public int getAtaque() { return ataque; }
    public int getDefensa() { return defensa; }
    public int getVelocidad() { return velocidad; }
}
