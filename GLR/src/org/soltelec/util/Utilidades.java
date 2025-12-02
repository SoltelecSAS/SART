/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

import org.soltelec.conexion_seriales.Conexion;

import com.soltelec.servidor.utils.CMensajes;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.Timer;

/**
 *
 * @author GerenciaDesarrollo
 */
public class Utilidades {
    private static char patron='M';

    public static String cifra(String cadena, char patron) {
        char[] secStr = cadena.toCharArray();
        String strEncript = "";
        int e=secStr.length;
        for (int n = 0; n < secStr.length; n++) {
            char c = (char) (secStr[n] ^ patron);
            strEncript = strEncript + c;
        }
        return strEncript;
    }
    
    public static String deCifrar(String cadena) {
        char[] secStr = cadena.toCharArray();
        String strEncript = "";
        for (int n = 0; n < secStr.length; n++) {
            char c = (char) (secStr[n] ^ patron);
            strEncript = strEncript + c;
        }
        return strEncript;
    }    
     public static void servicio() {
         
     }

    private static String metodoRpm = "NA";
    public static void setMetodoMedicionRpm(String metodo){
        Utilidades.metodoRpm = metodo;
    }

    public static String getMetodoMedicionRpm(){
        return Utilidades.metodoRpm;
    }

    private static String metodoRpmDiesel;
    public static void setMetodoMedicionRpmDiesel(String metodo){
        Utilidades.metodoRpmDiesel = metodo;
    }

    public static String getMetodoMedicionRpmDiesel(){
        return Utilidades.metodoRpmDiesel;
    }

    private static double tempAmbiente;
    public static double getTempAmbiente() {
        return tempAmbiente;
    }

    public static void setTempAmbiente(double tempAmbiente) {
        Utilidades.tempAmbiente = tempAmbiente;
    }

    private static double humedadAmbiente;
    public static double getHumedadAmbiente() {
        return humedadAmbiente;
    }

    public static void setHumedadAmbiente(double humedadAmbiente) {
        Utilidades.humedadAmbiente = humedadAmbiente;
    }

    private static long idUsuarioMotos;

    public static long getIdUsuarioMotos() {
        return idUsuarioMotos;
    }

    public static void setIdUsuarioMotos(long idUsuarioMotos) {
        Utilidades.idUsuarioMotos = idUsuarioMotos;
    }

    private static int intentos = 0;

    public static int getIntentos() {
        return intentos;
    }

    private static long idPrueba;

    public static long getIdPrueba() {
        return idPrueba;
    }
    public static void setIdPrueba(long idPrueba) {
        Utilidades.idPrueba = idPrueba;
    }

    private static String tipoVehiculo;

    public static String getTipoVehiculo(){
        return tipoVehiculo;
    }

    public static void setTipoVehiculo(String tipoVehiculo){
        Utilidades.tipoVehiculo = tipoVehiculo;
    }

    

    public static void setIntentos(int intentos) {
        Utilidades.intentos = intentos;
    }
    
    public static String obtenerComentario() {
        // Preguntar si desea agregar un comentario
        int respuesta = JOptionPane.showConfirmDialog(null, "¿Desea agregar un comentario de la prueba?", "Confirmar", JOptionPane.YES_NO_OPTION);
        
        // Si la respuesta es Sí
        if (respuesta == JOptionPane.YES_OPTION) {
            String comentario = JOptionPane.showInputDialog(null, "Ingrese el comentario:", "Comentario", JOptionPane.QUESTION_MESSAGE);
            
            // Validar que el comentario no esté vacío ni sea null
            if (comentario != null && !comentario.trim().isEmpty()) {
                return comentario;  // Retornar el comentario ingresado
            }
        }
        
        // Si la respuesta es No o si el comentario está vacío, devolver un String vacío
        return "";
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

    public static void actualizarConfigCalibracion(int serial, double config1, double config2) {
        Conexion.setConexionFromFile();
        String updateQuery = "UPDATE config_calibracion SET config_1 = ?, config_2 = ? WHERE serial = ?";

        try (Connection conexion = DriverManager.getConnection(Conexion.getUrl(), Conexion.getUsuario(), Conexion.getContrasena());
            PreparedStatement pstmt = conexion.prepareStatement(updateQuery)) {

            // Asignar valores a los parámetros
            pstmt.setDouble(1, config1);
            pstmt.setDouble(2, config2);
            pstmt.setInt(3, serial);

            System.out.println("Ejecutando actualización para el serial: " + serial);
            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("Configuración actualizada correctamente para el serial: " + serial);
            } else {
                System.out.println("No se encontró ningún registro con el serial: " + serial);
            }

        } catch (SQLException e) {
            System.err.println("Error al actualizar configuración para el serial (" + serial + "). Detalles: " + e.getMessage());
        }
    }

    public static void cargarCausalRechazoGases(int idCausal, Long idPrueba) {
        Conexion.setConexionFromFile();
        String addDefecto = "INSERT INTO pruebas_rechazo_gases (id_prueba, id_rechazo_gases) VALUES(?, ?)";
    
        try (Connection conexion = DriverManager.getConnection(Conexion.getUrl(), Conexion.getUsuario(), Conexion.getContrasena());
                PreparedStatement pstmt = conexion.prepareStatement(addDefecto)) {
    
            // Asigna los valores al PreparedStatement
            pstmt.setLong(1, idPrueba);
            pstmt.setInt(2, idCausal);
            
    
            System.out.println("Ejecutando query para insertar la causalRechazoGases con id_rechazo_gases: " + idCausal + " en la prueba con ID: " + idPrueba);
    
            // Ejecuta la actualización
            int filasAfectadas = pstmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Causal insertada correctamente.");
            } else {
                System.out.println("No se pudo insertar la Causal.");
            }
    
        } catch (SQLException e) {
            System.err.println("Error al intentar insertar la causalRechazoGases (ID: " + idCausal + ") para la prueba (ID: " + idPrueba + "). " +
                    "Es posible que el defecto y la prueba ya estén registrados en 'defxprueba'. Detalles del error: " + e.getMessage());
        }
    }

    

    // Array de código de medidas
    private static final Integer[][] CODIGO_MEDIDAS = {
        {2042, 2041, 2040, 2044, 2045, 2046}, // Deviaciones bajas
        {2026, 2025, 2024, 2031, 2030, 2029}, // Bajas
        {2038, 2037, 2032, 2036, 2033, 2034}, // Altas
        {2052, 2051, 2050, 2053, 2054, 2055}  // Exploradoras
    };

    // Método estático que encapsula toda la lógica
    public static void verificarMedidasLuces(long idPrueba) {
        // Verificación para "deviaciones bajas" (primera fila del array)
        for (int medida : CODIGO_MEDIDAS[0]) {
            if (!verificarRango(idPrueba, medida, 0.5, 3.5)) {
                cargarDefectos(20002, idPrueba); // Si está fuera de rango, ejecuta el defecto
                System.out.println("Cargar defecto por medida: "+medida);
            }
        }

        // Verificación para "bajas" (segunda fila del array)
        for (int medida : CODIGO_MEDIDAS[1]) {
            if (!verificarRango(idPrueba, medida, 2.5, Double.MAX_VALUE)) {
                cargarDefectos(20000, idPrueba); // Si está por debajo de 2.5, ejecuta el defecto
                System.out.println("Cargar defecto por medida: "+medida);
            }
        }
    }

    // Método auxiliar para verificar si el valor de la medida está en el rango especificado
    private static boolean verificarRango(long idPrueba, int measureType, double min, double max) {

        boolean dentroDeRango = true;
        Conexion.setConexionFromFile();
    
        // Consulta SQL separada en un String
        String query = "SELECT m.Valor_medida " +
                       "FROM pruebas p " +
                       "INNER JOIN medidas m ON m.TEST = p.Id_Pruebas " +
                       "WHERE p.Id_Pruebas = ? AND m.MEASURETYPE = ?";
    
        try (Connection conexion = DriverManager.getConnection(Conexion.getUrl(), Conexion.getUsuario(), Conexion.getContrasena());
             PreparedStatement stmt = conexion.prepareStatement(query)) {
    
            // Asigna los valores a los parámetros
            stmt.setLong(1, idPrueba);
            stmt.setInt(2, measureType);
    
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                double valor = rs.getDouble("Valor_medida");
                // Verifica si el valor está dentro del rango especificado
                dentroDeRango = valor >= min && valor <= max;
            } 
    
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return dentroDeRango;
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

    public static void actualizarPrueba(boolean finalizada, boolean aprobada, Long usuario, String serialEquipo, Long idPruebas, String comentario) {
        // Consulta SQL para actualizar los valores en la tabla pruebas
        Conexion.setConexionFromFile();
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
    

    public static void mostrarDialogoAutoCierre(JFrame frame) {
        // Crear el cuadro de diálogo
        JDialog dialog = new JDialog(frame, "Mensaje", true);
        dialog.setSize(200, 100);
        dialog.setLocationRelativeTo(frame);
        dialog.setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.Y_AXIS));
        
        JLabel mensaje = new JLabel("Prueba finalizada con exito. \nPor seguridad cerraremos el programa.");
        mensaje.setHorizontalAlignment(SwingConstants.CENTER);
        dialog.add(mensaje);

        // Crear un temporizador para cerrar el cuadro de diálogo
        Timer timer = new Timer(3000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose(); // Cerrar el cuadro de diálogo
                System.exit(0);
            }
        });

        timer.setRepeats(false); // Solo ejecutar una vez
        timer.start(); // Iniciar el temporizador

        dialog.setVisible(true); // Mostrar el cuadro de diálogo
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
}
