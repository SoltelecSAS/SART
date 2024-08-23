/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.procesosbanco;

import org.soltelec.util.MedicionGases;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import org.soltelec.util.AproximacionMedidas;
import org.soltelec.util.MedicionOpacidad;

/**
 *
 * @author Gerencia Desarrollo de Soluciones Tecnologicas
 */
public class ProcesoRuido implements Runnable, ActionListener {

    private float promedioCO, promedioCO2, promedioHC, promedioO2, promedioTemp, promedioRPM;
    private PanelProgreso panelProgreso;
    private Timer timer;
    private BancoGasolina banco;
    private JButton botonHabilitar, buttonGenerarArchivo;
    private WorkerDatos worker;///cuestión de sincronizacion
    int contadorTimer = 0;
    int muestra = 1;
    DecimalFormat df2;
    private int TIEMPO = 20;
    private int TIEMPOOPA = 9;
    private List<MedicionGases> listaMediciones = new LinkedList<MedicionGases>();
    private List<MedicionGases> listaMedicionesAux = new LinkedList<MedicionGases>();
    private JTable tabla;
    public static String placa;
    public static String tipos;
    public static String fecha;
    public static String serialResolucion;

    public ProcesoRuido() {
        this.buttonGenerarArchivo = new JButton("GenerarArchivo");
        buttonGenerarArchivo.addActionListener(new ManejadorGenerarArchivo());
    }

    public ProcesoRuido(PanelProgreso panelProgreso) {
        this();
        this.panelProgreso = panelProgreso;

    }

    public ProcesoRuido(String serialResolucion) {
        this.serialResolucion = serialResolucion;

    }

    public ProcesoRuido(PanelProgreso panelProgreso, WorkerDatos theWorker) {
        this.panelProgreso = panelProgreso;
        this.worker = theWorker;
        this.buttonGenerarArchivo = new JButton("Exportar Ult. Lectura de  Gases");
        buttonGenerarArchivo.addActionListener(new exporUltLectGases());
    }

    public ProcesoRuido(PanelProgreso thePanelProgreso, JButton boton) {//propositos de depuracion
        this();
        this.panelProgreso = thePanelProgreso;
        this.botonHabilitar = boton;

    }

    //constructor para interfaz grafica
    public ProcesoRuido(PanelProgreso thePanelProgreso, BancoGasolina theBanco, WorkerDatos theWorker, String serialResolucion) {
        this();
        this.panelProgreso = thePanelProgreso;
        this.banco = theBanco;
        this.worker = theWorker;
        this.serialResolucion = serialResolucion;
    }

    public void run() {
        iniciarProceso();
    }

    public void servWriteDatosFile(ArrayList<Double> listaDatosRecogidosOpacidad) {
        File file = getFile();
        try {

            PrintWriter pw = new PrintWriter(file);
            int i = 0;
            double tr = 0.500;
            int muestra = listaDatosRecogidosOpacidad.size();
            double seg = ((double) TIEMPOOPA) / muestra;
            double time = seg;
            System.out.println("TIEMPO: " + TIEMPOOPA);
            System.out.println("Total muestra: " + muestra);
            System.out.println("Tiempo x muestra: " + seg);
            Date fec = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(fec);
            String fecha = String.valueOf(cal.get(Calendar.YEAR)).concat("-");
            String mes;
            if (cal.get(Calendar.MONTH) < 9) {
                mes = String.valueOf(cal.get(Calendar.MONTH) + 1).concat("-");
            } else {
                mes = "0".concat(String.valueOf(cal.get(Calendar.MONTH) + 1).concat("-"));
            }
            String dia = String.valueOf(cal.get(Calendar.DATE));
            String hora = " Hora: ".concat(String.valueOf(cal.get(Calendar.HOUR_OF_DAY)).concat(":").concat(String.valueOf(cal.get(Calendar.MINUTE))));

            pw.printf("%s\t\n", "Serial del Opacimetro:".concat(ProcesoRuido.serialResolucion).trim());
            pw.printf("%s%s%s%s\t\n", "Fecha Generacion: ".concat(fecha.trim()), mes.trim(), dia, hora);

            pw.printf("%s\t%s\t%s\t\n", "Num", "OPAC.RECOGIDAD", "Time(.500MS)");
            double opacidad;
            for (Double opa : listaDatosRecogidosOpacidad) {
                if (opa * 0.1 >= 95) {
                    opacidad = Double.parseDouble("99.".concat(String.valueOf(opa).substring(3, 4)));
                } else {
                    opacidad = opa * 0.1;
                }
                pw.printf("%d\t%.2f\t%.2f\t\n", i, (float) opacidad, (float) tr);
                i++;
                tr = tr + 0.500;
                time += seg;
            }
            pw.flush();
        } catch (FileNotFoundException ex) {
            System.out.println("Este archivo no existe");
            ex.printStackTrace(System.err);
        }

    }//end of actionPerformed

    public void lodUltLectGases() {
        try {
            worker.setInterrumpido(true);
            FileReader fw = new FileReader("ultLectGases.soltelec");
            BufferedReader br = new BufferedReader(fw);
            String tmpStr;
            String linea, temp;
            String[] cntMuestra;
            MedicionGases medicion;
            String[] arrDatos = Utilidades.deCifrar(br.readLine()).split(";");
            //Lee la primera linea del archivo ultLectGases.soltelec que es Tipo;PlacaDeLaMoto
            if (arrDatos.length == 2) {
                ProcesoRuido.tipos = arrDatos[0];// guarda el tipo de vehiculo si es moto o si es carro
                ProcesoRuido.placa = arrDatos[1];//guarda la placa del vehiculo
                ProcesoRuido.fecha = "";
            } else if (arrDatos.length == 3) {
                ProcesoRuido.tipos = arrDatos[0];// guarda el tipo de vehiculo si es moto o si es carro
                ProcesoRuido.placa = arrDatos[1];//guarda la placa del vehiculo
                ProcesoRuido.fecha = arrDatos[2];//guarda la placa del vehiculo
            }
            Locale loc = new Locale("CO");
            NumberFormat nf = NumberFormat.getNumberInstance(loc);
            DecimalFormat df = (DecimalFormat) NumberFormat.getInstance(Locale.ENGLISH);
            df.applyPattern("#0.00");
            int n = 0;
            try {
                int bandera = 0;
                while ((linea = br.readLine()) != null) {   //mientras la linea no sea null sigue leyendo el archivo
                    temp = Utilidades.deCifrar(linea);
                    if (temp.startsWith("***") || temp.startsWith("####") || temp.startsWith("nnn") || temp.startsWith("Moto")) {
                    } else {

                        if (linea.length() > 0) {
                            linea = Utilidades.deCifrar(linea);

                            linea = linea.replace(',', '.');
                            cntMuestra = linea.split(";");
                            medicion = new MedicionGases();
                            medicion.setValorHC(Integer.parseInt(cntMuestra[0].trim()));
                            medicion.setValCO(Double.parseDouble(df.format(Double.parseDouble(cntMuestra[1].trim()))));
                            String yu = df.format(Double.parseDouble(cntMuestra[2].trim()));
                            medicion.setValCO2(Double.parseDouble(df.format(Double.parseDouble(cntMuestra[2].trim()))));
                            medicion.setValorO2(Double.parseDouble(cntMuestra[3].trim()));
                            medicion.setValorRPM(Integer.parseInt(cntMuestra[4].trim()));
                            medicion.setValorTAceite(Integer.parseInt(cntMuestra[5].trim()));
                            medicion.setLect5Seg(Boolean.parseBoolean(cntMuestra[6].trim()));
                            listaMediciones.add(medicion);
                        }
                    }
                    // System.out.println("valor de la linea: " + bandera+ " "+temp);
                    bandera++;
                }
            } catch (NullPointerException ex) {
                int e = 0;
            }

            df = (DecimalFormat) nf;
            df.applyPattern("#.###");
            JPanel panelGraficas = new JPanel(new BorderLayout());
            GenerarGrafica generarGrafica = new GenerarGrafica(listaMediciones);
            panelGraficas.add(generarGrafica.getGraficaTodas(), BorderLayout.WEST);
            //llenar la lista un JTable
            //mostrar el calculo
            //toca armar un arreglo de vectores
            Vector nombresVector = new Vector();
            nombresVector.add("NumeroMuestras");
            nombresVector.add("HC");
            nombresVector.add("CO");
            nombresVector.add("CO2");
            nombresVector.add("O2");
            nombresVector.add("RPM");
            nombresVector.add("TEMP");
            nombresVector.add("Ult. Lect.");
            int tamañoLista = listaMediciones.size();
            //sacar los promedios

            Vector vectorFilas = new Vector();
            MedicionGases med;
            int y = 0;
            for (int i = 0; i < tamañoLista; i++) {
                Vector fila = new Vector();
                fila.add(i + 1);//aqui nos pone el indice de la muestra 
                med = listaMediciones.get(i);
                fila.add(med.getValorHC());
                fila.add(String.valueOf(med.getValCO()).concat("%"));
                fila.add(String.valueOf(med.getValCO2()).concat("%"));
                fila.add(String.valueOf(med.getValorO2()).concat("%"));
                fila.add(med.getValorRPM());
                fila.add(String.valueOf(med.getValorTAceiteSinTabla()).concat("Cº"));
                if (med.isLect5Seg() == true) {
                    fila.add("SI");
                    promedioHC += med.getValorHC();
                    promedioCO += (med.getValCO());
                    promedioCO2 += (med.getValCO2());
                    promedioO2 += (med.getValorO2());
                    promedioRPM += (med.getValorRPM());
                    promedioTemp += (med.getValorTAceiteSinTabla());
                    y = y + 1;
                } else {
                    fila.add("NO");
                }
                vectorFilas.add(fila);
            }//end for
            Vector pr = new Vector();

            promedioHC /= y;
            promedioCO /= y;
            promedioCO2 /= y;
            promedioO2 /= y;
            promedioRPM /= y;
            promedioTemp /= y;

            pr.add("Promedio");
            pr.add(df.format(AproximacionMedidas.AproximarMedidas((double) promedioHC)));
            pr.add(df.format(AproximacionMedidas.AproximarMedidas((double) promedioCO)));
            pr.add(df.format(AproximacionMedidas.AproximarMedidas((double) promedioCO2)));
            pr.add(df.format(AproximacionMedidas.AproximarMedidas((double) promedioO2)));
            pr.add(df.format(AproximacionMedidas.AproximarMedidas((double) promedioRPM)));
            pr.add(df.format(AproximacionMedidas.AproximarMedidas((double) promedioTemp)));
            //calcular el ruido

            double sumatoriaHC = 0;
            double sumatoriaCO = 0;
            double sumatoriaCO2 = 0;
            double sumatoriaO2 = 0;
            double sumatoriaTemp = 0;
            double sumatoriaRPM = 0;

            for (MedicionGases mr : listaMediciones) {
                sumatoriaHC += Math.pow((mr.getValorHC() - promedioHC), 2);
                sumatoriaCO += Math.pow((mr.getValorCO() - promedioCO), 2);
                sumatoriaCO2 += Math.pow(mr.getValorCO2() - promedioCO2, 2);
                sumatoriaRPM += Math.pow(mr.getValorRPM() - promedioRPM, 2);
                sumatoriaTemp += Math.pow(mr.getValorTAceite() - promedioTemp, 2);
            }
            /* Vector mt = new Vector();
            mt.add("Cuad dif");
            mt.add(df.format(sumatoriaHC));
            mt.add(df.format(sumatoriaCO));
            mt.add(df.format(sumatoriaCO2));
            mt.add(df.format(sumatoriaO2));
            mt.add(df.format(sumatoriaRPM));
            mt.add(df.format(sumatoriaTemp));

            double ruidoHC = Math.sqrt(sumatoriaHC / tamañoLista);
            double ruidoCO = Math.sqrt(sumatoriaCO / tamañoLista);
            double ruidoCO2 = Math.sqrt(sumatoriaCO2 / tamañoLista);
            double ruidoO2 = Math.sqrt(sumatoriaO2 / tamañoLista);
            double ruidoRPM = Math.sqrt(sumatoriaRPM / tamañoLista);
            double ruidoTEMP = Math.sqrt(sumatoriaTemp / tamañoLista);

            Vector fr = new Vector();
            fr.add("Resultado");
            fr.add(df.format(ruidoHC));
            fr.add(df.format(ruidoCO));
            fr.add(df.format(ruidoCO2));
            fr.add(df.format(ruidoO2));
            fr.add(df.format(ruidoRPM));
            fr.add(df.format(ruidoTEMP));
             vectorFilas.add(fr);
            vectorFilas.add(mt);*/
            vectorFilas.add(pr);

            tabla = new JTable(vectorFilas, nombresVector);
            tabla.setPreferredScrollableViewportSize(new Dimension(620, 650));
            JScrollPane scroll = new JScrollPane(tabla);
            panelGraficas.add(scroll, BorderLayout.EAST);
            panelGraficas.add(buttonGenerarArchivo, BorderLayout.SOUTH);
//          panelGraficas.add(generarGrafica.getGraficaHC());
//         panelGraficas.add(generarGrafica.getGraficaCO2());
//          panelGraficas.add(generarGrafica.getGraficaCO());
//          panelGraficas.add(generarGrafica.getGraficaO2());
            panelProgreso.removeAll();
            panelProgreso.setLayout(new BorderLayout());
            panelProgreso.add(new JScrollPane(panelGraficas));
            panelProgreso.revalidate();
            panelProgreso.repaint();
            //banco.mapaFisicoES(mapa, true);//devolver el banco a su estado anterior
            //apagar el solenoide uno y el solenoide de calibracion dos
            worker.setInterrumpido(false);
            //ExportarDatos();
        } catch (IOException ex) {
            Logger.getLogger(ProcesoRuido.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void iniciarProceso() {
        Locale loc = new Locale("CO");
        NumberFormat nf = NumberFormat.getNumberInstance(loc);
        DecimalFormat df = (DecimalFormat) nf;
        df.applyPattern("#.###");
        worker.setInterrumpido(true);
        MedicionGases medicion;
        panelProgreso.getLabelMensaje().setText("Recoleccion de Toma de  Datos en  \t" + TIEMPO + " Seg..!");

        //Obtiene y guarda el Estado del mapa fisico, es decir lo lee
        short mapa = banco.mapaFisicoES((short) 128, false);

        panelProgreso.getBarraTiempo().setMaximum(TIEMPO);
        timer = new Timer(1000, (ActionListener) this);
        int contadorAnterior = contadorTimer;
        int totMuestra = TIEMPO * 2;
        //encender los solenoides apropiados????
        //desglosar esta informacion.
        //banco.mapaFisicoES((short) 6, true);//soleniode uno y solenoide de calibracion 2

        //banco.encenderCalSol2(false);//la pregunta es si en el banco capelec la secuencia seria la misma?
        //banco.encenderSolenoideUno(false);//el solenoide uno hace que ingrese la muestra hacia el banco pero por donde
        timer.start();
        int tam = 0;
        while (contadorTimer < TIEMPO) {
//            try {
            panelProgreso.getLabelTiempo().setText("Contando.. " + contadorTimer + "Seg.");
            panelProgreso.getBarraTiempo().setValue(contadorTimer);
            System.out.println("-----------------------------------------------------------------------");
//                Thread.sleep(276); fuiste 
            contadorAnterior = contadorTimer;
            medicion = banco.obtenerDatos();
            if (listaMediciones.size() < totMuestra) {
                listaMediciones.add(medicion);
                System.out.println("medicion------------------------------------------------------------");
                System.out.println("-------------"+medicion);
            } else {
                listaMedicionesAux.add(medicion);
                System.out.println("medicionaux------------------------------------------------------------");
                System.out.println("-------------"+medicion);
            }
//            } catch (InterruptedException ie) {
//                ie.printStackTrace();
//            }
        }//end while
        timer.stop();
        
        System.out.println("tamaño lista medicion: " + listaMediciones.size());
        if (listaMedicionesAux.size() > 0) {
            int e = listaMedicionesAux.size();
            System.out.println("------------aux---------------------"+e);
            for (int i = 0; i < e; i++) {
                listaMediciones.remove(0);
            }
            for (int i = 0; i < e; i++) {
                listaMediciones.add(listaMedicionesAux.get(i));
            }

        }
        
        
        
        System.out.println("tamaño lista medicion: " + listaMediciones.size());
        JPanel panelGraficas = new JPanel(new BorderLayout());
        GenerarGrafica generarGrafica = new GenerarGrafica(listaMediciones);
        panelGraficas.add(generarGrafica.getGraficaTodas(), BorderLayout.WEST);
        //llenar la lista un JTable
        //mostrar el calculo
        //toca armar un arreglo de vectores
        Vector nombresVector = new Vector();
        nombresVector.add("NumeroMuestras");
        nombresVector.add("HC");
        nombresVector.add("CO");
        nombresVector.add("CO2");
        nombresVector.add("O2");
        int tamañoLista = listaMediciones.size();
        //sacar los promedios

        Vector vectorFilas = new Vector();
        MedicionGases med;
        for (int i = 0; i < tamañoLista; i++) {
            Vector fila = new Vector();
            fila.add(i);
            med = listaMediciones.get(i);
            promedioHC += med.getValorHC();
            fila.add(med.getValorHC());
            promedioCO += (med.getValorCO() * 0.01);
            fila.add(df.format(med.getValorCO() * 0.01).concat("%"));
            promedioCO2 += (med.getValorCO2() * 0.1);
            fila.add(df.format(med.getValorCO2() * 0.1).concat("%"));
            promedioO2 += (med.getValorO2() * 0.01);
            fila.add(df.format(med.getValorO2() * 0.01).concat("%"));
            fila.add(String.valueOf(med.isLect5Seg()));
            vectorFilas.add(fila);
        }//end for
        Vector pr = new Vector();

        promedioHC /= tamañoLista;
        promedioCO /= tamañoLista;
        promedioCO2 /= tamañoLista;
        promedioO2 /= tamañoLista;

        pr.add("Promedios");
        pr.add(df.format(promedioHC));
        pr.add(df.format(promedioCO));
        pr.add(df.format(promedioCO2));
        pr.add(df.format(promedioO2));
        //calcular el ruido

        double sumatoriaHC = 0;
        double sumatoriaCO = 0;
        double sumatoriaCO2 = 0;
        double sumatoriaO2 = 0;
        for (MedicionGases mr : listaMediciones) {
            sumatoriaHC += Math.pow((mr.getValorHC() - promedioHC), 2);
            sumatoriaCO += Math.pow((mr.getValorCO() * 0.01 - promedioCO), 2);
            sumatoriaCO2 += Math.pow(mr.getValorCO2() * 0.1 - promedioCO2, 2);
            sumatoriaO2 += Math.pow(mr.getValorO2() * 0.01 - promedioO2, 2);
        }
        /*Vector mt = new Vector();
        mt.add("Cuad dif");
        mt.add(df.format(sumatoriaHC));
        mt.add(df.format(sumatoriaCO));
        mt.add(df.format(sumatoriaCO2));
        mt.add(df.format(sumatoriaO2));*/

 /* double ruidoHC = Math.sqrt(sumatoriaHC / tamañoLista);
        double ruidoCO = Math.sqrt(sumatoriaCO / tamañoLista);
        double ruidoCO2 = Math.sqrt(sumatoriaCO2 / tamañoLista);
        double ruidoO2 = Math.sqrt(sumatoriaO2 / tamañoLista);
        Vector fr = new Vector();
        fr.add("Resultado");
        fr.add(df.format(ruidoHC));
        fr.add(df.format(ruidoCO));
        fr.add(df.format(ruidoCO2));
        fr.add(df.format(ruidoO2));*/
        // vectorFilas.add(mt);
        vectorFilas.add(pr);
        // vectorFilas.add(fr);
        tabla = new JTable(vectorFilas, nombresVector);
        tabla.setPreferredScrollableViewportSize(new Dimension(300, 650));
        JScrollPane scroll = new JScrollPane(tabla);
        panelGraficas.add(scroll, BorderLayout.EAST);
        panelGraficas.add(buttonGenerarArchivo, BorderLayout.SOUTH);
//          panelGraficas.add(generarGrafica.getGraficaHC());
//          panelGraficas.add(generarGrafica.getGraficaCO2());
//          panelGraficas.add(generarGrafica.getGraficaCO());
//          panelGraficas.add(generarGrafica.getGraficaO2());
        panelProgreso.removeAll();
        panelProgreso.setLayout(new BorderLayout());
        panelProgreso.add(new JScrollPane(panelGraficas));
        panelProgreso.revalidate();
        panelProgreso.repaint();
        //banco.mapaFisicoES(mapa, true);//devolver el banco a su estado anterior
        //apagar el solenoide uno y el solenoide de calibracion dos
        banco.encenderCalSol2(false);//Este proceso puede ser distinto en el banco capelec
        banco.encenderSolenoideUno(false);//Este proceso puede ser distinto en el banco capelec
        worker.setInterrumpido(false);
    }//end of method proceso

    public List<MedicionGases> getListaMediciones() {
        return listaMediciones;
    }

    public void actionPerformed(ActionEvent e) {//timer se cumple, incrementar el contador
        contadorTimer++;
    }//

    private File getFile() {
        // display file dialog, so user can choose file to open
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(
                JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setSelectedFile(new File("medidasGases_Placa_" + placa + ".txt"));

        int result = fileChooser.showSaveDialog(null);
        // if user clicked Cancel button on dialog, return
        if (result == JFileChooser.CANCEL_OPTION) {
            JOptionPane.showMessageDialog(null, "Operacion Cancelada por usuario");
        }

        File fileName = fileChooser.getSelectedFile(); // get selected file

        // display error if invalid
        if ((fileName == null) || (fileName.getName().equals(""))) {
            JOptionPane.showMessageDialog(null, "Nombre de Archivo Invalido",
                    "Invalid File Name", JOptionPane.ERROR_MESSAGE);
        } // end if

        return fileName;
    } // end method getFile

    public static void main(String args[]) {
        /* PanelProgreso progreso = new PanelProgreso();
        ProcesoFugas proceso = new ProcesoFugas(progreso);
        JFrame app = new JFrame();
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        app.setSize(650, 650);
        app.add(progreso);
        (new Thread(proceso)).start();
        app.setVisible(true);*/
        ProcesoRuido pr = new ProcesoRuido();
        pr.lodUltLectGases();

    }

    class ManejadorGenerarArchivo implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            File file = getFile();
            try {
                PrintWriter pw = new PrintWriter(file);
                int i = 0;
                int muestra = listaMediciones.size();
                double seg = ((double) TIEMPO) / muestra;
                double time = seg;
                System.out.println("TIEMPO: " + TIEMPO);
                System.out.println("Total muestra: " + muestra);
                System.out.println("Tiempo x muestra: " + seg);
                Date fec = new Date();
                Calendar cal = Calendar.getInstance();
                cal.setTime(fec);
                String fecha = String.valueOf(cal.get(Calendar.YEAR)).concat("-");
                String mes;
                if (cal.get(Calendar.MONTH) < 9) {
                    mes = String.valueOf(cal.get(Calendar.MONTH) + 1).concat("-");
                } else {
                    mes = "0".concat(String.valueOf(cal.get(Calendar.MONTH) + 1).concat("-"));
                }
                String dia = String.valueOf(cal.get(Calendar.DATE));
                String hora = " Hora: ".concat(String.valueOf(cal.get(Calendar.HOUR_OF_DAY)).concat(":").concat(String.valueOf(cal.get(Calendar.MINUTE))));
                String pef = banco.obtenerPEF();
                if (pef.endsWith(".0")) {
                    pef = pef.substring(0, pef.length() - 2);
                }


                pw.printf("%s\t\n", "Serial del Banco:".concat(ProcesoRuido.serialResolucion).trim());
                pw.printf("%s\t\n", " PEF Banco: 0.".concat(pef).trim());
                pw.printf("%s%s%s%s\t\n", "Fecha Generacion: ".concat(fecha.trim()), mes.trim(), dia, hora);

                pw.printf("%s\t%s\t%s\t%s\t%s\t%s\t%s\t\n", "NumMuestra", "HC ppm", "CO %", "CO2 %", "O2 %", "Time(seg)", "Ult. 5 Seg.");
                for (MedicionGases m : listaMediciones) {
                    pw.printf("%d\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%s\t\n", i, (float) m.getValorHC(), m.getValorCO() * 0.01, m.getValorCO2() * 0.1, m.getValorO2() * 0.01, (float) time, m.isLect5Seg());
                    i++;
                    time += seg;
                }
                pw.flush();
            } catch (FileNotFoundException ex) {
                System.out.println("Este archivo no existe");
                ex.printStackTrace(System.err);
            }

        }//end of actionPerformed

    }

    class exporUltLectGases implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            File file = getFile();
            try {
                PrintWriter pw = new PrintWriter(file);
                int i = 0;
                int cutLst = 0;
                int cut = 1;
                int muestra = listaMediciones.size();
                double seg = ((double) TIEMPO) / muestra;
                // double time = seg;
                System.out.println("TIEMPO: " + TIEMPO);
                System.out.println("Total muestra: " + muestra);
                System.out.println("Tiempo x muestra: " + seg);

                pw.printf("%s\n", "-.*** Lectura de Valores de los Ultimos 5 Segundos .-*** ");

                pw.printf("%s\n", "-.** Prueba Aplicada a la Placa: " + ProcesoRuido.placa.toUpperCase().concat(" Fecha de la prueba: ").concat(ProcesoRuido.fecha).concat("-- .-**"));
                if (ProcesoRuido.tipos.equalsIgnoreCase("Motos")) {
                    cutLst = 60;
                    pw.printf("%s\n", "-.* Tabla de Valores Moto en RALENTI  EXHOSTO 1.-* ");
                } else {
                    cutLst = 60;
                    pw.printf("%s\n", "-.* Tabla de Valores Vehiculo en Crucero  EXHOSTO 1.-* ");
                }
                pw.printf("%s\n", "\t HC \t CO \t CO2 \t O2 \t RPM \t TEMP\t SEGUNDO\t Ult. Lect \t");
                String rpm;
                String temp, HC, CO, CO2, O2, ultlect, segundos;
                for (MedicionGases m : listaMediciones) {

                    rpm = String.valueOf(m.getValorRPM());
                    temp = String.valueOf(m.getValorTAceiteSinTabla());
                    O2 = String.valueOf(m.getValorO2());
                    CO2 = String.valueOf(m.getValCO2());
                    CO = String.valueOf(m.getValCO());
                    HC = String.valueOf(m.getValorHC());
                    segundos = String.valueOf(i / 2);
                    ultlect = String.valueOf(m.isLect5Seg());
                    if (m.isLect5Seg() == true) {
                        ultlect = "SI";
                    } else {
                        ultlect = "NO";
                    }
                    pw.printf("%d\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\n", i + 1, HC, CO, CO2, O2, rpm, temp, segundos, ultlect);
                    if (cut == cutLst && listaMediciones.size() > cutLst) {
                        if (ProcesoRuido.tipos.equalsIgnoreCase("Motos")) {
                            pw.printf("%s\n", "-.** Tabla de Valores Moto en RALENTI  EXHOSTO 2.-** ");
                        } else {
                            pw.printf("%s\n", "-.** Tabla de Valores Vehiculo en RALENTI  EXHOSTO 1.-** ");
                        }
                    }
                    cut++;
                    i++;
                    //time += seg;
                }
                pw.flush();
            } catch (FileNotFoundException ex) {
                System.out.println("Este archivo no existe");
                ex.printStackTrace(System.err);
            }

        }//end of actionPerformed

    }

    public int getTIEMPO() {
        return TIEMPO;
    }

    public void setTIEMPO(int TIEMPO) {
        this.TIEMPO = TIEMPO;
    }

}
