/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.

 */
package org.soltelec.luxometro.capelec.cap2500;

//import com.soltelec.integrador.cliente.ClienteSicov;
//import com.soltelec.estandar.EstadoEventosSicov;
import com.soltelec.modulopuc.utilidades.Mensajes;
import gnu.io.SerialPort;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import org.soltelec.luxometro.DialogoAnguloInclinacion;
import org.soltelec.luxometro.PanelLuxometroMotos;
import org.soltelec.util.Conex;
import org.soltelec.util.ConsultarDatosVehiculo;

/**
 *
 * @author Usuario
 */
public class HiloLucesVehiculoCapelec implements Runnable {

    private PanelLuxometroMotos panelLuxometro;
    private Timer timer;
    private int contadorTemporizacion = 0;
    private SerialPort serialPort;
    private InputStream in;
    private OutputStream out;
    private int luzBajaDerecha;
    private int luzAltaDerecha;
    private int luzBajaIzquierda;
    private int luzAltaIzquierda;
    private double anguloBajaDerecha;
    private double anguloBajaIzquierda;
    private StringBuilder sb;
    private int numeroExploradoras;
    private int[] medidasExploradoras;
    private DialogoAnguloInclinacion daiBajaDerecha, daiBajaIzquierda;

    private volatile Thread blinker;
    private int idPrueba = 9;
    private int idUsuario = 1;
    private int idHojaPrueba = 4;
    private boolean cancelacion = false;
    private String urljdbc;
    private String puertoLuxometro;
    private double permisibleBaja = 2.5;
    private double permisibleAlta;
    private double permisibleSumatoria = 225;
    private double permisibleAnguloBajo = 0.5;
    private double permisibleAnguloAlto = 3.5;
    private int placa;

    public HiloLucesVehiculoCapelec(PanelLuxometroMotos panel) {
        this.panelLuxometro = panel;
    }

    public HiloLucesVehiculoCapelec() {
    }

    HiloLucesVehiculoCapelec(Object object, boolean b) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void registrarMedidas(MedidasVehiculos medidasVehiculos) throws ClassNotFoundException, SQLException {
        Connection conexion = null;
        permisibleBaja = 3.0;
        conexion = Conex.getConnection();
        conexion.setAutoCommit(false);
        int numLuces = 1;
        String statement = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
        PreparedStatement instruccion = conexion.prepareStatement(statement);

        instruccion.setInt(1, 2013);
        instruccion.setDouble(2, medidasVehiculos.getInclinacionLuzBajaDerecha());
        instruccion.setInt(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();
        instruccion.setInt(1, 2014);
        instruccion.setDouble(2, medidasVehiculos.getIntencidadLuzBajaDerecha());
        instruccion.setInt(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();

        instruccion.setInt(1, 2000);
        instruccion.setDouble(2, medidasVehiculos.getIntencidadLuzAltaDerecha());
        instruccion.setInt(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();

        String str2 = "INSERT INTO defxprueba(id_defecto,id_prueba,Tipo_defecto) VALUES (?,?,'A')";
        PreparedStatement psDefectos = conexion.prepareStatement(str2);
        boolean aprobada = true;
        if (!((medidasVehiculos.getInclinacionLuzBajaDerecha() >= permisibleAnguloBajo) || (medidasVehiculos.getInclinacionLuzBajaDerecha() <= permisibleAnguloAlto))) {
            psDefectos.clearParameters();
            psDefectos.setInt(1, 20002);//codigo defecto es distinto
            psDefectos.setInt(2, idPrueba);
            psDefectos.executeUpdate();
            aprobada = false;

        }
        if (!(medidasVehiculos.getIntencidadLuzBajaDerecha() >= permisibleBaja)) {
            psDefectos.clearParameters();
//            psDefectos.setInt(1, 20002);//codigo defecto es Sdistinto
            psDefectos.setInt(1, 24005);//codigo defecto es distinto
            psDefectos.setInt(2, idPrueba);
            //psDefectos.setInt(3, idHojaPrueba);
            psDefectos.executeUpdate();
            aprobada = false;
        }
        if (!(medidasVehiculos.getIntencidadLuzAltaDerecha() >= permisibleSumatoria)) {
            //las que se pueden prender al tiempo yse supone mayores
            psDefectos.clearParameters();
            psDefectos.setInt(1, 20001);
            psDefectos.setInt(2, idPrueba);
            psDefectos.executeUpdate();
            aprobada = false;
        }

        String statement2;
        String serialEquipo = "";
        try {
            serialEquipo = ConsultarDatosVehiculo.buscarSerialEquipo(idPrueba);
        } catch (Exception e) {
            serialEquipo = "Serial no encontrado";
            e.printStackTrace();
        }
        if (aprobada) {
            statement2 = "UPDATE pruebas SET Finalizada = 'Y',Aprobada ='Y',Abortada='N',usuario_for = ?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?";
        } else {
            statement2 = "UPDATE pruebas SET Finalizada = 'Y',Aprobada ='N',Abortada='N',usuario_for = ?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?";
        }
        PreparedStatement instruccion2 = conexion.prepareStatement(statement2);
        instruccion2.setInt(1, idUsuario);
        instruccion2.setString(2, serialEquipo);
        instruccion2.setInt(3, idPrueba);
        int executeUpdate = instruccion2.executeUpdate();
        conexion.commit();
        conexion.setAutoCommit(true);
        conexion.close();
        Mensajes.messageDoneTime("Se ha Registrado la Prueba de Luces para el Vehiculo de una manera Exitosa ..¡",3);
        // INICIO EVENTOS SICOV
        //ClienteSicov.eventoPruebaSicov(idPrueba, EstadoEventosSicov.FINALIZADO, "", serialEquipo);
        // FIN EVENTOS SICOV

    }//end of method registrar medidas

    public boolean verificarMedidas(double... medidas) {
        for (double d : medidas) {
            if (d <= 0) {
                return false;
            }
        }
        return true;
    }

    private void registrarCancelacion(String comentario, int idPrueba, int idUsuario) {
        try {
            String serialEquipo = "";
            try {
                serialEquipo = ConsultarDatosVehiculo.buscarSerialEquipo(idPrueba);
            } catch (Exception e) {
                serialEquipo = "Serial no encontrado";
                e.printStackTrace();
            }
            Connection conexion = Conex.getConnection();
            //Crear objeto preparedStatement para realizar la consulta con la base de datos
            String statement = "UPDATE pruebas SET Finalizada = 'Y',Aprobada='N',Abortada='Y',Comentario_aborto=?,usuario_for = ?,Fecha_aborto=?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?";
            PreparedStatement instruccion = conexion.prepareStatement(statement);
            instruccion.setString(1, comentario);
            instruccion.setInt(2, idUsuario);
            instruccion.setString(4, serialEquipo);
            instruccion.setInt(5, idPrueba);
            Date d = new Date();
            Timestamp timestamp = new Timestamp(d.getTime());
            instruccion.setTimestamp(3, timestamp);
            //Un objeto ResultSet, almacena los datos de resultados de una <span class="IL_AD" id="IL_AD11">consulta</span>
            int n = instruccion.executeUpdate();
            conexion.close();
            Mensajes.messageDoneTime("Se ha Registrado la Prueba de Luces para  el Vehiculo de una manera Exitosa ..¡",3);
            // INICIO EVENTOS SICOV
            //ClienteSicov.eventoPruebaSicov(idPrueba, EstadoEventosSicov.INICIADO, comentario, serialEquipo);
            // FIN EVENTOS SICOV
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(panelLuxometro, "Error no se encuentra el driver de MySQL");
            System.out.println(e);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(panelLuxometro, "Error conectandose con la base de datos");
            System.out.println(e);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(panelLuxometro, "Error conectandose con la base de datos");
            System.out.println(e);
        }
    }

    public int getIdPrueba() {
        return idPrueba;
    }

    public void setIdPrueba(int idPrueba) {
        this.idPrueba = idPrueba;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    private void cargarUrl() {
        Properties props = new Properties();
        //try retrieve data from file
        try {
            props.load(new FileInputStream("./propiedades.properties"));//TODO warining
            urljdbc = props.getProperty("urljdbc");
            System.out.println(urljdbc);
        } //catch exception in case properties file does not exist
        catch (IOException e) {
            e.printStackTrace();
        }
    }//end of method cargarUrl

    private boolean cargarPermisibles() throws ClassNotFoundException, SQLException {

        Connection conexion = Conex.getConnection();
        String statement = "SELECT Valor_maximo FROM permisibles WHERE Id_permisible = ?";
        PreparedStatement instruccion = conexion.prepareStatement(statement);
        instruccion.setInt(1, 6);
        ResultSet executeQuery = instruccion.executeQuery();
        if (executeQuery.first()) {
            permisibleBaja = executeQuery.getDouble("Valor_maximo");
            permisibleBaja = 2.5;//hardcoded very hardcoded...
        } else {
            JOptionPane.showMessageDialog(null, "Error cargando los valores vehiculo");
            conexion.close();
            return false;
        }
        instruccion.clearParameters();
        instruccion.setInt(1, 7);
        executeQuery = instruccion.executeQuery();
        if (executeQuery.first()) {
            permisibleAlta = executeQuery.getDouble("Valor_maximo");
        } else {
            JOptionPane.showMessageDialog(null, "Error cargando los valores vehiculo");
            conexion.close();
            return false;
        }
        instruccion.clearParameters();
        instruccion.setInt(1, 5);
        executeQuery = instruccion.executeQuery();
        if (executeQuery.first()) {
            permisibleSumatoria = executeQuery.getDouble("Valor_maximo");
        } else {
            JOptionPane.showMessageDialog(null, "Error cargando los valores vehiculo");
            conexion.close();
            return false;
        }
        String strDos = "SELECT Valor_minimo,Valor_maximo FROM permisibles WHERE Id_permisible = 9";
        PreparedStatement instruccion2 = conexion.prepareStatement(strDos);
        executeQuery = instruccion2.executeQuery();
        if (executeQuery.first()) {
            permisibleAnguloBajo = executeQuery.getDouble("Valor_minimo");
            permisibleAnguloAlto = executeQuery.getDouble("Valor_maximo");
        } else {
            JOptionPane.showMessageDialog(null, "Error cargando los valores vehiculo");
            conexion.close();
            return false;
        }
        conexion.close();
        return true;
    }

    public void setIdHojaPrueba(int theIdHojaPruebas) {
        idHojaPrueba = theIdHojaPruebas;
    }

    @Override
    public void run() {
        LecturaComCapelec lecturaComCapelec = new LecturaComCapelec();
        LecturaInformacionCapelec lecturaInformacionCapelec = new LecturaInformacionCapelec();
        try {
            List<String> lineas = lecturaComCapelec.lecturaCom();
            MedidasVehiculos medidasVehiculos = lecturaInformacionCapelec.buscarDatosMotos(lineas);
            registrarMedidas(medidasVehiculos);
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    private boolean cargarParametrosVehiculo() throws ClassNotFoundException, SQLException {

        Connection conexion = Conex.getConnection();
        String statement = "SELECT v.Modelo,v.CAR,v.Tiempos_motor,hp.TESTSHEET FROM pruebas as p INNER JOIN hoja_pruebas as hp ON p.hoja_pruebas_for = hp.TESTSHEET INNER JOIN Vehiculos as v ON hp.Vehiculo_for = v.CAR WHERE p.Id_Pruebas = ?";
        PreparedStatement instruccion = conexion.prepareStatement(statement);
        instruccion.setInt(1, idPrueba);
        ResultSet rs = instruccion.executeQuery();
        if (rs.first()) {
            int modelo = rs.getInt("Modelo");
            placa = rs.getInt("CAR");
            int tiemposMotor = rs.getInt("Tiempos_motor");
            conexion.close();
            return true;
        } else {
            conexion.close();
            return false;
        }

    }

    void setVisible(boolean b) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
