package Objetos;

import Primitivas.HashTable;
import Primitivas.Lista;

/**
 * Clase que representa el árbol genealógico y maneja la construcción del mismo.
 *
 * @version 4/11/2024
 */
public class ArbolGenealogico {
    private HashTable<String, NodoArbol> tablaPersonasPorId; // Mapea ID de persona a NodoArbol
    private Grafos grafos;

    public ArbolGenealogico() {
        tablaPersonasPorId = new HashTable<>();
        this.grafos = new Grafos();
    }


    public void construirArbol(Lista<String> relaciones, Grafos grafos) {
        for (int i = 0; i < relaciones.getSize(); i++) {
            String relacion = relaciones.get(i);
            String[] partes = relacion.split(" : ");
            if (partes.length == 2) {
                String padreNombre = partes[0];
                String hijoNombre = partes[1];

                // Añadir al grafo
                Persona padre = new Persona(padreNombre);
                Persona hijo = new Persona(hijoNombre);

                grafos.addPersona(padre);
                grafos.addPersona(hijo);

                // Agregar el arco solo si no existe una conexión previa
                if (!grafos.estaConectado(padre.getId(), hijo.getId())) {
                    grafos.addArco1(padre.getId(), hijo.getId());
                }
            }
        }
    }

    // Método auxiliar para obtener un nodo existente o crear uno nuevo
    private NodoArbol obtenerONuevoNodo(String nombre) {
        String id = generarIdDesdeNombre(nombre);
        NodoArbol nodo = tablaPersonasPorId.get(id);

        // Si no existe el nodo, crearlo y añadirlo a la tabla y al grafo
        if (nodo == null) {
            Persona nuevaPersona = new Persona(nombre);
            nodo = new NodoArbol(nuevaPersona);
            tablaPersonasPorId.put(id, nodo);
            grafos.addPersona(nuevaPersona); // Añadir al grafo
        }
        return nodo;
    }

    // Genera un ID único para una persona basado en el nombre
    private String generarIdDesdeNombre(String nombre) {
        return nombre.replaceAll(" ", "_").toLowerCase();
    }
}