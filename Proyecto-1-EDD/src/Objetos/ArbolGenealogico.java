package Objetos;

import Primitivas.HashTable;
import Primitivas.Lista;

/**
 * Clase que representa el árbol genealógico y maneja la construcción del mismo.
 *
 * @author ...
 * @version 4/11/2024
 */
public class ArbolGenealogico {
    private HashTable<String, NodoArbol> tablaPersonasPorId; // Mapea ID de persona a NodoArbol
    private HashTable<String, String> nombreAId; // Mapea nombres (incluyendo ofHisName) a IDs

    public ArbolGenealogico() {
        tablaPersonasPorId = new HashTable<>();
        nombreAId = new HashTable<>();
    }

    public void construirArbol(Lista<Persona> personas, Grafos grafos) {
        // Crear nodos y almacenarlos en la tabla hash
        for (int i = 0; i < personas.len(); i++) {
            Persona persona = personas.get(i);
            NodoArbol nodo = new NodoArbol(persona);
            tablaPersonasPorId.put(persona.getId(), nodo);

            // Mapear el nombre canónico al ID
            nombreAId.put(persona.getNombre(), persona.getId());

            // Mapear combinaciones de nombre y ofHisName al ID
            if (!persona.getOfHisName().isEmpty()) {
                String clave = persona.getNombre() + ", " + persona.getOfHisName() + " of his name";
                nombreAId.put(clave, persona.getId());
            }

            // Mapear nombres alternativos al ID
            Lista<String> nombresAlternativos = persona.getNombresAlternativos();
            for (int j = 0; j < nombresAlternativos.len(); j++) {
                String nombreAlternativo = nombresAlternativos.get(j);
                nombreAId.put(nombreAlternativo, persona.getId());
            }

            grafos.addPersona(persona); // Añadir persona al grafo
        }

        // Establecer relaciones padre-hijo
        for (int i = 0; i < personas.len(); i++) {
            Persona persona = personas.get(i);
            NodoArbol nodoActual = tablaPersonasPorId.get(persona.getId());

            // Establecer padres
            Lista<String> padres = persona.getBornTo();
            if (padres != null && padres.len() > 0) {
                for (int j = 0; j < padres.len(); j++) {
                    String nombrePadreOMadre = padres.get(j);
                    String idPadreOMadre = resolverIdPorNombre(nombrePadreOMadre);

                    if (idPadreOMadre != null) {
                        NodoArbol nodoPadreOMadre = tablaPersonasPorId.get(idPadreOMadre);
                        nodoPadreOMadre.agregarHijo(nodoActual);
                        grafos.addArco1(nodoPadreOMadre.getPersona().getNombre(), persona.getNombre()); // Usamos los nombres canónicos
                    } else {
                        // Si no encontramos al padre, podríamos crear un nuevo nodo
                        Persona nuevoPadre = new Persona(nombrePadreOMadre);
                        NodoArbol nuevoNodoPadre = new NodoArbol(nuevoPadre);
                        tablaPersonasPorId.put(nuevoPadre.getId(), nuevoNodoPadre);
                        nombreAId.put(nuevoPadre.getNombre(), nuevoPadre.getId());
                        grafos.addPersona(nuevoPadre);
                        grafos.addArco1(nuevoPadre.getNombre(), persona.getNombre());
                    }
                }
            }

            // Establecer hijos
            Lista<String> nombresHijos = persona.getHijos();
            if (nombresHijos != null) {
                for (int j = 0; j < nombresHijos.len(); j++) {
                    String nombreHijo = nombresHijos.get(j);
                    String idHijo = resolverIdPorNombre(nombreHijo);

                    if (idHijo != null) {
                        NodoArbol nodoHijo = tablaPersonasPorId.get(idHijo);
                        nodoActual.agregarHijo(nodoHijo);
                        grafos.addArco1(persona.getNombre(), nodoHijo.getPersona().getNombre());
                    } else {
                        // Si no encontramos al hijo, podríamos crear un nuevo nodo
                        Persona nuevoHijo = new Persona(nombreHijo);
                        NodoArbol nuevoNodoHijo = new NodoArbol(nuevoHijo);
                        tablaPersonasPorId.put(nuevoHijo.getId(), nuevoNodoHijo);
                        nombreAId.put(nuevoHijo.getNombre(), nuevoHijo.getId());
                        grafos.addPersona(nuevoHijo);
                        grafos.addArco1(persona.getNombre(), nuevoHijo.getNombre());
                    }
                }
            }
        }
    }

    // Método para resolver el ID de una persona dado un nombre, intentando normalizar si es necesario
    private String resolverIdPorNombre(String nombre) {
        String id = nombreAId.get(nombre);
        if (id == null) {
            nombre = normalizarNombre(nombre);
            id = nombreAId.get(nombre);
        }
        return id;
    }

    // Método para normalizar nombres eliminando descriptores comunes y manejando "Of his name"
    private String normalizarNombre(String nombre) {
        // Intentar separar el nombre y "Of his name"
        if (nombre.contains(",")) {
            String[] partes = nombre.split(",");
            String nombreBase = partes[0].trim();
            String ofHisName = partes[1].trim();
            nombre = nombreBase + ", " + ofHisName;
        } else {
            nombre = nombre.trim();
        }
        return nombre;
    }

    public NodoArbol buscarPorId(String id) {
        return tablaPersonasPorId.get(id);
    }

    public NodoArbol buscarPorNombre(String nombre) {
        String id = nombreAId.get(nombre);
        if (id != null) {
            return tablaPersonasPorId.get(id);
        }
        return null;
    }
}
