/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.pruebasgases;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *Para probar que el codigo funcione
 * @author Usuario
 */
public class EscribirFechas {
    private static final String FUGAS = "fugas";
    private static final String PIPETAS = "pipetas";
    private static final String OPACIMETRO = "opacimetro";
    private FechasPruebas fechas;
    private AnalizadorFechas an= new AnalizadorFechas();
    public EscribirFechas(){
       fechas = an.getFechas();
       
   }//end of constructor

    public void establecerFechasPruebas(String prueba){
//        Date fechaFugas = new Date();
//        Calendar c = Calendar.getInstance();
//        c.setTime(fechaFugas);
//        c.add(Calendar.DATE, -5);
//        fechas.setUltimaFechaFugas(c.getTime());
//        fechas.setUltimaFechaPipetas(c.getTime());
//        c.add(Calendar.MONTH,-4);
//        fechas.setUltimaFechaOpacidad(c.getTime());
        if (prueba.equals(FUGAS)){
            fechas.setUltimaFechaFugas(new Date());
        }else if(prueba.equals(PIPETAS)) {
            fechas.setUltimaFechaPipetas(new Date());
        }else if(prueba.equals(OPACIMETRO)){
            fechas.setUltimaFechaPipetas(new Date());
        }
        escribirArchivo();
    }
    public void establecerFechasFalsas(String prueba){
        Date fechaFugas = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(fechaFugas);
        c.add(Calendar.DATE, 0);
        fechas = new FechasPruebas();
        fechas.setUltimaFechaFugas(c.getTime());
        fechas.setUltimaFechaPipetas(c.getTime());
        c.add(Calendar.MONTH,-1);
        fechas.setUltimaFechaOpacidad(c.getTime());
//        if (prueba.equals(FUGAS)){
//            fechas.setUltimaFechaFugas(new Date());
//        }else if(prueba.equals(PIPETAS)) {
//            fechas.setUltimaFechaPipetas(new Date());
//        }else if(prueba.equals(OPACIMETRO)){
//            fechas.setUltimaFechaPipetas(new Date());
//        }
        escribirArchivo();
    }

    private void escribirArchivo(){
        ObjectOutputStream output = null;
        try {
            File file = new File("fechas.ser");
            output = new ObjectOutputStream(new FileOutputStream(file));
            output.writeObject(fechas);
        } catch (FileNotFoundException ex) {
           // Logger.getLogger(EscribirFechas.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace(System.err);

        } catch (IOException ioe){
            ioe.printStackTrace();
        }
        finally {
            try {
                output.close();
            } catch (IOException ex) {
                System.out.println("Error escribiendo el archivo");
            }
        }
    }//end of method

    public static void main(String args[]){
        EscribirFechas es = new EscribirFechas();
        es.establecerFechasFalsas(FUGAS);
        es.escribirArchivo();
    }
}//end of class EscribirFechas
