package uadefight.gui;

import uadefight.control.GestorDePersonajes;
import uadefight.modelo.Party;
import uadefight.modelo.Personaje;
import uadefight.modelo.habilidades.Habilidad;
import uadefight.modelo.items.Armadura;
import uadefight.modelo.items.Arma;
import uadefight.modelo.items.Pocion;
import uadefight.modelo.personajes.Arquero;
import uadefight.modelo.personajes.Curador;
import uadefight.modelo.personajes.Guerrero;
import uadefight.modelo.personajes.Mago;
import uadefight.util.BotonPixel;
import uadefight.util.Paleta;
import uadefight.util.Sprites;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

// pantalla de "ARMA TU PARTY". arriba hay 4 slots (los lugares del equipo) y
// abajo las 4 cards de personajes q se pueden elegir. al clickear una card te
// muestra la info y la podes agregar o sacar. cuando tenes al menos 2 se prende
// el boton PELEAR. tambien levanta el progreso guardado de partidas anteriores
// TO_DO prioridad baja: poner mas lindo la info del personaje, buscar buenos diseños en google 
public class PantallaParty extends JPanel {

    private final VentanaPrincipal ventana;

    // Slots (1 a 4) -> personaje asignado
    private Personaje[] slots;
    private List<PersonajeCarta> opciones;
    private BotonPixel btnPelear;
    private JLabel lblContador;

    public PantallaParty(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(null);
        setBackground(new Color(20, 40, 80));
        slots = new Personaje[4];

        // === Opciones (los 4 personajes disponibles) ===
        // Si ya jugamos antes, recuperamos el nivel/experiencia ganados.
        Map<String, Personaje> progresoGuardado =
                new GestorDePersonajes("personajes.sav").cargarPersonajes();
        opciones = new ArrayList<>();
        opciones.add(new PersonajeCarta(obtenerPersonaje(progresoGuardado, "Chamo", () -> new Guerrero("Chamo")), "Chamo"));
        opciones.add(new PersonajeCarta(obtenerPersonaje(progresoGuardado, "Pulvi", () -> new Curador("Pulvi")), "Pulvi"));
        opciones.add(new PersonajeCarta(obtenerPersonaje(progresoGuardado, "Toti", () -> new Arquero("Toti")), "Toti"));
        opciones.add(new PersonajeCarta(obtenerPersonaje(progresoGuardado, "Joaco", () -> new Mago("Joaco")), "Joaco"));

        int spacing = 18;
        int cardW = 110;
        int cardH = 100;
        int totalW = 4 * cardW + 3 * spacing;
        int startX = (VentanaPrincipal.ANCHO - totalW) / 2;
        int yCards = 420;
        for (int i = 0; i < opciones.size(); i++) {
            PersonajeCarta c = opciones.get(i);
            c.setBounds(startX + i * (cardW + spacing), yCards, cardW, cardH);
            add(c);
        }

        // === Boton PELEAR ===
        btnPelear = new BotonPixel("> PELEAR", Paleta.BOTON_VERDE, Color.WHITE);
        btnPelear.setBounds(540, 540, 160, 40);
        btnPelear.setEnabled(false);
        btnPelear.addActionListener(e -> arrancarBatalla());
        add(btnPelear);

        BotonPixel btnVolver = new BotonPixel("< VOLVER", Paleta.FONDO_PANEL_CLARO, Color.WHITE);
        btnVolver.setBounds(40, 540, 130, 36);
        btnVolver.addActionListener(e -> ventana.mostrarInicio());
        add(btnVolver);

        lblContador = new JLabel("0 / 4 ELEGIDOS", SwingConstants.CENTER);
        lblContador.setFont(Paleta.fuentePixel(13));
        lblContador.setForeground(Paleta.TEXTO_SUAVE);
        lblContador.setBounds(200, 550, 320, 20);
        add(lblContador);
    }

    public void reiniciar() {
        slots = new Personaje[4];
        for (PersonajeCarta c : opciones) {
            // cada vez q volves a armar la party curo a todos del todo (vida y mana),
            // sino quedan muertos/lastimados de la pelea anterior y arrancas perdiendo.
            // como son los mismos objetos q se usaron en batalla, hay q resetearlos aca
            Personaje p = c.personaje;
            p.recibirCuracion(p.getVidaMaxima());
            p.restaurarMana(p.getManaMaximo());
            c.setSeleccionado(false);
        }
        actualizarContador();
        repaint();
    }

    // si este personaje ya lo jugue antes y quedo guardado, lo recupero con su
    // nivel y xp y lo curo entero para arrancar. si nunca lo use,
    // lo creo de cero en nivel 1
    private Personaje obtenerPersonaje(Map<String, Personaje> guardados, String nombre,
                                       Supplier<Personaje> creador) {
        Personaje p = guardados.get(nombre);
        if (p == null) return creador.get();
        p.recibirCuracion(p.getVidaMaxima());
        p.restaurarMana(p.getManaMaximo());
        return p;
    }

    // cuando clickeo una carta abro un cartelito con la info del personaje (nivel,
    // stats, cuanto daño hace y sus especiales) y dos botones. solo si aprieto
    // agregar/quitar cambio la party. si cancelo no pasa nada
    private void mostrarInfoCarta(PersonajeCarta carta) {
        boolean yaSeleccionado = estaSeleccionado(carta.personaje);
        if (!yaSeleccionado && !tieneSlotLibre()) {
            JOptionPane.showMessageDialog(this, "La party ya esta completa (4 personajes).",
                    "Party llena", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String accion = yaSeleccionado ? "Quitar" : "Agregar";
        String[] opciones = { accion, "Cancelar" };
        int r = JOptionPane.showOptionDialog(this, construirInfo(carta.personaje),
                carta.personaje.getNombre() + " - " + carta.personaje.getClasePersonaje(),
                JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null,
                opciones, opciones[0]);
        if (r != 0) return; // Cancelar o cerrar el dialogo

        if (yaSeleccionado) quitarDeParty(carta);
        else agregarAParty(carta);
    }

    // arma el textito con toda la info q se muestra en el cartel. lo hago con html
    // adentro de un JLabel asi me queda ordenado en varias lineas
    //MEJORAR DISEÑo
    private JComponent construirInfo(Personaje p) {
        // Mismo escalado que calcularDanioAtaque(): ataque * porNivel * (0.85..1.15)
        double porNivel = 1 + (p.getNivel() - 1) * 0.12;
        int atk = p.getAtaqueTotal();
        int prom = (int) Math.round(atk * porNivel);
        int min = (int) Math.round(atk * porNivel * 0.85);
        int max = (int) Math.round(atk * porNivel * 1.15);

        StringBuilder sb = new StringBuilder();
        sb.append("<html><div style='width:300px;'>");
        sb.append("<b>").append(p.getNombre()).append("</b> - ")
          .append(p.getClasePersonaje()).append(" &nbsp; Nivel ").append(p.getNivel())
          .append("<br><br>");
        sb.append("Vida: ").append(p.getVidaMaxima())
          .append(" &nbsp;&nbsp; Mana: ").append(p.getManaMaximo()).append("<br>");
        sb.append("Ataque: ").append(atk)
          .append(" &nbsp;&nbsp; Defensa: ").append(p.getDefensaTotal())
          .append(" &nbsp;&nbsp; Velocidad: ").append(p.getVelocidad()).append("<br>");
        sb.append("Dano por golpe: <b>~").append(prom).append("</b> (")
          .append(min).append(" - ").append(max).append(")<br>");
        sb.append("<span style='color:gray'>Cada nivel suma +12% de dano.</span><br><br>");
        sb.append("<b>Ataques especiales:</b><br>");
        if (p.getHabilidades().isEmpty()) {
            sb.append("Ninguno.<br>");
        } else {
            for (Habilidad h : p.getHabilidades()) {
                String tipo = h.etiquetaTipo();
                sb.append("&bull; <b>").append(h.getNombre()).append("</b> (")
                  .append(h.getCostoMana()).append(" MP, ").append(tipo).append(" ")
                  .append(h.getValorEfecto()).append(")<br>")
                  .append("&nbsp;&nbsp;<i>").append(h.getDescripcion()).append("</i><br>");
            }
        }
        sb.append("</div></html>");
        return new JLabel(sb.toString());
    }

    private boolean estaSeleccionado(Personaje p) {
        for (Personaje s : slots) if (s == p) return true;
        return false;
    }

    private boolean tieneSlotLibre() {
        for (Personaje s : slots) if (s == null) return true;
        return false;
    }

    private void agregarAParty(PersonajeCarta carta) {
        for (int i = 0; i < 4; i++) {
            if (slots[i] == null) {
                slots[i] = carta.personaje;
                carta.setSeleccionado(true);
                actualizarContador();
                repaint();
                return;
            }
        }
    }

    private void quitarDeParty(PersonajeCarta carta) {
        for (int i = 0; i < 4; i++) {
            if (slots[i] == carta.personaje) {
                slots[i] = null;
                carta.setSeleccionado(false);
                actualizarContador();
                repaint();
                return;
            }
        }
    }

    private void actualizarContador() {
        int n = 0;
        for (Personaje p : slots) if (p != null) n++;
        lblContador.setText(n + " / 4 ELEGIDOS");
        btnPelear.setEnabled(n >= Party.getTamanioMinimo());
    }

    private void arrancarBatalla() {
        Party party = new Party();
        for (Personaje p : slots) if (p != null) party.agregarPersonaje(p);

        // items con los q arranca la party. quedan todos en el inventario 
        party.getInventario().agregarItem(new Pocion("Pocion Chica", 25));
        party.getInventario().agregarItem(new Pocion("Pocion Grande", 50));
        party.getInventario().agregarItem(new Pocion("Pocion Mediana", 35));
        party.getInventario().agregarItem(new Armadura("Armadura UADE", 6));
        party.getInventario().agregarItem(new Arma("Espada de Apuntes", 5));

        ventana.iniciarBatalla(party);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        int w = getWidth();
        int h = getHeight();

        // Cielo nocturno
        g2.setColor(new Color(20, 40, 80));
        g2.fillRect(0, 0, w, h);

        // Pasto abajo
        g2.setColor(Paleta.PASTO);
        g2.fillRect(0, h - 70, w, 70);

        // Edificios apagados
        g2.setColor(new Color(30, 50, 100));
        for (int i = 0; i < 8; i++) {
            int ex = i * 130 - 30;
            int eh = 80 + (i * 37) % 110;
            g2.fillRect(ex, h - 70 - eh, 60, eh);
        }

        // Obelisco chiquito atras
        g2.setColor(new Color(150, 150, 170, 160));
        g2.fillRect(w / 2 - 8, h - 200, 16, 130);

        // Titulo
        g2.setFont(Paleta.fuenteTitulo(34));
        String titulo = "ARMA TU PARTY";
        FontMetrics fm = g2.getFontMetrics();
        int tx = (w - fm.stringWidth(titulo)) / 2;
        int ty = 60;
        g2.setColor(new Color(0, 0, 0, 90));
        g2.drawString(titulo, tx + 2, ty + 2);
        g2.setColor(Paleta.TEXTO_CLARO);
        g2.drawString(titulo, tx, ty);
        g2.setColor(Paleta.TEXTO_AMARILLO);
        g2.fillRect(tx + 10, ty + 8, fm.stringWidth(titulo) - 20, 3);

        // === SLOTS (los rectangulos vacios de arriba) ===
        int slotW = 140;
        int slotH = 220;
        int slotSp = 30;
        int totalSlotW = 4 * slotW + 3 * slotSp;
        int startX = (w - totalSlotW) / 2;
        int slotY = 100;

        for (int i = 0; i < 4; i++) {
            int x = startX + i * (slotW + slotSp);
            g2.setColor(new Color(30, 50, 90));
            g2.fillRoundRect(x, slotY, slotW, slotH, 10, 10);

            // Borde punteado
            g2.setColor(new Color(80, 120, 180));
            Stroke prev = g2.getStroke();
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                    1f, new float[]{6f, 4f}, 0f));
            g2.drawRoundRect(x, slotY, slotW, slotH, 10, 10);
            g2.setStroke(prev);

            if (slots[i] != null) {
                // Dibujamos el sprite del personaje en el slot
                Sprites.SpriteData sd = Sprites.getSpritePorNombre(slots[i].getNombre());
                Sprites.dibujarSprite(g2, sd.sprite, sd.paleta,
                        x + slotW / 2 - sd.sprite[0].length() * 5,
                        slotY + 30, 9);
                g2.setFont(Paleta.fuentePixel(13));
                g2.setColor(Paleta.TEXTO_CLARO);
                String nom = slots[i].getNombre();
                int nx = x + (slotW - g2.getFontMetrics().stringWidth(nom)) / 2;
                g2.drawString(nom, nx, slotY + slotH - 35);
                g2.setFont(Paleta.fuentePixel(10));
                g2.setColor(Paleta.TEXTO_SUAVE);
                String cls = slots[i].getClasePersonaje() + " - NVL " + slots[i].getNivel();
                int cx = x + (slotW - g2.getFontMetrics().stringWidth(cls)) / 2;
                g2.drawString(cls, cx, slotY + slotH - 18);
            } else {
                // Texto VACIO en el slot
                g2.setFont(Paleta.fuentePixel(12));
                g2.setColor(new Color(80, 120, 180));
                String vacio = "VACIO";
                int vx = x + (slotW - g2.getFontMetrics().stringWidth(vacio)) / 2;
                g2.drawString(vacio, vx, slotY + slotH / 2);
            }
        }

        g2.dispose();
    }

    // cada una de las cartitas de abajo q se pueden clickear para elegir personaje.
    // es un panel chico q se dibuja solo (el sprite, el nivel y el nombre)
    private class PersonajeCarta extends JPanel {
        final Personaje personaje;
        final String nombre;
        boolean seleccionado;

        PersonajeCarta(Personaje p, String nombre) {
            this.personaje = p;
            this.nombre = nombre;
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    mostrarInfoCarta(PersonajeCarta.this);
                }
            });
        }

        void setSeleccionado(boolean v) { this.seleccionado = v; repaint(); }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            int w = getWidth();
            int h = getHeight();

            g2.setColor(seleccionado ? new Color(100, 200, 130) : new Color(15, 30, 60));
            g2.fillRoundRect(0, 0, w, h, 8, 8);

            g2.setColor(seleccionado ? new Color(160, 240, 180) : new Color(80, 120, 180));
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(0, 0, w - 1, h - 1, 8, 8);

            // Sprite chico
            Sprites.SpriteData sd = Sprites.getSpritePorNombre(nombre);
            Sprites.dibujarSprite(g2, sd.sprite, sd.paleta, 25, 8, 5);

            // Nivel (arriba a la derecha)
            g2.setFont(Paleta.fuentePixel(10));
            g2.setColor(Paleta.TEXTO_AMARILLO);
            String nvl = "NVL " + personaje.getNivel();
            g2.drawString(nvl, w - g2.getFontMetrics().stringWidth(nvl) - 6, 14);

            // Nombre
            g2.setFont(Paleta.fuentePixel(11));
            g2.setColor(Paleta.TEXTO_CLARO);
            FontMetrics fm = g2.getFontMetrics();
            int tx = (w - fm.stringWidth(nombre.toUpperCase())) / 2;
            g2.drawString(nombre.toUpperCase(), tx, h - 8);

            g2.dispose();
        }
    }
}
