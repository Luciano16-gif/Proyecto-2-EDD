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
        if (graph.getNode(id) == null) {
            graph.addNode(id); // Crea el nodo con el identificador único
            graph.getNode(id).setAttribute("ui.label", persona.getNombre());
            graph.getNode(id).setAttribute("ui.style", "fill-color: yellow; shape: circle; size: 15px;");
        } else {
            System.out.println("La persona con ID " + id + " ya existe en el grafo.");
        }
    }

    public void addArco1(String idPadre, String idHijo) {
        if (graph.getNode(idPadre) != null && graph.getNode(idHijo) != null) {
            String edgeId = idPadre + "-" + idHijo;
            if (graph.getEdge(edgeId) == null) {
                graph.addEdge(edgeId, idPadre, idHijo, true); // true para arco dirigido
                graph.getEdge(edgeId).setAttribute("ui.style", "fill-color: red;");
            }
        }
    }

    public boolean estaConectado(String idPadre, String idHijo) {
        // Genera el ID del arco dirigido para buscar la conexión
        String edgeId = idPadre + "-" + idHijo;
        return graph.getEdge(edgeId) != null;
    }

    public void mostrarGrafo() {
        System.setProperty("org.graphstream.ui", "swing");
        Viewer viewer = graph.display();
        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.CLOSE_VIEWER);
    }
}
