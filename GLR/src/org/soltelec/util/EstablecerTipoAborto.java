package org.soltelec.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import org.soltelec.models.entities.TipoAborto;

/**
 * Clase encargada de mostrar modal con los tipos de aborto que pueden haber en
 * la aplicacion
 *
 * @fecha 18/07/2015
 * @author Luis Berna
 */
public class EstablecerTipoAborto {

    public static int retornarCodigoDefecto() {
        Connection conn = null;
        List<TipoAborto> abortos = new ArrayList<>();
        try {
            conn = Conex.getConnection();
            PreparedStatement pst = conn.prepareStatement("SELECT * FROM tipo_aborto");
            ResultSet result = pst.executeQuery();

            while (result.next()) {
                abortos.add(new TipoAborto(result.getInt("id"), result.getString("descripcion"), result.getInt("codigo")));
            }

            for (TipoAborto tipoAborto : abortos) {
                System.out.println(tipoAborto);
            }

            TipoAborto objSeleccion = (TipoAborto) JOptionPane.showInputDialog(
                    null,
                    "Motivo de la cancelación",
                    "Cancelacion",
                    JOptionPane.QUESTION_MESSAGE,
                    null, // null para icono defecto
                    abortos.toArray(),
                    "Causa de la cancelación");

            return objSeleccion.getCodigo();

        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e.getMessage());
            return -1;
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                System.out.println(ex);
            }
        }

    }
}
