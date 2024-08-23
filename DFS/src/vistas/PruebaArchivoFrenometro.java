/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vistas;

/**
 *
 * @author Dany
 */
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Locale;
import javax.swing.JOptionPane;

public class PruebaArchivoFrenometro
{
  String[] cabeceraPeso = { "[PesaVeicolo]", "Marca=VAMAG", "Tipo=InspectorM 2PLD/M", "NumOmologa=*", "NumSerie=0123456789", "NumVersioneSoftware=Version 3.5.4", "DataScadenza=13082009", "PesoStatico=995", "CodErrore=", "DataMisura=16012011", "InizioMisura=101100", "FineMisura=101200", "Operatore=OPERADOR 1", "[PesoAsse_1]", "PesoStaticoSx=0" };
  String[] colaEje1 = { "PesoDinamicoSx=0", "PesoDinamicoDx=981", "PesoDinamicoAsse=981", "[PesoAsse_2]", "PesoStaticoSx=0" };
  String[] colaEje2 = { "PesoDinamicoSx=0", "PesoDinamicoDx=935", "PesoDinamicoAsse=935" };
  String[] cabeceraFreno = { "[ProvaFreni]", "Marca=VAMAG", "Tipo=InspectorM 2PLD/M", "NumOmologa=*", "NumSerie=0123456789", "NumVersioneSoftware=Version 3.5.4", "DataScadenza=13082009", "ImpFrenanteServ=IDRAULICO", "ImpFrenanteStaz=IDRAULICO", "Soglia%EffServizio=40", "Soglia%EffFrenoStaz=0", "Soglia%DissServizio=0", "SogliaForzaServizio=350", "SogliaForzaLevaServizio=400", "SogliaForzaStaz=200" };
  String[] cabeceraFreno2 = { "EffFrenoStazionamento=00", "EsitoEffFrenoStazionamento=E", "PressAtmosferica=#000.0", "TempAmbiente=#00", "UmiditaRelativa=#000", "CodErrore=", "DataMisura=16012011", "InizioMisura=101100", "FineMisura=101200", "Operatore=OPERADOR 1", "[DettagliFreniAsse_1]", "ForzaParrRuotaSingola=", "ForzaFrenanteRuotaSingola=", "Irreg%MaxRuotaSingola=", "ForzaParrSx=0", "ForzaParrDx=0", "ForzaFrenanteSx=0" };
  String[] colaEje1Freno = { "SforzoPedale=0", "SforzoLevaSx=0", "SforzoLevaDx=0", "Dissimetria%Dinamica=", "EsitoDissimetria%Dinamica=", "Irreg%MaxSx=0", "Irreg%MaxDx=0", "[DettagliFreniAsse_2]", "ForzaParrRuotaSingola=", "ForzaFrenanteRuotaSingola=", "Irreg%MaxRuotaSingola=", "ForzaParrSx=0", "ForzaParrDx=0", "ForzaFrenanteSx=0" };
  String[] colaEje2Freno = { "SforzoPedale=0", "SforzoLevaSx=0", "SforzoLevaDx=0", "Dissimetria%Dinamica=", "EsitoDissimetria%Dinamica=", "Irreg%MaxSx=0", "Irreg%MaxDx=0", "[DettagliFrenoStazionamentoAsse_1]", "ForzaFrenanteSx=0", "ForzaFrenanteDx=0", "SforzoStaz=", "SforzoPedale=0", "[DettagliFrenoStazionamentoAsse_2]", "ForzaFrenanteSx=0", "ForzaFrenanteDx=0", "SforzoStaz=", "SforzoPedale=0" };
  DecimalFormat df2 = (DecimalFormat)DecimalFormat.getInstance(Locale.ENGLISH);
  
  public void crearArchivoPeso(String nombreArchivo, int pesoEje1, int pesoEje2)
  {
    this.df2.applyPattern("#.##");
    this.df2.setMinimumFractionDigits(3);
    File file = new File("\\\\LucesGases\\MCTC\\DIR_PES\\" + nombreArchivo + ".PES");
    System.out.println("Ruta: C:\\MCTC\\DIR_PES\\" + nombreArchivo + ".PES");
    
    PrintWriter pw = null;
    try
    {
      file.createNewFile();
      pw = new PrintWriter(file);
      for (String str : this.cabeceraPeso) {
        pw.println(str);
      }
      pw.println("PesoStaticoDx=" + pesoEje1);
      pw.println("PesoStaticoAsse=" + pesoEje1);
      for (String str : this.colaEje1) {
        pw.println(str);
      }
      pw.println("PesoStaticoDx=" + pesoEje2);
      pw.println("PesoStaticoAsse=" + pesoEje2);
      for (String str : this.colaEje2) {
        pw.println(str);
      }
    }
    catch (IOException ioexc)
    {
      JOptionPane.showMessageDialog(null, "Error escribiendo el archivo");
      System.out.println("Error escribiendo el archivo");
      ioexc.printStackTrace();
    }
    finally
    {
      pw.flush();
      pw.close();
    }
  }
  
  public void crearArchivoFreno(String nombreArchivo, int fuerzaEje1, int fuerzaEje2, int eficacia, boolean isAprobado)
  {
    this.df2.applyPattern("#.##");
    this.df2.setMinimumFractionDigits(3);
    File file = new File("\\\\LucesGases\\MCTC\\DIR_PFR\\" + nombreArchivo + ".PFR");
    System.out.println("Ruta: C:\\MCTC\\DIR_PFR\\" + nombreArchivo + ".PFR");
    
    PrintWriter pw = null;
    try
    {
      file.createNewFile();
      pw = new PrintWriter(file);
      for (String str : this.cabeceraFreno) {
        pw.println(str);
      }
      pw.println("EffFrenoServizio=" + eficacia);
      if (isAprobado) {
        pw.println("EsitoEffFrenoServizio=R");
      } else {
        pw.println("EsitoEffFrenoServizio=I");
      }
      for (String str : this.cabeceraFreno2) {
        pw.println(str);
      }
      pw.println("ForzaFrenanteDx=" + fuerzaEje1);
      for (String str : this.colaEje1Freno) {
        pw.println(str);
      }
      pw.println("ForzaFrenanteDx=" + fuerzaEje2);
      for (String str : this.colaEje2Freno) {
        pw.println(str);
      }
    }
    catch (IOException ioexc)
    {
      JOptionPane.showMessageDialog(null, "Error escribiendo el archivo");
      System.out.println("Error escribiendo el archivo");
      ioexc.printStackTrace();
    }
    finally
    {
      pw.flush();
      pw.close();
    }
  }
  
  public static void main(String[] args)
  {
    PruebaArchivoFrenometro pa = new PruebaArchivoFrenometro();
    pa.registrarCalibracion(1200.0D, 4.0D, 6.0D, 15000.0D, 12.0D, 16.0D, true);
    pa.registrarCalibracion(1200.0D, 4.0D, 6.0D, 15000.0D, 12.0D, 16.0D, true);
  }
  
  public void registrarCalibracion(double valor1, double valor2, double valor3, double valor4, double valor5, double valor6, boolean aprobada)
  {
    FileWriter fw = null;
    try
    {
      fw = new FileWriter("Calibraciones.txt", true);
      
      StringBuilder sb = new StringBuilder();
      DateFormat df = DateFormat.getDateInstance();
      Date d = new Date();
      sb.append(df.format(d));
      sb.append("valor1\t").append(valor1);
      sb.append("valor2\t").append(valor2);
      sb.append("valor3\t").append(valor3);
      sb.append("valor4\t").append(valor4);
      sb.append("valor5\t").append(valor5);
      sb.append("valor6\t").append(valor6);
      if (aprobada) {
        sb.append("Aprobada");
      } else {
        sb.append("Reprobada");
      }
      sb.append("\r\n");
      fw.write(sb.toString());
      fw.flush();
      fw.close();
      
      System.out.println("Archivo Escrito");
    }
    catch (IOException ex)
    {
      ex = 
      
        ex;JOptionPane.showMessageDialog(null, "Error escribiendo el archivo");ex.printStackTrace();
    }
    finally {}
  }
}

