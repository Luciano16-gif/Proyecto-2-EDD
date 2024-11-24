package Objetos;

import Primitivas.Lista;
import com.google.gson.JsonElement;


/**

 * Esta clase define el objeto JsonArray, lo cual hay diferentes metodos y atributos 

 * @author: Ricardo Paez - Luciano Minardo - Gabriele Colarusso

 * @version: 21/11/2024

 */

public class JsonArray {
    private Lista<JsonElement> elementos;

    public JsonArray() {
        elementos = new Lista<>();
    }

    public void add(JsonElement elemento) {
        elementos.append(elemento);
    }

    // Getters y Setters
    public JsonElement get(int index) {
        return elementos.get(index);
    }

    public int size() {
        return elementos.getSize();
    }

    public Lista<JsonElement> getElementos() {
        return elementos;
    }
}
