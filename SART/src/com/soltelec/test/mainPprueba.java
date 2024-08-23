
package com.soltelec.test;

import com.soltelec.igrafica.Frm_Placas;
import java.awt.Frame;
import javax.swing.SwingUtilities;
import org.soltelec.luxometro.JDialogLucesMoto;

/**
 *
 * @author SOLTELEC
 */
public class mainPprueba {
    public static void main(String[] args) 
    {
        double sumaFuerzas=1549.0;
        double sumaPesos=11195.0;
        
        double eficacia = ((sumaFuerzas) / (sumaPesos)) * 100.0;
        
        System.out.println("" + eficacia);
//        mainPprueba llamado=new mainPprueba();
//        llamado.prueba();
       

    }
    
    
    private void prueba(){
        
        Frm_Placas a=new Frm_Placas();
        Frame frame = (Frame) SwingUtilities.getWindowAncestor(a);
        long idPrueba=120;
        JDialogLucesMoto insta=new JDialogLucesMoto(frame,true,idPrueba,123,123,1,"Serial");
    }
    
}
