package uadefight.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;

// junte aca todos los colores y las fuentes q usan las pantallas, asi no ando
// repitiendo new Color(...) por todos lados. si quiero cambiar la onda de colores
// del juego lo toco todo de un solo lugar
public final class Paleta {

    private Paleta() {}

    public static final Color FONDO_OSCURO = new Color(15, 23, 42);
    public static final Color FONDO_PANEL = new Color(30, 41, 59);
    public static final Color FONDO_PANEL_CLARO = new Color(51, 65, 85);
    public static final Color FONDO_CIELO = new Color(56, 130, 211);
    public static final Color FONDO_CIELO_CLARO = new Color(96, 165, 235);
    public static final Color PASTO = new Color(34, 102, 51);
    public static final Color VEREDA = new Color(120, 95, 70);

    public static final Color TEXTO_CLARO = new Color(241, 245, 249);
    public static final Color TEXTO_SUAVE = new Color(148, 163, 184);
    public static final Color TEXTO_AMARILLO = new Color(250, 204, 21);
    public static final Color TEXTO_VERDE = new Color(74, 222, 128);
    public static final Color TEXTO_ROJO = new Color(248, 113, 113);

    public static final Color BOTON_AZUL = new Color(37, 99, 235);
    public static final Color BOTON_VERDE = new Color(16, 185, 129);
    public static final Color BOTON_AMARILLO = new Color(245, 158, 11);
    public static final Color BOTON_VIOLETA = new Color(124, 58, 237);
    public static final Color BOTON_ROJO = new Color(220, 38, 38);

    public static final Color HP_BAR_FONDO = new Color(60, 60, 70);
    public static final Color HP_BAR_LLENO = new Color(74, 222, 128);
    public static final Color HP_BAR_BAJO = new Color(248, 113, 113);
    public static final Color MP_BAR_LLENO = new Color(99, 102, 241);
    public static final Color XP_BAR_LLENO = new Color(124, 58, 237);

    // fuente para los textos. uso Monospaced (la de ancho fijo) xq existe siempre
    // en cualquier java y da un aire medio retro/pixel q pega con el juego
    public static Font fuentePixel(int size) {
        return new Font(Font.MONOSPACED, Font.BOLD, size);
    }

    public static Font fuenteTitulo(int size) {
        return new Font(Font.MONOSPACED, Font.BOLD, size);
    }

    // chequea si una fuente esta instalada en la compu. la deje por si despues
    // queremos usar una fuente pixel de verdad, pero por ahora ni la uso
    public static boolean fuenteDisponible(String name) {
        String[] disponibles = GraphicsEnvironment
                .getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        for (String s : disponibles) if (s.equalsIgnoreCase(name)) return true;
        return false;
    }
}
