package Objetos;

import Primitivas.HashTable;
import Primitivas.Lista;

public class ArbolGenealogico {
    private NodoArbol raiz;
    private HashTable<String, NodoArbol> tablaPersonas;

    public ArbolGenealogico() {
        tablaPersonas = new HashTable<>();
    }

    public void construirArbol(Lista<Persona> personas) {
        // Crear nodos y almacenarlos en la tabla hash
        for (int i = 0; i < personas.getSize(); i++) {
            Persona persona = personas.get(i);
            NodoArbol nodo = new NodoArbol(persona);
            tablaPersonas.put(persona.getNombre(), nodo);
        }

        // Establecer relaciones padre-hijo
        for (int i = 0; i < personas.getSize(); i++) {
            Persona persona = personas.get(i);
            NodoArbol nodoActual = tablaPersonas.get(persona.getNombre());

            // Establecer padre
            if (persona.getBornTo() != null && !persona.getBornTo().equals("[Unknown]")) {
                NodoArbol nodoPadre = tablaPersonas.get(persona.getBornTo());
                if (nodoPadre != null) {
                    nodoPadre.agregarHijo(nodoActual);
                }
            } else {
                // Si no tiene padre conocido, es una posible raíz
                if (raiz == null) {
                    raiz = nodoActual;
                }
            }

            // Establecer hijos
            Lista<String> nombresHijos = persona.getHijos();
            for (int j = 0; j < nombresHijos.getSize(); j++) {
                String nombreHijo = nombresHijos.get(j);
                NodoArbol nodoHijo = tablaPersonas.get(nombreHijo);
                if (nodoHijo != null) {
                    nodoActual.agregarHijo(nodoHijo);
                }
            }
        }
    }

    public NodoArbol getRaiz() {
        return raiz;
    }

    // Métodos para búsqueda y otras funcionalidades
    public NodoArbol buscarPorNombre(String nombre) {
        return tablaPersonas.get(nombre);
    }

    // Implementar otros métodos según los requerimientos
}
