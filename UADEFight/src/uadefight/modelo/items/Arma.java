package uadefight.modelo.items;

import uadefight.modelo.Entidad;
import uadefight.modelo.Personaje;

// el arma es un equipable q te sube el ataque cuando la tenes puesta. 
public class Arma extends Equipable {

    private static final long serialVersionUID = 1L;

    private int ataqueExtra;

    public Arma(String nombre, int ataqueExtra) {
        super(nombre, "+" + ataqueExtra + " de Ataque al equiparse");
        this.ataqueExtra = ataqueExtra;
    }

    public int getAtaqueExtra() { return ataqueExtra; }

    // usar el arma = equiparsela. le pido a la entidad q se equipe: si es un
    // personaje se la pone, si es un enemigo no hace nada. sin instanceof
    @Override
    public void aplicarEfecto(Entidad objetivo) {
        if (objetivo != null) objetivo.equipar(this);
    }

    // parte del doble despacho: cuando el personaje me acepta, me pongo como su arma
    @Override
    public void equiparEn(Personaje personaje) {
        personaje.equiparArma(this);
    }

    // un arma solo la puede agarrar un personaje q este vivo
    @Override
    public boolean puedeUsarseSobre(Entidad objetivo) {
        return objetivo.esPersonaje() && objetivo.estaViva();
    }
}
