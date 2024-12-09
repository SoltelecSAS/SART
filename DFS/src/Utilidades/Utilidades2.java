package Utilidades;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import conexion.Conexion;

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

   public static String obtenerTipoVehiculoPorPlaca(String carPlate) {
      Conexion.setConexionFromFile();
      Connection connection = null;
      PreparedStatement selectStatement = null;
      ResultSet resultSet = null;

      try {
         // Establecer conexión
         String url = Conexion.getUrl(); 
         String user = Conexion.getUsuario(); 
         String password = Conexion.getContrasena();
         connection = DriverManager.getConnection(url, user, password);

         // Consulta SQL
         String selectSql = "SELECT v.CARTYPE FROM vehiculos v WHERE v.CARPLATE = ?";
         selectStatement = connection.prepareStatement(selectSql);
         selectStatement.setString(1, carPlate);

         resultSet = selectStatement.executeQuery();

         // Retornar el resultado si existe
         if (resultSet.next()) {
               return resultSet.getString("CARTYPE");
         } else {
               System.out.println("No se encontró ningún vehículo con la placa: " + carPlate);
         }
      } catch (SQLException e) {
         e.printStackTrace();
         throw new RuntimeException("Error al obtener el tipo de vehículo");
      } finally {
         try {
               if (resultSet != null) resultSet.close();
               if (selectStatement != null) selectStatement.close();
               if (connection != null) connection.close();
         } catch (SQLException ex) {
               ex.printStackTrace();
         }
      }
      return null;
   }
 }
