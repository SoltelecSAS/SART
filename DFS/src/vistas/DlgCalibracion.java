/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DlgCalibracion.java
 *
 * Created on 9/05/2011, 10:45:45 AM
 */
package vistas;

import com.soltelec.modulopuc.utilidades.Mensajes;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Robin
 */
public class DlgCalibracion extends javax.swing.JDialog {

    final double gravedad = 9.81;
    private PuertoRS232 puerto;
    private String puertotarjeta;
    private final byte[] buffer = new byte[1024];
    private byte data;
    private final int[] ComandoRecibido = new int[4];
    private int j = 0, i = 0, len = -1, partealta, t = 0;
    private BufferedReader config;

    private final List<Integer> Datos1 = new ArrayList<>(); //Datos correspondientes al canal a calibrar
    private final double[] muestrasfd = new double[4];
    private final double[] muestrasfi = new double[4];
    private final double[] patronesfd = new double[4];
    private final double[] patronesfi = new double[4];
    private final double[] muestraspd = new double[4];
    private final double[] muestraspi = new double[4];
    private final double[] patronespd = new double[4];
    private final double[] patronespi = new double[4];
    private final double[] muestrasvd = new double[4];
    private final double[] muestrasvi = new double[4];
    private final double[] patronesvd = new double[4];
    private final double[] patronesvi = new double[4];
    private final double[] muestrasd = new double[3];
    private final double[] patronesd = new double[3];
    private double spanfd, spanfi, spanpd, spanpi, spanvd, spanvi, spand;
    private double offsetfd, offsetfi, offsetpd, offsetpi, offsetvd, offsetvi, offsetd;

    private String line;
    private int numpuntofd = 0, numpuntofi = 0, numpuntopd = 0, numpuntopi = 0, numpuntovd = 0, numpuntovi = 0, numpuntod = 0;
    private String uniseleccionada = "Kg";

    /**
     * Creates new form dialogcalibracion
     *
     * @param parent
     * @param modal
     */
    public DlgCalibracion(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setSize(this.getToolkit().getScreenSize());
        configuracion();
    }

    private void configuracion() {
        try {
            config = new BufferedReader(new FileReader(new File("configuracion.txt")));
            while (!config.readLine().startsWith("[DAQ]")) {
            }
            if ((line = config.readLine()).startsWith("puerto:")) {
                puertotarjeta = line.substring(line.indexOf(" ") + 1, line.length());
                //System.out.println(puertotarjeta);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            System.out.println("no se pudo abrir" + ex);
        }
        puerto = new PuertoRS232();
        try {
            puerto.connect(puertotarjeta);
        } catch (Exception e) {
            Mensajes.mostrarExcepcion(e);
        }
    }

    public void comandoSTOP() {
        try {
            puerto.out.write('H');
            puerto.out.write('B');
            puerto.out.write('W');
        } catch (IOException ex) {
            System.out.println("Error en el comando de STOP");
        }
    }

    public void comandoSERV(byte canal) {
        if (canal <= 8) {
            try {
                puerto.out.write('H');
                puerto.out.write('I');
                puerto.out.write(canal);
                puerto.out.write('W');
            } catch (IOException ex) {
                System.out.println("Error en el comando de servicio");
            }
        }
    }

    public void comandoCONEX() {
        try {
            puerto.out.write('H');
            puerto.out.write('J');
            puerto.out.write('W');
        } catch (IOException ex) {
            System.out.println("Error en el comando de conexión a la tarjeta");
        }
    }

    public boolean PruebaConexion() {
        String respuesta;
        comandoCONEX();
        try {
            Thread.currentThread().sleep(200);
            puerto.in.read(buffer);
        } catch (InterruptedException | IOException ex) {
            Logger.getLogger(DlgFrenoMoto.class.getName()).log(Level.SEVERE, null, ex);
        }
        respuesta = new String(buffer);
        //System.out.println("r"+respuesta);
        return respuesta.startsWith("HDAQ1W");
    }

    public double CalcularMedia(List<Integer> Datos) {
        long b, s;
        double c;
        s = Datos.size();
        //System.out.println("El numero de datos es: "+s);
        b = 0;
        for (t = 0; t < s; t++) {
            b += Datos.get(t);
        }
        c = (double) b / s;
        //System.out.println(c);
        return c;
    }

    public double CalcularMediana(List<Integer> Datos) {
        int s, r, auxMediana;
        double c;
        s = Datos.size();
        if (s == 0) {
            return 0;
        }
        if (s == 1) {
            return Datos.get(0);
        }
        for (t = 0; t < s; t++) {
            for (r = t + 1; r < s; r++) {
                if (Datos.get(r) > Datos.get(t)) {
                    auxMediana = Datos.get(t);
                    Datos.set(t, Datos.get(r));
                    Datos.set(r, auxMediana);
                }
            }
        }
        if (s % 2 == 0) {
            c = (double) (Datos.get((s - 1) / 2) + Datos.get((s - 1) / 2 + 1)) / 2;
        } else {
            c = (double) Datos.get(s / 2);
        }
        return c;
    }

    public double[] Regresionlineal(double a[], double b[]) {
        double mediax, mediay, sumx = 0, sumy = 0, sumxy = 0, sumx2 = 0;
        double[] valores = new double[2];
        byte k;
        for (k = 0; k < a.length; k++) {
            sumx += a[k];
            sumy += b[k];
            sumxy += a[k] * b[k];
            sumx2 += a[k] * a[k];
        }
        mediax = sumx / a.length;
        mediay = sumy / b.length;
         JOptionPane.showMessageDialog(null, "sum  x "+sumx,
                    "Error", JOptionPane.ERROR_MESSAGE);
         
          JOptionPane.showMessageDialog(null, "sum  y "+sumy,
                    "Error", JOptionPane.ERROR_MESSAGE);
         
        JOptionPane.showMessageDialog(null, "sum media x "+mediax,
                    "Error", JOptionPane.ERROR_MESSAGE);
        JOptionPane.showMessageDialog(null, "sum media Y "+mediay,
                    "Error", JOptionPane.ERROR_MESSAGE);
        valores[0] = (sumxy - mediay * sumx) / (sumx2 - mediax * sumx);   //pendiente calculada
        valores[1] = mediay - valores[0] * mediax;                  //interesecto con cero calculado
        return valores;
    }

    public void CapturarFD() {
        double muestra;
        DecimalFormat formateador = new DecimalFormat("####.00");
        comandoSERV((byte) 0);
        for (t = 0; t < 4; t++) {
            try {
                Thread.currentThread().sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(DlgCalibracion.class.getName()).log(Level.SEVERE, null, ex);
            }
            CapturarDatos();
        }
        comandoSTOP();
        muestra = CalcularMediana(Datos1);
        Datos1.clear();
        try {
            switch (numpuntofd) {
                case 0:
                    muestrasfd[0] = muestra;
                    patronesfd[0] = 0;
                    CampoTexPun1FD.setEditable(true);
                    LabelPun0FD.setText(formateador.format(muestra));
                    LabelPun0FD.setBackground(Color.green);
                    LabelPun1FD.setBackground(Color.red);
                    break;
                case 1:
                    muestrasfd[1] = muestra;
                    patronesfd[1] = Double.parseDouble(CampoTexPun1FD.getText());
                    CampoTexPun1FD.setEditable(false);
                    CampoTexPun2FD.setEditable(true);
                    LabelPun1FD.setText(formateador.format(muestra));
                    LabelPun1FD.setBackground(Color.green);
                    LabelPun2FD.setBackground(Color.red);
                    break;
                case 2:
                    muestrasfd[2] = muestra;
                    patronesfd[2] = Double.parseDouble(CampoTexPun2FD.getText());
                    CampoTexPun2FD.setEditable(false);
                    CampoTexPun3FD.setEditable(true);
                    LabelPun2FD.setText(formateador.format(muestra));
                    LabelPun2FD.setBackground(Color.green);
                    LabelPun3FD.setBackground(Color.red);
                    break;
                case 3:
                    muestrasfd[3] = muestra;
                    patronesfd[3] = Double.parseDouble(CampoTexPun3FD.getText());
                    CampoTexPun3FD.setEditable(false);
                    LabelPun3FD.setText(formateador.format(muestra));
                    LabelPun3FD.setBackground(Color.green);
                    BotonCalcularFD.setEnabled(true);
                    break;
            }
            numpuntofd++;
            if (numpuntofd == 4) {
                BotonCapturarFD.setEnabled(false);
            }
            if (numpuntofd == 1) {
                BotonAnteriorFD.setEnabled(true);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Introduzca un valor correcto en el patron",
                    "Error", JOptionPane.ERROR_MESSAGE);
            if (CampoTexPun1FD.isEditable()) {
                CampoTexPun1FD.setText("");
            }
            if (CampoTexPun2FD.isEditable()) {
                CampoTexPun2FD.setText("");
            }
            if (CampoTexPun3FD.isEditable()) {
                CampoTexPun3FD.setText("");
            }
        }
    }

    public void AnteriorFD() {
        switch (numpuntofd) {
            case 1:
                CampoTexPun1FD.setEditable(false);
                LabelPun1FD.setText("");
                LabelPun0FD.setBackground(Color.red);
                LabelPun1FD.setBackground(jPanel7.getBackground());
                break;
            case 2:
                CampoTexPun1FD.setEditable(true);
                CampoTexPun2FD.setEditable(false);
                LabelPun2FD.setText("");
                LabelPun1FD.setBackground(Color.red);
                LabelPun2FD.setBackground(jPanel7.getBackground());
                break;
            case 3:
                CampoTexPun2FD.setEditable(true);
                CampoTexPun3FD.setEditable(false);
                LabelPun3FD.setText("");
                LabelPun2FD.setBackground(Color.red);
                LabelPun3FD.setBackground(jPanel7.getBackground());
                break;
            case 4:
                CampoTexPun3FD.setEditable(true);
                LabelPun3FD.setBackground(Color.red);
                BotonCalcularFD.setEnabled(false);
                break;
        }
        numpuntofd--;
        if (numpuntofd == 0) {
            BotonAnteriorFD.setEnabled(false);
        }
        if (numpuntofd == 3) {
            BotonCapturarFD.setEnabled(true);
        }
    }

    public void CapturarFI() {
        double muestra;
        DecimalFormat formateador = new DecimalFormat("####.00");
        comandoSERV((byte) 1);
        for (t = 0; t < 4; t++) {
            try {
                Thread.currentThread().sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(DlgCalibracion.class.getName()).log(Level.SEVERE, null, ex);
            }
            CapturarDatos();
        }
        comandoSTOP();
        muestra = CalcularMediana(Datos1);
        Datos1.clear();
        try {
            switch (numpuntofi) {
                case 0:
                    muestrasfi[0] = muestra;
                    patronesfi[0] = 0;
                    CampoTexPun1FI.setEditable(true);
                    LabelPun0FI.setText(formateador.format(muestra));
                    LabelPun0FI.setBackground(Color.green);
                    LabelPun1FI.setBackground(Color.red);
                    break;
                case 1:
                    muestrasfi[1] = muestra;
                    patronesfi[1] = Double.parseDouble(CampoTexPun1FI.getText());
                    CampoTexPun1FI.setEditable(false);
                    CampoTexPun2FI.setEditable(true);
                    LabelPun1FI.setText(formateador.format(muestra));
                    LabelPun1FI.setBackground(Color.green);
                    LabelPun2FI.setBackground(Color.red);
                    break;
                case 2:
                    muestrasfi[2] = muestra;
                    patronesfi[2] = Double.parseDouble(CampoTexPun2FI.getText());
                    CampoTexPun2FI.setEditable(false);
                    CampoTexPun3FI.setEditable(true);
                    LabelPun2FI.setText(formateador.format(muestra));
                    LabelPun2FI.setBackground(Color.green);
                    LabelPun3FI.setBackground(Color.red);
                    break;
                case 3:
                    muestrasfi[3] = muestra;
                    patronesfi[3] = Double.parseDouble(CampoTexPun3FI.getText());
                    CampoTexPun3FI.setEditable(false);
                    LabelPun3FI.setText(formateador.format(muestra));
                    LabelPun3FI.setBackground(Color.green);
                    BotonCalcularFI.setEnabled(true);
                    break;
            }
            numpuntofi++;
            if (numpuntofi == 4) {
                BotonCapturarFI.setEnabled(false);
            }
            if (numpuntofi == 1) {
                BotonAnteriorFI.setEnabled(true);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Introduzca un valor correcto en el patron",
                    "Error", JOptionPane.ERROR_MESSAGE);
            if (CampoTexPun1FI.isEditable()) {
                CampoTexPun1FI.setText("");
            }
            if (CampoTexPun2FI.isEditable()) {
                CampoTexPun2FI.setText("");
            }
            if (CampoTexPun3FI.isEditable()) {
                CampoTexPun3FI.setText("");
            }
        }
    }

    public void AnteriorFI() {
        switch (numpuntofi) {
            case 1:
                CampoTexPun1FI.setEditable(false);
                LabelPun1FI.setText("");
                LabelPun0FI.setBackground(Color.red);
                LabelPun1FI.setBackground(jPanel7.getBackground());
                break;
            case 2:
                CampoTexPun1FI.setEditable(true);
                CampoTexPun2FI.setEditable(false);
                LabelPun2FI.setText("");
                LabelPun1FI.setBackground(Color.red);
                LabelPun2FI.setBackground(jPanel7.getBackground());
                break;
            case 3:
                CampoTexPun2FI.setEditable(true);
                CampoTexPun3FI.setEditable(false);
                LabelPun3FI.setText("");
                LabelPun2FI.setBackground(Color.red);
                LabelPun3FI.setBackground(jPanel7.getBackground());
                break;
            case 4:
                CampoTexPun3FI.setEditable(true);
                LabelPun3FI.setBackground(Color.red);
                BotonCalcularFI.setEnabled(false);
                break;
        }
        numpuntofi--;
        if (numpuntofi == 0) {
            BotonAnteriorFI.setEnabled(false);
        }
        if (numpuntofi == 3) {
            BotonCapturarFI.setEnabled(true);
        }
    }

    public void CapturarPD() {
        double muestra;
        DecimalFormat formateador = new DecimalFormat("####.00");
        comandoSERV((byte) 2);
        for (t = 0; t < 4; t++) {
            try {
                Thread.currentThread().sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(DlgCalibracion.class.getName()).log(Level.SEVERE, null, ex);
            }
            CapturarDatos();
        }
        comandoSTOP();
        muestra = CalcularMediana(Datos1);
          
        Datos1.clear();
        try {
            switch (numpuntopd) {
                case 0:
                    JOptionPane.showMessageDialog(null, "valor entregado x calculoMediana 0 "+muestra,
                    "Error", JOptionPane.ERROR_MESSAGE);
                    muestraspd[0] = muestra;
                    patronespd[0] = 0;
                    CampoTexPun1PD.setEditable(true);
                    LabelPun0PD.setText(formateador.format(muestra));
                    LabelPun0PD.setBackground(Color.green);
                    LabelPun1PD.setBackground(Color.red);
                    break;
                case 1:
                    JOptionPane.showMessageDialog(null, "valor entregado x calculoMediana 1 "+muestra,
                    "Error", JOptionPane.ERROR_MESSAGE);
                    muestraspd[1] = muestra;
                    patronespd[1] = Double.parseDouble(CampoTexPun1PD.getText());
                    if (uniseleccionada.equals("Kg")) {
                        patronespd[1] *= gravedad;
                    }
                    CampoTexPun1PD.setEditable(false);
                    CampoTexPun2PD.setEditable(true);
                    LabelPun1PD.setText(formateador.format(muestra));
                    LabelPun1PD.setBackground(Color.green);
                    LabelPun2PD.setBackground(Color.red);
                    break;
                case 2:
                    JOptionPane.showMessageDialog(null, "valor entregado x calculoMediana 2 "+muestra,
                    "Error", JOptionPane.ERROR_MESSAGE);
                    muestraspd[2] = muestra;
                    patronespd[2] = Double.parseDouble(CampoTexPun2PD.getText());
                    if (uniseleccionada.equals("Kg")) {
                        patronespd[2] *= gravedad;
                    }
                    CampoTexPun2PD.setEditable(false);
                    CampoTexPun3PD.setEditable(true);
                    LabelPun2PD.setText(formateador.format(muestra));
                    LabelPun2PD.setBackground(Color.green);
                    LabelPun3PD.setBackground(Color.red);
                    break;
                case 3:
                    JOptionPane.showMessageDialog(null, "valor entregado x calculoMediana 3 "+muestra,
                    "Error", JOptionPane.ERROR_MESSAGE);
                    muestraspd[3] = muestra;
                    patronespd[3] = Double.parseDouble(CampoTexPun3PD.getText());
                    if (uniseleccionada.equals("Kg")) {
                        patronespd[3] *= gravedad;
                    }
                    CampoTexPun3PD.setEditable(false);
                    LabelPun3PD.setText(formateador.format(muestra));
                    LabelPun3PD.setBackground(Color.green);
                    BotonCalcularPD.setEnabled(true);
                    break;
            }
            numpuntopd++;
            if (numpuntopd == 4) {
                BotonCapturarPD.setEnabled(false);
            }
            if (numpuntopd == 1) {
                BotonAnteriorPD.setEnabled(true);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Introduzca un valor correcto en el patron",
                    "Error", JOptionPane.ERROR_MESSAGE);
            if (CampoTexPun1PD.isEditable()) {
                CampoTexPun1PD.setText("");
            }
            if (CampoTexPun2PD.isEditable()) {
                CampoTexPun2PD.setText("");
            }
            if (CampoTexPun3PD.isEditable()) {
                CampoTexPun3PD.setText("");
            }
        }
    }

    public void AnteriorPD() {
        switch (numpuntopd) {
            case 1:
                CampoTexPun1PD.setEditable(false);
                LabelPun1PD.setText("");
                LabelPun0PD.setBackground(Color.red);
                LabelPun1PD.setBackground(jPanel7.getBackground());
                break;
            case 2:
                CampoTexPun1PD.setEditable(true);
                CampoTexPun2PD.setEditable(false);
                LabelPun2PD.setText("");
                LabelPun1PD.setBackground(Color.red);
                LabelPun2PD.setBackground(jPanel7.getBackground());
                break;
            case 3:
                CampoTexPun2PD.setEditable(true);
                CampoTexPun3PD.setEditable(false);
                LabelPun3PD.setText("");
                LabelPun2PD.setBackground(Color.red);
                LabelPun3PD.setBackground(jPanel7.getBackground());
                break;
            case 4:
                CampoTexPun3PD.setEditable(true);
                LabelPun3PD.setBackground(Color.red);
                BotonCalcularPD.setEnabled(false);
                break;
        }
        numpuntopd--;
        if (numpuntopd == 0) {
            BotonAnteriorPD.setEnabled(false);
        }
        if (numpuntopd == 3) {
            BotonCapturarPD.setEnabled(true);
        }
    }

    public void CapturarPI() {
        double muestra;
        DecimalFormat formateador = new DecimalFormat("####.00");
        comandoSERV((byte) 3);
        for (t = 0; t < 4; t++) {
            try {
                Thread.currentThread().sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(DlgCalibracion.class.getName()).log(Level.SEVERE, null, ex);
            }
            CapturarDatos();
        }
        comandoSTOP();
        muestra = CalcularMediana(Datos1);
        Datos1.clear();
        try {
            switch (numpuntopi) {
                case 0:
                    muestraspi[0] = muestra;
                    patronespi[0] = 0;
                    CampoTexPun1PI.setEditable(true);
                    LabelPun0PI.setText(formateador.format(muestra));
                    LabelPun0PI.setBackground(Color.green);
                    LabelPun1PI.setBackground(Color.red);
                    break;
                case 1:
                    muestraspi[1] = muestra;
                    patronespi[1] = Double.parseDouble(CampoTexPun1PI.getText());
                    if (uniseleccionada.equals("Kg")) {
                        patronespi[1] *= gravedad;
                    }
                    CampoTexPun1PI.setEditable(false);
                    CampoTexPun2PI.setEditable(true);
                    LabelPun1PI.setText(formateador.format(muestra));
                    LabelPun1PI.setBackground(Color.green);
                    LabelPun2PI.setBackground(Color.red);
                    break;
                case 2:
                    muestraspi[2] = muestra;
                    patronespi[2] = Double.parseDouble(CampoTexPun2PI.getText());
                    if (uniseleccionada.equals("Kg")) {
                        patronespi[2] *= gravedad;
                    }
                    CampoTexPun2PI.setEditable(false);
                    CampoTexPun3PI.setEditable(true);
                    LabelPun2PI.setText(formateador.format(muestra));
                    LabelPun2PI.setBackground(Color.green);
                    LabelPun3PI.setBackground(Color.red);
                    break;
                case 3:
                    muestraspi[3] = muestra;
                    patronespi[3] = Double.parseDouble(CampoTexPun3PI.getText());
                    if (uniseleccionada.equals("Kg")) {
                        patronespi[3] *= gravedad;
                    }
                    CampoTexPun3PI.setEditable(false);
                    LabelPun3PI.setText(formateador.format(muestra));
                    LabelPun3PI.setBackground(Color.green);
                    BotonCalcularPI.setEnabled(true);
                    break;
            }
            numpuntopi++;
            if (numpuntopi == 4) {
                BotonCapturarPI.setEnabled(false);
            }
            if (numpuntopi == 1) {
                BotonAnteriorPI.setEnabled(true);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Introduzca un valor correcto en el patron",
                    "Error", JOptionPane.ERROR_MESSAGE);
            if (CampoTexPun1PI.isEditable()) {
                CampoTexPun1PI.setText("");
            }
            if (CampoTexPun2PI.isEditable()) {
                CampoTexPun2PI.setText("");
            }
            if (CampoTexPun3PI.isEditable()) {
                CampoTexPun3PI.setText("");
            }
        }
    }

    public void AnteriorPI() {
        switch (numpuntopi) {
            case 1:
                CampoTexPun1PI.setEditable(false);
                LabelPun1PI.setText("");
                LabelPun0PI.setBackground(Color.red);
                LabelPun1PI.setBackground(jPanel7.getBackground());
                break;
            case 2:
                CampoTexPun1PI.setEditable(true);
                CampoTexPun2PI.setEditable(false);
                LabelPun2PI.setText("");
                LabelPun1PI.setBackground(Color.red);
                LabelPun2PI.setBackground(jPanel7.getBackground());
                break;
            case 3:
                CampoTexPun2PI.setEditable(true);
                CampoTexPun3PI.setEditable(false);
                LabelPun3PI.setText("");
                LabelPun2PI.setBackground(Color.red);
                LabelPun3PI.setBackground(jPanel7.getBackground());
                break;
            case 4:
                CampoTexPun3PI.setEditable(true);
                LabelPun3PI.setBackground(Color.red);
                BotonCalcularPI.setEnabled(false);
                break;
        }
        numpuntopi--;
        if (numpuntopi == 0) {
            BotonAnteriorPI.setEnabled(false);
        }
        if (numpuntopi == 3) {
            BotonCapturarPI.setEnabled(true);
        }
    }

    public void CapturarVD() {
        double muestra;
        DecimalFormat formateador = new DecimalFormat("####.00");
        comandoSERV((byte) 4);
        for (t = 0; t < 4; t++) {
            try {
                Thread.currentThread().sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(DlgCalibracion.class.getName()).log(Level.SEVERE, null, ex);
            }
            CapturarDatos();
        }
        comandoSTOP();
        muestra = CalcularMediana(Datos1);
        Datos1.clear();
        try {
            switch (numpuntovd) {
                case 0:
                    muestrasvd[0] = muestra;
                    patronesvd[0] = 0;
                    CampoTexPun1VD.setEditable(true);
                    LabelPun0VD.setText(formateador.format(muestra));
                    LabelPun0VD.setBackground(Color.green);
                    LabelPun1VD.setBackground(Color.red);
                    break;
                case 1:
                    muestrasvd[1] = muestra;
                    patronesvd[1] = Double.parseDouble(CampoTexPun1VD.getText());
                    CampoTexPun1VD.setEditable(false);
                    CampoTexPun2VD.setEditable(true);
                    LabelPun1VD.setText(formateador.format(muestra));
                    LabelPun1VD.setBackground(Color.green);
                    LabelPun2VD.setBackground(Color.red);
                    break;
                case 2:
                    muestrasvd[2] = muestra;
                    patronesvd[2] = Double.parseDouble(CampoTexPun2VD.getText());
                    CampoTexPun2VD.setEditable(false);
                    CampoTexPun3VD.setEditable(true);
                    LabelPun2VD.setText(formateador.format(muestra));
                    LabelPun2VD.setBackground(Color.green);
                    LabelPun3VD.setBackground(Color.red);
                    break;
                case 3:
                    muestrasvd[3] = muestra;
                    patronesvd[3] = Double.parseDouble(CampoTexPun3VD.getText());
                    CampoTexPun3VD.setEditable(false);
                    LabelPun3VD.setText(formateador.format(muestra));
                    LabelPun3VD.setBackground(Color.green);
                    BotonCalcularVD.setEnabled(true);
                    break;
            }
            numpuntovd++;
            if (numpuntovd == 4) {
                BotonCapturarVD.setEnabled(false);
            }
            if (numpuntovd == 1) {
                BotonAnteriorVD.setEnabled(true);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Introduzca un valor correcto en el patron",
                    "Error", JOptionPane.ERROR_MESSAGE);
            if (CampoTexPun1VD.isEditable()) {
                CampoTexPun1VD.setText("");
            }
            if (CampoTexPun2VD.isEditable()) {
                CampoTexPun2VD.setText("");
            }
            if (CampoTexPun3VD.isEditable()) {
                CampoTexPun3VD.setText("");
            }
        }
    }

    public void AnteriorVD() {
        switch (numpuntovd) {
            case 1:
                CampoTexPun1VD.setEditable(false);
                LabelPun1VD.setText("");
                LabelPun0VD.setBackground(Color.red);
                LabelPun1VD.setBackground(jPanel7.getBackground());
                break;
            case 2:
                CampoTexPun1VD.setEditable(true);
                CampoTexPun2VD.setEditable(false);
                LabelPun2VD.setText("");
                LabelPun1VD.setBackground(Color.red);
                LabelPun2VD.setBackground(jPanel7.getBackground());
                break;
            case 3:
                CampoTexPun2VD.setEditable(true);
                CampoTexPun3VD.setEditable(false);
                LabelPun3VD.setText("");
                LabelPun2VD.setBackground(Color.red);
                LabelPun3VD.setBackground(jPanel7.getBackground());
                break;
            case 4:
                CampoTexPun3VD.setEditable(true);
                LabelPun3VD.setBackground(Color.red);
                BotonCalcularVD.setEnabled(false);
                break;
        }
        numpuntovd--;
        if (numpuntovd == 0) {
            BotonAnteriorVD.setEnabled(false);
        }
        if (numpuntovd == 3) {
            BotonCapturarVD.setEnabled(true);
        }
    }

    public void CapturarVI() {
        double muestra;
        DecimalFormat formateador = new DecimalFormat("####.00");
        comandoSERV((byte) 5);
        for (t = 0; t < 4; t++) {
            try {
                Thread.currentThread().sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(DlgCalibracion.class.getName()).log(Level.SEVERE, null, ex);
            }
            CapturarDatos();
        }
        comandoSTOP();
        muestra = CalcularMediana(Datos1);
        Datos1.clear();
        try {
            switch (numpuntovi) {
                case 0:
                    muestrasvi[0] = muestra;
                    patronesvi[0] = 0;
                    CampoTexPun1VI.setEditable(true);
                    LabelPun0VI.setText(formateador.format(muestra));
                    LabelPun0VI.setBackground(Color.green);
                    LabelPun1VI.setBackground(Color.red);
                    break;
                case 1:
                    muestrasvi[1] = muestra;
                    patronesvi[1] = Double.parseDouble(CampoTexPun1VI.getText());
                    CampoTexPun1VI.setEditable(false);
                    CampoTexPun2VI.setEditable(true);
                    LabelPun1VI.setText(formateador.format(muestra));
                    LabelPun1VI.setBackground(Color.green);
                    LabelPun2VI.setBackground(Color.red);
                    break;
                case 2:
                    muestrasvi[2] = muestra;
                    patronesvi[2] = Double.parseDouble(CampoTexPun2VI.getText());
                    CampoTexPun2VI.setEditable(false);
                    CampoTexPun3VI.setEditable(true);
                    LabelPun2VI.setText(formateador.format(muestra));
                    LabelPun2VI.setBackground(Color.green);
                    LabelPun3VI.setBackground(Color.red);
                    break;
                case 3:
                    muestrasvi[3] = muestra;
                    patronesvi[3] = Double.parseDouble(CampoTexPun3VI.getText());
                    CampoTexPun3VI.setEditable(false);
                    LabelPun3VI.setText(formateador.format(muestra));
                    LabelPun3VI.setBackground(Color.green);
                    BotonCalcularVI.setEnabled(true);
                    break;
            }
            numpuntovi++;
            if (numpuntovi == 4) {
                BotonCapturarVI.setEnabled(false);
            }
            if (numpuntovi == 1) {
                BotonAnteriorVI.setEnabled(true);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Introduzca un valor correcto en el patron",
                    "Error", JOptionPane.ERROR_MESSAGE);
            if (CampoTexPun1VI.isEditable()) {
                CampoTexPun1VI.setText("");
            }
            if (CampoTexPun2VI.isEditable()) {
                CampoTexPun2VI.setText("");
            }
            if (CampoTexPun3VI.isEditable()) {
                CampoTexPun3VI.setText("");
            }
        }
    }

    public void AnteriorVI() {
        switch (numpuntovi) {
            case 1:
                CampoTexPun1VI.setEditable(false);
                LabelPun1VI.setText("");
                LabelPun0VI.setBackground(Color.red);
                LabelPun1VI.setBackground(jPanel7.getBackground());
                break;
            case 2:
                CampoTexPun1VI.setEditable(true);
                CampoTexPun2VI.setEditable(false);
                LabelPun2VI.setText("");
                LabelPun1VI.setBackground(Color.red);
                LabelPun2VI.setBackground(jPanel7.getBackground());
                break;
            case 3:
                CampoTexPun2VI.setEditable(true);
                CampoTexPun3VI.setEditable(false);
                LabelPun3VI.setText("");
                LabelPun2VI.setBackground(Color.red);
                LabelPun3VI.setBackground(jPanel7.getBackground());
                break;
            case 4:
                CampoTexPun3VI.setEditable(true);
                LabelPun3VI.setBackground(Color.red);
                BotonCalcularVI.setEnabled(false);
                break;
        }
        numpuntovi--;
        if (numpuntovi == 0) {
            BotonAnteriorVI.setEnabled(false);
        }
        if (numpuntovi == 3) {
            BotonCapturarVI.setEnabled(true);
        }
    }

    public void CapturarD() {
        double muestra;
        DecimalFormat formateador = new DecimalFormat("####.00");
        comandoSERV((byte) 6);
        for (t = 0; t < 4; t++) {
            try {
                Thread.currentThread().sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(DlgCalibracion.class.getName()).log(Level.SEVERE, null, ex);
            }
            CapturarDatos();
        }
        comandoSTOP();
        muestra = CalcularMediana(Datos1);
        
        Datos1.clear();
        try {
            switch (numpuntod) {
                case 0:
                    muestrasd[0] = muestra;
                    patronesd[0] = 0;
                    CampoTexPun1D.setEditable(true);
                    LabelPun0D.setText(formateador.format(muestra));
                    LabelPun0D.setBackground(Color.green);
                    LabelPun1D.setBackground(Color.red);
                    break;
                case 1:
                    muestrasd[1] = muestra;
                    patronesd[1] = Double.parseDouble(CampoTexPun1D.getText());
                    CampoTexPun1D.setEditable(false);
                    CampoTexPun2D.setEditable(true);
                    LabelPun1D.setText(formateador.format(muestra));
                    LabelPun1D.setBackground(Color.green);
                    LabelPun2D.setBackground(Color.red);
                    break;
                case 2:
                    muestrasd[2] = muestra;
                    patronesd[2] = Double.parseDouble(CampoTexPun2D.getText());
                    CampoTexPun2D.setEditable(false);
                    LabelPun2D.setText(formateador.format(muestra));
                    LabelPun2D.setBackground(Color.green);
                    BotonCalcularD.setEnabled(true);
                    break;
            }
            numpuntod++;
            if (numpuntod == 3) {
                BotonCapturarD.setEnabled(false);
            }
            if (numpuntod == 1) {
                BotonAnteriorD.setEnabled(true);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Introduzca un valor correcto en el patron",
                    "Error", JOptionPane.ERROR_MESSAGE);
            if (CampoTexPun1D.isEditable()) {
                CampoTexPun1D.setText("");
            }
            if (CampoTexPun2D.isEditable()) {
                CampoTexPun2D.setText("");
            }
        }
    }

    public void AnteriorD() {
        switch (numpuntod) {
            case 1:
                CampoTexPun1D.setEditable(false);
                LabelPun1D.setText("");
                LabelPun0D.setBackground(Color.red);
                LabelPun1D.setBackground(jPanel7.getBackground());
                break;
            case 2:
                CampoTexPun1D.setEditable(true);
                CampoTexPun2D.setEditable(false);
                LabelPun2D.setText("");
                LabelPun1D.setBackground(Color.red);
                LabelPun2D.setBackground(jPanel7.getBackground());
                break;
            case 3:
                CampoTexPun2D.setEditable(true);
                LabelPun2D.setBackground(Color.red);
                BotonCalcularD.setEnabled(false);
                break;
        }
        numpuntod--;
        if (numpuntod == 0) {
            BotonAnteriorD.setEnabled(false);
        }
        if (numpuntod == 2) {
            BotonCapturarD.setEnabled(true);
        }
    }

    public void Guardar(int propiedad) {
        Properties archivop = new Properties();

        try {
            archivop.load(new FileInputStream("calibracion.properties"));
        } catch (IOException e) {
            System.out.println("Ha ocurrido una excepcion al abrir el fichero, no se encuentra o está protegido");
        }
        //System.out.println(archivop.getProperty("spand"));
        switch (propiedad) {
            case 0:
                archivop.setProperty("spanfd", Double.toString(spanfd));
                 JOptionPane.showMessageDialog(null, "spanfd "+spanfd,"Error", JOptionPane.ERROR_MESSAGE);
                archivop.setProperty("offsetfd", Double.toString(offsetfd));
                break;
            case 1:
                archivop.setProperty("spanfi", Double.toString(spanfi));
                  JOptionPane.showMessageDialog(null, "spanfi "+spanfi,"Error", JOptionPane.ERROR_MESSAGE);
                archivop.setProperty("offsetfi", Double.toString(offsetfi));
                break;
            case 2:
                archivop.setProperty("spanpd", Double.toString(spanpd));
                  JOptionPane.showMessageDialog(null, "spanpd "+spanpd,"Error", JOptionPane.ERROR_MESSAGE);
                archivop.setProperty("offsetpd", Double.toString(offsetpd));
                break;
            case 3:
                archivop.setProperty("spanpi", Double.toString(spanpi));
                  JOptionPane.showMessageDialog(null, "spanpd "+spanpi,"Error", JOptionPane.ERROR_MESSAGE);
                archivop.setProperty("offsetpi", Double.toString(offsetpi));
                break;
            case 4:
                archivop.setProperty("spanvd", Double.toString(spanvd));
                archivop.setProperty("offsetvd", Double.toString(offsetvd));
                break;
            case 5:
                archivop.setProperty("spanvi", Double.toString(spanvi));
                archivop.setProperty("offsetvi", Double.toString(offsetvi));
                break;
            case 6:
                archivop.setProperty("spand", Double.toString(spand));
                archivop.setProperty("offsetd", Double.toString(offsetd));
                break;
        }
        try {
            archivop.store(new FileOutputStream("calibracion.properties"), "Valores de calibración de los canales");
        } catch (IOException ex) {
            Logger.getLogger(DlgCalibracion.class.getName()).log(Level.SEVERE, null, ex);
        }
  JOptionPane.showMessageDialog(null, "He Regstrado Sastifactoriamente Valores Calibracion","Mensage", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        CampoTexPun1FD = new javax.swing.JTextField();
        CampoTexPun2FD = new javax.swing.JTextField();
        CampoTexPun3FD = new javax.swing.JTextField();
        jPanel8 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        LabelPun0FD = new javax.swing.JLabel();
        LabelPun1FD = new javax.swing.JLabel();
        LabelPun2FD = new javax.swing.JLabel();
        LabelPun3FD = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        CampoTexPun1FI = new javax.swing.JTextField();
        CampoTexPun2FI = new javax.swing.JTextField();
        CampoTexPun3FI = new javax.swing.JTextField();
        jPanel13 = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jPanel14 = new javax.swing.JPanel();
        LabelPun0FI = new javax.swing.JLabel();
        LabelPun1FI = new javax.swing.JLabel();
        LabelPun2FI = new javax.swing.JLabel();
        LabelPun3FI = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jPanel41 = new javax.swing.JPanel();
        BotonCapturarFD = new javax.swing.JButton();
        BotonAnteriorFD = new javax.swing.JButton();
        BotonCalcularFD = new javax.swing.JButton();
        BotonGuardarFD = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel42 = new javax.swing.JPanel();
        BotonCapturarFI = new javax.swing.JButton();
        BotonAnteriorFI = new javax.swing.JButton();
        BotonCalcularFI = new javax.swing.JButton();
        BotonGuardarFI = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jPanel17 = new javax.swing.JPanel();
        jLabel47 = new javax.swing.JLabel();
        CampoTexPun1PD = new javax.swing.JTextField();
        CampoTexPun2PD = new javax.swing.JTextField();
        CampoTexPun3PD = new javax.swing.JTextField();
        jPanel18 = new javax.swing.JPanel();
        Label0uniPD = new javax.swing.JLabel();
        Label1uniPD = new javax.swing.JLabel();
        Label2uniPD = new javax.swing.JLabel();
        Label3uniPD = new javax.swing.JLabel();
        jPanel19 = new javax.swing.JPanel();
        LabelPun0PD = new javax.swing.JLabel();
        LabelPun1PD = new javax.swing.JLabel();
        LabelPun2PD = new javax.swing.JLabel();
        LabelPun3PD = new javax.swing.JLabel();
        jPanel20 = new javax.swing.JPanel();
        jLabel56 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        jLabel58 = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
        jLabel60 = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        jPanel21 = new javax.swing.JPanel();
        jLabel63 = new javax.swing.JLabel();
        jLabel64 = new javax.swing.JLabel();
        jLabel65 = new javax.swing.JLabel();
        jLabel66 = new javax.swing.JLabel();
        jPanel22 = new javax.swing.JPanel();
        jLabel67 = new javax.swing.JLabel();
        CampoTexPun1PI = new javax.swing.JTextField();
        CampoTexPun2PI = new javax.swing.JTextField();
        CampoTexPun3PI = new javax.swing.JTextField();
        jPanel23 = new javax.swing.JPanel();
        Label0uniPI = new javax.swing.JLabel();
        Label1uniPI = new javax.swing.JLabel();
        Label2uniPI = new javax.swing.JLabel();
        Label3uniPI = new javax.swing.JLabel();
        jPanel24 = new javax.swing.JPanel();
        LabelPun0PI = new javax.swing.JLabel();
        LabelPun1PI = new javax.swing.JLabel();
        LabelPun2PI = new javax.swing.JLabel();
        LabelPun3PI = new javax.swing.JLabel();
        jPanel25 = new javax.swing.JPanel();
        jLabel76 = new javax.swing.JLabel();
        jLabel77 = new javax.swing.JLabel();
        jLabel78 = new javax.swing.JLabel();
        jLabel79 = new javax.swing.JLabel();
        jLabel80 = new javax.swing.JLabel();
        jLabel81 = new javax.swing.JLabel();
        jLabel82 = new javax.swing.JLabel();
        jPanel43 = new javax.swing.JPanel();
        BotonCapturarPD = new javax.swing.JButton();
        BotonAnteriorPD = new javax.swing.JButton();
        BotonCalcularPD = new javax.swing.JButton();
        BotonGuardarPD = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        jPanel44 = new javax.swing.JPanel();
        BotonCapturarPI = new javax.swing.JButton();
        BotonAnteriorPI = new javax.swing.JButton();
        BotonCalcularPI = new javax.swing.JButton();
        BotonGuardarPI = new javax.swing.JButton();
        jLabel126 = new javax.swing.JLabel();
        SeleccionKgoN = new javax.swing.JComboBox();
        jPanel3 = new javax.swing.JPanel();
        jPanel26 = new javax.swing.JPanel();
        jLabel83 = new javax.swing.JLabel();
        jLabel84 = new javax.swing.JLabel();
        jLabel85 = new javax.swing.JLabel();
        jLabel86 = new javax.swing.JLabel();
        jPanel27 = new javax.swing.JPanel();
        jLabel87 = new javax.swing.JLabel();
        CampoTexPun1VD = new javax.swing.JTextField();
        CampoTexPun2VD = new javax.swing.JTextField();
        CampoTexPun3VD = new javax.swing.JTextField();
        jPanel28 = new javax.swing.JPanel();
        jLabel88 = new javax.swing.JLabel();
        jLabel89 = new javax.swing.JLabel();
        jLabel90 = new javax.swing.JLabel();
        jLabel91 = new javax.swing.JLabel();
        jPanel29 = new javax.swing.JPanel();
        LabelPun0VD = new javax.swing.JLabel();
        LabelPun1VD = new javax.swing.JLabel();
        LabelPun2VD = new javax.swing.JLabel();
        LabelPun3VD = new javax.swing.JLabel();
        jPanel30 = new javax.swing.JPanel();
        jLabel96 = new javax.swing.JLabel();
        jLabel97 = new javax.swing.JLabel();
        jLabel98 = new javax.swing.JLabel();
        jLabel99 = new javax.swing.JLabel();
        jLabel100 = new javax.swing.JLabel();
        jLabel101 = new javax.swing.JLabel();
        jLabel102 = new javax.swing.JLabel();
        jPanel31 = new javax.swing.JPanel();
        jLabel103 = new javax.swing.JLabel();
        jLabel104 = new javax.swing.JLabel();
        jLabel105 = new javax.swing.JLabel();
        jLabel106 = new javax.swing.JLabel();
        jPanel32 = new javax.swing.JPanel();
        jLabel107 = new javax.swing.JLabel();
        CampoTexPun1VI = new javax.swing.JTextField();
        CampoTexPun2VI = new javax.swing.JTextField();
        CampoTexPun3VI = new javax.swing.JTextField();
        jPanel33 = new javax.swing.JPanel();
        jLabel108 = new javax.swing.JLabel();
        jLabel109 = new javax.swing.JLabel();
        jLabel110 = new javax.swing.JLabel();
        jLabel111 = new javax.swing.JLabel();
        jPanel34 = new javax.swing.JPanel();
        LabelPun0VI = new javax.swing.JLabel();
        LabelPun1VI = new javax.swing.JLabel();
        LabelPun2VI = new javax.swing.JLabel();
        LabelPun3VI = new javax.swing.JLabel();
        jPanel35 = new javax.swing.JPanel();
        jLabel116 = new javax.swing.JLabel();
        jLabel117 = new javax.swing.JLabel();
        jLabel118 = new javax.swing.JLabel();
        jLabel119 = new javax.swing.JLabel();
        jLabel120 = new javax.swing.JLabel();
        jLabel121 = new javax.swing.JLabel();
        jLabel122 = new javax.swing.JLabel();
        jPanel45 = new javax.swing.JPanel();
        BotonCapturarVD = new javax.swing.JButton();
        BotonAnteriorVD = new javax.swing.JButton();
        BotonCalcularVD = new javax.swing.JButton();
        BotonGuardarVD = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JSeparator();
        jPanel46 = new javax.swing.JPanel();
        BotonCapturarVI = new javax.swing.JButton();
        BotonAnteriorVI = new javax.swing.JButton();
        BotonCalcularVI = new javax.swing.JButton();
        BotonGuardarVI = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jPanel36 = new javax.swing.JPanel();
        LabelPun0D = new javax.swing.JLabel();
        LabelPun1D = new javax.swing.JLabel();
        LabelPun2D = new javax.swing.JLabel();
        jPanel37 = new javax.swing.JPanel();
        jLabel127 = new javax.swing.JLabel();
        jLabel128 = new javax.swing.JLabel();
        jLabel129 = new javax.swing.JLabel();
        jPanel38 = new javax.swing.JPanel();
        jLabel131 = new javax.swing.JLabel();
        jLabel132 = new javax.swing.JLabel();
        jLabel133 = new javax.swing.JLabel();
        jPanel39 = new javax.swing.JPanel();
        jLabel135 = new javax.swing.JLabel();
        CampoTexPun1D = new javax.swing.JTextField();
        CampoTexPun2D = new javax.swing.JTextField();
        jPanel40 = new javax.swing.JPanel();
        jLabel136 = new javax.swing.JLabel();
        jLabel137 = new javax.swing.JLabel();
        jLabel138 = new javax.swing.JLabel();
        jLabel140 = new javax.swing.JLabel();
        jLabel141 = new javax.swing.JLabel();
        jPanel47 = new javax.swing.JPanel();
        BotonCapturarD = new javax.swing.JButton();
        BotonAnteriorD = new javax.swing.JButton();
        BotonCalcularD = new javax.swing.JButton();
        BotonGuardarD = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        BotonSalir = new javax.swing.JButton();
        BotonConectar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jTabbedPane1.setMaximumSize(new java.awt.Dimension(1630, 325));

        jPanel1.setMaximumSize(new java.awt.Dimension(987, 297));

        jPanel6.setLayout(new java.awt.GridLayout(4, 0));

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setText("Punto 0:");
        jPanel6.add(jLabel3);

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel4.setText("Punto 1:");
        jPanel6.add(jLabel4);

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setText("Punto 2:");
        jPanel6.add(jLabel5);

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel6.setText("Punto 3:");
        jPanel6.add(jLabel6);

        jPanel7.setLayout(new java.awt.GridLayout(4, 0));

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("0");
        jPanel7.add(jLabel7);

        CampoTexPun1FD.setEditable(false);
        CampoTexPun1FD.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jPanel7.add(CampoTexPun1FD);

        CampoTexPun2FD.setEditable(false);
        CampoTexPun2FD.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jPanel7.add(CampoTexPun2FD);

        CampoTexPun3FD.setEditable(false);
        CampoTexPun3FD.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jPanel7.add(CampoTexPun3FD);

        jPanel8.setLayout(new java.awt.GridLayout(4, 0));

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("N");
        jPanel8.add(jLabel8);

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("N");
        jPanel8.add(jLabel9);

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("N");
        jPanel8.add(jLabel10);

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("N");
        jPanel8.add(jLabel11);

        jPanel9.setLayout(new java.awt.GridLayout(4, 0));

        LabelPun0FD.setBackground(new java.awt.Color(255, 0, 0));
        LabelPun0FD.setMaximumSize(new java.awt.Dimension(40, 14));
        LabelPun0FD.setMinimumSize(new java.awt.Dimension(40, 14));
        LabelPun0FD.setOpaque(true);
        LabelPun0FD.setPreferredSize(new java.awt.Dimension(40, 14));
        jPanel9.add(LabelPun0FD);

        LabelPun1FD.setMaximumSize(new java.awt.Dimension(40, 14));
        LabelPun1FD.setMinimumSize(new java.awt.Dimension(40, 14));
        LabelPun1FD.setOpaque(true);
        LabelPun1FD.setPreferredSize(new java.awt.Dimension(40, 14));
        jPanel9.add(LabelPun1FD);

        LabelPun2FD.setMaximumSize(new java.awt.Dimension(40, 14));
        LabelPun2FD.setMinimumSize(new java.awt.Dimension(40, 14));
        LabelPun2FD.setOpaque(true);
        LabelPun2FD.setPreferredSize(new java.awt.Dimension(40, 14));
        jPanel9.add(LabelPun2FD);

        LabelPun3FD.setMaximumSize(new java.awt.Dimension(40, 14));
        LabelPun3FD.setMinimumSize(new java.awt.Dimension(40, 14));
        LabelPun3FD.setOpaque(true);
        LabelPun3FD.setPreferredSize(new java.awt.Dimension(40, 14));
        jPanel9.add(LabelPun3FD);

        jPanel10.setLayout(new java.awt.GridLayout(4, 0));

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("uni");
        jPanel10.add(jLabel16);

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("uni");
        jPanel10.add(jLabel17);

        jLabel18.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("uni");
        jPanel10.add(jLabel18);

        jLabel19.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("uni");
        jPanel10.add(jLabel19);

        jLabel20.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel20.setText("PARTE DERECHA");

        jLabel21.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("PATRONES");

        jLabel22.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel22.setText("MEDICIONES");

        jPanel11.setLayout(new java.awt.GridLayout(4, 0));

        jLabel23.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel23.setText("Punto 0:");
        jPanel11.add(jLabel23);

        jLabel24.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel24.setText("Punto 1:");
        jPanel11.add(jLabel24);

        jLabel25.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel25.setText("Punto 2:");
        jPanel11.add(jLabel25);

        jLabel26.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel26.setText("Punto 3:");
        jPanel11.add(jLabel26);

        jPanel12.setLayout(new java.awt.GridLayout(4, 0));

        jLabel27.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setText("0");
        jPanel12.add(jLabel27);

        CampoTexPun1FI.setEditable(false);
        CampoTexPun1FI.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jPanel12.add(CampoTexPun1FI);

        CampoTexPun2FI.setEditable(false);
        CampoTexPun2FI.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jPanel12.add(CampoTexPun2FI);

        CampoTexPun3FI.setEditable(false);
        CampoTexPun3FI.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jPanel12.add(CampoTexPun3FI);

        jPanel13.setLayout(new java.awt.GridLayout(4, 0));

        jLabel28.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setText("N");
        jPanel13.add(jLabel28);

        jLabel29.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setText("N");
        jPanel13.add(jLabel29);

        jLabel30.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setText("N");
        jPanel13.add(jLabel30);

        jLabel31.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setText("N");
        jPanel13.add(jLabel31);

        jPanel14.setLayout(new java.awt.GridLayout(4, 0));

        LabelPun0FI.setBackground(new java.awt.Color(255, 0, 0));
        LabelPun0FI.setOpaque(true);
        jPanel14.add(LabelPun0FI);

        LabelPun1FI.setOpaque(true);
        jPanel14.add(LabelPun1FI);

        LabelPun2FI.setOpaque(true);
        jPanel14.add(LabelPun2FI);

        LabelPun3FI.setOpaque(true);
        jPanel14.add(LabelPun3FI);

        jPanel15.setLayout(new java.awt.GridLayout(4, 0));

        jLabel36.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel36.setText("uni");
        jPanel15.add(jLabel36);

        jLabel37.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel37.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel37.setText("uni");
        jPanel15.add(jLabel37);

        jLabel38.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel38.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel38.setText("uni");
        jPanel15.add(jLabel38);

        jLabel39.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel39.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel39.setText("uni");
        jPanel15.add(jLabel39);

        jLabel40.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel40.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel40.setText("PARTE IZQUIERDA");

        jLabel41.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel41.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel41.setText("PATRONES");

        jLabel42.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel42.setText("MEDICIONES");

        jPanel41.setLayout(new java.awt.GridLayout(4, 0));

        BotonCapturarFD.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BotonCapturarFD.setText("Capturar");
        BotonCapturarFD.setEnabled(false);
        BotonCapturarFD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonCapturarFDActionPerformed(evt);
            }
        });
        jPanel41.add(BotonCapturarFD);

        BotonAnteriorFD.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BotonAnteriorFD.setText("Anterior");
        BotonAnteriorFD.setEnabled(false);
        BotonAnteriorFD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonAnteriorFDActionPerformed(evt);
            }
        });
        jPanel41.add(BotonAnteriorFD);

        BotonCalcularFD.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BotonCalcularFD.setText("Calcular");
        BotonCalcularFD.setEnabled(false);
        BotonCalcularFD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonCalcularFDActionPerformed(evt);
            }
        });
        jPanel41.add(BotonCalcularFD);

        BotonGuardarFD.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BotonGuardarFD.setText("Guardar");
        BotonGuardarFD.setEnabled(false);
        BotonGuardarFD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonGuardarFDActionPerformed(evt);
            }
        });
        jPanel41.add(BotonGuardarFD);

        jSeparator1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel42.setLayout(new java.awt.GridLayout(4, 0));

        BotonCapturarFI.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BotonCapturarFI.setText("Capturar");
        BotonCapturarFI.setEnabled(false);
        BotonCapturarFI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonCapturarFIActionPerformed(evt);
            }
        });
        jPanel42.add(BotonCapturarFI);

        BotonAnteriorFI.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BotonAnteriorFI.setText("Anterior");
        BotonAnteriorFI.setEnabled(false);
        BotonAnteriorFI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonAnteriorFIActionPerformed(evt);
            }
        });
        jPanel42.add(BotonAnteriorFI);

        BotonCalcularFI.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BotonCalcularFI.setText("Calcular");
        BotonCalcularFI.setEnabled(false);
        BotonCalcularFI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonCalcularFIActionPerformed(evt);
            }
        });
        jPanel42.add(BotonCalcularFI);

        BotonGuardarFI.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BotonGuardarFI.setText("Guardar");
        BotonGuardarFI.setEnabled(false);
        BotonGuardarFI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonGuardarFIActionPerformed(evt);
            }
        });
        jPanel42.add(BotonGuardarFI);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(18, 18, 18)
                .addComponent(jPanel41, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel41, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel42)))
                    .addComponent(jLabel40, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jPanel42, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(36, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel21)
                            .addComponent(jLabel22))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel41, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(52, 52, 52))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel40)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel41)
                            .addComponent(jLabel42))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel42, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jPanel14, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel15, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel13, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel12, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        jTabbedPane1.addTab("Fuerza", jPanel1);

        jPanel2.setMaximumSize(new java.awt.Dimension(987, 297));

        jPanel16.setLayout(new java.awt.GridLayout(4, 0));

        jLabel43.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel43.setText("Punto 0:");
        jPanel16.add(jLabel43);

        jLabel44.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel44.setText("Punto 1:");
        jPanel16.add(jLabel44);

        jLabel45.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel45.setText("Punto 2:");
        jPanel16.add(jLabel45);

        jLabel46.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel46.setText("Punto 3:");
        jPanel16.add(jLabel46);

        jPanel17.setLayout(new java.awt.GridLayout(4, 0));

        jLabel47.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel47.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel47.setText("0");
        jPanel17.add(jLabel47);

        CampoTexPun1PD.setEditable(false);
        CampoTexPun1PD.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jPanel17.add(CampoTexPun1PD);

        CampoTexPun2PD.setEditable(false);
        CampoTexPun2PD.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jPanel17.add(CampoTexPun2PD);

        CampoTexPun3PD.setEditable(false);
        CampoTexPun3PD.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jPanel17.add(CampoTexPun3PD);

        jPanel18.setLayout(new java.awt.GridLayout(4, 0));

        Label0uniPD.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Label0uniPD.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Label0uniPD.setText("Kg");
        jPanel18.add(Label0uniPD);

        Label1uniPD.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Label1uniPD.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Label1uniPD.setText("Kg");
        jPanel18.add(Label1uniPD);

        Label2uniPD.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Label2uniPD.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Label2uniPD.setText("Kg");
        jPanel18.add(Label2uniPD);

        Label3uniPD.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Label3uniPD.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Label3uniPD.setText("Kg");
        jPanel18.add(Label3uniPD);

        jPanel19.setLayout(new java.awt.GridLayout(4, 0));

        LabelPun0PD.setBackground(new java.awt.Color(255, 0, 0));
        LabelPun0PD.setMaximumSize(new java.awt.Dimension(40, 14));
        LabelPun0PD.setMinimumSize(new java.awt.Dimension(40, 14));
        LabelPun0PD.setOpaque(true);
        LabelPun0PD.setPreferredSize(new java.awt.Dimension(40, 14));
        jPanel19.add(LabelPun0PD);

        LabelPun1PD.setMaximumSize(new java.awt.Dimension(40, 14));
        LabelPun1PD.setMinimumSize(new java.awt.Dimension(40, 14));
        LabelPun1PD.setOpaque(true);
        LabelPun1PD.setPreferredSize(new java.awt.Dimension(40, 14));
        jPanel19.add(LabelPun1PD);

        LabelPun2PD.setMaximumSize(new java.awt.Dimension(40, 14));
        LabelPun2PD.setMinimumSize(new java.awt.Dimension(40, 14));
        LabelPun2PD.setOpaque(true);
        LabelPun2PD.setPreferredSize(new java.awt.Dimension(40, 14));
        jPanel19.add(LabelPun2PD);

        LabelPun3PD.setMaximumSize(new java.awt.Dimension(40, 14));
        LabelPun3PD.setMinimumSize(new java.awt.Dimension(40, 14));
        LabelPun3PD.setOpaque(true);
        LabelPun3PD.setPreferredSize(new java.awt.Dimension(40, 14));
        jPanel19.add(LabelPun3PD);

        jPanel20.setLayout(new java.awt.GridLayout(4, 0));

        jLabel56.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel56.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel56.setText("uni");
        jPanel20.add(jLabel56);

        jLabel57.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel57.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel57.setText("uni");
        jPanel20.add(jLabel57);

        jLabel58.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel58.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel58.setText("uni");
        jPanel20.add(jLabel58);

        jLabel59.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel59.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel59.setText("uni");
        jPanel20.add(jLabel59);

        jLabel60.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel60.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel60.setText("PARTE DERECHA");

        jLabel61.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel61.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel61.setText("PATRONES");

        jLabel62.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel62.setText("MEDICIONES");

        jPanel21.setLayout(new java.awt.GridLayout(4, 0));

        jLabel63.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel63.setText("Punto 0:");
        jPanel21.add(jLabel63);

        jLabel64.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel64.setText("Punto 1:");
        jPanel21.add(jLabel64);

        jLabel65.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel65.setText("Punto 2:");
        jPanel21.add(jLabel65);

        jLabel66.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel66.setText("Punto 3:");
        jPanel21.add(jLabel66);

        jPanel22.setLayout(new java.awt.GridLayout(4, 0));

        jLabel67.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel67.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel67.setText("0");
        jPanel22.add(jLabel67);

        CampoTexPun1PI.setEditable(false);
        CampoTexPun1PI.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jPanel22.add(CampoTexPun1PI);

        CampoTexPun2PI.setEditable(false);
        CampoTexPun2PI.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jPanel22.add(CampoTexPun2PI);

        CampoTexPun3PI.setEditable(false);
        CampoTexPun3PI.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jPanel22.add(CampoTexPun3PI);

        jPanel23.setLayout(new java.awt.GridLayout(4, 0));

        Label0uniPI.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Label0uniPI.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Label0uniPI.setText("Kg");
        jPanel23.add(Label0uniPI);

        Label1uniPI.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Label1uniPI.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Label1uniPI.setText("Kg");
        jPanel23.add(Label1uniPI);

        Label2uniPI.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Label2uniPI.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Label2uniPI.setText("Kg");
        jPanel23.add(Label2uniPI);

        Label3uniPI.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Label3uniPI.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Label3uniPI.setText("Kg");
        jPanel23.add(Label3uniPI);

        jPanel24.setLayout(new java.awt.GridLayout(4, 0));

        LabelPun0PI.setBackground(new java.awt.Color(255, 0, 0));
        LabelPun0PI.setOpaque(true);
        jPanel24.add(LabelPun0PI);

        LabelPun1PI.setOpaque(true);
        jPanel24.add(LabelPun1PI);

        LabelPun2PI.setOpaque(true);
        jPanel24.add(LabelPun2PI);

        LabelPun3PI.setOpaque(true);
        jPanel24.add(LabelPun3PI);

        jPanel25.setLayout(new java.awt.GridLayout(4, 0));

        jLabel76.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel76.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel76.setText("uni");
        jPanel25.add(jLabel76);

        jLabel77.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel77.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel77.setText("uni");
        jPanel25.add(jLabel77);

        jLabel78.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel78.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel78.setText("uni");
        jPanel25.add(jLabel78);

        jLabel79.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel79.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel79.setText("uni");
        jPanel25.add(jLabel79);

        jLabel80.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel80.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel80.setText("PARTE IZQUIERDA");

        jLabel81.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel81.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel81.setText("PATRONES");

        jLabel82.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel82.setText("MEDICIONES");

        jPanel43.setLayout(new java.awt.GridLayout(4, 0));

        BotonCapturarPD.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BotonCapturarPD.setText("Capturar");
        BotonCapturarPD.setEnabled(false);
        BotonCapturarPD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonCapturarPDActionPerformed(evt);
            }
        });
        jPanel43.add(BotonCapturarPD);

        BotonAnteriorPD.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BotonAnteriorPD.setText("Anterior");
        BotonAnteriorPD.setEnabled(false);
        BotonAnteriorPD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonAnteriorPDActionPerformed(evt);
            }
        });
        jPanel43.add(BotonAnteriorPD);

        BotonCalcularPD.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BotonCalcularPD.setText("Calcular");
        BotonCalcularPD.setEnabled(false);
        BotonCalcularPD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonCalcularPDActionPerformed(evt);
            }
        });
        jPanel43.add(BotonCalcularPD);

        BotonGuardarPD.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BotonGuardarPD.setText("Guardar");
        BotonGuardarPD.setEnabled(false);
        BotonGuardarPD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonGuardarPDActionPerformed(evt);
            }
        });
        jPanel43.add(BotonGuardarPD);

        jSeparator2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel44.setLayout(new java.awt.GridLayout(4, 0));

        BotonCapturarPI.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BotonCapturarPI.setText("Capturar");
        BotonCapturarPI.setEnabled(false);
        BotonCapturarPI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonCapturarPIActionPerformed(evt);
            }
        });
        jPanel44.add(BotonCapturarPI);

        BotonAnteriorPI.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BotonAnteriorPI.setText("Anterior");
        BotonAnteriorPI.setEnabled(false);
        BotonAnteriorPI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonAnteriorPIActionPerformed(evt);
            }
        });
        jPanel44.add(BotonAnteriorPI);

        BotonCalcularPI.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BotonCalcularPI.setText("Calcular");
        BotonCalcularPI.setEnabled(false);
        BotonCalcularPI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonCalcularPIActionPerformed(evt);
            }
        });
        jPanel44.add(BotonCalcularPI);

        BotonGuardarPI.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BotonGuardarPI.setText("Guardar");
        BotonGuardarPI.setEnabled(false);
        BotonGuardarPI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonGuardarPIActionPerformed(evt);
            }
        });
        jPanel44.add(BotonGuardarPI);

        jLabel126.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel126.setText("Unidades:");

        SeleccionKgoN.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        SeleccionKgoN.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Kg", "N" }));
        SeleccionKgoN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SeleccionKgoNActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel60, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel61, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                        .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel62, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addGap(18, 18, 18)
                        .addComponent(jPanel43, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel126)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(SeleccionKgoN, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel81, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jPanel23, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jPanel24, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jPanel25, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel82)))
                    .addComponent(jLabel80, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jPanel44, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(36, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator2)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel60)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel61)
                            .addComponent(jLabel62))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel17, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel16, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel19, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel20, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel18, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel43, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel126)
                            .addComponent(SeleccionKgoN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(20, 20, 20))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel80)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel81)
                            .addComponent(jLabel82))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel44, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jPanel24, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel25, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel23, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel21, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel22, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        jTabbedPane1.addTab("Peso", jPanel2);

        jPanel3.setMaximumSize(new java.awt.Dimension(987, 297));

        jPanel26.setLayout(new java.awt.GridLayout(4, 0));

        jLabel83.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel83.setText("Punto 0:");
        jPanel26.add(jLabel83);

        jLabel84.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel84.setText("Punto 1:");
        jPanel26.add(jLabel84);

        jLabel85.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel85.setText("Punto 2:");
        jPanel26.add(jLabel85);

        jLabel86.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel86.setText("Punto 3:");
        jPanel26.add(jLabel86);

        jPanel27.setLayout(new java.awt.GridLayout(4, 0));

        jLabel87.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel87.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel87.setText("0");
        jPanel27.add(jLabel87);

        CampoTexPun1VD.setEditable(false);
        CampoTexPun1VD.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jPanel27.add(CampoTexPun1VD);

        CampoTexPun2VD.setEditable(false);
        CampoTexPun2VD.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jPanel27.add(CampoTexPun2VD);

        CampoTexPun3VD.setEditable(false);
        CampoTexPun3VD.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jPanel27.add(CampoTexPun3VD);

        jPanel28.setLayout(new java.awt.GridLayout(4, 0));

        jLabel88.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel88.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel88.setText("Km/h");
        jPanel28.add(jLabel88);

        jLabel89.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel89.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel89.setText("Km/h");
        jPanel28.add(jLabel89);

        jLabel90.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel90.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel90.setText("Km/h");
        jPanel28.add(jLabel90);

        jLabel91.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel91.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel91.setText("Km/h");
        jPanel28.add(jLabel91);

        jPanel29.setLayout(new java.awt.GridLayout(4, 0));

        LabelPun0VD.setBackground(new java.awt.Color(255, 0, 0));
        LabelPun0VD.setMaximumSize(new java.awt.Dimension(40, 14));
        LabelPun0VD.setMinimumSize(new java.awt.Dimension(40, 14));
        LabelPun0VD.setOpaque(true);
        LabelPun0VD.setPreferredSize(new java.awt.Dimension(40, 14));
        jPanel29.add(LabelPun0VD);

        LabelPun1VD.setMaximumSize(new java.awt.Dimension(40, 14));
        LabelPun1VD.setMinimumSize(new java.awt.Dimension(40, 14));
        LabelPun1VD.setOpaque(true);
        LabelPun1VD.setPreferredSize(new java.awt.Dimension(40, 14));
        jPanel29.add(LabelPun1VD);

        LabelPun2VD.setMaximumSize(new java.awt.Dimension(40, 14));
        LabelPun2VD.setMinimumSize(new java.awt.Dimension(40, 14));
        LabelPun2VD.setOpaque(true);
        LabelPun2VD.setPreferredSize(new java.awt.Dimension(40, 14));
        jPanel29.add(LabelPun2VD);

        LabelPun3VD.setMaximumSize(new java.awt.Dimension(40, 14));
        LabelPun3VD.setMinimumSize(new java.awt.Dimension(40, 14));
        LabelPun3VD.setOpaque(true);
        LabelPun3VD.setPreferredSize(new java.awt.Dimension(40, 14));
        jPanel29.add(LabelPun3VD);

        jPanel30.setLayout(new java.awt.GridLayout(4, 0));

        jLabel96.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel96.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel96.setText("uni");
        jPanel30.add(jLabel96);

        jLabel97.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel97.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel97.setText("uni");
        jPanel30.add(jLabel97);

        jLabel98.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel98.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel98.setText("uni");
        jPanel30.add(jLabel98);

        jLabel99.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel99.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel99.setText("uni");
        jPanel30.add(jLabel99);

        jLabel100.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel100.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel100.setText("PARTE DERECHA");

        jLabel101.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel101.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel101.setText("PATRONES");

        jLabel102.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel102.setText("MEDICIONES");

        jPanel31.setLayout(new java.awt.GridLayout(4, 0));

        jLabel103.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel103.setText("Punto 0:");
        jPanel31.add(jLabel103);

        jLabel104.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel104.setText("Punto 1:");
        jPanel31.add(jLabel104);

        jLabel105.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel105.setText("Punto 2:");
        jPanel31.add(jLabel105);

        jLabel106.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel106.setText("Punto 3:");
        jPanel31.add(jLabel106);

        jPanel32.setLayout(new java.awt.GridLayout(4, 0));

        jLabel107.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel107.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel107.setText("0");
        jPanel32.add(jLabel107);

        CampoTexPun1VI.setEditable(false);
        CampoTexPun1VI.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jPanel32.add(CampoTexPun1VI);

        CampoTexPun2VI.setEditable(false);
        CampoTexPun2VI.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jPanel32.add(CampoTexPun2VI);

        CampoTexPun3VI.setEditable(false);
        CampoTexPun3VI.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jPanel32.add(CampoTexPun3VI);

        jPanel33.setLayout(new java.awt.GridLayout(4, 0));

        jLabel108.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel108.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel108.setText("Km/h");
        jPanel33.add(jLabel108);

        jLabel109.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel109.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel109.setText("Km/h");
        jPanel33.add(jLabel109);

        jLabel110.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel110.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel110.setText("Km/h");
        jPanel33.add(jLabel110);

        jLabel111.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel111.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel111.setText("Km/h");
        jPanel33.add(jLabel111);

        jPanel34.setLayout(new java.awt.GridLayout(4, 0));

        LabelPun0VI.setBackground(new java.awt.Color(255, 0, 0));
        LabelPun0VI.setOpaque(true);
        jPanel34.add(LabelPun0VI);

        LabelPun1VI.setOpaque(true);
        jPanel34.add(LabelPun1VI);

        LabelPun2VI.setOpaque(true);
        jPanel34.add(LabelPun2VI);

        LabelPun3VI.setOpaque(true);
        jPanel34.add(LabelPun3VI);

        jPanel35.setLayout(new java.awt.GridLayout(4, 0));

        jLabel116.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel116.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel116.setText("uni");
        jPanel35.add(jLabel116);

        jLabel117.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel117.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel117.setText("uni");
        jPanel35.add(jLabel117);

        jLabel118.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel118.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel118.setText("uni");
        jPanel35.add(jLabel118);

        jLabel119.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel119.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel119.setText("uni");
        jPanel35.add(jLabel119);

        jLabel120.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel120.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel120.setText("PARTE IZQUIERDA");

        jLabel121.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel121.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel121.setText("PATRONES");

        jLabel122.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel122.setText("MEDICIONES");

        jPanel45.setLayout(new java.awt.GridLayout(4, 0));

        BotonCapturarVD.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BotonCapturarVD.setText("Capturar");
        BotonCapturarVD.setEnabled(false);
        BotonCapturarVD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonCapturarVDActionPerformed(evt);
            }
        });
        jPanel45.add(BotonCapturarVD);

        BotonAnteriorVD.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BotonAnteriorVD.setText("Anterior");
        BotonAnteriorVD.setEnabled(false);
        BotonAnteriorVD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonAnteriorVDActionPerformed(evt);
            }
        });
        jPanel45.add(BotonAnteriorVD);

        BotonCalcularVD.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BotonCalcularVD.setText("Calcular");
        BotonCalcularVD.setEnabled(false);
        BotonCalcularVD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonCalcularVDActionPerformed(evt);
            }
        });
        jPanel45.add(BotonCalcularVD);

        BotonGuardarVD.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BotonGuardarVD.setText("Guardar");
        BotonGuardarVD.setEnabled(false);
        BotonGuardarVD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonGuardarVDActionPerformed(evt);
            }
        });
        jPanel45.add(BotonGuardarVD);

        jSeparator3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel46.setLayout(new java.awt.GridLayout(4, 0));

        BotonCapturarVI.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BotonCapturarVI.setText("Capturar");
        BotonCapturarVI.setEnabled(false);
        BotonCapturarVI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonCapturarVIActionPerformed(evt);
            }
        });
        jPanel46.add(BotonCapturarVI);

        BotonAnteriorVI.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BotonAnteriorVI.setText("Anterior");
        BotonAnteriorVI.setEnabled(false);
        BotonAnteriorVI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonAnteriorVIActionPerformed(evt);
            }
        });
        jPanel46.add(BotonAnteriorVI);

        BotonCalcularVI.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BotonCalcularVI.setText("Calcular");
        BotonCalcularVI.setEnabled(false);
        BotonCalcularVI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonCalcularVIActionPerformed(evt);
            }
        });
        jPanel46.add(BotonCalcularVI);

        BotonGuardarVI.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BotonGuardarVI.setText("Guardar");
        BotonGuardarVI.setEnabled(false);
        BotonGuardarVI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonGuardarVIActionPerformed(evt);
            }
        });
        jPanel46.add(BotonGuardarVI);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel26, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel100, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel101, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                .addComponent(jPanel27, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jPanel28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jPanel29, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jPanel30, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel102, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(18, 18, 18)
                .addComponent(jPanel45, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel31, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel121, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                .addComponent(jPanel32, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jPanel33, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jPanel34, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jPanel35, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel122)))
                    .addComponent(jLabel120, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jPanel46, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator3)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel100)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel101)
                            .addComponent(jLabel102))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel45, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel120)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel121)
                            .addComponent(jLabel122))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel46, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel33, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jPanel34, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel35, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel31, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel32, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE)))))
                .addGap(52, 52, 52))
        );

        jTabbedPane1.addTab("Velocidad", jPanel3);

        LabelPun0D.setBackground(new java.awt.Color(255, 0, 0));
        LabelPun0D.setMaximumSize(new java.awt.Dimension(40, 14));
        LabelPun0D.setMinimumSize(new java.awt.Dimension(40, 14));
        LabelPun0D.setOpaque(true);
        LabelPun0D.setPreferredSize(new java.awt.Dimension(40, 14));

        LabelPun1D.setMaximumSize(new java.awt.Dimension(40, 14));
        LabelPun1D.setMinimumSize(new java.awt.Dimension(40, 14));
        LabelPun1D.setOpaque(true);
        LabelPun1D.setPreferredSize(new java.awt.Dimension(40, 14));

        LabelPun2D.setMaximumSize(new java.awt.Dimension(40, 14));
        LabelPun2D.setMinimumSize(new java.awt.Dimension(40, 14));
        LabelPun2D.setOpaque(true);
        LabelPun2D.setPreferredSize(new java.awt.Dimension(40, 14));

        javax.swing.GroupLayout jPanel36Layout = new javax.swing.GroupLayout(jPanel36);
        jPanel36.setLayout(jPanel36Layout);
        jPanel36Layout.setHorizontalGroup(
            jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(LabelPun0D, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(LabelPun1D, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(LabelPun2D, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel36Layout.setVerticalGroup(
            jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel36Layout.createSequentialGroup()
                .addComponent(LabelPun0D, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(LabelPun1D, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(LabelPun2D, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jLabel127.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel127.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel127.setText("uni");

        jLabel128.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel128.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel128.setText("uni");

        jLabel129.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel129.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel129.setText("uni");

        javax.swing.GroupLayout jPanel37Layout = new javax.swing.GroupLayout(jPanel37);
        jPanel37.setLayout(jPanel37Layout);
        jPanel37Layout.setHorizontalGroup(
            jPanel37Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel127, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jLabel128, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jLabel129, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel37Layout.setVerticalGroup(
            jPanel37Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel37Layout.createSequentialGroup()
                .addComponent(jLabel127, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jLabel128, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jLabel129, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jLabel131.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel131.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel131.setText("mm");

        jLabel132.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel132.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel132.setText("mm");

        jLabel133.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel133.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel133.setText("mm");

        javax.swing.GroupLayout jPanel38Layout = new javax.swing.GroupLayout(jPanel38);
        jPanel38.setLayout(jPanel38Layout);
        jPanel38Layout.setHorizontalGroup(
            jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel131, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jLabel132, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jLabel133, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel38Layout.setVerticalGroup(
            jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel38Layout.createSequentialGroup()
                .addComponent(jLabel131, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jLabel132, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jLabel133, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jLabel135.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel135.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel135.setText("0");

        CampoTexPun1D.setEditable(false);
        CampoTexPun1D.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        CampoTexPun2D.setEditable(false);
        CampoTexPun2D.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        javax.swing.GroupLayout jPanel39Layout = new javax.swing.GroupLayout(jPanel39);
        jPanel39.setLayout(jPanel39Layout);
        jPanel39Layout.setHorizontalGroup(
            jPanel39Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel135, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(CampoTexPun1D, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(CampoTexPun2D, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel39Layout.setVerticalGroup(
            jPanel39Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel39Layout.createSequentialGroup()
                .addComponent(jLabel135, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(CampoTexPun1D, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(CampoTexPun2D, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jLabel136.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel136.setText("Punto 0 (Cero):");

        jLabel137.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel137.setText("Punto 1 (Derecha):");

        jLabel138.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel138.setText("Punto 2 (Izquierda):");

        javax.swing.GroupLayout jPanel40Layout = new javax.swing.GroupLayout(jPanel40);
        jPanel40.setLayout(jPanel40Layout);
        jPanel40Layout.setHorizontalGroup(
            jPanel40Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel136, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jLabel137, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jLabel138)
        );
        jPanel40Layout.setVerticalGroup(
            jPanel40Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel40Layout.createSequentialGroup()
                .addComponent(jLabel136, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jLabel137, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jLabel138, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jLabel140.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel140.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel140.setText("PATRONES");

        jLabel141.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel141.setText("MEDICIONES");

        BotonCapturarD.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BotonCapturarD.setText("Capturar");
        BotonCapturarD.setEnabled(false);
        BotonCapturarD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonCapturarDActionPerformed(evt);
            }
        });

        BotonAnteriorD.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BotonAnteriorD.setText("Anterior");
        BotonAnteriorD.setEnabled(false);
        BotonAnteriorD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonAnteriorDActionPerformed(evt);
            }
        });

        BotonCalcularD.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BotonCalcularD.setText("Calcular");
        BotonCalcularD.setEnabled(false);
        BotonCalcularD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonCalcularDActionPerformed(evt);
            }
        });

        BotonGuardarD.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BotonGuardarD.setText("Guardar");
        BotonGuardarD.setEnabled(false);
        BotonGuardarD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonGuardarDActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel47Layout = new javax.swing.GroupLayout(jPanel47);
        jPanel47.setLayout(jPanel47Layout);
        jPanel47Layout.setHorizontalGroup(
            jPanel47Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel47Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel47Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(BotonCapturarD)
                    .addComponent(BotonAnteriorD, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BotonCalcularD, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BotonGuardarD, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel47Layout.setVerticalGroup(
            jPanel47Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel47Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(BotonCapturarD, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(BotonAnteriorD, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(BotonCalcularD, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(BotonGuardarD, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel40, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel140, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jPanel39, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel38, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jPanel36, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel37, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel141))
                .addGap(18, 18, 18)
                .addComponent(jPanel47, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel140)
                    .addComponent(jLabel141))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jPanel36, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel38, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel39, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel40, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel37, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel47, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Desviación", jPanel4);

        jPanel5.setLayout(new java.awt.GridLayout(2, 0));

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/solt.png"))); // NOI18N
        jPanel5.add(jLabel1);

        jLabel2.setFont(new java.awt.Font("Showcard Gothic", 0, 36)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("MENU DE CALIBRACION");
        jPanel5.add(jLabel2);

        BotonSalir.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        BotonSalir.setText("Salir");
        BotonSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonSalirActionPerformed(evt);
            }
        });

        BotonConectar.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        BotonConectar.setText("Conectar");
        BotonConectar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonConectarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, 1038, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1008, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(BotonConectar)
                        .addGap(70, 70, 70)
                        .addComponent(BotonSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 336, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BotonConectar, javax.swing.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
                    .addComponent(BotonSalir, javax.swing.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE))
                .addGap(25, 25, 25))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BotonConectarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonConectarActionPerformed
        // TODO add your handling code here:
        int opcion;
        comandoSTOP();
        if (!PruebaConexion()) {
            opcion = JOptionPane.showOptionDialog(this, "La tarjeta no se encuentra conectada o inicializada."
                    + " Desea conectarla", "Configuración", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Si", "No"}, "Si");
            if (opcion == 0) {
                Tarjeta tar = new Tarjeta();
                tar.setPuerto(puerto);
                tar.IniciaTarjeta();
                if (tar.getNumconex() == 3) {
                    JOptionPane.showMessageDialog(null, "Llame a servicio técnico","Error", JOptionPane.ERROR_MESSAGE);
                    puerto.close();
                    dispose();
                } else {
                    try {
                        Thread.currentThread().sleep(500);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(DlgFrenoMoto.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    BotonConectar.doClick();
                }
            } else {
                puerto.close();
                dispose();
            }
        } else {
            BotonCapturarFD.setEnabled(true);
            BotonCapturarFI.setEnabled(true);
            BotonCapturarPD.setEnabled(true);
            BotonCapturarPI.setEnabled(true);
            BotonCapturarVD.setEnabled(true);
            BotonCapturarVI.setEnabled(true);
            BotonCapturarD.setEnabled(true);
        }
        BotonConectar.setEnabled(false);
    }//GEN-LAST:event_BotonConectarActionPerformed

    private void BotonSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonSalirActionPerformed
        try {
            puerto.close();
        } catch (NullPointerException ex) {
            System.out.println("El puerto no fue creado");
        }
        this.dispose();
    }//GEN-LAST:event_BotonSalirActionPerformed

    private void BotonCapturarFDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonCapturarFDActionPerformed
        CapturarFD();
    }//GEN-LAST:event_BotonCapturarFDActionPerformed

    private void BotonAnteriorFDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonAnteriorFDActionPerformed
        AnteriorFD();
    }//GEN-LAST:event_BotonAnteriorFDActionPerformed

    private void BotonCalcularFDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonCalcularFDActionPerformed
        double[] aux;
        aux = Regresionlineal(muestrasfd, patronesfd);
        spanfd = aux[0];
        offsetfd = aux[1];
        System.out.println(spanfd);
        System.out.println(offsetfd);
        JOptionPane.showMessageDialog(null, "<html>Los valores obtenidos en la calibracion fueron: <br>"
                + "span= " + spanfd + "<br> offset= " + offsetfd + "</html>", "Valores calculados", JOptionPane.INFORMATION_MESSAGE);
        BotonGuardarFD.setEnabled(true);
    }//GEN-LAST:event_BotonCalcularFDActionPerformed

    private void BotonGuardarFDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonGuardarFDActionPerformed
        Guardar(0);
    }//GEN-LAST:event_BotonGuardarFDActionPerformed

    private void BotonCapturarFIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonCapturarFIActionPerformed
        CapturarFI();
    }//GEN-LAST:event_BotonCapturarFIActionPerformed

    private void BotonAnteriorFIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonAnteriorFIActionPerformed
        AnteriorFI();
    }//GEN-LAST:event_BotonAnteriorFIActionPerformed

    private void BotonCalcularFIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonCalcularFIActionPerformed
        double[] aux;
        aux = Regresionlineal(muestrasfi, patronesfi);
        spanfi = aux[0];
        offsetfi = aux[1];
        System.out.println(spanfi);
        System.out.println(offsetfi);
        JOptionPane.showMessageDialog(null, "<html>Los valores obtenidos en la calibracion fueron: <br>"
                + "span= " + spanfi + "<br> offset= " + offsetfi + "</html>", "Valores calculados", JOptionPane.INFORMATION_MESSAGE);
        BotonGuardarFI.setEnabled(true);
    }//GEN-LAST:event_BotonCalcularFIActionPerformed

    private void BotonGuardarFIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonGuardarFIActionPerformed
        Guardar(1);
    }//GEN-LAST:event_BotonGuardarFIActionPerformed

    private void BotonCapturarPDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonCapturarPDActionPerformed
        CapturarPD();
    }//GEN-LAST:event_BotonCapturarPDActionPerformed

    private void BotonAnteriorPDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonAnteriorPDActionPerformed
        AnteriorPD();
    }//GEN-LAST:event_BotonAnteriorPDActionPerformed

    private void BotonCalcularPDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonCalcularPDActionPerformed
        double[] aux;
        aux = Regresionlineal(muestraspd, patronespd);
        spanpd = aux[0];
        offsetpd = aux[1];
        System.out.println(spanpd);
        System.out.println(offsetpd);
        JOptionPane.showMessageDialog(null, "<html>Los valores obtenidos en la calibracion fueron: <br>"
                + "span= " + spanpd + "<br> offset= " + offsetpd + "</html>", "Valores calculados", JOptionPane.INFORMATION_MESSAGE);
        BotonGuardarPD.setEnabled(true);
    }//GEN-LAST:event_BotonCalcularPDActionPerformed

    private void BotonGuardarPDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonGuardarPDActionPerformed
        Guardar(2);
    }//GEN-LAST:event_BotonGuardarPDActionPerformed

    private void BotonCapturarPIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonCapturarPIActionPerformed
        CapturarPI();
    }//GEN-LAST:event_BotonCapturarPIActionPerformed

    private void BotonAnteriorPIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonAnteriorPIActionPerformed
        AnteriorPI();
    }//GEN-LAST:event_BotonAnteriorPIActionPerformed

    private void BotonCalcularPIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonCalcularPIActionPerformed
        double[] aux;
        aux = Regresionlineal(muestraspi, patronespi);
        spanpi = aux[0];
        offsetpi = aux[1];
        System.out.println(spanpi);
        System.out.println(offsetpi);
        JOptionPane.showMessageDialog(null, "<html>Los valores obtenidos en la calibracion fueron: <br>"
                + "span= " + spanpi + "<br> offset= " + offsetpi + "</html>", "Valores calculados", JOptionPane.INFORMATION_MESSAGE);
        BotonGuardarPI.setEnabled(true);
    }//GEN-LAST:event_BotonCalcularPIActionPerformed

    private void BotonGuardarPIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonGuardarPIActionPerformed
        Guardar(3);
    }//GEN-LAST:event_BotonGuardarPIActionPerformed

    private void BotonCapturarVDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonCapturarVDActionPerformed
        CapturarVD();
    }//GEN-LAST:event_BotonCapturarVDActionPerformed

    private void BotonAnteriorVDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonAnteriorVDActionPerformed
        AnteriorVD();
    }//GEN-LAST:event_BotonAnteriorVDActionPerformed

    private void BotonCalcularVDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonCalcularVDActionPerformed
        double[] aux;
        aux = Regresionlineal(muestrasvd, patronesvd);
        spanvd = aux[0];
        offsetvd = aux[1];
        System.out.println(spanvd);
        System.out.println(offsetvd);
        JOptionPane.showMessageDialog(null, "<html>Los valores obtenidos en la calibracion fueron: <br>"
                + "span= " + spanvd + "<br> offset= " + offsetvd + "</html>", "Valores calculados", JOptionPane.INFORMATION_MESSAGE);
        BotonGuardarVD.setEnabled(true);
    }//GEN-LAST:event_BotonCalcularVDActionPerformed

    private void BotonGuardarVDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonGuardarVDActionPerformed
        Guardar(4);
    }//GEN-LAST:event_BotonGuardarVDActionPerformed

    private void BotonCapturarVIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonCapturarVIActionPerformed
        CapturarVI();
    }//GEN-LAST:event_BotonCapturarVIActionPerformed

    private void BotonAnteriorVIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonAnteriorVIActionPerformed
        AnteriorVI();
    }//GEN-LAST:event_BotonAnteriorVIActionPerformed

    private void BotonCalcularVIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonCalcularVIActionPerformed
        double[] aux;
        aux = Regresionlineal(muestrasvi, patronesvi);
        spanvi = aux[0];
        offsetvi = aux[1];
        System.out.println(spanvi);
        System.out.println(offsetvi);
        JOptionPane.showMessageDialog(null, "<html>Los valores obtenidos en la calibracion fueron: <br>"
                + "span= " + spanvi + "<br> offset= " + offsetvi + "</html>", "Valores calculados", JOptionPane.INFORMATION_MESSAGE);
        BotonGuardarVI.setEnabled(true);
    }//GEN-LAST:event_BotonCalcularVIActionPerformed

    private void BotonGuardarVIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonGuardarVIActionPerformed
        Guardar(5);
    }//GEN-LAST:event_BotonGuardarVIActionPerformed

    private void BotonCapturarDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonCapturarDActionPerformed
        CapturarD();
    }//GEN-LAST:event_BotonCapturarDActionPerformed

    private void BotonAnteriorDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonAnteriorDActionPerformed
        AnteriorD();
    }//GEN-LAST:event_BotonAnteriorDActionPerformed

    private void BotonCalcularDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonCalcularDActionPerformed
        double[] aux = new double[2];
        aux = Regresionlineal(muestrasd, patronesd);
        spand = aux[0];
        offsetd = aux[1];
        System.out.println(spand);
        System.out.println(offsetd);
        JOptionPane.showMessageDialog(null, "<html>Los valores obtenidos en la calibracion fueron: <br>"
                + "span= " + spand + "<br> offset= " + offsetd + "</html>", "Valores calculados", JOptionPane.INFORMATION_MESSAGE);
        BotonGuardarD.setEnabled(true);
    }//GEN-LAST:event_BotonCalcularDActionPerformed

    private void BotonGuardarDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonGuardarDActionPerformed
        Guardar(6);
    }//GEN-LAST:event_BotonGuardarDActionPerformed

    private void SeleccionKgoNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SeleccionKgoNActionPerformed
        double aux;
        DecimalFormatSymbols simbolos = new DecimalFormatSymbols();
        simbolos.setDecimalSeparator('.');
        DecimalFormat formateador = new DecimalFormat("####.00", simbolos);
        if (SeleccionKgoN.getSelectedIndex() == 0) {
            uniseleccionada = "Kg";
            Label0uniPD.setText("Kg");
            Label1uniPD.setText("Kg");
            Label2uniPD.setText("Kg");
            Label3uniPD.setText("Kg");
            Label0uniPI.setText("Kg");
            Label1uniPI.setText("Kg");
            Label2uniPI.setText("Kg");
            Label3uniPI.setText("Kg");
            switch (4 - numpuntopd) {
                case 0:
                    aux = patronespd[3];
                    aux /= gravedad;
                    CampoTexPun3PD.setText(formateador.format(aux));
                case 1:
                    aux = patronespd[2];
                    aux /= gravedad;
                    CampoTexPun2PD.setText(formateador.format(aux));
                case 2:
                    aux = patronespd[1];
                    aux /= gravedad;
                    CampoTexPun1PD.setText(formateador.format(aux));
                case 3:
                    break;
            }
            switch (4 - numpuntopi) {
                case 0:
                    aux = patronespi[3];
                    aux /= gravedad;
                    CampoTexPun3PI.setText(formateador.format(aux));
                case 1:
                    aux = patronespi[2];
                    aux /= gravedad;
                    CampoTexPun2PI.setText(formateador.format(aux));
                case 2:
                    aux = patronespi[1];
                    aux /= gravedad;
                    CampoTexPun1PI.setText(formateador.format(aux));
                case 3:
                    break;
            }
        } else {
            uniseleccionada = "N";
            Label0uniPD.setText("N");
            Label1uniPD.setText("N");
            Label2uniPD.setText("N");
            Label3uniPD.setText("N");
            Label0uniPI.setText("N");
            Label1uniPI.setText("N");
            Label2uniPI.setText("N");
            Label3uniPI.setText("N");
            switch (4 - numpuntopd) {
                case 0:
                    CampoTexPun3PD.setText(formateador.format(patronespd[3]));
                case 1:
                    CampoTexPun2PD.setText(formateador.format(patronespd[2]));
                case 2:
                    CampoTexPun1PD.setText(formateador.format(patronespd[1]));
                case 3:
                    break;
            }
            switch (4 - numpuntopi) {
                case 0:
                    CampoTexPun3PI.setText(formateador.format(patronespi[3]));
                case 1:
                    CampoTexPun2PI.setText(formateador.format(patronespi[2]));
                case 2:
                    CampoTexPun1PI.setText(formateador.format(patronespi[1]));
                case 3:
                    break;
            }
        }
    }//GEN-LAST:event_SeleccionKgoNActionPerformed

    public void CapturarDatos() {
        try {
            len = puerto.in.read(buffer);
            if (len > 0) {
                for (i = 0; i < len; i++) {
                    data = buffer[i];
                    if (data == 72) {
                        ComandoRecibido[0] = data;
                        j = 1;
                    } else if (j == 1) {
                        if (data <= 15) {

                        } else {
                        }
                        j = 2;
                    } else if (j == 2) {
                        partealta = byteToInt(data);
                        j = 3;
                    } else if (j == 3) {
                        ComandoRecibido[1] = partealta * 256 + byteToInt(data);
                        if (ComandoRecibido[1] <= 4095) {
                            Datos1.add(ComandoRecibido[1]);
                        } else {

                        }
                        j = 4;
                    } else if (j == 4) {
                        ComandoRecibido[2] = data;
                        j = 0;
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static int byteToInt(byte b) {
        String cadenaLSB = Integer.toBinaryString(b & 0xFF);
        StringBuilder lsb = new StringBuilder();
        for (int i = cadenaLSB.length() - 1; i < 7; i++) {
            lsb.append('0');
        }
        lsb.append(cadenaLSB);
        int valor = Integer.parseInt(cadenaLSB, 2);
        return valor;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                DlgCalibracion dialog = new DlgCalibracion(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BotonAnteriorD;
    private javax.swing.JButton BotonAnteriorFD;
    private javax.swing.JButton BotonAnteriorFI;
    private javax.swing.JButton BotonAnteriorPD;
    private javax.swing.JButton BotonAnteriorPI;
    private javax.swing.JButton BotonAnteriorVD;
    private javax.swing.JButton BotonAnteriorVI;
    private javax.swing.JButton BotonCalcularD;
    private javax.swing.JButton BotonCalcularFD;
    private javax.swing.JButton BotonCalcularFI;
    private javax.swing.JButton BotonCalcularPD;
    private javax.swing.JButton BotonCalcularPI;
    private javax.swing.JButton BotonCalcularVD;
    private javax.swing.JButton BotonCalcularVI;
    private javax.swing.JButton BotonCapturarD;
    private javax.swing.JButton BotonCapturarFD;
    private javax.swing.JButton BotonCapturarFI;
    private javax.swing.JButton BotonCapturarPD;
    private javax.swing.JButton BotonCapturarPI;
    private javax.swing.JButton BotonCapturarVD;
    private javax.swing.JButton BotonCapturarVI;
    private javax.swing.JButton BotonConectar;
    private javax.swing.JButton BotonGuardarD;
    private javax.swing.JButton BotonGuardarFD;
    private javax.swing.JButton BotonGuardarFI;
    private javax.swing.JButton BotonGuardarPD;
    private javax.swing.JButton BotonGuardarPI;
    private javax.swing.JButton BotonGuardarVD;
    private javax.swing.JButton BotonGuardarVI;
    private javax.swing.JButton BotonSalir;
    private javax.swing.JTextField CampoTexPun1D;
    private javax.swing.JTextField CampoTexPun1FD;
    private javax.swing.JTextField CampoTexPun1FI;
    private javax.swing.JTextField CampoTexPun1PD;
    private javax.swing.JTextField CampoTexPun1PI;
    private javax.swing.JTextField CampoTexPun1VD;
    private javax.swing.JTextField CampoTexPun1VI;
    private javax.swing.JTextField CampoTexPun2D;
    private javax.swing.JTextField CampoTexPun2FD;
    private javax.swing.JTextField CampoTexPun2FI;
    private javax.swing.JTextField CampoTexPun2PD;
    private javax.swing.JTextField CampoTexPun2PI;
    private javax.swing.JTextField CampoTexPun2VD;
    private javax.swing.JTextField CampoTexPun2VI;
    private javax.swing.JTextField CampoTexPun3FD;
    private javax.swing.JTextField CampoTexPun3FI;
    private javax.swing.JTextField CampoTexPun3PD;
    private javax.swing.JTextField CampoTexPun3PI;
    private javax.swing.JTextField CampoTexPun3VD;
    private javax.swing.JTextField CampoTexPun3VI;
    private javax.swing.JLabel Label0uniPD;
    private javax.swing.JLabel Label0uniPI;
    private javax.swing.JLabel Label1uniPD;
    private javax.swing.JLabel Label1uniPI;
    private javax.swing.JLabel Label2uniPD;
    private javax.swing.JLabel Label2uniPI;
    private javax.swing.JLabel Label3uniPD;
    private javax.swing.JLabel Label3uniPI;
    private javax.swing.JLabel LabelPun0D;
    private javax.swing.JLabel LabelPun0FD;
    private javax.swing.JLabel LabelPun0FI;
    private javax.swing.JLabel LabelPun0PD;
    private javax.swing.JLabel LabelPun0PI;
    private javax.swing.JLabel LabelPun0VD;
    private javax.swing.JLabel LabelPun0VI;
    private javax.swing.JLabel LabelPun1D;
    private javax.swing.JLabel LabelPun1FD;
    private javax.swing.JLabel LabelPun1FI;
    private javax.swing.JLabel LabelPun1PD;
    private javax.swing.JLabel LabelPun1PI;
    private javax.swing.JLabel LabelPun1VD;
    private javax.swing.JLabel LabelPun1VI;
    private javax.swing.JLabel LabelPun2D;
    private javax.swing.JLabel LabelPun2FD;
    private javax.swing.JLabel LabelPun2FI;
    private javax.swing.JLabel LabelPun2PD;
    private javax.swing.JLabel LabelPun2PI;
    private javax.swing.JLabel LabelPun2VD;
    private javax.swing.JLabel LabelPun2VI;
    private javax.swing.JLabel LabelPun3FD;
    private javax.swing.JLabel LabelPun3FI;
    private javax.swing.JLabel LabelPun3PD;
    private javax.swing.JLabel LabelPun3PI;
    private javax.swing.JLabel LabelPun3VD;
    private javax.swing.JLabel LabelPun3VI;
    private javax.swing.JComboBox SeleccionKgoN;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel100;
    private javax.swing.JLabel jLabel101;
    private javax.swing.JLabel jLabel102;
    private javax.swing.JLabel jLabel103;
    private javax.swing.JLabel jLabel104;
    private javax.swing.JLabel jLabel105;
    private javax.swing.JLabel jLabel106;
    private javax.swing.JLabel jLabel107;
    private javax.swing.JLabel jLabel108;
    private javax.swing.JLabel jLabel109;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel110;
    private javax.swing.JLabel jLabel111;
    private javax.swing.JLabel jLabel116;
    private javax.swing.JLabel jLabel117;
    private javax.swing.JLabel jLabel118;
    private javax.swing.JLabel jLabel119;
    private javax.swing.JLabel jLabel120;
    private javax.swing.JLabel jLabel121;
    private javax.swing.JLabel jLabel122;
    private javax.swing.JLabel jLabel126;
    private javax.swing.JLabel jLabel127;
    private javax.swing.JLabel jLabel128;
    private javax.swing.JLabel jLabel129;
    private javax.swing.JLabel jLabel131;
    private javax.swing.JLabel jLabel132;
    private javax.swing.JLabel jLabel133;
    private javax.swing.JLabel jLabel135;
    private javax.swing.JLabel jLabel136;
    private javax.swing.JLabel jLabel137;
    private javax.swing.JLabel jLabel138;
    private javax.swing.JLabel jLabel140;
    private javax.swing.JLabel jLabel141;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel87;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JLabel jLabel89;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel90;
    private javax.swing.JLabel jLabel91;
    private javax.swing.JLabel jLabel96;
    private javax.swing.JLabel jLabel97;
    private javax.swing.JLabel jLabel98;
    private javax.swing.JLabel jLabel99;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JPanel jPanel28;
    private javax.swing.JPanel jPanel29;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel30;
    private javax.swing.JPanel jPanel31;
    private javax.swing.JPanel jPanel32;
    private javax.swing.JPanel jPanel33;
    private javax.swing.JPanel jPanel34;
    private javax.swing.JPanel jPanel35;
    private javax.swing.JPanel jPanel36;
    private javax.swing.JPanel jPanel37;
    private javax.swing.JPanel jPanel38;
    private javax.swing.JPanel jPanel39;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel40;
    private javax.swing.JPanel jPanel41;
    private javax.swing.JPanel jPanel42;
    private javax.swing.JPanel jPanel43;
    private javax.swing.JPanel jPanel44;
    private javax.swing.JPanel jPanel45;
    private javax.swing.JPanel jPanel46;
    private javax.swing.JPanel jPanel47;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables

}
