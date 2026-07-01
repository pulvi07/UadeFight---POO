package uadefight.gui;

import uadefight.modelo.Batalla;
import uadefight.modelo.Party;
import uadefight.modelo.RecompensaBatalla;
import uadefight.util.Paleta;

import javax.swing.*;
import java.awt.*;

// esta es la ventana del juego. adentro uso un CardLayout, q es como un mazo de
// cartas: tengo las 4 pantallas apiladas y voy mostrando una a la vez (inicio,
// armar party, batalla y resumen). los metodos mostrarX cambian de carta
public class VentanaPrincipal extends JFrame {

    public static final int ANCHO = 960;
    public static final int ALTO = 640;

    private CardLayout cardLayout;
    private JPanel contenedor;

    private PantallaInicio pantallaInicio;
    private PantallaParty pantallaParty;
    private PantallaBatalla pantallaBatalla;
    private PantallaResumen pantallaResumen;

    public VentanaPrincipal() {
        super("UADE Fight");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(ANCHO, ALTO);
        setMinimumSize(new Dimension(ANCHO, ALTO));
        // dejo la ventana fija en 960x640: asi no se puede maximizar ni estirar y la
        // interfaz (q usa posiciones fijas) nunca se descalza (si tengo tiempo dejo q
        // se pudea fullear la pantalla y escalar todo pero me di cuenta a ultimo momento de esto)
        setResizable(false);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Paleta.FONDO_OSCURO);

        cardLayout = new CardLayout();
        contenedor = new JPanel(cardLayout);
        contenedor.setBackground(Paleta.FONDO_OSCURO);

        pantallaInicio = new PantallaInicio(this);
        pantallaParty = new PantallaParty(this);

        contenedor.add(pantallaInicio, "INICIO");
        contenedor.add(pantallaParty, "PARTY");

        add(contenedor);
    }

    public void mostrarInicio() {
        cardLayout.show(contenedor, "INICIO");
    }

    public void mostrarParty() {
        pantallaParty.reiniciar();
        cardLayout.show(contenedor, "PARTY");
    }

    public void recrearPantallaParty() {
        contenedor.remove(pantallaParty);
        pantallaParty = new PantallaParty(this);
        contenedor.add(pantallaParty, "PARTY");
    }

    public void iniciarBatalla(Party party) {
        iniciarBatalla(party, null);
    }

    public void iniciarBatalla(Party party, Batalla batallaGuardada) {
        // cada vez q empieza una pelea creo la pantalla de batalla de cero, asi no
        // me quedan datos pegados de una pelea anterior
        if (pantallaBatalla != null) contenedor.remove(pantallaBatalla);
        pantallaBatalla = new PantallaBatalla(this, party, batallaGuardada);
        contenedor.add(pantallaBatalla, "BATALLA");
        cardLayout.show(contenedor, "BATALLA");
    }

    public void mostrarResumen(Batalla batalla, RecompensaBatalla recompensa, boolean victoria) {
        if (pantallaResumen != null) contenedor.remove(pantallaResumen);
        pantallaResumen = new PantallaResumen(this, batalla, recompensa, victoria);
        contenedor.add(pantallaResumen, "RESUMEN");
        cardLayout.show(contenedor, "RESUMEN");
    }
}
