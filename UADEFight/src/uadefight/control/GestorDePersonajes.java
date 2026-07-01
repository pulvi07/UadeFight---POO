package uadefight.control;

import uadefight.modelo.Personaje;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

// parecido al GestorDePartida pero para otra cosa: este guarda el progreso de
// los personajes (nivel, xp, stats, equipo) en personajes.sav. la diferencia es
// q el otro guarda una partida puntual y este guarda el avance de los personajes
// para q se mantenga aunq arranques una party nueva desde cero
public class GestorDePersonajes {

    private String rutaArchivo;

    public GestorDePersonajes(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    // devuelve un map nombre->personaje con lo guardado. si no hay archivo o si
    // algo falla al leer, devuelvo un map vacio asi el juego no explota
    @SuppressWarnings("unchecked")
    public Map<String, Personaje> cargarPersonajes() {
        File f = new File(rutaArchivo);
        if (!f.exists()) return new HashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(rutaArchivo)))) {
            return (Map<String, Personaje>) ois.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            return new HashMap<>();
        }
    }

    // guarda el map de personajes al archivo. hago una copia por
    // las dudas de no guardar algo raro q me pasen de afuera
    public void guardarPersonajes(Map<String, Personaje> personajes) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(rutaArchivo)))) {
            oos.writeObject(new HashMap<>(personajes));
        }
    }
}
