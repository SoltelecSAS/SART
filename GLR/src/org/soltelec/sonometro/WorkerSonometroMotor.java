/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.sonometro;

//import com.soltelec.modulopuc.configuracion.modelo.Propiedades;
import gnu.io.SerialPort;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import org.apache.log4j.Logger;
import org.soltelec.medicionrpm.CapelecSerial;
import org.soltelec.medicionrpm.ConsumidorCapelec;
import org.soltelec.medicionrpm.JDialogReconfiguracionKit;
import org.soltelec.medicionrpm.centralauto.PanelServicioTB8600;
import org.soltelec.medicionrpm.MedidorRevTemp;
import org.soltelec.medicionrpm.ProductorCapelec;
import org.soltelec.medicionrpm.centralauto.TB85000;
import org.soltelec.medicionrpm.centralauto.TB86000;
import org.soltelec.procesosbanco.BancoSensors;
import org.soltelec.pruebasgases.DialogoRPMTemp;
import org.soltelec.pruebasgases.ListenerCambioRpms;
import org.soltelec.sonometro.gyp.ConsumidorH55633T;
import org.soltelec.sonometro.gyp.ProductorH55633T;
import org.soltelec.sonometro.indutesa.ConsumidorPC322A;
import org.soltelec.sonometro.indutesa.ProductorSonometroPC322A;
import org.soltelec.util.BufferStrings;
import org.soltelec.util.MedidaGeneral;
import org.soltelec.util.PortSerialUtil;
import org.soltelec.util.RegistrarMedidas;
import org.soltelec.util.ShiftRegister;
import org.soltelec.util.UtilPropiedades;
import org.soltelec.util.propiedadesSonometro;

/**
 * Clase para hacer la ejecucion de la prueba de sonometro pero la parte del
 * ruido del motor
 *
 * @author GerenciaDesarrollo
 */
public class WorkerSonometroMotor extends SwingWorker<Void, Void> {

    PanelPruebaSonometro panel;
    private final DialogoRPMTemp dialogRPMTemp;
    private final long idUsuario, idPrueba, idHojaPrueba;
    private Sonometro sonometro;
    private String nombregasolina;
    Thread threadProductor;
    Thread threadConsumidor;
//    private propiedadesSonometro PropiedadesSonometro;
  

    public WorkerSonometroMotor(PanelPruebaSonometro panel, long idUsuario, long idHojaPrueba, long idPrueba) {

        this.panel = panel;
        dialogRPMTemp = new DialogoRPMTemp(idHojaPrueba);
        this.idUsuario = idUsuario;
        this.idPrueba = idPrueba;
        this.idHojaPrueba = idHojaPrueba;
//        PropiedadesSonometro=new propiedadesSonometro();

    }

    WorkerSonometroMotor(PanelPruebaSonometro panel, long idUsuario, long idHojaPrueba, long idPrueba, String nombregasolina) {
        this.panel = panel;
        this.nombregasolina = nombregasolina;
        dialogRPMTemp = new DialogoRPMTemp(idHojaPrueba);
        this.idUsuario = idUsuario;
        this.idPrueba = idPrueba;
        this.idHojaPrueba = idHojaPrueba;
//        PropiedadesSonometro=new propiedadesSonometro();
    }

    @Override
    protected Void doInBackground() throws Exception {
        
        propiedadesSonometro  PropiedadesSonometro=new propiedadesSonometro();
        sonometro = instanciarSonometro();
        Thread.sleep(1500);
        int contador = 0;

        ShiftRegister sr = new ShiftRegister(5, 0);
        double decibeles = sonometro.getDecibeles();
             System.out.println("voy contador y en db :"+ decibeles);
        while (contador < 10) {
            Thread.sleep(500);
            decibeles = sonometro.getDecibeles();
            sr.ponerElemento(decibeles);
            contador++;
        }
          System.out.println("voy tamaño ShiftRegister is :"+ sr.getSize());
        if (sr.media() < 0) {
            JOptionPane.showMessageDialog(null, "SONOMETRO NO ENCENDIDO O DESCONECTADO");
            Thread.sleep(2000);
            liberarRecursos();
            String nombrePuertoSerial = UtilPropiedades.cargarPropiedad("PuertoSonometro", "propiedades.properties");
            SerialPort puertoSerial = PortSerialUtil.connect(nombrePuertoSerial, 2400, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            puertoSerial.close();
        }
        if (!PropiedadesSonometro.cargarConfiProperties()) {
            mostrarDialogoRPM();//mostrar un dialogo para la seleccion de las revoluciones
        }
        System.out.println("btn kit rpm salto ");
        System.out.println("Numero de cilindros :" + dialogRPMTemp.getNumeroCilindros());
        System.out.println("Equipo de Medicion :" + dialogRPMTemp.getEquipo());
        System.out.println("Metodo de Medicion :" + dialogRPMTemp.getMetodoMedicion());
        System.out.println("Multiplicador :" + dialogRPMTemp.isUsaMultiplicador());

        int numeroCilindros = dialogRPMTemp.getNumeroCilindros();//variable de clase??
        String equipoMedicion = dialogRPMTemp.getEquipo();
        String metodoMedicion = dialogRPMTemp.getMetodoMedicion();
        boolean multiplicador = dialogRPMTemp.isUsaMultiplicador();

        //SEGUN LA OOP DEBERIA USAR LA INSTANCIA DEL APARATO QUE MIDE RPM
        //EN CONCORDANCIA A LO QUE SELECCIONE USANDO EL DIALOGO
        //Y LLAMAR POLIMORFICAMENTE AL METODO PARA QUE ME DE LA TEMPERATURA
        //Y LAS REVOLUCIONES
        //LA OTRA ALTERNATIVA ES USE IF ELSE Y REPETIR CODIGO ESA HA SIDO SIEMPRE LA SOLUCION
        //Cuando se cambia de revoluciones no se debe cerrar el consumidor del sonometro ni tampoco se debe 
        //terminar el hilo productor ni cerrar el puerto serial, se debe cerrar el puerto serial del dispositivo de revoluciones
        //segun sea el caso.
        //Si se cancela entonces se debe cerrar el consumidor del sonometro, el productor y el puerto serial del sonometro
        //ademas de cerrar el puerto serial del dispositivo de medicionn de revoluciones segun sea el caso.
        //Si sucede una excepcion se deben liberar los recursos
        //
        //Simulacion
        boolean simulacion = dialogRPMTemp.simulacion;
        List<List<Double>> listaMediciones = null;
        try {
            //DEPENDIENDO DEL METODO DE MEDICION INICIAR UN HILO DISTINTO
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<List<List<Double>>> future = null;
            //Indica que cuando la prueba de rpm esta habilitada se debe simular y no mostrar el tacometro
            
            if (simulacion || PropiedadesSonometro.cargarConfiProperties())
            {
                future = executor.submit(new CallableSonSimul(panel, sonometro));
                panel.getButtonRpm().addActionListener(new ListenerCambioRpms(future, null,null,null,idUsuario));
                panel.getButtonFinalizar().addActionListener(new ListenerCancelacionSonometro(future, idPrueba, panel, sonometro,idUsuario));
                listaMediciones = future.get();
            } else if (equipoMedicion.equals("banco")) {
                MedidorRevTemp medidorRevTemp = null;
                try {
                    medidorRevTemp = new BancoSensors();
                    String nombrePuerto = UtilPropiedades.cargarPropiedad("PuertoBanco", "propiedades.properties");
                    SerialPort puertoSerial = PortSerialUtil.connect(nombrePuerto, 9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                    medidorRevTemp.setPuertoSerial(puertoSerial);
                    future = executor.submit(new CallableAceleraciones(panel, sonometro, medidorRevTemp));
                    // panel.getButtonRpm().addActionListener(new ListenerCambioRpms(future,null));
                    panel.getButtonFinalizar().addActionListener(new ListenerCancelacionSonometro(future, idPrueba, panel, sonometro,idUsuario));
                    listaMediciones = future.get();

                } catch (CancellationException cexc) {//cambio de dispositivo para medir las revoluciones o cancelacion

                    if (medidorRevTemp != null) {
                        medidorRevTemp.getPuertoSerial().close();
                    }
                    return null;//retorna del metodo.
                }

            } else if (equipoMedicion.equals("kit")) {

                String nombrePuerto = UtilPropiedades.cargarPropiedad("PuertoKit", "propiedades.properties");
                SerialPort serialPort = PortSerialUtil.connect(nombrePuerto, 9600, SerialPort.DATABITS_8,
                        SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                InputStream in = serialPort.getInputStream();
                OutputStream out = serialPort.getOutputStream();
                CapelecSerial capelecServicios = new CapelecSerial();
                capelecServicios.setOut(out);
                //capelecServicios.enviarComandoNoParametros(CapelecSerial.RESET);

                BufferStrings bufferMed = new BufferStrings();
                ProductorCapelec productor = new ProductorCapelec(in, bufferMed);
                ConsumidorCapelec capelec = new ConsumidorCapelec(bufferMed, productor);
                //PARA QUE SE PUEDA CERRAR EL PUERTO SERIAL
                capelec.setPuertoSerial(serialPort);
                (new Thread(productor)).start();
                (new Thread(capelec)).start();
                //llamar al mismo callable polimorfico
                JDialogReconfiguracionKit dlgReconfiguracion = new JDialogReconfiguracionKit(capelec, serialPort);
                dlgReconfiguracion.setVisible(true);//se bloquea hasta que el kit este correctamente configurado

                try {
                    future = executor.submit(new CallableAceleraciones(panel, sonometro, capelec, nombregasolina));
                    panel.getButtonFinalizar().addActionListener(new ListenerCancelacionSonometro(future, idPrueba, panel, sonometro,idUsuario));
                    // panel.getButtonRpm().addActionListener(new ListenerCambioRpms(future,null));
                    listaMediciones = future.get();

                } catch (CancellationException cexc) {
                    System.out.println("Prueba cancelada");
                    return null;
                } finally {

                    capelec.setTerminado(true);//si no ocurre ninguna excepcion
                    Thread.sleep(1000);
                    productor.setTerminado(true);//si no ocurre ninguna excepcion
                    Thread.sleep(1000);

                    try {
                        serialPort.close();
                    } catch (Exception exc) {

                    }
                }

            } else if (equipoMedicion.equals("centralAuto")) {

                String nombrePuerto = UtilPropiedades.cargarPropiedad("PuertoCentralAuto", "propiedades.properties");
                SerialPort puertoSerial = PortSerialUtil.connect(nombrePuerto, 9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

                //TB85000 tb85000 = new TB85000(puertoSerial);
                MedidorRevTemp medidorRevTemp = new TB85000(puertoSerial);
                future = executor.submit(new CallableAceleraciones(panel, sonometro, medidorRevTemp, nombregasolina));
                // panel.getButtonRpm().addActionListener(new ListenerCambioRpms(future,null));
                panel.getButtonFinalizar().addActionListener(new ListenerCancelacionSonometro(future, idPrueba, panel, sonometro,idUsuario));

                try {
                    listaMediciones = future.get();
                } catch (CancellationException cexc) {

                    return null;
                } finally {
                    if (puertoSerial != null) {
                        puertoSerial.close();
                    }
                }

            } else if (equipoMedicion.equals("tb8600")) {

                String nombrePuerto = UtilPropiedades.cargarPropiedad("PuertoCentralAuto", "propiedades.properties");
                SerialPort puerto = PortSerialUtil.connect(nombrePuerto, 9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

                TB86000 tb86000 = new TB86000(puerto);
                future = executor.submit(new CallableAceleraciones(panel, sonometro, tb86000));
                panel.getButtonFinalizar().setVisible(true);
                panel.getButtonFinalizar().addActionListener(new ListenerCancelacionSonometro(future, idPrueba, panel, sonometro,idUsuario));
                //JOptionPane.showMessageDialog(null,"Numero de Tiempos: " + numTiempos + " Numero de Cilindros: " + numeroCilindros);               
                //tb86000.setNumeroCilindros((numeroCilindros > 4)? numeroCilindros : 4);
                PanelServicioTB8600 gui = new PanelServicioTB8600(tb86000);
                JDialog app = new JDialog();
                app.add(gui);
                app.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                app.setSize(app.getToolkit().getScreenSize());
                app.setModal(true);
                app.setVisible(true);

                try {

                    listaMediciones = future.get();
                } catch (CancellationException cexc) {//si esa future es cancelada entonces salir del metodo

                    return null;

                } finally {

                    if (puerto != null) {
                        puerto.close();
                    }

                }

            }
            //Procesar los resultados
            
            List<MedidaGeneral> listaMedidas = armarMedidas(listaMediciones);
            MedidaGeneral mG = (MedidaGeneral) listaMedidas.iterator().next();
            Double valMax = mG.getValorMedida();
            for (MedidaGeneral mg : listaMedidas) {
                if(mg.getTipoMedida()==7005){
                    valMax = mg.getValorMedida();
                }
                /*if(mg.getValorMedida()>  valMax ){
                    valMax = mg.getValorMedida();
                }*/
            }            
            liberarRecursos();
            //            
            registrarResultados(listaMedidas);
             Thread.sleep(1000);
            //Registrar los resultados en la base de datos
        } catch (ExecutionException exc) {
            JOptionPane.showMessageDialog(null, "Error realizando la prueba");
            exc.printStackTrace();

        }//end of catch
        catch (CancellationException cexc) {
            Logger.getRootLogger().info("Cancelacion o cambio de rpms en prueba de sonometro");
        } catch (SQLException sqlExc) {
            JOptionPane.showMessageDialog(panel, "Error registrando los datos en la bd");
            sqlExc.printStackTrace();
        } catch (Exception exc) {
            JOptionPane.showMessageDialog(panel, "Error " + exc.getMessage());
            exc.printStackTrace();
        } finally {//cierra solamente cuando se completo la operacion finally siempre se ejecuta antes del return
            sonometro.stop();   
            
        }
        return null;
    }//end of method doInBackground

    public void mostrarDialogoRPM() {
        JDialog d = new JDialog(SwingUtilities.getWindowAncestor(panel));
        d.getContentPane().add(dialogRPMTemp);
        d.setSize(500, 300);
        d.setResizable(false);
        d.setModal(true);
        d.setLocationRelativeTo(panel);
        d.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        d.setVisible(true);
    }//end of method mostrarDialogoRPM

    private List<MedidaGeneral> armarMedidas(List<List<Double>> listaMediciones) {
        List<MedidaGeneral> listaMedidas = new ArrayList<MedidaGeneral>(3);
//        listaMedidas.add(new MedidaGeneral(7000, listaMedidasPito.get(0)));
//        listaMedidas.add(new MedidaGeneral(7001,listaMedidasPito.get(1)));
//        listaMedidas.add(new MedidaGeneral(7002,listaMedidasPito.get(2)));
        Double maxEscapeCiclo1 = Collections.max(listaMediciones.get(0));
        Double maxEscapeCiclo2 = Collections.max(listaMediciones.get(1));
        Double maxEscapeCiclo3 = Collections.max(listaMediciones.get(2));
        listaMedidas.add(new MedidaGeneral(7003, maxEscapeCiclo1));
        listaMedidas.add(new MedidaGeneral(7004, maxEscapeCiclo2));
        listaMedidas.add(new MedidaGeneral(7005, maxEscapeCiclo3));
        return listaMedidas;
    }

    private void registrarResultados(List<MedidaGeneral> listaMedidas) throws SQLException, ClassNotFoundException {
        RegistrarMedidas registrarMedidas = new RegistrarMedidas();
        boolean registro = verificarMedidas(listaMedidas);//Verifica si una medida es menor o igual a cero
        if (!registro) {
            JOptionPane.showMessageDialog(null, "Alguno o todos los datos son invalidos debe reiniciar la prueba?");
            return;
        }
        try (Connection con = registrarMedidas.getConnection()) {
            registrarMedidas.registrarMedidas(listaMedidas, idPrueba, con);         
            registrarMedidas.registrarPruebaFinalizada(con, true, idUsuario, idPrueba,true,"192");
        }
    }

    public boolean verificarMedidas(List<MedidaGeneral> medidas) {
        for (MedidaGeneral d : medidas) {
            if (d.getValorMedida() <= 0) {
                return false;
            }
        }
        return true;
    }

    private void liberarRecursos() {
        try {
            panel.cerrar();
            sonometro.stop();
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                System.out.println("Error con el thread");
            }
        } catch (Exception exc) {
            Logger.getRootLogger().error("Error liberando los recursos de la prueba de sonometro", exc);
        }
    }

    private Sonometro instanciarSonometro() throws Exception {
        Sonometro son = null;
        String strMarcaSonometro = UtilPropiedades.cargarPropiedad("MarcaSonometro", "propiedades.properties");

        if (strMarcaSonometro == null || strMarcaSonometro.equals(Sonometro.XTECH)) {

            SerialPort puertoSerial = conectarPuertoSerialSonometro();
            //Iniciar el hilo del sonometro 
            puertoSerial.setDTR(true);
            puertoSerial.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT);
            puertoSerial.setRTS(true);

            BufferStrings bufferMed = new BufferStrings();
            LectorHTech lectorHTech = new LectorHTech(puertoSerial, bufferMed);
            ConsumidorHtech consumidorHTech = new ConsumidorHtech(bufferMed, lectorHTech);
            threadProductor = new Thread(lectorHTech);
            threadProductor.start();
             threadConsumidor = new Thread(consumidorHTech);
            threadConsumidor.start();
            son = consumidorHTech;//polimorfismo

        } else if (strMarcaSonometro.equals(Sonometro.PCE322A)) {

            SerialPort puertoSerial = conectarPuertoSerial9600();

            BlockingQueue<List<Integer>> lista = new ArrayBlockingQueue<List<Integer>>(100);

            ProductorSonometroPC322A productor = new ProductorSonometroPC322A(puertoSerial, lista);
            ConsumidorPC322A consumidor = new ConsumidorPC322A(lista, productor);

            Thread threadProductor = new Thread(productor);
            threadProductor.start();
            Thread threadConsumidor = new Thread(consumidor);
            threadConsumidor.start();

            son = consumidor;
            System.out.println("Sonometro Indutesa iniciado");

//            for(int i = 0; i < 10 ; i++){
//                
//                System.out.println("TOMANDO MEDICIOM: " + sonometro.getDecibeles());
//                Thread.sleep(1000);
//            
//            }
        } else if (strMarcaSonometro.equals(Sonometro.PCE322ASerial)) {

        } else if (strMarcaSonometro.equals(Sonometro.H55633T)) {
            SerialPort puertoSerial = conectarPuertoSerial9600();
            BlockingQueue<List<Integer>> lista = new ArrayBlockingQueue<>(100);
            ProductorH55633T productor = new ProductorH55633T(puertoSerial, lista);
            ConsumidorH55633T consumidor = new ConsumidorH55633T(lista, productor);
            Thread threadConsumidor = new Thread(productor);
            threadConsumidor.start();
            Thread threadProductor = new Thread(consumidor);
            threadProductor.start();
            son = consumidor;
            System.out.println("Sonometro H5563T iniciado");
        }

        return son;
    }

    private SerialPort conectarPuertoSerialSonometro() throws Exception {

        String nombrePuertoSerial = UtilPropiedades.cargarPropiedad("PuertoSonometro", "propiedades.properties");
        SerialPort puertoSerial = PortSerialUtil.connect(nombrePuertoSerial, 2400, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        return puertoSerial;
    }

    private SerialPort conectarPuertoSerial9600() throws Exception {
        String nombrePuertoSerial = UtilPropiedades.cargarPropiedad("PuertoSonometro", "propiedades.properties");
        SerialPort puertoSerial = PortSerialUtil.connect(nombrePuertoSerial, 9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        return puertoSerial;
    }
}//end of class
