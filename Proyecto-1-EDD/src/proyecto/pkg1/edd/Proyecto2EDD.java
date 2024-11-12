package proyecto.pkg1.edd;

import Objetos.Funcion;
import Objetos.ArbolGenealogico;
import Objetos.Clicks;
import Objetos.Grafos;
import Objetos.DatosProyecto;
import Objetos.NodoArbol;
import Objetos.Persona;
import Primitivas.Lista;
import Primitivas.HashTable;

/**
 * Clase que representa el main.
 *
 * @version 4/11/2024
 */
public class Proyecto2EDD {

    public static void main(String[] args) {
        // Leer las personas desde el JSON usando la clase Funcion
        DatosProyecto datos = Funcion.leerJsonConFileChooser();
        Lista<Persona> personas = datos.getPersonas();
        
        if (personas != null && personas.getSize() > 0) {
            ArbolGenealogico arbolGenealogico = new ArbolGenealogico();
            
            // Crear el grafo
            Grafos grafos = new Grafos();

            grafos.mostrarArbol();
            
            // Construir el árbol genealógico y agregar los arcos al grafo
            arbolGenealogico.construirArbol(personas, grafos);
            

            // Construir la tabla de nombres modificados
            arbolGenealogico.construirTablaNombreModificado();

            // Probar la búsqueda por nombre con la tabla modificada
            String nombreBusqueda = "Lex"; 
            Lista<NodoArbol> nodos = arbolGenealogico.buscarPorNombre(nombreBusqueda);

            // Mostrar los resultados de la búsqueda
            if (nodos.getSize() > 0) {
                System.out.println("Personas encontradas:");
                for (int i = 0; i < nodos.getSize(); i++) {
                    NodoArbol nodo = nodos.get(i);
                    Persona persona = nodo.getPersona();
                    System.out.println(persona.toString());

                    // Mostrar los padres
                    System.out.println("Padres de " + persona.getNombre() + ":");
                    for (int j = 0; j < nodo.getPadres().len(); j++) {
                        NodoArbol padre = nodo.getPadres().get(j);
                        System.out.println(" - " + padre.getPersona().getNombre());
                    }

                    // Mostrar los hijos
                    System.out.println("Hijos de " + persona.getNombre() + ":");
                    for (int j = 0; j < nodo.getHijos().len(); j++) {
                        NodoArbol hijo = nodo.getHijos().get(j);
                        System.out.println(" - " + hijo.getPersona().getNombre());
                    }
                }
            } else {
                System.out.println("No se encontró ninguna persona con el nombre: " + nombreBusqueda);
            }
        } else {
            System.out.println("No se pudo cargar el árbol genealógico.");
        }
    }
}
