package uadefight.modelo;

import uadefight.modelo.items.Item;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// el inventario es uno solo y lo comparte toda la party (habria q ver si cambiarloodejarlo asi). 
// adentro es basicamente una lista de items con los metodos tipicos: agregar,
// sacar, fijarse si algo esta y contar cuantos hay. nada raro
public class Inventario implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Item> items;

    public Inventario() {
        this.items = new ArrayList<>();
    }

    // mete un item a la lista. si me mandan null no hago nada para no romper
    public void agregarItem(Item item) {
        if (item == null) return;
        items.add(item);
    }

    // saca un item. devuelve true si estaba y lo pudo borrar
    public boolean eliminarItem(Item item) {
        if (item == null) return false;
        return items.remove(item);
    }

    // me dice si el item esta adentro del inventario
    public boolean contieneItem(Item item) {
        return items.contains(item);
    }

    // devuelvo la lista pero de solo lectura asi nadie me la modifica de afuera
    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }

    // cuantos items tengo en total
    public int cantidadItems() { return items.size(); }
}
