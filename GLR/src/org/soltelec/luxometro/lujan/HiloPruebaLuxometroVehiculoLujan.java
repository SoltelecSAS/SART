/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.luxometro.lujan;

//import com.soltelec.integrador.cliente.ClienteSicov;
//import com.soltelec.estandar.EstadoEventosSicov;
import org.soltelec.luxometro.*;
import org.soltelec.pruebasgases.FrmComentario;

import static com.soltelec.modulopuc.persistencia.conexion.DBUtil.execIndTrama;
import com.soltelec.modulopuc.utilidades.Mensajes;
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.Frame;
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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import org.soltelec.util.Conex;
import org.soltelec.util.ConsultarDatosVehiculo;


/**
 *
 *
 * @author Gerencia Desarrollo de Soluciones Tecnologicas
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
public class HiloPruebaLuxometroVehiculoLujan implements Runnable, ActionListener {

    private PanelLuxometroLujan panelLuxometro;
    private Timer timer;
    private int contadorTemporizacion = 0;
    private SerialPort serialPort;
    private InputStream in;
    private OutputStream out;
    private double luzBajaDerecha;
    private double luzAltaDerecha;
    private double luzBajaIzquierda;
    private double luzAltaIzquierda;
    private double anguloBajaDerecha;
     private double anguloBajaIzquierda;
     
    private double farBajaDerecha;
    private double farAltaDerecha;
    private double farBajaIzquierda;
    private double farAltaIzquierda;
    private double angBajaDerecha;
     private double angBajaIzquierda;
     
     private String nivelParseo;   
    private StringBuilder sb;
    private int numeroExploradoras;
    private double[] medidasExploradoras;
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
    private String puertoLuxometro;
    public static int aplicTrans = 1;
    public static String escrTrans = "";
    public static String ipEquipo;
    public static String serialEquipo = "";
    private static String unidadMed = "";
    public static int lngTrama;
    private String lblExploradoras= " ";
    private String placas;
    

    public HiloPruebaLuxometroVehiculoLujan(PanelLuxometroLujan panel, int aplicTrans,String placas) {
       
        this.panelLuxometro = panel;
        this.placas= placas;
        panelLuxometro.getButtonCancelar().addActionListener(this);
        this.aplicTrans = aplicTrans;        
    }

    public boolean buscarLuxometro() {
        return true;
    }
    /*try {
     this.utileriaGerencia();
     } catch (ClassNotFoundException ex) {
     JOptionPane.showMessageDialog(panelLuxometro, "Error no se encuentra el driver de MySQL");
     ex.printStackTrace(System.err);
     } catch (SQLException ex) {
     JOptionPane.showMessageDialog(panelLuxometro, "Error de sentencia sql escribiendo medidas");
     ex.printStackTrace(System.err);
     }*/

    @Override
    public void run() {
        try {
            //Dialogo para configuración de exploradoras
            DecimalFormat df = (DecimalFormat) NumberFormat.getInstance(Locale.ENGLISH);
            df.applyPattern("#0.0");//averiguar sobre DecimalFormat
            try {                   
                cargarPlaca();
                Thread.sleep(200);
            } catch (FileNotFoundException ex) {
                JOptionPane.showMessageDialog(null, "Disculpe, no Existe la Mustra Correspondiente de la Placa");
                return;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Imposible cargar archivo propiedades");
                return;
            }
            PanelConfiguracionExploradoras p = new PanelConfiguracionExploradoras();
            JOptionPane.showMessageDialog(null, p, "ParamLuc.Adicional", JOptionPane.PLAIN_MESSAGE);
            Thread.sleep(1500);           

            panelLuxometro.getLabelMensaje().setText("Conectando con LUJAN..! X favor vaya Luxometro Recoger Muestra");

            try {//Intentar conectarse con el puerto serial
                cargarParametros();
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
                JOptionPane.showMessageDialog(panelLuxometro, "Error conectandose al Luxometro Lujan");
                panelLuxometro.cerrar();
                return;
            }
            
            if (p.getCheckExploradoras().isSelected()) {
                numeroExploradoras = ((Integer) p.getComboNumeroExploradoras().getSelectedItem());
                medidasExploradoras = new double[numeroExploradoras];
                panelLuxometro.habilitar(numeroExploradoras);
            }
            blinker = Thread.currentThread();
            
            /* Object numeroLuces = JOptionPane.showInputDialog(panelLuxometro, " Luces Bajas Der.", "Numero de Farolas", JOptionPane.DEFAULT_OPTION, new ImageIcon(),
                    new Object[]{new Integer(1), new Integer(2), new Integer(3), new Integer(4)}, (Object) (new Integer(9)));
            int numLuces = ((Integer) numeroLuces).intValue();            
            luzBajaDerecha= new double[numLuces];
            anguloBajaDerecha= new double[numLuces];*/
            int numLuces =1;
            for (int i = 0; i < numLuces; ++i) {
              /*  daiBajaDerecha = new DialogoAnguloInclinacion(SwingUtilities.getWindowAncestor(panelLuxometro)," Angulo Inclinacion Luz Baja Derecha "+(i+1), JDialog.DEFAULT_MODALITY_TYPE);
                daiBajaDerecha.getPanel().getLabelTitulo().setText("Angulo Inclinacion Baja Derecha Farola: "+(i+1));
                daiBajaDerecha.setVisible(true);            
                anguloBajaDerecha = Double.parseDouble(df.format(daiBajaDerecha.getPanel().getValorSlider()));*/
               // System.out.println("take angulo BD "+(i+1)+" "+anguloBajaDerecha[i]);                
                Thread.sleep(1500);
                panelLuxometro.getLabelMensaje().setText("BAJA DERECHA Farola: "+(i+1)+" PRES ENTER ..! ");
                panelLuxometro.getLabelLuzBajaDerecha().setForeground(Color.BLUE);
                contadorTemporizacion = 0;
                crearNuevoTimer();
                timer.start();
            //watchdog para recibir la trama de datos unos 60 segundos
                //como indicar que presione enter nuevamente???? si esta equivocada ??
                panelLuxometro.getBajaDerecha().setBlinking(true);
                sb = new StringBuilder("!");
                int seqBajaAlta = 0;
                
                
                if (contadorTemporizacion > 59) {
                    JOptionPane.showMessageDialog(panelLuxometro, "Fin prueba Un minuto sin medicion");
                    panelLuxometro.cerrar();
                    serialPort.close();
                    return;
                }
                 panelLuxometro.getLabelMensaje().setText("MEDICION TOMADA Farola "+(i+1)+"..!");
                Thread.sleep(1000);
            }
           
            panelLuxometro.getBajaDerecha().setBlinking(false);
            panelLuxometro.getBajaDerecha().setOnOff(true);

            timer.stop();
            contadorTemporizacion = 0;
            
           /* numeroLuces = JOptionPane.showInputDialog(panelLuxometro, "Numero de Luces Alta Derecha", "Numero de Farolas", JOptionPane.DEFAULT_OPTION, new ImageIcon(),
                    new Object[]{new Integer(1), new Integer(2), new Integer(3), new Integer(4)}, (Object) (new Integer(9)));
            numLuces = ((Integer) numeroLuces).intValue(); 
            luzAltaDerecha= new double[numLuces];   */
            
            for (int i = 0; i < numLuces; ++i) {
                 panelLuxometro.getLabelMensaje().setText("ALTA DER. Farola: "+(i+1)+" PRES ENTER ..! ");                
                panelLuxometro.getLabelLuzAltaDerecha().setForeground(Color.BLUE);
                panelLuxometro.getAltaDerecha().setBlinking(true);
                crearNuevoTimer();
                contadorTemporizacion = 0;
                timer.start();
            //watchdog para que despues de un minuto se termine la prueba
                //TODO registrar terminación por falta de tiempo
                sb = new StringBuilder("!");
               
                if (contadorTemporizacion > 59) {
                    JOptionPane.showMessageDialog(panelLuxometro, "Fin prueba \n Un minuto sin medicion");
                    panelLuxometro.cerrar();
                    serialPort.close();
                    return;
                }
                panelLuxometro.getLabelMensaje().setText("MEDICION TOMADA Farola "+(i+1)+"..!");  
                Thread.sleep(2000);                
            }
            panelLuxometro.getAltaDerecha().setBlinking(false);
            panelLuxometro.getAltaDerecha().setOnOff(true);
            timer.stop();
                     

            //Exploradoras validacion número de enter tres veces;
            //hilo de exploradoras ????? con el mismo panel OK!!!
            //nuevo hilo con nuevo panel
            if (p.getCheckExploradoras().isSelected()) {
                try {
                    if (!medirExploradoras()) {
                        return;
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(panelLuxometro, "Error de I/O");
                    panelLuxometro.cerrar();
                }
            }
           /* numeroLuces = JOptionPane.showInputDialog(panelLuxometro, "Luces Bajas Izq.", "Numero de Farolas", JOptionPane.DEFAULT_OPTION, new ImageIcon(),
                    new Object[]{new Integer(1), new Integer(2), new Integer(3), new Integer(4)}, (Object) (new Integer(9)));
            numLuces = ((Integer) numeroLuces).intValue();
            luzBajaIzquierda= new double[numLuces];
             anguloBajaIzquierda= new double[numLuces];*/
            ///Medir la inclinacion de la luz baja izquierda
            for (int i = 0; i < numLuces; ++i) {
               /* daiBajaIzquierda = new DialogoAnguloInclinacion(SwingUtilities.getWindowAncestor(panelLuxometro), "Angulo Inclinacion Luz Izquierda Baja " + (i+1), ModalityType.DOCUMENT_MODAL);
                daiBajaIzquierda.getPanel().getLabelTitulo().setText("Angulo Inclinacion Baja Izquierda Farola: "+(i+1));              
                daiBajaIzquierda.setVisible(true);
                anguloBajaIzquierda =Double.parseDouble(df.format(daiBajaIzquierda.getPanel().getValorSlider()));*/
               // System.out.println("take angulo BI "+(i+1)+" "+anguloBajaIzquierda[i]);                

                //Mide la luz baja izquierda
                crearNuevoTimer();
                contadorTemporizacion = 0;
                timer.start();
                panelLuxometro.getLabelMensaje().setText("LUZ BAJA IZQ. PRES. ENTER  FAROLA " +(i+1) + " .!!");
                panelLuxometro.getLabelLuzBajaIzquierda().setForeground(Color.BLUE);
                panelLuxometro.getBajaIzquierda().setBlinking(true);
            //watchdog para que despues de un minuto se termine la prueba
                //TODO registrar terminación por falta de tiempo
                sb = new StringBuilder("!");
               
            
            if (contadorTemporizacion > 59) {
                JOptionPane.showMessageDialog(panelLuxometro, "Fin prueba \n Un minuto sin medicion");
                panelLuxometro.cerrar();
                serialPort.close();
                return;
            }
            //Tomar la medición de la luz alta izquierda            
             panelLuxometro.getLabelMensaje().setText("MEDICION TOMADA Farola "+(i+1)+"..!");  
              Thread.sleep(2000);   
            }
            panelLuxometro.getBajaIzquierda().setBlinking(false);
            panelLuxometro.getBajaIzquierda().setOnOff(true);
           /*  numeroLuces = JOptionPane.showInputDialog(panelLuxometro, "Luces Alta Izq.", "Numero de Farolas", JOptionPane.DEFAULT_OPTION, new ImageIcon(),
                    new Object[]{new Integer(1), new Integer(2), new Integer(3), new Integer(4)}, (Object) (new Integer(9)));
            numLuces = ((Integer) numeroLuces).intValue();
            luzAltaIzquierda= new double[numLuces];*/
            for (int i = 0; i < numLuces; ++i) {
                crearNuevoTimer();
                contadorTemporizacion = 0;
                timer.start();
                 panelLuxometro.getLabelMensaje().setText("ALTA IZQUIERDA  Farola: "+(i+1)+" PRES ENTER ..! ");                 
                panelLuxometro.getLabelLuzAltaIzquierda().setForeground(Color.BLUE);
                panelLuxometro.getAltaIzquierda().setBlinking(true);

                sb = new StringBuilder("!");
                             
                if (contadorTemporizacion > 59) {
                    JOptionPane.showMessageDialog(panelLuxometro, "Fin prueba \n Un minuto sin medicion");
                    panelLuxometro.cerrar();
                    serialPort.close();
                    return;
                }
                // anguloBajaDerecha = daiBajaDerecha.getPanel().getCheckBoxFueraRango().isSelected() ? 4.0 : anguloBajaDerecha;
                 panelLuxometro.getLabelMensaje().setText("MEDICION TOMADA Farola "+(i+1)+"..!");  
                  Thread.sleep(2000);  
            }
            panelLuxometro.getAltaIzquierda().setBlinking(false);
            panelLuxometro.getAltaIzquierda().setOnOff(true);
            int contador = 0;
           /* sumaExploradoras = 0;
            if (medidasExploradoras != null) {
                for (double medidaExploradora : medidasExploradoras) {
                    System.err.println("Exploradora  " + ++contador + " : " + medidaExploradora);
                    sumaExploradoras += medidaExploradora;
                }//end for
            }//end if
            else {
                sumaExploradoras = 0;
            }*/
            System.out.println("Suma de las Luces Adicionales: " + sumaExploradoras);
            // anguloBajaIzquierda = daiBajaIzquierda.getPanel().getCheckBoxFueraRango().isSelected() ? 4.0 : anguloBajaIzquierda;
           
            Thread.sleep(2000);
            //serialPort.close();
            try {
                cargarPermisibles();
                registrarMedidas(idPrueba, idUsuario, aplicTrans, ipEquipo);

            } catch (ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(panelLuxometro, "Error no se encuentra el driver de MySQL");
                ex.printStackTrace(System.err);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(panelLuxometro, "Error de sentencia sql escribiendo medidas");
                ex.printStackTrace(System.err);
            }
            panelLuxometro.cerrar();
        } catch (InterruptedException ie) {
            panelLuxometro.cerrar();
            serialPort.close();
            if (cancelacion) {
                String showInputDialog = JOptionPane.showInputDialog("Comentaro de Aborto");
                registrarCancelacion(showInputDialog, idPrueba, idUsuario);

            }
        }
    }//end of method run
    
    private void cargarPlaca() throws FileNotFoundException, IOException {        
            //try retrieve data from file
            String ruta = "./CARPETA CG/abc777.txt";
            File archivo = new File(ruta);
            BufferedWriter bw = null;
            bw = new BufferedWriter(new FileWriter(archivo));
            bw.write("[CARTEGRISE]");
            bw.newLine();
            bw.write("0200=" + this.placas);
            bw.flush();
            bw.close(); 
        }//end of method cargarUrl

    private void cargarParametros() throws FileNotFoundException, IOException {

            //try retrieve data from file
    String ruta = "./CARPETA RES/"+this.placas+".P";
            BufferedReader config = new BufferedReader(new FileReader(new File(ruta)));
                String line;           
                while (true) {                
                    line = config.readLine();
                    //luzAltaDerecha
                   // luzAltaIzquierda

                     if(line.startsWith("7729")){
                      luzAltaDerecha = Double.parseDouble(line.substring(line.indexOf("=") + 1, line.length()));                  
                    }
                    if(line.startsWith("7721")){
                      luzAltaIzquierda = Double.parseDouble(line.substring(line.indexOf("=") + 1, line.length()));                  
                    }
                    if(line.startsWith("0493")){
                      anguloBajaDerecha = Double.parseDouble(line.substring(line.indexOf("=") + 1, line.length()));                  
                    }
                    if(line.startsWith("7709")){
                      luzBajaDerecha = Double.parseDouble(line.substring(line.indexOf("=") + 1, line.length()));                    
                    }
                    if(line.startsWith("0491")){
                      anguloBajaIzquierda = Double.parseDouble(line.substring(line.indexOf("=") + 1, line.length()));                    
                    }
                    if(line.startsWith("7704")){
                      luzBajaIzquierda = Double.parseDouble(line.substring(line.indexOf("=") + 1, line.length()));                    
                    }

                    if(line.startsWith("0493")){
                      anguloBajaDerecha = Double.parseDouble(line.substring(line.indexOf("=") + 1, line.length()));                    
                    }

                    if(line.startsWith("0491")){
                      anguloBajaIzquierda = Double.parseDouble(line.substring(line.indexOf("=") + 1, line.length()));                    
                    }

                    if(line.startsWith("7744")){
                      sumaExploradoras = sumaExploradoras +Double.parseDouble(line.substring(line.indexOf("=") + 1, line.length()));    
                      this.lblExploradoras=this.lblExploradoras.concat(" ").concat("Expl ".concat(String.valueOf(1)).concat(": ").concat(String.valueOf(Double.parseDouble(line.substring(line.indexOf("=") + 1, line.length())))));
                    }

                    if(line.startsWith("7749")){
                      sumaExploradoras = sumaExploradoras +Double.parseDouble(line.substring(line.indexOf("=") + 1, line.length()));  
                      this.lblExploradoras=this.lblExploradoras.concat(" ").concat("Expl ".concat(String.valueOf(2)).concat(": ").concat(String.valueOf(Double.parseDouble(line.substring(line.indexOf("=") + 1, line.length())))));
                    }       

                    if(line.startsWith("[CRC]")){
                      break;
                    }

                }
         }//end of method cargarUrl


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

    /**
     * Obtener la trama y analizarla verificar que la trama corresponda con la
     * secuencia
     *
     * @return bandera indicando que coincide
     */
    private double tomarMedicion(Luz luz) throws IOException {
        byte[] b;
        b = leerTrama();
        if (b == null) {
            return -100;//retorno ok
        }
        String cadena = new String(b);//rogamos a Dios que sea la correcta
        StringBuilder sb = new StringBuilder(cadena);
        sb.deleteCharAt(0);
        cadena = sb.toString();
        System.out.println("CADENA IS "+cadena );
        double valor = -1;    
        boolean medicionTomada = false;
        switch (luz) {
            case ALTADERECHA://verificar que la cadena corresponda
                 String cadenaComparacion;
                if (this.nivelParseo.equalsIgnoreCase("SERIAL")) {
                    System.out.println(" MARQUE ALTADERECHA");
                    HiloPruebaLuxometroVehiculoLujan.unidadMed = cadena.substring(5, 6);
                    //cadena hay que quitarle los espacios???  
                    System.out.println("lng trama is " + HiloPruebaLuxometroVehiculoLujan.lngTrama);
                    if (HiloPruebaLuxometroVehiculoLujan.unidadMed.equalsIgnoreCase("K")) {
                        cadenaComparacion = "L01MRK";                       
                    } else {
                        cadenaComparacion = "L01MRL";
                    }
                    if (cadena.regionMatches(0, cadenaComparacion, 0, 6)) {
                        System.out.println("Luz Alta Derecha");
                        medicionTomada = true;                       
                        System.out.println(" WHEN 6,9  " + cadena.substring(6, 9));
                        System.out.println(" WHEN 6, 10  " + cadena.substring(6, 10)); 
                         System.out.println(" WHEN 6, 11  " + cadena.substring(6, 10)); 
                        //  valor = Double.parseDouble(cadena.substring(6, 11));
                        valor = Double.parseDouble(cadena.substring(6, 9));
                    }
                }else{                  
                    HiloPruebaLuxometroVehiculoLujan.unidadMed = cadena.substring(5, 6);                   
                    cadenaComparacion = "L01M1K";                    
                    if (cadena.regionMatches(0, cadenaComparacion, 0, 6)) {
                           System.out.println(" TAKE MEDICION ALTADERECHA");                      
                        medicionTomada = true;
                        valor = Double.parseDouble(cadena.substring(7, 11));
                    }
                }
                break;
            case ALTAIZQUIERDA:
                //cadenaComparacion = "L01MLK";
                System.out.println(" MARQUE ALTAIZQUIERDA");
                System.out.println("lng trama is " + HiloPruebaLuxometroVehiculoLujan.lngTrama);
                if (this.nivelParseo.equalsIgnoreCase("SERIAL")) {
                    if (HiloPruebaLuxometroVehiculoLujan.unidadMed.equalsIgnoreCase("K")) {
                        cadenaComparacion = "L01MLK";                        
                    } else {
                        cadenaComparacion = "L01MLL";
                    }
                    if (cadena.regionMatches(0, cadenaComparacion, 0, 6)) {
                        System.out.println("Luz Alta Izquierda");                        
                        medicionTomada = true;                        
                        System.out.println(" WHEN 6,9  " + cadena.substring(6, 9));
                        System.out.println(" WHEN 6, 10  " + cadena.substring(6, 10));
                         System.out.println(" WHEN 6, 11  " + cadena.substring(6, 11));
                        // valor = Double.parseDouble(cadena.substring(6, 11));
                          valor = Double.parseDouble(cadena.substring(6, 9));
                    }
                } else {
                   
                    HiloPruebaLuxometroVehiculoLujan.unidadMed = cadena.substring(5, 6);
                    cadenaComparacion = "L01M1K";
                    if (cadena.regionMatches(0, cadenaComparacion, 0, 6)) {
                        System.out.println(" TAKE MEDICION ALTA IZQUIERDA ");                        
                        medicionTomada = true;
                        valor = Double.parseDouble(cadena.substring(7, 11));
                    }
                }
                break;
            case BAJADERECHA:
                 System.out.println(" MARQUE BAJA DERECHA " );
                 System.out.println("CADENA RECOGIDA " + cadena); 
                 System.out.println("lng trama is "+HiloPruebaLuxometroVehiculoLujan.lngTrama );
                 System.out.println("unidad default " + HiloPruebaLuxometroVehiculoLujan.unidadMed);
                 System.out.println("unidad recojida" + cadena.substring(5, 6));                
                 
                if (this.nivelParseo.equalsIgnoreCase("SERIAL")) {
                     System.out.println("nivel Parseo" + cadena.substring(5, 6)); 
                    HiloPruebaLuxometroVehiculoLujan.unidadMed = cadena.substring(5, 6);
                    if (HiloPruebaLuxometroVehiculoLujan.unidadMed.equalsIgnoreCase("K")) {
                        cadenaComparacion = "L01DRK";                                             
                    } else {
                        cadenaComparacion = "L01DRL";
                    }
                    if (cadena.regionMatches(0, cadenaComparacion, 0, 6)) {
                        System.out.println("Luz Baja Derecha");
                        medicionTomada = true;
                        System.out.println(" WHEN 5,9  " + cadena.substring(5, 9));
                        System.out.println(" WHEN 6,9  " + cadena.substring(6, 9));
                        System.out.println(" WHEN 6, 10  " + cadena.substring(6, 10)); 
                        System.out.println(" WHEN 6, 11  " + cadena.substring(6, 11)); 
                       // valor = Double.parseDouble(cadena.substring(6, 11));
                         valor = Double.parseDouble(cadena.substring(6, 9));
                    }
                } else {
                    System.out.println("lng trama is " + HiloPruebaLuxometroVehiculoLujan.lngTrama);
                    System.out.println("unidad recojida WHEN 6,10" + cadena.substring(7, 10)); 
                    System.out.println("unidad recojida WHEN 7,11" + cadena.substring(7, 11));                   
                    System.out.println("La cadena TOMADA IS: " + cadena);
                    HiloPruebaLuxometroVehiculoLujan.unidadMed = cadena.substring(5, 6);
                    cadenaComparacion = "L01D1K";
                    if (cadena.regionMatches(0, cadenaComparacion, 0, 6)) {
                        System.out.println(" MARQUE BAJA DERECHA ");
                        System.out.println("Luz Baja Derecha");
                        medicionTomada = true;
                        valor = Double.parseDouble(cadena.substring(7, 11));
                    }
                }
                //cadenaComparacion = "L01DRK";
                break;
            case BAJAIZQUIERDA:
                //cadenaComparacion = "L01DLK";
                System.out.println(" MARQUE BAJAIZQUIERDA" );
                if (this.nivelParseo.equalsIgnoreCase("SERIAL")) {
                    System.out.println("lng trama is " + HiloPruebaLuxometroVehiculoLujan.lngTrama);
                    if (HiloPruebaLuxometroVehiculoLujan.unidadMed.equalsIgnoreCase("K")) {
                        cadenaComparacion = "L01DLK";
                    } else {
                        cadenaComparacion = "L01DLL";
                    }
                    if (cadena.regionMatches(0, cadenaComparacion, 0, 6)) {
                        System.out.println("Luz Baja Izquierda");
                        medicionTomada = true;
                       
                        System.out.println(" WHEN 6,9  " + cadena.substring(6, 9));
                        System.out.println(" WHEN 6, 10  " + cadena.substring(6, 10)); 
                         System.out.println(" WHEN 6, 11  " + cadena.substring(6, 11)); 
                        // valor = Double.parseDouble(cadena.substring(6, 11));
                          valor = Double.parseDouble(cadena.substring(6, 9));
                    }
                }else {
                    System.out.println("lng trama is " + HiloPruebaLuxometroVehiculoLujan.lngTrama);
                    System.out.println("unidad recojida WHEN 9,10" + cadena.substring(9, 10));
                    System.out.println("unidad recojida WHEN 5,6" + cadena.substring(8, 12));
                    System.out.println("unidad recojida WHEN 8,11" + cadena.substring(8, 11));
                    System.out.println("La cadena TOMADA IS: " + cadena);
                    HiloPruebaLuxometroVehiculoLujan.unidadMed = cadena.substring(5, 6);
                    cadenaComparacion = "L01D1K";
                    if (cadena.regionMatches(0, cadenaComparacion, 0, 6)) {
                        System.out.println(" MARQUE BAJA DERECHA ");
                        System.out.println("Luz Baja Derecha");
                        medicionTomada = true;
                        valor = Double.parseDouble(cadena.substring(7, 11));
                    }
                }
                break;
            case EXPLORADORA:
                /* cadenaComparacion = "L01FLK";
                 */ String cadenaComparacion2 = "L01FRK";
                if (this.nivelParseo.equalsIgnoreCase("SERIAL")) {
                    if (HiloPruebaLuxometroVehiculoLujan.unidadMed.equalsIgnoreCase("K")) {
                        cadenaComparacion = "L01F1K";
                        cadenaComparacion2 = "L01FRK";
                    } else {
                        cadenaComparacion = "L01FLL";
                        cadenaComparacion2 = "L01FRL";
                    }
                    if (cadena.regionMatches(0, cadenaComparacion, 0, 6) || cadena.regionMatches(0, cadenaComparacion2, 0, 6)) {
                        System.out.println("Luz Exploradora");
                        medicionTomada = true;
                       System.out.println(" WHEN 5,9  " + cadena.substring(5, 9));
                        System.out.println(" WHEN 6,9  " + cadena.substring(6, 9));
                        System.out.println(" WHEN 6, 10  " + cadena.substring(6, 10));                       
                         valor = Double.parseDouble(cadena.substring(6, 9));
                    }
                }else {
                    System.out.println("lng trama is " + HiloPruebaLuxometroVehiculoLujan.lngTrama);                    
                    System.out.println("unidad recojida WHEN 7,11" + cadena.substring(7, 11));                   
                    System.out.println("La cadena TOMADA IS: " + cadena);
                    HiloPruebaLuxometroVehiculoLujan.unidadMed = cadena.substring(5, 6);                    
                    cadenaComparacion = "L01F1K";
                    if (cadena.regionMatches(0, cadenaComparacion, 0, 6)) {
                        System.out.println(" TAKE EXPLORADORA");                       
                        medicionTomada = true;
                        valor = Double.parseDouble(cadena.substring(7, 11));
                    }
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
     * metodo para capturar el numero de exploradoras
     */
    private boolean medirExploradoras() throws IOException, InterruptedException {
        //watchdog para controlar por tiempo la salida
        boolean terminadoConExito = false;
        contadorTemporizacion = 0;
        crearNuevoTimer();
        timer.start();
        int numeroExploradorasMedidas = 0;
        double medicionTemporal = -100;
        //mientras en numero de exploradoras medidas sea menora al numero de exploradoras
int j=0;
        while (numeroExploradorasMedidas < numeroExploradoras && contadorTemporizacion < 300 && blinker != null) {
            //medir la exploradora correspondiente
            panelLuxometro.getLabelMensaje().setText("Luz Adicional " + (numeroExploradorasMedidas + 1));
            panelLuxometro.getExploradoras()[numeroExploradoras - numeroExploradorasMedidas - 1].setBlinking(true);
                     
            Thread.sleep(250);
            panelLuxometro.getExploradoras()[numeroExploradoras - numeroExploradorasMedidas - 1].setBlinking(false);
            panelLuxometro.getExploradoras()[numeroExploradoras - numeroExploradorasMedidas - 1].setOnOff(true);
            medidasExploradoras[numeroExploradorasMedidas++] = medicionTemporal;
            j=j+1;
            //
        }//end while todas las exploradoras
        timer.stop();

        if (contadorTemporizacion >= 300) {
            JOptionPane.showMessageDialog(panelLuxometro, "Fin prueba\nCinco minutos sin terminar el proceso ....");
            panelLuxometro.cerrar();
            serialPort.close();
            return false;
        }
        return true;

    }//end method

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
        HiloPruebaLuxometroVehiculoLujan.lngTrama=0;
       System.out.println("Longitud del mensaje:" + len);
        if (len > 0) {//si hay datos
            System.out.println("Longitud del mensaje:" + len);
            HiloPruebaLuxometroVehiculoLujan.lngTrama=len;
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
////            }
            return arreglo;
        }//end if else
        else {
            return null;//
        }
    }

    private void registrarCancelacion(String comentario, Long idPrueba, long idUsuario) {
        try {

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
            String statement = "UPDATE pruebas SET Finalizada = 'Y',Aprobado='N',Abortado='Y',Comentario_aborto=?,usuario_for = ?,Fecha_aborto=?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?";//pendiente la fecha de aborto
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

            // INICIO EVENTOS SICOV
            // ClienteSicov.eventoPruebaSicov(idPrueba.intValue(), EstadoEventosSicov.CANCELADO, comentario, serialEquipo);
            // FIN EVENTOS SICOV
        } catch (ClassNotFoundException e) {
            System.out.println(e);
        } catch (SQLException e) {
            System.out.println(e);
        } catch (Exception e) {
            System.out.println(e);
        }

    }
   
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

    private void registrarMedidas(Long idPrueba, long idUsuario, int aplicTrans, String ipEquipo) throws ClassNotFoundException, SQLException {

        //Crear el objeto de <span class="IL_AD" id="IL_AD12">conexion</span> a la base de datos
        Connection conexion = Conex.getConnection();
        conexion.setAutoCommit(false);
        permisibleBaja = 2.5;
        permisibleAnguloAlto = 3.5;
        permisibleAnguloBajo = 0.5;
        permisibleSumatoria = 225;
        String lblObservacion=" ";

        farBajaDerecha = luzBajaDerecha;
        angBajaDerecha = anguloBajaDerecha;
       
        String statement = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
        PreparedStatement instruccion = conexion.prepareStatement(statement);
        instruccion.setInt(1, 2024);
        instruccion.setDouble(2, luzBajaDerecha);
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();
        
        instruccion.setInt(1, 2040);
        instruccion.setDouble(2, anguloBajaDerecha);
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();        

        farBajaIzquierda = luzBajaIzquierda;
        angBajaIzquierda = anguloBajaIzquierda;
        statement = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
        instruccion = conexion.prepareStatement(statement);
        instruccion.setInt(1, 2031);
        instruccion.setDouble(2, luzBajaIzquierda);
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();
        
        instruccion.setInt(1, 2044);
        instruccion.setDouble(2, anguloBajaIzquierda);
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();

        farAltaDerecha = luzAltaDerecha;
        statement = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
        instruccion = conexion.prepareStatement(statement);
        instruccion.setInt(1, 2036);
        instruccion.setDouble(2, luzAltaDerecha);
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();

        farAltaIzquierda = luzAltaIzquierda;
        statement = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
        instruccion = conexion.prepareStatement(statement);
        instruccion.setInt(1, 2032);
        instruccion.setDouble(2, luzAltaIzquierda);
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();

         
        double mayorSuma;
        Object[] choices = {"Caso 1", "Caso 2"};
        int dato = JOptionPane.showOptionDialog(null, "<html><img src='file:images/luces.png'></html>", "CRITERIO PARA LA SUMATORIA TOTAL DE LUCES SELECCIONE UNA OPCION", 0, JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
         if (dato == 0) {
           mayorSuma = (luzAltaDerecha + luzAltaIzquierda + sumaExploradoras) > (luzBajaDerecha + luzBajaIzquierda + sumaExploradoras)
                    ? (luzAltaDerecha + luzAltaIzquierda + sumaExploradoras)
                    : (luzBajaDerecha + luzBajaIzquierda + sumaExploradoras);
        } else {
            mayorSuma = (luzAltaDerecha + luzAltaIzquierda + luzBajaDerecha + luzBajaIzquierda + sumaExploradoras);
        }

        //Evaluar la prueba
        String str2 = "INSERT INTO defxprueba(id_defecto,id_prueba,Tipo_defecto) VALUES (?,?,'A')";
        PreparedStatement psDefectos = conexion.prepareStatement(str2);
        //String str3 = "UPDATE medidas SET Condicion='*' WHERE medidas.MEASURE = ?";
        boolean aprobada = true;
        if (farBajaDerecha < permisibleBaja || farBajaIzquierda < permisibleBaja) {
            if (aplicTrans == 0) {
                psDefectos.clearParameters();
                psDefectos.setInt(1, 20000);
                psDefectos.setLong(2, idPrueba);
                psDefectos.executeUpdate();
            }
            aprobada = false;
        }
//            } else if(luzAltaDerecha < permisibleAlta || luzAltaIzquierda < permisibleAlta) {
//                psDefectos.clearParameters();
//                psDefectos.setInt(1, 20003);
//                psDefectos.setLong(2, idPrueba);
//                psDefectos.executeUpdate();
//                aprobada = false;
//            }
        if (angBajaDerecha < permisibleAnguloBajo || angBajaDerecha > permisibleAnguloAlto) {
            if (aplicTrans == 0) {
                psDefectos.clearParameters();
                psDefectos.setInt(1, 20002);//codigo defecto es distinto
                psDefectos.setLong(2, idPrueba);
                psDefectos.executeUpdate();
            }
            aprobada = false;
        } else if (angBajaIzquierda < permisibleAnguloBajo || angBajaIzquierda > permisibleAnguloAlto) {
            if (aplicTrans == 0) {
                psDefectos.clearParameters();
                psDefectos.setInt(1, 20002);//codigo defecto es distinto
                psDefectos.setLong(2, idPrueba);
                psDefectos.executeUpdate();
            }
            aprobada = false;
        }

        if ((mayorSuma) > permisibleSumatoria) {
            //las que se pueden prender al tiempo y se supone mayores
            if (aplicTrans == 0) {
                psDefectos.clearParameters();
                psDefectos.setInt(1, 20001);
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
        HiloPruebaLuxometroVehiculoLujan.serialEquipo = serialEquipo;
        boolean escrTrans = false;
        if (aplicTrans == 1 && aprobada == true) {
            escrTrans = true;
        }
        if (aplicTrans == 0) {
            escrTrans = true;
        }

        statement = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
        instruccion = conexion.prepareStatement(statement);
//      instruccion.setInt(1, 2003);
        instruccion.setInt(1, 2040);
        instruccion.setDouble(2, angBajaDerecha);
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();
        
//      instruccion.setInt(1, 2006);
        instruccion.setInt(1, 2024);
        instruccion.setDouble(2, farBajaDerecha);
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();
        
//      instruccion.setInt(1, 2007);
        instruccion.setInt(1, 2036);
        instruccion.setDouble(2, farAltaDerecha);
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();
        
//      instruccion.setInt(1, 2005);
        instruccion.setInt(1,2044);
        instruccion.setDouble(2, angBajaIzquierda);
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();
        
        
//      instruccion.setInt(1, 2009);
        instruccion.setInt(1, 2031);
        instruccion.setDouble(2, farBajaIzquierda);
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();
        
//        instruccion.setInt(1, 2010);
        instruccion.setInt(1, 2032);
        instruccion.setDouble(2, farAltaIzquierda);
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();
        
        instruccion.setInt(1, 2008);
        instruccion.setDouble(2, sumaExploradoras);
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();
        
        instruccion.setInt(1, 2011);
        instruccion.setDouble(2, mayorSuma);
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();        
        
        
        if (escrTrans == true) {
            if (aprobada) {
                statement2 = "UPDATE pruebas SET Finalizada = 'Y',Aprobada ='Y',Abortada='N',usuario_for = ?,serialEquipo = ? ,observaciones = ? WHERE pruebas.Id_Pruebas = ?";
            } else {
                statement2 = "UPDATE pruebas SET Finalizada = 'Y',Aprobada ='N',Abortada='N',usuario_for = ?,serialEquipo = ? ,observaciones = ? WHERE pruebas.Id_Pruebas = ?";
            }
            PreparedStatement instruccion2 = conexion.prepareStatement(statement2);
            instruccion2.setLong(1, idUsuario);
            instruccion2.setString(2, serialEquipo);
            instruccion2.setString(3, lblObservacion.concat(this.lblExploradoras));
            instruccion2.setLong(4, idPrueba);
            int executeUpdate = instruccion2.executeUpdate();
              
        } else {
            statement2 = "UPDATE pruebas SET Autorizada = 'A',usuario_for = ?,serialEquipo = ? ,observaciones = ? WHERE pruebas.Id_Pruebas = ?";
            PreparedStatement instruccion2 = conexion.prepareStatement(statement2);
            instruccion2.setLong(1, idUsuario);
            instruccion2.setString(2, serialEquipo);
            instruccion2.setString(3, lblObservacion.concat(this.lblExploradoras));
            instruccion2.setLong(4, idPrueba);
            int executeUpdate = instruccion2.executeUpdate();
            
        }
        conexion.commit();
        conexion.setAutoCommit(true);
        conexion.close();
        lblObservacion=lblObservacion.concat(this.lblExploradoras);
        if (org.soltelec.util.Mensajes.mensajePregunta("¿Desea Agregar un Comentario a la Prueba ?")) {
            FrmComentario frm = new FrmComentario(SwingUtilities.getWindowAncestor(panelLuxometro), idPrueba, JDialog.DEFAULT_MODALITY_TYPE,lblObservacion);
            frm.setVisible(true);
            frm.setModal(true);
        }
        if (escrTrans == true) {
            HiloPruebaLuxometroVehiculoLujan.escrTrans = "E";
        } else {
            HiloPruebaLuxometroVehiculoLujan.escrTrans = "A";
        }
        Mensajes.messageDoneTime("Se ha REGISTRADO la Prueba de Luces para el Vehiculo de una manera Exitosa ..¡", 3);
    }

    private boolean utileriaGerencia() throws ClassNotFoundException, SQLException {

        //Crear el objeto de <span class="IL_AD" id="IL_AD12">conexion</span> a la base de datos
        Connection conexion = Conex.getConnection();
        //Insertar las medidas dependiendo del numero de tiempos del motor
        //cargar el permisible inferior de luces
        String statement = "SELECT CRLNAME,CARLINE FROM lineas_vehiculos order by CARLINE";
        String statementUp = "UPDATE lineas_vehiculos SET CRLNAME =? WHERE CARLINE =? ";
        conexion.setAutoCommit(false);
        PreparedStatement instruccion = conexion.prepareStatement(statement);

        ResultSet rs = instruccion.executeQuery();
        String lnACambiar = null;
        String lnTratada = null;

        while (rs.next()) {
            lnACambiar = rs.getString("CRLNAME");
            lnTratada = lnACambiar.replace("\t", ""); //substring(1, lnACambiar.length()); //.replace("\t", lnACambiar);
            instruccion = conexion.prepareStatement(statementUp);
            instruccion.setString(1, lnTratada);
            instruccion.setInt(2, rs.getInt("CARLINE"));
            instruccion.executeUpdate();
            System.out.println("carLine " + rs.getInt("CARLINE"));
            conexion.commit();
        }

        conexion.close();
        return true;
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
