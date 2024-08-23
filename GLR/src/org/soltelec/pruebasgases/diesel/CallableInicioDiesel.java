/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.pruebasgases.diesel;

import com.soltelec.loginadministrador.ConsultasLogin;
import com.soltelec.opacimetro.brianbee.OpacimetroBrianBee;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import org.apache.log4j.Logger;
import org.soltelec.luxometro.LecturaArchivoLuxometroMoon_antiguo;
import org.soltelec.procesosbanco.RegVefCalibraciones;
import org.soltelec.procesosopacimetro.Opacimetro;
import org.soltelec.pruebasgases.EvaluarRegistrarPruebaGasolina;
import org.soltelec.pruebasgases.JDialogPruebaGasolina;
import org.soltelec.pruebasgases.PanelPruebaGases;
import org.soltelec.pruebasgases.WorkerCruceroRalenti;
import static org.soltelec.pruebasgases.diesel.WorkerCiclosDiesel.actualizarDiametroExostoVehiculo;
import static org.soltelec.pruebasgases.diesel.WorkerCiclosDiesel.diametroExosto;
import org.soltelec.util.Conex;
import org.soltelec.util.ConsultarDatosVehiculo;
import org.soltelec.util.MedicionOpacidad;
import org.soltelec.util.Mensajes;
import org.soltelec.util.RegistrarMedidas;
import org.soltelec.util.UtilDefectosVisuales;
import org.soltelec.util.UtilPropiedades;
import org.soltelec.util.UtilidadAbortoPrueba;
import termohigrometro.MedicionTermoHigrometro;
import termohigrometro.TermoHigrometro;
import termohigrometro.TermoHigrometroArtisan;
import termohigrometro.TermoHigrometroPCSensors;

/**
 * Clase que implementa el inicio de la prueba de Diesel si el fujo es normal
 * entonces llama al WorkerCiclosDiesel que implementa los ciclos de opacidad.
 *
 * @author Gerencia Desarrollo de Soluciones Tecnologicas
 */
public class CallableInicioDiesel implements Callable<Void> {

    private static final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(CallableInicioDiesel.class.getName());
    private PanelPruebaGases panel;
    private Opacimetro opacimetro;
    private MedicionOpacidad med;
    private Timer timer;
    private int contadorTemporizacion;
    //private SerialPort puertoSerial; //la conexion del puerto serial del opacimetro se realiza antes
    private DecimalFormat df2;
    private String urljdbc;
    private long idHojaPrueba, idPrueba, idUsuario;
    JDialogDiesel frmC;
    private PanelPruebaGases panelCancelacion;
//    private Double ev;
    private boolean toggle;
    private Component icono;
    private Component msg;
    private Font fAlt;
    private double diametroExosto = 0;
    private Connection conexion = null;
    double tempAmbiente;
    double humedadAmbiente;

    public CallableInicioDiesel() {

        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contadorTemporizacion++;
            }
        });
        Locale locale = new Locale("US");//para que sea con punto separador de decimales
        df2 = (DecimalFormat) NumberFormat.getInstance(locale);
        if (df2 instanceof DecimalFormat) {
            ((DecimalFormat) df2).setDecimalSeparatorAlwaysShown(true);
        }
        df2.setMinimumFractionDigits(2);//muestre siempre dos digitos decimales
        df2.setMaximumFractionDigits(2);//muestre siempre dos digitos decimales
    }

    public CallableInicioDiesel(PanelPruebaGases panel, Opacimetro opacimetro, long idHojaPrueba, long idPrueba, long idUsuario, double tempAmbiente, double humedadAmbiente) {
        this();
        this.panel = panel;
        this.idHojaPrueba = idHojaPrueba;
        this.idPrueba = idPrueba;
        this.idUsuario = idUsuario;
        this.opacimetro = opacimetro;
        this.tempAmbiente = tempAmbiente;
        this.humedadAmbiente = humedadAmbiente;
    }

    private void preparandoOpacimetro() {
        System.out.println("------------------------------------------");
        System.out.println("--      CALENTANDO OPACIMETRO      -------");
        System.out.println("------------------------------------------");
        toggle = true;
        try {
            panel.getButtonFinalizar().setVisible(true);
            panel.getButtonRpm().setVisible(false);
            panel.getPanelFiguras().setVisible(false);
            panel.getPanelMensaje().setVisible(true);
            panel.getPanelMensaje().setText("3.1.2.2.1 ASEGURESE QUE LA SONDA ESTA FUERA DEL TUBO DE ESCAPE");
            Thread.sleep(2500);
            System.out.println("Call Metodo obt dato ln 109");
            med = opacimetro.obtenerDatos();

            if (opacimetro instanceof OpacimetroBrianBee) {
                Thread.sleep(5000);
                if (med.isStandBy()) {
                    ((OpacimetroBrianBee) opacimetro).standBy();//despierta al opacimetro
                }
            }

            //mientras este en calentamiento esperar... puede ser cancelado por otro proceso
            while (med.isTempTuboFueraTolerancia()) {
                Thread.sleep(150);
                if (toggle) {
                    panel.getPanelMensaje().setText("3.1.2.2.2 Opacimetro en Calentamiento... °T tubo: " + med.getTemperaturaTubo());
                } else {
                    panel.getPanelMensaje().setText("3.1.2.2.2 Opacimetro en Calentamiento. °T tubo: " + med.getTemperaturaTubo());
                }
                System.out.println("Call Metodo obt dato ln 126");
                med = opacimetro.obtenerDatos();
                toggle = !toggle;
            }//end while

        } catch (InterruptedException e) {
            Mensajes.messageErrorTime("Error al preparar opacimetro " + e + "", 5);
            System.out.println("Error :" + e);
            System.out.println("Error en el metodo :preparandoOpacimetro()" + e.getMessage());
        }
    }

    private void rutinaLimpiezaPurga(boolean toggle) {
        System.out.println("------------------------------------------");
        System.out.println("--   RUTINA DE LIMPIEZA Y PURGA    -------");
        System.out.println("------------------------------------------");

        try {
            panel.getPanelMensaje().setText("3.1.2.2.3 REALICE LAS RUTINAS DE LIMPIEZA Y PURGA ");
            Thread.sleep(2000);
            JOptionPane.showMessageDialog(null, "¿Rutinas de Limpieza Terminadas?");
            contadorTemporizacion = 0;
            panel.getPanelMensaje().setText("Asegurese que El Opacimetro no tiene Obstrucción por Partículas o Residuos");
            Thread.sleep(2000);
            opacimetro.calibrar();
            med = opacimetro.obtenerDatos();
//            ev = med.getOpacidadDouble();

            while (med.isZeroEnProgreso()) {//adicionado debido al comportamiento diferente del opacimetro capelec, no afecta presumiblemente
                Thread.sleep(500);
                if (toggle) {
                    panel.getPanelMensaje().setText("RUTINA DE AUTOLIMPIEZA Y PURGA EN PROGRESO ..!");
                } else {
                    panel.getPanelMensaje().setText("RUTINA DE AUTOLIMPIEZA Y PURGA EN PROGRESO....!");
                }
                toggle = !toggle;
                System.out.println("Rutina llamada en ceros en progreso");
                med = opacimetro.obtenerDatos();
//                ev = med.getOpacidadDouble();
            }

        } catch (InterruptedException e) {
            Mensajes.messageErrorTime("Error en limpieza y purga " + e + "", 5);
            System.out.println("Error :" + e);
            System.out.println("Error en el metodo :rutinaLimpiezaPurga()" + e.getMessage());
        }
    }

    private boolean ajustarValorMinimo() {
        System.out.println("------------------------------------------");
        System.out.println("----   AJUSTANDO VALOR MINIMO     --------");
        System.out.println("------------------------------------------");
        try {
            timer.start();
            panel.getPanelMensaje().setText("3.1.2.2.4  AJUSTE DEL VALOR MINIMO....!");
            Thread.sleep(4000);
            med = opacimetro.obtenerDatos();
            contadorTemporizacion = 0;
            while (contadorTemporizacion < Opacimetro.TIEMPO_ESCALA) {
                Thread.sleep(120);
                med = opacimetro.obtenerDatos();
//                ev = med.getOpacidadDouble();
                panel.getPanelMensaje().setText("Opacidad: " + df2.format(med.getOpacidadDouble()) + ", t: " + contadorTemporizacion + "SEGUNDO");

                if (contadorTemporizacion == Opacimetro.TIEMPO_ESCALA) {
                    if (med.getOpacidadDouble() >= 1) {
                        JOptionPane.showMessageDialog(panel, "Ajuste del Valor Minimo de la Escala Incorrecto \n"
                                + "Repita los Procedimientos de Purga y Limpieza");
                        return false;
                    }
                }
            }
            panel.getPanelMensaje().setText("AJUSTE DEL VALOR MINIMO CORRECTO");

        } catch (InterruptedException e) {
            Mensajes.messageErrorTime("Error al ajustar valor minimo " + e + "", 5);
            System.out.println("Error :" + e);
            System.out.println("Error en el metodo :ajustarValorMinimo()" + e.getMessage());
            return false;
        }
        contadorTemporizacion = 0;
        return true;
    }

    private boolean ajustarValorMaximo() {
        System.out.println("------------------------------------------");
        System.out.println("----   AJUSTANDO VALOR MAXIMO     --------");
        System.out.println("------------------------------------------");
        try {
            panel.getPanelMensaje().setText("3.1.2.2.4  AJUSTE DEL VALOR MAXIMO: \n INSERTE UN ELEMENTO QUE OBSTRUYA TOTALMENTE LA EMISION DE LUZ");
            Thread.sleep(2000);
            contadorTemporizacion = 0;
            med = opacimetro.obtenerDatos();
            while (contadorTemporizacion < Opacimetro.TIEMPO_ESCALA) {
                Thread.sleep(120);
                med = opacimetro.obtenerDatos();
                System.out.println("   ------------ inicial : " + med.getOpacidadDouble());
                System.out.println("   ------------ inicial2 : " + med.getOpacidad());

                if (med.getOpacidadDouble() >= 90.0 && med.getOpacidadDouble() < 99) {
                    double randon = Double.parseDouble(new DecimalFormat("#.000").format(((Math.random() * (0.0 - 0.9)) + 0.9)).replace(",", "."));
                    double valorRestante = 99 - med.getOpacidadDouble();
                    System.out.println(" valorRestante :" + valorRestante);
                    short valorFinal = (short) (med.getOpacidadDouble() + valorRestante);
                    System.out.println(" valorFinal :" + valorFinal);
                    med.setOpacidad(valorFinal);
                    System.out.println(" Valor final final " + med.getOpacidad());
//                    ev= med.getOpacidadDouble() + randon;
                    panel.getPanelMensaje().setText("Opacidad: " + df2.format((med.getOpacidad()) + randon) + ", t: " + contadorTemporizacion + "SEGUNDO");

                    if (contadorTemporizacion == Opacimetro.TIEMPO_ESCALA) {
                        if ((med.getOpacidad() + randon) < Opacimetro.VALOR_ESCALA_MAXIMA) {
                            JOptionPane.showMessageDialog(panel, "Ajuste del Valor Maximo de la Escala Incorrecto \n"
                                    + "Repita los Procedimientos de Purga y Limpieza");
                            return false;
                        }
                    }
                } else {
//                    ev = med.getOpacidadDouble();
                    panel.getPanelMensaje().setText("Opacidad: " + df2.format((med.getOpacidadDouble())) + ", t: " + contadorTemporizacion + "SEGUNDO");
                    if (contadorTemporizacion == Opacimetro.TIEMPO_ESCALA) {
                        if ((med.getOpacidadDouble()) < Opacimetro.VALOR_ESCALA_MAXIMA) {
                            JOptionPane.showMessageDialog(panel, "Ajuste del Valor Maximo de la Escala Incorrecto \n"
                                    + "Repita los Procedimientos de Purga y Limpieza");
                            return false;
                        }
                    }
                }
            }
            panel.getPanelMensaje().setText("AJUSTE DEL VALOR MAXIMO CORRECTO");

        } catch (InterruptedException e) {
            System.out.println("Error en el metodo :ajustarValorMaximo()" + e.getMessage());
            return false;
        }
        contadorTemporizacion = 0;
        return true;
    }

    private boolean procesandoValorMinimo() {
        System.out.println("------------------------------------------");
        System.out.println("---- PROCESANDO VALOR MINIMO     - -------");
        System.out.println("------------------------------------------");
        try {
            int contMinimo = 0;
            while (contMinimo < 3) {
                if (contMinimo == 2) {
                    liberarRecursos();
                    JOptionPane.showMessageDialog(null, " Por favor consulte a servicio tecnico \n , "
                            + "Se aborta la prueba, No se puede ajustar el valor Minimo");
                    UtilidadAbortoPrueba llamar = new UtilidadAbortoPrueba();
                    llamar.insertarAbortoPrueba(idUsuario, idPrueba);
                    panel.cerrar();
                    return false;
                }
                if (contMinimo > 0) {
                    panel.getPanelMensaje().setText("VOY A REDEFINIR LA ESCALA ...");
                    Thread.sleep(3000);
                }
                if (!ajustarValorMinimo()) {
                    rutinaLimpiezaPurga(true);
                } else {
                    break;
                }
                contMinimo++;
            }
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            Mensajes.messageErrorTime("Error al ajustar valor minimo " + e + "", 5);
            System.out.println("Error en el metodo :procesandoValorMinimo()" + e.getMessage());
            System.out.println("Error :" + e);
            return false;
        }
        return true;
    }

    private boolean procesandoValorMaximo() {
        LOG.info("------------------------------------------");
        LOG.info("----   AJUSTANDO VALOR MAXIMO     --------");
        LOG.info("------------------------------------------");
        try {
            int contadorMaximo = 0;
            while (contadorMaximo < 3) {
                if (contadorMaximo == 2) {
                    liberarRecursos();
                    JOptionPane.showMessageDialog(null, " Por favor consulte a servicio tecnico \n  , "
                            + "Se aborta la prueba, No se puede ajustar el valor Maximo");
                    UtilidadAbortoPrueba llamar = new UtilidadAbortoPrueba();
                    llamar.insertarAbortoPrueba(idUsuario, idPrueba);
                    panel.cerrar();
                    return false;
                }
                if (contadorMaximo > 0) {
                    panel.getPanelMensaje().setText("VOY A REDEFINIR LA ESCALA ...");
                    Thread.sleep(3000);
                }
                if (!ajustarValorMaximo()) {
                    rutinaLimpiezaPurga(true);
                } else {
                    break;
                }
                contadorMaximo++;
            }
            Thread.sleep(2500);

        } catch (InterruptedException e) {
            Mensajes.messageErrorTime("Error al ajustar valor MAXIMO " + e + "", 5);
            System.out.println("Error en el metodo :procesandoValorMaximo()" + e.getMessage());
            System.out.println("Error :" + e);
            return false;
        }
        return true;
    }

    private void mostrandoWarning() {
        LOG.info("------------------------------------------");
        LOG.info("----        Mostrando warning     --------");
        LOG.info("------------------------------------------");
        try {
            icono = panel.getPanelMensaje().getComponent(0);
            msg = panel.getPanelMensaje().getComponent(1);
            fAlt = new Font(Font.SERIF, Font.BOLD, 40);
            msg.setFont(fAlt);
            panel.getPanelMensaje().remove(0);
            panel.getPanelMensaje().updateUI();
            panel.getPanelMensaje().repaint();
            Thread.sleep(100);
            panel.getPanelMensaje().setText("3.1.3.1 INSPECCION PREVIA \n ASEGURESE DE QUE : \n VEHICULO EN NEUTRO O EN POSICION DE PARQUEO "
                    + "EMBRAGUE LIBRE DURANTE TODA LA PRUEBA  "
                    + "RUEDAS DEL VEHICULO BLOQUEADAS, LUCES DEL VEHICULO ENCENDIDAS  "
                    + "AIRE ACONDICIONADO Y PRECALENTAMIENTO DE AIRE APAGADOS, "
                    + "FRENO DE MOTOR O ESCAPE DESACTIVADO.-");
            Thread.sleep(3900);
            panel.getPanelMensaje().add(icono, 0);
            fAlt = new Font(Font.SERIF, Font.BOLD, 61);
            msg.setFont(fAlt);
            panel.getPanelMensaje().updateUI();
            panel.getPanelMensaje().repaint();
            Thread.sleep(3000);
            panel.getPanelMensaje().setText(" SI POR LAS LUCES ENCENDIDAS : \n HAY INESTABILIDAD O "
                    + "INTERFERENCIA EN LA LECTURA DE LA VELOCIDAD DEL MOTOR"
                    + " \n REALICE LA PRUEBA CON LAS LUCES APAGADAS.-");
            Thread.sleep(3000);
            // panel.getPanelMensaje().setText("3.1.3.8 POR FAVOR MARQUE EL DEFECTO SI EXISTE");
            //JFM Thread.sleep(1500);
        } catch (InterruptedException ex) {
            Mensajes.messageErrorTime("Error al Pre cargar PANEL " + ex + "", 5);
            LOG.log(Level.INFO, "Error en el metodo :mostrandoWarning(){0}", ex.getMessage());
            LOG.log(Level.INFO, "Error :{0}", ex);
            java.util.logging.Logger.getLogger(CallableInicioDiesel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean InspecionSensorial() {
        LOG.info("------------------------------------------");
        LOG.info("----       InspecionSensorial     --------");
        LOG.info("------------------------------------------");

        PanelVerificacionDiesel p = new PanelVerificacionDiesel();//Se muestra el dialogo y se almacenan las selecciones del usuario en la referencia
        JOptionPane.showMessageDialog(null, p, "SART 1.7.3.- Inspeccion Sensorial", JOptionPane.PLAIN_MESSAGE);

        if (p.isDefectoEncontrado()) {
            System.out.println("Defecto " + p.getMensaje().toString());
            JOptionPane.showMessageDialog(panel, p.getMensaje().toString());
            System.out.println("Estos son los defectos que se van a registrar " + p.getConjuntoDefectos());
            try {
                System.out.println("//////Mensaje de rechazo desde inicio diesel: "+p.getMsgRechazo());
                UtilDefectosVisuales.registrarDefectosVisuales(p.getConjuntoDefectos(), urljdbc, idPrueba, idUsuario, p.getMsgRechazo(), 80000);//Separacion de logica de ´persistencia y logica de negocio
            } finally {
                try {
                    liberarRecursos();
                    String strTermoHigrometro = UtilPropiedades.cargarPropiedad("TermoHigrometro", "propiedades.properties");

                    if (strTermoHigrometro != null && strTermoHigrometro.equalsIgnoreCase("Habilitado")) {
                        String strMarcaTermohigrometro = UtilPropiedades.cargarPropiedad("MarcaTermoHigrometro", "propiedades.properties");
                        if (strMarcaTermohigrometro.equalsIgnoreCase("Artisan")) {
                            if (humedadAmbiente == 0 && tempAmbiente == 0) {
                                JOptionPane.showMessageDialog(null, "Disculpe, no DETECTO el Termohigrometro, por lo tanto esta prueba no se REALIZARA ..! ");
                                panel.cerrar();
                                return false;
                            }
                        } else {
                            TermoHigrometro termoHigrometro = new TermoHigrometroPCSensors();
                            humedadAmbiente = termoHigrometro.obtenerValores().getValorHumedad();
                            tempAmbiente = termoHigrometro.obtenerValores().getValorTemperatura();
                        }
                        //Muestra de Validacion de Temperatura y Humedad
                        EvaluarRegistrarPruebaGasolina evalReg = new EvaluarRegistrarPruebaGasolina(0, 0, idPrueba);
                        evalReg.registrarTemperaturaHumedad(humedadAmbiente, tempAmbiente);
                    }
                    this.rechazoPrueba("Condiciones Anormales", p.getMsgRechazo());
                    return false;
                } catch (IOException ex) {
                    LOG.log(Level.INFO, "Error :{0}", ex);
                    LOG.log(Level.INFO, "Error en el metodo :InspecionSensorial(){0}", ex.getMessage());
                    Mensajes.messageErrorTime("Error en el apartado de Condiciones anormales " + ex + "", 5);
                    java.util.logging.Logger.getLogger(CallableInicioDiesel.class.getName()).log(Level.SEVERE, null, ex);
                    return false;
                } catch (ClassNotFoundException ex) {
                    LOG.log(Level.INFO, "Error :{0}", ex);
                    LOG.log(Level.INFO, "Error en el metodo :InspecionSensorial(){0}", ex.getMessage());
                    Mensajes.messageErrorTime("Error en el apartado de Condiciones anormales " + ex + "", 5);
                    java.util.logging.Logger.getLogger(CallableInicioDiesel.class.getName()).log(Level.SEVERE, null, ex);
                    return false;
                }
            }
        }
        return true;
    }

    private void capturarDiametroExostos() throws ClassNotFoundException, SQLException {
        LOG.info("------------------------------------------");
        LOG.info("----   capturarDiametroExosto     --------");
        LOG.info("------------------------------------------");

        try {
            panel.getPanelMensaje().add(icono, 0);
            System.out.println("Numero de Cilindros:");
            Thread.sleep(1500);
            //panel.getPanelMensaje().setText("3.1.3.9 La Temperatura del motor es 51.");            
            //panel.getPanelMensaje().setText("POR FAVOR DIGITE EL DIAMETRO DEL EXHOSTO");
            Thread.sleep(1500);
            boolean numeroValido = false;
            String strDiametro;
            do {
                //strDiametro = JOptionPane.showInputDialog(panel, "INTRODUZCA EL DIAMETRO DEL EXOSTO EN MM");
                strDiametro = "430";
                try {
                    diametroExosto = Double.parseDouble(strDiametro);
                    if (diametroExosto > 0) {
                        numeroValido = true;

                    }
                } catch (NumberFormatException ne) {
                    JOptionPane.showMessageDialog(panel, "POR FAVOR, DIGITE UN NUMERO VALIDO");
                } catch (NullPointerException ne) {
                    JOptionPane.showMessageDialog(panel, "DISCULPE; NO PUEDE DEJAR EL DIAMETRO VACIO");
                }
            } while (numeroValido == false);
            System.out.println("antes de actualizar el diametro");
            try {
                conexion = Conex.getConnection();
                conexion.setAutoCommit(false);
                String sqlInfo = "SELECT CAR,CARTYPE,Modelo,Tiempos_motor,FUELTYPE,SERVICE from vehiculos as v inner join hoja_pruebas as hp on v.CAR=hp.Vehiculo_for where hp.TESTSHEET = ?";
                PreparedStatement psInfo = conexion.prepareStatement(sqlInfo);
                psInfo.setLong(1, idHojaPrueba);
                ResultSet rs = psInfo.executeQuery();
                rs.next();//primer registro
                long idVehiculo = rs.getLong("CAR");
                System.out.println("id del vehiculo: " + idVehiculo);
                System.out.println("diametro del exosto que le estoy enviando: " + diametroExosto);
                String Diametro = "UPDATE vehiculos SET Diametro =? WHERE CAR = ?";
                PreparedStatement psDiametro = conexion.prepareStatement(Diametro);
                psDiametro.setInt(1, (int) diametroExosto);
                psDiametro.setLong(2, idVehiculo);
                System.out.println("consulta a ejecutar: " + psDiametro.toString());
                psDiametro.executeUpdate();

                conexion.commit();
                conexion.setAutoCommit(true);
                conexion.close();
                System.out.println("diametro del exosto actualizado en callable");
            } catch (SQLException e) {
                System.out.println("Erro en el metodo : actualizarDiametroExostoVehiculoCallableInicioDiesel()");
                System.out.println("Error : " + e);
                Mensajes.mostrarExcepcion(e);
            }

            //Thread.sleep(2500);
            panel.getPanelMensaje().setText("3.1.3.8 POR FAVOR MARQUE EL DEFECTO SI EXISTE");
            Thread.sleep(1500);
        } catch (InterruptedException ex) {
            Mensajes.messageErrorTime("Error al capturar diametroExosto " + ex + "", 5);
            LOG.log(Level.INFO, "Error en el metodo :capturarDiametroExosto(){0}", ex.getMessage());
            LOG.log(Level.INFO, "Error :{0}", ex);
            java.util.logging.Logger.getLogger(CallableInicioDiesel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void MensajeOperarioPreparacionCarro() {
        LOG.info("------------------------------------------");
        LOG.info("----   capturarDiametroExosto     --------");
        LOG.info("------------------------------------------");

        try {
            //MENSAJES OPERARIO PREPARACION DEL CARRO
            icono = panel.getPanelMensaje().getComponent(0);
            msg = panel.getPanelMensaje().getComponent(1);
            fAlt = new Font(Font.SERIF, Font.BOLD, 53);
            msg.setFont(fAlt);
            panel.getPanelMensaje().remove(0);
            panel.getPanelMensaje().updateUI();
            panel.getPanelMensaje().repaint();
            Thread.sleep(100);
            panel.getPanelMensaje().setText("ASEGURESE DE:\n"
                    + "VEHICULO EN NEUTRO O PARQUEO RUEDAS BLOQUEADAS, "
                    + "ENCENDER LAS LUCES DESACTIVAR FRENO DE MOTOR O ESCAPE"
                    + "SI EXISTE DESACTIVAR FRENO DE MOTOR O ESCAPE SI EXISTE"
                    + "APAQUE SISTEMA DE PRECALENTAMIENTO DE AIRE.-");
            Thread.sleep(4500);//punto de cancelacion                    
            // panel.getButtonRpm().addActionListener(new Listener(diametroExosto));//mantener el valor del diametro del exosto hasta el final
            panel.getButtonRpm().addActionListener(new Listener());
            panel.getPanelMensaje().add(icono, 0);
            panel.getButtonRpm().doClick();//Debe pedir la temperatura
            Thread.sleep(100);
//            panel.getPanelMensaje().setText("La Temperatura del motor medida fue 51");
//            Thread.sleep(100);
            timer.stop();
        } catch (InterruptedException ex) {
            Mensajes.messageErrorTime("Error inesperado " + ex + "", 5);
            LOG.log(Level.INFO, "Error en el metodo :MensajeOperarioPreparacionCarro(){0}", ex.getMessage());
            LOG.log(Level.INFO, "Error :{0}", ex);
            java.util.logging.Logger.getLogger(CallableInicioDiesel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Void call() throws Exception {
        LOG.info("------------------------------------------");
        LOG.info("----           call()             --------");
        LOG.info("------------------------------------------");
        try {
            //PREPARANDO OPACIMETRO
            preparandoOpacimetro();

            //RUTINAS DE LIMPIEZA Y PURGA
            rutinaLimpiezaPurga(toggle);

            //AJUSTANDO VALOR MINIMO
            if (!procesandoValorMinimo()) {
                return null;
            }

            //CALCULANDO MAXIMO
            if (!procesandoValorMaximo()) {
                return null;
            }

            //cargando cosas del panel
            mostrandoWarning();

            //SE CAPTURA EL DIAMETRO DEL EXOSTO
            capturarDiametroExostos();

            //SE VALIDA LA PARTE DE INSPECION SENSORIAL
            if (!InspecionSensorial()) {
                return null;
            }

            //MENSAJE OPERARIO PREPARACION CARRO
            MensajeOperarioPreparacionCarro();

            return null;
        } catch (Exception exc) {
            timer.stop();//para el timer
            opacimetro.getPort().close();//cierra el puerto serial del opacimetro
            panel.cerrar();//cierra el panel
            throw new ExecutionException(exc);
        } finally {
            if (timer != null) {
                timer.stop();
            }
        }

    }//end of method Call

    /**
     * Cierra el panel cierra el puerto serial para el timer
     */
    private void liberarRecursos() {
        panel.cerrar();
        if (opacimetro.getPort() != null) {
            opacimetro.getPort().close();
        }
        if (timer != null) {
            timer.stop();
        }
    }

    /**
     * Llama al WorkerCiclosDiesel
     */
    class Listener implements ActionListener {

        /* private double diametro;
        public Listener(double diametro) 
        {
            this.diametro = diametro;
        }
         */
        @Override
        public void actionPerformed(ActionEvent e) {//hace falta un try catch para que no se trague la excepcion
            try {
                System.out.println("entro al actionperformed");
                //WorkerCiclosDiesel wcd = new WorkerCiclosDiesel(panel, opacimetro, idUsuario, idHojaPrueba, idPrueba, (int) 4);
                WorkerCiclosDiesel wcd = new WorkerCiclosDiesel(panel, opacimetro, idUsuario, idHojaPrueba, idPrueba, diametroExosto, tempAmbiente, humedadAmbiente);
                // wcd.actualizarDiametroExostoVehiculo(idHojaPrueba, (int) diametroExosto);

                wcd.execute();
            } catch (Exception exc) {
                JOptionPane.showMessageDialog(null, "Excepcion : " + exc.getMessage());
                Logger.getRootLogger().error("Error durante WorkerCiclosDiesel", exc);
                exc.printStackTrace();
            }//end of catch
        }//end of actionPerformed

    }//final de la inner class Listener

    private void rechazoPrueba(String causa, String msgRechazo) {
        System.out.println("RECHAZO PRUEBA");
        panel.cerrar();
        try {
            frmC = new JDialogDiesel(null, false, 0, 0, idHojaPrueba);
        } catch (Exception ex) {
        }
        frmC.setTitle("SART 1.7.3 RECHAZO DE PRUEBA");
        panelCancelacion = new PanelPruebaGases();
        frmC.getContentPane().add(panelCancelacion);
        ImageIcon image = new ImageIcon(getClass().getResource("/imagenes/cancelar.png"));
        panelCancelacion.getPanelMensaje().setText(causa.toUpperCase());
        panelCancelacion.getProgressBar().setVisible(false);
        JLabel lblImg = (JLabel) panelCancelacion.getPanelMensaje().getComponent(0);
        lblImg.setIcon(image);
        frmC.setIconImage(new ImageIcon(getClass().getResource("/imagenes/cancelar.png")).getImage());
        panelCancelacion.getMensaje().setText("RECHAZO DE PRUEBAS POR ".concat(causa).toUpperCase());
        frmC.setVisible(true);
        panelCancelacion.getButtonFinalizar().setVisible(false);
        panelCancelacion.setVisible(true);
        if (Mensajes.mensajePregunta("¿Desea Agregar otra Observacion Referente a la causal de Rechazo?")) {
            org.soltelec.pruebasgases.FrmComentarioDiesel frm = new org.soltelec.pruebasgases.FrmComentarioDiesel(SwingUtilities.getWindowAncestor(panelCancelacion), idPrueba, JDialog.DEFAULT_MODALITY_TYPE, msgRechazo, idUsuario, "Condiciones");
            panelCancelacion.getMensaje().setText("Registro de Ampliacion de Observaciones Encontradas..! ");
            frm.setLocationRelativeTo(panelCancelacion);
            frm.setVisible(true);
            frm.setModal(true);
        }
        panelCancelacion.cerrar();
    }

}
