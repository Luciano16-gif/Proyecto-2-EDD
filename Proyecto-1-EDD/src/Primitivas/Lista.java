package Primitivas;

/**
 * Clase que representa la clase lista.
 *
 * @author Luciano Minardo, Ricardo Paez y Gabriele Colarusso
 * 
 * @version 4/11/2024
 * 
 */


public class Lista<T> {
    private class Nodo<T> {
        T dato;
        Nodo<T> siguiente;

        public Nodo(T dato) {
            this.dato = dato;
            this.siguiente = null;
        }
    }

    private Nodo<T> cabeza;
    private int size;

    public Lista() {
        this.cabeza = null;
        this.size = 0;
    }
    
    // Crear el iterador manual
    public class ListaIterator {
        private Nodo<T> actual;

        public ListaIterator() {
            this.actual = cabeza; // Comienza desde la cabeza
        }

        public boolean hasNext() {
            return actual != null;
        }

        public T next() {
            if (!hasNext()) {
                throw new IllegalStateException("No hay más elementos.");
            }
            T dato = actual.dato;
            actual = actual.siguiente;
            return dato;
        }
    }

    // Método para devolver el iterador
    public ListaIterator iterator() {
        return new ListaIterator();
    }

    public Lista<String> getKeys() {
        Lista<String> keys = new Lista<>();
        Nodo<T> actual = cabeza;
        while (actual != null) {
            keys.append((String) actual.dato);  // Suponiendo que los datos en la lista son de tipo String
            actual = actual.siguiente;
        }
        return keys;
    }
    
    /**
     * Verifica si la lista está vacía.
     * @return true si la lista está vacía, false de lo contrario.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    // Añadir un elemento al final de la lista
    public void append(T dato) {
        Nodo<T> nuevoNodo = new Nodo<>(dato);
        if (cabeza == null) {
            cabeza = nuevoNodo;
        } else {
            Nodo<T> actual = cabeza;
            while (actual.siguiente != null) {
                actual = actual.siguiente;
            }
            actual.siguiente = nuevoNodo;
        }
        size++;
    }

    // Obtener el tamaño de la lista
    public int len() {
        return size;
    }

    public int getSize() {
        return size;
    }

    // Obtener el elemento en una posición dada
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Índice fuera de rango");
        }
        Nodo<T> actual = cabeza;
        for (int i = 0; i < index; i++) {
            actual = actual.siguiente;
        }
        return actual.dato;
    }

    // Verificar si un elemento está en la lista
    public boolean contains(T dato) {
        Nodo<T> actual = cabeza;
        while (actual != null) {
            if (actual.dato.equals(dato)) {
                return true;
            }
            actual = actual.siguiente;
        }
        return false;
    }

    // Eliminar un elemento en una posición dada
    public void remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Índice fuera de rango");
        }
        if (index == 0) {
            cabeza = cabeza.siguiente;
        } else {
            Nodo<T> actual = cabeza;
            for (int i = 0; i < index - 1; i++) {
                actual = actual.siguiente;
            }
            actual.siguiente = actual.siguiente.siguiente;
        }
        size--;
    }
    

    // Imprimir la lista (opcional)
    public void printList() {
        Nodo<T> actual = cabeza;
        while (actual != null) {
            System.out.print(actual.dato + " -> ");
            actual = actual.siguiente;
        }
        System.out.println("null");
    }
    
    @Override
public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("[");
    for (int i = 0; i < this.getSize(); i++) {
        sb.append(this.get(i));
        if (i < this.getSize() - 1) {
            sb.append(", ");
        }
    }
    sb.append("]");
    return sb.toString();
}

}
