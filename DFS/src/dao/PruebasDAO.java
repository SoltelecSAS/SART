/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

//import com.soltelec.estandar.EstadoEventosSicov;
import com.soltelec.loginadministrador.UtilPropiedades;
import com.soltelec.modulopuc.configuracion.modelo.Conexion;
import com.soltelec.modulopuc.persistencia.conexion.DBUtil;
import com.soltelec.modulopuc.utilidades.Mensajes;
import excepciones.NoPersistException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import modelo.Pruebas;

/**
 *
 * @author GerenciaDesarrollo
 */
public class PruebasDAO {

    public void finalizarPrueba(int idPrueba, int idUsuario, String aprobada, Boolean escTran, String ipEquipo) throws NoPersistException {
        String serialEquipo = "";
        try {
            serialEquipo = buscarSerialEquipo(idPrueba);
        } catch (Exception e) {
            serialEquipo = "Serial no encontrado";
        }
        PruebaDefaultDAO.serialEquipo=serialEquipo;
        if (escTran == true) {
            String[] campos = {Pruebas.APROBADA, Pruebas.ID_USUARIO, Pruebas.FINALIZADA, Pruebas.SERIAL_EQUIPO};
            String[] valores = {aprobada, String.valueOf(idUsuario), "Y", serialEquipo};
            String condicion = Pruebas.ID_PRUEBAS + " = " + idPrueba;
            try {
                DBUtil.update(Pruebas.TABLA, campos, valores, condicion);
            } catch (SQLException | ClassNotFoundException ex) {
                System.out.println("Error: finalizarPrueba - (PruebasDAO)");
            }
        } else {
            try {
                //Integer idTr = DBUtil.execIndTrama();
                String[] campos = {Pruebas.ID_USUARIO, Pruebas.AUTORIZADA, Pruebas.SERIAL_EQUIPO};
                String[] valores = {String.valueOf(idUsuario), "A", serialEquipo};
                String condicion = Pruebas.ID_PRUEBAS + " = " + idPrueba;
                DBUtil.update(Pruebas.TABLA, campos, valores, condicion);
            } catch (SQLException | ClassNotFoundException ex) {
                System.out.println("Error: finalizarPrueba - (PruebasDAO)");
            }
        }
    }

    public void cancelarPrueba(String comentario, int idPrueba, int idUsuario, Date fechaAborto) throws ClassNotFoundException, SQLException {
        String serialEquipo = "";
        try {
            serialEquipo = buscarSerialEquipo(idPrueba);
        } catch (Exception e) {
            serialEquipo = "Serial no encontrado";
        }

        String[] campos = {Pruebas.FINALIZADA, Pruebas.APROBADA, Pruebas.AUTORIZADA, Pruebas.ABORTADA, Pruebas.observaciones,
            Pruebas.ID_USUARIO, Pruebas.FECHA_ABORTO, Pruebas.SERIAL_EQUIPO};
        String[] valores = {"Y", "N", "N", "Y", comentario, String.valueOf(idUsuario), String.valueOf(fechaAborto), serialEquipo};
        String condicion = Pruebas.ID_PRUEBAS + " = " + idPrueba;
        System.out.println("Script Cancelar Prueba - Campos: " + Arrays.toString(campos) + "Valores" + Arrays.toString(valores) + "Condicion" + condicion);
        DBUtil.update(Pruebas.TABLA, campos, valores, condicion);

    }

    public void guardarDefectos(List<Integer> defectos, int idPrueba) throws NoPersistException {
        String tabla = "defxprueba";
        String campos = "(id_defecto, id_prueba)";
        String valores = "";

        for (Integer defecto : defectos) {
            valores += "('" + defecto + "','" + idPrueba + "'), ";
        }
        System.out.println("valor del arreglo valores: "+ valores);

        valores = valores.substring(0, valores.length() - 2);
        System.out.println("ya arme defectos voy a entrar ");
        try {
            DBUtil.insert(tabla, campos, valores);
        } catch (SQLException ex) {
            System.out.println("Error: guardarDefectos - (PruebasDAO)");
            //throw new NoPersistException(ex);
        } catch (ClassNotFoundException ex) {
            System.out.println("Error: guardarDefectos - (PruebasDAO)");
            ex.printStackTrace(System.err);
            throw new NoPersistException(ex);
        }
    }

    /**
     * Consulta el serial de una prueba
     *
     * @param idPrueba identificador de la prueba
     * @return
     * @throws java.sql.SQLException
     * @throws java.lang.ClassNotFoundException
     */
    public static String buscarSerialEquipo(long idPrueba) throws SQLException, ClassNotFoundException, IOException 
    {
        Connection conn = DriverManager.getConnection(Conexion.getUrl(), Conexion.getUsuario(), Conexion.getContraseña());
        Long idHojaPrueba = 0L;
        Long idTipoCombustible = 0L;
        Long idTipoPrueba = 0L;
        String nombreCombustible = "";
        String nombreTipoPrueba = "";
        String codigoEquipo = "";
        String serialEquipo = "";
        String str2 = "SELECT p.hoja_pruebas_for, v.FUELTYPE, tg.Nombre_gasolina,p.Tipo_prueba_for, tp.Nombre_tipo_prueba\n"
                + "FROM pruebas p\n"
                + "INNER JOIN hoja_pruebas hp ON p.hoja_pruebas_for = hp.TESTSHEET\n"
                + "INNER JOIN vehiculos v ON hp.Vehiculo_for = v.CAR\n"
                + "INNER JOIN tipo_prueba tp ON p.Tipo_prueba_for = tp.TESTTYPE\n"
                + "INNER JOIN tipos_gasolina tg ON v.`FUELTYPE` = tg.FUELTYPE\n"
                + "WHERE p.Id_Pruebas = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(str2);
        preparedStatement.setLong(1, idPrueba);
        ResultSet data = preparedStatement.executeQuery();
        while (data.next()) 
        {
            idHojaPrueba = data.getLong(1);
            idTipoCombustible = data.getLong(2);
            nombreCombustible = data.getString(3);
            idTipoPrueba = data.getLong(4);
            nombreTipoPrueba = data.getString(5);
        }
        
        System.out.println("nombre prop para recuperar serial: "+nombreTipoPrueba.toUpperCase());
        if (idTipoPrueba == 8 && nombreCombustible.equals("DIESEL")) {
            codigoEquipo = UtilPropiedades.cargarPropiedad("OPACIMETRO", "equipos.properties");
        } else {
            codigoEquipo = UtilPropiedades.cargarPropiedad(nombreTipoPrueba.toUpperCase(), "equipos.properties");
        }

        str2 = "select e.serialresolucion from equipos e where e.id_equipo = ?";
        preparedStatement = conn.prepareStatement(str2);
        preparedStatement.setLong(1, Long.parseLong(codigoEquipo));
        data = preparedStatement.executeQuery();
        while (data.next())
        {
            serialEquipo = data.getString(1);
        }
        return serialEquipo;
    }

}
