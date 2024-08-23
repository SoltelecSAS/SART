/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.procesosbanco.verificacion;

import com.soltelec.loginadministrador.UtilPropiedades;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
import org.soltelec.procesosbanco.BancoGasolina;

import org.soltelec.procesosbanco.BancoSensors;
import org.soltelec.util.MedicionGases;

/**
 *Clase que controla el proceso de verificacion
 * 
 * @author User
 */
public class ControladorVerificacion implements ActionListener{
    
    PanelProcesoVerificacion panelProcesoVerificacion;
    private enum FasesProceso { INICIO,VALORES_PIPETAS, SUBIDA_PRESION,TOMA_VALORES};
    private BancoSensors bancoGases;
    ExecutorService executorService = Executors.newCachedThreadPool();
    Future<Boolean> futurePresion = null;
    private int numeroTiempos;
    private TipoMuestra tipoMuestra;
    private boolean tomarMuestraBaja = false;
    private boolean tomarMuestraAlta = false;
    private boolean bajaOK = false;
    private boolean altaOK = false;
        
    private FasesProceso faseProceso = FasesProceso.INICIO;
    private MedicionGases medicionMuestraBaja;
    private MedicionGases medicionMuestraAlta;
    private WorkerVerificacionValores workerValores;
    
    private int pef;

    public ControladorVerificacion(PanelProcesoVerificacion panelProcesoVerificacion, BancoSensors bancoGases) {
        this.panelProcesoVerificacion = panelProcesoVerificacion;
        this.bancoGases = bancoGases;
    }
        

    @Override
    public void actionPerformed(ActionEvent e) {
        
        
        if( faseProceso == FasesProceso.INICIO ){//se supone que debe mostrar las tablas
            
            //Un mensaje de validación vacío significa que no hubo error al ingresar los valores de las pipetas
            if (panelProcesoVerificacion.getPanelSeleccionMuestra().obtenerValidarPuntos().isEmpty()){
                
                numeroTiempos = panelProcesoVerificacion.getPanelSeleccionMuestra().getTiempos();
                
                switch( numeroTiempos ){
                    
                    case 2:
                        
                        tipoMuestra = TipoMuestra.BAJA_2T;
                        
                    break;    
                    
                    case 4:
                        
                        tipoMuestra = TipoMuestra.BAJA_4T;
                        
                        
                    break;
                        
                        
                    
                }//end of switch
                
               
                
                //Poner en el panel el panel de Ingreso de gas
                
                //abrir los solenoides correspondientes
                abrirSolenoidesMuestraBaja(bancoGases);
                //Ejecutar el hilo de presion
                tomarMuestraBaja = true;
                tomarMuestraAlta = false;
                
                executorService.submit( new HiloSubidaPresion() );
                
                
               
                
            } else {
                
                // no haga nada 
               
            }
            
            
        }//si la fase no es proceso
        
        else if (faseProceso == FasesProceso.SUBIDA_PRESION){//si esta en subida de presion  y se oprime el boton cancela la subida de presion
            
            
             
                if(futurePresion != null){
                    
                    futurePresion.cancel(true);
                    
                }//end of if futurePresion
            
            
        } else if (faseProceso == FasesProceso.TOMA_VALORES){
            
            workerValores.cancel( true );
            
            
        }
        
    }//end of method actionPerformed  
    
    
    
    
    /**
     * Registra el resultado de la verificacion
     */
    private void registrarResultado(){
        
      if (medicionMuestraBaja != null && medicionMuestraAlta != null){
          
            try {
                
                String strPEF =  bancoGases.obtenerPEF();
                String serial = bancoGases.numeroSerial();
                bajaOK = UtilVerificacion.medicionEsValida(tipoMuestra, medicionMuestraBaja,pef);
                altaOK = UtilVerificacion.medicionEsValida(tipoMuestra, medicionMuestraAlta, pef);
                String marca =  UtilPropiedades.cargarPropiedad("MarcaBanco","propiedades.properties");
                apagarTodo(bancoGases);
                UtilRegistrarVerificacion.registrar(medicionMuestraBaja,medicionMuestraAlta,strPEF,serial,marca,bajaOK && altaOK,panelProcesoVerificacion.getIdUsuario());
                cerrarVentana();
                
            } //end of if
            catch (FileNotFoundException ex) {
                
                Logger.getRootLogger().error("Probablemente no se encuentra el archivo de propiedades",ex);
                
            } catch (IOException ex) {
                
                Logger.getRootLogger().error("Probablemente no se encuentra el archivo de propiedades",ex);
            }
        
              if(!altaOK){
                JOptionPane.showMessageDialog(null,"Ajuste el equipo, verificacion de muestra baja fallida");
              }
              if(!bajaOK){
                JOptionPane.showMessageDialog(null,"Ajuste el equipo, verificacion de muestra alta fallida");
              }
                        
                        
      }//end of if
        
        
        
    }//end of method registrarResultado
    
    
    /**
     * Metodo que cierra los solenoides del banco de gases
     * @param banco 
     */
     public void apagarTodo(BancoGasolina banco){
        
                
        banco.encenderSolenoideUno(false);
        banco.encenderSolenoide2(false);
        banco.encenderCalSol1(false);
        banco.encenderCalSol2(false);
        banco.encenderBombaMuestras(false);
        banco.encenderDrainPump(false);
        
    }
     
    
     /**
      * Abre los solenoides para que ingrese el gas  de la Muestra alta
      * @param banco 
      */
    public void abrirSolenoidesMuestraAlta(BancoGasolina banco){
        
        banco.encenderCalSol1(false);
        //encender el solenoide de calibracion 2
        banco.encenderCalSol2(true);
        //Encender el solenoide de aire 1 AirSol1
        banco.encenderSolenoideUno(true);
        banco.encenderSolenoide2(true);
        banco.encenderBombaMuestras(false);
        banco.encenderDrainPump(false);
        
    } //end of method abrirSolenoideMuestraAlta
    
    
    public void abrirSolenoidesMuestraBaja(BancoGasolina banco){
        
        try {       
                banco.encenderCalSol1(false);
                Thread.sleep(100);
                banco.encenderCalSol2(true);
                Thread.sleep(100);
                banco.encenderSolenoideUno(true);
                Thread.sleep(100);
                banco.encenderSolenoide2(false);
                Thread.sleep(100);
                //Apagar la bomba de muestra y la bomba de Drenaje palabra : 00001100
                banco.encenderDrainPump(false);
                Thread.sleep(100);
                banco.encenderBombaMuestras(false);
                Thread.sleep(100);
        } catch (InterruptedException ex) {
            
        }
    }
    /**
     * Cierra el panel del proceso de verificacion
     */
    public void cerrarVentana(){
        
        Window windowAncestor = SwingUtilities.getWindowAncestor(panelProcesoVerificacion);
        windowAncestor.dispose();
        
        
    }
        
    class HiloSubidaPresion implements Runnable{
        
        
        @Override
        public void run(){
            
            
            CallableSubidaPresion callableSubida = new CallableSubidaPresion(panelProcesoVerificacion.getPanelProgresoPresion(), bancoGases);
            futurePresion = executorService.submit( callableSubida );
            faseProceso = FasesProceso.SUBIDA_PRESION;
            panelProcesoVerificacion.pintarPanelPresion();
            
            try {            
            
                
                boolean isPresionUp = futurePresion.get();
                
                //Si la presion subió, entonces ejecutar el siguiente hilo
                
                if (isPresionUp){
                    
                    executorService.submit( new HiloTomaDatosVerificacion() );
                    
                    
                } else { 
                    
                //Si la presion no subió
                    
                    cerrarVentana();
                    
                }
                
                
            } catch (InterruptedException ex) {
               
                Logger.getRootLogger().error("Hilo de presion interrumpido durante verificacion", ex);
                ex.printStackTrace(System.err);
                
            } catch (ExecutionException ex) {
                
                Logger.getRootLogger().error("Error durante la subida de presion", ex);
                ex.printStackTrace(System.err);
                
            }
                
           
            
        }//end of method run
        
    }//end of class HiloSubidaPresion
    
    
    /**
     * Clase para 
     */
    class HiloTomaDatosVerificacion implements Runnable{
            

            @Override
            public void run() {
                
                panelProcesoVerificacion.pintarPanelVerificacion();
                faseProceso = FasesProceso.TOMA_VALORES;
                
                panelProcesoVerificacion.getPanelVerificacion().setTipoMuestra(tipoMuestra);
                
                 workerValores = 
                          new WorkerVerificacionValores(bancoGases, panelProcesoVerificacion.getPanelVerificacion() , tipoMuestra);
                
                
                
                 workerValores.addPropertyChangeListener( new PropertyChangeListener() {

                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                        if("progress".equals(evt.getPropertyName())){
                            panelProcesoVerificacion.getPanelVerificacion().getjProgressBar1().setValue( (Integer)evt.getNewValue());
                        }
                    }
                
                
                });//end of addPropertyChangeListener
                 
                
                workerValores.execute();
                
                try{
                
                    if( tomarMuestraBaja ) {
                    
                        medicionMuestraBaja = workerValores.get();
                                     
                        tomarMuestraBaja = false;
                        tomarMuestraAlta = true;
                    
                        switch( numeroTiempos ){
                        
                            case 2 :
                            
                                tipoMuestra = TipoMuestra.ALTA_2T;
                            
                            break;
                            
                            case 4:
                        
                                tipoMuestra = TipoMuestra.ALTA_4T;
                            
                            break;    
                        
                        
                        }
                    
                        //JOptionPane.showMessageDialog(null,"Cierre el regulador del cilindro de la muestra y preparese para la siguiente");
                        
                        abrirSolenoidesMuestraAlta(bancoGases);
                        panelProcesoVerificacion.getPanelVerificacion().setTipoMuestra(tipoMuestra);
                        JOptionPane.showMessageDialog(null, tipoMuestra);
                    
                        executorService.submit( new HiloSubidaPresion() );
                    
                        return;
                    
                    } //end of if tomarMuestraBaja
                 
                    if (  tomarMuestraAlta ){
                    
                        medicionMuestraAlta = workerValores.get();
                    
                        registrarResultado();
                
                    }//end of if tomarMuestraAlta
                
                }//end of try
                catch(ExecutionException execxc){
                    
                    Logger.getRootLogger().error("Error durante la verificacion", execxc);
                    JOptionPane.showMessageDialog(null, "Error durante la verificacion: " + execxc.getMessage() );
                    
                }
                catch(InterruptedException iexc){
                    
                    Logger.getRootLogger().info("Verificacion cancelada durante toma de datos",iexc);
                    JOptionPane.showMessageDialog(null, "Verificacion cancelada");
                    
                }
                 
                 
            }//end of method run
        
        
    }//end of class HiloTomaDatos
        
    }//end of class ControladorVerificacion
    
    
    
    

