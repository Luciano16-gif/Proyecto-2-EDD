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
    public Lista<Persona> personas;
    private Graph graph;

    /**
     *Constructor de la clase Grafos
     */
    public Grafos() {
        arcos = new Lista<>();
        personas = new Lista<>();
        this.graph = new SingleGraph("Grafo"); 
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
    
    public void addArco1(String nombrePadre, String nombreHijo) {
    // Asegúrate de que el grafo contenga ambos nodos
    if (graph.getNode(nombrePadre) != null && graph.getNode(nombreHijo) != null) {
        // Genera un ID único para el arco usando el nombre de los nodos
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
    public void addPersona(Persona persona) {
    String nombre = persona.getNombre();
    // Verifica si el nodo ya existe antes de agregar
    if (graph.getNode(nombre) == null) {
        graph.addNode(nombre); // Crea el nodo con el nombre de la persona
        personas.append(persona); // Agrega la persona a la lista de personas
    } else {
        // Si ya existe, podrías optar por actualizar algunos atributos de la persona existente
        // o simplemente dejarlo como está, dependiendo de tus necesidades
        System.out.println("La persona " + nombre + " ya existe en el grafo.");
    }
}


    /**
     * Muestra el grafo de estaciones y arcos en una ventana de visualización.
     * 
     * @param estaciones Lista de estaciones que se van a mostrar en el grafo.
     */
    /**
 * Muestra el grafo de personas y sus conexiones en una ventana de visualización.
 */
public void mostrarGrafo() {
    System.setProperty("org.graphstream.ui", "swing");
    graph = new SingleGraph("Grafo de Personas");

    // Agregar nodos al grafo
    for (int i = 0; i < personas.len(); i++) {
        Persona persona = personas.get(i);
        String nombre = persona.getNombre();
        
        // Crea el nodo con el nombre de la persona
        graph.addNode(nombre);
        graph.getNode(nombre).setAttribute("ui.label", nombre);

        // Establecer el color de los nodos
        graph.getNode(nombre).setAttribute("ui.style", "fill-color: blue; shape: circle; size: 15px;");
    }

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
                graph.getEdge(arcoId).setAttribute("ui.style", "fill-color: gray;");
            }
        } else {
            System.out.println("Índice fuera de rango para los arcos: " + arco.getSrc() + ", " + arco.getDest());
        }
    }

    // Mostrar el grafo
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
                graph.getNode(nodeId).setAttribute("ui.style", "fill-color: " + "; shape: circle; size: 15px;");
            }
        }
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
