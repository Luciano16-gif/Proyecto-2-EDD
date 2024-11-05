package Funciones;

import Objetos.Persona;
import Primitivas.Lista;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.swing.JFileChooser;
import java.io.File;
import java.io.FileReader;
import java.util.Set;

/**
 * Clase que contiene funciones útiles para el proyecto, incluyendo la lectura del archivo JSON.
 *
 * @author ...
 * @version 4/11/2024
 */
public class Funcion {

    /**
     * Método para leer el archivo JSON seleccionado por el usuario y construir una lista de personas.
     *
     * @return Lista de objetos Persona construidos a partir del archivo JSON.
     */
    public static Lista<Persona> leerJsonConFileChooser() {
        Lista<Persona> personas = new Lista<>();
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccione el archivo JSON");
        int userSelection = fileChooser.showOpenDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToOpen = fileChooser.getSelectedFile();
            try (FileReader reader = new FileReader(fileToOpen)) {
                Gson gson = new Gson();
                JsonElement jsonElement = gson.fromJson(reader, JsonElement.class);
                JsonObject jsonObject = jsonElement.getAsJsonObject();

                // Obtener el array de "House Baratheon"
                JsonElement houseElement = jsonObject.get("House Baratheon");
                if (houseElement != null && houseElement.isJsonArray()) {
                    com.google.gson.JsonArray houseArray = houseElement.getAsJsonArray();

                    // Iterar sobre cada persona en el array
                    for (int i = 0; i < houseArray.size(); i++) {
                        JsonElement personElement = houseArray.get(i);
                        if (personElement.isJsonObject()) {
                            JsonObject personObject = personElement.getAsJsonObject();

                            // Cada personObject tiene una clave (nombre de la persona)
                            Set<String> personNames = personObject.keySet();
                            for (String nombrePersona : personNames) {
                                JsonElement atributosElement = personObject.get(nombrePersona);

                                // Crear un objeto Persona
                                Persona persona = new Persona(nombrePersona);

                                // Procesar los atributos
                                if (atributosElement.isJsonArray()) {
                                    com.google.gson.JsonArray atributosArray = atributosElement.getAsJsonArray();

                                    for (int j = 0; j < atributosArray.size(); j++) {
                                        JsonElement atributoElement = atributosArray.get(j);

                                        if (atributoElement.isJsonObject()) {
                                            JsonObject atributoObj = atributoElement.getAsJsonObject();

                                            // Obtener las claves del atributoObj
                                            Set<String> atributoKeys = atributoObj.keySet();

                                            for (String attributeKey : atributoKeys) {
                                                JsonElement valueElement = atributoObj.get(attributeKey);

                                                // Procesar los atributos como antes
                                                asignarAtributo(persona, attributeKey, valueElement);
                                            }
                                        }
                                    }
                                }

                                // Agregar la persona a la lista
                                personas.append(persona);
                            }
                        }
                    }
                } else {
                    System.out.println("No se encontró 'House Baratheon' o no es un array.");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return personas;
    }

    // Método auxiliar para asignar atributos a la persona
    private static void asignarAtributo(Persona persona, String attributeKey, JsonElement valueElement) {
        switch (attributeKey) {
            case "Of his name":
                persona.setOfHisName(valueElement.getAsString());
                break;
            case "Born to":
                if (valueElement.isJsonArray()) {
                    com.google.gson.JsonArray bornToArray = valueElement.getAsJsonArray();
                    for (int j = 0; j < bornToArray.size(); j++) {
                        String padre = bornToArray.get(j).getAsString();
                        persona.addBornTo(padre);
                    }
                } else {
                    persona.addBornTo(valueElement.getAsString());
                }
                break;
            case "Known throughout as":
                persona.setApodo(valueElement.getAsString());
                break;
            case "Held title":
                persona.setTitle(valueElement.getAsString());
                break;
            case "Wed to":
                persona.setWedTo(valueElement.getAsString());
                break;
            case "Of eyes":
                persona.setColorOjos(valueElement.getAsString());
                break;
            case "Of hair":
                persona.setColorCabello(valueElement.getAsString());
                break;
            case "Fate":
                persona.setFate(valueElement.getAsString());
                break;
            case "Father to":
                if (valueElement.isJsonArray()) {
                    com.google.gson.JsonArray hijosArray = valueElement.getAsJsonArray();
                    for (int k = 0; k < hijosArray.size(); k++) {
                        String hijoNombre = hijosArray.get(k).getAsString();
                        persona.addHijo(hijoNombre);
                    }
                } else {
                    persona.addHijo(valueElement.getAsString());
                }
                break;
            case "Notes":
                persona.addNota(valueElement.getAsString());
                break;
            // Agregar otros casos si es necesario
            default:
                // Manejar otros atributos
                break;
        }
    }
}
