package Utilidades;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

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
        Conexion.setConexionFromFile();
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

    public static void writeListToFile2(List<Double> numbers, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Double number : numbers) {
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

    public static void cargarDefectos(int codigoDefecto, Long idPrueba) {
        Conexion.setConexionFromFile();
        String addDefecto = "INSERT INTO defxprueba (id_defecto, id_prueba) VALUES(?, ?)";
    
        try (Connection conexion = DriverManager.getConnection(Conexion.getUrl(), Conexion.getUsuario(), Conexion.getContrasena());
                PreparedStatement pstmt = conexion.prepareStatement(addDefecto)) {
    
            // Asigna los valores al PreparedStatement
            pstmt.setInt(1, codigoDefecto);
            pstmt.setLong(2, idPrueba);
    
            System.out.println("Ejecutando query para insertar defecto con código: " + codigoDefecto + " en la prueba con ID: " + idPrueba);
    
            // Ejecuta la actualización
            int filasAfectadas = pstmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Defecto insertado correctamente.");
            } else {
                System.out.println("No se pudo insertar el defecto.");
            }
    
        } catch (SQLException e) {
            System.err.println("Error al intentar insertar el defecto (ID: " + codigoDefecto + ") para la prueba (ID: " + idPrueba + "). " +
                    "Es posible que el defecto y la prueba ya estén registrados en 'defxprueba'. Detalles del error: " + e.getMessage());
        }
    }

    public static String obtenerSerialResolucionPorId(int idEquipo) {
        Conexion.setConexionFromFile();
        String consulta = "SELECT serialresolucion FROM equipos WHERE id_equipo = ?";
    
        try (Connection conexion = DriverManager.getConnection(Conexion.getUrl(), Conexion.getUsuario(), Conexion.getContrasena());
             PreparedStatement statement = conexion.prepareStatement(consulta)) {
            
            statement.setInt(1, idEquipo);
    
            try (ResultSet resultado = statement.executeQuery()) {
                if (resultado.next()) {
                    return resultado.getString("serialresolucion");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return null; // Retorna null si no se encontró o hubo error
    }

    public static void actualizarPrueba(boolean finalizada, boolean aprobada, Long usuario, String serialEquipo, Long idPruebas, String comentario) {
        // Consulta SQL para actualizar los valores en la tabla pruebas
        String query = "UPDATE pruebas " +
                       "SET Finalizada = ?, " +
                       "    Aprobada = ?, " +
                       "    Abortada = 'N', " +
                       "    usuario_for = ?, " +
                       "    serialEquipo = ?, " +
                       "    observaciones = ?" +
                       "WHERE Id_Pruebas = ?";
        
        try (Connection conexion = DriverManager.getConnection(Conexion.getUrl(), Conexion.getUsuario(), Conexion.getContrasena());
                PreparedStatement stmt = conexion.prepareStatement(query)) {
    
            // Asigna los valores a los parámetros de la consulta
            stmt.setString(1, finalizada ? "Y" : "N");
            stmt.setString(2, aprobada ? "Y" : "N");
            stmt.setLong(3, usuario);
            stmt.setString(4, serialEquipo);
            stmt.setString(5, comentario);
            stmt.setLong(6, idPruebas);
    
            // Ejecuta la actualización
            int filasActualizadas = stmt.executeUpdate();
    
            // Verifica cuántas filas fueron actualizadas
            if (filasActualizadas > 0) {
                System.out.println("La prueba con Id_Pruebas " + idPruebas + " fue actualizada exitosamente.");
            } else {
                System.out.println("No se encontró ninguna prueba con Id_Pruebas " + idPruebas + " para actualizar.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al actualizar la base de datos.", e);
        }
    }

    public static String obtenerDatos(String rutaArchivo, String key) {
        Properties propiedades = new Properties();

        try (FileInputStream input = new FileInputStream(rutaArchivo)) {
            propiedades.load(input);
            return propiedades.getProperty(key); // Retorna el valor asociado a la clave FRENO
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null; // Retorna null si no se encuentra o si ocurre un error
    }

    public static Double leerDoubleDesdeArchivo(String fileName, String key) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(key + "=")) {
                    String[] parts = line.split("=", 2);
                    if (parts.length == 2) {
                        try {
                            System.out.println("===Leyendo el valor de la clave: " + key + " valor: " + parts[1]);
                            return Double.parseDouble(parts[1].trim());
                        } catch (NumberFormatException e) {
                            System.err.println("Valor inválido para la clave '" + key + "': " + parts[1]);
                            return 0.0;
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: "+fileName+" error:"+  e.getMessage());
        }
        System.out.println("===Datos no encontrados en el archivo: " + fileName + " para la clave: " + key);
        return 0.0;
    }

    public static Boolean leerBooleanDesdeArchivo(String fileName, String key) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(key + "=")) {
                    String[] parts = line.split("=", 2);
                    if (parts.length == 2) {
                        String value = parts[1].trim().toLowerCase();
                        System.out.println("===Leyendo el valor de la clave: " + key + " valor: " + value);
                        if (value.equals("true") || value.equals("false")) {
                            return Boolean.parseBoolean(value);
                        } else {
                            System.err.println("Valor inválido para la clave '" + key + "': " + value+". Se asumira como false.");
                            return false;
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + fileName + " error: " + e.getMessage());
        }
        System.out.println("===Datos no encontrados en el archivo: " + fileName + " para la clave: " + key);
        return false;
    }

    public static Long leerLongDesdeArchivo(String fileName, String key) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(key + "=")) {
                    String[] parts = line.split("=", 2);
                    if (parts.length == 2) {
                        try {
                            System.out.println("===Leyendo el valor de la clave: " + key + " valor: " + parts[1]);
                            return Long.parseLong(parts[1].trim());
                        } catch (NumberFormatException e) {
                            System.err.println("Valor inválido para la clave '" + key + "': " + parts[1]);
                            return 0L;
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: "+fileName+" error:"+  e.getMessage());
        }
        System.out.println("===Datos no encontrados en el archivo: " + fileName + " para la clave: " + key);
        return 0L;
    }

    public static double getMayor(double num1, double num2){
        if (num1>num2) return num1;
        return num2;
    }

    public static double getMenor(double num1, double num2){
        if (num1<num2) return num1;
        return num2;
    }

    public static double[][] medidasCuatrimotoPequena = {
        /*Pesos                            Fuerzas*/
        {0                   ,                   0}, //llanta 1 DDer 
        {0                   ,                   0}, //llanta 2 TDer
        {0                   ,                   0}, //llanta 3 DIzq
        {0                   ,                   0}, //llanta 4 TIzq
        //-----------------------------------------------------------------------------
        {0                   ,                   0}, //fuerzas de estacionamiento I , D
        /*esDer                              esIzq*/
    };

    public static double[][] parametros = {
        /*Valor 0 mV, Valor fuerza mV, Valor offset mV, span */
        { 0         , 0              , 0              , 0}, //llanta 1 DDer 
        { 0         , 0              , 0              , 0}, //llanta 2 TDer
        { 0         , 0              , 0              , 0}, //llanta 3 DIzq
        { 0         , 0              , 0              , 0}, //llanta 4 TIzq
        //----------------------------------------------------------------------------
        { 0         , 0              , 0              , 0}, //fuerzas de estacionamiento Der , D
        { 0         , 0              , 0              , 0} //fuerzas de estacionamiento Izq , I
    };

    public static String generarReporteParametros(boolean aplicarOffset) {
        String[] etiquetas = {
            "Llanta 1 Delantera Derecha",
            "Llanta 2 Trasera Derecha",
            "Llanta 3 Delantera Izquierda",
            "Llanta 4 Trasera Izquierda",
            "Estacionamiento Derecho",
            "Estacionamiento Izquierdo"
        };
    
        String reporte = "";
    
        for (int i = 0; i < parametros.length; i++) {
            int indexFuerza = aplicarOffset ? 1 : 0;
            double valorCero = parametros[i][0];
            double valorFuerza = parametros[i][1];
            double valorOffset = parametros[i][2];
            double span = parametros[i][3];
    
            double ceroConOffset = aplicarOffset ? (valorCero + valorOffset) : valorCero;
            double fuerzaConCeroSinSpan = valorFuerza - ceroConOffset;
            double fuerzaTotal = fuerzaConCeroSinSpan * span;

            fuerzas[i][indexFuerza] = fuerzaTotal;

            String offset = aplicarOffset ? "con offset" : "sin offset";
    
            reporte += "[" + etiquetas[i] + " " +offset+"]\n";
            reporte += "  Valor 0 mV: " + valorCero + "\n";
            reporte += "  Valor fuerza mV: " + valorFuerza + "\n";
            reporte += "  Valor offset mV: " + valorOffset + "\n";
            reporte += "  Span: " + span + "\n";
            reporte += "  Cero " + (aplicarOffset ? "con offset" : "sin offset") + ": " + ceroConOffset + "\n";
            reporte += "  Fuerza con cero sin span: " + fuerzaConCeroSinSpan + "\n";
            reporte += "  Fuerza total: " + fuerzaTotal + "\n";
            reporte += "---------------------------------------------\n";
        }
    
        return reporte;
    }
    
    public static double[][] fuerzas = {
        /*Sin offset                    Con offset*/
        {0                   ,                   0}, //llanta 1 DDer  
        {0                   ,                   0}, //llanta 2 TDer
        {0                   ,                   0}, //llanta 3 DIzq
        {0                   ,                   0}, //llanta 4 TIzq
        //-----------------------------------------------------------------------------
        {0                   ,                   0}, //fuerzas de estacionamiento Der
        {0                   ,                   0}, //fuerzas de estacionamiento Izq
    };

    public static String generarReporteFuerzas() {
        String[] etiquetas = {
            "Llanta 1 Delantera Derecha",
            "Llanta 2 Trasera Derecha",
            "Llanta 3 Delantera Izquierda",
            "Llanta 4 Trasera Izquierda",
            "Estacionamiento Derecho",
            "Estacionamiento Izquierdo"
        };
    
        String reporte = "";
    
        for (int i = 0; i < fuerzas.length; i++) {
            double fuerzaSinOffset = fuerzas[i][0];
            double fuerzaConOffset = fuerzas[i][1];
    
            reporte += "[" + etiquetas[i] + "]\n";
            reporte += "  Fuerza sin offset: " + fuerzaSinOffset + "\n";
            reporte += "  Fuerza con offset: " + fuerzaConOffset + "\n";
            reporte += "---------------------------------------------\n";
        }
    
        return reporte;
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

    public static double interpolar(double[] x, double[] y, double valorX) {

        // Recorremos para encontrar el intervalo donde cae valorX
        for (int i = 0; i < x.length - 1; i++) {
            double x1 = x[i];
            double x2 = x[i + 1];

            if ((valorX >= x1 && valorX <= x2) || (valorX <= x1 && valorX >= x2)) {
                double y1 = y[i];
                double y2 = y[i + 1];

                // Fórmula de interpolación lineal
                return y1 + (valorX - x1) * (y2 - y1) / (x2 - x1);
            }
        }

        // Si no entró en ningún intervalo → fuera de rango
        if (valorX < x[0]) {
            return y[0]; // está antes del rango
        } else if (valorX > x[x.length - 1]) {
            return y[y.length - 1]; // está después del rango
        } else {
            // caso raro si el arreglo no está ordenado
            return 0;
        }
    }

    //valorMxK = valor metros por kilometro
    //voltajes = valores en mV
    // valorVoltaje = valor en mV a interpolar
    public static double interpolarMedidaDesv(double[] voltajes, double[] valorMxK, double valorVoltaje) {
        double porcentajeTolerancia = leerTolerancia();
        double valorCero = voltajes[5];
        double tolerancia = calcularTolerancia(valorCero, porcentajeTolerancia);

        Double valorInterpolado = interpolarDesv(voltajes, valorMxK, valorVoltaje);

        if (estaDentroDeTolerancia(valorVoltaje, valorCero, tolerancia)
                && esMenorAlUnoPorCiento(valorInterpolado)) {
            return 0.0;
        }

        if (valorInterpolado != null) {
            return valorInterpolado;
        }

        return extrapolar(voltajes, valorMxK, valorVoltaje);
    }

    private static double leerTolerancia() {
        return leerDoubleDesdeArchivo("calibracion.properties", "toleranciaCeroDesv");
    }

    private static double calcularTolerancia(double valorCero, double porcentaje) {
        return valorCero * (porcentaje / 100);
    }

    private static Double interpolarDesv(double[] voltajes, double[] valores, double valorVoltaje) {
        for (int i = 0; i < voltajes.length - 1; i++) {
            double x1 = voltajes[i], x2 = voltajes[i + 1];

            if ((valorVoltaje >= x1 && valorVoltaje <= x2) || (valorVoltaje <= x1 && valorVoltaje >= x2)) {
                double y1 = valores[i], y2 = valores[i + 1];
                return y1 + (valorVoltaje - x1) * (y2 - y1) / (x2 - x1);
            }
        }
        return null;
    }

    private static boolean estaDentroDeTolerancia(double valor, double referencia, double tolerancia) {
        return referencia - tolerancia <= valor && valor <= referencia + tolerancia;
    }

    private static boolean esMenorAlUnoPorCiento(Double valor) {
        return valor != null && Math.abs(valor) < 1.0;
    }

    private static double extrapolar(double[] voltajes, double[] valores, double valorVoltaje) {
        if (valorVoltaje < voltajes[0]) {
            return valores[0];
        } else if (valorVoltaje > voltajes[voltajes.length - 1]) {
            return valores[valores.length - 1];
        }

        CMensajes.mensajeError("El arreglo de voltajes no está ordenado correctamente. \nPor favor, revise la configuración de calibración.");
        throw new IllegalArgumentException("El arreglo de voltajes no está ordenado correctamente.");
    }

    public static double[] stringToDoubleArray(String s) {
        // Quitamos corchetes y espacios
        s = s.replace("[", "").replace("]", "").trim();

        if (s.isEmpty()) {
            return new double[0]; // arreglo vacío
        }

        // Separamos por coma
        String[] parts = s.split(",");

        double[] result = new double[parts.length];
        for (int i = 0; i < parts.length; i++) {
            result[i] = Double.parseDouble(parts[i].trim());
        }

        return result;
    }
}
