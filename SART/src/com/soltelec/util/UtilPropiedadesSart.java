/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.soltelec.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import com.soltelec.util.MensajesOut;
/**
 *
 * @author GerenciaDesarrollo
 */
public class UtilPropiedadesSart 
{

    private static final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(UtilPropiedadesSart.class.getName());
    /**
     * Metodo para cargar una propiedad de un archivo properties
     *
     * @param nombreArchivo
     * @return el valor de la propiedad
     * @throws java.io.FileNotFoundException
     */
    public static List cargarPropiedad(String nombreArchivo) throws FileNotFoundException, IOException 
    {
        String[] clave = {"Labrado9004",
                          "Labrado9005",
                          "Labrado9006",
                          "Labrado9007",
                          "Labrado9008",
                          "Labrado9009",
                          "Labrado9010",
                          "Labrado9011",
                          "Labrado9012",
                          "Labrado9013",
                          "Labrado9014",
                          "Labrado9015",
                          "Labrado9016",
                          "Labrado9017",
                          "Labrado9018",
                          "Labrado9019",
                          "Labrado9020",
                          "Labrado9021"};
        int cont=0;
        List<Integer> ejemploLista = new ArrayList<Integer>();
        Properties propiedades = new Properties();
        propiedades.load(new FileInputStream("./" + nombreArchivo));
        Enumeration<Object> claves = propiedades.keys();
        String key=null;
        while (cont<clave.length) 
        {
            key=(String) propiedades.get(clave[cont]);
            if (key!=null) 
            {
               ejemploLista.add(Integer.parseInt(key)) ;
            }
            cont++; 
        }
        return ejemploLista;
    }
    
    /**
     * 
     * @param nombreArchivo
     * @return
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public static Map cargarConfigLabrado(String nombreArchivo) throws FileNotFoundException, IOException 
    {   
        Map<String,String > map = new HashMap<String,String>();
        Properties propiedades = new Properties();
        propiedades.load(new FileInputStream("./" + nombreArchivo));
        Enumeration<Object> claves = propiedades.keys();
        while (claves.hasMoreElements()) 
        {
            String clave = claves.nextElement().toString();
        String valor= propiedades.get(clave).toString();
            map.put(clave,valor);
            
            System.out.println(clave + " - " + propiedades.get(clave).toString());
        }
        return map;
    }
    
    public static String cargarCodigosLabrado(String nombreArchivo,String clave) throws FileNotFoundException, IOException 
    {   
        Properties propiedades = new Properties();
        propiedades.load(new FileInputStream("./" + nombreArchivo));
        String valor= (String) propiedades.get(clave);
        return valor;
    }
    
     /**
     * Metodo para cargar una propiedad de un archivo properties
     *
     * @param nombrePropiedad
     * @param nombreArchivo
     * @return el valor de la propiedad
     */
    public static String cargarPropiedad(String nombrePropiedad, String nombreArchivo)
    {
        System.out.println("------------------------------------------");
        System.out.println("- Cargar Propiedad  :" +  nombrePropiedad + " archivo : " + nombreArchivo);
        System.out.println("------------------------------------------");
        try 
        {
            Properties props = new Properties();
            props.load(new FileInputStream("./" + nombreArchivo));
            System.out.println("Pripiedad retornada : " + props.getProperty(nombrePropiedad));
            return props.getProperty(nombrePropiedad);
        } catch (FileNotFoundException e) 
        {
            LOG.log(Level.INFO, "Error :{0}", e);
            LOG.log(Level.INFO, "Error en el metodo :cargarPropiedad()", e.getMessage());
            java.util.logging.Logger.getLogger(UtilPropiedadesSart.class.getName()).log(Level.SEVERE, null, e);
        }catch (IOException ex) 
        {
            LOG.log(Level.INFO, "Error :{0}", ex);
            LOG.log(Level.INFO, "Error en el metodo :cargarPropiedad()", ex.getMessage());
            java.util.logging.Logger.getLogger(UtilPropiedadesSart.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
