package Objetos;

import Primitivas.Lista;
import Primitivas.Lista.ListaIterator;

/**
 * Clase que representa una persona en el árbol genealógico.
 * 
 * @author: Ricardo Paez - Luciano Minardo - Gabriele Colarusso
 * 
 * @version 21/11/2024
 * 
 */
public class Persona {
    private String id;
    private String nombre;
    private String apodo;
    private String ofHisName;
    private String title;
    private String wedTo;
    private String colorOjos;
    private String colorCabello;
    private String fate;
    private Lista<String> bornTo;
    private Lista<String> hijos;
    private Lista<String> notas;
     private Lista<Persona> fatherTo;

    /**
     * Constructor de la clase Persona.
     *
     * @param nombre Nombre de la persona.
     */
    public Persona(String nombre) {
        this.nombre = nombre;
        this.apodo = "";
        this.ofHisName = "";
        this.title = "";
        this.wedTo = "";
        this.colorOjos = "";
        this.colorCabello = "";
        this.fate = "";
        this.bornTo = new Lista<>();
        this.hijos = new Lista<>();
        this.notas = new Lista<>();
        this.fatherTo = new Lista<>();
    }

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }
    
    public String getId() {
        if (id != null && !id.isEmpty()) {
            return id;
        } else {
            // Always use a combination of name and 'Of His Name' for uniqueness
            String uniqueId = nombre;
            if (ofHisName != null && !ofHisName.isEmpty()) {
                uniqueId += ", " + ofHisName + " of his name";
            }
            return uniqueId;
        }
    }

    // Setter para el campo 'id'
    public void setId(String id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApodo() {
        return apodo;
    }

    public void setApodo(String apodo) {
        this.apodo = apodo;
    }

    public String getOfHisName() {
        return ofHisName;
    }

    public void setOfHisName(String ofHisName) {
        this.ofHisName = ofHisName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWedTo() {
        return wedTo;
    }

    public void setWedTo(String wedTo) {
        this.wedTo = wedTo;
    }

    public String getColorOjos() {
        return colorOjos;
    }

    public void setColorOjos(String colorOjos) {
        this.colorOjos = colorOjos;
    }

    public String getColorCabello() {
        return colorCabello;
    }

    public void setColorCabello(String colorCabello) {
        this.colorCabello = colorCabello;
    }

    public String getFate() {
        return fate;
    }

    public void setFate(String fate) {
        this.fate = fate;
    }

    public Lista<String> getBornTo() {
        return bornTo;
    }
    
    public Lista<Persona> getFatherTo() {
        return fatherTo;
    }
    
    public void addFatherTo(Persona hijo) {
        if (!fatherTo.contains(hijo)) {
            fatherTo.append(hijo);
        }
    }

    public void addBornTo(String nombrePadre) {
        if (!bornTo.contains(nombrePadre)) {
            bornTo.append(nombrePadre);
        }
    }


    public void addHijo(String nombreHijo) {
        if (!hijos.contains(nombreHijo)) {
            hijos.append(nombreHijo);
        }
    }
    
    public void addBornTo2(String padre) {
    // Comprobar si todos los atributos (excepto el nombre) están vacíos o nulos
    if ((apodo == null || apodo.isEmpty()) &&
        (ofHisName == null || ofHisName.isEmpty()) &&
        (title == null || title.isEmpty()) &&
        (wedTo == null || wedTo.isEmpty()) &&
        (colorOjos == null || colorOjos.isEmpty()) &&
        (colorCabello == null || colorCabello.isEmpty()) &&
        (fate == null || fate.isEmpty()) &&
        (notas == null || notas.isEmpty()) &&
        (hijos == null || hijos.isEmpty())) {
        
        // Si todos los demás atributos están vacíos o nulos, agregar el padre a bornTo
        if (bornTo == null) {
            bornTo = new Lista<>();
        }
        bornTo.append(padre);
    }
    }   


    public Lista<String> getHijos() {
        return hijos;
    }


    public Lista<String> getNotas() {
        return notas;
    }

    public void addNota(String nota) {
        this.notas.append(nota);
    }
    
    @Override
    public String toString() {
        // Inicia la representación de la persona
        StringBuilder sb = new StringBuilder();

        // Agregar "Nombre"
        sb.append("Nombre: '").append(nombre).append("'\n");

        // Agregar "Of his name" si existe
        if (ofHisName != null && !ofHisName.isEmpty()) {
            sb.append("Of His Name: '").append(ofHisName).append("'\n");
        }

        // Agregar "Apodo" si existe
        if (apodo != null && !apodo.isEmpty()) {
            sb.append("Apodo: '").append(apodo).append("'\n");
        }

        // Agregar "Título" si existe
        if (title != null && !title.isEmpty()) {
            sb.append("Título: '").append(title).append("'\n");
        }

        // Agregar "Casado con" si existe
        if (wedTo != null && !wedTo.isEmpty()) {
            sb.append("Casado con: '").append(wedTo).append("'\n");
        }

        // Agregar "Color de ojos" si existe
        if (colorOjos != null && !colorOjos.isEmpty()) {
            sb.append("Color de ojos: '").append(colorOjos).append("'\n");
        }

        // Agregar "Color de cabello" si existe
        if (colorCabello != null && !colorCabello.isEmpty()) {
            sb.append("Color de cabello: '").append(colorCabello).append("'\n");
        }

        // Agregar "Destino" si existe
        if (fate != null && !fate.isEmpty()) {
            sb.append("Destino: '").append(fate).append("'\n");
        }

        // Agregar "Notas" si existe
        if (notas != null && !notas.isEmpty()) {
            sb.append("Notas: '").append(notas).append("'\n");
        }

        // Siempre mostrar los padres (bornTo), aunque esté vacío
        if (bornTo != null && !bornTo.isEmpty()) {
            sb.append("Nacido de:\n");

            // Usar el iterador de Lista
            ListaIterator iterator = bornTo.iterator();
            while (iterator.hasNext()) {
                String padre = (String) iterator.next();
                sb.append("  - ").append(padre).append("\n");
            }
        }

        // Agregar "Hijos" si existen
        if (hijos != null && !hijos.isEmpty()) {
            sb.append("Hijos:\n");

            // Usar el iterador de Lista para hijos
            ListaIterator iterator = hijos.iterator();
            while (iterator.hasNext()) {
                String hijo = (String) iterator.next();
                sb.append("  - ").append(hijo).append("\n");
            }
        }

        return sb.toString();  // Devolver la cadena final
    }
}
