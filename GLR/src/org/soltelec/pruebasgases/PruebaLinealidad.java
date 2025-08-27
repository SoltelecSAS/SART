/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.pruebasgases;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import org.soltelec.procesosbanco.RegVefCalibraciones;
import org.soltelec.procesosopacimetro.Opacimetro;
import static org.soltelec.procesosopacimetro.Opacimetro.LIMITE_FILTROS;
import org.soltelec.procesosopacimetro.OpacimetroSensors;
import org.soltelec.util.LeerArchivo;
import org.soltelec.util.MedicionOpacidad;
import org.soltelec.util.Mensajes;
import org.soltelec.util.PortSerialUtil;

/**
 * Clase para implementar la prueba de linealidad del opacimetro
 *
 * @author Usuario - GerenciaDesarrollo
 */
public class PruebaLinealidad implements Runnable, ActionListener {

    private PanelPruebaGases panel;
    private Opacimetro opacimetro;
    private Timer timer;
    private int contadorTemporizacion;
    private DialogoFiltros dialogo;
    double valorFiltro1, valorFiltro2, valorFiltro3, valorFiltro4;
    private MedicionOpacidad med;
    private ArrayList<Double> listaMed;
    private boolean filtro1Coincide, filtro2Coincide, filtro3Coincide, filtroCuatroCoincide,filtroDiferencia;//falso por defecto

    private double promedioParaFiltro1;
    private double promedioParaFiltro2;
    private double promedioParaFiltro3;
    private double promedioParaFiltro4;

    private int idUser;

    public PruebaLinealidad() {
        dialogo = new DialogoFiltros();
        dialogo.setTitle("SART Version:1.7.3 copyright  2009 ");
        dialogo.setIconImage(getIconImage());
        
        opacimetro = new OpacimetroSensors();

    }//end of constructor

    public PruebaLinealidad(Opacimetro op, int idUser) {
        dialogo = new DialogoFiltros((op.obtenerSerial()));
        opacimetro = op;
        this.idUser = idUser;
    }
    
    public Image getIconImage() {
        ImageIcon image = new ImageIcon(this.getClass().getResource("/com/soltelec/Icon/car.png"));
        return image.getImage();
    }

    @Override
    public void run() {
        boolean toggle = true;
        panel.getPanelMensaje().setText("Buscando Opacimetro por favor Espere ..!");
        panel.getProgressBar().setVisible(false);
        panel.getButtonFinalizar().setVisible(false);
        filtroDiferencia=true;
        dialogo.setVisible(true);
        valorFiltro1 = dialogo.getValorFiltro1();
        valorFiltro2 = dialogo.getValorFiltro2();
        valorFiltro3 = dialogo.getValorFiltro3();
        valorFiltro4 = dialogo.getValorFiltro4();
        System.out.println("Valor Filtro Uno: " + valorFiltro1);
        System.out.println("Valor Filtro Dos: " + valorFiltro2);
        System.out.println("Valor Filtro Tres: " + valorFiltro3);
        System.out.println("Valor Filtro Cuatro: " + valorFiltro4);
//        if(opacimetro == null){
//            return;//TODO escribir resultado preuba linealidad
//        }//
        int opcion;
        int valorFiltro1Comp=1;
        panel.getPanelMensaje().setText("Calibracion de Cero\n Asegurese que no existan residuos\n ni particulas en el opacimetro");
        dormir(1000);
        do {
            opcion = JOptionPane.showConfirmDialog(panel, "Calibrar", "Calibracion", JOptionPane.YES_NO_OPTION);
        } while (opcion != JOptionPane.YES_OPTION);

        opacimetro.calibrar();
        med = opacimetro.obtenerDatosEstatusBruto();
        while (med.isZeroEnProgreso()) {
            if (toggle) {
                panel.getPanelMensaje().setText("CALIBRACION DE CERO.");
            } else {
                panel.getPanelMensaje().setText("CALIBRACION DE CERO ....");
            }
            toggle = !toggle;
            med = opacimetro.obtenerDatosEstatusBruto();
            dormir(100);
        }
        dormir(1200);
        panel.getPanelMensaje().setVisible(true);
        panel.getPanelMensaje().setText("Introduzca el Filtro # 1");
        dormir(1000);
        do {
            opcion = JOptionPane.showConfirmDialog(panel, "Filtro#1 Introducido?", "Filtro1", JOptionPane.YES_NO_OPTION);
        } while (opcion != JOptionPane.YES_OPTION);

        panel.getPanelMensaje().setText("Filtro1 : Tomando Medidas");
        promedioParaFiltro1 = obtenerPromedioMedidas();
         System.out.println("Valor Recogido del opa: "+promedioParaFiltro1);
        if ((promedioParaFiltro1 >= (valorFiltro1 - valorFiltro1Comp )) && ( promedioParaFiltro1 <= (valorFiltro1 + valorFiltro1Comp))) {
             Double difAritmetica1= Math.abs(promedioParaFiltro1-valorFiltro1);
            if ((difAritmetica1 > 1)) {
                filtro1Coincide = false;
                System.out.println("Medidas no coinciden con el Valor del Filtro1");
                panel.getPanelMensaje().setText("Medidas no coinciden con el Valor\n del Filtro 1 ");
                dormir(1200);
            } else {
                filtro1Coincide = true;
                panel.getPanelMensaje().setText("Verificacion del Filtro1 OK");
                System.out.println("Filtro1 OK");
                dormir(1000);
            }            
        } else {
            filtro1Coincide = false;
            System.out.println("Medidas no coinciden con el Valor del Filtro1 ");
            panel.getPanelMensaje().setText("Medidas no coinciden con el Valor \n del Filtro1");
            dormir(1200);
        }

        panel.getPanelMensaje().setText("Introduzca el Filtro # 2");
        do {
            opcion = JOptionPane.showConfirmDialog(panel, "Filtro#2 Introducido?", "Filtro2", JOptionPane.YES_NO_OPTION);
        } while (opcion != JOptionPane.YES_OPTION);

        panel.getPanelMensaje().setText("Filtro 2: Tomando Medidas ... ");
        promedioParaFiltro2 = obtenerPromedioMedidas();
        System.out.println("Valor Recogido(2) del opa: "+promedioParaFiltro2);
        
       
        if ((promedioParaFiltro2 >= (valorFiltro2 - LIMITE_FILTROS)) && (promedioParaFiltro2 <= (valorFiltro2 + LIMITE_FILTROS))) {
            Double difAritmetica2= Math.abs(promedioParaFiltro2-valorFiltro2);       
            if ((difAritmetica2 > 2)) {
                filtro2Coincide = false;
                System.out.println("Medidas no coinciden con el Valor del Filtro2");
                panel.getPanelMensaje().setText("Medidas no coinciden con el Valor\n del Filtro 2 ");
                dormir(1200);
            } else {
                filtro2Coincide = true;
                System.out.println("Filtro2 OK");
                panel.getPanelMensaje().setText("Verificacion del Filtro2 OK");
                dormir(1200);
            }         
        } else {
            filtro2Coincide = false;
            System.out.println("Medidas no coinciden con el Valor del Filtro2");
            panel.getPanelMensaje().setText("Medidas no coinciden con el Valor\n del Filtro 2 ");
            dormir(1200);
        }
        panel.getPanelMensaje().setText("Introduzca Filtro 3");
        do {
            opcion = JOptionPane.showConfirmDialog(panel, "Filtro#3 Introducido?", "Filtro3", JOptionPane.YES_NO_OPTION);
        } while (opcion != JOptionPane.YES_OPTION);
        promedioParaFiltro3 = obtenerPromedioMedidas();
        System.out.println("Valor Recogido(3) del opa: "+promedioParaFiltro3);
        if ((promedioParaFiltro3 >= (valorFiltro3 - LIMITE_FILTROS)) && (promedioParaFiltro3 <= (valorFiltro3 + LIMITE_FILTROS))) {
            Double difAritmetica3= Math.abs(promedioParaFiltro3-valorFiltro3);       
            if ((difAritmetica3 > 2)) {
                filtro3Coincide = false;
                System.out.println("Medidas no coinciden con el Valor del Filtro3");
                panel.getPanelMensaje().setText("Medidas no coinciden con el Valor\n del Filtro 3 ");
                dormir(1200);
            } else {
                filtro3Coincide = true;
                panel.getPanelMensaje().setText("Verificacion del Filtro3 OK");
                System.out.println("Filtro3 OK");
                dormir(1200);
            }    
        } else {
            filtro3Coincide = false;
            panel.getPanelMensaje().setText("Medidas no coinciden con el Valor\n del Filtro 3");
            System.out.println("Medidas no coinciden con el valor Del Filtro3");
        }
        do {
            opcion = JOptionPane.showConfirmDialog(panel, "Filtro#4 Introducido?", "Filtro4", JOptionPane.YES_NO_OPTION);
        } while (opcion != JOptionPane.YES_OPTION);

        promedioParaFiltro4 = obtenerPromedioMedidas();        
         if (promedioParaFiltro4 >= 91) {
             Double difAritmetica4= Math.abs(promedioParaFiltro4-valorFiltro4); 
             if ((difAritmetica4 > 1)) {
                 filtroCuatroCoincide = false;
                 System.out.println("Medidas no coinciden con el Valor del Filtro4");
                 panel.getPanelMensaje().setText("Medidas no coinciden con el Valor\n del Filtro 4 por diferencia aritmetica");
                 dormir(10000);
             } else {
                 filtroCuatroCoincide = true;
                 String val = String.valueOf(promedioParaFiltro4);
                 System.out.println("valor del filtro en str " + val);
                 promedioParaFiltro4 = Double.parseDouble("99.".concat(val.substring(3, 4)));
                 filtroCuatroCoincide = true;
                 panel.getPanelMensaje().setText("Verificacion del Filtro 4 OK");
                 System.out.println("Filtro 4 OK");
                 dormir(1200);
             }
        } else {
            filtroCuatroCoincide = false;
            panel.getPanelMensaje().setText("Medidas no coinciden con el Valor\n del Filtro 4");
            System.out.println("Medidas no coinciden con el valor del Filtro 4");
            dormir(10000);
        }
//        if(valorFiltro2>(valorFiltro3*0.15)){
//            
//        }
         Double difArtimeticaPuntos=Math.abs(promedioParaFiltro3-promedioParaFiltro2); 
          if (difArtimeticaPuntos <15) {
              filtroDiferencia=false;
          }
        long serial = opacimetro.obtenerSerial();
        String strSerial = String.valueOf(serial);

        RegVefCalibraciones rc = new RegVefCalibraciones();
        if (filtro1Coincide && filtro2Coincide && filtro3Coincide && filtroCuatroCoincide) {
            panel.getPanelMensaje().setText("Prueba Realizada con Exito");
            try {                  
                rc.registrarPruebaLinelidad(strSerial, true,valorFiltro1,valorFiltro2,valorFiltro3,valorFiltro4,promedioParaFiltro1,promedioParaFiltro2,promedioParaFiltro3,promedioParaFiltro4, idUser);
            } catch (SQLException | ClassNotFoundException | IOException ex) {
                Mensajes.mostrarExcepcion(ex);
            }
        } else {
            StringBuilder sb = new StringBuilder();
            if (!filtroDiferencia) {
                sb.append("la diferencia Aritmetica entre Filtro2 y Filtro3 esmenor a 15% ");
            }
            if (!filtro1Coincide) {
                sb.append("El valor del Filtro1 no Coincide");
            }
            if (!filtro2Coincide) {
                sb.append("\nEl valor del Filtro2 no Coincide");
            }
            if (!filtro3Coincide) {
                sb.append("\nEl valor del Filtro3 no Coincide");
            }
            if (!filtroCuatroCoincide) {
                sb.append("\n El valor del Filtro 4 no Coincide");
            }

            panel.getPanelMensaje().setText("Prueba Finalizada sin Exito\n");
            panel.getPanelMensaje().appendText(sb.toString());

            try {
                rc.registrarPruebaLinelidad(strSerial, false,valorFiltro1,valorFiltro2,valorFiltro3,valorFiltro4,promedioParaFiltro1,promedioParaFiltro2,promedioParaFiltro3,promedioParaFiltro4,1);
            } catch (SQLException | ClassNotFoundException | IOException ex) {
                Mensajes.mostrarExcepcion(ex);
            }
        }
        try {
            Thread.sleep(2500);
        } catch (InterruptedException ignorada) {
        }
        panel.cerrar();

    }

    private double obtenerPromedioMedidas() {
        DecimalFormat df = new DecimalFormat("#0.0#");
        listaMed = new ArrayList<>();
        contadorTemporizacion = 0;
        crearNuevoTimer();
        timer.start();
        med = opacimetro.obtenerDatosEstatusBruto();

        double factor = LeerArchivo.getFactorOpacidad();
        // CAMBIAR MULTIPLICADOR DE OPACIDAD START
        while (contadorTemporizacion < 4) {
            dormir(100);
            med = opacimetro.obtenerDatosEstatusBruto();
             if (med.getOpacidadDouble()>=91) {
                 String val = String.valueOf(med.getOpacidadDouble());
                 System.out.println("valor del filtro en Linealidad " + val);                   
                 panel.getPanelMensaje().setText("Opacidad: " + df.format(Double.parseDouble("99.".concat(val.substring(3, 4)))) + "  t: " + contadorTemporizacion);
                 listaMed.add(Double.parseDouble("99.".concat(val.substring(3, 4))));
             }else{
                 panel.getPanelMensaje().setText("Opacidad: " + df.format(med.getOpacidadDouble() * factor) + "  t: " + contadorTemporizacion);
                 listaMed.add(med.getOpacidadDouble() * factor);
             }        
            
        } // CAMBIAR MULTIPLICADOR DE OPACIDAD END
        
        timer.stop();
        //end of while
        double promedio = 0;
        double suma = 0;
        for (Double d : listaMed) {
            suma += d;
        } //end for
        //end for
        promedio = suma / listaMed.size();
        return promedio;
    }//end of method

    public Opacimetro getOpacimetro() {
        return opacimetro;
    }

    public void setOpacimetro(Opacimetro opacimetro) {
        this.opacimetro = opacimetro;
    }

    public PanelPruebaGases getPanel() {
        return panel;
    }

    public void setPanel(PanelPruebaGases panel) {
        this.panel = panel;
    }

    public static void main(String args[]) {
        PanelPruebaGases panel = new PanelPruebaGases();
        JDialog d = new JDialog();
        d.getContentPane().add(panel);
        d.setSize(d.getToolkit().getScreenSize());
        PruebaLinealidad p = new PruebaLinealidad();
        p.setPanel(panel);
        Thread t = new Thread(p);
        t.start();
        d.setVisible(true);
    }

    private void dormir(int tiempo) {
        try {
            Thread.sleep(tiempo);
        } catch (InterruptedException ex) {
            Logger.getLogger(PruebaLinealidad.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        contadorTemporizacion++;
    }

    private void crearNuevoTimer() {
        timer = new Timer(1000, this);
    }

    public boolean buscarOpacimetro(Opacimetro opacimetro) {
        HashSet<CommPortIdentifier> puertosSeriales = PortSerialUtil.getAvailableSerialPorts();
        if (puertosSeriales.isEmpty()) {
            System.out.println("No hay puertos Seriales Conectados");
            return false;
        }//end if
        Iterator<CommPortIdentifier> itr = puertosSeriales.iterator();
        while (itr.hasNext()) {
            try {
                CommPortIdentifier portIdentifier = itr.next();
                CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);
                SerialPort serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                opacimetro.setPort(serialPort);
                System.out.println("Buscando opacimetro en:" + portIdentifier.getName());
                int frecMuestreo = opacimetro.obtenerFrecuenciaMuestreo();
                if (frecMuestreo > 0) {
                    System.out.println("Opacimetro encontrado en" + portIdentifier.getName() + "Frecuencia Muestreo" + frecMuestreo);
                    System.out.println("Flujos de Entrada/Salida del banco configurados");
                    return true;
                } else {
                    System.out.println("Opacimetro no encontrado en " + portIdentifier.getName());
                    serialPort.close();
                }
            } //end of while
            catch (PortInUseException ex) {

            } catch (UnsupportedCommOperationException ucoe) {
                ucoe.printStackTrace(System.err);
            }
        }//end of while
        return false;//retorna falso si sale por aca
    }//end method

}//end of class

/**
 * Clase para ingresar los valores de los filtros para la prueba de linealidad
 *
 * @author Usuario
 */
