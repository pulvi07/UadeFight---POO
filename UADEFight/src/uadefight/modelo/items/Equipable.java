package uadefight.modelo.items;

import uadefight.modelo.Personaje;

// de aca heredan el arma y la armadura.
// a diferencia de la pocion estos no se gastan, se quedan puestos y mientras
// los tengas equipados te modifican las estadisticaas (ataque o defensa)
public abstract class Equipable extends Item {

    private static final long serialVersionUID = 1L;

    private boolean equipado;

    protected Equipable(String nombre, String descripcion) {
        super(nombre, descripcion);
        this.equipado = false;
    }

    public boolean isEquipado() { return equipado; }
    public void equipar() { this.equipado = true; }
    public void desequipar() { this.equipado = false; }

    // un equipable nunca se gasta, asi q siempre devuelvo false
    @Override
    public boolean esConsumible() { return false; }

    // cada equipable sabe en q "slot" del personaje se pone (el arma como arma, la
    // armadura como armadura). lo llama Personaje.equipar() en el doble despacho
    public abstract void equiparEn(Personaje personaje);
}
