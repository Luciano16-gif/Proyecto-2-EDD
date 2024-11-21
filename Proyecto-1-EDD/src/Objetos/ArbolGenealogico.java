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
        HashTable<String, String> nombreCortoAId = new HashTable<>();

        for (int i = 0; i < listaPersonas.len(); i++) {
            Persona persona = listaPersonas.get(i);
            String nombreCompleto = persona.getNombre();

            // Extraer el nombre sin apellido
            String nombreSinApellido = extraerPrimerNombre(nombreCompleto);

            // Evitar sobrescribir si ya está mapeado
            if (!nombreCortoAId.containsKey(nombreSinApellido)) {
                nombreCortoAId.put(nombreSinApellido, persona.getId());
            } else {
                // Si el nombre sin apellido ya está mapeado a otro ID, marcar como no único
                nombreCortoAId.put(nombreSinApellido, null); // null indica no único
            }
        }

        // Agregar los nombres sin apellido únicos a nombreAId
        Lista<String> nombresCortos = nombreCortoAId.keys();
        for (int i = 0; i < nombresCortos.len(); i++) {
            String nombreCorto = nombresCortos.get(i);
            String id = nombreCortoAId.get(nombreCorto);
            if (id != null) {
                // Es único, agregar a nombreAId
                nombreAId.put(nombreCorto, id);
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
    * Método para resolver el ID de una persona dado su nombre y un contexto opcional (por ejemplo, el padre).
    *
    * @param nombre Nombre de la persona a buscar.
    * @param contexto Persona que actúa como contexto (puede ser null).
    * @return ID único de la persona si se encuentra, null en caso contrario.
    */
    private String resolverIdPorNombreConContexto(String nombre, Persona contexto) {
        String id = null;

        // Intentar encontrar un match exacto en nombreAId
        if (nombreAId.containsKey(nombre)) {
            id = nombreAId.get(nombre);
        } else {
            // Intentar encontrar una persona cuyo nombre contenga el nombre dado y coincida con el contexto
            Lista<String> posiblesIds = nombreAId.values();
            for (int i = 0; i < posiblesIds.len(); i++) {
                String posibleId = posiblesIds.get(i);
                NodoArbol posibleNodo = tablaPersonasPorId.get(posibleId);
                if (posibleNodo != null) {
                    Persona posiblePersona = posibleNodo.getPersona();
                    if (posiblePersona.getNombre().contains(nombre)) {
                        // Verificar si el contexto (padre) está en la lista bornTo de la posible persona
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

            // Mapear nombre y apodo a ID
            nombreAId.put(persona.getNombre(), persona.getId());
            if (persona.getApodo() != null && !persona.getApodo().isEmpty()) {
                nombreAId.put(persona.getApodo(), persona.getId());
            }

            // **Añadir la persona al grafo**
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
                // Obtener los padres del placeholder
                Lista<NodoArbol> padresPlaceholder = nodoPlaceholder.getPadres();
                for (int j = 0; j < padresPlaceholder.len(); j++) {
                    NodoArbol padrePlaceholder = padresPlaceholder.get(j);
                    Persona padrePersona = padrePlaceholder.getPersona();

                    // Verificar si el padre del placeholder coincide con alguno de los bornTo de la persona
                    Lista<String> bornTo = persona.getBornTo();
                    for (int k = 0; k < bornTo.len(); k++) {
                        String nombrePadreBornTo = bornTo.get(k);

                        // Comparar IDs, nombres y apodos
                        if (nombrePadreBornTo.equals(padrePersona.getId()) ||
                            nombrePadreBornTo.equals(padrePersona.getNombre()) ||
                            (padrePersona.getApodo() != null && !padrePersona.getApodo().isEmpty() &&
                             nombrePadreBornTo.equals(padrePersona.getApodo()))) {
                            return persona.getId();
                        }
                    }
                }
            }
        }
        return null;
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

    // Primero, busca en nombreAId (por nombre exacto)
    String id = nombreAId.get(nombre);

    // Si no se encuentra, busca en nombreAIdModificado (por apodo o alias)
    if (id == null) {
        // Buscar en nombreAIdModificado para alias y apodos
        Lista<String> clavesModificadas = nombreAIdModificado.getKeys();
        for (int i = 0; i < clavesModificadas.getSize(); i++) {
            String claveModificada = clavesModificadas.get(i);
            if (claveModificada.equalsIgnoreCase(nombre)) {
                id = nombreAIdModificado.get(claveModificada);
                break;
            }
        }
    }

    // Si no se encuentra, tratar de resolver el ID directamente (en caso de un nombre único no registrado)
    if (id == null) {
        id = nombre;  // Puede ser un ID por defecto, en caso de no encontrar
    }

    // Si se encuentra el ID, buscamos el NodoArbol
    if (id != null && tablaPersonasPorId.containsKey(id)) {
        NodoArbol nodo = tablaPersonasPorId.get(id);
        if (nodo != null) {
            resultados.append(nodo);

            // Mostrar padres y asignar en bornTo
            Lista<NodoArbol> padres = obtenerPadres(nodo);
            if (padres.getSize() > 0) {
                for (int i = 0; i < padres.getSize(); i++) {
                    NodoArbol padre = padres.get(i);
                    Persona padrePersona = padre.getPersona();
                    nodo.getPersona().addBornTo2(padrePersona.getNombre() + ", " + padrePersona.getOfHisName());
                }
            }

            // Crear lista para descendientes por nivel
            Lista<Lista<NodoArbol>> descendientesPorNivel = new Lista<>();
            mostrarDescendientes(nodo, 0, descendientesPorNivel); // Nivel inicial es 0 para los hijos

            // Guardar descendientes en tabla para acceso rápido en la gráfica
            for (int nivel = 0; nivel < descendientesPorNivel.getSize(); nivel++) {
                Lista<NodoArbol> descendientesNivel = descendientesPorNivel.get(nivel);
                for (int j = 0; j < descendientesNivel.getSize(); j++) {
                    NodoArbol descendiente = descendientesNivel.get(j);
                    tablaPersonasPorId.put(descendiente.getPersona().getId(), descendiente);
                }
            }
        }
    } else {
        System.out.println("No se encontró ninguna persona con el nombre o apodo proporcionado.");
    }

    return resultados;
}

    public void mostrarDescendientes(NodoArbol nodo, int nivel, Lista<Lista<NodoArbol>> descendientesPorNivel) {
        // Asegúrate de que hay una lista en descendientesPorNivel para el nivel actual
        while (descendientesPorNivel.getSize() <= nivel) {
            descendientesPorNivel.append(new Lista<NodoArbol>());
        }

        // Añadir los hijos del nodo actual a la lista del nivel actual
        Lista<NodoArbol> hijos = nodo.getHijos();
        if (hijos.getSize() > 0) {
            Lista<NodoArbol> nivelDescendientes = descendientesPorNivel.get(nivel);
            for (int j = 0; j < hijos.getSize(); j++) {
                NodoArbol hijo = hijos.get(j);
                nivelDescendientes.append(hijo);

                // Llamada recursiva para cada hijo, incrementando el nivel
                mostrarDescendientes(hijo, nivel + 1, descendientesPorNivel);
            }
        }
    }

    public void mostrarArbolGenealogicoPorNombre(String nombre, Grafos grafosOriginal) {
        Lista<NodoArbol> nodosEncontrados = buscarPorNombre(nombre);

        if (nodosEncontrados.getSize() == 0) {
            System.out.println("La persona con el nombre " + nombre + " no fue encontrada.");
            return;
        }

        // Suponemos que solo se encuentra una persona con el nombre
        NodoArbol nodoPersona = nodosEncontrados.get(0);  // Tomamos el primer resultado

        // Primer pase: Crear nodos para la persona y sus relaciones
        grafosOriginal.addPersona(nodoPersona.getPersona());  // Añadir la persona seleccionada al nuevo grafo

        // Segundo pase: Establecer relaciones "Born to"
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
            }
        }

        // Tercer pase: Establecer relaciones "Father to" (hijos)
        Lista<NodoArbol> hijos = nodoPersona.getHijos();  // Obtener los hijos de la persona
        if (hijos.getSize() > 0) {
            for (int i = 0; i < hijos.getSize(); i++) {
                NodoArbol nodoHijo = hijos.get(i);
                grafosOriginal.addPersona(nodoHijo.getPersona());  // Añadir el hijo al nuevo grafo

                // Añadir relación hijo-padre (Father to) si no existe
                boolean conectado = false;
                for (int j = 0; j < nodoPersona.getHijos().len(); j++) {
                    NodoArbol hijoExistente = nodoPersona.getHijos().get(j);
                    if (hijoExistente.getPersona().getId().equals(nodoHijo.getPersona().getId())) {
                        grafosOriginal.addArco1(nodoPersona.getPersona().getId(), nodoHijo.getPersona().getId());
                        conectado = true;
                        break;
                    }
                }

                // Si no está conectado, añadir la relación y el arco
                if (!conectado) {
                    nodoPersona.agregarHijo(nodoHijo);
                    grafosOriginal.addArco1(nodoPersona.getPersona().getId(), nodoHijo.getPersona().getId()); // Se asume distancia 1
                }

                // Cuarto pase: Recursión para añadir los descendientes de los hijos
                agregarDescendientesRecursivos(nodoHijo, grafosOriginal); // Llamada recursiva para agregar los descendientes
            }
        }
    }

    // Método recursivo para agregar los descendientes de una persona (hijos, nietos, bisnietos, etc.)
    private void agregarDescendientesRecursivos(NodoArbol nodoPadre, Grafos grafosOriginal) {
        Lista<NodoArbol> hijos = nodoPadre.getHijos();  // Obtener los hijos del nodo

        if (hijos.getSize() > 0) {
            for (int i = 0; i < hijos.getSize(); i++) {
                NodoArbol nodoHijo = hijos.get(i);
                grafosOriginal.addPersona(nodoHijo.getPersona());  // Añadir al grafo

                // Añadir relación hijo-padre
                grafosOriginal.addArco1(nodoPadre.getPersona().getId(), nodoHijo.getPersona().getId());

                // Llamada recursiva para agregar los descendientes del hijo
                agregarDescendientesRecursivos(nodoHijo, grafosOriginal);
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

    // Llenar la lista de descendientes por nivel
    mostrarDescendientes(nodoInicial, 0, descendientesPorNivel);

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
            agregarAntepasadosRecursivos(nodoPadre, grafosOriginal); // Llamada recursiva para agregar los antepasados
        }
    }
}

private void agregarAntepasadosRecursivos(NodoArbol nodoHijo, Grafos grafosOriginal) {
    // Obtener los padres del nodo
    Lista<NodoArbol> padres = obtenerPadres(nodoHijo);

    if (padres.getSize() > 0) {
        for (int i = 0; i < padres.getSize(); i++) {
            NodoArbol nodoPadre = padres.get(i);
            grafosOriginal.addPersona(nodoPadre.getPersona());  // Añadir al grafo

            // Añadir relación padre-hijo
            grafosOriginal.addArco1(nodoPadre.getPersona().getId(), nodoHijo.getPersona().getId());

            // Llamada recursiva para agregar los antepasados del padre
            agregarAntepasadosRecursivos(nodoPadre, grafosOriginal);
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
                persona.toString(), // Utiliza el método `toString` para mostrar la información
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