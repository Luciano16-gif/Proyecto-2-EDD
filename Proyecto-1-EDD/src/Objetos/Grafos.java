package Objetos;

import Primitivas.Lista;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;

/**
 * Clase para manejar grafos de personas y arcos.
 * 
 * @author ...
 * @version 4/11/2024
 */
public class Grafos {
    private Lista<Arco> arcos;
    public Lista<Persona> personas;
    private Graph graph;

    /**
     * Constructor de la clase Grafos
     */
    public Grafos() {
        arcos = new Lista<>();
        personas = new Lista<>();
        this.graph = new SingleGraph("Grafo de Personas");
    }

    public void addArco1(String nombrePadre, String nombreHijo) {
        // Asegúrate de que el grafo contenga ambos nodos
        if (graph.getNode(nombrePadre) != null && graph.getNode(nombreHijo) != null) {
            // Genera un ID único para el arco usando los nombres de los nodos
            String edgeId = nombrePadre + "-" + nombreHijo;

            // Verifica si el arco ya existe antes de agregarlo
            if (graph.getEdge(edgeId) == null) {
                graph.addEdge(edgeId, nombrePadre, nombreHijo, true); // true para crear un arco dirigido
                
                // Usa el nuevo indexOf para obtener los índices
                int padreIndex = indexOf(nombrePadre);
                int hijoIndex = indexOf(nombreHijo);
                
                // Agregar el arco a la lista solo si los índices son válidos
                if (padreIndex != -1 && hijoIndex != -1) {
                    arcos.append(new Arco(padreIndex, hijoIndex, 1)); // Agregar el arco a la lista
                }
            }
        }
    }

    public void addPersona(Persona persona) {
        String nombre = persona.getNombre(); // Nombre canónico
        // Verifica si el nodo ya existe antes de agregar
        if (graph.getNode(nombre) == null) {
            graph.addNode(nombre); // Crea el nodo con el nombre de la persona
            graph.getNode(nombre).setAttribute("ui.label", nombre);

            // Establecer el estilo del nodo (opcional)
            graph.getNode(nombre).setAttribute("ui.style", "fill-color: yellow; shape: circle; size: 15px;");

            personas.append(persona); // Agrega la persona a la lista de personas
        } else {
            // Si ya existe, podrías optar por actualizar algunos atributos de la persona existente
            // o simplemente dejarlo como está
            System.out.println("La persona " + nombre + " ya existe en el grafo.");
        }
    }

    public void mostrarGrafo() {
        System.setProperty("org.graphstream.ui", "swing");

        // Agregar arcos al grafo
        for (int i = 0; i < arcos.len(); i++) {
            Arco arco = arcos.get(i);
            
            // Asegurarse de que los índices están dentro del rango de la lista de personas
            if (arco.getSrc() < personas.len() && arco.getDest() < personas.len()) {
                String nombrePadre = personas.get(arco.getSrc()).getNombre();
                String nombreHijo = personas.get(arco.getDest()).getNombre();
                
                // Generar un ID único para el arco usando los nombres de los nodos
                String arcoId = nombrePadre + "-" + nombreHijo;

                // Agregar el arco si no existe ya
                if (graph.getEdge(arcoId) == null) {
                    graph.addEdge(arcoId, nombrePadre, nombreHijo, true); // true para crear un arco dirigido
                    graph.getEdge(arcoId).setAttribute("ui.style", "fill-color: red;");
                }
            } else {
                System.out.println("Índice fuera de rango para los arcos: " + arco.getSrc() + ", " + arco.getDest());
            }
        }

        // Mostrar el grafo
        Viewer viewer = graph.display();
        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.CLOSE_VIEWER);
    }

    public int indexOf(String nombre) {
        for (int i = 0; i < this.personas.len(); i++) {
            if (this.personas.get(i).getNombre().equals(nombre)) {
                return i;
            }
        }
        return -1; // No se encontró
    }
}
