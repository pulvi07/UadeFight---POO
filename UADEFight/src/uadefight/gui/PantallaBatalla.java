package uadefight.gui;

import uadefight.control.ControladorBatalla;
import uadefight.control.FabricaDeBatalla;
import uadefight.control.GestorDePartida;
import uadefight.control.GestorDePersonajes;
import uadefight.modelo.*;
import uadefight.modelo.enemigos.Ayudante;
import uadefight.modelo.enemigos.Enemigo;
import uadefight.modelo.habilidades.Habilidad;
import uadefight.modelo.items.Item;
import uadefight.util.BotonPixel;
import uadefight.util.Paleta;
import uadefight.util.Sprites;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// esta es la pantalla donde se pelea de verdad. dibuja el aula (pizarron, ventana),
// los enemigos a la izquierda y la party a la derecha, las barras de vida, el log
// abajo y los 4 botones (ATACAR, DEFENSA, ITEM, ESPECIAL).
// aca solo manejo lo visual y los clicks; las cuentas del combate se las pido al
// ControladorBatalla, q es el q sabe

// IMPORTANTE: no use sprites xq no me gusta como se ve la resolucion,se mueve en la pantalla raro,etc
// saque las lineas de los diseños de la pagina q pase por whastapp y si era algo especifico le pedi a 
// chatgpt pero los hace medio feos, intentar sacar todos de esa pagina, no intentar hacer a mano q te vuelve loco

public class PantallaBatalla extends JPanel {

    private final VentanaPrincipal ventana;
    private final Batalla batalla;
    private final ControladorBatalla cb;

    private Entidad turnoActual;
    private JTextArea logArea;

    // para las mini animaciones: quien se esta moviendo y cuanto lo corro de su
    // lugar (en x e y). cuando no hay animacion, entidadAnimada queda en null
    private Entidad entidadAnimada;
    private int animOffsetX;
    private int animOffsetY;

    private BotonPixel btnAtacar;
    private BotonPixel btnDefensa;
    private BotonPixel btnItem;
    private BotonPixel btnEspecial;
    private BotonPixel btnGuardar;

    public PantallaBatalla(VentanaPrincipal ventana, Party party) {
        this(ventana, party, null);
    }

    public PantallaBatalla(VentanaPrincipal ventana, Party party, Batalla batallaGuardada) {
        this.ventana = ventana;
        if (batallaGuardada != null && !batallaGuardada.getEnemigos().isEmpty()) {
            // si venimos de cargar una partida, uso esa batalla tal cual (mantiene
            // los enemigos, la vida q tenian y las estadisticas)
            this.batalla = batallaGuardada;
        } else {
            // quien decide q enemigos aparecen y como se escalan es la fabrica, no
            // la vista. yo solo le pido la batalla ya armada para esta party
            this.batalla = new FabricaDeBatalla().crearBatalla(party);
        }
        this.cb = new ControladorBatalla(batalla);

        setLayout(null);
        setBackground(Paleta.FONDO_OSCURO);

        // Area de log (las acciones aparecen ahi)
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(Paleta.fuentePixel(12));
        logArea.setBackground(Paleta.FONDO_PANEL);
        logArea.setForeground(Paleta.TEXTO_CLARO);
        logArea.setMargin(new Insets(8, 12, 8, 12));
        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setBorder(BorderFactory.createLineBorder(Paleta.FONDO_PANEL_CLARO, 2));
        scroll.setBounds(20, 420, VentanaPrincipal.ANCHO - 40, 80);
        add(scroll);

        // Botones de accion (estilo barra inferior)
        int botY = 530;
        int botH = 55;
        int botW = (VentanaPrincipal.ANCHO - 60) / 4;
        int botX = 15;

        btnAtacar = new BotonPixel("ATACAR", Paleta.BOTON_AZUL, Color.WHITE);
        btnAtacar.setBounds(botX, botY, botW, botH);
        btnAtacar.addActionListener(e -> accionAtacar());
        add(btnAtacar);

        btnDefensa = new BotonPixel("DEFENSA", Paleta.BOTON_VERDE, Color.WHITE);
        btnDefensa.setBounds(botX + (botW + 10), botY, botW, botH);
        btnDefensa.addActionListener(e -> accionDefender());
        add(btnDefensa);

        btnItem = new BotonPixel("ITEM", Paleta.BOTON_AMARILLO, Color.WHITE);
        btnItem.setBounds(botX + 2 * (botW + 10), botY, botW, botH);
        btnItem.addActionListener(e -> accionUsarItem());
        add(btnItem);

        btnEspecial = new BotonPixel("ESPECIAL", Paleta.BOTON_VIOLETA, Color.WHITE);
        btnEspecial.setBounds(botX + 3 * (botW + 10), botY, botW, botH);
        btnEspecial.addActionListener(e -> accionEspecial());
        add(btnEspecial);

        // Boton guardar partida (chiquito arriba a la derecha)
        btnGuardar = new BotonPixel("GUARDAR", Paleta.FONDO_PANEL_CLARO, Color.WHITE);
        btnGuardar.setFont(Paleta.fuentePixel(11));
        btnGuardar.setBounds(VentanaPrincipal.ANCHO - 120, 15, 100, 26);
        btnGuardar.addActionListener(e -> guardarPartida());
        add(btnGuardar);

        // Arrancamos el primer turno
        appendLog("La batalla comienza! Que sea menos doloroso que un parcial...");
        avanzarTurno();
    }

    private void appendLog(String s) {
        logArea.append(s + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    // copia todo el log del controlador al cuadro de texto. lo mas facil: borro lo
    // q habia y lo reescribo entero. al final muevo el cursor abajo asi se ve lo ultimo
    private void sincronizarLog() {
        StringBuilder sb = new StringBuilder();
        for (String s : cb.getLog()) sb.append(s).append("\n");
        logArea.setText(sb.toString());
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    // pasa al turno q sigue. si le toca a un personaje prendo los botones y espero
    // q el jugador elija. si le toca a un enemigo lo resuelvo solo con un timer
    // (le pongo una esperita asi se alcanza a leer lo q hizo)
    private void avanzarTurno() {
        // antes de seguir me fijo si la pelea ya termino (gano o perdio alguien)
        EstadoBatalla estado = batalla.verificarFin();
        if (estado == EstadoBatalla.VICTORIA || estado == EstadoBatalla.DERROTA) {
            terminarBatalla();
            return;
        }

        turnoActual = cb.avanzarAlSiguienteTurno();
        if (turnoActual == null) {
            terminarBatalla();
            return;
        }

        repaint();

        if (turnoActual instanceof Enemigo) {
            // turno del enemigo: espero un toque para q se alcance a leer y despues
            // el enemigo hace su saltito hacia la party y pega
            setBotonesHabilitados(false);
            Enemigo en = (Enemigo) turnoActual;
            Timer t = new Timer(450, ev -> reproducirAnimacion(en, "ataque",
                    () -> cb.ejecutarTurnoEnemigo(en),
                    this::avanzarTurno));
            t.setRepeats(false);
            t.start();
        } else {
            // Turno del jugador
            setBotonesHabilitados(true);
            Personaje p = (Personaje) turnoActual;
            appendLog("Turno de " + p.getNombre() + " - elegi accion");
        }
    }

    private void setBotonesHabilitados(boolean v) {
        btnAtacar.setEnabled(v);
        btnDefensa.setEnabled(v);
        btnItem.setEnabled(v);
        btnEspecial.setEnabled(v);
    }

    // === MINI ANIMACIONES (medio truchas se hizo lo q se pudo) ===
    // muevo el sprite del q actua unos pixeles con un Timer q va frame por frame.
    // "impacto" es lo q pasa en la mitad de la animacion (ahi recien aplico el daño
    // o la cura, cuando "llega" al objetivo) y "alFinalizar" se llama al terminar
    // (normalmente para pasar de turno).
    private void reproducirAnimacion(Entidad actor, String tipo,
                                     Runnable impacto, Runnable alFinalizar) {
        entidadAnimada = actor;
        final int totalFrames = 12;
        final int[] frame = {0};
        final boolean[] yaImpacto = {false};
        // los personajes estan a la derecha y pegan hacia la izquierda; los enemigos
        // al reves. asi se "acercan" al lado contrario
        final int dir = (actor instanceof Enemigo) ? 1 : -1;

        Timer t = new Timer(28, null);
        t.addActionListener(ev -> {
            frame[0]++;
            double p = frame[0] / (double) totalFrames;
            // seno: arranca en 0, llega al maximo en la mitad y vuelve a 0
            double ola = Math.sin(p * Math.PI);

            switch (tipo) {
                case "ataque":
                    animOffsetX = (int) (ola * 60 * dir);
                    animOffsetY = (int) (-ola * 14);   // saltito
                    break;
                case "especial":
                    animOffsetX = (int) (ola * 80 * dir);
                    animOffsetY = (int) (-ola * 22);   // salto mas grande
                    break;
                case "defensa":
                    animOffsetX = (int) (Math.sin(p * Math.PI * 7) * 7); // tiembla
                    animOffsetY = 0;
                    break;
                case "item":
                default:
                    animOffsetX = 0;
                    animOffsetY = (int) (-Math.abs(Math.sin(p * Math.PI * 2)) * 16); // rebote
                    break;
            }

            // a mitad de camino "pega"/usa el item y actualizo el log
            if (!yaImpacto[0] && p >= 0.5) {
                yaImpacto[0] = true;
                if (impacto != null) impacto.run();
                sincronizarLog();
            }
            repaint();

            if (frame[0] >= totalFrames) {
                ((Timer) ev.getSource()).stop();
                entidadAnimada = null;
                animOffsetX = 0;
                animOffsetY = 0;
                repaint();
                if (alFinalizar != null) alFinalizar.run();
            }
        });
        t.start();
    }

    // === ACCIONES DEL JUGADOR ===

    private void accionAtacar() {
        if (!(turnoActual instanceof Personaje)) return;
        // Elegimos enemigo objetivo (si hay solo uno, va directo)
        Enemigo objetivo = elegirEnemigo();
        if (objetivo == null) return;
        Entidad atacante = turnoActual;
        // apago los botones, hago el saltito y el golpe se aplica a mitad de animacion
        setBotonesHabilitados(false);
        reproducirAnimacion(atacante, "ataque",
                () -> cb.ejecutarAtaque(atacante, objetivo),
                this::avanzarTurno);
    }

    private void accionDefender() {
        if (!(turnoActual instanceof Personaje)) return;
        Entidad actor = turnoActual;
        setBotonesHabilitados(false);
        reproducirAnimacion(actor, "defensa",
                () -> cb.ejecutarDefensa(actor),
                this::avanzarTurno);
    }

    private void accionUsarItem() {
        if (!(turnoActual instanceof Personaje)) return;
        Personaje p = (Personaje) turnoActual;
        Inventario inv = batalla.getParty().getInventario();
        if (inv.cantidadItems() == 0) {
            JOptionPane.showMessageDialog(this, "El inventario esta vacio.",
                    "Inventario", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Item item = (Item) JOptionPane.showInputDialog(this,
                "Elegi un item:", "Items",
                JOptionPane.PLAIN_MESSAGE, null,
                inv.getItems().toArray(), inv.getItems().get(0));
        if (item == null) return;

        // El objetivo depende del tipo de item
        Entidad objetivo;
        if (item.esConsumible()) {
            // Pocion: a un aliado
            objetivo = elegirAliado(p);
        } else {
            // Equipable: equipa el propio usuario
            objetivo = p;
        }
        if (objetivo == null) return;
        Entidad obj = objetivo;
        Item it = item;
        setBotonesHabilitados(false);
        // rebotecito de "tomar pocion" / equiparse
        reproducirAnimacion(p, "item",
                () -> cb.ejecutarUsoItem(p, it, obj),
                this::avanzarTurno);
    }

    private void accionEspecial() {
        if (!(turnoActual instanceof Personaje)) return;
        Personaje p = (Personaje) turnoActual;
        if (p.getHabilidades().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Este personaje no tiene habilidades.",
                    "Especial", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Habilidad h = (Habilidad) JOptionPane.showInputDialog(this,
                "Elegi una habilidad:", "Habilidades",
                JOptionPane.PLAIN_MESSAGE, null,
                p.getHabilidades().toArray(), p.getHabilidades().get(0));
        if (h == null) return;
        if (!h.puedeUsarsePor(p)) {
            JOptionPane.showMessageDialog(this,
                    "No tenes mana suficiente (necesitas " + h.getCostoMana() + ").",
                    "Mana insuficiente", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Entidad objetivo;
        if (h.apuntaAAliado()) {
            objetivo = elegirAliado(p);
        } else {
            objetivo = elegirEnemigo();
        }
        if (objetivo == null) return;
        Entidad obj = objetivo;
        setBotonesHabilitados(false);
        // el especial usa el salto grande
        reproducirAnimacion(p, "especial",
                () -> cb.ejecutarHabilidad(p, h, obj),
                this::avanzarTurno);
    }

    private Enemigo elegirEnemigo() {
        List<Enemigo> vivos = new ArrayList<>();
        // Solo se puede atacar a los enemigos que ya aparecieron en combate
        for (Enemigo e : batalla.obtenerEnemigosActivos()) if (e.estaViva()) vivos.add(e);
        if (vivos.isEmpty()) return null;
        if (vivos.size() == 1) return vivos.get(0);
        return (Enemigo) JOptionPane.showInputDialog(this,
                "A quien atacas?", "Objetivo",
                JOptionPane.PLAIN_MESSAGE, null,
                vivos.toArray(), vivos.get(0));
    }

    private Personaje elegirAliado(Personaje porDefecto) {
        List<Personaje> vivos = batalla.getParty().getPersonajesVivos();
        if (vivos.isEmpty()) return null;
        if (vivos.size() == 1) return vivos.get(0);
        return (Personaje) JOptionPane.showInputDialog(this,
                "A quien?", "Aliado",
                JOptionPane.PLAIN_MESSAGE, null,
                vivos.toArray(), porDefecto);
    }

    private void terminarBatalla() {
        setBotonesHabilitados(false);
        boolean victoria = batalla.getEstado() == EstadoBatalla.VICTORIA;
        RecompensaBatalla recompensa = victoria ? cb.repartirRecompensa() : RecompensaBatalla.vacia();
        if (victoria) guardarProgresoPersonajes();
        // Pequena pausa antes de cambiar de pantalla
        Timer t = new Timer(700, ev -> ventana.mostrarResumen(batalla, recompensa, victoria));
        t.setRepeats(false);
        t.start();
    }

    // cuando ganamos, guardo el nivel/xp/stats de cada personaje al archivo asi
    // la proxima vez q jugas siguen como los dejaste
    private void guardarProgresoPersonajes() {
        try {
            GestorDePersonajes gp = new GestorDePersonajes("personajes.sav");
            Map<String, Personaje> progreso = gp.cargarPersonajes();
            for (Personaje p : batalla.getParty().getPersonajes()) {
                progreso.put(p.getNombre(), p);
            }
            gp.guardarPersonajes(progreso);
        } catch (IOException ex) {
            appendLog("No se pudo guardar el progreso de los personajes: " + ex.getMessage());
        }
    }

    private void guardarPartida() {
        try {
            GestorDePartida g = new GestorDePartida("partida.sav");
            g.guardarPartida(new GestorDePartida.EstadoPartida(batalla.getParty(), batalla));
            JOptionPane.showMessageDialog(this, "Partida guardada en partida.sav",
                    "Guardar", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // === RENDER ===

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        int w = getWidth();
        int h = getHeight();

        // Fondo del aula (piso de madera abajo, pared arriba)
        g2.setColor(new Color(105, 75, 50));
        g2.fillRect(0, 0, w, h - 250);
        g2.setColor(new Color(85, 60, 40));
        g2.fillRect(0, h - 250, w, 50);

        // Piso de madera con vetas
        g2.setColor(new Color(130, 95, 60));
        g2.fillRect(0, h - 200, w, 60);
        g2.setColor(new Color(75, 55, 40));
        for (int i = 0; i < w; i += 90) {
            g2.fillRect(i, h - 200, 3, 60);
        }

        // === Pizarron izquierda ===
        g2.setColor(new Color(180, 145, 90));
        g2.fillRect(40, 50, 380, 230);
        g2.setColor(new Color(35, 95, 70));
        g2.fillRect(55, 65, 350, 200);
        // Lineas blancas en el pizarron
        g2.setColor(new Color(255, 255, 255, 180));
        for (int i = 0; i < 5; i++) {
            int yy = 85 + i * 25;
            g2.fillRect(75, yy, 100 + (i * 30) % 200, 3);
        }
        // Borrador
        g2.setColor(new Color(220, 220, 220));
        g2.fillRect(330, 245, 50, 12);

        // === Ventana derecha ===
        g2.setColor(new Color(180, 145, 90));
        g2.fillRect(w - 250, 30, 200, 220);
        // Cruceta y vidrios
        g2.setColor(new Color(120, 165, 220));
        g2.fillRect(w - 240, 40, 180, 200);
        g2.setColor(new Color(180, 145, 90));
        g2.fillRect(w - 150, 40, 8, 200);
        g2.fillRect(w - 240, 130, 180, 8);

        // === Tarima debajo del profesor ===
        g2.setColor(new Color(85, 60, 40));
        g2.fillRect(60, 270, 350, 12);

        // === Luchadores ===
        // Enemigos a la izquierda y party a la derecha. En ambos lados, el
        // que tiene el turno activo se dibuja grande adelante y el resto
        // mas chico atras. Cada luchador lleva su nombre + barra de HP.
        dibujarEnemigos(g2);
        dibujarParty(g2, w);

        // VS en el medio
        g2.setFont(Paleta.fuentePixel(18));
        g2.setColor(Paleta.TEXTO_SUAVE);
        String vs = "VS";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(vs, w / 2 - fm.stringWidth(vs) / 2, 357);

        g2.dispose();
    }

    private Personaje obtenerPersonajeMostrar() {
        if (turnoActual instanceof Personaje) return (Personaje) turnoActual;
        // si no, mostrar el primer personaje vivo
        List<Personaje> vivos = batalla.getParty().getPersonajesVivos();
        return vivos.isEmpty() ? null : vivos.get(0);
    }

    // cual es el enemigo "activo" (el q va grande adelante): si justo es el turno
    // de un enemigo, ese; sino el primero q siga vivo
    private Enemigo obtenerEnemigoActivo() {
        if (turnoActual instanceof Enemigo) return (Enemigo) turnoActual;
        for (Enemigo e : batalla.getEnemigos()) {
            if (e.estaViva()) return e;
        }
        List<Enemigo> enemigos = batalla.getEnemigos();
        return enemigos.isEmpty() ? null : enemigos.get(enemigos.size() - 1);
    }

    // dibuja los enemigos a la izquierda. primero dibujo los chicos (quedan atras)
    // y al final el activo grande encima, asi tapa a los de atras y se ve adelante
    private void dibujarEnemigos(Graphics2D g2) {
        Enemigo activo = obtenerEnemigoActivo();
        // Primero los chicos (quedan atras), despues el grande encima.
        int chicoX = 215;
        for (Enemigo e : batalla.getEnemigos()) {
            if (e == activo) continue;
            dibujarLuchador(g2, e, Sprites.PROFESOR, paletaDe(e), chicoX, 150, 7, false);
            chicoX += 100;
        }
        if (activo != null) {
            dibujarLuchador(g2, activo, Sprites.PROFESOR, paletaDe(activo), 110, 110, 12, true);
        }
    }

    // igual q los enemigos pero con la party, del lado derecho. el q tiene el turno
    // va grande adelante y los demas chiquitos atrass
    private void dibujarParty(Graphics2D g2, int w) {
        Personaje activo = obtenerPersonajeMostrar();
        int chicoX = w / 2 + 50;
        for (Personaje p : batalla.getParty().getPersonajes()) {
            if (p == activo) continue;
            Sprites.SpriteData sd = Sprites.getSpritePorNombre(p.getNombre());
            dibujarLuchador(g2, p, sd.sprite, sd.paleta, chicoX, 150, 7, false);
            chicoX += 90;
        }
        if (activo != null) {
            Sprites.SpriteData sd = Sprites.getSpritePorNombre(activo.getNombre());
            dibujarLuchador(g2, activo, sd.sprite, sd.paleta, w - 230, 110, 12, true);
            // Barra de mana, bajo el sprite grande del personaje activo
            dibujarBarraMP(g2, w - 230, 290, 140, 12, activo.getMana(), activo.getManaMaximo());
        }
    }

    private java.util.Map<Character, Color> paletaDe(Enemigo e) {
        return e instanceof Ayudante ? Sprites.paletaAyudante() : Sprites.paletaProfesor();
    }

    // dibuja a un luchador cualquiera (personaje o enemigo) con su nombre y barra
    // de vida arriba. si esta muerto lo dibujo medio transparente para q se note
    private void dibujarLuchador(Graphics2D g2, Entidad ent, String[] sprite,
                                 java.util.Map<Character, Color> paleta,
                                 int x, int y, int escala, boolean activo) {
        // si este es el q se esta animando, le sumo el corrimiento al dibujo.
        // muevo solo el sprite, la barrita de vida la dejo quieta en su lugar
        int ox = (ent == entidadAnimada) ? animOffsetX : 0;
        int oy = (ent == entidadAnimada) ? animOffsetY : 0;

        float alpha = ent.estaViva() ? 1f : 0.3f;
        Composite prev = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        Sprites.dibujarSprite(g2, sprite, paleta, x + ox, y + oy, escala);
        g2.setComposite(prev);

        int wpx = Math.max(sprite[0].length() * escala, 100);
        dibujarBarraChica(g2, x, y - (activo ? 26 : 20), wpx, ent, activo);
    }

    // la barrita de vida con el nombre q va arriba de cada luchador. la del activo
    // la hago un poco mas grande para q se note cual esta jugando
    private void dibujarBarraChica(Graphics2D g2, int x, int y, int w, Entidad ent, boolean activo) {
        g2.setFont(Paleta.fuentePixel(activo ? 12 : 10));
        g2.setColor(activo ? Paleta.TEXTO_AMARILLO : Paleta.TEXTO_CLARO);
        g2.drawString(ent.getNombre(), x, y - 3);

        int h = activo ? 12 : 8;
        g2.setColor(Paleta.HP_BAR_FONDO);
        g2.fillRect(x, y, w, h);
        double pct = ent.getVidaMaxima() > 0 ? ent.getVida() / (double) ent.getVidaMaxima() : 0;
        g2.setColor(pct < 0.3 ? Paleta.HP_BAR_BAJO : Paleta.HP_BAR_LLENO);
        g2.fillRect(x, y, (int) (w * pct), h);
        g2.setColor(Paleta.FONDO_PANEL_CLARO);
        g2.drawRect(x, y, w, h);

        g2.setFont(Paleta.fuentePixel(activo ? 11 : 9));
        g2.setColor(Paleta.TEXTO_AMARILLO);
        String hp = ent.getVida() + "/" + ent.getVidaMaxima();
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(hp, x + w - fm.stringWidth(hp), y - 3);
    }

    private void dibujarBarraMP(Graphics2D g2, int x, int y, int w, int h, int mp, int max) {
        g2.setColor(Paleta.HP_BAR_FONDO);
        g2.fillRect(x, y, w, h);
        double pct = mp / (double) max;
        g2.setColor(Paleta.MP_BAR_LLENO);
        g2.fillRect(x, y, (int)(w * pct), h);
        g2.setFont(Paleta.fuentePixel(9));
        g2.setColor(Paleta.TEXTO_SUAVE);
        g2.drawString("MP " + mp + "/" + max, x + 4, y + 9);
    }
}
