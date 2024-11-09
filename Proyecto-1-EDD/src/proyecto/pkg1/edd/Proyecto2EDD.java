package proyecto.pkg1.edd;

import Objetos.Funcion;
import Objetos.ArbolGenealogico;
import Objetos.Grafos;
import Objetos.DatosProyecto;
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
        String person1 = "Michael Raymond";
        String person2 = "Michael Raymond Baratheon";
        System.out.println(person2.startsWith(person1));
        DatosProyecto datos = Funcion.leerJsonConFileChooser();

        Lista<Persona> personas = datos.getPersonas();
        HashTable<String, Persona> hashTable = datos.getHashTable();

        if (personas != null && personas.getSize() > 0) {
            // Crear el árbol genealógico y el grafo
            ArbolGenealogico arbolGenealogico = new ArbolGenealogico();
            Grafos grafos = new Grafos();

            // Construir el árbol genealógico y agregar los arcos al grafo
            arbolGenealogico.construirArbol(personas, grafos);

            // Mostrar el grafo
            grafos.mostrarGrafo();
        } else {
            System.out.println("No se pudo cargar el árbol genealógico.");
        }
    }
}
