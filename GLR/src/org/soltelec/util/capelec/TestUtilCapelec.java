/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.util.capelec;

import java.text.DecimalFormat;
import org.soltelec.util.UtilGasesModelo;

/**
 *
 * @author User
 */
public class TestUtilCapelec {
    
    public static void main(String args[]) throws Exception{
        
//        byte numeroComando = 0x43;
//        byte numeroDatos = 0x10;
//        byte datos[] = {(byte)0x87,(byte)0x31,(byte)0x2e,(byte)0x35};
//        byte checksum = MensajeCapelec.obtenerCheckSum(numeroComando, numeroDatos, datos);
//        byte[] trama = UtilGasesCapelec.armarTrama( new MensajeCapelec(numeroComando, numeroDatos, datos));
//        
//       
//        System.out.println("checksum:" + Integer.toHexString(checksum & 0xff) );
//        
//        byte letraComando = 'A';
//        numeroDatos = 0x01;
//        datos = new byte[]{0x15};
//        checksum = MensajeCapelec.obtenerCheckSum(numeroComando, numeroDatos, datos);
//        trama = UtilGasesCapelec.armarTrama(new MensajeCapelec(letraComando, numeroDatos, datos));
//         MensajeCapelec mensaje2 = UtilGasesCapelec.armarMensajeCapelec(trama);
//        System.out.println("checksum2:" + Integer.toHexString( checksum&0xFF ));
//        
//        byte b = 65;
//        char c = (char) b;
//        
//        b = -128;
//        System.out.println("b es:" + b);
//        System.out.println("La A debe ser la" + c);
//       
//        if(c == 'A'){
//            System.out.println("comando correcto");
//        }
//        
//        b = (byte) 0xFF;
//        
//        System.out.println("Valor de byte:" + b);
//        
//       
//        System.out.println("Verdadero valor de a:" + UtilGasesModelo.byteToInt(b));
//        
//        byte[] arrayByte = {/*LSB*/(byte)0x80,/*MSB*/(byte)0x00};
//        System.out.println("Valor verdadero del arreglo:" + UtilGasesModelo.fromBytesToShort(arrayByte));
//        
//        arrayByte = new byte[]{0x05,0x05};
//        
//        System.out.println("Otro valor del byte:" + UtilGasesModelo.fromBytesToShort( arrayByte));
//        
//        //FLOAT
//        arrayByte = new byte[]{(byte)0x40,(byte)0x00,(byte)0xa3,(byte)0xd7};
//        
//        
//        System.out.println("Verdadero float 1:" + UtilGasesModelo.byteArrayToFloat(arrayByte));
//        
//        arrayByte = new byte[]{(byte)0x41,(byte)0x4e,(byte)0x66,(byte)0x66};
//        
//        System.out.println("Verdadero float 2:" + UtilGasesModelo.byteArrayToFloat(arrayByte));
//        
//        arrayByte = new byte[]{(byte)0x44,(byte)0xBB,(byte)0x40,(byte)0x00};
//        
//        System.out.println("Verdadero float 3:" + UtilGasesModelo.byteArrayToFloat(arrayByte));
//        
//        char letraC = 'C';
//        System.out.println("La letra C debe ser 65: " + (byte)letraC);
//        
//        byte pruebas[] = new byte[]{0x01,00};
//        
//        System.out.println("Pruebas debe ser uno :" + UtilGasesModelo.fromBytesToShort(pruebas));
//        short s = 1;
//        
//        boolean[] flags = UtilGasesModelo.shortToFlags(s);
//        
//        float f1 = 0;
//        float f2 = 1.256f;
//        float f3 = 1.25f;
//        float f4 = 0.1234f;
//        
//        
//        System.out.println("numero decimales f1:" + UtilGasesCapelec.numeroDecimales(f1));
//        System.out.println("numero decimales f2:" + UtilGasesCapelec.numeroDecimales(f2));
//        System.out.println("numero decimales f3:" + UtilGasesCapelec.numeroDecimales(f3));
//        System.out.println("numero de decimales f4:" + UtilGasesCapelec.numeroDecimales(f4));
        
           byte b = -128;
           int entero = UtilGasesModelo.byteToInt(b);
           boolean[] arregloBits = UtilGasesModelo.shortToFlags((short)entero);
           
           arregloBits[7] = false;
           
           short respuesta = UtilGasesModelo.fromFlagsToShort( arregloBits );
           byte respuestaByte = UtilGasesModelo.toBytes(respuesta)[0];
           System.out.println("Respuesta debe ser 64: " + respuesta);
           System.out.println("Respuesta byte :" + respuestaByte);
           
          for(BancoCapelec.TipoCalibracion tc: BancoCapelec.TipoCalibracion.values()){
              
              System.out.println("Valor de la calibracion:" + UtilGasesModelo.shortToString((byte)tc.getValor()));
              //UtilGasesModelo.shortToFlags( tc.getValor());
              
          }
          
       DecimalFormat df = new DecimalFormat("00.00");
            
           // DecimalFormat df = new DecimalFormat("00.00");
        
        String COFormateado = df.format(1.25);
        String CO2Formateado = df.format(0.21);
        
        df.applyPattern("00000");
        String HCFormateado = df.format(1200);
        
        
        for(int i = 0; i < 10; i+=2){
            System.out.println("Valor de i: " + i);
            System.out.println("Valor de i mas uno :" + (i +1));
            System.out.println("Valor de i dividido dos: " + i/2);
        }
        //concatenar todos los valores en un arrglo de bytes
        //concatenar toda la cadena y pasarla a el arreglo de bytes
        //pasar cada cadena a arreglos y unirlos
        
//        StringBuilder sb = new StringBuilder(COFormateado);
//        sb.append(CO2Formateado).append(HCFormateado);
//        
//        BancoGasolina banco = new BancoSensors();
//        banco = new BancoCapelec();
//            if(banco instanceof BancoSensors){
//                System.out.println("Prueba efectiva de l operador instanceof");
//            } else if (banco instanceof BancoCapelec){
//                System.out.println("Banco capelec");
//            }
//       JDialog d = new JDialog();
//       Window w = SwingUtilities.getWindowAncestor(d);
        
    }
    
}
