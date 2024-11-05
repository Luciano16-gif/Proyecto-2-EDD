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
    Grafos grafos = new Grafos(); // Crear instancia de Grafos
    ArbolGenealogico arbolGenealogico = new ArbolGenealogico(); // Crear instancia de ArbolGenealogico

    // Construir el árbol y el grafo
    arbolGenealogico.construirArbol(personas, grafos);

    // Mostrar el grafo
    grafos.mostrarGrafo(personas);
}



    private static int buscarIndicePorNombre(Lista<Persona> personas, String nombre) {
        for (int i = 0; i < personas.len(); i++) {
            Persona persona = personas.get(i);
            if (persona.getNombre().equals(nombre)) { // Comparar nombre
                return i; // Retornar el índice de la persona encontrada
            }
        }
        return -1; // Retornar -1 si no se encuentra la persona
    }
    
    private static Persona buscarPersonaPorNombre(Lista<Persona> personas, String nombre) {
        for (int i = 0; i < personas.len(); i++) {
            Persona persona = personas.get(i);
            if (persona.getNombre().equals(nombre)) { // Asegúrate de que el método getNombre() existe
                return persona;
            }
        }
        return null; // Retornar null si no se encuentra
    }
}
