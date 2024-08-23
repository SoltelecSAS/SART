/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.soltelec.conexion_seriales;




import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.soltelec.util.CMensajes;
import com.soltelec.util.Utilidades2;

/**
 *
 * @author Gerencia TIC
 */
public class Conexion implements Serializable {

    public static final String ARCHIVO = "Conexion.stlc"; 
    private String driver = "com.mysql.jdbc.Driver";      
    protected static String baseDatos;
    protected static String ipServidor;
    protected static String usuario;
    protected static String puerto;
    protected static String contrasena;
    private static Conexion instance;

    public static Conexion getInstance() {     
       setConexionFromFile();
       return instance;
    }

    private static final String CARPETA = "./configuracion/";
    private static final String EXTENSION = ".soltelec";
    private static final String NOMBRE_ARCHIVO = "Conexion";

    public static void setConexionFromFile() {
        try {
            FileReader fileReader = new FileReader(CARPETA + NOMBRE_ARCHIVO + EXTENSION);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String linea;
            int numLinea = 0;
            List<String> datos = new ArrayList<>();

            while ((linea = bufferedReader.readLine()) != null) {
                
                numLinea +=1;
                
                String dato = "";
                
                if(numLinea !=2){
                    linea = Utilidades2.deCifrar(linea);
                    int indice = linea.indexOf("&");
                    dato = linea.substring(indice +1, linea.length());
                }else{
                    int indice = linea.indexOf(":");
                    dato = linea.substring(indice +1, linea.length());
                }
                
                datos.add(dato);
            }

            baseDatos = datos.get(0);
            ipServidor = datos.get(1);
            usuario = datos.get(2);
            puerto = datos.get(3);
            contrasena = datos.get(4);

            bufferedReader.close();
        } catch (IOException ex) {
            CMensajes.mensajeError("No se pudo leer el archivo de conexion "+ CARPETA + NOMBRE_ARCHIVO + EXTENSION);
            System.out.println("No se pudo leer el archivo de conexion de la base de datos");
            ex.printStackTrace();
        }
    }
    
    public static String getBaseDatos() {
        return baseDatos;
    }

    public static void setBaseDatos(String baseDatos) {
        Conexion.baseDatos = baseDatos;
    }

    public static String getIpServidor() {
        return ipServidor;
    }

    public static void setIpServidor(String ipServidor) {
        Conexion.ipServidor = ipServidor;
    }

    public static String getUsuario() {
        return usuario;
    }

    public static void setUsuario(String usuario) {
        Conexion.usuario = usuario;
    }

    public static String getContrasena() {
        return contrasena;
    }

    public static void setContrasena(String contrasena) {
        Conexion.contrasena = contrasena;
    }

    public static String getUrl() {
        return "jdbc:mysql://" + ipServidor + ":" + puerto + "/" + baseDatos + "?zeroDateTimeBehavior=convertToNull";
    }

    /**
     * @return the driver
     */
    public String getDriver() {
        return driver;
    }

    /**
     * @param driver the driver to set
     */
    public void setDriver(String driver) {
        this.driver = driver;
    }

    /**
     * @return the puerto
     */
    public static String getPuerto() 
    {
        return puerto;
    }

    /**
     * @param puerto the puerto to set
     */
    public void setPuerto(String puerto) {
        Conexion.puerto = puerto;
    }
}
