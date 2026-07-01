package uadefight.modelo;

import uadefight.modelo.habilidades.Habilidad;
import uadefight.modelo.items.Arma;
import uadefight.modelo.items.Armadura;
import uadefight.modelo.items.Equipable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// el Personaje es lo q maneja el jugador. es abstracta xq por si sola no significa
// nada: la gracia esta en las subclases (Guerrero, Mago, Arquero, Curador), q cada
// una arma sus habilidades y decide como sube de nivel.
// arriba de lo q ya tiene la Entidad, el personaje suma mana, nivel, experiencia,
// la lista de habilidades y el equipo (arma y armadura)
public abstract class Personaje extends Entidad {

    private static final long serialVersionUID = 1L;

    private int mana;
    private int manaMaximo;
    private int nivel;
    private int experiencia;
    private List<Habilidad> habilidades;
    private Arma armaEquipada;
    private Armadura armaduraEquipada;

    protected Personaje(String nombre, int vidaMaxima, int manaMaximo,
                        int ataque, int defensa, int velocidad) {
        super(nombre, vidaMaxima, ataque, defensa, velocidad);
        this.manaMaximo = manaMaximo;
        this.mana = manaMaximo;
        this.nivel = 1;
        this.experiencia = 0;
        this.habilidades = new ArrayList<>();
    }

    public int getMana() { return mana; }
    public int getManaMaximo() { return manaMaximo; }
    public int getNivel() { return nivel; }
    public int getExperiencia() { return experiencia; }
    public List<Habilidad> getHabilidades() { return Collections.unmodifiableList(habilidades); }
    public Arma getArmaEquipada() { return armaEquipada; }
    public Armadura getArmaduraEquipada() { return armaduraEquipada; }

    protected void agregarHabilidad(Habilidad h) {
        if (h != null) habilidades.add(h);
    }

    // si, un Personaje si es jugable (a diferencia del Enemigo)
    @Override
    public boolean esPersonaje() { return true; }

    // el personaje si sabe equiparse: le paso el item al propio equipable y el se
    // encarga de ponerse en el lugar q corresponde (arma o armadura). esto es doble
    // despacho: entre la entidad y el equipable resuelven el tipo sin instanceof
    @Override
    public void equipar(Equipable equipable) {
        if (equipable != null) equipable.equiparEn(this);
    }

    // le saco mana cuando tira una habilidad. nunca baja de 0
    public void consumirMana(int cantidad) {
        if (cantidad < 0) return;
        mana = Math.max(0, mana - cantidad);
    }

    public void restaurarMana(int cantidad) {
        if (cantidad < 0) return;
        mana = Math.min(manaMaximo, mana + cantidad);
    }

    // le sumo la xp q gano. uso un while xq si gano mucho de una puede subir
    // varios niveles juntos. devuelvo cuantos niveles subio para mostrarlo despues
    public int ganarExperiencia(int cantidad) {
        if (cantidad <= 0) return 0;
        experiencia += cantidad;
        int subidos = 0;
        while (experiencia >= xpParaSiguienteNivel()) {
            experiencia -= xpParaSiguienteNivel();
            subirNivel();
            subidos++;
        }
        return subidos;
    }

    // cuanta xp necesito para el proximo nivel. cada nivel pide mas q el anterior
    // (100, 250, 400...) asi subir cuesta cada vez un poco mas
    public int xpParaSiguienteNivel() {
        return 100 + (nivel - 1) * 150;
    }

    // sube un nivel: incrementa el contador y aplica el crecimiento de esta clase.
    // aca esta toda la logica del "que pasa al subir" (mas stats via aplicarCrecimiento
    // y el mana se rellena). cada clase solo dice CUANTO crece en getCrecimientoPorNivel()
    private void subirNivel() {
        nivel++;
        CrecimientoNivel c = getCrecimientoPorNivel();
        aplicarCrecimiento(c.getVida(), c.getAtaque(), c.getDefensa(), c.getVelocidad());
        manaMaximo += c.getMana();
        mana = manaMaximo; // al subir de nivel se rellena el mana
    }

    // cuanto crece esta clase por nivel. cada subclase devuelve el suyo. es data,
    // no toca nada: la subida en si la hace subirNivel()
    protected abstract CrecimientoNivel getCrecimientoPorNivel();

    // me pongo un arma. si ya tenia una puesta, primero la saco (no se acumulan)
    public void equiparArma(Arma arma) {
        if (armaEquipada != null) armaEquipada.desequipar();
        this.armaEquipada = arma;
        if (arma != null) arma.equipar();
    }

    public void equiparArmadura(Armadura armadura) {
        if (armaduraEquipada != null) armaduraEquipada.desequipar();
        this.armaduraEquipada = armadura;
        if (armadura != null) armadura.equipar();
    }

    // ataque real del personaje = el suyo + lo q le suma el arma si tiene una puesta
    @Override
    public int getAtaqueTotal() {
        int base = getAtaque();
        if (armaEquipada != null && armaEquipada.isEquipado()) {
            base += armaEquipada.getAtaqueExtra();
        }
        return base;
    }

    // igual q el ataque pero con la defensa y la armadura
    @Override
    public int getDefensaTotal() {
        int base = getDefensa();
        if (armaduraEquipada != null && armaduraEquipada.isEquipado()) {
            base += armaduraEquipada.getDefensaExtra();
        }
        return base;
    }

    // aca piso el calculo de daño de la Entidad para q el nivel pese.
    // aparte de q al subir de nivel ya pega mas (le subo el ataque), le agrego
    // un +12% de daño por cada nivel. asi se nota q un personaje alto pega mucho mas
    @Override
    public int calcularDanioAtaque() {
        double factor = 0.85 + Math.random() * 0.30;
        double porNivel = 1 + (nivel - 1) * 0.12;
        return Math.max(1, (int) Math.round(getAtaqueTotal() * factor * porNivel));
    }

    // devuelve el nombre de la clase ("Guerrero", "Mago"...) para mostrarlo en pantalla.
    // cada subclase pone el suyo
    public abstract String getClasePersonaje();
}
