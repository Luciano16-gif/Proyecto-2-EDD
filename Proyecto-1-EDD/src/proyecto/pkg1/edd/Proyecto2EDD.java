package proyecto.pkg1.edd;

import Objetos.Funcion;
import Objetos.ArbolGenealogico;
import Objetos.Grafos;
import Objetos.DatosProyecto;
import Objetos.Persona;
import Primitivas.Lista;
import Primitivas.HashTable;

/**
 * Clase que representa el main.
 *
 * @version 4/11/2024
 */
public class Proyecto2EDD {

    public static void main(String[] args) {
\\
        // Leer las personas y relaciones desde el JSON usando la clase Funcion
        DatosProyecto datos = Funcion.leerJsonConFileChooser();

        if (datos != null) {
            Lista<Persona> personas = datos.getPersonas();
            HashTable<String, Persona> hashTable = datos.getHashTable();
            Lista<String> relaciones = datos.getRelaciones(); // Obtiene la lista de relaciones

            if (relaciones != null && relaciones.getSize() > 0) {
                // Crear el árbol genealógico y el grafo
                ArbolGenealogico arbolGenealogico = new ArbolGenealogico();
                Grafos grafos = new Grafos();

                // Construir el árbol genealógico y agregar los arcos al grafo usando relaciones
                arbolGenealogico.construirArbol(relaciones, grafos);

                // Mostrar el grafo en la interfaz gráfica
                grafos.mostrarGrafo();
            } else {
                System.out.println("No se pudo cargar el árbol genealógico: lista de relaciones vacía.");
            }
        } else {
            System.out.println("No se pudo cargar el árbol genealógico: datos nulos.");
        }
    }
    
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

                                        if (atributosElement.isJsonArray()) {
                                            com.google.gson.JsonArray atributosArray = atributosElement.getAsJsonArray();
                                            for (int j = 0; j < atributosArray.size(); j++) {
                                                JsonElement atributoElement = atributosArray.get(j);

                                                if (atributoElement.isJsonObject()) {
                                                    JsonObject atributoObj = atributoElement.getAsJsonObject();

                                                    for (String attributeKey : atributoObj.keySet()) {
                                                        JsonElement valueElement = atributoObj.get(attributeKey);
                                                        asignarAtributo(persona, attributeKey, valueElement, relaciones, nombreCompleto, nombrePersona);

                                                        // Actualiza el nombre completo cuando se encuentra "Known throughout as"
                                                        if (attributeKey.equals("Known throughout as")) {
                                                            nombreCompleto = valueElement.getAsString();  // Actualiza el nombre completo
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        // Ahora que nombreCompleto ha sido actualizado, puedes agregar las relaciones
                                        if (atributosElement.isJsonArray()) {
                                            com.google.gson.JsonArray atributosArray = atributosElement.getAsJsonArray();
                                            for (int j = 0; j < atributosArray.size(); j++) {
                                                JsonElement atributoElement = atributosArray.get(j);

                                                if (atributoElement.isJsonObject()) {
                                                    JsonObject atributoObj = atributoElement.getAsJsonObject();

                                                    for (String attributeKey : atributoObj.keySet()) {
                                                        JsonElement valueElement = atributoObj.get(attributeKey);
                                                        
                                                        if (attributeKey.equals("Born to")) {
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
                                                                        isPadre = false;
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
                                                        }
                                                    }
                                                }
                                            }
                                        }

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

        return new DatosProyecto(personas, hashTable);
    }

    private static void asignarAtributo(Persona persona, String attributeKey, JsonElement valueElement, Lista<String> relaciones, String nombreCompleto, String nombrePersona) {
        // Se elimina el uso de la lista relacionesAgregadas, ya que se maneja directamente dentro de la función principal
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
            default:
                break;
        }
    }
}
    
    
}
