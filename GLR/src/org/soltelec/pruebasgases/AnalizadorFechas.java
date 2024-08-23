/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.pruebasgases;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 *
 * @author Usuario
 */
public class AnalizadorFechas {

    private FechasPruebas fechas;
    private ObjectInputStream input;

    public AnalizadorFechas(){
        fechas = obtenerFechasPruebas();
    }
    public boolean necesitaFugas(){
        Date hoy = new Date();//Obtengo la fecha de hoy
        Date siguienteFugas = getFechaSigPruebaFugas();
        Calendar c = Calendar.getInstance();
        c.setTime(hoy);
        Calendar c1 = Calendar.getInstance();
        c1.setTime(siguienteFugas);
        //Si hoy es anterior o igual a la fecha de la siguiente prueba  no es necesaria la prueba
        if(c.compareTo(c1) <= 0)
            return false;
        else
            return true;
    }//end of method

     public Date getFechaSigPruebaFugas(){
       Calendar cal = Calendar.getInstance();
       cal.setTime(fechas.getUltimaFechaFugas());
       cal.add(Calendar.DATE,1);
       return cal.getTime();
    }//end of method

     public Date getFechaSiguienteCalPipetas(){
         Calendar cal = Calendar.getInstance();
         cal.setTime(fechas.getUltimaFechaPipetas());
         cal.add(Calendar.DATE, 3);
         return cal.getTime();
     }//end of method

     public Date getFechaSiguienteOpacidad(){
         Calendar cal = Calendar.getInstance();
         cal.setTime(fechas.getUltimaFechaOpacidad());
         cal.add(Calendar.MONTH, 3);
         return cal.getTime();
     }//end of method


     public boolean necesitaCalPipetas(){
        Date hoy = new Date();//Obtengo la fecha de hoy
        Date siguientePipetas = getFechaSiguienteCalPipetas();
        Calendar c = Calendar.getInstance();
        c.setTime(hoy);//lo mismo
        Calendar c1 = Calendar.getInstance();
        c1.setTime(siguientePipetas);
        //Si hoy es anterior o igual a la fecha de la siguiente prueba  no es necesaria la prueba
        if(c.compareTo(c1) <= 0)
            return false;
        else
            return true;
    }//end of method

    public boolean necesitaOpacidad(){
        Date hoy = new Date();//Obtengo la fecha de hoy
        Date siguienteOpacidad = getFechaSiguienteOpacidad();
        Calendar c = Calendar.getInstance();
        c.setTime(hoy);//lo mismo
        Calendar c1 = Calendar.getInstance();
        c1.setTime(siguienteOpacidad);
        //Si hoy es anterior o igual a la fecha de la siguiente prueba  no es necesaria la prueba
        if(c.compareTo(c1) <= 0)
            return false;
        else
            return true;
    }//end of method

    public static String formatearFecha(Date f){
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.FULL,DateFormat.LONG, new Locale("es"));
        return dateFormat.format(f);
    }//end of method


    private FechasPruebas obtenerFechasPruebas() {
     try{
         openFile();

     } catch(Exception exc){//si el archivo no existe
         return new FechasPruebas(true);//retorna una fecha invalida
     }
        FechasPruebas fechasP = new FechasPruebas();
        try // input the values from the file
     {
        while ( true )
        {
           fechasP = ( FechasPruebas ) input.readObject();
         } // end while
      } // end try
      catch ( EOFException endOfFileException )
      {
         return fechasP; // end of file was reached
      } // end catch
      catch ( ClassNotFoundException classNotFoundException )
      {
         System.err.println( "Unable to create object." );
      } // end catch
      catch ( IOException ioException )
      {
         System.err.println( "Error during read from file." );
      } // end catch


      return fechasP;
    }//end of method

    public void openFile() throws IOException
    {
       
             input = new ObjectInputStream(
             new FileInputStream( "./fechas.ser" ) );//ojo con esta ruta
       
       
    } // end method openFile

    public FechasPruebas getFechas() {
        return fechas;
    }//end of method 


}
