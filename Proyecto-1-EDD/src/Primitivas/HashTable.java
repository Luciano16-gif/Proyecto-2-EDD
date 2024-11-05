package Primitivas;

public class HashTable<K, V> {
    private class Entry<K, V> {
        K key;
        V value;
        Entry<K, V> next; // Para manejar colisiones usando encadenamiento

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private Lista<Entry<K, V>>[] table;
    private int capacity;
    private int size;

    @SuppressWarnings("unchecked")
    public HashTable() {
        capacity = 100; // Tamaño inicial de la tabla
        table = new Lista[capacity];
        for (int i = 0; i < capacity; i++) {
            table[i] = new Lista<>();
        }
        size = 0;
    }

    private int hash(K key) {
        return Math.abs(key.hashCode()) % capacity;
    }

    // Método para insertar una clave y un valor en la tabla hash
    public void put(K key, V value) {
        int index = hash(key);
        Lista<Entry<K, V>> bucket = table[index];

        // Verificar si la clave ya existe y actualizar el valor
        for (int i = 0; i < bucket.len(); i++) {
            Entry<K, V> entry = bucket.get(i);
            if (entry.key.equals(key)) {
                entry.value = value;
                return;
            }
        }

        // Si la clave no existe, agregar una nueva entrada
        Entry<K, V> newEntry = new Entry<>(key, value);
        bucket.append(newEntry);
        size++;
    }

    // Método para obtener el valor asociado a una clave
    public V get(K key) {
        int index = hash(key);
        Lista<Entry<K, V>> bucket = table[index];

        for (int i = 0; i < bucket.len(); i++) {
            Entry<K, V> entry = bucket.get(i);
            if (entry.key.equals(key)) {
                return entry.value;
            }
        }

        return null; // Clave no encontrada
    }

    // Método para obtener una lista de todas las claves
    public Lista<K> getKeys() {
        Lista<K> keys = new Lista<>();
        for (int i = 0; i < capacity; i++) {
            Lista<Entry<K, V>> bucket = table[i];
            for (int j = 0; j < bucket.len(); j++) {
                Entry<K, V> entry = bucket.get(j);
                keys.append(entry.key);
            }
        }
        return keys;
    }

    // Método para obtener el número de elementos en la tabla
    public int size() {
        return size;
    }

    // Método para verificar si la tabla está vacía
    public boolean isEmpty() {
        return size == 0;
    }

    // Método para eliminar una clave de la tabla
    public void remove(K key) {
        int index = hash(key);
        Lista<Entry<K, V>> bucket = table[index];

        for (int i = 0; i < bucket.len(); i++) {
            Entry<K, V> entry = bucket.get(i);
            if (entry.key.equals(key)) {
                bucket.remove(i);
                size--;
                return;
            }
        }
    }
}
