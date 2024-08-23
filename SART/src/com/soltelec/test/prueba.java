/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.soltelec.test;

import com.github.sarxos.webcam.Webcam;
import java.io.File;
import java.nio.file.Files;
import javax.imageio.ImageIO;

/**
 *
 * @author SOLTELEC
 */
public class prueba 
{
    public static void main(String[] args) 
    {
        prueba.tomarFoto();
    }
    
    
    public void validarDirectorioArchivo() 
    {
        File archivo = new File("./configuracion/Propiedades.stlc");
        if (archivo.exists()) 
        {
            if (archivo.isFile()) 
            {
                System.out.println("Es un archivo");
            }
            if (archivo.isDirectory())
            {
                System.out.println("Es una carpeta");
            }
        } else 
        {
            System.out.println("OJO: ¡¡No existe el archivo de configuración!!");
        }
    }  

    public static void tomarFoto()
    {
        try 
        {
            Webcam webcam = Webcam.getDefault();
            webcam.open();
            ImageIO.write(webcam.getImage(), "PNG", new File("C:\\hello-world.png"));
              
        } catch (Exception e) {
            System.out.println("Error");
        }
        
    }
}
