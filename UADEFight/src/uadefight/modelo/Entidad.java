package uadefight.modelo;

import uadefight.modelo.items.Equipable;

import java.io.Serializable;

// esta es la clase madre de todo lo q pelea: tanto los personajes como los
// enemigos heredan de aca. tiene lo basico q comparten: nombre, vida, ataque,
// defensa y velocidad, mas los metodos para recibir daño o curarse.
public abstract class Entidad implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nombre;
    private int vida;
    private int vidaMaxima;
    private int ataque;
    private int defensa;
    private int velocidad;
    private boolean defendiendo;

    protected Entidad(String nombre, int vidaMaxima, int ataque, int defensa, int velocidad) {
        this.nombre = nombre;
        this.vidaMaxima = vidaMaxima;
        this.vida = vidaMaxima;
        this.ataque = ataque;
        this.defensa = defensa;
        this.velocidad = velocidad;
        this.defendiendo = false;
    }

    public String getNombre() { return nombre; }
    public int getVida() { return vida; }
    public int getVidaMaxima() { return vidaMaxima; }
    public int getAtaque() { return ataque; }
    public int getDefensa() { return defensa; }
    public int getVelocidad() { return velocidad; }
    public boolean isDefendiendo() { return defendiendo; }

    protected void setVida(int vida) {
        this.vida = Math.max(0, Math.min(vidaMaxima, vida));
    }

    // aplica una mejora de stats al subir de nivel: sube los maximos segun los
    // "extra" q le pase cada clase y deja la entidad a full de vida. centralizo el
    // cambio aca asi las subclases ya no tocan vida/ataque/defensa/velocidad una
    // por una con setters sueltos
    protected void aplicarCrecimiento(int vidaExtra, int ataqueExtra,
                                      int defensaExtra, int velocidadExtra) {
        this.vidaMaxima += vidaExtra;
        this.vida = vidaMaxima; // al subir de nivel se recupera del todo
        this.ataque += ataqueExtra;
        this.defensa += defensaExtra;
        this.velocidad += velocidadExtra;
    }

    // esta viva mientras le quede aunq sea 1 de vida
    public boolean estaViva() {
        return vida > 0;
    }

    // aca recibo un golpe. el daño q entra no es todo: la defensa te tapa una
    // parte (cuanta mas defensa, mas tapa, pero nunca el 100%). y si justo estaba
    // defendiendo, encima le bajo la mitad. devuelvo lo q realmente le saque de vida.
    // siempre saca al menos 1 asi no quedan golpes q hacen 0
    public int recibirDanio(int danioBase) {
        int defensa = getDefensaTotal();
        double mitigacion = defensa / (double) (defensa + 20);
        double danioMitigado = danioBase * (1 - mitigacion);
        if (defendiendo) {
            danioMitigado /= 2;
        }
        int efectivo = Math.max(1, (int) Math.round(danioMitigado));
        setVida(vida - efectivo);
        return efectivo;
    }

    // suma vida pero sin pasarse del maximo (el setVida ya lo limita).
    // devuelvo cuanta vida se curo de verdad
    public int recibirCuracion(int cantidad) {
        int antes = vida;
        setVida(vida + cantidad);
        return vida - antes;
    }

    public void activarDefensa() { this.defendiendo = true; }
    public void desactivarDefensa() { this.defendiendo = false; }

    // Personaje lo pisa devolviendo true. lo uso para no andar preguntando con
    // instanceof por todos lados (curaciones, items, estadisticas): cada entidad
    // responde por si misma
    public boolean esPersonaje() { return false; }

    // le pido a la entidad q se equipe un item. por defecto no hace nada: un
    // enemigo comun no usa equipo. el Personaje lo pisa para ponerselo de verdad.
    // asi el arma/armadura no necesita chequear con instanceof a quien se la aplica
    public void equipar(Equipable equipable) { }

    // defensa "de verdad". aca devuelve la base, pero el Personaje lo pisa para
    // sumarle lo de la armadura q tenga puesta
    public int getDefensaTotal() { return defensa; }

    // lo mismo q arriba pero con el ataque. el Personaje le suma el arma
    public int getAtaqueTotal() { return ataque; }

    // cuanto daño hace al pegar. le meto un random de +-15% para q no sea siempre
    // exacto, asi las peleas no son tan robotizadas. los personajes lo pisan para
    // q el daño escale con el nivel
    public int calcularDanioAtaque() {
        double factor = 0.85 + Math.random() * 0.30;
        return Math.max(1, (int) Math.round(getAtaqueTotal() * factor));
    }

    @Override
    public String toString() {
        return nombre + " (" + vida + "/" + vidaMaxima + " HP)";
    }
}
