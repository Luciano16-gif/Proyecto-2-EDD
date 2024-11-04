package Objetos;

import Primitivas.Lista;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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

            // Iterar sobre las casas en el JSON
            for (String houseName : jsonObject.keySet()) {
                JsonElement houseElement = jsonObject.get(houseName);

                // Convertir el elemento de la casa en un JsonArray personalizado
                JsonArray houseArray = parseJsonElementToJsonArray(houseElement);

                // Iterar sobre cada persona en la casa
                for (int i = 0; i < houseArray.size(); i++) {
                    JsonObject personObject = houseArray.get(i).getAsJsonObject();

                    for (String personName : personObject.keySet()) {
                        JsonElement attributesElement = personObject.get(personName);

                        // Crear una nueva instancia de Persona
                        Persona persona = new Persona(personName);

                        // Convertir los atributos en un JsonArray personalizado
                        JsonArray attributesArray = parseJsonElementToJsonArray(attributesElement);

                        // Recorrer los atributos
                        for (int j = 0; j < attributesArray.size(); j++) {
                            JsonObject attributeObject = attributesArray.get(j).getAsJsonObject();

                            for (String attributeKey : attributeObject.keySet()) {
                                JsonElement valueElement = attributeObject.get(attributeKey);

                                switch (attributeKey) {
                                    case "Of his name":
                                        persona.setOfHisName(valueElement.getAsString());
                                        break;
                                    case "Born to":
                                        persona.addBornTo(valueElement.getAsString());
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
                                        JsonArray hijosArray = parseJsonElementToJsonArray(valueElement);
                                        for (int k = 0; k < hijosArray.size(); k++) {
                                            String hijoNombre = hijosArray.get(k).getAsString();
                                            persona.addHijo(hijoNombre);
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

    // Método para convertir un JsonElement en un JsonArray personalizado
    private static JsonArray parseJsonElementToJsonArray(JsonElement element) {
        JsonArray jsonArray = new JsonArray();

        if (element.isJsonArray()) {
            // Iterar sobre los elementos del array sin usar JsonArray de Gson
            for (JsonElement el : element.getAsJsonArray()) {
                jsonArray.add(el);
            }
        } else if (element.isJsonObject()) {
            jsonArray.add(element);
        } else {
            // Si es un elemento simple
            jsonArray.add(element);
        }

        return jsonArray;
    }
}
