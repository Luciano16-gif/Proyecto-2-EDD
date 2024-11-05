package Objetos;

import Primitivas.Lista;

/**
 * Clase que representa una persona en el árbol genealógico.
 *
 * @author ...
 * @version 4/11/2024
 */
public class Persona {
    private String id; // Identificador único
    private String nombre; // Nombre canónico
    private Lista<String> nombresAlternativos; // Nombres alternativos, incluyendo apodos y variaciones
    private Lista<String> bornTo; // Lista de nombres de los padres
    private Lista<String> hijos; // Lista de nombres de los hijos
    private String ofHisName;
    private String apodo;
    private String title;
    private String wedTo;
    private String colorOjos;
    private String colorCabello;
    private String fate;
    private Lista<String> notas; // Lista de notas

    public Persona(String nombre) {
        this.nombre = nombre;
        this.apodo = ""; // Inicializar apodo antes de llamar a generarIdUnico()
        this.ofHisName = ""; // Inicializar ofHisName antes de llamar a generarIdUnico()
        this.nombresAlternativos = new Lista<>();
        this.bornTo = new Lista<>();
        this.hijos = new Lista<>();
        this.notas = new Lista<>();
        this.id = generarIdUnico(); // Llamar a generarIdUnico() después de inicializar los atributos
    }

    // Método para generar un ID único basado en el nombre, apodo y ofHisName
    private String generarIdUnico() {
        String baseId = nombre.toLowerCase().replaceAll("\\s+", "_");
        if (!apodo.isEmpty()) {
            baseId += "_" + apodo.toLowerCase().replaceAll("\\s+", "_");
        } else if (!ofHisName.isEmpty()) {
            baseId += "_of_his_name_" + ofHisName.toLowerCase().replaceAll("\\s+", "_");
        }
        return baseId;
    }

    // Métodos para manejar los atributos adicionales

    public void setOfHisName(String ofHisName) {
        this.ofHisName = ofHisName;
        // Actualizar el ID ya que ofHisName ha cambiado
        this.id = generarIdUnico();
    }

    public String getOfHisName() {
        return ofHisName;
    }

    public void setApodo(String apodo) {
        this.apodo = apodo;
        agregarNombreAlternativo(apodo); // Agregar el apodo a los nombres alternativos
        // Actualizar el ID ya que el apodo ha cambiado
        this.id = generarIdUnico();
    }

    public String getApodo() {
        return apodo;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setWedTo(String wedTo) {
        this.wedTo = wedTo;
    }

    public String getWedTo() {
        return wedTo;
    }

    public void setColorOjos(String colorOjos) {
        this.colorOjos = colorOjos;
    }

    public String getColorOjos() {
        return colorOjos;
    }

    public void setColorCabello(String colorCabello) {
        this.colorCabello = colorCabello;
    }

    public String getColorCabello() {
        return colorCabello;
    }

    public void setFate(String fate) {
        this.fate = fate;
    }

    public String getFate() {
        return fate;
    }

    public void addNota(String nota) {
        notas.append(nota);
    }

    public Lista<String> getNotas() {
        return notas;
    }

    // Métodos para agregar padres e hijos
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

    // Método para agregar nombres alternativos
    public void agregarNombreAlternativo(String nombreAlternativo) {
        if (!nombresAlternativos.contains(nombreAlternativo)) {
            nombresAlternativos.append(nombreAlternativo);
        }
    }

    // Getters y setters básicos
    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Lista<String> getNombresAlternativos() {
        return nombresAlternativos;
    }

    public Lista<String> getBornTo() {
        return bornTo;
    }

    public Lista<String> getHijos() {
        return hijos;
    }
}
