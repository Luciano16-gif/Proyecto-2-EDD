package Primitivas;

/**
 * Clase que representa la HasHTable, donde tienen sus atributos y metodos
 *
 * @author: Ricardo Paez - Luciano Minardo - Gabriele Colarusso
 * 
 * @version 21/11/2024
 */

public class HashTable<K, V> {
    private static final int INITIAL_CAPACITY = 16;
    private Lista<Entry<K, V>>[] buckets;
    private int size;

    /**
     * Clase interna para representar pares clave-valor.
     */
    private static class Entry<K, V> {
        K key;
        V value;

        Entry(K key, V value){
            this.key = key;
            this.value = value;
        }
    }

    @SuppressWarnings("unchecked")
    public HashTable() {
        buckets = new Lista[INITIAL_CAPACITY];
        for(int i = 0; i < INITIAL_CAPACITY; i++) {
            buckets[i] = new Lista<>();
        }
        size = 0;
    }

    /**
     * Método para calcular el índice del bucket basado en la clave.
     */
    private int getBucketIndex(K key) {
        return Math.abs(key.hashCode()) % buckets.length;
    }

    /**
     * Método para añadir o actualizar un par clave-valor.
     */
    public void put(K key, V value) {
        int index = getBucketIndex(key);
        Lista<Entry<K, V>> bucket = buckets[index];

        for(int i = 0; i < bucket.len(); i++) {
            Entry<K, V> entry = bucket.get(i);
            if(entry.key.equals(key)) {
                entry.value = value;
                return;
            }
        }

        bucket.append(new Entry<>(key, value));
        size++;

        // Opcional: Redimensionar si se supera cierta carga
    }

    /**
     * Método para obtener el valor asociado a una clave.
     */
    public V get(K key) {
        int index = getBucketIndex(key);
        Lista<Entry<K, V>> bucket = buckets[index];

        for(int i = 0; i < bucket.len(); i++) {
            Entry<K, V> entry = bucket.get(i);
            if(entry.key.equals(key)) {
                return entry.value;
            }
        }

        return null;
    }

    /**
     * Método para verificar si una clave existe en la tabla.
     */
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    /**
     * Método para eliminar un par clave-valor basado en la clave.
     *
     * @param key Clave del par a eliminar.
     * @return true si la clave fue encontrada y eliminada, false en caso contrario.
     */
    public boolean remove(K key) {
        int index = getBucketIndex(key);
        Lista<Entry<K, V>> bucket = buckets[index];

        for(int i = 0; i < bucket.len(); i++) {
            Entry<K, V> entry = bucket.get(i);
            if(entry.key.equals(key)) {
                bucket.remove(i);
                size--;
                return true;
            }
        }

        return false;
    }

    /**
     * Método para obtener todas las claves de la tabla.
     *
     * @return Lista de todas las claves.
     */
    public Lista<K> keys() {
        Lista<K> allKeys = new Lista<>();
        for(int i = 0; i < buckets.length; i++) {
            Lista<Entry<K, V>> bucket = buckets[i];
            for(int j = 0; j < bucket.len(); j++) {
                allKeys.append(bucket.get(j).key);
            }
        }
        return allKeys;
    }

    /**
     * Método para obtener el tamaño de la tabla.
     *
     * @return Número de pares clave-valor en la tabla.
     */
    public int size() {
        return size;
    }
    
    /**
     * Método para obtener todas las claves de la tabla.
     * @return Lista de todas las claves.
     */
    public Lista<K> getKeys() {
        Lista<K> allKeys = new Lista<>();
        for(int i = 0; i < buckets.length; i++) {
            Lista<Entry<K, V>> bucket = buckets[i];
            for(int j = 0; j < bucket.len(); j++) {
                allKeys.append(bucket.get(j).key);
            }
        }
        return allKeys;
    }
    
    /**
     * Método para obtener todos los valores de la tabla.
     *
     * @return Lista de todos los valores.
     */
    public Lista<V> values() {
        Lista<V> allValues = new Lista<>();
        for (int i = 0; i < buckets.length; i++) {
            Lista<Entry<K, V>> bucket = buckets[i];
            Lista<Entry<K, V>>.ListaIterator iterator = bucket.iterator();
            while (iterator.hasNext()) {
                Entry<K, V> entry = iterator.next();
                allValues.append(entry.value);
            }
        }
        return allValues;
    }


}