/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.procesosbanco;

import com.soltelec.modulopuc.utilidades.Mensajes;
import eu.hansolo.steelseries.gauges.Radial;
import eu.hansolo.steelseries.tools.Section;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import org.soltelec.procesosbanco.verificacion.PanelProgresoPresion;
import org.soltelec.util.MedicionGases;
import org.soltelec.util.ShiftRegister;
import org.soltelec.util.capelec.BancoCapelec;


/**
 * 24 de Marzo de 2011 añadida la subida de presión de 30 mbar y comandos de
 * calibración de pipetas de dos puntos
 *
 * @author Gerencia de Desarrollo de Soluciones Tecnologicas
 */
public class ProcesoCalibracion implements Runnable {

    private PanelProgresoPresion panelProgreso;//Barra de progreso y JLabel para escribir mensajes
    private Timer timer;
    private BancoGasolina banco;
    private JButton botonHabilitar; //habilitar o deshabiltar segun el procesProcesoRuido
    int contadorTimer = 0;
    private PuntoCalibracion ptoCalibracion;//variable global para el punto de calibración
    int muestra = 1;//dependiendo de la muestra el hilo ejecutara un procedimiento distinto
    private MedicionGases medicion;
    private Radial radial;//para mostrar la presion
    private ShiftRegister sr;
    private boolean procesoAltaExitoso = false;
    private boolean procesoBajaExitoso = false;
    boolean procesoExitoso = true;
    private boolean validaPresion= false;
   

    public ProcesoCalibracion() {
        sr = new ShiftRegister(10, 0);
    }

    public ProcesoCalibracion(PanelProgresoPresion thePanelProgreso, JButton boton, BancoGasolina theBanco) {//propositos de depuracion
        this();
        this.panelProgreso = thePanelProgreso;
        this.botonHabilitar = boton;
        this.radial = thePanelProgreso.getRadial();
        this.banco = theBanco;
    }

    public ProcesoCalibracion(PanelProgresoPresion thePanelProgreso, BancoGasolina theBanco, PuntoCalibracion thePuntoCalibracion) {
        this();
        this.panelProgreso = thePanelProgreso;
        this.banco = theBanco;
        this.ptoCalibracion = thePuntoCalibracion;
        this.radial = thePanelProgreso.getRadial();
    }

    @Override
    public void run() {       
        radial.setPreferredSize(new Dimension( this.panelProgreso.tall,this.panelProgreso.tall));         
        if (banco instanceof BancoSensors) {
            if (muestra == 1) {
                procesoMuestraBaja();
            } else {
                procesoMuestraAlta();
            }
        } else if (banco instanceof BancoCapelec) {
             System.out.println("|----->> call bancoCapelec<<-----");
            procesoMuestra();
        }
    }

    private void procesoMuestraBaja() {
        System.out.println("|----->> Iniciando Proceso Baja <<-----");

        System.out.println("Baja Exitoso 1: " + procesoBajaExitoso);
        try {
            panelProgreso.getBarraTiempo().setMaximum(2);

            timer = new Timer(1000, new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    contadorTimer++;
                }
            });
            timer.start();

            short mapa = banco.mapaFisicoES((short) 128, false);
            banco.encenderCalSol1(false);
            Thread.sleep(100);
            banco.encenderCalSol2(true);
            Thread.sleep(100);
            banco.encenderSolenoideUno(true);
            Thread.sleep(100);
            banco.encenderSolenoide2(false);
            Thread.sleep(100);
            banco.encenderDrainPump(false);
            Thread.sleep(100);
            banco.encenderBombaMuestras(false);
            Thread.sleep(100);

            panelProgreso.getLabelMensaje().setText("Ingresando Gas....");

            medicion = banco.obtenerDatos();
            System.out.println("----- Datos de medicion tomados del banco ---");
            System.out.println(medicion.toString());
            int presionInicial = medicion.getValorPres();
            sr.ponerElemento(presionInicial);

            while (!sr.isStable()) {
                sr.ponerElemento(medicion.getValorPres());
                Thread.sleep(100);
            }

            presionInicial = (int) sr.media();
            radial.setValue(presionInicial);
          
            int umbral = presionInicial + 30;

            panelProgreso.getLabelMensaje().setText("Por Favor se Recomienda ABRIR POCO a POCO la válvula");
            Thread.sleep(1500);
            boolean presionOk = false;            
            while (!presionOk) {
                medicion = banco.obtenerDatos();  
                while ((medicion.getValorPres() <= umbral || medicion.getValorPres() >= umbral + 15) && contadorTimer < BancoGasolina.LIM_TIEMPO_CALIBRACION) {//fuera de rango?                    
                    Thread.sleep(100);
                    if (medicion.getValorPres() < umbral) {
                        panelProgreso.getLabelMensaje().setText("Por Favor Ajustar  a presión  " + umbral + " SUBA POCO A POCO la Presion:" + contadorTimer + " seg.");
                    } else if (medicion.getValorPres() > umbral + 10) {
                        panelProgreso.getLabelMensaje().setText("Por Favor Ajustar  a presión " + umbral + " BAJE POCO A POCO la Presion:" + contadorTimer + " seg.");
                    }                  
                    medicion = banco.obtenerDatos();
                    sr.ponerElemento(medicion.getValorPres());                                      
                    radial.setValue(medicion.getValorPres());                     
                }
                if (contadorTimer >= BancoGasolina.LIM_TIEMPO_CALIBRACION) {
                    JOptionPane.showMessageDialog(null, "Presion inadecuada ... calibracion cancelada");
                    //procesoExitoso = false;
                    Thread.sleep(250);
                    Window w = SwingUtilities.getWindowAncestor(panelProgreso);
                    w.dispose();
                    banco.mapaFisicoES(mapa, true);
                    timer.stop();
                    banco.getPuertoSerial().close();
                    return;
                }

                panelProgreso.getLabelMensaje().setText("Validando Tolerancia de Gases ..!");
                Thread.sleep(1000);
                MedicionGases medicionGases = banco.obtenerDatos();               
                double valExHC;
                double valExCo2;
                double valExCo;               
                if (PanelCalibracion2Pts.analizador.equalsIgnoreCase("4t")) {
                    //JOptionPane.showMessageDialog(null, "Recogiendo Naturaleza exactitud para 4 T");
                    valExHC = Double.parseDouble(PanelCalibracion2Pts.props.getProperty("EPBHC"));
                    valExCo2 = Double.parseDouble(PanelCalibracion2Pts.props.getProperty("EPBCO2"));
                    valExCo = Double.parseDouble(PanelCalibracion2Pts.props.getProperty("EPBCO"));                   
                } else {
                    
                    valExHC = Double.parseDouble(PanelCalibracion2Pts.props.getProperty("EPBHC2T"));
                    valExCo2 = Double.parseDouble(PanelCalibracion2Pts.props.getProperty("EPBCO22T"));
                    valExCo = Double.parseDouble(PanelCalibracion2Pts.props.getProperty("EPBCO2T"));                    
                }
                
                
                  System.out.println("obt pef"+banco.obtenerPEF());
                    System.out.println("obt pef"+String.valueOf(banco.obtenerPEF()));
                double pef = ((Double.parseDouble(banco.obtenerPEF())) / 1000);               
                double valoTolMin = (ptoCalibracion.getValorHC() ) - valExHC;
                double valoTolMax = (ptoCalibracion.getValorHC() ) +valExHC;
               boolean validacionHc ;
                JOptionPane.showMessageDialog(null, "Valores tolerancia de hc  ("+valoTolMin+" y "+valoTolMax+" ) valor Recogido del canal de Hc div Pef: "+medicionGases.getValorHC()/pef);
                //double valorTolerancia = 0.99;               
                System.out.println("\nPEF: " + pef);
               
                if(valoTolMin <=(medicionGases.getValorHC()/pef )  &&  valoTolMax >= (medicionGases.getValorHC()/pef)){                   
                     validacionHc=true;
                }else{
                     validacionHc=false;                     
                }
                valoTolMin = (ptoCalibracion.getValorCO() * 0.01) - valExCo;
                valoTolMax = (ptoCalibracion.getValorCO() * 0.01) + valExCo;
                //JOptionPane.showMessageDialog(null, "Valores tolerancia de CO ("+valoTolMin+" y "+valoTolMax+") Valor Recogido del canal de CO: "+medicionGases.getValorCO()* 0.01);
                boolean validacionCo;                
                if(valoTolMin <= (medicionGases.getValorCO()* 0.01 ) &&  valoTolMax >= (medicionGases.getValorCO() * 0.01) ){
                    validacionCo=true;                    
                }else{
                    validacionCo=false;                    
                }              
                boolean validacionCo2;
                valoTolMin = (ptoCalibracion.getValorCO2()*0.1) - valExCo2;
                valoTolMax = (ptoCalibracion.getValorCO2()*0.1) + valExCo2;
               // JOptionPane.showMessageDialog(null, "Valores tolerancia de CO2 ( "+valoTolMin+" y "+valoTolMax+") Valor Recogido del canal de CO2:  "+medicionGases.getValorCO2()*0.1);                
                if(valoTolMin <= (medicionGases.getValorCO2()*0.1 ) &&  valoTolMax >= (medicionGases.getValorCO2()*0.1) ){
                    validacionCo2=true;                    
                }else{
                    validacionCo2=false;                    
                }
                //Calibracion calibracion = new Calibracion().getIdEquipo();
                //int usuario = calibracion.getUsuario();
                //else {
                System.out.println("Baja Exitoso 2: " + procesoBajaExitoso);
                System.out.println(String.format("\n---> Banco <---\nHC: %s\nCO: %s\nCO2: %s\n", medicionGases.getValorHC() ,
                        medicionGases.getValorCO() * 0.01,
                        medicionGases.getValorCO2() * 0.1));
                System.out.println("Toma de valores 2 o2");
                System.out.println(medicionGases.toString());
/* n-Hexanos Calibracion*/
                ptoCalibracion.setBancoHc(Double.parseDouble(String.valueOf(medicionGases.getValorHC() )) );
                ptoCalibracion.setBancoCo(medicionGases.getValorCO() * 0.01);
                ptoCalibracion.setBancoCo2(medicionGases.getValorCO2() * 0.1);
                ptoCalibracion.setValorOxigeno((short)medicionGases.getValorO2());
                //}

                contadorTimer = 0;
                if (validacionHc== false || validacionCo==false || validacionCo2==false) {
                    Mensajes.mensajeError("Tolerancia Incorrecta\n Por Favor Verifique que la Pipeta sea la Correcta ..!");
                     if (validacionHc== false ) {
                         // Mensajes.mensajeError("salida Rango En el canal HC..!");
                     }
                     if (validacionCo== false ) {
                        //  Mensajes.mensajeError("salida Rango En el canal CO..!");
                     }
                   if (validacionCo2== false ) {
                         // Mensajes.mensajeError("salida Rango En el canal CO2..!");
                     }
                    procesoExitoso = false;
                    procesoBajaExitoso = false;
                    System.out.println("Baja Exitoso 3: " + procesoBajaExitoso);
                    System.out.println("Contador Timer: " + contadorTimer);
                    //--->SIN VALIDACION
                    //Window w = SwingUtilities.getWindowAncestor(panelProgreso);
                    //w.dispose();
                    banco.mapaFisicoES(mapa, true);
                    //timer.stop();
                    //return;
                    contadorTimer = 3;
                }

                while (medicion.getValorPres() >= umbral - 5 && medicion.getValorPres() <= umbral + 20 ) {
                    panelProgreso.getBarraTiempo().setValue(contadorTimer);
                    Thread.sleep(77);
                    medicion = banco.obtenerDatos();
                    if (contadorTimer >=0) {
                        panelProgreso.getLabelMensaje().setText("Proceso Exitoso Enviando Comando ..!");
                        //banco.leerEscribirDatosCal(true, ptoCalibracion, 11);
                        //Validacion para Reprobar Calibracion                       
                        if (procesoBajaExitoso = false) {
                            System.out.println("Baja Exitoso 4: " + procesoBajaExitoso);
                        } else {
                            procesoBajaExitoso = true;
                        }
                        panelProgreso.getBarraTiempo().setValue(100);
                       // banco.ordenarCalibracion(2);
                        presionOk = true;
                        break;
                    } else {
                        panelProgreso.getLabelMensaje().setText(" !.-PRESION EN RANGO-.! ");
                         Thread.sleep(1500);
                    }
                    medicion = banco.obtenerDatos();
                    radial.setValue(medicion.getValorPres());                   
                }
            }           
            botonHabilitar.setEnabled(true);
            banco.mapaFisicoES(mapa, true);
            timer.stop();
            System.out.println("----->> Finalizando Proceso Baja <<-----");
        } catch (InterruptedException ex) {
            Mensajes.mostrarExcepcion(ex);
        }
    }

    private void procesoMuestraAlta() {
        System.out.println("|----->> Iniciando Proceso Alta <<-----");
        try {
            panelProgreso.getBarraTiempo().setMaximum(3);
            short mapa = banco.mapaFisicoES((short) 128, false);
            banco.encenderCalSol1(false);
            banco.encenderCalSol2(true);
            banco.encenderSolenoideUno(true);
            banco.encenderSolenoide2(true);
            banco.encenderBombaMuestras(false);
            banco.encenderDrainPump(false);

            panelProgreso.getLabelMensaje().setText("Tomando presion Inicial");

            int presionInicial = banco.obtenerDatos().getValorPres();
            sr = new ShiftRegister(10, 0);
            sr.ponerElemento(presionInicial);
            while (!sr.isStable()) {
                sr.ponerElemento(medicion.getValorPres());
                Thread.sleep(200);
            }

            panelProgreso.getLabelMensaje().setText("Por Favor ABRIR POCO a POCO la  valvula");
            radial.setValue(presionInicial);
           

            timer = new Timer(1000, new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    contadorTimer++;
                }
            });

            timer.start();
            int umbral = presionInicial + 30;
            boolean presionOk = false;
            while (!presionOk) {
                panelProgreso.getBarraTiempo().setValue(0);
                medicion = banco.obtenerDatos();
                while ((medicion.getValorPres() <= umbral || medicion.getValorPres() >= umbral + 20) && contadorTimer < BancoGasolina.LIM_TIEMPO_CALIBRACION) {//fuera de rango?
                    Thread.sleep(150);
                    if (medicion.getValorPres() <= umbral) {
                        panelProgreso.getLabelMensaje().setText("Por Favor Ajustar  presión a " + umbral + " SUBA POCO A POCO la presión:" + contadorTimer + " seg.");
                    } else if (medicion.getValorPres() >= umbral + 20) {
                        panelProgreso.getLabelMensaje().setText("Por Favor Ajustar presión a " + umbral + " BAJE POCO A POCO la presión:" + contadorTimer + " seg.");
                    }
                    medicion = banco.obtenerDatos();
                    sr.ponerElemento(medicion.getValorPres());
                    radial.setValue(medicion.getValorPres());
                   
                }
                if (contadorTimer >= BancoGasolina.LIM_TIEMPO_CALIBRACION) {
                    JOptionPane.showMessageDialog(null, "Presion inadecuada ... calibracion cancelada");
                    //procesoExitoso = false;
                    Thread.sleep(250);
                    Window w = SwingUtilities.getWindowAncestor(panelProgreso);
                    w.dispose();
                    banco.mapaFisicoES(mapa, true);
                    timer.stop();
                    banco.getPuertoSerial().close();
                    return;
                }
                panelProgreso.getLabelMensaje().setText("Epere Por Favor. Validando Tolerancia de Gases");
                Thread.sleep(1000);
                MedicionGases medicionGases = banco.obtenerDatos();
                double valorTolerancia = 0.09;
                // double valorTolerancia = 0.99;
                double pef = ((Double.parseDouble(banco.obtenerPEF())) / 1000);
                double valExHC;
                double valExCo2;
                double valExCo;
                if (PanelCalibracion2Pts.analizador.equalsIgnoreCase("4t")) {
                    valExHC = Double.parseDouble(PanelCalibracion2Pts.props.getProperty("EPAHC"));
                    valExCo2 = Double.parseDouble(PanelCalibracion2Pts.props.getProperty("EPACO2"));
                    valExCo = Double.parseDouble(PanelCalibracion2Pts.props.getProperty("EPACO"));
                } else {
                    valExHC = Double.parseDouble(PanelCalibracion2Pts.props.getProperty("EPAHC2T"));
                    valExCo2 = Double.parseDouble(PanelCalibracion2Pts.props.getProperty("EPACO22T"));
                    valExCo = Double.parseDouble(PanelCalibracion2Pts.props.getProperty("EPACO2T"));
                }    
                System.out.println("variable---------------------------------------------------------------------"+valExHC);
                System.out.println("PEF: " + pef);
                double valoTolMin = (ptoCalibracion.getValorHC() ) - valExHC;
                System.out.println("--------------valor minimo-----------------");
                double valoTolMax = (ptoCalibracion.getValorHC() ) +valExHC;
                System.out.println("-----------------valor maximo--------------------");
               boolean validacionHc ;
                //double valorTolerancia = 0.99;               
                System.out.println("\nPEF: " + pef); 
                JOptionPane.showMessageDialog(null, "Valores tolerancia de hc  ("+valoTolMin+" y "+valoTolMax+" ) valor Recogido del canal de Hc div Pef: "+medicionGases.getValorHC()/pef);
                if(valoTolMin <= (medicionGases.getValorHC()/pef)  &&  valoTolMax >= (medicionGases.getValorHC()/pef)){
                     validacionHc=true;
                }else{
                     validacionHc=false;
                }               
                valoTolMin = (ptoCalibracion.getValorCO() *0.01) - valExCo;
                valoTolMax = (ptoCalibracion.getValorCO() *0.01) + valExCo;
              //  JOptionPane.showMessageDialog(null, "Valores tolerancia de CO ("+valoTolMin+" y "+valoTolMax+" Valor Recogido del canal de CO: "+medicionGases.getValorCO()* 0.01);
                boolean validacionCo;
                if(valoTolMin <= (medicionGases.getValorCO()*0.01)  &&  valoTolMax >= (medicionGases.getValorCO()*0.01)){
                    validacionCo=true;
                }else{
                    validacionCo=false;
                }              
                boolean validacionCo2;
                valoTolMin = (ptoCalibracion.getValorCO2() *0.1) - valExCo2;
                valoTolMax = (ptoCalibracion.getValorCO2() *0.1) + valExCo2;
              //  JOptionPane.showMessageDialog(null, "Valores tolerancia de CO2 ("+valoTolMin+" y "+valoTolMax+" Valor Recogido del canal de CO2: "+medicionGases.getValorCO2()* 0.1);
                if(valoTolMin <= (medicionGases.getValorCO2()*0.1)  &&  valoTolMax >= (medicionGases.getValorCO2()*0.1) ){
                    validacionCo2=true;
                }else{
                    validacionCo2=false;
                }
                //else {
                System.out.println(String.format("---> Banco <---\nHC: %s\nCO: %s\nCO2: %s", medicionGases.getValorHC() ,
                        medicionGases.getValorCO() * 0.01,
                        medicionGases.getValorCO2() * 0.1));
                /*Hexanos Calibracion
                ptoCalibracion.setBancoHc(Double.parseDouble(String.valueOf(medicionGases.getValorHC()/pef)) );*/
                ptoCalibracion.setBancoHc(Double.parseDouble(String.valueOf(medicionGases.getValorHC())) );
                ptoCalibracion.setBancoCo(medicionGases.getValorCO() * 0.01);
                ptoCalibracion.setBancoCo2(medicionGases.getValorCO2() * 0.1);
                ptoCalibracion.setValorOxigeno((short)medicionGases.getValorO2());
                // }
                if (validacionHc== false || validacionCo==false || validacionCo2==false){
                    Mensajes.mensajeError("Tolerancia Incorrecta\n Por Favor Verifique que la Pipeta sea la Correcta ..!");
                    procesoExitoso = false;
                    procesoAltaExitoso = false;
                     if (validacionHc== false ) {
                          Mensajes.mensajeError("salida Rango En el canal HC..!");
                     }
                     if (validacionCo== false ) {
                          Mensajes.mensajeError("salida Rango En el canal CO..!");
                     }
                   if (validacionCo2== false ) {
                          Mensajes.mensajeError("salida Rango En el canal CO2..!");
                     }
                    //--> SIN CALIBRACION
                    Window w = SwingUtilities.getWindowAncestor(panelProgreso);
                    //w.dispose();
                    banco.mapaFisicoES(mapa, true);
                    timer.stop();
                    //return;
                }
                contadorTimer = 0;
                while (medicion.getValorPres() >= umbral - 5 && medicion.getValorPres() <= umbral + 20 ) {
                    panelProgreso.getBarraTiempo().setValue(contadorTimer);
                    Thread.sleep(55);
                    medicion = banco.obtenerDatos();
                    if (procesoAltaExitoso == false) {
                        contadorTimer = 3;
                    }
                    //En que parte se pasa contadorTimer
                    if (contadorTimer > 2) {
                        panelProgreso.getLabelMensaje().setText("Proceso Exitoso enviando Comando ..!");
                       // banco.leerEscribirDatosCal(true, ptoCalibracion, 21);
                       // banco.ordenarCalibracion(3);
                        ///Validacion para Reprobar Calibracion
                        if (procesoAltaExitoso = false) {
                            procesoAltaExitoso = false;
                        } else {
                            procesoAltaExitoso = true;
                        }
                        panelProgreso.getBarraTiempo().setValue(contadorTimer);
                        presionOk = true;
                        break;
                    } else {
                        panelProgreso.getLabelMensaje().setText(" .-PRESION EN RANGO-. ");
                    }
                    medicion = banco.obtenerDatos();
                    radial.setValue(medicion.getValorPres());
                   
                }
            }
            contadorTimer = 0;
            botonHabilitar.setEnabled(true);
            banco.mapaFisicoES(mapa, true);
            timer.stop();
            System.out.println("----->> Finalizando Proceso Alta <<-----");
        } catch (HeadlessException | InterruptedException ex) {
            Mensajes.mostrarExcepcion(ex);
        }
    }

    private void procesoMuestra() {
        System.out.println("|----->> Iniciando Proceso Muestra <<-----");
    try {
      this.panelProgreso.getBarraTiempo().setMaximum(8);
      this.timer = new Timer(1000, new ActionListener()
      {
        public void actionPerformed(ActionEvent e) {
          ProcesoCalibracion.this.contadorTimer += 1;
        }
      });
      int umbral = 0;
      this.timer.start();
      if (this.validaPresion) {
        this.banco.encenderCalSol1(false);
        Thread.sleep(100L);
        this.banco.encenderCalSol2(true);
        Thread.sleep(100L);
        this.banco.encenderSolenoideUno(true);
        Thread.sleep(100L);
        this.banco.encenderSolenoide2(false);
        Thread.sleep(100L);
        this.banco.encenderDrainPump(false);
        Thread.sleep(100L);
        this.banco.encenderBombaMuestras(false);
        Thread.sleep(100L);

        this.panelProgreso.getLabelMensaje().setText("Ingresando Gas...!");

        this.medicion = this.banco.obtenerDatos();
        int presionInicial = this.medicion.getValorPres();
        this.sr.ponerElemento(presionInicial);

        while (!this.sr.isStable()) {
          this.sr.ponerElemento(this.medicion.getValorPres());
          Thread.sleep(100L);
        }
        presionInicial = (int)this.sr.media();
        this.radial.setValue(presionInicial);        
        umbral = presionInicial + 30;
        this.panelProgreso.getLabelMensaje().setText("Abrir un poco la válvula");
      }
      Thread.sleep(1500L);
      boolean presionOk = false;

      while (!presionOk) {
        this.medicion = this.banco.obtenerDatos();
        if (this.validaPresion){
          while (((this.medicion.getValorPres() <= umbral) || (this.medicion.getValorPres() >= umbral + 15)) && (this.contadorTimer < 180)) {
            Thread.sleep(100L);

            if (this.medicion.getValorPres() < umbral)
              this.panelProgreso.getLabelMensaje().setText("Ajustar presión a " + umbral + " SUBA LA PRESION" + this.contadorTimer + " s");
            else if (this.medicion.getValorPres() > umbral + 10) {
              this.panelProgreso.getLabelMensaje().setText("Ajustar presión A" + umbral + " BAJE LA PRESION" + this.contadorTimer + " s");
            }

            this.medicion = this.banco.obtenerDatos();
            this.sr.ponerElemento(this.medicion.getValorPres());
                      
          }

          if (this.contadorTimer >= 180) {
            JOptionPane.showMessageDialog(null, "Presion inadecuada ... calibracion cancelada");
            this.procesoExitoso = false;
            Thread.sleep(250L);
            Window w = SwingUtilities.getWindowAncestor(this.panelProgreso);
            w.dispose();
            this.banco.encenderSolenoideUno(false);
            this.banco.encenderCalSol2(false);
            this.timer.stop();
          }
        } else {
          Mensajes.mensajeAdvertencia("Por Favor Ingrese el gas de calibracion a 5 PSI");
          this.banco.encenderSolenoideUno(true);
        }
        this.panelProgreso.getLabelMensaje().setText("Validando Tolerancia del Gases BANCO CAPELEC");
        Thread.sleep(2000L);

        MedicionGases medicionGases = this.banco.obtenerDatos();
        double valorTolerancia = 0.4D;

        double pef = Double.parseDouble(this.banco.obtenerPEF()) / 1000.0D;

        System.out.println("PEF: " + pef);
        System.out.println(String.format("(%s - %s) > (%s)", new Object[] { Double.valueOf(this.ptoCalibracion.getValorHC() * pef), Integer.valueOf(medicionGases.getValorHC()), Double.valueOf(this.ptoCalibracion.getValorHC() * pef * valorTolerancia) }));
        System.out.println(String.format("(%d - %d) > (%s)", new Object[] { Short.valueOf(this.ptoCalibracion.getValorCO()), Integer.valueOf(medicionGases.getValorCO()), Double.valueOf(this.ptoCalibracion.getValorCO() * valorTolerancia) }));
        System.out.println(String.format("(%d - %d) > (%s)", new Object[] { Short.valueOf(this.ptoCalibracion.getValorCO2()), Integer.valueOf(medicionGases.getValorCO2()), Double.valueOf(this.ptoCalibracion.getValorCO2() * valorTolerancia) }));

        boolean validacionHc = Math.abs(this.ptoCalibracion.getValorHC() * pef - medicionGases.getValorHC()) > this.ptoCalibracion.getValorHC() * valorTolerancia;
        boolean validacionCo = Math.abs(this.ptoCalibracion.getValorCO() - medicionGases.getValorCO()) > this.ptoCalibracion.getValorCO() * valorTolerancia;
        boolean validacionCo2 = Math.abs(this.ptoCalibracion.getValorCO2() - medicionGases.getValorCO2()) > this.ptoCalibracion.getValorCO2() * valorTolerancia;

        System.out.println(String.format("\n---> Banco <---\nHC: %s\nCO: %s\nCO2: %s", new Object[] { Double.valueOf(medicionGases.getValorHC() / pef), Double.valueOf(medicionGases.getValorCO() * 0.01D), Double.valueOf(medicionGases.getValorCO2() * 0.1D) }));

        //this.ptoCalibracion.setBancoHc(Double.valueOf(medicionGases.getValorHC() / pef));
        this.ptoCalibracion.setBancoHc(Double.valueOf(medicionGases.getValorHC()));
        this.ptoCalibracion.setBancoCo(Double.valueOf(medicionGases.getValorCO() * 0.01D));
        if ((this.banco instanceof BancoCapelec))
          this.ptoCalibracion.setBancoCo2(Double.valueOf(medicionGases.getValorCO2() * 0.01D));
        else {
          this.ptoCalibracion.setBancoCo2(Double.valueOf(medicionGases.getValorCO2() * 0.1D));
        }
        ptoCalibracion.setValorOxigeno((short)medicionGases.getValorO2());
        this.contadorTimer = 0;

        while (((this.medicion.getValorPres() >= umbral - 5) && (this.medicion.getValorPres() <= umbral + 20) && (this.contadorTimer < 8)) || (!this.validaPresion)) {
          this.panelProgreso.getBarraTiempo().setValue(this.contadorTimer);
          Thread.sleep(50L);
          this.medicion = this.banco.obtenerDatos();

          if (this.contadorTimer > 2) {
            this.panelProgreso.getBarraTiempo().setValue(this.contadorTimer);
            this.banco.calibracion(BancoCapelec.TipoCalibracion.UNPUNTO, this.ptoCalibracion.getValorCO() / 100.0D, this.ptoCalibracion.getValorCO2() / 10.0D, this.ptoCalibracion.getValorHC());

            while (this.contadorTimer < 7) {
              this.medicion = this.banco.obtenerDatos();
              if ((this.contadorTimer > 4) && (!this.medicion.isCalibracionEnProgreso())) {
                break;
              }

            }
              if (validacionHc== false || validacionCo==false || validacionCo2==false) {
                   this.procesoBajaExitoso = false;
                    this.procesoAltaExitoso = false;
            }else {
              this.procesoBajaExitoso = true;
               this.procesoAltaExitoso = true;
            }
            presionOk = true;
            this.panelProgreso.getLabelMensaje().setText("Proceso Exitoso enviando Comando");
            break;
          }
          this.panelProgreso.getLabelMensaje().setText(this.validaPresion == true ? "PRESION EN RANGO " : "");

          this.medicion = this.banco.obtenerDatos();
          this.radial.setValue(this.medicion.getValorPres());
         
        }
      }

      this.botonHabilitar.setEnabled(true);
      this.banco.encenderSolenoideUno(false);
      this.banco.encenderCalSol2(false);
      this.timer.stop();

      System.out.println("----->> Finalizando Proceso Muestra <<-----");
    } catch (HeadlessException|InterruptedException ex) {
      Mensajes.mostrarExcepcion(ex);
    }
    }

    public void setMuestra(int muestra) {
        this.muestra = muestra;
    }

    public void setPtoCalibracion(PuntoCalibracion ptoCalibracion) {
        this.ptoCalibracion = ptoCalibracion;
    }

    public PuntoCalibracion getPtoCalibracion() {
        return ptoCalibracion;
    }

    public boolean isProcesoAltaExitoso() {
        return procesoAltaExitoso;
    }

    public void setProcesoAltaExitoso(boolean procesoAltaExitoso) {
        this.procesoAltaExitoso = procesoAltaExitoso;
    }

    public boolean isProcesoBajaExitoso() {
        return procesoBajaExitoso;
    }

    public void setProcesoBajaExitoso(boolean procesoBajaExitoso) {
        this.procesoBajaExitoso = procesoBajaExitoso;
    }

}
