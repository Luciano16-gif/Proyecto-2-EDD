package proyecto.pkg1.edd;

import Funciones.Funcion;
import Objetos.ArbolGenealogico;
import Objetos.Grafos;
import Objetos.Persona;
import Primitivas.Lista;

public class Proyecto2EDD {

    public static void main(String[] args) {
        // Leer las personas desde el JSON usando la clase Funcion
        Lista<Persona> personas = Funcion.leerJsonConFileChooser();

        if (personas != null && personas.getSize() > 0) {
            // Crear el árbol genealógico y el grafo
            ArbolGenealogico arbolGenealogico = new ArbolGenealogico();
            Grafos grafos = new Grafos();

            // Construir el árbol y el grafo
            arbolGenealogico.construirArbol(personas, grafos);

            // Mostrar el grafo
            grafos.mostrarGrafo();
        } else {
            System.out.println("No se pudo cargar el árbol genealógico.");
        }
    }
}
