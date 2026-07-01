package uadefight.modelo.personajes;

import uadefight.modelo.CrecimientoNivel;
import uadefight.modelo.Personaje;
import uadefight.modelo.habilidades.HabilidadDanio;

// el Mago es el q pega de lejos con magia: un monton de mana pero es de cristal,
// poca vida y defensa. si lo dejan q le peguen se muere rapido. es Joaco
public class Mago extends Personaje {

    private static final long serialVersionUID = 1L;

    public Mago(String nombre) {
        super(nombre, /*vida*/ 60, /*mana*/ 80, /*ataque*/ 14, /*defensa*/ 6, /*velocidad*/ 12);
        agregarHabilidad(new HabilidadDanio("Bola de Fuego", "Llama magica de alto dano",
                20, 40));
        agregarHabilidad(new HabilidadDanio("Magia negra", "Disparo de varita magicarda",
                12, 28));
    }

    // al subir de nivel el mago casi no gana vida (sigue siendo fragil) pero
    // mejora su ataque magico. el mana se rellena solo al subir (lo hace subirNivel())
    @Override
    protected CrecimientoNivel getCrecimientoPorNivel() {
        return new CrecimientoNivel(/*vida*/ 5, /*mana*/ 0, /*ataque*/ 3, /*defensa*/ 1, /*velocidad*/ 1);
    }

    @Override
    public String getClasePersonaje() { return "Mago"; }
}
