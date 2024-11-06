package Objetos;

import Primitivas.HashTable;
import Primitivas.Lista;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.swing.JFileChooser;
import java.io.File;
import java.io.FileReader;

/**
 * Clase que contiene funciones útiles para el proyecto, incluyendo la lectura del archivo JSON.
 *
 * @version 4/11/2024
 */
public class Funcion {

    public static DatosProyecto leerJsonConFileChooser() {
        Lista<Persona> personas = new Lista<>();
        HashTable<String, Persona> hashTable = new HashTable<>(); // Tabla hash para búsqueda rápida

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccione el archivo JSON");
        int userSelection = fileChooser.showOpenDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToOpen = fileChooser.getSelectedFile();
            try (FileReader reader = new FileReader(fileToOpen)) {
                Gson gson = new Gson();
                JsonElement jsonElement = gson.fromJson(reader, JsonElement.class);

                if (jsonElement.isJsonObject()) {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();

                    // Procesar cada entrada principal en el JSON como un grupo de personas
                    for (String key : jsonObject.keySet()) {
                        JsonElement groupElement = jsonObject.get(key);

                        if (groupElement.isJsonArray()) {
                            com.google.gson.JsonArray groupArray = groupElement.getAsJsonArray();

                            // Iterar sobre cada persona en el array
                            for (int i = 0; i < groupArray.size(); i++) {
                                JsonElement personElement = groupArray.get(i);

                                if (personElement.isJsonObject()) {
                                    JsonObject personObject = personElement.getAsJsonObject();

                                    // Cada personObject tiene una clave (nombre de la persona)
                                    for (String nombrePersona : personObject.keySet()) {
                                        JsonElement atributosElement = personObject.get(nombrePersona);

                                        // Crear un objeto Persona
                                        Persona persona = new Persona(nombrePersona);

                                        // Procesar los atributos de cada persona
                                        if (atributosElement.isJsonArray()) {
                                            com.google.gson.JsonArray atributosArray = atributosElement.getAsJsonArray();

                                            for (int j = 0; j < atributosArray.size(); j++) {
                                                JsonElement atributoElement = atributosArray.get(j);

                                                if (atributoElement.isJsonObject()) {
                                                    JsonObject atributoObj = atributoElement.getAsJsonObject();

                                                    // Iterar sobre cada clave de atributo y asignar a la persona
                                                    for (String attributeKey : atributoObj.keySet()) {
                                                        JsonElement valueElement = atributoObj.get(attributeKey);

                                                        // Procesar los atributos
                                                        asignarAtributo(persona, attributeKey, valueElement);
                                                    }
                                                }
                                            }
                                        }

                                        // Agregar la persona a la lista y a la tabla hash
                                        personas.append(persona);
                                        hashTable.put(persona.getId(), persona);
                                    }
                                }
                            }
                        } else {
                            System.out.println("La entrada '" + key + "' no es un array de personas.");
                        }
                    }
                } else {
                    System.out.println("El archivo JSON seleccionado no tiene el formato esperado.");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No se seleccionó ningún archivo.");
        }

        return new DatosProyecto(personas, hashTable);
    }

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
            // Resto de casos...
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
            // Agregar otros casos si es necesario
            default:
                // Manejar otros atributos
                break;
        }
    }
}
