package com.soltelec.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.soltelec.conexion_seriales.Conexion;

public class Utilidades2 {
    private static char patron = 's';
 
    public Utilidades2() {
    }
 
    public static String cifra(String cadena, char patron) {
       char[] secStr = cadena.toCharArray();
       String strEncript = "";
 
       for(int n = 0; n < secStr.length; ++n) {
          char c = (char)(secStr[n] ^ patron);
          strEncript = strEncript + c;
       }
 
       return strEncript;
    }

    public static String deCifrar(String cadena) {
       char[] secStr = cadena.toCharArray();
       String strEncript = "";
 
       for(int n = 0; n < secStr.length; ++n) {
          char c = (char)(secStr[n] ^ patron);
          strEncript = strEncript + c;
       }
 
       return strEncript;
    }
 
    public static void servicio() {
    }

    public static boolean dialogo2Opciones(String opcion1, String opcion2, String title, String message) {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setLocationRelativeTo(null);

        Object[] options = {opcion1, opcion2};
        int choice = JOptionPane.showOptionDialog(frame,
                message,
                title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        return choice == JOptionPane.YES_OPTION;
    }

   public static String leerDatoDesdeArchivo(String archivo, String buscarTexto) {
      try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
         String linea;
         while ((linea = br.readLine()) != null) {
               // Ignorar líneas vacías o comentarios
               if (linea.isEmpty() || !linea.startsWith(buscarTexto)) continue;
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

   public static String obtenerRutaTermoHigrometro(){
      String archivo = "propiedades.properties"; // Nombre del archivo
      String buscarTexto = "TermoHData=";

      String resultado = Utilidades2.leerDatoDesdeArchivo(archivo, buscarTexto);
      if (resultado != null) return resultado;
      return null;
   }

   public static int getIsEditable(){ //evalua si el artefacto esta activo o no
      String consulta = "SELECT artf FROM cda WHERE id_cda = 1";
      Conexion.setConexionFromFile();
      try (Connection con = DriverManager.getConnection(
         Conexion.getUrl(), 
         Conexion.getUsuario(), 
         Conexion.getContrasena()
      ); 
         PreparedStatement consultaDagma = con.prepareStatement(consulta)) {

         //rc representa el resultado de la consulta
         try (ResultSet rc = consultaDagma.executeQuery()) {
               while (rc.next()) {
                  return rc.getInt("artf");
               }
         }
         return 0;
      } catch (Exception e) {
         CMensajes.mensajeError(
               "Hubo un error al tratar de conectarse a la base de datos, contactese con Soltelec.\n"+
               "Revise por favor el archivo conexion.soltelec\n"
         );
         e.printStackTrace();
         throw new RuntimeException("Error al tratar de conectarse con el base de datos: \n"+ e.getMessage());
      }
   }

   public static boolean mockTermoHigrometro(){
      String archivo = "propiedades.properties"; // Nombre del archivo
      String buscarTexto = "mockTermohigrometro=";

      String resultado = Utilidades2.leerDatoDesdeArchivo(archivo, buscarTexto);

      boolean estaActivoDesdePropiedades = resultado != null && resultado.equalsIgnoreCase("true");
      boolean estaActivoElArtefacto = getIsEditable() == 1;
      boolean elTermohigrometroEsSlave = obtenerFuncionTermohigrometro() != null && !obtenerFuncionTermohigrometro().equalsIgnoreCase("Master");
      return estaActivoDesdePropiedades && estaActivoElArtefacto && elTermohigrometroEsSlave;
   }

   public static boolean termohigrometroAtlanticoMotos(){
      String archivo = "propiedades.properties"; // Nombre del archivo
      String buscarTexto = "termohigrometroAtlanticoMotos=";

      String resultado = Utilidades2.leerDatoDesdeArchivo(archivo, buscarTexto);

      boolean estaActivoDesdePropiedades = resultado != null && resultado.equalsIgnoreCase("true");
      boolean estaActivoElArtefacto = getIsEditable() == 1;
      return estaActivoDesdePropiedades && estaActivoElArtefacto;
   }

   public static void editarHumedadTemperatura(String humedad, String temperatura) {

      if (mockTermoHigrometro()) {
         System.err.println("El mock del termohigrómetro está activado. No se van a actualizar los datos reales.");
         return;
      }

      String rutaArchivoDatos = obtenerRutaTermoHigrometro();
      if (rutaArchivoDatos == null) {
         System.err.println("No se encontró la ruta del archivo en propiedades.properties");
         return;
      }

      File archivoDatos = new File(rutaArchivoDatos);
      Properties props = new Properties();

      // Cargar datos existentes
      try (FileInputStream fis = new FileInputStream(archivoDatos)) {
         props.load(fis);
      } catch (IOException e) {
         e.printStackTrace();
      }

      // Actualizar los valores
      props.setProperty("humedad", humedad);
      props.setProperty("temperatura", temperatura);

      // Guardar cambios
      try (FileOutputStream fos = new FileOutputStream(archivoDatos)) {
         props.store(fos, "Actualización de humedad y temperatura");
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public static String obtenerFuncionTermohigrometro(){
      String archivo = "propiedades.properties";
      String buscarTexto = "FuncionTermoHigrometro=";
      return leerDatoDesdeArchivo(archivo, buscarTexto);
   }
}
