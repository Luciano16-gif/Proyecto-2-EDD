package Objetos;

import Primitivas.HashTable;
import Primitivas.Lista;

/**
 * Esta clase define el objeto ArbolGenealogico, donde se elabora el arbol ( donde hay diferentes atributos y metodos)

 * @author: Ricardo Paez - Luciano Minardo - Gabriele Colarusso

 * @version: 4/11/2024
 */

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

            // Establecer padres
            Lista<String> padres = persona.getBornTo();
            if (padres != null && padres.getSize() > 0) {
                for (int j = 0; j < padres.getSize(); j++) {
                    String nombrePadreOMadre = padres.get(j);
                    NodoArbol nodoPadreOMadre = tablaPersonas.get(nombrePadreOMadre);
                    //if (nodoPadreOMadre != null) {
                    //    nodoPadreOMadre.agregarHijo(nodoActual);
                    //    System.out.println("Establecida relación: " + nodoPadreOMadre.getPersona().getNombre() + " -> " + nodoActual.getPersona().getNombre());
                    //} else {
                    //    System.out.println("Padre/Madre no encontrado para: " + persona.getNombre() + ". Nombre: " + nombrePadreOMadre);
                    //}
                }
            } else {
                // Si no tiene padres conocidos, es una posible raíz
                if (raiz == null) {
                    raiz = nodoActual;
                    System.out.println("Nodo raíz establecido: " + nodoActual.getPersona().getNombre());
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
