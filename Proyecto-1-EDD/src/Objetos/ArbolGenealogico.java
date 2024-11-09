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
    private HashTable<String, String> nombreAId; // Mapea nombres (incluyendo apodos y alias únicos) a IDs
    private Lista<Persona> listaPersonas; // Lista de todas las personas

    public ArbolGenealogico() {
        tablaPersonasPorId = new HashTable<>();
        nombreAId = new HashTable<>();
        listaPersonas = new Lista<>();
    }

    /**
     * Extrae el primer nombre de un nombre completo.
     * 
     * @param nombreCompleto Nombre completo (ej: "Ormund Baratheon")
     * @return Primer nombre (ej: "Ormund")
     */
    public String extraerPrimerNombre(String nombreCompleto) {
        if (nombreCompleto == null || nombreCompleto.isEmpty()) {
            return "";
        }
    
        // Split the full name into parts
        String[] parts = nombreCompleto.split(" ");
    
        // If only one word, return it
        if (parts.length <= 1) {
            return nombreCompleto;
        }

        // Build everything except the last part
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++) {
            result.append(parts[i]);
            // Add space if it's not the last part
            if (i < parts.length - 2) {
                result.append(" ");
            }
        }
        //System.out.println(result.toString());    
        return result.toString();
    }

    /**
     * Mapea alias (nombres cortos) a los IDs completos de las personas solo si son únicos.
     * Por ejemplo, mapea "Ormund" a "Ormund Baratheon, First of his name" si "Ormund" no está mapeado a otro ID.
     */
    private void mapearAliasesUnicos() {
        for (int i = 0; i < listaPersonas.len(); i++) {
            Persona persona = listaPersonas.get(i);
            String nombreCompleto = persona.getNombre();

            // Extraer el nombre corto (primer nombre)
            String nombreCorto = extraerPrimerNombre(nombreCompleto);

            // Evitar sobrescribir si ya está mapeado
            if (!nombreAId.containsKey(nombreCorto)) {
                nombreAId.put(nombreCorto, persona.getId());
                //System.out.println(nombreAId.get(nombreCorto));
            } else {
                // Si el alias ya está mapeado a otro ID, no mapear y emitir una advertencia
                String idExistente = nombreAId.get(nombreCorto);
                System.out.println(nombreAId.get(idExistente));
                if (!idExistente.equals(persona.getId())) {
                    System.out.println("Advertencia: Alias \"" + nombreCorto + "\" ya está mapeado a otro ID. Alias omitido para: " + persona.getNombre());
                }
            }
        }
    }

    /**
     * Elimina el nodo placeholder después de encontrar la persona real.
     * 
     * @param nodoPadre Nodo del padre
     * @param nombreHijo Nombre del hijo a buscar
     * @param grafos Objeto Grafos para actualizar las conexiones
     */
    private void eliminarPlaceholder(NodoArbol nodoPadre, String nombreHijo, Grafos grafos) {
        String primerNombre = extraerPrimerNombre(nombreHijo);
        
        Lista<NodoArbol> hijos = nodoPadre.getHijos();
        for (int i = 0; i < hijos.len(); i++) {
            NodoArbol hijo = hijos.get(i);
            String nombreHijoCompleto = hijo.getPersona().getNombre();
            String primerNombreHijo = extraerPrimerNombre(nombreHijoCompleto);
            
            // Verificar si el nodo es un placeholder
            if (primerNombreHijo.equals(primerNombre) && hijo.getPersona().getOfHisName().isEmpty()) {
                // Resolver el ID completo usando el nombre del hijo
                String idCompleto = resolverIdPorNombre(nombreHijo);
                
                if (idCompleto != null && tablaPersonasPorId.containsKey(idCompleto)) {
                    NodoArbol nodoCompleto = tablaPersonasPorId.get(idCompleto);
                    
                    // Reasignar hijos al nodo completo
                    for (int j = 0; j < hijo.getHijos().len(); j++) {
                        NodoArbol nieto = hijo.getHijos().get(j);
                        nodoCompleto.agregarHijo(nieto);
                        grafos.addArco1(idCompleto, nieto.getPersona().getId());
                    }
                    
                    // Eliminar el nodo placeholder del grafo y las tablas
                    grafos.removerPersona(hijo.getPersona().getId());
                    nodoPadre.removerHijo(hijo);
                    nombreAId.remove(hijo.getPersona().getNombre());
                    tablaPersonasPorId.remove(hijo.getPersona().getId());
                    
                    System.out.println("Placeholder eliminado y reemplazado por: " + idCompleto);
                }
                break; // Salir después de eliminar el placeholder
            }
        }
    }

    /**
     * Construye el árbol genealógico a partir de una lista de personas y añade las relaciones al grafo.
     *
     * @param personas Lista de personas a procesar.
     * @param grafos   Objeto Grafos para visualizar las conexiones.
     */
    public void construirArbol(Lista<Persona> personas, Grafos grafos) {
        listaPersonas = personas;
        
        // Primer pase: Crear nodos para todas las personas en el JSON
        for (int i = 0; i < personas.len(); i++) {
            Persona persona = personas.get(i);
            NodoArbol nodo = new NodoArbol(persona);
            tablaPersonasPorId.put(persona.getId(), nodo);

            // Mapear el nombre completo al ID
            nombreAId.put(persona.getNombre(), persona.getId());

            // Si tiene apodo, mapear el apodo al ID solo si es único
            if (!persona.getApodo().isEmpty()) {
                if (!nombreAId.containsKey(persona.getApodo())) {
                    nombreAId.put(persona.getApodo(), persona.getId());
                } else {
                    // Si el apodo ya está mapeado a otro ID, omitir el mapeo y emitir una advertencia
                    System.out.println("Advertencia: Alias \"" + persona.getApodo() + "\" ya está mapeado a otro ID. Alias omitido para: " + persona.getNombre());
                }
            }

            grafos.addPersona(persona); // Añadir persona al grafo
        }

        // Segundo pase: Mapear alias únicos antes de establecer relaciones
        mapearAliasesUnicos();

        // Tercer pase: Establecer relaciones "Born to"
        for (int i = 0; i < personas.len(); i++) {
            Persona persona = personas.get(i);
            NodoArbol nodoActual = tablaPersonasPorId.get(persona.getId());

            Lista<String> padres = persona.getBornTo();
            if (padres != null && padres.len() > 0) {
                for (int j = 0; j < padres.len(); j++) {
                    String nombrePadre = padres.get(j);
                    String idPadre = resolverIdPorNombre(nombrePadre);

                    if (idPadre != null && tablaPersonasPorId.containsKey(idPadre)) {
                        NodoArbol nodoPadre = tablaPersonasPorId.get(idPadre);
                        if (nodoPadre != null) {
                            boolean duplicado = false;
                            for (int k = 0; k < nodoPadre.getHijos().len(); k++) {
                                NodoArbol hijoExistente = nodoPadre.getHijos().get(k);
                                if (hijoExistente.getPersona().getNombre().equals(persona.getNombre()) &&
                                    hijoExistente.getPersona().getOfHisName().equals(persona.getOfHisName())) {
                                    duplicado = true;
                                    grafos.removerPersona(persona.getId());
                                    System.out.println("Duplicado encontrado y eliminado: " + persona.getNombre() +
                                                       (persona.getOfHisName().isEmpty() ? "" : ", " + persona.getOfHisName()));
                                    break;
                                }
                            }
                            
                            if (!duplicado) {
                                // Verificar si se crea un ciclo
                                if (!creaCiclo(nodoActual, nodoPadre)) {
                                    nodoPadre.agregarHijo(nodoActual);
                                    grafos.addArco1(idPadre, persona.getId());
                                    // Buscar y eliminar placeholder si existe
                                    eliminarPlaceholder(nodoPadre, persona.getNombre(), grafos);
                                } else {
                                    System.out.println("Advertencia: Relación padre-hijo entre " + nodoPadre.getPersona().getNombre() + " y " + persona.getNombre() + " crea un ciclo. Relación omitida.");
                                }
                            }
                        }
                    } else {
                        // Crear placeholder para padre ausente
                        Persona padrePlaceholder = new Persona(nombrePadre);
                        padrePlaceholder.setOfHisName("");
                        String idPlaceholder = padrePlaceholder.getId();

                        if (!tablaPersonasPorId.containsKey(idPlaceholder)) {
                            NodoArbol nodoPadrePlaceholder = new NodoArbol(padrePlaceholder);
                            tablaPersonasPorId.put(padrePlaceholder.getId(), nodoPadrePlaceholder);
                            nombreAId.put(padrePlaceholder.getNombre(), padrePlaceholder.getId());
                            grafos.addPersona(padrePlaceholder);
                        }

                        NodoArbol nodoPadrePlaceholderExistente = tablaPersonasPorId.get(idPlaceholder);
                        nodoPadrePlaceholderExistente.agregarHijo(nodoActual);
                        grafos.addArco1(idPlaceholder, persona.getId());
                    }
                }
            }
        }

        // Cuarto pase: Establecer relaciones "Father to"
        for (int i = 0; i < personas.len(); i++) {
            Persona padre = personas.get(i);
            NodoArbol nodoPadre = tablaPersonasPorId.get(padre.getId());
            
            Lista<String> hijos = padre.getHijos();
            if (hijos != null && hijos.len() > 0) {
                for (int j = 0; j < hijos.len(); j++) {
                    String nombreHijo = hijos.get(j);
                    String idHijo = resolverIdPorNombre(nombreHijo);

                    if (idHijo != null && tablaPersonasPorId.containsKey(idHijo)) {
                        NodoArbol nodoHijo = tablaPersonasPorId.get(idHijo);
                        if (nodoHijo != null) {
                            boolean conectado = false;
                            for (int k = 0; k < nodoPadre.getHijos().len(); k++) {
                                NodoArbol hijoExistente = nodoPadre.getHijos().get(k);
                                if (hijoExistente.getPersona().getId().equals(idHijo)) {
                                    conectado = true;
                                    break;
                                }
                            }

                            if (!conectado) {
                                // Verificar si se crea un ciclo
                                if (!creaCiclo(nodoHijo, nodoPadre)) {
                                    nodoPadre.agregarHijo(nodoHijo);
                                    grafos.addArco1(padre.getId(), idHijo);
                                    // Buscar y eliminar placeholder si existe
                                    eliminarPlaceholder(nodoPadre, nodoHijo.getPersona().getNombre(), grafos);
                                } else {
                                    System.out.println("Advertencia: Relación padre-hijo entre " + padre.getNombre() + " y " + nombreHijo + " crea un ciclo. Relación omitida.");
                                }
                            }
                        }
                    } else {
                        // Crear placeholder para hijo ausente
                        Persona hijoPlaceholder = new Persona(nombreHijo);
                        hijoPlaceholder.setOfHisName("");
                        String idPlaceholder = hijoPlaceholder.getId();

                        if (!tablaPersonasPorId.containsKey(idPlaceholder)) {
                            NodoArbol nodoHijoPlaceholder = new NodoArbol(hijoPlaceholder);
                            tablaPersonasPorId.put(hijoPlaceholder.getId(), nodoHijoPlaceholder);
                            nombreAId.put(hijoPlaceholder.getNombre(), hijoPlaceholder.getId());
                            grafos.addPersona(hijoPlaceholder);
                        }

                        NodoArbol nodoHijoPlaceholderExistente = tablaPersonasPorId.get(idPlaceholder);
                        nodoPadre.agregarHijo(nodoHijoPlaceholderExistente);
                        grafos.addArco1(padre.getId(), idPlaceholder);
                    }
                }
            }
        }

        // Opcional: Eliminar placeholders redundantes si es necesario
        eliminarPlaceholdersRedundantes(grafos);
    }

    /**
     * Elimina placeholders redundantes que ya tienen un nodo completo correspondiente.
     *
     * @param grafos Objeto Grafos para actualizar las conexiones.
     */
    private void eliminarPlaceholdersRedundantes(Grafos grafos) {
        Lista<String> ids = tablaPersonasPorId.keys();
        for (int i = 0; i < ids.len(); i++) {
            String id = ids.get(i);
            if (id.startsWith("[Placeholder]_")) {
                String nombre = id.substring("[Placeholder]_".length());
                String idCompleto = resolverIdPorNombre(nombre);
                if (idCompleto != null && tablaPersonasPorId.containsKey(idCompleto)) {
                    // Eliminar el nodo placeholder
                    NodoArbol nodoPlaceholder = tablaPersonasPorId.get(id);
                    Lista<NodoArbol> hijosPlaceholder = nodoPlaceholder.getHijos();

                    for (int j = 0; j < hijosPlaceholder.len(); j++) {
                        NodoArbol hijo = hijosPlaceholder.get(j);
                        NodoArbol nodoCompleto = tablaPersonasPorId.get(idCompleto);
                        nodoCompleto.agregarHijo(hijo);
                        grafos.addArco1(idCompleto, hijo.getPersona().getId());
                    }

                    grafos.removerPersona(id);
                    tablaPersonasPorId.remove(id);
                    nombreAId.remove(nombre);

                    System.out.println("Placeholder redundante eliminado: " + nombre);
                }
            }
        }
    }

    /**
     * Verifica si establecer una relación padre-hijo crearía un ciclo.
     *
     * @param nodoHijo Nodo del hijo.
     * @param nodoPadre Nodo del padre.
     * @return true si se crea un ciclo, false en caso contrario.
     */
    private boolean creaCiclo(NodoArbol nodoHijo, NodoArbol nodoPadre) {
        // Realiza una búsqueda en profundidad desde el padre para ver si encuentra al hijo
        return buscarEnAncestros(nodoPadre, nodoHijo);
    }

    /**
     * Busca recursivamente si un nodo está en los ancestros de otro nodo.
     *
     * @param actual Nodo actual en la búsqueda.
     * @param objetivo Nodo que se busca en los ancestros.
     * @return true si el objetivo se encuentra en los ancestros, false en caso contrario.
     */
    private boolean buscarEnAncestros(NodoArbol actual, NodoArbol objetivo) {
        if (actual == null) {
            return false;
        }
        if (actual.equals(objetivo)) {
            return true;
        }
        Lista<NodoArbol> padres = obtenerPadres(actual);
        for (int i = 0; i < padres.len(); i++) {
            if (buscarEnAncestros(padres.get(i), objetivo)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Obtiene una lista de padres de un nodo.
     * 
     * @param nodo Nodo del cual se obtienen los padres.
     * @return Lista de nodos padres.
     */
    private Lista<NodoArbol> obtenerPadres(NodoArbol nodo) {
        Lista<NodoArbol> padres = new Lista<>();
        Lista<String> todasLasClaves = tablaPersonasPorId.keys();
        for (int i = 0; i < todasLasClaves.len(); i++) {
            NodoArbol posiblePadre = tablaPersonasPorId.get(todasLasClaves.get(i));
            if (posiblePadre != null && posiblePadre.getHijos().contains(nodo)) {
                padres.append(posiblePadre);
            }
        }
        return padres;
    }

    /**
     * Busca el nombre completo de una persona dado un nombre corto o apodo.
     *
     * @param nombreCorto Nombre corto o apodo a buscar.
     * @return Nombre completo si existe, null en caso contrario.
     */
    private String buscarNombreCompleto(String nombreCorto) {
        for (int i = 0; i < listaPersonas.len(); i++) {
            Persona persona = listaPersonas.get(i);
            if (persona.getNombre().equalsIgnoreCase(nombreCorto) || persona.getApodo().equalsIgnoreCase(nombreCorto)) {
                return persona.getNombre();
            }
        }
        return null;
    }

    /**
     * Método para resolver el ID de una persona dado un nombre o apodo único.
     *
     * @param nombre Nombre completo, apodo o alias único de la persona.
     * @return ID único de la persona si se encuentra, null en caso contrario.
     */
    private String resolverIdPorNombre(String nombre) {
        // Busca en la tabla hash usando el nombre completo, apodo o alias único
        return nombreAId.get(nombre);
    }

    /**
     * Busca un nodo en el árbol por su ID.
     *
     * @param id Identificador único de la persona.
     * @return NodoArbol correspondiente si se encuentra, null en caso contrario.
     */
    public NodoArbol buscarPorId(String id) {
        return tablaPersonasPorId.get(id);
    }

    /**
     * Busca un nodo en el árbol por su nombre o apodo.
     *
     * @param nombre Nombre completo o apodo de la persona.
     * @return NodoArbol correspondiente si se encuentra, null en caso contrario.
     */
    public NodoArbol buscarPorNombre(String nombre) {
        String id = nombreAId.get(nombre);
        if (id != null) {
            return tablaPersonasPorId.get(id);
        }
        return null;
    }
    
}
