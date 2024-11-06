package Objetos;

import Primitivas.Lista;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;

/**
 * Clase para manejar grafos de personas y arcos.
 *
 * @version 4/11/2024
 */
public class Grafos {
    private Graph graph;

    public Grafos() {
        this.graph = new SingleGraph("Grafo de Personas");
    }

    public void addPersona(Persona persona) {
        String id = persona.getId(); // Identificador único
        // Verifica si el nodo ya existe antes de agregar
        if (graph.getNode(id) == null) {
            graph.addNode(id); // Crea el nodo con el identificador único
            graph.getNode(id).setAttribute("ui.label", persona.getNombre());

            // Establecer el estilo del nodo (opcional)
            graph.getNode(id).setAttribute("ui.style", "fill-color: yellow; shape: circle; size: 15px;");
        } else {
            System.out.println("La persona con ID " + id + " ya existe en el grafo.");
        }
    }

    public void addArco1(String idPadre, String idHijo) {
        // Asegúrate de que el grafo contenga ambos nodos
        if (graph.getNode(idPadre) != null && graph.getNode(idHijo) != null) {
            // Genera un ID único para el arco usando los IDs de los nodos
            String edgeId = idPadre + "-" + idHijo;

            // Verifica si el arco ya existe antes de agregarlo
            if (graph.getEdge(edgeId) == null) {
                graph.addEdge(edgeId, idPadre, idHijo, true); // true para crear un arco dirigido

                // Establecer el estilo del arco (opcional)
                graph.getEdge(edgeId).setAttribute("ui.style", "fill-color: red;");
            }
        }
    }

    public void mostrarGrafo() {
        System.setProperty("org.graphstream.ui", "swing");

        // Mostrar el grafo
        Viewer viewer = graph.display();
        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.CLOSE_VIEWER);
    }
}
