import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import Primitivas.Lista;
import Objetos.Persona;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


public class Funcion{
    
    public static Lista<Lista<String[]>> parseJsonToList(String json) {
    Gson gson = new Gson();
    JsonObject jsonObject = gson.fromJson(json, JsonObject.class);

    // Lista para almacenar las listas de atributos de cada persona
    Lista<Lista<String[]>> listaPersonas = new Lista<>();

    // Acceder al objeto de la casa Baratheon
    JsonElement houseBaratheon = jsonObject.get("House Baratheon");

    // Iterar sobre cada persona en la casa
    for (String nombre : houseBaratheon.getAsJsonArray().toString().replaceAll("[{}\"]", "").split(",")) {
        String[] nombreYValor = nombre.split(":");
        String personaNombre = nombreYValor[0].trim();
        String atributosString = nombreYValor[1].trim();

        // Crear una lista para almacenar los atributos de la persona
        Lista<String[]> atributos = new Lista<>();

        // Split para obtener los pares clave-valor
        String[] pares = atributosString.split("\\}, \\{");
        for (String par : pares) {
            par = par.replace("{", "").replace("}", "").trim(); // Limpiar
            String[] claveValor = par.split(":");

            if (claveValor.length == 2) {
                String clave = claveValor[0].trim().replaceAll("[\" ]", "");
                String valor = claveValor[1].trim().replaceAll("[\" ]", "");
                
                // Si el valor está en formato de lista (indicado por "Father to" por ejemplo)
                if (clave.equals("Father to")) {
                    String[] valores = valor.substring(1, valor.length() - 1).split(", ");
                    atributos.append(new String[]{clave, String.join(", ", valores)});
                } else {
                    atributos.append(new String[]{clave, valor});
                }
            }
        }

        // Añadir la lista de atributos de esta persona a la lista general
        listaPersonas.append(atributos);
    }

    return listaPersonas;
}

    public static Lista<Persona> leerJson(String archivoJson) {
        Lista<Persona> personas = new Lista<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(archivoJson))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (linea.trim().startsWith("{")) {
                    // Leer el objeto JSON de la casa Baratheon
                    Lista<Lista<String[]>> listaAtributos = extraerAtributos(linea);
                    
                    // Obtener el nombre (la clave principal) del JSON
                    String nombre = extraerNombre(linea);
                    
                    // Crear la persona a partir de los atributos extraídos
                    Persona persona = Persona.fromJson(nombre, listaAtributos);
                    personas.append(persona);
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); // Maneja la excepción según sea necesario
        }

        return personas;
    }

    private static String extraerNombre(String jsonLine) {
        // Extrae el nombre del objeto JSON (la clave del primer nivel)
        // Ejemplo: "Orys Baratheon"
        int inicio = jsonLine.indexOf("\"") + 1;
        int fin = jsonLine.indexOf("\"", inicio);
        return jsonLine.substring(inicio, fin);
    }

    private static Lista<Lista<String[]>> extraerAtributos(String jsonLine) {
        Lista<Lista<String[]>> atributos = new Lista<>();

        // Aquí debes implementar la lógica para extraer los atributos del objeto JSON
        // Ejemplo simplificado, deberías parsear correctamente el JSON
        String[] partes = jsonLine.split("\\},\\{");
        for (String parte : partes) {
            parte = parte.replaceAll("[{}]", ""); // Elimina llaves
            Lista<String[]> listaAtributos = new Lista<>();
            String[] atributosRaw = parte.split(",");
            for (String atributoRaw : atributosRaw) {
                String[] par = atributoRaw.split(":");
                String clave = par[0].trim().replaceAll("\"", "");
                String valor = par[1].trim().replaceAll("\"", "");
                listaAtributos.append(new String[]{clave, valor});
            }
            atributos.append(listaAtributos);
        }

        return atributos;
    }
}
