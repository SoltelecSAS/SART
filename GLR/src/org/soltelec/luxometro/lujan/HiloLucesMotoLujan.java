/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.

 */
package org.soltelec.luxometro.lujan;


import static com.soltelec.modulopuc.persistencia.conexion.DBUtil.execIndTrama;
import com.soltelec.modulopuc.utilidades.Mensajes;
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import java.awt.Color;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.soltelec.util.Conex;
import org.soltelec.util.ConsultarDatosVehiculo;

/**
 *
 * @author Gerencia TIC
 */
public class HiloLucesMotoLujan implements Runnable, ActionListener {

    private PanelLuxometroMotosLujan panelLuxometro;
    private Timer timer;
    private int contadorTemporizacion = 0;
    private SerialPort serialPort;
    private InputStream in;
    private OutputStream out;
    private double Far1luzBajaDerecha, Far2luzBajaDerecha, Far3luzBajaDerecha, Far4luzBajaDerecha;
    private int luzAltaDerecha;
    private int luzBajaIzquierda;
    private int luzAltaIzquierda;
    private double Far1anguloBajaDerecha, Far2anguloBajaDerecha, Far3anguloBajaDerecha, Far4anguloBajaDerecha;
    private double anguloBajaIzquierda;
    private StringBuilder sb;
    private int numeroExploradoras;
    private int[] medidasExploradoras;
   // private DialogoAnguloInclinacion daiBajaDerecha, daiBajaDerecha2, daiBajaDerecha3, daiBajaDerecha4;

    private volatile Thread blinker;
    private Long idPrueba = 9L;
    private int idUsuario = 1;
    private int idHojaPrueba = 4;
    private boolean cancelacion = false;
    private String urljdbc;
    private String puertoLuxometro;
     private String nivelParseo;
    private double permisibleBaja = 2.5;
    private double permisibleAlta;
    private double permisibleSumatoria = 225;
    private double permisibleAnguloBajo = 0.5;
    private double permisibleAnguloAlto = 3.5;
    private String placas;
    private int placa;
    public static String escrTrans = "";
    public static int aplicTrans;
    public static String ipEquipo;
    public static String serialEquipo = "";
    private static String unidadMed = "";
    public static int lngTrama;
    private String tipoLuxometro;
    Properties props ;
    

    public HiloLucesMotoLujan(PanelLuxometroMotosLujan panel, int aplicTrans,String tipoLuxometro,String placas) {
        this.panelLuxometro = panel;
        this.panelLuxometro.getButtonCancelar().addActionListener(this);
        Far1luzBajaDerecha = 0;
        Far2luzBajaDerecha = 0;
        Far3luzBajaDerecha = 0;
        Far4luzBajaDerecha = 0;
        this.placas= placas;
        this.tipoLuxometro=tipoLuxometro;
        this.aplicTrans = aplicTrans;
    }

    public boolean buscarLuxometro() {
        return true;
    }

    @Override
    public void run() {
       
       try {
            //Dialogo para pedir el numero de luces... no exploradoras
            //cargarUrl(); reemplazado pr cargarParametros           
           cargarPlaca();
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Disculpe, no Existe la Mustra Correspondiente de la Placa");
            return;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Imposible cargar archivo propiedades");
            return;
        }
        
        panelLuxometro.getLabelTitulo().setText("Conectando con LUJAN..! X favor vaya Luxometro Recoger Muestra");
        JOptionPane.showMessageDialog(null, "Por Favor Dirigase al Luxometro para Recoger Muestra");
        try {
           /* Object numeroLuces = JOptionPane.showInputDialog(panelLuxometro, "Seleccione el numero de Farolas ", "Numero de Farolas", JOptionPane.DEFAULT_OPTION, new ImageIcon(),
                    new Object[]{new Integer(1), new Integer(2)}, (Object) (new Integer(9)));*/
            
            try {
            //Dialogo para pedir el numero de luces... no exploradoras
            //cargarUrl(); reemplazado pr cargarParametros           
            cargarParametros();
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Disculpe, no Existe la Mustra Correspondiente de la Placa");
            return;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Imposible cargar archivo propiedades");
            return;
        }
            int numLuces =1;// ((Integer) numeroLuces).intValue();
            panelLuxometro.habilitarLuces(numLuces);
            blinker = Thread.currentThread();
            try {
                //Dialogo para pedir el puerto serial
                //inicializarPuertoSerial(puertoLuxometro);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panelLuxometro, "Error conectandose con el puerto serial revise propiedades");
                panelLuxometro.cerrar();
                return;
            }           
            
            Thread.sleep(1500);
            panelLuxometro.getLabelTitulo().setText("FAROLA 1 BAJA DERECHA POR FAVOR  PRES ENTER !!! ");
            panelLuxometro.getLabelLuzBaja().setForeground(Color.BLUE);
            contadorTemporizacion = 0;
            timer = new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    contadorTemporizacion++;
                }
            });
            timer.start();
            //watchdog para recibir la trama de datos unos 60 segundos
            panelLuxometro.getLuzBaja().setBlinking(true);
            sb = new StringBuilder("!");
            int seqBajaAlta = 0;
           
            try {
                //Far1luzBajaDerecha = tomarMedicion(Luz.BAJADERECHA);
                
                Thread.sleep(50);
                timer.stop();
            }//end try
            catch (Exception ioe) {
                JOptionPane.showMessageDialog(panelLuxometro, "Disculpe, he presentado problemas en leer la trama");
                ioe.printStackTrace();
            }
            if (contadorTemporizacion > 59) {
                JOptionPane.showMessageDialog(panelLuxometro, "Disculpe, la prueba se finaliza debido a que tengo un minuto sin leer Trama ");
                panelLuxometro.cerrar();
                serialPort.close();
                return;
            }
            Thread.sleep(1000);
            panelLuxometro.getLabelTitulo().setText("MEDICION TOMADA DE LA FAROLA 1");
            panelLuxometro.getLuzBaja().setBlinking(false);
            panelLuxometro.getLuzBaja().setOnOff(true);
            Thread.sleep(2000);

            if (numLuces > 1) {         
                panelLuxometro.getLuzBaja2().setVisible(true);
                panelLuxometro.getLabelLuzBaja2().setVisible(true);
                contadorTemporizacion = 0;
                contadorTemporizacion = 0;
                crearNuevoTimer();
                timer.start();
                panelLuxometro.getLuzBaja2().setBlinking(true);
                panelLuxometro.getLabelTitulo().setText("FAROLA2 BAJA DERECHA POR FAVOR  PRES ENTER !!!");
                // panelLuxometro.getLabelLuzAlta().setForeground(Color.BLUE);
                //panelLuxometro.getLuzAlta().setBlinking(true);
                //watchdog para que despues de un minuto se termine la prueba
                //TODO registrar terminación por falta de tiempo
                sb = new StringBuilder("!");
                
                if (contadorTemporizacion > 59) {
                    JOptionPane.showMessageDialog(panelLuxometro, "Disculpe, la prueba se finaliza debido a que tengo un minuto sin leer Trama ");
                    panelLuxometro.cerrar();
                    serialPort.close();
                    return;
                }

                //  panelLuxometro.getLuzAlta().setBlinking(false);
                // panelLuxometro.getLuzAlta().setOnOff(true);
                timer.stop();
                panelLuxometro.getLabelTitulo().setText("MEDICION TOMADA DE LA FAROLA 2");
                Thread.sleep(1000);
                panelLuxometro.getLuzBaja2().setBlinking(false);
                panelLuxometro.getLuzBaja2().setOnOff(true);
                Thread.sleep(2000);
            }//end if numeroDe Luces mayor a uno
            

            
            panelLuxometro.getLabelTitulo().setText("Prueba Finalizada con exito");
            //anguloBajaDerecha = daiBajaDerecha.getPanel().getCheckBoxFueraRango().isSelected() ? 4.1 : anguloBajaDerecha;            
            System.err.println("Luz Baja Derecha FAROLA4" + Far4anguloBajaDerecha);

            Thread.sleep(2000);

          //  serialPort.close();
            try {
                
                registrarMedidas(idPrueba, idUsuario, numLuces);

            } catch (ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(null, "No se encuentra el driver de Mysql");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error en la BD debido a " + ex.getMessage());
                ex.printStackTrace(System.err);
            } finally {
                panelLuxometro.cerrar();
            }

        } catch (InterruptedException ie) {
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
    private double tomarMedicion(Luz luz) throws IOException {

        byte[] b;
     System.out.println(" voy a Recoger trama en bytes del luxometro ");
        b = leerTrama();
         System.out.println("entre de nuevo a Tomar medicion  ");
        if (b == null) {
            System.out.println("el array de bytes es null ");
            return -100;//retorno ok
        }
        System.out.println("leng arry Bytes "+b.length);
        String cadena = new String(b);//rogamos a Dios que sea la correcta
        System.out.println("value cadena "+cadena);
        StringBuilder sb = new StringBuilder(cadena);
        sb.deleteCharAt(0);
        cadena = sb.toString();
        double valor = -1;
        boolean medicionTomada = false;
        String cadenaComparacion = null;
        switch (luz) {          
            /* case ALTADERECHA://verificar que la cadena corresponda
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
             break;*/
            case BAJADERECHA:
                if (this.nivelParseo.equalsIgnoreCase("SERIAL")) {
                    System.out.println(" MARQUE BAJA DERECHA ");
                    
                    
                    
                    System.out.println("La cadena TOMADA IS: " + cadena);
                    System.out.println("unidad IS: 5,6 " + cadena.substring(5, 6));
                    HiloLucesMotoLujan.unidadMed = cadena.substring(5, 6);
                    if (HiloLucesMotoLujan.unidadMed.equalsIgnoreCase("K")) {
                        cadenaComparacion = "L01DRK";
                    } else {
                        cadenaComparacion = "L01DRL";
                    }
                    if (cadena.regionMatches(0, cadenaComparacion, 0, 6)) {
                        System.out.println("Luz Baja Derecha");
                        medicionTomada = true;
                         System.out.println("unidad recojida WHEN 5,9" + cadena.substring(5, 9));
                        System.out.println("unidad recojida WHEN 6,9" + cadena.substring(6, 9));
                          System.out.println("unidad recojida WHEN 6,10" + cadena.substring(6, 10));
                          System.out.println("unidad recojida WHEN 6,11" + cadena.substring(6, 11));
                         valor = Double.parseDouble(cadena.substring(6, 11));
                         
                        
                    }
                }else{                
                    
                    System.out.println("unidad recojida WHEN 9,10" + cadena.substring(9,10));
                    System.out.println("unidad recojida WHEN 5,6" + cadena.substring(8, 12));
                    System.out.println("unidad recojida WHEN 8,11" + cadena.substring(8, 11));
                    System.out.println("La cadena TOMADA IS: " + cadena);
                    HiloLucesMotoLujan.unidadMed = cadena.substring(5, 6);
                    cadenaComparacion = "L01D1K";                    
                    if (cadena.regionMatches(0, cadenaComparacion, 0, 6)) {
                         System.out.println(" MARQUE BAJA DERECHA ");
                        System.out.println("Luz Baja Derecha");
                        medicionTomada = true;
                        valor = Double.parseDouble(cadena.substring(7, 11));
                    }
                }
                break;
            /* case BAJAIZQUIERDA:
             cadenaComparacion = "L01DLK";
             if (cadena.regionMatches(0, cadenaComparacion, 0, 6)) {
             System.out.println("Luz Baja Izquierda");
             medicionTomada = true;
             valor = Integer.parseInt(cadena.substring(6, 9));
             }
             break;*/
            case EXPLORADORA:
                String cadenaComparacion2 = "L01FRK";
                if (HiloLucesMotoLujan.unidadMed.equalsIgnoreCase("K")) {
                    cadenaComparacion = "L01FLK";
                    cadenaComparacion2 = "L01FRK";
                } else {
                    cadenaComparacion = "L01FLL";
                    cadenaComparacion2 = "L01FRL";
                }
                if (cadena.regionMatches(0, cadenaComparacion, 0, 6) || cadena.regionMatches(0, cadenaComparacion2, 0, 6)) {
                    System.out.println("Luz Exploradora");
                    medicionTomada = true;
                    valor = Integer.parseInt(cadena.substring(6, 11));
                }
                break;
            default:
                medicionTomada = false;
                System.out.println("Trama no reconocida");
                break;
        }//end of switch
        cadena = null;
        return valor;
    }

    /**
     * Leer byte a byte la trama desde el puerto serial
     *
     * @throws IOException
     */
    private byte[] leerTrama() throws IOException {
System.out.println(" entre en Leer Trama:" );
        boolean existeTrama = false;
        byte[] buffer = new byte[1024];
        System.out.println(" creo nuevo arreglo de Bytes:" );
        int data;
        int len = 0;
        in = serialPort.getInputStream();
        System.out.println(" take inputStream del puerto serial: "+in.toString() );
        
        while ((data = in.read()) > -1) {
            System.out.println("recogi Byte:" + data);
            buffer[len++] = (byte) data;
        }//end while
         System.out.println(" termine de read while ");
        
        System.out.println(" leng del buffer: "+len );
        if (len > 0) {//si hay datos
            HiloLucesMotoLujan.lngTrama = len;
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

     private void cargarPlaca() throws FileNotFoundException, IOException {        
        //try retrieve data from file
         String ruta = "./CARPETA CG/abc777.txt";
         File archivo = new File(ruta);
        BufferedWriter bw=null;
        
            bw = new BufferedWriter(new FileWriter(archivo));
            bw.write("[CARTEGRISE]");
            bw.newLine();
            bw.write("0200="+this.placas);
            bw.flush();
        
        bw.close();    

    }//end of method cargarUrl
    private void cargarParametros() throws FileNotFoundException, IOException {        
        //try retrieve data from file
        BufferedReader config = new BufferedReader(new FileReader(new File("./CARPETA RES/"+this.placas+".P")));
            String line;           
            while (true) {                
                line = config.readLine();
                if(line.startsWith("0493")){
                  Far1anguloBajaDerecha = Double.parseDouble(line.substring(line.indexOf("=") + 1, line.length()));                  
                }
                if(line.startsWith("7709")){
                  Far1luzBajaDerecha = Double.parseDouble(line.substring(line.indexOf("=") + 1, line.length()));                    
                }
                if(line.startsWith("0491")){
                  Far2anguloBajaDerecha = Double.parseDouble(line.substring(line.indexOf("=") + 1, line.length()));                    
                }
                if(line.startsWith("7704")){
                  Far2luzBajaDerecha = Double.parseDouble(line.substring(line.indexOf("=") + 1, line.length()));                    
                }
                if(line.startsWith("[CRC]")){
                  break;
                }                
            }       

    }//end of method cargarUrl

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
        serialPort = connect(portName,2400, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        if (serialPort != null) {
            //textAreaRPM.append("Conexion con" + serialPort.getName());
            System.out.println("Puerto Serial conectado");
        }
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

    private void registrarMedidas(Long idPrueba, int idUsuario, int numLuces) throws ClassNotFoundException, SQLException {
        Connection conexion = null;
//        permisibleBaja = 2.5; Se modifica ya que esta fallando
        permisibleBaja = 2.5;
        //Cargar clase de controlador de base de datos

        //Crear el objeto de <span class="IL_AD" id="IL_AD12">conexion</span> a la base de datos
        conexion = Conex.getConnection();
        conexion.setAutoCommit(false);
        //Crear objeto preparedStatement para realizar la consulta con la base de datos
//            String statementBorrar = "DELETE  FROMmedidas WHERE TEST = ?";
//            PreparedStatement instruccionBorrar = conexion.prepareStatement(statementBorrar);
//            instruccionBorrar.setInt(1, idPrueba);
//            instruccionBorrar.executeUpdate();

        double luzMenorBaja = -1;
        double angIncBaja = -1;
        int medidaDefectoLuz = -1;
        double limInfer = 0;
        double limSup = 0;
        if (numLuces == 1) {
            luzMenorBaja = Far1luzBajaDerecha;
            angIncBaja = Far1anguloBajaDerecha;

        }
        if (numLuces == 2) {
            luzMenorBaja = (Far1luzBajaDerecha <= Far2luzBajaDerecha ? Far1luzBajaDerecha : Far2luzBajaDerecha);
            double nearErrorFar1;
            double nearErrorFar2;
            boolean overErrorFar1 = false;
            boolean overErrorFar2 = false;
            String menor;
            if ( Far1anguloBajaDerecha < permisibleAnguloBajo || Far1anguloBajaDerecha > permisibleAnguloAlto) {
                overErrorFar1 = true;
            }
            if ( Far2anguloBajaDerecha < permisibleAnguloBajo  || Far2anguloBajaDerecha > permisibleAnguloAlto) {
                overErrorFar2 = true;
            }
            if (overErrorFar1 == true || overErrorFar2 == true) {
                if (overErrorFar1 == true) {
                    angIncBaja = Far1anguloBajaDerecha;
                }
                if (overErrorFar2 == true) {
                    angIncBaja = Far2anguloBajaDerecha;
                }
            } else {
                limInfer = Math.abs(Far1anguloBajaDerecha - permisibleAnguloBajo);
                limSup = Math.abs(Far1anguloBajaDerecha - permisibleAnguloAlto);
                nearErrorFar1 = (limInfer <= limSup ? limInfer : limSup);
                limInfer =  Math.abs(Far2anguloBajaDerecha - permisibleAnguloBajo);
                limSup =  Math.abs(Far2anguloBajaDerecha - permisibleAnguloAlto);
                nearErrorFar2 = (limInfer <= limSup ? limInfer : limSup);              
                if (nearErrorFar1 <= nearErrorFar2) {
                    angIncBaja = Far1anguloBajaDerecha;
                } else {
                    angIncBaja = Far2anguloBajaDerecha;
                }               
            }
        }
        if (numLuces == 3) {
            double nearErrorFar1;
            double nearErrorFar2;
            double nearErrorFar3;
            String menor;
            boolean overErrorFar1 = false;
            boolean overErrorFar2 = false;
            boolean overErrorFar3 = false;
            luzMenorBaja = (Far1luzBajaDerecha <= Far2luzBajaDerecha ? Far1luzBajaDerecha : Far2luzBajaDerecha);
            if (Far3luzBajaDerecha <= luzMenorBaja) {
                luzMenorBaja = Far3luzBajaDerecha;
            }
            if (Far1anguloBajaDerecha< permisibleAnguloBajo  || Far1anguloBajaDerecha > permisibleAnguloAlto) {
                overErrorFar1 = true;
            }
            if ( Far2anguloBajaDerecha< permisibleAnguloBajo  || Far2anguloBajaDerecha > permisibleAnguloAlto) {
                overErrorFar2 = true;
            }
            if ( Far3anguloBajaDerecha < permisibleAnguloBajo|| Far3anguloBajaDerecha > permisibleAnguloAlto) {
                overErrorFar3 = true;
            }
            if (overErrorFar1 == true || overErrorFar2 == true || overErrorFar3 == true) {
                if (overErrorFar1 == true) {
                    angIncBaja = Far1anguloBajaDerecha;
                }
                if (overErrorFar2 == true) {
                    angIncBaja = Far2anguloBajaDerecha;
                }
                if (overErrorFar3 == true) {
                    angIncBaja = Far3anguloBajaDerecha;
                }
            } else {
                limInfer = Math.abs(Far1anguloBajaDerecha - permisibleAnguloBajo);
                limSup = Math.abs(Far1anguloBajaDerecha - permisibleAnguloAlto);
                nearErrorFar1 = (limInfer <= limSup ? limInfer : limSup);
                limInfer = Math.abs(Far2anguloBajaDerecha - permisibleAnguloBajo);
                limSup = Math.abs(Far2anguloBajaDerecha - permisibleAnguloAlto);
                nearErrorFar2 = (limInfer <= limSup ? limInfer : limSup);
                limInfer = Math.abs(Far3anguloBajaDerecha - permisibleAnguloBajo);
                limSup = Math.abs(Far3anguloBajaDerecha - permisibleAnguloAlto);
                nearErrorFar3 = (limInfer <= limSup ? limInfer : limSup);
                menor = (nearErrorFar1 <= nearErrorFar2 ? "baj1" : "baj2");
                if (menor.equalsIgnoreCase("baj1")) {
                    if (nearErrorFar3 <= nearErrorFar1) {
                        angIncBaja = Far3anguloBajaDerecha;
                    } else {
                        angIncBaja = Far1anguloBajaDerecha;
                    }
                }
                if (menor.equalsIgnoreCase("baj2")) {
                    if (nearErrorFar3 <= nearErrorFar2) {
                        angIncBaja = Far3anguloBajaDerecha;
                    } else {
                        angIncBaja = Far2anguloBajaDerecha;
                    }
                }
            }
        }
        if (numLuces == 4) {
            double nearErrorFar1;
            double nearErrorFar2;
            double nearErrorFar3;
            double nearErrorFar4;
            boolean overErrorFar1 = false;
            boolean overErrorFar2 = false;
            boolean overErrorFar3 = false;
            boolean overErrorFar4 = false;
            String menor;

            if ( Far1anguloBajaDerecha < permisibleAnguloBajo || Far1anguloBajaDerecha > permisibleAnguloAlto) {
                overErrorFar1 = true;
            }
            if (Far2anguloBajaDerecha < permisibleAnguloBajo || Far2anguloBajaDerecha > permisibleAnguloAlto) {
                overErrorFar2 = true;
            }
            if ( Far3anguloBajaDerecha< permisibleAnguloBajo || Far3anguloBajaDerecha > permisibleAnguloAlto) {
                overErrorFar3 = true;
            }
            if ( Far4anguloBajaDerecha< permisibleAnguloBajo || Far4anguloBajaDerecha > permisibleAnguloAlto) {
                overErrorFar4 = true;
            }

            if (overErrorFar1 == true || overErrorFar2 == true || overErrorFar3 == true || overErrorFar4 == true) {
                if (overErrorFar1 == true) {
                    angIncBaja = Far1anguloBajaDerecha;
                }
                if (overErrorFar2 == true) {
                    angIncBaja = Far2anguloBajaDerecha;
                }
                if (overErrorFar3 == true) {
                    angIncBaja = Far3anguloBajaDerecha;
                }
                if (overErrorFar4 == true) {
                    angIncBaja = Far4anguloBajaDerecha;
                }
            } else {
                limInfer = Math.abs(Far1anguloBajaDerecha - permisibleAnguloBajo);
                limSup = Math.abs(Far1anguloBajaDerecha - permisibleAnguloAlto);
                nearErrorFar1 = (limInfer <= limSup ? limInfer : limSup);
                limInfer = Math.abs(Far2anguloBajaDerecha - permisibleAnguloBajo);
                limSup = Math.abs(Far2anguloBajaDerecha - permisibleAnguloAlto);
                nearErrorFar2 = (limInfer <= limSup ? limInfer : limSup);
                limInfer = Math.abs(Far3anguloBajaDerecha - permisibleAnguloBajo);
                limSup = Math.abs(Far3anguloBajaDerecha - permisibleAnguloAlto);
                nearErrorFar3 = (limInfer <= limSup ? limInfer : limSup);
                limInfer = Math.abs(Far4anguloBajaDerecha - permisibleAnguloBajo);
                limSup = Math.abs(Far4anguloBajaDerecha - permisibleAnguloAlto);
                nearErrorFar4 = (limInfer <= limSup ? limInfer : limSup);
                menor = (nearErrorFar1 <= nearErrorFar2 ? "baj1" : "baj2");
                if (menor.equalsIgnoreCase("baj1")) {
                    if (nearErrorFar3 <= nearErrorFar1) {
                        menor = (nearErrorFar3 <= nearErrorFar4 ? "baj3" : "baj4");
                        if (menor.equalsIgnoreCase("baj3") && (permisibleAnguloBajo < Far3anguloBajaDerecha || Far3anguloBajaDerecha > permisibleAnguloAlto)) {
                            angIncBaja = Far3anguloBajaDerecha;
                        } else {
                            angIncBaja = Far4anguloBajaDerecha;
                        }
                    } else {
                        angIncBaja = Far1anguloBajaDerecha;
                    }
                }
                if (menor.equalsIgnoreCase("baj2")) {
                    if (nearErrorFar3 <= nearErrorFar2) {
                        menor = (nearErrorFar3 <= nearErrorFar4 ? "baj3" : "baj4");
                        if (menor.equalsIgnoreCase("baj3")) {
                            angIncBaja = Far3anguloBajaDerecha;
                        } else {
                            angIncBaja = Far4anguloBajaDerecha;
                        }
                    } else {
                        angIncBaja = Far2anguloBajaDerecha;
                    }
                }
            }
            luzMenorBaja = (Far1luzBajaDerecha <= Far2luzBajaDerecha ? Far1luzBajaDerecha : Far2luzBajaDerecha);
            if (Far3luzBajaDerecha <= luzMenorBaja) {
                luzMenorBaja = Far3luzBajaDerecha;
            }
            if (Far4luzBajaDerecha <= luzMenorBaja) {
                luzMenorBaja = Far4luzBajaDerecha;
            }
        }
        String str2 = "INSERT INTO defxprueba(id_defecto,id_prueba,Tipo_defecto) VALUES (?,?,'A')";
        PreparedStatement psDefectos = conexion.prepareStatement(str2);
        String str3 = "UPDATE medidas SET Condicion='*' WHERE medidas.MEASURE = ?";//Esto no hace nada
        boolean aprobada = true;
        if (luzMenorBaja < permisibleBaja /*|| luzMenorAlta < permisibleBaja*/) {
            if (aplicTrans == 0) {
                psDefectos.clearParameters();
                psDefectos.setInt(1, 24005); //La intensidad de la luz menor a 2.5 klux a 1 m o 4 lux a 25 m. Se debe acelerar la moto hasta lograr la mayor intensidad de luz
                psDefectos.setLong(2, idPrueba);
                psDefectos.executeUpdate();
            }
            aprobada = false;
        }
        if (angIncBaja < permisibleAnguloBajo || angIncBaja > permisibleAnguloAlto) {
            if (aplicTrans == 0) {
                psDefectos.clearParameters();
//              psDefectos.setInt(1, 20002);//codigo defecto es distinto
                psDefectos.setInt(1, 24006);//La desviacion de cualquier haz de luz en posicion de bajas esta por fuera del rango entre 0.5 y 3.5% siendo 0 el horizonte y 3.5% la desviacion hacia el piso
                psDefectos.setLong(2, idPrueba);
                psDefectos.executeUpdate();
            }
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
        HiloLucesMotoLujan.serialEquipo = serialEquipo;
        boolean escrTrans = false;
        double sumaLuces = 0;
        System.out.println(" estoy new modulo glr ");
        if (aplicTrans == 1 && aprobada == true) {
            escrTrans = true;
        }
        if (aplicTrans == 0) {
            escrTrans = true;
        }

        String statement = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
        PreparedStatement instruccion = conexion.prepareStatement(statement);
        if (numLuces == 1) {
            instruccion.setInt(1, 2013);
            instruccion.setDouble(2, angIncBaja);
            instruccion.setLong(3, idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();
            instruccion.setInt(1, 2014);
            instruccion.setDouble(2, luzMenorBaja);
            instruccion.setLong(3, idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();
        }
        if (numLuces == 2) {
            instruccion.setInt(1, 2013);
            instruccion.setDouble(2, Far1anguloBajaDerecha);
            instruccion.setLong(3, idPrueba);
            instruccion.executeUpdate();
            instruccion.setInt(1, 2002);
            instruccion.setDouble(2,Far2anguloBajaDerecha);
            instruccion.setLong(3, idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();
            instruccion.setInt(1, 2014);
            instruccion.setDouble(2, Far1luzBajaDerecha);
            instruccion.setLong(3, idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();
             instruccion.setInt(1, 2015);
            instruccion.setDouble(2,Far2luzBajaDerecha);
            instruccion.setLong(3, idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();
        }
        
        if (numLuces> 2) {
           instruccion.setInt(1, 2013);
            instruccion.setDouble(2, angIncBaja);
            instruccion.setLong(3, idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();
            instruccion.setInt(1, 2014);
            instruccion.setDouble(2, luzMenorBaja);
            instruccion.setLong(3, idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();
             instruccion.setInt(1, 2015);
            instruccion.setDouble(2, Far2luzBajaDerecha);
            instruccion.setLong(3, idPrueba);
             instruccion.clearParameters();
              instruccion.setInt(1, 2000);
            instruccion.setDouble(2, Far3luzBajaDerecha);
            instruccion.setLong(3, idPrueba);
             instruccion.clearParameters();
              if (numLuces> 3) {
                  instruccion.setInt(1, 2001);
                  instruccion.setDouble(2, Far4luzBajaDerecha);
                  instruccion.setLong(3, idPrueba);
                  instruccion.clearParameters();
              }            
        }
        sumaLuces = Far1luzBajaDerecha + Far2luzBajaDerecha + Far3luzBajaDerecha + Far4luzBajaDerecha;
        instruccion.setInt(1, 2011);
        instruccion.setDouble(2, Far1luzBajaDerecha + Far2luzBajaDerecha + Far3luzBajaDerecha + Far4luzBajaDerecha);
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();
        if (escrTrans == true) {
            if (aprobada) {
                statement2 = "UPDATE pruebas SET Finalizada = 'Y',Aprobada ='Y',Abortada='N',usuario_for = ?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?";
            } else {
                statement2 = "UPDATE pruebas SET Finalizada = 'Y',Aprobada ='N',Abortada='N',usuario_for = ?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?";
            }
            PreparedStatement instruccion2 = conexion.prepareStatement(statement2);
            instruccion2.setInt(1, idUsuario);
            instruccion2.setString(2, serialEquipo);
            instruccion2.setLong(3, idPrueba);
            int executeUpdate = instruccion2.executeUpdate();
        } else {
            statement2 = "UPDATE pruebas SET Autorizada = 'A',usuario_for = ?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?";
            PreparedStatement instruccion2 = conexion.prepareStatement(statement2);
            instruccion2.setLong(1, idUsuario);
            instruccion2.setString(2, serialEquipo);
            instruccion2.setLong(3, idPrueba);
            int executeUpdate = instruccion2.executeUpdate();
        }
        conexion.commit();
        conexion.setAutoCommit(true);
        conexion.close();
        if (org.soltelec.util.Mensajes.mensajePregunta("¿Desea Agregar un Comentario a la Prueba ?")) {
            FrmComentario frm = new FrmComentario(SwingUtilities.getWindowAncestor(panelLuxometro), idPrueba, JDialog.DEFAULT_MODALITY_TYPE);
            frm.setVisible(true);
            frm.setModal(true);
        }
        if (escrTrans == true) {
            HiloLucesMotoLujan.escrTrans = "E";
        } else {
            HiloLucesMotoLujan.escrTrans = "A";
        }
        Mensajes.messageDoneTime("Se ha REGISTRADO la Prueba de Luces para la Moto de una Manera Exitosa ..¡", 3);

    }//end of method registrar medidas

    public boolean verificarMedidas(double... medidas) {
        for (double d : medidas) {
            if (d <= 0) {
                return false;
            }
        }
        return true;
    }

    private void registrarCancelacion(String comentario, Long idPrueba, int idUsuario) {
        try {
            //Cargar clase de controlador de base de datos

            String serialEquipo = "";
            try {
                serialEquipo = ConsultarDatosVehiculo.buscarSerialEquipo(idPrueba);
            } catch (Exception e) {
                serialEquipo = "Serial no encontrado";
                e.printStackTrace();
            }

            //Crear el objeto de <span class="IL_AD" id="IL_AD12">conexion</span> a la base de datos
            Connection conexion = Conex.getConnection();

            //Crear objeto preparedStatement para realizar la consulta con la base de datos
            String statement = "UPDATE pruebas SET Finalizada = 'Y',Aprobada='N',Abortada='Y',Comentario_aborto=?,usuario_for = ?,Fecha_aborto=?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?";
            PreparedStatement instruccion = conexion.prepareStatement(statement);
            instruccion.setString(1, comentario);
            instruccion.setInt(2, idUsuario);
            instruccion.setString(4, serialEquipo);
            instruccion.setLong(5, idPrueba);
            Date d = new Date();
            Timestamp timestamp = new Timestamp(d.getTime());
            instruccion.setTimestamp(3, timestamp);
            Mensajes.messageDoneTime("Se ha cancelado la Prueba de Luces para la Moto de una manera Exitosa ..¡", 3);
            //Un objeto ResultSet, almacena los datos de resultados de una <span class="IL_AD" id="IL_AD11">consulta</span>
            int n = instruccion.executeUpdate();
            conexion.close();
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

    public Long getIdPrueba() {
        return idPrueba;
    }

    public void setIdPrueba(Long idPrueba) {
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
        instruccion.setLong(1, idPrueba);
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
