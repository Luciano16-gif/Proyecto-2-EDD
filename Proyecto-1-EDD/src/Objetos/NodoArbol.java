package Objetos;

import Primitivas.Lista;

/**
 * Esta clase representa un nodo en el árbol genealógico.
 * 
 * @author Luciano Minardo, Ricardo Paez y Gabriele Colarusso
 * 
 * @version 4/11/2024
 */
public class NodoArbol {
    private Persona persona;
    private Lista<NodoArbol> hijos;

    public NodoArbol(Persona persona) {
        this.persona = persona;
        this.hijos = new Lista<>();
    }

    public void agregarHijo(NodoArbol hijo) {
        this.hijos.append(hijo);
    }

    public Lista<NodoArbol> getHijos() {
        return hijos;
    }

    public Persona getPersona() {
        return persona;
    }
}
