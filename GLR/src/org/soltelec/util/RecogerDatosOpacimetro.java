/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.util;


import com.soltelec.opacimetro.brianbee.OpacimetroBrianBee;
import gnu.io.SerialPort;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import javax.swing.JOptionPane;
import org.soltelec.procesosbanco.ProcesoRuido;
import org.soltelec.procesosopacimetro.Opacimetro;
import org.soltelec.procesosopacimetro.OpacimetroSensors;
import org.soltelec.procesosopacimetro.WorkerDatosOpacimetro;
import org.soltelec.pruebasgases.diesel.WorkerCiclosDiesel;
import org.soltelec.util.MedicionOpacidad;
import org.soltelec.util.PortSerialUtil;
import org.soltelec.util.UtilPropiedades;
import org.soltelec.util.capelec.OpacimetroCapelec;

/**
 *
 * @author user
 */
public class RecogerDatosOpacimetro implements Runnable{ 
    private String name; 
    private Double vlOpa=0.9;
    private MedicionOpacidad med;
    public  static int finRecogida=0;   
    public Opacimetro opacimetro;
    private String serialOpacimetro;
    private ScheduledExecutorService executor;
    WorkerDatosOpacimetro workerDatos;
    byte[] datosOpacidad ;
    protected List<Byte> listaOpacidad;
    public  static ArrayList<Double> listaDoubleCiclo;
    
    public RecogerDatosOpacimetro(Opacimetro opacimetro,ScheduledExecutorService executor,WorkerDatosOpacimetro workerDatos,String serialOpacimetro) {
        this.opacimetro=opacimetro;       
        this.serialOpacimetro=serialOpacimetro;
        RecogerDatosOpacimetro.listaDoubleCiclo=new ArrayList();
        this.executor=executor;
        this.workerDatos=workerDatos;
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
            System.out.println("set Puerto");
            
            //Iniciar una recolección nueva
            opacimetro.armarLCS();
            //Segun el manual un segundo despues se debe disparar el trigger
             Thread.sleep(800);
            
            opacimetro.dispararTrigger();
            
            
            //med.setTiempoRecogidaDatos(tiempoRecDatos);            
                       
             finRecogida++;     
             if (finRecogida == 50) {
                 opacimetro.desarmarTrigger();
                 datosOpacidad = opacimetro.obtenerArregloDatosOpacidad();
                 for(byte b: datosOpacidad)
                     listaOpacidad.add(b);
                 for (int i = 0; i < listaOpacidad.size() / 2; i++) {
                     listaDoubleCiclo.add((new Integer(UtilGasesModelo.fromBytesToShort(new byte[]{listaOpacidad.get(2 * i + 1), listaOpacidad.get(2 * i)}))) * 0.1);
                 }
               //  listaDatosRecogidos.add(med); 
                 finRecogida=0;
                 executor.shutdown();
                  JOptionPane.showMessageDialog(null, "HE FINALIZADO RECOGER LOS DATOS OPACIMETRO ");
                    workerDatos.setInterrumpido(false);
                   ProcesoRuido proceso = new ProcesoRuido(this.serialOpacimetro);          
                   proceso.servWriteDatosFile(RecogerDatosOpacimetro.listaDoubleCiclo);
                JOptionPane.showMessageDialog(null, "HE FINALIZADO ESCRIBIR LA MUESTRA DEL OPACIMETRO EN UN ARCHIVO ");
            } 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

