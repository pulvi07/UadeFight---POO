package uadefight;

import javax.swing.SwingUtilities;
import uadefight.gui.VentanaPrincipal;

// clase main, por aca arranca todo el juego.
// no hace mucho: prepara el estilo de las ventanas y abre la ventana principal
// en la pantalla de inicio. de ahi en mas se encarga VentanaPrincipal
public class Main {

    public static void main(String[] args) {
        // invokeLater = todo lo de la interfaz tiene q correr en el hilo de swing,
        // sino swing se pone raro. asi q meto la creacion de la ventana adentro
        SwingUtilities.invokeLater(() -> {
            try {
                // le pongo un estilo q se ve igual en cualquier compu (windows, mac...)
                javax.swing.UIManager.setLookAndFeel(
                        javax.swing.UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {
                // si falla el estilo no pasa nada, sigue con el q venga por defecto
            }

            // creo la ventana, la hago visible y muestro la pantalla de inicio
            VentanaPrincipal ventana = new VentanaPrincipal();
            ventana.setVisible(true);
            ventana.mostrarInicio();
        });
    }
}
