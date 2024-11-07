package Objetos;

import Primitivas.Lista;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Edge;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;

/**
 * Clase para manejar grafos de personas y arcos.
 *
 * @version 4/11/2024
 */
public class Grafos {
    private Graph graph;

    /**
     * Constructor de la clase Grafos.
     */
    public Grafos() {
        this.graph = new SingleGraph("Grafo de Personas");
        // Configuración de estilos básicos
        graph.setAttribute("ui.stylesheet",
            "node {" +
            "   text-alignment: above;" +
            "   text-size: 14;" +
            "}" +
            "edge {" +
            "   arrow-shape: arrow;" +
            "   size: 2px;" +
            "}");
    }

    /**
     * Añade una persona como nodo en el grafo si no existe ya.
     *
     * @param persona Persona a añadir.
     */
    public void addPersona(Persona persona) {
        String id = persona.getId(); // Identificador único
        // Verifica si el nodo ya existe antes de agregar
        if (graph.getNode(id) == null) {
            graph.addNode(id); // Crea el nodo con el identificador único
            graph.getNode(id).setAttribute("ui.label", persona.getNombre());

            // Establecer el estilo del nodo (opcional)
            graph.getNode(id).setAttribute("ui.style", "fill-color: yellow; shape: circle; size: 15px;");
        } else {
            // Opcional: Actualizar el nodo si ya existe
            // Por ejemplo, actualizar el label si cambió
            graph.getNode(id).setAttribute("ui.label", persona.getNombre());
        }
    }

    /**
     * Añade un arco dirigido entre dos nodos si no existe ya.
     *
     * @param idPadre ID único del padre.
     * @param idHijo  ID único del hijo.
     */
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

    /**
     * Elimina una persona del grafo, incluyendo todos sus arcos conectados.
     *
     * @param id Identificador único de la persona a eliminar.
     */
    public void removerPersona(String id) {
        Node node = graph.getNode(id);
        if (node != null) {
            // Recopilar todos los arcos conectados al nodo
            Lista<Edge> arcosConectados = getEdgesConnectedToNode(node);

            // Eliminar los arcos recopilados
            for (int i = 0; i < arcosConectados.len(); i++) {
                graph.removeEdge(arcosConectados.get(i));
            }

            // Eliminar el nodo
            graph.removeNode(node);
            System.out.println("Nodo eliminado del grafo: " + id);
        }
    }

    /**
     * Recopila todos los arcos conectados a un nodo.
     *
     * @param node Nodo del cual se recopilarán los arcos.
     * @return Lista de arcos conectados al nodo.
     */
    private Lista<Edge> getEdgesConnectedToNode(Node node) {
        Lista<Edge> connectedEdges = new Lista<>();
        int totalEdges = graph.getEdgeCount();

        for (int i = 0; i < totalEdges; i++) {
            Edge edge = graph.getEdge(i);
            if (edge != null) { // Asegurarse de que el arco no sea null
                Node node0 = edge.getNode0();
                Node node1 = edge.getNode1();

                if (node0.equals(node) || node1.equals(node)) {
                    connectedEdges.append(edge);
                }
            }
        }

        return connectedEdges;
    }

    /**
     * Muestra el grafo utilizando GraphStream.
     */
    public void mostrarGrafo() {
        System.setProperty("org.graphstream.ui", "swing");

        // Mostrar el grafo
        Viewer viewer = graph.display();
        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.CLOSE_VIEWER);
    }
}
