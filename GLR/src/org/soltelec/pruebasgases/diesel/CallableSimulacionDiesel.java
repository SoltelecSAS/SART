/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.pruebasgases.diesel;

import com.soltelec.opacimetro.brianbee.HiloTomaDatosDieselBrianBee;
import com.soltelec.opacimetro.brianbee.OpacimetroBrianBee;
import eu.hansolo.steelseries.tools.BackgroundColor;
import eu.hansolo.steelseries.tools.LcdColor;
import java.awt.Component;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import org.apache.log4j.Logger;
import org.soltelec.medicionrpm.MedidorRevTemp;
import org.soltelec.procesosopacimetro.Opacimetro;
import org.soltelec.pruebasgases.PanelPruebaGases;
import org.soltelec.pruebasgases.SimuladorRpm;
import org.soltelec.util.AproximacionMedidas;
import org.soltelec.util.ConsultarDatosVehiculo;
import org.soltelec.util.MedicionOpacidad;
import org.soltelec.util.MedidaGeneral;
import org.soltelec.util.ShiftRegister;
import org.soltelec.util.UtilGasesModelo;
import org.soltelec.util.UtilPropiedades;

/**
 * Clase que simula las revoluciones y toma las medidas de los datos. esta es
 * completamente dependiente del opacimetro Sensors (2012-01-22)
 *
 * @author Gerencia Desarrollo de Soluciones Tecnologicas
 */
public class CallableSimulacionDiesel implements Callable<List<MedidaGeneral>> {

    private int contadorSimulacion;
    private Timer timerSimulacion;
    private int contadorTemporizacion;
    private double rpmSimuladas;
    private Timer timer;
    private DialogoCiclosAceleracion dialogo;//la interfaz grafica
    private int T_ANTES_ACELERAR = 18;
    private boolean simuladasRalenti = true;
    private double velRalenti = 700;
    private Random random = new Random();
    private boolean subidaAceleracion = false;//se controla la simulacion con booleanos
    private double velCrucero = 4500;
    private boolean simuladasCrucero;
    private boolean bajadaAceleracion;
    //private HiloTomaDatosDiesel hiloTomaDatos;
    //private Thread t;
    private Opacimetro opacimetro;
    private List<Byte> listaCiclo0, listaCiclo1, listaCiclo2, listaCiclo3;
    private List[] arregloListaCiclos = new ArrayList[4];
    //private ArrayList<ArregloOpacidadCiclo> listaDatos;
    int numeroPruebasUnitarias = 0;
    private PanelPruebaGases panel;
    private SimuladorRpm simuladorRpm;
    private MedidorRevTemp medidorRevTemp;
    private int rpm;
    private HiloTomaDatosDiesel hiloTomaDatos;
    private Thread t;
    private ShiftRegister shiftRegisterVelocidad;
    private int temp;
    private ArrayList<Double> listaCiclo0CorreBerLambert = new ArrayList<Double>();
    ;
    private ArrayList<Double> listaCiclo1CorreBerLambert = new ArrayList<Double>();
    private ArrayList<Double> listaCiclo2CorreBerLambert = new ArrayList<Double>();
    private ArrayList<Double> listaCiclo3CorreBerLambert = new ArrayList<Double>();
    private ArrayList<Double> listaCiclo0Filtrada;
    private ArrayList<Double> listaCiclo1Filtrada;
    private ArrayList<Double> listaCiclo2Filtrada;
    private ArrayList<Double> listaCiclo3Filtrada;
    private ArrayList<Double> listaCiclo1FilterBuffer;
    private ArrayList<Double> listaCiclo2FilterBuffer;
    private ArrayList<Double> listaCiclo3FilterBuffer;
    private ArrayList<Double> listaCiclo0FilterBuffer;
    private DecimalFormat df2;
    private CausasAbortoDiesel causaUltimoIntento, causaPenultimoIntento;

    private double diametroExosto;
    private JDialog d;
    private ArrayList<Double> listaDoubleCiclo0;
    private ArrayList<Double> listaDoubleCiclo1;
    private ArrayList<Double> listaDoubleCiclo2;
    private ArrayList<Double> listaDoubleCiclo3;
    public static List<MedidaGeneral> listaMedidasTemp;

    /**
     * Simula las revoluciones para los ciclos de medicion de opacidad retorna
     * una lista de listas con las mediciones tomadas.
     *
     * @param d
     * @param op
     */
    public CallableSimulacionDiesel() {
    }

    public CallableSimulacionDiesel(Opacimetro op, PanelPruebaGases panel, double diametroExosto, long idHojaPrueba) {

        //dialogo.getButtonCancelar().addActionListener(this);
        this.opacimetro = op;
        timer = new Timer(1000, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                contadorTemporizacion++;
            }//end of method actionPerformed

        });

        timerSimulacion = new Timer(250, new ListenerSimulacion());
        this.panel = panel;
        simuladorRpm = new SimuladorRpm();
        Random r = new Random();
        //TODO: Agregar validacion de vehiculos pesados y livianos
        try {
            ConsultarDatosVehiculo datosVehiculo = new ConsultarDatosVehiculo();
            Integer idTipoVehiculo = datosVehiculo.buscarTipoVehiculo(idHojaPrueba);
            switch (idTipoVehiculo) {
                case 1:
                case 2:
                case 6:
                    velCrucero = 3800 - r.nextInt(500);//Livianos
                    System.out.println("Velocidad crucero liviano");
                    break;
                case 3:
                case 7:
                    velCrucero = 2600 - r.nextInt(350);//Pesados
                    System.out.println("Velocidad crucero pesado");
                    break;
                default:
                    velCrucero = 2200 - r.nextInt(500);
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Error: no se pudo establecer los valores de velocidad crucero. " + e);
            System.out.println("Se dejan valores por defecto de velocidad crucero: 2200");
            velCrucero = 2200 - r.nextInt(500);
        }

//        velRalenti =  800 - r.nextInt(350);// Se modifica ya que esta arrojando valores ireales
        velRalenti = 800 - r.nextInt(280);
//        velCrucero = 2200 - r.nextInt(500);
        simuladorRpm.setREVOLUCIONES_CRUCERO((int) velCrucero);
        simuladorRpm.setREVOLUCIONES_RALENTI((int) velRalenti);
        simuladorRpm.setSimularCrucero(false);
        this.medidorRevTemp = simuladorRpm;
        this.diametroExosto = diametroExosto;
        df2 = new DecimalFormat("##.##");
        causaUltimoIntento = CausasAbortoDiesel.CICLO_NO_REALIZADO_AUN;
        causaPenultimoIntento = CausasAbortoDiesel.CICLO_NO_REALIZADO_AUN;

    }//end of constructor

    @Override
    public List<MedidaGeneral> call() throws Exception {
        timer.start();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Void> futureSimulador = executor.submit(simuladorRpm);
        pedirAceleracionSuave();

        detectarRalentiGobernadas();

        futureSimulador.cancel(true);
        simuladorRpm.stop();
        executor.shutdown();
        timerSimulacion.start();//timer de 250 milisegundos para calcular los valores de la simulacion

        panel.getPanelFiguras().setVisible(false);
        panel.getPanelMensaje().setVisible(true);
        Component icono = panel.getPanelMensaje().getComponent(0);
        Component msg = panel.getPanelMensaje().getComponent(1);
        panel.getPanelMensaje().add(icono, 0);
        Font fAlt = new Font(Font.SERIF, Font.BOLD, 61);
        msg.setFont(fAlt);
        panel.getPanelMensaje().updateUI();
        panel.getPanelMensaje().repaint();
        Thread.sleep(100);
        panel.getPanelMensaje().setText("POR FAVOR \n  ACELERE A GOBERNADAS EN MENOS DE 5 seg CUANDO SE LE INDIQUE");
        Thread.sleep(2500);
        probarAceleracionGobernada();
        panel.getPanelFiguras().setVisible(false);
        panel.getPanelMensaje().setVisible(true);
        panel.getPanelMensaje().setText("INICIO DE LA PRUEBA UNITARIA");
        Thread.sleep(2500);

        try {
            contadorTemporizacion = 0;
            List<Double> listaMaximos = null;
            boolean pruebaExitosa = false;
            while (numeroPruebasUnitarias < 3 && !pruebaExitosa) {
                JOptionPane.showMessageDialog(null, "Por Favor \n INTRODUZCA la sonda de prueba");

                int ciclos = 0;
                mostrarDlgCiclos();
                dialogo.repintarRangos(velRalenti, velCrucero);

                while (ciclos < 4) {
                    dialogo.getDisplayTemperatura().setLcdValue(medidorRevTemp.getTemp());
                    if (!(opacimetro instanceof OpacimetroBrianBee)) {
                        hiloTomaDatos = new HiloTomaDatosDiesel();
                        hiloTomaDatos.setNumeroCiclo(ciclos);
                        hiloTomaDatos.setOpacimetro(opacimetro);
                    } else {
                        hiloTomaDatos = new HiloTomaDatosDieselBrianBee();
                        hiloTomaDatos.setOpacimetro(opacimetro);
                    }
                    t = new Thread(hiloTomaDatos);//inicia un nuevo hilo por ciclo deberia ser una variable local                    

                    T_ANTES_ACELERAR = 6;

                    simuladasRalenti = true;//subida aceleracion = false EL SIMULADOR EMPIEZA A RETORNAR LA VELOCIDAD RALENTI
                    contadorTemporizacion = 0;

                    dialogo.getLabelMensaje().setText("Ciclo: " + (ciclos));
                    dialogo.getLedRojo().setLedBlinking(false);
                    dialogo.getLedRojo().setLedOn(false);
                    //en este momento se espera que el vehiculo este en ralenti
                    dialogo.getLabelMensaje().setText("MANTENGA RALENTI CICLO:" + (ciclos));
                    contadorTemporizacion = 0;
                    dialogo.getDisplayTiempo().setLcdColor(LcdColor.BLACK_LCD);

                    while (contadorTemporizacion < T_ANTES_ACELERAR) {

                        Thread.sleep(100);
                        semaforo();//metodo que enciende o apaga los leds segun el caso
                        dialogo.getDisplayTiempo().setLcdValue(T_ANTES_ACELERAR - contadorTemporizacion);
                        dialogo.getRadialTacometro().setValue(rpmSimuladas);

                    }//end while

                    Thread.sleep(50);//proporciona un punto de salida
                    semaforo();
                    //ACELERACION EN MENOS DE 5S
                    contadorTemporizacion = 0;
                    contadorSimulacion = 0;
                    simuladasRalenti = false;
                    subidaAceleracion = true;//EL SIMULADOR INICIA A SUBIR LAS ACELERACIONES HASTA GOBERNADAS

                    //INICIO TOMA DE DATOS
                    t.start();
                    hiloTomaDatos.setNumeroCiclo(ciclos);

                    System.err.println("CICLO: " + ciclos);

                    dialogo.getDisplayTiempo().setLcdValue(contadorTemporizacion);
                    //Aceleracion o subida
                    while (contadorTemporizacion < 5 && rpmSimuladas <= velCrucero - 125) {

                        Thread.sleep(50);
                        dialogo.getDisplayTiempo().setLcdValue(contadorTemporizacion);
                        dialogo.getRadialTacometro().setValue(rpmSimuladas);

                    }//end of while
                    //El vehiculo esta acelerado hasta las velocidades gobrnadas
                    subidaAceleracion = false;
                    contadorTemporizacion = 0;
                    simuladasCrucero = true;//EL SIMULADOR EMPIEZA A RETORNAR LAS VELOCIDADES GOBERNADAS
                    dialogo.getLabelMensaje().setText("CICLO:" + ciclos + " GOBERNADAS!!!");
                    dialogo.getDisplayTiempo().setLcdColor(LcdColor.BLUE2_LCD);
                    //Mantener la aceleracion

                    while (contadorTemporizacion < 4 && (rpmSimuladas >= (velCrucero - 250) && rpmSimuladas <= (velCrucero + 150))) {

                        Thread.sleep(50);
                        dialogo.getDisplayTiempo().setLcdValue(contadorTemporizacion);
                        dialogo.getRadialTacometro().setValue(rpmSimuladas);

                    }

                    simuladasCrucero = false;
                    bajadaAceleracion = true;//EL SIMULADOR EMPIEZA A DESACELERAR HASTA RALENTI
                    contadorSimulacion = 0;
                    contadorTemporizacion = 0;
                    dialogo.getDisplayTiempo().setLcdColor(LcdColor.RED_LCD);
                    dialogo.getLedRojo().setLedOn(true);
                    dialogo.getLedVerde().setLedOn(false);
                    dialogo.getLabelMensaje().setText("CICLO: " + ciclos + "POR FAVOR SUELTE PEDAL!");

                    //bajada de la aceleracion
                    while (contadorTemporizacion <= 15) {
                        Thread.sleep(150);
                        dialogo.getDisplayTiempo().setLcdValue(contadorTemporizacion);
                        dialogo.getRadialTacometro().setValue(rpmSimuladas);
                    }

                    hiloTomaDatos.setCicloTerminado(true);//finalizaria la toma de datos
                    Thread.sleep(900);

                    if (ciclos == 0) {
                        listaCiclo0 = hiloTomaDatos.getListaOpacidad();
                    }
                    if (ciclos == 1) {
                        listaCiclo1 = hiloTomaDatos.getListaOpacidad();//Son hilos distintos todos                          
                    }
                    if (ciclos == 2) {
                        listaCiclo2 = hiloTomaDatos.getListaOpacidad();//pero la toma de datos no se afecta                          
                    }
                    if (ciclos == 3) {
                        listaCiclo3 = hiloTomaDatos.getListaOpacidad();//entonces diseñar acorde la clase                          
                    }

                    hiloTomaDatos.setCicloTerminado(true);
                    hiloTomaDatos.setInterrumpido(true);
                    hiloTomaDatos.setTerminado(true);

                    bajadaAceleracion = false;
                    contadorSimulacion = 0;
                    System.err.println("FIN CICLO:" + ciclos);
                    System.out.println("Debe salir");
                    ciclos++;
                }//en while ciclos

                arregloListaCiclos[0] = listaCiclo0;
                arregloListaCiclos[1] = listaCiclo1;
                arregloListaCiclos[2] = listaCiclo2;
                arregloListaCiclos[3] = listaCiclo3;

                Thread.sleep(1500);
                List<List<Byte>> listaDatos = new ArrayList<List<Byte>>();
                listaDatos.add(listaCiclo0);
                listaDatos.add(listaCiclo1);
                listaDatos.add(listaCiclo2);
                listaDatos.add(listaCiclo3);

                listaMaximos = procesarDatos(listaDatos);

                generarArchivo();
                List<Double> listaUltimosTresCiclos = new ArrayList<Double>();
                listaUltimosTresCiclos.add(listaMaximos.get(1));
                listaUltimosTresCiclos.add(listaMaximos.get(2));
                listaUltimosTresCiclos.add(listaMaximos.get(3));

                double maximo = Collections.max(listaUltimosTresCiclos);
                double minimo = Collections.min(listaUltimosTresCiclos);

                int comparador = 10;
                if (Boolean.parseBoolean(UtilPropiedades.cargarPropiedad("ltoe", "propiedades.properties"))) {
                    if (Integer.parseInt(UtilPropiedades.cargarPropiedad("ltoe_valor", "propiedades.properties")) == 230) {
                        comparador = 5;
                    }
                }
//                if (Math.abs(maximo - minimo) > Opacimetro.LIMITE_DIFERENCIA) {

                if (Math.abs(maximo - minimo) > 0.5) {
                    if (causaUltimoIntento == CausasAbortoDiesel.DIFERENCIA_ARITMETICA_CICLOS && causaPenultimoIntento == CausasAbortoDiesel.DIFERENCIA_ARITMETICA_CICLOS) {
                        //numeral 3.2.5 pruebas no validas    
                        System.out.println("------------------------------ENTRE RECHAZADA---------------------------------------");
                        JOptionPane.showMessageDialog(null, " Prueba rechazada por que presenta diferencia entre los ciclos"
                                + "mayor a " + 0.5 + "m^-1 durante tres veces consecutivas norma 4231 numeral 3.2.5 b");

                        listaMedidasTemp = armarListas(listaMaximos, velRalenti, velCrucero, medidorRevTemp.getTemp());
                        DeficienteOperacionException.tipoException = "La diferencia aritmetica entre el valor mayor y menor de opacidad de las tres aceleraciones.";
                        throw new DeficienteOperacionException();
                    } else {
                        causaPenultimoIntento = causaUltimoIntento;
                        causaUltimoIntento = CausasAbortoDiesel.DIFERENCIA_ARITMETICA_CICLOS;
                        numeroPruebasUnitarias++;
                        JOptionPane.showMessageDialog(null, "Diferencia aritmetica de ciclos: maximo: " + maximo + " minimo: " + minimo);
                        d.dispose();
                        continue;//vuelve a hacer los cuatro ciclos
                    }

                }//end of if maximo - minimo

                d.dispose();
                JOptionPane.showMessageDialog(null, "Retire la sonda del tubo de escape");

                panel.getPanelFiguras().setVisible(false);
                panel.getPanelMensaje().setVisible(true);
                panel.getPanelMensaje().setText("Verificacion de opacidad despues de los ciclos");
                panel.getMensaje().setText("");
                Thread.sleep(2500);

                MedicionOpacidad medicion = opacimetro.obtenerDatos();
                ShiftRegister shiftOpacidad = new ShiftRegister(8, medicion.getOpacidadDouble());
                contadorTemporizacion = 0;
                while (contadorTemporizacion < 5) {
                    medicion = opacimetro.obtenerDatos();
                    shiftOpacidad.ponerElemento(medicion.getOpacidadDouble());
                    panel.getPanelMensaje().setText("Opacidad: " + df2.format(medicion.getOpacidadDouble()) + " t: " + contadorTemporizacion);
                    Thread.sleep(50);

                }

                if (shiftOpacidad.media() >= Opacimetro.LIMITE_RESIDUOS) {
                    if (causaPenultimoIntento == CausasAbortoDiesel.DOS_PORCIENTO_DESPUES_CICLOS && causaUltimoIntento == CausasAbortoDiesel.DOS_PORCIENTO_DESPUES_CICLOS) {

                        JOptionPane.showMessageDialog(null, "Debe repetir la prueba cuando se hayan resuelto las anomalias del opacimetro\n"
                                + "Norma tecnica 4231 numeral 3.2.5 a");
                        return null;

                    } else {

                        JOptionPane.showMessageDialog(null, "Repetir la prueba porque el opacimetro mide \n"
                                + "mas de 0.15 m^-1 opacidad:" + shiftOpacidad.media());
                        d.dispose();
                        if (numeroPruebasUnitarias < 3) {
                            ajusteInicialMinimoMaximo();
                        }
                        causaPenultimoIntento = causaUltimoIntento;
                        causaUltimoIntento = CausasAbortoDiesel.DOS_PORCIENTO_DESPUES_CICLOS;
                        numeroPruebasUnitarias++;
                        continue;
                    }
                }//end of if opacidad >= 2

                if (numeroPruebasUnitarias >= 3) {
                    JOptionPane.showMessageDialog(null, "No se pueden realizar mas de tres pruebas unitarias de aceleracion "
                            + " numeral 3.2.2 norma 4231");
                    throw new DeficienteOperacionException();
                }
                panel.getPanelMensaje().setText("Prueba terminada con exito");
                pruebaExitosa = true;
            }//end while intentos
            timer.stop();
            timerSimulacion.stop();

            List<MedidaGeneral> listaMedidas = armarListas(listaMaximos, velRalenti, velCrucero, medidorRevTemp.getTemp());

            return listaMedidas;
        } catch (InterruptedException ie) {

            if (hiloTomaDatos != null) {

                hiloTomaDatos.setCicloTerminado(true);
                hiloTomaDatos.setInterrumpido(true);
                hiloTomaDatos.setTerminado(true);

            }

            Thread.sleep(1000);

            throw new CancellationException("prueba Cancelada");//PROPAGA LA EXCEPCION DE TAL MANRA QUE SEA ATRAPADA POR EL WORKERCICLOSOPACIDAD

        }//end catch InterruptedException
        catch (Exception exc) {//Excepcion inesperada durante la prueba

            if (hiloTomaDatos != null) {

                hiloTomaDatos.setCicloTerminado(true);
                hiloTomaDatos.setInterrumpido(true);
                hiloTomaDatos.setTerminado(true);

            }
            Logger.getRootLogger().error("Error durante la prueba de opacidad", exc);
            exc.printStackTrace();
            throw exc;//Propaga la excepcion de tal manera que sea atrapada por la clase WorkerCiclosDiesel

        } finally {
            salir();
        }

    }

    /**
     * Para los timers, cierra el dialogo y cierra el puerto serial del
     * opacimetro
     */
    private void salir() {//cambio de revoluciones no debe cerrar el panel, pero debe cerrar siempre el dialogo de ciclos.
        if (timer != null) {
            timer.stop();
        }
        if (timerSimulacion != null) {
            timerSimulacion.stop();
        }
        if (dialogo != null) {
            dialogo.setPruebaCancelada(true);
            dialogo.cerrar();
        }

    }

    private void liberarRecursos() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public ListenerSimulacion listenerSimulacion() {
        return new ListenerSimulacion();
    }

    class ListenerSimulacion implements ActionListener {//listener que se ejecuta cada segundo... controla la simulacion
        //las variables booleanas se van modificando en el hilo principal
        //segun el tiempo vaya transcurriendo

        @Override
        public void actionPerformed(ActionEvent e) {
            if (simuladasRalenti && !subidaAceleracion) {//debe permanecer en velocidad de ralenti
                rpmSimuladas = velRalenti + (15 - random.nextInt(30));
                rpmSimuladas = rpmSimuladas - rpmSimuladas % 10;//siempre ser multiplo de diez
            }
            if (subidaAceleracion && !simuladasCrucero) {//subiendo  es decir aceleracion desde ralenti a crucero
                contadorSimulacion++;
                rpmSimuladas = ((velCrucero - velRalenti) * (1 - Math.exp(0 - (0.25 - random.nextDouble() * 0.03) * contadorSimulacion)) + (15 - random.nextInt(30))) + velRalenti;
                rpmSimuladas = rpmSimuladas - rpmSimuladas % 10;
            }
            if (simuladasCrucero && !simuladasRalenti && !subidaAceleracion) {//mantener la velocidad de crucero
                rpmSimuladas = ((velCrucero) + (15 - random.nextInt(30)));
                rpmSimuladas = rpmSimuladas - rpmSimuladas % 10;
            }
            if (bajadaAceleracion && !simuladasRalenti && !subidaAceleracion && !simuladasCrucero) {//bajada de aceleacion
                contadorSimulacion++;
                rpmSimuladas = (velRalenti + (velCrucero - velRalenti) * (Math.exp((-0.12 - random.nextDouble() * 0.01) * contadorSimulacion)) + (16 - random.nextInt(28)));
                rpmSimuladas = rpmSimuladas - rpmSimuladas % 10;
            }

        }

    }

    private void semaforo() {

        if (contadorTemporizacion == (T_ANTES_ACELERAR - 5)) {
            dialogo.getLabelMensaje().setText("5s RESTANTES");
            dialogo.getDisplayTiempo().setLcdColor(LcdColor.RED_LCD);
            dialogo.getLedRojo().setLedOn(true);
            dialogo.getLedAmarillo().setLedOn(false);
            dialogo.getLedVerde().setLedOn(false);
        }
        if (contadorTemporizacion == (T_ANTES_ACELERAR - 3)) {
            dialogo.getLabelMensaje().setText("3s!");
            dialogo.getLedAmarillo().setLedOn(true);
            //dialogo.getDisplayTiempo().setLcdColor(LcdColor.YELLOW_LCD);
            dialogo.getLedRojo().setLedOn(false);
            dialogo.getLedVerde().setLedOn(false);
        }
        if (contadorTemporizacion == (T_ANTES_ACELERAR)) {
            dialogo.getDisplayTiempo().setLcdColor(LcdColor.YELLOW_LCD);
            dialogo.getLedVerde().setLedOn(true);
            dialogo.getLedRojo().setLedOn(false);
            dialogo.getLedAmarillo().setLedOn(false);
            dialogo.getLabelMensaje().setText("ACELERE!!!!!!");
        }
    }

    private void pedirAceleracionSuave() throws HeadlessException, InterruptedException, DeficienteOperacionException {
        //ACELERACION SUAVE
        panel.getPanelMensaje().setVisible(true);
        panel.getPanelFiguras().setVisible(false);
        panel.getPanelMensaje().setText("POR FAVOR \n REALICE UNA ACELERACION SUAVE ");
        Thread.sleep(2500);
        int opcion = JOptionPane.showConfirmDialog(null, "Fue correcta la operacion de los sistemas de control de velocidad de giro?", "SART 1.7.3.- Control de Giro", JOptionPane.YES_NO_OPTION);

        if (opcion == JOptionPane.NO_OPTION) {

            //El usuario indica deficientes condiciones de operacion          
            //se lanza una excepcion (yo se yo se mala practica de programacion controlar el flujo con escepciones)
            liberarRecursos();
            DeficienteOperacionException.tipoException = "Deficiente Operacion del Sistema de Control de Velocidad de Giro del Motor; segun Num 3.1.3.10";
            listaMedidasTemp = new ArrayList<MedidaGeneral>();
            throw new DeficienteOperacionException();
        }
    }//end of method pedirAceleracionSuave

    private void detectarRalentiGobernadas() throws InterruptedException {

        panel.getPanelMensaje().setText("POR FAVOR \n PONGA EL VEHICULO EN VELOCIDAD RALENTI O MINIMA");
        Thread.sleep(2500);

        panel.getButtonRpm().setActionCommand("Mal funcionamiento Motor");
        panel.getButtonRpm().setVisible(true);
        panel.getPanelMensaje().setVisible(false);
        panel.getPanelFiguras().setVisible(true);

        //Durante diez segundos tomar la velocidad ralenti
        simuladorRpm.setSimularCrucero(false);
        //simula la velocidad ralenti
        //simulacion
        medidorRevTemp.getRpm();
        rpm = medidorRevTemp.getRpm();
        shiftRegisterVelocidad = new ShiftRegister(4, rpm);
        contadorTemporizacion = 0;
        temp = medidorRevTemp.getTemp();
        while (contadorTemporizacion < 10) {

            rpm = medidorRevTemp.getRpm();
            panel.getRadialTacometro().setValue(rpm);
            panel.getLinearTemperatura().setValue(medidorRevTemp.getTemp());

            if (contadorTemporizacion >= 5) {
                shiftRegisterVelocidad.ponerElemento(rpm);
            }

            if (shiftRegisterVelocidad.isStable()) {
                panel.getMensaje().setText("Estable  t:" + contadorTemporizacion);
            }
            if (shiftRegisterVelocidad.isAcelerando()) {
                panel.getMensaje().setText("Acelerando t:" + contadorTemporizacion);
            }
            Thread.sleep(100);

        }//end of while de velocidad 

        velRalenti = shiftRegisterVelocidad.media();
        panel.getPanelFiguras().setVisible(false);
        panel.getPanelMensaje().setVisible(true);
        panel.getPanelMensaje().setText("POR FAVOR ACELERE A GOBERNADAS");
        //poner el simulador a trabajar

        //
        Thread.sleep(1500);

        panel.getPanelMensaje().setVisible(false);
        panel.getPanelFiguras().setVisible(true);
        simuladorRpm.setSimularCrucero(true);
        contadorTemporizacion = 0;
        boolean noCrearDenuevo = true;

        while (contadorTemporizacion < 12) {

            rpm = medidorRevTemp.getRpm();
            panel.getRadialTacometro().setValue(rpm);
            panel.getLinearTemperatura().setValue(medidorRevTemp.getTemp());
            if (contadorTemporizacion == 9 && noCrearDenuevo) {
                shiftRegisterVelocidad = new ShiftRegister(4, rpm);
                noCrearDenuevo = false;
            }
            panel.getMensaje().setText("TIEMPO: " + contadorTemporizacion);
            if (contadorTemporizacion > 10) {

                panel.getMensaje().setText("POR FAVOR. MANTENGA A GOBERNADAS");
                shiftRegisterVelocidad.ponerElemento(rpm);
            }
            Thread.sleep(50);
        }

        velCrucero = shiftRegisterVelocidad.media();

    }

    /**
     * Retorna el maximo de los 4 ciclos y el promedio de los tres ciclos
     * validos
     *
     * @param listaCiclosByte
     * @return
     */
    public List<Double> procesarDatos(List<List<Byte>> listaCiclosByte) {
        //sacar los datos
        listaDoubleCiclo0 = new ArrayList<Double>();
        listaDoubleCiclo1 = new ArrayList<Double>();
        listaDoubleCiclo2 = new ArrayList<Double>();
        listaDoubleCiclo3 = new ArrayList<Double>();

        List<Byte> listaByteCiclo0 = listaCiclosByte.get(0);
        List<Byte> listaByteCiclo1 = listaCiclosByte.get(1);
        List<Byte> listaByteCiclo2 = listaCiclosByte.get(2);
        List<Byte> listaByteCiclo3 = listaCiclosByte.get(3);

        for (int i = 0; i < listaByteCiclo0.size() / 2; i++) {
            listaDoubleCiclo0.add((new Integer(UtilGasesModelo.fromBytesToShort(new byte[]{listaByteCiclo0.get(2 * i + 1), listaByteCiclo0.get(2 * i)}))) * 0.1);
        }

        for (int i = 0; i < listaByteCiclo1.size() / 2; i++) {
            listaDoubleCiclo1.add((new Integer(UtilGasesModelo.fromBytesToShort(new byte[]{listaByteCiclo1.get(2 * i + 1), listaByteCiclo1.get(2 * i)}))) * 0.1);
        }
        for (int i = 0; i < listaByteCiclo2.size() / 2; i++) {
            listaDoubleCiclo2.add((new Integer(UtilGasesModelo.fromBytesToShort(new byte[]{listaByteCiclo2.get(2 * i + 1), listaByteCiclo2.get(2 * i)}))) * 0.1);
        }
        for (int i = 0; i < listaByteCiclo3.size() / 2; i++) {
            listaDoubleCiclo3.add((new Integer(UtilGasesModelo.fromBytesToShort(new byte[]{listaByteCiclo3.get(2 * i + 1), listaByteCiclo3.get(2 * i)}))) * 0.1);
        }

        //Filtrar y corregir los datos
        if (!(opacimetro instanceof OpacimetroBrianBee)) {
            listaCiclo0Filtrada = filtrarLista(listaDoubleCiclo0, listaCiclo0CorreBerLambert);
            listaCiclo1Filtrada = filtrarLista(listaDoubleCiclo1, listaCiclo1CorreBerLambert);
            listaCiclo2Filtrada = filtrarLista(listaDoubleCiclo2, listaCiclo2CorreBerLambert);
            listaCiclo3Filtrada = filtrarLista(listaDoubleCiclo3, listaCiclo3CorreBerLambert);
        } else {//No se puede filtrar cuando el opacimetro es BrianBee
            listaCiclo0Filtrada = listaDoubleCiclo0;
            listaCiclo1Filtrada = listaDoubleCiclo1;
            listaCiclo2Filtrada = listaDoubleCiclo2;
            listaCiclo3Filtrada = listaDoubleCiclo3;
        }
        double maximoLista0 = Collections.max(listaCiclo0Filtrada);
        double maximoLista1 = Collections.max(listaCiclo1Filtrada);
        double maximoLista2 = Collections.max(listaCiclo2Filtrada);
        double maximoLista3 = Collections.max(listaCiclo3Filtrada);

        double promedio = (maximoLista1 + maximoLista2 + maximoLista3) / 3;
        List<Double> listaMaximos = new ArrayList<Double>();
        listaMaximos.add(maximoLista0);
        listaMaximos.add(maximoLista1);
        listaMaximos.add(maximoLista2);
        listaMaximos.add(maximoLista3);
        listaMaximos.add(promedio);
        return listaMaximos;
    }//end of procesarDatos

    private void generarArchivo() {
        File file = new File("medidasCiclo0.txt");
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException ex) {
            System.out.println("Error creando el archivo");
            Logger.getRootLogger().error(ex);
        }
        PrintWriter output = null;
        try {
            //Generar el archivo txt de la entrada, la entrada corregida y la salida
            //filtrada
            output = new PrintWriter(file);
            double tR = 0.0;
            int tamanio = listaDoubleCiclo0.size() > listaCiclo0Filtrada.size() ? listaCiclo0Filtrada.size() : listaDoubleCiclo0.size();
            System.out.println("Tamaño list opa 0 ".concat(String.valueOf(tamanio)));
            System.out.println("Tamaño list filtrada ".concat(String.valueOf(listaCiclo0Filtrada.size())));
            System.out.println("Tamaño list doubleCiclo ".concat(String.valueOf(listaDoubleCiclo0.size())));

            output.write(" SERIAL DEL OPACIMETRO " + String.valueOf(opacimetro.obtenerSerial()) + "\n");
            output.write("-.** Prueba Aplicada a la Placa: ".concat(WorkerCiclosDiesel.placas).concat(".-**\n "));
            output.write("-.* Tabla de Valores Medida de Ciclo Nro 0.-* \n");
            output.write("-.* Diametro Exosto.-* ".concat(String.valueOf(WorkerCiclosDiesel.diametroExosto)).concat(" mm \n "));
            output.write("Tmp Resp: " + "\t Opacidad Sin Filtro " + "\t Opacidad Con Filtro" + "\t Opacidad Con Correccion BerLambert " + "\n");
            for (int i = 0; i < tamanio - 1; i++) {
                if (listaDoubleCiclo0.get(i) >= 94) {
                    listaDoubleCiclo0.set(i, 100.00);
                }
                //   output.write(" "+df2.format(tR)+ "\t\t\t" +df2.format(listaDoubleCiclo0.get(i)) + "\t\t\t" + df2.format(listaCiclo0Filtrada.get(i)) + "\t\t\t" + "\n");
                tR = tR + 0.02;
            }
            output.close();
            File file1 = new File("medidasCiclo1.txt");
            if (file1.exists()) {
                file1.delete();
            }
            try {
                file1.createNewFile();
            } catch (IOException ex) {
                System.out.println("Error creando el archivo medidasCiclo1.txt");
                Logger.getRootLogger().error("Error creando el archivo medidasCiclo1.txt", ex);
            }
            output = new PrintWriter(file1);
            tamanio = listaDoubleCiclo1.size() > listaCiclo1Filtrada.size() ? listaCiclo1Filtrada.size() : listaDoubleCiclo1.size();
            tR = 0.0;
            System.out.println("Tamaño list opa 1 ".concat(String.valueOf(tamanio)));
            output.write(" SERIAL DEL OPACIMETRO " + String.valueOf(opacimetro.obtenerSerial()) + "\n");
            output.write("-.** Prueba Aplicada a la Placa: ".concat(WorkerCiclosDiesel.placas).concat(".-**\n "));
            output.write("-.* Tabla de Valores Medida de Ciclo Nro 1.-* \n");
            output.write("-.* Diametro Exosto.-* ".concat(String.valueOf(WorkerCiclosDiesel.diametroExosto)).concat(" mm \n "));
            output.write("Tmp Resp: " + "\t Opacidad Sin Filtro " + "\t Opacidad Con Filtro" + "\t Opacidad Con Correccion BerLambert " + "\n");
            for (int i = 0; i < tamanio - 1; i++) {
                if (listaDoubleCiclo1.get(i) >= 94) {
                    listaDoubleCiclo1.set(i, 100.00);
                }
                //  output.write(" "+df2.format(tR)+ "\t\t\t" +df2.format(listaDoubleCiclo1.get(i)) + "\t\t\t" + df2.format(listaCiclo1Filtrada.get(i))+ "\t\t\t"  + "\n");
                tR = tR + 0.02;
            }
            output.close();
            File file2 = new File("medidasCiclo2.txt");
            if (file2.exists()) {
                file2.delete();
            }
            try {
                file2.createNewFile();
            } catch (IOException ex) {
                System.out.println("Error creando archivo de texto");
                Logger.getRootLogger().error(ex);
            }
            output = new PrintWriter(file2);
            tamanio = listaDoubleCiclo2.size() > listaCiclo2Filtrada.size() ? listaCiclo2Filtrada.size() : listaDoubleCiclo2.size();
            tR = 0.0;
            System.out.println("Tamaño list opa 2 ".concat(String.valueOf(tamanio)));
            output.write(" SERIAL DEL OPACIMETRO " + String.valueOf(opacimetro.obtenerSerial()) + "\n");
            output.write("-.** Prueba Aplicada a la Placa: ".concat(WorkerCiclosDiesel.placas).concat(".-**\n "));
            output.write("-.* Tabla de Valores Medida de Ciclo Nro 2.-* \n");
            output.write("-.* Diametro Exosto.-* ".concat(String.valueOf(WorkerCiclosDiesel.diametroExosto)).concat(" mm \n "));
            output.write("Tmp Resp: " + "\t Opacidad Sin Filtro " + "\t Opacidad Con Filtro" + "\t Opacidad Con Correccion BerLambert " + "\n");
            for (int i = 0; i < tamanio - 1; i++) {
                if (listaDoubleCiclo2.get(i) >= 94) {
                    listaDoubleCiclo2.set(i, 100.00);
                }
                //  output.write(" "+df2.format(tR)+ "\t\t\t" +df2.format(listaDoubleCiclo2.get(i)) + "\t\t\t" + df2.format(listaCiclo2Filtrada.get(i))+ "\t\t\t"  + "\n");
                tR = tR + 0.02;
            }
            output.close();
            File file3 = new File("medidasCiclo3.txt");
            if (file3.exists()) {
                file3.delete();
            }
            try {
                file3.createNewFile();
            } catch (IOException ex) {
                System.out.println("Error creando el archivo medidasCiclo3.txt");
                Logger.getRootLogger().error("Error creando el archivo medidasCiclo3.txt", ex);
            }
            output = new PrintWriter(file3);
            tamanio = listaDoubleCiclo3.size() > listaCiclo3Filtrada.size() ? listaCiclo3Filtrada.size() : listaDoubleCiclo3.size();
            tR = 0.0;
            System.out.println("Tamaño list opa 3 ".concat(String.valueOf(tamanio)));
            output.write(" SERIAL DEL OPACIMETRO " + String.valueOf(opacimetro.obtenerSerial()) + "\n");
            output.write("-.** Prueba Aplicada a la Placa: ".concat(WorkerCiclosDiesel.placas).concat(".-**\n "));
            output.write("-.* Tabla de Valores Medida de Ciclo Nro 0.-* \n");
            output.write("-.* Diametro Exosto.-* ".concat(String.valueOf(WorkerCiclosDiesel.diametroExosto)).concat(" mm \n "));
            output.write("Tmp Resp: " + "\t Opacidad Sin Filtro " + "\t Opacidad Con Filtro" + "\t Opacidad Con Correccion BerLambert " + "\n");
            for (int i = 0; i < tamanio - 1; i++) {
                if (listaDoubleCiclo3.get(i) >= 94) {
                    listaDoubleCiclo3.set(i, 100.00);
                }
                //  output.write(" "+df2.format(tR)+ "\t\t\t" + df2.format(listaDoubleCiclo3.get(i)) + "\t\t\t" + df2.format(listaCiclo3Filtrada.get(i))+ "\t\t\t" + df2.format(listaCiclo3CorreBerLambert.get(i)) + "\n");
                tR = tR + 0.02;
            }
            output.close();
        } catch (FileNotFoundException ex) {
            Logger.getRootLogger().error(ex);
        } finally {

        }
    }

    private ArrayList<Double> filtroForBuffer(ArrayList<Double> listaCiclo1) {
        ArrayList<Double> listaY = new ArrayList<Double>();
        final double ts = 0.02;// TIEMPO DE MUESTREO
        final double fc = 0.695;//FRECUENCIA DE CORTE DE BESSEL 0.725
        final double b = 0.618034;// CONT DE BESSEL
        final double omega = 1 / Math.tan(Math.PI * ts * fc);// ESTE ES FILTRO BESSEL
        final double c = 1 / (1 + (omega * Math.sqrt(3 * b) + b * omega * omega));
        final double K = 2 * c * (b * omega * omega - 1) - 1;
        ArrayList<Double> listaX = new ArrayList<Double>();
        double kaux;
        double opacidadCorregida;
        double opacidadFiltrada = 0;
        int j = 0;
        for (Double opacidad : listaCiclo1) {
            if (opacidad >= 94) {
                opacidad = 100.00;
            }
            opacidadCorregida = 100 * (1 - Math.pow((1 - opacidad * 0.01), (diametroExosto * 0.001) / 0.364));
            listaX.add(j, opacidadCorregida);
            j++;
        }
        double maxCorregir = -1;
        try {
            maxCorregir = Collections.max(listaCiclo1);
        } catch (NoSuchElementException nse) {
            System.out.println("No hay elementos en la lista");
            maxCorregir = 1;
        }
        System.out.println("Corregida: " + maxCorregir);
        //se supone que los valores estan corregidos;
        Double Opa;
        for (int i = 0; i < listaX.size(); i++) {
            Opa = listaX.get(i);
            if (Opa >= 50) {
                int jw = 10;
            }
            if (i == 0) {
                opacidadFiltrada = ((Double) listaX.get(i)).doubleValue() * c;
            }
            if (i == 1) {
                opacidadFiltrada = ((Double) listaY.get(i - 1)).doubleValue() + c * (((Double) listaX.get(i)).doubleValue() + 2.0D * ((Double) listaX.get(i - 1)).doubleValue()) + K * ((Double) listaY.get(i - 1)).doubleValue();
            }
            if (i >= 2) {
                opacidadFiltrada = ((Double) listaY.get(i - 1)).doubleValue() + c * (((Double) listaX.get(i)).doubleValue() + 2.0D * ((Double) listaX.get(i - 1)).doubleValue() + ((Double) listaX.get(i - 2)).doubleValue() - 4.0D * ((Double) listaY.get(i - 2)).doubleValue()) + K * (((Double) listaY.get(i - 1)).doubleValue() - ((Double) listaY.get(i - 2)).doubleValue());
            }
            if (opacidadFiltrada >= 100) {
                opacidadFiltrada = 100.00;
            }
            listaY.add(i, opacidadFiltrada);
        }
        return listaY;
    }

    private ArrayList<Double> filtrarLista(ArrayList<Double> listaCicloSinFiltrar, ArrayList<Double> listaCicloCorrecion) {
        ArrayList<Double> listaY = new ArrayList<Double>();
        final double ts = 0.02;// TIEMPO DE MUESTREO
        final double fc = 0.725; //FRECUENCIA DE CORTE DE BESSEL 0.725;
        final double b = 0.618034;// CONT DE BESSEL
        final double omega = 1 / Math.tan(Math.PI * ts * fc);
        final double c = 1 / (1 + (omega * Math.sqrt(3 * b) + b * omega * omega));
        final double K = 2 * c * (b * omega * omega - 1) - 1;
        ArrayList<Double> listaX = new ArrayList<Double>();
        double kaux;
        double opacidadCorregida;
        double opacidadFiltrada = 0;
        int j = 0;
        for (Double opacidad : listaCicloSinFiltrar) {
            if (opacidad >= 94) {
                opacidad = 100.00;
            }
            opacidadCorregida = 100 * (1 - Math.pow((1 - opacidad * 0.01), (diametroExosto * 0.001) / 0.364));
            listaX.add(j, new Double(opacidadCorregida));
            listaCicloCorrecion.add(j, opacidadCorregida);
            j++;
        }
        double maxCorregir = -1;
        try {
            maxCorregir = Collections.max(listaX);
        } catch (NoSuchElementException nse) {
            System.out.println("No hay elementos en la lista");
            maxCorregir = 1;
        }
        System.out.println("Corregida: " + maxCorregir);
        //se supone que los valores estan corregidos;
        Double Opa;
        for (int i = 0; i < listaX.size(); i++) {
            Opa = listaX.get(i);
            if (Opa >= 50) {
                int jw = 10;
            }
            if (i == 0) {
                opacidadFiltrada = ((Double) listaX.get(i)).doubleValue() * c;
            }
            if (i == 1) {
                opacidadFiltrada = ((Double) listaY.get(i - 1)).doubleValue() + c * (((Double) listaX.get(i)).doubleValue() + 2.0D * ((Double) listaX.get(i - 1)).doubleValue()) + K * ((Double) listaY.get(i - 1)).doubleValue();
            }
            if (i >= 2) {
                opacidadFiltrada = ((Double) listaY.get(i - 1)).doubleValue() + c * (((Double) listaX.get(i)).doubleValue() + 2.0D * ((Double) listaX.get(i - 1)).doubleValue() + ((Double) listaX.get(i - 2)).doubleValue() - 4.0D * ((Double) listaY.get(i - 2)).doubleValue()) + K * (((Double) listaY.get(i - 1)).doubleValue() - ((Double) listaY.get(i - 2)).doubleValue());
            }
            if (opacidadFiltrada >= 100) {
                opacidadFiltrada = 100.00;
            }
            listaY.add(i, opacidadFiltrada);//recemos para que el orden coincida
        }
        return listaY;
    }

    private List<MedidaGeneral> armarListas(List<Double> listaMaximosTresCiclos, double velocidadRalenti, double velocidadGobernada, int temperaturaAntesIniciar) {
        List<MedidaGeneral> listaMedidas = new ArrayList<MedidaGeneral>();
        MedidaGeneral maximoCiclo0 = new MedidaGeneral(8033, AproximacionMedidas.AproximarMedidas(listaMaximosTresCiclos.get(0)));
        listaMedidas.add(maximoCiclo0);

        MedidaGeneral maximoPrimerCiclo = new MedidaGeneral(8013, AproximacionMedidas.AproximarMedidas(listaMaximosTresCiclos.get(1)));
        listaMedidas.add(maximoPrimerCiclo);

        MedidaGeneral maximoSegundoCiclo = new MedidaGeneral(8014, AproximacionMedidas.AproximarMedidas(listaMaximosTresCiclos.get(2)));
        listaMedidas.add(maximoSegundoCiclo);

        MedidaGeneral maximoTercerCiclo = new MedidaGeneral(8015, listaMaximosTresCiclos.get(3));
        listaMedidas.add(maximoTercerCiclo);

        MedidaGeneral promedio = new MedidaGeneral(8017, AproximacionMedidas.AproximarMedidas(listaMaximosTresCiclos.get(4)));
        listaMedidas.add(promedio);

        MedidaGeneral opacidad = new MedidaGeneral(8030, AproximacionMedidas.AproximarMedidas(listaMaximosTresCiclos.get(4)));
        listaMedidas.add(opacidad);
        //no registrar el maximo

        MedidaGeneral temperaturaMotor = new MedidaGeneral(8034, temperaturaAntesIniciar);
        listaMedidas.add(temperaturaMotor);

        MedidaGeneral velocidadRal = new MedidaGeneral(8035, AproximacionMedidas.AproximarMedidas(velocidadRalenti));
        listaMedidas.add(velocidadRal);

        MedidaGeneral velGobernada = new MedidaGeneral(8036, AproximacionMedidas.AproximarMedidas(velocidadGobernada));
        listaMedidas.add(velGobernada);

        return listaMedidas;
    }

    private void mostrarDlgCiclos() {

        d = new JDialog();
        dialogo = new DialogoCiclosAceleracion(900, 1200);
        d.getContentPane().add(dialogo);
        d.setSize(d.getToolkit().getScreenSize());
        d.setResizable(false);
        d.setModal(true);
        d.setTitle("SART 1.7.3 TOMA DE MUESTRA OPACIDAD");
        d.setLocationRelativeTo(dialogo);
        d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        for (ActionListener al : panel.getButtonFinalizar().getActionListeners()) {
            dialogo.getButtonCancelar().addActionListener(al);
        }
        dialogo.getButtonCambiarRpms().setText("Falla subita");
        for (ActionListener al : panel.getButtonRpm().getActionListeners()) {
            dialogo.getButtonCambiarRpms().addActionListener(al);
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                d.setVisible(true);
            }
        });
    }//end of method

    private void ajusteInicialMinimoMaximo() throws InterruptedException, FallaAjusteInicialException, HeadlessException {
        DecimalFormat df2 = new DecimalFormat("##.##");
        panel.getPanelMensaje().setText("REALICE LAS RITUNAS DE LIMPIEZA Y PURGA");
        Thread.sleep(2000);
        JOptionPane.showMessageDialog(null, "Rutinas de limpieza terminadas?");
        contadorTemporizacion = 0;
        panel.getPanelMensaje().setText("CALIBRACION DE CERO\n Asegurese que El Opacimetro no tiene\n obstrucción por partículas o Residuos");
        Thread.sleep(2000);
        opacimetro.calibrar();
        MedicionOpacidad med = opacimetro.obtenerDatos();

        while (med.isZeroEnProgreso()) {//adicionado debido al comportamiento diferente del opacimetro capelec, no afecta presumiblemente
            Thread.sleep(500);
            med = opacimetro.obtenerDatos();
        }

        med = opacimetro.obtenerDatos();

        while (med.getOpacidadDouble() >= 1 && contadorTemporizacion < Opacimetro.TIEMPO_ESCALA) {
            Thread.sleep(120);
            med = opacimetro.obtenerDatos();
            panel.getPanelMensaje().setText("Opacidad: " + df2.format(med.getOpacidadDouble()) + ", t: " + contadorTemporizacion + "s");

        }//end of while
        if (contadorTemporizacion >= Opacimetro.TIEMPO_ESCALA) {
            JOptionPane.showMessageDialog(panel, "Tolerancia invalida para el punto Cero realice \n"
                    + "procedimientos de purga y limpieza nuevamente");//sale del proceso sin abortar la prueba
            liberarRecursos();
            throw new FallaAjusteInicialException();
            //no registra el aborto de la prueba;
        }

        panel.getPanelMensaje().setText("Ajuste del valor minimo correcto");
        Thread.sleep(2500);
        panel.getPanelMensaje().setText("ESCALA MAXIMA \n Inserte un elemento que obstruya totalmente la \n emision de luz");
        Thread.sleep(2000);
        contadorTemporizacion = 0;
        med = opacimetro.obtenerDatos();
        //Esperar hasta que la medicion de opacidad suba hasta 90 %
        while (med.getOpacidadDouble() <= Opacimetro.VALOR_ESCALA_MAXIMA && contadorTemporizacion < Opacimetro.TIEMPO_ESCALA) {

            Thread.sleep(120);
            med = opacimetro.obtenerDatos();
            panel.getPanelMensaje().setText("Opacidad: " + df2.format(med.getOpacidadDouble()) + ", t: " + contadorTemporizacion + "s");

        }//end of while

        if (contadorTemporizacion >= Opacimetro.TIEMPO_ESCALA) {
            JOptionPane.showMessageDialog(panel, "Ajuste del valor maximo de la escala incorrecto \n"
                    + "repita los procedimientos de purga y limpieza");//sale del proceso
            liberarRecursos();//no se registra la prueba como cancelada simplemente no se finaliza y como
            //todas las pruebas inician por defecto reprobadas entonces no puede aprobar y la prueba no se da por finalizada
            throw new FallaAjusteInicialException();
        }
        dialogo.setVisible(true);
    }

    private void probarAceleracionGobernada() throws InterruptedException, DeficienteOperacionException {

        panel.getPanelMensaje().setVisible(true);
        panel.getPanelFiguras().setVisible(false);
        panel.getPanelMensaje().setText("ACELERACION A GOBERNADAS \n OPRIMA EL ACELERADOR EN MENOS DE 1 SEG."
                + "\n CUANDO SE LE INDIQUE");
        Thread.sleep(2500);
        panel.getPanelMensaje().setVisible(false);
        panel.getPanelFiguras().setVisible(true);

        //PRIMERO VALIDAR QUE EL VEHICULO ESTE EN RALENTI
        boolean aceleracionGobernadas5sValida = false;
        int intentosAceleracionGobernada = 0;
        while (!aceleracionGobernadas5sValida && intentosAceleracionGobernada < 3) {
            contadorTemporizacion = 0;
            panel.getRadialTacometro().setBackgroundColor(BackgroundColor.BLACK);
            simuladasRalenti = true;
            rpm = (int) rpmSimuladas;
            while (rpm < (velRalenti - 100) || rpm > (velRalenti + 100)) {

                rpm = (int) rpmSimuladas;
                panel.getRadialTacometro().setValue(rpm);
                panel.getMensaje().setText("PONGA LA VELOCIDAD EN : " + velRalenti + "rpms.. tiempo: " + contadorTemporizacion);
                Thread.sleep(100);

            }

            rpm = (int) rpmSimuladas;

            T_ANTES_ACELERAR = 10;
            contadorTemporizacion = 0;
            panel.getMensaje().setText("MANTENGA RALENTI");

            boolean noRepetir = true;
            panel.getProgressBar().setMaximum(5);
            panel.getProgressBar().setStringPainted(true);
            while (contadorTemporizacion < T_ANTES_ACELERAR && (rpm >= velRalenti - 100 && rpm <= velRalenti + 100)) {

                if (contadorTemporizacion > 5) {

                    panel.getProgressBar().setValue(10 - contadorTemporizacion);
                    panel.getProgressBar().setString(String.valueOf(10 - contadorTemporizacion));
                    if (noRepetir) {
                        panel.getRadialTacometro().setBackgroundColor(BackgroundColor.RED);
                        noRepetir = false;
                    }
                    panel.getMensaje().setText("Preparese para acelerar");
                }
                rpm = (int) rpmSimuladas;
                panel.getRadialTacometro().setValue(rpm);

                Thread.sleep(100);

            }//end of while

            if (contadorTemporizacion < T_ANTES_ACELERAR) {

                panel.getMensaje().setText("ACELERE CUANDO SE LE INDIQUE");
                Thread.sleep(2000);
                continue;

            }
            contadorTemporizacion = 0;
            simuladasRalenti = false;
            subidaAceleracion = true;
            noRepetir = true;
            while (contadorTemporizacion < 5 && (rpm < velCrucero - 100)) {

                if (noRepetir) {
                    panel.getRadialTacometro().setBackgroundColor(BackgroundColor.GREEN);
                }
                panel.getMensaje().setText("ACELERE !");
                rpm = (int) rpmSimuladas;
                panel.getRadialTacometro().setValue(rpm);
                Thread.sleep(100);

            }

            if (contadorTemporizacion >= 5) {

                panel.getMensaje().setText("ACELERACION FALLIDA: intentos restantes " + (2 - intentosAceleracionGobernada));
                Thread.sleep(2000);
                intentosAceleracionGobernada++;
            } else {

                aceleracionGobernadas5sValida = true;
                simuladasRalenti = true;
                subidaAceleracion = false;
            }

        }

        if (intentosAceleracionGobernada >= 3) {

            throw new DeficienteOperacionException();

        }

    }//end of method probarAceleracion
}
