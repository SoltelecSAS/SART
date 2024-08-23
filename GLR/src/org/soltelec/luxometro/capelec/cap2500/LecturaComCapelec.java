/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.luxometro.capelec.cap2500;
import com.soltelec.modulopuc.configuracion.modelo.Propiedades;
import giovynet.serial.Baud;
import giovynet.serial.Com;
import giovynet.serial.Parameters;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author User
 */
public class LecturaComCapelec {

    String caracter = "";

    public List<String> lecturaCom() throws Exception {
        Parameters configuracion = null;
        String puertoluxometro = Propiedades.getInstance().getPuertoLuxometro();
        Com puerto = null;
        try {
            configuracion = new Parameters();
            if(puertoluxometro.equals(""))
            {
                JOptionPane.showMessageDialog(null, "ERROR PUERTO NO CONFIGURADO", "ERROR PUERTO NO CONFIGURADO", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
            configuracion.setPort(puertoluxometro);
            configuracion.setBaudRate(Baud._9600);
            puerto = new Com(configuracion);
            List<String> lineasLeidas = new ArrayList<String>();
            String linea = null;
            int cont = 0;
            boolean bandera = true;
            while (bandera) {
                caracter = puerto.receiveSingleString();
                while (!caracter.equals("\n")) {
                    caracter = puerto.receiveSingleString();
                    //System.out.print(caracter);
                    linea = linea + caracter;
                }
                if (caracter.equals("\n")) {
                    if (linea != null) {
                        //System.out.print(dato2);
                        if (cont < 74) {
                            //System.out.println(cont);
                            lineasLeidas.add(linea);
                            linea = "";
                            cont++;
                        } else {
                            bandera = false;
                        }
                    }

                }

            }
            return lineasLeidas;
           
        } catch (Exception e1) {
            throw e1;
        } finally {
            try {
                puerto.close();
            } catch (Exception ex) {
                Logger.getLogger(LecturaComCapelec.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
}
