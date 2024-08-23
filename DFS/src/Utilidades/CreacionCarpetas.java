/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utilidades;

import com.soltelec.modulopuc.utilidades.Mensajes;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

 /**
     * Metodo para crear archivos y carpetas
     *
     * @param nombrePropiedad
     * @param nombreArchivo
     * @return el valor de la propiedad
     * @throws java.io.FileNotFoundException
     */
public class CreacionCarpetas {

    public static String CrearCarpeta(String ruta, String nombreCarpeta) {
        File file = new File(ruta);
        String Mensaje = "";
        if (!file.exists()) {
            if (file.mkdirs()) {
                Mensaje = "Directory " + nombreCarpeta + " successfully created";
            } else {
                Mensaje = "the directory " + nombreCarpeta + " was not created in " + ruta + " check the route";
            }
        } else {
            Mensaje = "the directory " + nombreCarpeta + " it's already exists ";
        }

        return Mensaje;

    }

    public static String CrearArchivo(String ruta, String nombreArchivo, String extension) throws IOException {

        File file = new File(ruta.concat(nombreArchivo).concat(extension));
        String Mensaje = "";
        if (!file.exists()) {
            file.createNewFile();
            Mensaje = "file " + nombreArchivo  + " create on " + ruta;
        }else {
            Mensaje = "file " + nombreArchivo + " it's already exists ";
        }
        return Mensaje;
    }

}
