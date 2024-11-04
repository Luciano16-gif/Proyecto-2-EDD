package Objetos;

import Primitivas.Lista;

/**

 * Esta clase define la clase NodoArbol con la cual tiene diferentes atributos y funciones que lo definen

 * @author: Ricardo Paez - Luciano Minardo - Gabriele Colarusso

 * @version: 4/11/2024

 */

public class NodoArbol {
    private Persona persona;
    private NodoArbol padre;
    private Lista<NodoArbol> hijos;

    public NodoArbol(Persona persona) {
        this.persona = persona;
        this.hijos = new Lista<>();
    }

    // Getters y setters

    public Persona getPersona() {
        return persona;
    }

    public NodoArbol getPadre() {
        return padre;
    }

    public void setPadre(NodoArbol padre) {
        this.padre = padre;
    }

    public Lista<NodoArbol> getHijos() {
        return hijos;
    }

    public void agregarHijo(NodoArbol hijo) {
        this.hijos.append(hijo);
        hijo.setPadre(this);
    }
}
