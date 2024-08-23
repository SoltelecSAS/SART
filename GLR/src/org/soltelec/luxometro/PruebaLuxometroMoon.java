/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.luxometro;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author SOLTELEC
 */
public class PruebaLuxometroMoon {
    
    
    public static void lectura(){
        BufferedReader br = null;
        BufferedReader indices=null;
        Map<Object,String>  medidas = new HashMap();
        String key=null;
        String medida=null;
      
        int contador = 0;
        try {
            br = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/luxometro/SALIDA.DAT"));
            indices = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/luxometro/datos.txt"));
       
            
            while ((medida=br.readLine())!=null) {
                //if (contador == 0) {
                    try {
                        //medida = br.readLine().split(",")[1];
                        key = (String)indices.readLine();
                        medidas.put(key , medida);
                        System.out.println("[ "+key+" ]"+" -- "+medidas.get(key));
                    } catch (Exception e) {
                        
                    }
                    //break;
                //}
                //br.readLine();
                contador++;
            }
        } catch (IOException e) {
            //LOG.log(Level.SEVERE, "Problemas al buscar el archivo {0}", e);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                //LOG.log(Level.SEVERE, "Problemas al intentar cerrar el BufferedReader {0}", ex);
            }
        }
        //LOG.log(Level.INFO, "Lectura de medida satisfactoria");

    }
    
    public static void escritura(){
        
        try {
            final Process process = Runtime.getRuntime().exec(String.format("cmd /c start /wait %s/luxometro/luxometro.bat",System.getProperty("user.dir")));
             final int exitVal = process.waitFor();
                    if (exitVal == 0)
                    { 
                    int contador=0;
                        if (contador==0) 
                      
                        {
                           ///copiaArchivoDatos(System.getProperty("user.dir") + "/luxometro/SALIDA.DAT",System.getProperty("user.dir") + "/luxometro/DatosFarola1.DAT");   
                        }else{
                           //copiaArchivoDatos(System.getProperty("user.dir") + "/luxometro/SALIDA.DAT",System.getProperty("user.dir") + "/luxometro/DatosFarola2.DAT"); 
                        }                        
                    }
        } catch (IOException ex) {
            Logger.getLogger(PruebaLuxometroMoon.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(PruebaLuxometroMoon.class.getName()).log(Level.SEVERE, null, ex);
        }    
    }
    
    public static void main(String arg[]) {
            
        boolean aux=false;
        
        if(aux){
            PruebaLuxometroMoon.escritura();
        }else{
            PruebaLuxometroMoon.lectura();
        }   
    }
}
