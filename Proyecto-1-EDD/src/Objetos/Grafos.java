package Objetos;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;
import Primitivas.Lista;

/**
 * Clase para manejar grafos de estaciones y arcos.
 * 
 * @author: Ricardo Paez - Luciano Minardo - Gabriele Colarusso
 * 
 * @version: 4/11/2024
 */
public class Grafos {
    private Lista<Arco> arcos;
    private Lista<Persona> personas;
    private Graph graph;

    /**
     *Constructor de la clase Grafos
     */
    public Grafos() {
        arcos = new Lista<>();
        personas = new Lista<>();
    }

    /**
     *Añade un arco al grafo entre dos estaciones
     * 
     * @param src Indice de la estacion de origen
     * @param dest Indice de la estacion de destino
     */
    public void addArco(int src, int dest) {
        if (!existeArco(src, dest) && !existeArco(dest, src)) {
            arcos.append(new Arco(src, dest, 1));
        }
    }

    /**
     * Verifica si existe un arco entre dos estaciones
     * 
     * @param src Índice de la estación de origen
     * @param dest Índice de la estación de destino
     * @return true si existe un arco entre las estaciones, false en caso contrario
     */
    private boolean existeArco(int src, int dest) {
        for (int i = 0; i < arcos.len(); i++) {
            Arco arco = arcos.get(i);
            if ((arco.getSrc() == src && arco.getDest() == dest) || (arco.getSrc() == dest && arco.getDest() == src)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Añade una estación al grafo
     * 
     * @param estacion Objeto Estacion que se va a añadir al grafo
     */
    public void addPersona(Persona estacion) {
        personas.append(estacion);
    }

    /**
     * Muestra el grafo de estaciones y arcos en una ventana de visualización.
     * 
     * @param estaciones Lista de estaciones que se van a mostrar en el grafo.
     */
    public void mostrarGrafo(Lista<Persona> estaciones) {
        this.personas = estaciones;
        System.setProperty("org.graphstream.ui", "swing");
        graph = new SingleGraph("Grafo Metro");

        // Agregar nodos al grafo
        for (int i = 0; i < estaciones.len(); i++) {
            Persona estacion = estaciones.get(i);
            String nodeId = String.valueOf(i);
            graph.addNode(nodeId);
            //graph.getNode(nodeId).setAttribute("ui.label", estacion.getNombre());

            // Si la estación pertenece a múltiples líneas, asignar un color especial
            //String nodeColor = estacion.getColor();
            //if (estacion.getLineas().len() > 1) {
            //    nodeColor = "Gray"; // Color para intersecciones
            //}

            //graph.getNode(nodeId).setAttribute("ui.style", "fill-color: " + nodeColor + "; shape: circle; size: 15px;");
        }

        // Agregar arcos al grafo
        for (int i = 0; i < arcos.len(); i++) {
            Arco arco = arcos.get(i);
            int src = arco.getSrc();
            int dest = arco.getDest();
            String arcoId;

            // Ordenar los nodos para el identificador de la arista
            if (src < dest) {
                arcoId = src + "-" + dest;
            } else {
                arcoId = dest + "-" + src;
                // Intercambiar src y dest para asegurar que la arista se agrega correctamente
                int temp = src;
                src = dest;
                dest = temp;
            }

            if (graph.getEdge(arcoId) == null) { // Evitar duplicados en GraphStream
                graph.addEdge(arcoId, String.valueOf(src), String.valueOf(dest), false);
                graph.getEdge(arcoId).setAttribute("ui.style", "fill-color: gray;");
            }
        }
        Viewer viewer = graph.display();
        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.CLOSE_VIEWER); 
    }


    /**
     * Obtiene una lista de índices de estaciones adyacentes a una estación específica.
     * 
     * @param nodo Índice de la estación para la cual se obtendrán las estaciones adyacentes.
     * @return Lista de índices de estaciones adyacentes.
     */
    public Lista<Integer> getAdyacentes(int nodo) {
        Lista<Integer> adyacentes = new Lista<>();
        for (int i = 0; i < arcos.len(); i++) {
            Arco arco = arcos.get(i);
            if (arco.getSrc() == nodo) {
                adyacentes.append(arco.getDest());
            } else if (arco.getDest() == nodo) {
                adyacentes.append(arco.getSrc());
            }
        }
        return adyacentes;
    }

    /**
     * Resalta en el grafo las estaciones que son sucursales y las que están cubiertas por al menos una sucursal.
     * 
     * @param coberturasSucursales Lista de listas de índices de estaciones que están cubiertas por cada sucursal.
     * @param estaciones Lista de estaciones que se van a resaltar en el grafo.
     */
    public void resaltarEstaciones(Lista<Lista<Integer>> coberturasSucursales, Lista<Persona> personas) {
        // Crear un arreglo para rastrear las estaciones cubiertas y sucursales
        boolean[] estacionesCubiertas = new boolean[personas.len()];
        boolean[] estacionesSucursal = new boolean[personas.len()];

        // Marcar las estaciones que son sucursales
        //for (int i = 0; i < personas.len(); i++) {
        //    if (personas.get(i).esSucursal()) {
        //        estacionesSucursal[i] = true;
        //}
        //}

        // Marcar las estaciones cubiertas por las sucursales
        for (int s = 0; s < coberturasSucursales.len(); s++) {
            Lista<Integer> cobertura = coberturasSucursales.get(s);
            for (int i = 0; i < cobertura.len(); i++) {
                int indiceEstacion = cobertura.get(i);
                estacionesCubiertas[indiceEstacion] = true;
            }
        }

        // Actualizar los estilos de las estaciones
        for (int i = 0; i < personas.len(); i++) {
            String nodeId = String.valueOf(i);
            Persona estacion = personas.get(i);

            if (estacionesSucursal[i]) {
                // Estación que es una sucursal
                graph.getNode(nodeId).setAttribute("ui.style", "fill-color: red; shape: box; size: 20px;");
            } else if (estacionesCubiertas[i]) {
                // Estación cubierta por al menos una sucursal
                graph.getNode(nodeId).setAttribute("ui.style", "fill-color: gold; shape: circle; size: 20px;");
            } else {
                // Estación no cubierta
                //String nodeColor = estacion.getColor();
                //if (estacion.getLineas().len() > 1) {
                //    nodeColor = "gray"; // Color para intersecciones
                //}
                //graph.getNode(nodeId).setAttribute("ui.style", "fill-color: " + nodeColor + "; shape: circle; size: 15px;");
            }
        }
    }
}
