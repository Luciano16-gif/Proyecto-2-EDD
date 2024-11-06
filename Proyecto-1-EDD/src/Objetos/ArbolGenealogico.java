package Objetos;

import Primitivas.HashTable;
import Primitivas.Lista;

/**
 * Clase que representa el árbol genealógico y maneja la construcción del mismo.
 *
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

            // Mapear el nombre completo al ID
            nombreAId.put(persona.getId(), persona.getId());

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
                        grafos.addArco1(nodoPadreOMadre.getPersona().getId(), persona.getId()); // Usamos los IDs únicos
                    } else {
                        // Si no encontramos al padre, podríamos crear un nuevo nodo
                        Persona nuevoPadre = new Persona(nombrePadreOMadre);
                        NodoArbol nuevoNodoPadre = new NodoArbol(nuevoPadre);
                        tablaPersonasPorId.put(nuevoPadre.getId(), nuevoNodoPadre);
                        nombreAId.put(nuevoPadre.getId(), nuevoPadre.getId());
                        grafos.addPersona(nuevoPadre);
                        grafos.addArco1(nuevoPadre.getId(), persona.getId()); // Usamos los IDs únicos
                    }
                }
            }

            // Establecer hijos
            Lista<String> nombresHijos = persona.getHijos();
            if (nombresHijos != null) {
                for (int j = 0; j < nombresHijos.len(); j++) {
                    String nombreHijo = nombresHijos.get(j);
                    String idHijo = resolverIdPorNombreHijo(nombreHijo, persona);

                    if (idHijo != null) {
                        NodoArbol nodoHijo = tablaPersonasPorId.get(idHijo);
                        nodoActual.agregarHijo(nodoHijo);
                        grafos.addArco1(persona.getId(), nodoHijo.getPersona().getId()); // Usamos los IDs únicos
                    } else {
                        // Si no encontramos al hijo, podríamos crear un nuevo nodo
                        Persona nuevoHijo = new Persona(nombreHijo);
                        NodoArbol nuevoNodoHijo = new NodoArbol(nuevoHijo);
                        tablaPersonasPorId.put(nuevoHijo.getId(), nuevoNodoHijo);
                        nombreAId.put(nuevoHijo.getId(), nuevoHijo.getId());
                        grafos.addPersona(nuevoHijo);
                        grafos.addArco1(persona.getId(), nuevoHijo.getId()); // Usamos los IDs únicos
                    }
                }
            }
        }
    }

    // Método para resolver el ID de una persona dado un nombre, incluyendo "Of his name"
    private String resolverIdPorNombre(String nombre) {
        // Intentar encontrar el ID usando el nombre tal cual
        if (nombreAId.get(nombre) != null) {
            return nombreAId.get(nombre);
        } else {
            // Si no se encuentra, intentar agregar posibles sufijos "First of his name", etc.
            String[] sufijos = {"First", "Second", "Third", "Fourth", "Fifth", "Sixth", "Seventh", "Eighth", "Ninth", "Tenth"};
            for (String sufijo : sufijos) {
                String posibleNombre = nombre + ", " + sufijo + " of his name";
                if (nombreAId.get(posibleNombre) != null) {
                    return nombreAId.get(posibleNombre);
                }
            }
        }
        return null;
    }

    // Método para resolver el ID de un hijo considerando el contexto del padre
    private String resolverIdPorNombreHijo(String nombreHijo, Persona padre) {
        // Obtener las claves (IDs) de todas las personas en tablaPersonasPorId
        Lista<String> listaIds = tablaPersonasPorId.getKeys();
        
        for (int i = 0; i < listaIds.len(); i++) {
            String idPersona = listaIds.get(i);
            Persona posibleHijo = tablaPersonasPorId.get(idPersona).getPersona();
            if (posibleHijo.getNombre().equals(nombreHijo) && posibleHijo.getBornTo().contains(padre.getId())) {
                return posibleHijo.getId();
            }
        }
        
        // Si no se encuentra, intentar con sufijos
        String[] sufijos = {"First", "Second", "Third", "Fourth", "Fifth"};
        for (String sufijo : sufijos) {
            String posibleNombre = nombreHijo + ", " + sufijo + " of his name";
            if (nombreAId.get(posibleNombre) != null) {
                Persona posibleHijo = tablaPersonasPorId.get(nombreAId.get(posibleNombre)).getPersona();
                if (posibleHijo.getBornTo().contains(padre.getId())) {
                    return posibleHijo.getId();
                }
            }
        }
        return null;
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
