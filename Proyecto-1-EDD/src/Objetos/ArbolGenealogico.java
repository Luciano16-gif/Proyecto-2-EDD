package Objetos;

import Primitivas.HashTable;
import Primitivas.Lista;

/**
 * Esta clase define el objeto ArbolGenealogico, donde se elabora el arbol ( donde hay diferentes atributos y metodos)

 * @author: Ricardo Paez - Luciano Minardo - Gabriele Colarusso

 * @version: 4/11/2024
 */

public class ArbolGenealogico {
    private NodoArbol raiz; // Raíz del árbol genealógico
    private HashTable<String, NodoArbol> tablaPersonas; // Tabla para buscar nodos por nombre

    public ArbolGenealogico() {
        tablaPersonas = new HashTable<>();
    }

    public void construirArbol(Lista<Persona> personas, Grafos grafos) {
    // Crear nodos y almacenarlos en la tabla hash
    for (int i = 0; i < personas.len(); i++) {
        Persona persona = personas.get(i);
        NodoArbol nodo = new NodoArbol(persona);
        tablaPersonas.put(persona.getNombre(), nodo);
        grafos.addPersona(persona); // Añadir persona al grafo
    }

    // Establecer relaciones padre-hijo
    for (int i = 0; i < personas.len(); i++) {
        Persona persona = personas.get(i);
        NodoArbol nodoActual = tablaPersonas.get(persona.getNombre());

        // Establecer padres
        Lista<String> padres = persona.getBornTo();
        if (padres != null && padres.len() > 0) {
            for (int j = 0; j < padres.len(); j++) {
                String nombrePadreOMadre = padres.get(j);
                NodoArbol nodoPadreOMadre = tablaPersonas.get(nombrePadreOMadre);
                if (nodoPadreOMadre != null) {
                    nodoPadreOMadre.agregarHijo(nodoActual); // Agregar relación en el árbol
                    // Agregar arco al grafo
                    grafos.addArco1(nombrePadreOMadre, persona.getNombre());
                } else {
                    // Si no se encuentra, se puede agregar el padre al grafo
                    Persona nuevoPadre = new Persona(nombrePadreOMadre);
                    grafos.addPersona(nuevoPadre); // Agregar padre al grafo
                    grafos.addArco1(nombrePadreOMadre, persona.getNombre()); // Crear arco
                }
            }
        }

        // Establecer hijos
        Lista<String> nombresHijos = persona.getHijos();
        for (int j = 0; j < nombresHijos.len(); j++) {
            String nombreHijo = nombresHijos.get(j);
            NodoArbol nodoHijo = tablaPersonas.get(nombreHijo);
            if (nodoHijo != null) {
                nodoActual.agregarHijo(nodoHijo); // Agregar relación en el árbol
                // Agregar arco al grafo
                grafos.addArco1(persona.getNombre(), nombreHijo);
            } else {
                // Si no se encuentra, se puede agregar el hijo al grafo
                Persona nuevoHijo = new Persona(nombreHijo);
                grafos.addPersona(nuevoHijo); // Agregar hijo al grafo
                grafos.addArco1(persona.getNombre(), nombreHijo); // Crear arco
            }
        }
    }
}


    public NodoArbol getRaiz() {
        return raiz;
    }

    public NodoArbol buscarPorNombre(String nombre) {
        return tablaPersonas.get(nombre);
    }

    // Implementar otros métodos según los requerimientos
}
