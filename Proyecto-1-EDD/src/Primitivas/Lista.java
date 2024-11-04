package Primitivas;

/**

 * Esta clase define el objeto Lista, con la cual tiene diferentes atributos y funciones que lo definen

 * @author: Ricardo Paez - Luciano Minardo - Gabriele Colarusso

 * @version: 4/11/2024

 */

public class Lista<T> {
    private Nodo head;
    private Nodo tail;
    private int size;
    //Campos de la clase
    //constructor
    /**
     *Constructor de la clase Lista. Inicializa la lista vacia
     */
    public Lista() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }
    //getter
    /**
     *Obtiene el nodo cabeza de la lista
     * @return Nodo cabeza de la lista
     */
    public Nodo getHead() {
        return head;
    }

    //setter
    /**
     *Establece el nodo de cabeza de la lista
     * @param head Nuevo nodo cabeza de la lista
     */
    public void setHead(Nodo head) {
        this.head = head;
    }

    //getter
    /**
     *Obtiene el nodo cola de la lista
     * @return Nodo cola de la lista
     */
    public Nodo getTail() {
        return tail;
    }

    //setter
    /**
     *Establece el nodo cola de lista
     * @param tail Nuevo nodo cola de la lista
     */
    public void setTail(Nodo tail) {
        this.tail = tail;
    }

    //getter
    /**
     *Obtiene el tamaño de la lista
     * @return Tamaño de la lista
     */
    public int getSize() {
        return size;
    }

    //setter

    /**
     *Establece el tamaño de la lista
     * @param size Nuevo tamaño de la lista
     */
    public void setSize(int size) {
        this.size = size;
    }
    
    //funcion de la clase Lista para conocer si lita esta vacia

    /**
     *Verifica  si la lista esta vacia
     * @return true si la lista esta vacia, false en caso contrario
     */
    public boolean isEmpty(){
        return head == null;
    }
    
    //funcion de la clase Lista para conocer tamaño lista
    /**
     *Obtiene el tamaño de la lista
     * @return Tamaño de la lista
     */
    public int len(){
        return getSize();
    }
    
    //metodo de la clase Lista para borrar lista
    /**
     *Borra la lista, estableciendo la cabeza, cola y tamaño a 0
     */
    public void delete(){
        head = null;
        tail = null;
        size = 0;
    }
    
    //metodo de la clase Lista para agregar datos
    /**
     *Agregar un nuevo al final de la lista con el dato proporcionado
     * @param data Dato a agregar a la lista
     */
    public final void append(T data){
        Nodo newNode = new Nodo(data);
        if(isEmpty() == true){
            head = newNode;
            tail = newNode;
            size++;
        }
        else{
            tail.setNext(newNode);
            tail = newNode;
            size++;
        }
    }
    
    //metodo de la clase Lista para eliminar y devolver como variable
    /**
     *Elimina el nodo en la posisicon proporcionada y reduce el tamaño de la lista en 1
     * @param position Posicion del nodo a eliminar
     */
    public void pop(int position){
        Nodo pointer = head;
        if(position == 0){
            head = head.getNext();
            size--;
        }
        else if(position == len()-1){
            for(int x = 2; x < len(); x++){
                pointer = pointer.getNext();
            }
            pointer.setNext(null);
            tail = pointer;
            size--;
        }
        else{
            for(int x = 0; x < position-1; x++){
                pointer = pointer.getNext();
            }
            pointer.setNext(pointer.getNext().getNext());
            size--;
        }
    }
    
    //funcion de la clase Lista para buscar
    /**
     *Busca un dato en la lista y devuelve su posicion
     * @param data Dato a buscar en la lista
     * @return Posisicon del dato en la lista, o 0 si no se encuentra
     */
    public int find(T data){
        if(head.getData() == data){
            return 0;
        }
        else if(tail.getData() == data){
            return size-1;
        }
        else{
            Nodo pointer = head;
            for(int x = 0; x < len(); x++){
                if(pointer.getData() == data){
                    return x;
                }
                pointer = pointer.getNext();
            }
        }
        return 0;
    } 
    
    //metodo de la clase Lista para pre agregar
    /**
     *Agrega un nuevo nodo al principio de la lista con el dato proporcionado
     * @param data Dato a agregar al principio de la lista
     */
    public void preappend(T data){
        Nodo newNode = new Nodo(data);
        if(isEmpty() == true){
            head = newNode;
            tail = newNode;
            size++;
        }
        else{
            newNode.setNext(head);
            head = newNode;
            size++;
        }
    }
    
    //funcion de la clase Lista para conocer puntero
    /**
     *Obtiene el dato del nodo en la posicion proporcionada
     * @param position Posicion del nodo
     * @return Dato del nodo en la posicion proporcionada, o null si la posicion es invalida
     */
    public T get(int position){
        Nodo pointer = head;
        if(position < 0 || position >= len()){
            return null;
        }
        else if(position == 0){
            return (T) head.getData();
        }
        else if(position == len()-1){
            return (T) tail.getData();
        }
        else{
            for(int x = 1; x <= position; x++){
                pointer = pointer.getNext();
            }
            return (T) pointer.getData();
        }
    }
    
    //metodo de la clase Lista para insertar nodos
    /**
     *Inserta un nuevo nodo en la posicion proporcionada con el dato proporcionado
     * @param position Posicion donde insetar el nuevo nodo
     * @param data DAto a insertar en el nuevo nodo
     */
    public void insert(int position, T data){
        boolean run = true;
        Nodo newNode = new Nodo(data);
        while(run == true){
            if(position < 0 || position >= len()){
                run = false;
            }
            else{
                if(position == 0){
                    preappend(data);
                    run = false;
                }
                else{
                    Nodo pointer = head;
                    for(int x = 1; x < position; x++){
                        pointer = pointer.getNext();
                    }
                    newNode.setNext(pointer.getNext());
                    pointer.setNext(newNode);
                    size++;  
                    run = false;
                }
            }
        }
    }   

    //metodo de la clase Lista para reemplazar informacion
    /**
     * Reemplaza el dato del nodo en la posición proporcionada con el nuevo dato proporcionado.
     *
     * @param position Posición del nodo a reemplazar.
     * @param data Nuevo dato para el nodo.
     */
    public void replace(int position, T data){
        boolean run = true;
 
        while(run == true){
            if(position < 0 || position >= len()){
                run = false;
            }
            else{
                if(position == 0){
                    head.setData(data);
                    run = false;
                }
                else if(position == len()-1){
                    tail.setData(data);
                    run = false;
                }
                else{
                    Nodo pointer = head;
                    for(int x = 0; x < position; x++){
                        pointer = pointer.getNext();
                    }
                    pointer.setData(data);
                    run = false;
                }
            }
        }
    }    
    
    //funcion de la clase Lista para conocer existencia de un nodo
    /**
     *Verifica si un dato existe en la lista.
     * @param data Dato a buscar en la lista
     * @return true si el dato existe en la lista, false en caso contrario
     */
     public boolean exist(T data) {
        Nodo<T> current = head;
        while (current != null) { // Verifica si current no es null
            if (current.getData().equals(data)) {
                return true;
            }
            current = current.getNext();
        }
        return false;
    }

    // Función para obtener la posición de un dato en la lista
    /**
     * Obtiene la posición del dato en la lista.
     * @param data Dato a buscar en la lista.
     * @return Posición del dato en la lista, o -1 si no se encuentra.
     */
    public int indexOf(T data) {
        Nodo<T> pointer = head;
        int index = 0;
        while (pointer != null) {
            if (pointer.getData().equals(data)) {
                return index;
            }
            pointer = pointer.getNext();
            index++;
        }
        return -1; // No encontrado
    }
    
    // Obtener el índice del elemento, si el índice es válido elimina el nodo en el índice encontrado
    /**
     * Elimina el primer nodo con el dato proporcionado de la lista.
     *
     * @param data Dato del nodo a eliminar.
     */
    public void remove(T data) {
        int index = indexOf(data); 
        if (index != -1) { 
            pop(index); 
        }
    }
    
    
    // Función para verificar la existencia de un dato distinto en la lista
    /**
     * Verifica si un dato distinto al proporcionado existe en la lista.
     *
     * @param data Dato a comparar con los demás datos en la lista.
     * @return true si un dato distinto existe en la lista, false en caso contrario.
     */
    public boolean existenciaDistinta(String data) {
    if (head == null) { // Comprueba si la lista está vacía
        return false;
    }
    Nodo current = head;
    while (current != null) {
        if (current.getData().equals(data)) {
            return true;
        }
        current = current.getNext();
    }
    return false;
}
}