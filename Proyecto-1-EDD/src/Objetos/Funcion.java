package Objetos;

import Primitivas.HashTable;
import Primitivas.Lista;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javax.swing.JFileChooser;
import java.io.File;
import java.io.FileReader;

public class Funcion {

    /**
     * Lee un archivo JSON seleccionado por el usuario y lo procesa en objetos Persona.
     *
     * @return DatosProyecto que encapsula la lista de personas y la tabla hash.
     */
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

                                    // Ahora que nombreCompleto ha sido actualizado, puedes agregar las relaciones
                                    personas.append(persona);
                                    hashTable.put(persona.getId(), persona);
                                }
                            }
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No se seleccionó ningún archivo.");
        }

        return new DatosProyecto(personas, hashTable);
    }

    /**
     * Asigna un atributo específico a una persona basado en la clave del atributo.
     *
     * @param persona       Persona a la que se le asignará el atributo.
     * @param attributeKey  Clave del atributo.
     * @param valueElement  Valor del atributo.
     */
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
                }
            } else {
                String hijoNombre = valueElement.getAsString() + " " + nombrePersona.split(" ")[1];
                persona.addHijo(hijoNombre);
                String relacion = nombreCompleto + " : " + hijoNombre;
                if (!relaciones.contains(relacion)) {
                    relaciones.append(relacion);  // Guardamos la relación
                }
            }
            break;
        case "Born to":
            // Agregar las relaciones con los padres usando el nombre completo actualizado
            if (valueElement.isJsonArray()) {
                com.google.gson.JsonArray bornToArray = valueElement.getAsJsonArray();
                // Primero el padre, luego la madre (si existe)
                boolean isPadre = true; // Flag para asegurar que el padre se agregue primero
                for (JsonElement padre : bornToArray) {
                    String padreNombre = padre.getAsString();
                    persona.addBornTo(padreNombre);
                    if (isPadre) {
                        // Agregar la relación de padre primero
                        relaciones.append(padreNombre + " : " + nombreCompleto);
                        isPadre = false; // Después de agregar el padre, agregar la madre si existe
                    } else {
                        // Si ya es el padre, se agrega la madre (si existe)
                        relaciones.append(padreNombre + " : " + nombreCompleto);
                    }
                }
            } else {
                String padreNombre = valueElement.getAsString();
                persona.addBornTo(padreNombre);
                relaciones.append(padreNombre + " : " + nombreCompleto);
            }
            break;
        default:
            break;
    }
}
}
