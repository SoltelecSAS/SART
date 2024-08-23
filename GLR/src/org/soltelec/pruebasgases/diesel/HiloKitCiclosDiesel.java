/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.pruebasgases.diesel;

import eu.hansolo.steelseries.gauges.Radial;
import eu.hansolo.steelseries.tools.BackgroundColor;
import eu.hansolo.steelseries.tools.GaugeType;
import eu.hansolo.steelseries.tools.LcdColor;
import eu.hansolo.steelseries.tools.Section;
import java.util.List;
import org.soltelec.procesosopacimetro.Opacimetro;
import org.soltelec.procesosopacimetro.OpacimetroSensors;
import gnu.io.SerialPort;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.concurrent.CancellationException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import org.soltelec.medicionrpm.CapelecSerial;
import org.soltelec.medicionrpm.ConsumidorCapelec;
import org.soltelec.medicionrpm.JDialogReconfiguracionKit;

/**
 * Clase para realizar la prueba diesel usando opacimetro y kit de revoluciones
 * capelec
 *
 * @author Usuario
 */
public class HiloKitCiclosDiesel implements Runnable, ActionListener {

    private int contadorTemporizacion;
    private Timer timer;
    private DialogoCiclosAceleracion dialogo;
    private int T_ANTES_ACELERAR = 20;
    private double velRalenti = 420;
    private double velCrucero = 4500;
    //private HiloTomaDatosDiesel hiloTomaDatos;
    //private Thread t;
    private Opacimetro opacimetro;
    private ConsumidorCapelec consumidorCapelec;
    private int T_RALENTI = 20;//20 segundos para entrar a ralenti
    private int rpmKit;
    private boolean pruebaExitosa = false;
    //private ArrayList<ArregloOpacidadCiclo> listaDatos;//implementacion mala por eso se remueve
    private volatile Thread blinker;
    private CapelecSerial capelecServicios;
    private JDialogReconfiguracionKit dialogReconfiguracion;
    private SerialPort serialPort;
    private Radial radialTacometro;
    private List<Byte> listaCiclo1;
    private List<Byte> listaCiclo2;
    private List<Byte> listaCiclo3;

    public HiloKitCiclosDiesel(DialogoCiclosAceleracion d, OpacimetroSensors op, ConsumidorCapelec c, SerialPort sp) {
        dialogo = d;
        d.getButtonCancelar().addActionListener(this);
        this.opacimetro = op;
        //hiloTomaDatos = new HiloTomaDatosDiesel();
        this.consumidorCapelec = c;
        //hiloTomaDatos.setOpacimetro((OpacimetroSensors) opacimetro);
        //t = new Thread(hiloTomaDatos);
        this.serialPort = sp;
    }//end of constructor

    @Override
    public void run() {
        HiloTomaDatosDiesel hiloTomaDatos = null;
        Thread t = null;
        int ciclos = 0;
        crearNuevoTimer();
        timer.start();
        int intentos = 0;
        blinker = Thread.currentThread();
        try {
            while (intentos < 3 && !pruebaExitosa && blinker != null) {
                //11 de Abril de 20011 modificacion de parametros de kit
                if (intentos > 0) {
                    //Preguntar si se quiere reconfigurar el kit y los valores de ralenti gorbernadas
                    dialogReconfiguracion = new JDialogReconfiguracionKit(consumidorCapelec, serialPort);
                    dialogReconfiguracion.setVisible(true);
                    boolean valido = true;

                    //do while para validar las velocidades
                    do {

                        do {
                            try {
                                valido = true;
                                velRalenti = Integer.parseInt(JOptionPane.showInputDialog(dialogo, "Digite Velocidad Ralenti"));
                            } catch (NumberFormatException ne) {
                                valido = false;
                            }
                        } while (!valido);

                        do {
                            try {
                                valido = true;
                                velCrucero = Integer.parseInt(JOptionPane.showInputDialog("Velocidad Crucero"));
                            } catch (NumberFormatException ne) {
                                valido = false;
                            }
                        } while (!valido);

                    } while ((velRalenti >= velCrucero) && (velRalenti < 0 || velCrucero < 0));

                    repintarRangos(velRalenti, velCrucero);
                    //se se quiere configurar entonces mostrar un dialogo de reconfiguracion del ki

                }
                //11 de Abril de 2011 modificacion de paramteros de kit final
                ciclos = 0;
                while (ciclos < 4 && blinker != null) {
                    if (ciclos == 0) {
                        T_ANTES_ACELERAR = 20;
                    } else {
                        T_ANTES_ACELERAR = 8;
                    }
                    contadorTemporizacion = 0;
                    dialogo.getDisplayTiempo().setLcdColor(LcdColor.STANDARD_LCD);
                    dialogo.getLabelMensaje().setText("CICLO: " + ciclos);
                    dialogo.getLedRojo().setLedBlinking(false);
                    dialogo.getLedRojo().setLedOn(true);
                    dialogo.getLedVerde().setLedOn(false);
                    //El vehiculo debe estar en ralenti + - 50 rpms para que entre al rango con algo de olgura
                    rpmKit = consumidorCapelec.getRpm();
                    while ((rpmKit < velRalenti - 100 || rpmKit > velRalenti + 100) && contadorTemporizacion < T_RALENTI && blinker != null) {
                        Thread.sleep(100);
                        System.out.println("Vehiculo fuera del rango de ralenti +-50 rpms");
                        dialogo.getLabelMensaje().setText("FUERA RANGO RALENTI");
                        dialogo.getDisplayTiempo().setLcdValue(contadorTemporizacion);
                        dialogo.getRadialTacometro().setValue(rpmKit);
                        rpmKit = consumidorCapelec.getRpm();
                    }//end of while
                    Thread.sleep(10);//punto de cancelacion
                    //si pasan 20 segundos y el vehiculo no entro a ralenti ... gastar un intento
                    if (contadorTemporizacion >= 20) {
                        intentos++;
                        JOptionPane.showMessageDialog(dialogo, "el vehiculo no entra a ralenti intentos :" + intentos);
                        break;//vuelve al ciclo de intentos
                    }//end if
                    //Antes de iniciar el ciclo de bariido se dan 20 SEGUNDOS para entrar al carro
                    //en los ultimos 3 segundos se da la indicacion del semaforo
                    contadorTemporizacion = 0;
                    dialogo.getLabelMensaje().setText("MANTENGA RALENTI");
                    rpmKit = consumidorCapelec.getRpm();
                    dialogo.getDisplayTiempo().setLcdColor(LcdColor.BLACK_LCD);
                    while ((rpmKit >= velRalenti - 250 && rpmKit <= velRalenti + 250) && contadorTemporizacion <= T_ANTES_ACELERAR && blinker != null) {
                        Thread.sleep(120);
                        semaforo();
                        dialogo.getDisplayTiempo().setLcdValue(T_ANTES_ACELERAR - contadorTemporizacion);
                        dialogo.getRadialTacometro().setValue(rpmKit);
                        rpmKit = consumidorCapelec.getRpm();
                    } //Si acelera antes de tiempo mal
                    Thread.sleep(10);
                    if (!(contadorTemporizacion >= T_ANTES_ACELERAR)) {
                        JOptionPane.showMessageDialog(dialogo, "Mantenga el vehiculo en ralenti hasta que se le indique que acelere\n Intentos: " + intentos++);
                        break;
                    }
                    //ACELERACION EN MENOS DE 5S
                    //INICIO TOMA DE DATOS

                    if (ciclos >= 1) {
                        hiloTomaDatos = new HiloTomaDatosDiesel();
                        hiloTomaDatos.setNumeroCiclo(ciclos);
                        hiloTomaDatos.setOpacimetro((OpacimetroSensors) opacimetro);
                        t = new Thread(hiloTomaDatos);
                        t.start();
                    }
                    System.err.println("CICLO: " + ciclos);
                    contadorTemporizacion = 1;
                    dialogo.getDisplayTiempo().setLcdValue(contadorTemporizacion);
                    rpmKit = consumidorCapelec.getRpm();
                    while (contadorTemporizacion < 5 && rpmKit <= velCrucero - 125 && blinker != null) {
                        dialogo.getDisplayTiempo().setLcdValue(contadorTemporizacion);
                        rpmKit = consumidorCapelec.getRpm();
                        dialogo.getRadialTacometro().setValue(rpmKit);
                        Thread.sleep(10);
                    }//end of while
                    Thread.sleep(10);
                    if (contadorTemporizacion >= 5) {
                        JOptionPane.showMessageDialog(dialogo, " El vehiculo no alcanza las rpm Gobernadas en menos de 5 s \n tiene:" + intentos++);
                        break;
                    }//end if
                    dialogo.getLabelMensaje().setText("CICLO: " + ciclos + " GOBERNADAS");
                    dialogo.getDisplayTiempo().setLcdColor(LcdColor.BLUE2_LCD);
                    rpmKit = consumidorCapelec.getRpm();
                    contadorTemporizacion = 0;
                    while (contadorTemporizacion < 4 && (rpmKit >= (velCrucero - 250) && rpmKit <= (velCrucero + 250)) && blinker != null) {
                        Thread.sleep(10);
                        dialogo.getDisplayTiempo().setLcdValue(contadorTemporizacion);
                        dialogo.getRadialTacometro().setValue(rpmKit);
                        rpmKit = consumidorCapelec.getRpm();
                    }//end while
                    Thread.sleep(10);//punto de cancelacion
                    if (contadorTemporizacion < 2) {
                        JOptionPane.showMessageDialog(dialogo, "Vehiculo solo mantuvo rpm gobernadas durante: " + contadorTemporizacion + "s");
                        intentos++;
                        break;
                    }//end if
                    dialogo.getDisplayTiempo().setLcdColor(LcdColor.RED_LCD);
                    dialogo.getLedRojo().setLedOn(true);
                    dialogo.getLedVerde().setLedOn(false);
                    dialogo.getLabelMensaje().setText("CICLO: " + ciclos + "SUELTE EL PEDAL");
                    rpmKit = consumidorCapelec.getRpm();
                    contadorTemporizacion = 0;
                    while (contadorTemporizacion <= 11 && blinker != null) {//no importa mucho la velocidad
                        dialogo.getRadialTacometro().setValue(rpmKit);
                        dialogo.getDisplayTiempo().setLcdValue(contadorTemporizacion);
                        rpmKit = consumidorCapelec.getRpm();
                        Thread.sleep(10);
                    }//despues de los quince segundos el carro debe entrar a ralenti
                    if (ciclos > 0) {
                        hiloTomaDatos.setCicloTerminado(true);

                        if (ciclos == 1) {
                            listaCiclo1 = hiloTomaDatos.getListaOpacidad();
                        }
                        if (ciclos == 2) {
                            listaCiclo2 = hiloTomaDatos.getListaOpacidad();
                        }
                        if (ciclos == 3) {
                            listaCiclo3 = hiloTomaDatos.getListaOpacidad();
                        }
                    }
                    //termina toma de datos
                    System.err.println("FIN CICLO:" + ciclos);
                    ciclos++;
                }//en while
                Thread.sleep(10);//punto de cancelacion
                if (ciclos >= 3) {
                    System.out.println("Prueba finalizada con éxito");
                    pruebaExitosa = true;
                    break;
                }

            }//end while intentos
            Thread.sleep(10);//punto de cancelacion
            if (intentos < 3) {
                System.out.println("No se gastaron todos los intentos");
                JOptionPane.showMessageDialog(dialogo, "Ciclos Realizados con exito");

            } else {
                System.out.println("Prueba rechazada en 3 intentos");
                JOptionPane.showMessageDialog(dialogo, "Prueba Rechazada por 3 intentos fallidos");
                //TODO registrar en la base de datos el motivo del rechazo de la prueaba
                pruebaExitosa = false;
                dialogo.setPruebaCancelada(true);
            }
            if (pruebaExitosa) {
                dialogo.cerrar();
            }
            timer.stop();
        }//end try
        catch (InterruptedException ie) {//esta exception es sobretodo para cancelacion
            dialogo.cerrar();
            timer.stop();
            dialogo.setPruebaCancelada(true);
            throw new CancellationException("Prueba Cancelada");
        }
    }//end of method run

    private void semaforo() {

        if (contadorTemporizacion == (T_ANTES_ACELERAR - 5)) {
            dialogo.getLabelMensaje().setText("5s  RESTANTES");
            dialogo.getDisplayTiempo().setLcdColor(LcdColor.RED_LCD);
            dialogo.getLedRojo().setLedOn(true);
            dialogo.getLedAmarillo().setLedOn(false);
            dialogo.getLedVerde().setLedOn(false);
        }
        if (contadorTemporizacion == (T_ANTES_ACELERAR - 3)) {
            dialogo.getLabelMensaje().setText("3S");
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
            dialogo.getLabelMensaje().setText("ACELERE!!!");
        }
    }

    private void crearNuevoTimer() {
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                contadorTemporizacion++;
            }
        });

    }//end of method

//    private void dormir(int i) {
//        try {
//            Thread.sleep(i);
//        } catch (InterruptedException ex) {
//            Logger.getLogger(HiloKitCiclosDiesel.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }//end of dormir
    private void repintarRangos(double ralenti, double crucero) {
        radialTacometro = new Radial();
        Dimension d = dialogo.getToolkit().getScreenSize();
        int size = (int) (d.width * 0.15);
        radialTacometro.setPreferredSize(new Dimension(size, size));
        radialTacometro.setMaxValue(8000);
        radialTacometro.setMinValue(0);
        radialTacometro.setTitle("RPM");
        radialTacometro.setUnitString("rev/min");
       // radialTacometro.setTickLabelPeriod(1000);
        //radialTacometro.setScaleDividerPower(2);
        radialTacometro.setGaugeType(GaugeType.TYPE3);
        final Section[] SECTIONS = {new Section(0, 5900, java.awt.Color.WHITE), new Section(6000, 8000, java.awt.Color.RED)};
        radialTacometro.setTickmarkSections(SECTIONS);
        radialTacometro.setTickmarkSectionsVisible(true);
        radialTacometro.setThreshold(2500);
        radialTacometro.setSectionsVisible(true);
        radialTacometro.setBackgroundColor(BackgroundColor.BLUE);
        Color color = new Color(120, 0, 0);
        final Section[] SECTIONS2 = {new Section(ralenti - 100, ralenti + 100, color), new Section(crucero - 250, crucero + 250, color)};
        radialTacometro.setAreas(SECTIONS2);
        radialTacometro.setAreasVisible(true);
        dialogo.setRadialTacometro(radialTacometro);
        dialogo.repaint();

    }

    public void setVelCrucero(double velCrucero) {
        this.velCrucero = velCrucero;
    }

    public void setVelRalenti(double velRalenti) {
        this.velRalenti = velRalenti;
    }

    public boolean isPruebaExitosa() {
        return pruebaExitosa;
    }

    public List<Byte> getListaCiclo1() {
        return listaCiclo1;
    }

    public List<Byte> getListaCiclo2() {
        return listaCiclo2;
    }

    public List<Byte> getListaCiclo3() {
        return listaCiclo3;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.stop();

    }

    private void stop() {
        Thread tmpBlinker = blinker;
        blinker = null;
        if (tmpBlinker != null) {
            tmpBlinker.interrupt();
        }
    }

}//end of class HiloSimulacionDiesel
