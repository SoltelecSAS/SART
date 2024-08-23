/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.util;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import javax.management.RuntimeErrorException;
import javax.swing.JOptionPane;

/**
 *
 * @author gerenciaDesarrollo
 */
public class PortSerialUtil {

    public static Map<String, CommPortIdentifier> listPorts() {
        Map<String, CommPortIdentifier> nombrePuertoMapa = new HashMap<String, CommPortIdentifier>();
        java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier portIdentifier = portEnum.nextElement();

            String nombrePuerto = portIdentifier.getName() + "-" + getPortTypeName(portIdentifier.getPortType());
            //Solamente poner los puertos seriales
            if (getPortTypeName(portIdentifier.getPortType()).equals("Serial")) {
                nombrePuertoMapa.put(nombrePuerto, portIdentifier);
            }
        }
        return nombrePuertoMapa;
    }

    public static Boolean BusqPuertoEspec(String puerto) {
        Boolean encontrado = false;
        //java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
        java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier portIdentifier = portEnum.nextElement();

            String nombrePuerto = portIdentifier.getName();
            //Solamente poner los puertos seriales
            if (nombrePuerto.equalsIgnoreCase(puerto)) {
                encontrado = true;
            }
        }
        return encontrado;
    }

    static String getPortTypeName(int portType) {
        switch (portType) {
            case CommPortIdentifier.PORT_SERIAL:
                return "Serial";
            case CommPortIdentifier.PORT_PARALLEL:
                return "Parallel";
            default:
                return "unknown type";
        }
    }//end of method

    /**
     * @return A HashSet containing the CommPortIdentifier for all serial ports
     * that are not currently being used.
     */
    public static HashSet<CommPortIdentifier> getAvailableSerialPorts() {
        HashSet<CommPortIdentifier> h = new HashSet<CommPortIdentifier>();
        Enumeration thePorts = CommPortIdentifier.getPortIdentifiers();
        while (thePorts.hasMoreElements()) {
            CommPortIdentifier com = (CommPortIdentifier) thePorts.nextElement();
            switch (com.getPortType()) {
                case CommPortIdentifier.PORT_SERIAL:
                    try {
                    CommPort thePort = com.open("CommUtil", 50);
                    thePort.close();
                    h.add(com);
                } catch (PortInUseException e) {
                    System.out.println("Port, " + com.getName() + ", is in use.");
                    JOptionPane.showMessageDialog(null, "El puerto serial esta ocupado, reinice el programa");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "No se puede abrir el puerto \n Una causa probable es que el numero de puerto \n que esta intentando usar puede no existir\n revise propiedades ");
                    System.err.println("Failed to open port " + com.getName());
                    e.printStackTrace();
                }
            }
        }
        return h;
    }//end of method

    public static SerialPort connectTermo(String portName, int baudRate, int dataBits, int stopBits, int parity) throws NoSuchPortException, Exception {
        SerialPort serialPort = null;
        try {
//            if(serialPort == null){
//                JOptionPane.showMessageDialog(null, "El puerto serial " + portName + " no existe o no se detecta");
//                throw new Exception("Error en puerto");
//            }

            if (portName == null) {
                throw new IllegalArgumentException("Nombre del puerto nulo");
            }
            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
            if (portIdentifier.isCurrentlyOwned()) {
                JOptionPane.showMessageDialog(null, "El puerto Asiganado para El Termohigrometro " + portName + " se encuentra ocupado, Se saldra de la Prueba");
                return serialPort = null;

            } else {
                CommPort commPort = portIdentifier.open(null, 1000);
                if (commPort instanceof SerialPort) {
                    serialPort = (SerialPort) commPort;
                    serialPort.setSerialPortParams(baudRate, dataBits, stopBits, parity);
                    serialPort.enableReceiveTimeout(100);
                } else {
                    System.out.println("Error: Only serial ports are handled by this example.");
                }

            }
        } catch (NoSuchPortException nexc) {
            JOptionPane.showMessageDialog(null, "Disculpe No Puedo Continuar Con la presente prueba debido a que no DETECTO el Termohigrometro");
            serialPort = null;
        }
        return serialPort;

    }

    public static SerialPort connect(String portName, int baudRate, int dataBits, int stopBits, int parity) throws NoSuchPortException, Exception {
        SerialPort serialPort = null;
        try {
            CommPortIdentifier portIdentifier;
//            if(serialPort == null){
//                JOptionPane.showMessageDialog(null, "El puerto serial " + portName + " no existe o no se detecta");
//                throw new Exception("Error en puerto");
//            }

            if (portName == null) {
                throw new IllegalArgumentException("Nombre del puerto nulo");
            }

            portIdentifier = CommPortIdentifier.getPortIdentifier(portName);

            if (portIdentifier.isCurrentlyOwned()) {
                JOptionPane.showMessageDialog(null, "El puerto serial " + portName + " se encuentra ocupado, cierre el programa y valide la configuracion por favor");
                System.out.println("verificar archivo de propiedades que la variable Estabilizacion este en false o que el puerto com este correcto en el archivo propiedades ");
                throw new Exception("Puerto serial  ocupado");

            } else {
                CommPort commPort = portIdentifier.open(null, 1000);
                if (commPort instanceof SerialPort) {
                    serialPort = (SerialPort) commPort;
                    serialPort.setSerialPortParams(baudRate, dataBits, stopBits, parity);
                    serialPort.enableReceiveTimeout(100);
                } else {
                    System.out.println("Error: Only serial ports are handled by this example.");
                }
            }
            return serialPort;
        } catch (NoSuchPortException nexc) {
            JOptionPane.showMessageDialog(null, "El puerto serial " + portName + " no existe o no se detecta");
            System.out.println("verificar archivo de propiedades que la variable Estabilizacion este en false o que el puerto com este correcto en el archivo propiedades ");
            //Mensajes.mostrarExcepcion(nexc);
            serialPort = null;
            //return serialPort;
            throw new RuntimeException(nexc);

        }

    }//end of method connect

}
