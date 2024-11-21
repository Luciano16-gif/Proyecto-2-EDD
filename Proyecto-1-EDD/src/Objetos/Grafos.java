package Objetos;

import Primitivas.Lista;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Edge;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.view.Viewer;

/**
 * Clase para manejar grafos de personas y arcos.
 *
 * @author: Ricardo Paez - Luciano Minardo - Gabriele Colarusso
 * 
 * 
 * @version 21/11/2024
 */
public class Grafos {
    private Graph graph;
    private ArbolGenealogico arbolGenealogico;

    public Grafos() {
        this.graph = new MultiGraph("Árbol Genealógico");
        graph.setAttribute("ui.stylesheet",
            "node {" +
            "   size: 30px;" +
            "   shape: circle;" +
            "   fill-color: yellow;" +
            "   stroke-mode: plain;" +
            "   stroke-color: black;" +
            "   stroke-width: 1px;" +
            "   text-alignment: at-right;" +
            "   text-offset: 5px, 0px;" +
            "   text-size: 14;" +
            "}" +
            "edge {" +
            "   shape: line;" +
            "   size: 2px;" +
            "   fill-color: black;" +
            "   arrow-size: 8px, 6px;" +
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
            System.out.println("Persona añadida al grafo: " + id);
        } else {
            // Opcional: Actualizar el nodo si ya existe
            // Por ejemplo, actualizar el label si cambió
            graph.getNode(id).setAttribute("ui.label", persona.getNombre());
            System.out.println("Persona ya existe en el grafo: " + id);
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
                System.out.println("Arco añadido al grafo: " + idPadre + " -> " + idHijo);
            } else {
                System.out.println("Arco ya existe en el grafo: " + idPadre + " -> " + idHijo);
            }
        } else {
            System.out.println("Error: Uno de los nodos no existe en el grafo. Padre: " + idPadre + ", Hijo: " + idHijo);
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
        int degree = node.getDegree();
        for (int i = 0; i < degree; i++) {
            Edge edge = node.getEdge(i);
            connectedEdges.append(edge);
        }
        return connectedEdges;
    }


    /**
     * Muestra el árbol genealógico en una ventana gráfica.
     */
    public void mostrarArbol(ArbolGenealogico arbolGenealogico) {
        System.setProperty("org.graphstream.ui", "swing");
        Clicks visualizador = new Clicks(graph, arbolGenealogico);
    }

    /**
     * Obtiene una lista con los IDs de todos los nodos en el grafo.
     *
     * @return Lista de IDs de los nodos.
     */
    public Lista<String> getIdsNodos() {
        Lista<String> idsNodos = new Lista<>();
        for (Node node : graph) {  // Itera sobre todos los nodos en el grafo
            idsNodos.append(node.getId());  // Añade el ID de cada nodo a la lista
        }
        return idsNodos;
    }
}
