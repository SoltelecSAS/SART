package com.soltelec.igrafica;


import com.soltelec.dao.UsuariosJpaController;
import com.soltelec.dao.conexion.PersistenceController;
import com.soltelec.model.TextPrompt;
import com.soltelec.model.Usuarios;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import static java.time.temporal.TemporalQueries.localDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;







/**
 * @author Gerencia TIC
 */
public class FrmLogin extends javax.swing.JFrame {

    private String pass, nombeUsuario;
    private boolean nombreCorrecto, passCorrecto;
    private List results;
   

    /**
     * Creates new form Frm_password
     */
    public FrmLogin()  
    {   
        System.out.println("paso validando");
        initComponents();
        ponerLookAndFeel();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(d);
        setResizable(false);
       TextPrompt datos = new TextPrompt(" Usuario",camUsuario);
       TextPrompt datos2 = new TextPrompt(" Contraseña",camContraseña);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        btn_ingresar = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        camUsuario = new javax.swing.JTextField();
        camContraseña = new javax.swing.JPasswordField();
        btn_change_pswd = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("SART 1.7.3 Inicio Session");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setResizable(false);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/soltelec/Icon/Solte.jpg"))); // NOI18N
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 420, 150));

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btn_ingresar.setBackground(new java.awt.Color(255, 255, 255));
        btn_ingresar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/soltelec/Icon/man_24.png"))); // NOI18N
        btn_ingresar.setText("Ingresar");
        btn_ingresar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ingresarActionPerformed(evt);
            }
        });
        jPanel1.add(btn_ingresar, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 170, 130, -1));

        jButton1.setBackground(new java.awt.Color(255, 255, 255));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/soltelec/Icon/salir24.png"))); // NOI18N
        jButton1.setText("Cancelar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 170, 130, -1));

        camUsuario.setBackground(new java.awt.Color(230, 230, 230));
        camUsuario.setFont(new java.awt.Font("Monospaced", 1, 19)); // NOI18N
        camUsuario.setBorder(null);
        camUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                camUsuarioActionPerformed(evt);
            }
        });
        jPanel1.add(camUsuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 40, 300, 40));

        camContraseña.setBackground(new java.awt.Color(230, 230, 230));
        camContraseña.setFont(new java.awt.Font("Monospaced", 1, 19)); // NOI18N
        camContraseña.setBorder(null);
        jPanel1.add(camContraseña, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 100, 300, 40));

        btn_change_pswd.setBackground(new java.awt.Color(255, 255, 255));
        btn_change_pswd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/soltelec/Icon/editar24x24.png"))); // NOI18N
        btn_change_pswd.setText("Cambiar pwd");
        btn_change_pswd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_change_pswdActionPerformed(evt);
            }
        });
        jPanel1.add(btn_change_pswd, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 210, 140, -1));

        jPanel2.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 240, 390, 250));

        jPanel3.setBackground(new java.awt.Color(102, 153, 255));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("INICIO DE SESIÓN");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(159, 159, 159)
                .addComponent(jLabel4)
                .addContainerGap(127, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addContainerGap())
        );

        jPanel2.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 440, 40));

        jPanel4.setBackground(new java.awt.Color(102, 153, 255));
        jPanel4.setForeground(new java.awt.Color(102, 153, 255));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 440, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        jPanel2.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 530, 440, 40));

        jPanel5.setBackground(new java.awt.Color(230, 230, 230));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 440, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 20, Short.MAX_VALUE)
        );

        jPanel2.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 40, -1, 20));

        jPanel6.setBackground(new java.awt.Color(230, 230, 230));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 440, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 20, Short.MAX_VALUE)
        );

        jPanel2.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 510, -1, 20));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(84, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(140, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(83, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(92, 92, 92))
        );

        setBounds(0, 0, 680, 784);
    }// </editor-fold>//GEN-END:initComponents

    private void btn_ingresarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ingresarActionPerformed

        //Captura de lo digitado en el campo de texto de la contraseña
        
     /*  final  Logger log = Logger.getLogger("igrafica");
      final Logger logFile = Logger.getLogger("DB");*/
    
        char p[] = camContraseña.getPassword();
        pass = new String(p);
        nombeUsuario = camUsuario.getText();
        Usuarios user = null;
        try {
            EntityManager em = PersistenceController.getEntityManager();
            em.getTransaction().begin();

           try {
                Query queryPass = em.createQuery("SELECT u FROM Usuarios u WHERE u.contrasenia = :contrasena");
                queryPass.setParameter("contrasena", pass);
                results = queryPass.getResultList();
                user = (Usuarios) results.get(0);                
                if ( user.getContrasenia().equals(pass) ) {
                    passCorrecto = true;
                    //02 12 2011 Verificacion de la fecha de validación de la contrasena
                    Date fechaValidacion = user.getFechavalidacion();
                    Query queryFechaHoy = em.createNativeQuery("SELECT NOW()");//traer la fecha desde el servidor
                    
                    //Date fechaHoy = (Date) queryFechaHoy.getSingleResult();
                    //Validar la Fecha
                    
                    //JFM
                    SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
                    Date fechaHoy = new Date(System.currentTimeMillis());
                    System.out.println("fecha de la variable fechaHoy  " + fechaHoy  );
                    //JFM
                 
                    if (contrasenaExpirada(fechaValidacion, fechaHoy)) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        JOptionPane.showMessageDialog(null, "Debe Cambiar su contrasena:"
                                + "\n Fecha Expiracion : " + sdf.format(fechaValidacion));
                        return;
                    }
                    String nomTemp;                   
                    if (user.getNickusuario().startsWith(".DT")==true) {
                        nomTemp = user.getNickusuario().substring(3, user.getNickusuario().length());
                       } else {
                        nomTemp = user.getNickusuario();
                    }
                    if (nombeUsuario.equalsIgnoreCase(nomTemp)) {
                        nombreCorrecto = true;
                    } else {
                        JOptionPane.showMessageDialog(null, "Disculpe Su Nombre de usuario  es INCORRECTO, por Favor vuelva a ingresarlo", "ERROR", JOptionPane.ERROR_MESSAGE);
                        nombreCorrecto = false;
                        camUsuario.setText("");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Su contrasena es incorrecta, vuelva a ingresarla por favor", "ERROR", JOptionPane.ERROR_MESSAGE);
                }

            } catch (IndexOutOfBoundsException | IllegalArgumentException ex) {
                System.out.println("ERROR DE LOGIN : " + ex);
                JOptionPane.showMessageDialog(null, "Disculpe La Clave del usuario es INCORRECTA", "SART 1.7.3", JOptionPane.ERROR_MESSAGE);
                
                camUsuario.setText("");
                camContraseña.setText("");
                //esCorrecto = "incorrectos";
                nombreCorrecto = false; //Reiniciar variable en caso de que se vuelvan a pedir datos
            }

            em.getTransaction().commit();//si genio no hay actualizaciones asi que el commit no es necesario

            if (nombreCorrecto == true && passCorrecto == true) {
                //TODO: Enviar a la base de datos la hora y la fecha a la que ingreso el usuario que se ha validado
                //No permitir el ingreso si la contraseña esta vencida
                //Abrir la siguiente ventana e indicando que FrmLogin es el padre.
                //this.dispose();
                //dispose();
               this.setExtendedState(ICONIFIED);
                
                
                //logFile.warn("Aplicacion iniciada por :"  + c1.getNombreusuario());
                //Frm_IGCentral dlg = new Frm_IGCentral(this, true); Cambios para el nuevo diseño 
                Logger.getLogger("igrafica").info("Aplicacion iniciada por :"  + nombeUsuario);
                Frm_UICentral dlg = new Frm_UICentral(this, true, em);   
                dlg.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
                dlg.setTitle("SART Version:1.7.3 copyright  2009 ");
               dlg.setIconImage(getIconImage());
                dlg.setNombreUsuario(user.getNombreusuario());
                dlg.setVisible(true);
                camContraseña.setText("");
                 
            }
        } catch (PersistenceException e) {
            System.out.println("error que presento en el login : " + e);
            JOptionPane.showMessageDialog(null, "No se puede obtener una conexion con la base de datos");
            
            //Logger.getRootLogger().error("Error probablemente iniciando la factory para la unidad de persistencia", e);

        }      
        
    }//GEN-LAST:event_btn_ingresarActionPerformed
    
    private boolean contrasenaExpirada(Date fechaValidacion, Date fechaHoy) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        //System.out.println("Fecha de validacion: " + sdf.format(fechaValidacion));
        //System.out.println("Fecha de Hoy: " + sdf.format(fechaHoy));
        Calendar calendarValidacion = Calendar.getInstance();
        calendarValidacion.setTime(fechaValidacion);
        Calendar calendarHoy = Calendar.getInstance();
        calendarHoy.setTime(fechaHoy);
        return calendarValidacion.before(calendarHoy);

    }
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btn_change_pswdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_change_pswdActionPerformed
        //Validar que el usuario y la contraseña correspondan y despues mostrar un 
        char p[] = camContraseña.getPassword();
        pass = new String(p);
        nombeUsuario = camUsuario.getText();
        UsuariosJpaController ujc = new UsuariosJpaController();

        if (!validarUsuarioContraseña(nombeUsuario, pass, ujc)) {
            JOptionPane.showMessageDialog(null, "Para cambiar su contraseña ingrese "
                    + "\n su usuario y contraseña actual y presione CAMBIAR PWD");
            camUsuario.setText("");
            camContraseña.setText("");
        } else {
            DlgChangePassword dlg = new DlgChangePassword(this, true);
            dlg.setUjc(ujc);
            dlg.setPass(pass);
            dlg.setVisible(true);
            //Dialogo para que ingrese la nueva contraseña
        }
    }//GEN-LAST:event_btn_change_pswdActionPerformed

    private void camUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_camUsuarioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_camUsuarioActionPerformed
    /**
     * @param args the command line arguments
     */

    /*
     * public static void main(String args[]) {
     * java.awt.EventQueue.invokeLater(new Runnable() { public void run() { new
     * FrmLogin().setVisible(true); } }); }
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_change_pswd;
    private javax.swing.JButton btn_ingresar;
    private javax.swing.JPasswordField camContraseña;
    private javax.swing.JTextField camUsuario;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    // End of variables declaration//GEN-END:variables

    private void ponerLookAndFeel() {
        this.setIconImage(getIconImage());
    }

    @Override
    public Image getIconImage() {
        //ImageIcon image = new ImageIcon(this.getClass().getResource("/com/soltelec/Icon/car.png"));
        ImageIcon image = new ImageIcon(this.getClass().getResource("/com/soltelec/Icon/icono.jpg"));
        return image.getImage();
    }

    /**
     *
     * @param fechaValidacion
     * @param fechaHoy fecha de hoy desde el servidor
     * @return
     */
    private boolean contraseñaExpirada(Date fechaValidacion, Date fechaHoy) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        System.out.println("Fecha de validacion: " + sdf.format(fechaValidacion));
        System.out.println("Fecha de Hoy: " + sdf.format(fechaHoy));
        Calendar calendarValidacion = Calendar.getInstance();
        calendarValidacion.setTime(fechaValidacion);
        Calendar calendarHoy = Calendar.getInstance();
        calendarHoy.setTime(fechaHoy);
        return calendarValidacion.before(calendarHoy);
    }

    private boolean validarUsuarioContraseña(String nombeUsuario, String pass, UsuariosJpaController ujc) {                               
        Usuarios usuario = ujc.getPassWord(pass);   
         String nomTemp; 
         if (usuario != null) {
             if (usuario.getNickusuario().startsWith(".DT")==true) {
                 nomTemp = usuario.getNickusuario().substring(3, usuario.getNickusuario().length());
             } else {
                 nomTemp = usuario.getNickusuario();
             } 
             if (nombeUsuario.equalsIgnoreCase(nomTemp)) {
                 nombreCorrecto = true;
             } else {
                 nombreCorrecto=false;
             }
            return nombreCorrecto;
        } else {
            return false;
        }
    }

    public static void main(String[] args) throws ClassNotFoundException {
        
        new FrmLogin().setVisible(true);
    }
}
