package Objetos;

import Primitivas.Lista;
import com.google.gson.JsonElement;

public class JsonArray {
    private Lista<JsonElement> elementos;

    public JsonArray() {
        elementos = new Lista<>();
    }

    public void add(JsonElement elemento) {
        elementos.append(elemento);
    }

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
