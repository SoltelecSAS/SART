/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.medicionrpm.centralauto;

import gnu.io.SerialPort;
import org.soltelec.util.PortSerialUtil;

/**
 *
 * @author DANIELA SANA
 */
public class TestTB86000 {    
    
//
    public static void main(String args[]){
    try{
    SerialPort serialPort = PortSerialUtil.connect("COM3",9600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
    TB86000 tb6000 = new TB86000(serialPort);
    
//            String comandoSAY = tb6000.comandoSAY();
//            String comandoC = tb6000.comandoC();
//            String comandoR = tb6000.comandoR(4,4);
//            String comandoT = tb6000.comandoT();
//            String comandou = tb6000.comandoU(4,4);
//            String comandov = tb6000.comandoV();
//            String comandom = tb6000.comandoM();
//            String comandon = tb6000.comandoN();
    int rpms = -1;
    for (int i = 0; i< 1000; i++){
        rpms = tb6000.getRpm();
        System.out.println("rpm:" + rpms);
    }
      tb6000.getPuertoSerial().close();
    }
    catch(Exception exc){

    }
    finally{

    }
    }


}


