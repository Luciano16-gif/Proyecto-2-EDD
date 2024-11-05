/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package proyecto.pkg1.edd;
import Objetos.Funcion;
import Primitivas.Lista;
import Objetos.Persona;
import Objetos.ArbolGenealogico;
import Objetos.Grafos;

/**

 * Esta clase define la clase main

 * @author: Ricardo Paez - Luciano Minardo - Gabriele Colarusso

 * @version: 4/11/2024

 */
public class Proyecto2EDD {
    public static void main(String[] args) {
    Lista<Persona> personas = Funcion.leerJsonConFileChooser(); // Cargar datos desde el JSON
     if (personas != null && personas.getSize() > 0) {
            System.out.println("Personas cargadas exitosamente:");
            for (int i = 0; i < personas.getSize(); i++) {
                Persona persona = personas.get(i);
                System.out.println(persona);
                System.out.println("----------------------------------------------------");
                }
            Grafos grafos = new Grafos(); // Crear instancia de Grafos
            ArbolGenealogico arbolGenealogico = new ArbolGenealogico(); // Crear instancia de ArbolGenealogico

            // Construir el árbol y el grafo
            arbolGenealogico.construirArbol(personas, grafos);

            // Mostrar el grafo
            grafos.mostrarGrafo();
            arbolGenealogico.mostrarPadres();
            arbolGenealogico.mostrarHijos();
            } else {
            System.out.println("No se pudo cargar el árbol genealógico.");
        }
    }
}
