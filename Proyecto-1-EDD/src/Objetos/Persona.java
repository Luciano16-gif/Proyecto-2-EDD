package Objetos;

import Primitivas.Lista;

/**
 * Clase que representa una Persona en el contexto de los datos de la Casa Baratheon.
 * Almacena la información básica y opcional de cada persona, como su título, padres, hijos, etc.
 * 
 * @author:
 * @version: 04/11/2024
 */
public class Persona {
    // Atributos de la clase (mismos que mencionaste anteriormente)
    private String nombre;
    private String ofHisName;
    private String bornTo; // Puede ser una lista, debe considerarse si hay múltiples padres.
    private String title;
    private String apodo; // 'Known throughout as' 
    private String wedTo;
    private String colorOjos;
    private String colorCabello;
    private String fate;
    private Lista<String> hijos; // Lista de hijos
    private Lista<String> notas; // Lista de notas

    public Persona(String nombre) {
        this.nombre = nombre;
        this.hijos = new Lista<>();
        this.notas = new Lista<>();
    }

    // Métodos setter y getter (omitidos para brevedad)

    /**
     * Método estático para construir un objeto Persona a partir del JSON.
     * 
     * @param nombre Nombre de la persona.
     * @param atributos Atributos de la persona extraídos del JSON.
     * @return Instancia de Persona construida.
     */
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
                    persona.ofHisName = valor;
                    break;
                case "Born to":
                    persona.bornTo = valor; // Se puede almacenar como un String, si hay múltiples, se puede ajustar.
                    break;
                case "Held title":
                    persona.title = valor;
                    break;
                case "Wed to":
                    persona.wedTo = valor;
                    break;
                case "Of eyes":
                    persona.colorOjos = valor;
                    break;
                case "Of hair":
                    persona.colorCabello = valor;
                    break;
                case "Father to":
                    String[] hijos = valor.replace("[", "").replace("]", "").split(","); // Eliminar corchetes y separar
                    for (String hijo : hijos) {
                        persona.addHijo(hijo.trim());
                    }
                    break;
                case "Known throughout as":
                    persona.apodo = valor;
                    break;
                case "Notes":
                    persona.addNota(valor);
                    break;
                case "Fate":
                    persona.fate = valor;
                    break;
                default:
                    break; // Ignorar claves desconocidas
            }
        }
    }
    return persona;
}


    public void addHijo(String hijo) {
        this.hijos.append(hijo);
    }

    public void addNota(String nota) {
        this.notas.append(nota);
    }

    @Override
    public String toString() {
        return "Persona{" +
               "nombre='" + nombre + '\'' +
               ", ofHisName='" + ofHisName + '\'' +
               ", bornTo='" + bornTo + '\'' +
               ", title='" + title + '\'' +
               ", wedTo='" + wedTo + '\'' +
               ", colorOjos='" + colorOjos + '\'' +
               ", colorCabello='" + colorCabello + '\'' +
               ", fate='" + fate + '\'' +
               ", hijos=" + hijos +
               ", notas=" + notas +
               '}';
    }

    // Modificaciones en equals y hashCode
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Persona)) return false;
        Persona persona = (Persona) obj;
        return this.nombre.equalsIgnoreCase(persona.nombre);
    }

    @Override
    public int hashCode() {
        return nombre.toLowerCase().hashCode();
    }
}
