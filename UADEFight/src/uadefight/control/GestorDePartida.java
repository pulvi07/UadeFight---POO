package uadefight.control;

import uadefight.modelo.Batalla;
import uadefight.modelo.Party;

import java.io.*;

// este se encarga de guardar y cargar la partida en un archivo (partida.sav).
public class GestorDePartida {

    private String rutaArchivo;

    public GestorDePartida(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    public String getRutaArchivo() { return rutaArchivo; }
    public void setRutaArchivo(String rutaArchivo) { this.rutaArchivo = rutaArchivo; }

    // cajita con todo lo q define una partida guardada: la party y, si habia una
    // pelea en curso, la batalla. si llego a hacer el tema del oro lo agrego aca
    public static class EstadoPartida implements Serializable {
        private static final long serialVersionUID = 1L;
        private Party party;
        private Batalla batalla;

        public EstadoPartida(Party party, Batalla batalla) {
            this.party = party;
            this.batalla = batalla;
        }
        public Party getParty() { return party; }
        public Batalla getBatalla() { return batalla; }
    }

    // escribe la partida al archivo. 
    public void guardarPartida(EstadoPartida partida) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(rutaArchivo)))) {
            oos.writeObject(partida);
        }
    }

    // lee la partida del archivo. si el archivo no existe devuelvo null (no hay
    // nada guardado todavia)
    public EstadoPartida cargarPartida() throws IOException, ClassNotFoundException {
        File f = new File(rutaArchivo);
        if (!f.exists()) return null;
        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(rutaArchivo)))) {
            return (EstadoPartida) ois.readObject();
        }
    }

    public boolean existePartidaGuardada() {
        return new File(rutaArchivo).exists();
    }
}
