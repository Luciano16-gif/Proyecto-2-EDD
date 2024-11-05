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
    private Lista<String> listaPadres; // Lista para almacenar los padres en el formato deseado
    private Lista<String> listaHijos; // Lista para almacenar los hijos en el formato deseado

    public ArbolGenealogico() {
        tablaPersonas = new HashTable<>();
        listaPadres = new Lista<>();
        listaHijos = new Lista<>();
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

            // Establecer padres con el formato "nombre del padre, nombre de la persona"
            Lista<String> padres = persona.getBornTo();
            if (padres != null && padres.len() > 0) {
                for (int j = 0; j < padres.len(); j++) {
                    String nombrePadreOMadre = padres.get(j);
                    // Formato para la lista de padres
                    String padreFormato = (nombrePadreOMadre.isEmpty() ? "[Unknown]" : nombrePadreOMadre) + ": " + persona.getNombre();
                    listaPadres.append(padreFormato); // Agregar a la lista de padres

                    NodoArbol nodoPadreOMadre = tablaPersonas.get(nombrePadreOMadre);
                    if (nodoPadreOMadre != null) {
                        nodoPadreOMadre.agregarHijo(nodoActual);
                        grafos.addArco1(nombrePadreOMadre, persona.getNombre()); // Crear arco en el grafo
                    } else {
                        // Agregar nuevo padre si no existe
                        Persona nuevoPadre = new Persona(nombrePadreOMadre);
                        grafos.addPersona(nuevoPadre);
                        grafos.addArco1(nombrePadreOMadre, persona.getNombre()); // Crear arco
                    }
                }
            }

            // Establecer hijos con el formato "nombre del hijo, nombre de la persona"
            Lista<String> nombresHijos = persona.getHijos();
            for (int j = 0; j < nombresHijos.len(); j++) {
                String nombreHijo = nombresHijos.get(j);
                // Formato para la lista de hijos
                String hijoFormato = nombreHijo + ": " + persona.getNombre();
                listaHijos.append(hijoFormato); // Agregar a la lista de hijos

                NodoArbol nodoHijo = tablaPersonas.get(nombreHijo);
                if (nodoHijo != null) {
                    nodoActual.agregarHijo(nodoHijo);
                    grafos.addArco1(persona.getNombre(), nombreHijo); // Crear arco en el grafo
                } else {
                    // Agregar nuevo hijo si no existe
                    Persona nuevoHijo = new Persona(nombreHijo);
                    grafos.addPersona(nuevoHijo);
                    grafos.addArco1(persona.getNombre(), nombreHijo); // Crear arco
                }
            }
        }
    }

    // Método para mostrar la lista de padres
    public void mostrarPadres() {
        System.out.println("Lista de Padres:");
        for (int i = 0; i < listaPadres.len(); i++) {
            System.out.println(listaPadres.get(i));
        }
    }

    // Método para mostrar la lista de hijos
    public void mostrarHijos() {
        System.out.println("Lista de Hijos:");
        for (int i = 0; i < listaHijos.len(); i++) {
            System.out.println(listaHijos.get(i));
        }
    }

    public NodoArbol getRaiz() {
        return raiz;
    }

    public NodoArbol buscarPorNombre(String nombre) {
        return tablaPersonas.get(nombre);
    }
}
