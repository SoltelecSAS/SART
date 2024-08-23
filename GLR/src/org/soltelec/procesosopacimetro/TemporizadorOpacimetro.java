/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.procesosopacimetro;

import com.soltelec.modulopuc.utilidades.Mensajes;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.logging.Level;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import org.apache.log4j.Logger;
import org.soltelec.procesosopacimetro.JDialogServicioOpacimetro;
import utiltermohigrometro.UtilPropiedades;

/**
 *
 * @author SOLTELEC
 */
public class TemporizadorOpacimetro extends Thread {
        public Timer timerOpacimetro; 
    public int segundo=0, minuto=0;
    JDialogServicioOpacimetro dlgServicioOpacimetro;
    String TempOpaci = "";
    String nombreUsuario = "";
    public boolean continuar = true;
    
     public void reiniciar()
   {
      segundo=0;
      minuto=0;
   }
    
    
     public void run(){
         while(continuar){
            try {
                TempOpaci = UtilPropiedades.cargarPropiedad("TemporizadorOpacimetro", "propiedades.properties");
            } catch (IOException ex) {
                System.out.println("error al leer el archivo de configuracion para inicializar el temporizador del opacimetro " + ex);
               JOptionPane.showMessageDialog(null, "Error al leer el archivo propiedades.properties", "ERROR DE LECTURA", JOptionPane.WARNING_MESSAGE);
            }
            
           
            //TempOpaci = UtilPropiedades.cargarPropiedad("TemporizadorOpacimetro", "propiedades.properties");//LEE EL VALOR DE LA VARIABLE TemporizadorOpacimetro
            System.out.println("valor de TempOpaci :" + TempOpaci);
        
            TempOpaci=(TempOpaci==null)?"":TempOpaci;    

        if (TempOpaci.equalsIgnoreCase("") || TempOpaci.equalsIgnoreCase("false") )//LA VARIABLE ESTA VACIA O ESTA CONFIGURADA COMO FALSE
        {
            TempOpaci = "";
            System.out.println("temporizador apagado, la variable TemporizadorOpacimetro esta configurada en false, esta vacia o no existe");
        }else{
             System.out.println("temporizador del opacimetro esta activado");
             
          // timerOpacimetro = new Timer(1000, new ActionListener() {
            //       public void actionPerformed(ActionEvent e) 
              //      {
                                segundo=segundo+1;
                                if(segundo > 59)//AUMENTA SEGUNDO
                                  {
                                        segundo=0;
                                        minuto=minuto+1;
                                        if(minuto ==55)
                                        {
                                           Mensajes.messageDoneTime("Dentro de 5 minutos se realizara la prueba del opacimetro",15);//SI ESTA EN EL MINUTO 55 MUESTRA MENSAJE PARA NOTIFICAR QUE SE HARA LA PRUEBA EN 5 MINUTOS 
                                            System.out.println("FALTAN 5 MINUTOS PARA REALIZAR VALIDACION DEL OPACIMETRO");
                                           segundo = segundo+15;//SUMO LOS 5 SEGUNDOS DEL MENSAJE
                                        }                                        
                                        
                                        if(minuto ==1)//AUMENTA HORA
                                        {
                                                
                                                Mensajes.messageDoneTime("Realizando validacion de opacimetro",10);
                                                System.out.println("validando opacimetro");//SI SE CUMPLE LA HORA VALIDA OPACIMETRO Y REINICIA VALORES
                                                dlgServicioOpacimetro = new JDialogServicioOpacimetro(null, true);
                                                dlgServicioOpacimetro.setTitle("Validacion opacimetro ");
                                                //dlgServicioOpacimetro.setDefaultCloseOperation(JDialogServicioOpacimetro.DISPOSE_ON_CLOSE);
                                                dlgServicioOpacimetro.setDefaultCloseOperation(JDialogServicioOpacimetro.DISPOSE_ON_CLOSE);
                                                dlgServicioOpacimetro.setVisible(true);
                                                Logger.getLogger("igrafica").info("se realiza validacion del opacimetro, usuario logeado :" + nombreUsuario );
                                                minuto=0;
                                                segundo=0;
                                        }
                                       System.out.println( minuto +":"+ segundo); //MUESTRA TEMPORIZADOR
                                   }else{
                                        System.out.println( minuto +":"+ segundo);//MUESTRA TEMPORIZADOR
                                        }
                    }
             //});
               // timerOpacimetro.start();
           //}
                   
    }
     }
       
    
}

