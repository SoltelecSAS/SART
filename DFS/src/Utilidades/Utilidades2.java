package Utilidades;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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

   public static String[] obtenerUltimaPruebaNoAutorizada(String testSheet, String testType) {
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
          String selectSql = "SELECT p.idPruebas, p.abortada, p.usuarios.geuser, p.finalizada " +
                             "FROM Pruebas p " +
                             "WHERE p.autorizada = 'N' " +
                             "AND p.hojaPruebas.testsheet = ? " +
                             "AND p.tipoPrueba.testtype = ? " +
                             "ORDER BY p.idPruebas DESC " +
                             "LIMIT 1";
          
          selectStatement = connection.prepareStatement(selectSql);
          selectStatement.setString(1, testSheet);
          selectStatement.setString(2, testType);
          
          resultSet = selectStatement.executeQuery();
  
          // Retornar el resultado si existe
          if (resultSet.next()) {
              return new String[]{
                  resultSet.getString("idPruebas"),
                  resultSet.getString("abortada"),
                  resultSet.getString("geuser"),
                  resultSet.getString("finalizada")
              };
          } else {
              System.out.println("No se encontró ninguna prueba no autorizada con los criterios dados.");
          }
      } catch (SQLException e) {
          e.printStackTrace();
          throw new RuntimeException("Error al obtener la prueba no autorizada");
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

    public static boolean guardarOModificarMedida(int measureType, int test, Double nuevoValorMedida, String nuevoSimult) {
        String verificarExistencia = "SELECT COUNT(*) FROM medidas WHERE MEASURETYPE = ? AND TEST = ?";
        String actualizacion = "UPDATE medidas SET Valor_medida = ?, Simult = ? WHERE MEASURETYPE = ? AND TEST = ?";
        String insercion = "INSERT INTO medidas (MEASURETYPE, TEST, Valor_medida, Simult) VALUES (?, ?, ?, ?)";

        try (Connection conexion = DriverManager.getConnection(Conexion.getUrl(), Conexion.getUsuario(), Conexion.getContrasena())) {
            
            // Verificar si la medida existe
            try (PreparedStatement consultaVerificacion = conexion.prepareStatement(verificarExistencia)) {
                consultaVerificacion.setInt(1, measureType);
                consultaVerificacion.setInt(2, test);

                try (ResultSet resultado = consultaVerificacion.executeQuery()) {
                    if (resultado.next() && resultado.getInt(1) > 0) {
                        // La medida existe, se actualiza
                        try (PreparedStatement consultaActualizacion = conexion.prepareStatement(actualizacion)) {
                            consultaActualizacion.setDouble(1, nuevoValorMedida);
                            consultaActualizacion.setString(2, nuevoSimult);
                            consultaActualizacion.setInt(3, measureType);
                            consultaActualizacion.setInt(4, test);

                            int filasAfectadas = consultaActualizacion.executeUpdate();
                            return filasAfectadas > 0; // Retorna true si se actualizó al menos una fila
                        }
                    } else {
                        // La medida no existe, se inserta
                        try (PreparedStatement consultaInsercion = conexion.prepareStatement(insercion)) {
                            consultaInsercion.setInt(1, measureType);
                            consultaInsercion.setInt(2, test);
                            consultaInsercion.setDouble(3, nuevoValorMedida);
                            consultaInsercion.setString(4, nuevoSimult);

                            int filasInsertadas = consultaInsercion.executeUpdate();
                            return filasInsertadas > 0; // Retorna true si se insertó una fila
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false; // Retorna false si ocurre un error
    }

    public static void writeListToFile(List<Integer> numbers, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Integer number : numbers) {
                writer.write(number.toString());
                writer.newLine(); // Salto de línea entre cada número
            }
            System.out.println("Datos escritos correctamente en " + fileName);
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo: " + e.getMessage());
        }
    }

    public static boolean eliminarMedida(int test, int measureType) {
        Conexion.setConexionFromFile();
        String eliminacion = "DELETE FROM medidas WHERE TEST = ? AND MEASURETYPE = ?";
    
        try (Connection conexion = DriverManager.getConnection(Conexion.getUrl(), Conexion.getUsuario(), Conexion.getContrasena());
             PreparedStatement consultaEliminacion = conexion.prepareStatement(eliminacion)) {
            
            consultaEliminacion.setInt(1, test);
            consultaEliminacion.setInt(2, measureType);
            
            int filasEliminadas = consultaEliminacion.executeUpdate();
            if (filasEliminadas > 0) {
                System.out.println("Medida de la prueba con id: "+test+" y con tipo de medida: "+measureType+ " Eliminada con exito");
            }
            return filasEliminadas > 0; // Retorna true si se eliminó al menos una fila
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false; // Retorna false si ocurre un error
    }


    public static int getIsEditable(){
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
                    return rc.getInt("cont_test");
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
  
 }
