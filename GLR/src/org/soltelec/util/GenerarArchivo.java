/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.soltelec.procesosbanco.BancoGasolina;
import org.soltelec.util.MedicionGases;
import org.soltelec.util.capelec.BancoCapelec;

import java.io.BufferedWriter;
import java.io.FileWriter;
import static java.lang.System.out;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase para generar un archivo con las mediciones de gases
 *
 * @author GerenciaDesarrollo
 */
public class GenerarArchivo {

    public static int temperatura;

    public static void generarArchivo(List<List<MedicionGases>> listaDeLista) {
        File file = new File("BUFFER.txt");
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException ex) {
            System.out.println("Error creando el archivo");
            ex.printStackTrace(System.err);
        }
        PrintWriter output = null;
        String titulo = "        HC\tCO\tCO2\tO2\tRPM\tTEMP\t";
        String formato = "%10d\t%10s\t%10s%10s%10d%10d\n";
        DecimalFormat df = new DecimalFormat("#0.0#");
        try {
            //Generar el archivo txt de la entrada, la entrada corregida y la salida
            //filtrada
            output = new PrintWriter(file);
            output.println(titulo);
            for (List<MedicionGases> lista : listaDeLista) {
                for (MedicionGases med : lista) {
                    int medHC14 = med.getValorHC();
                    int medHC13 = med.getValorHC();
                    int medHC12 = med.getValorHC();
                    int medHC11 = med.getValorHC();
                    int medHC10 = med.getValorHC();
                    int medHC9 = med.getValorHC();
                    int medHC8 = med.getValorHC();
                    int medHC7 = med.getValorHC();
                    int medHC6 = med.getValorHC();
                    int medHC5 = med.getValorHC();
                    int medHC4 = med.getValorHC();
                    int medHC3 = med.getValorHC();
                    int medHC2 = med.getValorHC();
                    int medHC = med.getValorHC();
                    double medCO11 = med.getValorCO() * 0.01;
                    String strMedCO11 = df.format(medCO11);
                    double medCO211 = med.getValorCO2() * 0.1;
                    String strMedCO211 = df.format(medCO211);
                    double medO211 = med.getValorO2() * 0.01;
                    String strMedO211 = df.format(medO211);
                    int medRPM11 = med.getValorRPM();
                    int medTemp11 = med.getValorTAceite();
                    double medCO = med.getValorCO() * 0.01;
                    String strMedCO = df.format(medCO);
                    double medCO2 = med.getValorCO2() * 0.1;
                    String strMedCO2 = df.format(medCO2);
                    double medO2 = med.getValorO2() * 0.01;
                    String strMedO2 = df.format(medO2);
                    int medRPM = med.getValorRPM();
                    int medTemp = med.getValorTAceite();
                    if (medTemp == 0 || medTemp > 110) {
                        medTemp = med.getValorTAceiteSinTabla();
                    }
                    output.printf(formato, medHC, strMedCO, strMedCO2, strMedO2, medRPM, medTemp, medHC2, medHC3, medHC4, medHC5, medHC6, medHC7, medHC8, medHC9, medHC10, medHC11, medHC12, medHC13, medHC14);
                    output.println();

                }
            }

        } catch (FileNotFoundException ex) {
            ex.printStackTrace(System.err);
        } catch (Exception exc) {

            exc.printStackTrace();
        } finally {
            output.close();
        }

    }

    public static List<List<MedicionGases>> generarArchivoGasesLiviano(List<List<MedicionGases>> listaDeLista, String placas, String tVeh, BancoGasolina banco) {
        int medTemp = 0;
        FileWriter fw = null/*,fw2 = null*/;
        try {
            System.out.println("entro a generar archivo");
            fw = new FileWriter("ultLectGases.soltelec");
            //fw2 = new FileWriter(placas +".soltelec");

            String titulo = "HC\tCO\tCO2\tO2\tRPM\tTEMP\t";
            String formato = "%10d;%10s;%10s;%10s;%10d;%10d\n";
            DecimalFormat df = new DecimalFormat("#0.00#");
            String out = ""/*, out2=""*/;
            //Generar el archivo txt de la entrada, la entrada corregida y la salida
            //filtrada
            BufferedWriter bwP = new BufferedWriter(fw);
            //BufferedWriter bwP2 = new BufferedWriter(fw2);
            int cnt = 1;
            for (List<MedicionGases> lista : listaDeLista) {
                if (tVeh.equalsIgnoreCase("C")) {
                    if (cnt == 1) {
                        out = Utilidades.cifra("Liviano;".concat(placas), 'M');
                        //out2 = "Liviano;".concat(placas);
                        bwP.write(out);
                        //bwP2.write(out2);
                        bwP.newLine();
                        //          bwP2.newLine();
                        bwP.flush();
                        //          bwP2.flush();
                    }
                } else {
                    out = Utilidades.cifra("Liviano;" + placas, 'M');
                    out = "Liviano;" + placas;
                    bwP.write(out);
                    //bwP2.write(out2);
                    bwP.newLine();
                    //       bwP2.newLine();
                    bwP.flush();
                    //       bwP2.flush();
                }
                int contLectSeg = 0;
                boolean encontrado = false;
                int pos = 0;
                int posEnc = 0;
                int limitNoUlt = 0;
                for (MedicionGases med : lista) {
                    pos++;
                    if (med.isLect5Seg() == true) {
                        if (encontrado == false) {
                            encontrado = true;
                            posEnc = pos - 1;
                            limitNoUlt = pos - 2;
                        }
                        contLectSeg++;
                    }
                }

                if (contLectSeg > 10) {
                    int cont = contLectSeg - 10;
                    for (int i = 0; i < cont; i++) {
                        lista.remove(posEnc); // se remueven los de inico de lectura DE LOS 5 SEGUNDOS                        
                    }
                }

                if (contLectSeg < 10) {
                    int cont = 10 - contLectSeg;
                    posEnc = posEnc - cont;
                    limitNoUlt = posEnc;
                    MedicionGases med;
                    for (int i = 0; i < cont; i++) {
                        med = lista.get(posEnc);
                        med.setLect5Seg(true);
                        posEnc++;
                    }
                }
                if (lista.size() > 60) {

                    int lngLst = lista.size() - 60;
                    for (int i = 0; i < lngLst; i++) {
                        if (i % 2 == 0) {
                            lista.remove(limitNoUlt);
                        } else {
                            lista.remove(0);
                        }
                        limitNoUlt--;
                        if (lista.size() == 60) {
                            break;
                        }
                    }
                }
                if (lista.size() < 60) {

                    int tot = limitNoUlt;
                    int posIni = tot - 1;
                    int posFin = tot;
                    int apFill = 0;
                    while (true) {
                        apFill = ThreadLocalRandom.current().nextInt(posIni, posFin);
                        MedicionGases med = lista.get(apFill);
                        lista.add(limitNoUlt, med);
                        limitNoUlt++;
                        if (lista.size() == 60) {
                            break;
                        }
                    }

                }
                int tot = lista.size();

                for (MedicionGases med : lista) {
                    int medHC = med.getValorHC();
                    double medCO = med.getValorCO() * 0.01;
                    String strMedCO = String.valueOf(df.format(medCO));
                    double medCO2 = 0;
                    if (banco instanceof BancoCapelec) {
                        medCO2 = (med.getValorCO2() * 0.1) / 10;
                    } else {
                        medCO2 = med.getValorCO2() * 0.1;
                    }
                    String strMedCO2 = String.valueOf(df.format(medCO2));
                    double medO2 = med.getValorO2() * 0.01;
                    String strMedO2 = String.valueOf(df.format(medO2));
                    int medRPM = med.getValorRPM();
                    medTemp = med.getValorTAceiteSinTabla();

                    if (medTemp == 0 || medTemp > 110) {
                        medTemp = med.getValorTAceiteSinTabla();
                    }
                    out = Utilidades.cifra(medHC + ";" + strMedCO + ";" + strMedCO2 + ";" + strMedO2 + ";" + medRPM + ";" + medTemp + ";" + med.isLect5Seg(), 'M');
                    //      out2 = medHC + ";" + strMedCO + ";" + strMedCO2 + ";" + strMedO2 + ";" + medRPM + ";" + medTemp + ";" + med.isLect5Seg();
                    bwP.write(out);
                    //    bwP2.write(out2);
                    bwP.newLine();
                    //      bwP2.newLine();
                    bwP.flush();
                    //      bwP2.flush();
                }

                out = Utilidades.cifra("*****", 'M');
                //out2 = "*****";
                bwP.write(out);
                //bwP2.write(out2);
                bwP.newLine();
                //bwP2.newLine();
                bwP.flush();
                //bwP2.flush();
                cnt++;
            }
            out = Utilidades.cifra("####", 'M');
            //out2 = "####";
            bwP.write(out);
            //bwP2.write(out2);
            bwP.newLine();
            //bwP2.newLine();
            bwP.flush();
            //bwP2.flush();            
            GenerarArchivo.temperatura = medTemp;
            fw.close();
            // fw2.close();
            bwP.close();
            // bwP2.close();
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        return listaDeLista;
    }

    public static List<List<MedicionGases>> generarArchivoGases(List<List<MedicionGases>> listaDeLista, String placas, String tVeh, BancoGasolina banco) {
        int medTemp = 0;
        FileWriter fw = null/*,fw2 = null*/;
        List<List<MedicionGases>> newListaDeLista = new ArrayList<>();
        List<MedicionGases> newLista;
        try {
            fw = new FileWriter("ultLectGases.soltelec");
            // fw2 = new FileWriter(placas+".soltelec");
            String titulo = "HC\tCO\tCO2\tO2\tRPM\tTEMP\t";
            String formato = "%10d;%10s;%10s;%10s;%10d;%10d\n";
            DecimalFormat df = new DecimalFormat("#0.00#");
            String out = "", out2 = "";
            //Generar el archivo txt de la entrada, la entrada corregida y la salida
            //filtrada
            BufferedWriter bwP = new BufferedWriter(fw);
            //BufferedWriter bwP2 = new BufferedWriter(fw2);
            int cnt = 1;
            for (List<MedicionGases> lista : listaDeLista) {
                newLista = new ArrayList<>();
                if (tVeh.equalsIgnoreCase("C")) {
                    if (cnt == 1) {
                        out = Utilidades.cifra("Motos;".concat(placas), 'M');
                        bwP.write(out);
                        bwP.newLine();
                    }
                } else {
                    out = Utilidades.cifra("Moto;" + placas, 'M');
                    bwP.write(out);
                    bwP.newLine();
                }
                boolean encontrado = false;
                int pos = -1;
                int posEnc = 0;;
                int limitNoUlt = 0;
                for (MedicionGases med : lista) {
                    pos++;
                    if (med.isLect5Seg() == true) {
                        if (encontrado == false) {
                            encontrado = true;
                            posEnc = pos;
                            limitNoUlt = pos - 1;
                        }
                    }
                }

                if (lista.size() > 60) {
                    int lngLst = lista.size() - 60;
                    for (int i = 0; i < lngLst; i++) {
                        if (i % 2 == 0) {
                            lista.remove(limitNoUlt - 1);
                        } else {
                            lista.remove(0);
                        }
                        limitNoUlt--;
                        if (lista.size() == 60) {
                            break;
                        }
                    }
                }

//                if (lista.size() < 60) {
//                    int tot = limitNoUlt - 2;
//                    int posIni = tot - 1;
//                    int posFin = tot;
//                    int apFill = 0;
//                    while (true) {
//                        apFill = ThreadLocalRandom.current().nextInt(posIni, posFin);
//                        MedicionGases med = lista.get(apFill);
//                        lista.add(limitNoUlt, med);
//                        limitNoUlt++;
//                        if (lista.size() == 60) {
//                            break;
//                        }
//                    }
//                }
//                if (lista.size() < 60) {
//                    int tot = limitNoUlt - 2;
//                    int posIni = tot - 1;
//                    int posFin = tot;
//                    while (lista.size() < 60) {
//                        // Asegúrate de no exceder los límites de la lista original
//                        if (posIni >= 0 && posIni < lista.size()) {
//                            MedicionGases med = lista.get(posIni);
//                            lista.add(limitNoUlt, med);
//                            limitNoUlt++;
//                        } else if (posFin >= 0 && posFin < lista.size()) {
//                            MedicionGases med = lista.get(posFin);
//                            lista.add(limitNoUlt, med);
//                            limitNoUlt++;
//                        }
//                        // Actualiza las posiciones para el siguiente ciclo
//                        posIni--;
//                        posFin++;
//                    }
//                }
                int tot = lista.size();
                int eve = 0;
                newListaDeLista.add(newLista);
                for (MedicionGases med : lista) {
                    int medHC = med.getValorHC();
                    double medCO = med.getValorCO() * 0.01;
                    String strMedCO = String.valueOf(df.format(medCO));
                    double medCO2 = 0;
                    if (banco instanceof BancoCapelec) {
                        medCO2 = (med.getValorCO2() * 0.1) / 10;
                    } else {
                        medCO2 = med.getValorCO2() * 0.1;
                    }
                    String strMedCO2 = String.valueOf(df.format(medCO2));
                    double medO2 = med.getValorO2() * 0.01;
                    String strMedO2 = String.valueOf(df.format(medO2));
                    int medRPM = med.getValorRPM();
                    medTemp = med.getValorTAceiteSinTabla();
                    if (eve <= 49) {
                        med.setLect5Seg(false);
                    }
                    if (eve >= 50) {
                        med.setLect5Seg(true);
                    }
                    newLista.add(med);
                    out = Utilidades.cifra(medHC + ";" + strMedCO + ";" + strMedCO2 + ";" + strMedO2 + ";" + medRPM + ";" + medTemp + ";" + med.isLect5Seg(), 'M');
                    if (medTemp == 0 || medTemp > 110) {
                        medTemp = med.getValorTAceiteSinTabla();
                    }
                    bwP.write(out);
                    bwP.newLine();
                    bwP.flush();
                    eve++;
                }
                out = Utilidades.cifra("*****", 'M');
                bwP.write(out);
                bwP.newLine();
                bwP.flush();
                cnt++;
            }
            out = Utilidades.cifra("####", 'M');
            bwP.write(out);
            bwP.newLine();
            bwP.flush();
            GenerarArchivo.temperatura = medTemp;
            fw.close();
            bwP.close();
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        return newListaDeLista;
    }

}
