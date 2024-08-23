/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author GerenciaDesarrollo
 */
public class UtilPropiedades {

    /**
     * Metodo para cargar una propiedad de un archivo properties
     *
     * @param nombrePropiedad
     * @param nombreArchivo
     * @return el valor de la propiedad
     * @throws java.io.FileNotFoundException
     */
    public static String cargarPropiedad(String nombrePropiedad, String nombreArchivo) throws FileNotFoundException, IOException {
        Properties props = new Properties();
        props.load(new FileInputStream("./" + nombreArchivo));
        return props.getProperty(nombrePropiedad);
    }//end of method cargarPropiedad
}
