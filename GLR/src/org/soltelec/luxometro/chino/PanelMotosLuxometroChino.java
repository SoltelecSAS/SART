/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.luxometro.chino;

//import com.soltelec.integrador.cliente.ClienteSicov;
//import com.soltelec.estandar.EstadoEventosSicov;
import com.soltelec.modulopuc.utilidades.Mensajes;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
import org.soltelec.componenteluces.LuzAlta;
import org.soltelec.componenteluces.LuzBaja;
import org.soltelec.util.ConsultarDatosVehiculo;
import org.soltelec.util.UtilConexion;

/**
 *
 * @author User
 */
public class PanelMotosLuxometroChino extends JPanel implements ActionListener {

    private LuzBaja luzBaja;
    private LuzAlta luzAlta;

    private JButton buttonMedirBaja;
    private JButton buttonMedirAlta;
    private MedidaLuxometroChino medidaLuzBaja;
    private MedidaLuxometroChino medidaLuzAlta;

    private JButton buttonFinalizar;
    private JProgressBar barra;

    private LuxometroChino luxometro;

    private JLabel labelTitulo;

    private int idPrueba, idUsuario;

    public PanelMotosLuxometroChino() {
        crearComponentes();
        ponerComponentes();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == buttonMedirBaja) {

            buttonMedirBaja.setEnabled(false);
            buttonMedirAlta.setEnabled(true);
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    try {
                        medidaLuzBaja = obtenerMedidaLuxometro();
                    } catch (InterruptedException ex) {
                        Logger.getRootLogger().error(ex);
                    } catch (ExecutionException ex) {
                        Logger.getRootLogger().error("Error en obtenerMedidaLuxometro motos", ex);
                    }
                }
            });

        } else if (e.getSource() == buttonMedirAlta) {

            buttonMedirAlta.setEnabled(false);
            buttonFinalizar.setEnabled(true);
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    try {
                        medidaLuzAlta = obtenerMedidaLuxometro();
                    } catch (InterruptedException ex) {
                        Logger.getRootLogger().error(ex);
                    } catch (ExecutionException ex) {
                        Logger.getRootLogger().error("Error en obtenerMeddiaLuxometro motos", ex);
                    }

                }
            });

        } else if (e.getSource() == buttonFinalizar) {

            registrarMedidas(idPrueba, idUsuario, 1);
            (SwingUtilities.getWindowAncestor(this)).dispose();

        }

    }//end of method actionPerformed

    private void crearComponentes() {

        luzBaja = new LuzBaja();

        luzAlta = new LuzAlta();

        buttonMedirBaja = new JButton("Medir Luz Baja");
        buttonMedirAlta = new JButton("Medir Luz Alta");
        buttonMedirAlta.setEnabled(false);
        buttonFinalizar = new JButton("Finalizar");
        buttonFinalizar.setEnabled(false);

        labelTitulo = new JLabel("PRUEBA DE LUCES PARA MOTOCICLETAS");
        barra = new JProgressBar(0, 5);

    }

    private void ponerComponentes() {

        JPanel panelLuzBaja = new JPanel();
        panelLuzBaja.setPreferredSize(new Dimension(200, 200));
        BoxLayout box = new BoxLayout(panelLuzBaja, BoxLayout.Y_AXIS);
        panelLuzBaja.setLayout(box);
        panelLuzBaja.add(luzBaja);
        panelLuzBaja.add(buttonMedirBaja);

        JPanel panelLuzAlta = new JPanel();
        panelLuzAlta.setPreferredSize(new Dimension(200, 200));
        BoxLayout boxAlta = new BoxLayout(panelLuzAlta, BoxLayout.Y_AXIS);
        panelLuzAlta.setLayout(boxAlta);
        panelLuzAlta.add(luzAlta);
        panelLuzAlta.add(buttonMedirAlta);

        //CREAR EL LAYOUT GENERAL
        GridBagLayout bag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        Insets insets = new Insets(5, 10, 5, 10);
        c.insets = insets;
        this.setLayout(bag);

        filaColumna(1, 1, c);
        this.add(labelTitulo, c);

        filaColumna(2, 1, c);
        this.add(panelLuzAlta, c);

        filaColumna(3, 1, c);
        this.add(panelLuzBaja, c);

        c.anchor = GridBagConstraints.LINE_END;
        c.fill = GridBagConstraints.HORIZONTAL;
        filaColumna(4, 1, c);
        this.add(barra, c);

        c.fill = GridBagConstraints.NONE;
        filaColumna(5, 1, c);
        this.add(buttonFinalizar, c);

    }//end of method ponerComponents

    private void filaColumna(int fila, int columna, GridBagConstraints c) {
        c.gridx = columna;
        c.gridy = fila;
    }//end of filaColumna

    public static void main(String args[]) {

        JFrame app = new JFrame();
        app.setExtendedState(JFrame.MAXIMIZED_BOTH);
        PanelMotosLuxometroChino panel = new PanelMotosLuxometroChino();
        app.add(panel);
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        app.setVisible(true);

    }

    private MedidaLuxometroChino obtenerMedidaLuxometro() throws InterruptedException, ExecutionException {

        WorkerLuxometroChino worker = new WorkerLuxometroChino(luxometro);
        worker.execute();
        MedidaLuxometroChino medida = worker.get();
        return medida;

    }

    private void registrarMedidas(int idPrueba, int idUsuario, int numLuces) {
        Connection conexion = null;

        try {
            double permisibleBaja = 2.5;
            double permisibleAnguloBajo = 1.5;
            double permisibleAnguloAlto = 3.5;

            double anguloBajaDerecha = medidaLuzBaja.getPorcentajeInclinacion();
            double luzBajaDerecha = medidaLuzBaja.getPorcentajeInclinacion();
            double luzAltaDerecha = medidaLuzAlta.getLuminosidad();
            //Cargar clase de controlador de base de datos
            conexion = UtilConexion.obtenerConexion();
            conexion.setAutoCommit(false);
            //Crear objeto preparedStatement para realizar la consulta con la base de datos
//            String statementBorrar = "DELETE  FROMmedidas WHERE TEST = ?";
//            PreparedStatement instruccionBorrar = conexion.prepareStatement(statementBorrar);
//            instruccionBorrar.setInt(1, idPrueba);
//            instruccionBorrar.executeUpdate();
            String statement = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
            PreparedStatement instruccion = conexion.prepareStatement(statement);

            if (numLuces > 0) {
                instruccion.setInt(1, 2013);
                instruccion.setDouble(2, anguloBajaDerecha);
                instruccion.setInt(3, idPrueba);
                instruccion.executeUpdate();
                instruccion.clearParameters();
                instruccion.setInt(1, 2014);
                instruccion.setDouble(2, luzBajaDerecha);
                instruccion.setInt(3, idPrueba);
                instruccion.executeUpdate();
                instruccion.clearParameters();
            }

            instruccion.setInt(1, 2000);
            instruccion.setDouble(2, luzAltaDerecha);
            instruccion.setInt(3, idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();

            String str2 = "INSERT INTO defxprueba(id_defecto,id_prueba,Tipo_defecto) VALUES (?,?,'A')";
            PreparedStatement psDefectos = conexion.prepareStatement(str2);
            String str3 = "UPDATE medidas SET Condicion='*' WHERE medidas.MEASURE = ?";//Esto no hace nada
            boolean aprobada = true;
            if (luzBajaDerecha < permisibleBaja /*|| luzMenorAlta < permisibleBaja*/) {
                psDefectos.clearParameters();
                psDefectos.setInt(1, 24005);
                psDefectos.setInt(2, idPrueba);
                psDefectos.executeUpdate();
                aprobada = false;
            }
            if (anguloBajaDerecha < permisibleAnguloBajo || anguloBajaDerecha > permisibleAnguloAlto) {
                psDefectos.clearParameters();
                psDefectos.setInt(1, 20002);//codigo defecto es distinto
                psDefectos.setInt(2, idPrueba);
                //psDefectos.setInt(3, idHojaPrueba);
                psDefectos.executeUpdate();
                aprobada = false;
            }

            String statement2;
            String serialEquipo = "";
            try {
                serialEquipo = ConsultarDatosVehiculo.buscarSerialEquipo(idPrueba);
            } catch (Exception e) {
                serialEquipo = "Serial no encontrado";
                e.printStackTrace();
            }
            if (aprobada) {
                statement2 = "UPDATE pruebas SET Finalizada = 'Y',Aprobada ='Y',Abortada='N',usuario_for = ?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?";
            } else {
                statement2 = "UPDATE pruebas SET Finalizada = 'Y',Aprobada ='N',Abortada='N',usuario_for = ?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?";
            }
            PreparedStatement instruccion2 = conexion.prepareStatement(statement2);
            instruccion2.setInt(1, idUsuario);
            instruccion2.setString(2, serialEquipo);
            instruccion2.setInt(3, idPrueba);
            int executeUpdate = instruccion2.executeUpdate();
            conexion.commit();
            conexion.setAutoCommit(true);
            conexion.close();
            Mensajes.messageDoneTime("Se ha Registrado la Prueba de Luces para la Moto de una manera Exitosa ..¡",3);
            // INICIO EVENTOS SICOV
            //ClienteSicov.eventoPruebaSicov(idPrueba, EstadoEventosSicov.FINALIZADO, "",serialEquipo);
            // FIN EVENTOS SICOV
        } catch (SQLException se) {
            org.apache.log4j.Logger.getRootLogger().error("Error registrando medidas motos luxometro chino", se);

        } catch (Exception e) {
            org.apache.log4j.Logger.getRootLogger().error("Error registrando medidas motos luxometro chino", e);
        } finally {

            try {
                if (conexion != null) {
                    conexion.close();
                }

            } catch (Exception e) {
            }

        }
    }//end of method registrar medidas

}
