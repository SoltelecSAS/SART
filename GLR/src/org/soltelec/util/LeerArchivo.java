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

    public static boolean activarLogsMoto() {
        String archivo = "propiedades.properties"; // Nombre del archivo
        String buscarTexto = "activarLogs=";
        
        String resultado = LeerArchivo.leerDatoDesdeArchivo(archivo, buscarTexto);
        if (resultado != null) return resultado.equals("true");
        return false;
    }

    public static boolean activarDismiHC() { //Por defecto siempre esta activo
        String archivo = "propiedades.properties"; // Nombre del archivo
        String buscarTexto = "activarDismiHC=";

        String resultado = LeerArchivo.leerDatoDesdeArchivo(archivo, buscarTexto);
        if (resultado != null) return resultado.equals("true");
        return false;
    }

    public static boolean desactivarDesviacionCeroYSondaExosto() { //POR DEFECTO RETORNA FALSE SI LA PROPIEDAD NO EXISTE
        String archivo = "propiedades.properties"; // Nombre del archivo
        String buscarTexto = "desactivarDesviacionCeroYSondaExosto=";

        String resultado = LeerArchivo.leerDatoDesdeArchivo(archivo, buscarTexto);
        if (resultado != null) return resultado.equals("true") && Utilidades.getIsEditable() == 1;
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

    public static int getTiempoCeroBrianBee() {
        String archivo = "propiedades.properties"; // Nombre del archivo
        String buscarTexto = "tiempoCeroBrianBee=";
        
        String resultado = LeerArchivo.leerDatoDesdeArchivo(archivo, buscarTexto);
        if (resultado != null) return Integer.parseInt(resultado);
        return 0;
    }

    public static double getFactorOpacidad() {
        String archivo = "propiedades.properties"; // Nombre del archivo
        String buscarTexto = "factorOpacimetro=";
        
        String resultado = LeerArchivo.leerDatoDesdeArchivo(archivo, buscarTexto);
        if (resultado != null) return Double.parseDouble(resultado);
        return 1;
    }

    public static double getTiempoHc() {
        String archivo = "propiedades.properties"; // Nombre del archivo
        String buscarTexto = "hcTime=";
        
        String resultado = LeerArchivo.leerDatoDesdeArchivo(archivo, buscarTexto);
        System.out.println("Valor hcTime: "+resultado);
        if (resultado != null) return Double.parseDouble(resultado);
        return 100; // retorna 100 por defecto
    }

    public static int getIdEquipoFromEquiposProperties(){
        String archivo = "equipos.properties"; // Nombre del archivo
        String buscarTexto = "GASES=";
        String resultado = LeerArchivo.leerDatoDesdeArchivo(archivo, buscarTexto);
        if (resultado != null) return Integer.parseInt(resultado);
        return 0;
    }

    public static String getSerialOtto() throws IOException{
        String marcaAnalizador = UtilPropiedades.cargarPropiedad("marcaAnalizador", "seriales.properties");
        String modeloAnalizador = UtilPropiedades.cargarPropiedad("modeloAnalizador", "seriales.properties");
        String modeloBanco = UtilPropiedades.cargarPropiedad("modeloBanco", "seriales.properties");
        String marcaKit = UtilPropiedades.cargarPropiedad("marcaKit", "seriales.properties");
        String marcaTermo = UtilPropiedades.cargarPropiedad("marcaTermo", "seriales.properties");
        String pefAnalizador = UtilPropiedades.cargarPropiedad("pefAnalizador", "seriales.properties");
        String serialAnalizador = UtilPropiedades.cargarPropiedad("serialAnalizador", "seriales.properties");
        String serialBanco = UtilPropiedades.cargarPropiedad("serialBanco", "seriales.properties");
        String serialRpm = UtilPropiedades.cargarPropiedad("serialKit", "seriales.properties");
        String serialBateria = UtilPropiedades.cargarPropiedad("serialBateria", "seriales.properties");
        String serialVibracion = UtilPropiedades.cargarPropiedad("serialVibracion", "seriales.properties");
        String serialTemperatura = UtilPropiedades.cargarPropiedad("serialTemperatura", "seriales.properties");
        String serialTermohigrometro = UtilPropiedades.cargarPropiedad("serialTermo", "seriales.properties");

        String serialAnalizadorCompleto = 
            serialBanco.equals("")  ? 
                pefAnalizador+"-"+serialAnalizador : pefAnalizador+"-"+serialAnalizador+"-"+serialBanco;
        
        if (Utilidades.getMetodoMedicionRpm().equalsIgnoreCase("NA")) serialVibracion = " ? "; //CUANDO NO SELECCIONA METODO DE MEDICION DEJARA UN SERIAL CON SIGNO ? EN EL SERIAL DE LA SONDA
        
        String serialKitCompleto = 
            Utilidades.getMetodoMedicionRpm().equalsIgnoreCase("Bateria") ?
                serialRpm+"/"+serialTemperatura+"/"+serialBateria:
                serialRpm+"/"+serialTemperatura+"/"+serialVibracion;
        
        //if (formaTemp.equalsIgnoreCase("C") || diseno.equalsIgnoreCase("Scooter")) {
        //    serialKitCompleto = serialKitCompleto.replace("/"+serialTemperatura, "");
        //}
        
        String serialCompleto = "otto~"+
            marcaAnalizador+";"+marcaKit+";"+marcaTermo+"~"+
            serialAnalizadorCompleto+";"+serialKitCompleto+";"+serialTermohigrometro+"~"
            +modeloAnalizador+";"+modeloBanco;

        return serialCompleto;
    }

    public static String getSerialOttoPantallaServicio() throws IOException{
        String modeloAnalizador = UtilPropiedades.cargarPropiedad("modeloAnalizador", "seriales.properties");
        String modeloBanco = UtilPropiedades.cargarPropiedad("modeloBanco", "seriales.properties");
        String pefAnalizador = UtilPropiedades.cargarPropiedad("pefAnalizador", "seriales.properties");
        String serialAnalizador = UtilPropiedades.cargarPropiedad("serialAnalizador", "seriales.properties");
        String serialBanco = UtilPropiedades.cargarPropiedad("serialBanco", "seriales.properties");
        String serialRpm = UtilPropiedades.cargarPropiedad("serialKit", "seriales.properties");
        String serialBateria = UtilPropiedades.cargarPropiedad("serialBateria", "seriales.properties");
        String serialVibracion = UtilPropiedades.cargarPropiedad("serialVibracion", "seriales.properties");
        String serialTemperatura = UtilPropiedades.cargarPropiedad("serialTemperatura", "seriales.properties");
        String serialTermohigrometro = UtilPropiedades.cargarPropiedad("serialTermo", "seriales.properties");

        String serialAnalizadorCompleto = 
            serialBanco.equals("")  ? 
                pefAnalizador+"~"+serialAnalizador : pefAnalizador+"~"+serialAnalizador+"-"+serialBanco;
        
        String serialKitCompleto = serialRpm+"/"+serialTemperatura+"/("+serialBateria+" "+serialVibracion+")";
        
        String serialCompleto = serialAnalizadorCompleto+";"+serialKitCompleto+";"+serialTermohigrometro+"~"
            +modeloAnalizador+";"+modeloBanco;

        return serialCompleto;
    }
    
}
