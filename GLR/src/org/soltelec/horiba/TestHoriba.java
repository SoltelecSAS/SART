/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.horiba;

//import com.sun.xml.internal.fastinfoset.sax.SAXDocumentSerializer;
import gnu.io.SerialPort;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.soltelec.util.MedicionGases;
import org.soltelec.util.PortSerialUtil;

/**
 *
 * @author User
 */
public class TestHoriba {
    
    public static void main(String args[]){
        try {
            SerialPort puerto = PortSerialUtil.connect("COM1", 9600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
            BancoHoriba bancoHoriba = new BancoHoriba();
            bancoHoriba.setPuertoSerial(puerto);
            String serial = bancoHoriba.numeroSerial();
            System.out.println("Serial: " + serial);
            String pef = bancoHoriba.obtenerPEF();
            System.out.println("Pef es:" + pef);
//            bancoHoriba.encenderBombaMuestras(true);
//            Thread.sleep(3000);
//            bancoHoriba.encenderBombaMuestras(false);
//            bancoHoriba.encenderSolenoideUno(true);
//            Thread.sleep(5000);
//            bancoHoriba.encenderSolenoideUno(false);
//            Thread.sleep(1000);
//            bancoHoriba.encenderSolenoide2(true);
//            Thread.sleep(2000);
//            bancoHoriba.encenderSolenoide2(false);
//            Thread.sleep(1000);
//            bancoHoriba.encenderCalSol1(true);
//            Thread.sleep(1500);
//            bancoHoriba.encenderCalSol1(false);
//            Thread.sleep(2000);
//            bancoHoriba.encenderCalSol2(true);
//            Thread.sleep(3000);
//            bancoHoriba.encenderCalSol2(false);
//            Thread.sleep(1500);
//            bancoHoriba.encenderDrainPump(true);
//            Thread.sleep(3000);
//            bancoHoriba.encenderDrainPump(false);
            
            for(int i=0; i < 100; i++){
                Thread.sleep(100);
                MedicionGases medicion = bancoHoriba.obtenerDatos();
                System.out.println("Valor de HC:" + medicion.getValorHC());
                System.out.println("i: "+ i + "**********************************************");
            }
            
            puerto.close();
            
        } catch (Exception ex) {
            Logger.getLogger(TestHoriba.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
}
