/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.procesosbanco.verificacion;


import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Callable;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import org.soltelec.procesosbanco.BancoGasolina;
import org.soltelec.util.MedicionGases;
import org.soltelec.util.ShiftRegister;

/**
 *El objetivo de esta clase es implementar un hilo que verifique
 * que la presión en el gas sube  entre 30 mbar y 40 mbar, espera tres minutos que la presion suba
 * 
 * 
 * @author User
 */
public class CallableSubidaPresion implements Callable<Boolean>{
    
    
    PanelProgresoPresion panelProgreso;//se usa en el caso de que sea un banco Sensors el que se va a verificar
    BancoGasolina banco;
    private ShiftRegister sr = new ShiftRegister(4,0);
    private int contadorTemporizacion = 0;   
    
    
    private Timer timer = new Timer(1000 , new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            contadorTemporizacion++;
        }
    });

    public CallableSubidaPresion(PanelProgresoPresion panelProgreso,  BancoGasolina banco) {
        this.panelProgreso = panelProgreso;
        this.banco = banco;
        
    }
    

    
    @Override
    public Boolean call() throws Exception {
        
            System.out.println("Inicio de subida de presion");
            MedicionGases medicion = banco.obtenerDatos();
            int presionInicial = medicion.getValorPres(); //
            sr.ponerElemento(presionInicial);
            //Toma 5 mediciones antes de iniciar
            int contador = 0;
            while (contador < 5) {
                sr.ponerElemento(medicion.getValorPres());
                medicion = banco.obtenerDatos();                
                Thread.sleep(100);
                contador++;
                
            }//does not work
            //presion estable
            presionInicial = (int) sr.media();
            panelProgreso.getRadial().setValue(presionInicial);
            //medicion tomada en mBares
            //ciclo de inicio para verificar la presion
            int umbral = presionInicial + 30; //la presin de calibracin es de 30 mbar por encima de la presion actual???
            panelProgreso.getLabelMensaje().setText("Abrir un poco la válvula");
            Thread.sleep(500);
            boolean presionOk = false; 
            
            medicion = banco.obtenerDatos();
            timer.start();
            while( (medicion.getValorPres() <= umbral || medicion.getValorPres() >= umbral + 15) && contadorTemporizacion < BancoGasolina.LIM_TIEMPO_CALIBRACION){//fuera de rango?
            //panelProgreso.getLabelMensaje().setText("Ajustar presin ...de ser "+ umbral +" "+ contadorTimer +" s");                
                Thread.sleep(100);                
                if (medicion.getValorPres() < umbral) {
                        panelProgreso.getLabelMensaje().setText("Ajustar presion a " + umbral + " SUBA LA PRESION" + contadorTemporizacion + " s");

                } else if (medicion.getValorPres() > umbral + 10) {
                        panelProgreso.getLabelMensaje().setText("Ajustar presion A" + umbral + " BAJE LA PRESION" + contadorTemporizacion + " s");
                }
                medicion = banco.obtenerDatos();
                sr.ponerElemento(medicion.getValorPres());
                panelProgreso.getRadial().setValue(medicion.getValorPres());
            }//end of while

            timer.stop();
            if(contadorTemporizacion >= 119){//Salida por error mucho tiempo esperando
             JOptionPane.showMessageDialog(null,"Presion inadecuada ... calibracion cancelada*");
             
                    Thread.sleep(250);
               
             Window w = SwingUtilities.getWindowAncestor(panelProgreso);
             w.dispose();
             //
             return false;//el banco es propiedad de otro y es el otro quien debe cerrarlo, se cierra la ventana
            }//end if
            //en rango estabilizando un poco

            //medicion = banco.obtenerDatos();
            contadorTemporizacion = 0;
            //siendo la presion adecuada entonces se pasa a observar que valores esta leyendo el banco
            return true;             
        
    }//fin del metodo call
}//final de la clase
    
    
    
   

    
   
