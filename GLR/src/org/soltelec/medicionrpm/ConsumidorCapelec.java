/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.medicionrpm;

import gnu.io.SerialPort;
import org.soltelec.util.BufferStrings;
import eu.hansolo.steelseries.gauges.Linear;
import eu.hansolo.steelseries.gauges.Radial;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JTextArea;

/**
 *Clase que se encarga de tomar datos
 * que se lean desde el puerto serial
 * y procesarlos
 * @author Usuario
 */
public class ConsumidorCapelec implements Runnable,MedidorRevTemp{

private JTextArea txtArea;
private final BufferStrings bufferStr;
private boolean terminado = false;
private boolean inicializando = true;
private String str;
private Radial radialRPM;
private Linear linearTemperatura;
private int rpm;
private int temperatura;
private SerialPort serialPort;
private ProductorCapelec productor;

    public ConsumidorCapelec(BufferStrings theBuffer, JTextArea ta, Radial r, Linear l){

        this.bufferStr = theBuffer;
        this.txtArea = ta;
        this.radialRPM = r;
        this.linearTemperatura = l;

    }//end of constructor

    public ConsumidorCapelec(BufferStrings theBuffer,ProductorCapelec productor){
        this.bufferStr = theBuffer;
        this.productor = productor;
    }


    //Este hilo debe estar esperando las cadenas que pone el otro buffer
    // y publicarlas
    public void run() {

        while(!terminado){
            synchronized(bufferStr){
                if(bufferStr.isEmpty())
                    try {
                        bufferStr.wait();
                    } catch (InterruptedException ex) {
                        
                    }
                try{
                str = bufferStr.getStr();
                }
                catch(Exception exc){
                 System.out.println("Excepcion leyendo la lista de datos");
                 exc.printStackTrace();
                 break;
                }
                //System.err.println(str);
                procesarString(str);
           }//end synchronized
        }//end of while
        System.out.println("Hilo Consumidor Terminado");
    }//end of run

    private void procesarString(String theStr) {
        //quitar espacios
        boolean noMostrar = false;
        System.err.println("---"+theStr);


        if( (!theStr.equals(" ")) || !(theStr.isEmpty()) ){
        //Pattern  expression = Pattern.compile("\\.*T(\\d+)\\.*");
       // Pattern  expression = Pattern.compile("(\\d{3,4})T(\\d{3})\\.*");
        //Pattern  expression = Pattern.compile("(\\d{3,4})(T?)(\\d*)");
        Pattern  expression = Pattern.compile("T(\\d{3})");//Busco Temperatura en la cadena
        Matcher matcher = expression.matcher(theStr);
        
        while(matcher.find()){
            System.err.println(matcher.group());
            System.err.println("t:" + matcher.group(1));
            if(matcher.group(1).length() > 0 )
            try{
                    temperatura = Integer.parseInt(matcher.group(1).trim());
                    if(linearTemperatura!=null) {
                        linearTemperatura.setValue(temperatura);
                    }
                    if(txtArea!=null) {
                        txtArea.insert("t: " + temperatura + '\n',0);
                        return;
                    }                    
            }
            catch(NumberFormatException ne){
                System.err.println("Cadena no puede ser convertida a numero:" + matcher.group());
                ne.printStackTrace(System.err);
            }//end of catch
        }//end of while

        //Busco Inicializacion
        Pattern expression3 = Pattern.compile("\\.*[IDL]\\.*");
        matcher = expression3.matcher(theStr);
        while(matcher.find()){
            noMostrar = true;
            System.err.println("|||Inicializando");
            if(txtArea!=null) {
                txtArea.insert(theStr+'\n',0);
                inicializando = true;
            }
            if(radialRPM!=null) {
                radialRPM.setEnabled(false);
            }            
        }
        if(noMostrar == false){//Buscar Revoluciones
                if(theStr.length() <= 4 && theStr.length() >2){//Busca tres o cuatro digitos
//                Pattern expresion2 = Pattern.compile("^[A-Z]?\\d{3,4}");
//                matcher = expresion2.matcher(theStr);
//                while(matcher.find()){
//                    System.err.println("r:" + matcher.group());
//                    if(matcher.group().length() > 0 )
//                    try{
//                        rpm = Integer.parseInt(matcher.group().trim());
//                        if(radialRPM!=null)
//                        radialRPM.setValue(rpm);
//                        if(txtArea!=null)
//                        txtArea.append("r: " + rpm+'\n');
//                        }
//                    catch(NumberFormatException ne){
//                        System.err.println("Cadena no puede ser convertida a numero:" + matcher.group());
//                        ne.printStackTrace();
//                    }//end of catch
//                }//end of while
               
                Pattern expresion3 = Pattern.compile("\\d{3,4}");
                    matcher = expresion3.matcher(theStr);
                    while(matcher.find()){
                    if(matcher.group().length() > 0 )
                    try{
                        rpm = Integer.parseInt(matcher.group().trim());
                        System.err.println("r: " +rpm);
                        if(radialRPM!=null){
                        radialRPM.setValue(rpm);
                        radialRPM.setEnabled(true);//Bug
                        inicializando = false;
                        }
                        if(txtArea!=null)
                        txtArea.insert("r: " + rpm+'\n',0);
                    }
                    catch(NumberFormatException ne){
                        System.err.println("Cadena no puede ser convertida a numero:" + matcher.group());
                        ne.printStackTrace(System.err);
                    }
                    }//end while
                   
            }
        }//end if no mostrar
        else {
            return;
        }


        System.err.println("No medicion ");
        }//end if noMostrar
    }//edn of procesarString

    public void manejarRespuesta(String s){//rutina para identificar la respuesta a un comando
            //rutina para identificar la respuesta a un comando
            if(s.charAt(0) == '='){
              System.out.println("Comando Recibido OK ACK");
              txtArea.setText("Comando Recibido OK ACK");
            } else if(s.charAt(0) == '!' ){
              System.out.println("Comando mal Recibido o NACK");
              txtArea.setText("Comando mal Recibido o NACK");
            } else if(s.substring(1,3).equals("LCK")){
                System.out.println("Etapa de inicializacion medicion por bateria");
                txtArea.setText("Etapa de inicializacion medicion por bateria");
            }else {//supongo que estara enviando una medicion
                if(s.charAt(0) =='T'){//valor de temperatura
                    String strTemperatura = s.substring(1, 3);
                    int valorTemperatura = Integer.parseInt(strTemperatura);
                    linearTemperatura.setValue(valorTemperatura);
                    //this.temperaturaAceite = valorTemperatura;
                   System.out.println("Lectura de temperatura la temperatura:" + valorTemperatura);
                   txtArea.append("Lectura de temperatura :" + valorTemperatura);
                }//fin del if de temperatura
                else {
                    int valorRPM = Integer.parseInt(s);
                    radialRPM.setValue(valorRPM);
                    //this.rpm = valorRPM;
                    System.out.println("Lectura de rpms" + valorRPM);
                    txtArea.append("Lectura de rpms" + valorRPM);

                }//final de if de medicion
            }//final de mirar si la medicion es de temperatura o de rpm



    }//end of method manejarRespuesta

    public Linear getLinearTemperatura() {
        return linearTemperatura;
    }

    public void setLinearTemperatura(Linear linearTemperatura) {
        this.linearTemperatura = linearTemperatura;
    }

    public Radial getRadialRPM() {
        return radialRPM;
    }

    public void setRadialRPM(Radial radialRPM) {
        this.radialRPM = radialRPM;
    }

    public JTextArea getTxtArea() {
        return txtArea;
    }

    public void setTxtArea(JTextArea txtArea) {
        this.txtArea = txtArea;
    }

    @Override
    public int getRpm() {
        return rpm;
    }

    @Override
    public int getTemp(){
        return temperatura;
    }

    public boolean isTerminado() {
        return terminado;
    }

    public void setTerminado(boolean terminado) {
        this.terminado = terminado;
    }

    public boolean isInicializando() {
        return inicializando;
    }

    @Override
    public SerialPort getPuertoSerial() {
       return serialPort;
    }

    @Override
    public void setPuertoSerial(SerialPort port) {
        this.serialPort = port;
    }

    @Override
    public void stop() {
        try {
            productor.setInterrumpido(true);
            Thread.sleep(100);
            productor.setTerminado(true);
            Thread.sleep(100);
            this.setTerminado(true);
            Thread.sleep(100);
            this.serialPort.close();
        } catch (InterruptedException ex) {
            
            ex.printStackTrace(System.err);
            
        }
                
        
    }

    @Override
    public void setRpm(Integer rpm) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
 }
