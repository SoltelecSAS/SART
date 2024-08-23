/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.sonometro;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.soltelec.util.BufferStrings;

/**
 * Clase para probar las clases lectorHTech y consumidorHTech
 *
 * @author Usuario
 */
public class TestSonometro {

    private SerialPort serialPort;
    private BufferStrings bufferMed;
    private LectorHTech lectorHTech;
    private ConsumidorHtech consumidorHTech;
    private Thread threadProductor;
    private Thread threadConsumidor;

    public TestSonometro() {
        bufferMed = new BufferStrings();
    }

    /**
     * retorna el puerto serial
     *
     * @param portName
     * @throws Exception
     */
    private void inicializarPuertoSerial(String portName) throws Exception {
        serialPort = connect(portName, 2400, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        if (serialPort != null) {
            //textAreaRPM.append("Conexion con" + serialPort.getName());
            System.out.println("Puerto Serial conectado");
        }
        //capelecServicios.setPuertoSerial(serialPort);
        //buttonConectar.setEnabled(false);
    }//end of inicializarPuertoSerial

    /**
     * Este metodo configura o inicializa las variables de instancia
     * serialPort,lectorHTech y consumidorHtech
     *
     * @param portName
     * @param baudRate
     * @param dataBits
     * @param stopBits
     * @param parity
     * @return
     * @throws Exception
     */
    private SerialPort connect(String portName, int baudRate, int dataBits, int stopBits, int parity) throws Exception {
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        if (portIdentifier.isCurrentlyOwned()) {
            System.out.println("Error: Port is currently in use");
        } else {
            CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);

            if (commPort instanceof SerialPort) {
                serialPort = (SerialPort) commPort;

                //serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
                serialPort.setSerialPortParams(2400, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                serialPort.setDTR(false);
                serialPort.setRTS(false);
                serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN);
                InputStream in = serialPort.getInputStream();
                //OutputStream out = serialPort.getOutputStream();
                //serialPort.addEventListener(new ProductorCapelec(in,bufferMed));
                //serialPort.notifyOnDataAvailable(true);
                lectorHTech = new LectorHTech(serialPort, bufferMed);
                consumidorHTech = new ConsumidorHtech(bufferMed, lectorHTech);
                threadProductor = new Thread(lectorHTech);
                threadProductor.start();
                threadConsumidor = new Thread(consumidorHTech);
                threadConsumidor.start();
                Sonometro sonometro = consumidorHTech;
                int i = 0;
                while (i < 500) {

                    Thread.sleep(500);
                    System.out.println("medicion: " + sonometro.getDecibeles());
                    i++;
                }
            } else {
                System.out.println("Error: Only serial ports are handled by this example.");
            }
        }
        return serialPort;
    }//end of method connect

    public static void main(String args[]) {
        TestSonometro testSonometro = new TestSonometro();
        try {
            testSonometro.inicializarPuertoSerial("COM3");
        } catch (Exception ex) {
            Logger.getLogger(TestSonometro.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}//end of class TestSonometro
