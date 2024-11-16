
package Primitivas;

/**

 * Esta clase define el objeto Nodo, con la cual tiene diferentes atributos y funciones que lo definen

 * @author: Ricardo Paez - Luciano Minardo - Gabriele Colarusso

 * @version: 4/11/2024

 */

public class Nodo<T> {
    private Nodo next;
    private T data;


    //Campos de la clase
    //constructor

    /**
     *Constructor de la clase Nodo. Inicializa un nuevo nodo con el dato proporcionado
     * 
     * @param data Dato a almacenar en el nodo
     */
    public Nodo(T data){
        this.next = null;
        this.data = data;
    }

    //getter del siguiente nodo
    /**
     * Obtiene el siguiente nodo en la lista.
     *
     * @return Siguiente nodo en la lista.
     */
    public Nodo getNext() {
        return next;
    }

    //setter del siguiente nodo
    /**
     * Establece el siguiente nodo en la lista.
     *
     * @param next Nuevo siguiente nodo en la lista.
     */
    public void setNext(Nodo next) {
        this.next = next;
    }

    //getter de la informacion
    /**
     * Obtiene el dato almacenado en el nodo.
     *
     * @return Dato almacenado en el nodo.
     */
    public T getData() {
        return data;
    }

    //setter de la informacion
    /**
     * Establece el dato almacenado en el nodo.
     *
     * @param data Nuevo dato a almacenar en el nodo.
     */
    public void setData(T data) {
        this.data = data;
    }
}


