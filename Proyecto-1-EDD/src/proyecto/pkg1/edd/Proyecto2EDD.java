/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package proyecto.pkg1.edd;
import Objetos.Funcion;
import Primitivas.Lista;
import Objetos.Persona;
import Objetos.ArbolGenealogico;

/**
 *
 * @author nicolagabrielecolarusso
 */
public class Proyecto2EDD {

    public static void main(String[] args) {
        Lista<Persona> personas = Funcion.leerJsonConFileChooser();

        if (personas != null && personas.getSize() > 0) {
            System.out.println("Personas cargadas exitosamente:");
            for (int i = 0; i < personas.getSize(); i++) {
                Persona persona = personas.get(i);
                System.out.println(persona);
                System.out.println("----------------------------------------------------");
                }
            // Continuar con la construcción del árbol genealógico
            // Por ejemplo:
            ArbolGenealogico arbol = new ArbolGenealogico();
            arbol.construirArbol(personas);

            // Mostrar el árbol o inicializar la interfaz gráfica
            // ...
        } else {
            System.out.println("No se pudo cargar el árbol genealógico.");
        }
    }
}

    

