package Objetos;

import Primitivas.Lista;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JFileChooser;

public class Funcion {

    public static Lista<Persona> leerJsonConFileChooser() {
        Lista<Persona> personas = new Lista<>();

        try {
            // Abrir un JFileChooser para seleccionar el archivo JSON
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Seleccione el archivo JSON del árbol genealógico");

            int userSelection = fileChooser.showOpenDialog(null);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File jsonFile = fileChooser.getSelectedFile();

                // Leer el contenido del archivo JSON
                BufferedReader in = new BufferedReader(new FileReader(jsonFile));
                StringBuilder content = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine.trim()); // Eliminar espacios en blanco al inicio y fin
                }
                in.close();

                // Convertir el contenido en una cadena
                String jsonString = content.toString();

                // Parsear el JSON
                personas = parseJsonString(jsonString);

            } else {
                System.out.println("No se seleccionó ningún archivo.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return personas;
    }

    private static Lista<Persona> parseJsonString(String jsonString) {
        Lista<Persona> personas = new Lista<>();

        try {
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);

            // Iterar sobre las casas en el JSON (por si hay más de una)
            for (String houseName : jsonObject.keySet()) {
                JsonArray houseArray = jsonObject.getAsJsonArray(houseName);

                // Iterar sobre cada persona en la casa
                for (JsonElement personElement : houseArray) {
                    JsonObject personObject = personElement.getAsJsonObject();

                    // Cada persona tiene su nombre como clave
                    for (String personName : personObject.keySet()) {
                        JsonArray attributesArray = personObject.getAsJsonArray(personName);

                        // Crear una nueva instancia de Persona
                        Persona persona = new Persona(personName);

                        // Recorrer los atributos de la persona
                        for (JsonElement attributeElement : attributesArray) {
                            JsonObject attributeObject = attributeElement.getAsJsonObject();

                            // Cada atributo tiene una clave y un valor
                            for (String attributeKey : attributeObject.keySet()) {
                                JsonElement valueElement = attributeObject.get(attributeKey);

                                switch (attributeKey) {
                                    case "Of his name":
                                        persona.setOfHisName(valueElement.getAsString());
                                        break;
                                    case "Born to":
                                        persona.setBornTo(valueElement.getAsString());
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
                                        // Manejar lista de hijos
                                        if (valueElement.isJsonArray()) {
                                            JsonArray hijosArray = valueElement.getAsJsonArray();
                                            for (JsonElement hijoElement : hijosArray) {
                                                String hijoNombre = hijoElement.getAsString();
                                                persona.addHijo(hijoNombre);
                                            }
                                        }
                                        break;
                                    case "Notes":
                                        persona.addNota(valueElement.getAsString());
                                        break;
                                    // Puedes agregar otros casos si hay más atributos
                                    default:
                                        // Manejar otros atributos si es necesario
                                        break;
                                }
                            }
                        }

                        // Agregar la persona a la lista
                        personas.append(persona);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return personas;
    }
}
