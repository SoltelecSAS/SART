/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.procesosbanco.verificacion;

import org.soltelec.procesosbanco.BancoGasolina;
import org.soltelec.util.MedicionGases;

/**
 *
 * @author User
 */
public class UtilVerificacion {
    
    
      public static boolean estaEnRangoHC(TipoMuestra tipo , int valorHC) {
        
        switch (tipo) {
            case BAJA_4T:
                
            return (valorHC >= 285 && valorHC <= 315); 
               
            case ALTA_4T:
                
            return (valorHC >= 1140 && valorHC <= 1260);    
            
            case BAJA_2T:
            
            return (valorHC >= 285 && valorHC <= 315);    
               
            case ALTA_2T:
              
             return (valorHC >= 3040 && valorHC <= 3360);   
             
            default:
               
            return false;
            
            
            
        }
        
        
        
    }//end of method
     
     
     public static  boolean estaEnRangoCO(TipoMuestra tipoMuestra, double valorCO) {
        
         switch (tipoMuestra){
             case BAJA_4T:
                 
             return (valorCO >= 0.95 && valorCO <= 1.05);
                             
             case ALTA_4T:
             return (valorCO >= 3.8 && valorCO <= 4.2);
                 
                 
             case BAJA_2T:
             return (valorCO >= 0.95 && valorCO <= 1.05);    
            
             case ALTA_2T:
             return (valorCO >= 7.6 && valorCO <= 8.4);    
                 
             default:
                     
             return false;
         }
    }//end of method estaEnRangoCO

    public static boolean estaEnRangoCO2(TipoMuestra tipoMuestra, double valorCO2) {
        
        switch (tipoMuestra){
            
            case BAJA_4T:                
            return (valorCO2 >= 5.7 && valorCO2 <= 6.3);
                
            case ALTA_4T:
            return (valorCO2 >=11.4 && valorCO2 <= 12.6 ); 
                
            case BAJA_2T:            
            return(valorCO2 >= 5.7 && valorCO2 <= 6.3);
            
            case ALTA_2T:
            return (valorCO2 >= 11.4 && valorCO2 <= 12.6);    
            
            default:
            return false;    
        }
    
    }//end of method

    static boolean medicionEsValida(TipoMuestra tipoMuestra, MedicionGases medicion,int pef) {
        double valorPef = pef*0.001; 
        boolean validoHC = estaEnRangoHC(tipoMuestra, (int)(medicion.getValorHC()/valorPef));
        boolean validoCO = estaEnRangoCO(tipoMuestra, medicion.getValorCO()*0.01);
        boolean validoCO2 = estaEnRangoCO2(tipoMuestra, medicion.getValorCO2()*0.1);
        
        return (validoHC && validoCO && validoCO2);
    }
    
    
   
    
   
    
}
