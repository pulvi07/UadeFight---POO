package uadefight.control;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// la bitacora del combate: guarda las lineas de texto q se van generando durante
// la pelea (ataques, curaciones, derrotas...) para q despues la pantalla las
// muestre. antes esto estaba aentro del ControladorBatalla; se separo asi el
// controlador no se ocupa tambien de manejar el log
public class BitacoraCombate implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<String> lineas = new ArrayList<>();

    public void registrar(String linea) {
        lineas.add(linea);
    }

    public List<String> getLineas() {
        return lineas;
    }
}
