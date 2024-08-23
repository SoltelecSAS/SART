/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.soltelec.eventos.conexion;




import com.soltelec.eventos.Utilidades;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
//import com.AppEncript.Utilidades;


/**
 *
 * @author Gerencia TIC
 */
public class Conexion implements Serializable {

    public static final String ARCHIVO = "Conexion.stlc";   
    private static Conexion secondInstance;
    private String driver;      
    protected static String baseDatos;
    protected static String ipServidor;
    protected static String usuario;
    protected static String puerto;
    protected static String contrasena;
    private static Conexion instance;
    private static char patron='s';   
     
     
    private Conexion() {
        baseDatos = "db_cda";
        ipServidor = "localhost";
        puerto = "3306";
        usuario = "root";
        contrasena = "50lt3l3c545";
        driver = "com.mysql.jdbc.Driver";
    }

    public static Conexion getInstance() {     
        loadAsObject();
        return instance;
    }

    
    public static void loadAsObject() {        
        FileReader fw;        
        String CARPETA = "./configuracion/"; //ruta donde se desea guardar o consultar el archivo
        String EXTENSION = ".soltelec"; //extension con la cual se desea guardar el objeto.
        String ARCHIVO = "Conexion"; //Nombre del Objeto.
        try {
            fw = new FileReader(CARPETA + ARCHIVO + EXTENSION);
            BufferedReader br = new BufferedReader(fw);                     
            String tmpStr="";
            int posCut;
            try{
                 tmpStr = Utilidades.deCifrar(br.readLine());
            } catch (Throwable e) {
                int event=1;
            }          
            posCut = tmpStr.indexOf("&");
            Conexion.baseDatos = tmpStr.substring(posCut +1, tmpStr.length());
            System.out.println("Nombre de Base de Datos: " + Conexion.baseDatos);
            tmpStr = br.readLine();
            posCut = tmpStr.indexOf(":");
            Conexion.ipServidor = tmpStr.substring(posCut+1, tmpStr.length());
            System.out.println("Ip Servidor " + Conexion.ipServidor);
            tmpStr = Utilidades.deCifrar(br.readLine());
            posCut = tmpStr.indexOf("&");
            Conexion.usuario = tmpStr.substring(posCut+1, tmpStr.length());
             System.out.println("usuario " + Conexion.usuario);
            tmpStr = Utilidades.deCifrar(br.readLine());
            posCut = tmpStr.indexOf("&");
            Conexion.puerto =tmpStr.substring(posCut+1, tmpStr.length());
             System.out.println("Puerto " + Conexion.puerto);
            tmpStr = Utilidades.deCifrar(br.readLine());
            posCut = tmpStr.indexOf("&");
            Conexion.contrasena = tmpStr.substring(posCut +1, tmpStr.length());
             System.out.println("password " + Conexion.contrasena);
        } catch (IOException ex) { }
    }
    
  
     
    public static String getBaseDatos() {
        return baseDatos;
    }

    public void setBaseDatos(String baseDatos) {
        this.baseDatos = baseDatos;
    }

    public static String getIpServidor() {
        return ipServidor;
    }

    public void setIpServidor(String ipServidor) {
        this.ipServidor = ipServidor;
    }

    public static String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public static String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public static String getUrl() {
        return "jdbc:mysql://" + ipServidor + ":" + puerto + "/" + baseDatos + "?zeroDateTimeBehavior=convertToNull";        
//        return "jdbc:mysql://" + ipServidor + "/" + baseDatos + "?zeroDateTimeBehavior=convertToNull";
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
        this.puerto = puerto;
    }
}
