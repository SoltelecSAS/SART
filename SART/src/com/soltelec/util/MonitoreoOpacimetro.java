/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.soltelec.util;

import com.soltelec.igrafica.*;
import com.soltelec.util.*;
import com.soltelec.opacimetro.brianbee.OpacimetroBrianBee;
import gnu.io.SerialPort;
import javax.swing.JOptionPane;
import org.soltelec.procesosopacimetro.Opacimetro;
import org.soltelec.procesosopacimetro.OpacimetroSensors;
import org.soltelec.pruebasgases.diesel.WorkerCiclosDiesel;
import org.soltelec.util.MedicionOpacidad;
import org.soltelec.util.PortSerialUtil;
import Utilidades.UtilPropiedades;
import org.apache.log4j.Logger;
import org.soltelec.util.capelec.OpacimetroCapelec;

/**
 *
 * @author user
 */
public class MonitoreoOpacimetro implements Runnable{ 
    private String name; 
    private Double vlOpa=0.9;
     private MedicionOpacidad med;
     String nombreUsuario = "";
    public MonitoreoOpacimetro() {
        
    }
     
    public String getName() {
        return name;
    }
    public Double getVlOpa() {
        return this.vlOpa;
    }
    @Override
    public void run() {
        try {
            if(WorkerCiclosDiesel.ipEquipo.length()>3) {
                 this.vlOpa =-1.0;
               return ;
            }
            Opacimetro opacimetro = null;
            SerialPort puerto = null;
            String marcaOpacimetro = UtilPropiedades.cargarPropiedad("MarcaOpacimetro", "propiedades.properties");//VERIFICA MARCA DEL OPACIMETRO
            System.out.println("marcaOpacimetro: "+ marcaOpacimetro);
            
            System.out.println("take marca "+marcaOpacimetro);
            if (marcaOpacimetro == null || marcaOpacimetro.equals("Sensors")) {//VALIDA LA MARCA DEL SENSOR PARA TOMAR ASI LA MEDIDA DE LA OPACIDAD
                opacimetro = new OpacimetroSensors();
                System.out.println("Opacimetro : "+opacimetro.toString());
            } else if (marcaOpacimetro.equals("Capelec")) {
                opacimetro = new OpacimetroCapelec();
            } else if (marcaOpacimetro.equalsIgnoreCase("BrianBee")) {
                opacimetro = new OpacimetroBrianBee();
            }/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            String nombrePuerto = UtilPropiedades.cargarPropiedad("PuertoOpacimetro", "propiedades.properties");
            System.out.println("Puerto en donde esta el opacimetro :"+nombrePuerto);
            if (marcaOpacimetro.equals("Capelec")) {
                puerto = PortSerialUtil.connect(nombrePuerto, 19200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);//PUERTO POR DONDE RECIBE LOS DATOS DEL OPACIMETRO
                System.out.println("puerto de recepcion de datos :" + puerto);
            } else {
                try{
                puerto = PortSerialUtil.connect(nombrePuerto, 9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);//PUERTO POR DONDE RECIBE LOS DATOS DEL OPACIMETRO
                System.out.println("puerto de recepcion de datos :" + puerto);
                }catch(Exception l){
                    System.out.println("verificar archivo de propiedades que la variable Estabilizacion este en false o que el puerto com este correcto en el archivo propiedades ");
                    
                }
            }
            opacimetro.setPort(puerto);//ENVIA EL PUERTO AL OPACIMETRO
            med = opacimetro.obtenerDatos();//OBTIENE LOS DATOS DE LA OPACIDAD
            Double ev= med.getOpacidadDouble();
            System.out.println("valor opacidad: "+ev);
            if(ev>1.0)//SI LA OPACIDAD ES MAYOR A 0 ENVIA EL ERROR 
            {
                JOptionPane.showMessageDialog(null, "validacion del opacimetro terminada, valor de la opacidad: "+ ev +", la opacidad es mayor a 1.0, por favor realice calibración del dispositivo o consulte con soporte tecnico.");  
                //Logger.getLogger("opacimetro").info("se realiza validacion del opacimetro, valor de la opacidad :");
                Logger.getLogger("igrafica").info("Se realiza validacion del opacidad, valor obtenido:" + String.valueOf(ev) + ", se le indica al usuario que debe realizar calibracion o comunicarse con  soporte tecnico." );
            }else{//SI ES MENOR A 0.0 DEJA QUE CONTINUE NORMAL
                 JOptionPane.showMessageDialog(null, "validacion del opacimetro terminada, valor de la opacidad actual: "+ ev);  
                Logger.getLogger("igrafica").info("Se realiza validacion del opacidad, valor obtenido: " + String.valueOf(ev) + ", no se requieren acciones." );
            }
            this.vlOpa = ev;
            puerto.close();//CIERRA EL PUERTO
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

