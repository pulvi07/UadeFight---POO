package uadefight.util;

import java.awt.Color;
import java.awt.Graphics2D;

// aca tengo los dibujitos de los personajes y enemigos hechos a mano, sin usar
// imagenes de afuera. cada sprite es un array de strings donde cada letra es un
// pixel, y la paleta dice de q color va cada letra. el espacio ' ' es transparente.
// asi no dependo de ningun archivo .png y el juego anda en cualquier lado y no 
// se bugea como estaba pasando antes con las imgenes
// igualmente esa es la teoria, no se muy bien como funciona, los saque de google los dibujos
public final class Sprites {

    private Sprites() {}


    public static void dibujarSprite(Graphics2D g, String[] sprite,
                                     java.util.Map<Character, Color> paleta,
                                     int x, int y, int escala) {
        for (int row = 0; row < sprite.length; row++) {
            String fila = sprite[row];
            for (int col = 0; col < fila.length(); col++) {
                char c = fila.charAt(col);
                Color color = paleta.get(c);
                if (color == null) continue;
                g.setColor(color);
                g.fillRect(x + col * escala, y + row * escala, escala, escala);
            }
        }
    }

    // === SPRITES ===

    public static final String[] CHAMO = {
            "  KKKKKK  ",
            " KKKKKKKK ",
            " KKPPPPKK ",
            " KKPPPPKK ",
            " KPWPPWPK ",
            "  PPPPPP  ",
            " GGGGGGGG ",
            "GGGGGGGGGG",
            "GGGGGGGGGG",
            " GGGGGGGG ",
            " BB    BB ",
            " BB    BB ",
            " BB    BB ",
            " WW    WW "
    };

    public static final String[] PULVI = {
            "  OOOOOO  ",
            " OOOOOOOO ",
            " OOPPPPOO ",
            " OOPPPPOO ",
            " OPWPPWPO ",
            "  PPPPPP  ",
            " RRRRRRRR ",
            "RRRRRRRRRR",
            "RRRRRRRRRR",
            " RRRRRRRR ",
            " BB    BB ",
            " BB    BB ",
            " BB    BB ",
            " WW    WW "
    };

    public static final String[] TOTI = {
            "  PPPPPP  ",
            " PPPPPPPP ",
            " PPPPPPPP ",
            " PPPPPPPP ",
            " PWPPPPWP ",
            "  PPPPPP  ",
            " YYYYYYYY ",
            "YYYYYYYYYY",
            "YYYYYYYYYY",
            " YYYYYYYY ",
            " BB    BB ",
            " BB    BB ",
            " BB    BB ",
            " WW    WW "
    };

    public static final String[] JOACO = {
            "  KKKKKK  ",
            " KKKKKKKK ",
            " KKPPPPKK ",
            " KKPPPPKK ",
            " KPWPPWPK ",
            "  PPPPPP  ",
            " BBBBBBBB ",
            "BBBWWWWBBB",
            "BBBWWWWBBB",
            " BBBBBBBB ",
            " BB    BB ",
            " BB    BB ",
            " BB    BB ",
            " WW    WW "
    };

    public static final String[] PROFESOR = {
            "    KKKKKK    ",
            "   KKKKKKKK   ",
            "   KKPPPPKK   ",
            "   KPPPPPPK   ",
            "   PWPPPPWP   ",
            "    PPPPPP    ",
            " BBBBBWNNBBBB ",
            "BBBBBWWNNBBBBB",
            "BBBBBWWNNBBBBB",
            "BBBBBWWNNBBBBB",
            " BBBBWWNNBBBB ",
            "  BB      BB  ",
            "  BB      BB  ",
            "  BB      BB  ",
            "  WW      WW  "
    };

    public static java.util.Map<Character, Color> paletaChamo() {
        java.util.Map<Character, Color> m = new java.util.HashMap<>();
        m.put('K', new Color(35, 30, 30));        // pelo negro
        m.put('P', new Color(120, 80, 60));        // piel
        m.put('W', new Color(255, 255, 255));     // ojos
        m.put('G', new Color(34, 139, 87));        // remera verde
        m.put('B', new Color(35, 65, 130));        // pantalon azul
        return m;
    }

    public static java.util.Map<Character, Color> paletaPulvi() {
        java.util.Map<Character, Color> m = new java.util.HashMap<>();
        m.put('O', new Color(230, 140, 60));      // pelo naranja
        m.put('P', new Color(245, 200, 170));     // piel
        m.put('W', new Color(255, 255, 255));
        m.put('R', new Color(190, 40, 50));        // remera roja
        m.put('B', new Color(35, 65, 130));
        return m;
    }

    public static java.util.Map<Character, Color> paletaToti() {
        java.util.Map<Character, Color> m = new java.util.HashMap<>();
        m.put('P', new Color(225, 180, 140));     // piel/pelo claro
        m.put('W', new Color(255, 255, 255));
        m.put('Y', new Color(225, 140, 50));      // remera naranja
        m.put('B', new Color(60, 50, 50));        // pantalon oscuro
        return m;
    }

    public static java.util.Map<Character, Color> paletaJoaco() {
        java.util.Map<Character, Color> m = new java.util.HashMap<>();
        m.put('K', new Color(35, 30, 30));
        m.put('P', new Color(235, 195, 165));
        m.put('W', new Color(255, 255, 255));
        m.put('B', new Color(35, 65, 130));        // camisa azul
        return m;
    }

    public static java.util.Map<Character, Color> paletaProfesor() {
        java.util.Map<Character, Color> m = new java.util.HashMap<>();
        m.put('K', new Color(35, 30, 30));
        m.put('P', new Color(235, 195, 165));
        m.put('W', new Color(255, 255, 255));
        m.put('B', new Color(50, 70, 110));       // saco azul
        m.put('N', new Color(20, 20, 30));         // corbata
        return m;
    }

    // uso el mismo dibujo q el profe pero le cambio los colores para el ayudante
    public static java.util.Map<Character, Color> paletaAyudante() {
        java.util.Map<Character, Color> m = new java.util.HashMap<>();
        m.put('K', new Color(60, 45, 35));
        m.put('P', new Color(225, 180, 140));
        m.put('W', new Color(255, 255, 255));
        m.put('B', new Color(90, 110, 70));       // saco verdoso
        m.put('N', new Color(40, 40, 30));         // corbata
        return m;
    }

    // segun el nombre del personaje te devuelve su dibujo y sus colores. lo hago
    // mirando si el nombre contiene "chamo", "pulvi", etc. si no matchea ninguno
    // devuelvo el del profesor por las dudas
    public static SpriteData getSpritePorNombre(String nombre) {
        if (nombre == null) return new SpriteData(CHAMO, paletaChamo());
        String n = nombre.toUpperCase();
        if (n.contains("CHAMO")) return new SpriteData(CHAMO, paletaChamo());
        if (n.contains("PULVI")) return new SpriteData(PULVI, paletaPulvi());
        if (n.contains("TOTI")) return new SpriteData(TOTI, paletaToti());
        if (n.contains("JOACO")) return new SpriteData(JOACO, paletaJoaco());
        return new SpriteData(PROFESOR, paletaProfesor());
    }

    public static class SpriteData {
        public final String[] sprite;
        public final java.util.Map<Character, Color> paleta;
        public SpriteData(String[] sprite, java.util.Map<Character, Color> paleta) {
            this.sprite = sprite;
            this.paleta = paleta;
        }
    }
}
