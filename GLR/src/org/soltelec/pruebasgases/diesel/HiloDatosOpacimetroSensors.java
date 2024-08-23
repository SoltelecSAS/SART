/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.pruebasgases.diesel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Timer;
import org.apache.log4j.Logger;
import org.soltelec.procesosopacimetro.HiloTomaDatosGenerico;
import org.soltelec.procesosopacimetro.Opacimetro;
import org.soltelec.procesosopacimetro.OpacimetroSensors;

/**
 *
 * @author DANIELA SANA
 */
public class HiloDatosOpacimetroSensors extends HiloTomaDatosGenerico{


    private int numeroArreglo;
    private int numeroCiclo;
    //private ArrayList<ArregloOpacidadCiclo> listaDatos = new ArrayList<ArregloOpacidadCiclo>();
    private Timer timer;
    private int contadorTemporizacion = 0;
    private volatile boolean cicloTerminado;
    private List<Byte> listaOpacidad;

    public HiloDatosOpacimetroSensors(Opacimetro opacimetro) {
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
        OpacimetroSensors opacimetroSensors = (OpacimetroSensors)opacimetro;
        timer.start();//inicio de la temporizacion
        contadorTemporizacion = 0;
        System.err.println("Toma de datos ciclo: " + numeroCiclo);
        opacimetroSensors.armarLCS();//no se sabe si tienen exactamente los mismos comandos por tanto este es fuertemente acoplado
        //Segun el manual un segundo despues se debe disparar el trigger
        dormir(800);
        opacimetroSensors.dispararTrigger();
        System.out.println("Opacimetro disparado");
        //Sabiendo que SIEMPRE la recolección dura 10 segundos minimo
        //poner a esperar al
        while(contadorTemporizacion <= 19){//cuando hayan transcurrido 9.5 segundos
            dormir(500);
        }
        //Preguntar durante un segundo si la recoleccion ha terminado
        while(opacimetroSensors.obtenerDatos().isRecoleccionDisparada() && contadorTemporizacion < 21){
            dormir(100);
        }
        //Suponemos que en este momento la recoleccion ha terminado
        byte[] datosOpacidad = opacimetroSensors.obtenerArregloDatosOpacidad();

        //pasar esos datos a una lista de opacidad
        for(byte b: datosOpacidad) {
            listaOpacidad.add(b);
        }
        //Iniciar una recolección nueva
        opacimetroSensors.armarLCS();
        //Segun el manual un segundo despues se debe disparar el trigger
        dormir(800);
        opacimetroSensors.dispararTrigger();
        //Cuando termine el ciclo  o pasen otros diez segundos detener la recoleccion
        while(!cicloTerminado &&   (contadorTemporizacion<=40 && opacimetroSensors.obtenerDatos().isRecoleccionDisparada()) ){
                    System.out.println("Ciclo esperar a que termine de disparar");
                    dormir(50);
        }
         //juntar los datos de las recolecciones y meterlos en una lista o arreglo
        timer.stop();
        if(opacimetroSensors.obtenerDatos().isRecoleccionDisparada()){
                System.err.println("Hilo interrumpido recolectando datos");
                opacimetroSensors.desarmarTrigger();
                int n =opacimetroSensors.numeroDatosAdquiridos();
                System.err.println("#Datos obtenidos: " + n);
                datosOpacidad = null;
                datosOpacidad = opacimetroSensors.obtenerArregloDatosOpacidad();
                byte[] datosCortados = new byte[2*n];
                System.arraycopy(datosOpacidad,0, datosCortados, 0,2*n);
                System.err.println("Añadiendo datos:"+ "#Arreglo: " +numeroArreglo +"#Ciclo: " +numeroCiclo);
                for(byte b: datosCortados) {
                    listaOpacidad.add(b);
                }

        } else {//PROVISIONALMENTE NO SE PERMITE ESTA OPCION, el try catch es el que la impide

                datosOpacidad = opacimetroSensors.obtenerArregloDatosOpacidad();
                System.err.println("Añadiendo datos:"+ "#Arreglo: " +numeroArreglo +"#Ciclo: " +numeroCiclo);
                for(byte b:datosOpacidad){
                    listaOpacidad.add(b);
                }
              

       }

       return listaOpacidad;
    }

    public void setCicloTerminado(boolean cicloTerminado) {
        this.cicloTerminado = cicloTerminado;
    }

    
     private void dormir(int tiempo){
        try {
            Thread.sleep(tiempo);
        } catch (InterruptedException ex) {

            System.out.println("---interrumpido mientras duerme");
        }
    }//end of method dormir

         

}
