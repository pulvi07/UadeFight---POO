package uadefight.gui;

import uadefight.modelo.AporteCombate;
import uadefight.modelo.Batalla;
import uadefight.modelo.Personaje;
import uadefight.modelo.RecompensaBatalla;
import uadefight.modelo.enemigos.Enemigo;
import uadefight.util.BotonPixel;
import uadefight.util.Paleta;
import uadefight.util.Sprites;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

// pantalla q aparece cuando termina la pelea. muestra si ganaste o perdiste, el
// enemigo, el MVP con un trofeo, un panelcito con las estadisticas
// (turnos, daño total, items, etc) y la barra de xp. si ganaste tira confeti
//TODO: error, la vida total se reinicia para la proxima partida y tira toda la vida en la stat
public class PantallaResumen extends JPanel {

    private final VentanaPrincipal ventana;
    private final Batalla batalla;
    private final RecompensaBatalla recompensa;
    private final boolean victoria;

    private final int[] confetiX;
    private final int[] confetiY;
    private final Color[] confetiC;

    public PantallaResumen(VentanaPrincipal ventana, Batalla batalla,
                           RecompensaBatalla recompensa, boolean victoria) {
        this.ventana = ventana;
        this.batalla = batalla;
        this.recompensa = recompensa;
        this.victoria = victoria;
        setLayout(null);
        setBackground(Paleta.FONDO_OSCURO);

        // genero el confeti una sola vez con posiciones random.
        Random r = new Random(7);
        confetiX = new int[60];
        confetiY = new int[60];
        confetiC = new Color[60];
        Color[] palette = {
                new Color(248, 113, 113),
                new Color(250, 204, 21),
                new Color(74, 222, 128),
                new Color(96, 165, 235),
                new Color(192, 132, 252)
        };
        for (int i = 0; i < confetiX.length; i++) {
            confetiX[i] = r.nextInt(VentanaPrincipal.ANCHO);
            confetiY[i] = r.nextInt(VentanaPrincipal.ALTO);
            confetiC[i] = palette[r.nextInt(palette.length)];
        }

        // Botones
        BotonPixel btnInicio = new BotonPixel("VOLVER AL INICIO",
                Paleta.BOTON_AZUL, Color.WHITE);
        btnInicio.setBounds(280, 575, 180, 36);
        btnInicio.addActionListener(e -> ventana.mostrarInicio());
        add(btnInicio);

        BotonPixel btnSalir = new BotonPixel("SALIR",
                Paleta.FONDO_PANEL_CLARO, Color.WHITE);
        btnSalir.setBounds(480, 575, 140, 36);
        btnSalir.addActionListener(e -> System.exit(0));
        add(btnSalir);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        int w = getWidth();
        int h = getHeight();

        // Fondo oscuro
        g2.setColor(Paleta.FONDO_OSCURO);
        g2.fillRect(0, 0, w, h);

        // Confeti pixelado
        if (victoria) {
            for (int i = 0; i < confetiX.length; i++) {
                g2.setColor(confetiC[i]);
                g2.fillRect(confetiX[i], confetiY[i], 6, 6);
            }
        }

        // === Titulo ===
        String titulo = victoria ? "VICTORIA" : "DERROTA";
        Color colorTitulo = victoria ? Paleta.TEXTO_AMARILLO : Paleta.TEXTO_ROJO;
        g2.setFont(Paleta.fuenteTitulo(46));
        FontMetrics fm = g2.getFontMetrics();
        int tx = (w - fm.stringWidth(titulo)) / 2;
        g2.setColor(new Color(0, 0, 0, 100));
        g2.drawString(titulo, tx + 2, 56);
        g2.setColor(colorTitulo);
        g2.drawString(titulo, tx, 54);

        // Subtitulo
        g2.setFont(Paleta.fuentePixel(13));
        g2.setColor(Paleta.TEXTO_VERDE);
        String sub;
        if (victoria && !batalla.getEnemigos().isEmpty()) {
            if (batalla.getEnemigos().size() == 1) {
                sub = batalla.getEnemigos().get(0).getNombre().toUpperCase() + " FUE DERROTADO";
            } else {
                sub = "TODOS LOS ENEMIGOS FUERON DERROTADOS";
            }
        } else {
            sub = "TU PARTY FUE DERROTADA";
        }
        int sx = (w - g2.getFontMetrics().stringWidth(sub)) / 2;
        g2.setColor(victoria ? Paleta.TEXTO_VERDE : Paleta.TEXTO_ROJO);
        g2.drawString(sub, sx, 80);

        // Linea amarilla bajo el titulo
        g2.setColor(Paleta.TEXTO_AMARILLO);
        g2.fillRect(tx, 88, fm.stringWidth(titulo), 3);

        // === Personajes mostrados ===
        // Enemigo a la izquierda (con calavera si murio)
        if (!batalla.getEnemigos().isEmpty()) {
            Enemigo e = batalla.getEnemigos().get(0);
            int ex = w / 2 - 220;
            int ey = 130;
            Sprites.dibujarSprite(g2, Sprites.PROFESOR, Sprites.paletaProfesor(),
                    ex, ey, 7);
            g2.setFont(Paleta.fuentePixel(11));
            g2.setColor(Paleta.TEXTO_CLARO);
            String n = e.getNombre().toUpperCase();
            int nx = ex + (Sprites.PROFESOR[0].length() * 7 - g2.getFontMetrics().stringWidth(n)) / 2;
            g2.drawString(n, nx, ey + 140);
            // KO badge si esta muerto
            if (!e.estaViva()) {
                int badgeX = ex + 30;
                int badgeY = ey + 150;
                g2.setColor(Paleta.TEXTO_ROJO);
                g2.drawRoundRect(badgeX, badgeY, 40, 20, 6, 6);
                g2.setFont(Paleta.fuentePixel(11));
                g2.drawString("KO", badgeX + 12, badgeY + 14);
            }
        }

        // el MVP de verdad: el q mas aporto (daño + curacion), calculado por la
        // batalla. reemplaza al viejo "primer personaje vivo", q era arbitrario
        Personaje mvp = batalla.calcularMVP();

        // Trofeo en el medio, con el nombre del MVP y su aporte asi se justifica
        if (victoria && mvp != null) {
            dibujarTrofeo(g2, w / 2 - 30, 140, 60);
            AporteCombate aporte = batalla.getAporte(mvp);
            g2.setFont(Paleta.fuentePixel(11));
            g2.setColor(Paleta.TEXTO_AMARILLO);
            String etiqueta = "* MVP: " + mvp.getNombre().toUpperCase() + " *";
            int mx = (w - g2.getFontMetrics().stringWidth(etiqueta)) / 2;
            g2.drawString(etiqueta, mx, 292);
            g2.setFont(Paleta.fuentePixel(10));
            g2.setColor(Paleta.TEXTO_SUAVE);
            String detalle = aporte.getDanioHecho() + " de daño / " + aporte.getCuracionHecha() + " de cura";
            int dx = (w - g2.getFontMetrics().stringWidth(detalle)) / 2;
            g2.drawString(detalle, dx, 308);
        }

        // Personaje destacado a la derecha = el MVP
        if (mvp != null) {
            Sprites.SpriteData sd = Sprites.getSpritePorNombre(mvp.getNombre());
            int gx = w / 2 + 160;
            int gy = 130;
            Sprites.dibujarSprite(g2, sd.sprite, sd.paleta, gx, gy, 7);
            g2.setFont(Paleta.fuentePixel(11));
            g2.setColor(Paleta.TEXTO_CLARO);
            String n = mvp.getNombre().toUpperCase();
            int nx = gx + (sd.sprite[0].length() * 7 - g2.getFontMetrics().stringWidth(n)) / 2;
            g2.drawString(n, nx, gy + 140);
            if (victoria) {
                int badgeX = gx + 20;
                int badgeY = gy + 150;
                g2.setColor(Paleta.TEXTO_AMARILLO);
                g2.drawRoundRect(badgeX, badgeY, 56, 20, 6, 6);
                g2.setFont(Paleta.fuentePixel(11));
                g2.drawString("MVP", badgeX + 16, badgeY + 14);
            }
        }

        // === Panel de resumen ===
        int panelX = 80;
        int panelY = 330;
        int panelW = w - 160;
        int panelH = 130;
        g2.setColor(new Color(20, 30, 50));
        g2.fillRoundRect(panelX, panelY, panelW, panelH, 14, 14);
        g2.setColor(new Color(50, 90, 140));
        g2.drawRoundRect(panelX, panelY, panelW, panelH, 14, 14);

        g2.setFont(Paleta.fuentePixel(12));
        g2.setColor(Paleta.TEXTO_AMARILLO);
        String resTitulo = "RESUMEN DE BATALLA";
        int rtx = panelX + (panelW - g2.getFontMetrics().stringWidth(resTitulo)) / 2;
        g2.drawString(resTitulo, rtx, panelY + 18);

        int filaY = panelY + 40;
        int filaH = 18;
        // HP restante del mvp
        int hpRest = mvp != null ? mvp.getVida() : 0;
        int hpMax = mvp != null ? mvp.getVidaMaxima() : 0;

        dibujarLineaResumen(g2, panelX + 20, filaY, panelW - 40,
                "TURNOS JUGADOS", String.valueOf(batalla.getTurnosJugados()),
                Paleta.TEXTO_CLARO);
        dibujarLineaResumen(g2, panelX + 20, filaY + filaH, panelW - 40,
                "DANIO TOTAL INFLIGIDO", String.valueOf(batalla.getDanioTotalInfligido()),
                Paleta.TEXTO_ROJO);
        dibujarLineaResumen(g2, panelX + 20, filaY + 2 * filaH, panelW - 40,
                "HP RESTANTE", hpRest + " / " + hpMax,
                Paleta.TEXTO_VERDE);
        dibujarLineaResumen(g2, panelX + 20, filaY + 3 * filaH, panelW - 40,
                "ITEMS USADOS", String.valueOf(batalla.getItemsUsados()),
                Paleta.TEXTO_AMARILLO);
        dibujarLineaResumen(g2, panelX + 20, filaY + 4 * filaH, panelW - 40,
                "ESPECIALES", String.valueOf(batalla.getHabilidadesUsadas()),
                Paleta.MP_BAR_LLENO);

        // === Barra de XP ===
        if (victoria && mvp != null) {
            int barX = 80;
            int barY = 480;
            int barW = w - 160;
            int barH = 48;

            g2.setColor(new Color(20, 30, 50));
            g2.fillRoundRect(barX, barY, barW, barH, 14, 14);
            g2.setColor(new Color(50, 90, 140));
            g2.drawRoundRect(barX, barY, barW, barH, 14, 14);

            g2.setFont(Paleta.fuentePixel(11));
            g2.setColor(Paleta.TEXTO_CLARO);
            String txt = mvp.getNombre().toUpperCase() + " - NVL " + mvp.getNivel();
            g2.drawString(txt, barX + 14, barY + 16);

            int xpGanado = recompensa.getXpPorPersonaje();
            String xpTxt = "+" + xpGanado + " XP";
            g2.setColor(Paleta.MP_BAR_LLENO);
            g2.drawString(xpTxt, barX + barW - 14 - g2.getFontMetrics().stringWidth(xpTxt),
                    barY + 16);

            // Barra
            int innerX = barX + 14;
            int innerY = barY + 24;
            int innerW = barW - 28;
            int innerH = 14;
            g2.setColor(Paleta.HP_BAR_FONDO);
            g2.fillRect(innerX, innerY, innerW, innerH);
            double pct = mvp.getExperiencia() / (double) mvp.xpParaSiguienteNivel();
            g2.setColor(Paleta.XP_BAR_LLENO);
            g2.fillRect(innerX, innerY, (int)(innerW * Math.min(1, pct)), innerH);

            g2.setFont(Paleta.fuentePixel(10));
            g2.setColor(Paleta.TEXTO_SUAVE);
            String pie = mvp.getExperiencia() + " / "
                    + mvp.xpParaSiguienteNivel() + " XP para NVL "
                    + (mvp.getNivel() + 1);
            g2.drawString(pie, innerX, innerY + innerH + 10);
        }

        g2.dispose();
    }

    private void dibujarLineaResumen(Graphics2D g2, int x, int y, int w,
                                     String izq, String der, Color colorVal) {
        g2.setFont(Paleta.fuentePixel(11));
        g2.setColor(Paleta.TEXTO_SUAVE);
        g2.drawString(izq, x, y);
        g2.setColor(colorVal);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(der, x + w - fm.stringWidth(der), y);
    }

    private void dibujarTrofeo(Graphics2D g2, int x, int y, int size) {
        Color oro = Paleta.TEXTO_AMARILLO;
        Color oroOscuro = new Color(180, 130, 8);
        // Asas
        g2.setColor(oroOscuro);
        g2.fillRect(x - 8, y + 10, 8, 25);
        g2.fillRect(x + size, y + 10, 8, 25);
        // Copa
        g2.setColor(oro);
        g2.fillRect(x, y, size, 45);
        // Cintura
        g2.setColor(oroOscuro);
        g2.fillRect(x + 5, y + 45, size - 10, 6);
        // Pie
        g2.setColor(oro);
        g2.fillRect(x + size / 2 - 4, y + 51, 8, 18);
        // Base
        g2.fillRect(x - 5, y + 69, size + 10, 8);
        // Brillo
        g2.setColor(Color.WHITE);
        g2.fillRect(x + 6, y + 6, 6, 18);

        // Estrellas a los lados
        g2.setColor(oro);
        g2.fillRect(x - 30, y + 35, 6, 6);
        g2.fillRect(x + size + 24, y + 35, 6, 6);
    }
}
