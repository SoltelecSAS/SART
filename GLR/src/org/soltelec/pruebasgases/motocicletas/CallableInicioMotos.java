/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.pruebasgases.motocicletas;

import com.soltelec.loginadministrador.ConsultasLogin;
import gnu.io.CommPortIdentifier;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import static jdk.nashorn.internal.objects.NativeRegExp.exec;
import static jdk.nashorn.internal.runtime.ScriptingFunctions.exec;
import org.apache.log4j.Logger;
import org.soltelec.medicionrpm.MedidorRevTemp;
import org.soltelec.procesosbanco.BancoGasolina;
import org.soltelec.procesosbanco.EstabilizacionMedidas;
import org.soltelec.procesosbanco.PanelCero;
import org.soltelec.procesosbanco.WorkerCalentamiento;
import org.soltelec.pruebasgases.CondicionesAnormalesException;
import org.soltelec.pruebasgases.EvaluarRegistrarPruebaGasolina;
import org.soltelec.pruebasgases.JDialogPruebaGasolina;
import org.soltelec.pruebasgases.ListenerCancelacionGases;
import org.soltelec.pruebasgases.ListenerCancelacionPorRpm;
import org.soltelec.pruebasgases.PanelPruebaGases;
import org.soltelec.pruebasgases.PanelVerificacionMoto;
import org.soltelec.pruebasgases.WorkerCruceroRalenti;
import org.soltelec.pruebasgases.diesel.PanelVerificacionDiesel;
import org.soltelec.util.InfoVehiculo;
import org.soltelec.util.MedicionGases;
import org.soltelec.util.Mensajes;
import org.soltelec.util.RegistrarMedidas;
import org.soltelec.util.UtilPropiedades;
import termohigrometro.MedicionTermoHigrometro;
import termohigrometro.MedicionTermoHigrometro;
import termohigrometro.TermoHigrometro;
import termohigrometro.TermoHigrometroArtisan;
import termohigrometro.TermoHigrometroPCSensors;

/**
 * Inicio de la prueba de Motos antes de las mediciones de rpm y temperatura
 *
 * @author Gerencia Desarrollo de Soluciones U 1 tecnologicas
 */
public class CallableInicioMotos implements Callable<Void> {

    private final BancoGasolina banco;
    private PanelPruebaGases panel;
    private final long idHojaPrueba, idPrueba, idUsuario;
    private int contadorTemporizacion;
    private final Timer timer;
    private PanelVerificacionMoto p;
    private final DialogoMotos dlgMotos;
    private int limiteHC = 20;
    private String placas;
    String Estabilizacion = "";
    JDialogMotosGases frmC;
    private EstabilizacionMedidas estabilizacion;
    private boolean Prueba;
    public static String lecturaCondicionesAnormales = "";
    private final double tempAmbiente;
    private final double humedadAmbiente;

    public CallableInicioMotos(BancoGasolina banco, PanelPruebaGases panel, long idHojaPrueba, long idPrueba, long idUsuario, String placas, double tempAmbiente, double humedadAmbiente) {
        this.banco = banco;
        this.panel = panel;
        this.idHojaPrueba = idHojaPrueba;
        this.idPrueba = idPrueba;
        this.idUsuario = idUsuario;
        this.placas = placas;
        this.Prueba = true;
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contadorTemporizacion++;
            }
        });
        dlgMotos = new DialogoMotos();
        this.tempAmbiente = tempAmbiente;
        this.humedadAmbiente = humedadAmbiente;
    }//end of constructor


    @Override
    public Void call() throws Exception {
        try {
            MedicionGases medicion = null;
            panel.getPanelFiguras().setVisible(false);
            panel.getPanelMensaje().setVisible(true);

            System.out.println("Busca leer la Intacia del banco de Gases");

            medicion = banco.obtenerDatos();
         
            //Calentamiento
            System.out.println("salto lectura de banco gases");
            if (medicion.isCalentamiento()) {
                panel.getProgressBar().setVisible(true);
                System.out.println("Por favor; Banco esta en calentamiento");
                //Usando join no lo puedo creer
                panel.getPanelMensaje().setText("Por favor espere. \n Banco en calentamiento..!");
                panel.getPanelMensaje().getCronometro().setText("");
                WorkerCalentamiento workerCalentamiento = new WorkerCalentamiento(this.banco, this.panel.getProgressBar(), this.panel.getPanelMensaje().getCronometro());
                Thread t = new Thread(workerCalentamiento);
                t.start();
                try {
                    //Uniendo dos hilos
                    t.join(); //espera hasta que el otro hilo termine si es que no sabes
                } catch (InterruptedException ex) {
                    System.out.println("Error join con workerCalentamiento");
                }
                panel.getProgressBar().setVisible(false);
                //CREO FUNCION PARA MEDIR HC,CO Y CO2 DURANTE 5 MINUTOS
                
                
                

                Estabilizacion = UtilPropiedades.cargarPropiedad("Estabilizacion", "propiedades.properties");//LEE EL VALOR DE LA VARIABLE TemporizadorOpacimetro
                Estabilizacion = (Estabilizacion == null) ? "false" : Estabilizacion;
                System.out.println((Estabilizacion == "" || Estabilizacion.equalsIgnoreCase("false")) ? "variable estabilizacion configurada en false" : "variable estabilizacion configurada en true");
                if (Estabilizacion.equalsIgnoreCase("true")) {
                    System.out.println("voy a entrar a hilo de mestabilizacion");
                    estabilizacion = new EstabilizacionMedidas(this.panel, this.banco, 4);
                    //CREO HILO
                    Thread t2 = new Thread(estabilizacion);
                    //INICIA HILO
                    t2.start();
                    t2.join();

                    Thread.sleep(3500);
                    Prueba = estabilizacion.Resultado;
                } else {
                    Prueba = true;
                }
                // panel.getProgressBar().setVisible(false);
            } //end of if calentamiento
            System.out.println("valor de prueba: " + Prueba);
            if (Prueba) {
                panel.getProgressBar().setVisible(false);
                panel.getPanelMensaje().setText("Verifique : \n Transmision en neutro.\n Y que la Moto Se Encuentre Sobre el Soporte Central");
                Thread.sleep(3500);
                Mensajes.mensajeCorrecto("¿SE ENCUENTRA LA MOTO SOBRE EL SOPORTE CENTRAL ? (Cuando Aplique el Caso)");

                panel.getPanelMensaje().setText("Verifique :\n Luces encendidas y Comprobar que cualquier otro equipo Electrico este Apagado Como el Comprobar Ahogador / Control manual de choque apagado para esta Moto " + "");
                Thread.sleep(3950);
                int opcion = JOptionPane.showOptionDialog(null, " ¿SE ENCUENTRA LAS  LUCES ENCENDIDAS  APAGADOS EQUIPOS ELECTRONICOS COMO EL COMPROBADOR AHOGADOR ?  ",
                        null, JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Si "}, "NO");

                /* if (opcion == 1) {
                RegistrarMedidas regMedidas = new RegistrarMedidas();
                regMedidas.setMensaDefe("Las Luces no se Pudieron Encender");
                CallableInicioMotos.lecturaCondicionesAnormales = regMedidas.getMensaDefe();
                Set<Integer> conjuntoDefectos = null;
                regMedidas.registrarDefectosVisuales(conjuntoDefectos, idPrueba, idUsuario);
                String strTermoHigrometro = UtilPropiedades.cargarPropiedad("TermoHigrometro", "propiedades.properties");
                if (strTermoHigrometro != null && strTermoHigrometro.equalsIgnoreCase("Habilitado")) {
                    String strMarcaTermohigrometro = UtilPropiedades.cargarPropiedad("MarcaTermoHigrometro", "propiedades.properties");
                    double tempAmbiente = 0;
                    double humedadAmbiente = 0;
                    if (strMarcaTermohigrometro.equalsIgnoreCase("Artisan")) {
                        Integer i = 0;
                        TermoHigrometroArtisan termoHigrometroArtisan = new TermoHigrometroArtisan(i);
                        humedadAmbiente = Double.parseDouble(termoHigrometroArtisan.obtenerHumedad());
                        tempAmbiente = Double.parseDouble(termoHigrometroArtisan.obtenerTemperatura());
                    } else {
                        MedicionTermoHigrometro medicionTermoHigrometro = null;
                        TermoHigrometro termoHigrometro = new TermoHigrometroPCSensors();
                        medicionTermoHigrometro = termoHigrometro.obtenerValores();
                    }
                    //Muestra de Validacion de Temperatura y Humedad
                    EvaluarRegistrarPruebaGasolina evalReg = new EvaluarRegistrarPruebaGasolina(0, 0, idPrueba);
                    evalReg.registrarTemperaturaHumedad(tempAmbiente, humedadAmbiente);
                }// validacion del Termohigrometro
               //  panel.cerrar();
                 liberarRecursos();
               //  return null;
            }*/
                // Mensajes.mensajeCorrecto("¿SE ENCUENTRA LAS  LUCES ENCENDIDAS  APAGADOS EQUIPOS ELECTRONICOS COMO EL COMPROBADOR AHOGADOR ?");
                /*panel.getPanelMensaje().setText("Verifique :\n " + "Comprobar Ahogador / Control manual de choque apagado para esta Moto  ");
             panel.getPanelMensaje().setText("");
             Thread.sleep(2050);
             if (Mensajes.mensajePregunta("¿  APAGADOS COMPROBADOR DE AHOGADOR Y CONTROL MANUAL ?")) {
                   
             } else {
             JOptionPane.showMessageDialog(null, "JOptionPane.showMessageDialog(null, \" Disculpe; no se puede efectuar el procedimiento para toma y analisis de la muestra de gases \\n Razon: Incumplimiento de las condiciones establecidas en el numeral 4.1.3.3 de la NTC5365");
             panel.cerrar();
             liberarRecursos();
             return null;
             }*/
                String ActivarTapaLlenado = UtilPropiedades.cargarPropiedad("ActivarTapaLlenado", "propiedades.properties");
                boolean TapaLlenadoActivador = Boolean.parseBoolean(ActivarTapaLlenado);

                String ActivarSalidasAdicional = UtilPropiedades.cargarPropiedad("ActivarSalidasAdicional", "propiedades.properties");
                boolean alidasAdicionalActivador = Boolean.parseBoolean(ActivarSalidasAdicional);

                CallableInicioMotos.lecturaCondicionesAnormales = "";
                //-------------------------------PANEL DE INSPECCION VISUAL INCIO-------------------------------------------------------------
                panel.getPanelMensaje().setText(" ");
                configurarPanelVerificacion(TapaLlenadoActivador, alidasAdicionalActivador);//oculta items no aplicables a Motocicletas
                JOptionPane.showMessageDialog(null, p, "SART 1.7.3.- Inspeccion Sensorial ", JOptionPane.PLAIN_MESSAGE);
                if (p.isDefectoEncontrado()) {
                    System.out.println("Defecto " + p.getMensaje().toString());
                    JOptionPane.showMessageDialog(panel, p.getMensaje().toString());
                    try {
                        RegistrarMedidas regMedidas = new RegistrarMedidas();
                        regMedidas.setMensaDefe(p.getDetallesRechazos());
                        CallableInicioMotos.lecturaCondicionesAnormales = regMedidas.getMensaDefe();
                        System.out.println("Imprimo :" + CallableInicioMotos.lecturaCondicionesAnormales);
                        regMedidas.registrarDefectosVisuales(p.getConjuntoDefectos(), idPrueba, idUsuario);
                        regMedidas.registrarTemperaturInicio(tempAmbiente, idPrueba);
                        regMedidas.registrarHumedadInicio(humedadAmbiente, idPrueba);
                        
                    } finally {

                        //  return null;
                    }
                    //liberarRecursos();
                    // this.rechazoPrueba("Condiciones Anormales"); 
                }
                // mostrarDialogoMotos();
                Thread.sleep(1000);
//                panel.getBtnWorkerCicloMotos().addActionListener(new ActionListener() {
//                    @Override
//                    public void actionPerformed(ActionEvent e) {
//                        try {
//                            WorkerCiclosMoto wcm = new WorkerCiclosMoto(panel, banco, idHojaPrueba, idPrueba, idUsuario, dlgMotos.getNumeroTiempos(), dlgMotos.getNumeroEscapes(), placas, termoHigrometroArtisan);
//                            wcm.execute();
//                            while (!wcm.isDone()) {
//                                Thread.sleep(1500);
//                            }
//                            /*añadido 07/09/2022*/
//                            /*añadido 7/09/2022*/
//                            /*System.out.println("ingrese a boton rpm---------------------------");
//                            RegistrarMedidas regMedidas = new RegistrarMedidas();
//                            regMedidas.registraRechazo(" ", "rechazo por revoluciones fuera de rango. ", idUsuario, idPrueba, 84018);
//                            System.out.println("Ya registre medidas de rechazo por RPM: " + "idPrueba = " + idPrueba);
//                            /*añadido 07/09/2022*/
//                            panel.cerrar();
//                            System.out.println("Cierre de Panel ");
//                        } catch (Exception exc) {
//                            JOptionPane.showMessageDialog(null, "Error durante ciclos moto: \n" + exc.getMessage());
//                        }
//                    }
//                });
                //panel.getBtnWorkerCicloMotos().doClick();
                panel.getBtnWorkerCicloMotos().setVisible(true);

                panel.getButtonRpm().setVisible(true);
                panel.getBtnWorkerCicloMotos().setVisible(true);
                panel.getButtonRpm().addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            WorkerCiclosMoto wcm = new WorkerCiclosMoto(panel, banco, idHojaPrueba, idPrueba, idUsuario, dlgMotos.getNumeroTiempos(), dlgMotos.getNumeroEscapes(), placas, tempAmbiente, humedadAmbiente);
                            wcm.execute();
                            while (!wcm.isDone()) {
                                Thread.sleep(1500);
                            }
                            System.out.println("ingrese a boton rpm. Este boton se llama automaticamente---------------------------");
                            /*RegistrarMedidas regMedidas = new RegistrarMedidas();
                            regMedidas.registraRechazo(" ", "rechazo por revoluciones fuera de rango. ", idUsuario, idPrueba, 84018);
                            System.out.println("Ya registre medidas de rechazo por RPM: " + "idPrueba = " + idPrueba);
                            panel.cerrar();
                            System.out.println("Cierre de Panel ");*/
                            panel.cerrar();
                        } catch (Exception exc) {
                            JOptionPane.showMessageDialog(null, "Error durante ciclos moto: \n" + exc.getMessage());
                        }
                    }
                });
                // actualizarInfoVehiculo();
                panel.getButtonRpm().doClick();

                //-------------------------------PANEL DE INSPECCION VISUAL FIN-------------------------------------------------------------
                //-------------------------------PANEL DE PARAMETROS DE MOTO INCIO-------------------------------------------------------------
                //del dialogo se traen los parametros 
                //-------------------------------PANEL DE PARAMETROS DE MOTO FIN-------------------------------------------------------------
                //-------------------------------DISPOSITIVO MEDICION RPM y T ACEITE INICIO---------------------------------------------
                //Registrar el worker para la segunda parte de la prueba de gases para motos
                //JOptionPane.showMessageDialog(panel, "Iniciar la Prueba, verifique acople o extension\n tubo de escape");
                //en este punto los niveles de hc deben estar por debajo de 20 ppm
//            panel.getPanelMensaje().setText("Verifique : \n Transmision en neutro.\n" + "Luces encendidas y Accesorios desconectados\n" + "Ahogador / Control manual de choque apagado");
//            panel.getPanelMensaje().setText("Ponga la sonda de muestreo en contacto\n con el Ambiente");
//            //Se supone que para limpiar el banco
//            Thread.sleep(1500);
//            //
//
//            if (dlgMotos.getNumeroTiempos() == 4) {
//                limiteHC = 20;//60 ppm
//            } else {
//                limiteHC = 500;
//            }
//            //espera un tiempo determinado a que los niveles  bajen... si no bajan sale automaticamente
//
//            Thread.sleep(1500);
//            medicion = banco.obtenerDatos();
//            contadorTemporizacion = 0;
//            timer.start();//desde aqui se inicializa el timer
//            panel.getProgressBar().setMaximum(BancoGasolina.TIEMPO_HC_DESCIENDA);
//            panel.getProgressBar().setValue(BancoGasolina.TIEMPO_HC_DESCIENDA);
//            //panel.getProgressBar().setVisible(true);
//            banco.encenderBombaMuestras(true);
//            panel.getPanelMensaje().setText("Esperando a disminucion de HC");
//            Thread.sleep(2000);
//            medicion = banco.obtenerDatos();
//            contadorTemporizacion = 0;
//            while (medicion.getValorHC() > limiteHC && contadorTemporizacion < BancoGasolina.TIEMPO_HC_DESCIENDA) {
//                Thread.sleep(100);
//                medicion = banco.obtenerDatos();
//                panel.getPanelMensaje().setText(" HC: " + medicion.getValorHC());
//                panel.getProgressBar().setValue(BancoGasolina.TIEMPO_HC_DESCIENDA - contadorTemporizacion);
//            } //end while
//
//            banco.encenderBombaMuestras(false);
//            panel.getProgressBar().setValue(0);
//            timer.stop();
//            if (contadorTemporizacion >= BancoGasolina.TIEMPO_HC_DESCIENDA) {
//                //Si sale del while por tiempo la prueba se cancela
//                panel.getPanelMensaje().setText("HC no desciende");
//                JOptionPane.showMessageDialog(panel, "Prueba abortada por no descenso de HC");
//                try {
//                    RegistrarMedidas regMedidas = new RegistrarMedidas();
//                    regMedidas.registrarCancelacion("Prueba abortada por no descenso de HC", idUsuario, idPrueba);
//                } catch (ClassNotFoundException ex) {
//                    JOptionPane.showMessageDialog(null, "No se encuentra el driver de MySQL");
//                } catch (SQLException ex) {
//                    JOptionPane.showMessageDialog(null, "Error registrando datos con la base de datos");
//                    ex.printStackTrace(System.err);
//                    Logger.getRootLogger().error("Error registrando aborto de prueba", ex);
//                } finally {
//                    liberarRecursos();
//                }
//                return null;
//            }//end if
//            //-------------------------------COMPROBAR HC MENOR A 20 PPM FIN---------------------------------------------
//
//            //Registrar el worker para la segunda parte de la prueba de gases para motos
//            panel.getPanelMensaje().setText("Inicio de la Prueba");
//            Thread.sleep(1200);
//            //JOptionPane.showMessageDialog(panel, "Iniciar la Prueba, verifique acople o extension\n tubo de escape");
//            //en este punto los niveles de hc deben estar por debajo de 20 ppm
//            panel.getPanelMensaje().setText("Verifique : \n Transmision en neutro.\n" + "Luces encendidas y Accesorios desconectados\n" + "Ahogador / Control manual de choque apagado");
//            Thread.sleep(2500);
//            panel.getProgressBar().setVisible(false);
//            panel.getProgressBar().setMaximum(30);
//            panel.getButtonFinalizar().setVisible(true);
//            panel.getButtonRpm().setEnabled(true);
//            panel.getButtonRpm().setVisible(true);
//
//            panel.getButtonRpm().addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    try {
//
//                        WorkerCiclosMoto wcm = new WorkerCiclosMoto(panel, banco, idHojaPrueba, idPrueba, idUsuario, dlgMotos.getNumeroTiempos(), dlgMotos.getNumeroEscapes());
//                        wcm.execute();
//
//                    } catch (Exception exc) {
//
//                        JOptionPane.showMessageDialog(null, "Error durante ciclos moto: \n" + exc.getMessage());
//
//                    }
//
//                }
//            });
//            panel.getButtonRpm().doClick();
            } else {
                //  banco.getPuertoSerial().close();
                System.out.println("prueba no permitida debido a que no se pudo estabilizar las medidas, calibre los equipos o revise la variable Estabilizacion");
                Prueba = true;
                System.out.println("valor de prueba2: " + Prueba);
                panel.cerrar();
                banco.getPuertoSerial().close();
                throw new CancellationException("Prueba Cancelada, no se ha podido estabilizar las medidas, por favor realice calibracion del equipo o consulte con soporte tecnico");

            }
        } catch (InterruptedException ie) {
            liberarRecursos();
            System.out.println("Prueba de gases cancelada externamente");
            throw new CancellationException("Prueba cancelada");

        } catch (Exception exc) {
            liberarRecursos();
            banco.getPuertoSerial().close();
            System.out.println("Error durante el inicio de la prueba de gases");
            exc.printStackTrace(System.err);
            throw exc;
        }
        return null;
    }//end of method call

    private void configurarPanelVerificacion(boolean ActivarTapaLlenado, boolean ActivarSalidasAdicional) {
        p = new PanelVerificacionMoto(ActivarTapaLlenado, ActivarSalidasAdicional);
        p.setSize(583, 125);
    }

    private void liberarRecursos() {
        if (timer != null) {
            timer.stop();
        }
        banco.getPuertoSerial().close();
    }

    private void mostrarDialogoMotos() {
        JDialog d = new JDialog((JDialog) SwingUtilities.getWindowAncestor(panel), true);
        d.setTitle("Parametros Moto");
        dlgMotos.fillData("");
        d.add(dlgMotos);
        d.setSize(240, 370);
        d.getRootPane().setDefaultButton(dlgMotos.getButtonContinuar());
        d.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        d.setLocationRelativeTo(panel);
        d.setVisible(true);
    }//end of method mostrarDialogoMotos

    private void rechazoPrueba(String causa) {
        System.out.println("voy a mostrar  logica de Rechazo");
        panel.cerrar();
        try {
            frmC = new JDialogMotosGases(null, false, 0, 0, 0, null, "", null);

        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(WorkerCiclosMoto.class.getName()).log(Level.SEVERE, null, ex);
        }
        frmC.setTitle("SART 1.7.3 RECHAZO DE PRUEBA");
        panel = new PanelPruebaGases();
        panel.setDialogMotosGases(frmC);
        frmC.getContentPane().add(panel);
        ImageIcon image = new ImageIcon(getClass().getResource("/imagenes/cancelar.png"));
        Font eve = new Font(Font.SANS_SERIF, Font.BOLD, 10);
        panel.getPanelMensaje().setFont(eve);
        panel.getPanelMensaje().setText(causa.toUpperCase());
        panel.getProgressBar().setVisible(false);
        JLabel lblImg = (JLabel) panel.getPanelMensaje().getComponent(0);
        lblImg.setIcon(image);
        frmC.setIconImage(new ImageIcon(getClass().getResource("/imagenes/cancelar.png")).getImage());
        panel.getButtonFinalizar().setVisible(false);
        frmC.setVisible(true);
        panel.setVisible(true);

        if (Mensajes.mensajePregunta("¿Desea Agregar otra Observacion Referente a la causal de Rechazo?")) {
            frmC.setModal(true);
            panel.getMensaje().setText("Registro de Ampliacion de Observaciones Encontradas..! ");
            org.soltelec.pruebasgases.FrmComentario frm = new org.soltelec.pruebasgases.FrmComentario(SwingUtilities.getWindowAncestor(panel), idPrueba, JDialog.DEFAULT_MODALITY_TYPE, causa.toUpperCase(), idUsuario, "rechazo", 84018);
            frm.setLocationRelativeTo(panel);
            System.out.println("RECHAZO PRUEBA");
            frm.setVisible(true);
            frm.setModal(true);
        } else {
            try {
                RegistrarMedidas regMedidas = new RegistrarMedidas();
                Connection cn = regMedidas.getConnection();
                ConsultasLogin consultasLogin = new ConsultasLogin();
                regMedidas.registraRechazo(causa, causa, this.idUsuario, idPrueba, 84018);//Registra la prueba como Rechazada
            } catch (SQLException | ClassNotFoundException exc) {
                Mensajes.mostrarExcepcion(exc);
            } finally {
            }
        }
        System.out.println("Ya sali de la vista");
        panel.cerrar();
        frmC.setVisible(false);
        System.out.println("fin fin fin ..!");
        frmC.setVisible(false);
        panel.setVisible(false);
        panel.cerrar();
    }

    private void actualizarInfoVehiculo() throws Exception {//guarda el numero de tiempo de 

        InfoVehiculo infoVehiculo = new InfoVehiculo(dlgMotos.getNumeroTiempos(), 1, 1, 1, "");
        infoVehiculo.setNumeroExostos(dlgMotos.getNumeroEscapes());
        infoVehiculo.setNumeroTiempos(dlgMotos.getNumeroTiempos());
        if (dlgMotos.getScooter() == 1) {
            infoVehiculo.setDiseño("Scooter");
        } else {
            infoVehiculo.setDiseño("Convencional");
        }
        //falta el numero de cilindros
        RegistrarMedidas reg = new RegistrarMedidas();
        try (Connection cn = reg.getConnection()) {
            reg.actualizarInfoVehiculo(infoVehiculo, "", cn);
        }

    }

}//end of class CallableInicioMotos
