/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.util.capelec;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Timer;
import org.soltelec.procesosopacimetro.HiloTomaDatosGenerico;
import org.soltelec.procesosopacimetro.Opacimetro;
import org.soltelec.procesosopacimetro.OpacimetroSensors;

/**
 *
 * @author User
 */
public class HiloDatosOpacimetroCapelec extends HiloTomaDatosGenerico{

    private Timer timer;
    private int contadorTemporizacion = 0;
    private List<Byte> listaOpacidad;
    private volatile boolean cicloTerminado = false; 
    
    public HiloDatosOpacimetroCapelec(Opacimetro opacimetro) {
        super(opacimetro);
        timer = new Timer(500, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                contadorTemporizacion++;
            }
        });
        listaOpacidad = new ArrayList<Byte>();
    }
    
    
     
    @Override
    public List<Byte> call() throws Exception {
        
    
        timer.start();//inicio de la temporizacion
        contadorTemporizacion = 0;
        opacimetro.armarLCS();//no se sabe si tienen exactamente los mismos comandos por tanto este es fuertemente acoplado
        //Segun el manual un segundo despues se debe disparar el trigger
        dormir(900);
        opacimetro.dispararTrigger();
        System.out.println("Opacimetro disparado");
        //Sabiendo que SIEMPRE la recolección dura 10 segundos minimo
        //poner a esperar al
        while(contadorTemporizacion <= 19){//cuando hayan transcurrido 9.5 segundos
            dormir(500);
        }
        //Preguntar durante un segundo si la recoleccion ha terminado
        while(opacimetro.obtenerDatos().isTriggerActivado() && contadorTemporizacion < 21){
            dormir(100);
        }
        //Suponemos que en este momento la recoleccion ha terminado
        byte[] datosOpacidad = opacimetro.obtenerArregloDatosOpacidad();

        //pasar esos datos a una lista de opacidad
        for(byte b: datosOpacidad)
            listaOpacidad.add(b);
        //Iniciar una recolección nueva
        opacimetro.armarLCS();
        //Segun el manual un segundo despues se debe disparar el trigger
        dormir(900);
        opacimetro.dispararTrigger();
        //Cuando termine el ciclo  o pasen otros diez segundos detener la recoleccion
        while(!cicloTerminado &&   (contadorTemporizacion<=40 && opacimetro.obtenerDatos().isTriggerActivado()) ){
                    System.out.println("Ciclo esperar a que termine de disparar");
                    dormir(50);
        }
         //juntar los datos de las recolecciones y meterlos en una lista o arreglo
        timer.stop();
        if(opacimetro.obtenerDatos().isTriggerActivado()){
                System.err.println("Hilo interrumpido recolectando datos");
                //opacimetroSensors.desarmarTrigger();
                int n =opacimetro.numeroDatosAdquiridos();
                System.err.println("#Datos obtenidos: " + n);
                byte[] datosRealTime = null;
                datosRealTime = opacimetro.datosRTTrigger(0, n);
                
                for(byte op: datosRealTime)
                    listaOpacidad.add(op);

        } else {
                datosOpacidad = null;
                datosOpacidad = opacimetro.obtenerArregloDatosOpacidad();
                for(byte b:datosOpacidad)
                    listaOpacidad.add(b);

       }

       return listaOpacidad;
        
    }
    
     private void dormir(int tiempo){
        try {
            Thread.sleep(tiempo);
        } catch (InterruptedException ex) {

            System.out.println("---interrumpido mientras duerme");
        }
    }//end of method dormir

    public void setCicloTerminado(boolean cicloTerminado) {
        this.cicloTerminado = cicloTerminado;
    }
     
     
}
