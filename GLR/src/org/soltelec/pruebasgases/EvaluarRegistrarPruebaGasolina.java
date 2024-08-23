/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.pruebasgases;

import static com.soltelec.modulopuc.persistencia.conexion.DBUtil.execIndTrama;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.soltelec.util.AproximacionMedidas;
import org.soltelec.util.Conex;
import org.soltelec.util.ConsultarDatosVehiculo;
import org.soltelec.util.MedicionGases;
import org.soltelec.util.RegistrarMedidas;
import termohigrometro.MedicionTermoHigrometro;

/**
 * Clase para evaluar y registrar la medida de las pruebas de gasolina
 *
 * @author Gerencia Desarrollo y Soluciones Tecnologicas
 */
public class EvaluarRegistrarPruebaGasolina {

    List<List<MedicionGases>> arregloListaMediciones;
    List<MedicionGases> listaCrucero;
    List<MedicionGases> listaRalenti;
    private Double hcRalenti;
    private Double coRalenti;
    private Double co2Ralenti;
    private Double o2Ralenti;
    private Integer rpmRalenti;
    private Integer tempRalenti;
    private Double hcCrucero;
    private Double coCrucero;
    private Double co2Crucero;
    private Double o2Crucero;
    private Integer rpmCrucero;
    private Integer tempCrucero;
    private Integer diluRalenty = 0;
    private Integer diluCrucero = 0;
    private boolean convertidorCatalitico;

    //VARIABLES PARA REGISTRAR LAS MEDIDAS
    private final long idUsuario, idHojaPrueba, idPrueba;

    private boolean tempDeBanco = false;
    private boolean convertidorCataliticoRalenti;
    private boolean convertidorCataliticoCrucero;

    public EvaluarRegistrarPruebaGasolina(List<List<MedicionGases>> arregloListaMediciones, long usuario, long hojaPruebas, long pruebas, boolean tempDeBanco) {
        this.arregloListaMediciones = arregloListaMediciones;
        listaCrucero = arregloListaMediciones.get(0);
        listaRalenti = arregloListaMediciones.get(1);
        this.tempDeBanco = tempDeBanco;
        this.idUsuario = usuario;
        this.idHojaPrueba = hojaPruebas;
        this.idPrueba = pruebas;
    }

    public EvaluarRegistrarPruebaGasolina(long usuario, long hojaPruebas, long pruebas) {
        this.idUsuario = usuario;
        this.idHojaPrueba = hojaPruebas;
        this.idPrueba = pruebas;
    }

    private void calcularMedidas() {
        Double AcumHC = 0.0;
        Double AcumCO = 0.0;
        Double AcumCO2 = 0.0;
        Double AcumO2 = 0.0;
        Integer AcumRpm = 0;
        Integer AcumTemp = 0;
        Integer cntMed = 0;
        for (MedicionGases m : listaRalenti) {
            if (m.isLect5Seg() == true) {
                AcumHC = m.getValHC();
                AcumCO = AcumCO + m.getValCO() * 0.01;
                AcumCO2 = AcumCO2 + m.getValCO2() * 0.1;
                AcumO2 = AcumO2 + m.getValorO2() * 0.01;
                AcumRpm = (int) +m.getValRPM();
                if (tempDeBanco) {
                    AcumTemp = AcumTemp + m.getValorTAceiteSinTabla();
                } else {
                    AcumTemp = AcumTemp + m.getValorTAceite();
                }
                cntMed++;
            }//condicional ult Lectura de gases
            if (m.isConvertidorCatalitico()) {
                convertidorCataliticoRalenti = true;
            }

        }
        //sacar los promedios de las Variables de Gases
        hcRalenti = AcumHC.doubleValue();
        coRalenti = AcumCO;
        co2Ralenti = AcumCO2;
        o2Ralenti = AcumO2;
        rpmRalenti = (AcumRpm.intValue());
        tempRalenti = (AcumTemp.intValue());
        System.out.println("*** -- ******");
        System.out.println(" Resultado O2Ralenty " + o2Ralenti + " y el co2 " + co2Ralenti);
        System.out.println("*** -- ******");
        if (o2Ralenti > 5 || co2Ralenti < 7) {
            diluRalenty = 1;
        }
        System.out.println(" estado diluRalenty " + diluRalenty);
        //reseteo de valores 
        AcumHC = 0.0;
        AcumCO = 0.0;
        AcumCO2 = 0.0;
        AcumO2 = 0.0;
        AcumRpm = 0;
        AcumTemp = 0;
        cntMed = 0;
        for (MedicionGases m : listaCrucero) {
            if (m.isLect5Seg() == true) {
                AcumHC = m.getValHC();
                AcumCO = AcumCO + m.getValCO() * 0.01;
                AcumCO2 = AcumCO2 + m.getValCO2() * 0.1;
                AcumO2 = AcumO2 + m.getValorO2() * 0.01;
                AcumRpm = (int) +m.getValRPM();
                cntMed++;
                if (tempDeBanco) {
                    AcumTemp = AcumTemp + m.getValorTAceiteSinTabla();
                } else {
                    AcumTemp = AcumTemp + m.getValorTAceite();
                }
            }
            if (m.isConvertidorCatalitico()) {
                convertidorCataliticoCrucero = true;
            }
        }
        hcCrucero = AcumHC;
        coCrucero = AcumCO;
        co2Crucero = AcumCO2;
        o2Crucero = AcumO2;
        rpmCrucero = (AcumRpm.intValue());
        tempCrucero = (AcumTemp.intValue());
        System.out.println("*** -- ******");
        System.out.println(" Resultado o2Crucero " + o2Crucero + " y el co2 " + co2Crucero);
        System.out.println("*** -- ******");
        if (o2Crucero > 5 || co2Crucero < 7) {
            diluCrucero = 1;
        }
        System.out.println(" estado diluCrucero " + diluCrucero);
    }//end of calcular medidas

    /**
     * Este metodo tiene unida la logica de guardar los datos en la base de
     * datos y la logica de la evaluacion de la prueba.
     *
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    private void registrarMedidas(int aplicTrans, String ipEquipo) throws ClassNotFoundException, SQLException {
        System.out.println("-_--_-__-_--_--___-_ ESTOY EN REGISTRAR MEDIDAS PARA RECHAZAR __-_-_-____--______--___");
        try {
            Connection conexion = Conex.getConnection();
            conexion.setAutoCommit(false);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
            Date fechaInicioResolucion0762Tabla29 = sdf.parse("2023-02-07 23:59:59");
            Date fechaDeIngresoVehiculo = sdf.parse(sdf.format(consultarFechaIngresoVehiculo(idHojaPrueba)));

            boolean registro = verificarMedidas(co2Crucero, co2Ralenti);//Verifica si una medida es menor o igual a cero
            if (!registro) {
                JOptionPane.showMessageDialog(null, "Disculpe He detectado que Algunos datos son invalidos por lo tanto se debe reiniciar la prueba ");
                return;
            }

            String sql = "SELECT v.CARTYPE,v.Modelo,v.Tiempos_motor,v.FUELTYPE,v.SERVICE from vehiculos as v inner join hoja_pruebas as hp on v.CAR=hp.Vehiculo_for where hp.TESTSHEET = ?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, idHojaPrueba);
            ResultSet rs = ps.executeQuery();
            rs.next();//primer registro
            int modelo = rs.getInt("Modelo");
            int gasolina = rs.getInt("FUELTYPE");
            boolean pruebaAprobada = false;
            boolean dilucion = false;
            //Verificar la Dilucion
            System.out.println("Modelo del vehiculo: " + modelo);
            System.out.println("Fuelttype: " + gasolina);
            System.out.println("Prueba aprobada: " + pruebaAprobada);
            System.out.println("hcRalenti:  1 " + hcRalenti);
            System.out.println("hcCrucero 1" + hcCrucero);
            System.out.println("coRalenti: 1" + coRalenti);
            System.out.println("coCrucero: 1" + coCrucero);
//           
            if (gasolina == 1) {
                if (o2Crucero > 5 || o2Ralenti > 5 || co2Crucero <= 7 || co2Ralenti <= 7 || co2Ralenti >= 17 || co2Crucero >= 17) {
                    dilucion = true;
                    pruebaAprobada = false;
                }
            }
            {
                //EVALUAR LA PRUEBA
//                if(fechaDeIngresoVehiculo.before(fechaInicioResolucion0762Tabla29)){
//                    System.out.println("valores vieja resolucion--------------------------------------------------------------------------------------------------------");
//                    if ((modelo) <= 1970) {
//                        pruebaAprobada = hcRalenti <= 800 && hcCrucero <= 800 && coRalenti <= 5 && coCrucero <= 5;
//                    } else if (modelo > 1970 && modelo <= 1984) {
//                        pruebaAprobada = hcRalenti <= 650 && hcCrucero <= 650 && coRalenti <= 4 && coCrucero <= 4;
//                    } else if (modelo > 1984 && modelo <= 1997) {
//                        pruebaAprobada = hcRalenti <= 400 && hcCrucero <= 400 && coRalenti <= 3 && coCrucero <= 3;
//                    } else if (modelo > 1997) {
//                        pruebaAprobada = hcRalenti <= 200 && hcCrucero <= 200 && coRalenti <= 1 && coCrucero <= 1;
//                    }
//                }else{
                System.out.println("valores nueva resolucion--------------------------------------------------------------------------------------------------------");
                if (modelo <= 1984) {
                    pruebaAprobada = hcRalenti <= 650 && hcCrucero <= 650 && coRalenti <= 4 && coCrucero <= 4;
                    System.out.println("entro a menores a 1984");
                } else if (modelo >= 1985 && modelo <= 1997) {
                    pruebaAprobada = hcRalenti <= 400 && hcCrucero <= 400 && coRalenti <= 3 && coCrucero <= 3;
                    System.out.println("entro entre 1985 y 1997");
                } else if (modelo >= 1998 && modelo <= 2009) {
                    pruebaAprobada = hcRalenti <= 200 && hcCrucero <= 200 && coRalenti <= 1 && coCrucero <= 1;
                    System.out.println("entro entre 1998 y 2009");
                } else {//modelo >= 2010
                    pruebaAprobada = hcRalenti <= 160 && hcCrucero <= 160 && coRalenti <= 0.8 && coCrucero <= 0.8;
                    System.out.println("entro mayores 2010");
                }
                System.out.println("prueba aprobada: " + pruebaAprobada);
                // }
            }
            String serialEquipo = "";
            try {
                serialEquipo = ConsultarDatosVehiculo.buscarSerialEquipo(idPrueba);
            } catch (Exception e) {
                serialEquipo = "Serial no encontrado";
            }
            if (dilucion) {
                pruebaAprobada = false;
            }
            System.out.println("ESTA DENTRO DEL METODO EVALUARrEGISTRARpRUEBA");
            System.out.println(" ");
            System.out.println("LA APLICACION TRANS ES  " + aplicTrans);

            boolean escrTrans = true;
            if (aplicTrans == 1 && pruebaAprobada == false) {
                escrTrans = false;
            }
            if (escrTrans == false) {
                String statement2 = "UPDATE pruebas SET Autorizada = 'A',usuario_for = ?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?";
                PreparedStatement instruccion2 = conexion.prepareStatement(statement2);
                instruccion2.setLong(1, idUsuario);
                instruccion2.setString(2, serialEquipo);
                instruccion2.setLong(3, idPrueba);
                int executeUpdate = instruccion2.executeUpdate();
                System.out.println("Pruebas CARGADAS =" + executeUpdate);
            } else {
                String statement2 = "UPDATE pruebas SET Finalizada = 'Y',Aprobada=?,Abortada='N',usuario_for = ?,serialEquipo = ?,observaciones = ?,Comentario_aborto = ?,Fecha_final = ? WHERE pruebas.Id_Pruebas = ?";
                PreparedStatement instruccion2 = conexion.prepareStatement(statement2);
                System.out.println("FINALIZO PRUEBA");
                if (pruebaAprobada) {
                    instruccion2.setString(1, "Y");
                } else {
                    instruccion2.setString(1, "N");
                }
                instruccion2.setLong(2, idUsuario);
                instruccion2.setString(3, serialEquipo);
                String observ = "";
                String flag = "";
                if (diluRalenty == 1) {
                    observ = "DILUCION EN LA MUESTRA DE RALENTY";
                    flag = "DILUCION DE MUESTRA";
                }
                if (diluCrucero == 1) {
                    if (observ.length() > 0) {
                        observ = observ.concat("- DILUCION EN LA MUESTRA DE CRUCERO");
                    } else {
                        observ = "DILUCION EN LA MUESTRA DE CRUCERO";
                    }
                    flag = "DILUCION DE MUESTRA";
                }
                instruccion2.setString(4, observ);
                instruccion2.setString(5, flag);

                Timestamp fechaActual = new Timestamp(new Date().getTime());
                System.out.println("-------------------MENSAJE ALERTA FECHA QUE TIENE AL MOMENTO DE REGISTRAR---------------------");
                System.out.println("-------------------" + fechaActual + "---------------------");
                instruccion2.setTimestamp(6, fechaActual);
                instruccion2.setLong(7, idPrueba);
                int executeUpdate = instruccion2.executeUpdate();
                System.out.println("Pruebas CARGADAS =" + executeUpdate);

                //insertar el defecto en este caso es unico
                if (!pruebaAprobada) {
                    String strDefecto = "INSERT INTO defxprueba(id_defecto,id_prueba) VALUES (?,?)";
                    PreparedStatement psDefecto = conexion.prepareStatement(strDefecto);
                    psDefecto.setInt(1, 80000);
                    psDefecto.setLong(2, idPrueba);
                    psDefecto.executeUpdate();
                }
            }// FIN DE LA TOMA DE DESION DEL ESTADO APLICATRANS
            String statement = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
            PreparedStatement instruccion = conexion.prepareStatement(statement);
            instruccion.setInt(1, 8001);//HC cuatro tiempos
            instruccion.setDouble(2, AproximacionMedidas.AproximarMedidas(hcRalenti));
            instruccion.setLong(3, idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();
            System.out.println("escr medida  8001");

            instruccion.setInt(1, 8002);//CO cuatro tiempos
            instruccion.setDouble(2, AproximacionMedidas.AproximarMedidas(coRalenti));
            instruccion.setLong(3, idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();

            instruccion.setInt(1, 8003);//CO2 ralenti
            instruccion.setDouble(2, AproximacionMedidas.AproximarMedidas(co2Ralenti));
            instruccion.setLong(3, idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();
            System.out.println("escr medida  8003");
            instruccion.setInt(1, 8004);//O2 en ralenti
            instruccion.setDouble(2, AproximacionMedidas.AproximarMedidas(o2Ralenti));
            instruccion.setLong(3, idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();
            System.out.println("escr medida  8004");
            instruccion.setInt(1, 8005);
            instruccion.setDouble(2, rpmRalenti);
            instruccion.setLong(3, idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();
            System.out.println("escr medida  8005");
            instruccion.setInt(1, 8006);//temperatura en ralenti
            instruccion.setDouble(2, tempRalenti);
            instruccion.setLong(3, idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();
            System.out.println("escr medida  8006");
            instruccion.setInt(1, 8007);//HC crucero
            instruccion.setDouble(2, AproximacionMedidas.AproximarMedidas(hcCrucero));
            instruccion.setLong(3, idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();
            System.out.println("escr medida  8007");
            instruccion.setInt(1, 8008);//CO crucero
            instruccion.setDouble(2, AproximacionMedidas.AproximarMedidas(coCrucero));
            instruccion.setLong(3, idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();
            System.out.println("escr medida  8008");
            instruccion.setInt(1, 8009);//CO2 crucero
            instruccion.setDouble(2, AproximacionMedidas.AproximarMedidas(co2Crucero));
            instruccion.setLong(3, idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();
            System.out.println("escr medida  8009");
            instruccion.setInt(1, 8010);//O2 crucero
            instruccion.setDouble(2, AproximacionMedidas.AproximarMedidas(o2Crucero));
            instruccion.setLong(3, idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();
            System.out.println("escr medida  8010");
            instruccion.setInt(1, 8011);//rpm crucero
            instruccion.setDouble(2, rpmCrucero);
            instruccion.setLong(3, idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();
            System.out.println("escr medida  8011");
            instruccion.setInt(1, 8012);
            instruccion.setDouble(2, tempCrucero);
            instruccion.setLong(3, idPrueba);
            instruccion.executeUpdate();
            System.out.println("escr medida  8012");
            conexion.commit();
            conexion.setAutoCommit(true);
        } catch (Exception ex) {
            System.out.println(">>ex causa >>" + ex.getCause());
            System.out.println(">>ex mensage >>" + ex.getMessage());

        }
    }//end of method registarMedidas

    private Date consultarFechaIngresoVehiculo(long idPrueba) {
        String statement = "SELECT hp.Fecha_ingreso_vehiculo FROM pruebas as p INNER JOIN hoja_pruebas as hp ON p.hoja_pruebas_for = hp.TESTSHEET INNER JOIN vehiculos as v ON hp.Vehiculo_for = v.CAR WHERE hp.TESTSHEET = ?";
        try {
            Connection cn = Conex.getConnection();
            PreparedStatement consultaFechaIngreso = cn.prepareStatement(statement);
            consultaFechaIngreso.setLong(1, idPrueba);
            ResultSet rs = consultaFechaIngreso.executeQuery();
            while (rs.first()) {
                System.out.println("-------sin parsear" + rs.getDate("Fecha_ingreso_vehiculo"));
                System.out.println("-------to string" + rs.getDate("Fecha_ingreso_vehiculo").toString());
                return rs.getDate("Fecha_ingreso_vehiculo");
            }
        } catch (SQLException e) {
            System.err.println("Error en el metodo : consultarFecha()" + e.getMessage() + e.getLocalizedMessage());
        } catch (ClassNotFoundException exp) {
            exp.printStackTrace();
        }
        return null;
    }

    public boolean verificarMedidas(double... medidas) {
        for (double d : medidas) {
            if (d <= 0) {
                return false;
            }
        }
        return true;
    }

    public void registrar(int aplicTrans, String ipEquipo) throws SQLException, ClassNotFoundException {
        calcularMedidas();
        registrarMedidas(aplicTrans, ipEquipo);
    }

    public void registrorRPMTemp(Integer rpm, Integer temp) throws ClassNotFoundException {
        Connection cn = null;
        try {
            cn = Conex.getConnection();
            cn.setAutoCommit(false);
            String strInsert = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
            PreparedStatement ps = cn.prepareStatement(strInsert);
            ps.setInt(1, 8011);
            ps.setDouble(2, rpm);
            ps.setLong(3, idPrueba);
            ps.executeUpdate();
            ps.clearParameters();
            ps.setInt(1, 8006);
            ps.setDouble(2, temp);
            ps.setLong(3, idPrueba);
            ps.executeUpdate();
            cn.commit();
        } catch (SQLException exc) {
            System.out.println("me cai x : " + exc.getMessage());
            System.out.println("state sql : " + exc.getSQLState());
            System.out.println("state cause : " + exc.getCause());
        } finally {
            try {
                cn.close();
            } catch (SQLException e) {
            }
        }
    }

    public void registrarTemperaturaHumedad(Double temperatura, Double humedad) throws ClassNotFoundException {

        Connection cn = null;
        try {
            cn = Conex.getConnection();
            String strInsert = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
            PreparedStatement ps = cn.prepareStatement(strInsert);
            //insertar la temperatura
            cn.setAutoCommit(false);
            ps.setInt(1, 8031);
            ps.setDouble(2, temperatura);
            ps.setLong(3, idPrueba);
            ps.executeUpdate();
            ps.clearParameters();
            ps.setInt(1, 8032);
            ps.setDouble(2, humedad);
            ps.setLong(3, idPrueba);
            ps.executeUpdate();
            cn.commit();
            cn.setAutoCommit(true);
            System.out.println("Termohigrometro Registrado. Temp: " + temperatura + "Humedad: " + humedad);
        } catch (SQLException exc) {
            JOptionPane.showMessageDialog(null, "No se pueden grabar los valores de humedad y temperatura");
            Logger.getLogger("igrafica").error("No se pueden grabar los valores de humedad y temperatura", exc);
            System.out.println("Error registrando Termohigrometro " + exc);
        } finally {
            try {
                cn.close();
            } catch (SQLException e) {
            }
        }
    }

}
