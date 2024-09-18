/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.util;

//import com.soltelec.integrador.cliente.ClienteSicov;
//import com.soltelec.estandar.EstadoEventosSicov;
import static com.soltelec.modulopuc.persistencia.conexion.DBUtil.execIndTrama;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.soltelec.pruebasgases.CallableCiclosGasolina;
import org.soltelec.pruebasgases.CallableSimulacionGasolina;
import org.soltelec.pruebasgases.WorkerCruceroRalenti;
import org.soltelec.pruebasgases.motocicletas.CallableInicioMotos;
import org.soltelec.pruebasgases.motocicletas.CallablePruebaMotos;
import org.soltelec.pruebasgases.motocicletas.CallableSimulacionMotos;
import org.soltelec.pruebasgases.motocicletas.WorkerCiclosMoto;
import org.soltelec.sonometro.JDialogSonometro;

import org.soltelec.test.Test;

/**
 * Clase para guardar medidas en la base de datos usando jdbc Esta la realice
 * para registrar las medidas de la prueba de luces usando el sonometro gemini
 *
 * @author Gerencia Desarrollo de Soluciones Tecnologicas
 */
public class RegistrarMedidas {

    private String mensajeDefe;

    public RegistrarMedidas() {
    }

    public Connection getConnection() throws ClassNotFoundException, SQLException {
        //Crear el objeto de <span class="IL_AD" id="IL_AD12">conexion</span> a la base de datos
        Connection conexion = Conex.getConnection();
        return conexion;
    }

    public void closeConnection(Connection con) throws SQLException {
        con.close();
    }

    public void registrarMedidas(List<MedidaGeneral> listaMedidas, long idPrueba, Connection con) throws SQLException {
        System.out.println("************************************* ingrese a registrar medidas +++++++++++++++++++++++++++++++++++");
        String statement = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
        PreparedStatement ps = con.prepareCall(statement);
        for (MedidaGeneral mg : listaMedidas) {
            ps.clearParameters();
            ps.setInt(1, mg.getTipoMedida());
            System.out.println("REG MEDIA ().-* " + mg.getTipoMedida() + " CON UN VALOR: " + mg.getValorMedida());
            ps.setDouble(2, mg.getValorMedida());
            if (mg.getTipoMedida() == 8001) {
                System.out.println("++++++++++++++++++++valor que se añadio a la base de datos para HC:" + mg.getValorMedida() + " ++++++++++++++++++++++++++++++++++");
            }
            if (mg.getTipoMedida() == 8001) {
                System.out.println("++++++++++++++++++++valor que se añadio a la base de datos para CO:" + mg.getValorMedida() + " ++++++++++++++++++++++++++++++++++");
            }
//            if (mg.getTipoMedida() == 8006) {
//                if (mg.getValorMedida() < 40) {
//                    double nuevaMedida = 41;
//                    mg.setValorMedida(nuevaMedida);
//
//                } else if (mg.getValorMedida() > 65) {
//                    double nuevaMedida = 64;
//                    mg.setValorMedida(nuevaMedida);
//                }
//            }
            if (mg.getTipoMedida() == 8031) {
                if (mg.getValorMedida() < 5) {
                    double nuevaMedida = 6;
                    mg.setValorMedida(nuevaMedida);

                } else if (mg.getValorMedida() > 50) {
                    double nuevaMedida = 48;
                    mg.setValorMedida(nuevaMedida);
                }
            }
            if (mg.getTipoMedida() == 8032) {
                if (mg.getValorMedida() < 30) {
                    double nuevaMedida = 31;
                    mg.setValorMedida(nuevaMedida);

                } else if (mg.getValorMedida() > 90) {
                    double nuevaMedida = 89;
                    mg.setValorMedida(nuevaMedida);
                }
            }
            ps.setLong(3, idPrueba);
            ps.execute();

        }
        System.out.println("************************************* sali de registrar medidas +++++++++++++++++++++++++++++++++++");
    }

    /**
     * Metodo para registrar defectos en la base de datos
     *
     * @param listaDefectos
     * @param idPrueba
     * @param con
     * @throws java.sql.SQLException
     */
    public void registrarDefectos(List<DefectoGeneral> listaDefectos, long idPrueba, Connection con) throws SQLException {
        String str2 = "INSERT INTO defxprueba(id_defecto,id_prueba) VALUES (?,?)";
        PreparedStatement psDefectos = con.prepareStatement(str2);

//        for (DefectoGeneral dg : listaDefectos) {
//            psDefectos.clearParameters();
//            psDefectos.setInt(1, dg.getCodigoDefecto());
//            psDefectos.setLong(2, idPrueba);
//            psDefectos.execute();
//        }
        for (DefectoGeneral dg : listaDefectos) {
            // Validar si el defecto ya existe para esta prueba
            String consultaExistencia = "SELECT COUNT(*) FROM defxprueba WHERE id_defecto = ? AND id_prueba = ?";
            PreparedStatement consultaExistenciaInstruccion = con.prepareStatement(consultaExistencia);
            consultaExistenciaInstruccion.setInt(1, dg.getCodigoDefecto());
            consultaExistenciaInstruccion.setLong(2, idPrueba);

            ResultSet resultadoExistencia = consultaExistenciaInstruccion.executeQuery();
            resultadoExistencia.next();

            int cantidadFilas = resultadoExistencia.getInt(1);

            if (cantidadFilas > 0) {
                // El defecto ya existe para esta prueba, realiza aquí la lógica necesaria
                System.out.println("El defecto " + dg.getCodigoDefecto() + " ya existe para esta prueba.");
                // Puedes lanzar una excepción, mostrar un mensaje, etc.
            } else {
                // El defecto no existe, procede con la inserción
                psDefectos.clearParameters();
                psDefectos.setInt(1, dg.getCodigoDefecto());
                psDefectos.setLong(2, idPrueba);
                psDefectos.execute();
                System.out.println("Defecto " + dg.getCodigoDefecto() + " insertado correctamente.");
            }

            // Cerrar recursos
            consultaExistenciaInstruccion.close();
            resultadoExistencia.close();
        }
    }

    /**
     * Registra una prueba finalizada o no
     *
     * @param cn
     * @param aprobada
     * @param idUsuario
     */
    /**
     * Registra una prueba finalizada o no
     *
     * @param cn
     * @param aprobada
     * @param idUsuario
     * @param idPrueba
     * @throws java.sql.SQLException
     */
    public void registrarPruebaFinalizada(Connection cn, boolean aprobada, long idUsuario, Long idPrueba, boolean escrTrans, String ipEquipo) throws SQLException {
        String serialEquipo = "";
        System.out.println("--------------------------REGISTRANDO EN registrarPruebaFinalizada---------------------------------");
        try {
            serialEquipo = ConsultarDatosVehiculo.buscarSerialEquipo(idPrueba);
            System.out.println(" LECTURA SERIAL");
        } catch (Exception e) {
            System.out.println(" ME CAI CON " + e.getMessage());
            e.printStackTrace(); // Imprime la traza completa para obtener más información
            serialEquipo = "SERIAL NO ENCONTRADO";
        }
        String statement2 = "";
        if (escrTrans == true) {
            System.out.println(" entre escrTrans = true");
            try {
                if (aprobada) {
                    //statement2 = "UPDATE pruebas SET Finalizada = 'Y',Aprobada ='Y',Abortada='N',usuario_for = ?,Fecha_prueba = ?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?";
                    statement2 = "UPDATE pruebas SET Finalizada = 'Y',Aprobada ='Y',Abortada='N',usuario_for = ?,Fecha_final = ?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?";
                } else {
                    //statement2 = "UPDATE pruebas SET Finalizada = 'Y',Aprobada ='N',Abortada='N',usuario_for = ?,Fecha_prueba = ?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?";
                    statement2 = "UPDATE pruebas SET Finalizada = 'Y',Aprobada ='N',Abortada='N',usuario_for = ?,Fecha_final = ?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?";
                }
                PreparedStatement ps = cn.prepareStatement(statement2);
                ps.setLong(1, idUsuario);
                ps.setTimestamp(2, new java.sql.Timestamp((new Date()).getTime()));
                ps.setString(3, serialEquipo);
                ps.setLong(4, idPrueba);
                ps.execute();
            } catch (Exception ex) {
                System.out.println(" ME CAI CON Exception updatePrueba " + ex.getMessage());
                Logger.getLogger(RegistrarMedidas.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println(" entre escrTrans = false");
            try {
                statement2 = "UPDATE pruebas SET Autorizada = 'A',usuario_for = ?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?";
                PreparedStatement instruccion2 = cn.prepareStatement(statement2);
                instruccion2.setLong(1, idUsuario);
                instruccion2.setString(2, serialEquipo);
                instruccion2.setLong(3, idPrueba);
                int executeUpdate = instruccion2.executeUpdate();

            } catch (Exception ex) {
                System.out.println(" ME CAI CON Exception updatePrueba " + ex.getMessage());
                Logger.getLogger(RegistrarMedidas.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void registrarPruebaFinalizadaConObservacion(Connection cn, boolean aprobada, long idUsuario, Long idPrueba, boolean escrTrans, String ipEquipo, String strObservacion) throws SQLException {
        String serialEquipo = "";
        System.out.println("--------------------------REGISTRANDO EN registrarPruebaFinalizadaConObservacion---------------------------------");
        try {
            serialEquipo = ConsultarDatosVehiculo.buscarSerialEquipo(idPrueba);
            System.out.println(" LECTURA SERIAL");
        } catch (Exception e) {
            System.out.println(" ME CAI CON " + e.getMessage());
            System.out.println(" CAUSA " + e.getCause());
            serialEquipo = "Serial no encontrado";
            e.printStackTrace();
        }
        String statement2;
        if (escrTrans == true) {
            if (aprobada) {
                statement2 = "UPDATE pruebas SET Finalizada = 'Y',Aprobada ='Y',Abortada='N',usuario_for = ?,Fecha_final = ?,serialEquipo = ?,observaciones=?,Comentario_aborto = ? WHERE pruebas.Id_Pruebas = ?";
                //statement2 = "UPDATE pruebas SET Finalizada = 'Y',Aprobada ='Y',Abortada='N',usuario_for = ?,Fecha_prueba = ?,serialEquipo = ?,observaciones=?,Comentario_aborto = ? WHERE pruebas.Id_Pruebas = ?";
            } else {
                statement2 = "UPDATE pruebas SET Finalizada = 'Y',Aprobada ='N',Abortada='N',usuario_for = ?,Fecha_final = ?,serialEquipo = ? ,observaciones=?,Comentario_aborto = ? WHERE pruebas.Id_Pruebas = ?";
                //statement2 = "UPDATE pruebas SET Finalizada = 'Y',Aprobada ='N',Abortada='N',usuario_for = ?,Fecha_prueba = ?,serialEquipo = ? ,observaciones=?,Comentario_aborto = ? WHERE pruebas.Id_Pruebas = ?";
            }
            PreparedStatement ps = cn.prepareStatement(statement2);
            ps.setLong(1, idUsuario);
            ps.setTimestamp(2, new java.sql.Timestamp((new Date()).getTime()));
            ps.setString(3, serialEquipo);
            ps.setString(4, strObservacion);
            if (strObservacion.equalsIgnoreCase("LA DIFERENCIA  ARITMETICA ENTRE EL MAYOR Y EL MENOR DE OPACIDAD ES MAYOR AL 0,5 m^-1 EN LAS TRES ACELERACIONES; (Norma 4231 numeral 3.2.4 b) ")) {
                ps.setString(5, "");
            } else {
                ps.setString(5, "Condiciones Anormales");
            }
            ps.setLong(6, idPrueba);
            ps.execute();
        } else {
            try {
                statement2 = "UPDATE pruebas SET Autorizada = 'A',usuario_for = ?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?";
                PreparedStatement instruccion2 = cn.prepareStatement(statement2);
                instruccion2.setLong(1, idUsuario);
                instruccion2.setString(2, serialEquipo);
                instruccion2.setLong(3, idPrueba);
                instruccion2.setString(4, "Condiciones Anormales");
                int executeUpdate = instruccion2.executeUpdate();

            } catch (Exception ex) {
                Logger.getLogger(RegistrarMedidas.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void registrarPruebaSoloConObservacion(Connection cn, long idUsuario, Long idPrueba, String ipEquipo, String strObservacion) throws SQLException {
        String serialEquipo = "";
        System.out.println("--------------------------REGISTRANDO EN registrarPruebaSoloConObservacion---------------------------------");
        try {
            serialEquipo = ConsultarDatosVehiculo.buscarSerialEquipo(idPrueba);
            System.out.println(" LECTURA SERIAL");
        } catch (Exception e) {
            serialEquipo = "Serial no encontrado";
            e.printStackTrace();
        }

        String statement2;
        try {
            cn.setAutoCommit(false);
            statement2 = "UPDATE pruebas SET Finalizada = 'Y',Aprobada ='N',Abortada='N',usuario_for = ?,serialEquipo = ?,observaciones=? WHERE pruebas.Id_Pruebas = ?";
            PreparedStatement instruccion2 = cn.prepareStatement(statement2);
            instruccion2.setLong(1, idUsuario);
            instruccion2.setString(2, serialEquipo);
            instruccion2.setString(3, strObservacion);
            instruccion2.setLong(4, idPrueba);
            int executeUpdate = instruccion2.executeUpdate();
            cn.setAutoCommit(true);

        } catch (Exception ex) {
            Logger.getLogger(RegistrarMedidas.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void registrarCancelacion(int idTipoAborto, long idUsuario, Long idPrueba) throws SQLException, ClassNotFoundException {

        //Insertar las medidas dependiendo del numero de tiempos del motor
        Connection conexion = getConnection();
//        if (comentario.equals("RPMS FUERA DE RANGO O INESTABLES")) {
//            registrarRPMSFueraRango((int) idUsuario, idPrueba, idPrueba, conexion);
//        } else {
        String serialEquipo = "";
        try {
            serialEquipo = ConsultarDatosVehiculo.buscarSerialEquipo(idPrueba);
        } catch (Exception e) {
            serialEquipo = "Serial no encontrado";
            e.printStackTrace();
        }
        String statement = "UPDATE pruebas SET Finalizada = 'Y',Aprobada='N',Abortada='Y',id_tipo_aborto=?,usuario_for = ?,Fecha_aborto=?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?";
        PreparedStatement instruccion = conexion.prepareStatement(statement);
        instruccion.setInt(1, idTipoAborto);
        instruccion.setLong(2, idUsuario);
        Date d = new Date();
        Calendar c = new GregorianCalendar();
        c.setTime(d);
        instruccion.setTimestamp(3, new java.sql.Timestamp(c.getTimeInMillis()));
        instruccion.setString(4, serialEquipo);
        instruccion.setLong(5, idPrueba);
        instruccion.executeUpdate();

    }//end of method registrarCancelacion

    public void registrarCancelacion(String idTipoAborto, String observacion, long idUsuario, Long idPrueba) throws SQLException, ClassNotFoundException {

        Connection conexion = getConnection();
//        if (comentario.equals("RPMS FUERA DE RANGO O INESTABLES")) {
//            registrarRPMSFueraRango((int) idUsuario, idPrueba, idPrueba, conexion);
//        } else {
        String serialEquipo = "";
        try {
            serialEquipo = ConsultarDatosVehiculo.buscarSerialEquipo(idPrueba);
        } catch (Exception e) {
            serialEquipo = "Serial no encontrado";
            e.printStackTrace();
        }
        String statement = "UPDATE pruebas SET Finalizada = 'N',Aprobada='N',Abortada='Y',Comentario_aborto=?,observaciones=?,usuario_for = ?,serialEquipo = ?,Fecha_final = ? WHERE pruebas.Id_Pruebas = ?";
        PreparedStatement instruccion = conexion.prepareStatement(statement);
        instruccion.setString(1, idTipoAborto);
        instruccion.setString(2, observacion);
        instruccion.setLong(3, idUsuario);
        instruccion.setString(4, serialEquipo);
        Date d = new Date();
        Calendar c = new GregorianCalendar();
        c.setTime(d);
        instruccion.setTimestamp(5, new java.sql.Timestamp(c.getTimeInMillis()));
        instruccion.setLong(6, idPrueba);
        instruccion.executeUpdate();

//        }
    }//end of method registrarCancelacion

    public void registraRechazoCondiciones(String idTipoAborto, long idUsuario, Long idPrueba) throws SQLException, ClassNotFoundException {
        Connection conexion = getConnection();
        String serialEquipo = "";
        try {
            serialEquipo = ConsultarDatosVehiculo.buscarSerialEquipo(idPrueba);
        } catch (Exception e) {
            serialEquipo = "Serial no encontrado";
            e.printStackTrace();
        }
        String statement = "UPDATE pruebas SET Finalizada = 'Y',Aprobada='N',Abortada='N',Comentario_aborto=?,usuario_for = ?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?";
        PreparedStatement instruccion = conexion.prepareStatement(statement);
        if (idTipoAborto.equalsIgnoreCase("DILUCION DE MUESTRA")) {

        } else {
            instruccion.setString(1, idTipoAborto);
        }
        instruccion.setLong(2, idUsuario);
        Date d = new Date();
        Calendar c = new GregorianCalendar();
        c.setTime(d);
        instruccion.setString(3, serialEquipo);
        instruccion.setLong(4, idPrueba);
        instruccion.executeUpdate();
    }//end of method registrarRechazo

    public void registraRechazoRpm(String idTipoAborto, String observacion, long idUsuario, Long idPrueba, int nroDefecto) throws SQLException, ClassNotFoundException {
        System.out.println("idPrueba:" + idPrueba);
        Connection conexion = getConnection();
        String serialEquipo = "";
        try {
            serialEquipo = ConsultarDatosVehiculo.buscarSerialEquipo(idPrueba);
        } catch (Exception e) {
            serialEquipo = "Serial no encontrado";
            e.printStackTrace();
        }
        String statement = "UPDATE pruebas SET Finalizada = 'Y',Aprobada='N',Abortada='N',Comentario_aborto=?,observaciones=?,usuario_for = ?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?";
        PreparedStatement instruccion = conexion.prepareStatement(statement);
        if (idTipoAborto.equalsIgnoreCase("DILUCION DE MUESTRA")) {

        } else {
            instruccion.setString(1, idTipoAborto);
        }

        if (!idTipoAborto.equalsIgnoreCase("Condiciones Anormales")) {
            if (CallableInicioMotos.lecturaCondicionesAnormales.length() > 2) {
                instruccion.setString(2, observacion);
            } else {
                instruccion.setString(2, observacion);
            }
            instruccion.setLong(3, idUsuario);
            Date d = new Date();
            Calendar c = new GregorianCalendar();
            c.setTime(d);
            instruccion.setString(4, serialEquipo);
            instruccion.setLong(5, idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();
        } else {
            /*  String plel= idTipoAborto.concat(".-").concat(CallableInicioMotos.lecturaCondicionesAnormales);
                instruccion.setString(2, idTipoAborto.concat(".-").concat(CallableInicioMotos.lecturaCondicionesAnormales));
                instruccion.setString(1, "Condiciones Anormales");        
            instruccion.setString(1, idTipoAborto);
            instruccion.setString(2, idTipoAborto);*/
        }

//        instruccion.clearParameters();
//        statement = "INSERT INTO defxprueba(id_defecto,id_prueba) VALUES(?,?)";
//        instruccion = conexion.prepareStatement(statement);
//        instruccion.setInt(1, nroDefecto);
//        instruccion.setLong(2, idPrueba);
//        instruccion.executeUpdate();
        String consultaExistencia = "SELECT COUNT(*) FROM defxprueba WHERE id_defecto = ? AND id_prueba = ?";
        PreparedStatement consultaExistenciaInstruccion = conexion.prepareStatement(consultaExistencia);
        consultaExistenciaInstruccion.setInt(1, nroDefecto);
        consultaExistenciaInstruccion.setLong(2, idPrueba);

        ResultSet resultadoExistencia = consultaExistenciaInstruccion.executeQuery();
        resultadoExistencia.next();

        int cantidadFilas = resultadoExistencia.getInt(1);

        if (cantidadFilas > 0) {
            // El defecto ya existe, realiza aquí la lógica necesaria (puedes lanzar una excepción, mostrar un mensaje, etc.)
            System.out.println("El defecto ya existe.");
        } else {
            // El defecto no existe, procede con la inserción
            instruccion.clearParameters();
            String statementt = "INSERT INTO defxprueba(id_defecto, id_prueba) VALUES (?, ?)";
            instruccion = conexion.prepareStatement(statementt);
            instruccion.setInt(1, nroDefecto);
            instruccion.setLong(2, idPrueba);
            instruccion.executeUpdate();
            System.out.println("Inserción exitosa.");
        }
    }//end of method registrarRechazo

    public void registraRechazo(String idTipoAborto, String observacion, long idUsuario, Long idPrueba, int nroDefecto) throws SQLException, ClassNotFoundException {
        System.out.println("idPrueba:" + idPrueba);
        Connection conexion = getConnection();
        String serialEquipo = "";
        try {
            serialEquipo = ConsultarDatosVehiculo.buscarSerialEquipo(idPrueba);
        } catch (Exception e) {
            serialEquipo = "Serial no encontrado";
            e.printStackTrace();
        }
        String statement = "UPDATE pruebas SET Finalizada = 'Y',Aprobada='N',Abortada='N',Comentario_aborto=?,observaciones=?,usuario_for = ?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?";
        PreparedStatement instruccion = conexion.prepareStatement(statement);
        if (idTipoAborto.equalsIgnoreCase("DILUCION DE MUESTRA")) {

        } else {
            instruccion.setString(1, idTipoAborto);
        }

        if (!idTipoAborto.equalsIgnoreCase("Condiciones Anormales")) {
            if (CallableInicioMotos.lecturaCondicionesAnormales.length() > 2) {
                instruccion.setString(2, idTipoAborto.concat("; Condiciones Anormales Encotradas .-").concat(CallableInicioMotos.lecturaCondicionesAnormales));
            } else {
                instruccion.setString(2, observacion);
            }
            instruccion.setLong(3, idUsuario);
            Date d = new Date();
            Calendar c = new GregorianCalendar();
            c.setTime(d);
            instruccion.setString(4, serialEquipo);
            instruccion.setLong(5, idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();
        } else {
             String plel= idTipoAborto.concat(".-").concat(CallableInicioMotos.lecturaCondicionesAnormales);
                instruccion.setString(2, idTipoAborto.concat(".-").concat(CallableInicioMotos.lecturaCondicionesAnormales));
                instruccion.setString(1, "Condiciones Anormales");        
            instruccion.setString(1, idTipoAborto);
            instruccion.setString(2, idTipoAborto);
        }
        instruccion.clearParameters();
        statement = "INSERT INTO defxprueba(id_defecto,id_prueba) VALUES(?,?)";
        instruccion = conexion.prepareStatement(statement);
        instruccion.setInt(1, nroDefecto);
        instruccion.setLong(2, idPrueba);
        instruccion.executeUpdate();
    }//end of method registrarRechazo

    public void cargarPermisibles() {

    }

    public void registrarDefectos(Set<Integer> conjuntoDefectos, Long idPrueba, long idUsuario) {
        Connection cn = null;
        try {
            cn = getConnection();
            String statement = "INSERT INTO defxprueba(id_defecto,id_prueba) VALUES(?,?)";
            PreparedStatement instruccion = cn.prepareStatement(statement);
            for (Integer def : conjuntoDefectos) {
                instruccion.clearParameters();
                instruccion.setInt(1, def);
                instruccion.setLong(2, idPrueba);
                instruccion.executeUpdate();
            }
        } catch (ClassNotFoundException | SQLException cexc) {
            Mensajes.mostrarExcepcion(cexc);
        } finally {
            try {
                if (cn != null) {
                    cn.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    public void registrarDefectosDiesel(Long idPrueba, long idUsuario) {
        Connection cn = null;
        try {
            cn = getConnection();

            String statement = "INSERT INTO defxprueba(id_defecto,id_prueba) VALUES(?,?)";
            PreparedStatement instruccion = cn.prepareStatement(statement);

            instruccion.clearParameters();
            instruccion.setInt(1, 80000);
            instruccion.setLong(2, idPrueba);
            instruccion.executeUpdate();
            CallablePruebaMotos.condicionTemperatura = 1;

            cn.setAutoCommit(false);
            String serialEquipo = "";
            try {
                serialEquipo = ConsultarDatosVehiculo.buscarSerialEquipo(idPrueba);

            } catch (Exception e) {
                serialEquipo = "Serial no encontrado";
                e.printStackTrace();
            }
            String strFinalizarPrueba = "UPDATE pruebas SET Finalizada = 'Y',Aprobada='N',Abortada='N',usuario_for = ?,serialEquipo = ?,observaciones = ?,Comentario_aborto = ? WHERE pruebas.Id_Pruebas = ?";
            PreparedStatement psFinalizar = cn.prepareStatement(strFinalizarPrueba);

            psFinalizar.setLong(1, idUsuario);
            psFinalizar.setString(2, serialEquipo);
            if (this.mensajeDefe.equals("Falla Subita del Motor y /o sus Accesosrios")) {
                psFinalizar.setString(3, "Condiciones Anormales Encotradas .-" + "2.1.1.1.13 " + this.mensajeDefe);
            } else {
                psFinalizar.setString(3, "Condiciones Anormales Encotradas .-" + this.mensajeDefe);
            }

            psFinalizar.setString(4, "Condiciones Anormales");
            psFinalizar.setLong(5, idPrueba);

            int n = psFinalizar.executeUpdate();

            cn.commit();
            cn.setAutoCommit(true);

            System.out.println("Registro :" + this.mensajeDefe);
        } catch (ClassNotFoundException | SQLException cexc) {
            Mensajes.mostrarExcepcion(cexc);
        } finally {
            try {
                if (cn != null) {
                    cn.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    public void registrarDefectosVisuales(Set<Integer> conjuntoDefectos, Long idPrueba, long idUsuario) {
        Connection cn = null;
        try {
            cn = getConnection();

            /* String statement = "INSERT INTO defxprueba(id_defecto,id_prueba) VALUES(?,?)";
            PreparedStatement instruccion = cn.prepareStatement(statement);
           
            instruccion.clearParameters();
            instruccion.setInt(1, 80000);
            instruccion.setLong(2, idPrueba);
            instruccion.executeUpdate();*/
            CallablePruebaMotos.condicionTemperatura = 1;

            //cn.setAutoCommit(false);
            String serialEquipo = "";
            try {
                serialEquipo = ConsultarDatosVehiculo.buscarSerialEquipo(idPrueba);

            } catch (Exception e) {
                serialEquipo = "Serial no encontrado";
                e.printStackTrace();
            }
            String strFinalizarPrueba = "UPDATE pruebas SET Finalizada = 'Y',Aprobada='N',Abortada='N',usuario_for = ?,serialEquipo = ?,observaciones = ?,Comentario_aborto = ? WHERE pruebas.Id_Pruebas = ?";
            PreparedStatement psFinalizar = cn.prepareStatement(strFinalizarPrueba);

            psFinalizar.setLong(1, idUsuario);

            psFinalizar.setString(2, serialEquipo);

            psFinalizar.setString(3, "Condiciones Anormales Encotradas .-" + this.mensajeDefe);

            psFinalizar.setString(4, "Condiciones Anormales");
            psFinalizar.setLong(5, idPrueba);

            int n = psFinalizar.executeUpdate();
            // --- registrar defectos visual
            conjuntoDefectos.clear();
            conjuntoDefectos.add(84018);
            this.registrarDefectos(conjuntoDefectos, idPrueba, idUsuario);

            //cn.commit();
            //cn.setAutoCommit(true);
            System.out.println("Registro :" + this.mensajeDefe);
        } catch (ClassNotFoundException | SQLException cexc) {
            Mensajes.mostrarExcepcion(cexc);
        } finally {
            try {
                if (cn != null) {
                    cn.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    public InfoVehiculo cargarInfoVehiculo(long idPrueba, Connection conexion) throws SQLException {

        //debe traer los parametros del vehiculo y retornar un objeto del tipo
        //InfoVehiculo
        String statement = "SELECT v.Modelo,v.CARPLATE,v.Tiempos_motor,v.CARTYPE,v.FUELTYPE,v.Numero_exostos, v.diseño, hp.TESTSHEET FROM pruebas as p "
                + "INNER JOIN hoja_pruebas as hp ON p.hoja_pruebas_for = hp.TESTSHEET "
                + "INNER JOIN vehiculos as v ON hp.vehiculo_for = v.CAR WHERE p.Id_Pruebas = ?";
        ResultSet rs = null;
        try {
            PreparedStatement instruccion = conexion.prepareStatement(statement);
            instruccion.setLong(1, idPrueba);
            rs = instruccion.executeQuery();
        } catch (SQLException e) {
            int i = 0;

        }
        if (rs.first()) {
            int modelo = rs.getInt("Modelo");
            String placa = rs.getString("CARPLATE");
            int tiemposMotor = rs.getInt("Tiempos_motor");
            int tipoCarro = rs.getInt("CARTYPE");
            int tipoCombustible = rs.getInt("FUELTYPE");
            int numeroExostos = rs.getInt("Numero_exostos");
            String diseño = rs.getString("diseño");
            InfoVehiculo info = new InfoVehiculo(tiemposMotor, modelo, tipoCombustible, tipoCarro, placa);
            info.setNumeroExostos(numeroExostos);
            info.setDiseño(diseño);

            return info;
        } else {
            return null;
        }

    }//end of method cargarInfoVehiculo

    public void actualizarInfoVehiculo(InfoVehiculo infoVehiculo, String numeroPrueba, Connection cn) throws SQLException {

        //conseguir el vehiculo al que se le esta haciendo la prueba
        /*  String consultaIdVehiculo = "SELECT v.CAR FROM vehiculos as v inner join "
                + " hoja_pruebas as hp on v.CAR = hp.Vehiculo_for "
                + " inner join pruebas as p on p.hoja_pruebas_for = hp.TESTSHEET "
                + " where p.Id_Pruebas = ?";
        PreparedStatement psIdVehiculo = cn.prepareStatement(consultaIdVehiculo);
        psIdVehiculo.setLong(1, numeroPrueba);
        ResultSet rsIdVehiculo = psIdVehiculo.executeQuery();
        int idVehiculo = -1;
        if (rsIdVehiculo.next()) {
            idVehiculo = rsIdVehiculo.getInt(1);
        }*/
        String strActualizacion = "UPDATE vehiculos SET Tiempos_motor=?,Numero_exostos=?, diseño=?,catalizador=? WHERE  "
                + "vehiculos.CARPLATE = ?";

        PreparedStatement ps = cn.prepareStatement(strActualizacion);

        ps.setInt(1, infoVehiculo.getNumeroTiempos());
        ps.setInt(2, infoVehiculo.getNumeroExostos());
        ps.setString(3, infoVehiculo.getDiseño());
        ps.setString(4, infoVehiculo.getCatalizador());
        ps.setString(5, numeroPrueba);
        ps.executeUpdate();
        ps.clearParameters();
        strActualizacion = "UPDATE lineas_vehiculos SET rpm_ini=?,rpm_fin=? WHERE  " + "lineas_vehiculos.CARLINE= ?";
        ps = cn.prepareStatement(strActualizacion);
        ps.setInt(1, infoVehiculo.getRpmIni());
        ps.setInt(2, infoVehiculo.getRpmFin());
        ps.setInt(3, infoVehiculo.getNroLinea());
        ps.executeUpdate();
    }

    public void actualizarLivianoLineas(InfoVehiculo infoVehiculo, String numeroPrueba, Connection cn) throws SQLException {
        String strActualizacion = "UPDATE lineas_vehiculos SET rpm_ini=?,rpm_fin=? WHERE  " + "lineas_vehiculos.CARLINE= ?";
        PreparedStatement ps = cn.prepareStatement(strActualizacion);
        ps = cn.prepareStatement(strActualizacion);
        ps.setInt(1, infoVehiculo.getRpmIni());
        ps.setInt(2, infoVehiculo.getRpmFin());
        ps.setInt(3, infoVehiculo.getNroLinea());
        ps.executeUpdate();
    }

    public InfoVehiculo findInfoVehiculo(InfoVehiculo infoVehiculo, String numeroPrueba, Connection cn) throws SQLException {

        //conseguir el vehiculo al que se le esta haciendo la prueba
        String consultaIdVehiculo = "SELECT v.Numero_exostos,v.Tiempos_motor,v.diseño,v.CARLINE FROM vehiculos as v inner join "
                + " hoja_pruebas as hp on v.CAR = hp.Vehiculo_for "
                + " inner join pruebas as p on p.hoja_pruebas_for = hp.TESTSHEET "
                + " where v.CARPLATE = ?";
        PreparedStatement psIdVehiculo = cn.prepareStatement(consultaIdVehiculo);
        psIdVehiculo.setString(1, numeroPrueba);
        ResultSet rsIdVehiculo = psIdVehiculo.executeQuery();
        if (rsIdVehiculo.next()) {
            infoVehiculo.setNumeroExostos(rsIdVehiculo.getInt(1));
            infoVehiculo.setNumeroTiempos(rsIdVehiculo.getInt(2));
            infoVehiculo.setDiseño(rsIdVehiculo.getString(3));
            infoVehiculo.setNroLinea(rsIdVehiculo.getInt(4));
        }
        psIdVehiculo.clearParameters();
        consultaIdVehiculo = "SELECT rpm_ini,rpm_fin FROM lineas_vehiculos as l  " + " where l.CARLINE = ?";
        psIdVehiculo = cn.prepareStatement(consultaIdVehiculo);
        psIdVehiculo.setInt(1, infoVehiculo.getNroLinea());
        rsIdVehiculo = psIdVehiculo.executeQuery();
        if (rsIdVehiculo.next()) {
            infoVehiculo.setRpmIni(rsIdVehiculo.getInt(1));
            infoVehiculo.setRpmFin(rsIdVehiculo.getInt(2));
        }
        return infoVehiculo;
    }

    public void registrarTemperaturInicio(double temp, long idPrueba) throws SQLException {
        try {
            System.out.println("estoy en temperatura inicio");
            Connection con = getConnection();
            String agregarTem = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
            PreparedStatement ps = con.prepareStatement(agregarTem);
            ps.clearParameters();
            ps.setInt(1, 8031);
            System.out.println("REG MEDIA ().-* " + 8031 + " CON UN VALOR: " + temp);
            ps.setDouble(2, temp);
            ps.setLong(3, idPrueba);
            ps.executeUpdate();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(RegistrarMedidas.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void registrarHumedadInicio(double humedad, long idPrueba) throws SQLException {

        try {
            System.out.println("estoy en temperatura inicio");
            Connection con = getConnection();
            String agregarTem = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
            PreparedStatement ps = con.prepareStatement(agregarTem);
            ps.clearParameters();
            ps.setInt(1, 8032);
            System.out.println("REG MEDIA ().-* " + 8031 + " CON UN VALOR: " + humedad);
            ps.setDouble(2, humedad);

            ps.setLong(3, idPrueba);
            ps.execute();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(RegistrarMedidas.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void registrarTemperaturaMotor(int tempAceite, long idPrueba) throws SQLException {

        try {
            System.out.println("estoy en temperatura inicio");
            Connection con = getConnection();
            String agregarTem = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
            PreparedStatement ps = con.prepareStatement(agregarTem);
            ps.clearParameters();
            ps.setInt(1, 8006);
            System.out.println("REG MEDIA ().-* " + 8006 + " CON UN VALOR: " + tempAceite);
            ps.setInt(2, tempAceite);

            ps.setLong(3, idPrueba);
            ps.execute();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(RegistrarMedidas.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Metodo que registra la dilucion de muestra en la prueba de gasolina
     *
     * @param idPrueba
     * @param idUsuario
     * @param conexion
     */
    public static void main(String[] args) throws Exception {
        RegistrarMedidas reg = new RegistrarMedidas();
        Connection cn = reg.getConnection();
//        reg.registrarCancelacion("", 1, 110581);
    }

    public void registrarTempFueraRango(int idUsuario, long idPrueba, long idHojaPrueba, Connection cn) throws SQLException {
        String serialEquipo = "";
        try {
            serialEquipo = ConsultarDatosVehiculo.buscarSerialEquipo(idPrueba);
        } catch (Exception e) {
            serialEquipo = "Serial no encontrado";
            e.printStackTrace();
        }

        String strTemp = "UPDATE pruebas SET Finalizada = 'Y',Aprobada='N',Abortada='Y',usuario_for = ?,Comentario_aborto=?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?";
        PreparedStatement ps = cn.prepareStatement(strTemp);
        ps.setInt(1, idUsuario);
        ps.setString(2, "TEMPERATURA FUERA DE RANGO");
        ps.setString(3, serialEquipo);
        ps.setLong(4, idPrueba);
        ps.executeUpdate();

    }

    public void setMensaDefe(String msg) {
        this.mensajeDefe = msg;
    }

    public String getMensaDefe() {
        return this.mensajeDefe;
    }

}//end of class RegistrarMedidas

