package Objetos;

import Primitivas.Lista;

/**
 * Clase que representa una persona en el árbol genealógico.
 *
 * @version 4/11/2024
 */
public class Persona {
    private String id; // Identificador único
    private String nombre; // Nombre canónico
    private String ofHisName;
    private String apodo;
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
        this.bornTo = new Lista<>();
        this.hijos = new Lista<>();
        this.notas = new Lista<>();
        this.id = generarIdUnico();
    }

    // Método para generar un ID único basado en el nombre y ofHisName
    private String generarIdUnico() {
        String baseId = nombre;
        if (!ofHisName.isEmpty()) {
            baseId += ", " + ofHisName + " of his name";
        }
        return baseId;
    }

    // Getters y setters modificados para regenerar el ID cuando cambian los atributos relevantes

    public void setOfHisName(String ofHisName) {
        this.ofHisName = ofHisName;
        this.id = generarIdUnico();
    }

    public String getOfHisName() {
        return ofHisName;
    }

    // Resto de getters y setters...

    public String getId() {
        return id;
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
}
