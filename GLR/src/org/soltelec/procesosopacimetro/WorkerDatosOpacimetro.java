/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.procesosopacimetro;

import com.soltelec.opacimetro.brianbee.OpacimetroBrianBee;
import org.soltelec.procesosbanco.WorkerDatos;
import org.soltelec.util.LeerArchivo;
import org.soltelec.util.MedicionOpacidad;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;

/**
 *  Hilo que esta continuamente actualizando la interfaz grafica con los datos
 * que obtiene del opacimetro
 * @author GerenciaDesarrollo
 */

public class WorkerDatosOpacimetro extends SwingWorker<Integer,MedicionOpacidad>{
    private boolean interrumpido,terminado;
    private boolean primeraEjecucion = true;
    private boolean opCorregida = true;
    private PanelMedicionStatus panelMedidas;//para actualizar la interfaz cada cierto tiempo
    private Random random = new Random();
    private Opacimetro opacimetro;//hay que ponerselo al instanciarlo
    DecimalFormat df;
    private MedicionOpacidad medicion;
    public WorkerDatosOpacimetro(PanelMedicionStatus panelMedidas, Opacimetro theOpacimetro) {
        this.panelMedidas = panelMedidas;
        this.opacimetro = theOpacimetro;
        df = new DecimalFormat("#.##");
    }//end of constructor



    //Metodo que realiza la tarea principal
    protected Integer doInBackground(){

        boolean repetir = true;
     // medicion = new MedicionOpacidad();
       while(!isTerminado()){
         if(!isInterrumpido()){
           try {
              if(opacimetro!=null){
                  if(repetir){
                      if(opacimetro instanceof OpacimetroBrianBee){
                           System.out.println("Entro en intacia BrianBeee");
                        medicion = opacimetro.obtenerDatos();
                        if(medicion.isStandBy()){                          
                          ((OpacimetroBrianBee)opacimetro).standBy();
                            System.out.println("Stand By ejecutado");
                           
                        }                      
                       repetir = false;
                      }//end of if instanceof
                  }//end of if repetir
                  
                if(isOpCorregida()){
                    medicion = opacimetro.obtenerDatos();
                }
                else {
                    medicion = opacimetro.obtenerDatosEstatusBruto();
                }
                //publish(medicion);
                if(!medicion.isTempTuboFueraTolerancia() ){
                    panelMedidas.getLabelStatus().setText(" Opacimetro Conectado");
                    panelMedidas.getLedTempTubo().setLedOn(false);
                    publish(medicion);
                   }else{// opacimetro calentando
                    panelMedidas.getLedTempTubo().setLedOn(true);
                    panelMedidas.getLabelStatus().setText("OP CALENTANDO :T° Tubo:" + medicion.getStrTemperaturaTubo());
                   }//end else calentamiento
              }//end opacimetro != null
            Thread.sleep(120);
            } catch (InterruptedException ex) {
                Logger.getLogger(WorkerDatos.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{//dormir un ratito
                try {
                    //System.out.println("Proceso interrumpido");
                 Thread.sleep(500);
                } catch (InterruptedException ex) {
                    //Logger.getLogger(WorkerDatosOpacimetro.class.getName()).log(Level.SEVERE, null, ex);
                }
        }
       }
       return 0;
    }//end of method doInBackGround

    protected void publish(MedicionOpacidad valores){

        // CAMBIAR MULTIPLICADOR DE OPACIDAD START

        double factor = LeerArchivo.getFactorOpacidad();

        double ja =valores.getOpacidad()*0.1;
        if((valores.getOpacidad()*0.1)>=91 && valores.getOpacidad()*0.1< 99 ){
             String val = String.valueOf(valores.getOpacidad()*0.1);
               System.out.println("valor Recogido del lente 100 % is "+val);
             double valRef = Double.parseDouble("99.".concat(val.substring(3, 4)));
             panelMedidas.getDisplayOpacidad().setLcdValue(valRef); 
        }else{
           panelMedidas.getDisplayOpacidad().setLcdValue(valores.getOpacidad()*0.1*factor); 
        }        
        // CAMBIAR MULTIPLICADOR DE OPACIDAD END

        
        panelMedidas.getDisplayTempGas().setLcdValue(valores.getTemperaturaGas());
        panelMedidas.getDisplayTempTubo().setLcdValue(valores.getTemperaturaTubo());

        double k = medicion.getDensidadHumo();
        panelMedidas.getDisplayDensidad().setLcdValue(k);
        //Ahora todos los maricos leds
        panelMedidas.getLedArmado().setLedOn(valores.isArmadoRecoleccion());
        panelMedidas.getLedCalibracion().setLedOn(valores.isCalibracionProgreso());
        panelMedidas.getLedCleanWindow().setLedOn(valores.isLimpiarVentanas());
        panelMedidas.getLedEEprom().setLedOn(valores.isEEPROM());
        panelMedidas.getLedFueraRango().setLedOn(valores.isOpacidadFueraRango());
        panelMedidas.getLedTempAmb().setLedOn(valores.isTempAmbFueraTolerancia());
        panelMedidas.getLedTempDet().setLedOn(valores.isTempDetectFueraTolerancia());
        panelMedidas.getLedTempTubo().setLedOn(valores.isTempTuboFueraTolerancia());
        panelMedidas.getLedTermistor().setLedOn(valores.isTermistorDañado());
        panelMedidas.getLedVentilador().setLedOn(valores.isDesequilibrioVentiladores());
        panelMedidas.getLedVoltaje().setLedOn(valores.isVoltLineaFueraTolerancia());
        panelMedidas.getLedUmbralGas().setLedOn(valores.isUmbralTemperatura());
        panelMedidas.getLedResetProcesador().setLedOn(valores.isResetProcesador());
        panelMedidas.getLedRealTimeData().setLedOn(valores.isRealTimeDataNotReady());
        panelMedidas.getLedDisp().setLedOn(valores.isRecoleccionDisparada());
    }//end of publish method




    @Override
    public void done(){
        System.out.println("El Hilo de datos es Terminado");
        setTerminado(true);
    }//end of method done

    public boolean isOpCorregida() {
        return opCorregida;
    }

    public void setOpCorregida(boolean opCorregida) {
        this.opCorregida = opCorregida;
    }


    public boolean isInterrumpido() {
        return interrumpido;
    }

    public void setInterrumpido(boolean interrumpido) {
        this.interrumpido = interrumpido;
    }

    public boolean isTerminado() {
        return terminado;
    }

    public void setTerminado(boolean terminado) {
        this.terminado = terminado;
    }

    public Opacimetro getOpacimetro() {
        return opacimetro;
    }

    public void setOpacimetro(OpacimetroSensors opacimetro) {
        this.opacimetro = opacimetro;
    }

   


}//end of class WorkerDatos

