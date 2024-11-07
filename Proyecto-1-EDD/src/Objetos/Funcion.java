package Objetos;

import Objetos.DatosProyecto;
import Objetos.JsonArray;
import Objetos.Persona;
import Primitivas.HashTable;
import Primitivas.Lista;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javax.swing.JFileChooser;
import java.io.File;
import java.io.FileReader;

public class Funcion {

    public static DatosProyecto leerJsonConFileChooser() {
    Lista<Persona> personas = new Lista<>();
    HashTable<String, Persona> hashTable = new HashTable<>();
    Lista<String> relaciones = new Lista<>(); // Lista para guardar relaciones de nombre-completo:padre/hijo

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

                for (String key : jsonObject.keySet()) {
                    JsonElement groupElement = jsonObject.get(key);

                    if (groupElement.isJsonArray()) {
                        com.google.gson.JsonArray groupArray = groupElement.getAsJsonArray();

                        for (int i = 0; i < groupArray.size(); i++) {
                            JsonElement personElement = groupArray.get(i);

                            if (personElement.isJsonObject()) {
                                JsonObject personObject = personElement.getAsJsonObject();

                                for (String nombrePersona : personObject.keySet()) {
                                    JsonElement atributosElement = personObject.get(nombrePersona);
                                    Persona persona = new Persona(nombrePersona);

                                    String nombreCompleto = nombrePersona; // Inicializa con el nombre original
                                    boolean tieneAlias = false; // Para saber si ya se asignó un alias

                                    // Procesamos los atributos
                                    if (atributosElement.isJsonArray()) {
                                        com.google.gson.JsonArray atributosArray = atributosElement.getAsJsonArray();
                                        for (int j = 0; j < atributosArray.size(); j++) {
                                            JsonElement atributoElement = atributosArray.get(j);

                                            if (atributoElement.isJsonObject()) {
                                                JsonObject atributoObj = atributoElement.getAsJsonObject();

                                                for (String attributeKey : atributoObj.keySet()) {
                                                    JsonElement valueElement = atributoObj.get(attributeKey);

                                                    // Llamada a la función que maneja la asignación de atributos y relaciones
                                                    asignarAtributo(persona, attributeKey, valueElement, relaciones, nombreCompleto, nombrePersona);

                                                    // Si se encuentra el alias "Known throughout as", se actualiza el nombre completo
                                                    if (attributeKey.equals("Known throughout as") && !tieneAlias) {
                                                        nombreCompleto = valueElement.getAsString();  // Actualiza el nombre completo con el alias
                                                        tieneAlias = true; // Marca que ya se ha asignado el alias
                                                    }
                                                    
                                                    // Ahora, si encontramos "Of his name" solo agregamos el sufijo si no tiene alias
                                                    if (attributeKey.equals("Of his name") && !tieneAlias) {
                                                        nombreCompleto += ", " + valueElement.getAsString() + " of his name"; // Agrega "of his name"
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // Ahora que nombreCompleto ha sido actualizado, puedes agregar las relaciones
                                    personas.append(persona);
                                    hashTable.put(persona.getId(), persona);
                                }
                            }
                        }
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

    // Imprimir las relaciones
    for (int i = 0; i < relaciones.getSize(); i++) {
        System.out.println(relaciones.get(i));
    }

    return new DatosProyecto(personas, hashTable, relaciones);
}

private static void asignarAtributo(Persona persona, String attributeKey, JsonElement valueElement, Lista<String> relaciones, String nombreCompleto, String nombrePersona) {
    // Usamos un arreglo manual para almacenar las relaciones agregadas, evitamos duplicados.
    Lista<String> relacionesAgregadas = new Lista<String>();  // Suponiendo que Lista tiene el método `append`
    
    switch (attributeKey) {
        case "Of his name":
            persona.setOfHisName(valueElement.getAsString());
            break;
        case "Father to":
            // Verificar y agregar hijos, evitando duplicados
            if (valueElement.isJsonArray()) {
                com.google.gson.JsonArray hijosArray = valueElement.getAsJsonArray();
                for (JsonElement hijo : hijosArray) {
                    String hijoNombre = hijo.getAsString() + " " + nombrePersona.split(" ")[1];
                    persona.addHijo(hijoNombre);
                    String relacion = nombreCompleto + " : " + hijoNombre;
                    if (!relaciones.contains(relacion)) {
                        relaciones.append(relacion);  // Guardamos la relación
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
                        relaciones.append(nombreCompleto + " : " + padreNombre);
                        isPadre = false; // Después de agregar el padre, agregar la madre si existe
                    } else {
                        // Si ya es el padre, se agrega la madre (si existe)
                        relaciones.append(nombreCompleto + " : " + padreNombre);
                    }
                }
            } else {
                String padreNombre = valueElement.getAsString();
                persona.addBornTo(padreNombre);
                relaciones.append(nombreCompleto + " : " + padreNombre);
            }
            break;
        default:
            break;
    }
}
}
