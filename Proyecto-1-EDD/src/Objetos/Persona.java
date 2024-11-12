package Objetos;

import Primitivas.Lista;

/**
 * Clase que representa una persona en el árbol genealógico.

 */
public class Persona {
    private String nombre;
    private String apodo;
    private String ofHisName;
    private String title;
    private String wedTo;
    private String colorOjos;
    private String colorCabello;
    private String fate;
    private Lista<String> bornTo; // Lista de nombres de los padres
    private Lista<String> hijos; // Lista de nombres de los hijos
    private Lista<String> notas; // Lista de notas
  
  
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
    }

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApodo() {
        return apodo;
    }

    public String getId() {
        return id;
    }
    
    public String getApodo() {
        return apodo;
    }

    public String getNombre() {
        return nombre;
    }

    public Lista<String> getBornTo() {
        return bornTo;
    }

    public void addBornTo(String nombrePadre) {
        if (!bornTo.contains(nombrePadre)) {
            bornTo.append(nombrePadre);
        }
    }

    public Lista<String> getHijos() {
        return hijos;
    }

    public void addHijo(String nombreHijo) {
        if (!hijos.contains(nombreHijo)) {
            hijos.append(nombreHijo);
        }
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

    public Lista<String> getNotas() {
        return notas;
    }

    public void addNota(String nota) {
        this.notas.append(nota);
    }

    /**
     * Genera un ID único basado en el nombre y "Of his name".
     *
     * @return ID único de la persona.
     */
    public String getId() {
        StringBuilder idBuilder = new StringBuilder(nombre);
        if (ofHisName != null && !ofHisName.isEmpty()) {
            idBuilder.append(", ").append(ofHisName).append(" of his name");
        }
        return idBuilder.toString();
    }
    
    
}
