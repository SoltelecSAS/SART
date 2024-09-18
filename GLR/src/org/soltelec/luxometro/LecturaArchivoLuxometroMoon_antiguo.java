/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.luxometro;

//import com.soltelec.integrador.cliente.ClienteSicov;
//import com.soltelec.estandar.EstadoEventosSicov;
import com.soltelec.modulopuc.configuracion.modelo.Propiedades;
import com.soltelec.modulopuc.utilidades.Mensajes;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import org.ini4j.Wini;
//import static org.soltelec.luxometro.HiloLucesMoto.aplicTrans;
import org.soltelec.luxometro.capelec.cap2500.MedidasVehiculosLuxometro;
import org.soltelec.util.Conex;
import org.soltelec.util.ConsultarDatosVehiculo;

/**
 * Inicializa la Carga de informacion de las medidas del luxometro tecnolux
 *
 * @Fecha 16/04/2016
 * @author GerenciaDesarrollo
 */
public class LecturaArchivoLuxometroMoon_antiguo {

    private static final Logger LOG = Logger.getLogger(LecturaArchivoLuxometroMoon_antiguo.class.getName());
    private MedidasVehiculosLuxometro medidasVehiculos;
    public static String tramaAuditoria;
    public int aplicTrans;
    
    
    public LecturaArchivoLuxometroMoon_antiguo(int aplicTrans) 
    {
       this.aplicTrans=aplicTrans;
    }

    
    
    public void iniciarTomaMedidaLuxometro(String placa, int idHojaPrueba, int idPrueba, int idUsuario,int tipoVehiculo) {
        try {
            JOptionPane.showMessageDialog(null, "Asegurece que el luxometro este encendido");
//            String puerto = UtilPropiedades.cargarPropiedad("PuertoLuxometro", "propiedades.properties").replaceAll("COM", "");
            /*String puerto = Propiedades.getInstance().getPuertoLuxometro().replaceAll("COM", "");
            Wini ini = new Wini(new File(System.getProperty("user.dir") + "/luxometro/gHBTInt-R.ini"));
            ini.put("Folders", "SharingFolder", new File(System.getProperty("user.dir")
                    + "/luxometro/").toURI().toURL().toString().replaceAll("file:/", ""));
            ini.put("Communication", "COMPort", puerto);
            ini.store();*/
            crearPlacaFile(placa);
            final Process process = Runtime.getRuntime().exec(String.format("cmd /c start /wait %s/luxometro/luxometro.bat",
                    System.getProperty("user.dir")));
            final int exitVal = process.waitFor();
            if (exitVal == 0) {
                medidasVehiculos = new MedidasVehiculosLuxometro();
               if(tipoVehiculo==2){ 
                
                medidasVehiculos.setIntensidadLuzBajaDerecha(obtenerMedida(16));//ok
                medidasVehiculos.setInclinacionLuzBajaDerecha(obtenerMedida(17));
                medidasVehiculos.setIntensidadLuzBajaIzquierda(obtenerMedida(21));
                medidasVehiculos.setIntensidadLuzAltaDerecha(obtenerMedida(25));
                medidasVehiculos.setInclinacionLuzBajaIzquierda(obtenerMedida(22));
                medidasVehiculos.setIntensidadLuzAltaIzquierda(obtenerMedida(30));
                medidasVehiculos.setExploradoraDerecha(obtenerMedida(35));
                medidasVehiculos.setExploradoraIzquierda(obtenerMedida(40));
//                LOG.log(Level.INFO, medidasVehiculos.toString());
                
                }
               else{
                   medidasVehiculos.setIntensidadLuzBajaIzquierda(obtenerMedida(21));
                   medidasVehiculos.setInclinacionLuzBajaIzquierda(obtenerMedida(22));
               }
                try {
                   tramaAuditoria= registrarMedidas(idPrueba, idUsuario,tipoVehiculo);
                } catch (ClassNotFoundException | SQLException ex) {
                    Logger.getLogger(LecturaArchivoLuxometroMoon_antiguo.class.getName()).log(Level.SEVERE, null, ex);
                }
               
            }
        } catch (IOException | InterruptedException e) {
            LOG.log(Level.SEVERE, "Problemas al iniciar el programa {0}", e);
        }   
    }

    private static void crearPlacaFile(String placa) {
        try {
            File file = new File(System.getProperty("user.dir") + "/luxometro/ENTRADA.DAT");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(placa);
            bw.close();
            LOG.log(Level.INFO, "Se logro crear el archivo-placa ENTRADA.DAT");
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Problemas al crear el archivo de entrada {0}", e);
        }
    }

    /**
     * Obtiene la medida de ina linea en particular
     *
     * @param index Numero de linea a leer.
     */
    private static double obtenerMedida(int index) {
        BufferedReader br = null;
        String medida = "";
        int contador = 1;
        try {
            br = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/luxometro/SALIDA.DAT"));
            while (true) {
                if (contador == index) {
                    try {
                        medida = br.readLine().split(",")[1];
                    } catch (Exception e) {
                        medida = "0";
                    }
                    break;
                }
                br.readLine();
                contador++;
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Problemas al buscar el archivo {0}", e);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Problemas al intentar cerrar el BufferedReader {0}", ex);
            }
        }
        LOG.log(Level.INFO, "Lectura de medida satisfactoria");

        double medidad = Double.parseDouble(medida);
        if (medidad < 0) {
            medidad = (medidad * -1);
        }

        return medidad;
    }

    private String  registrarMedidas(Integer idPrueba, long idUsuario, Integer tipoVehiculo) throws ClassNotFoundException, SQLException 
    {
        //Crear el objeto de <span class="IL_AD" id="IL_AD12">conexion</span> a la base de datos
        Connection conexion = Conex.getConnection();
        conexion.setAutoCommit(false);
        double permisibleBaja = 2.5;
        double permisibleAnguloAlto = 3.5;
        double permisibleAnguloBajo = 0.5;
        double permisibleSumatoria = 225;
        boolean aprobada = true;
        double luzMenorBaja = -1;
        double angIncBaja = -1;
        String statement ="";
        String statement2;
        String serialEquipo = "";
        //Crear objeto preparedStatement para realizar la consulta con la base de datos
        //String statementBorrar = ("DELETE  FROMmedidas WHERE TEST = ?");
        //PreparedStatement instruccionBorrar = conexion.prepareStatement(statementBorrar);
        //instruccionBorrar.setInt(1,idPrueba);
        //instruccionBorrar.executeUpdate();*

        
        if(tipoVehiculo ==1){
            permisibleBaja = 2.5;
            //HiloLucesMoto hm =new HiloLucesMoto(null, aplicTrans, tramaAuditoria);
            //hm.verificarMedidas(medidasVehiculos.getIntencidadLuzBajaIzquierda());
            statement = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
            PreparedStatement instruccion = conexion.prepareStatement(statement);
            instruccion.setInt(1, 2014);
            instruccion.setDouble(2, medidasVehiculos.getIntensidadLuzBajaIzquierda());
            instruccion.setLong(3, idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();
            instruccion.setInt(1, 2013);
            instruccion.setDouble(2, medidasVehiculos.getInclinacionLuzBajaIzquierda());
            instruccion.setLong(3, idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();
            
            luzMenorBaja = medidasVehiculos.getIntensidadLuzBajaIzquierda();
            angIncBaja = medidasVehiculos.getInclinacionLuzBajaIzquierda();
            
            String str2 = "INSERT INTO defxprueba(id_defecto,id_prueba,Tipo_defecto) VALUES (?,?,'A')";
            PreparedStatement psDefectos = conexion.prepareStatement(str2);
            String str3 = "UPDATE medidas SET Condicion='*' WHERE medidas.MEASURE = ?";//Esto no hace nada
            aprobada = true;
            if (luzMenorBaja < permisibleBaja /*|| luzMenorAlta < permisibleBaja*/   ) {
                if (aplicTrans == 0) {
                    psDefectos.clearParameters();
                    psDefectos.setInt(1, 24005); //La intensidad de la luz menor a 2.5 klux a 1 m o 4 lux a 25 m. Se debe acelerar la moto hasta lograr la mayor intensidad de luz
                    psDefectos.setLong(2, idPrueba);
                    psDefectos.executeUpdate();
                }
                aprobada = false;
            }
            if (angIncBaja < permisibleAnguloBajo || angIncBaja > permisibleAnguloAlto) {
            if (aplicTrans == 0) {
                System.out.println("Debo agregar defecto 24006");
                psDefectos.clearParameters();
//              psDefectos.setInt(1, 20002);//codigo defecto es distinto
                psDefectos.setInt(1, 24006);//La desviacion de cualquier haz de luz en posicion de bajas esta por fuera del rango entre 0.5 y 3.5% siendo 0 el horizonte y 3.5% la desviacion hacia el piso
                psDefectos.setLong(2, idPrueba);
                psDefectos.executeUpdate();
            }
            aprobada = false;
        }
        
            try {
                serialEquipo = ConsultarDatosVehiculo.buscarSerialEquipo(idPrueba);
            } catch (Exception e) {
                serialEquipo = "Serial no encontrado";
                e.printStackTrace();
            }
            
        }else{
            permisibleBaja = 2.5;
            permisibleAnguloAlto = 3.5;
            permisibleAnguloBajo = 0.5;
            permisibleSumatoria = 225;
            statement = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
            PreparedStatement instruccion = conexion.prepareStatement(statement);
            instruccion.setInt(1, 2003);
            instruccion.setDouble(2, medidasVehiculos.getInclinacionLuzBajaDerecha());
            instruccion.setLong(3, idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();
            instruccion.setInt(1, 2006);
            instruccion.setDouble(2, medidasVehiculos.getIntensidadLuzBajaDerecha());
            instruccion.setLong(3, idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();
            instruccion.setInt(1, 2007);
            instruccion.setDouble(2, medidasVehiculos.getIntensidadLuzAltaDerecha());
            instruccion.setLong(3, idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();
            instruccion.setInt(1, 2005);
            instruccion.setDouble(2, medidasVehiculos.getInclinacionLuzBajaIzquierda());
            instruccion.setLong(3, idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();
            instruccion.setInt(1, 2009);
            instruccion.setDouble(2, medidasVehiculos.getIntensidadLuzBajaIzquierda());
            instruccion.setLong(3, idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();
            instruccion.setInt(1, 2010);
            instruccion.setDouble(2, medidasVehiculos.getIntensidadLuzAltaIzquierda());
            instruccion.setLong(3, idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();
            instruccion.setInt(1, 2008);
            instruccion.setDouble(2, medidasVehiculos.getExploradoraDerecha() + medidasVehiculos.getExploradoraIzquierda());
            instruccion.setLong(3, idPrueba);
            instruccion.setInt(1, 2011);

        double mayorSuma = 0;
        /*
        Object[] choices = {"Caso 1", "Caso 2"};
        int dato = JOptionPane.showOptionDialog(null, "<html><img src='file:images/luces.png'></html>", "CRITERIO PARA LA SUMATORIA TOTAL DE LUCES SELECCIONE UNA OPCION", 0, JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
    
        double sumaExplora = (medidasVehiculos.getExploradoraDerecha() + medidasVehiculos.getExploradoraIzquierda());
        double sumaAlta = medidasVehiculos.getIntencidadLuzAltaDerecha()
                + medidasVehiculos.getIntencidadLuzAltaIzquierda()
                + sumaExplora;
        double sumaBaja = medidasVehiculos.getIntencidadLuzBajaDerecha()
                + medidasVehiculos.getIntencidadLuzBajaIzquierda()
                + sumaExplora;

        if (dato == 0) {
            mayorSuma = (sumaAlta + sumaExplora) > (sumaBaja + sumaExplora) ? (sumaAlta + sumaExplora) : (sumaBaja + sumaExplora);
        } else {
            mayorSuma = (sumaAlta + sumaBaja + sumaExplora);
        }

        instruccion.setDouble(2, mayorSuma);
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();
        */
    PanelLuxometro panelLuxometro;
    double[] luzBajaDerecha;
    double[] luzAltaDerecha;
    double[] luzBajaIzquierda;
    double[] luzAltaIzquierda;
    double[] anguloBajaDerecha;
    double[] anguloBajaIzquierda;
     
    double farBajaDerecha;
    double farAltaDerecha;
    double farBajaIzquierda;
    double farAltaIzquierda;
    double angBajaDerecha;
    double angBajaIzquierda;
    
        tramaAuditoria = "{\"intensidadDerecha\":\"".concat(String.valueOf(medidasVehiculos.getIntensidadLuzBajaDerecha())).concat("\",").concat("\"inclinacionDerecha\":\"").concat(String.valueOf(medidasVehiculos.getIntensidadLuzBajaDerecha())).concat("\",").concat("\"intensidadIzquierda\":\"").concat(String.valueOf(medidasVehiculos.getIntensidadLuzBajaIzquierda())).concat("\",").concat("\"inclinacionIzquierda\":\"").concat(String.valueOf(medidasVehiculos.getInclinacionLuzBajaIzquierda())).concat("\",").concat("\"SumatoriaIntensidad\":\"").concat(String.valueOf(mayorSuma)).concat("\",").concat("\"tablaAfectada\":\"medidas\",\"idRegistro\":\"").concat(String.valueOf(idPrueba)).concat("\"}");
        }
        if (aprobada) {
            statement2 = "UPDATE pruebas SET Finalizada = 'Y',Aprobada ='Y',Abortada='N',usuario_for = ?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?";
        } else {
            statement2 = "UPDATE pruebas SET Finalizada = 'Y',Aprobada ='N',Abortada='N',usuario_for = ?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?";
        }
        PreparedStatement instruccion2 = conexion.prepareStatement(statement2);
        instruccion2.setLong(1, idUsuario);
        instruccion2.setString(2, serialEquipo);
        instruccion2.setLong(3, idPrueba);
        int executeUpdate = instruccion2.executeUpdate();
        conexion.commit();
        conexion.setAutoCommit(true);
        conexion.close();
        Mensajes.messageDoneTime("Se ha Registrado la Prueba de Luces de una manera Exitosa ..¡",3);
        
    return tramaAuditoria;    
    }

    public static void main(String[] args) {

        System.out.println(obtenerMedida(17));

    }
}
