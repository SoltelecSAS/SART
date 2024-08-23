/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.procesosbanco.verificacion;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import org.apache.log4j.Logger;
import org.soltelec.procesosbanco.BancoGasolina;
import org.soltelec.util.MedicionGases;

/**
 *Clase que toma y publica mediciones de gases durante 10 segundos
 * retorna la ultima medicion que tomo
 * @author soltelec
 */
public class WorkerVerificacionValores extends SwingWorker<MedicionGases, MedicionGases> {

    private BancoGasolina banco;
    private PanelVerificacion panelVerificacion;
    private TipoMuestra tipoMuestra;
    private Timer timer;
    private int contadorTemporizacion ;
    MedicionGases medicion = null;
    private static final Logger LOG = Logger.getLogger(CallableVerificacion.class.getName());

    public WorkerVerificacionValores(BancoGasolina banco, PanelVerificacion panelVerificacion, TipoMuestra tipoMuestra) {
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
    protected MedicionGases doInBackground() throws Exception {
        
       try{
           int pef = Integer.parseInt( banco.obtenerPEF() );
           panelVerificacion.setPef(pef);
           timer.start();
           medicion = banco.obtenerDatos();
           
           while(contadorTemporizacion < 3 || contadorTemporizacion < 10 ){
               
               medicion = banco.obtenerDatos();
               
               publish(medicion);
               
               setProgress(contadorTemporizacion);
              Thread.sleep(250);
               
                              
           }
           timer.stop();
           done();
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

    @Override
    protected void process(List<MedicionGases> chunks) {
       for(MedicionGases med: chunks){
           panelVerificacion.setMedicionGases(med);
       }
        
        
    }

    @Override
    protected void done() {
        panelVerificacion.setMedicionGases(medicion);
    }

    
    
    
    
}
