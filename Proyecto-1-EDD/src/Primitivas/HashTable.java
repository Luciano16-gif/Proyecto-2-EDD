package Primitivas;

/**

 * Esta clase define la clase HashTable con sus metodos, y atributos

 * @author: Ricardo Paez - Luciano Minardo - Gabriele Colarusso

 * @version: 4/11/2024

 */
public class HashTable<K, V> {
    private int size = 128; // Tamaño inicial de la tabla
    private Lista<NodoHash<K, V>>[] table;

    public HashTable() {
        table = new Lista[size];
        for (int i = 0; i < size; i++) {
            table[i] = new Lista<>();
        }
    }

    private int hash(K key) {
        return Math.abs(key.hashCode()) % size;
    }

    public void put(K key, V value) {
        int index = hash(key);
        Lista<NodoHash<K, V>> bucket = table[index];
        // Verificar si la clave ya existe y actualizar
        for (int i = 0; i < bucket.len(); i++) {
            NodoHash<K, V> nodo = bucket.get(i);
            if (nodo.key.equals(key)) {
                nodo.value = value;
                return;
            }
        }
        // Si no existe, agregar nuevo nodo
        bucket.append(new NodoHash<>(key, value));
    }

    public V get(K key) {
        int index = hash(key);
        Lista<NodoHash<K, V>> bucket = table[index];
        for (int i = 0; i < bucket.len(); i++) {
            NodoHash<K, V> nodo = bucket.get(i);
            if (nodo.key.equals(key)) {
                return nodo.value;
            }
        }
        return null; // No encontrado
    }

    // Clase interna para los nodos de la tabla hash
    private static class NodoHash<K, V> {
        K key;
        V value;

        public NodoHash(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
}
