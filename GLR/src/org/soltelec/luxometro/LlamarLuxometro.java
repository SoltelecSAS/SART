/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.luxometro;

import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.JOptionPane;
import org.soltelec.util.UtilPropiedades;

/**
 *
 * @author Usuario
 */
public class LlamarLuxometro {

    private String nombreArchivo = "propiedades.properties";
    private String nombrePropiedad = "RutaProgLux";

    public void LlamarProgramaLuxometro() {
        try {
            String rutaProgramaLux = UtilPropiedades.cargarPropiedad(nombrePropiedad,nombreArchivo);
            System.out.println(rutaProgramaLux);
            Runtime rt = Runtime.getRuntime();
            //Process exec = rt.exec(rutaProgramaLux);
            Process exec = rt.exec("luxometro.bat");
            exec.waitFor();//Funciona con el programa del Luxometro
            System.out.println("Programa Terminado");
        } catch (FileNotFoundException ex) {
           JOptionPane.showMessageDialog(null,"No se encuentra el archivo");
        } catch (IOException ex) {
           JOptionPane.showMessageDialog(null,"Error de E/S leyendo el archivo");
           ex.printStackTrace(System.err);
        } catch(InterruptedException iexc){
            iexc.printStackTrace(System.err);
        }
    }//end of method LlamarProgramaLuxometro

    public static void main(String args[]){
        LlamarLuxometro llamarLuxometro = new LlamarLuxometro();
        
            llamarLuxometro.LlamarProgramaLuxometro();
        
    }//end of method main


}
