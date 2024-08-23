package com.soltelec.opacimetro.brianbee;

import gnu.io.NoSuchPortException;
import gnu.io.SerialPort;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.soltelec.util.MedicionOpacidad;
import org.soltelec.util.PortSerialUtil;

public class TestComandosOpacimetro
{
  public static void main(String[] args)
  {
    try
    {
      OpacimetroBrianBee opacimetro = new OpacimetroBrianBee();
      SerialPort puerto = PortSerialUtil.connect("COM1", 9600, 8, 1, 0);
      opacimetro.setPort(puerto);
      opacimetro.standBy();
      opacimetro.obtenerSerial();
      MedicionOpacidad medicion = new MedicionOpacidad();

      medicion = opacimetro.obtenerDatos();
      while (medicion.isTempTuboFueraTolerancia()) {
        medicion = opacimetro.obtenerDatos();
        System.out.println("Calentamiento..");
        Thread.sleep(100L);
      }
      opacimetro.calibrar();
      medicion = opacimetro.obtenerDatos();
      while (medicion.isCalibracionProgreso()) {
        medicion = opacimetro.obtenerDatos();
        System.out.println("Calibracion en progreso");

        Thread.sleep(50L);
      }
      while (true) {
        medicion = opacimetro.obtenerDatos();

        System.out.println("Medicion de transmitancia: " + medicion.getOpacidad());
        Thread.sleep(100L);
      }
    } catch (NoSuchPortException ex) {
      Logger.getLogger(TestComandosOpacimetro.class.getName()).log(Level.SEVERE, null, ex);
    } catch (Exception ex) {
      Logger.getLogger(TestComandosOpacimetro.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
}