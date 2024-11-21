package Objetos;

import Primitivas.HashTable;
import Primitivas.Lista;
import Primitivas.Lista.ListaIterator;
import javax.swing.JOptionPane;


/**
 * Clase que representa el árbol genealógico y maneja la construcción del mismo.
 *
 * @version 4/11/2024
 */
public class ArbolGenealogico {
    private HashTable<String, NodoArbol> tablaPersonasPorId; // Mapea ID de persona a NodoArbol
    private HashTable<String, String> nombreAId; // Mapea nombres (incluyendo apodos y alias únicos) a IDs
    private Lista<Persona> listaPersonas; // Lista de todas las personas
    private HashTable<String, String> nombreAIdModificado;

    public ArbolGenealogico() {
        tablaPersonasPorId = new HashTable<>();
        nombreAId = new HashTable<>();
        listaPersonas = new Lista<>();
        nombreAIdModificado = new HashTable<>();
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
        HashTable<String, Integer> nameCounts = new HashTable<>();

        // Count occurrences of each short name
        for (int i = 0; i < listaPersonas.len(); i++) {
            Persona persona = listaPersonas.get(i);
            String nombreSinApellido = extraerPrimerNombre(persona.getNombre());

            int count = nameCounts.containsKey(nombreSinApellido) ? nameCounts.get(nombreSinApellido) : 0;
            nameCounts.put(nombreSinApellido, count + 1);
        }

        // Map only unique short names
        for (int i = 0; i < listaPersonas.len(); i++) {
            Persona persona = listaPersonas.get(i);
            String nombreSinApellido = extraerPrimerNombre(persona.getNombre());

            if (nameCounts.get(nombreSinApellido) == 1) {
                nombreAId.put(nombreSinApellido, persona.getId());
            }
        }
    }

    
    /**
    * Método para resolver el ID de una persona dado su nombre y un contexto opcional (por ejemplo, el padre).
    *
    * @param nombre Nombre de la persona a buscar.
    * @param contexto Persona que actúa como contexto (puede ser null).
    * @return ID único de la persona si se encuentra, null en caso contrario.
    */
    private String resolverIdPorNombreConContexto(String nombre, Persona contexto) {
        String id = null;

        // First try exact match in nombreAId
        if (nombreAId.containsKey(nombre)) {
            id = nombreAId.get(nombre);

            // Verify the generational relationship
            NodoArbol posibleNodo = tablaPersonasPorId.get(id);
            if (posibleNodo != null) {
                Persona posiblePersona = posibleNodo.getPersona();

                // Check if this person is actually a child of the context
                Lista<String> bornTo = posiblePersona.getBornTo();
                boolean isChild = false;
                for (int j = 0; j < bornTo.len(); j++) {
                    String nombrePadreBornTo = bornTo.get(j);
                    if (nombrePadreBornTo.equals(contexto.getId()) ||
                        nombrePadreBornTo.equals(contexto.getNombre()) ||
                        (contexto.getApodo() != null && !contexto.getApodo().isEmpty() &&
                         nombrePadreBornTo.equals(contexto.getApodo()))) {
                        isChild = true;
                        break;
                    }
                }

                // If not a child, keep searching
                if (!isChild) {
                    id = null;
                }
            }
        }

        // If no exact match or match wasn't a child, search through all persons
        if (id == null) {
            Lista<String> posiblesIds = nombreAId.values();
            for (int i = 0; i < posiblesIds.len(); i++) {
                String posibleId = posiblesIds.get(i);
                NodoArbol posibleNodo = tablaPersonasPorId.get(posibleId);
                if (posibleNodo != null) {
                    Persona posiblePersona = posibleNodo.getPersona();
                    if (posiblePersona.getNombre().contains(nombre)) {
                        // Verify this person is actually a child of the context
                        Lista<String> bornTo = posiblePersona.getBornTo();
                        for (int j = 0; j < bornTo.len(); j++) {
                            String nombrePadreBornTo = bornTo.get(j);
                            if (nombrePadreBornTo.equals(contexto.getId()) ||
                                nombrePadreBornTo.equals(contexto.getNombre()) ||
                                (contexto.getApodo() != null && !contexto.getApodo().isEmpty() &&
                                 nombrePadreBornTo.equals(contexto.getApodo()))) {
                                id = posibleId;
                                break;
                            }
                        }
                    }
                }
            }
        }

        return id;
    }
    
    



    /**
    * Construye el árbol genealógico a partir de una lista de personas y añade las relaciones al grafo.
    *
    * @param personas Lista de personas a procesar.
    * @param grafos   Objeto Grafos para visualizar las conexiones.
    */
    public void construirArbol(Lista<Persona> personas, Grafos grafos) {
        listaPersonas = personas;

        // Crear NodoArbol para cada persona y mapear nombres a IDs
        for (int i = 0; i < personas.len(); i++) {
            Persona persona = personas.get(i);
            NodoArbol nodo = new NodoArbol(persona);
            tablaPersonasPorId.put(persona.getId(), nodo);

            // Mapear nombre único (incluyendo 'Of His Name') a ID
            String uniqueName = persona.getId(); // Use the unique ID as the key
            if (!nombreAId.containsKey(uniqueName)) {
                nombreAId.put(uniqueName, persona.getId());
            }

            // Mapear apodo a ID
            if (persona.getApodo() != null && !persona.getApodo().isEmpty()) {
                if (!nombreAId.containsKey(persona.getApodo())) {
                    nombreAId.put(persona.getApodo(), persona.getId());
                }
            }

            // Añadir la persona al grafo
            grafos.addPersona(persona);
        }

        // Mapear nombres sin apellido únicos a IDs
        mapearAliasesUnicos();

        // Resolver referencias a hijos y padres
        for (int i = 0; i < personas.len(); i++) {
            Persona padrePersona = personas.get(i);
            NodoArbol nodoPadre = tablaPersonasPorId.get(padrePersona.getId());

            // Procesar hijos
            ListaIterator hijosIterator = padrePersona.getHijos().iterator();
            while (hijosIterator.hasNext()) {
                String nombreHijo = (String) hijosIterator.next();
                
                String idHijo = resolverIdPorNombreConContexto(nombreHijo, padrePersona);

                NodoArbol nodoHijo;
                if (idHijo != null && tablaPersonasPorId.containsKey(idHijo)) {
                    nodoHijo = tablaPersonasPorId.get(idHijo);
                } else {
                    // Crear placeholder con un ID único
                    String placeholderId = "[Placeholder]_" + nombreHijo + "_hijo_de_" + padrePersona.getId();
                    Persona hijoPlaceholder = new Persona(nombreHijo);
                    hijoPlaceholder.setId(placeholderId); // Asegurarse de que el ID es único
                    nodoHijo = new NodoArbol(hijoPlaceholder);
                    tablaPersonasPorId.put(placeholderId, nodoHijo);
                    nombreAId.put(placeholderId, placeholderId);
                    grafos.addPersona(hijoPlaceholder);
                }

                // Agregar hijo al nodo padre si no está ya conectado
                if (!nodoPadre.getHijos().contains(nodoHijo)) {
                    nodoPadre.agregarHijo(nodoHijo);
                    nodoHijo.agregarPadre(nodoPadre);
                    grafos.addArco1(padrePersona.getId(), nodoHijo.getPersona().getId());
                }

                // Agregar objeto Persona del hijo al padrePersona
                padrePersona.addFatherTo(nodoHijo.getPersona());
            }

            // Procesar bornTo (padres)
            ListaIterator padresIterator = padrePersona.getBornTo().iterator();
            while (padresIterator.hasNext()) {
                String nombrePadre = (String) padresIterator.next();
                String idPadre = resolverIdPorNombre(nombrePadre);

                NodoArbol nodoPadreReal;
                if (idPadre != null && tablaPersonasPorId.containsKey(idPadre)) {
                    nodoPadreReal = tablaPersonasPorId.get(idPadre);
                } else {
                    // Crear placeholder si el padre no existe
                    Persona padrePlaceholder = new Persona(nombrePadre);
                    nodoPadreReal = new NodoArbol(padrePlaceholder);
                    tablaPersonasPorId.put(padrePlaceholder.getId(), nodoPadreReal);
                    nombreAId.put(padrePlaceholder.getNombre(), padrePlaceholder.getId());

                    // Añadir el placeholder al grafo
                    grafos.addPersona(padrePlaceholder);
                }

                // Agregar este nodo como hijo del padre real si no está ya conectado
                if (!nodoPadreReal.getHijos().contains(nodoPadre)) {
                    nodoPadreReal.agregarHijo(nodoPadre);
                    nodoPadre.agregarPadre(nodoPadreReal);
                    grafos.addArco1(nodoPadreReal.getPersona().getId(), padrePersona.getId());
                }
            }
        }

        // Opcional: Eliminar placeholders redundantes si es necesario
        eliminarPlaceholdersRedundantes(grafos);

    }
    
    
    
    private void actualizarConexiones(NodoArbol nodoPlaceholder, NodoArbol nodoReal, Grafos grafos) {
        // Actualizar padres
        Lista<NodoArbol> padres = nodoPlaceholder.getPadres();
        for (int i = 0; i < padres.len(); i++) {
            NodoArbol padre = padres.get(i);
            padre.removerHijo(nodoPlaceholder);
            padre.agregarHijo(nodoReal);
            grafos.addArco1(padre.getPersona().getId(), nodoReal.getPersona().getId());
        }

        // Actualizar hijos
        Lista<NodoArbol> hijos = nodoPlaceholder.getHijos();
        for (int i = 0; i < hijos.len(); i++) {
            NodoArbol hijo = hijos.get(i);
            nodoReal.agregarHijo(hijo);
            hijo.removerPadre(nodoPlaceholder);
            hijo.agregarPadre(nodoReal);
            grafos.addArco1(nodoReal.getPersona().getId(), hijo.getPersona().getId());
        }
    }


    /**
    * Elimina placeholders redundantes que ya tienen un nodo completo correspondiente.
    *
    * @param grafos Objeto Grafos para actualizar las conexiones.
    */
    private void eliminarPlaceholdersRedundantes(Grafos grafos) {
        Lista<String> idsPlaceholders = new Lista<>();

        // Recopilar todos los IDs de placeholders
        Lista<String> ids = tablaPersonasPorId.keys();
        for (int i = 0; i < ids.len(); i++) {
            String id = ids.get(i);
            if (id.startsWith("[Placeholder]_")) {
                idsPlaceholders.append(id);
            }
        }

        // Para cada placeholder, intentar encontrar una persona real que coincida
        for (int i = 0; i < idsPlaceholders.len(); i++) {
            String idPlaceholder = idsPlaceholders.get(i);
            NodoArbol nodoPlaceholder = tablaPersonasPorId.get(idPlaceholder);
            Persona personaPlaceholder = nodoPlaceholder.getPersona();
            String nombrePlaceholder = personaPlaceholder.getNombre();

            // Llamar al nuevo método con el nodoPlaceholder
            String idReal = buscarPersonaPorNombreYContexto(nombrePlaceholder, nodoPlaceholder);

            if (idReal != null) {
                NodoArbol nodoReal = tablaPersonasPorId.get(idReal);

                // Actualizar las conexiones
                actualizarConexiones(nodoPlaceholder, nodoReal, grafos);

                // Eliminar el placeholder
                tablaPersonasPorId.remove(idPlaceholder);
                nombreAId.remove(idPlaceholder);
                grafos.removerPersona(idPlaceholder);

                System.out.println("Placeholder eliminado y reemplazado por: " + idReal);
            }
        }
    }
    
private String buscarPersonaPorNombreYContexto(String nombre, NodoArbol nodoPlaceholder) {
    for (int i = 0; i < listaPersonas.len(); i++) {
        Persona persona = listaPersonas.get(i);
        if (persona.getNombre().contains(nombre)) {
            // Get placeholder's parents
            Lista<NodoArbol> padresPlaceholder = nodoPlaceholder.getPadres();
            for (int j = 0; j < padresPlaceholder.len(); j++) {
                NodoArbol padrePlaceholder = padresPlaceholder.get(j);
                Persona padrePersona = padrePlaceholder.getPersona();
                
                // Check if the placeholder's parent matches any of the person's bornTo
                Lista<String> bornTo = persona.getBornTo();
                for (int k = 0; k < bornTo.len(); k++) {
                    String nombrePadreBornTo = bornTo.get(k);
                    
                    // Compare IDs, names, and nicknames, also verify generational context
                    if ((nombrePadreBornTo.equals(padrePersona.getId()) ||
                         nombrePadreBornTo.equals(padrePersona.getNombre()) ||
                         (padrePersona.getApodo() != null && !padrePersona.getApodo().isEmpty() &&
                          nombrePadreBornTo.equals(padrePersona.getApodo())))) {
                        return persona.getId();
                    }
                }
            }
        }
    }
    return null;
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

    private String resolverIdPorNombre(String nombre) {
        // First, try to find an exact match in nombreAId
        if (nombreAId.containsKey(nombre)) {
            return nombreAId.get(nombre);
        }
        // If not found, attempt to find by name including 'Of His Name'
        Lista<String> keys = nombreAId.keys();
        for (int i = 0; i < keys.len(); i++) {
            String key = keys.get(i);
            if (key.startsWith(nombre)) {
                return nombreAId.get(key);
            }
        }
        return null;
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

        // Método para construir la HashTable secundaria
        public void construirTablaNombreModificado() {
        nombreAIdModificado = new HashTable<String, String>();

        // Recorrer todas las claves de nombreAId para construir las entradas modificadas
        Lista<String> claves = nombreAId.getKeys();
        for (int i = 0; i < claves.getSize(); i++) {
            String nombreKey = claves.get(i);
            String id = nombreAId.get(nombreKey);

            // Obtener la persona correspondiente en tablaPersonasPorId
            NodoArbol nodo = tablaPersonasPorId.get(id);
            if (nodo != null) {
                Persona persona = nodo.getPersona();

                // Construir nombre con "Of his name" si está presente
                String nombreConOfHisName = nombreKey;
                if (persona.getOfHisName() != null && !persona.getOfHisName().isEmpty()) {
                    nombreConOfHisName += ", " + persona.getOfHisName() + " of his name";
                }

                // Añadir ambos nombres a la hashtable modificada
                nombreAIdModificado.put(nombreConOfHisName, id);
                System.out.println("Añadido a nombreAIdModificado: " + nombreConOfHisName + " -> " + id);

                // Si tiene un alias "Known throughout as", añadirlo también
                if (persona.getApodo() != null && !persona.getApodo().isEmpty()) {
                    nombreAIdModificado.put(persona.getApodo(), id);
                    System.out.println("Añadido a nombreAIdModificado (apodo): " + persona.getApodo() + " -> " + id);
                }
            }
        }
    }



    /**
     * Busca un nodo en el árbol por su nombre o apodo.
     *
     * @param nombre Nombre completo o apodo de la persona.
     * @return NodoArbol correspondiente si se encuentra, null en caso contrario.
     */
    public Lista<NodoArbol> buscarPorNombre(String nombre) {
        Lista<NodoArbol> resultados = new Lista<>();

        // Find all keys that match the given name or start with the name
        Lista<String> keys = nombreAId.keys();
        for (int i = 0; i < keys.len(); i++) {
            String key = keys.get(i);
            if (key.equalsIgnoreCase(nombre) || key.startsWith(nombre + ",") || key.startsWith(nombre + " ")) {
                String id = nombreAId.get(key);
                if (tablaPersonasPorId.containsKey(id)) {
                    NodoArbol nodo = tablaPersonasPorId.get(id);
                    resultados.append(nodo);
                }
            }
        }

        // If no results found, try partial matches
        if (resultados.len() == 0) {
            for (int i = 0; i < keys.len(); i++) {
                String key = keys.get(i);
                if (key.toLowerCase().contains(nombre.toLowerCase())) {
                    String id = nombreAId.get(key);
                    if (tablaPersonasPorId.containsKey(id)) {
                        NodoArbol nodo = tablaPersonasPorId.get(id);
                        resultados.append(nodo);
                    }
                }
            }
        }

        return resultados;
    }


    public void mostrarDescendientes(NodoArbol nodo, int nivel, Lista<Lista<NodoArbol>> descendientesPorNivel, Lista<NodoArbol> visitados) {
        if (visitados.contains(nodo)) {
            return; // Node already visited, prevent infinite recursion
        }
        visitados.append(nodo);

        // Ensure the list has a list for the current level
        while (descendientesPorNivel.getSize() <= nivel) {
            descendientesPorNivel.append(new Lista<NodoArbol>());
        }

        // Add the children of the current node to the list of the current level
        Lista<NodoArbol> hijos = nodo.getHijos();
        if (hijos.getSize() > 0) {
            Lista<NodoArbol> nivelDescendientes = descendientesPorNivel.get(nivel);
            for (int j = 0; j < hijos.getSize(); j++) {
                NodoArbol hijo = hijos.get(j);
                nivelDescendientes.append(hijo);

                // Recursive call for each child, increasing the level
                mostrarDescendientes(hijo, nivel + 1, descendientesPorNivel, visitados);
            }
        }
    }


    public void mostrarArbolGenealogicoPorNombre(String nombre, Grafos grafosOriginal) {
        Lista<NodoArbol> nodosEncontrados = buscarPorNombre(nombre);

        if (nodosEncontrados.getSize() == 0) {
            System.out.println("La persona con el nombre " + nombre + " no fue encontrada.");
            return;
        }

        // Assume only one person is found
        NodoArbol nodoPersona = nodosEncontrados.get(0);

        // Add only the starting person to the graph
        grafosOriginal.addPersona(nodoPersona.getPersona());

        // Create the 'visitados' list and add the current node
        Lista<String> visitados = new Lista<>();
        visitados.append(nodoPersona.getPersona().getId());

        // Process only children and descendants (not parents)
        Lista<NodoArbol> hijos = nodoPersona.getHijos();
        if (hijos.getSize() > 0) {
            for (int i = 0; i < hijos.getSize(); i++) {
                NodoArbol nodoHijo = hijos.get(i);
                grafosOriginal.addPersona(nodoHijo.getPersona());

                // Add child relationship
                grafosOriginal.addArco1(nodoPersona.getPersona().getId(), nodoHijo.getPersona().getId());

                // Recursive call
                agregarDescendientesRecursivos(nodoHijo, grafosOriginal, visitados);
            }
        }
    }



    private void agregarDescendientesRecursivos(NodoArbol nodoPadre, Grafos grafosOriginal, Lista<String> visitados) {
        if (visitados.contains(nodoPadre.getPersona().getId())) {
            return; // Already visited
        }
        visitados.append(nodoPadre.getPersona().getId());

        Lista<NodoArbol> hijos = nodoPadre.getHijos();

        if (hijos.getSize() > 0) {
            for (int i = 0; i < hijos.getSize(); i++) {
                NodoArbol nodoHijo = hijos.get(i);
                grafosOriginal.addPersona(nodoHijo.getPersona());

                // Add parent-child relationship
                grafosOriginal.addArco1(nodoPadre.getPersona().getId(), nodoHijo.getPersona().getId());

                // Recursive call with updated visited list
                agregarDescendientesRecursivos(nodoHijo, grafosOriginal, visitados);
            }
        }
    }


    
    public String obtenerDescendenciaOrdenada(String nombrePersona) {
        // Buscar a la persona en el árbol genealógico
        Lista<NodoArbol> nodosEncontrados = buscarPorNombre(nombrePersona);

        if (nodosEncontrados.getSize() == 0) {
            return "La persona con el nombre '" + nombrePersona + "' no fue encontrada.";
        }

        NodoArbol nodoInicial = nodosEncontrados.get(0); // Tomar el primer resultado
        Lista<Lista<NodoArbol>> descendientesPorNivel = new Lista<>();

        Lista<NodoArbol> visitados = new Lista<>();

        // Llenar la lista de descendientes por nivel
        mostrarDescendientes(nodoInicial, 0, descendientesPorNivel, visitados);
        // Eliminar duplicados de cada nivel
        for (int i = 0; i < descendientesPorNivel.getSize(); i++) {
            Lista<NodoArbol> nivelActual = descendientesPorNivel.get(i);
            Lista<NodoArbol> nivelSinDuplicados = new Lista<>();

            for (int j = 0; j < nivelActual.getSize(); j++) {
                NodoArbol nodo = nivelActual.get(j);
                if (!nivelSinDuplicados.contains(nodo)) {
                    nivelSinDuplicados.append(nodo);
                }
            }

            descendientesPorNivel.set(i, nivelSinDuplicados); // Actualizar con los nodos únicos
        }

        // Eliminar el último nivel si existe
        if (descendientesPorNivel.getSize() > 0) {
            descendientesPorNivel.remove(descendientesPorNivel.getSize() - 1);
        }

        // Construir el String con la descendencia ordenada
        StringBuilder descendenciaOrdenada = new StringBuilder();
        for (int nivel = 0; nivel < descendientesPorNivel.getSize(); nivel++) {
            Lista<NodoArbol> nivelActual = descendientesPorNivel.get(nivel);

            descendenciaOrdenada.append("Nivel ").append(nivel + 1).append(":\n");
            for (int j = 0; j < nivelActual.getSize(); j++) {
                NodoArbol nodo = nivelActual.get(j);
                descendenciaOrdenada.append("- ").append(nodo.getPersona().getNombre()).append("\n");
            }
        }

        return descendenciaOrdenada.toString();
    }


    
    public Lista<Persona> buscarPorTitulo(String titulo) {
        Lista<Persona> resultado = new Lista<>();

        for (int i = 0; i < listaPersonas.getSize(); i++) {
            System.out.println(listaPersonas.get(i));
            Persona persona = listaPersonas.get(i);
            if (persona.getTitle() != null && persona.getTitle().equalsIgnoreCase(titulo)) {
                   resultado.append(persona);
            }
        }
        return resultado;
    }
    
    public Lista<Persona> NombreEspecifico(String nombre){
        Lista<Persona> resultado = new Lista<>();
        for (int i = 0; i < listaPersonas.getSize(); i++) {
            Persona persona = listaPersonas.get(i);
            // Verifica que el nombre no sea nulo y compara ignorando mayúsculas/minúsculas
            if (persona.getNombre() != null && persona.getNombre().equalsIgnoreCase(nombre)) {
                resultado.append(persona);
            }
        }
        return resultado;
    }
    
    public void mostrarAntepasados(String nombre, Grafos grafosOriginal) {
        // Buscar los nodos correspondientes al nombre
        Lista<NodoArbol> nodosEncontrados = buscarPorNombre(nombre);

        if (nodosEncontrados.getSize() == 0) {
            System.out.println("La persona con el nombre " + nombre + " no fue encontrada.");
            return;
        }
        

        // Suponemos que solo se encuentra una persona con el nombre
        NodoArbol nodoPersona = nodosEncontrados.get(0);  // Tomamos el primer resultado
        
        // Create the 'visitados' list and add the current node
       Lista<String> visitados = new Lista<>();
       visitados.append(nodoPersona.getPersona().getId());

        // Primer pase: Crear nodos para la persona y sus relaciones
        grafosOriginal.addPersona(nodoPersona.getPersona());  // Añadir la persona seleccionada al nuevo grafo

        // Segundo pase: Establecer relaciones "Born to" (padres)
        Lista<NodoArbol> padres = obtenerPadres(nodoPersona);  // Obtener los padres
        if (padres.getSize() > 0) {
            for (int i = 0; i < padres.getSize(); i++) {
                NodoArbol nodoPadre = padres.get(i);
                grafosOriginal.addPersona(nodoPadre.getPersona());  // Añadir el padre al nuevo grafo

                // Añadir relación padre-hijo (Born to) si no existe
                boolean duplicado = false;
                for (int j = 0; j < nodoPadre.getHijos().len(); j++) {
                    NodoArbol hijoExistente = nodoPadre.getHijos().get(j);
                    if (hijoExistente.getPersona().getId().equals(nodoPersona.getPersona().getId())) {
                        grafosOriginal.addArco1(nodoPadre.getPersona().getId(), nodoPersona.getPersona().getId());
                        duplicado = true;
                        break;
                    }
                }

                // Si no es un duplicado, añadir la relación y el arco
                if (!duplicado) {
                    nodoPadre.agregarHijo(nodoPersona);
                    grafosOriginal.addArco1(nodoPadre.getPersona().getId(), nodoPersona.getPersona().getId()); // Se asume distancia 1
                }

                // Recursión para añadir los antepasados del padre
                agregarAntepasadosRecursivos(nodoPadre, grafosOriginal, visitados); // Llamada recursiva para agregar los antepasados
            }
        }
    }

    private void agregarAntepasadosRecursivos(NodoArbol nodoHijo, Grafos grafosOriginal, Lista<String> visitados) {
        if (visitados.contains(nodoHijo.getPersona().getId())) {
            return; // Already visited
        }
        visitados.append(nodoHijo.getPersona().getId());

        // Obtener los padres del nodo
        Lista<NodoArbol> padres = obtenerPadres(nodoHijo);

        if (padres.getSize() > 0) {
            for (int i = 0; i < padres.getSize(); i++) {
                NodoArbol nodoPadre = padres.get(i);
                grafosOriginal.addPersona(nodoPadre.getPersona());  // Añadir al grafo

                // Añadir relación padre-hijo
                grafosOriginal.addArco1(nodoPadre.getPersona().getId(), nodoHijo.getPersona().getId());

                // Llamada recursiva con la lista de visitados
                agregarAntepasadosRecursivos(nodoPadre, grafosOriginal, visitados);
            }
        }
    }

        /**
     * Método para obtener la ascendencia de una persona en forma ordenada.
     *
     * @param nombre El nombre de la persona cuya ascendencia se desea obtener.
     * @return Una representación en String de los antepasados organizados por niveles.
     */
    public String obtenerAscendenciaOrdenada(String nombre) {
        Lista<NodoArbol> nodosEncontrados = buscarPorNombre(nombre);

        if (nodosEncontrados.getSize() == 0) {
            return "No se encontró a ninguna persona con el nombre: " + nombre;
        }

        // Suponemos que solo se encuentra una persona con el nombre
        NodoArbol nodoPersona = nodosEncontrados.get(0); // Tomar el primer resultado

        // Lista para almacenar los ancestros por niveles
        Lista<Lista<String>> ancestrosPorNivel = new Lista<>();

        // Llenar la lista de ancestros por niveles
        obtenerAncestrosPorNivel(nodoPersona, ancestrosPorNivel, 0);

        // Eliminar duplicados de cada nivel
        for (int i = 0; i < ancestrosPorNivel.len(); i++) {
            Lista<String> nivelSinDuplicados = eliminarDuplicados(ancestrosPorNivel.get(i));
            ancestrosPorNivel.set(i, nivelSinDuplicados);
        }

        // Construir la representación en String
        StringBuilder resultado = new StringBuilder();
        resultado.append("Ascendencia de ").append(nodoPersona.getPersona().getNombre()).append(":\n");

        for (int i = 0; i < ancestrosPorNivel.len(); i++) {
            resultado.append("Nivel ").append(i + 1).append(": ").append(ancestrosPorNivel.get(i).toString()).append("\n");
        }

        return resultado.toString();
    }

    /**
     * Método recursivo para obtener los ancestros de una persona organizados por niveles.
     *
     * @param nodoActual Nodo actual en el árbol genealógico.
     * @param ancestrosPorNivel Lista de niveles que almacenará los ancestros.
     * @param nivel Nivel actual del nodo en el árbol.
     */
    private void obtenerAncestrosPorNivel(NodoArbol nodoActual, Lista<Lista<String>> ancestrosPorNivel, int nivel) {
        if (nodoActual == null || nodoActual.getPersona() == null) {
            return;
        }

        // Asegurarse de que exista una lista para el nivel actual
        while (ancestrosPorNivel.len() <= nivel) {
            ancestrosPorNivel.append(new Lista<>());
        }

        // Añadir al nivel actual el nombre de la persona
        ancestrosPorNivel.get(nivel).append(nodoActual.getPersona().getNombre());

        // Llamar recursivamente para cada padre (suponiendo que "bornTo" representa a los padres)
        Lista<NodoArbol> padres = obtenerPadres(nodoActual); // Obtener la lista de padres
        for (int i = 0; i < padres.getSize(); i++) {
            NodoArbol nodoPadre = padres.get(i);
            obtenerAncestrosPorNivel(nodoPadre, ancestrosPorNivel, nivel + 1);
        }
    }

    /**
     * Elimina duplicados de una lista.
     *
     * @param lista Lista de la que se eliminarán duplicados.
     * @return Una nueva lista sin duplicados.
     */
    private Lista<String> eliminarDuplicados(Lista<String> lista) {
        Lista<String> listaSinDuplicados = new Lista<>();
        Lista<String>.ListaIterator iterador = lista.iterator();

        while (iterador.hasNext()) {
            String elemento = iterador.next();
            if (!listaSinDuplicados.contains(elemento)) {
                listaSinDuplicados.append(elemento);
            }
        }

        return listaSinDuplicados;
    }


    
    public Lista<Persona> buscarPorNombreParcial(String nombreBusqueda) {
        Lista<Persona> resultado = new Lista<>();

        // Recorrer la lista de personas para encontrar coincidencias con el nombre, apodo, o nombre completo que incluye "Of His Name"
        for (int i = 0; i < listaPersonas.getSize(); i++) {
            Persona persona = listaPersonas.get(i);

            // Verificar coincidencias con el nombre o apodo
            if ((persona.getNombre() != null && persona.getNombre().toLowerCase().contains(nombreBusqueda.toLowerCase())) ||
                (persona.getApodo() != null && persona.getApodo().toLowerCase().contains(nombreBusqueda.toLowerCase()))) {
                resultado.append(persona);
            }

            // Verificar coincidencias con el nombre completo que incluye "Of His Name"
            if (persona.getNombre() != null && persona.getOfHisName() != null) {
                String nombreCompleto = persona.getNombre() + ", " + persona.getOfHisName() + " of his name";
                if (nombreCompleto.toLowerCase().contains(nombreBusqueda.toLowerCase())) {
                    resultado.append(persona);
                }
            }

            // Verificar coincidencias con el apodo explícitamente
            if (persona.getApodo() != null && persona.getApodo().toLowerCase().contains(nombreBusqueda.toLowerCase())) {
                resultado.append(persona);
            }
        }

        // Eliminar duplicados de la lista
        Lista<Persona> resultadoSinDuplicados = new Lista<>();
        for (int i = 0; i < resultado.getSize(); i++) {
            Persona persona = resultado.get(i);
            if (!resultadoSinDuplicados.contains(persona)) {
                resultadoSinDuplicados.append(persona);
            }
        }

        return resultadoSinDuplicados;
    }
    
    public Lista<Persona> obtenerPersonasGeneracion(int numeroGeneracion) {
        if (numeroGeneracion < 0) {
            throw new IllegalArgumentException("El número de generación debe ser mayor o igual a 0.");
        }

        Lista<Lista<NodoArbol>> niveles = new Lista<>();

        NodoArbol raiz = obtenerRaiz();
        if (raiz == null) {
            throw new IllegalStateException("El árbol genealógico está vacío.");
        }

        construirNiveles(raiz, 0, niveles);

        if (numeroGeneracion >= niveles.getSize()) {
            throw new IllegalArgumentException("El número de generación solicitado supera la profundidad del árbol.");
        }

        Lista<NodoArbol> generacion = niveles.get(numeroGeneracion);
        if (generacion.getSize() == 0) {
            return new Lista<>(); // Retornar una lista vacía si no hay integrantes
        }

        // Eliminar duplicados
        Lista<NodoArbol> generacionSinDuplicados = new Lista<>();
        for (int i = 0; i < generacion.getSize(); i++) {
            NodoArbol nodo = generacion.get(i);
            if (!contieneNodo(generacionSinDuplicados, nodo)) {
                generacionSinDuplicados.append(nodo);
            }
        }

        // Crear y llenar la lista de personas
        Lista<Persona> personasGeneracion = new Lista<>();
        for (int i = 0; i < generacionSinDuplicados.getSize(); i++) {
            NodoArbol nodo = generacionSinDuplicados.get(i);
            personasGeneracion.append(nodo.getPersona());
        }

        return personasGeneracion;
    }

    // Método auxiliar para verificar si un NodoArbol ya está en la lista
    private boolean contieneNodo(Lista<NodoArbol> lista, NodoArbol nodo) {
        for (int i = 0; i < lista.getSize(); i++) {
            if (lista.get(i).getPersona().getId().equals(nodo.getPersona().getId())) {
                return true;
            }
        }
        return false;
    }



    // Método auxiliar para construir los niveles del árbol
    private void construirNiveles(NodoArbol nodo, int nivel, Lista<Lista<NodoArbol>> niveles) {
        // Asegurarse de que la lista tenga espacio para el nivel actual
        while (niveles.getSize() <= nivel) {
            niveles.append(new Lista<NodoArbol>());
        }

        // Agregar el nodo actual al nivel correspondiente
        niveles.get(nivel).append(nodo);
        //System.out.println("\nEl nodo es: " + nodo.getPersona().toString());

        // Llamar recursivamente para los hijos del nodo actual
        Lista<NodoArbol> hijos = nodo.getHijos();
        for (int i = 0; i < hijos.getSize(); i++) {
            construirNiveles(hijos.get(i), nivel + 1, niveles);
        }
    }

    // Método para obtener la raíz del árbol genealógico
    private NodoArbol obtenerRaiz() {
        // Iterar por las personas para encontrar aquella cuyo padre sea "[Unknown]"
        Lista<String> ids = tablaPersonasPorId.getKeys();
        for (int i = 0; i < ids.getSize(); i++) {
            NodoArbol nodo = tablaPersonasPorId.get(ids.get(i));
            if (nodo != null && esRaiz(nodo.getPersona())) {
                return nodo; // Persona con "Born to" igual a "[Unknown]" es la raíz
            }
        }
        return null; // Si no se encuentra, el árbol está vacío
    }

    // Método auxiliar para verificar si una persona es la raíz
    private boolean esRaiz(Persona persona) {
        Lista<String> padres = persona.getBornTo();
        for (int i = 0; i < padres.getSize(); i++) {
            if (padres.get(i).equals("[Unknown]")) {
                return true; // La persona tiene como padre "[Unknown]"
            }
        }
        return false; // La persona tiene otros padres o no tiene padres definidos
    }


    public int obtenerAltura() {
        NodoArbol raiz = obtenerRaiz();
        if (raiz == null) {
            return 0; // Altura de un árbol vacío
        }
        return calcularAltura(raiz);
    }

    private int calcularAltura(NodoArbol nodo) {
        if (nodo == null) {
            return 0;
        }

        int alturaMaxima = 0;
        Lista<NodoArbol> hijos = nodo.getHijos();
        for (int i = 0; i < hijos.getSize(); i++) {
            alturaMaxima = Math.max(alturaMaxima, calcularAltura(hijos.get(i)));
        }

        return 1 + alturaMaxima;
    }
    
    public void mostrarInformacionNodo(String id) {
        NodoArbol nodo = tablaPersonasPorId.get(id); // Buscar el nodo por ID
        if (nodo != null) {
            Persona persona = nodo.getPersona();
            if (persona != null) {
                JOptionPane.showMessageDialog(null,
                    persona.toString(), // Utiliza el método toString para mostrar la información
                    "Información de " + persona.getNombre(),
                    JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(null,
                    "No se encontró información asociada a este nodo.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        } else {
            JOptionPane.showMessageDialog(null,
                "No se encontró el nodo con ID: " + id,
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}