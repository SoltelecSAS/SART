/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.luxometro.gemini;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.soltelec.util.Conex;
import org.soltelec.util.DefectoGeneral;
import org.soltelec.util.MedidaGeneral;
import org.soltelec.util.TipoAutomovil;
import org.soltelec.util.TipoDefecto;
import org.soltelec.util.UtilPropiedades;

/**
 *Clase para generar el archivo .acc
 * y leer el archivo .far
 * @author Usuario
 */
public class LogicaLuxoGemini {

    //Inicio del archivo partes fijas y genericas
    private static String[] primeraParteArchivo = {"[IdentificazioneProtocollo]",
        "Versione=100",
        "Data=02111999",
        "[Prenotazione]",
        "DataAccettazione=01012000",
        "DataPrenotazione=01012000",
        "Ora=000000",
        "Operatore=Soltelec",
        "Linea=1",
        "TipoRevisione=ANNUALI"
    };

    private static String[] segundaParteArchivo = {
      "Sesso=",
      "DataNascita=01012000",
      "LuogoNascita=BOGOTA",
      "ProvinciaNascita=DC",
      "Indirizzo=CRA 1 #1-1",
      "CAP=37100",
      "Citta=BOGOTA",
      "Provincia=DC",
      "Telefono=1234567890",
      "Note=",
      "[DatiLibrettoVeicolo]",
      "TipoVeicolo=LEGGERO",
      "DescrizioneVeicolo=AUTOVETTURA"
    };

    private static String[] terceraParteArchivo = {
        "Fabbrica=OPEL",
        "Tipo=Corsa",
        "TipoMotore=ASPIRATO",
        "NumOmologazione=SDFKKLLVIOG",
        "AnnoPrimaImm=2005",
        "DataRilascio=01012005",
        "DataUltimaRev=",
        "Alimentazione_1=BENZINA CAT",
        "Alimentazione_2=NESSUNA",
        "Km=123456",
        "Tara=12345",
        "MassaComplessiva=12345",
        "MassaRimorchiabile=12345",
        "Cilindrata=12345",
        "PotMaxkW=123.12",
        "PotFiscaleCV=123",
        "Decibel=123",
        "GiriMotoredB=1234",
        "Veicolo4WD=N",
        "ImpiantoABS=N",
        "NumTotalePosti=5",
        "FrenoSoccorso=XX",
        "FrenoStazionamento=MANO",
        "NumTotaleAssi=2",
        "NumAssiStazionamento=1"
    };

     private final double permisibleLucesBajasMenor = 2.5;
    //private double permisibleLucesBajasMayor = ;
    private final double permisibleSumatoria = 225;
    private final double permisibleAnguloMenor = 0.5;
    private final double permisibleAnguloMayor = 3.5;

    /**
     * Escribe el archivo acc de la prueba de luces
     * recibe los parametros de el objeto que
     * llame al metodo
     * @param placa
     * @param VIN
     * @param nombre
     * @param apellido
     * @param idHojaPrueba
     */
    public void generarArchivoACC(Map<String,String> mapaInformacion, long idHojaPrueba) throws FileNotFoundException{
        String placa = mapaInformacion.get("placa");
        String VIN = mapaInformacion.get("VIN");
        String nombre = mapaInformacion.get("Nombres");
        String apellido = mapaInformacion.get("Apellidos");
        File file = new File("C:\\MCTC\\DIR_FAR\\"+idHojaPrueba+placa+".acc");
        PrintWriter pw = new PrintWriter(file);
        //Escribe el inicio del archivo
        for(String s: primeraParteArchivo){
            pw.println(s);
        }//end of for
        //Escribir el nombre y el apellido del propietario
        pw.println("CognomeDenominazione="+apellido.trim());
        pw.println("Nome="+nombre.trim());
        for(String s: segundaParteArchivo){
            pw.println(s);
        }
        //Escribir la placa y el VIN
        pw.println("Targa="+placa);
        pw.println("Telaio="+VIN);
        for(String s: terceraParteArchivo){
            pw.println(s);
        }
        pw.flush();
        pw.close();
    }//end of method generarArchivoACC

    /**
     * Lee el archivo de salida al cerrar el programa de luces
     * @param placa
     * @param idHojaPrueba
     */
    public MedidaLuxometro leerArchivo(String placa, long idHojaPrueba) throws FileNotFoundException{

        MedidaLuxometro medidaLuz = new MedidaLuxometro();
        File file = new File("C:\\MCTC\\DIR_FAR\\"+idHojaPrueba+placa+".far");
        Scanner scanner = new Scanner(file);
        Pattern p = Pattern.compile("(.+)=(.+)");//cualquier caracter 1 o mas veces
        String line = null;
        Matcher m = null;

        while(scanner.hasNextLine()){
           line = scanner.nextLine();
           System.out.println(line);
           m = p.matcher(line);
           if(m.find()){
                System.out.println(m.group(1) + "---" + m.group(2));
                if(m.group(1).equals("IllumLuxAnabDx")){
                    System.out.println("Intensidad Luz Baja Derecha: " + m.group(2));
                    if( !m.group(2).isEmpty() ){
                        try{
                        Double intensidadLuzBajaDerecha = Double.parseDouble(m.group(2));
                        intensidadLuzBajaDerecha = intensidadLuzBajaDerecha/1000;
                        medidaLuz.setIntensidadLuzBajaDerecha(intensidadLuzBajaDerecha);
                        } catch(NumberFormatException nfe){
                            System.out.println("No se puede tomar la medicion de intensidadLuzBajaDerecha");
                        }
                    }
                }
               else if(m.group(1).equals("IllumLuxAnabSx"))
               {
                    System.out.println("Intensidad Luz Baja Izquierda: " + m.group(2));
                    if( !m.group(1).isEmpty() ){
                        try {
                        Double intensidadLuzBajaIzquierda = Double.parseDouble(m.group(2));
                        intensidadLuzBajaIzquierda = intensidadLuzBajaIzquierda / 1000;
                        medidaLuz.setIntensidadLuzBajaIzquierda(intensidadLuzBajaIzquierda);
                        }catch(NumberFormatException nfe){
                            System.out.println("Error tomando la intensidad luz baja izquierda");
                        }
                   }
                }
               else if(m.group(1).equals("IllumLuxAbbDx"))
               {
                    System.out.println("Intensidad Luz Alta Derecha: " + m.group(2));
                    if( !m.group(2).isEmpty() ){
                      try{
                          Double intensidadLuzAltaDerecha = Double.parseDouble(m.group(2));
                          intensidadLuzAltaDerecha = intensidadLuzAltaDerecha/1000;
                          medidaLuz.setIntensidadLuzAltaDerecha(intensidadLuzAltaDerecha);
                      } catch( NumberFormatException nfe){
                          System.out.println("Error tomando la intensidad luz alta derecha");
                      }
                   }
               }
               else if (m.group(1).equals("IllumLuxAbbSx")) {
                    System.out.println("Intensidad Luz Alta Izquierda: " + m.group(2));
                    if( !m.group(2).isEmpty() ){
                        try{
                            Double intensidadLuzAltaIzquierda = Double.parseDouble(m.group(2));
                            intensidadLuzAltaIzquierda = intensidadLuzAltaIzquierda/1000;
                            medidaLuz.setIntensidadLuzAltaIzquierda(intensidadLuzAltaIzquierda);
                        } catch( NumberFormatException nfe){
                            System.out.println("Error tomando la intensidad de la luz Alta izquierda");
                        }
                    }
               }
               else if(m.group(1).equals("ValVertAnabSx")){
                    System.out.println("Angulo Inclinacion Vertical Luz Baja Izquierda: "+ m.group(2));
                    if( !m.group(2).isEmpty() ){
                        try {
                         Double inclinacionVertBajaIzquierda = Double.parseDouble(m.group(2));
                         medidaLuz.setAnguloInclinacionVertBajaIzquierda(inclinacionVertBajaIzquierda);
                        } catch (NumberFormatException e) {
                            System.out.println("No se puede leer el valor de inclinacion luz baja izquierda");
                        }
                    }
               }
               else if(m.group(1).equals("ValOrizAnabSx")){
                    System.out.println("Angulo Inclinacion Horizontal Luz Baja Izquierda: "+ m.group(2));
                    if( !m.group(2).isEmpty() ){
                        try {
                         Double inclinacionHorBajaIzquierda = Double.parseDouble(m.group(2));
                         medidaLuz.setAngInclinacionHorBajaIzquierda( inclinacionHorBajaIzquierda );
                        } catch (NumberFormatException e) {
                            System.out.println("No se puede leer el valor de inclinacion luz baja izquierda");
                        }
                    }
               }
               else if(m.group(1).equals("ValVertAnabDx")){
                    System.out.println("Angulo Inclinacion Vertical Luz Baja Derecha: "+ m.group(2));
                    if( !m.group(2).isEmpty() ){
                        try {
                         Double inclinacionVertBajaIzquierda = Double.parseDouble(m.group(2));
                         medidaLuz.setAngInclinacionHorBajaIzquierda( inclinacionVertBajaIzquierda );
                        } catch (NumberFormatException e) {
                            System.out.println("No se puede leer el valor de inclinacion luz baja izquierda");
                        }
                    }
               }
               else if(m.group(1).equals("ValOrizAnabDx")){
                    System.out.println("Angulo Inclinacion Horizontal Luz Baja Derecha: "+ m.group(2));
                    if( !m.group(2).isEmpty() ){
                        try {
                          Double anguloHorBajaDerecha = Double.parseDouble(m.group(2));
                          medidaLuz.setAngInclinacionHorBajaDerecha(anguloHorBajaDerecha);
                        } catch(NumberFormatException nfe) {
                            System.out.println("No se puede tomar Angulo Inclinacion Luz Baja Derecha");
                        }
                    }
               }
               else if(m.group(1).equals("IllumTot")){
                    System.out.println("Iluminacion Total "+ m.group(2));
                    if(!m.group(2).isEmpty()){
                        try {
                         Double iluminacionTotal = Double.parseDouble(m.group(2));
                         iluminacionTotal = iluminacionTotal / 1000;
                         medidaLuz.setIntensidadTotal(iluminacionTotal);
                        } catch(NumberFormatException e) {
                            System.out.println("No se puede tomar iluminacionTotal");
                        }
                    }
               }
            }

        }
        System.out.println("Fin del archivo");
        return medidaLuz;
    }

/**
 * Trae la información de propietario y
 * vehiculo según la hoja de prueba.
 *
 * @return un mapa con la información
 */
    public Map<String,String> traerInfo(long idHojaPrueba) throws ClassNotFoundException, FileNotFoundException, IOException, SQLException{
        Map<String,String> infoMap = new HashMap<String, String>(4);

        String urljdbc = UtilPropiedades.cargarPropiedad("urljdbc","propiedades.properties");
        Connection con =Conex.getConnection();
         
        String strInfo = "SELECT v.CARPLATE,v.VIN,v.CARTYPE,p.Nombres,p.Apellidos FROM vehiculos as v"+
                         " inner join hoja_pruebas as h on h.Vehiculo_for = v.CAR"+
                         " inner join propietarios as p on h.Propietario_for = p.CAROWNER"+
                         " where h.TESTSHEET = ?";
        PreparedStatement ps = con.prepareStatement(strInfo);
        ps.setLong(1,idHojaPrueba);
        ResultSet rs = ps.executeQuery();
        rs.next();
        infoMap.put("placa", rs.getString("CARPLATE"));
        infoMap.put("VIN",rs.getString("VIN"));
        infoMap.put("Nombres",rs.getString("Nombres"));
        infoMap.put("Apellidos", rs.getString("Apellidos"));
        infoMap.put("TipoVehiculo",rs.getString("CARTYPE"));
        con.close();
        return infoMap;
    }


    /**
     * Arma los codigos para insertarlos en la base de datos
     * @param med
     * @param tipo
     * @return
     */
    public List<MedidaGeneral> generarMedidas(MedidaLuxometro med,TipoAutomovil tipo){

        List<MedidaGeneral> lista = new ArrayList<>();
        if(tipo.equals(TipoAutomovil.LIVIANO) || tipo.equals(TipoAutomovil.CUATROXCUATRO)
               || tipo.equals(TipoAutomovil.PESADO)){
           lista.add(new MedidaGeneral(2003,med.getAngInclinacionVertBajaDerecha()));
           lista.add(new MedidaGeneral(2006,med.getIntensidadLuzBajaDerecha()));
           lista.add(new MedidaGeneral(2007,med.getIntensidadLuzAltaDerecha()));
           lista.add(new MedidaGeneral(2005,med.getAnguloInclinacionVertBajaIzquierda()));
           lista.add(new MedidaGeneral(2009,med.getIntensidadLuzBajaIzquierda()));
           lista.add(new MedidaGeneral(2010,med.getIntensidadLuzAltaIzquierda()));
           lista.add(new MedidaGeneral(2011,med.getIntensidadTotal()));

        } else if(tipo.equals(TipoAutomovil.MOTO)){
            //Cuando se trata de motos el luxometro gemini genera las medida de la luz en el lado izquierdo
            lista.add(new MedidaGeneral(2013,med.getAnguloInclinacionVertBajaIzquierda()));//en lusxometro gemini trabaja con la luz izquierda
            lista.add(new MedidaGeneral(2014,med.getIntensidadLuzBajaIzquierda()));
            lista.add(new MedidaGeneral(2000, med.getIntensidadLuzAltaIzquierda()));
            //si tiene dos faros
            //lista.add(new MedidaGeneral(2002,med.getAngInclinacionVertBajaDerecha()));
            //lista.add(new MedidaGeneral(2015,med.getIntensidadLuzBajaIzquierda()));
            //lista.add(new MedidaGeneral(2001,med.getIntensidadLuzAltaDerecha()));
            //
            //lista.add(new MedidaGeneral(2011,med.getIntensidadTotal())); Se quita ya que no aplica para motos
        }else if(tipo.equals(TipoAutomovil.MOTOCARRO)){

        }
        return lista;
    }


    /**
     * Evalua la prueba
     * @param med
     */

    public List<DefectoGeneral> validarMedida(MedidaLuxometro med, TipoAutomovil tipoAutomovil) {
        List<DefectoGeneral> listaDefectos = new ArrayList<>();
        boolean pruebaValida = true;
        if( (med.getAngInclinacionVertBajaDerecha() < permisibleAnguloMenor   || med.getAngInclinacionVertBajaDerecha()> permisibleAnguloMayor )
                || (med.getAnguloInclinacionVertBajaIzquierda() < permisibleAnguloMenor || med.getAnguloInclinacionVertBajaIzquierda() > permisibleAnguloMayor) ){
            if(tipoAutomovil.equals(TipoAutomovil.MOTO)){
                if(med.getAnguloInclinacionVertBajaIzquierda() < permisibleAnguloMenor || med.getAnguloInclinacionVertBajaIzquierda() > permisibleAnguloMayor){
                    listaDefectos.add(new DefectoGeneral(20002, TipoDefecto.DEFECTO_A));
                }
                
            }else {
                listaDefectos.add(new DefectoGeneral(24006, TipoDefecto.DEFECTO_A));
            }
        }
        //Evaluar las luces Bajas
        if(tipoAutomovil.equals(TipoAutomovil.LIVIANO) || tipoAutomovil.equals(TipoAutomovil.PESADO)
                || tipoAutomovil.equals(TipoAutomovil.PESADO) ){
            //Mira ambas
            if(med.getIntensidadLuzBajaDerecha() < permisibleLucesBajasMenor || med.getIntensidadLuzBajaIzquierda() < permisibleLucesBajasMenor){
              pruebaValida = false;
              listaDefectos.add(new DefectoGeneral(20000,TipoDefecto.DEFECTO_A));
            }

        }else if(tipoAutomovil.equals(TipoAutomovil.MOTO)){
            //Mira solamene la derecha
            if(med.getIntensidadLuzBajaIzquierda() < permisibleLucesBajasMenor){
                pruebaValida =false;
                listaDefectos.add(new DefectoGeneral(24005,TipoDefecto.DEFECTO_A));
            }
        }
        //Evaluar la suma total
        if(med.getIntensidadTotal() > permisibleSumatoria){
            pruebaValida = false;
            listaDefectos.add(new DefectoGeneral(20001,TipoDefecto.DEFECTO_A));
        }

        return listaDefectos;

    }
}
