/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.medicionrpm.centralauto;

import gnu.io.SerialPort;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;
import org.apache.log4j.Logger;

import org.soltelec.medicionrpm.MedidorRevTemp;

/**
 *La comunicacion con este modulo es completamente sincrona
 * por lo tanto no existe productor consumidor como con el
 * kit capelec
 * @author GerenciaDesarrollo
 */
public class TB86000 implements MedidorRevTemp{

    private SerialPort puertoSerial;
    private OutputStream out;
    private InputStream in;

    private int numeroTiempos = 4;
    private int numeroCilindros = 4;

    private int rpms;
    private int temp;
    private final int TEMPERATURA;
     private int contadorErrores = 0;
    
    public TB86000(SerialPort puertoSerial) {
        try {
            this.puertoSerial = puertoSerial;
            this.out = puertoSerial.getOutputStream();
            this.in = puertoSerial.getInputStream();
        } catch (IOException ex) {
            System.out.println("No se pueden establecer los flujos");
        }
        Random r =new Random();
        TEMPERATURA = 55 + r.nextInt(25);
    }
    


/**
 * El comando es #SAY checksum -16
 * traducido a byte es:
 * 35 83 65 89 -16
 * @return
     * @throws java.io.IOException
 */
    public String comandoSAY() throws IOException{
            short comando = 35;
            short[] argumentos = {83,65,89};
            ComandoTB86000 comandoTb = new ComandoTB86000(comando,argumentos);
            enviarRecibirBytes(comandoTb.comandoToBytes());
            return  "EMIS";
    }


    /**
     * Comando 'C
     * decimal 67
     * @return
     *
     */

    public String comandoC() throws IOException{
        short numComando = 67;
        ComandoTB86000 comandoTB = new ComandoTB86000(numComando, new short[0]);
        enviarRecibirBytes(comandoTB.comandoToBytes());
        return "NOS";
    }


    /**
     * Comando 'R'
     * decimal 82
     * @return
     */

    public String comandoR(int numeroTiempos, int numeroCilindros) throws IOException{
        short numComando = 82;
        short[] argumentos = new short[]{(short)numeroTiempos,(short)numeroCilindros};
        ComandoTB86000 comandoTB = new ComandoTB86000(numComando,argumentos);
        enviarRecibirBytes(comandoTB.comandoToBytes());
        return "NEA";
    }

    /**
     * Comando'T'
     * decimal 84
     * para la temperatura y si está o no
     * presente la temperatura
     * @return
     */
    public String comandoT() throws IOException{
        short numeroComando = 84;
        ComandoTB86000 comandoTB = new ComandoTB86000(numeroComando,new short[0]);
        byte[] enviarRecibirBytes = enviarRecibirBytes(comandoTB.comandoToBytes());
        return "CS";
    }


    /**
     * comando u revoluciones y temperatura
     * decimal 117
     * @param numeroTiempos
     * @param numeroCilindros
     * @return
     * @throws java.io.IOException
     */

    public String comandoU(int numeroTiempos, int numeroCilindros) throws IOException{
        
        System.out.println("..... comandoU .....");
        System.out.println("numero de cilindros: " + numeroCilindros);
        System.out.println("tipos del vehiculo: " + numeroTiempos);
        
        short numeroComando = 117;
        short argumentos[] = new short[]{(short)numeroCilindros,(short)numeroTiempos};
        ComandoTB86000 comandoTB = new ComandoTB86000(numeroComando,argumentos);
        byte[] enviarRecibirBytes = enviarRecibirBytes(comandoTB.comandoToBytes());
        int rpmAnterior = rpms;
        int tempAnterior = temp;
        try{            
            rpms = rpmsFromU(enviarRecibirBytes);
            temp = tempFromU(enviarRecibirBytes);
            contadorErrores = 0;
        }catch (ArrayIndexOutOfBoundsException aexc){
            if(contadorErrores < 5){
            Logger.getRootLogger().error("Error obteniendo rpms", aexc);
            rpms = rpmAnterior;
            temp = tempAnterior;
            contadorErrores++;
            }else {
                throw aexc;
            }
        }
        return "R: " + rpms +"T: " + temp;
    }
    /**
     * Comando para pedir la version del
     * software y hardware del aparato
     * 'v'
     * decimal: 118
     * @return
     */

    public String comandoV() throws IOException{
        short numeroComando = 118;
        ComandoTB86000 comandoTB = new ComandoTB86000(numeroComando, new short[0]);
        enviarRecibirBytes(comandoTB.comandoToBytes());
        return "VGS";
    }

    /**
     * comando m
     * decimal 109
     * @return
     */

    public String comandoM() throws IOException{
        short numeroComando = 109;
        ComandoTB86000 comandoTB = new ComandoTB86000(numeroComando, new short[0]);
        enviarRecibirBytes(comandoTB.comandoToBytes());
        return "LD";
    }

    /**
     * comando z calibracion de cero de la pt100
     * decimal 122
     * @return
     */

    public String comandoZ() throws IOException{
        short numeroComando = 122;
        ComandoTB86000 comandoTB = new ComandoTB86000(numeroComando, new short[0]);
        enviarRecibirBytes(comandoTB.comandoToBytes());
        return "APV";
    }


    /**
     * comando g
     * calibracion de la ganancia de la pt100
     * decimal 103
     * @return
     */

    public String comandoG() throws IOException{
        short numeroComando = 103;
        ComandoTB86000 comandoTB = new ComandoTB86000(numeroComando, new short[0]);
        enviarRecibirBytes(comandoTB.comandoToBytes());
        return "NN";

    }

    /**
     * comando n solicitud de valores de cero y de ganancia
     * decimal: 110
     * @return
     */

    public String comandoN() throws IOException{
        short numeroComando = 110;
        ComandoTB86000 comandoTB = new ComandoTB86000(numeroComando, new short[0]);
        enviarRecibirBytes(comandoTB.comandoToBytes());
        return "Liv TYLEr";
    }


    /**
     * comando b
     * escritura y lectura del buffer de datos para
     * la lectura de las rpms
     *
     * @return
     */


    public void comandoB(){
        System.out.println("no implementado");
    }

    /**
     * comandox
     * Lectura de valores de eeprom
     * @return
     */

    public void comandoX(){
        System.out.println("No implementado");
    }






    @Override
    public int getRpm() {
        try {
            String str = comandoU(numeroTiempos, numeroCilindros);
            System.out.println(str);
        } catch (IOException ex) {
            System.out.println("Error obteniendo las rpms");
            throw new ArrayIndexOutOfBoundsException();
        }
        return rpms;
    }

    @Override
    public int getTemp() {
        try {
            comandoU(numeroTiempos, numeroCilindros);
        } catch (IOException ex) {
            System.out.println("Error obteniendo temperatura");
            throw new ArrayIndexOutOfBoundsException();
        }
        return temp;
    }


    public synchronized byte[] enviarRecibirBytes(byte arregloEnviar[]) throws IOException{

            out.write(arregloEnviar);
            System.out.println("Trama enviada: ");
            for(byte b: arregloEnviar)
                System.out.print(b+" ");
            //Leer la respuesta que envia el TB6000
            byte[] buffer = new byte[1024];
            int data = -1;

                int len = 0;
                int numeroIntentos = 0;
                while (numeroIntentos < 3){
                    while ((data = in.read()) > -1) {
                        buffer[len++] = (byte) data;
                    }
                    if (len > 0) {
                        byte[] arregloRetornar = new byte[len];
                        System.arraycopy(buffer, 0, arregloRetornar, 0, len);
                        System.out.println("");
                        String str = new String(arregloRetornar);
                        System.out.println(str);
                        for(short s: arregloRetornar)
                            System.out.print(s +" ");
                        return arregloRetornar;
                    } else {
                        System.out.println("No hubo respuesta desde el dispositivo");
                        //return null;
                        numeroIntentos++;
                        System.out.println("Numero Intentos: " + numeroIntentos++);
                        try {
                            Thread.sleep(250);
                        } catch (InterruptedException ex) {
                           Logger.getRootLogger().info("No hubo respuesta");
                        }

                    }
                }//end while intentos
                return null;

    }

    @Override
    public void setPuertoSerial(SerialPort puertoSerial) {
        try {
            this.puertoSerial = puertoSerial;
            this.out = puertoSerial.getOutputStream();
            this.in = puertoSerial.getInputStream();
        } catch (IOException ex) {
            System.out.println("error estableciendo los flujos de entrada y salida");
        }
    }

    @Override
    public SerialPort getPuertoSerial() {
        return puertoSerial;
    }

    public int getNumeroCilindros() {
        return numeroCilindros;
    }

    public int getNumeroTiempos() {
        return numeroTiempos;
    }

    private int rpmsFromU(byte[] b) {
        int rpmA = b[1]&0xFF;
        int rpmB = b[2]&0xFF;
        int rpmC = b[3]&0xFF;

        int rpmsCalc = rpmA*65536 + rpmB*256 + rpmC;
        return rpmsCalc;

    }

    private int tempFromU(byte[] b) {
        return (b[6] + b[5]*256);
    }

    public void setNumeroCilindros(int numeroCilindros) {
        this.numeroCilindros = numeroCilindros;
    }

    public void setNumeroTiempos(int numeroTiempos) {
        this.numeroTiempos = numeroTiempos;
    }

    @Override
    public void stop() { 
     this.puertoSerial.close();   
        
    }

    @Override
    public void setRpm(Integer rpm) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
