/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.luxometro.chino;

//import com.soltelec.integrador.cliente.ClienteSicov;
//import com.soltelec.estandar.EstadoEventosSicov;
import com.soltelec.loginadministrador.UtilPropiedades;
import com.soltelec.modulopuc.utilidades.Mensajes;
import gnu.io.SerialPort;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
import org.soltelec.componenteluces.LuzAlta;
import org.soltelec.componenteluces.LuzBaja;
import org.soltelec.util.ConsultarDatosVehiculo;
import org.soltelec.util.PortSerialUtil;
import org.soltelec.util.UtilConexion;

/**
 *
 * @author User
 */
public class PanelLuxometroChino extends JPanel implements ActionListener {

    private JLabel labelTitulo;
    private LuzAlta altaDerecha, altaIzquierda;
    private LuzBaja bajaDerecha, bajaIzquierda;

    private JToggleButton buttonBajaDerecha, buttonBajaIzquierda, buttonAltaDerecha, buttonAltaIzquierda;
    private JButton buttonFinalizar;

    private JPanel panelLuzBajaDerecha, panelLuzBajaIzquierda, panelLuzAltaDerecha, panelLuzAltaIzquierda;
    private JProgressBar barra;

    private LuxometroChino luxometroChino;

    MedidaLuxometroChino medidaBajaDerecha, medidaAltaDerecha, medidaBajaIzquierda, medidaAltaIzquierda;

    private long idPrueba, idUsuario;

    public PanelLuxometroChino(long idPrueba, long idUsuario) {
        this.idPrueba = idPrueba;
        this.idUsuario = idUsuario;
        crearComponentes();
        ponercomponentes();
        crearLuxometro();
    }

    private void crearComponentes() {

        buttonBajaDerecha = new JToggleButton("Medir Baja Derecha");
        buttonBajaDerecha.addActionListener(this);
        buttonBajaDerecha.setEnabled(true);

        buttonBajaIzquierda = new JToggleButton("Madir Baja Izquierda");
        buttonBajaIzquierda.setEnabled(false);
        buttonBajaIzquierda.addActionListener(this);

        buttonAltaDerecha = new JToggleButton("Medir Alta Derecha");
        buttonAltaDerecha.setEnabled(false);
        buttonAltaDerecha.addActionListener(this);

        buttonAltaIzquierda = new JToggleButton("Medir Alta Izquierda");
        buttonAltaIzquierda.setEnabled(false);
        buttonAltaIzquierda.addActionListener(this);

        bajaDerecha = new LuzBaja();

        bajaDerecha.setBlinking(true);

        bajaIzquierda = new LuzBaja();
        altaDerecha = new LuzAlta();
        altaIzquierda = new LuzAlta();

        panelLuzBajaDerecha = construirPanel(buttonBajaDerecha, bajaDerecha);
        panelLuzBajaIzquierda = construirPanel(buttonBajaIzquierda, bajaIzquierda);

        panelLuzAltaDerecha = construirPanelAlta(buttonAltaDerecha, altaDerecha);

        panelLuzAltaIzquierda = construirPanelAlta(buttonAltaIzquierda, altaIzquierda);

        labelTitulo = new JLabel("PRUEBA DE LUCES");
        labelTitulo.setForeground(Color.red);
        labelTitulo.setFont(new Font(Font.SERIF, Font.BOLD, 24));

        barra = new JProgressBar(0, 5);

        buttonFinalizar = new JButton("ButtonFinalizar");
        buttonFinalizar.addActionListener(this);

    }

    private void ponercomponentes() {

        GridBagLayout bag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        this.setLayout(bag);

        c.insets = new Insets(5, 5, 5, 5);
        filaColumna(0, 0, c);
        c.gridwidth = GridBagConstraints.REMAINDER;
        this.add(labelTitulo, c);

        c.gridwidth = 1;
        filaColumna(1, 1, c);
        this.add(panelLuzAltaIzquierda, c);
        filaColumna(1, 2, c);
        this.add(panelLuzAltaDerecha, c);

        filaColumna(2, 1, c);
        this.add(panelLuzBajaIzquierda, c);

        filaColumna(2, 2, c);
        this.add(panelLuzBajaDerecha, c);

        c.gridwidth = 2;
        filaColumna(3, 1, c);
        c.fill = GridBagConstraints.HORIZONTAL;
        this.add(barra, c);

        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        filaColumna(4, 2, c);
        this.add(buttonFinalizar, c);

    }

    private JPanel construirPanel(JToggleButton boton, LuzBaja luz) {

        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(200, 200));
        BoxLayout box = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(box);
        panel.add(luz);

        panel.add(boton);

        return panel;

    }

    private JPanel construirPanelAlta(JToggleButton boton, LuzAlta luz) {

        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(200, 200));

        BoxLayout box = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(box);
        panel.add(luz);
        panel.add(boton);

        return panel;

    }

    private void filaColumna(int fila, int columna, GridBagConstraints c) {
        c.gridx = columna;
        c.gridy = fila;
    }//end of filaColumna

    public static void main(String args[]) {

        JFrame app = new JFrame();
        app.setExtendedState(JFrame.MAXIMIZED_BOTH);
        PanelLuxometroChino panel = new PanelLuxometroChino(1, 1);
        app.add(panel);
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        app.setVisible(true);
    }

    private void crearLuxometro() {
        try {

            String puertoLuxometro = UtilPropiedades.cargarPropiedad("PuertoLuxometro", "propiedades.properties");
            SerialPort puertoSerial = PortSerialUtil.connect(puertoLuxometro, 2400, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            luxometroChino = new LuxometroChino();
            luxometroChino.setPuertoSerial(puertoSerial);

        } catch (FileNotFoundException ex) {
            ex.printStackTrace(System.err);
            JOptionPane.showMessageDialog(null, "No se puede inicar el luxometro");
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
            JOptionPane.showMessageDialog(null, "No se puede inicar el luxometro");
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            JOptionPane.showMessageDialog(null, "No se puede inicar el luxometro");
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == buttonBajaDerecha) {

            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    try {
                        obtenerMedicionBajaDerecha();
                    } catch (ExecutionException ex) {

                        Logger.getRootLogger().error("Error midiendo luz baja derecha", ex);

                    } catch (InterruptedException ex) {

                        Logger.getRootLogger().error(ex);

                    }
                }
            });

            buttonAltaDerecha.setEnabled(true);

        } else if (e.getSource() == buttonBajaIzquierda) {

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        medirLuzBajaIzquierda();
                    } catch (ExecutionException ex) {
                        Logger.getRootLogger().error(ex);
                    } catch (InterruptedException ex) {
                        Logger.getRootLogger().error(ex);
                    }
                }
            });

            buttonBajaIzquierda.setEnabled(false);
            buttonAltaIzquierda.setEnabled(true);

        } else if (e.getSource() == buttonAltaDerecha) {
            try {
                WorkerLuxometroChino worker = new WorkerLuxometroChino(luxometroChino);
                worker.addPropertyChangeListener(new PropertyChangeListener() {

                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        if (evt.getPropertyName().equals("progress")) {
                            barra.setValue((Integer) evt.getNewValue());
                        }
                    }
                });
                worker.execute();
                labelTitulo.setText("Midiendo la luz Alta Derecha");
                buttonAltaDerecha.setEnabled(false);
                medidaAltaDerecha = worker.get();
                buttonBajaIzquierda.setEnabled(true);
            } catch (InterruptedException ex) {
                Logger.getRootLogger().error(ex);
            } catch (ExecutionException ex) {
                Logger.getRootLogger().error("Error midiendo luz alta derecha", ex);
            }

        } else if (e.getSource() == buttonAltaIzquierda) {
            try {
                WorkerLuxometroChino worker = new WorkerLuxometroChino(luxometroChino);
                worker.addPropertyChangeListener(new PropertyChangeListener() {

                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        if (evt.getPropertyName().equals("progress")) {
                            barra.setValue((Integer) evt.getNewValue());
                        }
                    }
                });
                worker.execute();
                labelTitulo.setText("Midiendo la luz Alta Izquierda");
                buttonAltaIzquierda.setEnabled(false);
                medidaAltaIzquierda = worker.get();
                buttonFinalizar.setEnabled(true);
            } catch (InterruptedException ex) {
                Logger.getRootLogger().error(ex);
            } catch (ExecutionException ex) {
                Logger.getRootLogger().error(ex);
            }

        } else if (e.getSource() == buttonFinalizar) {
            try {
                registrarMedidas(idPrueba, idUsuario);
            } catch (ClassNotFoundException ex) {
                Logger.getRootLogger().error(ex);
                JOptionPane.showMessageDialog(null, "No se puede registrar las medidas");
            } catch (SQLException ex) {
                Logger.getRootLogger().error(ex);
                JOptionPane.showMessageDialog(null, "No se puede registrar las medidas");
            } catch (Exception ex) {
                Logger.getRootLogger().error(ex);
                JOptionPane.showMessageDialog(null, "No se puede registrar las medidas");
            } finally {
                (SwingUtilities.getWindowAncestor(this)).dispose();
                luxometroChino.getPuertoSerial().close();
            }
        }
    }

    private void registrarMedidas(Long idPrueba, long idUsuario) throws ClassNotFoundException, SQLException, Exception {

        //Cargar clase de controlador de base de datos
        Class.forName("com.mysql.jdbc.Driver");
        //Crear el objeto de <span class="IL_AD" id="IL_AD12">conexion</span> a la base de datos
        Connection conexion = UtilConexion.obtenerConexion();
        conexion.setAutoCommit(false);
        double permisibleBaja = 2.5;
        double permisibleAnguloAlto = 3.5;
        double permisibleAnguloBajo = 0.5;
        int permisibleSumatoria = 225;
        //Crear objeto preparedStatement para realizar la consulta con la base de datos
        //String statementBorrar = ("DELETE  FROMmedidas WHERE TEST = ?");
        //PreparedStatement instruccionBorrar = conexion.prepareStatement(statementBorrar);
        //instruccionBorrar.setInt(1,idPrueba);
        //instruccionBorrar.executeUpdate();
        double anguloBajaDerecha = medidaBajaDerecha.getPorcentajeInclinacion();
        double luzBajaDerecha = medidaBajaDerecha.getLuminosidad();
        double luzAltaDerecha = medidaAltaDerecha.getLuminosidad();
        double anguloBajaIzquierda = medidaBajaIzquierda.getPorcentajeInclinacion();
        double luzBajaIzquierda = medidaBajaIzquierda.getLuminosidad();
        double luzAltaIzquierda = medidaAltaIzquierda.getLuminosidad();
        double sumaExploradoras = 0;
        String statement = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
        PreparedStatement instruccion = conexion.prepareStatement(statement);
        instruccion.setInt(1, 2003);
        instruccion.setDouble(2, anguloBajaDerecha);
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();
        instruccion.setInt(1, 2006);
        instruccion.setDouble(2, luzBajaDerecha);
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();
        instruccion.setInt(1, 2007);
        instruccion.setDouble(2, luzAltaDerecha);
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();
        instruccion.setInt(1, 2005);
        instruccion.setDouble(2, anguloBajaIzquierda);
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();
        instruccion.setInt(1, 2009);
        instruccion.setDouble(2, luzBajaIzquierda);
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();
        instruccion.setInt(1, 2010);
        instruccion.setDouble(2, luzAltaIzquierda);
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();
        instruccion.setInt(1, 2008);
        instruccion.setDouble(2, sumaExploradoras);
        instruccion.setLong(3, idPrueba);
        instruccion.setInt(1, 2011);
        instruccion.setDouble(2, luzBajaDerecha + luzBajaIzquierda + sumaExploradoras);
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();

        //Evaluar la prueba
        String str2 = "INSERT INTO defxprueba(id_defecto,id_prueba,Tipo_defecto) VALUES (?,?,'A')";
        PreparedStatement psDefectos = conexion.prepareStatement(str2);
        //String str3 = "UPDATE medidas SET Condicion='*' WHERE medidas.MEASURE = ?";
        boolean aprobada = true;
        if (luzBajaDerecha < permisibleBaja || luzBajaIzquierda < permisibleBaja) {
            psDefectos.clearParameters();
            psDefectos.setInt(1, 20000);
            psDefectos.setLong(2, idPrueba);
            psDefectos.executeUpdate();
            aprobada = false;
        }
//            } else if(luzAltaDerecha < permisibleAlta || luzAltaIzquierda < permisibleAlta) {
//                psDefectos.clearParameters();
//                psDefectos.setInt(1, 20003);
//                psDefectos.setLong(2, idPrueba);
//                psDefectos.executeUpdate();
//                aprobada = false;
//            }
        if (anguloBajaDerecha < permisibleAnguloBajo || anguloBajaDerecha > permisibleAnguloAlto) {
            psDefectos.clearParameters();
            psDefectos.setInt(1, 20002);//codigo defecto es distinto
            psDefectos.setLong(2, idPrueba);
            //psDefectos.setInt(3, idHojaPrueba);
            psDefectos.executeUpdate();
            aprobada = false;
        } else if (anguloBajaIzquierda < permisibleAnguloBajo || anguloBajaIzquierda > permisibleAnguloAlto) {
            psDefectos.clearParameters();
            psDefectos.setInt(1, 20002);//codigo defecto es distinto
            psDefectos.setLong(2, idPrueba);
            //psDefectos.setInt(3, idHojaPrueba);
            psDefectos.executeUpdate();
            aprobada = false;
        }
        if ((luzAltaDerecha + luzAltaIzquierda + sumaExploradoras) > permisibleSumatoria) {
            //las que se pueden prender al tiempo yse supone mayores
            psDefectos.clearParameters();
            psDefectos.setInt(1, 20001);
            psDefectos.setLong(2, idPrueba);
            psDefectos.executeUpdate();
            aprobada = false;
        }
        String statement2;
        String serialEquipo = "";
        try {
            serialEquipo = ConsultarDatosVehiculo.buscarSerialEquipo(idPrueba);
        } catch (Exception e) {
            serialEquipo = "Serial no encontrado";
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
        Mensajes.messageDoneTime("Se ha Registrado la Prueba de Luces para el Vehiculo de una manera Exitosa ..¡",3);
        // INICIO EVENTOS SICOV
        //ClienteSicov.eventoPruebaSicov(idPrueba.intValue(), EstadoEventosSicov.FINALIZADO, "",serialEquipo);
        // FIN EVENTOS SICOV
    }

    public void obtenerMedicionBajaDerecha() throws ExecutionException, InterruptedException {
        WorkerLuxometroChino worker = new WorkerLuxometroChino(luxometroChino);
        worker.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("progress")) {
                    barra.setValue((Integer) evt.getNewValue());
                }
            }
        });
        labelTitulo.setText("Midiendo la luz baja Derecha");
        buttonBajaDerecha.setEnabled(false);
        worker.execute();
        medidaBajaDerecha = worker.get();
    }

    public void medirLuzBajaIzquierda() throws ExecutionException, InterruptedException {
        WorkerLuxometroChino worker = new WorkerLuxometroChino(luxometroChino);
        worker.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("progress")) {
                    barra.setValue((Integer) evt.getNewValue());
                }
            }
        });
        labelTitulo.setText("Midiendo la luz baja Izquierda");
        worker.execute();
        medidaBajaIzquierda = worker.get();
    }
}
