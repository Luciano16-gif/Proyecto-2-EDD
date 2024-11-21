package Objetos;

import Primitivas.Lista;

/**
 * Esta clase representa un nodo en el árbol genealógico.
 * 
 * @author Luciano Minardo, Ricardo Paez y Gabriele Colarusso
 * 
 * @version 21/11/2024
 */
public class NodoArbol {
    private Persona persona;
    private Lista<NodoArbol> hijos;
    private Lista<NodoArbol> padres; // Nuevo atributo para almacenar los padres

    public NodoArbol(Persona persona) {
        this.persona = persona;
        this.hijos = new Lista<>();
        this.padres = new Lista<>(); // Inicializamos la lista de padres
    }

    public void agregarHijo(NodoArbol hijo) {
        this.hijos.append(hijo);
        hijo.agregarPadre(this); // También agregamos este nodo como padre del hijo
    }

    public void agregarPadre(NodoArbol padre) {
        this.padres.append(padre);
    }
        
    
    public void removerPadre(NodoArbol padre) {
        padres.remove(padre);
    }


    public Lista<NodoArbol> getHijos() {
        return hijos;
    }

    public Lista<NodoArbol> getPadres() {
        return padres;
    }

    public Persona getPersona() {
        return persona;
    }
    
    /**
     * Remueve un hijo de la lista de hijos del nodo actual.
     *
     * @param hijo NodoArbol que representa al hijo a remover.
     */
    public void removerHijo(NodoArbol hijo) {
        for(int i = 0; i < hijos.len(); i++) {
            if(hijos.get(i).equals(hijo)) {
                hijos.remove(i);
                System.out.println("Hijo removido: " + hijo.getPersona().getNombre());
                return;
            }
        }
        System.out.println("Hijo no encontrado para remover: " + hijo.getPersona().getNombre());
    }
    
    public void printSon() {
        for (int i = 0; i < hijos.len(); i++) {
            System.out.println(hijos.get(i).getPersona().getNombre());
        }
    }
    
    

}
