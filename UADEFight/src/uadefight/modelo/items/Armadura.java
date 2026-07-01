package uadefight.modelo.items;

import uadefight.modelo.Entidad;
import uadefight.modelo.Personaje;

// la armadura es el otro equipable, igual q el arma pero te sube la defensa
public class Armadura extends Equipable {

    private static final long serialVersionUID = 1L;

    private int defensaExtra;

    public Armadura(String nombre, int defensaExtra) {
        super(nombre, "+" + defensaExtra + " de Defensa al equiparse");
        this.defensaExtra = defensaExtra;
    }

    public int getDefensaExtra() { return defensaExtra; }

    // usar la armadura = ponersela. le pido a la entidad q se equipe; el cambio de
    // armadura lo maneja el propio personaje en equiparArmadura(). sin instanceof
    @Override
    public void aplicarEfecto(Entidad objetivo) {
        if (objetivo != null) objetivo.equipar(this);
    }

    // parte del doble despacho: cuando el personaje me acepta, me pongo como su armadura
    @Override
    public void equiparEn(Personaje personaje) {
        personaje.equiparArmadura(this);
    }

    // igual q el arma: solo un personaje vivo se la puede poner
    @Override
    public boolean puedeUsarseSobre(Entidad objetivo) {
        return objetivo.esPersonaje() && objetivo.estaViva();
    }
}
