/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.sonometro;

//import com.soltelec.estandar.EstadoEventosSicov;
import gnu.io.SerialPort;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import org.apache.log4j.Logger;
import org.soltelec.pruebasgases.PanelPruebaGases;
import org.soltelec.sonometro.gyp.ConsumidorH55633T;
import org.soltelec.sonometro.gyp.ProductorH55633T;
import org.soltelec.sonometro.indutesa.ConsumidorPC322A;
import org.soltelec.sonometro.indutesa.ProductorSonometroPC322A;
import org.soltelec.util.BufferStrings;
import org.soltelec.util.PortSerialUtil;
import org.soltelec.util.UtilPropiedades;
import org.soltelec.util.UtilSicov;

/**
 * Llama al hilo de la bocina
 *
 * @author Usuario
 */
public class WorkerSonometroBocina extends SwingWorker<Void, Void> {

    private PanelPruebaSonometro panel;
    private Long idUsuario, idHojaPrueba, idPrueba;

    public WorkerSonometroBocina(PanelPruebaSonometro panel, long usuario, long hojaPrueba, long prueba) {
        this.panel = panel;
        this.idPrueba = prueba;
        this.idUsuario = usuario;
        this.idHojaPrueba = hojaPrueba;
    }

    @Override
    protected Void doInBackground() throws Exception {
        //Tengo una referencia a panel, con lo cual lo puedo cerrar.
        try {
            ExecutorService executor = Executors.newCachedThreadPool();

            //Leer la marca del sonometro
            Sonometro sonometro = null;
            //Iniciar los hilos del sonometro dependiendo de la marca
            String strMarcaSonometro = UtilPropiedades.cargarPropiedad("MarcaSonometro", "propiedades.properties");

            if (strMarcaSonometro == null || strMarcaSonometro.equals(Sonometro.XTECH)) {

                SerialPort puertoSerial = conectarPuertoSerialSonometro();
                //Iniciar el hilo del sonometro 
                puertoSerial.setDTR(true);
                puertoSerial.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN
                        | SerialPort.FLOWCONTROL_RTSCTS_OUT);
                puertoSerial.setRTS(true);
                InputStream in = puertoSerial.getInputStream();
                OutputStream out = puertoSerial.getOutputStream();
                //serialPort.addEventListener(new ProductorCapelec(in,bufferMed));
                //serialPort.notifyOnDataAvailable(true);
                BufferStrings bufferMed = new BufferStrings();
                LectorHTech lectorHTech = new LectorHTech(puertoSerial, bufferMed);
                ConsumidorHtech consumidorHTech = new ConsumidorHtech(bufferMed, lectorHTech);
                Thread threadProductor = new Thread(lectorHTech);
                threadProductor.start();
                Thread threadConsumidor = new Thread(consumidorHTech);
                threadConsumidor.start();
                sonometro = consumidorHTech;//polimorfismo

            } else if (strMarcaSonometro.equals(Sonometro.PCE322A)) {

                SerialPort puertoSerial = conectarPuertoSerial9600();

                BlockingQueue<List<Integer>> lista = new ArrayBlockingQueue<>(100);

                ProductorSonometroPC322A productor = new ProductorSonometroPC322A(puertoSerial, lista);
                ConsumidorPC322A consumidor = new ConsumidorPC322A(lista, productor);

                executor.submit(productor);
                executor.submit(consumidor);

                sonometro = consumidor;
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

                executor.submit(productor);
                executor.submit(consumidor);

                sonometro = consumidor;
                System.out.println("Sonometro H5563T iniciado");
            }
            UtilSicov.cargarDatosSicov(idPrueba.intValue());

            Future<List<Double>> futureBocina = executor.submit(new CallableBocina(panel, idUsuario, idPrueba, idHojaPrueba, sonometro));//CallableBocina toma el ruido del motor
            panel.getButtonFinalizar().addActionListener(new ListenerCancelacionSonometro(futureBocina, idPrueba, panel, sonometro,idUsuario));

            List<Double> listaPito = futureBocina.get();//Hacer la prueba normal

        }//end of try
        catch (ExecutionException exec) {
            JOptionPane.showMessageDialog(panel, "Error haciendo la prueba" + exec.getCause());
            exec.printStackTrace();
            Logger.getRootLogger().error("Error durante prueba de sonometro: " + idPrueba, exec);
            panel.cerrar();
            //UtilSicov.cargarDatosSicov(idPrueba.intValue(),EstadoEventosSicov.ABORTADO);
        } catch (CancellationException ce) {
            JOptionPane.showMessageDialog(panel, "Cancelada");
            Logger.getLogger("igrafica").info("Prueba de sonometro cancelada:" + idPrueba);
            //panel.cerrar();            
            //UtilSicov.cargarDatosSicov(idPrueba.intValue(),EstadoEventosSicov.CANCELADO);
        } catch (Exception exc) {
            Logger.getRootLogger().error("Error con el sonometro", exc);
            JOptionPane.showMessageDialog(null, "Error con el sonometro" + exc.getMessage());
            panel.cerrar();
           // UtilSicov.cargarDatosSicov(idPrueba.intValue());

        }
        return null;

    }//end of method doInBackground

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
}//end of class WorkerSonometroBocina
