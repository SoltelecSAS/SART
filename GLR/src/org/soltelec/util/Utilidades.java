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

    private static String metodoRpm = null;
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
        String consulta = "SELECT cont_test FROM cda WHERE id_cda = 1";
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

    public static void actualizarPrueba(boolean finalizada, boolean aprobada, Long usuario, String serialEquipo, Long idPruebas) {
        // Consulta SQL para actualizar los valores en la tabla pruebas
        String query = "UPDATE pruebas " +
                       "SET Finalizada = ?, " +
                       "    Aprobada = ?, " +
                       "    Abortada = 'N', " +
                       "    usuario_for = ?, " +
                       "    serialEquipo = ? " +
                       "WHERE Id_Pruebas = ?";
        
        try (Connection conexion = DriverManager.getConnection(Conexion.getUrl(), Conexion.getUsuario(), Conexion.getContrasena());
                PreparedStatement stmt = conexion.prepareStatement(query)) {
    
            // Asigna los valores a los parámetros de la consulta
            stmt.setString(1, finalizada ? "Y" : "N");
            stmt.setString(2, aprobada ? "Y" : "N");
            stmt.setLong(3, usuario);
            stmt.setString(4, serialEquipo);
            stmt.setLong(5, idPruebas);
    
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
}
