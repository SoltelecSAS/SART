/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.pruebasgases.motocicletas;

import com.soltelec.loginadministrador.ConsultasLogin;
import com.soltelec.loginadministrador.LoginServiceCDA;
import eu.hansolo.steelseries.tools.BackgroundColor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import org.jdesktop.swingx.JXLoginPane;
import org.soltelec.medicionrpm.MedidorRevTemp;
import org.soltelec.procesosbanco.BancoGasolina;
import org.soltelec.procesosbanco.PanelCero;
import org.soltelec.pruebasgases.CondicionesAnormalesException;
import org.soltelec.pruebasgases.HumoNegroException;
import org.soltelec.pruebasgases.ListenerCancelacionGases;
import org.soltelec.pruebasgases.PanelPruebaGases;
import org.soltelec.pruebasgases.WorkerCruceroRalenti;
import org.soltelec.util.GenerarArchivo;
import org.soltelec.util.Mensajes;
import org.soltelec.util.InfoVehiculo;
import org.soltelec.util.MedicionGases;
import org.soltelec.util.RegistrarMedidas;
import org.soltelec.util.UtilInfoVehiculo;
import org.soltelec.util.Utilidades;
import org.soltelec.util.capelec.BancoCapelec;
import utiltermohigrometro.UtilPropiedades;

/**
 * Clase para hacer la simulacion de las revoluciones y la temperatura de la
 * prueba de motos
 *
 * @author Usuario
 */
public class CallableSimulacionMotos implements Callable<List<MedicionGases>> {

    private BancoGasolina banco;
    private PanelPruebaGases panel;
    private long idPrueba, idUsuario;
    private int numeroTiempos;
    private int numeroEscapes;
    private Timer timer, timerSimulacion, timerHc;
    private int contadorTemporizacion, contadorSimulacion, contadorHC;
    private Double rpmsSimuladas;
    private Random random;
    private int temp;
    private List<MedicionGases> listaDeListas;
    private List<MedicionGases> lista;
    private MedicionGases medicion;
    private int LIM_TEMP = 40;
     private String placas;
    public static String humo_mot;
    private final MedidorRevTemp medidorRevTemp;
    List<MedicionGases> lista50Datos =null;
    List<MedicionGases> lista10Datos =null;
    List<MedicionGases> lstMedFur =null;
    FileWriter fw = null, fw2 = null;
    BufferedWriter bwP,bwP2;
    DecimalFormat df ;
    String out ="",out2 ="";
    private int valorHC;
    String activarBotonRPM;
    String  ActivarLogs,activarDismiHC;

    public CallableSimulacionMotos(PanelPruebaGases panel, BancoGasolina banco, MedidorRevTemp medidorRevTemp, int numeroTiempos, int numeroEscapes, long idPrueba, long idUsuario,String placas) {
        this.panel = panel;
        this.banco = banco;
        this.medidorRevTemp = medidorRevTemp;
        this.numeroEscapes = numeroEscapes;
        this.numeroTiempos = numeroTiempos;
        this.idPrueba = idPrueba;
        this.idUsuario = idUsuario;
        this.placas = placas;
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contadorTemporizacion++;
            }
        });
        timerHc = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contadorHC++;
            }
        });
        random = new Random();
    }//end of method CallableSimulacionMotos

    @Override
    public List<MedicionGases> call() throws Exception {
        
         try {         
            ActivarLogs = UtilPropiedades.cargarPropiedad("activarLogs", "propiedades.properties");
            
             if(ActivarLogs==null)
            {
                ActivarLogs="false";
            }
            
            ActivarLogs = (ActivarLogs.equalsIgnoreCase("") ? "false" : ActivarLogs);            

        } catch (Exception e) {
            System.out.println("-----------------------------------------------------------");
            System.out.println("----------------------ERRROR ------------------------------");
            System.out.println("Error al cargar la variable :ActivarLogs del properties" + e.getMessage());

        }
        System.out.println("entro en hilo de simulacion de motos");
        InfoVehiculo infoVehiculo = UtilInfoVehiculo.traerInfoVehiculo(idPrueba);
        this.numeroTiempos= infoVehiculo.getNumeroTiempos();
        this.numeroEscapes= infoVehiculo.getNumeroExostos();                
        try {
            List<MedicionGases> listaRalentiUno = new ArrayList<>();
            List<MedicionGases> listaRalentiDos = new ArrayList<>();
            System.out.println("entro en hilo de simulacion de motos 0 .1 ");
            listaDeListas = new ArrayList<>();
            lista = new ArrayList<>();
            panel.getPanelFiguras().setVisible(false);
            panel.getButtonRpm().setVisible(false);
            panel.getPanelMensaje().setVisible(true);
            panel.getProgressBar().setVisible(false);
            //Validar Temperatura
            System.out.println("Diseno scoter " + infoVehiculo.getDiseño());
            if (!(infoVehiculo.getDiseño().equalsIgnoreCase("Scooter"))) {
                Mensajes.messageWarningTime("INICIANDO VALIDACION DE TEMPERATURA  SEGUN LA NORMA NTC 5365 ", 5);
            }
            System.out.println("entro en hilo de simulacion de motos 1");
            Mensajes.messageDoneTime(">>> TEMPERATURA VALIDA PARA CONTINUAR PRUEBA  <<<", 4);
            if (infoVehiculo.getDiseño().equalsIgnoreCase("Scooter")) {
                if (Mensajes.mensajePregunta("¿El motor de la Moto se ha mantenido\n encendido por 10 min?")) {
                    temp = 0;
                    panel.getLinearTemperatura().setValue(temp);
                    LIM_TEMP = 0;
                } else {
                    JOptionPane.showMessageDialog(null, "Por Favor Reanude la prueba cuando la Moto este listo");
                    banco.getPuertoSerial().close();
                    //medidorRevTemp.stop();
                    panel.cerrar();
                    return null;
                }
            } else {
                temp = 41 + random.nextInt(25);
                panel.getLinearTemperatura().setValue(temp);
            }
            System.out.println("voy a entrar a primera Aceleracion");
            Boolean retorno = primeraAceleracion();
            if (retorno == true) {                
                 CallablePruebaMotos.tempStored= temp;
                List<MedicionGases> lecturaRechazada = new ArrayList<>();
                return lecturaRechazada;
            }
            System.out.println("SALGO DE PRIMERA ACELERACION");
            retorno = variosEscapes();
            if (retorno == true) {
                List<MedicionGases> lecturaRechazada = new ArrayList<>();
                return lecturaRechazada;
            }
            //inicioPrueba();
            panel.cerrar();
            return this.lstMedFur;

        } catch (InterruptedException iexc) {
            liberarRecursos();
            throw new CancellationException(iexc.getMessage());
        }
    }

    private Boolean primeraAceleracion() throws InterruptedException, FileNotFoundException, IOException {

        //crea un timer para la simulacion de la primera aceleracion
        //la cual dura 10 seg, rpm rango entre 2500 y 3000            
        timerSimulacion = new Timer(250, new ActionListener() {
            int base = 3000 - random.nextInt(300);

            @Override
            public void actionPerformed(ActionEvent e) {
                contadorSimulacion++;
                rpmsSimuladas = ((base) * (1 - Math.exp(0 - contadorSimulacion * 0.31))) + (0 - 25 - random.nextInt(25));
                rpmsSimuladas = rpmsSimuladas - rpmsSimuladas % 10;
            }//end of actionPerformed
        });

        panel.getPanelFiguras().setVisible(false);
        panel.getPanelMensaje().setVisible(true);
        panel.getPanelMensaje().setText(" ACELERE entre 2500 y 3000 RPMS \n MANTENGA DURANTE 10 seg. ");
        Thread.sleep(2500);

        panel.getPanelFiguras().setVisible(true);
        panel.getPanelMensaje().setVisible(false);

        panel.getRadialTacometro().setBackgroundColor(BackgroundColor.BLACK);
        contadorTemporizacion = 0;
        rpmsSimuladas = 0.0;

        timerSimulacion.start();
        timer.start();

        while (rpmsSimuladas < 2500 || rpmsSimuladas > 3000) {
            Thread.sleep(200);
            panel.getMensaje().setText(" Acelere entre 2500 y 3000 rpm T:" + contadorTemporizacion);
            panel.getRadialTacometro().setValue(rpmsSimuladas);
        }

        contadorTemporizacion = 0;
        panel.getProgressBar().setMaximum(10);
        panel.getRadialTacometro().setBackgroundColor(BackgroundColor.GREEN);
        while (rpmsSimuladas >= 2500 && rpmsSimuladas <= 3000 && contadorTemporizacion < 10) {
            panel.getRadialTacometro().setValue(rpmsSimuladas);
            panel.getProgressBar().setVisible(true);
            Thread.sleep(200);
            panel.getMensaje().setText("POR FAVOR MANTENGA ACELERADA DURANTE 10 Seg. ");
            panel.getProgressBar().setValue(contadorTemporizacion);
            panel.getProgressBar().setString(String.valueOf(contadorTemporizacion).concat(" Seg."));
        }
        panel.getRadialTacometro().setValue(rpmsSimuladas);
        panel.getProgressBar().setValue(0);
        panel.getProgressBar().setString("");
        liberarRecursos();
        contadorTemporizacion = 0;
        CallablePruebaMotos.rpmStored=rpmsSimuladas.intValue();            
        rpmsSimuladas = 0.0;
        if (numeroTiempos == 4 /*|| numeroTiempos == 2*/) {
//            if (Mensajes.mensajePregunta("Hubo presencia de humo negro o de humo azul")) {
//                timer.stop();
//                banco.encenderBombaMuestras(false);
//                Thread.sleep(100);
//                banco.getPuertoSerial().close();
//
//                throw new HumoNegroException();
//            }
            int opcion = JOptionPane.showOptionDialog(null, "¿VISUALIZO PRESENCIA DE HUMO?",
                    "SART 1.7.3", JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Negro", "Azul", "NO"}, "NO");
            if (opcion == JOptionPane.YES_OPTION) {
                timer.stop();
                banco.encenderBombaMuestras(false);
                Thread.sleep(100);
                banco.getPuertoSerial().close();//aun asi el orden es importante como la forma
                System.out.println("4.1.1.1.7  Selecciono Humo negro");
                humo_mot = "negro";
                panel.getFuncion().setText("4.1.1.1.7 Presencia de humo negro o azul (Negro)");
                return true;
            } else if (opcion == JOptionPane.NO_OPTION) {
                timer.stop();
                banco.encenderBombaMuestras(false);
                Thread.sleep(100);
                banco.getPuertoSerial().close();//aun asi el orden es importante como la forma
                System.out.println("4.1.1.1.7 Selecciono Humo Azul");
                humo_mot = "azul";
                panel.getFuncion().setText("4.1.1.1.7 Presencia de humo negro o azul (Azul)");
                return true;
            }
            if (CallableInicioMotos.lecturaCondicionesAnormales.length() > 3) {
                panel.getFuncion().setText("Condiciones Anormales");
                return true;
            }
        }
        timerSimulacion.stop();
        return false;
    }

    private boolean variosEscapes() throws InterruptedException, FileNotFoundException, IOException, Exception {

        timerSimulacion = new Timer(250, new ActionListener() {
            int base = 1800 - random.nextInt(900);

            @Override
            public void actionPerformed(ActionEvent e) {
                contadorSimulacion++;
                rpmsSimuladas = ((base) * (1 - Math.exp(0 - contadorSimulacion * 0.31))) + (0 - 25 - random.nextInt(25));
                rpmsSimuladas = rpmsSimuladas - rpmsSimuladas % 10;
            }//end of actionPerformed
        });
        System.out.println("ESTOY DENTRO DEL METODO VARIOS ESCAPES");

        try 
        {
            activarDismiHC = UtilPropiedades.cargarPropiedad("activarDismiHC", "propiedades.properties");
            ActivarLogs = UtilPropiedades.cargarPropiedad("activarLogs", "propiedades.properties");           
             if(ActivarLogs==null)
            {
                ActivarLogs="false";
            }
            if(activarDismiHC==null)
            {
                activarDismiHC="True";
            }
            ActivarLogs = ( ActivarLogs.equalsIgnoreCase("") ? "false" : ActivarLogs);
            activarDismiHC = ( activarDismiHC.equalsIgnoreCase("")) ? "true" : activarDismiHC;
            System.out.println("estado  de los logs :" + ActivarLogs);
            System.out.println("estado  de activarDismiHC :" + activarDismiHC);
          // activarDismiHC = Boolean.parseBoolean(activarBotonRPM);
        } catch (Exception e) {
            System.out.println("-----------------------------------------------------------");
            System.out.println("----------------------ERRROR ------------------------------");
            System.out.println("Error al cargar la variable :activarDismiHC del properties" +e.getMessage());
            
        }

        int j = 1;
        try {
            lstMedFur = new ArrayList<>();
            while (j <= numeroEscapes) {
                lista = new ArrayList<MedicionGases>();
                System.out.println("INICIALIZO LISTA DE MEDICION DE GASES");
                if (j != 0) {
                    //Hacer cero y hacer preuba de residuos
                    JOptionPane.showMessageDialog(null, "Por Favor Retire la sonda de prueba \n Iniciando  Cero en el Banco ");
                    panel.getProgressBar().setString(""); 
                    panel.getMensaje().setText("");
                    Thread.sleep(2500);

                    panel.getPanelFiguras().setVisible(false);
                    panel.getPanelMensaje().setVisible(true);

                    PanelCero panelCero = new PanelCero(); //PanelCero es una mezcla de GUI con hilo refactorizar
                    panel.getPanelMensaje().setText("POR FAVOR ESPERE \n Haciendo Cero en el Banco ...!");
                    panelCero.setBanco(banco);
                    panelCero.setBarraTiempo(this.panel.getProgressBar());
                    panelCero.setLabelMensaje(this.panel.getPanelMensaje().getCronometro());
                    Thread t1 = new Thread(panelCero);
                    t1.start();

                    try {
                        t1.join(); //Espera a que el hilo del proceso  termine
                    } catch (InterruptedException ex) {
                        System.out.println("Error haciendo join con hilo de cero");
                    }
                    int limiteHC;                   
                    if (numeroTiempos == 4) {
                        limiteHC = 20;                        
                    } else {
                        limiteHC = 500;                        
                    }
                    Thread.sleep(1500);
                    medicion = banco.obtenerDatos();
                    contadorTemporizacion = 0;
                    panel.getProgressBar().setMaximum(BancoGasolina.TIEMPO_HC_DESCIENDA);
                    panel.getProgressBar().setValue(BancoGasolina.TIEMPO_HC_DESCIENDA);
                    //panel.getProgressBar().setVisible(true);
                    banco.encenderBombaMuestras(true);
                    panel.getPanelMensaje().setText("Esperando a disminucion de HC");
                    Thread.sleep(2000);
                    medicion = banco.obtenerDatos();
                    contadorTemporizacion = 0;
                    timerHc.start();
                    int temp;
                    if (activarDismiHC.equalsIgnoreCase("True")) 
                    {
                        valorHC = this.medicion.getValorHC();
                        System.out.println("--------------------------------------------");
                        System.out.println("------------ESTAMOS EN LA DISMI HC ---------");
                        System.out.println("-------------------------------------------");
                        System.out.println("Valor capturado HC Inicial: " + valorHC);
                        System.out.println("Valor Limite HC : " + limiteHC);
                        
                        while ((valorHC > limiteHC) && (this.contadorTemporizacion < 270))
                        {
                            Thread.sleep(1000);
                            if (PanelCero.calibradoCero.intValue() == 1) {
                                this.panel.getPanelMensaje().setText(" EL CERO DEL EQUIPO SE ENCUENTRA DESVIADO \n POR FAVOR CONSULTE AL SOPORTE TECNICO ...!");
                                Thread.sleep(10000);
                                this.panel.getFuncion().setText("condicioninesperada");
                                return true;
                            }
                            int randon=generarRandon(valorHC);
                            valorHC = valorHC - randon;
                            System.out.println("Valor dismi HC: " + randon);
                            System.out.println("Valor Resultante HC : " + valorHC);
                            this.panel.getPanelMensaje().setText(new StringBuilder().append(" HC: ").append(valorHC).toString()+ "ppm");
                            this.panel.getProgressBar().setValue(270 - this.contadorTemporizacion);
                        }

                    } else {

                        while ((this.medicion.getValorHC() > limiteHC) && (this.contadorTemporizacion < 270)) {
                            Thread.sleep(1000);
                            if (PanelCero.calibradoCero.intValue() == 1) {
                                this.panel.getPanelMensaje().setText(" EL CERO DEL EQUIPO SE ENCUENTRA DESVIADO \n POR FAVOR CONSULTE AL SOPORTE TECNICO ...!");
                                Thread.sleep(10000);
                                this.panel.getFuncion().setText("condicioninesperada");
                                return true;
                            }
                            this.medicion = this.banco.obtenerDatos();
                            this.panel.getPanelMensaje().setText(new StringBuilder().append(" HC: ").append(this.medicion.getValorHC()).toString()+ "ppm");
                            this.panel.getProgressBar().setValue(270 - this.contadorTemporizacion);
                        }

                    }
                    this.panel.getPanelMensaje().setText("Inicia con HC  de " + valorHC + "ppm");
                    Thread.sleep(1500);
                    banco.encenderBombaMuestras(false);
                    panel.getProgressBar().setValue(0);
                    if (contadorTemporizacion >= BancoGasolina.TIEMPO_HC_DESCIENDA) {
                        //Si sale del while por tiempo la prueba se cancela
                        panel.getPanelMensaje().setText("HC NO LOGRO DECENDER");
                        JOptionPane.showMessageDialog(panel, "Disculpe, la Prueba sera abortada por no descenso de HC en el Banco");
                        liberarRecursos();
                        panel.getFuncion().setText("condicioninesperada");
                        throw new CancellationException();
                    }//end if se demora mucho descendiendo

                } //end of if i!= 0 o sea que no sea el primer tubo de escape
                System.out.println("TERMINO DE HACER CERO EN EL BANCO");
                Thread.sleep(50);
                panel.getPanelMensaje().setText("Exosto # " + j);
                //Mientras este fuera de Rango, debe entrar al rango
                JOptionPane.showMessageDialog(null, "Por favor Introduzca la Sonda de Prueba a Minimo(300 mm)en el Exhosto ");

                panel.getPanelFiguras().setVisible(true);
                panel.getPanelMensaje().setVisible(false);

                liberarRecursos();
                timer.start();
                timerSimulacion.start();

                panel.getRadialTacometro().setBackgroundColor(BackgroundColor.BLACK);
                panel.getRadialTacometro().setValue((int) Math.floor(rpmsSimuladas));
                panel.getLinearTemperatura().setValue(temp);
                banco.encenderBombaMuestras(true);
                medicion = banco.obtenerDatos();
                System.out.println("RECOGIENDO INFORMACION DEL BANCO DE GASES");
                boolean cicloTerminado = false;
                while (!cicloTerminado) {
                    boolean dioxValidad = false;
                    this.lista50Datos = new ArrayList<>();
                    this.lista10Datos = new ArrayList<>();
                    int nroInt = 1;
                    contadorTemporizacion = 0;
                    //Mientras el valor de revoluciones este por fuera de 1050 y 2000 espere o mientras sea la temperatura espere
                    panel.getRadialTacometro().setBackgroundColor(BackgroundColor.BLACK);
                    while ((rpmsSimuladas < 800.0) || (rpmsSimuladas > 1800.0) || (medicion.getValorO2() * 0.01 > 18.0) || (temp < LIM_TEMP) || ((medicion.isBajoFlujo()) && (contadorTemporizacion < 60))) {
                        panel.getRadialTacometro().setValue((int) Math.floor(rpmsSimuladas));
                        panel.getLinearTemperatura().setValue(temp);
                        StringBuilder mensaje = new StringBuilder("");
                        if (rpmsSimuladas <= BancoGasolina.LIM_RALENTI_MOTOS_SUP - 900 || rpmsSimuladas >= BancoGasolina.LIM_RALENTI_MOTOS_SUP - 50) {
                            mensaje.append("DETECTO REV FUERA DE RANGO ");
                        }
                        if (temp < LIM_TEMP) {
                            mensaje.append(".-DETECTO TEMP BAJA ");
                        }
                        if (medicion.isBajoFlujo()) {
                            mensaje.append(".-DETECTO BAJO FLUJO ");
                        }

                        while (dioxValidad == false) {
                            medicion = banco.obtenerDatos();
                            //validacion co2 medicion.getValorCO2() * 0.1 ; el valor permitido es 1
                            dioxValidad = validadorCO2((17), (medicion.getValorO2() * 0.01), medicion, nroInt);
                            if (dioxValidad == true) {
                                break;
                            }
                            nroInt++;
                            if (nroInt == 4) {
                                panel.getFuncion().setText("condicioninesperada");
                                throw new CancellationException();
                            }
                        }
                        if (nroInt > 1) {
                            timer.start();
                            dioxValidad = false;
                            nroInt = 1;
                        }
                        if (contadorTemporizacion >= BancoGasolina.TIEMPO_FUERA_CONDICIONES) {
                            JOptionPane.showMessageDialog(null, "Disculpe No puedo CONTINUAR realizando la prueba. (Fuera de Condiciones)");
                            banco.getPuertoSerial().close();
                            liberarRecursos();
                            panel.cerrar();
                            banco.encenderBombaMuestras(false);
                            panel.getFuncion().setText("condicioninesperada");
                            throw new CancellationException();
                        }
                        panel.getMensaje().setText(mensaje + " t: " + contadorTemporizacion);
                        medicion = banco.obtenerDatos();
                        Thread.sleep(50);

                    } //edn while fuera de rango                    

                    //edn while fuera de rango
                    //Si no logra acelerar en un minuto termina la prueba
                    if (contadorTemporizacion >= BancoGasolina.TIEMPO_FUERA_CONDICIONES) {
//                        JOptionPane.showMessageDialog(null, "No se puede realizar la prueba");
//                        liberarRecursos(); //sale porque no se pueden tomar mediciones en un minuto pero no registra la cancelacion de la prueba
//                        throw new Exception("Condiciones inadecuadas para realizar la prueba");
                        JOptionPane.showMessageDialog(null, "Disculpe No puedo CONTINUAR realizando la prueba. (Fuera de Condiciones)");
                        banco.getPuertoSerial().close();
                        liberarRecursos();
                        panel.cerrar();
                        banco.encenderBombaMuestras(false);
                        panel.getFuncion().setText("condicioninesperada");
                        throw new CancellationException();
                    }

                    banco.encenderBombaMuestras(true);//entre 1050 y 1900
                    panel.getRadialTacometro().setValue((int) Math.floor(rpmsSimuladas));
                    panel.getProgressBar().setMaximum(30);
                    panel.getLinearTemperatura().setValue((int) Math.floor(rpmsSimuladas));
                    medicion = banco.obtenerDatos();
                    contadorTemporizacion = 0;
                    panel.getRadialTacometro().setBackgroundColor(BackgroundColor.RED);
                    int valorO2 = 0;
                    while ((rpmsSimuladas >= 800.0) && (rpmsSimuladas <= 1800.0) && (!medicion.isBajoFlujo()) && (temp >= LIM_TEMP) && (contadorTemporizacion < 30) && (medicion.getValorO2() * 0.01 < 18.0)) {
//                    while ((rpmsSimuladas >= BancoGasolina.LIM_RALENTI_MOTOS_SUP - 1000 && rpmsSimuladas <= BancoGasolina.LIM_RALENTI_MOTOS_SUP) && !medicion.isBajoFlujo()
//                            && temp >= LIM_TEMP && contadorTemporizacion < 30) {
                        //mientras este en rango
                        panel.getRadialTacometro().setValue((int) Math.floor(rpmsSimuladas));
                        panel.getLinearTemperatura().setValue(temp);
                        while (dioxValidad == false) {
                            medicion = banco.obtenerDatos();
                           
                            dioxValidad = validadorCO2(17, (medicion.getValorO2() * 0.01), medicion, nroInt);
                            if (dioxValidad == true) {
                                break;
                            }
                            nroInt++;
                            if (nroInt == 4) {
                                panel.getFuncion().setText("condicioninesperada");
                                throw new CancellationException();
                            }
                        }
                        if (nroInt > 1) {
                            timer.start();
                            dioxValidad = false;
                            nroInt = 1;
                        }
                        medicion = banco.obtenerDatos();
                        medicion.setValorRPM((int) Math.floor(rpmsSimuladas));
                        System.out.println("LEEO DE NUEVO EN EL BANCO MEDICION REF " + medicion.toString());
                        medicion.setValorTAceite(temp);
                        if (contadorTemporizacion >= 25) {
                            System.out.println("valor del contado de temporizaciones: " + contadorTemporizacion);
                            panel.getMensaje().setText("Tomando Medidas ..!");
                            medicion.setLect5Seg(true);
                            this.lista10Datos.add(medicion);  //se activa cuando hayan auditoria ambientales 
                            this.lista10Datos.add(medicion); // COMPLETAR LST 2 DATOS X SEG DADO QUE AL TENER DOS RESOURCES HAY MAS DILACION
                            //lista.add(medicion);                          
                        } else {
                            medicion.setLect5Seg(false); //aplica para las auditoria ambientales
                            panel.getMensaje().setText("Ralenti, tiempo: " + contadorTemporizacion);
                            this.lista50Datos.add(medicion);
                            this.lista50Datos.add(medicion);
                             System.out.println("valor del contado de temporizaciones: " + contadorTemporizacion);
                        }
                        lista.add(medicion); // se activa cuando hayan auditoria ambientales
                        panel.getProgressBar().setValue(30 - contadorTemporizacion);
                        panel.getProgressBar().setString(String.valueOf(30 - contadorTemporizacion).concat(" Seg."));
                        medicion = banco.obtenerDatos();
                        System.out.println("LEEO DE NUEVO EN EL BANCO MEDICION REF " + medicion.toString());
                        valorO2 = (int) (medicion.getValorO2() * 0.01);
                        Thread.sleep(1);
                        System.out.println("valor del size de la lista 10 "+ lista10Datos.size());
                        System.out.println("valor del size de la lista 50 "+ lista50Datos.size());
                        //rpm = medidorRevTemp.getRpm();
                        //oxigeno = med.getValorO2()*0.01; en motos se debe tener en cuenta el oxigeno???
                        //temp = medidorRevTemp.getTemp();

                    } //end of while rango
                    ///banco.encenderBombaMuestras(false);
                    //end of while rango
                    //timer.stop();

                    if (contadorTemporizacion >= 30) {
                        cicloTerminado = true;
                        generarArchivoGases(this.placas,banco,j);
                        sacarPromMedidas();
                        liberarRecursos();
                        panel.getRadialTacometro().setValue(0);
                        panel.getRadialTacometro().setBackgroundColor(BackgroundColor.BLACK);
                        panel.getProgressBar().setValue(0);
                        panel.getProgressBar().setString("0 Seg.");
                        j++;//esto significa que si tomo datos
                        Mensajes.messageWarningTime("POR FAVOR RETIRE LA SONDA", 3);
                    }

                }//end of while cicloterminado
                System.out.println("EL TAMAÑO DE LA LISTA QUEDO EN: " + lstMedFur.size());                
                banco.encenderBombaMuestras(false);
            }
            System.out.println("SALGO DEL CICLO  ");
            banco.getPuertoSerial().close();
            liberarRecursos();
            fw.close();
            //fw2.close();
            bwP.close();
           // bwP2.close();
            System.out.println("LIBERANDO RECURSOS DE LOS TIMER");
        } catch (InterruptedException iexc) {
            liberarRecursos();
            throw new CancellationException("Callable cancelado");
        }
        return false;
    }
    
    private void sacarPromMedidas() {
        List<MedicionGases> listaPromedios = new ArrayList<MedicionGases>();
        double mediaHC= 0;
        double mediaCO= 0;
        double mediaCO2= 0;
        double mediaO2= 0;
        double mediaRPM= 0;
        double mediaTemp= 0;
        int tamanioLista = 0;
        System.out.println("TAMAÑO DE LA LST MET sacarMediaDeListas es " + tamanioLista);
        System.out.println("HC \t CO \t CO2 ;\t  O2 ");
        for (MedicionGases med : lista10Datos) {
            System.out.println(med.getValorHC() + ";\t" + med.getValorCO() + ";\t" + med.getValorCO2() + ";\t" + med.getValorO2());
            mediaHC = mediaHC + med.getValorHC();
            mediaCO = mediaCO + med.getValorCO();
            mediaCO2 = mediaCO2 + med.getValorCO2();
            mediaO2 = mediaO2 + med.getValorO2();
            mediaRPM = mediaRPM + med.getValorRPM();                         
        }
            //  System.out.println("Oxigeno FUR" + mediaO2 * 100);
            //  System.out.println("TERMINE DE SACAR LOS ACUMULADOS (sacarMediaDeListas) ");
            mediaHC = mediaHC / 10;
            mediaCO = mediaCO / 10;
            mediaCO2 = mediaCO2 / 10;
            mediaO2 = mediaO2 / 10;
            mediaRPM = mediaRPM / 10;
            mediaTemp =GenerarArchivo.temperatura ;
            //mediaTemp = mediaTemp / tamanioLista;
            MedicionGases medicion = new MedicionGases(mediaHC, mediaCO,  mediaCO2,mediaO2);
            medicion.setValRPM(mediaRPM);
            medicion.setValorTAceite((int) mediaTemp);            
            lstMedFur.add(medicion);      
    }

    private int generarRandon(int valor) 
    {
        int numero=0;
        try 
        {
            if (valor>100) 
            {
                numero = (int) (Math.random() * 50);
            }else if(valor>50)
            {
                numero = (int) (Math.random() * 30);
            }else{
                numero = (int) (Math.random() * 20);
            }    
        } catch (Exception e) {
            System.out.println("Error en el metodo : generarRandon()" + e.getMessage());
        }
        return numero;
    } 


    public  void generarArchivoGases(String placas, BancoGasolina banco,int nroExosto) {
        int medTemp = 0;
       
        List<List<MedicionGases>> newListaDeLista= new ArrayList<>();
        List<MedicionGases> newLista ;
        System.out.println("entro a genertar archivo en simulaacion motos callable");
        try {
           if (nroExosto == 1) {
                fw = new FileWriter("ultLectGases.soltelec");
                df = new DecimalFormat("#0.00#");
                bwP = new BufferedWriter(fw);
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                out = Utilidades.cifra("Motos;".concat(placas).concat(dtf.format(LocalDateTime.now())), 'M');
                //out = "Motos;".concat(placas);
                bwP.write(out);
                bwP.newLine();
               System.out.println("");
                if (ActivarLogs.equalsIgnoreCase("true")) {
                   // System.out.println("entro a generar archivo de lectura");
                    fw2 = new FileWriter("UltLectura_" + placas + ".soltelec");
                    bwP2 = new BufferedWriter(fw2);
                    bwP2.write("-.** Lectura de Valores de los Ultimos 5 Segundos .-** ");
                    bwP2.newLine();
                    bwP2.write("Prueba Aplicada a la Moto: " + placas);
                    bwP2.newLine();
                    bwP2.write("-.** Tabla de Valores Moto en RALENTI EXHOSTO 1 .-** ");
                    bwP2.newLine();
                    bwP2.write("HC\tCO\tCO2\tO2\tRPM\tTEMP");
                    bwP2.newLine();
                }
            } else {
                if (ActivarLogs.equalsIgnoreCase("true")) {
                    bwP2.write("-.** Tabla de Valores Moto en RALENTI EXHOSTO 2.-** ");
                    bwP2.newLine();
                }
            }
            
                   
            //Generar el archivo txt de la entrada, la entrada corregida y la salida
            //filtrada
           
            int cnt = 1;
            
                newLista = new ArrayList<>();
                
                if (lista50Datos.size() > 50) {                  
                    Integer lngLst =lista50Datos.size()-50;                    
                    for (int i = 0; i < lngLst; i++) {                       
                         lista50Datos.remove(0);                                            
                        if (lista50Datos.size() ==50) {
                            break;
                        }
                    }
                }
     
                if (lista50Datos.size() < 50) {                    
                    int tot = lista50Datos.size()-3;
                    int posIni = tot - 1;
                    int posFin = tot;
                    int apFill = 0;
                    while (true) {
                        apFill = ThreadLocalRandom.current().nextInt(posIni, posFin);
                        MedicionGases med = lista50Datos.get(apFill);                   
                        lista50Datos.add(med);                                              
                        if(lista50Datos.size()== 50){
                            break;
                        }
                    }  
                }                
                int tot = lista50Datos.size();                
                int eve=0;                          
                for (MedicionGases med : lista50Datos) {
                    int medHC = med.getValorHC();
                    double medCO = med.getValorCO() * 0.01;
                    String strMedCO = String.valueOf(df.format(medCO));
                    double medCO2 = 0;
                    if (banco instanceof BancoCapelec) {
                        medCO2 = (med.getValorCO2() * 0.1) / 10;
                    } else {
                        medCO2 = med.getValorCO2() * 0.1;
                    }
                    String strMedCO2 = String.valueOf(df.format(medCO2));
                    double medO2 = med.getValorO2() * 0.01;
                    String strMedO2 = String.valueOf(df.format(medO2));
                    int medRPM = med.getValorRPM();
                    medTemp = med.getValorTAceiteSinTabla();                    
                    newLista.add(med);
                   // System.out.println("valor que encripto: "+ medHC + ";" + strMedCO + ";" + strMedCO2 + ";" + strMedO2 + ";" + medRPM + ";" + medTemp + ";"+med.isLect5Seg());
                    out = Utilidades.cifra(medHC + ";" + strMedCO + ";" + strMedCO2 + ";" + strMedO2 + ";" + medRPM + ";" + medTemp + ";"+med.isLect5Seg(), 'M');                                      
                   // out = medHC + ";" + strMedCO + ";" + strMedCO2 + ";" + strMedO2 + ";" + medRPM + ";" + medTemp + ";"+med.isLect5Seg();                                      
                    if (medTemp == 0 || medTemp > 110) {
                        medTemp = med.getValorTAceiteSinTabla();
                    }                   
                    bwP.write(out);
                    bwP.newLine(); 
                    bwP.flush();
                    eve++;
                }
                out = Utilidades.cifra("*****", 'M');
                //out = "*****";
                bwP.write(out);
                bwP.newLine();
                bwP.flush();
                cnt++;
            
                             
                if (lista10Datos.size() > 10) {                  
                    Integer lngLst =lista10Datos.size()-10;
                    for (int i = 0; i < lngLst; i++) {
                        lista10Datos.remove(0);                        
                        if (lista10Datos.size() ==10) {
                            break;
                        }
                    }
                }
     
                if (lista10Datos.size() < 10) {                    
                    tot = lista10Datos.size()-3;
                    int posIni = tot - 1;
                    int posFin = tot;
                    int apFill = 0;
                    while (true) {
                        apFill = ThreadLocalRandom.current().nextInt(posIni, posFin);
                        MedicionGases med = lista10Datos.get(apFill);                   
                        lista10Datos.add(med);                                              
                        if(lista10Datos.size()== 10){
                            break;
                        }
                    }  
                }                
                tot = lista10Datos.size();                
                eve=0;
                newListaDeLista.add(newLista);
                  int  PromRPM =0, PromTEMM=0 ;
                double PromCO = 0,PromCO2 = 0, PromO2 = 0, PromHc = 0;
                for (MedicionGases med : lista10Datos) {
                    
                    int medHC = med.getValorHC();
                    double medCO = med.getValorCO() * 0.01;
                    String strMedCO = String.valueOf(df.format(medCO));
                    double medCO2 = 0;
                    if (banco instanceof BancoCapelec) {
                        medCO2 = (med.getValorCO2() * 0.1) / 10;
                    } else {
                        medCO2 = med.getValorCO2() * 0.1;
                    }
                    String strMedCO2 = String.valueOf(df.format(medCO2));
                    double medO2 = med.getValorO2() * 0.01;
                    String strMedO2 = String.valueOf(df.format(medO2));
                    int medRPM = med.getValorRPM();
                    medTemp = med.getValorTAceiteSinTabla();                    
                    newLista.add(med);
                    PromHc = PromHc + medHC;
                    PromCO = PromCO + medCO;
                    PromCO2=PromCO2 + medCO2;
                    PromO2 = PromO2 + medO2;
                    PromRPM = PromRPM + medRPM;
                    PromTEMM = PromTEMM + medTemp;
                    out = Utilidades.cifra(medHC + ";" + strMedCO + ";" + strMedCO2 + ";" + strMedO2 + ";" + medRPM + ";" + medTemp + ";"+med.isLect5Seg(), 'M');                    
                    //out = medHC + ";" + strMedCO + ";" + strMedCO2 + ";" + strMedO2 + ";" + medRPM + ";" + medTemp + ";"+med.isLect5Seg();                    
                   out2 = medHC + ";" + strMedCO + ";" + strMedCO2 + ";" + strMedO2 + ";" + medRPM + ";" + medTemp;                    
                     if (medTemp == 0 || medTemp > 110) {
                    medTemp = med.getValorTAceiteSinTabla();
                }
                bwP.write(out);
                bwP.newLine();
                bwP.flush();
                if (ActivarLogs.equalsIgnoreCase("true")) {
                    bwP2.write(out2);
                    bwP2.newLine();
                    bwP2.flush();
                }

                eve++;
                }     
              // System.out.println("antes de promediar, HC: " + PromHc + "  CO:" + PromCO + "  CO2:" + PromCO2 + "  O2:" + PromO2 + "  RPM:" + PromRPM + "  TEMP:" + PromTEMM);

            out2 = "Prom:" + (PromHc / 10) + "\t" + (PromCO / 10) + "\t" + (PromCO2 / 10) + "\t" + (PromO2 / 10) + "\t" + (PromRPM / 10) + "\t" + (PromTEMM / 10);
            //System.out.println("despues de  promediar, HC: " + (PromHc / 10) + "  CO:" + (PromCO / 10) + "  CO2:" + (PromCO2 / 10) + "  O2:" + (PromO2 / 10) + "  RPM:" + (PromRPM / 10) + "  TEMP:" + (PromTEMM / 10));

            //bwP.newLine();

            out = Utilidades.cifra("####", 'M');
            //out = "####";
            bwP.write(out);
            bwP.newLine();
            bwP.flush();

            if (ActivarLogs.equalsIgnoreCase("true")) {
                bwP2.write(out2);
                bwP2.newLine();
                out2 = "########################################";
                bwP2.write(out2);
                bwP2.newLine();
                bwP2.flush();
            }
            GenerarArchivo.temperatura = medTemp;
            //ExportarDatos(placas);            
            
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }        
    }
    private void liberarRecursos() {

        if (timer != null) {
            timer.stop();
        }
        if (timerSimulacion != null) {
            timerSimulacion.stop();
        }
        contadorTemporizacion = 0;
        contadorSimulacion = 0;
        //if(banco!= null)
        //banco.getPuertoSerial().close();       
    }

    private boolean validadorCO2(int valPerm, double valCo2, MedicionGases medicion, int nroInt) {
        boolean valDilc = true;
        if (valCo2 > valPerm && nroInt == 1) {
            Mensajes.mensajeAdvertencia(" SE HA DETECTADO QUE LA SONDA NO ESTA BIEN PUESTA DENTRO DEL EXHOSTO  \n POR FAVOR DIRIJASE HASTA EL  Y REVISE LA CAUSAL  \n CUANDO ESTE LISTO PRESIONE ACEPTAR ");
            valDilc = false;
            timer.stop();
            contadorTemporizacion = 0;
        }
        try {

            if (nroInt == 2) {
                Thread.sleep(1300);
                medicion = banco.obtenerDatos();
                valCo2 = (medicion.getValorO2() * 0.01);
                System.out.println("valor del c02 a validar 2 " + medicion.getValorCO2() * 0.1);
                if (valCo2 > valPerm) {
                    Mensajes.mensajeAdvertencia("DISCULPE, NO PUEDO CONTINUAR CON LA PRUEBA DEBIDO A QUE LA SONDA NO ESTA BIEN PUESTA DENTRO DEL EXHOTO. \n POR FAVOR REVISE LOS ACOPLES DEL EXHOSTO ..! \n CUANDO TERMINE PRESIONE ACEPTAR ");
                    valDilc = false;
                }
            }

            if (nroInt == 3) {
                Thread.sleep(10000);
                medicion = banco.obtenerDatos();
                valCo2 = (medicion.getValorO2() * 0.01);
                System.out.println("valor del c02 a validar 3" + medicion.getValorCO2() * 0.1);
                if (valCo2 > valPerm) {
                    int opcion = JOptionPane.showOptionDialog(null, "DISCULPE, LAMENTABLEMENTE LA PRUEBA SERA ABORTADA DEBIDO A QUE NO SE PUEDE TOMAR LA MUESTRA CORRECTAMENTE  EN EL  EXHOSTO \n " + "   POR FAVOR REVISE EL EQUIPO DE MEDICION",
                            null, JOptionPane.YES_NO_CANCEL_OPTION,
                            JOptionPane.INFORMATION_MESSAGE, null, new Object[]{"Abortar Prueba"}, "NO");
                    timer.stop();
                    liberarRecursos();
                    Thread.sleep(100);
                    valDilc = false;
                }
            }
        } catch (InterruptedException ex) {
        }
        return valDilc;
    }

    private void logCancelTest() {
        try {
            panel.getProgressBar().setVisible(false);
            panel.getPanelFiguras().setVisible(false);
            panel.getButtonFinalizar().setVisible(false);
            panel.getPanelMensaje().setVisible(true);
            panel.getMensaje().setText("Esperando AUTORIZACION del Ingeniero Pista");
            Font eve = new Font(Font.SANS_SERIF, Font.BOLD, 40);
            panel.getPanelMensaje().setFont(eve);
            panel.getPanelMensaje().setText("Prueba Actual de Gases ha sido ABORTADA ");
            Thread.sleep(500);
            ImageIcon image = new ImageIcon(getClass().getResource("/imagenes/cancelar.png"));
            JLabel lblImg = (JLabel) panel.getPanelMensaje().getComponent(0);
            lblImg.setIcon(image);
            Thread.sleep(500);

            //Status status = UtilLogin.loginAdminDBCDA();
            JXLoginPane pane = new JXLoginPane(new LoginServiceCDA(true));

            pane.setLocale(new Locale("ES"));
            pane.setBannerText("ADMINISTRADOR");
            pane.setErrorMessage("Usuario No Autorizado para Abortar Prueba de Gases");

            //loginDialog.setVisible(true);
            JXLoginPane.Status status = JXLoginPane.showLoginDialog(null, pane);
            if (status == JXLoginPane.Status.SUCCEEDED) {

                String nombreUsuario = pane.getUserName();//El nombre de usuario que se acaba de autenticar
                //traer el id de ese usuario

                boolean pressMotivo = false;
                Object objSeleccion = null;
                while (pressMotivo == false) {
                    objSeleccion = JOptionPane.showInputDialog(
                            null,
                            "Motivo del Aborto Prueba Gases",
                            "Abortar Prueba desde callable:",
                            JOptionPane.QUESTION_MESSAGE,
                            null, // null para icono defecto
                            new Object[]{
                                "Fallas del equipo de medicion",
                                "Falla subita de fluido electrico del equipo de medicion",
                                "Bloqueo forzado del equipo de medicion",
                                "Ejecucion Incorrecta de la Prueba"},
                            "Causa de la cancelacion");
                    if (objSeleccion == null) {
                        Mensajes.messageWarningTime(">>>  Disculpe, NO me has especificado el Motivo de Aborto, Por favor seleccione  uno<<<", 5);
                    } else {
                        pressMotivo = true;
                    }
                }
                String strSeleccion = (String) objSeleccion;
                if (Mensajes.mensajePregunta("¿Desea Agregar otra Observacion Referente al Aborto?")) {
                    org.soltelec.pruebasgases.FrmComentario frm = new org.soltelec.pruebasgases.FrmComentario(SwingUtilities.getWindowAncestor(panel), idPrueba, JDialog.DEFAULT_MODALITY_TYPE, strSeleccion, idUsuario, "aborto",0);
                    frm.setLocationRelativeTo(this.panel);
                    frm.setVisible(true);
                    frm.setModal(true);
                    JOptionPane.showMessageDialog(null, "Aborto de la  prueba por: " + strSeleccion);
                }
                panel.cerrar();
            } else {
                System.out.println("El usuario no se a autenticado como administrador");
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(ListenerCancelacionGases.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}