package com.soltelec.opacimetro.brianbeeold;
/*    */ 
/*    */ import gnu.io.NoSuchPortException;
/*    */ import java.io.PrintStream;
/*    */ import java.util.logging.Level;
/*    */ import java.util.logging.Logger;
/*    */ import org.soltelec.util.MedicionOpacidad;
/*    */ 
/*    */ public class TestComandosOpacimetro
/*    */ {
/*    */   public static void main(String[] args)
/*    */   {
/*    */     try
/*    */     {
/* 15 */       OpacimetroBrianBee opacimetro = new OpacimetroBrianBee();
/* 16 */       gnu.io.SerialPort puerto = org.soltelec.util.PortSerialUtil.connect("COM3", 9600, 8, 1, 0);
/* 17 */       opacimetro.setPort(puerto);
/* 18 */       opacimetro.standBy();
/* 19 */       opacimetro.obtenerSerial();
/*    */       
/* 21 */       MedicionOpacidad medicion = opacimetro.obtenerDatos();
/* 22 */       while (medicion.isTempTuboFueraTolerancia()) {
/* 23 */         medicion = opacimetro.obtenerDatos();
/* 24 */         System.out.println("Calentamiento..");
/* 25 */         Thread.sleep(100L);
/*    */       }
/* 27 */       opacimetro.calibrar();
/* 28 */       medicion = opacimetro.obtenerDatos();
/* 29 */       while (medicion.isCalibracionProgreso()) {
/* 30 */         medicion = opacimetro.obtenerDatos();
/* 31 */         System.out.println("Calibracion en progreso");
/*    */         
/* 33 */         Thread.sleep(50L);
/*    */       }
/*    */       for (;;) {
/* 36 */         medicion = opacimetro.obtenerDatos();
/*    */         
/* 38 */         System.out.println("Medicion de transmitancia: " + medicion.getOpacidad());
/* 39 */         Thread.sleep(100L);
/*    */       }
/*    */     } catch (NoSuchPortException ex) {
/* 42 */       Logger.getLogger(TestComandosOpacimetro.class.getName()).log(Level.SEVERE, null, ex);
/*    */     } catch (Exception ex) {
/* 44 */       Logger.getLogger(TestComandosOpacimetro.class.getName()).log(Level.SEVERE, null, ex);
/*    */     }
/*    */   }
/*    */ }


/* Location:              /home/luisberna/Dropbox/maven-soltelec/Sart-git-3/modulos/ModuloGLR.jar!/com/soltelec/opacimetro/brianbee/TestComandosOpacimetro.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */