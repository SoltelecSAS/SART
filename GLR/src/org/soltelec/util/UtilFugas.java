/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.util;

import com.soltelec.opacimetro.brianbee.OpacimetroBrianBee;
import gnu.io.SerialPort;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.soltelec.horiba.BancoHoriba;
import org.soltelec.procesosbanco.BancoGasolina;
import org.soltelec.procesosopacimetro.Opacimetro;
import org.soltelec.procesosbanco.BancoSensors;
import org.soltelec.procesosopacimetro.OpacimetroSensors;
import org.soltelec.util.capelec.BancoCapelec;
import org.soltelec.util.capelec.OpacimetroCapelec;

/**
 *
 * @author GerenciaDesarrollo
 */
public class UtilFugas {

    public static String obtenerSerialBanco() throws FileNotFoundException, IOException, Exception
    {
         String strSerial="";
        BancoGasolina banco = null;
        String marcaBanco = UtilPropiedades.cargarPropiedad("MarcaBanco", "propiedades.properties");
        if (marcaBanco == null || marcaBanco.equals("Sensors"))
        {
            banco = new BancoSensors();
        } else if (marcaBanco.equals("Capelec")) {
            banco = new BancoCapelec();
        } else if (marcaBanco.equals("Horiba")) {
            banco = new BancoHoriba();
        }
        //TODO poner las otras marcas de banco
      System.out.println("La Marca del banco es " + marcaBanco);
        String nombrePuerto = UtilPropiedades.cargarPropiedad("PuertoBanco", "propiedades.properties");
      
            SerialPort puerto = PortSerialUtil.connect(nombrePuerto, 9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            if(puerto !=null){
            banco.setPuertoSerial(puerto);
            strSerial = banco.numeroSerial();
            banco.getPuertoSerial().close();
            }else
            {
                strSerial= "";
            }
            
       
       return strSerial;
        //int numeroSerial = Integer.parseInt(strSerial);

        
    }

    public static String obtenerPEFBanco() throws FileNotFoundException, IOException, Exception {

        BancoGasolina banco = null;

        String marcaBanco = UtilPropiedades.cargarPropiedad("MarcaBanco", "propiedades.properties");
        if (marcaBanco == null || marcaBanco.equals("Sensors")) {
            banco = new BancoSensors();
        } else if (marcaBanco.equals("Capelec")) {
            banco = new BancoCapelec();
        } else if (marcaBanco.equals("Horiba")) {
            banco = new BancoHoriba();
        }
        //TODO poner las otras marcas de banco

        String nombrePuerto = UtilPropiedades.cargarPropiedad("PuertoBanco", "propiedades.properties");
        SerialPort puerto = PortSerialUtil.connect(nombrePuerto, 9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        banco.setPuertoSerial(puerto);

        String strPEF = banco.obtenerPEF();
        banco.getPuertoSerial().close();
        //int numeroSerial = Integer.parseInt(strSerial);

        return strPEF;

    }

    public static BancoGasolina obtenerBancoConectado() throws Exception {

        BancoGasolina banco = null;

        String marcaBanco = UtilPropiedades.cargarPropiedad("MarcaBanco", "propiedades.properties");
        if (marcaBanco == null || marcaBanco.equals("Sensors")) {
            banco = new BancoSensors();
        } else if (marcaBanco.equals("Capelec")) {
            banco = new BancoCapelec();
        } else if (marcaBanco.equals("Horiba")) {
            banco = new BancoHoriba();
        }
        //TODO poner las otras marcas de banco

        String nombrePuerto = UtilPropiedades.cargarPropiedad("PuertoBanco", "propiedades.properties");
        SerialPort puerto = PortSerialUtil.connect(nombrePuerto, 9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        banco.setPuertoSerial(puerto);

        return banco;
    }

    public static long obtenerSerialOpacimetro() throws FileNotFoundException, IOException, Exception {
        Opacimetro opacimetro = null;
        SerialPort puerto = null;
        try {
            String marcaOpacimetro = UtilPropiedades.cargarPropiedad("MarcaOpacimetro", "propiedades.properties");
            if (marcaOpacimetro == null || marcaOpacimetro.equals("Sensors")) {
                opacimetro = new OpacimetroSensors();
            } else if (marcaOpacimetro.equals("Capelec")) {
                opacimetro = new OpacimetroCapelec();
            } else if (marcaOpacimetro.equalsIgnoreCase("BrianBee")) {
                opacimetro = new OpacimetroBrianBee();
            } 
            String nombrePuerto = UtilPropiedades.cargarPropiedad("PuertoOpacimetro", "propiedades.properties");
             if (marcaOpacimetro.equals("Capelec")) {
                  puerto = PortSerialUtil.connect(nombrePuerto, 19200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
             }else{
                  puerto = PortSerialUtil.connect(nombrePuerto, 9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE); 
            }      
            opacimetro.setPort(puerto);
            
        } catch (Exception e) { }
        long strSerial = opacimetro.obtenerSerial();
        opacimetro.getPort().close();
        //int numeroSerial = Integer.parseInt(strSerial);
        return strSerial;
    }
    public static long monitoreoOpacimetro() throws FileNotFoundException, IOException, Exception {
        Opacimetro opacimetro = null;
        SerialPort puerto = null;
        try {
            String marcaOpacimetro = UtilPropiedades.cargarPropiedad("MarcaOpacimetro", "propiedades.properties");
            if (marcaOpacimetro == null || marcaOpacimetro.equals("Sensors")) {
                opacimetro = new OpacimetroSensors();
            } else if (marcaOpacimetro.equals("Capelec")) {
                opacimetro = new OpacimetroCapelec();
            } else if (marcaOpacimetro.equalsIgnoreCase("BrianBee")) {
                opacimetro = new OpacimetroBrianBee();
            } 
            String nombrePuerto = UtilPropiedades.cargarPropiedad("PuertoOpacimetro", "propiedades.properties");
             if (marcaOpacimetro.equals("Capelec")) {
                  puerto = PortSerialUtil.connect(nombrePuerto, 19200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
             }else{
                 puerto = PortSerialUtil.connect(nombrePuerto, 9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE); 
            }      
            opacimetro.setPort(puerto);
            
        } catch (Exception e) { }
          long strSerial = opacimetro.obtenerSerial();
          opacimetro.getPort().close();
        //int numeroSerial = Integer.parseInt(strSerial);
        return strSerial;
    }

}
