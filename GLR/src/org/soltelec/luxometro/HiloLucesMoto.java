package org.soltelec.luxometro;

import eu.hansolo.steelseries.gauges.DisplayMulti;
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
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

public class HiloLucesMoto implements Runnable, ActionListener 
{

    private PanelLuxometroMotos panelLuxometro;
    private Timer timer;
    private int contadorTemporizacion = 0;
    private SerialPort serialPort;
    private InputStream in;
    private OutputStream out;
    private double[] valorMedidaBajaDerecha;
    private double[] valorMedidaAltaDerecha;
    private double[] anguloBaja;
    private double[] medidasExploradoras;
    private StringBuilder sb;
    private volatile Thread blinker;
    private Long idPrueba = Long.valueOf(9L);
    private int idUsuario = 1;
    private String tipoVehiculo = "";
    private int idHojaPrueba = 4;
    private boolean cancelacion = false;
    private String urljdbc;
    private String puertoLuxometro;
    public static String escrTrans = "";
    public static String ipEquipo;
    private static String unidadMed = "";
    public static int lngTrama;
    private String tipoLuxometro;
    private String lblExploradoras = " ";

    private int seqBajaAlta = 0;
    private double seqMov = 0.0D;
    private double min = 0.18D;
    private double max = 0.25D;
    private double minH = 0.62D;
    private double maxH = 0.82D;

    ArrayList<String> tramasBajas = new ArrayList();
    ArrayList<String> tramasAltas = new ArrayList();
    ArrayList<String> tramasFarolas = new ArrayList();

    public static String serialEquipo = "";
    private Connection conexion = null;
    public int aplicaModificacion;
    int valorInicialTrama = 6;
    int valorFinalTrama = 11;

    private Object opcion = "BAJAS Y ALTAS";

    public void setTipoVehiculo(String tipoVehiculo) {
        this.tipoVehiculo = tipoVehiculo;
    }

    public HiloLucesMoto(PanelLuxometroMotos panel, int aplicaModificacion, String tipoLuxometro) 
    {
        this.panelLuxometro = panel;
        
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

        this.tipoLuxometro = tipoLuxometro;
        this.aplicaModificacion = aplicaModificacion;

        cargarLongitudTramaLeer();
    }

    public boolean buscarLuxometro() 
    {
        return true;
    }

    private void cargarLongitudTramaLeer() 
    {
        
        System.out.println("--------------------------------------------------");
        System.out.println("---------  Cargar Longitud Trama Leer  -----------");
        System.out.println("--------------------------------------------------");

        Properties props = new Properties();
        try {
            props.load(new FileInputStream("./propiedades.properties"));
            
            System.out.println(".... Buscando variable valorInicialTrama.....");
            
            if ((props.getProperty("valorInicialTrama") != null) || (!props.getProperty("valorInicialTrama").equalsIgnoreCase(""))) 
            {
                this.valorInicialTrama = Integer.parseInt(props.getProperty("valorInicialTrama"));
                System.out.println(new StringBuilder().append("se obtiene de valorInicialTrama el dato: ").append(this.valorInicialTrama).toString());
                
            } else 
            {
                System.out.println(new StringBuilder().append(" No recojo nada del properties para  ::valorInicialTrama::  se toma el valor por defecto ").append(this.valorInicialTrama).toString());
            }

            if ((props.getProperty("valorFinalTrama") != null) || (!props.getProperty("valorFinalTrama").equalsIgnoreCase(""))) 
            {
                this.valorFinalTrama = Integer.parseInt(props.getProperty("valorFinalTrama"));
                System.out.println(new StringBuilder().append("se obtiene de valorFinalTrama el dato: ").append(this.valorFinalTrama).toString());
            } else {
                System.out.println(new StringBuilder().append(" No recojo nada del properties para  ::valorFinalTrama::  se toma el valor por defecto ").append(this.valorFinalTrama).toString());
            }
        } catch (IOException e) 
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

    private int getNumeroLuces(String texto) 
    {
        System.out.println("----------------------------------------------------");
        System.out.println("---------  capturarNumeroLucesBajaDerecha ----------");
        System.out.println("----------------------------------------------------");
        try 
        {
            Object numeroLuces = JOptionPane.showInputDialog(this.panelLuxometro, texto, "Numero de Farolas", -1, new ImageIcon(), new Object[]{new Integer(1), new Integer(2), new Integer(3), new Integer(4)}, new Integer(9));
            return ((Integer) numeroLuces).intValue();
        } catch (Exception e) {
            System.out.println(new StringBuilder().append("Error en el metodo : getNumeroLuces()").append(e).toString());
        }
        return 0;
    }

    private Object getOpcionMostrar() 
    {
        System.out.println("--------------------------------------");
        System.out.println("---------  getOpcionMostrar ----------");
        System.out.println("--------------------------------------");
        try 
        {
            return JOptionPane.showInputDialog(this.panelLuxometro, "Opciones", "", -1, new ImageIcon(), new Object[]{new String("BAJAS Y ALTAS"), new String("BAJAS")}, new Integer(9));
            
        } catch (Exception e) 
        {
            System.out.println(new StringBuilder().append("Error en el metodo : getOpcionMostrar()").append(e).toString());
        }
        return "BAJAS Y ALTAS";
    }

    private String tomarMedidaBajas() throws InterruptedException 
    {
        System.out.println("----------------------------------------------------");
        System.out.println("-----   Tomando Medidas Luces  Bajas       ---------");
        System.out.println("----------------------------------------------------");
        try {
            int numeroFarolas = getNumeroLuces("Numero de  Luces Bajas ");
            this.valorMedidaBajaDerecha = new double[numeroFarolas];
            this.anguloBaja = new double[numeroFarolas];

            for (int i = 0; i < numeroFarolas; i++) 
            {
                DialogoAnguloInclinacion panelAngulo = new DialogoAnguloInclinacion(SwingUtilities.getWindowAncestor(this.panelLuxometro), "Angulo Inclinacion Luz Baja", JDialog.DEFAULT_MODALITY_TYPE);
                panelAngulo.getPanel().getLabelTitulo().setText(new StringBuilder().append("Farola Angulo Inclinacion  Luz Baja ").append(i + 1).toString());
                panelAngulo.setVisible(true);
                this.anguloBaja[i] = panelAngulo.getPanel().getValorSlider();
                this.panelLuxometro.getLuzBajaUno().setBlinking(true);
                Thread.sleep(1500L);
                this.panelLuxometro.getLabelTitulo().setText(new StringBuilder().append("Tomando medida farola baja numero : ").append(i + 1).append(" PRES ENTER ..! ").toString());
                this.sb = new StringBuilder("!");
                crearNuevoTimer();
                this.timer.start();
                this.contadorTemporizacion = 0;
                while(this.contadorTemporizacion < 60 && this.blinker != null) 
                {
                    Thread.sleep(50L);
                    this.panelLuxometro.getDisplay().setLcdValue(this.contadorTemporizacion);
                    this.valorMedidaBajaDerecha[i] = tomarMedicion(Luz.BAJADERECHA);
                    if (valorMedidaBajaDerecha[i] > 0) 
                    {     System.out.println("entro a if, valor de la medida de las farolas bajas "+ valorMedidaBajaDerecha[i] + " >-1 \n unidad de medida : "+ unidadMed + "\n" );
                        if (unidadMed.equalsIgnoreCase("L")) 
                        { System.out.println("entro a L\n");
                            System.out.println(new StringBuilder().append("Recibo del Luxometro la cantidad de :").append(this.valorMedidaBajaDerecha).toString());
                            if (this.valorMedidaBajaDerecha[i] < 15) 
                            {
                                this.seqBajaAlta = ThreadLocalRandom.current().nextInt(0, 1);
                                if (this.seqBajaAlta == 0) 
                                {
                                    this.seqMov = ThreadLocalRandom.current().nextDouble(this.min, this.max);
                                    this.valorMedidaBajaDerecha[i] -= this.seqMov;
                                    System.out.println(new StringBuilder().append("SeqMov es Baja :").append(this.seqMov).toString());
                                    System.out.println(new StringBuilder().append("Resultado es  :").append(this.valorMedidaBajaDerecha).toString());
                                } else {
                                    this.seqMov = ThreadLocalRandom.current().nextDouble(this.minH, this.maxH);
                                    this.valorMedidaBajaDerecha[i] += this.seqMov;
                                    System.out.println(new StringBuilder().append("SeqMov es Alta :").append(this.seqMov).toString());
                                    System.out.println(new StringBuilder().append("Resultado es  :").append(this.valorMedidaBajaDerecha).toString());
                                }
                                this.valorMedidaBajaDerecha[i] *= 0.625;
                            } else 
                            {
                                System.out.println("valor a dividir por encima de 15 :");
                                this.valorMedidaBajaDerecha[i] *= 0.625;
                            }
                        }
                        System.out.println("FINALIZO PROCESO CON LA FAROLA " + i );
                        break;
                    } else {
                       
                        System.out.println(new StringBuilder().append("syst medICION ").append(unidadMed).toString());
                        
                        if ((this.valorMedidaBajaDerecha[i] == -100) || (this.valorMedidaBajaDerecha[i] == -1))
                        {
                            System.out.println("entre en logica de -100 or -1, sigo midiendo, quedan :" + this.contadorTemporizacion + "segundos");
                        } else if (this.valorMedidaBajaDerecha[i] == -200) 
                        {
                            System.out.println("Entra a  la logica del -200");
                        } else{
                  System.out.println("entre en logica de 0.1 ");
                  int n=  JOptionPane.showConfirmDialog ( null,"¿esta seguro de la medicion?", "", JOptionPane.YES_NO_OPTION );
                  if(n == JOptionPane.YES_OPTION)
                  {
                      this.valorMedidaBajaDerecha[i] = 0.1;
                      this.contadorTemporizacion =121;
                      break;
                      
                  }else
                  {
                      this.contadorTemporizacion =0;
                      
                  }
                } 
                    }     
                }
         if(this.contadorTemporizacion>120)
        {
            System.out.println("no se detecto ningun valor, el valor guardado en la base de datos para LuzBajaDerecha es cero ´0´ ");
            JOptionPane.showMessageDialog(this.panelLuxometro, "Prueba luz baja derecha finalizada");
            this.valorMedidaBajaDerecha[i] = 0;
        }
                this.panelLuxometro.getLabelTitulo().setText(new StringBuilder().append("MEDICION TOMADA FAROLA ").append(i + 1).append("..!").toString());
                this.panelLuxometro.getLuzBajaUno().setBlinking(false);
                this.panelLuxometro.getLuzBajaUno().setOnOff(true);
                this.timer.stop();
                Thread.sleep(2000L);
            }
        } catch (Exception ioe) {
            JOptionPane.showMessageDialog(this.panelLuxometro, "Disculpe, he presentado problemas en leer la trama");
            System.out.println("error al leer la trama: "+ ioe);
            ioe.printStackTrace();
            return null;
        }
        return "";
    }

    private String tomarMedidaAltas() throws InterruptedException
    {
        System.out.println("----------------------------------------------------");
        System.out.println("-----   Tomando Medidas Luces  Altas       ---------");
        System.out.println("----------------------------------------------------");
        try {
            //toma el numero de farolas que tiene la moto
            int numeroFarolas = getNumeroLuces("Numero de  Luces Altas ");
            this.valorMedidaAltaDerecha = new double[numeroFarolas];

            for (int i = 0; i < numeroFarolas; i++)
            {
                Thread.sleep(1500L);
                this.panelLuxometro.getLabelTitulo().setText(new StringBuilder().append("Tomando medida farola alta numero : ").append(i + 1).append(" PRES ENTER ..! ").toString());
                this.panelLuxometro.getLuzAltaUno().setBlinking(true);
                this.sb = new StringBuilder("!");
                crearNuevoTimer();
                this.timer.start();
                this.contadorTemporizacion = 0;
                while (this.contadorTemporizacion < 60 && this.blinker != null) 
                {
                  Thread.sleep(50L);
                  this.panelLuxometro.getDisplay().setLcdValue(this.contadorTemporizacion);
                  this.valorMedidaAltaDerecha[i] = tomarMedicion(Luz.ALTADERECHA);
                  System.out.println(new StringBuilder().append("recogi del luxometro FAR2 ").append(this.valorMedidaAltaDerecha[i]).toString());
                  if (valorMedidaAltaDerecha[i] > 0) 
                  {     
                    System.out.println("entro a if "+ valorMedidaAltaDerecha[i] + " >-1 \n unidad de medida : "+ unidadMed + "\n" );
                    if (unidadMed.equalsIgnoreCase("L"))
                    {
                      System.out.println("entro a L \n");
                      if (this.valorMedidaAltaDerecha[i] < 15)
                      {
                        this.seqBajaAlta = ThreadLocalRandom.current().nextInt(0, 1);
                        if (this.seqBajaAlta == 0) 
                        {
                          this.seqMov = ThreadLocalRandom.current().nextDouble(this.min, this.max);
                          this.valorMedidaAltaDerecha[i] -= this.seqMov;
                        }else
                        {
                          this.seqMov = ThreadLocalRandom.current().nextDouble(this.minH, this.maxH);
                          this.valorMedidaAltaDerecha[i] += this.seqMov;
                        }
                        this.valorMedidaAltaDerecha[i] *= 0.625;
                       }else
                       {
                          this.valorMedidaAltaDerecha[i] *= 0.625;
                       }
                    }
                    System.out.println("FINALICE PROCESAMIENTO DE LA TRAMA DEBO SALIR DE LA FAROLA2:");
                    break;
                   }else
                   {
                      System.out.println(new StringBuilder().append("syst medICION ").append(unidadMed).toString());
                      if ((this.valorMedidaAltaDerecha[i] == -100.0D) || (this.valorMedidaAltaDerecha[i] == -1.0D)) 
                      {
                        System.out.println("entre en logica de -100 far 2 or -1 ");
                      } else if (this.valorMedidaAltaDerecha[i] == -200) 
                        {
                          System.out.println("Entra a  la logica del -200");
                        }else
                        {
                            System.out.println("entre en logica de 0.1 ");
                            int n=  JOptionPane.showConfirmDialog ( null,"¿esta seguro de la medicion?", "", JOptionPane.YES_NO_OPTION );
                            if(n == JOptionPane.YES_OPTION)
                            {
                              this.valorMedidaAltaDerecha[i] = 0.1;
                              this.contadorTemporizacion =121;
                              break;
                            }else
                            {
                                this.contadorTemporizacion=0;
                            }            
                        }  
                   }
                }
                if(this.contadorTemporizacion>120)
                {
                  System.out.println("no se detecto ningun valor, el valor guardado en la base de datos para LuzMedidaAltaDerecha es cero ´0´ ");
                  JOptionPane.showMessageDialog(this.panelLuxometro, "Prueba luz alta derecha finalizada");
                  this.valorMedidaAltaDerecha[i] = 0;
                }
                this.panelLuxometro.getLabelTitulo().setText(new StringBuilder().append("MEDICION TOMADA FAROLA ALTA NUMERO : ").append(i + 1).append("..!").toString());
                this.panelLuxometro.getLuzAltaUno().setBlinking(false);
                this.panelLuxometro.getLuzAltaUno().setOnOff(true);
                Thread.sleep(2000L);
                this.timer.stop();
            }
        } catch (Exception ioe) 
        {
            JOptionPane.showMessageDialog(this.panelLuxometro, "Disculpe, he presentado problemas en leer la trama");
            ioe.printStackTrace();
            return null;
        }
        return "";
    }

    private String tomarMedidaBajaIzquierda()throws InterruptedException 
    {
        //        DialogoAnguloInclinacion panelAngulo = new DialogoAnguloInclinacion(SwingUtilities.getWindowAncestor(panelLuxometro),"Angulo Inclinacion Luz Baja", JDialog.DEFAULT_MODALITY_TYPE);
//        panelAngulo.getPanel().getLabelTitulo().setText("Farola 3: Angulo Inclinacion  Luz Baja");
//        panelAngulo.setVisible(true);
//        anguloBajaIzquierda = panelAngulo.getPanel().getValorSlider();
//        contadorTemporizacion = 0;
//        
//        crearNuevoTimer();
//        timer.start();
//        panelLuxometro.getLuzBajaDos().setBlinking(true);
//
//        sb = new StringBuilder("!");
//        try 
//        {
//            while (contadorTemporizacion < 60 && blinker != null) 
//            {
//                Thread.sleep(50);
//                panelLuxometro.getDisplay().setLcdValue(contadorTemporizacion);
//                valorMedidaBajaIzquierda = tomarMedicion(Luz.BAJAIZQUIERDA);
//                System.out.println("recogi del luxometro FAR3 " + valorMedidaBajaIzquierda);
//                if (valorMedidaBajaIzquierda > 0) 
//                {
//                    if (HiloLucesMoto.unidadMed.equalsIgnoreCase("L"))
//                    {
//                        if (valorMedidaBajaIzquierda < 15)
//                        {
//                            seqBajaAlta = ThreadLocalRandom.current().nextInt(0, 1);
//                            if (seqBajaAlta == 0) 
//                            {
//                                seqMov = ThreadLocalRandom.current().nextDouble(min, max);
//                                valorMedidaBajaIzquierda = valorMedidaBajaIzquierda - seqMov;
//                            } else {
//                                seqMov = ThreadLocalRandom.current().nextDouble(minH, maxH);
//                                valorMedidaBajaIzquierda = valorMedidaBajaIzquierda + seqMov;
//                            }
//                            valorMedidaBajaIzquierda = valorMedidaBajaIzquierda * 0.625;
//                        } else {
//                            valorMedidaBajaIzquierda = valorMedidaBajaIzquierda * 0.625;
//                        }
//                    }
//                    System.out.println("FINALICE PROCESAMIENTO DE LA TRAMA DEBO SALIR DE LA FAROLA3 :");
//                    break;
//                } else {
//                    System.out.println("syst medICION " + HiloLucesMoto.unidadMed);
//                    if (valorMedidaBajaIzquierda == -100 || valorMedidaBajaIzquierda == -1) 
//                    {//-100 para indicar que no se leyo nada
//                        System.out.println("entre en logica de -100 or -1 ");
//                    } else if(valorMedidaBajaIzquierda==-200)
//                    {
//                        continue;
//                    }else{
//                        System.out.println("entre en logica de 0.1 ");
//                        valorMedidaBajaIzquierda = 0.1;
//                        break;
//                    }
//                }
//            }//end outer while
//            Thread.sleep(50);
//            timer.stop();
//        }//end try
//        catch (Exception ioe) 
//        {
//            JOptionPane.showMessageDialog(panelLuxometro, "Disculpe, he presentado problemas en leer la trama");
//            ioe.printStackTrace();
//            return null;
//        }
//        if (contadorTemporizacion > 59) 
//        {
//            JOptionPane.showMessageDialog(panelLuxometro, "Disculpe, la prueba se finaliza debido a que tengo un minuto sin leer Trama ");
//            panelLuxometro.cerrar();
//            serialPort.close();
//            return null;
//        }
//        //  panelLuxometro.getLuzAlta().setBlinking(false);
//        // panelLuxometro.getLuzAlta().setOnOff(true);
//        timer.stop();
//        panelLuxometro.getLuzBajaDos().setBlinking(false);
//        panelLuxometro.getLuzBajaDos().setOnOff(true);
//        Thread.sleep(2000);
//        Thread.sleep(1000);
        return "";
    }

    private String tomarMedidaAltaIzquierda()throws InterruptedException {
        //        contadorTemporizacion = 0;
//        crearNuevoTimer();
//        timer.start();
//        
//        panelLuxometro.getLuzAltaDos().setBlinking(true);
//
//        sb = new StringBuilder("!");
//        try 
//        {
//            while (contadorTemporizacion < 60 && blinker != null)
//            {
//                Thread.sleep(50);
//                panelLuxometro.getDisplay().setLcdValue(contadorTemporizacion);
//                valorMedidaAltaIzquierda = tomarMedicion(Luz.ALTAIZQUIERDA);
//                System.out.println("recogi del luxometro  " + valorMedidaAltaIzquierda);
//                if (valorMedidaAltaIzquierda > 0) 
//                {
//                    if (HiloLucesMoto.unidadMed.equalsIgnoreCase("L")) 
//                    {
//                        if (valorMedidaAltaIzquierda < 15) 
//                        {
//                            seqBajaAlta = ThreadLocalRandom.current().nextInt(0, 1);
//                            if (seqBajaAlta == 0) {
//                                seqMov = ThreadLocalRandom.current().nextDouble(min, max);
//                                valorMedidaAltaIzquierda = valorMedidaAltaIzquierda - seqMov;
//                            } else {
//                                seqMov = ThreadLocalRandom.current().nextDouble(minH, maxH);
//                                valorMedidaAltaIzquierda = valorMedidaAltaIzquierda + seqMov;
//                            }
//                            valorMedidaAltaIzquierda = valorMedidaAltaIzquierda * 0.625;
//                        } else {
//                            valorMedidaAltaIzquierda = valorMedidaAltaIzquierda * 0.625;
//                        }
//                    }
//                    System.out.println("FINALICE PROCESAMIENTO DE LA TRAMA DEBO SALIR DE LA FAROLA4 :");
//                    break;
//                } else {
//                    System.out.println("syst medICION " + HiloLucesMoto.unidadMed);
//                    if (valorMedidaAltaIzquierda == -100 || valorMedidaAltaIzquierda == -1) 
//                    {  //-100 para indicar que no se leyo nada
//                        System.out.println("entre en logica de -100 or -1 far4 ");
//                    }else if(valorMedidaAltaIzquierda==-200)
//                    {
//                        continue;
//                    }else 
//                    {
//                        System.out.println("entre en logica de 0.1 far4 ");
//                        valorMedidaAltaIzquierda = 0.1;
//                        break;
//                    }
//                }
//            }
//            Thread.sleep(50);
//            timer.stop();
//        }//end try
//        catch (Exception ioe)
//        {
//            JOptionPane.showMessageDialog(panelLuxometro, "Disculpe, he presentado problemas en leer la trama");
//            ioe.printStackTrace();
//            return null;
//        }
//        if (contadorTemporizacion > 59) 
//        {
//            JOptionPane.showMessageDialog(panelLuxometro, "Disculpe, la prueba se finaliza debido a que tengo un minuto sin leer Trama ");
//            panelLuxometro.cerrar();
//            serialPort.close();
//            return null;
//        }
//        panelLuxometro.getLuzAltaDos().setBlinking(false);
//        panelLuxometro.getLuzAltaDos().setOnOff(true);
//        timer.stop();
//        Thread.sleep(1000);
        return "";
    }

    private boolean getDatosExplo(int numeroExploradoras)throws InterruptedException 
    {
        System.out.println("----------------------------------------------------");
        System.out.println("---------------- exploradorasSeleciondas ------------");
        System.out.println("----------------------------------------------------");
        try {
            if (!getDatosExploradoras(numeroExploradoras)) 
            {
                return false;
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this.panelLuxometro, "Error de I/O");
            this.panelLuxometro.cerrar();
        }
        return true;
    }

    private boolean getDatosExploradoras(int numeroExploradoras) throws IOException, InterruptedException 
    {
        System.out.println("----------------------------------------------------");
        System.out.println("-------------------MEDIR  EXPLORADORAS--------------");
        System.out.println("----------------------------------------------------");

        this.contadorTemporizacion = 0;
        crearNuevoTimer();
        this.timer.start();
        int numeroExploradorasMedidas = 0;
        double medicionTemporal = -100.0D;

        int j = 0;
        while ((numeroExploradorasMedidas < numeroExploradoras) && (this.contadorTemporizacion < 300) && (this.blinker != null)) {
            this.panelLuxometro.getLabelTitulo().setText(new StringBuilder().append("Luz Adicional ").append(numeroExploradorasMedidas + 1).toString());
            this.panelLuxometro.getExploradoras()[(numeroExploradoras - numeroExploradorasMedidas - 1)].setBlinking(true);
            medicionTemporal = tomarMedicion(Luz.EXPLORADORA);

            while ((medicionTemporal < 0.0D) && (this.contadorTemporizacion < 300) && (this.blinker != null)) {
                if (medicionTemporal == -1.0D) {
                    this.panelLuxometro.getLabelTitulo().setText(new StringBuilder().append("press enter").append(this.sb).toString());
                    this.sb.append("!");
                }

                medicionTemporal = tomarMedicion(Luz.EXPLORADORA);
                Thread.sleep(277L);
            }

            this.panelLuxometro.getExploradoras()[(numeroExploradoras - numeroExploradorasMedidas - 1)].setBlinking(false);
            this.panelLuxometro.getExploradoras()[(numeroExploradoras - numeroExploradorasMedidas - 1)].setOnOff(true);

            if (unidadMed.equalsIgnoreCase("L")) {
                medicionTemporal *= 0.625D;
            }

            System.out.println(new StringBuilder().append(" numeroExploradorasMedidas ").append(numeroExploradorasMedidas).toString());
            this.medidasExploradoras[numeroExploradorasMedidas] = medicionTemporal;
            numeroExploradorasMedidas++;
            j += 1;
           // this.lblExploradoras = this.lblExploradoras.concat(" ").concat("Expl ".concat(String.valueOf(j)).concat(": ").concat(String.valueOf(medicionTemporal)));
        }
        this.timer.stop();

        if (this.contadorTemporizacion >= 300) {
            JOptionPane.showMessageDialog(this.panelLuxometro, "Fin prueba\nCinco minutos sin terminar el proceso ....");
            this.panelLuxometro.cerrar();
            this.serialPort.close();
            return false;
        }
        return true;
    }

    public void run() {

        this.panelLuxometro.agregarDisplay(true);
        this.panelLuxometro.botonCancelar(true);
        this.panelLuxometro.getButtonCancelar().addActionListener(this);
        this.panelLuxometro.farolaAltaUno(true);
        this.panelLuxometro.farolaBajaUno(true);

        if (cargarParametros() == null) {
            return;
        }

        this.panelLuxometro.getLabelTitulo().setText("Conectando con BD..!");

        try {

            this.blinker = Thread.currentThread();

            if (inicializarPuertoSerial(this.puertoLuxometro) == null) 
            {
                return;
            }

            opcion = getOpcionMostrar();

            if (opcion.equals("BAJAS")) 
            {
                if (tomarMedidaBajas() != null);

            } else {
                if (tomarMedidaBajas() == null) {
                    return;
                }

                if (tomarMedidaAltas() == null) {
                    return;
                }

            }

            Thread.sleep(2000L);
            this.serialPort.close();
            escrTrans = "@";
            logicaRegistrarMedidas(this.idPrueba, this.idUsuario, 0, this.tipoVehiculo);
            this.panelLuxometro.cerrar();
            
        } catch (InterruptedException ie) {
            System.out.println("Prueba Cancelada");
            String showInputDialog = JOptionPane.showInputDialog("comentario de aborto");
            this.panelLuxometro.cerrar();
            this.serialPort.close();
            this.timer.stop();
            if (this.cancelacion == true) {
                registrarCancelacion(showInputDialog, this.idPrueba, this.idUsuario);
            }
        }
    }

    private void crearNuevoTimer() {
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contadorTemporizacion++;
                panelLuxometro.getDisplay().setLcdValue(contadorTemporizacion);
            }

        });
    }

    private double tomarMedicion(Luz luz) throws IOException 
    {
        System.out.println("-------------------------------------");
        System.out.println("------   TOMAR MEDIDAS  -------------");
        System.out.println("-------------------------------------");

        System.out.println(" voy a Recoger trama en bytes del luxometro ");
        //guarda la trama de luces en una cadena de byte
        byte[] b = leerTrama();
        System.out.println("entre de nuevo a Tomar medicion  ");
        if (b == null) 
        {
            System.out.println("el array de bytes es null, trama esta vacia ");
            return -100;
        }
        String cadena = new String(b);
        StringBuilder sb = new StringBuilder(cadena);
        sb.deleteCharAt(0);
        cadena = sb.toString();

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
                return leerTramaFarolas(cadena);
        }
        System.out.println("Trama no reconocida");
        return -1;
    }

    private double leerTramaAltas(String cadena) 
    {
        System.out.println(" -- LEYENDO DATOS ALTA");
        System.out.println(new StringBuilder().append(" -- TRAMA RECOGIDA :").append(cadena).toString());
        System.out.println(new StringBuilder().append(" -- LONGITUD TRAMA :").append(cadena.length()).toString());
        unidadMed = cadena.substring(5, 6);
        System.out.println(new StringBuilder().append(" -- UNIDAD DE MEDIDA RECOGIDA ").append(unidadMed).toString());

        for (String dato : this.tramasAltas) 
        {
            if(cadena.regionMatches(0, dato, 0, 6) && unidadMed.equalsIgnoreCase("K")) 
            {
                System.out.println(new StringBuilder().append(" -- CADENA DE COMPARACION :").append(dato).toString());
                System.out.println(new StringBuilder().append(" -- DE ").append(cadena).append(" SE TOMA EL DATO :").append(cadena.substring(6, 11)).toString());
                return Double.parseDouble(cadena.substring(this.valorInicialTrama, this.valorFinalTrama));
            }
            if ((cadena.regionMatches(0, dato, 0, 6)) && (unidadMed.equalsIgnoreCase("L"))) 
            {
                System.out.println(new StringBuilder().append(" -- CADENA DE COMPARACION :").append(dato).toString());
                System.out.println(new StringBuilder().append(" -- DE ").append(cadena).append(" SE TOMA EL DATO ").append(cadena.substring(6, 9)).toString());
                return Double.parseDouble(cadena.substring(this.valorInicialTrama, this.valorFinalTrama));
            }
        }

        System.err.println(new StringBuilder().append(" La trama ").append(cadena).append("no reconocida").toString());
        System.out.println(" Retorno la logica del -200");
        return -200;
    }

    private double leerTramaBajas(String cadena)
    {
        System.out.println(" -- LEYENDO DATOS BAJAS");
        System.out.println(" -- TRAMA RECOGIDA : " + cadena);
        System.out.println(" -- LONGITUD TRAMA : "+ cadena.length());
        unidadMed = cadena.substring(5, 6);
        System.out.println(" -- UNIDAD DE MEDIDA RECOGIDA :" + unidadMed);

        for (String dato : this.tramasBajas) 
        {
            if (cadena.regionMatches(0, dato, 0, 6) && unidadMed.equalsIgnoreCase("K")) 
            {
                System.out.println(" -- CADENA DE COMPARACION :");
                System.out.println(new StringBuilder().append(" -- DE ").append(cadena).append(" SE TOMA EL DATO :").append(cadena.substring(6, 11)).toString());
                System.out.println("entra al if de K, valor a del return:" + Double.parseDouble(cadena.substring(valorInicialTrama,valorFinalTrama)));
                return Double.parseDouble(cadena.substring(valorInicialTrama,valorFinalTrama));
            }
            if ((cadena.regionMatches(0, dato, 0, 6)) && (unidadMed.equalsIgnoreCase("L")))
            {
                System.out.println(new StringBuilder().append(" -- CADENA DE COMPARACION :").append(dato).toString());
                System.out.println(new StringBuilder().append(" -- DE ").append(cadena).append(" SE TOMA EL DATO ").append(cadena.substring(6, 9)).toString());
                System.out.println("entra al if de L, valor del return: "+ cadena.substring(this.valorInicialTrama, this.valorFinalTrama) );
                return Double.parseDouble(cadena.substring(this.valorInicialTrama, this.valorFinalTrama));
            }
        }

        System.err.println(new StringBuilder().append(" La trama ").append(cadena).append(" no reconocida").toString());
        System.out.println(" Retorno la logica del -200");
        return -200.0D;
    }

    private double leerTramaFarolas(String cadena) 
    {
        System.out.println(" -- LEYENDO DATOS ALTA");
        System.out.println(new StringBuilder().append(" -- TRAMA RECOGIDA :").append(cadena).toString());
        System.out.println(new StringBuilder().append(" -- LONGITUD TRAMA :").append(cadena.length()).toString());
        unidadMed = cadena.substring(5, 6);
        System.out.println(new StringBuilder().append(" -- UNIDAD DE MEDIDA RECOGIDA ").append(unidadMed).toString());

        for (String dato : this.tramasFarolas) {
            if ((cadena.regionMatches(0, dato, 0, 6)) && (unidadMed.equalsIgnoreCase("K"))) {
                System.out.println(new StringBuilder().append(" -- CADENA DE COMPARACION :").append(dato).toString());
                System.out.println(new StringBuilder().append(" -- DE ").append(cadena).append(" SE TOMA EL DATO :").append(cadena.substring(6, 11)).toString());
                return Double.parseDouble(cadena.substring(this.valorInicialTrama, this.valorFinalTrama));
            }
            if ((cadena.regionMatches(0, dato, 0, 6)) && (unidadMed.equalsIgnoreCase("L"))) {
                System.out.println(new StringBuilder().append(" -- CADENA DE COMPARACION :").append(dato).toString());
                System.out.println(new StringBuilder().append(" -- DE ").append(cadena).append(" SE TOMA EL DATO ").append(cadena.substring(6, 9)).toString());
                return Double.parseDouble(cadena.substring(this.valorInicialTrama, this.valorFinalTrama));
            }
        }

        System.err.println(new StringBuilder().append(" La trama ").append(cadena).append(" no reconocida").toString());
        System.out.println(" Retorno la logica del -200");
        return -200.0D;
    }

    private byte[] leerTrama() throws IOException 
    {
        System.out.println(" entre en Leer Trama:");
        boolean existeTrama = false;
        byte[] buffer = new byte[1024];
        System.out.println(" creo nuevo arreglo de Bytes:");

        int len = 0;
        this.in = this.serialPort.getInputStream();
        System.out.println(new StringBuilder().append(" take inputStream del puerto serial: ").append(this.in.toString()).toString());
        int data;
        while ((data = this.in.read()) > -1) {
            System.out.println(new StringBuilder().append("recogi Byte:").append(data).toString());
            buffer[(len++)] = ((byte) data);
        }
        System.out.println(" termine de read while ");

        System.out.println(new StringBuilder().append(" leng del buffer: ").append(len).toString());
        if (len > 0) {
            lngTrama = len;
            System.out.println(new StringBuilder().append("Longitud del mensaje:").append(len).toString());

            byte[] arreglo = new byte[len];

            System.arraycopy(buffer, 0, arreglo, 0, len);

            return arreglo;
        }

        return null;
    }

    private String cargarParametros() {
        try {
            Properties props = new Properties();

            props.load(new FileInputStream("./propiedades.properties"));
            this.urljdbc = props.getProperty("urljdbc");
            this.puertoLuxometro = props.getProperty("PuertoLuxometro");

            System.out.println(this.urljdbc);
            System.out.println(this.puertoLuxometro);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(HiloLucesMoto.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Imposible cargar archivo propiedades");
            return null;
        } catch (IOException ex) {
            Logger.getLogger(HiloLucesMoto.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Imposible cargar archivo propiedades");
            return null;
        }

        return "";
    }

    private int cargarPropiedades(String propiedad) {
        System.out.println("----------------------------------------------");
        System.out.println(new StringBuilder().append("----- Cargando propiedad :").append(propiedad).append(" ----").toString());
        System.out.println("----------------------------------------------");
        int valocidadTransferencia = 2400;
        try {
            Properties props = new Properties();
            props.load(new FileInputStream("./propiedades.properties"));
            valocidadTransferencia = Integer.parseInt(props.getProperty(propiedad));
            System.out.println(new StringBuilder().append("Valor de ").append(propiedad).append(" : ").append(valocidadTransferencia).toString());
        } catch (FileNotFoundException ex) {
            com.soltelec.modulopuc.utilidades.Mensajes.messageErrorTime(new StringBuilder().append("Error al cargar la propiedad ").append(propiedad).toString(), 5);
            System.out.println(new StringBuilder().append("Error en el metodo : cargarPropiedades()").append(ex).toString());
            System.out.println(new StringBuilder().append("Error ").append(ex.getMessage()).toString());
            Logger.getLogger(HiloLucesMoto.class.getName()).log(Level.SEVERE, null, ex);
            valocidadTransferencia = 2400;
        } catch (IOException ex) {
            com.soltelec.modulopuc.utilidades.Mensajes.messageErrorTime(new StringBuilder().append("Error al cargar la propiedad ").append(propiedad).toString(), 5);
            System.out.println(new StringBuilder().append("Error en el metodo : cargarPropiedades()").append(ex).toString());
            System.out.println(new StringBuilder().append("Error ").append(ex.getMessage()).toString());
            Logger.getLogger(HiloLucesMoto.class.getName()).log(Level.SEVERE, null, ex);
            valocidadTransferencia = 2400;
        }
        return valocidadTransferencia;
    }

    private SerialPort connect(String portName, int baudRate, int dataBits, int stopBits, int parity) throws Exception {
        SerialPort serialPort = null;
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        if (portIdentifier.isCurrentlyOwned()) {
            System.out.println("Error: Port is currently in use");
        } else {
            CommPort commPort = portIdentifier.open(getClass().getName(), 2000);

            if ((commPort instanceof SerialPort)) {
                serialPort = (SerialPort) commPort;

                serialPort.setSerialPortParams(baudRate, dataBits, stopBits, parity);
                serialPort.setDTR(false);
                serialPort.setRTS(false);
                serialPort.setFlowControlMode(1);
                this.in = serialPort.getInputStream();
                this.out = serialPort.getOutputStream();
            } else {
                System.out.println("Error: Only serial ports are handled by this example.");
            }
        }
        return serialPort;
    }

    private String inicializarPuertoSerial(String portName) {
        System.out.println("---------------------------------------------");
        System.out.println("---  Conectando con el puerto serial --------");
        System.out.println("---------------------------------------------");
        try {
            System.out.println(new StringBuilder().append(" Port Name : ").append(portName).toString());
            this.serialPort = connect(portName, cargarPropiedades("VelocidadPuerto"), 8, 1, 0);
            if (this.serialPort != null) {
                System.out.println("Puerto Serial conectado");
            }
        } catch (Exception ex) {
            System.out.println(new StringBuilder().append("Error en el metodo:inicializarPuertoSerial()").append(ex).toString());
            System.out.println(new StringBuilder().append("Error ").append(ex.getMessage()).toString());
            Logger.getLogger(HiloLucesMoto.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, new StringBuilder().append("Error al conectarse al puerto ").append(portName).toString());
            this.panelLuxometro.cerrar();
            this.serialPort.close();
            this.timer.stop();
            return null;
        }
        return "";
    }

    public void actionPerformed(ActionEvent e) {
        stop();
    }

    private void stop() {
        this.cancelacion = true;
        Thread tmpBlinker = this.blinker;
        this.blinker = null;
        if (tmpBlinker != null) {
            tmpBlinker.interrupt();
        }
    }

    private int[] cargarArrayTipoMedidasAngulos() 
    {
        int[] medidasAngulos = new int[this.anguloBaja.length];

        if (this.anguloBaja.length == 1) 
        {
            medidasAngulos[0] = 2013;
        }

        if (this.anguloBaja.length == 2) {
            medidasAngulos[0] = 2013;
            medidasAngulos[1] = 2002;
        }

        if (this.anguloBaja.length == 3) {
            medidasAngulos[0] = 2013;
            medidasAngulos[1] = 2002;
            medidasAngulos[2] = 2022;
        }

        if (this.anguloBaja.length == 4) {
            medidasAngulos[0] = 2013;
            medidasAngulos[1] = 2002;
            medidasAngulos[2] = 2022;
            medidasAngulos[3] = 2023;
        }

        return medidasAngulos;
    }

    private int[] cargarArrayTipoMedidasBajas()
    {
        int[] medidasBajas = new int[this.valorMedidaBajaDerecha.length];

        if (this.valorMedidaBajaDerecha.length == 1) {
            medidasBajas[0] = 2014;
        }

        if (this.valorMedidaBajaDerecha.length == 2) {
            medidasBajas[0] = 2014;
            medidasBajas[1] = 2015;
        }

        if (this.valorMedidaBajaDerecha.length == 3) {
            medidasBajas[0] = 2014;
            medidasBajas[1] = 2015;
            medidasBajas[2] = 2000;
        }

        if (this.valorMedidaBajaDerecha.length == 4) {
            medidasBajas[0] = 2014;
            medidasBajas[1] = 2015;
            medidasBajas[2] = 2000;
            medidasBajas[3] = 2001;
        }

        return medidasBajas;
    }

    private int[] cargarArrayTipoMedidasAltas() 
    {
        int[] medidasAltas = new int[this.valorMedidaAltaDerecha.length];

        if (this.valorMedidaAltaDerecha.length == 1) {
            medidasAltas[0] = 2056;
        }

        if (this.valorMedidaAltaDerecha.length == 2) {
            medidasAltas[0] = 2056;
            medidasAltas[1] = 2057;
        }

        if (this.valorMedidaAltaDerecha.length == 3) {
            medidasAltas[0] = 2056;
            medidasAltas[1] = 2057;
            medidasAltas[2] = 2061;
        }

        if (this.valorMedidaAltaDerecha.length == 4) {
            medidasAltas[0] = 2056;
            medidasAltas[1] = 2057;
            medidasAltas[2] = 2061;
            medidasAltas[3] = 2062;
        }

        if (this.valorMedidaAltaDerecha.length == 5) {
            medidasAltas[0] = 2056;
            medidasAltas[1] = 2057;
            medidasAltas[2] = 2061;
            medidasAltas[3] = 2062;
            medidasAltas[4] = 2063;
        }

        if (this.valorMedidaAltaDerecha.length == 6) {
            medidasAltas[0] = 2056;
            medidasAltas[1] = 2057;
            medidasAltas[2] = 2061;
            medidasAltas[3] = 2062;
            medidasAltas[4] = 2063;
            medidasAltas[5] = 2064;
        }

        return medidasAltas;
    }

    private void logicaRegistrarMedidas(Long idPrueba, int idUsuario, int numLuces, String tipo) 
    {
        boolean defectoisInsert = false;
        boolean isAprobada = true;
        double permisibleLuces = 2.5D;
        double permisibleAnguloBajo = 0.5D;
        double permisibleAnguloAlto = 3.5D;
        try {
            this.conexion = Conex.getConnection();
            this.conexion.setAutoCommit(false);

            if (this.anguloBaja.length > 0) {
                int[] medidasAngulos = cargarArrayTipoMedidasAngulos();

                for (int i = 0; i < this.anguloBaja.length; i++) {
                    if ((this.anguloBaja[i] < permisibleAnguloBajo) || (this.anguloBaja[i] > permisibleAnguloAlto)) {
                        if (this.aplicaModificacion == 0) {
                            int codigo = tipo.equalsIgnoreCase("CICLOMOTOR") ? 21002: 24006;
                            registrarDefectos(codigo, idPrueba);
                        }
                        isAprobada = false;
                    }
                    
                    registrarMedida(medidasAngulos[i], this.anguloBaja[i], idPrueba);
                }

            }

            double suma = 0;
            if (this.valorMedidaBajaDerecha.length > 0) {
                int[] medidasBajas = cargarArrayTipoMedidasBajas();
                for (int i = 0; i < this.valorMedidaBajaDerecha.length; i++) {
                    if ((this.valorMedidaBajaDerecha[i] < permisibleLuces) || (this.valorMedidaBajaDerecha[i] < permisibleLuces)) {
                        if (this.aplicaModificacion == 0) {
                            defectoisInsert = true;
                            
                        }
                        isAprobada = false;
                    }
                    System.out.println("valor de la medida bajaderecha : " + this.valorMedidaBajaDerecha[i] + ", id de la prueba : "+ idPrueba);
                    registrarMedida(medidasBajas[i], this.valorMedidaBajaDerecha[i], idPrueba);

                    if(tipo.equalsIgnoreCase("CICLOMOTOR")){
                        int opcion = JOptionPane.showConfirmDialog(
                            null, 
                            "¿La farola #"+(i+1)+" baja se encuentra dentro de las simultaneas?", 
                            "Confirmación", 
                            JOptionPane.YES_NO_OPTION
                        );

                        if (opcion == JOptionPane.YES_OPTION) {
                            suma += this.valorMedidaBajaDerecha[i];
                        }
                    }
                }
            }

            if (!this.opcion.equals("BAJAS")) {
                if (this.valorMedidaAltaDerecha.length > 0) {
                    int[] medidasAltas = cargarArrayTipoMedidasAltas();
                    for (int i = 0; i < this.valorMedidaAltaDerecha.length; i++) {
                        if ((this.valorMedidaAltaDerecha[i] < permisibleLuces)) {
                            if (this.aplicaModificacion == 0) {
                                //defectoisInsert = true;
                            }
                           //< isAprobada = false;
                        }
                        System.out.println("valor de la medida alta derecha : " + this.valorMedidaAltaDerecha[i] + ", id de la prueba : "+ idPrueba);
                        registrarMedida(medidasAltas[i], this.valorMedidaAltaDerecha[i], idPrueba);
                        
                        if(tipo.equalsIgnoreCase("CICLOMOTOR")){
                            int opcion = JOptionPane.showConfirmDialog(
                                null, 
                                "¿La farola #"+(i+1)+" alta se encuentra dentro de las simultaneas?", 
                                "Confirmación", 
                                JOptionPane.YES_NO_OPTION
                            );
    
                            if (opcion == JOptionPane.YES_OPTION) {
                                suma += this.valorMedidaAltaDerecha[i];
                            }
                        }
                    }
                }
            }

            registrarMedida(2011, suma, idPrueba);

            if(suma > 225D && tipo.equalsIgnoreCase("CICLOMOTOR")){
                registrarDefectos(21005, idPrueba);
            }

            if (defectoisInsert) {
                int codigo = tipo.equalsIgnoreCase("CICLOMOTOR") ? 21006 : 24005;
                registrarDefectos(codigo, idPrueba);
            }

            if (this.aplicaModificacion == 0) {
                if (isAprobada) {
                    actualizarPruebas(idUsuario, idPrueba, "UPDATE pruebas SET Finalizada = 'Y',Aprobada ='Y',Abortada='N',usuario_for = ?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?");
                } else {
                    actualizarPruebas(idUsuario, idPrueba, "UPDATE pruebas SET Finalizada = 'Y',Aprobada ='N',Abortada='N',usuario_for = ?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?");
                }

            } else if (isAprobada) {
                actualizarPruebas(idUsuario, idPrueba, "UPDATE pruebas SET Finalizada = 'Y',Aprobada ='Y',Abortada='N',usuario_for = ?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?");
            } else {
                actualizarPruebas(idUsuario, idPrueba, "UPDATE pruebas SET Autorizada = 'A',usuario_for = ?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?");
            }

            this.conexion.commit();
            this.conexion.setAutoCommit(true);
            this.conexion.close();

            //if (org.soltelec.util.Mensajes.mensajePregunta("¿Desea Agregar un Comentario a la Prueba ?")) {
            //    FrmComentario frm = new FrmComentario(SwingUtilities.getWindowAncestor(this.panelLuxometro), idPrueba, JDialog.DEFAULT_MODALITY_TYPE, " ");
            //    frm.setVisible(true);
            //    frm.setModal(true);
            //}

            com.soltelec.modulopuc.utilidades.Mensajes.messageDoneTime("Se ha REGISTRADO la Prueba de Luces para la Moto de una Manera Exitosa ..¡", 3);
        } catch (SQLException ex) {
            Logger.getLogger(HiloLucesMoto.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(HiloLucesMoto.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void registrarMedidasExploradoras() 
    {
        System.out.println("----------------------------------------------------");
        System.out.println("----------registrarMedidasExploradoras---------------");
        System.out.println("----------------------------------------------------");
        try {
            if (this.medidasExploradoras.length == 1) {
                System.out.println(new StringBuilder().append("-- medidasExploradoras : ").append(this.medidasExploradoras.length).append("--").toString());
                String statement = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
                PreparedStatement instruccion = this.conexion.prepareStatement(statement);
                instruccion.setInt(1, 2058);
                instruccion.setDouble(2, this.medidasExploradoras[0]);
                instruccion.setLong(3, this.idPrueba.longValue());
                instruccion.executeUpdate();
                instruccion.clearParameters();
                System.out.println(new StringBuilder().append("-- INTENSIDAD : 2058 : ").append(this.medidasExploradoras[0]).toString());
            }
            if (this.medidasExploradoras.length == 2) {
                System.out.println(new StringBuilder().append("-- medidasExploradoras : ").append(this.medidasExploradoras.length).append("--").toString());

                String statement = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
                PreparedStatement instruccion = this.conexion.prepareStatement(statement);
                instruccion.setInt(1, 2058);
                instruccion.setDouble(2, this.medidasExploradoras[0]);
                instruccion.setLong(3, this.idPrueba.longValue());
                instruccion.executeUpdate();
                instruccion.clearParameters();
                System.out.println(new StringBuilder().append("-- INTENSIDAD : 2058 : ").append(this.medidasExploradoras[0]).toString());

                instruccion.setInt(1, 2059);
                instruccion.setDouble(2, this.medidasExploradoras[1]);
                instruccion.setLong(3, this.idPrueba.longValue());
                instruccion.executeUpdate();
                System.out.println(new StringBuilder().append("-- INTENSIDAD : 2059 : ").append(this.medidasExploradoras[1]).toString());
            }
            if (this.medidasExploradoras.length == 3) {
                String statement = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
                PreparedStatement instruccion = this.conexion.prepareStatement(statement);

                System.out.println(new StringBuilder().append("-- medidasExploradoras : ").append(this.medidasExploradoras.length).append("--").toString());

                instruccion.setInt(1, 2058);
                instruccion.setDouble(2, this.medidasExploradoras[0]);
                instruccion.setLong(3, this.idPrueba.longValue());
                instruccion.executeUpdate();
                instruccion.clearParameters();
                System.out.println(new StringBuilder().append("-- INTENSIDAD : 2058 : ").append(this.medidasExploradoras[0]).toString());

                instruccion.setInt(1, 2059);
                instruccion.setDouble(2, this.medidasExploradoras[1]);
                instruccion.setLong(3, this.idPrueba.longValue());
                instruccion.executeUpdate();
                instruccion.clearParameters();
                System.out.println(new StringBuilder().append("-- INTENSIDAD : 2059 : ").append(this.medidasExploradoras[1]).toString());

                instruccion.setInt(1, 2060);
                instruccion.setDouble(2, this.medidasExploradoras[2]);
                instruccion.setLong(3, this.idPrueba.longValue());
                instruccion.executeUpdate();
                System.out.println(new StringBuilder().append("-- INTENSIDAD : 2060 : ").append(this.medidasExploradoras[2]).toString());
            }
            if (this.medidasExploradoras.length == 4) {
                System.out.println(new StringBuilder().append("-- medidasExploradoras : ").append(this.medidasExploradoras.length).append("--").toString());

                String statement = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
                PreparedStatement instruccion = this.conexion.prepareStatement(statement);
                instruccion.setInt(1, 2058);
                instruccion.setDouble(2, this.medidasExploradoras[0]);
                instruccion.setLong(3, this.idPrueba.longValue());
                instruccion.executeUpdate();
                instruccion.clearParameters();
                System.out.println(new StringBuilder().append("-- INTENSIDAD : 2058 : ").append(this.medidasExploradoras[0]).toString());

                instruccion.setInt(1, 2059);
                instruccion.setDouble(2, this.medidasExploradoras[1]);
                instruccion.setLong(3, this.idPrueba.longValue());
                instruccion.executeUpdate();
                instruccion.clearParameters();
                System.out.println(new StringBuilder().append("-- INTENSIDAD : 2059 : ").append(this.medidasExploradoras[1]).toString());

                instruccion.setInt(1, 2060);
                instruccion.setDouble(2, this.medidasExploradoras[2]);
                instruccion.setLong(3, this.idPrueba.longValue());
                instruccion.executeUpdate();
                instruccion.clearParameters();
                System.out.println(new StringBuilder().append("-- INTENSIDAD : 2060 : ").append(this.medidasExploradoras[2]).toString());

                instruccion.setInt(1, 2055);
                instruccion.setDouble(2, this.medidasExploradoras[3]);
                instruccion.setLong(3, this.idPrueba.longValue());
                instruccion.executeUpdate();
                System.out.println(new StringBuilder().append("-- INTENSIDAD : 2055 : ").append(this.medidasExploradoras[3]).toString());
            }
        } catch (SQLException e) {
            System.out.println(new StringBuilder().append("Error en el metodo :registrarMedidasExploradoras()").append(e).toString());
        }
    }

    private void registrarDefectos(int codigo, Long idPrueba) 
    {
        String query = "INSERT INTO defxprueba(id_defecto,id_prueba,Tipo_defecto) VALUES (?,?,'A')";
        try {
            PreparedStatement psDefectos = this.conexion.prepareStatement(query);
            psDefectos.clearParameters();
            psDefectos.setInt(1, codigo);
            psDefectos.setLong(2, idPrueba.longValue());
            psDefectos.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(HiloLucesMoto.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void actualizarPruebas(int idUsuario, Long idPrueba, String query) {
        try {
            PreparedStatement instruccion2 = this.conexion.prepareStatement(query);
            instruccion2.setInt(1, idUsuario);
            instruccion2.setString(2, consultarSerialEquipo(idPrueba));
            instruccion2.setLong(3, idPrueba.longValue());
            int i = instruccion2.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(HiloLucesMoto.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String consultarSerialEquipo(Long idPrueba) 
    {
        System.out.println("---------------------------------------------------");
        System.out.println("----       consultarSerialEquipo   ----------------");
        System.out.println("---------------------------------------------------");
        try {
            return ConsultarDatosVehiculo.buscarSerialEquipo(idPrueba.longValue());
        } catch (Exception e) {
            System.out.println(new StringBuilder().append(" Error en el metodo :consultarSerialEquipo()").append(e).toString());
            System.out.println(new StringBuilder().append(" Serial no encontrado ").append(e).toString());
        }
        return "Serial no encontrado";
    }

    private void registrarMedida(int codigo, double valor, Long idPrueba) 
    {
        String query = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
        try {
            PreparedStatement instruccion = this.conexion.prepareStatement(query);
            instruccion.setInt(1, codigo);
            instruccion.setDouble(2, valor);
            instruccion.setLong(3, idPrueba.longValue());
            instruccion.execute();
            instruccion.clearParameters();
        } catch (SQLException ex) {
            Logger.getLogger(HiloLucesMoto.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean verificarMedidas(double[] medidas) 
    {
        for (double d : medidas) {
            if (d <= 0.0D) {
                return false;
            }
        }
        return true;
    }

    private void registrarCancelacion(String comentario, Long idPrueba, int idUsuario)
    {
        try {
            String serialEquipo = "";
            try {
                serialEquipo = ConsultarDatosVehiculo.buscarSerialEquipo(idPrueba.longValue());
            } catch (Exception e) {
                serialEquipo = "Serial no encontrado";
            }

            Connection conexion = Conex.getConnection();

            String statement = "UPDATE pruebas SET Finalizada = 'Y',Aprobada='N',Abortada='Y',Comentario_aborto=?,usuario_for = ?,Fecha_aborto=?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?";
            PreparedStatement instruccion = conexion.prepareStatement(statement);
            instruccion.setString(1, comentario);
            instruccion.setInt(2, idUsuario);
            instruccion.setString(4, serialEquipo);
            instruccion.setLong(5, idPrueba.longValue());
            Date d = new Date();
            Timestamp timestamp = new Timestamp(d.getTime());
            instruccion.setTimestamp(3, timestamp);
            com.soltelec.modulopuc.utilidades.Mensajes.messageDoneTime("Se ha cancelado la Prueba de Luces para la Moto de una manera Exitosa ..¡", 3);

            int n = instruccion.executeUpdate();
            conexion.close();
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this.panelLuxometro, "Error no se encuentra el driver de MySQL");
            System.out.println(e);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this.panelLuxometro, "Error conectandose con la base de datos");
            System.out.println(e);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this.panelLuxometro, "Error conectandose con la base de datos");
            System.out.println(e);
        }
    }

    public Long getIdPrueba() {
        return this.idPrueba;
    }

    public void setIdPrueba(Long idPrueba) {
        this.idPrueba = idPrueba;
    }

    public int getIdUsuario() {
        return this.idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    private void cargarUrl() {
        Properties props = new Properties();
        try {
            props.load(new FileInputStream("./propiedades.properties"));
            this.urljdbc = props.getProperty("urljdbc");
            System.out.println(this.urljdbc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setIdHojaPrueba(int theIdHojaPruebas) {
        this.idHojaPrueba = theIdHojaPruebas;
    }

    private boolean cargarParametrosVehiculo()
            throws ClassNotFoundException, SQLException {
        return false;
    }

    static enum Luz {
        ALTADERECHA(1), ALTAIZQUIERDA(2), BAJADERECHA(3), BAJAIZQUIERDA(4), EXPLORADORA(5);

        private int numeroLuz;

        private Luz(int numLuz) {
            this.numeroLuz = numLuz;
        }

        public void setNumeroLuz(int numeroLuz) {
            this.numeroLuz = numeroLuz;
        }
    }
}
