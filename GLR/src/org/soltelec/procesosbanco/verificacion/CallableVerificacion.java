/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.procesosbanco.verificacion;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Callable;
import javax.swing.Timer;
import org.apache.log4j.Logger;
import org.soltelec.procesosbanco.BancoGasolina;
import org.soltelec.util.MedicionGases;

/**
 *La idea es que esta clase tome y muestre valores de datos y si encuentra algun valor
 * que esta dentro del rango retorne inmediatamente el valro encapsulado en la medida
 * de gases
 * @author User
 */
public class CallableVerificacion implements Callable<MedicionGases>{
    
    private BancoGasolina banco;
    private PanelVerificacion panelVerificacion;
    private TipoMuestra tipoMuestra;
    private Timer timer;
    private int contadorTemporizacion ;
    private static final Logger LOG = Logger.getLogger(CallableVerificacion.class.getName());
    
    public CallableVerificacion(BancoGasolina banco, PanelVerificacion panelVerificacion, TipoMuestra tipoMuestra) {
        this.banco = banco;
        this.panelVerificacion = panelVerificacion;
        this.tipoMuestra = tipoMuestra;
        timer = new Timer(1000, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                contadorTemporizacion++;
            }
        });
    }
    
    
     
    
    @Override
    public MedicionGases call() throws Exception {
        
        //Va a tomar muestras continuamente hasta que la  muestra sea valida, si la
        //muestra no es valida entonces terminara el proceso al finalizar los 10 segundos
       MedicionGases medicion = null;
       
       try{
           
           timer.start();
           medicion = banco.obtenerDatos();
           int pef = Integer.parseInt(banco.obtenerPEF());
           while(contadorTemporizacion < 3 || contadorTemporizacion < 10 && !UtilVerificacion.medicionEsValida(tipoMuestra,medicion,pef)){
               
               medicion = banco.obtenerDatos();
               panelVerificacion.setMedicionGases(medicion);//repinta los gases
               
                              
           }
           timer.stop();
           return medicion;
           
        }
        catch(Exception exc){
            LOG.error(exc, exc);
            throw exc;
        }
       
      finally{
           timer.stop();
       } 
        
    }
    
    
    
}
