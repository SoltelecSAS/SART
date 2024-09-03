package org.soltelec.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class LeerArchivo {

    public static boolean rechazarPorRpm() {
        String archivo = "propiedades.properties"; // Nombre del archivo
        String buscarTexto = "rechazarPorRpm=";
        
        String resultado = LeerArchivo.leerDatoDesdeArchivo(archivo, buscarTexto);
        if (resultado != null) return resultado.equals("true");
        return false;
    }

    public static String leerDatoDesdeArchivo(String archivo, String buscarTexto) {
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.contains(buscarTexto)) {
                    // Extraer el dato después de "opacimetro-calibracion:"
                    return linea.substring(linea.indexOf(buscarTexto) + buscarTexto.length()).trim();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static double getFactorOpacidad() {
        String archivo = "propiedades.properties"; // Nombre del archivo
        String buscarTexto = "factorOpacimetro=";
        
        String resultado = LeerArchivo.leerDatoDesdeArchivo(archivo, buscarTexto);
        if (resultado != null) return Double.parseDouble(resultado);
        return 1;
    }

    public static int getIdEquipoFromEquiposProperties(){
        String archivo = "equipos.properties"; // Nombre del archivo
        String buscarTexto = "GASES=";
        String resultado = LeerArchivo.leerDatoDesdeArchivo(archivo, buscarTexto);
        if (resultado != null) return Integer.parseInt(resultado);
        return 0;
    }
}
