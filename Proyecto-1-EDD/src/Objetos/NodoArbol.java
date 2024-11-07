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
