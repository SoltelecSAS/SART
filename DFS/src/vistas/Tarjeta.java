/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vistas;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Robin
 */
public class Tarjeta {
    private  PuertoRS232 puerto;
    private String puertotarjeta;
    private byte[] buffer = new byte[50];
    private BufferedReader config;

    private String line;
    private int numconex;
    public Tarjeta(){

    }
    
    public void configuracion(){
        try {
            config = new BufferedReader(new FileReader(new File("configuracion.txt")));
            while(!config.readLine().startsWith("[DAQ]")){}
            if((line=config.readLine()).startsWith("puerto:")) {
                puertotarjeta = line.substring(line.indexOf(" ")+1, line.length());
                //System.out.println(puertotarjeta);
            }        } catch (FileNotFoundException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex){
            System.out.println("no se pudo abrir " + ex);
        }
        puerto = new PuertoRS232();
        try{
            puerto.connect(puertotarjeta);
        }catch ( Exception e ){
            e.printStackTrace(System.out);
        }
    }
    
    public void comandoCONEX (){
        try {
            puerto.out.write('H');
            puerto.out.write('J');
            puerto.out.write('W');
        } catch (IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void comandoCLAVE (){
        try {
            puerto.out.write('H');
            puerto.out.write('S');
            puerto.out.write('O');
            puerto.out.write('L');
            puerto.out.write('T');
            puerto.out.write('E');
            puerto.out.write('L');
            puerto.out.write('E');
            puerto.out.write('C');
            puerto.out.write('W');
        } catch (IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void IniciaTarjeta(){
        String respuesta;
        int i=0;
        boolean salida=false;
        numconex=0;
        while(!salida){
            comandoCLAVE();
            try {
                Thread.currentThread().sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(Tarjeta.class.getName()).log(Level.SEVERE, null, ex);
            }
            comandoCONEX();
            try {
                Thread.currentThread().sleep(500);
                puerto.in.read(buffer);
            } catch (InterruptedException ex) {
                Logger.getLogger(Tarjeta.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex){
                Logger.getLogger(DlgFrenoMoto.class.getName()).log(Level.SEVERE, null, ex);
            }
            respuesta = new String(buffer);
            //System.out.println("r"+respuesta);
            if(respuesta.startsWith("HDAQ1W")){
                System.out.println("Tarjeta correctamente conectada");
                JOptionPane.showMessageDialog(null,"La tarjeta se conecto correctamente","Conexión",
                    JOptionPane.INFORMATION_MESSAGE);
                salida=true;
            }else{
                System.out.println("Tarjeta no conectada , MENSAJE QUEW RECIBO: " + respuesta);
                JOptionPane.showMessageDialog(null,"La tarjeta no se pudo conectar, verifique las conexiones",
                        "Error",JOptionPane.ERROR_MESSAGE);
                numconex++;
            }
            if(numconex==3) {
                salida=true;
            }
        }
    }

    public void setPuerto(PuertoRS232 puerto) {
        this.puerto = puerto;
    }

    public int getNumconex() {
        return numconex;
    }

    public static void main(String args[]) {
        Tarjeta t = new Tarjeta();
        t.configuracion();
        t.IniciaTarjeta();
        System.exit(0);
    }
}
