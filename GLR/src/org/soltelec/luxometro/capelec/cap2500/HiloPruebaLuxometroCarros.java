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
import java.awt.Dialog.ModalityType;
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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import org.soltelec.util.Conex;
import org.soltelec.util.ConsultarDatosVehiculo;

/**
 *
 *
 * @author Usuario
 */

/*
 * Codigos para medidas pueba de luces de carros
 *
 * 2003 angulo inclinacion baja derecha
 * 2005 angulo inclinacion baja izquierda
 * 2006 intensidad baja derecha
 * 2007 intensidad alta derecha
 * 2008 intensidad luces exploradoras
 * 2009 intensidad baja izquierda
 * 2010 intensidad alta izquierda
 * 2011 suma de todas las intensidades
 */
public class HiloPruebaLuxometroCarros implements Runnable, ActionListener {

    private PanelLuxometro panelLuxometro;
    private Timer timer;
    private int contadorTemporizacion = 0;
    private SerialPort serialPort;
    private InputStream in;
    private OutputStream out;
    private int luzBajaDerecha;
    private int luzAltaDerecha;
    private double luzBajaIzquierda;
    private int luzAltaIzquierda;
    private double anguloBajaDerecha;
    private double anguloBajaIzquierda;
    private StringBuilder sb;
    private int numeroExploradoras;
    private int[] medidasExploradoras;
    private DialogoAnguloInclinacion daiBajaDerecha, daiBajaIzquierda;
    private String urljdbc;
    private boolean cancelacion = false;
    private Thread blinker;
    private Long idPrueba = 18L;
    private long idUsuario = 1;
    private long idHojaPrueba;
    private double sumaExploradoras;
    private double permisibleBaja;
    private double permisibleAlta;
    private double permisibleSumatoria;
    private double permisibleAnguloBajo;
    private double permisibleAnguloAlto;

    public HiloPruebaLuxometroCarros(PanelLuxometro panel) {
        this.panelLuxometro = panel;
        panelLuxometro.getButtonCancelar().addActionListener(this);
        cargarUrl();

    }

    public boolean buscarLuxometro() {
        return true;
    }

    @Override
    public void run() {

        LecturaComCapelec lecturaComCapelec = new LecturaComCapelec();
        LecturaInformacionCapelec lecturaInformacionCapelec = new LecturaInformacionCapelec();
        try {
            crearNuevoTimer();
            contadorTemporizacion = 0;
            timer.start();
            panelLuxometro.getLabelMensaje().setText("Esperando Datos...");

            List<String> lineas = lecturaComCapelec.lecturaCom();
            MedidasVehiculos medidasVehiculos = lecturaInformacionCapelec.buscarDatosAutos(lineas);
            anguloBajaIzquierda = medidasVehiculos.getInclinacionLuzBajaIzquierda();
            luzBajaDerecha = medidasVehiculos.getIntencidadLuzBajaDerecha();
            luzAltaDerecha = medidasVehiculos.getIntencidadLuzAltaDerecha();
            anguloBajaDerecha = medidasVehiculos.getInclinacionLuzBajaDerecha();
            luzAltaIzquierda = medidasVehiculos.getIntencidadLuzAltaIzquierda();
            luzBajaIzquierda = medidasVehiculos.getIntencidadLuzBajaIzquierda();

            try {
                cargarPermisibles();
                try {
                    registrarMedidas(idPrueba, idUsuario);
                } catch (Exception e) {
                    System.out.println(e);
                }
            } catch (ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(panelLuxometro, "Error no se encuentra el driver de MySQL");
                ex.printStackTrace(System.err);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(panelLuxometro, "Error de sentencia sql escribiendo medidas");
                ex.printStackTrace(System.err);
            }
            panelLuxometro.getLabelMensaje().setText("Datos Tomados... ");
            panelLuxometro.getLabelLuzBajaDerecha().setForeground(Color.green);
            panelLuxometro.getLabelLuzAltaDerecha().setForeground(Color.green);
            panelLuxometro.getLabelLuzAltaIzquierda().setForeground(Color.green);
            panelLuxometro.getLabelLuzBajaIzquierda().setForeground(Color.green);
            panelLuxometro.getAltaDerecha().setOnOff(true);
            panelLuxometro.getAltaIzquierda().setOnOff(true);
            panelLuxometro.getBajaDerecha().setOnOff(true);
            panelLuxometro.getBajaIzquierda().setOnOff(true);
            Thread.sleep(5000);
            panelLuxometro.cerrar();

        } catch (Exception e) {
            System.out.println(e);
            panelLuxometro.cerrar();
            serialPort.close();
            if (cancelacion) {
                String showInputDialog = JOptionPane.showInputDialog("Comentaro de Aborto");
                registrarCancelacion(showInputDialog, idPrueba, idUsuario);

            }
        }

    }//end of method run

    private void crearNuevoTimer() {
        timer = new Timer(1000, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    //contadorTemporizacion++;
                    panelLuxometro.getDisplay().setLcdValue(contadorTemporizacion++);
                    Thread.sleep(5);
                    panelLuxometro.getDisplay().setLcdValue(contadorTemporizacion);
                    Thread.sleep(5);
                    panelLuxometro.getDisplay().setLcdValue(contadorTemporizacion);
                    //System.out.println("I am alive");
                } catch (InterruptedException ex) {
                    System.out.println("Does not care");
                }
            }

        });
    }//end of crearNuevoTimer

//    private void Thread.sleep(int tiempo) {
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
            //copiar a un arreglo para armar el mensaje
            byte[] arreglo = new byte[len];
            //armar el mensaje
            System.arraycopy(buffer, 0, arreglo, 0, len);
//            //
//            int lenchars = len/2;
//            short[] caracteres = new short[lenchars];
//            for(int i = 0; i < len/2;i++){
//                byte[] a = new byte[]{arreglo[i],arreglo[i+1]};
//                caracteres[i] =  UtilGasesModelo.fromBytesToShort(a);
//
//
//            }

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

    private void registrarCancelacion(String comentario, Long idPrueba, long idUsuario) {
        try {
            String serialEquipo = "";
            try {
                serialEquipo = ConsultarDatosVehiculo.buscarSerialEquipo(idPrueba);
            } catch (Exception e) {
                serialEquipo = "Serial no encontrado";
            }
            //Crear el objeto de <span class="IL_AD" id="IL_AD12">conexion</span> a la base de datos
            Connection conexion = Conex.getConnection();
            //Crear objeto preparedStatement para realizar la consulta con la base de datos
            String statement = "UPDATE pruebas SET Finalizada = 'Y',Aprobado='N',Abortado='Y',Comentario_aborto=?,usuario_for = ?,Fecha_aborto=?,usuario_for = ? WHERE pruebas.Id_Pruebas = ?";//pendiente la fecha de aborto
            PreparedStatement instruccion = conexion.prepareStatement(statement);
            instruccion.setString(1, comentario);
            instruccion.setLong(2, idUsuario);
            instruccion.setString(4, serialEquipo);
            instruccion.setLong(5, idPrueba);
            Date d = new Date();
            Calendar c = new GregorianCalendar();
            c.setTime(d);
            instruccion.setDate(3, new java.sql.Date(c.getTimeInMillis()));
            //Un objeto ResultSet, almacena los datos de resultados de una <span class="IL_AD" id="IL_AD11">consulta</span>
            int n = instruccion.executeUpdate();
            conexion.close();
            Mensajes.messageDoneTime("Se ha Registrado la Prueba de Luces para el Vehiculo de una manera Exitosa ..¡",3);
            // INICIO EVENTOS SICOV
            //ClienteSicov.eventoPruebaSicov(idPrueba.intValue(), EstadoEventosSicov.INICIADO, comentario, serialEquipo);
            // FIN EVENTOS SICOV
        } catch (ClassNotFoundException e) {
            System.out.println(e);
        } catch (SQLException e) {
            System.out.println(e);
        } catch (Exception e) {
            System.out.println(e);
        }

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

    private void registrarMedidas(Long idPrueba, long idUsuario) throws ClassNotFoundException, SQLException {
        //Crear el objeto de <span class="IL_AD" id="IL_AD12">conexion</span> a la base de datos
        Connection conexion = Conex.getConnection();
        conexion.setAutoCommit(false);
        permisibleBaja = 2.5;
        permisibleAnguloAlto = 3.5;
        permisibleAnguloBajo = 0.5;
        permisibleSumatoria = 225;
        //Crear objeto preparedStatement para realizar la consulta con la base de datos
        //String statementBorrar = ("DELETE  FROMmedidas WHERE TEST = ?");
        //PreparedStatement instruccionBorrar = conexion.prepareStatement(statementBorrar);
        //instruccionBorrar.setInt(1,idPrueba);
        //instruccionBorrar.executeUpdate();
        String statement = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
        PreparedStatement instruccion = conexion.prepareStatement(statement);
        instruccion.setInt(1, 2003);
        instruccion.setDouble(2, anguloBajaDerecha);
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();
        instruccion.setInt(1, 2006);
        instruccion.setDouble(2, luzBajaDerecha);
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();
        instruccion.setInt(1, 2007);
        instruccion.setDouble(2, luzAltaDerecha);
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();
        instruccion.setInt(1, 2005);
        instruccion.setDouble(2, anguloBajaIzquierda);
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();
        instruccion.setInt(1, 2009);
        instruccion.setDouble(2, luzBajaIzquierda);
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();
        instruccion.setInt(1, 2010);
        instruccion.setDouble(2, luzAltaIzquierda);
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();
        instruccion.setInt(1, 2008);
        instruccion.setDouble(2, sumaExploradoras);
        instruccion.setLong(3, idPrueba);
        instruccion.setInt(1, 2011);
        double mayorSuma = (luzAltaDerecha + luzAltaIzquierda + sumaExploradoras) > (luzBajaDerecha + luzBajaIzquierda + sumaExploradoras) ? (luzAltaDerecha + luzAltaIzquierda + sumaExploradoras) : (luzBajaDerecha + luzBajaIzquierda + sumaExploradoras);
        instruccion.setDouble(2, mayorSuma);
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();

        //Evaluar la prueba
        String str2 = "INSERT INTO defxprueba(id_defecto,id_prueba,Tipo_defecto) VALUES (?,?,'A')";
        PreparedStatement psDefectos = conexion.prepareStatement(str2);
        //String str3 = "UPDATE medidas SET Condicion='*' WHERE medidas.MEASURE = ?";
        boolean aprobada = true;
        if (luzBajaDerecha < permisibleBaja || luzBajaIzquierda < permisibleBaja) {
            psDefectos.clearParameters();
            psDefectos.setInt(1, 20000); //La intensidad en algun haz de luz baja, es inferior a los 2,5 Klux a 1m o 4 lux a 25 m.
            psDefectos.setLong(2, idPrueba);
            psDefectos.executeUpdate();
            aprobada = false;
        }
//            } else if(luzAltaDerecha < permisibleAlta || luzAltaIzquierda < permisibleAlta) {
//                psDefectos.clearParameters();
//                psDefectos.setInt(1, 20003);
//                psDefectos.setLong(2, idPrueba);
//                psDefectos.executeUpdate();
//                aprobada = false;
//            }
        if (anguloBajaDerecha < permisibleAnguloBajo || anguloBajaDerecha > permisibleAnguloAlto) {
            psDefectos.clearParameters();
            psDefectos.setInt(1, 20002);//codigo defecto es distinto
            psDefectos.setLong(2, idPrueba); //La desviacion de cualquier haz de luz en posicion de bajas esta por fuera del rango entre 0.5 y 3.5%, siendo 0 el horizonte y 3.5% la desviacion hacia el piso
            //psDefectos.setInt(3, idHojaPrueba);
            psDefectos.executeUpdate();
            aprobada = false;
        } else if (anguloBajaIzquierda < permisibleAnguloBajo || anguloBajaIzquierda > permisibleAnguloAlto) {
            psDefectos.clearParameters();
            psDefectos.setInt(1, 20002);//codigo defecto es distinto
            psDefectos.setLong(2, idPrueba);
            //psDefectos.setInt(3, idHojaPrueba);
            psDefectos.executeUpdate();
            aprobada = false;
        }

        if ((mayorSuma) > permisibleSumatoria) {
            //las que se pueden prender al tiempo y se supone mayores
            psDefectos.clearParameters();
            psDefectos.setInt(1, 20001);
            psDefectos.setLong(2, idPrueba);
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
        instruccion2.setLong(1, idUsuario);
        instruccion2.setString(2, serialEquipo);
        instruccion2.setLong(3, idPrueba);
        int executeUpdate = instruccion2.executeUpdate();
        conexion.commit();
        conexion.setAutoCommit(true);
        conexion.close();
        Mensajes.messageDoneTime("Se ha Registrado la Prueba de Luces para el Vehiculo de una manera Exitosa ..¡",3);
        // INICIO EVENTOS SICOV
        //ClienteSicov.eventoPruebaSicov(idPrueba.intValue(), EstadoEventosSicov.FINALIZADO, "", serialEquipo);
        // FIN EVENTOS SICOV
    }

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
            permisibleBaja = 2.5;
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
            permisibleAnguloAlto = executeQuery.getDouble("Valor_maximo");//remiendo solamente un remiendo
        } else {
            JOptionPane.showMessageDialog(null, "Error cargando los valores vehiculo");
            conexion.close();
            return false;
        }
        conexion.close();
        return true;
    }

    public long getIdHojaPrueba() {
        return idHojaPrueba;
    }

    public void setIdHojaPrueba(long idHojaPrueba) {
        this.idHojaPrueba = idHojaPrueba;
    }

    public long getIdPrueba() {
        return idPrueba;
    }

    public void setIdPrueba(long idPrueba) {
        this.idPrueba = idPrueba;
    }

    public long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(long idUsuario) {
        this.idUsuario = idUsuario;

    }

    public enum Luz {

        ALTADERECHA(1), ALTAIZQUIERDA(2), BAJADERECHA(3), BAJAIZQUIERDA(4), EXPLORADORA(5);
        private int numeroLuz;

        Luz(int numLuz) {
            this.numeroLuz = numLuz;
        }

        public void setNumeroLuz(int numeroLuz) {
            this.numeroLuz = numeroLuz;
        }

    }

}
