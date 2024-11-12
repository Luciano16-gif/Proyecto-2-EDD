package Objetos;

import Primitivas.HashTable;
import Primitivas.Lista;

/**
 * Clase auxiliar para encapsular los datos del proyecto.
 *
 * @author ...
 * @version 4/11/2024
 */
public class DatosProyecto {
    private Lista<Persona> personas;
    private HashTable<String, Persona> hashTable;
    private Lista<String> relaciones; // Nueva lista para las relaciones

    public DatosProyecto(Lista<Persona> personas, HashTable<String, Persona> hashTable, Lista<String> relaciones) {
        this.personas = personas;
        this.hashTable = hashTable;
        this.relaciones = relaciones;
    }

    public Lista<Persona> getPersonas() {
        return personas;
    }

    public HashTable<String, Persona> getHashTable() {
        return hashTable;
    }
}
