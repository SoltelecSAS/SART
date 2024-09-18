package org.soltelec.luxometro;

import eu.hansolo.steelseries.gauges.DisplayMulti;
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Properties;
//import javafx.scene.input.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import org.soltelec.componenteluces.LuzAlta;
import org.soltelec.componenteluces.LuzBaja;
import org.soltelec.componenteluces.LuzExploradora;
import org.soltelec.pruebasgases.FrmComentario;
import org.soltelec.util.Conex;
import org.soltelec.util.ConsultarDatosVehiculo;

public class HiloPruebaLuxometro implements Runnable, ActionListener
{
  private PanelLuxometro panelLuxometro;
  private Timer timer;
  private int contadorTemporizacion = 0;
  private SerialPort serialPort;
  private InputStream in;
  private OutputStream out;
  private double[] luzBajaDerecha;
  private double[] luzAltaDerecha;
  private double[] luzBajaIzquierda;
  private double[] luzAltaIzquierda;
  private double[] anguloBajaDerecha;
  private double[] anguloBajaIzquierda;
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
  private String urljdbc;
  private boolean cancelacion = false;
  private Thread blinker;
  private Long idPrueba = Long.valueOf(18L);
  private long idUsuario = 1L;
  private long idHojaPrueba;
  private double sumaExploradoras;
  private double permisibleBaja;
  private double permisibleAlta;
  private double permisibleSumatoria;
  private double permisibleAnguloBajo;
  private double permisibleAnguloAlto;
  private String puertoLuxometro;
  public static int aplicTrans = 1;
  public static String escrTrans1 = "";
  public static String ipEquipo;
  public static String serialEquipo = "";
  private static String unidadMed = "";
  public static int lngTrama;
  private String lblExploradoras = "";
  Connection conexion = null;
  private String lblObservacion =  "";
  private boolean medicionTomada = false;

  private int valorInicialTrama = 6;
  private int valorFinalTrama = 11;
  private String tipoVehiculo = "";

  private ArrayList<String> tramasBajas = new ArrayList();
  private ArrayList<String> tramasAltas = new ArrayList();
  private ArrayList<String> tramasFarolas = new ArrayList();

  public void setTipoVehiculo(String tipoVehiculo) {
    System.out.println("tipo vehiculo ="+ tipoVehiculo);
    this.tipoVehiculo = tipoVehiculo;
  }

  public HiloPruebaLuxometro(PanelLuxometro panel, int aplicTrans)
  {
    try {
      cargarUrl();

    } catch (Exception e) {
      System.out.println("Error: "+e.getMessage());
    }

    try {
      this.panelLuxometro = panel;
      this.panelLuxometro.getButtonCancelar().addActionListener(this);
      this.aplicTrans = aplicTrans;

      this.tramasBajas.add("L01D1K");
      this.tramasBajas.add("L01DRK");
      this.tramasBajas.add("L01DLK");
      this.tramasBajas.add("L01D1L");
      this.tramasBajas.add("L01DRL");
      this.tramasBajas.add("L01DLL");

      this.tramasAltas.add("L01M1K");
      this.tramasAltas.add("L01M1L");
      this.tramasAltas.add("L01MRK");
      this.tramasAltas.add("L01MRL");
      this.tramasAltas.add("L01MLK");
      this.tramasAltas.add("L01MLL");

      this.tramasFarolas.add("L01F1K");
      this.tramasFarolas.add("L01F1L");
      this.tramasFarolas.add("L01FRK");
      this.tramasFarolas.add("L01FLL");
      this.tramasFarolas.add("L01FRL");
    } catch (Exception e) {
      System.out.println("Error: "+e.getMessage());
    }

      
    try {
      cargarLongitudTramaLeer();
    } catch (Exception e) {
      System.out.println("Error: "+e.getMessage());
    }
      
    
    
  }

  private void cargarLongitudTramaLeer()
  {
    System.out.println("--------------------------------------------------");
    System.out.println("---------  Cargar Longitud Trama Leer  -----------");
    System.out.println("--------------------------------------------------");

    Properties props = new Properties();
    try
    {
      props.load(new FileInputStream("./propiedades.properties"));
      System.out.println(".... Buscando variable valorInicialTrama.....");
      if ((props.getProperty("valorInicialTrama") != null) || (!props.getProperty("valorInicialTrama").equalsIgnoreCase("")))
      {
        this.valorInicialTrama = Integer.parseInt(props.getProperty("valorInicialTrama"));
        System.out.println(new StringBuilder().append("se obtiene de valorInicialTrama el dato: ").append(this.valorInicialTrama).toString());
      } else {
        System.out.println(new StringBuilder().append(" No recojo nada del properties para  ::valorInicialTrama::  se toma el valor por defecto ").append(this.valorInicialTrama).toString());
      }

      if ((props.getProperty("valorFinalTrama") != null) || (!props.getProperty("valorFinalTrama").equalsIgnoreCase("")))
      {
        this.valorFinalTrama = Integer.parseInt(props.getProperty("valorFinalTrama"));
        System.out.println(new StringBuilder().append("se obtiene de valorFinalTrama el dato: ").append(this.valorFinalTrama).toString());
      } else {
        System.out.println(new StringBuilder().append(" No recojo nada del properties para  ::valorFinalTrama::  se toma el valor por defecto ").append(this.valorFinalTrama).toString());
      }
      
    }catch (IOException e)
    {
      System.out.println(new StringBuilder().append("Se tomaran los valores por defectos para las variables : valorInicialTrama / valorFinalTrama").append(e).toString());
      System.out.println(new StringBuilder().append(" valorInicialTrama : ").append(this.valorInicialTrama).toString());
      System.out.println(new StringBuilder().append(" valorFinalTrama : ").append(this.valorFinalTrama).toString());
      e.printStackTrace();
      System.out.println(new StringBuilder().append("Error en el metodo : cargarLongitudTramaLeer()").append(e).toString());
    }catch (Exception e)
    {
        System.out.println(new StringBuilder().append("Se tomaran los valores por defectos para las variables : valorInicialTrama / valorFinalTrama").append(e).toString());
        System.out.println(new StringBuilder().append(" valorInicialTrama : ").append(this.valorInicialTrama).toString());
        System.out.println(new StringBuilder().append(" valorFinalTrama : ").append(this.valorFinalTrama).toString());
        e.printStackTrace();
        System.out.println(new StringBuilder().append("Error en el metodo : cargarLongitudTramaLeer()").append(e).toString());
    }
  }

  public boolean buscarLuxometro()
  {
    return true;
  }

  private boolean conectarPuertoSerial()
  {
    System.out.println("---------------------------------------------------");
    System.out.println("---Conectandose con el puerto serial---------------");
    System.out.println("---------------------------------------------------");
    try
    {
      this.serialPort = connect(this.puertoLuxometro, 2400, 8, 1, 0);
      System.out.println("Puerto Serial conectado");
      return true;
    }
    catch (Exception ex) {
      ex.printStackTrace(System.err);
      JOptionPane.showMessageDialog(this.panelLuxometro, "Error conectandose al puerto Serial");
      this.panelLuxometro.cerrar();
    }return false;
  }

  private int capturarNumeroLucesBajaDerecha()
  {
    System.out.println("----------------------------------------------------");
    System.out.println("---------  capturarNumeroLucesBajaDerecha ----------");
    System.out.println("----------------------------------------------------");
    try
    {
      Object numeroLuces = JOptionPane.showInputDialog(this.panelLuxometro, " Luces Bajas Der.", "Numero de Farolas", -1, new ImageIcon(), new Object[] { new Integer(1), new Integer(2), new Integer(3), new Integer(4) }, new Integer(9));

      return ((Integer)numeroLuces).intValue();
    }
    catch (Exception e) {
      System.out.println(new StringBuilder().append("Error en el metodo : capturarNumeroLucesBajaDerecha()").append(e.getMessage()).toString());
      System.out.println(new StringBuilder().append("Error en el metodo : capturarNumeroLucesBajaDerecha()").append(e.getStackTrace()).toString());
    }
    return 0;
  }

  
  private String pruebaLucesBajaDerecha()
  {
    System.out.println("----------------------------------------------------");
    System.out.println("--------------  pruebaLucesBajaDerecha     ---------");
    System.out.println("----------------------------------------------------");

    DecimalFormat df = (DecimalFormat)NumberFormat.getInstance(Locale.ENGLISH);
    df.applyPattern("#0.0");

    int numeroLucesBajaDerecha = capturarNumeroLucesBajaDerecha();
    this.luzBajaDerecha = new double[numeroLucesBajaDerecha];
    this.anguloBajaDerecha = new double[numeroLucesBajaDerecha];
    try
    {
      for (int i = 0; i < numeroLucesBajaDerecha; i++)
      {
        DialogoAnguloInclinacion daiBajaDerecha = new DialogoAnguloInclinacion(SwingUtilities.getWindowAncestor(this.panelLuxometro), new StringBuilder().append(" Angulo Inclinacion Luz Baja Derecha ").append(i + 1).toString(), JDialog.DEFAULT_MODALITY_TYPE);
        daiBajaDerecha.getPanel().getLabelTitulo().setText(new StringBuilder().append("Angulo Inclinacion Baja Derecha Farola: ").append(i + 1).toString());
        daiBajaDerecha.setVisible(true);
        this.anguloBajaDerecha[i] = Double.parseDouble(df.format(daiBajaDerecha.getPanel().getValorSlider()));
        this.panelLuxometro.getBajaDerecha().setBlinking(true);
        Thread.sleep(1500L);
        this.panelLuxometro.getLabelMensaje().setText(new StringBuilder().append("BAJA DERECHA Farola: ").append(i + 1).append(" PRES ENTER ..! ").toString());
        this.sb = new StringBuilder("!");
        crearNuevoTimer();
        this.timer.start();
        this.contadorTemporizacion = 0;
        //this.luzBajaDerecha[i] = 0;

        while ((this.contadorTemporizacion < 60) && (this.blinker != null))
        {
            
          Thread.sleep(250L);
          this.luzBajaDerecha[i] = tomarMedicion(Luz.BAJADERECHA);
          
          System.out.println(new StringBuilder().append("Recibo del Luxometro(Farola ").append(i).append("): Unidad Med ").append(this.luzBajaDerecha[i]).append(" Unidad Med ").append(unidadMed).toString());

          if (this.luzBajaDerecha[i] > 0)
          {
                if (unidadMed.equalsIgnoreCase("L"))
                {
                  System.out.println(new StringBuilder().append("ENTRE LOGICA LUX Luxometro:").append(this.luzBajaDerecha[i]).toString());
                  this.luzBajaDerecha[i] *= 0.625;
                }
                break;
          }else{
              
                System.out.println(new StringBuilder().append("syst medICION ").append(unidadMed).toString());
                
                if ((this.luzBajaDerecha[i] == -100) || (this.luzBajaDerecha[i] == -1))
                {
                  System.out.println("entre en logica de -100 or -1, no se detectan luces, sigo midiendo, van : " + this.contadorTemporizacion + "segundos");
                }
                else if (this.luzBajaDerecha[i] == -200)
                {
                  System.out.println("Entra a  la logica del -200");
                }
                else
                {
                  System.out.println("entre en logica de 0.1 ");
                  int n=  JOptionPane.showConfirmDialog ( null,"¿esta seguro de la medicion?", "", JOptionPane.YES_NO_OPTION );
                  if(n == JOptionPane.YES_OPTION)
                  {
                      this.luzBajaDerecha[i] = 0.1;
                      this.contadorTemporizacion =61;
                      break;
                      
                  }else{
                      this.contadorTemporizacion=0;
                  }            
                }                
          }

        }
         if(this.contadorTemporizacion>59)
        {
            
           System.out.println("no se detecto ningun valor, el valor guardado en la base de datos para luzBajaDerecha es cero ´0´ ");
            JOptionPane.showMessageDialog(this.panelLuxometro, "Prueba luz baja derecha finalizada");                    
            this.luzBajaDerecha[i] = 0;
        }
        this.panelLuxometro.getLabelMensaje().setText(new StringBuilder().append("MEDICION TOMADA Farola ").append(i + 1).append("..!").toString());
        this.panelLuxometro.getBajaDerecha().setBlinking(false);
        this.panelLuxometro.getBajaDerecha().setOnOff(true);
        this.timer.stop();
        Thread.sleep(2000L);
      }
      
    }catch (Exception ioe) 
    {
      JOptionPane.showMessageDialog(this.panelLuxometro, "Disculpe, he presentado problemas en leer la trama");
      System.out.println(new StringBuilder().append("Error en el metodo : lucesBajaDerecha()").append(ioe).toString());
      ioe.printStackTrace();
      return null;
    }
    return "";
  }

  private int capturarNumeroLucesAltaDerecha()
  {
    System.out.println("----------------------------------------------------");
    System.out.println("---------  capturarNumeroLucesAltaDerecha   --------");
    System.out.println("----------------------------------------------------");
    try
    {
      Object numeroLucesAltaDerecha = JOptionPane.showInputDialog(this.panelLuxometro, "Numero de Luces Alta Derecha", "Numero de Farolas", -1, new ImageIcon(), new Object[] { new Integer(1), new Integer(2), new Integer(3), new Integer(4) }, new Integer(9));

      return ((Integer)numeroLucesAltaDerecha).intValue();
    }
    catch (Exception e)
    {
      System.out.println(new StringBuilder().append("Error en el metodo : capturarNumeroLucesAltaDerecha()").append(e.getMessage()).toString());
      System.out.println(new StringBuilder().append("Error en el metodo : capturarNumeroLucesAltaDerecha()").append(e.getStackTrace()).toString());
    }
    return 0;
  }

  private String tomarMedidasAltaDerecha()
  {
    System.out.println("----------------------------------------------------");
    System.out.println("---------- pruebaLucesAltaDerechaFarola  --- -------");
    System.out.println("----------------------------------------------------");
    try
    {
      int numeroLucesAltaDerecha = capturarNumeroLucesAltaDerecha();
      this.luzAltaDerecha = new double[numeroLucesAltaDerecha];

      for (int i = 0; i < numeroLucesAltaDerecha; i++)
      {
        Thread.sleep(1500L);
        this.panelLuxometro.getLabelMensaje().setText(new StringBuilder().append("ALTA DER. Farola: ").append(i + 1).append(" PRES ENTER ..! ").toString());
        this.panelLuxometro.getAltaDerecha().setBlinking(true);
        this.sb = new StringBuilder("!");

        crearNuevoTimer();
        this.timer.start();
        this.contadorTemporizacion = 0;
        //this.luzAltaDerecha[i] = 0;

        while (this.contadorTemporizacion < 60 && this.blinker != null)
        {
          Thread.sleep(250L);
          this.luzAltaDerecha[i] = tomarMedicion(Luz.ALTADERECHA);
          
          System.out.println(new StringBuilder().append("recogi del luxometro alta derecha  ").append(this.luzAltaDerecha[i]).toString());
          
          if (this.luzAltaDerecha[i] > 0) 
          {
            if (unidadMed.equalsIgnoreCase("L"))
            {
              System.out.println(new StringBuilder().append("ENTRE LOGICA LUX Luxometro:").append(this.luzAltaDerecha[i]).toString());
              this.luzAltaDerecha[i] *= 0.625;
            }
            
            break;
          }else 
          {
            System.out.println(new StringBuilder().append("systema MEDICION ").append(unidadMed).toString());
            
            if ((this.luzAltaDerecha[i] == -100) || (this.luzAltaDerecha[i] == -1)) 
            {
                System.out.println("entre en logica de -100 or -1 , sigo midiendo, van: " + this.contadorTemporizacion + " segundos");
                
            } else if (this.luzAltaDerecha[i] == -200) 
            {
                
              System.out.println("Entra a  la logica del -200");
              
            }else
                {
                  System.out.println("entre en logica de 0.1 ");
                  int n=  JOptionPane.showConfirmDialog ( null,"¿esta seguro de la medicion?", "", JOptionPane.YES_NO_OPTION );
                  if(n == JOptionPane.YES_OPTION)
                  {
                      this.luzAltaDerecha[i] = 0.1;
                      this.contadorTemporizacion =61;
                      break;
                      
                  }else{
                      this.contadorTemporizacion=0;
                  } 
                }  
          }
        }
         if(this.contadorTemporizacion>59)
        {
            System.out.println("no se detecto ningun valor, el valor guardado en la base de datos para luzAltaDerecha es cero ´0´ ");
            JOptionPane.showMessageDialog(this.panelLuxometro, "Prueba luz alta derecha finalizada");  
           this.luzAltaDerecha[i] = 0;
        }
        this.panelLuxometro.getLabelMensaje().setText(new StringBuilder().append("MEDICION TOMADA FAROLA ALTA NUMERO :").append(i + 1).append("..!").toString());
        this.panelLuxometro.getAltaDerecha().setBlinking(false);
        this.panelLuxometro.getAltaDerecha().setOnOff(true);
        Thread.sleep(2000L);
        this.timer.stop();
      }
    }
    catch (Exception ioe)
    {
      JOptionPane.showMessageDialog(this.panelLuxometro, "Disculpe, he presentado problemas en leer la trama");
      System.out.println(new StringBuilder().append("Error en el metodo : altaDerechaFarola()").append(ioe.getMessage()).toString());
      System.out.println(new StringBuilder().append("Error en el metodo : altaDerechaFarola()").append(ioe).toString());
      ioe.printStackTrace();
      return null;
    }
    return "";
  }

  private int capturarNumeroLucesBajasIzq()
  {
    System.out.println("----------------------------------------------------");
    System.out.println("----------  capturarNumeroLucesBajasIzq --- --------");
    System.out.println("----------------------------------------------------");
    try
    {
      Object numeroLucesBajasIzq = JOptionPane.showInputDialog(this.panelLuxometro, "Luces Bajas Izq.", "Numero de Farolas", -1, new ImageIcon(), new Object[] { new Integer(1), new Integer(2), new Integer(3), new Integer(4) }, new Integer(9));

      return ((Integer)numeroLucesBajasIzq).intValue();
    }
    catch (Exception e) {
      System.out.println(new StringBuilder().append("Error en el metodo : capturarNumeroLucesBajasIzq()").append(e.getMessage()).toString());
      System.out.println(new StringBuilder().append("Error en el metodo : capturarNumeroLucesBajasIzq()").append(e.getStackTrace()).toString());
    }
    return 0;
  }

  private String tomarMedidaBajaIzquierda()
  {
    System.out.println("----------------------------------------------------");
    System.out.println("----------    tomarMedidaBajaIzquierda     ---------");
    System.out.println("----------------------------------------------------");

    DecimalFormat df = (DecimalFormat)NumberFormat.getInstance(Locale.ENGLISH);
    df.applyPattern("#0.0");
    try
    {
      int numeroLucesBajasIzq = capturarNumeroLucesBajasIzq();
      this.luzBajaIzquierda = new double[numeroLucesBajasIzq];
      this.anguloBajaIzquierda = new double[numeroLucesBajasIzq];

      for (int i = 0; i < numeroLucesBajasIzq; i++)
      {
        DialogoAnguloInclinacion daiBajaIzquierda = new DialogoAnguloInclinacion(SwingUtilities.getWindowAncestor(this.panelLuxometro), new StringBuilder().append("Angulo Inclinacion Luz Izquierda Baja ").append(i + 1).toString(), JDialog.ModalityType.DOCUMENT_MODAL);
        daiBajaIzquierda.getPanel().getLabelTitulo().setText(new StringBuilder().append("Angulo Inclinacion Baja Izquierda Farola: ").append(i + 1).toString());
        daiBajaIzquierda.setVisible(true);
        this.anguloBajaIzquierda[i] = Double.parseDouble(df.format(daiBajaIzquierda.getPanel().getValorSlider()));
        this.panelLuxometro.getBajaIzquierda().setBlinking(true);
        Thread.sleep(1500L);
        this.panelLuxometro.getLabelMensaje().setText(new StringBuilder().append("Tomando medida farola baja IZquierda numero  ").append(i + 1).append(" .!!").toString());
        this.sb = new StringBuilder("!");

        crearNuevoTimer();
        this.timer.start();
        this.contadorTemporizacion = 0;
      //  this.luzBajaIzquierda[i] = 0;
        
        while (this.contadorTemporizacion < 60 && this.blinker != null)
        {
          Thread.sleep(250L);
          this.luzBajaIzquierda[i] = tomarMedicion(Luz.BAJAIZQUIERDA);
          
          System.out.println(new StringBuilder().append("recogi del luxometro ").append(this.luzBajaIzquierda[i]).toString());
          if (this.luzBajaIzquierda[i] > 0) 
          {
            if (unidadMed.equalsIgnoreCase("L"))
            {
              System.out.println(new StringBuilder().append("ENTRE LOGICA LUX Luxometro:").append(this.luzBajaIzquierda[i]).toString());
              System.out.println("ENTRE LOGICA INTEND > 15:");
              this.luzBajaIzquierda[i] *= 0.625;
            }
            break;
          }else 
          {
            System.out.println(new StringBuilder().append("syst medICION ").append(unidadMed).toString());
            
            if (this.luzBajaIzquierda[i] == -100 || this.luzBajaIzquierda[i] == -1) 
            {
              System.out.println("entre en logica de -100 or -1, sigo midiendo, van: " + this.contadorTemporizacion + " segundos");
              
            }else if (this.luzBajaIzquierda[i] == -200)
            {
              System.out.println("Entra a  la logica del -200");
              
            } else
                {
                  System.out.println("entre en logica de 0.1 ");
                  int n=  JOptionPane.showConfirmDialog ( null,"¿esta seguro de la medicion?", "", JOptionPane.YES_NO_OPTION );
                  if(n == JOptionPane.YES_OPTION)
                  {
                      this.luzBajaIzquierda[i] = 0.1;
                      this.contadorTemporizacion =61;
                      break;
                      
                  }
                }          
          }

        }
         if(this.contadorTemporizacion>59)
        {
            System.out.println("no se detecto ningun valor, el valor guardado en la base de datos para luzBajaIzquierda es cero ´0´ ");
            JOptionPane.showMessageDialog(this.panelLuxometro, "Prueba de luz baja izquierda finalizada");  
            this.luzBajaIzquierda[i] = 0;
        }
        this.panelLuxometro.getLabelMensaje().setText(new StringBuilder().append("MEDICION TOMADA FAROLA ").append(i + 1).append("..!").toString());
        this.panelLuxometro.getBajaIzquierda().setBlinking(false);
        this.panelLuxometro.getBajaIzquierda().setOnOff(true);
        this.timer.stop();
        Thread.sleep(2000L);
      }
    }
    catch (Exception e)
    {
      JOptionPane.showMessageDialog(this.panelLuxometro, "Disculpe, he presentado problemas en leer la trama");
      System.out.println(new StringBuilder().append("Error en el metodo : tomarMedidaBajaIzquierda()").append(e.getMessage()).toString());
      e.printStackTrace();
      return null;
    }
    return "";
  }

  private int capturarLucesAltaIzq()
  {
    System.out.println("----------------------------------------------------");
    System.out.println("---------- pruebaLucesAltaDerechaFarola  --- --------");
    System.out.println("----------------------------------------------------");
    try
    {
      Object numeroLucesAltaIzq = JOptionPane.showInputDialog(this.panelLuxometro, "Luces Alta Izq.", "Numero de Farolas", -1, new ImageIcon(), new Object[] { new Integer(1), new Integer(2), new Integer(3), new Integer(4) }, new Integer(9));
      return ((Integer)numeroLucesAltaIzq).intValue();
    }
    catch (Exception e)
    {
      System.out.println(new StringBuilder().append("Error en el metodo : capturarLucesAltaIzq()").append(e.getMessage()).toString());
      System.out.println(new StringBuilder().append("Error en el metodo : capturarLucesAltaIzq()").append(e.getStackTrace()).toString());
    }
    return 0;
  }

  private String tomarMedidasAltaIzquierda()
  {
    System.out.println("----------------------------------------------------");
    System.out.println("---------------- pruebaLucesAltaIzq  ---------------");
    System.out.println("----------------------------------------------------");
    try
    {
      int numeroLucesAltaIzq = capturarLucesAltaIzq();
      this.luzAltaIzquierda = new double[numeroLucesAltaIzq];

      for (int i = 0; i < numeroLucesAltaIzq; i++)
      {
        Thread.sleep(1500L);
        this.panelLuxometro.getLabelMensaje().setText(new StringBuilder().append("Faro Alta Izquierda numero: ").append(i + 1).append(" PRES ENTER .! ").toString());
        this.panelLuxometro.getAltaIzquierda().setBlinking(true);
        this.sb = new StringBuilder("!");

        crearNuevoTimer();
        this.timer.start();
        this.contadorTemporizacion = 0;
     //   this.luzAltaIzquierda[i] = 0;
        

        while ((this.contadorTemporizacion < 60) && (this.blinker != null))
        { this.luzAltaIzquierda[i] = tomarMedicion(Luz.ALTAIZQUIERDA);
          Thread.sleep(50L);
          
          System.out.println(new StringBuilder().append("Recogi del luxometro  alta izquierda farola(").append(i + 1).append(")").append(this.luzAltaIzquierda[i]).toString());
          if (this.luzAltaIzquierda[i] > 0)
          {
            if (unidadMed.equalsIgnoreCase("L"))
            {
              System.out.println(new StringBuilder().append("Recibo del Luxometro:").append(this.luzAltaIzquierda[i]).toString());
              this.luzAltaIzquierda[i] *= 0.625;
            }
            break;
            
          }else{
              
            System.out.println(new StringBuilder().append("syst medICION ").append(unidadMed).toString());
            if (this.luzAltaIzquierda[i] == -100 || this.luzAltaIzquierda[i] == -1)
            {
              System.out.println("entre en logica de -100 or -1, sigo midiendo, quedan :" + this.contadorTemporizacion + "segundos");
            }
            else if (this.luzAltaIzquierda[i] == -200)
            {
              System.out.println("Entra a  la logica del -200");
            }
            else
                {
                  System.out.println("entre en logica de 0.1 ");
                  int n=  JOptionPane.showConfirmDialog ( null,"¿esta seguro de la medicion?", "", JOptionPane.YES_NO_OPTION );
                  if(n == JOptionPane.YES_OPTION)
                  {
                      this.luzAltaIzquierda [i] = 0.1;
                      this.contadorTemporizacion =61;
                      break;
                      
                  }else{
                      this.contadorTemporizacion=0;
                  } 
                }
          }

        }if(this.contadorTemporizacion>59)
        {
            System.out.println("no se detecto ningun valor, el valor guardado en la base de datos para luzAltaIzquierda es cero ´0´ ");
            JOptionPane.showMessageDialog(this.panelLuxometro, "Prueba de luz alta izquierda finalizada");  
            this.luzAltaIzquierda[i] = 0;
        }

        this.panelLuxometro.getLabelMensaje().setText(new StringBuilder().append("MEDICION TOMADA FAROLA ALTA NUMERO : ").append(i + 1).append("..!").toString());
        this.panelLuxometro.getAltaIzquierda().setBlinking(false);
        this.panelLuxometro.getAltaIzquierda().setOnOff(true);
        Thread.sleep(2000L);
        this.timer.stop();
      }
    }
    catch (Exception e)
    {
      JOptionPane.showMessageDialog(this.panelLuxometro, "Disculpe, he presentado problemas en leer la trama");
      System.out.println(new StringBuilder().append("Error en el metodo : tomarMedidasAltaIzquierda()").append(e.getMessage()).toString());
      e.printStackTrace();
      return null;
    }
    return "";
  }

  private void sumarExploradoras()
  {
    System.out.println("----------------------------------------------------");
    System.out.println("---------------- sumarExploradoras  ----------------");
    System.out.println("----------------------------------------------------");

    int contador = 0;
    this.sumaExploradoras = 0.0D;

    if (this.medidasExploradoras != null)
    {
      for (double medidaExploradora : this.medidasExploradoras)
      {
        System.err.println(new StringBuilder().append("Exploradora  ").append(++contador).append(" : ").append(medidaExploradora).toString());
        this.sumaExploradoras += medidaExploradora;
      }
    }
    else {
      this.sumaExploradoras = 0.0D;
    }
    System.out.println(new StringBuilder().append("Suma de las Luces Adicionales: ").append(this.sumaExploradoras).toString());
  }

  private boolean exploradorasSeleciondas() throws InterruptedException
  {
    System.out.println("----------------------------------------------------");
    System.out.println("---------------- exploradorasSeleciondas ------------");
    System.out.println("----------------------------------------------------");
    try
    {
      if (!medirExploradoras())
      {
        return false;
      }
    }
    catch (IOException ex) {
      JOptionPane.showMessageDialog(this.panelLuxometro, "Error de I/O");
      this.panelLuxometro.cerrar();
    }
    return true;
  }

  public void run()
  {
    try
    {
      JOptionPane.showMessageDialog(null, "VERIFIQUE PERILLA DE INCLINACION DE LUCES EN EL VEHICULO, SI LA TIENE, PONGALA EN CERO ANTES DE INICIAR");

      PanelConfiguracionExploradoras p = new PanelConfiguracionExploradoras(8);
      JOptionPane.showMessageDialog(null, p, "ParamLuc.Adicional", -1);
      Thread.sleep(1500L);

      if (!conectarPuertoSerial())
      {
        return;
      }
      if (p.getCheckExploradoras().isSelected())
      {
        this.numeroExploradoras = ((Integer)p.getComboNumeroExploradoras().getSelectedItem()).intValue();
        this.medidasExploradoras = new double[this.numeroExploradoras];
        this.panelLuxometro.habilitar(this.numeroExploradoras);
      }
      this.blinker = Thread.currentThread();

      if (pruebaLucesBajaDerecha() == null) 
      {
            return;
      }
      
      if (tomarMedidasAltaDerecha() == null) 
      {
            return;
      }
      
      if (p.getCheckExploradoras().isSelected())
      {
        if (!exploradorasSeleciondas()) 
        {
            return;
        }
      }
      if (tomarMedidaBajaIzquierda() == null)
      {
            return;
      }
      if (tomarMedidasAltaIzquierda() == null) 
      {
            return;
      }
      sumarExploradoras();

      Thread.sleep(2000L);
      this.serialPort.close();
      cargarPermisibles();
      registrarMedidas(this.idPrueba, this.idUsuario, aplicTrans, ipEquipo);
      this.panelLuxometro.cerrar();
      
    } catch (InterruptedException ie)
    {
      this.panelLuxometro.cerrar();
      this.serialPort.close();
      if (this.cancelacion)
      {
        String showInputDialog = JOptionPane.showInputDialog("Comentaro de Aborto");
        registrarCancelacion(showInputDialog, this.idPrueba, this.idUsuario);
      }
    }
  }

  private void crearNuevoTimer()
  {
    this.timer = new Timer(1000, new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
            try
            {
              HiloPruebaLuxometro.this.panelLuxometro.getDisplay().setLcdValue(contadorTemporizacion++);
              Thread.sleep(5);
              HiloPruebaLuxometro.this.panelLuxometro.getDisplay().setLcdValue(contadorTemporizacion);
              Thread.sleep(5);
              HiloPruebaLuxometro.this.panelLuxometro.getDisplay().setLcdValue(contadorTemporizacion);
            }catch (InterruptedException ex) 
            {
              System.out.println("Does not care");
            }
      }
    });
  }

  private double tomarMedicion(Luz luz) throws IOException
  {
    System.out.println("----------------------------------------------------");
    System.out.println("-------------------TOMAR MEDIDAS----------------");
    System.out.println("----------------------------------------------------");

    byte[] b = leerTrama();
    if (b == null)
    {
            //JFM----------------------------------------------------
            System.out.println("el array de bytes es null ");
           // return 0;
            return -100;
            //JFM-----------------------------------------------------
    }

    String cadena = new String(b);
    
    StringBuilder sb = new StringBuilder(cadena);
    sb.deleteCharAt(0);
    cadena = sb.toString();
    System.out.println(new StringBuilder().append("CADENA IS ").append(cadena).toString());

    switch (luz)
    {
        case BAJADERECHA:
          return leerTramaBajas(cadena);
        case ALTADERECHA:
          return leerTramaAltas(cadena);
        case BAJAIZQUIERDA:
          return leerTramaBajas(cadena);
        case ALTAIZQUIERDA:
          return leerTramaAltas(cadena);
        case EXPLORADORA:
          return leerTramaExploradora(cadena);
    }
    System.out.println("Trama no reconocida");

    return -1;
  }

  private double leerTramaBajas(String cadena)
  {
    System.out.println("---------------------------------------------------");
    System.out.println("----------  Leer Trama Luz Baja    Derecha   ------");
    System.out.println("---------------------------------------------------");

    System.out.println(" -- LEYENDO DATOS BAJAS");
    System.out.println(new StringBuilder().append(" -- TRAMA RECOGIDA :").append(cadena).toString());
    System.out.println(new StringBuilder().append(" -- LONGITUD TRAMA :").append(cadena.length()).toString());
    unidadMed = cadena.substring(5, 6);
    System.out.println(new StringBuilder().append(" -- UNIDAD DE MEDIDA RECOGIDA ").append(unidadMed).toString());

    for (String dato : this.tramasBajas)
    {
      if (cadena.regionMatches(0, dato, 0, 6) && unidadMed.equalsIgnoreCase("K"))
      {
        System.out.println(new StringBuilder().append(" -- CADENA DE COMPARACION :").append(dato).toString());
        System.out.println(new StringBuilder().append(" -- DE ").append(cadena).append(" SE TOMA EL DATO :").append(cadena.substring(6, 11)).toString());
        return Double.parseDouble(cadena.substring(this.valorInicialTrama, this.valorFinalTrama));
      }
      if (cadena.regionMatches(0, dato, 0, 6) && unidadMed.equalsIgnoreCase("L"))
      {
        System.out.println(new StringBuilder().append(" -- CADENA DE COMPARACION :").append(dato).toString());
        System.out.println(new StringBuilder().append(" -- DE ").append(cadena).append(" SE TOMA EL DATO ").append(cadena.substring(6, 9)).toString());
        return Double.parseDouble(cadena.substring(this.valorInicialTrama, this.valorFinalTrama));
      }
    }

    return -200;
  }

  private double leerTramaAltas(String cadena)
  {
    System.out.println("---------------------------------------------------");
    System.out.println("----------  Leer Trama Luz Alta    Derecha   ------");
    System.out.println("---------------------------------------------------");

    System.out.println(" -- LEYENDO DATOS ALTA");
    System.out.println(new StringBuilder().append(" -- TRAMA RECOGIDA :").append(cadena).toString());
    System.out.println(new StringBuilder().append(" -- LONGITUD TRAMA :").append(cadena.length()).toString());
    unidadMed = cadena.substring(5, 6);
    System.out.println(new StringBuilder().append(" -- UNIDAD DE MEDIDA RECOGIDA ").append(unidadMed).toString());

    for (String dato : this.tramasAltas)
    {
      if (cadena.regionMatches(0, dato, 0, 6) && unidadMed.equalsIgnoreCase("K"))
      {
        System.out.println(new StringBuilder().append(" -- CADENA DE COMPARACION :").append(dato).toString());
        System.out.println(new StringBuilder().append(" -- DE ").append(cadena).append(" SE TOMA EL DATO :").append(cadena.substring(6, 11)).toString());
        return Double.parseDouble(cadena.substring(this.valorInicialTrama, this.valorFinalTrama));
        
      }if (cadena.regionMatches(0, dato, 0, 6) && unidadMed.equalsIgnoreCase("L"))
      {
        System.out.println(new StringBuilder().append(" -- CADENA DE COMPARACION :").append(dato).toString());
        System.out.println(new StringBuilder().append(" -- DE ").append(cadena).append(" SE TOMA EL DATO ").append(cadena.substring(6, 9)).toString());
        return Double.parseDouble(cadena.substring(this.valorInicialTrama, this.valorFinalTrama));
      }
    }
    System.err.println(new StringBuilder().append(" La trama ").append(cadena).append(" no reconocida").toString());
    return -200;
  }

//  private double leerTramaLuzBajaIzquierda(String cadena)
//  {
//    System.out.println("---------------------------------------------------");
//    System.out.println("----------  Leer Trama Luz Baja   Izquierda  ------");
//    System.out.println("---------------------------------------------------");
//    double valor = 0.0D;
//
//    System.out.println(" MARQUE BAJAIZQUIERDA");
//    if (this.nivelParseo.equalsIgnoreCase("SERIAL")) 
//    {
//      System.out.println(new StringBuilder().append("lng trama is ").append(lngTrama).toString());
//      String cadenaComparacion;
//      if (unidadMed.equalsIgnoreCase("K"))
//        cadenaComparacion = "L01DLK";
//      else {
//        cadenaComparacion = "L01DLL";
//      }
//      if (cadena.regionMatches(0, cadenaComparacion, 0, 6)) {
//        System.out.println("Luz Baja Izquierda");
//        this.medicionTomada = true;
//
//        System.out.println(new StringBuilder().append(" WHEN 6,9  ").append(cadena.substring(6, 9)).toString());
//        System.out.println(new StringBuilder().append(" WHEN 6, 10  ").append(cadena.substring(6, 10)).toString());
//        System.out.println(new StringBuilder().append(" WHEN 6, 11  ").append(cadena.substring(6, 11)).toString());
//        valor = Double.parseDouble(cadena.substring(6, 11));
//
//        System.out.println(new StringBuilder().append("Baja Izquierda que registra ").append(valor).toString());
//      }
//    }
//    if (this.nivelParseo.equalsIgnoreCase("SERIAL2")) {
//      System.out.println("nivel Parseo SERIAL2 ");
//      unidadMed = cadena.substring(5, 6);
//      String cadenaComparacion;
//      if (unidadMed.equalsIgnoreCase("K"))
//        cadenaComparacion = "L01D1K";
//      else {
//        cadenaComparacion = "L01D1L";
//      }
//      if (cadena.regionMatches(0, cadenaComparacion, 0, 6)) {
//        System.out.println("Luz Baja Derecha");
//        this.medicionTomada = true;
//        System.out.println(new StringBuilder().append(" WHEN 6, 10  ").append(cadena.substring(6, 10)).toString());
//        System.out.println(new StringBuilder().append(" WHEN 6, 11  ").append(cadena.substring(6, 11)).toString());
//        valor = Double.parseDouble(cadena.substring(6, 11));
//      }
//    }
//
//    if (this.nivelParseo.equalsIgnoreCase("Moon12995")) {
//      System.out.println(new StringBuilder().append("lng trama is ").append(lngTrama).toString());
//      System.out.println(new StringBuilder().append("unidad recojida WHEN 9,10").append(cadena.substring(9, 10)).toString());
//      System.out.println(new StringBuilder().append("unidad recojida WHEN 5,6").append(cadena.substring(8, 12)).toString());
//      System.out.println(new StringBuilder().append("unidad recojida WHEN 8,11").append(cadena.substring(8, 11)).toString());
//      System.out.println(new StringBuilder().append("La cadena TOMADA IS: ").append(cadena).toString());
//      unidadMed = cadena.substring(5, 6);
//      String cadenaComparacion = "L01D1K";
//      if (cadena.regionMatches(0, cadenaComparacion, 0, 6)) {
//        System.out.println(" MARQUE BAJA DERECHA ");
//        System.out.println("Luz Baja Derecha");
//        this.medicionTomada = true;
//        valor = Double.parseDouble(cadena.substring(7, 11));
//      }
//    }
//    return valor;
//  }

//  private double leerTramaLuzAltaIzquierda(String cadena)
//  {
//    System.out.println("---------------------------------------------------");
//    System.out.println("----------  Leer Trama Luz Alta  Izquierda   ------");
//    System.out.println("---------------------------------------------------");
//    double valor = 0.0D;
//
//    System.out.println(" MARQUE ALTAIZQUIERDA");
//    System.out.println(new StringBuilder().append("lng trama is ").append(lngTrama).toString());
//    if (this.nivelParseo.equalsIgnoreCase("SERIAL"))
//    {
//      String cadenaComparacion;
//      String cadenaComparacion;
//      if (unidadMed.equalsIgnoreCase("K"))
//        cadenaComparacion = "L01MLK";
//      else {
//        cadenaComparacion = "L01MLL";
//      }
//      if (cadena.regionMatches(0, cadenaComparacion, 0, 6)) {
//        System.out.println("Luz Alta Izquierda");
//        this.medicionTomada = true;
//        System.out.println(new StringBuilder().append(" WHEN 6,9  ").append(cadena.substring(6, 9)).toString());
//        System.out.println(new StringBuilder().append(" WHEN 6, 10  ").append(cadena.substring(6, 10)).toString());
//        System.out.println(new StringBuilder().append(" WHEN 6, 11  ").append(cadena.substring(6, 11)).toString());
//        valor = Double.parseDouble(cadena.substring(6, 11));
//
//        System.out.println(new StringBuilder().append("Registrare Alta Izquierda con ").append(valor).toString());
//      }
//    }
//    if (this.nivelParseo.equalsIgnoreCase("SERIAL2")) {
//      System.out.println("nivel Parseo SERIAL2 ");
//      unidadMed = cadena.substring(5, 6);
//      String cadenaComparacion;
//      String cadenaComparacion;
//      if (unidadMed.equalsIgnoreCase("K"))
//        cadenaComparacion = "L01M1K";
//      else {
//        cadenaComparacion = "L01M1L";
//      }
//      if (cadena.regionMatches(0, cadenaComparacion, 0, 6)) {
//        System.out.println("Luz Baja Derecha");
//        this.medicionTomada = true;
//        System.out.println(new StringBuilder().append(" WHEN 6, 10  ").append(cadena.substring(6, 10)).toString());
//        System.out.println(new StringBuilder().append(" WHEN 6, 11  ").append(cadena.substring(6, 11)).toString());
//        valor = Double.parseDouble(cadena.substring(6, 11));
//      }
//    }
//
//    if (this.nivelParseo.equalsIgnoreCase("Moon12995")) {
//      unidadMed = cadena.substring(5, 6);
//      String cadenaComparacion = "L01M1K";
//      if (cadena.regionMatches(0, cadenaComparacion, 0, 6)) {
//        System.out.println(" TAKE MEDICION ALTA IZQUIERDA ");
//        this.medicionTomada = true;
//        valor = Double.parseDouble(cadena.substring(7, 11));
//      }
//    }
//
//    return valor;
//  }

  private double leerTramaExploradora(String cadena)
  {
    System.out.println("---------------------------------------------------");
    System.out.println("----------      Leer Trama Luz Exploradora   ------");
    System.out.println("---------------------------------------------------");

    System.out.println(" -- LEYENDO DATOS ALTA");
    System.out.println(new StringBuilder().append(" -- TRAMA RECOGIDA :").append(cadena).toString());
    System.out.println(new StringBuilder().append(" -- LONGITUD TRAMA :").append(cadena.length()).toString());
    unidadMed = cadena.substring(5, 6);
    System.out.println(new StringBuilder().append(" -- UNIDAD DE MEDIDA RECOGIDA ").append(unidadMed).toString());

    for (String dato : this.tramasFarolas)
    {
      if (cadena.regionMatches(0, dato, 0, 6) && unidadMed.equalsIgnoreCase("K"))
      {
        System.out.println(new StringBuilder().append(" -- CADENA DE COMPARACION :").append(dato).toString());
        System.out.println(new StringBuilder().append(" -- DE ").append(cadena).append(" SE TOMA EL DATO :").append(cadena.substring(6, 11)).toString());
        return Double.parseDouble(cadena.substring(this.valorInicialTrama, this.valorFinalTrama));
        
      }if (cadena.regionMatches(0, dato, 0, 6) && unidadMed.equalsIgnoreCase("L"))
      {
        System.out.println(new StringBuilder().append(" -- CADENA DE COMPARACION :").append(dato).toString());
        System.out.println(new StringBuilder().append(" -- DE ").append(cadena).append(" SE TOMA EL DATO ").append(cadena.substring(6, 9)).toString());
        return Double.parseDouble(cadena.substring(this.valorInicialTrama, this.valorFinalTrama));
      }
    }
    System.err.println(new StringBuilder().append(" La trama ").append(cadena).append(" no reconocida").toString());
    return -200;
  }
  

  private boolean medirExploradoras() throws IOException, InterruptedException
  {
    System.out.println("----------------------------------------------------");
    System.out.println("-------------------MEDIR  EXPLORADORAS--------------");
    System.out.println("----------------------------------------------------");

    boolean terminadoConExito = false;
    this.contadorTemporizacion = 0;
    crearNuevoTimer();
    this.timer.start();
    int numeroExploradorasMedidas = 0;
    double medicionTemporal = -100;

    int j = 0;
    while ((numeroExploradorasMedidas < this.numeroExploradoras) && (this.contadorTemporizacion < 300) && (this.blinker != null))
    {
      this.panelLuxometro.getLabelMensaje().setText(new StringBuilder().append("Luz Adicional ").append(numeroExploradorasMedidas + 1).toString());
      this.panelLuxometro.getExploradoras()[(this.numeroExploradoras - numeroExploradorasMedidas - 1)].setBlinking(true);
      medicionTemporal = tomarMedicion(Luz.EXPLORADORA);

      while ((medicionTemporal < 0) && (this.contadorTemporizacion < 300) && (this.blinker != null))
      {
        if (medicionTemporal == -1)
        {
          this.panelLuxometro.getLabelMensaje().setText(new StringBuilder().append("press enter").append(this.sb).toString());
          this.sb.append("!");
        }

        medicionTemporal = tomarMedicion(Luz.EXPLORADORA);
        Thread.sleep(277L);
      }
      this.panelLuxometro.getExploradoras()[(this.numeroExploradoras - numeroExploradorasMedidas - 1)].setBlinking(false);
      this.panelLuxometro.getExploradoras()[(this.numeroExploradoras - numeroExploradorasMedidas - 1)].setOnOff(true);

      if (unidadMed.equalsIgnoreCase("L"))
      {
        medicionTemporal *= 0.625;
      }

      this.medidasExploradoras[(numeroExploradorasMedidas++)] = medicionTemporal;
      j += 1;
      //this.lblExploradoras = this.lblExploradoras.concat(" ").concat("Expl ".concat(String.valueOf(j)).concat(": ").concat(String.valueOf(medicionTemporal)));
    }
    this.timer.stop();

    if (this.contadorTemporizacion >= 300)
    {
      JOptionPane.showMessageDialog(this.panelLuxometro, "Fin prueba\nCinco minutos sin terminar el proceso ....");
      this.panelLuxometro.cerrar();
      this.serialPort.close();
      return false;
    }
    return true;
  }

  private byte[] leerTrama() throws IOException
  {
    boolean existeTrama = false;
    byte[] buffer = new byte[1024];

    int len = 0;
    int data;
    while ((data = this.in.read()) > -1) {
      buffer[(len++)] = ((byte)data);
    }
    lngTrama = 0;
    System.out.println(new StringBuilder().append("Longitud del mensaje:").append(len).toString());
    if (len > 0) {
      System.out.println(new StringBuilder().append("Longitud del mensaje:").append(len).toString());
      lngTrama = len;

      byte[] arreglo = new byte[len];

      System.arraycopy(buffer, 0, arreglo, 0, len);

      return arreglo;
    }

    return null;
  }

  private SerialPort connect(String portName, int baudRate, int dataBits, int stopBits, int parity) throws Exception
  {
    SerialPort serialPort = null;
    CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
    if (portIdentifier.isCurrentlyOwned()) {
      System.out.println("Error: Port is currently in use");
    } else {
      CommPort commPort = portIdentifier.open(getClass().getName(), 2000);

      if ((commPort instanceof SerialPort)) {
        serialPort = (SerialPort)commPort;

        serialPort.setSerialPortParams(2400, 8, 1, 0);
        serialPort.setDTR(false);
        serialPort.setRTS(false);
        serialPort.setFlowControlMode(1);
        this.in = serialPort.getInputStream();
        this.out = serialPort.getOutputStream();
      }
      else
      {
        System.out.println("Error: Only serial ports are handled by this example.");
      }
    }
    return serialPort;
  }

  private void registrarCancelacion(String comentario, Long idPrueba, long idUsuario)
  {
    try 
    
    {
      String serialEquipo = "";
      try {
        serialEquipo = ConsultarDatosVehiculo.buscarSerialEquipo(idPrueba.longValue());
      } catch (Exception e) {
        serialEquipo = "Serial no encontrado";
        e.printStackTrace();
      }

      Connection conexion = Conex.getConnection();

      String statement = "UPDATE pruebas SET Finalizada = 'Y',Aprobado='N',Abortado='Y',Comentario_aborto=?,usuario_for = ?,Fecha_aborto=?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?";
      PreparedStatement instruccion = conexion.prepareStatement(statement);
      instruccion.setString(1, comentario);
      instruccion.setLong(2, idUsuario);
      instruccion.setString(4, serialEquipo);
      instruccion.setLong(5, idPrueba.longValue());
      java.util.Date d = new java.util.Date();
      Calendar c = new GregorianCalendar();
      c.setTime(d);
      instruccion.setDate(3, new java.sql.Date(c.getTimeInMillis()));

      int n = instruccion.executeUpdate();
      conexion.close();
    }
    catch (ClassNotFoundException e)
    {
      System.out.println(e);
    } catch (SQLException e) {
      System.out.println(e);
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  private void cargarUrl()
  {
    Properties props = new Properties();
    try
    {
      props.load(new FileInputStream("./propiedades.properties"));
      this.urljdbc = props.getProperty("urljdbc");
      this.puertoLuxometro = props.getProperty("PuertoLuxometro");
      this.nivelParseo = props.getProperty("nivelParseo");
      System.out.println("nivelParseo ".concat(this.nivelParseo));
      System.out.println(this.urljdbc);
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void actionPerformed(ActionEvent e)
  {
    stop();
  }

  private void stop() 
  {
    this.cancelacion = true;
    Thread tmpBlinker = this.blinker;
    this.blinker = null;
    if (tmpBlinker != null)
      tmpBlinker.interrupt();
  }

  private void registrarMedidasluzBajaDerecha()
  {
    System.out.println("----------------------------------------------------");
    System.out.println("----------registrarMedidasluzBajaDerecha -----------");
    System.out.println("----------------------------------------------------");
    try
    {
      System.out.println(new StringBuilder().append("-- luzBajaDerecha : ").append(this.luzBajaDerecha.length).append("--").toString());

      this.farBajaDerecha = this.luzBajaDerecha[0];
      this.angBajaDerecha = this.anguloBajaDerecha[0];

      String statement = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
      PreparedStatement instruccion = this.conexion.prepareStatement(statement);
      instruccion.setInt(1, 2024);
      instruccion.setDouble(2, this.luzBajaDerecha[0]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.execute();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- INTENSIDAD : 2024 : ").append(this.luzBajaDerecha[0]).toString());

      instruccion.setInt(1, 2040);
      instruccion.setDouble(2, this.anguloBajaDerecha[0]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.execute();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- ANGULO     : 2040 : ").append(this.anguloBajaDerecha[0]).toString());
    }
    catch (SQLException e)
    {
      System.out.println(new StringBuilder().append("Error en el metodo : pruebaLucesAltaIzq()").append(e.getMessage()).toString());
      System.out.println(new StringBuilder().append("Error en el metodo : pruebaLucesAltaIzq()").append(e.getStackTrace()).toString());
      System.out.println(new StringBuilder().append("Error en el metodo : pruebaLucesAltaIzq()").append(e.getSQLState()).toString());
    }
  }

  private void registrarLuzBajaIzquierda()
  {
    System.out.println("----------------------------------------------------");
    System.out.println("----------registrarLuzBajaIzquierda ----------------");
    System.out.println("----------------------------------------------------");
    try
    {
      System.out.println(new StringBuilder().append("-- luzBajaIzquierda : ").append(this.luzBajaIzquierda.length).append("--").toString());

      this.farBajaIzquierda = this.luzBajaIzquierda[0];
      this.angBajaIzquierda = this.anguloBajaIzquierda[0];
      String statement = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
      PreparedStatement instruccion = this.conexion.prepareStatement(statement);
      instruccion.setInt(1, 2031);
      instruccion.setDouble(2, this.luzBajaIzquierda[0]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- INTENSIDAD : 2031 : ").append(this.luzBajaIzquierda[0]).toString());

      instruccion.setInt(1, 2044);
      instruccion.setDouble(2, this.anguloBajaIzquierda[0]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- ANGULO     : 2044 : ").append(this.anguloBajaIzquierda[0]).toString());
    } catch (SQLException e) {
      System.out.println(new StringBuilder().append("Error en el metodo : pruebaLucesAltaIzq()").append(e.getMessage()).toString());
      System.out.println(new StringBuilder().append("Error en el metodo : pruebaLucesAltaIzq()").append(e.getStackTrace()).toString());
      System.out.println(new StringBuilder().append("Error en el metodo : pruebaLucesAltaIzq()").append(e.getSQLState()).toString());
    }
  }

  private void registrarMedidasluzAltaIzquierda()
  {
    System.out.println("----------------------------------------------------");
    System.out.println("---------- registrarMedidasluzAltaIzquierda ---------");
    System.out.println("----------------------------------------------------");
    try {
      System.out.println(new StringBuilder().append("-- luzAltaIzquierda : ").append(this.luzAltaIzquierda.length).append("--").toString());

      this.farAltaIzquierda = this.luzAltaIzquierda[0];
      String statement = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
      PreparedStatement instruccion = this.conexion.prepareStatement(statement);

      instruccion.setInt(1, 2032);
      instruccion.setDouble(2, this.luzAltaIzquierda[0]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- INTENSIDAD     : 2032 : ").append(this.luzAltaIzquierda[0]).toString());
    }
    catch (SQLException e) {
      System.out.println(new StringBuilder().append("Error en el metodo : registrarMedidasluzAltaIzquierda()").append(e.getMessage()).toString());
      System.out.println(new StringBuilder().append("Error en el metodo : registrarMedidasluzAltaIzquierda()").append(e.getStackTrace()).toString());
      System.out.println(new StringBuilder().append("Error en el metodo : registrarMedidasluzAltaIzquierda()").append(e.getSQLState()).toString());
    }
  }

  private void registrarMedidasluzAltaDerecha()
  {
    System.out.println("----------------------------------------------------");
    System.out.println("---------- registrarMedidasluzAltaDerecha ----------");
    System.out.println("----------------------------------------------------");
    try
    {
      System.out.println(new StringBuilder().append("-- luzAltaDerecha : ").append(this.luzAltaDerecha.length).append("--").toString());

      this.farAltaDerecha = this.luzAltaDerecha[0];
      String statement = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
      PreparedStatement instruccion = this.conexion.prepareStatement(statement);

      instruccion.setInt(1, 2036);
      instruccion.setDouble(2, this.luzAltaDerecha[0]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- INTENSIDAD     : 2036 : ").append(this.luzAltaDerecha[0]).toString());
    }
    catch (SQLException e)
    {
      System.out.println(new StringBuilder().append("Error en el metodo : pruebaLucesAltaIzq()").append(e.getMessage()).toString());
      System.out.println(new StringBuilder().append("Error en el metodo : pruebaLucesAltaIzq()").append(e.getStackTrace()).toString());
      System.out.println(new StringBuilder().append("Error en el metodo : pruebaLucesAltaIzq()").append(e.getSQLState()).toString());
    }
  }

  private void registrarMedidasluzBajaDerechaeje2()
  {
    System.out.println("----------------------------------------------------");
    System.out.println("---------- registrarMedidasluzBajaDerecha2 ---------");
    System.out.println("----------------------------------------------------");
    try
    {
      this.lblObservacion = this.lblObservacion.concat(" ").concat("Far1 B.D.=").concat(String.valueOf(this.luzBajaDerecha[0])).concat(" Far2 B.D.=").concat(String.valueOf(this.luzBajaDerecha[1])).concat(" Ang1. B.D.=").concat(String.valueOf(this.anguloBajaDerecha[0])).concat(" Ang2. B.D.=").concat(String.valueOf(this.anguloBajaDerecha[1]));
      this.farBajaDerecha = (this.luzBajaDerecha[0] <= this.luzBajaDerecha[1] ? this.luzBajaDerecha[0] : this.luzBajaDerecha[1]);

      boolean overErrorFar1 = false;
      boolean overErrorFar2 = false;
      double limInfer = 0.0D;
      double limSup = 0.0D;

      if ((this.anguloBajaDerecha[0] < this.permisibleAnguloBajo) || (this.anguloBajaDerecha[0] > this.permisibleAnguloAlto)) {
        overErrorFar1 = true;
      }
      if ((this.anguloBajaDerecha[1] < this.permisibleAnguloBajo) || (this.anguloBajaDerecha[1] > this.permisibleAnguloAlto)) {
        overErrorFar2 = true;
      }
      if ((overErrorFar1 == true) || (overErrorFar2 == true)) {
        if (overErrorFar1 == true) {
          this.angBajaDerecha = this.anguloBajaDerecha[0];
        }
        if (overErrorFar2 == true)
          this.angBajaDerecha = this.anguloBajaDerecha[1];
      }
      else {
        limInfer = Math.abs(this.anguloBajaDerecha[0] - this.permisibleAnguloBajo);
        limSup = Math.abs(this.anguloBajaDerecha[0] - this.permisibleAnguloAlto);
        double nearErrorFar1 = limInfer <= limSup ? limInfer : limSup;
        limInfer = Math.abs(this.anguloBajaDerecha[1] - this.permisibleAnguloBajo);
        limSup = Math.abs(this.anguloBajaDerecha[1] - this.permisibleAnguloAlto);
        double nearErrorFar2 = limInfer <= limSup ? limInfer : limSup;
        if (nearErrorFar1 <= nearErrorFar2)
          this.angBajaDerecha = this.anguloBajaDerecha[0];
        else {
          this.angBajaDerecha = this.anguloBajaDerecha[1];
        }
      }

      System.out.println(new StringBuilder().append("-- luzBajaDerecha.length : ").append(this.luzBajaDerecha.length).append("--").toString());

      String statement = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
      PreparedStatement instruccion = this.conexion.prepareStatement(statement);
      instruccion.setInt(1, 2024);
      instruccion.setDouble(2, this.luzBajaDerecha[0]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- INTENSIDAD : 2024 : ").append(this.luzBajaDerecha[0]).toString());

      instruccion.setInt(1, 2040);
      instruccion.setDouble(2, this.anguloBajaDerecha[0]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- ANGULO : 2040 : ").append(this.anguloBajaDerecha[0]).toString());

      instruccion.setInt(1, 2025);
      instruccion.setDouble(2, this.luzBajaDerecha[1]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- INTENSIDAD : 2025 : ").append(this.luzBajaDerecha[1]).toString());

      instruccion.setInt(1, 2041);
      instruccion.setDouble(2, this.anguloBajaDerecha[1]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- ANGULO : 2041 : ").append(this.anguloBajaDerecha[1]).toString());
    }
    catch (SQLException e)
    {
      System.out.println(new StringBuilder().append("Error en el metodo : registrarMedidasluzBajaDerecha2()").append(e.getMessage()).toString());
      System.out.println(new StringBuilder().append("Error en el metodo : registrarMedidasluzBajaDerecha2()").append(e.getStackTrace()).toString());
      System.out.println(new StringBuilder().append("Error en el metodo : registrarMedidasluzBajaDerecha2()").append(e.getSQLState()).toString());
    }
  }

  private void registrarMedidasluzBajaIzquierdaEje2()
  {
    System.out.println("----------------------------------------------------");
    System.out.println("--- registrarMedidasluzBajaIzquierdaEje2   ---------");
    System.out.println("----------------------------------------------------");
    try
    {
      this.lblObservacion = this.lblObservacion.concat(" ").concat("Far1 B.I.=").concat(String.valueOf(this.luzBajaIzquierda[0])).concat(" Far2 B.I.=").concat(String.valueOf(this.luzBajaIzquierda[1])).concat(" Ang1. B.I.=").concat(String.valueOf(this.anguloBajaIzquierda[0])).concat(" Ang2. B.I.=").concat(String.valueOf(this.anguloBajaIzquierda[1]));
      this.farBajaIzquierda = (this.luzBajaIzquierda[0] <= this.luzBajaIzquierda[1] ? this.luzBajaIzquierda[0] : this.luzBajaIzquierda[1]);

      boolean overErrorFar1 = false;
      boolean overErrorFar2 = false;
      double limInfer = 0;
      double limSup = 0;

      if ((this.anguloBajaIzquierda[0] < this.permisibleAnguloBajo) || (this.anguloBajaIzquierda[0] > this.permisibleAnguloAlto)) {
        overErrorFar1 = true;
      }
      if ((this.anguloBajaIzquierda[1] < this.permisibleAnguloBajo) || (this.anguloBajaIzquierda[1] > this.permisibleAnguloAlto)) {
        overErrorFar2 = true;
      }
      if ((overErrorFar1 == true) || (overErrorFar2 == true)) {
        if (overErrorFar1 == true) {
          this.angBajaIzquierda = this.anguloBajaIzquierda[0];
        }
        if (overErrorFar2 == true)
          this.angBajaIzquierda = this.anguloBajaIzquierda[1];
      }
      else {
        limInfer = Math.abs(this.anguloBajaIzquierda[0] - this.permisibleAnguloBajo);
        limSup = Math.abs(this.anguloBajaIzquierda[0] - this.permisibleAnguloAlto);
        double nearErrorFar1 = limInfer <= limSup ? limInfer : limSup;
        limInfer = Math.abs(this.anguloBajaIzquierda[1] - this.permisibleAnguloBajo);
        limSup = Math.abs(this.anguloBajaIzquierda[1] - this.permisibleAnguloAlto);
        double nearErrorFar2 = limInfer <= limSup ? limInfer : limSup;
        if (nearErrorFar1 <= nearErrorFar2)
          this.angBajaIzquierda = this.anguloBajaIzquierda[0];
        else {
          this.angBajaIzquierda = this.anguloBajaIzquierda[1];
        }
      }
      String statement = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
      PreparedStatement instruccion = this.conexion.prepareStatement(statement);

      System.out.println(new StringBuilder().append("-- luzBajaIzquierda : ").append(this.luzBajaIzquierda.length).append("--").toString());

      instruccion.setInt(1, 2031);
      instruccion.setDouble(2, this.luzBajaIzquierda[0]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- INTENSIDAD : 2031 : ").append(this.luzBajaIzquierda[0]).toString());

      instruccion.setInt(1, 2044);
      instruccion.setDouble(2, this.anguloBajaIzquierda[0]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- ANGULO : 2044 : ").append(this.anguloBajaIzquierda[0]).toString());

      instruccion.setInt(1, 2030);
      instruccion.setDouble(2, this.luzBajaIzquierda[1]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- INTENSIDAD : 2030 : ").append(this.luzBajaIzquierda[1]).toString());

      instruccion.setInt(1, 2045);
      instruccion.setDouble(2, this.anguloBajaIzquierda[1]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- ANGULO : 2045 : ").append(this.anguloBajaIzquierda[1]).toString());
    }
    catch (SQLException e)
    {
      System.out.println(new StringBuilder().append("Error en el metodo : pruebaLucesAltaIzq()").append(e.getMessage()).toString());
      System.out.println(new StringBuilder().append("Error en el metodo : pruebaLucesAltaIzq()").append(e.getStackTrace()).toString());
      System.out.println(new StringBuilder().append("Error en el metodo : pruebaLucesAltaIzq()").append(e.getSQLState()).toString());
    }
  }

  private void registrarMedidasluzAltaDerechaEje2()
  {
    System.out.println("----------------------------------------------------");
    System.out.println("---------- registrarMedidasluzAltaDerechaEje2 ------");
    System.out.println("----------------------------------------------------");
    try
    {
      System.out.println(new StringBuilder().append("-- luzAltaDerecha : ").append(this.luzAltaDerecha.length).append("--").toString());
      this.lblObservacion = this.lblObservacion.concat(" ").concat("Far1 A.D.=").concat(String.valueOf(this.luzAltaDerecha[0])).concat(" Far2 A.D.=").concat(String.valueOf(this.luzAltaDerecha[1]));

      this.farAltaDerecha = (this.luzAltaDerecha[0] <= this.luzAltaDerecha[1] ? this.luzAltaDerecha[0] : this.luzAltaDerecha[1]);
      String statement = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
      PreparedStatement instruccion = this.conexion.prepareStatement(statement);

      instruccion.setInt(1, 2032);
      instruccion.setDouble(2, this.luzAltaDerecha[0]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- INTENSIDAD : 2032 : ").append(this.luzAltaDerecha[0]).toString());

      instruccion.setInt(1, 2033);
      instruccion.setDouble(2, this.luzAltaDerecha[1]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- INTENSIDAD : 2033 : ").append(this.luzAltaDerecha[1]).toString());
    }
    catch (SQLException e)
    {
      System.out.println(new StringBuilder().append("Error en el metodo : registrarMedidasluzAltaDerechaEje2()").append(e.getMessage()).toString());
      System.out.println(new StringBuilder().append("Error en el metodo : registrarMedidasluzAltaDerechaEje2()").append(e.getStackTrace()).toString());
      System.out.println(new StringBuilder().append("Error en el metodo : registrarMedidasluzAltaDerechaEje2()").append(e.getSQLState()).toString());
    }
  }

  private void registrarMedidasluzAltaIzquierdaEje2()
  {
    System.out.println("----------------------------------------------------");
    System.out.println("---------- registrarMedidasluzAltaIzquierdaEje2-----");
    System.out.println("----------------------------------------------------");
    try {
      this.farAltaIzquierda = (this.luzAltaIzquierda[0] <= this.luzAltaIzquierda[1] ? this.luzAltaIzquierda[0] : this.luzAltaIzquierda[1]);

      this.lblObservacion = this.lblObservacion.concat(" ").concat("Far1 A.I.=").concat(String.valueOf(this.luzAltaIzquierda[0])).concat(" Far2 A.I.=").concat(String.valueOf(this.luzAltaIzquierda[1]));
      String statement = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
      PreparedStatement instruccion = this.conexion.prepareStatement(statement);

      System.out.println(new StringBuilder().append("-- luzAltaIzquierda : ").append(this.luzAltaIzquierda.length).append("--").toString());

      instruccion.setInt(1, 2036);
      instruccion.setDouble(2, this.luzAltaIzquierda[0]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- INTENSIDAD : 2036 : ").append(this.luzAltaIzquierda[0]).toString());

      instruccion.setInt(1, 2037);
      instruccion.setDouble(2, this.luzAltaIzquierda[1]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- INTENSIDAD : 2037 : ").append(this.luzAltaIzquierda[1]).toString());
    }
    catch (SQLException e) {
      System.out.println(new StringBuilder().append("Error en el metodo : pruebaLucesAltaIzq()").append(e.getMessage()).toString());
      System.out.println(new StringBuilder().append("Error en el metodo : pruebaLucesAltaIzq()").append(e.getStackTrace()).toString());
      System.out.println(new StringBuilder().append("Error en el metodo : pruebaLucesAltaIzq()").append(e.getSQLState()).toString());
    }
  }

  private void registrarMedidasluzBajaDerechaEje3()
  {
    System.out.println("----------------------------------------------------");
    System.out.println("---------- registrarMedidasluzAltaIzquierdaEje2 ----");
    System.out.println("----------------------------------------------------");
    try {
      this.lblObservacion = this.lblObservacion.concat(" ").concat("Far1 B.D.=").concat(String.valueOf(this.luzBajaDerecha[0])).concat(" Far2 B.D.=").concat(String.valueOf(this.luzBajaDerecha[1])).concat(" Far3 B.D.=").concat(String.valueOf(this.luzBajaDerecha[2])).concat(" Ang1. B.D.=").concat(String.valueOf(this.anguloBajaDerecha[0])).concat(" Ang2. B.D.=").concat(String.valueOf(this.anguloBajaDerecha[1])).concat(" Ang3. B.D.=").concat(String.valueOf(this.anguloBajaDerecha[2]));
      this.farBajaDerecha = (this.luzBajaDerecha[0] <= this.luzBajaDerecha[1] ? this.luzBajaDerecha[0] : this.luzBajaDerecha[1]);

      if (this.luzBajaDerecha[2] <= this.farBajaDerecha) {
        this.farBajaDerecha = this.luzBajaDerecha[2];
      }

      boolean overErrorFar1 = false;
      boolean overErrorFar2 = false;
      boolean overErrorFar3 = false;
      double limInfer = 0.0D;
      double limSup = 0.0D;

      if ((this.anguloBajaDerecha[0] < this.permisibleAnguloBajo) || (this.anguloBajaDerecha[0] > this.permisibleAnguloAlto)) {
        overErrorFar1 = true;
      }
      if ((this.anguloBajaDerecha[1] < this.permisibleAnguloBajo) || (this.anguloBajaDerecha[1] > this.permisibleAnguloAlto)) {
        overErrorFar2 = true;
      }
      if ((this.anguloBajaDerecha[2] < this.permisibleAnguloBajo) || (this.anguloBajaDerecha[2] > this.permisibleAnguloAlto)) {
        overErrorFar3 = true;
      }

      if ((overErrorFar1 == true) || (overErrorFar2 == true)) {
        if (overErrorFar1 == true) {
          this.angBajaDerecha = this.anguloBajaDerecha[0];
        }
        if (overErrorFar2 == true) {
          this.angBajaDerecha = this.anguloBajaDerecha[1];
        }
        if (overErrorFar3 == true)
          this.angBajaDerecha = this.anguloBajaDerecha[2];
      }
      else {
        limInfer = Math.abs(this.anguloBajaDerecha[0] - this.permisibleAnguloBajo);
        limSup = Math.abs(this.anguloBajaDerecha[0] - this.permisibleAnguloAlto);
        double nearErrorFar1 = limInfer <= limSup ? limInfer : limSup;
        limInfer = Math.abs(this.anguloBajaDerecha[1] - this.permisibleAnguloBajo);
        limSup = Math.abs(this.anguloBajaDerecha[1] - this.permisibleAnguloAlto);
        double nearErrorFar2 = limInfer <= limSup ? limInfer : limSup;
        limInfer = Math.abs(this.anguloBajaDerecha[2] - this.permisibleAnguloBajo);
        limSup = Math.abs(this.anguloBajaDerecha[2] - this.permisibleAnguloAlto);
        double nearErrorFar3 = limInfer <= limSup ? limInfer : limSup;
        String menor = nearErrorFar1 <= nearErrorFar2 ? "baj1" : "baj2";
        if (menor.equalsIgnoreCase("baj1")) {
          if (nearErrorFar3 <= nearErrorFar1)
            this.angBajaDerecha = this.anguloBajaDerecha[2];
          else {
            this.angBajaDerecha = this.anguloBajaDerecha[0];
          }
        }
        if (menor.equalsIgnoreCase("baj2")) {
          if (nearErrorFar3 <= nearErrorFar2)
            this.angBajaDerecha = this.anguloBajaDerecha[2];
          else {
            this.angBajaDerecha = this.anguloBajaDerecha[1];
          }
        }

      }

      System.out.println(new StringBuilder().append("-- luzBajaDerecha : ").append(this.luzBajaDerecha.length).append("--").toString());

      String statement = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
      PreparedStatement instruccion = this.conexion.prepareStatement(statement);
      instruccion.setInt(1, 2024);
      instruccion.setDouble(2, this.luzBajaDerecha[0]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- INTENSIDAD : 2024 : ").append(this.luzBajaDerecha[0]).toString());

      instruccion.setInt(1, 2040);
      instruccion.setDouble(2, this.anguloBajaDerecha[0]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- ANGULO : 2040 : ").append(this.anguloBajaDerecha[0]).toString());

      instruccion.setInt(1, 2025);
      instruccion.setDouble(2, this.luzBajaDerecha[1]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- INTENSIDAD : 2025 : ").append(this.luzBajaDerecha[1]).toString());

      instruccion.setInt(1, 2041);
      instruccion.setDouble(2, this.anguloBajaDerecha[1]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- ANGULO : 2041 : ").append(this.anguloBajaDerecha[1]).toString());

      instruccion.setInt(1, 2026);
      instruccion.setDouble(2, this.luzBajaDerecha[2]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- INTENSIDAD : 2026 : ").append(this.luzBajaDerecha[2]).toString());

      instruccion.setInt(1, 2042);
      instruccion.setDouble(2, this.anguloBajaDerecha[2]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- ANGULO : 2042 : ").append(this.anguloBajaDerecha[2]).toString());
    }
    catch (SQLException e) {
      System.out.println(new StringBuilder().append("Error en el metodo :registrarMedidasluzBajaDerechaEje3()").append(e.getMessage()).toString());
      System.out.println(new StringBuilder().append("Error en el metodo :registrarMedidasluzBajaDerechaEje3()").append(e.getStackTrace()).toString());
      System.out.println(new StringBuilder().append("Error en el metodo :registrarMedidasluzBajaDerechaEje3()").append(e.getSQLState()).toString());
    }
  }

  private void registrarMedidasLuzBajaIzquierdaEje3()
  {
    System.out.println("----------------------------------------------------");
    System.out.println("----------registrarMedidasLuzBajaIzquierdaEje3 -----");
    System.out.println("----------------------------------------------------");
    try {
      this.lblObservacion = this.lblObservacion.concat(" ").concat("Far1 B.I.=").concat(String.valueOf(this.luzBajaIzquierda[0])).concat(" Far2 B.I.=").concat(String.valueOf(this.luzBajaIzquierda[1])).concat(" Far3 B.I.=").concat(String.valueOf(this.luzBajaIzquierda[2])).concat(" Ang1. B.I.=").concat(String.valueOf(this.anguloBajaIzquierda[0])).concat(" Ang2. B.I.=").concat(String.valueOf(this.anguloBajaIzquierda[1])).concat(" Ang3. B.I.=").concat(String.valueOf(this.anguloBajaIzquierda[2]));

      this.farBajaIzquierda = (this.luzBajaIzquierda[0] <= this.luzBajaIzquierda[1] ? this.luzBajaIzquierda[0] : this.luzBajaIzquierda[1]);

      if (this.luzBajaIzquierda[2] <= this.farBajaIzquierda) {
        this.farBajaIzquierda = this.luzBajaIzquierda[2];
      }

      boolean overErrorFar1 = false;
      boolean overErrorFar2 = false;
      boolean overErrorFar3 = false;
      double limInfer = 0.0D;
      double limSup = 0.0D;

      if ((this.anguloBajaIzquierda[0] < this.permisibleAnguloBajo) || (this.anguloBajaIzquierda[0] > this.permisibleAnguloAlto)) {
        overErrorFar1 = true;
      }
      if ((this.anguloBajaIzquierda[1] < this.permisibleAnguloBajo) || (this.anguloBajaIzquierda[1] > this.permisibleAnguloAlto)) {
        overErrorFar2 = true;
      }
      if ((this.anguloBajaIzquierda[2] < this.permisibleAnguloBajo) || (this.anguloBajaIzquierda[2] > this.permisibleAnguloAlto)) {
        overErrorFar3 = true;
      }

      if ((overErrorFar1 == true) || (overErrorFar2 == true)) {
        if (overErrorFar1 == true) {
          this.angBajaIzquierda = this.anguloBajaIzquierda[0];
        }
        if (overErrorFar2 == true) {
          this.angBajaIzquierda = this.anguloBajaIzquierda[1];
        }
        if (overErrorFar3 == true)
          this.angBajaIzquierda = this.anguloBajaIzquierda[2];
      }
      else {
        limInfer = Math.abs(this.anguloBajaIzquierda[0] - this.permisibleAnguloBajo);
        limSup = Math.abs(this.anguloBajaIzquierda[0] - this.permisibleAnguloAlto);
        double nearErrorFar1 = limInfer <= limSup ? limInfer : limSup;
        limInfer = Math.abs(this.anguloBajaIzquierda[1] - this.permisibleAnguloBajo);
        limSup = Math.abs(this.anguloBajaIzquierda[1] - this.permisibleAnguloAlto);
        double nearErrorFar2 = limInfer <= limSup ? limInfer : limSup;
        limInfer = Math.abs(this.anguloBajaIzquierda[2] - this.permisibleAnguloBajo);
        limSup = Math.abs(this.anguloBajaIzquierda[2] - this.permisibleAnguloAlto);
        double nearErrorFar3 = limInfer <= limSup ? limInfer : limSup;
        String menor = nearErrorFar1 <= nearErrorFar2 ? "baj1" : "baj2";

        if (menor.equalsIgnoreCase("baj1")) {
          if (nearErrorFar3 <= nearErrorFar1)
            this.angBajaIzquierda = this.anguloBajaIzquierda[2];
          else {
            this.angBajaIzquierda = this.anguloBajaIzquierda[0];
          }
        }
        if (menor.equalsIgnoreCase("baj2")) {
          if (nearErrorFar3 <= nearErrorFar2)
            this.angBajaIzquierda = this.anguloBajaIzquierda[2];
          else {
            this.angBajaIzquierda = this.anguloBajaIzquierda[1];
          }
        }
      }

      System.out.println(new StringBuilder().append("-- luzBajaIzquierda : ").append(this.luzBajaIzquierda.length).append("--").toString());

      String statement = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
      PreparedStatement instruccion = this.conexion.prepareStatement(statement);
      instruccion.setInt(1, 2031);
      instruccion.setDouble(2, this.luzBajaIzquierda[0]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- INTENSIDAD : 2031 : ").append(this.luzBajaIzquierda[0]).toString());

      instruccion.setInt(1, 2044);
      instruccion.setDouble(2, this.anguloBajaIzquierda[0]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- ANGULO : 2044 : ").append(this.anguloBajaIzquierda[0]).toString());

      instruccion.setInt(1, 2030);
      instruccion.setDouble(2, this.luzBajaIzquierda[1]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- INTENSIDAD : 2030 : ").append(this.luzBajaIzquierda[1]).toString());

      instruccion.setInt(1, 2045);
      instruccion.setDouble(2, this.anguloBajaIzquierda[1]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- ANGULO : 2045 : ").append(this.anguloBajaIzquierda[1]).toString());

      instruccion.setInt(1, 2029);
      instruccion.setDouble(2, this.luzBajaIzquierda[2]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- INTENSIDAD : 2029 : ").append(this.luzBajaIzquierda[2]).toString());

      instruccion.setInt(1, 2046);
      instruccion.setDouble(2, this.anguloBajaIzquierda[2]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- ANGULO : 2046 : ").append(this.anguloBajaIzquierda[2]).toString());
    }
    catch (SQLException e) {
      System.out.println(new StringBuilder().append("Error en el metodo :registrarMedidasLuzBajaIzquierdaEje3()").append(e.getMessage()).toString());
      System.out.println(new StringBuilder().append("Error en el metodo :registrarMedidasLuzBajaIzquierdaEje3()").append(e.getStackTrace()).toString());
      System.out.println(new StringBuilder().append("Error en el metodo :registrarMedidasLuzBajaIzquierdaEje3()").append(e.getSQLState()).toString());
    }
  }

  private void registrarMedidasluzAltaDerechaEje3()
  {
    System.out.println("----------------------------------------------------");
    System.out.println("----------registrarMedidasluzAltaDerechaEje3 -----");
    System.out.println("----------------------------------------------------");
    try {
      this.lblObservacion = this.lblObservacion.concat(" ").concat("Far1 A.D.=").concat(String.valueOf(this.luzAltaDerecha[0])).concat(" Far2 A.D.=").concat(String.valueOf(this.luzAltaDerecha[1])).concat(" Far3 A.D.=").concat(String.valueOf(this.luzAltaDerecha[2]));
      this.farAltaDerecha = (this.luzAltaDerecha[0] <= this.luzAltaDerecha[1] ? this.luzAltaDerecha[0] : this.luzAltaDerecha[1]);
      if (this.luzAltaDerecha[2] <= this.farAltaDerecha) {
        this.farAltaDerecha = this.luzAltaDerecha[2];
      }

      String statement = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
      PreparedStatement instruccion = this.conexion.prepareStatement(statement);

      System.out.println(new StringBuilder().append("-- luzAltaDerecha : ").append(this.luzAltaDerecha.length).append("--").toString());

      instruccion.setInt(1, 2032);
      instruccion.setDouble(2, this.luzAltaDerecha[0]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- INTENSIDAD : 2032 : ").append(this.luzAltaDerecha[0]).toString());

      instruccion.setInt(1, 2033);
      instruccion.setDouble(2, this.luzAltaDerecha[1]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- INTENSIDAD : 2033 : ").append(this.luzAltaDerecha[1]).toString());

      instruccion.setInt(1, 2034);
      instruccion.setDouble(2, this.luzAltaDerecha[2]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- INTENSIDAD : 2034 : ").append(this.luzAltaDerecha[2]).toString());
    }
    catch (SQLException e) {
      System.out.println(new StringBuilder().append("Error en el metodo :registrarMedidasluzAltaDerechaEje3()").append(e.getMessage()).toString());
      System.out.println(new StringBuilder().append("Error en el metodo :registrarMedidasluzAltaDerechaEje3()").append(e.getStackTrace()).toString());
      System.out.println(new StringBuilder().append("Error en el metodo :registrarMedidasluzAltaDerechaEje3()").append(e.getSQLState()).toString());
    }
  }
  
  
  


  private void registrarMedidasLuzAltaIzquierdaEje3()
  {
    System.out.println("----------------------------------------------------");
    System.out.println("---------- registrarMedidasLuzAltaIzquierdaEje3 -----");
    System.out.println("----------------------------------------------------");
    try
    {
      this.lblObservacion = this.lblObservacion.concat(" ").concat("Far1 A.I.=").concat(String.valueOf(this.luzAltaIzquierda[0])).concat(" Far2 A.I.=").concat(String.valueOf(this.luzAltaIzquierda[1])).concat(" Far3 A.I.=").concat(String.valueOf(this.luzAltaIzquierda[2]));
      this.farAltaIzquierda = (this.luzAltaIzquierda[0] <= this.luzAltaIzquierda[1] ? this.luzAltaIzquierda[0] : this.luzAltaIzquierda[1]);
      if (this.luzAltaIzquierda[2] <= this.farAltaIzquierda) {
        this.farAltaIzquierda = this.luzAltaIzquierda[2];
      }
      String statement = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
      PreparedStatement instruccion = this.conexion.prepareStatement(statement);

      System.out.println(new StringBuilder().append("-- luzAltaIzquierda : ").append(this.luzAltaIzquierda.length).append("--").toString());

      instruccion.setInt(1, 2036);
      instruccion.setDouble(2, this.luzAltaIzquierda[0]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- INTENSIDAD : 2036 : ").append(this.luzAltaIzquierda[0]).toString());

      instruccion.setInt(1, 2037);
      instruccion.setDouble(2, this.luzAltaIzquierda[1]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- INTENSIDAD : 2037 : ").append(this.luzAltaIzquierda[1]).toString());

      instruccion.setInt(1, 2038);
      instruccion.setDouble(2, this.luzAltaIzquierda[2]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- INTENSIDAD : 2038 : ").append(this.luzAltaIzquierda[2]).toString());
    }
    catch (SQLException e) {
      System.out.println(new StringBuilder().append("Error en el metodo :registrarMedidasLuzAltaIzquierdaEje3()").append(e.getMessage()).toString());
      System.out.println(new StringBuilder().append("Error en el metodo :registrarMedidasLuzAltaIzquierdaEje3()").append(e.getStackTrace()).toString());
      System.out.println(new StringBuilder().append("Error en el metodo :registrarMedidasLuzAltaIzquierdaEje3()").append(e.getSQLState()).toString());
    }
  }

  private void registrarMedidasLuzBajaDerechaEje4()
  {
    System.out.println("----------------------------------------------------");
    System.out.println("---------- registrarMedidasLuzBajaDerechaEje4 ------");
    System.out.println("----------------------------------------------------");
    try
    {
      this.lblObservacion = this.lblObservacion.concat(" ").concat("Far1 B.D.=").concat(String.valueOf(this.luzBajaDerecha[0])).concat(" Far2 B.D.=").concat(String.valueOf(this.luzBajaDerecha[1])).concat(" Far3 B.D.=").concat(String.valueOf(this.luzBajaDerecha[2])).concat(" Far4 B.D.=").concat(String.valueOf(this.luzBajaDerecha[3])).concat(" Ang1. B.D.=").concat(String.valueOf(this.anguloBajaDerecha[0])).concat(" Ang2. B.D.=").concat(String.valueOf(this.anguloBajaDerecha[1])).concat(" Ang3. B.D.=").concat(String.valueOf(this.anguloBajaDerecha[2])).concat(" Ang4. B.D.=").concat(String.valueOf(this.anguloBajaDerecha[3]));
      this.farBajaDerecha = (this.luzBajaDerecha[0] <= this.luzBajaDerecha[1] ? this.luzBajaDerecha[0] : this.luzBajaDerecha[1]);

      if (this.luzBajaDerecha[2] <= this.farBajaDerecha) {
        this.farBajaDerecha = this.luzBajaDerecha[2];
      }

      if (this.luzBajaDerecha[3] <= this.farBajaDerecha) {
        this.farBajaDerecha = this.luzBajaDerecha[3];
      }

      boolean overErrorFar1 = false;
      boolean overErrorFar2 = false;
      boolean overErrorFar3 = false;
      boolean overErrorFar4 = false;
      double limInfer = 0.0D;
      double limSup = 0.0D;

      if ((this.anguloBajaDerecha[0] < this.permisibleAnguloBajo) || (this.anguloBajaDerecha[0] > this.permisibleAnguloAlto)) {
        overErrorFar1 = true;
      }
      if ((this.anguloBajaDerecha[1] < this.permisibleAnguloBajo) || (this.anguloBajaDerecha[1] > this.permisibleAnguloAlto)) {
        overErrorFar2 = true;
      }
      if ((this.anguloBajaDerecha[2] < this.permisibleAnguloBajo) || (this.anguloBajaDerecha[2] > this.permisibleAnguloAlto)) {
        overErrorFar3 = true;
      }
      if ((this.anguloBajaDerecha[3] < this.permisibleAnguloBajo) || (this.anguloBajaDerecha[3] > this.permisibleAnguloAlto)) {
        overErrorFar4 = true;
      }

      if ((overErrorFar1 == true) || (overErrorFar2 == true)) {
        if (overErrorFar1 == true) {
          this.angBajaDerecha = this.anguloBajaDerecha[0];
        }
        if (overErrorFar2 == true) {
          this.angBajaDerecha = this.anguloBajaDerecha[1];
        }
        if (overErrorFar3 == true) {
          this.angBajaDerecha = this.anguloBajaDerecha[2];
        }
        if (overErrorFar4 == true)
          this.angBajaDerecha = this.anguloBajaDerecha[3];
      }
      else {
        limInfer = Math.abs(this.anguloBajaDerecha[0] - this.permisibleAnguloBajo);
        limSup = Math.abs(this.anguloBajaDerecha[0] - this.permisibleAnguloAlto);
        double nearErrorFar1 = limInfer <= limSup ? limInfer : limSup;
        limInfer = Math.abs(this.anguloBajaDerecha[1] - this.permisibleAnguloBajo);
        limSup = Math.abs(this.anguloBajaDerecha[1] - this.permisibleAnguloAlto);
        double nearErrorFar2 = limInfer <= limSup ? limInfer : limSup;
        limInfer = Math.abs(this.anguloBajaDerecha[2] - this.permisibleAnguloBajo);
        limSup = Math.abs(this.anguloBajaDerecha[2] - this.permisibleAnguloAlto);
        double nearErrorFar3 = limInfer <= limSup ? limInfer : limSup;
        limInfer = Math.abs(this.anguloBajaDerecha[3] - this.permisibleAnguloBajo);
        limSup = Math.abs(this.anguloBajaDerecha[3] - this.permisibleAnguloAlto);
        double nearErrorFar4 = limInfer <= limSup ? limInfer : limSup;
        String menor = nearErrorFar1 <= nearErrorFar2 ? "baj1" : "baj2";
        if (menor.equalsIgnoreCase("baj1")) {
          if (nearErrorFar3 <= nearErrorFar1) {
            menor = nearErrorFar3 <= nearErrorFar4 ? "baj3" : "baj4";
            if (menor.equalsIgnoreCase("baj3"))
              this.angBajaDerecha = this.anguloBajaDerecha[2];
            else
              this.angBajaDerecha = this.anguloBajaDerecha[3];
          }
          else {
            this.angBajaDerecha = this.anguloBajaDerecha[0];
          }
        }
        if (menor.equalsIgnoreCase("baj2")) {
          if (nearErrorFar3 <= nearErrorFar2) {
            menor = nearErrorFar3 <= nearErrorFar4 ? "baj3" : "baj4";
            if (menor.equalsIgnoreCase("baj3"))
              this.angBajaDerecha = this.anguloBajaDerecha[2];
            else
              this.angBajaDerecha = this.anguloBajaDerecha[3];
          }
          else {
            this.angBajaDerecha = this.anguloBajaDerecha[1];
          }
        }
      }

      String statement = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
      PreparedStatement instruccion = this.conexion.prepareStatement(statement);

      System.out.println(new StringBuilder().append("-- luzBajaDerecha : ").append(this.luzBajaDerecha.length).append("--").toString());

      instruccion.setInt(1, 2024);
      instruccion.setDouble(2, this.luzBajaDerecha[0]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- INTENSIDAD     : 2024 : ").append(this.luzBajaDerecha[0]).toString());

      instruccion.setInt(1, 2040);
      instruccion.setDouble(2, this.anguloBajaDerecha[0]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- ANGULO  : 2040 : ").append(this.anguloBajaDerecha[0]).toString());

      instruccion.setInt(1, 2025);
      instruccion.setDouble(2, this.luzBajaDerecha[1]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- INTENSIDAD     : 2025 : ").append(this.luzBajaDerecha[1]).toString());

      instruccion.setInt(1, 2041);
      instruccion.setDouble(2, this.anguloBajaDerecha[1]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- ANGULO   : 2041 : ").append(this.anguloBajaDerecha[1]).toString());

      instruccion.setInt(1, 2026);
      instruccion.setDouble(2, this.luzBajaDerecha[2]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- INTENSIDAD   : 2026 : ").append(this.luzBajaDerecha[2]).toString());

      instruccion.setInt(1, 2042);
      instruccion.setDouble(2, this.anguloBajaDerecha[2]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- ANGULO   : 2042 : ").append(this.anguloBajaDerecha[2]).toString());

      instruccion.setInt(1, 2027);
      instruccion.setDouble(2, this.luzBajaDerecha[3]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- INTENSIDAD   : 2027 : ").append(this.luzBajaDerecha[3]).toString());

      instruccion.setInt(1, 2043);
      instruccion.setDouble(2, this.anguloBajaDerecha[3]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- ANGULO   : 2043 : ").append(this.anguloBajaDerecha[3]).toString());
    }
    catch (SQLException e) {
      System.out.println(new StringBuilder().append("Error en el metodo :registrarMedidasLuzBajaDerechaEje4()").append(e.getMessage()).toString());
      System.out.println(new StringBuilder().append("Error en el metodo :registrarMedidasLuzBajaDerechaEje4()").append(e.getStackTrace()).toString());
      System.out.println(new StringBuilder().append("Error en el metodo :registrarMedidasLuzBajaDerechaEje4()").append(e.getSQLState()).toString());
    }
  }

  private void registrarMedidasLuzBajaIzquierdaEje4()
  {
    System.out.println("----------------------------------------------------");
    System.out.println("----------registrarMedidasLuzBajaIzquierdaEje4 -----");
    System.out.println("----------------------------------------------------");
    try
    {
      this.lblObservacion = this.lblObservacion.concat(" ").concat("Far1 B.I.=").concat(String.valueOf(this.luzBajaIzquierda[0])).concat(" Far2 B.I.=").concat(String.valueOf(this.luzBajaIzquierda[1])).concat(" Far3 B.I.=").concat(String.valueOf(this.luzBajaIzquierda[2])).concat(" Far4 B.I.=").concat(String.valueOf(this.luzBajaIzquierda[3])).concat(" Ang1. B.D.=").concat(String.valueOf(this.anguloBajaIzquierda[0])).concat(" Ang2. B.D.=").concat(String.valueOf(this.anguloBajaIzquierda[1])).concat(" Ang3. B.D.=").concat(String.valueOf(this.anguloBajaIzquierda[2])).concat(" Ang4. B.D.=").concat(String.valueOf(this.anguloBajaIzquierda[3]));
      this.farBajaIzquierda = (this.luzBajaIzquierda[0] <= this.luzBajaIzquierda[1] ? this.luzBajaIzquierda[0] : this.luzBajaIzquierda[1]);

      if (this.luzBajaIzquierda[2] <= this.farBajaIzquierda) {
        this.farBajaIzquierda = this.luzBajaIzquierda[2];
      }

      if (this.luzBajaIzquierda[3] <= this.farBajaIzquierda) {
        this.farBajaIzquierda = this.luzBajaIzquierda[3];
      }

      boolean overErrorFar1 = false;
      boolean overErrorFar2 = false;
      boolean overErrorFar3 = false;
      boolean overErrorFar4 = false;
      double limInfer = 0.0D;
      double limSup = 0.0D;

      if ((this.anguloBajaIzquierda[0] < this.permisibleAnguloBajo) || (this.anguloBajaIzquierda[0] > this.permisibleAnguloAlto)) {
        overErrorFar1 = true;
      }
      if ((this.anguloBajaIzquierda[1] < this.permisibleAnguloBajo) || (this.anguloBajaIzquierda[1] > this.permisibleAnguloAlto)) {
        overErrorFar2 = true;
      }
      if ((this.anguloBajaIzquierda[2] < this.permisibleAnguloBajo) || (this.anguloBajaIzquierda[2] > this.permisibleAnguloAlto)) {
        overErrorFar3 = true;
      }
      if ((this.anguloBajaIzquierda[3] < this.permisibleAnguloBajo) || (this.anguloBajaIzquierda[3] > this.permisibleAnguloAlto)) {
        overErrorFar4 = true;
      }

      if ((overErrorFar1 == true) || (overErrorFar2 == true)) {
        if (overErrorFar1 == true) {
          this.angBajaIzquierda = this.anguloBajaIzquierda[0];
        }
        if (overErrorFar2 == true) {
          this.angBajaIzquierda = this.anguloBajaIzquierda[1];
        }
        if (overErrorFar3 == true) {
          this.angBajaIzquierda = this.anguloBajaIzquierda[2];
        }
        if (overErrorFar4 == true)
          this.angBajaIzquierda = this.anguloBajaIzquierda[3];
      }
      else {
        limInfer = Math.abs(this.anguloBajaIzquierda[0] - this.permisibleAnguloBajo);
        limSup = Math.abs(this.anguloBajaIzquierda[0] - this.permisibleAnguloAlto);
        double nearErrorFar1 = limInfer <= limSup ? limInfer : limSup;
        limInfer = Math.abs(this.anguloBajaIzquierda[1] - this.permisibleAnguloBajo);
        limSup = Math.abs(this.anguloBajaIzquierda[1] - this.permisibleAnguloAlto);
        double nearErrorFar2 = limInfer <= limSup ? limInfer : limSup;
        limInfer = Math.abs(this.anguloBajaIzquierda[2] - this.permisibleAnguloBajo);
        limSup = Math.abs(this.anguloBajaIzquierda[2] - this.permisibleAnguloAlto);
        double nearErrorFar3 = limInfer <= limSup ? limInfer : limSup;
        limInfer = Math.abs(this.anguloBajaIzquierda[3] - this.permisibleAnguloBajo);
        limSup = Math.abs(this.anguloBajaIzquierda[3] - this.permisibleAnguloAlto);
        double nearErrorFar4 = limInfer <= limSup ? limInfer : limSup;
        String menor = nearErrorFar1 <= nearErrorFar2 ? "baj1" : "baj2";
        if (menor.equalsIgnoreCase("baj1")) {
          if (nearErrorFar3 <= nearErrorFar1) {
            menor = nearErrorFar3 <= nearErrorFar4 ? "baj3" : "baj4";
            if (menor.equalsIgnoreCase("baj3"))
              this.angBajaIzquierda = this.anguloBajaIzquierda[2];
            else
              this.angBajaIzquierda = this.anguloBajaIzquierda[3];
          }
          else {
            this.angBajaIzquierda = this.anguloBajaIzquierda[0];
          }
        }
        if (menor.equalsIgnoreCase("baj2")) {
          if (nearErrorFar3 <= nearErrorFar2) {
            menor = nearErrorFar3 <= nearErrorFar4 ? "baj3" : "baj4";
            if (menor.equalsIgnoreCase("baj3"))
              this.angBajaIzquierda = this.anguloBajaIzquierda[2];
            else
              this.angBajaIzquierda = this.anguloBajaIzquierda[3];
          }
          else {
            this.angBajaIzquierda = this.anguloBajaIzquierda[1];
          }
        }
      }
      String statement = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
      PreparedStatement instruccion = this.conexion.prepareStatement(statement);

      System.out.println(new StringBuilder().append("-- luzBajaIzquierda : ").append(this.luzBajaIzquierda.length).append("--").toString());

      instruccion.setInt(1, 2031);
      instruccion.setDouble(2, this.luzBajaIzquierda[0]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- INTENSIDAD     : 2031 : ").append(this.luzBajaIzquierda[0]).toString());

      instruccion.setInt(1, 2044);
      instruccion.setDouble(2, this.anguloBajaIzquierda[0]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- ANGULO     : 2044 : ").append(this.anguloBajaIzquierda[0]).toString());

      instruccion.setInt(1, 2030);
      instruccion.setDouble(2, this.luzBajaIzquierda[1]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- INTENSIDAD  : 2030 : ").append(this.luzBajaIzquierda[1]).toString());

      instruccion.setInt(1, 2045);
      instruccion.setDouble(2, this.anguloBajaIzquierda[1]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- ANGULO  : 2045 : ").append(this.anguloBajaIzquierda[1]).toString());

      instruccion.setInt(1, 2029);
      instruccion.setDouble(2, this.luzBajaIzquierda[2]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- INTENSIDAD  : 2029 : ").append(this.luzBajaIzquierda[2]).toString());

      instruccion.setInt(1, 2046);
      instruccion.setDouble(2, this.anguloBajaIzquierda[2]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- ANGULO  : 2046 : ").append(this.anguloBajaIzquierda[2]).toString());

      instruccion.setInt(1, 2028);
      instruccion.setDouble(2, this.luzBajaIzquierda[3]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- INTENSIDAD  : 2028 : ").append(this.luzBajaIzquierda[3]).toString());

      instruccion.setInt(1, 2047);
      instruccion.setDouble(2, this.anguloBajaIzquierda[3]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- ANGULO  : 2047 : ").append(this.anguloBajaIzquierda[3]).toString());
    }
    catch (SQLException e) {
      System.out.println(new StringBuilder().append("Error en el metodo :registrarMedidasLuzBajaIzquierdaEje4()").append(e.getMessage()).toString());
      System.out.println(new StringBuilder().append("Error en el metodo :registrarMedidasLuzBajaIzquierdaEje4()").append(e.getStackTrace()).toString());
      System.out.println(new StringBuilder().append("Error en el metodo :registrarMedidasLuzBajaIzquierdaEje4()").append(e.getSQLState()).toString());
    }
  }

  private void registrarMedidasLuzAltaDerechaEje4()
  {
    System.out.println("----------------------------------------------------");
    System.out.println("----------registrarMedidasLuzAltaDerechaEje4 -------");
    System.out.println("----------------------------------------------------");
    try
    {
      this.lblObservacion = this.lblObservacion.concat(" ").concat("Far1 A.D.=").concat(String.valueOf(this.luzAltaDerecha[0])).concat(" Far2 A.D.=").concat(String.valueOf(this.luzAltaDerecha[1])).concat(" Far3 A.D.=").concat(String.valueOf(this.luzAltaDerecha[2])).concat(" Far4 A.D.=").concat(String.valueOf(this.luzAltaDerecha[3]));
      this.farAltaDerecha = (this.luzAltaDerecha[0] <= this.luzAltaDerecha[1] ? this.luzAltaDerecha[0] : this.luzAltaDerecha[1]);
      if (this.luzAltaDerecha[2] <= this.farAltaDerecha) {
        this.farAltaDerecha = this.luzAltaDerecha[2];
      }
      if (this.luzAltaDerecha[3] <= this.farAltaDerecha) {
        this.farAltaDerecha = this.luzAltaDerecha[3];
      }
      String statement = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
      PreparedStatement instruccion = this.conexion.prepareStatement(statement);

      System.out.println(new StringBuilder().append("-- luzAltaDerecha : ").append(this.luzAltaDerecha.length).append("--").toString());

      instruccion.setInt(1, 2036);
      instruccion.setDouble(2, this.luzAltaDerecha[0]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- INTENSIDAD     : 2036 : ").append(this.luzAltaDerecha[0]).toString());

      instruccion.setInt(1, 2037);
      instruccion.setDouble(2, this.luzAltaDerecha[1]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- INTENSIDAD     : 2037 : ").append(this.luzAltaDerecha[1]).toString());

      instruccion.setInt(1, 2038);
      instruccion.setDouble(2, this.luzAltaDerecha[2]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- INTENSIDAD     : 2038 : ").append(this.luzAltaDerecha[2]).toString());

      instruccion.setInt(1, 2039);
      instruccion.setDouble(2, this.luzAltaDerecha[3]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- INTENSIDAD     : 2039 : ").append(this.luzAltaDerecha[3]).toString());
    }
    catch (SQLException e) {
      System.out.println(new StringBuilder().append("Error en el metodo :registrarMedidasLuzAltaDerechaEje4()").append(e.getMessage()).toString());
      System.out.println(new StringBuilder().append("Error en el metodo :registrarMedidasLuzAltaDerechaEje4 ()").append(e.getStackTrace()).toString());
      System.out.println(new StringBuilder().append("Error en el metodo :registrarMedidasLuzAltaDerechaEje4 ()").append(e.getSQLState()).toString());
    }
  }

  private void registrarMedidasLuzAltaIzquierdaEje4()
  {
    System.out.println("----------------------------------------------------");
    System.out.println("----------registrarMedidasLuzAltaIzquierdaEje ------");
    System.out.println("----------------------------------------------------");
    try
    {
      this.lblObservacion = this.lblObservacion.concat(" ").concat("Far1 A.I.=").concat(String.valueOf(this.luzAltaIzquierda[0])).concat(" Far2 A.I.=").concat(String.valueOf(this.luzAltaIzquierda[1])).concat(" Far3 A.I.=").concat(String.valueOf(this.luzAltaIzquierda[2])).concat(" Far4 A.I.=").concat(String.valueOf(this.luzAltaIzquierda[3]));
      this.farAltaIzquierda = (this.luzAltaIzquierda[0] <= this.luzAltaIzquierda[1] ? this.luzAltaIzquierda[0] : this.luzAltaIzquierda[1]);
      if (this.luzAltaIzquierda[2] <= this.farAltaIzquierda) {
        this.farAltaIzquierda = this.luzAltaIzquierda[2];
      }
      if (this.luzAltaIzquierda[3] <= this.farAltaIzquierda) {
        this.farAltaIzquierda = this.luzAltaIzquierda[3];
      }
      String statement = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
      PreparedStatement instruccion = this.conexion.prepareStatement(statement);

      System.out.println(new StringBuilder().append("-- luzAltaIzquierda : ").append(this.luzAltaIzquierda.length).append("--").toString());

      instruccion.setInt(1, 2032);
      instruccion.setDouble(2, this.luzAltaIzquierda[0]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- INTESIDAD : 2032 : ").append(this.luzAltaIzquierda[0]).toString());

      instruccion.setInt(1, 2033);
      instruccion.setDouble(2, this.luzAltaIzquierda[1]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- INTESIDAD : 2033 : ").append(this.luzAltaIzquierda[1]).toString());

      instruccion.setInt(1, 2034);
      instruccion.setDouble(2, this.luzAltaIzquierda[2]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- INTESIDAD : 2034 : ").append(this.luzAltaIzquierda[2]).toString());

      instruccion.setInt(1, 2035);
      instruccion.setDouble(2, this.luzAltaIzquierda[3]);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.executeUpdate();
      instruccion.clearParameters();
      System.out.println(new StringBuilder().append("-- INTESIDAD : 2035 : ").append(this.luzAltaIzquierda[3]).toString());
    }
    catch (SQLException e) {
      System.out.println(new StringBuilder().append("Error en el metodo :registrarMedidasLuzAltaIzquierdaEje()").append(e.getMessage()).toString());
      System.out.println(new StringBuilder().append("Error en el metodo :registrarMedidasLuzAltaIzquierdaEje()").append(e.getStackTrace()).toString());
      System.out.println(new StringBuilder().append("Error en el metodo :registrarMedidasLuzAltaIzquierdaEje()").append(e.getSQLState()).toString());
    }
  }

  private void registrarMedidasExploradoras()
  {
    System.out.println("----------------------------------------------------");
    System.out.println("----------registrarMedidasExploradoras---------------");
    System.out.println("----------------------------------------------------");
    try
    {
      if (this.medidasExploradoras.length == 1)
      {
        System.out.println(new StringBuilder().append("-- medidasExploradoras : ").append(this.medidasExploradoras.length).append("--").toString());
        String statement = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
        PreparedStatement instruccion = this.conexion.prepareStatement(statement);
        instruccion.setInt(1, 2050);
        instruccion.setDouble(2, this.medidasExploradoras[0]);
        instruccion.setLong(3, this.idPrueba.longValue());
        instruccion.executeUpdate();
        instruccion.clearParameters();
        System.out.println(new StringBuilder().append("-- INTENSIDAD : 2050 : ").append(this.medidasExploradoras[0]).toString());
      }
      if (this.medidasExploradoras.length == 2)
      {
        System.out.println(new StringBuilder().append("-- medidasExploradoras : ").append(this.medidasExploradoras.length).append("--").toString());

        String statement = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
        PreparedStatement instruccion = this.conexion.prepareStatement(statement);
        instruccion.setInt(1, 2050);
        instruccion.setDouble(2, this.medidasExploradoras[0]);
        instruccion.setLong(3, this.idPrueba.longValue());
        instruccion.executeUpdate();
        instruccion.clearParameters();
        System.out.println(new StringBuilder().append("-- INTENSIDAD : 2050 : ").append(this.medidasExploradoras[0]).toString());

        instruccion.setInt(1, 2053);
        instruccion.setDouble(2, this.medidasExploradoras[1]);
        instruccion.setLong(3, this.idPrueba.longValue());
        instruccion.executeUpdate();
        System.out.println(new StringBuilder().append("-- INTENSIDAD : 2053 : ").append(this.medidasExploradoras[1]).toString());
      }
      if (this.medidasExploradoras.length == 3)
      {
        String statement = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
        PreparedStatement instruccion = this.conexion.prepareStatement(statement);

        System.out.println(new StringBuilder().append("-- medidasExploradoras : ").append(this.medidasExploradoras.length).append("--").toString());

        instruccion.setInt(1, 2050);
        instruccion.setDouble(2, this.medidasExploradoras[0]);
        instruccion.setLong(3, this.idPrueba.longValue());
        instruccion.executeUpdate();
        instruccion.clearParameters();
        System.out.println(new StringBuilder().append("-- INTENSIDAD : 2050 : ").append(this.medidasExploradoras[0]).toString());

        instruccion.setInt(1, 2051);
        instruccion.setDouble(2, this.medidasExploradoras[1]);
        instruccion.setLong(3, this.idPrueba.longValue());
        instruccion.executeUpdate();
        instruccion.clearParameters();
        System.out.println(new StringBuilder().append("-- INTENSIDAD : 2051 : ").append(this.medidasExploradoras[1]).toString());

        instruccion.setInt(1, 2053);
        instruccion.setDouble(2, this.medidasExploradoras[2]);
        instruccion.setLong(3, this.idPrueba.longValue());
        instruccion.executeUpdate();
        System.out.println(new StringBuilder().append("-- INTENSIDAD : 2053 : ").append(this.medidasExploradoras[2]).toString());
      }
      if (this.medidasExploradoras.length == 4)
      {
        System.out.println(new StringBuilder().append("-- medidasExploradoras : ").append(this.medidasExploradoras.length).append("--").toString());

        String statement = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
        PreparedStatement instruccion = this.conexion.prepareStatement(statement);
        instruccion.setInt(1, 2050);
        instruccion.setDouble(2, this.medidasExploradoras[0]);
        instruccion.setLong(3, this.idPrueba.longValue());
        instruccion.executeUpdate();
        instruccion.clearParameters();
        System.out.println(new StringBuilder().append("-- INTENSIDAD : 2050 : ").append(this.medidasExploradoras[0]).toString());

        instruccion.setInt(1, 2051);
        instruccion.setDouble(2, this.medidasExploradoras[1]);
        instruccion.setLong(3, this.idPrueba.longValue());
        instruccion.executeUpdate();
        instruccion.clearParameters();
        System.out.println(new StringBuilder().append("-- INTENSIDAD : 2051 : ").append(this.medidasExploradoras[1]).toString());

        instruccion.setInt(1, 2053);
        instruccion.setDouble(2, this.medidasExploradoras[2]);
        instruccion.setLong(3, this.idPrueba.longValue());
        instruccion.executeUpdate();
        instruccion.clearParameters();
        System.out.println(new StringBuilder().append("-- INTENSIDAD : 2053 : ").append(this.medidasExploradoras[2]).toString());

        instruccion.setInt(1, 2054);
        instruccion.setDouble(2, this.medidasExploradoras[3]);
        instruccion.setLong(3, this.idPrueba.longValue());
        instruccion.executeUpdate();
        System.out.println(new StringBuilder().append("-- INTENSIDAD : 2054 : ").append(this.medidasExploradoras[3]).toString());
      }
      if (this.medidasExploradoras.length == 5)
      {
        System.out.println(new StringBuilder().append("-- medidasExploradoras : ").append(this.medidasExploradoras.length).append("--").toString());

        String statement = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
        PreparedStatement instruccion = this.conexion.prepareStatement(statement);
        instruccion.setInt(1, 2050);
        instruccion.setDouble(2, this.medidasExploradoras[0]);
        instruccion.setLong(3, this.idPrueba.longValue());
        instruccion.executeUpdate();
        instruccion.clearParameters();
        System.out.println(new StringBuilder().append("-- INTENSIDAD     : 2050  : ").append(this.medidasExploradoras[0]).toString());

        instruccion.setInt(1, 2051);
        instruccion.setDouble(2, this.medidasExploradoras[1]);
        instruccion.setLong(3, this.idPrueba.longValue());
        instruccion.executeUpdate();
        instruccion.clearParameters();
        System.out.println(new StringBuilder().append("-- INTENSIDAD     : 2051  : ").append(this.medidasExploradoras[1]).toString());

        instruccion.setInt(1, 2052);
        instruccion.setDouble(2, this.medidasExploradoras[2]);
        instruccion.setLong(3, this.idPrueba.longValue());
        instruccion.executeUpdate();
        instruccion.clearParameters();
        System.out.println(new StringBuilder().append("-- INTENSIDAD     : 2052  : ").append(this.medidasExploradoras[2]).toString());

        instruccion.setInt(1, 2053);
        instruccion.setDouble(2, this.medidasExploradoras[3]);
        instruccion.setLong(3, this.idPrueba.longValue());
        instruccion.executeUpdate();
        instruccion.clearParameters();
        System.out.println(new StringBuilder().append("-- INTENSIDAD     : 2053  : ").append(this.medidasExploradoras[3]).toString());

        instruccion.setInt(1, 2054);
        instruccion.setDouble(2, this.medidasExploradoras[4]);
        instruccion.setLong(3, this.idPrueba.longValue());
        instruccion.executeUpdate();
        System.out.println(new StringBuilder().append("-- INTENSIDAD     : 2054  : ").append(this.medidasExploradoras[4]).toString());
      }

      if (this.medidasExploradoras.length == 6)
      {
        String statement = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
        PreparedStatement instruccion = this.conexion.prepareStatement(statement);

        System.out.println(new StringBuilder().append("-- medidasExploradoras : ").append(this.medidasExploradoras.length).append("--").toString());

        instruccion.setInt(1, 2050);
        instruccion.setDouble(2, this.medidasExploradoras[0]);
        instruccion.setLong(3, this.idPrueba.longValue());
        instruccion.executeUpdate();
        instruccion.clearParameters();
        System.out.println(new StringBuilder().append("-- INTENSIDAD     : 2050 : ").append(this.medidasExploradoras[0]).toString());

        instruccion.setInt(1, 2051);
        instruccion.setDouble(2, this.medidasExploradoras[1]);
        instruccion.setLong(3, this.idPrueba.longValue());
        instruccion.executeUpdate();
        instruccion.clearParameters();
        System.out.println(new StringBuilder().append("-- INTENSIDAD     : 2051 : ").append(this.medidasExploradoras[1]).toString());

        instruccion.setInt(1, 2052);
        instruccion.setDouble(2, this.medidasExploradoras[2]);
        instruccion.setLong(3, this.idPrueba.longValue());
        instruccion.executeUpdate();
        instruccion.clearParameters();
        System.out.println(new StringBuilder().append("-- INTENSIDAD     : 2052 : ").append(this.medidasExploradoras[2]).toString());

        instruccion.setInt(1, 2053);
        instruccion.setDouble(2, this.medidasExploradoras[3]);
        instruccion.setLong(3, this.idPrueba.longValue());
        instruccion.executeUpdate();
        instruccion.clearParameters();
        System.out.println(new StringBuilder().append("-- INTENSIDAD     : 2053 : ").append(this.medidasExploradoras[3]).toString());

        instruccion.setInt(1, 2054);
        instruccion.setDouble(2, this.medidasExploradoras[4]);
        instruccion.setLong(3, this.idPrueba.longValue());
        instruccion.executeUpdate();
        instruccion.clearParameters();
        System.out.println(new StringBuilder().append("-- INTENSIDAD     : 2054 : ").append(this.medidasExploradoras[4]).toString());

        instruccion.setInt(1, 2055);
        instruccion.setDouble(2, this.medidasExploradoras[5]);
        instruccion.setLong(3, this.idPrueba.longValue());
        instruccion.executeUpdate();
        System.out.println(new StringBuilder().append("-- INTENSIDAD     : 2055 : ").append(this.medidasExploradoras[5]).toString());
      }
    }
    catch (SQLException e)
    {
      System.out.println(new StringBuilder().append("Error en el metodo :registrarMedidasExploradoras()").append(e.getMessage()).toString());
      System.out.println(new StringBuilder().append("Error en el metodo :registrarMedidasExploradoras()").append(e.getStackTrace()).toString());
      System.out.println(new StringBuilder().append("Error en el metodo :registrarMedidasExploradoras()").append(e.getSQLState()).toString());
    }
  }

  private boolean insertarPermisibles(double mayorSuma)
  {
    boolean aprobada = true;

    System.out.println("----------------------------------------------------");
    System.out.println("---------- insertarPermisibles ---------------------");
    System.out.println("----------------------------------------------------");
    try
    {
      String str2 = "INSERT INTO defxprueba(id_defecto,id_prueba,Tipo_defecto) VALUES (?,?,'A')";
      PreparedStatement psDefectos = this.conexion.prepareStatement(str2);

      if ((this.farBajaDerecha < this.permisibleBaja) || (this.farBajaIzquierda < this.permisibleBaja)) {
        
        
        if (aplicTrans == 0) {
          int codigo = tipoVehiculo.equalsIgnoreCase("CICLOMOTOR") ? 21006 : 20000;
          psDefectos.clearParameters();
          psDefectos.setInt(1, codigo);
          psDefectos.setLong(2, this.idPrueba.longValue());
          psDefectos.executeUpdate();
        }
        aprobada = false;
      }

      if ((this.angBajaDerecha < this.permisibleAnguloBajo) || (this.angBajaDerecha > this.permisibleAnguloAlto)) {
        if (aplicTrans == 0) {
          int codigo = tipoVehiculo.equalsIgnoreCase("CICLOMOTOR") ? 21002 : 20002;
          psDefectos.clearParameters();
          psDefectos.setInt(1, codigo);
          psDefectos.setLong(2, this.idPrueba.longValue());
          psDefectos.executeUpdate();
        }
        aprobada = false;
      } else if ((this.angBajaIzquierda < this.permisibleAnguloBajo) || (this.angBajaIzquierda > this.permisibleAnguloAlto)) {
        if (aplicTrans == 0) {
          int codigo = tipoVehiculo.equalsIgnoreCase("CICLOMOTOR") ? 21002 : 20002;
          psDefectos.clearParameters();
          psDefectos.setInt(1, codigo);
          psDefectos.setLong(2, this.idPrueba.longValue());
          psDefectos.executeUpdate();
        }
        aprobada = false;
      }

      if (mayorSuma > this.permisibleSumatoria)
      {
        if (aplicTrans == 0) {
          int codigo = tipoVehiculo.equalsIgnoreCase("CICLOMOTOR") ? 21005 : 20001;
          psDefectos.clearParameters();
          psDefectos.setInt(1, codigo);
          psDefectos.setLong(2, this.idPrueba.longValue());
          psDefectos.executeUpdate();
        }
        aprobada = false;
      }
    }
    catch (SQLException e) {
      System.out.println(new StringBuilder().append("Error en el metodo :insertarPermisibles()").append(e.getMessage()).toString());
      System.out.println(new StringBuilder().append("Error en el metodo :insertarPermisibles()").append(e.getStackTrace()).toString());
      System.out.println(new StringBuilder().append("Error en el metodo :insertarPermisibles()").append(e.getSQLState()).toString());
    }

    return aprobada;
  }

  private void registrarMedidasSimultaneas(double mayorSuma, String[] simultaneas)
  {
    System.out.println("----------------------------------------------------");
    System.out.println("---------- registrarMedidasSimultaneas -------------");
    System.out.println("----------------------------------------------------");
    try
    {
      System.out.println("Voy a intentar registrar simultaneas");
      String statement = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST,Simult) VALUES(?,?,?,?)";
      PreparedStatement instruccion = this.conexion.prepareStatement(statement);

      instruccion.setInt(1, 2008);
      System.out.println("valor suma:" + sumaExploradoras);
      instruccion.setDouble(2, this.sumaExploradoras);
      System.out.println("valor suma:" + this.idPrueba.longValue());

      instruccion.setLong(3, this.idPrueba.longValue());
        System.out.println("simultaneas : " + simultaneas[0]) ;
      instruccion.setString(4, simultaneas[0]);
      instruccion.executeUpdate();
      instruccion.clearParameters();

      instruccion.setInt(1, 2011);
      instruccion.setDouble(2, mayorSuma);
      instruccion.setLong(3, this.idPrueba.longValue());
      instruccion.setNull(4, 12);
      instruccion.executeUpdate();
      instruccion.clearParameters();
        System.out.println("antes de simultaneas ----------------------------------------");
      String statement5 = "UPDATE medidas set Simult = ?  where MEASURETYPE= ? and TEST =?";
      PreparedStatement instruccion5 = this.conexion.prepareStatement(statement5);
      instruccion5.setString(1, simultaneas[1]);
      instruccion5.setInt(2, 2024);
      instruccion5.setLong(3, this.idPrueba.longValue());
      instruccion5.executeUpdate();
      instruccion5.clearParameters();
        System.out.println("despues de simultaneas ----------------------------------------");

      instruccion5.setString(1, simultaneas[2]);
      instruccion5.setInt(2, 2025);
      instruccion5.setLong(3, this.idPrueba.longValue());
      instruccion5.executeUpdate();
      instruccion5.clearParameters();

      instruccion5.setString(1, simultaneas[3]);
      instruccion5.setInt(2, 2026);
      instruccion5.setLong(3, this.idPrueba.longValue());
      instruccion5.executeUpdate();
      instruccion5.clearParameters();

      instruccion5.setString(1, simultaneas[4]);
      instruccion5.setInt(2, 2027);
      instruccion5.setLong(3, this.idPrueba.longValue());
      instruccion5.executeUpdate();
      instruccion5.clearParameters();

      instruccion5.setString(1, simultaneas[5]);
      instruccion5.setInt(2, 2031);
      instruccion5.setLong(3, this.idPrueba.longValue());
      instruccion5.executeUpdate();
      instruccion5.clearParameters();

      instruccion5.setString(1, simultaneas[6]);
      instruccion5.setInt(2, 2030);
      instruccion5.setLong(3, this.idPrueba.longValue());
      instruccion5.executeUpdate();
      instruccion5.clearParameters();
      instruccion5.setString(1, simultaneas[7]);
      instruccion5.setInt(2, 2029);
      instruccion5.setLong(3, this.idPrueba.longValue());
      instruccion5.executeUpdate();
      instruccion5.clearParameters();
      instruccion5.setString(1, simultaneas[8]);
      instruccion5.setInt(2, 2028);
      instruccion5.setLong(3, this.idPrueba.longValue());
      instruccion5.executeUpdate();
      instruccion5.clearParameters();

      instruccion5.setString(1, simultaneas[9]);
      instruccion5.setInt(2, 2032);
      instruccion5.setLong(3, this.idPrueba.longValue());
      instruccion5.executeUpdate();
      instruccion5.clearParameters();
      instruccion5.setString(1, simultaneas[10]);
      instruccion5.setInt(2, 2033);
      instruccion5.setLong(3, this.idPrueba.longValue());
      instruccion5.executeUpdate();
      instruccion5.clearParameters();
      instruccion5.setString(1, simultaneas[11]);
      instruccion5.setInt(2, 2034);
      instruccion5.setLong(3, this.idPrueba.longValue());
      instruccion5.executeUpdate();
      instruccion5.clearParameters();
      instruccion5.setString(1, simultaneas[12]);
      instruccion5.setInt(2, 2035);
      instruccion5.setLong(3, this.idPrueba.longValue());
      instruccion5.executeUpdate();
      instruccion5.clearParameters();

      instruccion5.setString(1, simultaneas[13]);
      instruccion5.setInt(2, 2036);
      instruccion5.setLong(3, this.idPrueba.longValue());
      instruccion5.executeUpdate();
      instruccion5.clearParameters();
      instruccion5.setString(1, simultaneas[14]);
      instruccion5.setInt(2, 2037);
      instruccion5.setLong(3, this.idPrueba.longValue());
      instruccion5.executeUpdate();
      instruccion5.clearParameters();
      instruccion5.setString(1, simultaneas[15]);
      instruccion5.setInt(2, 2038);
      instruccion5.setLong(3, this.idPrueba.longValue());
      instruccion5.executeUpdate();
      instruccion5.clearParameters();
      instruccion5.setString(1, simultaneas[16]);
      instruccion5.setInt(2, 2039);
      instruccion5.setLong(3, this.idPrueba.longValue());
      instruccion5.executeUpdate();
      instruccion5.clearParameters();
    }
    catch (Exception e) {
        System.out.println("error: " + e.getMessage());
      e.getStackTrace();
    }
  }

  private void registrarMedidas(Long idPrueba, long idUsuario, int aplicTrans, String ipEquipo)
  {
    System.out.println("----------------------------------------------------");
    System.out.println("-------------------REGISTRAR MEDIDAS----------------");
    System.out.println("----------------------------------------------------");

    this.permisibleBaja = 2.5;
    this.permisibleAnguloAlto = 3.5;
    this.permisibleAnguloBajo = 0.5;
    this.permisibleSumatoria = 225;
    try
    {
      this.conexion = Conex.getConnection();
      this.conexion.setAutoCommit(false);

      if (this.luzBajaDerecha.length == 1)
      {
        registrarMedidasluzBajaDerecha();
      }

      if (this.luzBajaIzquierda.length == 1)
      {
        registrarLuzBajaIzquierda();
      }

      if (this.luzAltaIzquierda.length == 1)
      {
        registrarMedidasluzAltaIzquierda();
      }

      if (this.luzAltaDerecha.length == 1)
      {
        registrarMedidasluzAltaDerecha();
      }

      if (this.luzBajaDerecha.length == 2)
      {
        registrarMedidasluzBajaDerechaeje2();
      }

      if (this.luzBajaIzquierda.length == 2)
      {
        registrarMedidasluzBajaIzquierdaEje2();
      }

      if (this.luzAltaDerecha.length == 2)
      {
        registrarMedidasluzAltaDerechaEje2();
      }

      if (this.luzAltaIzquierda.length == 2)
      {
        registrarMedidasluzAltaIzquierdaEje2();
      }

      if (this.luzBajaDerecha.length == 3)
      {
        registrarMedidasluzBajaDerechaEje3();
      }

      if (this.luzBajaIzquierda.length == 3)
      {
        registrarMedidasLuzBajaIzquierdaEje3();
      }

      if (this.luzAltaDerecha.length == 3)
      {
        registrarMedidasluzAltaDerechaEje3();
      }

      if (this.luzAltaIzquierda.length == 3)
      {
        registrarMedidasLuzAltaIzquierdaEje3();
      }

      if (this.luzBajaDerecha.length == 4)
      {
        registrarMedidasLuzBajaDerechaEje4();
      }

      if (this.luzBajaIzquierda.length == 4)
      {
        registrarMedidasLuzBajaIzquierdaEje4();
      }

      if (this.luzAltaDerecha.length == 4)
      {
        registrarMedidasLuzAltaDerechaEje4();
      }

      if (this.luzAltaIzquierda.length == 4)
      {
        registrarMedidasLuzAltaIzquierdaEje4();
      }

      FrmSeleccionSumLuces frmSum = new FrmSeleccionSumLuces(this.luzBajaDerecha, this.luzAltaDerecha, this.luzBajaIzquierda, this.luzAltaIzquierda, this.sumaExploradoras);
      frmSum.setLocationRelativeTo(this.panelLuxometro);
      frmSum.setModal(true);
      frmSum.setVisible(true);
      double mayorSuma = frmSum.getAcumSumaIntensidad();
      String[] simultaneas = frmSum.getLucesSimultaneas();
      System.out.print(simultaneas);

      if (this.medidasExploradoras != null)
      {
        registrarMedidasExploradoras();
      }

      boolean aprobada = insertarPermisibles(mayorSuma);

      String serialEquipo = "";
      try {
        serialEquipo = ConsultarDatosVehiculo.buscarSerialEquipo(idPrueba.longValue());
      } catch (Exception e) {
        serialEquipo = "Serial no encontrado";
        e.printStackTrace();
      }
      serialEquipo = serialEquipo;
      boolean escrTrans = false;
      if ((aplicTrans == 1) && (aprobada == true))
      {
        escrTrans = true;
      }
      if (aplicTrans == 0) {
        escrTrans = true;
      }

      registrarMedidasSimultaneas(mayorSuma, simultaneas);
      int i;
      if (escrTrans == true)
      {
        String statement2;
        if (aprobada)
          statement2 = "UPDATE pruebas SET Finalizada = 'Y',Aprobada ='Y',Abortada='N',usuario_for = ?,serialEquipo = ? ,observaciones = ? WHERE pruebas.Id_Pruebas = ?";
        else {
          statement2 = "UPDATE pruebas SET Finalizada = 'Y',Aprobada ='N',Abortada='N',usuario_for = ?,serialEquipo = ? ,observaciones = ? WHERE pruebas.Id_Pruebas = ?";
        }
        PreparedStatement instruccion2 = this.conexion.prepareStatement(statement2);
        instruccion2.setLong(1, idUsuario);
        instruccion2.setString(2, serialEquipo);
        instruccion2.setString(3, this.lblObservacion.concat(this.lblExploradoras));
        instruccion2.setLong(4, idPrueba.longValue());
        i = instruccion2.executeUpdate();
      }
      else {
        String statement2 = "UPDATE pruebas SET Autorizada = 'A',usuario_for = ?,serialEquipo = ? ,observaciones = ? WHERE pruebas.Id_Pruebas = ?";
        PreparedStatement instruccion2 = this.conexion.prepareStatement(statement2);
        instruccion2.setLong(1, idUsuario);
        instruccion2.setString(2, serialEquipo);
        instruccion2.setString(3, this.lblObservacion.concat(this.lblExploradoras));
        instruccion2.setLong(4, idPrueba.longValue());
        i = instruccion2.executeUpdate();
      }

      this.conexion.commit();
      this.conexion.setAutoCommit(true);
      this.conexion.close();
      this.lblObservacion = this.lblObservacion.concat(this.lblExploradoras);
      
      /* if (org.soltelec.util.Mensajes.mensajePregunta("¿Desea Agregar un Comentario a la Prueba ?")) 
      {
        FrmComentario frm = new FrmComentario(SwingUtilities.getWindowAncestor(this.panelLuxometro), idPrueba, JDialog.DEFAULT_MODALITY_TYPE, this.lblObservacion);
        frm.setVisible(true);
        frm.setModal(true);
      }else{ */
          String statement2 = "UPDATE pruebas SET Autorizada = 'A',usuario_for = ?,serialEquipo = ? ,observaciones = NULL WHERE pruebas.Id_Pruebas = ?";
        PreparedStatement instruccion2 = this.conexion.prepareStatement(statement2);
        instruccion2.setLong(1, idUsuario);
        instruccion2.setString(2, serialEquipo);
          System.out.println("se realizo la edicion NULL");
        //instruccion2.setString(3, this.lblObservacion.concat(this.lblExploradoras));
        instruccion2.setLong(3, idPrueba.longValue());
        i = instruccion2.executeUpdate();
      //}
      System.out.println("comentario es: " + this.lblObservacion);
      if (escrTrans == true)
        escrTrans1 = "E";
      else {
        escrTrans1 = "A";
      }
      com.soltelec.modulopuc.utilidades.Mensajes.messageDoneTime("Se ha REGISTRADO la Prueba de Luces para el Vehiculo de una manera Exitosa ..¡", 3);
    }
    catch (Exception localException1)
    {
    }
  }

  private boolean utileriaGerencia() throws ClassNotFoundException, SQLException
  {
      
    Connection conexion = Conex.getConnection();

    String statement = "SELECT CRLNAME,CARLINE FROM lineas_vehiculos order by CARLINE";
    String statementUp = "UPDATE lineas_vehiculos SET CRLNAME =? WHERE CARLINE =? ";
    conexion.setAutoCommit(false);
    PreparedStatement instruccion = conexion.prepareStatement(statement);

    ResultSet rs = instruccion.executeQuery();
    String lnACambiar = null;
    String lnTratada = null;

    while (rs.next()) {
      lnACambiar = rs.getString("CRLNAME");
      lnTratada = lnACambiar.replace("\t", "");
      instruccion = conexion.prepareStatement(statementUp);
      instruccion.setString(1, lnTratada);
      instruccion.setInt(2, rs.getInt("CARLINE"));
      instruccion.executeUpdate();
      System.out.println(new StringBuilder().append("carLine ").append(rs.getInt("CARLINE")).toString());
      conexion.commit();
    }

    conexion.close();
    return true;
  }

  private boolean cargarPermisibles()
  {
    try
    {
      Connection conexion = Conex.getConnection();

      String statement = "SELECT Valor_maximo FROM permisibles WHERE Id_permisible = ?";
      PreparedStatement instruccion = conexion.prepareStatement(statement);
      instruccion.setInt(1, 6);
      ResultSet executeQuery = instruccion.executeQuery();
      if (executeQuery.first()) {
        this.permisibleBaja = executeQuery.getDouble("Valor_maximo");
        this.permisibleBaja = 2.5D;
      } else {
        JOptionPane.showMessageDialog(null, "Error cargando los valores vehiculo");
        conexion.close();
        return false;
      }
      instruccion.clearParameters();
      instruccion.setInt(1, 7);
      executeQuery = instruccion.executeQuery();
      if (executeQuery.first()) {
        this.permisibleAlta = executeQuery.getDouble("Valor_maximo");
      } else {
        JOptionPane.showMessageDialog(null, "Error cargando los valores vehiculo");
        conexion.close();
        return false;
      }
      instruccion.clearParameters();
      instruccion.setInt(1, 5);
      executeQuery = instruccion.executeQuery();
      if (executeQuery.first()) {
        this.permisibleSumatoria = executeQuery.getDouble("Valor_maximo");
      } else {
        JOptionPane.showMessageDialog(null, "Error cargando los valores vehiculo");
        conexion.close();
        return false;
      }
      String strDos = "SELECT Valor_minimo,Valor_maximo FROM permisibles WHERE Id_permisible = 9";
      PreparedStatement instruccion2 = conexion.prepareStatement(strDos);
      executeQuery = instruccion2.executeQuery();
      if (executeQuery.first()) {
        this.permisibleAnguloBajo = executeQuery.getDouble("Valor_minimo");
        this.permisibleAnguloAlto = executeQuery.getDouble("Valor_maximo");
      } else {
        JOptionPane.showMessageDialog(null, "Error cargando los valores vehiculo");
        conexion.close();
        return false;
      }
      conexion.close();
      return true;
    }
    catch (ClassNotFoundException ex) {
      JOptionPane.showMessageDialog(this.panelLuxometro, "Error no se encuentra el driver de MySQL");
      ex.printStackTrace(System.err);
    } catch (SQLException ex) {
      System.out.println(new StringBuilder().append("Error  ").append(ex.getMessage()).toString());
      JOptionPane.showMessageDialog(this.panelLuxometro, "Error de sentencia sql escribiendo medidas");
      ex.printStackTrace(System.err);
    }
    return false;
  }

  public long getIdHojaPrueba()
  {
    return this.idHojaPrueba;
  }

  public void setIdHojaPrueba(long idHojaPrueba) {
    this.idHojaPrueba = idHojaPrueba;
  }

  public long getIdPrueba() {
    return this.idPrueba.longValue();
  }

  public void setIdPrueba(long idPrueba) {
    this.idPrueba = Long.valueOf(idPrueba);
  }

  public long getIdUsuario() {
    return this.idUsuario;
  }

  public void setIdUsuario(long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public static enum Luz
  {
    ALTADERECHA(1), ALTAIZQUIERDA(2), BAJADERECHA(3), BAJAIZQUIERDA(4), EXPLORADORA(5);

    private int numeroLuz;

    private Luz(int numLuz) { this.numeroLuz = numLuz; }

    public void setNumeroLuz(int numeroLuz)
    {
      this.numeroLuz = numeroLuz;
    }
  }
}