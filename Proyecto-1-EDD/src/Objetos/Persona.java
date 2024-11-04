package Objetos;

import Primitivas.Lista;

/**
 * Clase que representa una Persona en el contexto de los datos genealógicos.
 * Almacena la información básica y opcional de cada persona, como su título, padres, hijos, etc.
 */
public class Persona {
    // Atributos de la clase
    private String nombre;
    private String ofHisName;     // Numeral del nombre, e.g., "First", "Second"
    private String bornTo;        // Nombre del padre
    private String apodo;         // "Known throughout as"
    private String title;         // Título nobiliario
    private String wedTo;         // Con quién está casado
    private String colorOjos;     // Color de ojos
    private String colorCabello;  // Color de cabello
    private String fate;          // Destino o causa de muerte
    private Lista<String> hijos;  // Lista de nombres de hijos
    private Lista<String> notas;  // Lista de notas adicionales

    // Constructor
    public Persona(String nombre) {
        this.nombre = nombre;
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

    public String getOfHisName() {
        return ofHisName;
    }

    public void setOfHisName(String ofHisName) {
        this.ofHisName = ofHisName;
    }

    public String getBornTo() {
        return bornTo;
    }

    public void setBornTo(String bornTo) {
        this.bornTo = bornTo;
    }

    public String getApodo() {
        return apodo;
    }

    public void setApodo(String apodo) {
        this.apodo = apodo;
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

    public Lista<String> getHijos() {
        return hijos;
    }

    public void setHijos(Lista<String> hijos) {
        this.hijos = hijos;
    }

    public Lista<String> getNotas() {
        return notas;
    }

    public void setNotas(Lista<String> notas) {
        this.notas = notas;
    }

    // Métodos para agregar hijos y notas
    public void addHijo(String hijo) {
        this.hijos.append(hijo);
    }

    public void addNota(String nota) {
        this.notas.append(nota);
    }

    // Método estático para construir una Persona a partir de los datos JSON
    public static Persona fromJson(String nombre, Lista<Lista<String[]>> atributos) {
        Persona persona = new Persona(nombre);

        for (int i = 0; i < atributos.getSize(); i++) {
            Lista<String[]> atributo = atributos.get(i);
            for (int j = 0; j < atributo.getSize(); j++) {
                String[] par = atributo.get(j);
                String clave = par[0];
                String valor = par[1];

                switch (clave) {
                    case "Of his name":
                        persona.setOfHisName(valor);
                        break;
                    case "Born to":
                        persona.setBornTo(valor);
                        break;
                    case "Known throughout as":
                        persona.setApodo(valor);
                        break;
                    case "Held title":
                        persona.setTitle(valor);
                        break;
                    case "Wed to":
                        persona.setWedTo(valor);
                        break;
                    case "Of eyes":
                        persona.setColorOjos(valor);
                        break;
                    case "Of hair":
                        persona.setColorCabello(valor);
                        break;
                    case "Fate":
                        persona.setFate(valor);
                        break;
                    case "Father to":
                        // Suponiendo que los hijos están separados por comas
                        String[] hijosArray = valor.split(",");
                        for (String hijo : hijosArray) {
                            persona.addHijo(hijo.trim());
                        }
                        break;
                    case "Notes":
                        persona.addNota(valor);
                        break;
                    default:
                        // Puedes manejar otros casos o ignorarlos
                        break;
                }
            }
        }

        return persona;
    }

    // Representación en String de la persona
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Nombre: ").append(nombre).append("\n");
        if (ofHisName != null) sb.append("Of his name: ").append(ofHisName).append("\n");
        if (bornTo != null) sb.append("Born to: ").append(bornTo).append("\n");
        if (apodo != null) sb.append("Known throughout as: ").append(apodo).append("\n");
        if (title != null) sb.append("Held title: ").append(title).append("\n");
        if (wedTo != null) sb.append("Wed to: ").append(wedTo).append("\n");
        if (colorOjos != null) sb.append("Of eyes: ").append(colorOjos).append("\n");
        if (colorCabello != null) sb.append("Of hair: ").append(colorCabello).append("\n");
        if (fate != null) sb.append("Fate: ").append(fate).append("\n");
        if (hijos != null && hijos.getSize() > 0) {
            sb.append("Father to: ").append("\n");
            for (int i = 0; i < hijos.getSize(); i++) {
                sb.append("  - ").append(hijos.get(i)).append("\n");
            }
    }
        
    if (notas != null && notas.getSize() > 0) {
        sb.append("Notes: ").append("\n");
        for (int i = 0; i < notas.getSize(); i++) {
            sb.append("  - ").append(notas.get(i)).append("\n");
        }
    }
    return sb.toString();
}


    // Métodos equals y hashCode para uso en tablas hash
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Persona)) return false;
        Persona otraPersona = (Persona) obj;
        return this.nombre.equalsIgnoreCase(otraPersona.nombre);
    }

    @Override
    public int hashCode() {
        return nombre.toLowerCase().hashCode();
    }
}
