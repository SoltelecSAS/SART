/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.luxometro.chino;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingWorker;
import javax.swing.Timer;

/**
 *
 * @author User
 */
public class WorkerLuxometroChino extends SwingWorker<MedidaLuxometroChino, Integer>{

    
    private LuxometroChino luxometro;
    private Timer timer;
    private int contadorTemporizacion;

    public WorkerLuxometroChino(LuxometroChino luxometro) {
        this.luxometro = luxometro;
        timer = new Timer(1000, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                contadorTemporizacion++;
            }
        });
    }
    
    
    
    
    @Override
    protected MedidaLuxometroChino doInBackground() throws Exception {
        
        List<MedidaLuxometroChino> listaMedidas = new ArrayList<MedidaLuxometroChino>();
        timer.start();
        
        while(contadorTemporizacion < 5){
            
            MedidaLuxometroChino medida = luxometro.obtenerMedicion();
            listaMedidas.add(medida);
            Thread.sleep(100);
            publish(contadorTemporizacion);
            setProgress(contadorTemporizacion);
        }
        
        MedidaLuxometroChino medidaMedia = obtenerMedidaMedia(listaMedidas);
        
        return medidaMedia;
    }

    private MedidaLuxometroChino obtenerMedidaMedia(List<MedidaLuxometroChino> listaMedidas) {
        
        
        double mediaAngulo = 0;
        double mediaLuminosidad = 0;
        MedidaLuxometroChino medida = new MedidaLuxometroChino();
        
        if( listaMedidas != null && listaMedidas.size() != 0){
           
            for(MedidaLuxometroChino med:listaMedidas){
                mediaAngulo = mediaAngulo + med.getPorcentajeInclinacion();
                mediaLuminosidad = mediaLuminosidad + med.getLuminosidad();
                
            }
            
            mediaAngulo = mediaAngulo/listaMedidas.size();
            mediaLuminosidad = mediaLuminosidad/listaMedidas.size();
            medida.setLuminosidad(mediaLuminosidad);
            medida.setPorcentajeInclinacion(mediaAngulo);
            
        } else {
            
            medida.setLuminosidad(-1);
            medida.setPorcentajeInclinacion(-1);
        }
        timer.stop();
        return medida;
    }

  
    
    
    
    
}
