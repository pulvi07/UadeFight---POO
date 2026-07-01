package uadefight.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// es un boton comun de swing pero dibujado a mano para q quede con onda pixel:
// un color de relleno, una sombrita abajo y un borde clarito arriba. cuando le
// paso el mouse por encima se aclara un toque 
public class BotonPixel extends JButton {

    private Color colorBase;
    private Color colorHover;
    private Color colorTexto;
    private boolean hover;

    public BotonPixel(String texto, Color colorBase, Color colorTexto) {
        super(texto);
        this.colorBase = colorBase;
        this.colorTexto = colorTexto;
        this.colorHover = clarear(colorBase, 30);

        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setForeground(colorTexto);
        setFont(Paleta.fuentePixel(16));

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
            @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
        });
    }

    private Color clarear(Color c, int delta) {
        return new Color(
                Math.min(255, c.getRed() + delta),
                Math.min(255, c.getGreen() + delta),
                Math.min(255, c.getBlue() + delta));
    }

    private Color oscurecer(Color c, int delta) {
        return new Color(
                Math.max(0, c.getRed() - delta),
                Math.max(0, c.getGreen() - delta),
                Math.max(0, c.getBlue() - delta));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        int w = getWidth();
        int h = getHeight();
        int sombra = 4;

        // Sombra de pixel
        g2.setColor(oscurecer(colorBase, 60));
        g2.fillRect(0, sombra, w, h - sombra);

        // Cuerpo principal
        g2.setColor(hover ? colorHover : colorBase);
        g2.fillRect(0, 0, w, h - sombra);

        // Borde superior (highlight)
        g2.setColor(clarear(colorBase, 50));
        g2.fillRect(0, 0, w, 3);

        // Texto
        g2.setFont(getFont());
        g2.setColor(colorTexto);
        FontMetrics fm = g2.getFontMetrics();
        String txt = getText() == null ? "" : getText();
        int tw = fm.stringWidth(txt);
        int tx = (w - tw) / 2;
        int ty = (h - sombra) / 2 + (fm.getAscent() - fm.getDescent()) / 2;
        g2.drawString(txt, tx, ty);

        g2.dispose();
    }
}
