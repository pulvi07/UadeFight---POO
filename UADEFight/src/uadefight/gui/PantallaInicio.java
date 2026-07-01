package uadefight.gui;

import uadefight.control.GestorDePartida;
import uadefight.util.BotonPixel;
import uadefight.util.Paleta;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

// la primer pantalla q se ve al abrir el juego. es toda dibujada a mano con
// rectangulos: el titulo "UADE FIGHT", el obelisco, edificios, el sol y nubes,
// y abajo los botones jugar / continuar / salir
// la mayoria de rectangulos lo saque de la pagina q mande.
public class PantallaInicio extends JPanel {

    private final VentanaPrincipal ventana;
    // lo guardo como campo xq lo tengo q apagar despues de reiniciar stats
    private BotonPixel btnCargar;

    public PantallaInicio(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(null);
        setBackground(Paleta.FONDO_CIELO);

        BotonPixel btnJugar = new BotonPixel("> JUGAR", Paleta.BOTON_AMARILLO, new Color(70, 50, 5));
        btnCargar = new BotonPixel("> CONTINUAR", Paleta.BOTON_VERDE, Color.WHITE);
        BotonPixel btnReset = new BotonPixel("REINICIAR STATS", Paleta.BOTON_ROJO, Color.WHITE);
        BotonPixel btnSalir = new BotonPixel("X SALIR", Paleta.FONDO_PANEL_CLARO, Color.WHITE);
        btnReset.setFont(Paleta.fuentePixel(13)); // texto largo, lo achico un toque

        btnJugar.setBounds(380, 372, 200, 40);
        btnCargar.setBounds(380, 416, 200, 32);
        btnReset.setBounds(380, 452, 200, 32);
        btnSalir.setBounds(380, 488, 200, 32);

        btnJugar.addActionListener(e -> ventana.mostrarParty());
        btnCargar.addActionListener(e -> cargarPartidaGuardada());
        btnReset.addActionListener(e -> reiniciarStats());
        btnSalir.addActionListener(e -> System.exit(0));

        add(btnJugar);
        add(btnCargar);
        add(btnReset);
        add(btnSalir);

        // si no hay ninguna partida guardada, apago el boton de continuar para q
        // no te deje clickearlo,. TODO: mensaje si no tenes nda guardado 
        GestorDePartida g = new GestorDePartida("partida.sav");
        if (!g.existePartidaGuardada()) {
            btnCargar.setEnabled(false);
        }
    }

    // borra los archivos de progreso (personajes.sav y partida.sav) y recrea la
    // pantalla de party, asi los personajes vuelven a nivel 1 sin nada equipado.
    // pregunto antes xq esto no se puede deshacer
    private void reiniciarStats() {
        int r = JOptionPane.showConfirmDialog(this,
                "Esto borra todo el progreso guardado: niveles, experiencia y equipo.\n"
                + "Los personajes vuelven a nivel 1. Seguro?",
                "Reiniciar stats", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (r != JOptionPane.YES_OPTION) return;

        boolean borro = new File("personajes.sav").delete();
        borro = new File("partida.sav").delete() || borro;

        // recreo la pantalla de party para q los personajes se armen de cero (ya no
        // hay .sav q levantar). sino quedarian en memoria con los stats viejos
        ventana.recrearPantallaParty();
        btnCargar.setEnabled(false); // ya no hay partida para continuar

        JOptionPane.showMessageDialog(this,
                borro ? "Listo, stats reiniciados. Los personajes arrancan de cero."
                      : "No habia nada guardado para borrar.",
                "Reiniciar stats", JOptionPane.INFORMATION_MESSAGE);
    }

    private void cargarPartidaGuardada() {
        GestorDePartida g = new GestorDePartida("partida.sav");
        try {
            GestorDePartida.EstadoPartida estado = g.cargarPartida();
            if (estado != null && estado.getParty() != null) {
                JOptionPane.showMessageDialog(this,
                        "Partida cargada con " + estado.getParty().getTamanio() + " personajes.",
                        "Cargar", JOptionPane.INFORMATION_MESSAGE);
                ventana.iniciarBatalla(estado.getParty(), estado.getBatalla());
            }
        } catch (IOException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo cargar la partida: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        int w = getWidth();
        int h = getHeight();

        // === CIELO (gradiente vertical) ===
        GradientPaint cielo = new GradientPaint(0, 0, Paleta.FONDO_CIELO,
                0, h * 3 / 4, Paleta.FONDO_CIELO_CLARO);
        g2.setPaint(cielo);
        g2.fillRect(0, 0, w, h * 3 / 4);

        // === PASTO ===
        g2.setColor(Paleta.PASTO);
        g2.fillRect(0, h * 3 / 4, w, h / 4);

        // === SOL pixelart amarillo ===
        g2.setColor(Paleta.TEXTO_AMARILLO);
        int sx = w - 90;
        int sy = 40;
        g2.fillRect(sx, sy, 50, 50);
        g2.setColor(new Color(245, 158, 11));
        g2.fillRect(sx + 6, sy + 6, 38, 38);
        g2.setColor(Paleta.TEXTO_AMARILLO);
        g2.fillRect(sx + 12, sy + 12, 26, 26);

        // === NUBES pixelart ===
        dibujarNube(g2, 80, 80);
        dibujarNube(g2, 280, 50);
        dibujarNube(g2, 550, 90);

        // === EDIFICIOS izquierda ===
        dibujarEdificios(g2, 0, h * 3 / 4, w / 2 - 80, true);
        dibujarEdificios(g2, w / 2 + 80, h * 3 / 4, w / 2 - 80, false);

        // === OBELISCO en el centro ===
        dibujarObelisco(g2, w / 2 - 22, h * 3 / 4 - 200, 45, 200);

        // === TITULO "UADE FIGHT" ===
        Font fTitulo = Paleta.fuenteTitulo(54);
        g2.setFont(fTitulo);
        FontMetrics fm = g2.getFontMetrics();
        String uade = "UADE";
        String fight = "FIGHT";
        int gap = 30;
        int totalAncho = fm.stringWidth(uade) + gap + fm.stringWidth(fight);
        int tx = (w - totalAncho) / 2;
        int ty = 120;

        // Sombra
        g2.setColor(new Color(0, 0, 0, 80));
        g2.drawString(uade, tx + 3, ty + 3);
        g2.drawString(fight, tx + fm.stringWidth(uade) + gap + 3, ty + 3);
        // Texto
        g2.setColor(Paleta.TEXTO_CLARO);
        g2.drawString(uade, tx, ty);
        g2.setColor(Paleta.TEXTO_AMARILLO);
        g2.drawString(fight, tx + fm.stringWidth(uade) + gap, ty);

        // Subrayado amarillo
        g2.setColor(Paleta.TEXTO_AMARILLO);
        g2.fillRect(tx, ty + 12, totalAncho, 4);

        // === PANEL detras de los botones ===
        g2.setColor(new Color(15, 23, 42, 220));
        g2.fillRoundRect(350, 360, 260, 170, 18, 18);
        g2.setColor(new Color(96, 165, 235));
        g2.drawRoundRect(350, 360, 260, 170, 18, 18);

        g2.dispose();
    }

    private void dibujarNube(Graphics2D g2, int x, int y) {
        g2.setColor(new Color(255, 255, 255, 230));
        g2.fillRect(x, y + 8, 60, 16);
        g2.fillRect(x + 12, y, 40, 16);
        g2.fillRect(x + 8, y + 16, 50, 8);
    }

    private void dibujarEdificios(Graphics2D g2, int x0, int yBase, int ancho, boolean espejo) {
        int[] alturas = {220, 280, 180, 320, 240, 200};
        int[] ofsx = {0, 70, 140, 200, 270, 330};
        Color[] colores = {
                new Color(30, 41, 70),
                new Color(40, 51, 90),
                new Color(25, 35, 60)
        };
        for (int i = 0; i < alturas.length; i++) {
            int ex = x0 + ofsx[i];
            if (ex + 60 > x0 + ancho) break;
            int eh = alturas[i];
            int ey = yBase - eh;
            g2.setColor(colores[i % colores.length]);
            g2.fillRect(ex, ey, 60, eh);
            // Ventanitas iluminadas
            g2.setColor(new Color(96, 165, 235, 200));
            for (int wy = ey + 12; wy + 8 < yBase - 8; wy += 22) {
                for (int wx = ex + 8; wx + 8 < ex + 60 - 8; wx += 16) {
                    g2.fillRect(wx, wy, 6, 8);
                }
            }
        }
    }

    private void dibujarObelisco(Graphics2D g2, int x, int y, int ancho, int alto) {
        // Base
        g2.setColor(new Color(200, 175, 140));
        g2.fillRect(x - 8, y + alto - 14, ancho + 16, 14);
        // Cuerpo trapezoidal con rectangulos
        for (int i = 0; i < alto - 30; i += 4) {
            double t = i / (double)(alto - 30);
            int red = ancho - (int)(t * (ancho / 3));
            int xx = x + (ancho - red) / 2;
            g2.setColor(new Color(220, 200, 165));
            g2.fillRect(xx, y + i, red, 4);
            // Sombra del lado derecho
            g2.setColor(new Color(180, 160, 130));
            g2.fillRect(xx + red - 4, y + i, 4, 4);
        }
        // Punta
        g2.setColor(new Color(220, 200, 165));
        int[] xs = {x + ancho / 2 - 6, x + ancho / 2 + 6, x + ancho / 2};
        int[] ys = {y, y, y - 18};
        g2.fillPolygon(xs, ys, 3);
    }
}
