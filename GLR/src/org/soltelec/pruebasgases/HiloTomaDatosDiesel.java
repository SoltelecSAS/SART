/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.pruebasgases;

import org.soltelec.procesosopacimetro.Opacimetro;
import org.soltelec.procesosopacimetro.OpacimetroSensors;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;

/**
 *Clase que toma los datos durante los ciclos de aceleracion
 * version 2 11 de Marzo de 2011
 * Fuertemente acoplada al opacimetro sensors
 * @author Usuario
 */
public class HiloTomaDatosDiesel implements Runnable,ActionListener{

    private boolean terminado = false;
    private boolean interrumpido = false;
    private OpacimetroSensors opacimetro;
    private int numeroArreglo;
    private int numeroCiclo;
    //private ArrayList<ArregloOpacidadCiclo> listaDatos = new ArrayList<ArregloOpacidadCiclo>();
    private Timer timer;
    private int contadorTemporizacion = 0;
    private volatile boolean cicloTerminado;
    private List<Byte> listaOpacidad;
    public HiloTomaDatosDiesel(){
        numeroArreglo = 0;
        numeroCiclo = 1;
        timer = new Timer(500,this);
        listaOpacidad = new ArrayList<Byte>();
    }//end of constructor
/**
 * Se aprovecha el hecho que la toma de datos SIEMPRE  es mayor a 10 segundos
 */

    public void run() {
        timer.start();//inicio de la temporizacion
        contadorTemporizacion = 0;
        System.err.println("Toma de datos ciclo: " + numeroCiclo);
        opacimetro.armarLCS();
        //Segun el manual un segundo despues se debe disparar el trigger
        dormir(800);
        opacimetro.dispararTrigger();
        System.out.println("Opacimetro disparado");
        //Sabiendo que SIEMPRE la recolección dura 10 segundos minimo
        //poner a esperar al
        while(contadorTemporizacion <= 19){//cuando hayan transcurrido 9.5 segundos
            dormir(500);
        }
        //Preguntar durante un segundo si la recoleccion ha terminado
        while(opacimetro.obtenerDatos().isRecoleccionDisparada() && contadorTemporizacion < 21){
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
        dormir(800);
        opacimetro.dispararTrigger();
        //Cuando termine el ciclo  o pasen otros diez segundos detener la recoleccion
        while(!cicloTerminado &&   (contadorTemporizacion<=40 && opacimetro.obtenerDatos().isRecoleccionDisparada()) ){
                    System.out.println("Ciclo esperar a que termine de disparar");
                    dormir(50);
        }
         //juntar los datos de las recolecciones y meterlos en una lista o arreglo
        timer.stop();
        if(opacimetro.obtenerDatos().isRecoleccionDisparada()){
                System.err.println("Hilo interrumpido recolectando datos");
                opacimetro.desarmarTrigger();
                int n =opacimetro.numeroDatosAdquiridos();
                System.err.println("#Datos obtenidos: " + n);
                datosOpacidad = null;
                datosOpacidad = opacimetro.obtenerArregloDatosOpacidad();
                byte[] datosCortados = new byte[2*n];
                System.arraycopy(datosOpacidad,0, datosCortados, 0,2*n);
                System.err.println("Añadiendo datos:"+ "#Arreglo: " +numeroArreglo +"#Ciclo: " +numeroCiclo);
                for(byte b: datosCortados)
                    listaOpacidad.add(b);
               
        } else {
                datosOpacidad = null;
                datosOpacidad = opacimetro.obtenerArregloDatosOpacidad();
                System.err.println("Añadiendo datos:"+ "#Arreglo: " +numeroArreglo +"#Ciclo: " +numeroCiclo);
                for(byte b:datosOpacidad)
                    listaOpacidad.add(b);
                
       }       
        
  }//end of method run

    private void dormir(int tiempo){
        try {
            Thread.sleep(tiempo);
        } catch (InterruptedException ex) {
            Logger.getLogger(HiloTomaDatosDiesel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//end of method dormir

    public void actionPerformed(ActionEvent e) {
        contadorTemporizacion++;
        System.out.println("Ciclo: "+ numeroCiclo +"Tiempo transcurrido: " +contadorTemporizacion );
    }

    public boolean isInterrumpido() {
        return interrumpido;
    }

    public void setInterrumpido(boolean interrumpido) {
        this.interrumpido = interrumpido;
    }

    public int getNumeroCiclo() {
        return numeroCiclo;
    }

    public void setNumeroCiclo(int numeroCiclo) {
        this.numeroCiclo = numeroCiclo;
    }

    public OpacimetroSensors getOpacimetro() {
        return opacimetro;
    }

    public void setOpacimetro(OpacimetroSensors opacimetro) {
        this.opacimetro = opacimetro;
    }

    public boolean isTerminado() {
        return terminado;
    }

    public void setTerminado(boolean terminado) {
        this.terminado = terminado;
    }

    public List<Byte> getListaOpacidad() {
        return listaOpacidad;
    }

    public boolean isCicloTerminado() {
        return cicloTerminado;
    }

    public void setCicloTerminado(boolean cicloTerminado) {
        this.cicloTerminado = cicloTerminado;
    }
    
}//end of class HiloTomaDatosDiesel

