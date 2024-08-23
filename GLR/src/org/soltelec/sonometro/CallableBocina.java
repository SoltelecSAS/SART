/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.sonometro;

//import com.soltelec.estandar.EstadoEventosSicov;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import org.apache.log4j.Logger;
import org.soltelec.sonometro.indutesa.ConsumidorPC322A;
import org.soltelec.util.UtilSicov;

/**
 *Clase para tomar las tres mediciones de la bocina
 * esta inicia las mediciones de todas las otras.
 * @author Usuario
 */
public class CallableBocina implements Callable<List<Double>>{
    
    private double pito1,pito2,pito3;
    private final PanelPruebaSonometro panel;
    String urljdbc,puertoSonometro;
    private final Timer timer;
    private final Sonometro sonometro;
    private int contadorTemporizacion;
    private int UMBRAL_DECIBELES = 10;
    private final Long idUsuario,idPrueba,idHojaPrueba;

    public CallableBocina(PanelPruebaSonometro panel,long usuario,long prueba,long hojaPrueba,Sonometro sonometro) {
        this.panel = panel;
        this.idUsuario = usuario;
        this.idPrueba = prueba;
        this.idHojaPrueba = hojaPrueba;
        timer = new Timer(1000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        contadorTemporizacion++;
                    }
        });
        this.sonometro = sonometro;
    }//end of constructor



    @Override
    public List<Double> call() throws Exception {
        
        try {
                    
            panel.getProgressBar().setVisible(false);
            cargarParametros();
            //inicializarPuertoSerial(puertoSonometro);//inicia la adquisición de datos
            if(sonometro instanceof ConsumidorHtech){
                UMBRAL_DECIBELES = 10;
                
            } else if (sonometro instanceof ConsumidorPC322A){
                UMBRAL_DECIBELES = 20;
            }
           
            panel.getPanelMensaje().setText("Inicio de la prueba Calibrando cero de \n ruido ambiente");
            Thread.sleep(500);
        
            timer.start();
            panel.getButtonFinalizar().setVisible(true);
            List<Double> listaAmbiente = new ArrayList<>();
            double valorDb= 0.0;
            while(contadorTemporizacion < 10){
                Thread.sleep(250);
                valorDb = sonometro.getDecibeles();
                listaAmbiente.add(valorDb);
                panel.getPanelMensaje().setText("Calibrando Cero t:" + contadorTemporizacion);
            }//end while

            double media = 0;
            for(Double d : listaAmbiente){
               media+= d;
            }
            if(listaAmbiente.size() > 0){
                media /= listaAmbiente.size();
                System.out.println("Media " + media);
            }
            if (media < 0){//TODO liberarRecursos
               JOptionPane.showMessageDialog(panel,"no se tomaron mediciones del sonometro prueba abortada");
               panel.cerrar();
               liberarRecursos();
               //UtilSicov.cargarDatosSicov(idPrueba.intValue(),EstadoEventosSicov.ABORTADO,"no se tomaron mediciones del sonometro prueba abortada");
               return null;
               
            }
            boolean blink = false;//variable para hacer que el textfield haga blink
            int contador = 0;
            while(sonometro.getDecibeles() < media + UMBRAL_DECIBELES){
                if(blink && contador%2 == 0){
                    panel.getPanelMensaje().setText("ACCIONE LA BOCINA  # 1");
                    blink = false;
                }
                else{
                    panel.getPanelMensaje().setText("ACCIONE LA BOCINA  # 1 \n...");
                    blink = true;
                }
               Thread.sleep(250);
               contador++;
           }
           pito1 = sonometro.getDecibeles();
           Thread.sleep(1000);
           while(sonometro.getDecibeles() >= media + UMBRAL_DECIBELES){
                if(blink && contador%2==0){
                    panel.getPanelMensaje().setText("DISMINUIR RUIDO MEDICION TOMADA");
                    blink = false;
               }else {
                   blink = true;
                   panel.getPanelMensaje().setText("DISMINUIR RUIDO MEDICION TOMADA....");
               }
               Thread.sleep(250);
               contador++;
           }

           while(sonometro.getDecibeles() < media + UMBRAL_DECIBELES){
               if(blink && contador%2 == 0){
                panel.getPanelMensaje().setText("ACCIONE LA BOCINA  # 2");
                blink = false;
               }
               else{
                panel.getPanelMensaje().setText("ACCIONE LA BOCINA  # 2\n...");
                blink = true;
               }
               Thread.sleep(250);
               contador++;
           }
           pito2 = sonometro.getDecibeles();
           Thread.sleep(1000);
           while(sonometro.getDecibeles() >= media + UMBRAL_DECIBELES){
               if(blink && contador%2==0){
                    panel.getPanelMensaje().setText("DISMINUIR RUIDO MEDICION TOMADA");
                    blink = false;
               }else {
                   blink = true;
                   panel.getPanelMensaje().setText("DISMINUIR RUIDO MEDICION TOMADA....");
               }
               Thread.sleep(250);
               contador++;
           }

           while(sonometro.getDecibeles() < media + UMBRAL_DECIBELES){
               if(blink && contador%2 == 0){
                    panel.getPanelMensaje().setText("ACCIONE LA BOCINA  # 3");
                    blink = false;
               }
               else{
                    panel.getPanelMensaje().setText("ACCIONE LA BOCINA  # 3\n...");
                    blink = true;
               }
               Thread.sleep(250);
               contador++;
           }
           pito3 = sonometro.getDecibeles();
           Thread.sleep(1000);
           while(sonometro.getDecibeles() >= media + UMBRAL_DECIBELES){
               if(blink && contador%2==0){
                panel.getPanelMensaje().setText("DISMINUIR RUIDO MEDICION TOMADA");
                blink = false;
               }else {
                   blink = true;
                   panel.getPanelMensaje().setText("DISMINUIR RUIDO MEDICION TOMADA....");
               }
               Thread.sleep(250);
               contador++;
           }
           timer.stop();
           panel.getPanelMensaje().setText("SELECCIONE DISPOSITIVO MEDICION RPM");

           final List<Double> listaBocina = new ArrayList<>();
           listaBocina.add(pito1);
           listaBocina.add(pito2);
           listaBocina.add(pito3);
           panel.getButtonRpm().setVisible(true);
           panel.getButtonRpm().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
//                        WorkerSonometroMotor wsm = new WorkerSonometroMotor(panel,listaBocina,sonometro,idUsuario,idHojaPrueba,idPrueba);
//                        wsm.execute();
                }
           });
           
           panel.getButtonRpm().doClick();
           return listaBocina;
        }catch (InterruptedException iexc){//si este hilo ha sido interrumpido
             liberarRecursos();
             throw new CancellationException("Prueba de sonometro cancelada");
        }//end of catch
        catch(Exception exc){
            liberarRecursos();
            throw exc;
        }
           
    }//end of method call

    /**
     * Carga el nombre del puerto del Sonometro
     * y carga la direccion ip del servidor
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void cargarParametros() throws FileNotFoundException, IOException {
         Properties props = new Properties();
         //try retrieve data from file
         props.load(new FileInputStream("./propiedades.properties"));//TODO warining
         urljdbc = props.getProperty("urljdbc");
         puertoSonometro = props.getProperty("PuertoSonometro");
         System.out.println("Direccion del servidor: " +urljdbc);
         System.out.println("Puerto del sonometro:" +puertoSonometro);
     }//end of method cargarUrl

    
///**
// * Hace la conexion y retorna el puerto serial
// * Inicia el hilo productor (lectorHTech) y el consumidor de los datos del sonometro
// * consumidor(Htech)
// * @param portName
// * @param baudRate
// * @param dataBits
// * @param stopBits
// * @param parity
// * @return
// * @throws Exception
// */
//private SerialPort connect ( String portName, int baudRate, int dataBits, int stopBits,int parity ) throws Exception
//    {
//
//        SerialPort serialPort = null;
//        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName) ;
//        if ( portIdentifier.isCurrentlyOwned() )
//        {
//            System.out.println("Error: Port is currently in use");
//        }
//        else
//        {
//            CommPort commPort = portIdentifier.open(this.getClass().getName(),2000);
//
//            if ( commPort instanceof SerialPort )
//            {
//                serialPort = (SerialPort) commPort;
//
//
//                //serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
//                serialPort.setSerialPortParams(2400, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,SerialPort.PARITY_NONE );
//                serialPort.setDTR(false);
//                serialPort.setRTS(false);
//                serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN);
//                InputStream in = serialPort.getInputStream();
//                OutputStream out = serialPort.getOutputStream();
//                //serialPort.addEventListener(new ProductorCapelec(in,bufferMed));
//                //serialPort.notifyOnDataAvailable(true);
//                bufferMed = new BufferStrings();
//                lectorHTech = new LectorHTech(in, bufferMed);
//                consumidorHTech = new ConsumidorHtech(bufferMed,lectorHTech);
//                threadProductor = new Thread(lectorHTech);
//                threadProductor.start();
//                threadConsumidor = new Thread(consumidorHTech);
//                threadConsumidor.start();
//            }
//            else
//            {
//                System.out.println("Error: Only serial ports are handled by this example.");
//            }
//        }
//        return serialPort;
//    }//end of method connect


//private void iniciarHilosSonometro() throws UnsupportedCommOperationException, IOException{
//    
//    //serialPort.setDTR(false);
//    //serialPort.setRTS(false);
//    //serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN);
//    //23 sept 2012 lo cambie para revisar una cuestion de rxtx
//    serialPort.setDTR(true);
//    serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN
//                 | SerialPort.FLOWCONTROL_RTSCTS_OUT);
//    serialPort.setRTS(true);
//    InputStream in = serialPort.getInputStream();
//    OutputStream out = serialPort.getOutputStream();
//                //serialPort.addEventListener(new ProductorCapelec(in,bufferMed));
//                //serialPort.notifyOnDataAvailable(true);
//    bufferMed = new BufferStrings();
//    lectorHTech = new LectorHTech(in, bufferMed);
//    consumidorHTech = new ConsumidorHtech(bufferMed,lectorHTech);
//    threadProductor = new Thread(lectorHTech);
//    threadProductor.start();
//    threadConsumidor = new Thread(consumidorHTech);
//    threadConsumidor.start();
//    
//    
//}

    private void liberarRecursos() {
        
        
        try{
            if (timer != null){
                timer.stop();
            }
            
            sonometro.stop();
            
            
        } catch (Exception exc){//en este momento no importa la excepcion que se cause
            Logger.getRootLogger().error("Error liberando recursos prueba de sonometro", exc);
        }     

    }//end of method liberarRecursos


}//end of class
