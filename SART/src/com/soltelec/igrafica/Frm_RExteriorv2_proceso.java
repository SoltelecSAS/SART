/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.soltelec.igrafica;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.soltelec.modulopuc.utilidades.Mensajes;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import javax.swing.Icon;
import com.soltelec.model.Vehiculos;
import com.soltelec.modulopuc.configuracion.modelo.Conexion;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author GerenciaDesarrollo
 */
public class Frm_RExteriorv2_proceso extends javax.swing.JDialog {

    int numeroProblemas = 32;
    int alta = 100;
    int baja = 1;
    private int defecto;
    private int contadorA = 0;
    private int contadorB = 0;
    private String cadenaRExterior = "\n";
    private String grupo = "";
    private String placas = "";
    private boolean aprobado = true;
    private int idVehiculo;
    private int hojaPruebasActual;
    private int idPrueba;
//---Conexión por JDBC
    private Connection conexion;
    private String usuario;
    private String password;
    private String direccionIP;
    private Vehiculos vehiculo;
    NoReiniciable nrei;

    private Frm_RExteriorv2_proceso(JFrame jFrame, boolean b) {
        super(jFrame, b);
        initComponents();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(d);
        setResizable(false);
        
      //selecionado por defecto los no en vidrios
        RadioButtonNo.setSelected(true);
        RadioButtonNo2.setSelected(true);
        RadioButtonNo3.setSelected(true);
        RadioButtonNo4.setSelected(true);
        RadioButtonNo5.setSelected(true);
        RadioButtonNo6.setSelected(true);
        RadioButtonNo7.setSelected(true);

        //selecionado por defecto los no enlimpiaBrizas
        Panel1LimpiaBrisasNO.setSelected(true);
        CarroceriaGrupo10RadioNO.setSelected(true);
        CarroceriaGrupo11RadioNO.setSelected(true);
        CarroceriaGrupo12RadioNO.setSelected(true);
        CarroceriaGrupo13RadioNO.setSelected(true);
        CarroceriaGrupo1RadioNO.setSelected(true);
        CarroceriaGrupo2RadioNO.setSelected(true);
        CarroceriaGrupo3RadioNO.setSelected(true);
        CarroceriaGrupo4RadioNO.setSelected(true);
        CarroceriaGrupo5RadioNO.setSelected(true);
        CarroceriaGrupo6RadioNO.setSelected(true);
        CarroceriaGrupo7RadioNO.setSelected(true);
        CarroceriaGrupo8RadioNO.setSelected(true);
        CarroceriaGrupo9RadioNO.setSelected(true);
        Panel1LimpiaBrisasNO.setSelected(true);
        PeldanosGrupo1RadioButtonNO.setSelected(true);
        RetencionGrupo1RadioNO.setSelected(true);
        RetencionGrupo2RadioNO.setSelected(true);
        RetencionGrupo3RadioNO.setSelected(true);
        RetroGrupo1RadioNO.setSelected(true);
        RetroGrupo2RadioNO.setSelected(true);
        SoporteGrupo1RadioNO.setSelected(true);
        SoporteGrupo2RadioNO.setSelected(true);

        PlacasGrupo1NO.setSelected(true);
        PlacasGrupo2NO.setSelected(true); 
        PlacasGrupo3NO.setSelected(true);
        PlacasGrupo4NO.setSelected(true); 
        PlacasGrupo5NO.setSelected(true);
        PlacasGrupo6NO.setSelected(true); 
        PlacasGrupo7NO.setSelected(true);
    }

    public NoReiniciable getNrei() {
        return nrei;
    }

    public void setNrei(NoReiniciable nrei) {
        this.nrei = nrei;
    }
   
    NoReiniciable placas_bd;

    public NoReiniciable getPlacasBd() {
        return placas_bd;
    }

    public void setPlacasBd(NoReiniciable placas_bd) {
        this.placas_bd = placas_bd;
    }

    public Frm_RExteriorv2_proceso(java.awt.Frame parent, boolean modal, int idVehiculo, int hojaPruebasActual, int idPrueba, Vehiculos vehiculo) {
        super(parent, modal);
        this.idVehiculo = idVehiculo;
        this.hojaPruebasActual = hojaPruebasActual;
        this.idPrueba = idPrueba;       
        initComponents();
        this.vehiculo = vehiculo;
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        
        
      //selecionado por defecto los no en vidrios
        RadioButtonNo.setSelected(true);
        RadioButtonNo2.setSelected(true);
        RadioButtonNo3.setSelected(true);
        RadioButtonNo4.setSelected(true);
        RadioButtonNo5.setSelected(true);
        RadioButtonNo6.setSelected(true);
        RadioButtonNo7.setSelected(true);

        //selecionado por defecto los no enlimpiaBrizas
        Panel1LimpiaBrisasNO.setSelected(true);
        CarroceriaGrupo10RadioNO.setSelected(true);
        CarroceriaGrupo11RadioNO.setSelected(true);
        CarroceriaGrupo12RadioNO.setSelected(true);
        CarroceriaGrupo13RadioNO.setSelected(true);
        CarroceriaGrupo1RadioNO.setSelected(true);
        CarroceriaGrupo2RadioNO.setSelected(true);
        CarroceriaGrupo3RadioNO.setSelected(true);
        CarroceriaGrupo4RadioNO.setSelected(true);
        CarroceriaGrupo5RadioNO.setSelected(true);
        CarroceriaGrupo6RadioNO.setSelected(true);
        CarroceriaGrupo7RadioNO.setSelected(true);
        CarroceriaGrupo8RadioNO.setSelected(true);
        CarroceriaGrupo9RadioNO.setSelected(true);
        Panel1LimpiaBrisasNO.setSelected(true);
        PeldanosGrupo1RadioButtonNO.setSelected(true);
        RetencionGrupo1RadioNO.setSelected(true);
        RetencionGrupo2RadioNO.setSelected(true);
        RetencionGrupo3RadioNO.setSelected(true);
        RetroGrupo1RadioNO.setSelected(true);
        RetroGrupo2RadioNO.setSelected(true);
        SoporteGrupo1RadioNO.setSelected(true);
        SoporteGrupo2RadioNO.setSelected(true);

        PlacasGrupo1NO.setSelected(true);
        PlacasGrupo2NO.setSelected(true); 
        PlacasGrupo3NO.setSelected(true);
        PlacasGrupo4NO.setSelected(true); 
        PlacasGrupo5NO.setSelected(true);
        PlacasGrupo6NO.setSelected(true); 
        PlacasGrupo7NO.setSelected(true);
        
        if (vehiculo.getServicios().getService() == 2) 
        {
            PlacasGrupo1SI.setEnabled(true);
            PlacasGrupo1NO.setEnabled(true);
            PlacasGrupo1NA.setEnabled(true);
            PlacasGrupo2SI.setEnabled(true);
            PlacasGrupo2NO.setEnabled(true);
            PlacasGrupo2NA.setEnabled(true);
            PlacasGrupo3SI.setEnabled(true);
            PlacasGrupo3NO.setEnabled(true);
            PlacasGrupo3NA.setEnabled(true);
            PlacasGrupo4SI.setEnabled(true);
            PlacasGrupo4NO.setEnabled(true);
            PlacasGrupo4NA.setEnabled(true);
            PlacasGrupo5SI.setEnabled(true);
            PlacasGrupo5NO.setEnabled(true);
            PlacasGrupo5NA.setEnabled(true);
            PlacasGrupo6SI.setEnabled(true);
            PlacasGrupo6NO.setEnabled(true);
            PlacasGrupo6NA.setEnabled(true);
            PlacasGrupo7SI.setEnabled(true);
            PlacasGrupo7NO.setEnabled(true);
            PlacasGrupo7NA.setEnabled(true);
        }
        setSize(d);
        setResizable(false);

        this.jTabbedPane1.setIconAt(7, new javax.swing.ImageIcon(getClass().getResource("/com/soltelec/Icon/placa.png")));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Grupo1 = new javax.swing.ButtonGroup();
        Group2 = new javax.swing.ButtonGroup();
        Group3 = new javax.swing.ButtonGroup();
        Grupo4 = new javax.swing.ButtonGroup();
        Grupo5 = new javax.swing.ButtonGroup();
        Grupo6 = new javax.swing.ButtonGroup();
        GroupLimpiBrizas1 = new javax.swing.ButtonGroup();
        Group1Peldanos = new javax.swing.ButtonGroup();
        Group1RetroVisores = new javax.swing.ButtonGroup();
        Group2RetroVisores = new javax.swing.ButtonGroup();
        Group1Retencion = new javax.swing.ButtonGroup();
        Group2Retencion = new javax.swing.ButtonGroup();
        Group3Retencion = new javax.swing.ButtonGroup();
        Group1Soporte = new javax.swing.ButtonGroup();
        Group2Soporte = new javax.swing.ButtonGroup();
        Group1Carroceria = new javax.swing.ButtonGroup();
        Group2Carroceria = new javax.swing.ButtonGroup();
        Group3Carroceria = new javax.swing.ButtonGroup();
        Group4Carroceria = new javax.swing.ButtonGroup();
        Group5Carroceria = new javax.swing.ButtonGroup();
        Group6Carroceria = new javax.swing.ButtonGroup();
        Group7Carroceria = new javax.swing.ButtonGroup();
        Group8Carroceria = new javax.swing.ButtonGroup();
        Group9Carroceria = new javax.swing.ButtonGroup();
        Group10Carroceria = new javax.swing.ButtonGroup();
        Group11Carroceria = new javax.swing.ButtonGroup();
        Group12Carroceria = new javax.swing.ButtonGroup();
        Group13Carroceria = new javax.swing.ButtonGroup();
        Group1Placa = new javax.swing.ButtonGroup();
        Group2Placa = new javax.swing.ButtonGroup();
        Group3Placa = new javax.swing.ButtonGroup();
        Group4Placa = new javax.swing.ButtonGroup();
        Group5Placa = new javax.swing.ButtonGroup();
        Group6Placa = new javax.swing.ButtonGroup();
        Group7Placa = new javax.swing.ButtonGroup();
        Grupo7 = new javax.swing.ButtonGroup();
        jPanel8 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        Panel1Retencion = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        RetencionGrupo1RadioSI = new javax.swing.JRadioButton();
        RetencionGrupo1RadioNO = new javax.swing.JRadioButton();
        RetencionGrupo1RadioNA = new javax.swing.JRadioButton();
        Panel2Retencion = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        RetencionGrupo2RadioSI = new javax.swing.JRadioButton();
        RetencionGrupo2RadioNO = new javax.swing.JRadioButton();
        RetencionGrupo2RadioNA = new javax.swing.JRadioButton();
        Panel3Retencion = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        RetencionGrupo3RadioSI = new javax.swing.JRadioButton();
        RetencionGrupo3RadioNO = new javax.swing.JRadioButton();
        RetencionGrupo3RadioNA = new javax.swing.JRadioButton();
        jPanel4 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        Panel1LimpiaBrisasNA = new javax.swing.JRadioButton();
        Panel1LimpiaBrisasSi = new javax.swing.JRadioButton();
        Panel1LimpiaBrisasNO = new javax.swing.JRadioButton();
        jPanel5 = new javax.swing.JPanel();
        Panel1Peldanos = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        PeldanosGrupo1RadioButtonNA = new javax.swing.JRadioButton();
        PeldanosGrupo1RadioButtonSI = new javax.swing.JRadioButton();
        PeldanosGrupo1RadioButtonNO = new javax.swing.JRadioButton();
        jPanel7 = new javax.swing.JPanel();
        PanelVidrio1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        RadioButtonSi = new javax.swing.JRadioButton();
        RadioButtonNo = new javax.swing.JRadioButton();
        RadioButtonNA = new javax.swing.JRadioButton();
        PanelVidrio3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        RadioButtonNA3 = new javax.swing.JRadioButton();
        RadioButtonSi3 = new javax.swing.JRadioButton();
        RadioButtonNo3 = new javax.swing.JRadioButton();
        PanelVidrio2 = new javax.swing.JPanel();
        RadioButtonSi2 = new javax.swing.JRadioButton();
        RadioButtonNo2 = new javax.swing.JRadioButton();
        RadioButtonNA2 = new javax.swing.JRadioButton();
        jLabel7 = new javax.swing.JLabel();
        PanelVidrio4 = new javax.swing.JPanel();
        RadioButtonSi4 = new javax.swing.JRadioButton();
        RadioButtonNo4 = new javax.swing.JRadioButton();
        RadioButtonNA4 = new javax.swing.JRadioButton();
        jLabel8 = new javax.swing.JLabel();
        PanelVidrio5 = new javax.swing.JPanel();
        RadioButtonSi5 = new javax.swing.JRadioButton();
        RadioButtonNo5 = new javax.swing.JRadioButton();
        RadioButtonNA5 = new javax.swing.JRadioButton();
        jLabel9 = new javax.swing.JLabel();
        PanelVidrio6 = new javax.swing.JPanel();
        RadioButtonSi6 = new javax.swing.JRadioButton();
        RadioButtonNo6 = new javax.swing.JRadioButton();
        RadioButtonNA6 = new javax.swing.JRadioButton();
        jLabel10 = new javax.swing.JLabel();
        PanelVidrio7 = new javax.swing.JPanel();
        jLabel45 = new javax.swing.JLabel();
        RadioButtonSi7 = new javax.swing.JRadioButton();
        RadioButtonNo7 = new javax.swing.JRadioButton();
        RadioButtonNA7 = new javax.swing.JRadioButton();
        jPanel2 = new javax.swing.JPanel();
        Panel1Soporte = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        SoporteGrupo1RadioSI = new javax.swing.JRadioButton();
        SoporteGrupo1RadioNO = new javax.swing.JRadioButton();
        SoporteGrupo1RadioNA = new javax.swing.JRadioButton();
        Panel2Soporte = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        SoporteGrupo2RadioSI = new javax.swing.JRadioButton();
        SoporteGrupo2RadioNO = new javax.swing.JRadioButton();
        SoporteGrupo2RadioNA = new javax.swing.JRadioButton();
        jPanel1 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        CarroceriaGrupo1RadioSI = new javax.swing.JRadioButton();
        CarroceriaGrupo1RadioNO = new javax.swing.JRadioButton();
        CarroceriaGrupo1RadioNA = new javax.swing.JRadioButton();
        jPanel12 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        jPanel17 = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        CarroceriaGrupo2RadioSI = new javax.swing.JRadioButton();
        CarroceriaGrupo2RadioNO = new javax.swing.JRadioButton();
        CarroceriaGrupo2RadioNA = new javax.swing.JRadioButton();
        jPanel13 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        CarroceriaGrupo3RadioSI = new javax.swing.JRadioButton();
        CarroceriaGrupo3RadioNO = new javax.swing.JRadioButton();
        CarroceriaGrupo3RadioNA = new javax.swing.JRadioButton();
        jPanel14 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        CarroceriaGrupo4RadioNA = new javax.swing.JRadioButton();
        CarroceriaGrupo4RadioNO = new javax.swing.JRadioButton();
        CarroceriaGrupo4RadioSI = new javax.swing.JRadioButton();
        jPanel18 = new javax.swing.JPanel();
        jLabel26 = new javax.swing.JLabel();
        CarroceriaGrupo5RadioSI = new javax.swing.JRadioButton();
        CarroceriaGrupo5RadioNO = new javax.swing.JRadioButton();
        CarroceriaGrupo5RadioNA = new javax.swing.JRadioButton();
        jPanel19 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        CarroceriaGrupo6RadioSI = new javax.swing.JRadioButton();
        CarroceriaGrupo6RadioNO = new javax.swing.JRadioButton();
        CarroceriaGrupo6RadioNA = new javax.swing.JRadioButton();
        jPanel20 = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        CarroceriaGrupo7RadioSI = new javax.swing.JRadioButton();
        CarroceriaGrupo7RadioNO = new javax.swing.JRadioButton();
        CarroceriaGrupo7RadioNA = new javax.swing.JRadioButton();
        jPanel21 = new javax.swing.JPanel();
        jLabel29 = new javax.swing.JLabel();
        jPanel22 = new javax.swing.JPanel();
        jLabel30 = new javax.swing.JLabel();
        CarroceriaGrup8RadioSI = new javax.swing.JRadioButton();
        CarroceriaGrupo8RadioNO = new javax.swing.JRadioButton();
        CarroceriaGrupo8RadioNA = new javax.swing.JRadioButton();
        jPanel23 = new javax.swing.JPanel();
        jLabel31 = new javax.swing.JLabel();
        CarroceriaGrup9RadioSI = new javax.swing.JRadioButton();
        CarroceriaGrupo9RadioNO = new javax.swing.JRadioButton();
        CarroceriaGrupo9RadioNA = new javax.swing.JRadioButton();
        jPanel24 = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        CarroceriaGrup10RadioSI = new javax.swing.JRadioButton();
        CarroceriaGrupo10RadioNO = new javax.swing.JRadioButton();
        CarroceriaGrupo10RadioNA = new javax.swing.JRadioButton();
        jPanel25 = new javax.swing.JPanel();
        jLabel33 = new javax.swing.JLabel();
        jPanel26 = new javax.swing.JPanel();
        jLabel34 = new javax.swing.JLabel();
        CarroceriaGrup11RadioSI = new javax.swing.JRadioButton();
        CarroceriaGrupo11RadioNO = new javax.swing.JRadioButton();
        CarroceriaGrupo11RadioNA = new javax.swing.JRadioButton();
        jPanel27 = new javax.swing.JPanel();
        jLabel35 = new javax.swing.JLabel();
        CarroceriaGrup12RadioSI = new javax.swing.JRadioButton();
        CarroceriaGrupo12RadioNO = new javax.swing.JRadioButton();
        CarroceriaGrupo12RadioNA = new javax.swing.JRadioButton();
        jPanel28 = new javax.swing.JPanel();
        jLabel36 = new javax.swing.JLabel();
        CarroceriaGrup13RadioSI = new javax.swing.JRadioButton();
        CarroceriaGrupo13RadioNO = new javax.swing.JRadioButton();
        CarroceriaGrupo13RadioNA = new javax.swing.JRadioButton();
        jPanel9 = new javax.swing.JPanel();
        Panel1Placa = new javax.swing.JPanel();
        jLabel37 = new javax.swing.JLabel();
        PlacasGrupo1SI = new javax.swing.JRadioButton();
        PlacasGrupo1NO = new javax.swing.JRadioButton();
        PlacasGrupo1NA = new javax.swing.JRadioButton();
        Panel2Placa = new javax.swing.JPanel();
        jLabel38 = new javax.swing.JLabel();
        PlacasGrupo2SI = new javax.swing.JRadioButton();
        PlacasGrupo2NO = new javax.swing.JRadioButton();
        PlacasGrupo2NA = new javax.swing.JRadioButton();
        PanelVidrio9 = new javax.swing.JPanel();
        jLabel39 = new javax.swing.JLabel();
        RadioButtonSi8 = new javax.swing.JRadioButton();
        RadioButtonNo8 = new javax.swing.JRadioButton();
        RadioButtonNA8 = new javax.swing.JRadioButton();
        Panel3Placa = new javax.swing.JPanel();
        jLabel40 = new javax.swing.JLabel();
        PlacasGrupo3SI = new javax.swing.JRadioButton();
        PlacasGrupo3NO = new javax.swing.JRadioButton();
        PlacasGrupo3NA = new javax.swing.JRadioButton();
        Panel4Placa = new javax.swing.JPanel();
        jLabel41 = new javax.swing.JLabel();
        PlacasGrupo4SI = new javax.swing.JRadioButton();
        PlacasGrupo4NO = new javax.swing.JRadioButton();
        PlacasGrupo4NA = new javax.swing.JRadioButton();
        Panel5Placa = new javax.swing.JPanel();
        jLabel42 = new javax.swing.JLabel();
        PlacasGrupo5SI = new javax.swing.JRadioButton();
        PlacasGrupo5NO = new javax.swing.JRadioButton();
        PlacasGrupo5NA = new javax.swing.JRadioButton();
        Panel6Placa = new javax.swing.JPanel();
        jLabel43 = new javax.swing.JLabel();
        PlacasGrupo6SI = new javax.swing.JRadioButton();
        PlacasGrupo6NO = new javax.swing.JRadioButton();
        PlacasGrupo6NA = new javax.swing.JRadioButton();
        Panel7Placa = new javax.swing.JPanel();
        jLabel44 = new javax.swing.JLabel();
        PlacasGrupo7SI = new javax.swing.JRadioButton();
        PlacasGrupo7NO = new javax.swing.JRadioButton();
        PlacasGrupo7NA = new javax.swing.JRadioButton();
        jPanel6 = new javax.swing.JPanel();
        Panel1Retrovisores = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        RetroGrupo1RadioNA = new javax.swing.JRadioButton();
        RetroGrupo1RadioSI = new javax.swing.JRadioButton();
        RetroGrupo1RadioNO = new javax.swing.JRadioButton();
        Panel1Retrovisores1 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        RetroGrupo2RadioNA = new javax.swing.JRadioButton();
        RetroGrupo2RadioSI = new javax.swing.JRadioButton();
        RetroGrupo2RadioNO = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        Panel1Retencion.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        Panel1Retencion.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel14.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jLabel14.setText("<html>Mal estado de los anclajes y demás elementos para sujetar los contenedores cuando sea aplicable</html>");
        Panel1Retencion.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 540, 40));

        Group1Retencion.add(RetencionGrupo1RadioSI);
        RetencionGrupo1RadioSI.setText("SI");
        Panel1Retencion.add(RetencionGrupo1RadioSI, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 10, -1, -1));

        Group1Retencion.add(RetencionGrupo1RadioNO);
        RetencionGrupo1RadioNO.setText("NO");
        Panel1Retencion.add(RetencionGrupo1RadioNO, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 10, -1, -1));

        Group1Retencion.add(RetencionGrupo1RadioNA);
        RetencionGrupo1RadioNA.setText("N/A");
        Panel1Retencion.add(RetencionGrupo1RadioNA, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 10, -1, -1));

        jPanel3.add(Panel1Retencion, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 16, 730, -1));

        Panel2Retencion.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        Panel2Retencion.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel15.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jLabel15.setText("<html>La inexistencia o mal funcionamiento de puertas o compuertas de carga para <br/>vehículos con platón estacas o furgones</html>");
        Panel2Retencion.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 540, 40));

        Group2Retencion.add(RetencionGrupo2RadioSI);
        RetencionGrupo2RadioSI.setText("SI");
        Panel2Retencion.add(RetencionGrupo2RadioSI, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 10, -1, -1));

        Group2Retencion.add(RetencionGrupo2RadioNO);
        RetencionGrupo2RadioNO.setText("NO");
        Panel2Retencion.add(RetencionGrupo2RadioNO, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 10, -1, -1));

        Group2Retencion.add(RetencionGrupo2RadioNA);
        RetencionGrupo2RadioNA.setText("N/A");
        Panel2Retencion.add(RetencionGrupo2RadioNA, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 10, -1, -1));

        jPanel3.add(Panel2Retencion, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 730, -1));

        Panel3Retencion.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        Panel3Retencion.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel16.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jLabel16.setText("<html>Puertas o compuertas de carga que no dispongan de los cierres adecuados para impedir que estas se abranpor las fuerzas normales que actúan en sumovilización</html>");
        Panel3Retencion.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 540, 40));

        Group3Retencion.add(RetencionGrupo3RadioSI);
        RetencionGrupo3RadioSI.setText("SI");
        Panel3Retencion.add(RetencionGrupo3RadioSI, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 10, -1, -1));

        Group3Retencion.add(RetencionGrupo3RadioNO);
        RetencionGrupo3RadioNO.setText("NO");
        Panel3Retencion.add(RetencionGrupo3RadioNO, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 10, -1, -1));

        Group3Retencion.add(RetencionGrupo3RadioNA);
        RetencionGrupo3RadioNA.setText("N/A");
        Panel3Retencion.add(RetencionGrupo3RadioNA, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 10, -1, -1));

        jPanel3.add(Panel3Retencion, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 730, -1));

        jTabbedPane1.addTab("Dispositivos de retención de la carga", new javax.swing.ImageIcon(getClass().getResource("/com/soltelec/Icon/moving_24.png")), jPanel3); // NOI18N

        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel10.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel10.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel5.setFont(new java.awt.Font("Arial", 1, 15)); // NOI18N
        jLabel5.setText("Inexistencia o mal funcionamiento de los limpiaparabrisas delanteros");
        jPanel10.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 6, 496, 30));

        GroupLimpiBrizas1.add(Panel1LimpiaBrisasNA);
        Panel1LimpiaBrisasNA.setText("NA");
        jPanel10.add(Panel1LimpiaBrisasNA, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 10, -1, -1));

        GroupLimpiBrizas1.add(Panel1LimpiaBrisasSi);
        Panel1LimpiaBrisasSi.setText("SI");
        jPanel10.add(Panel1LimpiaBrisasSi, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 10, -1, -1));

        GroupLimpiBrizas1.add(Panel1LimpiaBrisasNO);
        Panel1LimpiaBrisasNO.setText("NO");
        jPanel10.add(Panel1LimpiaBrisasNO, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 10, -1, -1));

        jPanel4.add(jPanel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 720, 40));

        jTabbedPane1.addTab("Limpiabrizas", new javax.swing.ImageIcon(getClass().getResource("/com/soltelec/Icon/minus_24.png")), jPanel4); // NOI18N

        Panel1Peldanos.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        Panel1Peldanos.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel11.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jLabel11.setText("<html>La inexistencia o deterioro de peldaños o estribos para acceso  y salida <br> del vehículo</html>");
        Panel1Peldanos.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 510, 40));

        Group1Peldanos.add(PeldanosGrupo1RadioButtonNA);
        PeldanosGrupo1RadioButtonNA.setText("NA");
        Panel1Peldanos.add(PeldanosGrupo1RadioButtonNA, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 10, -1, -1));

        Group1Peldanos.add(PeldanosGrupo1RadioButtonSI);
        PeldanosGrupo1RadioButtonSI.setText("SI");
        Panel1Peldanos.add(PeldanosGrupo1RadioButtonSI, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 10, -1, -1));

        Group1Peldanos.add(PeldanosGrupo1RadioButtonNO);
        PeldanosGrupo1RadioButtonNO.setText("NO");
        Panel1Peldanos.add(PeldanosGrupo1RadioButtonNO, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 10, -1, -1));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Panel1Peldanos, javax.swing.GroupLayout.PREFERRED_SIZE, 720, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(29, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(Panel1Peldanos, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(372, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Peldaños", new javax.swing.ImageIcon(getClass().getResource("/com/soltelec/Icon/chart_bar_up_24.png")), jPanel5); // NOI18N

        jPanel7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jPanel7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        PanelVidrio1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        PanelVidrio1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel4.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jLabel4.setText("<html>Vidrio(s) parabrisas que distorsionan y/o deforman el campo de visión mínima del <br> conductor.</html>");
        PanelVidrio1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 540, 40));

        Grupo1.add(RadioButtonSi);
        RadioButtonSi.setText("SI");
        PanelVidrio1.add(RadioButtonSi, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 10, -1, -1));

        Grupo1.add(RadioButtonNo);
        RadioButtonNo.setText("NO");
        PanelVidrio1.add(RadioButtonNo, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 10, -1, -1));

        Grupo1.add(RadioButtonNA);
        RadioButtonNA.setText("N/A");
        PanelVidrio1.add(RadioButtonNA, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 10, -1, -1));

        jPanel7.add(PanelVidrio1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 730, 40));

        PanelVidrio3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel6.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jLabel6.setText("Inexistencia de algún vidrio fijo diferente a los parabrisas");

        Group3.add(RadioButtonNA3);
        RadioButtonNA3.setText("N/A");

        Group3.add(RadioButtonSi3);
        RadioButtonSi3.setText("SI");

        Group3.add(RadioButtonNo3);
        RadioButtonNo3.setText("NO");

        javax.swing.GroupLayout PanelVidrio3Layout = new javax.swing.GroupLayout(PanelVidrio3);
        PanelVidrio3.setLayout(PanelVidrio3Layout);
        PanelVidrio3Layout.setHorizontalGroup(
            PanelVidrio3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelVidrio3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 531, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(RadioButtonSi3)
                .addGap(16, 16, 16)
                .addComponent(RadioButtonNo3)
                .addGap(18, 18, 18)
                .addComponent(RadioButtonNA3)
                .addContainerGap(14, Short.MAX_VALUE))
        );
        PanelVidrio3Layout.setVerticalGroup(
            PanelVidrio3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelVidrio3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(PanelVidrio3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelVidrio3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(RadioButtonSi3)
                        .addComponent(RadioButtonNo3))
                    .addComponent(RadioButtonNA3))
                .addContainerGap())
        );

        jPanel7.add(PanelVidrio3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, 730, 40));

        PanelVidrio2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        PanelVidrio2.setMinimumSize(new java.awt.Dimension(713, 35));
        PanelVidrio2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        Group2.add(RadioButtonSi2);
        RadioButtonSi2.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        RadioButtonSi2.setText("SI");
        PanelVidrio2.add(RadioButtonSi2, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 10, 50, 20));

        Group2.add(RadioButtonNo2);
        RadioButtonNo2.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        RadioButtonNo2.setText("NO");
        PanelVidrio2.add(RadioButtonNo2, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 10, 50, 20));

        Group2.add(RadioButtonNA2);
        RadioButtonNA2.setText("N/A");
        PanelVidrio2.add(RadioButtonNA2, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 10, -1, 20));

        jLabel7.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jLabel7.setText("Inexistencia de alguno de los parabrisas o de los vidros móviles");
        PanelVidrio2.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 540, 30));

        jPanel7.add(PanelVidrio2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 64, 730, 40));

        PanelVidrio4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        PanelVidrio4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        Grupo4.add(RadioButtonSi4);
        RadioButtonSi4.setText("SI");
        PanelVidrio4.add(RadioButtonSi4, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 20, -1, -1));

        Grupo4.add(RadioButtonNo4);
        RadioButtonNo4.setText("NO");
        PanelVidrio4.add(RadioButtonNo4, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 10, 50, 40));

        Grupo4.add(RadioButtonNA4);
        RadioButtonNA4.setText("N/A");
        PanelVidrio4.add(RadioButtonNA4, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 20, -1, -1));

        jLabel8.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jLabel8.setText("<html>La inexistencia o mal funcionamiento de los mecanismos de accionamiento<br/> de alguno de los vidrios para vehículos de transporte público o especial de pasajeros.</html>");
        PanelVidrio4.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 4, 520, 50));

        jPanel7.add(PanelVidrio4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 210, 730, 60));

        PanelVidrio5.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        PanelVidrio5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        Grupo5.add(RadioButtonSi5);
        RadioButtonSi5.setText("SI");
        PanelVidrio5.add(RadioButtonSi5, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 20, -1, -1));

        Grupo5.add(RadioButtonNo5);
        RadioButtonNo5.setText("NO");
        PanelVidrio5.add(RadioButtonNo5, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 20, -1, -1));

        Grupo5.add(RadioButtonNA5);
        RadioButtonNA5.setText("N/A");
        PanelVidrio5.add(RadioButtonNA5, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 20, -1, -1));

        jLabel9.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jLabel9.setText("<html>La inexistencia o mal funcionamiento de los mecanismos de <br/>accionamiento de alguno de los vidrios para vehículos particulares</html>");
        PanelVidrio5.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 4, 520, 40));

        jPanel7.add(PanelVidrio5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 280, 730, 50));

        PanelVidrio6.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        PanelVidrio6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        Grupo6.add(RadioButtonSi6);
        RadioButtonSi6.setText("SI");
        PanelVidrio6.add(RadioButtonSi6, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 10, -1, -1));

        Grupo6.add(RadioButtonNo6);
        RadioButtonNo6.setText("NO");
        PanelVidrio6.add(RadioButtonNo6, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 10, -1, -1));

        Grupo6.add(RadioButtonNA6);
        RadioButtonNA6.setText("N/A");
        PanelVidrio6.add(RadioButtonNA6, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 10, -1, -1));

        jLabel10.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jLabel10.setText("<html>Vidrios que no sean transparentes en los vehículos de transporte público de<br/> \npasajeros de circulación urbana.</html>");
        PanelVidrio6.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 4, 530, 30));

        jPanel7.add(PanelVidrio6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 340, 730, 40));

        PanelVidrio7.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        PanelVidrio7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel45.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jLabel45.setText("<html>La existencia de fisuras, impactos o laminas adheridas, publicidad o adhesivos al (a los)parabrisa (s),  que dificulten el campo de visión mínima del conductor</html>");
        PanelVidrio7.add(jLabel45, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 540, 40));

        Grupo7.add(RadioButtonSi7);
        RadioButtonSi7.setText("SI");
        PanelVidrio7.add(RadioButtonSi7, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 10, -1, -1));

        Grupo7.add(RadioButtonNo7);
        RadioButtonNo7.setText("NO");
        PanelVidrio7.add(RadioButtonNo7, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 10, -1, -1));

        Grupo7.add(RadioButtonNA7);
        RadioButtonNA7.setText("N/A");
        PanelVidrio7.add(RadioButtonNA7, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 10, -1, -1));

        jPanel7.add(PanelVidrio7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 160, 730, -1));

        jTabbedPane1.addTab("Vidrios", new javax.swing.ImageIcon(getClass().getResource("/com/soltelec/Icon/resize_24.png")), jPanel7); // NOI18N

        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        Panel1Soporte.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        Panel1Soporte.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel17.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jLabel17.setText("Soporte de fijación roto o alguno de sus anclajes");
        Panel1Soporte.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 540, 40));

        Group1Soporte.add(SoporteGrupo1RadioSI);
        SoporteGrupo1RadioSI.setText("SI");
        Panel1Soporte.add(SoporteGrupo1RadioSI, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 10, -1, -1));

        Group1Soporte.add(SoporteGrupo1RadioNO);
        SoporteGrupo1RadioNO.setText("NO");
        Panel1Soporte.add(SoporteGrupo1RadioNO, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 10, -1, -1));

        Group1Soporte.add(SoporteGrupo1RadioNA);
        SoporteGrupo1RadioNA.setText("N/A");
        Panel1Soporte.add(SoporteGrupo1RadioNA, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 10, -1, -1));

        jPanel2.add(Panel1Soporte, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 11, 730, -1));

        Panel2Soporte.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        Panel2Soporte.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel18.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jLabel18.setText("Holgura con riesgo de desprendimiento de la rueda de repuesto");
        Panel2Soporte.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 540, 40));

        Group2Soporte.add(SoporteGrupo2RadioSI);
        SoporteGrupo2RadioSI.setText("SI");
        Panel2Soporte.add(SoporteGrupo2RadioSI, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 10, -1, -1));

        Group2Soporte.add(SoporteGrupo2RadioNO);
        SoporteGrupo2RadioNO.setText("NO");
        Panel2Soporte.add(SoporteGrupo2RadioNO, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 10, -1, -1));

        Group2Soporte.add(SoporteGrupo2RadioNA);
        SoporteGrupo2RadioNA.setText("N/A");
        Panel2Soporte.add(SoporteGrupo2RadioNA, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 10, -1, -1));

        jPanel2.add(Panel2Soporte, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 730, -1));

        jTabbedPane1.addTab("Soporte exterior de rueda de repuesto", new javax.swing.ImageIcon(getClass().getResource("/com/soltelec/Icon/spanner_24.png")), jPanel2); // NOI18N

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel11.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel11.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel19.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jLabel19.setText("<html>Presencia de aristas vivas o bordes cortantes exteriores en el vehículo</html>");
        jPanel11.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 581, 26));

        Group1Carroceria.add(CarroceriaGrupo1RadioSI);
        CarroceriaGrupo1RadioSI.setText("SI");
        jPanel11.add(CarroceriaGrupo1RadioSI, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 7, -1, 20));

        Group1Carroceria.add(CarroceriaGrupo1RadioNO);
        CarroceriaGrupo1RadioNO.setText("NO");
        jPanel11.add(CarroceriaGrupo1RadioNO, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 7, -1, 20));

        Group1Carroceria.add(CarroceriaGrupo1RadioNA);
        CarroceriaGrupo1RadioNA.setText("N/A");
        jPanel11.add(CarroceriaGrupo1RadioNA, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 7, -1, 20));

        jPanel1.add(jPanel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 740, 30));

        jPanel12.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel12.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel20.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jLabel20.setText("<html>La inexistencia de los sistemas mecánicos, neumáticos  y eléctricos de acoplamiento <br/> (Es aplicable a vehiculos diseñados para llevar remolques o semi-remolques) <html>");
        jPanel12.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 581, 40));

        jPanel15.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel15.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel23.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jLabel23.setText("<html>La inexistencia de los sistemas mecánicos, neumáticos  y eléctricos de acoplamiento <br/> (Es aplicable a vehiculos diseñados para llevar remolques o semi-remolques) <html>");
        jPanel15.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 581, 40));

        jPanel12.add(jPanel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 740, 40));

        jPanel16.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel16.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel24.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jLabel24.setText("<html>La inexistencia de los sistemas mecánicos, neumáticos  y eléctricos de acoplamiento <br/> (Es aplicable a vehiculos diseñados para llevar remolques o semi-remolques) <html>");
        jPanel16.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 581, 40));

        jPanel17.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel17.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel25.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jLabel25.setText("<html>La inexistencia de los sistemas mecánicos, neumáticos  y eléctricos de acoplamiento <br/> (Es aplicable a vehiculos diseñados para llevar remolques o semi-remolques) <html>");
        jPanel17.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 581, 40));

        jPanel16.add(jPanel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 740, 40));

        jPanel12.add(jPanel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 740, 40));

        Group2Carroceria.add(CarroceriaGrupo2RadioSI);
        CarroceriaGrupo2RadioSI.setText("SI");
        jPanel12.add(CarroceriaGrupo2RadioSI, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 10, -1, 20));

        Group2Carroceria.add(CarroceriaGrupo2RadioNO);
        CarroceriaGrupo2RadioNO.setText("NO");
        jPanel12.add(CarroceriaGrupo2RadioNO, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 10, -1, 20));

        Group2Carroceria.add(CarroceriaGrupo2RadioNA);
        CarroceriaGrupo2RadioNA.setText("N/A");
        jPanel12.add(CarroceriaGrupo2RadioNA, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 10, -1, 20));

        jPanel1.add(jPanel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 740, 40));

        jPanel13.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel13.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel21.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jLabel21.setText("Cierre inadecuado de baul");
        jPanel13.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 581, 30));

        Group3Carroceria.add(CarroceriaGrupo3RadioSI);
        CarroceriaGrupo3RadioSI.setText("SI");
        jPanel13.add(CarroceriaGrupo3RadioSI, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 0, -1, 20));

        Group3Carroceria.add(CarroceriaGrupo3RadioNO);
        CarroceriaGrupo3RadioNO.setText("NO");
        jPanel13.add(CarroceriaGrupo3RadioNO, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 0, -1, 20));

        Group3Carroceria.add(CarroceriaGrupo3RadioNA);
        CarroceriaGrupo3RadioNA.setText("N/A");
        jPanel13.add(CarroceriaGrupo3RadioNA, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 0, -1, 20));

        jPanel1.add(jPanel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 740, 30));

        jPanel14.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel14.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel22.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jLabel22.setText("Cierre inadecuado de puertas o capo");
        jPanel14.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 581, 26));

        Group4Carroceria.add(CarroceriaGrupo4RadioNA);
        CarroceriaGrupo4RadioNA.setText("N/A");
        jPanel14.add(CarroceriaGrupo4RadioNA, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 0, -1, 20));

        Group4Carroceria.add(CarroceriaGrupo4RadioNO);
        CarroceriaGrupo4RadioNO.setText("NO");
        jPanel14.add(CarroceriaGrupo4RadioNO, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 0, -1, 20));

        Group4Carroceria.add(CarroceriaGrupo4RadioSI);
        CarroceriaGrupo4RadioSI.setText("SI");
        jPanel14.add(CarroceriaGrupo4RadioSI, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 0, -1, 20));

        jPanel1.add(jPanel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, 740, 30));

        jPanel18.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel18.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel26.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jLabel26.setText("<html>Partes exteriores de la carroceria o cabina en mal estado (flojas, sueltas), que presenten peligro para los  demas usuarios en la via </html>");
        jPanel18.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 581, 30));

        Group5Carroceria.add(CarroceriaGrupo5RadioSI);
        CarroceriaGrupo5RadioSI.setText("SI");
        jPanel18.add(CarroceriaGrupo5RadioSI, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 0, -1, 20));

        Group5Carroceria.add(CarroceriaGrupo5RadioNO);
        CarroceriaGrupo5RadioNO.setText("NO");
        jPanel18.add(CarroceriaGrupo5RadioNO, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 0, -1, 20));

        Group5Carroceria.add(CarroceriaGrupo5RadioNA);
        CarroceriaGrupo5RadioNA.setText("N/A");
        jPanel18.add(CarroceriaGrupo5RadioNA, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 0, -1, 20));

        jPanel1.add(jPanel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, 740, 30));

        jPanel19.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel19.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel27.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jLabel27.setText("<html>Mal estado o problemas en el funcionamiento de los dispositivos de sujecion de las cabinas basculantes.</html>");
        jPanel19.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 581, 30));

        Group6Carroceria.add(CarroceriaGrupo6RadioSI);
        CarroceriaGrupo6RadioSI.setText("SI");
        jPanel19.add(CarroceriaGrupo6RadioSI, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 0, -1, 20));

        Group6Carroceria.add(CarroceriaGrupo6RadioNO);
        CarroceriaGrupo6RadioNO.setText("NO");
        jPanel19.add(CarroceriaGrupo6RadioNO, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 0, -1, 20));

        Group6Carroceria.add(CarroceriaGrupo6RadioNA);
        CarroceriaGrupo6RadioNA.setText("N/A");
        jPanel19.add(CarroceriaGrupo6RadioNA, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 0, -1, 20));

        jPanel1.add(jPanel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 160, 740, -1));

        jPanel20.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel20.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel28.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jLabel28.setText("<html>Mal estado de los elementos de sujecion de la carroceria al chasis</html>");
        jPanel20.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 6, 581, 20));

        Group7Carroceria.add(CarroceriaGrupo7RadioSI);
        CarroceriaGrupo7RadioSI.setText("SI");
        jPanel20.add(CarroceriaGrupo7RadioSI, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 0, -1, 20));

        Group7Carroceria.add(CarroceriaGrupo7RadioNO);
        CarroceriaGrupo7RadioNO.setText("NO");
        jPanel20.add(CarroceriaGrupo7RadioNO, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 0, -1, 20));

        Group7Carroceria.add(CarroceriaGrupo7RadioNA);
        CarroceriaGrupo7RadioNA.setText("N/A");
        jPanel20.add(CarroceriaGrupo7RadioNA, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 0, -1, 20));

        jPanel1.add(jPanel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 190, 740, 30));

        jPanel21.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel21.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel29.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jLabel29.setText("<html>Presencia de fisuras, cortes, dobleces o corrosión de los largueros y travesaños del chasis<html>");
        jPanel21.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 581, 26));

        jPanel22.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel22.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel30.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jLabel30.setText("<html>Presencia de fisuras, cortes, dobleces o corrosión de los largueros y travesaños del chasis<html>");
        jPanel22.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 581, 26));

        jPanel21.add(jPanel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 250, 740, 30));

        Group8Carroceria.add(CarroceriaGrup8RadioSI);
        CarroceriaGrup8RadioSI.setText("SI");
        jPanel21.add(CarroceriaGrup8RadioSI, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 0, -1, 20));

        Group8Carroceria.add(CarroceriaGrupo8RadioNO);
        CarroceriaGrupo8RadioNO.setText("NO");
        jPanel21.add(CarroceriaGrupo8RadioNO, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 0, -1, 20));

        Group8Carroceria.add(CarroceriaGrupo8RadioNA);
        CarroceriaGrupo8RadioNA.setText("N/A");
        jPanel21.add(CarroceriaGrupo8RadioNA, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 0, -1, 20));

        jPanel1.add(jPanel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 220, 740, 30));

        jPanel23.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel23.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel31.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jLabel31.setText("<html>Roce o interferencia entre las llantas y el guardabarros, carroceria o suspension</html>");
        jPanel23.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 581, 26));

        Group9Carroceria.add(CarroceriaGrup9RadioSI);
        CarroceriaGrup9RadioSI.setText("SI");
        jPanel23.add(CarroceriaGrup9RadioSI, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 0, -1, 20));

        Group9Carroceria.add(CarroceriaGrupo9RadioNO);
        CarroceriaGrupo9RadioNO.setText("NO");
        jPanel23.add(CarroceriaGrupo9RadioNO, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 0, -1, 20));

        Group9Carroceria.add(CarroceriaGrupo9RadioNA);
        CarroceriaGrupo9RadioNA.setText("N/A");
        jPanel23.add(CarroceriaGrupo9RadioNA, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 0, -1, 20));

        jPanel1.add(jPanel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 250, 740, 30));

        jPanel24.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel24.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel32.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jLabel32.setText("<html>Perforaciones que epermitan la entrada de agua o gases en el habitaculo de pasajeros.</html>");
        jPanel24.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 581, 26));

        Group10Carroceria.add(CarroceriaGrup10RadioSI);
        CarroceriaGrup10RadioSI.setText("SI");
        jPanel24.add(CarroceriaGrup10RadioSI, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 0, -1, 20));

        Group10Carroceria.add(CarroceriaGrupo10RadioNO);
        CarroceriaGrupo10RadioNO.setText("NO");
        jPanel24.add(CarroceriaGrupo10RadioNO, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 0, -1, 20));

        Group10Carroceria.add(CarroceriaGrupo10RadioNA);
        CarroceriaGrupo10RadioNA.setText("N/A");
        jPanel24.add(CarroceriaGrupo10RadioNA, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 0, -1, 20));

        jPanel1.add(jPanel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 280, 740, 30));

        jPanel25.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel25.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel33.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jLabel33.setText("<html>Inexistencia o riesgo de desprendimiento de defensas o parachoques</html>");
        jPanel25.add(jLabel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 581, 26));

        jPanel26.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel26.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel34.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jLabel34.setText("<html>Inexistencia o riesgo de desprendimiento de defensas o parachoques</html>");
        jPanel26.add(jLabel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 581, 26));

        jPanel25.add(jPanel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 340, 740, 30));

        Group11Carroceria.add(CarroceriaGrup11RadioSI);
        CarroceriaGrup11RadioSI.setText("SI");
        jPanel25.add(CarroceriaGrup11RadioSI, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 0, -1, 20));

        Group11Carroceria.add(CarroceriaGrupo11RadioNO);
        CarroceriaGrupo11RadioNO.setText("NO");
        jPanel25.add(CarroceriaGrupo11RadioNO, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 0, -1, 20));

        Group11Carroceria.add(CarroceriaGrupo11RadioNA);
        CarroceriaGrupo11RadioNA.setText("N/A");
        jPanel25.add(CarroceriaGrupo11RadioNA, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 0, -1, 20));

        jPanel1.add(jPanel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 310, 740, 30));

        jPanel27.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel27.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel35.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jLabel35.setText("Corrosion o mal estado de la carroceria");
        jPanel27.add(jLabel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 581, 26));

        Group12Carroceria.add(CarroceriaGrup12RadioSI);
        CarroceriaGrup12RadioSI.setText("SI");
        jPanel27.add(CarroceriaGrup12RadioSI, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 0, 40, 20));

        Group12Carroceria.add(CarroceriaGrupo12RadioNO);
        CarroceriaGrupo12RadioNO.setText("NO");
        jPanel27.add(CarroceriaGrupo12RadioNO, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 0, -1, 20));

        Group12Carroceria.add(CarroceriaGrupo12RadioNA);
        CarroceriaGrupo12RadioNA.setText("N/A");
        jPanel27.add(CarroceriaGrupo12RadioNA, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 0, -1, 20));

        jPanel1.add(jPanel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 340, 740, 30));

        jPanel28.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel28.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel36.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jLabel36.setText("Mal estado de parachoques y/o defensas");
        jPanel28.add(jLabel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 581, 26));

        Group13Carroceria.add(CarroceriaGrup13RadioSI);
        CarroceriaGrup13RadioSI.setText("SI");
        jPanel28.add(CarroceriaGrup13RadioSI, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 0, -1, 20));

        Group13Carroceria.add(CarroceriaGrupo13RadioNO);
        CarroceriaGrupo13RadioNO.setText("NO");
        jPanel28.add(CarroceriaGrupo13RadioNO, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 0, -1, 20));

        Group13Carroceria.add(CarroceriaGrupo13RadioNA);
        CarroceriaGrupo13RadioNA.setText("N/A");
        jPanel28.add(CarroceriaGrupo13RadioNA, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 0, -1, 20));

        jPanel1.add(jPanel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 370, 740, -1));

        jTabbedPane1.addTab("Carroceria y chasis", new javax.swing.ImageIcon(getClass().getResource("/com/soltelec/Icon/1334257877_muscle_car.png")), jPanel1); // NOI18N

        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        Panel1Placa.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        Panel1Placa.setEnabled(false);
        Panel1Placa.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel37.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jLabel37.setText("<html> La ubicacion de la placa en el techo en lugar diferente al eje longitudinal del vehiculo cualquiera sea la clase del vehiculo (debe estar colocada en cualquier punto a lo largo del eje longitudinal en forma perpendicular y centrada transversalmente   </html>");
        Panel1Placa.add(jLabel37, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 580, 50));

        Group1Placa.add(PlacasGrupo1SI);
        PlacasGrupo1SI.setText("SI");
        PlacasGrupo1SI.setEnabled(false);
        Panel1Placa.add(PlacasGrupo1SI, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 10, -1, 30));

        Group1Placa.add(PlacasGrupo1NO);
        PlacasGrupo1NO.setText("NO");
        PlacasGrupo1NO.setEnabled(false);
        Panel1Placa.add(PlacasGrupo1NO, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 10, -1, 30));

        Group1Placa.add(PlacasGrupo1NA);
        PlacasGrupo1NA.setText("N/A");
        PlacasGrupo1NA.setEnabled(false);
        Panel1Placa.add(PlacasGrupo1NA, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 10, -1, 30));

        jPanel9.add(Panel1Placa, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 740, 50));

        Panel2Placa.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        Panel2Placa.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel38.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jLabel38.setText("<html>La inexistencia de la placa impresa en los vehiculos de servicio publico en los costados o techo del vehiculo</html>");
        Panel2Placa.add(jLabel38, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 580, 40));

        Group2Placa.add(PlacasGrupo2SI);
        PlacasGrupo2SI.setText("SI");
        PlacasGrupo2SI.setEnabled(false);
        Panel2Placa.add(PlacasGrupo2SI, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 10, -1, 20));

        Group2Placa.add(PlacasGrupo2NO);
        PlacasGrupo2NO.setText("NO");
        PlacasGrupo2NO.setEnabled(false);
        Panel2Placa.add(PlacasGrupo2NO, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 10, -1, 20));

        Group2Placa.add(PlacasGrupo2NA);
        PlacasGrupo2NA.setText("N/A");
        PlacasGrupo2NA.setEnabled(false);
        Panel2Placa.add(PlacasGrupo2NA, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 10, -1, 20));

        PanelVidrio9.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        PanelVidrio9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel39.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jLabel39.setText("<html>La inexistencia de la placa impresa en los vehiculos de servicio publico en los costados o techo del vehiculo</html>");
        PanelVidrio9.add(jLabel39, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 580, 40));

        Grupo1.add(RadioButtonSi8);
        RadioButtonSi8.setText("SI");
        PanelVidrio9.add(RadioButtonSi8, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 10, -1, 20));

        Grupo1.add(RadioButtonNo8);
        RadioButtonNo8.setText("NO");
        PanelVidrio9.add(RadioButtonNo8, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 10, -1, 20));

        Grupo1.add(RadioButtonNA8);
        RadioButtonNA8.setText("N/A");
        PanelVidrio9.add(RadioButtonNA8, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 10, -1, 20));

        Panel2Placa.add(PanelVidrio9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 740, 40));

        jPanel9.add(Panel2Placa, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 740, 40));

        Panel3Placa.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        Panel3Placa.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel40.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jLabel40.setText("<html>La ubicacion de la placa en la parte externa lateral en un lugar diferente a la parte media de las puertas traseras en el caso de los vehiculos clase automovil y camioneta    </html>");
        Panel3Placa.add(jLabel40, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 580, 50));

        Group3Placa.add(PlacasGrupo3SI);
        PlacasGrupo3SI.setText("SI");
        PlacasGrupo3SI.setEnabled(false);
        Panel3Placa.add(PlacasGrupo3SI, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 10, -1, 30));

        Group3Placa.add(PlacasGrupo3NO);
        PlacasGrupo3NO.setText("NO");
        PlacasGrupo3NO.setEnabled(false);
        Panel3Placa.add(PlacasGrupo3NO, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 10, -1, 30));

        Group3Placa.add(PlacasGrupo3NA);
        PlacasGrupo3NA.setText("N/A");
        PlacasGrupo3NA.setEnabled(false);
        Panel3Placa.add(PlacasGrupo3NA, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 10, -1, 30));

        jPanel9.add(Panel3Placa, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, 740, -1));

        Panel4Placa.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        Panel4Placa.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel41.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jLabel41.setText("<html>La ubicacion de la placa en la parte externa lateral en un lugar diferente a la parte media de cada costado en el caso de los vehiculos clase bus, buseta y microbus </html>");
        Panel4Placa.add(jLabel41, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 580, 50));

        Group4Placa.add(PlacasGrupo4SI);
        PlacasGrupo4SI.setText("SI");
        PlacasGrupo4SI.setEnabled(false);
        Panel4Placa.add(PlacasGrupo4SI, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 10, -1, 30));

        Group4Placa.add(PlacasGrupo4NO);
        PlacasGrupo4NO.setText("NO");
        PlacasGrupo4NO.setEnabled(false);
        Panel4Placa.add(PlacasGrupo4NO, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 10, -1, 30));

        Group4Placa.add(PlacasGrupo4NA);
        PlacasGrupo4NA.setText("N/A");
        PlacasGrupo4NA.setEnabled(false);
        Panel4Placa.add(PlacasGrupo4NA, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 10, -1, 30));

        jPanel9.add(Panel4Placa, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, 740, 50));

        Panel5Placa.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        Panel5Placa.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel42.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jLabel42.setText("<html> La ubicacion de la placa (en la parte externa) en lugar diferente a la parte media del segundo cuerpo de cada costado del vehiculo en los vehiculos de servicio publico de transporte masivo, tipo articulado o biarticulado   </html>");
        Panel5Placa.add(jLabel42, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 580, 50));

        Group5Placa.add(PlacasGrupo5SI);
        PlacasGrupo5SI.setText("SI");
        PlacasGrupo5SI.setEnabled(false);
        Panel5Placa.add(PlacasGrupo5SI, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 10, -1, 30));

        Group5Placa.add(PlacasGrupo5NO);
        PlacasGrupo5NO.setText("NO");
        PlacasGrupo5NO.setEnabled(false);
        Panel5Placa.add(PlacasGrupo5NO, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 10, -1, 30));

        Group5Placa.add(PlacasGrupo5NA);
        PlacasGrupo5NA.setText("N/A");
        PlacasGrupo5NA.setEnabled(false);
        Panel5Placa.add(PlacasGrupo5NA, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 10, -1, 30));

        jPanel9.add(Panel5Placa, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 200, 740, -1));

        Panel6Placa.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        Panel6Placa.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel43.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jLabel43.setText("<html>La ubicacion de la placa en la parte externa lateral en un lugar diferente a la parte media de las puertas de la cabina en el caso de los vehiculos de transporte de carga </html>");
        Panel6Placa.add(jLabel43, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 580, 40));

        Group6Placa.add(PlacasGrupo6SI);
        PlacasGrupo6SI.setText("SI");
        PlacasGrupo6SI.setEnabled(false);
        Panel6Placa.add(PlacasGrupo6SI, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 10, -1, 20));

        Group6Placa.add(PlacasGrupo6NO);
        PlacasGrupo6NO.setText("NO");
        PlacasGrupo6NO.setEnabled(false);
        Panel6Placa.add(PlacasGrupo6NO, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 10, -1, 20));

        Group6Placa.add(PlacasGrupo6NA);
        PlacasGrupo6NA.setText("N/A");
        PlacasGrupo6NA.setEnabled(false);
        Panel6Placa.add(PlacasGrupo6NA, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 10, -1, 20));

        jPanel9.add(Panel6Placa, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 250, 740, 40));

        Panel7Placa.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        Panel7Placa.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel44.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jLabel44.setText("<html>La ubicacion de la placa en la parte externa lateral en un lugar diferente a la parte media de las puertas delanteras en caso de los vehiculos tipo campero    </html>");
        Panel7Placa.add(jLabel44, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 580, 40));

        Group7Placa.add(PlacasGrupo7SI);
        PlacasGrupo7SI.setText("SI");
        PlacasGrupo7SI.setEnabled(false);
        Panel7Placa.add(PlacasGrupo7SI, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 10, -1, 20));

        Group7Placa.add(PlacasGrupo7NO);
        PlacasGrupo7NO.setText("NO");
        PlacasGrupo7NO.setEnabled(false);
        Panel7Placa.add(PlacasGrupo7NO, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 10, -1, 20));

        Group7Placa.add(PlacasGrupo7NA);
        PlacasGrupo7NA.setText("N/A");
        PlacasGrupo7NA.setEnabled(false);
        Panel7Placa.add(PlacasGrupo7NA, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 10, -1, 20));

        jPanel9.add(Panel7Placa, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 290, 740, 40));

        jTabbedPane1.addTab("Placas", jPanel9);

        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        Panel1Retrovisores.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        Panel1Retrovisores.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel12.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jLabel12.setText("<html>La inexistencia de al menos dos espejos retrovisores funcionales e independientes,<br> o cámaras que  cumplan esta función</html>");
        Panel1Retrovisores.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 530, 40));

        Group1RetroVisores.add(RetroGrupo1RadioNA);
        RetroGrupo1RadioNA.setText("NA");
        Panel1Retrovisores.add(RetroGrupo1RadioNA, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 10, -1, -1));

        Group1RetroVisores.add(RetroGrupo1RadioSI);
        RetroGrupo1RadioSI.setText("SI");
        Panel1Retrovisores.add(RetroGrupo1RadioSI, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 10, -1, -1));

        Group1RetroVisores.add(RetroGrupo1RadioNO);
        RetroGrupo1RadioNO.setText("NO");
        Panel1Retrovisores.add(RetroGrupo1RadioNO, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 10, -1, -1));

        jPanel6.add(Panel1Retrovisores, new org.netbeans.lib.awtextra.AbsoluteConstraints(19, 11, 720, -1));

        Panel1Retrovisores1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        Panel1Retrovisores1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel13.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jLabel13.setText("<html>Estado de las superficies o fijación deficiente de cualquier espejo<br> retrovisor funcional</html>");
        Panel1Retrovisores1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 530, 40));

        Group2RetroVisores.add(RetroGrupo2RadioNA);
        RetroGrupo2RadioNA.setText("NA");
        Panel1Retrovisores1.add(RetroGrupo2RadioNA, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 10, -1, -1));

        Group2RetroVisores.add(RetroGrupo2RadioSI);
        RetroGrupo2RadioSI.setText("SI");
        Panel1Retrovisores1.add(RetroGrupo2RadioSI, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 10, -1, -1));

        Group2RetroVisores.add(RetroGrupo2RadioNO);
        RetroGrupo2RadioNO.setText("NO");
        Panel1Retrovisores1.add(RetroGrupo2RadioNO, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 10, -1, -1));

        jPanel6.add(Panel1Retrovisores1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, 720, -1));

        jTabbedPane1.addTab("Retrovisores", new javax.swing.ImageIcon(getClass().getResource("/com/soltelec/Icon/tray_24.png")), jPanel6); // NOI18N

        jLabel2.setFont(new java.awt.Font("Serif", 1, 36)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("<html><center>REVISION EXTERIOR</center></html>");

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/soltelec/Icon/save_24.png"))); // NOI18N
        jButton1.setText("Guardar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/soltelec/Icon/auto.png"))); // NOI18N

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/soltelec/Icon/salir24.png"))); // NOI18N
        jButton2.setText("cancelar");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel8Layout.createSequentialGroup()
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 764, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(50, 50, 50)
                        .addComponent(jLabel3)
                        .addGap(56, 56, 56)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 431, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(33, 33, 33)
                .addComponent(jTabbedPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton1)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        setVisible(false);
        dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        System.out.println("-----------------------------------------------------");
        System.out.println("--Prueba de Inspecion sensorial----------------------");
        System.out.println("-----------------------------------------------------");

        try 
        {
            NoReiniciable p = new NoReiniciable();
            p = Frm_Placas.placas_bd;
            boolean persistir = false;
            establecerGrupo("Revisión Exterior: ");

            conexion = llamarConexion();
            if (conexion == null) {
                return;
            }
//            idPrueba=18224108;
            persistir = vidrios(persistir);
            persistir = limpiaBrisas(persistir);
            persistir = peldanos(persistir);
            persistir = retroVisores(persistir);
            persistir = dispositivosRetenciónCarga(persistir);
            persistir = soporteRuedasRepuesto(persistir);
            persistir = carroceriaChasis(persistir);
            persistir = defectosPlaca(persistir);

            cerrarConexion(persistir);

            establecerAprobado(defecto);
            aprobado = obtenberAprobado();

            establecerCantidadProblemas(contadorA, contadorB);
            doClose(0);
            nrei.establecer_oprimido(true);

        } catch (Exception e) 
        {
            System.err.println("Error en Prueba de Inspecion sensorial " +e.getMessage());
        }
    }//GEN-LAST:event_jButton1ActionPerformed
   
    
    private void cerrarConexion(boolean persistir)
    {
        try 
        {
            if (persistir == true) 
            {
                conexion.commit();
                conexion.setAutoCommit(true);
                JOptionPane.showMessageDialog(this, "Se ha REGISTRADO los Defectos de la Revision Exterior de una Manera Exitosa ");
            }else{
                JOptionPane.showMessageDialog(this, "No se registran datos en la DB: No se evidencio seleciones");
            }
            conexion.close();
        } catch (SQLException ex) {
            Mensajes.mostrarExcepcion(ex);
        }
    }
    
    
    public Connection llamarConexion() 
    {
        try 
        {
            Class.forName("com.mysql.jdbc.Driver");
            Connection cn = (Connection) DriverManager.getConnection("jdbc:mysql://" + Conexion.getIpServidor() + ":"+Conexion.getPuerto() +  "/"+ Conexion.getBaseDatos() , Conexion.getUsuario(),Conexion.getContraseña());
//            Connection cn = (Connection) DriverManager.getConnection("jdbc:mysql://192.168.0.45:3306/db_cda", "root", "50lt3l3c545");
            return cn;
        } catch (ClassNotFoundException | SQLException ex) 
        {
            System.out.println("Error en el metodo llamarConexion() : PruebaInspecionSencorial: " +ex.getMessage());
            Mensajes.mostrarExcepcion(ex);
            JOptionPane.showMessageDialog(null, "Error al conectar con la db");
        }
        return null;
    }
    
    /**
     * Metodo que permite capturar los defectos selecionados para placas 
     * @autor ELKIN B
     * @param persistir
     * @return 
     */
    private boolean defectosPlaca(boolean persistir) 
    {
        System.out.println("-----------------------------------------------------");
        System.out.println("-----------Cargarndo defectos de placa----------------");
        System.out.println("-----------------------------------------------------");
        try 
        {
            if (!PlacasGrupo1NO.isSelected()) 
            {
                String opcionSelecinada = capturarOpcionSelecionadaPlaca("Group1Placa");
                persistir = registrarDefectos(10128, idPrueba, opcionSelecinada);
            }

            if (!PlacasGrupo2NO.isSelected()) 
            {
                String opcionSelecinada = capturarOpcionSelecionadaPlaca("Group2Placa");
                persistir = registrarDefectos(10131, idPrueba, opcionSelecinada);
            }
            
            if (!PlacasGrupo3NO.isSelected())
            {
                String opcionSelecinada = capturarOpcionSelecionadaPlaca("Group3Placa");
                persistir = registrarDefectos(10126, idPrueba, opcionSelecinada);
            }
            
            if (!PlacasGrupo4NO.isSelected())
            {
                String opcionSelecinada = capturarOpcionSelecionadaPlaca("Group4Placa");
                persistir = registrarDefectos(10127, idPrueba, opcionSelecinada);
            }
            
            if (!PlacasGrupo5NO.isSelected()) 
            {
                String opcionSelecinada = capturarOpcionSelecionadaPlaca("Group5Placa");
                persistir = registrarDefectos(10132, idPrueba, opcionSelecinada);
            }

            if (!PlacasGrupo6NO.isSelected())
            {
                String opcionSelecinada = capturarOpcionSelecionadaPlaca("Group6Placa");
                persistir = registrarDefectos(10130, idPrueba, opcionSelecinada);
            }
            
            if (!PlacasGrupo7NO.isSelected()) 
            {
                String opcionSelecinada = capturarOpcionSelecionadaPlaca("Group7Placa");
                persistir = registrarDefectos(10129, idPrueba, opcionSelecinada);
            }

        } catch (Exception e) {
            System.err.println("Error en el metodo :defectosplaca()" + e.getMessage());
        }
        return persistir;
    }

    
    private String capturarOpcionSelecionadaPlaca(String grupo)
    {
        String respuesta = "";
        if (grupo.equalsIgnoreCase("Group1Placa")) 
        {
            if (PlacasGrupo1SI.isSelected())
                respuesta = "SI";
            else if (PlacasGrupo1NA.isSelected())
                respuesta = "NA";
        }

        if (grupo.equalsIgnoreCase("Group2Placa")) 
        {
            if (PlacasGrupo2SI.isSelected())
                respuesta = "SI";
            else if (PlacasGrupo2NA.isSelected())
                respuesta = "NA";
        }
        if (grupo.equalsIgnoreCase("Group3Placa")) 
        {
            if (PlacasGrupo3SI.isSelected())
                respuesta = "SI";
            else if (PlacasGrupo3NA.isSelected())
                respuesta = "NA";
        }
        
        if (grupo.equalsIgnoreCase("Group4Placa")) 
        {
            if (PlacasGrupo4SI.isSelected())
                respuesta = "SI";
            else if (PlacasGrupo4NA.isSelected())
                respuesta = "NA";
        }
        
        if (grupo.equalsIgnoreCase("Group5Placa")) 
        {
            if (PlacasGrupo5SI.isSelected())
                respuesta = "SI";
            else if (PlacasGrupo5NA.isSelected())
                respuesta = "NA";
        }
        
        if (grupo.equalsIgnoreCase("Group6Placa")) 
        {
            if (PlacasGrupo6SI.isSelected())
                respuesta = "SI";
            else if (PlacasGrupo6NA.isSelected())
                respuesta = "NA";
        }
        
        if (grupo.equalsIgnoreCase("Group7Placa"))
        {
            if (PlacasGrupo7SI.isSelected())
                respuesta = "SI";
            else if (PlacasGrupo7NA.isSelected())
                respuesta = "NA";
        }
        return respuesta;
    }
    
    
    
    /**
     * Metodo que permite capturar los defectos selecionados para soporte de 
     * ruedas reuesto
     * @autor ELKIN B
     * @param persistir
     * @return 
     */
    private boolean soporteRuedasRepuesto(boolean persistir)
    {
        System.out.println("-----------------------------------------------------");
        System.out.println("--Cargarndo defectos de soporteRuedasRepuesto----------");
        System.out.println("-----------------------------------------------------");
        try
        {
            if (!SoporteGrupo1RadioNO.isSelected()) 
            {
                String opcionSelecinada = capturarOpcionSelecionadaSoporte("Group1Soporte");
                persistir = registrarDefectos(10016, idPrueba, opcionSelecinada);
            }
 
            if (!SoporteGrupo2RadioNO.isSelected()) 
            {
                String opcionSelecinada = capturarOpcionSelecionadaSoporte("Group2Soporte");
                persistir = registrarDefectos(10056, idPrueba, opcionSelecinada);
            }

        } catch (Exception e) {
            System.err.println("Error en el metodo :soporteRuedasRepuesto()" + e.getMessage());
        }
        return persistir;
    }
    
    
    private String capturarOpcionSelecionadaSoporte(String grupo)
    {
        String opcion1 = "";
        if (grupo.equalsIgnoreCase("Group1Soporte")) 
        {
            if (SoporteGrupo1RadioSI.isSelected())
                opcion1 = "SI";
            else if (SoporteGrupo1RadioNA.isSelected())
                opcion1 = "NA";
        }
        
        if (grupo.equalsIgnoreCase("Group2Soporte")) 
        {
            if (SoporteGrupo2RadioSI.isSelected())
                opcion1 = "SI";
            else if (SoporteGrupo2RadioNA.isSelected())
                opcion1 = "NA";
        }
        return opcion1;
    }
    
    /**
     * Metodo que permite capturar los defectos selecionados para carroceria y chasis
     * @autor ELKIN B
     * @param persistir
     * @return 
     */
    private boolean carroceriaChasis(boolean persistir) 
    {
        System.out.println("-----------------------------------------------------");
        System.out.println("--Cargarndo defectos de carroceriaChasis--------------");
        System.out.println("-----------------------------------------------------");
        try 
        {
            if (!CarroceriaGrupo1RadioNO.isSelected()) 
            {
                String opcionSelecinada = capturarOpcionSelecionadaCarroceria("Group1Carroceria");
                persistir = registrarDefectos(10000, idPrueba, opcionSelecinada);
            }
            
            if (!CarroceriaGrupo2RadioNO.isSelected()) 
            {
                String opcionSelecinada = capturarOpcionSelecionadaCarroceria("Group2Carroceria");
                persistir = registrarDefectos(10001, idPrueba, opcionSelecinada);
            }
            
            if (!CarroceriaGrupo3RadioNO.isSelected()) 
            {
                String opcionSelecinada = capturarOpcionSelecionadaCarroceria("Group3Carroceria");
                persistir = registrarDefectos(10002, idPrueba, opcionSelecinada);
            }
            
            if (!CarroceriaGrupo4RadioNO.isSelected()) 
            {
                String opcionSelecinada = capturarOpcionSelecionadaCarroceria("Group4Carroceria");
                persistir = registrarDefectos(10003, idPrueba, opcionSelecinada);
            }
            
            if (!CarroceriaGrupo5RadioNO.isSelected()) {
                String opcionSelecinada = capturarOpcionSelecionadaCarroceria("Group5Carroceria");
                persistir = registrarDefectos(10004, idPrueba, opcionSelecinada);
            }

            if (!CarroceriaGrupo6RadioNO.isSelected()) 
            {
                String opcionSelecinada = capturarOpcionSelecionadaCarroceria("Group6Carroceria");
                persistir = registrarDefectos(10005, idPrueba, opcionSelecinada);
            }
            if (!CarroceriaGrupo7RadioNO.isSelected()) 
            {
                String opcionSelecinada = capturarOpcionSelecionadaCarroceria("Group7Carroceria");
                persistir = registrarDefectos(10006, idPrueba, opcionSelecinada);
            }
            if (!CarroceriaGrupo8RadioNO.isSelected())
            {
                String opcionSelecinada = capturarOpcionSelecionadaCarroceria("Group8Carroceria");
                persistir = registrarDefectos(10007, idPrueba, opcionSelecinada);
            }

            if (!CarroceriaGrupo9RadioNO.isSelected())
            {
                String opcionSelecinada = capturarOpcionSelecionadaCarroceria("Group9Carroceria");
                persistir = registrarDefectos(10008, idPrueba, opcionSelecinada);
            }

            if (!CarroceriaGrupo13RadioNO.isSelected()) 
            {
                String opcionSelecinada = capturarOpcionSelecionadaCarroceria("Group13Carroceria");
                persistir = registrarDefectos(10009, idPrueba, opcionSelecinada);
            }

            if (!CarroceriaGrupo10RadioNO.isSelected())
            {
                String opcionSelecinada = capturarOpcionSelecionadaCarroceria("Group10Carroceria");
                persistir = registrarDefectos(10010, idPrueba, opcionSelecinada);
            }
            if (!CarroceriaGrupo11RadioNO.isSelected()) {
                String opcionSelecinada = capturarOpcionSelecionadaCarroceria("Group11Carroceria");
                persistir = registrarDefectos(10011, idPrueba, opcionSelecinada);
            }
            
            if (!CarroceriaGrupo12RadioNO.isSelected()) 
            {
                String opcionSelecinada = capturarOpcionSelecionadaCarroceria("Group12Carroceria");
                persistir = registrarDefectos(10012, idPrueba, opcionSelecinada);
            }
 
        } catch (Exception e)
        {
            System.err.println("Error en el metodo :carroceriaChasis()" + e.getMessage());
        }

        return persistir;
    }

    
    private String capturarOpcionSelecionadaCarroceria(String grupo)
    {
        String respuesta="";
        if (grupo.equalsIgnoreCase("Group1Carroceria")) 
        {
            if (CarroceriaGrupo1RadioSI.isSelected())
                respuesta= "SI";
            else if (CarroceriaGrupo1RadioNA.isSelected())
                respuesta= "NA";
        }
        
        if (grupo.equalsIgnoreCase("Group2Carroceria")) 
        {
            if (CarroceriaGrupo2RadioSI.isSelected())
                respuesta = "SI";
            else if (CarroceriaGrupo2RadioNA.isSelected())
                respuesta = "NA";
        }

        if (grupo.equalsIgnoreCase("Group3Carroceria"))
        {
            if (CarroceriaGrupo3RadioSI.isSelected())
                respuesta = "SI";
            else if (CarroceriaGrupo3RadioNA.isSelected())
                respuesta = "NA";
        }

        if (grupo.equalsIgnoreCase("Group4Carroceria"))
        {
            if (CarroceriaGrupo4RadioSI.isSelected())
                respuesta = "SI";
            else if (CarroceriaGrupo4RadioNA.isSelected())
                respuesta = "NA";
        }
        if (grupo.equalsIgnoreCase("Group5Carroceria")) {
            if (CarroceriaGrupo5RadioSI.isSelected()) 
                respuesta = "SI";
            else if (CarroceriaGrupo5RadioNA.isSelected())
                respuesta = "NA";
        }
        if (grupo.equalsIgnoreCase("Group6Carroceria")) {
            if (CarroceriaGrupo6RadioSI.isSelected())
                respuesta = "SI";
            else if (CarroceriaGrupo6RadioNA.isSelected())
                respuesta = "NA";
        }
        if (grupo.equalsIgnoreCase("Group7Carroceria")){
            if (CarroceriaGrupo7RadioSI.isSelected()) 
                respuesta = "SI";
             else if (CarroceriaGrupo7RadioNA.isSelected())
                respuesta = "NA";
        }
        
        if (grupo.equalsIgnoreCase("Group8Carroceria")) {
            if (CarroceriaGrup8RadioSI.isSelected())
                respuesta = "SI";
            else if (CarroceriaGrupo8RadioNA.isSelected())
                respuesta = "NA";
        }
    
        if (grupo.equalsIgnoreCase("Group9Carroceria")) 
        {
            if (CarroceriaGrup9RadioSI.isSelected())
                respuesta = "SI";
            else if (CarroceriaGrupo9RadioNA.isSelected())
                respuesta = "NA";
        }
        
        if (grupo.equalsIgnoreCase("Group10Carroceria")) {
            if (CarroceriaGrup10RadioSI.isSelected())
                respuesta = "SI";
            else if (CarroceriaGrupo10RadioNA.isSelected())
                respuesta = "NA";
        }

        if (grupo.equalsIgnoreCase("Group11Carroceria")) {
            if (CarroceriaGrup11RadioSI.isSelected())
                respuesta = "SI";
            else if (CarroceriaGrupo11RadioNA.isSelected())
                respuesta = "NA";
        }
        
        if (grupo.equalsIgnoreCase("Group12Carroceria"))
        {
            if (CarroceriaGrup12RadioSI.isSelected())
                respuesta = "SI";
            else if (CarroceriaGrupo12RadioNA.isSelected())
                respuesta = "NA";
        }
        
        if (grupo.equalsIgnoreCase("Group13Carroceria"))
        {
            if (CarroceriaGrup13RadioSI.isSelected())
                respuesta = "SI";
            else if (CarroceriaGrupo13RadioNA.isSelected())
                respuesta = "NA";
        }
                        
        return respuesta;
    }
    
    
    
    /**
     * Metodo que permite capturar los defectos selecionados para peldanos
     * 
     * @autor ELKIN B
     * @param persistir
     * @return 
     */
    private boolean peldanos(boolean persistir) 
    {
        System.out.println("-----------------------------------------------------");
        System.out.println("--Cargarndo defectos de peldanos-------------------");
        System.out.println("-----------------------------------------------------");
        try 
        {
            if (!PeldanosGrupo1RadioButtonNO.isSelected()) 
            {
                String opcionSelecinadaGrupo1 = capturarOpcionSelecionadaPeldanos("Group1Peldanos");
                persistir = registrarDefectos(10055, idPrueba, opcionSelecinadaGrupo1);
            }
        } catch (Exception e) {
            System.err.println("Error en el metodo :peldanos()" + e.getMessage());
        }
        return persistir;
    }

    
    private String capturarOpcionSelecionadaPeldanos(String grupo)
    {
        String respuesta="";
        if (grupo.equalsIgnoreCase("Group1Peldanos")) 
        {
            if (PeldanosGrupo1RadioButtonSI.isSelected()) {
                respuesta= "SI";
            } else if (PeldanosGrupo1RadioButtonNA.isSelected()) {
                respuesta= "NA";
            }
        }
        return respuesta;
    }
    
    
    
    /**
     * Metodo que permite capturar los defectos selecionados para retrovisores
     * 
     * @autor ELKIN B
     * @param persistir
     * @return 
     */
    private boolean retroVisores(boolean persistir)
    {
        System.out.println("-----------------------------------------------------");
        System.out.println("--Cargarndo defetos de retroVisores-------------------");
        System.out.println("-----------------------------------------------------"); 
        try
        {
            if (!RetroGrupo1RadioNO.isSelected()) 
            {
                String opcionSelecinada = capturarOpcionSelecionadaRetroV("Group1RetroVisores");
                persistir = registrarDefectos(10014, idPrueba, opcionSelecinada);
            }
            
            if (!RetroGrupo2RadioNO.isSelected()) 
            {
                String opcionSelecinada = capturarOpcionSelecionadaRetroV("Group2RetroVisores");
                persistir = registrarDefectos(10015, idPrueba, opcionSelecinada);
            }
        } catch (Exception e) 
        {
            System.err.println("Error en el metodo :retroVisores()" +e.getMessage());
        }
        return persistir;
    }
    
    
    private String capturarOpcionSelecionadaRetroV(String grupo)
    {
        String respuesta = "";
        if (grupo.equalsIgnoreCase("Group1RetroVisores")) 
        {
            if (RetroGrupo1RadioSI.isSelected())
                respuesta = "SI";
            else if (RetroGrupo1RadioNA.isSelected()) 
                respuesta = "NA";
        }

        if (grupo.equalsIgnoreCase("Group2RetroVisores"))
        {
            if (RetroGrupo2RadioSI.isSelected())
                respuesta = "SI";
            else if (RetroGrupo2RadioNA.isSelected())
                respuesta = "NA";
        }
        return respuesta;
    }
    
   
    /**
     * @autor ELKIN B
     * 
     * Metodo que permite capturar los defectos selecionados para limpia brizas
     * 
     * @param persistir
     * @return 
     */
    private boolean limpiaBrisas(boolean persistir)
    {
        System.out.println("-----------------------------------------------------");
        System.out.println("--Cargarndo defectos de limpiaBrisas-------------------");
        System.out.println("-----------------------------------------------------"); 
        try
        {
            if (!Panel1LimpiaBrisasNO.isSelected()) 
            {
                String opcionSelecinada=capturarOpcionSelecionadaLimpiaB("GroupLimpiBrizas1");
                persistir=registrarDefectos(10054,idPrueba,opcionSelecinada);
            }   
        } catch (Exception e) {
            System.err.println("Error en el metodo :LimpiaBrisas()" +e.getMessage());
        }
        
        return persistir;
    }
    
    private String capturarOpcionSelecionadaLimpiaB(String grupo)
    {
        String respuesta="";
        if (grupo.equalsIgnoreCase("GroupLimpiBrizas1")) 
        {
            if (Panel1LimpiaBrisasSi.isSelected()) {
                respuesta= "SI";
            } else if (Panel1LimpiaBrisasNA.isSelected()) {
                respuesta= "NA";
            }
        }
        return respuesta;
    }
    
    
    /**
     * @autor ELKIN B
     * 
     * Metodo que permite capturar los defectos selecionados para dispositivos
     * de Retención deCarga
     * 
     * @param persistir
     * @return 
     */
    private boolean dispositivosRetenciónCarga(boolean persistir) 
    {
        System.out.println("-----------------------------------------------------");
        System.out.println("--Cargarndo defectos de Dispositivos Retención Carga--");
        System.out.println("-----------------------------------------------------");
        try {
            
            if (!RetencionGrupo1RadioNO.isSelected())
            {
                String opcionSelecinada = capturarOpcionSelecionadarRetencion("Group1Retencion");
                persistir = registrarDefectos(10023, idPrueba, opcionSelecinada);
            }
            
            if (!RetencionGrupo2RadioNO.isSelected()) 
            {
                String opcionSelecinada = capturarOpcionSelecionadarRetencion("Group2Retencion");
                persistir = registrarDefectos(10024, idPrueba, opcionSelecinada);
            }
            
            if (!RetencionGrupo3RadioNO.isSelected()) 
            {
                String opcionSelecinada = capturarOpcionSelecionadarRetencion("Group3Retencion");
                persistir = registrarDefectos(10025, idPrueba, opcionSelecinada);
            }

        } catch (Exception e) {
            System.err.println("Error en el metodo : dispositivosRetenciónCarga()" + e.getMessage());
        }
        return persistir;
    }
    
    
    private String capturarOpcionSelecionadarRetencion(String grupo)
    {
        String respuesta = "";
        if (grupo.equalsIgnoreCase("Group1Retencion")) 
        {
            if (RetencionGrupo1RadioSI.isSelected())
                respuesta = "SI";
            else if (RetencionGrupo1RadioNA.isSelected())
                respuesta = "NA"; 
        }

        if (grupo.equalsIgnoreCase("Group2Retencion")) 
        {
            if (RetencionGrupo2RadioSI.isSelected())
                respuesta = "SI";
            else if (RetencionGrupo2RadioNA.isSelected())
                respuesta = "NA";
        }
        
        if (grupo.equalsIgnoreCase("Group3Retencion")) 
        {
            if (RetencionGrupo3RadioSI.isSelected())
                respuesta = "SI";
            else if (RetencionGrupo3RadioNA.isSelected())
                respuesta = "NA";
        }
        return respuesta;
    }

    
    
    /**
     * @autor ELKIN B
     * 
     * Metodo que permite capturar los defectos selecionados para vidrios
     * 
     * @param persistir
     * @return 
     */
    private boolean vidrios(boolean persistir) 
    {
        System.out.println("-----------------------------------------------------");
        System.out.println("--Cargarndo defectos de Dispositivos Retención Carga--");
        System.out.println("-----------------------------------------------------");
        
        try 
        {
            if (!RadioButtonNo.isSelected()) 
            {
                String opcionSelecinadaGrupo1 = capturarOpcionSelecionadaVidrios("Grupo1");
                persistir = registrarDefectos(10017, idPrueba, opcionSelecinadaGrupo1);
            }

            if (!RadioButtonNo2.isSelected()) 
            {
                String opcionSelecinadaGrupo2 = capturarOpcionSelecionadaVidrios("Grupo2");
                persistir = registrarDefectos(10018, idPrueba, opcionSelecinadaGrupo2);
            }

            if (!RadioButtonNo3.isSelected()) 
            {
                String opcionSelecinadaGrupo3 = capturarOpcionSelecionadaVidrios("Grupo3");
                persistir = registrarDefectos(10019, idPrueba, opcionSelecinadaGrupo3);
            }

            if (!RadioButtonNo4.isSelected()) 
            {
                String opcionSelecinadaGrupo4 = capturarOpcionSelecionadaVidrios("Grupo4");
                persistir = registrarDefectos(10020, idPrueba, opcionSelecinadaGrupo4);
            }
            
            if (!RadioButtonNo5.isSelected()) 
            {
                String opcionSelecinadaGrupo5 = capturarOpcionSelecionadaVidrios("Grupo5");
                persistir = registrarDefectos(10021, idPrueba, opcionSelecinadaGrupo5);

            }
                 
            if (!RadioButtonNo6.isSelected()) 
            {
                String opcionSelecinadaGrupo6 = capturarOpcionSelecionadaVidrios("Grupo6");
                persistir = registrarDefectos(10022, idPrueba, opcionSelecinadaGrupo6);
            }

            if (!RadioButtonNo7.isSelected())
            {
                String opcionSelecinadaGrupo7 = capturarOpcionSelecionadaVidrios("Grupo7");
                persistir = registrarDefectos(10057, idPrueba, opcionSelecinadaGrupo7);
            }
    
        } catch (Exception e) {
            System.err.println("Error en el metodo : vidrios()" + e.getMessage());
        }
        return persistir;
    }
    
    private String capturarOpcionSelecionadaVidrios(String grupo)
    {
        
        String opcion1 = "";
        if (grupo.equalsIgnoreCase("Grupo1")) 
        {
            if (RadioButtonSi.isSelected()) {
                opcion1 = "SI";
            }else if (RadioButtonNA.isSelected()) {
                opcion1 = "NA";
            }
        }
        
        if (grupo.equalsIgnoreCase("Grupo2")) 
        {
            if (RadioButtonSi2.isSelected()) {
                opcion1 = "SI";
            }else if (RadioButtonNA2.isSelected()) {
                opcion1 = "NA";
            }
        }
        
        if (grupo.equalsIgnoreCase("Grupo3")) 
        {
            if (RadioButtonSi3.isSelected()) {
                opcion1 = "SI";
            }else if (RadioButtonNA3.isSelected()) {
                opcion1 = "NA";
            }
        }
//        
        if (grupo.equalsIgnoreCase("Grupo4")) 
        {
            if (RadioButtonSi4.isSelected()) {
                opcion1 = "SI";
            }else if (RadioButtonNA4.isSelected()) {
                opcion1 = "NA";
            }
        }
        
        if (grupo.equalsIgnoreCase("Grupo5")) 
        {
            if (RadioButtonSi5.isSelected()) {
                opcion1 = "SI";
            }else if (RadioButtonNA5.isSelected()) {
                opcion1 = "NA";
            }
        }
        
        if (grupo.equalsIgnoreCase("Grupo6")) 
        {
            if (RadioButtonSi6.isSelected()) {
                opcion1 = "SI";
            }else if (RadioButtonNA6.isSelected()) {
                opcion1 = "NA";
            }
        }
        
        if (grupo.equalsIgnoreCase("Grupo7")) 
        {
            if (RadioButtonSi7.isSelected()) {
                opcion1 = "SI";
            } else if (RadioButtonNA7.isSelected()) {
                opcion1 = "NA";
            }
        }

        return opcion1;
    }
    
    
    
    
    /**
     * @autor ELKIN B
     * 
     * Metodo que permite ejecutar la query para registrar cada defecto en la db
     * 
     * @param sql
     * @param codigoDefecto
     * @param idPrueba
     * @param valor
     * @return 
     */
    private boolean registrarDefectos(int codigoDefecto,int idPrueba, String valor)
    {
        String sql = "INSERT INTO db_cda.defxprueba(id_defecto,id_prueba,validacion) VALUES(?,?,?)";
        try
        {
            conexion.setAutoCommit(false);
            PreparedStatement instruccion = (PreparedStatement) conexion.prepareStatement(sql);
            instruccion.setInt(1, codigoDefecto);
            instruccion.setInt(2, idPrueba);
            instruccion.setString(3,valor);
            instruccion.executeUpdate();
            instruccion.clearParameters();
            return true;
        } catch (SQLException ex) {
            System.err.println("Error en el metodo : registrarDefectos()" + ex.getMessage());
            Mensajes.mostrarExcepcion(ex);
        }
        return false;
    }
    
    
    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }

    /**
     * MÉTODOS SOBRE LA FUNCIONALIDAD
     * 
     * @param contadorA
     * @param contadorB 
     */
    public void establecerCantidadProblemas(int contadorA, int contadorB) {
        this.contadorA = contadorA;
        this.contadorB = contadorB;
    }

    public int obtenerCantidadProblemasA() {
        return contadorA;
    }

    public int obtenerCantidadProblemasB() {
        return contadorB;
    }

    public void establecerAprobado(int defecto) {
        if (defecto >= 100) {
            aprobado = false;
        }
        //TODO: Mirar si es un camion, una moto, un cuatriciclo, pq cada uno tiene un número máximo de rechazos
        //diferente, para carros normales es 10.
        if (defecto >= 10) {
            aprobado = false;
        }
    }

    public boolean obtenberAprobado() {
        return aprobado;
    }

    public void establecerNombreProblema(String cadenaRExterior) {
        this.cadenaRExterior += cadenaRExterior + "\n";
    }

    public String obtenerNombreProblema() {
        return cadenaRExterior;
    }

    public void establecerGrupo(String grupo) {
        this.grupo = grupo;
    }

    public String obtenerGrupo() {
        return grupo;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /*
         * Set the Nimbus look and feel
         */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Frm_RExteriorv2_proceso.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /*
         * Create and display the dialog
         */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                Frm_RExteriorv2_proceso dialog = new Frm_RExteriorv2_proceso(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton CarroceriaGrup10RadioSI;
    private javax.swing.JRadioButton CarroceriaGrup11RadioSI;
    private javax.swing.JRadioButton CarroceriaGrup12RadioSI;
    private javax.swing.JRadioButton CarroceriaGrup13RadioSI;
    private javax.swing.JRadioButton CarroceriaGrup8RadioSI;
    private javax.swing.JRadioButton CarroceriaGrup9RadioSI;
    private javax.swing.JRadioButton CarroceriaGrupo10RadioNA;
    private javax.swing.JRadioButton CarroceriaGrupo10RadioNO;
    private javax.swing.JRadioButton CarroceriaGrupo11RadioNA;
    private javax.swing.JRadioButton CarroceriaGrupo11RadioNO;
    private javax.swing.JRadioButton CarroceriaGrupo12RadioNA;
    private javax.swing.JRadioButton CarroceriaGrupo12RadioNO;
    private javax.swing.JRadioButton CarroceriaGrupo13RadioNA;
    private javax.swing.JRadioButton CarroceriaGrupo13RadioNO;
    private javax.swing.JRadioButton CarroceriaGrupo1RadioNA;
    private javax.swing.JRadioButton CarroceriaGrupo1RadioNO;
    private javax.swing.JRadioButton CarroceriaGrupo1RadioSI;
    private javax.swing.JRadioButton CarroceriaGrupo2RadioNA;
    private javax.swing.JRadioButton CarroceriaGrupo2RadioNO;
    private javax.swing.JRadioButton CarroceriaGrupo2RadioSI;
    private javax.swing.JRadioButton CarroceriaGrupo3RadioNA;
    private javax.swing.JRadioButton CarroceriaGrupo3RadioNO;
    private javax.swing.JRadioButton CarroceriaGrupo3RadioSI;
    private javax.swing.JRadioButton CarroceriaGrupo4RadioNA;
    private javax.swing.JRadioButton CarroceriaGrupo4RadioNO;
    private javax.swing.JRadioButton CarroceriaGrupo4RadioSI;
    private javax.swing.JRadioButton CarroceriaGrupo5RadioNA;
    private javax.swing.JRadioButton CarroceriaGrupo5RadioNO;
    private javax.swing.JRadioButton CarroceriaGrupo5RadioSI;
    private javax.swing.JRadioButton CarroceriaGrupo6RadioNA;
    private javax.swing.JRadioButton CarroceriaGrupo6RadioNO;
    private javax.swing.JRadioButton CarroceriaGrupo6RadioSI;
    private javax.swing.JRadioButton CarroceriaGrupo7RadioNA;
    private javax.swing.JRadioButton CarroceriaGrupo7RadioNO;
    private javax.swing.JRadioButton CarroceriaGrupo7RadioSI;
    private javax.swing.JRadioButton CarroceriaGrupo8RadioNA;
    private javax.swing.JRadioButton CarroceriaGrupo8RadioNO;
    private javax.swing.JRadioButton CarroceriaGrupo9RadioNA;
    private javax.swing.JRadioButton CarroceriaGrupo9RadioNO;
    private javax.swing.ButtonGroup Group10Carroceria;
    private javax.swing.ButtonGroup Group11Carroceria;
    private javax.swing.ButtonGroup Group12Carroceria;
    private javax.swing.ButtonGroup Group13Carroceria;
    private javax.swing.ButtonGroup Group1Carroceria;
    private javax.swing.ButtonGroup Group1Peldanos;
    private javax.swing.ButtonGroup Group1Placa;
    private javax.swing.ButtonGroup Group1Retencion;
    private javax.swing.ButtonGroup Group1RetroVisores;
    private javax.swing.ButtonGroup Group1Soporte;
    private javax.swing.ButtonGroup Group2;
    private javax.swing.ButtonGroup Group2Carroceria;
    private javax.swing.ButtonGroup Group2Placa;
    private javax.swing.ButtonGroup Group2Retencion;
    private javax.swing.ButtonGroup Group2RetroVisores;
    private javax.swing.ButtonGroup Group2Soporte;
    private javax.swing.ButtonGroup Group3;
    private javax.swing.ButtonGroup Group3Carroceria;
    private javax.swing.ButtonGroup Group3Placa;
    private javax.swing.ButtonGroup Group3Retencion;
    private javax.swing.ButtonGroup Group4Carroceria;
    private javax.swing.ButtonGroup Group4Placa;
    private javax.swing.ButtonGroup Group5Carroceria;
    private javax.swing.ButtonGroup Group5Placa;
    private javax.swing.ButtonGroup Group6Carroceria;
    private javax.swing.ButtonGroup Group6Placa;
    private javax.swing.ButtonGroup Group7Carroceria;
    private javax.swing.ButtonGroup Group7Placa;
    private javax.swing.ButtonGroup Group8Carroceria;
    private javax.swing.ButtonGroup Group9Carroceria;
    private javax.swing.ButtonGroup GroupLimpiBrizas1;
    private javax.swing.ButtonGroup Grupo1;
    private javax.swing.ButtonGroup Grupo4;
    private javax.swing.ButtonGroup Grupo5;
    private javax.swing.ButtonGroup Grupo6;
    private javax.swing.ButtonGroup Grupo7;
    private javax.swing.JRadioButton Panel1LimpiaBrisasNA;
    private javax.swing.JRadioButton Panel1LimpiaBrisasNO;
    private javax.swing.JRadioButton Panel1LimpiaBrisasSi;
    private javax.swing.JPanel Panel1Peldanos;
    private javax.swing.JPanel Panel1Placa;
    private javax.swing.JPanel Panel1Retencion;
    private javax.swing.JPanel Panel1Retrovisores;
    private javax.swing.JPanel Panel1Retrovisores1;
    private javax.swing.JPanel Panel1Soporte;
    private javax.swing.JPanel Panel2Placa;
    private javax.swing.JPanel Panel2Retencion;
    private javax.swing.JPanel Panel2Soporte;
    private javax.swing.JPanel Panel3Placa;
    private javax.swing.JPanel Panel3Retencion;
    private javax.swing.JPanel Panel4Placa;
    private javax.swing.JPanel Panel5Placa;
    private javax.swing.JPanel Panel6Placa;
    private javax.swing.JPanel Panel7Placa;
    private javax.swing.JPanel PanelVidrio1;
    private javax.swing.JPanel PanelVidrio2;
    private javax.swing.JPanel PanelVidrio3;
    private javax.swing.JPanel PanelVidrio4;
    private javax.swing.JPanel PanelVidrio5;
    private javax.swing.JPanel PanelVidrio6;
    private javax.swing.JPanel PanelVidrio7;
    private javax.swing.JPanel PanelVidrio9;
    private javax.swing.JRadioButton PeldanosGrupo1RadioButtonNA;
    private javax.swing.JRadioButton PeldanosGrupo1RadioButtonNO;
    private javax.swing.JRadioButton PeldanosGrupo1RadioButtonSI;
    private javax.swing.JRadioButton PlacasGrupo1NA;
    private javax.swing.JRadioButton PlacasGrupo1NO;
    private javax.swing.JRadioButton PlacasGrupo1SI;
    private javax.swing.JRadioButton PlacasGrupo2NA;
    private javax.swing.JRadioButton PlacasGrupo2NO;
    private javax.swing.JRadioButton PlacasGrupo2SI;
    private javax.swing.JRadioButton PlacasGrupo3NA;
    private javax.swing.JRadioButton PlacasGrupo3NO;
    private javax.swing.JRadioButton PlacasGrupo3SI;
    private javax.swing.JRadioButton PlacasGrupo4NA;
    private javax.swing.JRadioButton PlacasGrupo4NO;
    private javax.swing.JRadioButton PlacasGrupo4SI;
    private javax.swing.JRadioButton PlacasGrupo5NA;
    private javax.swing.JRadioButton PlacasGrupo5NO;
    private javax.swing.JRadioButton PlacasGrupo5SI;
    private javax.swing.JRadioButton PlacasGrupo6NA;
    private javax.swing.JRadioButton PlacasGrupo6NO;
    private javax.swing.JRadioButton PlacasGrupo6SI;
    private javax.swing.JRadioButton PlacasGrupo7NA;
    private javax.swing.JRadioButton PlacasGrupo7NO;
    private javax.swing.JRadioButton PlacasGrupo7SI;
    private javax.swing.JRadioButton RadioButtonNA;
    private javax.swing.JRadioButton RadioButtonNA2;
    private javax.swing.JRadioButton RadioButtonNA3;
    private javax.swing.JRadioButton RadioButtonNA4;
    private javax.swing.JRadioButton RadioButtonNA5;
    private javax.swing.JRadioButton RadioButtonNA6;
    private javax.swing.JRadioButton RadioButtonNA7;
    private javax.swing.JRadioButton RadioButtonNA8;
    private javax.swing.JRadioButton RadioButtonNo;
    private javax.swing.JRadioButton RadioButtonNo2;
    private javax.swing.JRadioButton RadioButtonNo3;
    private javax.swing.JRadioButton RadioButtonNo4;
    private javax.swing.JRadioButton RadioButtonNo5;
    private javax.swing.JRadioButton RadioButtonNo6;
    private javax.swing.JRadioButton RadioButtonNo7;
    private javax.swing.JRadioButton RadioButtonNo8;
    private javax.swing.JRadioButton RadioButtonSi;
    private javax.swing.JRadioButton RadioButtonSi2;
    private javax.swing.JRadioButton RadioButtonSi3;
    private javax.swing.JRadioButton RadioButtonSi4;
    private javax.swing.JRadioButton RadioButtonSi5;
    private javax.swing.JRadioButton RadioButtonSi6;
    private javax.swing.JRadioButton RadioButtonSi7;
    private javax.swing.JRadioButton RadioButtonSi8;
    private javax.swing.JRadioButton RetencionGrupo1RadioNA;
    private javax.swing.JRadioButton RetencionGrupo1RadioNO;
    private javax.swing.JRadioButton RetencionGrupo1RadioSI;
    private javax.swing.JRadioButton RetencionGrupo2RadioNA;
    private javax.swing.JRadioButton RetencionGrupo2RadioNO;
    private javax.swing.JRadioButton RetencionGrupo2RadioSI;
    private javax.swing.JRadioButton RetencionGrupo3RadioNA;
    private javax.swing.JRadioButton RetencionGrupo3RadioNO;
    private javax.swing.JRadioButton RetencionGrupo3RadioSI;
    private javax.swing.JRadioButton RetroGrupo1RadioNA;
    private javax.swing.JRadioButton RetroGrupo1RadioNO;
    private javax.swing.JRadioButton RetroGrupo1RadioSI;
    private javax.swing.JRadioButton RetroGrupo2RadioNA;
    private javax.swing.JRadioButton RetroGrupo2RadioNO;
    private javax.swing.JRadioButton RetroGrupo2RadioSI;
    private javax.swing.JRadioButton SoporteGrupo1RadioNA;
    private javax.swing.JRadioButton SoporteGrupo1RadioNO;
    private javax.swing.JRadioButton SoporteGrupo1RadioSI;
    private javax.swing.JRadioButton SoporteGrupo2RadioNA;
    private javax.swing.JRadioButton SoporteGrupo2RadioNO;
    private javax.swing.JRadioButton SoporteGrupo2RadioSI;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JPanel jPanel28;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables
    private int returnStatus = 0;
}
