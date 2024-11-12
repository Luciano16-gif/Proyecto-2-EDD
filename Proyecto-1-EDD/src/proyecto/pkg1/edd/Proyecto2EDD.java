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
\\
        // Leer las personas y relaciones desde el JSON usando la clase Funcion
        DatosProyecto datos = Funcion.leerJsonConFileChooser();

        if (datos != null) {
            Lista<Persona> personas = datos.getPersonas();
            HashTable<String, Persona> hashTable = datos.getHashTable();
            Lista<String> relaciones = datos.getRelaciones(); // Obtiene la lista de relaciones

            if (relaciones != null && relaciones.getSize() > 0) {
                // Crear el árbol genealógico y el grafo
                ArbolGenealogico arbolGenealogico = new ArbolGenealogico();
                Grafos grafos = new Grafos();

                // Construir el árbol genealógico y agregar los arcos al grafo usando relaciones
                arbolGenealogico.construirArbol(relaciones, grafos);

                // Mostrar el grafo en la interfaz gráfica
                grafos.mostrarGrafo();
            } else {
                System.out.println("No se pudo cargar el árbol genealógico: lista de relaciones vacía.");
            }
        } else {
            System.out.println("No se pudo cargar el árbol genealógico: datos nulos.");
        }
    }
}
