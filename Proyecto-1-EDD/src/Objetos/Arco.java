package Objetos;

/**
 * Esta clase define el objeto Arco, con diferentes atributos y metodos.
 * 
 * @author: Ricardo Paez - Luciano Minardo - Gabriele Colarusso

 * @version: 15/10/2024
 */
public class Arco implements Comparable<Arco> {
    private int src;
    private int dest;
    private boolean nodoEstacionVisitado = false;
    private int distancia;

    /**
     *Construye un nuevo objeto Arco con los nodos de origen, destino y distancia dados
     * 
     * @param src el nodo de origen 
     * @param dest el nodo de destino
     * @param distancia distancia entre los nodos de origen y destino
     * @throws IllegalArgumentException si cualquiera de los parámetros son negativos
     */
    public Arco(int src, int dest, int distancia) {
        if (src < 0 || dest < 0) {
            throw new IllegalArgumentException("Los nodos no pueden tener valores negativos.");
        }
        if (distancia < 0) {
            throw new IllegalArgumentException("La distancia no puede ser negativa.");
        }
        this.src = src;
        this.dest = dest;
        this.distancia = distancia;
    }

    /**
     *Compara este arco con el arco especificado para el orden
     * 
     * @param compareArco el arco a comparar
     * @return un numero entero negativo, cero, o un numero entero positivo segun este arco sea menor, igual o maayor que el arco especificado
     */
    @Override
    public int compareTo(Arco compareArco) {
        return Integer.compare(this.distancia, compareArco.distancia);
    }

    /**
     *Devuelve el nodo de origen de este arco
     * 
     * @return el nodo de origen
     */
    public int getSrc() {
        return src;
    }

    /**
     *Establece el nodo de origen de este arco
     * 
     * @param src el nodo de origen
     */
    public void setSrc(int src) {
        this.src = src;
    }

    /**
     *Devuelve el nodo de destino de este arco
     * 
     * @return el nodo de destino
     */
    public int getDest() {
        return dest;
    }

    /**
     *Establece el nodo de destino de este arco
     * 
     * @param dest el nodo de destino
     */
    public void setDest(int dest) {
        this.dest = dest;
    }

    /**
     *Marca este arco como visitado
     */
    public void marcarComoVisitado() {
        this.nodoEstacionVisitado = true;
    }

    /**
     *Devuelve true si este arco ha sido vvisistado; de lo contrario, devuelve falso
     * 
     * @return true si el arco ha sido visitado, falso si es lo contrario 
     */
    public boolean isNodoEstacionVisitado() {
        return nodoEstacionVisitado;
    }

    /**
     *Devuelve una representacion de cadena de este arco
     * 
     * @return una representacion de cadenade este arco
     */
    @Override
    public String toString() {
        return "Arco{" +
               "src=" + src +
               ", dest=" + dest +
               ", distancia=" + distancia +
               ", visitado=" + nodoEstacionVisitado +
               '}';
    }
}
