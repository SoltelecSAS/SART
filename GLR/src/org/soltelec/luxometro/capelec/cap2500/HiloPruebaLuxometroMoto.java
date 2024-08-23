/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.

 */
package org.soltelec.luxometro.capelec.cap2500;

//import com.soltelec.integrador.cliente.ClienteSicov;
//import com.soltelec.estandar.EstadoEventosSicov;
import com.soltelec.modulopuc.utilidades.Mensajes;
import org.soltelec.luxometro.*;
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import org.soltelec.util.Conex;
import org.soltelec.util.ConsultarDatosVehiculo;

/**
 *
 * @author Usuario
 */
public class HiloPruebaLuxometroMoto implements Runnable, ActionListener {

    private PanelLuxometroMotosCapelec panelLuxometro;
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
    //private String puertoLuxometro;
    private double permisibleBaja = 2.5;
    private double permisibleAlta;
    private double permisibleSumatoria = 225;
    private double permisibleAnguloBajo = 0.5;
    private double permisibleAnguloAlto = 3.5;
    private int placa;
    private int numLuces;

    public HiloPruebaLuxometroMoto(PanelLuxometroMotosCapelec panel) {
        this.panelLuxometro = panel;
        this.panelLuxometro.getButtonCancelar().addActionListener(this);
    }

    public boolean buscarLuxometro() {
        return true;
    }

    @Override
    public void run() {

        try {
            crearNuevoTimer();
            contadorTemporizacion = 0;
            timer.start();
            panelLuxometro.getLabelTitulo().setText("Esperando datos...");

            LecturaComCapelec lecturaComCapelec = new LecturaComCapelec();
            Object numeroLuces = JOptionPane.showInputDialog(panelLuxometro, "Digite el numero de Luces ", "Numero de Luces", JOptionPane.DEFAULT_OPTION, new ImageIcon(),
                    new Object[]{new Integer(1), new Integer(2)}, (Object) (new Integer(1)));
            numLuces = ((Integer) numeroLuces).intValue();
            panelLuxometro.habilitarLuces(numLuces);
            blinker = Thread.currentThread();

            List<String> lineas = lecturaComCapelec.lecturaCom();
            LecturaInformacionCapelec lecturaInformacionCapelec = new LecturaInformacionCapelec();
            MedidasVehiculos medidasVehiculos = lecturaInformacionCapelec.buscarDatosMotos(lineas);
            luzBajaDerecha = medidasVehiculos.getIntencidadLuzBajaDerecha();
            luzAltaDerecha = medidasVehiculos.getIntencidadLuzAltaDerecha();
            anguloBajaDerecha = medidasVehiculos.getInclinacionLuzBajaDerecha();
            //Dialogo para pedir el numero de luces... no exploradoras
            //cargarUrl(); reemplazado pr cargarParametros
            //cargarParametros();
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Imposible cargar archivo propiedades");
            return;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Imposible cargar archivo propiedades");
            return;
        }

        try {

            try {
                registrarMedidas(idPrueba, idUsuario, numLuces);
                panelLuxometro.getLabelTitulo().setText("Datos tomados");
                panelLuxometro.getLuzBaja().setBlinking(false);
                panelLuxometro.getLuzAlta2().setBlinking(false);
                panelLuxometro.getLuzAlta().setBlinking(false);
                panelLuxometro.getLuzBaja2().setBlinking(false);
                Thread.sleep(5000);
            } catch (ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(null, "No se encuentra el driver de Mysql");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error conectandose con la base de datos");
                ex.printStackTrace(System.err);
            } finally {
                panelLuxometro.cerrar();
            }

        } catch (Exception ie) {
            System.out.println("Prueba Cancelada");
            String showInputDialog = JOptionPane.showInputDialog("comentario de aborto");
            panelLuxometro.cerrar();
            serialPort.close();
            timer.stop();
            if (cancelacion == true) {
                registrarCancelacion(showInputDialog, idPrueba, idUsuario);
            }
        }

    }//end of method run

    private void crearNuevoTimer() {
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //contadorTemporizacion++;
                contadorTemporizacion++;
                panelLuxometro.getDisplay().setLcdValue(contadorTemporizacion);
            }

        });
    }//end of crearNuevoTimer

//    private void dormir(int tiempo) {
//        try {
//            Thread.sleep(tiempo);
//        } catch (InterruptedException ex) {
//            Logger.getLogger(HiloPruebaLuxometro.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    /**
     * Obtener la trama y analizarla verificar que la trama corresponda con la
     * secuencia
     *
     * @return bandera indicando que coincide
     */
    private int tomarMedicion(Luz luz) throws IOException {

        byte[] b;

        b = leerTrama();
        if (b == null) {
            return -100;//retorno ok
        }
        String cadena = new String(b);//rogamos a Dios que sea la correcta
        StringBuilder sb = new StringBuilder(cadena);
        sb.deleteCharAt(0);
        cadena = sb.toString();
        int valor = -1;
        boolean medicionTomada = false;
        switch (luz) {

            case ALTADERECHA://verificar que la cadena corresponda
                String cadenaComparacion = "L01MRK";//cadena hay que quitarle los espacios???
                if (cadena.regionMatches(0, cadenaComparacion, 0, 6)) {
                    System.out.println("Luz Alta Derecha");
                    medicionTomada = true;
                    valor = Integer.parseInt(cadena.substring(6, 9));
                }
                break;
            case ALTAIZQUIERDA:
                cadenaComparacion = "L01MLK";
                if (cadena.regionMatches(0, cadenaComparacion, 0, 6)) {
                    System.out.println("Luz Alta Izquierda");
                    medicionTomada = true;
                    valor = Integer.parseInt(cadena.substring(6, 9));
                }
                break;
            case BAJADERECHA:
                cadenaComparacion = "L01DRK";
                if (cadena.regionMatches(0, cadenaComparacion, 0, 6)) {
                    System.out.println("Luz Baja Derecha");
                    medicionTomada = true;
                    valor = Integer.parseInt(cadena.substring(6, 9));
                }

                break;
            case BAJAIZQUIERDA:
                cadenaComparacion = "L01DLK";
                if (cadena.regionMatches(0, cadenaComparacion, 0, 6)) {
                    System.out.println("Luz Baja Izquierda");
                    medicionTomada = true;
                    valor = Integer.parseInt(cadena.substring(6, 9));
                }
                break;
            case EXPLORADORA:
                cadenaComparacion = "L01FLK";
                String cadenaComparacion2 = "L01FRK";
                if (cadena.regionMatches(0, cadenaComparacion, 0, 6) || cadena.regionMatches(0, cadenaComparacion2, 0, 6)) {
                    System.out.println("Luz Exploradora");
                    medicionTomada = true;
                    valor = Integer.parseInt(cadena.substring(6, 9));
                }

                break;
            default:
                medicionTomada = false;
                System.out.println("Trama no reconocida");
                break;

        }//end of switch

        return valor;
    }

    /**
     * Leer byte a byte la trama desde el puerto serial
     *
     * @throws IOException
     */
    private byte[] leerTrama() throws IOException {

        boolean existeTrama = false;
        byte[] buffer = new byte[1024];
        int data;
        int len = 0;
        while ((data = in.read()) > -1) {
            buffer[len++] = (byte) data;
        }//end while
        if (len > 0) {//si hay datos
            System.out.println("Longitud del mensaje:" + len);            
            byte[] arreglo = new byte[len];
            //armar el mensaje
            System.arraycopy(buffer, 0, arreglo, 0, len);
            return arreglo;

        }//end if else
        else {
            return null;//
        }

    }

    private void connect(CommPortIdentifier portIdentifier) throws Exception {
        CommPort commPort;
        commPort = portIdentifier.open(this.getClass().getName(), 2000);
        if (commPort instanceof SerialPort) {
            serialPort = (SerialPort) commPort;
            //serialPort.setSerialPortParams(57600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
            serialPort.setSerialPortParams(2400, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            in = serialPort.getInputStream();
            out = serialPort.getOutputStream();
            //banco.setPuertoSerial(commPort);
            //banco.setIn(in);//Inicializar apropiadamente los flujos
            //banco.setOut(out);
        }//end if
    }//end of method connect

    private SerialPort connect(String portName, int baudRate, int dataBits, int stopBits, int parity) throws Exception {

        SerialPort serialPort = null;
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        if (portIdentifier.isCurrentlyOwned()) {
            System.out.println("Error: Port is currently in use");
        } else {
            CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);

            if (commPort instanceof SerialPort) {
                serialPort = (SerialPort) commPort;

                //serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
                serialPort.setSerialPortParams(2400, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                serialPort.setDTR(false);
                serialPort.setRTS(false);
                serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN);
                in = serialPort.getInputStream();
                out = serialPort.getOutputStream();
                //serialPort.addEventListener(new ProductorCapelec(in,bufferMed));
                //serialPort.notifyOnDataAvailable(true);
            } else {
                System.out.println("Error: Only serial ports are handled by this example.");
            }
        }
        return serialPort;
    }//end of method connect

    private void inicializarPuertoSerial(String portName) throws Exception {

        //capelecServicios.setPuertoSerial(serialPort);
        //buttonConectar.setEnabled(false);
    }//end of inicializarPuertoSerial

    @Override
    public void actionPerformed(ActionEvent e) {
        this.stop();
    }

    private void stop() {
        cancelacion = true;
        Thread tmpBlinker = blinker;
        blinker = null;
        if (tmpBlinker != null) {
            tmpBlinker.interrupt();
        }
    }

    private void registrarMedidas(int idPrueba, int idUsuario, int numLuces) throws ClassNotFoundException, SQLException {

//        boolean registro = verificarMedidas(anguloBajaDerecha, luzBajaDerecha, luzAltaDerecha, anguloBajaIzquierda, luzBajaIzquierda, luzAltaIzquierda);//Verifica si una medida es menor o igual a cero
//        if (!registro) {
//            JOptionPane.showMessageDialog(null, "Alguno o todos los datos son invalidos debe reiniciar la prueba?");
//            return;
//        }
        Connection conexion = null;
        permisibleBaja = 3.0;
        conexion = Conex.getConnection();
        conexion.setAutoCommit(false);
        String statement = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
        PreparedStatement instruccion = conexion.prepareStatement(statement);
        if (numLuces > 0) {
            instruccion.setInt(1, 2013);
            instruccion.setDouble(2, anguloBajaDerecha);
            instruccion.setInt(3, idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();
            instruccion.setInt(1, 2014);
            instruccion.setDouble(2, luzBajaDerecha);
            instruccion.setInt(3, idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();
        }
        if (numLuces > 1) {
            instruccion.setInt(1, 2000);
            instruccion.setDouble(2, luzAltaDerecha);
            instruccion.setInt(3, idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();
        }

        instruccion.setInt(1, 2011);
        if ((luzAltaDerecha + luzAltaIzquierda) > (luzBajaDerecha + luzBajaIzquierda)) {
            System.out.println("La luz alta es mayor a la luz baja");
            instruccion.setDouble(2, luzAltaDerecha + luzAltaIzquierda);
        } else {
            instruccion.setDouble(2, luzBajaDerecha + luzBajaIzquierda);
        }
        instruccion.setInt(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();

//        double luzMenorBaja = -1;
//        double luzMenorAlta = -1;
//        int medidaDefectoLuz = -1;
        String str2 = "INSERT INTO defxprueba(id_defecto,id_prueba,Tipo_defecto) VALUES (?,?,'A')";
        PreparedStatement psDefectos = conexion.prepareStatement(str2);
        String str3 = "UPDATE medidas SET Condicion='*' WHERE medidas.MEASURE = ?";//Esto no hace nada
        boolean aprobada = true;

        if (anguloBajaDerecha < permisibleAnguloBajo || anguloBajaDerecha > permisibleAnguloAlto) {
            psDefectos.clearParameters();
            psDefectos.setInt(1, 24006);//codigo defecto es distinto
            psDefectos.setInt(2, idPrueba);

            //psDefectos.setInt(3, idHojaPrueba);
            psDefectos.executeUpdate();
            aprobada = false;
        }
        if (luzBajaDerecha < permisibleBaja /*|| luzMenorAlta < permisibleBaja*/) {
            psDefectos.clearParameters();
            psDefectos.setInt(1, 24005);
            psDefectos.setInt(2, idPrueba);
            psDefectos.executeUpdate();
            aprobada = false;
        }
        if ((luzAltaDerecha + luzAltaIzquierda) > permisibleSumatoria) {
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
        Mensajes.messageDoneTime("Se ha Registrado la Prueba de Luces para el vehiculo de una manera Exitosa ..¡", 3);
        // INICIO EVENTOS SICOV
        // ClienteSicov.eventoPruebaSicov(idPrueba, EstadoEventosSicov.FINALIZADO, "", serialEquipo);
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
            //Cargar clase de controlador de base de datos
            String serialEquipo = "";
            try {
                serialEquipo = ConsultarDatosVehiculo.buscarSerialEquipo(idPrueba);
            } catch (Exception e) {
                serialEquipo = "Serial no encontrado";
            }
            //Crear el objeto de <span class="IL_AD" id="IL_AD12">conexion</span> a la base de datos
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
            Mensajes.messageDoneTime("Se ha Cancelado la Prueba de Luces para la Moto de una manera Exitosa ..¡", 3);
            // INICIO EVENTOS SICOV
            //ClienteSicov.eventoPruebaSicov(idPrueba, EstadoEventosSicov.CANCELADO, comentario, serialEquipo);
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

        //Crear el objeto de <span class="IL_AD" id="IL_AD12">conexion</span> a la base de datos
        Connection conexion = Conex.getConnection();
        //Insertar las medidas dependiendo del numero de tiempos del motor
        //cargar el permisible inferior de luces 
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

    enum Luz {

        ALTADERECHA(1), ALTAIZQUIERDA(2), BAJADERECHA(3), BAJAIZQUIERDA(4), EXPLORADORA(5);
        private int numeroLuz;

        Luz(int numLuz) {
            this.numeroLuz = numLuz;
        }

        public void setNumeroLuz(int numeroLuz) {
            this.numeroLuz = numeroLuz;
        }

    }

    private boolean cargarParametrosVehiculo() throws ClassNotFoundException, SQLException {

        //Crear el objeto de <span class="IL_AD" id="IL_AD12">conexion</span> a la base de datos
        Connection conexion = Conex.getConnection();
        //Insertar las medidas dependiendo del numero de tiempos del motor
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

}
