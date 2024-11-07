package Objetos;

import Primitivas.Lista;

public class Persona {
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

    public void setApodo(String apodo) {
        this.apodo = apodo;
    }

    public String getOfHisName() {
        return ofHisName;
    }

    public void setOfHisName(String ofHisName) {
        this.ofHisName = ofHisName;
    }

    public String getId() {
        // Genera un ID único basado en el nombre y "Of his name"
        if (ofHisName != null && !ofHisName.isEmpty()) {
            return nombre + ", " + ofHisName + " of his name";
        } else {
            return nombre;
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

    public Lista<String> getBornTo() {
        return bornTo;
    }

    public void addBornTo(String padre) {
        this.bornTo.append(padre);
    }

    public Lista<String> getHijos() {
        return hijos;
    }

    public void addHijo(String hijo) {
        this.hijos.append(hijo);
    }

    public Lista<String> getNotas() {
        return notas;
    }

    public void addNota(String nota) {
        this.notas.append(nota);
    }
}
