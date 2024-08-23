/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.util;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JOptionPane;

public class BloqueoEquipoUtilitario {
    
    public static void main(String[] args) {
        bloquearEquipo("Bloqueo predeterminado desde la clase de bloqueo");
        //System.out.println(validar());
        //validarYEliminarArchivo();
    }
    
    public static void bloquearEquipo(String lugarBloqueo) {
        try {
            // Crear un archivo temporal
            File archivo = new File(System.getProperty("java.io.tmpdir") + File.separator +"temp.txt");
            archivo.createNewFile();
            // Escribir en el archivo temporal
            FileWriter escritor = new FileWriter(archivo);
            escritor.write(lugarBloqueo);
            escritor.close();
            
            // Imprimir la ubicación del archivo temporal
            System.out.println("El archivo temporal se ha creado en: " + archivo.getAbsolutePath());
            System.out.println(System.getProperty("java.io.tmpdir"));
            
        } catch (IOException e) {
            System.out.println("Error al crear o escribir en el archivo temporal.");
            e.printStackTrace();
        }
    }
    
    public static void eliminarArchivo() {  
        File archivo = new File(System.getProperty("java.io.tmpdir") + File.separator +"temp.txt");
        if (archivo.exists()) {
            archivo.delete();
        }
    }
    
    public static void validarYEliminarArchivo() {  
       JOptionPane.showMessageDialog(null,"Debera apagar el analizador de gases y reiniciar todos los procesos...");
    }
    
    public static boolean validar() {
        return new File(System.getProperty("java.io.tmpdir") + File.separator +"temp.txt").exists();
    }
}

