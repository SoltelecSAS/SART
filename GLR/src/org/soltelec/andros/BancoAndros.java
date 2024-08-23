
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.andros;

import gnu.io.SerialPort;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.soltelec.procesosbanco.BancoGasolina;
import org.soltelec.procesosbanco.PuntoCalibracion;
import org.soltelec.util.EstadoMaquina;
import org.soltelec.util.MedicionGases;
import org.soltelec.util.UtilGasesModelo;
import org.soltelec.util.capelec.BancoCapelec.TipoCalibracion;
import org.soltelec.util.capelec.UtilGasesCapelec;

/**
 *
 * @author User
 */
public class BancoAndros implements BancoGasolina{
    
    
    private SerialPort puertoSerial;

    @Override
    public MedicionGases obtenerDatos() {
        //Tambien es    tablece el modo de operacion
        MensajeAndros mensaje = armarMensajeDatos();
        MensajeAndros mensajeRespuesta = enviarMensaje(mensaje);
        MedicionGases medicion = extraerDatosMensaje(mensajeRespuesta);
        MensajeAndros mensajeRPMTEMP = new MensajeAndros(5);
        MensajeAndros respuestaRPMTEMP = enviarMensaje(mensajeRPMTEMP);
        List<Byte> lista = respuestaRPMTEMP.getListaDatos();
        
        byte rpmBajo = lista.get(11);
        byte rpmAlto = lista.get(10);
        int valorRpm = UtilGasesModelo.fromBytesToShort(rpmBajo, rpmAlto);
        medicion.setValorRPM(valorRpm);
        
        byte tempBajo = lista.get(1);
        byte tempAlto = lista.get(0);
        int valorTemp = UtilGasesModelo.fromBytesToShort(tempBajo, tempAlto);
        
        medicion.setValorTAceite(valorTemp);
        
        return medicion;      
        
    }

    private MensajeAndros armarMensajeDatos() {
        //$01 es el comando para obtener los Datos y el Estatus
        MensajeAndros mensaje = new MensajeAndros(1);
        mensaje.setNumeroBytes(3);
        List<Byte> lista = new ArrayList<Byte>();
        lista.add((byte)0x01);//transmision de un solo paquete de datos, sale del modo stand by
        lista.add((byte)0x00);//se reportan los datos de HC como Hexano
        mensaje.setListaDatos(lista);
        mensaje.recalcularChecksum();
        return mensaje;
    }

    @Override
    public String obtenerPEF() {
        
        MensajeAndros mensaje = new MensajeAndros(5);
        MensajeAndros mensajeRespuesta = enviarMensaje(mensaje);
        List<Byte> listaDatos = mensajeRespuesta.getListaDatos();
        byte PEFBajo = listaDatos.get(3);
        byte PEFAlto = listaDatos.get(2);
        int valorPEF = UtilGasesModelo.fromBytesToShort(PEFBajo, PEFAlto);
        return String.valueOf(valorPEF);
        
        
    }

    @Override
    public String numeroSerial() {
       
        MensajeAndros mensaje = new MensajeAndros(4);
        MensajeAndros mensajeRespuesta = enviarMensaje(mensaje);
        
        List<Byte> lista = mensajeRespuesta.getListaDatos();
        byte[] arregloCaracteres = new byte[6];
        for(int i=0; i < arregloCaracteres.length; i++){
            arregloCaracteres[i] = lista.get(i);         
        }
        String serial = new String(arregloCaracteres);
        return serial;    
        
    }

    @Override
    public String encenderBombaMuestras(boolean onOff) {
        //aunque existe un comando para encender y apagar la bomba se va a usar el comando 01
        
        
        List<Byte> lista = new ArrayList<Byte>();
        if(onOff){//true significa encender la bomba
            lista.add((byte)0x01);//este comando enciende la bomba y pide un paquete de datos
        } else {//false significa apagar la bomba
            lista.add((byte)0x00);//este comando para la transmision de datos continua, apaga la bomba y la fuente IR
        }
        lista.add((byte)0x00);//para que reporte los datos como HExano
        MensajeAndros mensaje = new MensajeAndros(1, lista);
        MensajeAndros mensajeRespuesta = enviarMensaje(mensaje);
        
        return "";      
        
    }

    @Override
    public String encenderSolenoideUno(boolean onOff) {
        
        List<Byte> lista = new ArrayList<Byte>();
        lista.add((byte)2);//Mascara
        
        if(onOff){//Enciende el solenoide
            lista.add((byte)2);
        } else {//Apaga el solenoide
            lista.add((byte)0);
        }
        
        MensajeAndros mensaje = new MensajeAndros(8,lista);
        MensajeAndros mensajeRespuesta = enviarMensaje(mensaje);
        
        return "";
    }

    @Override
    public String encenderSolenoide2(boolean onOff) {//En este banco segun la demostracion es el solenoide de Calibracion
       
        List<Byte> lista = new ArrayList<Byte>();
        lista.add((byte)4);//00000100
        if(onOff){//Encender es true
            lista.add((byte)4);
        } else {
            lista.add((byte)0);
        }
        MensajeAndros mensaje = new MensajeAndros(8, lista);
        MensajeAndros mensajeRespuesta = enviarMensaje(mensaje);
        
        return "";
     
     }

    @Override
    public String encenderCalSol1(boolean onOff) {
        
        List<Byte> lista = new ArrayList<Byte>();
        lista.add( (byte) 8);//mascara: 000001000
        if(onOff){
            lista.add((byte) 8);//Enciende el solenoide
        }else {
            lista.add((byte) 0);//Apaga el solenoide
        }
        
        MensajeAndros mensaje = new MensajeAndros(8, lista);
        enviarMensaje(mensaje);
        return "";
    }

    @Override
    public String encenderCalSol2(boolean onOff) {
       
        List<Byte> lista = new ArrayList<Byte>();
        lista.add( (byte) 16);//00010000 dice en el manual calibration check gas
        if(onOff){//Encender 
            
            lista.add((byte)16);
        }else{//Apagar
            lista.add((byte)0);
        }
        MensajeAndros mensaje = new MensajeAndros(8, lista);
        enviarMensaje(mensaje);
        return "";
        
    }

    @Override
    public String encenderDrainPump(boolean on) {
        //En el manual dice AUXOUT0 = 6900 high capacity pump. asi que supondre que esta seria la bomba de drenaje
        
        List<Byte> lista = new ArrayList<Byte>();
        lista.add((byte)1);//00000001 esa seria la mascara
        if(on){//Encender la bomba
          lista.add((byte)1);  
        }else{
          lista.add((byte)0);  
        }
        MensajeAndros mensaje = new MensajeAndros(8,lista);
        enviarMensaje(mensaje);
        return "";
        
    }

    @Override
    public EstadoMaquina statusMaquina() {
        
        MensajeAndros mensaje = new MensajeAndros(1);
        MensajeAndros mensajeRespuesta = enviarMensaje(mensaje);
        List<Byte> lista = mensajeRespuesta.getListaDatos();
        boolean[] STAT1 = UtilGasesModelo.shortToFlags(lista.get(0));
        EstadoMaquina estado = new EstadoMaquina();
        estado.setZero(STAT1[5]);
        estado.setCalibracionEnProgreso(STAT1[4]);
        estado.setZeroEnProgreso(STAT1[4]);
        
         if( !STAT1[7] && !STAT1[6] ){           
            estado.setModo(EstadoMaquina.MODO_OPERACION.NORMAL);            
        } else if(!STAT1[7] && STAT1[6]){            
            estado.setModo(EstadoMaquina.MODO_OPERACION.INICIALIZACION);//modo = MODO_OPERACION.INICIALIZACION;                     
        } else if (!STAT1[7] && !STAT1[6]){            
            estado.setModo(EstadoMaquina.MODO_OPERACION.STANDBY);
        } else if ( STAT1[7] && STAT1[6]){
            estado.setModo(EstadoMaquina.MODO_OPERACION.FALLO);
        }
         
         boolean[] STAT2 = UtilGasesModelo.shortToFlags(lista.get(1));
         estado.setHCDataValid( STAT2[2]);
         estado.setCODataValid( STAT2[4]);
         estado.setCO2DataValid( STAT2[6]);
         estado.setO2DataValid( STAT2[1]);
         
         boolean[] STAT3 = UtilGasesModelo.shortToFlags( lista.get(2));
         estado.setBadNOx( STAT3[6] );
         
         boolean[] STAT4 = UtilGasesModelo.shortToFlags( lista.get(3));
         estado.setBajoFlujo(STAT4[1]);
         
         estado.setNewNox( STAT4[6]);
         estado.setNewO2(  STAT4[5]);
         estado.setPruebaFugasAprobada( STAT4[0]);
         
         return estado;
        
    }

    @Override
    public short mapaFisicoES(short palabra, boolean leerEscribir) {//este metodo no es portable debido a que cada banco tiene su propia configuracion
        
        List<Byte> listaDatos = new ArrayList<Byte>();
        ///la mascara si se va a leer entonces se pone la mascara en cero
        if(leerEscribir){
            listaDatos.add((byte)0);//con mascara cero no afecta ninguna salida
        }
        else {
            listaDatos.add((byte)palabra);
        }
        listaDatos.add((byte)palabra);//las salidas si son fijas
        
        MensajeAndros mensaje = new MensajeAndros(8,listaDatos);
        MensajeAndros mensajeRespuesta = enviarMensaje(mensaje);
        List<Byte> lista = mensajeRespuesta.getListaDatos();
        return lista.get(0);      
    }

    @Override
    public void ordenarCalibracion(int modoCalibracion) {
        switch(modoCalibracion){
            case 0://Hacer calibración de cero
                
                List<Byte> lista = new ArrayList<Byte>();
                lista.add((byte)20);
                MensajeAndros mensaje = new MensajeAndros(2, lista);
                enviarMensaje(mensaje);
                
            break;                
            default:
                System.out.println("Solo soporta calibracion de cero");
            break;         
            
        }
    }

    @Override
    public void leerEscribirDatosCal(boolean leerEscribir, PuntoCalibracion ptoCal, int dataset) {
        System.out.println("No aplicable al banco Andros");
    }

    @Override
    public PuntoCalibracion leerDatosCalibracionPto1(int dataset) {
        System.out.println("No aplicable en el banco Andros");
        return null;
    }

    @Override
    public SerialPort getPuertoSerial() {
       return puertoSerial;
    }

    @Override
    public void setPuertoSerial(SerialPort serialPort) throws IOException {
        this.puertoSerial = serialPort;
    }

    @Override
    public void calibracion(TipoCalibracion tipoCalibracion, double valorCO, double valorCO2, int valorHC) {
        switch(tipoCalibracion){
            
            case UNPUNTO:
                    
               List<Byte> listaDatos = new ArrayList<Byte>();
               //la mascara para calibrar solamente HC, CO y CO2 seria 00000111 o sea 4+2+1 7
               listaDatos.add((byte)7);
               //
               int valorEnteroCO2 = (int)(valorCO2*100);
               byte[] arrayValorCO2 = UtilGasesModelo.toBytes((short)valorEnteroCO2);
               listaDatos.add(arrayValorCO2[1]);//el segundo es el mas significativo
               listaDatos.add(arrayValorCO2[0]);//si el primero es el menos significativo
               
               int valorEnteroCO = (int)(valorCO*100);//dos digitos es suficiente pero pueden ser tres ojo
               byte[] arrayValorCO = UtilGasesModelo.toBytes((short)valorEnteroCO);
               listaDatos.add(arrayValorCO[1]);
               listaDatos.add(arrayValorCO[0]);
               
               //valor de HC
               byte[] arrayValorHC = UtilGasesModelo.toBytes( (short) valorHC);
               listaDatos.add(arrayValorHC[1]);
               listaDatos.add(arrayValorHC[0]);
               
               MensajeAndros mensaje = new MensajeAndros(3,listaDatos);
               MensajeAndros mensajeRespuesta = enviarMensaje(mensaje);
               
                
            break;
            default:
                
                System.out.println("No se soportan los demas tipos de calibracion");
                
            break;    
                
            
        }
    }

    
    /**
     * Envia un mensaje del protocolo Andros hacia el puerto serial y espera 
     * una respuesta del aparato
     * @param mensaje
     * @return 
     */
    private synchronized  MensajeAndros enviarMensaje(MensajeAndros mensaje) {
        
        byte[] trama = UtilGasesAndros.armarTrama(mensaje);
        InputStream in = null;
        OutputStream out = null;
        
        
         try {
         //Enviar la trama del puerto serial   
         out = puertoSerial.getOutputStream();
         out.write(trama);
         
        
        } catch (IOException ioexc){
            Logger.getRootLogger().error("Error emviando el comando", ioexc);
            ioexc.printStackTrace();
            return null;
        }
        //leer la trama del puerto serial
        
        try{
            
            in = puertoSerial.getInputStream();
            System.out.println("Leyendo datos..-..");
                byte[] buffer = new byte[1024];
                int data = 0;
                int len = 0;
                while ( ( data = in.read() ) > -1 )
                  {
                    buffer[len++] = (byte)data;
                  }//end while          
                byte arreglo[] = new byte[len];
                //armar el mensaje
                System.arraycopy(buffer, 0, arreglo, 0, len);
                return UtilGasesAndros.armarMensaje(arreglo);
            
        }catch(IOException ioexc){
            System.out.println("Error leyendo los datos desde el puerto serial");
            ioexc.printStackTrace();
            return null;
        }        
    }//end of method

    private MedicionGases extraerDatosMensaje(MensajeAndros mensajeRespuesta) {
        
        List<Byte> lista = mensajeRespuesta.getListaDatos();
        MedicionGases medicion = new MedicionGases();        
        byte stat1 = lista.get(0);
        boolean[] flagsStat1 = UtilGasesModelo.shortToFlags(stat1);
        medicion.setHCpropano( flagsStat1[0] );
        medicion.setCero( flagsStat1[5]);
        medicion.setBombaEncendida( flagsStat1[1] );//Bomba encendida esto es nuevo
        //si el modo es Startup decimos que esta en calentamiento
        if(flagsStat1[6] && !flagsStat1[7]){
            medicion.setCalentamiento(true);
        }
        //boolean[] flagsStat2 = UtilGasesModelo.shortToFlags(lista.get(1));
        //poner las banderas correspondientes
        //boolean[] flagsStat3 = UtilGasesModelo.shortToFlags( lista.get(2));
        //poner las banderas correspondientes
        boolean[] flagsStat4 = UtilGasesModelo.shortToFlags(lista.get(3));
        medicion.setPruebaFugasAprobada( !flagsStat4[0]);
        medicion.setBajoFlujo( !flagsStat4[1] );
        medicion.setTempAmbienRange(!flagsStat4[2]);
        //falta incluir banderas de este byte de estatus, se pueden poner en otro metodo
        byte CO2Bajo = lista.get(5);
        byte CO2Alto = lista.get(4);
        int valorCO2 = UtilGasesModelo.fromBytesToShort(CO2Bajo, CO2Alto);
        medicion.setValorCO2(valorCO2);
        
        byte COBajo = lista.get(7);
        byte COAlto = lista.get(6);
        
        int valorCO = UtilGasesModelo.fromBytesToShort(COBajo, COAlto);
        medicion.setValorCO(valorCO);
        
        byte HCMasBajo = lista.get(11);
        byte HCBajo = lista.get(10);
        byte HCMedio = lista.get(9);
        byte HCAlto = lista.get(8);
        
        int valorHC = UtilGasesModelo.bytesToInt(HCMasBajo, HCBajo, HCMedio, HCAlto);
        medicion.setValorHC(valorHC);
        
        byte O2Bajo = lista.get(13);
        byte O2Alto = lista.get(12);
        
        int valorO2 = UtilGasesModelo.fromBytesToShort(O2Bajo, O2Alto);
        medicion.setValorO2(valorO2);
        
        byte NOxBajo = lista.get(15);
        byte NOxAlto = lista.get(14);
        int valorNOx = UtilGasesModelo.fromBytesToShort(NOxBajo, NOxAlto);
        medicion.setValorNox(valorNOx);
        
        return medicion;
        
        
        
        
    }
        
     
   public void resetCalibracion(){
       
       List<Byte> list = new ArrayList<Byte>();
       //Mascara por defecto resetea los tres valores
       list.add((byte)7);
       MensajeAndros mensaje = new MensajeAndros(9,list);
       enviarMensaje(mensaje);
       
   } 
        
   
   public void hacerPruebaDeFugas(){
       
       List<Byte> list = new ArrayList<Byte>();
       list.add((byte)0x1E);//30 segundos que la bomba crea el vacio
       list.add((byte)0x1E);//30 segundos de tiempo de espera
       list.add((byte)0x73);
       
       MensajeAndros mensaje = new MensajeAndros(0x0B, list);
       MensajeAndros mensajeRespuesta = enviarMensaje(mensaje);
       
   }

    @Override
    public void setFactorRPM(int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
        
    
    
}
