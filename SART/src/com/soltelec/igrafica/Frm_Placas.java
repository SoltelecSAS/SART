
package com.soltelec.igrafica;//GEN-FIRST:event_btn_verificarActionPerformed
//GEN-LAST:event_btn_verificarActionPerformed
import com.soltelec.dao.ControladorVerificar;
import com.soltelec.indra.ClienteSicov;
import com.soltelec.model.AuditoriaSicov;
import com.soltelec.model.Defxprueba;
import com.soltelec.model.Pruebas;
import com.soltelec.model.SEQUENCE;
import com.soltelec.model.Cda;
import com.soltelec.model.Usuarios;
import com.soltelec.model.Vehiculos;
import com.soltelec.modulopuc.utilidades.Mensajes;
import java.awt.Frame;
import java.awt.Window;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.soltelec.luxometro.HiloPruebaLuxometro;
import org.soltelec.luxometro.JDialogLuces;
import org.soltelec.luxometro.lujan.JDialogLucesMotoLujan;
import org.soltelec.luxometro.JDialogLucesMoto;
import org.soltelec.luxometro.LecturaArchivoLuxometroMoon;
import org.soltelec.luxometro.FrmLuxometroCapelec;
import org.soltelec.luxometro.capelec.cap2500.JDialogLucesCarros;
import org.soltelec.luxometro.capelec.cap2500.JDialogLucesMotoCapelec;          //Sonometro
import org.soltelec.luxometro.chino.JDialogLuxometroChino;
import org.soltelec.luxometro.gemini.JDialogGemini;
import org.soltelec.luxometro.lujan.HiloPruebaLuxometroLujan;
import org.soltelec.luxometro.lujan.JDialogLucesVehiculoLujan;
import org.soltelec.procesosbanco.RegVefCalibraciones;
import org.soltelec.pruebasgases.JDialogPruebaGasolina;
import org.soltelec.pruebasgases.WorkerCruceroRalenti;
import org.soltelec.pruebasgases.diesel.JDialogDiesel;
import org.soltelec.pruebasgases.diesel.WorkerCiclosDiesel;
import org.soltelec.pruebasgases.motocicletas.DialogoMotos;
import org.soltelec.pruebasgases.motocicletas.JDialogMotosGases;
import org.soltelec.pruebasgases.motocicletas.WorkerCiclosMoto;
import org.soltelec.sonometro.JDialogSonometro;
import org.soltelec.util.UtilFugas;
import org.soltelec.util.UtilSicov;
import vistas.DlgFrenoMoto;
import vistas.DlgIntegrado4x4;
import vistas.DlgIntegradoLiviano;
import vistas.DlgIntegradoPesado;
import vistas.DlgTaximetro;
import vistas.dialogfrenmotos;
import vistas.dialogfrenmotos2;
import org.soltelec.util.UtilInfoServicioGases;
import com.soltelec.indra.EventoDTO;
import com.soltelec.indra.RespuestaDTO;
import com.soltelec.igrafica.labrado.presion.LabradoPresionLiviano;
import com.soltelec.igrafica.labrado.presion.LabradoPresionMoto;
import com.soltelec.igrafica.labrado.presion.LabradoPresionMotoCarro;
import java.util.List;
import myplayer.CapturarFoto;
import org.soltelec.pruebasgases.DialogoVehiculo;
import com.soltelec.util.MensajesOut;
import org.apache.commons.lang.StringUtils;
import org.soltelec.models.controllers.EquipoController;
import termohigrometro.TermoHigrometroArtisan;
import utiltermohigrometro.UtilPropiedades;
import vistas.DlgIntegradoMotoCarro;

/**
 * @author Gerencia TIC
 */
public class Frm_Placas extends javax.swing.JDialog {

    static NoReiniciable placas_bd;
    static NoReiniciable nrei;
    private String placas = "", nombre_usuario, pass;
    private String nombreBoton = "";
    private int valida_tipo;
    private String tipoPista = "";
    private int idVehiculoInt = 0, idPrueba = 0, idUsuario = 0;
    boolean inspeccionVisual = false, luces = false, foto = false, desviacion = false, frenometro = false, suspension = false, ruido = false, gases = false, taximetro = false;
    private BufferedReader config;
    static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger("igrafica");
    private String ipEquipo;
    private String serialEquipo;
    private static EntityManager em;
    static Usuarios usr;
    private int idPruebaDesviacion = 0, idPruebaFreno = 0, idPruebaSuspension = 0;
    private DialogoMotos dlgMotos = null;
    private DialogoVehiculo dlgVehiculo = null;
    private ControladorVerificar controladorVerificar = new ControladorVerificar();
    private boolean labradorealizada = false;
    private TermoHigrometroArtisan termoHigrometroArtisan;

    public Frm_Placas() {
    }

    /**
     * Creates new form Frm_Placas
     *
     * @param parent
     * @param modal
     */
    public Frm_Placas(java.awt.Frame parent, boolean modal, EntityManager eManager, TermoHigrometroArtisan termoHigrometroArtisan) {
        super(parent, modal);
        initComponents();
        setLocationRelativeTo(null);
        em = eManager;
        this.termoHigrometroArtisan = termoHigrometroArtisan;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        cam_placas = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        btn_verificar = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        cam_usuario = new javax.swing.JTextField();
        cam_contraseña = new javax.swing.JPasswordField();
        jLabel5 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        cam_placas.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N

        jLabel1.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N
        jLabel1.setText("Ingrese el número de placa del Vehículo:");

        jLabel2.setFont(new java.awt.Font("Serif", 1, 24)); // NOI18N
        jLabel2.setText("VERIFICACIÓN DE DATOS");

        jSeparator1.setForeground(new java.awt.Color(255, 0, 0));

        jSeparator2.setForeground(new java.awt.Color(255, 0, 0));

        btn_verificar.setFont(new java.awt.Font("Serif", 1, 14)); // NOI18N
        btn_verificar.setText("VERIFICAR");
        btn_verificar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_verificarMouseClicked(evt);
            }
        });
        btn_verificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_verificarActionPerformed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Serif", 1, 14)); // NOI18N
        jButton2.setText("CANCELAR");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N
        jLabel3.setText("Ingrese el nombre de usuario:");

        jLabel4.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N
        jLabel4.setText("Ingrese su contraseña:");

        cam_usuario.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N

        cam_contraseña.setFont(new java.awt.Font("Tempus Sans ITC", 1, 18)); // NOI18N
        cam_contraseña.setForeground(new java.awt.Color(255, 0, 51));

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/soltelec/Icon/Datos.gif"))); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel1)
                                        .addComponent(jLabel3)
                                        .addComponent(jLabel4))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(cam_contraseña, javax.swing.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE)
                                        .addComponent(cam_usuario, javax.swing.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE)
                                        .addComponent(cam_placas, javax.swing.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE))
                                .addGap(21, 21, 21))
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jSeparator2, javax.swing.GroupLayout.DEFAULT_SIZE, 604, Short.MAX_VALUE)
                                .addContainerGap())
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 604, Short.MAX_VALUE)
                                .addContainerGap())
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(jLabel2))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(159, 159, 159)
                                                .addComponent(btn_verificar, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jButton2)))
                                .addContainerGap(146, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[]{btn_verificar, jButton2});

        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel5)
                                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(cam_placas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel1))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel3)
                                        .addComponent(cam_usuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel4)
                                        .addComponent(cam_contraseña, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 6, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btn_verificar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>                        

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
        doClose(0);
    }

    /**
     *
     *
     * @param indPrueba
     * @param revTec
     * @param normal
     * @param eventoDTO
     * @param cda
     */
    private void regIdAuditoria(Integer indPrueba, int revTec, boolean normal, EventoDTO eventoDTO, Cda cda) {
        System.out.println("REGISTRO BLOQUEDO A BAJO NIVEL ");
        if (revTec == 1) {
            System.out.println("aparto idSicov para prueba");
            Query q;
            int nvoAudi = 0;
            Integer idAud = 0;
            try {
                Thread.sleep(1900);
            } catch (InterruptedException ex) {
            }
            em.clear();
            em.getTransaction().begin();
            em.flush();
            q = em.createQuery("SELECT p FROM Pruebas  p WHERE p.idPruebas= :idPruebas");
            q.setParameter("idPruebas", indPrueba);
            Pruebas test = (Pruebas) q.getSingleResult();
            em.getTransaction().commit();
            /*if(test.getFinalizada().equalsIgnoreCase("N")){
                 System.out.println("id (Audi) NO APARTADO PRUEBA ABORTADA");
                return ;
            }*/
            if (test.getTipoPrueba().getTesttype() == 3) {
                System.out.println("id (Audi) NO APARTADO PRUEBA OF FOTO");
                return;
            }

            //************************************************************
            try {
                Thread.sleep(377);
                em.getTransaction().begin();
                q = em.createNativeQuery("SELECT MAX(SEQ_COUNT) from sequence WHERE SEQ_NAME = 'AUD_SICOV' ");
                idAud = (Integer) q.getSingleResult();
                SEQUENCE s = em.find(SEQUENCE.class, "AUD_SICOV");
                nvoAudi = idAud + 1;
                s.setSEQCOUNT(nvoAudi);
                em.merge(s);
                em.flush();
                em.getTransaction().commit();
            } catch (Exception ex1) {
                em.getTransaction().rollback();
                System.out.println("REGISTRO ESTUVO BLOQUEDO A BAJO NIVEL  se libera el resource");
            }
            try {
                Thread.sleep(477);
            } catch (InterruptedException ex) {
            }
            //************************************************************            
            em.getTransaction().begin();
            if (normal == true) {
                System.out.println("Estoy Ejecutando el stored Procedure ");

                StoredProcedureQuery stQ = em.createStoredProcedureQuery("evtFechaRealPrueba");
                System.out.println("TEST " + test.getIdPruebas() + " tipo " + test.getTipoPrueba().getTesttype());
                stQ.registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN);
                stQ.registerStoredProcedureParameter(2, Integer.class, ParameterMode.IN);
                stQ.setParameter(1, test.getIdPruebas());
                stQ.setParameter(2, test.getTipoPrueba().getTesttype());
                stQ.executeUpdate();
                em.flush();
            }
            //************************************************************
            test.setFechaaborto(ipEquipo.concat(";").concat(String.valueOf(idAud)));
            em.merge(test);
            em.getTransaction().commit();
            if (cda.getProveedorSicov().equalsIgnoreCase("INDRA")) {
                ClienteSicov clienteIndra = new ClienteSicov();
                if (test.getAbortada().equalsIgnoreCase("N")) {
                    eventoDTO.setIdEvento(2);
                    Logger.getLogger(Frm_Placas.class.getName()).log(Level.WARNING, null, "Enviando event 2 FINALIZAR EVENTOS DE PLACA".concat(eventoDTO.getPlaca()).concat(" DE TIPO ").concat(test.getTipoPrueba().getNombretipoprueba()));
                } else {
                    eventoDTO.setIdEvento(3);
                    Logger.getLogger(Frm_Placas.class.getName()).log(Level.WARNING, null, "Enviando event 3 FINALIZAR EVENTOS DE PLACA".concat(eventoDTO.getPlaca()).concat(" DE TIPO ").concat(test.getTipoPrueba().getNombretipoprueba()));
                    if (test.getComentarioaborto() != null) {
                        eventoDTO.setMensajeEvento(test.getComentarioaborto());
                        Logger.getLogger(Frm_Placas.class.getName()).log(Level.WARNING, null, "Prueba abortada por PLACA".concat(eventoDTO.getPlaca()).concat(" TIPO ABORTO: ").concat(test.getComentarioaborto()));
                    } else {
                        Logger.getLogger(Frm_Placas.class.getName()).log(Level.WARNING, null, "Prueba abortada por PLACA".concat(eventoDTO.getPlaca()).concat(" TIPO ABORTO: NO ESPECIFICADO"));
                    }
                }
                String fecha = UtilSicov.askDate();
                fecha = fecha.substring(0, fecha.length() - 2);
                eventoDTO.setFecha(fecha);
                if (test.getTipoPrueba().getTesttype() == 5 || test.getTipoPrueba().getTesttype() == 6 || test.getTipoPrueba().getTesttype() == 4) {
                    eventoDTO.setNombrePrueba("FAS");
                }

                RespuestaDTO respuesta = clienteIndra.crearEvento(eventoDTO, cda);
                System.out.println("--------------------------------------------Respuesta Evento Verificado Gases");
                if (respuesta.getCodigoRespuesta().equals("0")) {
                    Logger.getLogger(Frm_Placas.class.getName()).log(Level.WARNING, null, "NO PUEDE FINALIZAR EVENTOS DE PLACA".concat(eventoDTO.getPlaca()).concat(" DE TIPO ").concat(test.getTipoPrueba().getNombretipoprueba()));
                    JOptionPane.showMessageDialog(null, "DISCULPE; No Pude FINALIZAR el Evento para la Pruebas Visual");
                    doClose(0);
                    return;
                } else {
                    Logger.getLogger(Frm_Placas.class.getName()).log(Level.WARNING, null, "ENVIO EVENTOS EXITOSO DE PLACA".concat(eventoDTO.getPlaca()).concat(" DE TIPO ").concat(test.getTipoPrueba().getNombretipoprueba()));
                }
            }
            System.out.println("id (Audi) apartado con exito");
        }
    }

    /**
     * @autor ELKIN B
     *
     * @param indPrueba
     * @param revTec
     * @param normal
     * @param cda
     */
    private void regIdAuditoriaRuido(int revTec, boolean normal,EventoDTO eventoDTO, Cda cda) {
        System.out.println("---------------------------------------------------");
        
        System.out.println("----------------regIdAuditoriaRuicdo---------------");
        System.out.println("---------------------------------------------------");

        System.out.println("REGISTRO BLOQUEDO A BAJO NIVEL ");
        if (revTec == 1) {
            System.out.println("aparto idSicov para prueba");
            Query q;
            int nvoAudi = 0;
            Integer idAud = 0;
            try {
                Thread.sleep(1900);
            } catch (InterruptedException ex) {
            }
            em.clear();
            em.getTransaction().begin();
            em.flush();
            q = em.createQuery("SELECT p FROM Pruebas  p WHERE p.idPruebas= :idPruebas");
            q.setParameter("idPruebas", idPrueba);
            Pruebas test = (Pruebas) q.getSingleResult();
            em.getTransaction().commit();
            /*if(test.getFinalizada().equalsIgnoreCase("N")){
                 System.out.println("id (Audi) NO APARTADO PRUEBA ABORTADA");
                return ;
            }*/
            if (test.getTipoPrueba().getTesttype() == 3) {
                System.out.println("id (Audi) NO APARTADO PRUEBA OF FOTO");
                return;
            }

            //************************************************************
            try {
                Thread.sleep(377);
                em.getTransaction().begin();
                q = em.createNativeQuery("SELECT MAX(SEQ_COUNT) from sequence WHERE SEQ_NAME = 'AUD_SICOV' ");
                idAud = (Integer) q.getSingleResult();
                SEQUENCE s = em.find(SEQUENCE.class, "AUD_SICOV");
                nvoAudi = idAud + 1;
                s.setSEQCOUNT(nvoAudi);
                em.merge(s);
                em.flush();
                em.getTransaction().commit();
            } catch (Exception ex1) {
                em.getTransaction().rollback();
                System.out.println("REGISTRO ESTUVO BLOQUEDO A BAJO NIVEL  se libera el resource");
            }
            try {
                Thread.sleep(477);
            } catch (InterruptedException ex) {
            }
            //************************************************************            
            em.getTransaction().begin();
            if (normal == true) {
                System.out.println("Estoy Ejecutando el stored Procedure ");

                StoredProcedureQuery stQ = em.createStoredProcedureQuery("evtFechaRealPrueba");
                System.out.println("TEST " + test.getIdPruebas() + " tipo " + test.getTipoPrueba().getTesttype());
                stQ.registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN);
                stQ.registerStoredProcedureParameter(2, Integer.class, ParameterMode.IN);
                stQ.setParameter(1, test.getIdPruebas());
                stQ.setParameter(2, test.getTipoPrueba().getTesttype());
                stQ.executeUpdate();
                em.flush();

            }
            //************************************************************
            test.setFechaaborto(ipEquipo.concat(";").concat(String.valueOf(idAud)));
            em.merge(test);
            em.getTransaction().commit();
            if (cda.getProveedorSicov().equalsIgnoreCase("INDRA")) 
            {
                ClienteSicov clienteIndra = new ClienteSicov();
                if (test.getAbortada().equalsIgnoreCase("N")) {
                    eventoDTO.setIdEvento(2);
                    Logger.getLogger(Frm_Placas.class.getName()).log(Level.WARNING, null, "Enviando event 2 FINALIZAR EVENTOS DE PLACA".concat(eventoDTO.getPlaca()).concat(" DE TIPO ").concat(test.getTipoPrueba().getNombretipoprueba()));
                } else {
                    eventoDTO.setIdEvento(3);
                    Logger.getLogger(Frm_Placas.class.getName()).log(Level.WARNING, null, "Enviando event 3 FINALIZAR EVENTOS DE PLACA".concat(eventoDTO.getPlaca()).concat(" DE TIPO ").concat(test.getTipoPrueba().getNombretipoprueba()));
                    if(test.getComentarioaborto()!=null){
                         eventoDTO.setMensajeEvento(test.getComentarioaborto());
                         Logger.getLogger(Frm_Placas.class.getName()).log(Level.WARNING, null, "Prueba abortada por PLACA".concat(eventoDTO.getPlaca()).concat(" TIPO ABORTO: ").concat(test.getComentarioaborto()));
                    }else{
                        Logger.getLogger(Frm_Placas.class.getName()).log(Level.WARNING, null, "Prueba abortada por PLACA".concat(eventoDTO.getPlaca()).concat(" TIPO ABORTO: NO ESPECIFICADO"));
                    }                   
                }
                String fecha = UtilSicov.askDate();
                fecha = fecha.substring(0, fecha.length() - 2);
                eventoDTO.setFecha(fecha);
                if (test.getTipoPrueba().getTesttype()==5 || test.getTipoPrueba().getTesttype()==6 || test.getTipoPrueba().getTesttype()==4) {
                    eventoDTO.setNombrePrueba("FAS");
                }
                
                RespuestaDTO respuesta = clienteIndra.crearEvento(eventoDTO, cda);
                System.out.println("--------------------------------------------Respuesta Evento Verificado Auditoria");
                System.out.println("");
                if (respuesta.getCodigoRespuesta().equals("0")) {
                    Logger.getLogger(Frm_Placas.class.getName()).log(Level.WARNING, null, "NO PUEDE FINALIZAR EVENTOS DE PLACA".concat(eventoDTO.getPlaca()).concat(" DE TIPO ").concat(test.getTipoPrueba().getNombretipoprueba()));
                    JOptionPane.showMessageDialog(null, "DISCULPE; No Pude FINALIZAR el Evento para la Pruebas Visual");
                    doClose(0);
                    return;
                } else {
                    Logger.getLogger(Frm_Placas.class.getName()).log(Level.WARNING, null, "ENVIO EVENTOS EXITOSO DE PLACA".concat(eventoDTO.getPlaca()).concat(" DE TIPO ").concat(test.getTipoPrueba().getNombretipoprueba()));
                }
            }
            System.out.println("id (Audi) apartado con exito");
        }
    }

    /**
     *
     * @param tramaAuditoria
     * @param evento
     * @param operacion
     * @param indPrueba
     * @param revTec
     * @param escrTrans
     * @param serialEquipo
     * @param usuario
     */
    //regTblAuditoriaSicov(null, 7, 1, idPrueba, revTec, "E", " ", usr);
    private void regTblAuditoriaSicov(String tramaAuditoria, Integer evento, Integer operacion, Integer indPrueba, int revTec, String escrTrans, String serialEquipo, Usuarios usuario) {
        System.out.println("ENTRO LECTURA SICOV SART 1.7.3 x 9");
        if (revTec == 1) {
            //************************************************************
            try {
                Thread.sleep(4000);
            } catch (InterruptedException ex) {

            }
            //************************************************************
            em.clear();
            //************************************************************
            em.getTransaction().begin();
            Query qDA = em.createNativeQuery("SELECT id_runt,usuario_sicov from cda ");
            Object[] arrayObjetos = (Object[]) qDA.getResultList().iterator().next();
            Query q = em.createQuery("SELECT p FROM Pruebas  p WHERE p.idPruebas= :idPruebas");
            q.setParameter("idPruebas", indPrueba);
            Pruebas test = (Pruebas) q.getSingleResult();
            //************************************************************
            StoredProcedureQuery stQ = em.createStoredProcedureQuery("evtFechaRealPrueba");
            stQ.registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN);
            stQ.registerStoredProcedureParameter(2, Integer.class, ParameterMode.IN);
            stQ.setParameter(1, test.getIdPruebas());
            stQ.setParameter(2, test.getTipoPrueba().getTesttype());
            stQ.executeUpdate();
            em.flush();
            em.refresh(test);
            em.getTransaction().commit();
            //************************************************************
            try {
                Thread.sleep(2500);
            } catch (InterruptedException ex) {
            }
            //************************************************************

            em.getTransaction().begin();
            q = em.createNativeQuery("SELECT MAX(SEQ_COUNT) from sequence WHERE SEQ_NAME = 'AUD_SICOV' ");
            Integer idAud = (Integer) q.getSingleResult();
            SEQUENCE s = em.find(SEQUENCE.class, "AUD_SICOV");
            int nvoAudi = idAud + 1;
            s.setSEQCOUNT(nvoAudi);
            em.merge(s);
            em.getTransaction().commit();
            try {
                Thread.sleep(1977);
            } catch (InterruptedException ex) {
            }
            //************************************************************
            if (serialEquipo.length() < 3) {
                if (test.getSerialEquipo() != null) {
                    serialEquipo = test.getSerialEquipo();
                }
            }
            //************************************************************
            try {
                em.getTransaction().begin();
                AuditoriaSicov auditoriaSicov = new AuditoriaSicov();
                auditoriaSicov.setIdRUNTCDA((Integer) arrayObjetos[0]);
                auditoriaSicov.setCodigoProveedor(858);
                auditoriaSicov.setIpEquipoMedicion(ipEquipo);
                auditoriaSicov.setTipoEvento(evento);
                auditoriaSicov.setTipoOperacion(operacion);
                if (escrTrans.equalsIgnoreCase("A")) {
                    System.out.println("TOMO TRANSICION DE LA TRAMA PARA PRUEBA");
                    test.setFechaaborto(ipEquipo.concat(";").concat(String.valueOf(idAud)));
                    test.setSerialEquipo(serialEquipo);
                    test.setAutorizada("A");
                    em.merge(test);
                } else {
                    System.out.println("TRANSICION ES " + escrTrans);
                    if (tramaAuditoria == null) {
                        String infLabr = "";
                        String cmLabr = "";
                        if (test.getTipoPrueba().getTesttype() == 1) {
                            int posObs = test.getObservaciones().indexOf("obs");
                            String observaciones = "";
                            String tramaDefectos = "";
                            for (Defxprueba defxprueba : test.getDefxpruebaList()) {
                                tramaDefectos = tramaDefectos.concat(defxprueba.getDefectos().getCodigoSuperintendencia().concat("_"));
                            }
                            if (tramaDefectos.length() > 0) {
                                tramaDefectos = tramaDefectos.substring(0, tramaDefectos.length() - 1);
                            }
                            String[] lstObs = test.getObservaciones().split("obs");
                            String[] lstEjes = lstObs[0].split("&");
                            for (int i = 0; i < lstEjes.length; i++) {
                                infLabr = lstEjes[i].replace("$", "-");
                                cmLabr = cmLabr.concat("Eje" + String.valueOf(i + 1)).concat(" ").concat(infLabr).concat("mm; ");
                            }
                            if (lstObs.length > 1) {
                                cmLabr = cmLabr.concat("\n").concat(" ");
                            }
                            observaciones = "Profundidad labrado: ".concat(cmLabr);
                            if (posObs > 0) {
                                observaciones = observaciones.concat("Obs:").concat(test.getObservaciones().substring(posObs + 3, test.getObservaciones().length()));
                            }
                            auditoriaSicov.setTRAMA("{\"ObservacionesVisual\":\"".concat(tramaDefectos).concat(" ").concat(observaciones).concat("\",").concat("\"tablaAfectada\":\"defxprueba\",\"idRegistro\":\"").concat(String.valueOf(idPrueba)).concat("\"}"));
                            auditoriaSicov.setSerialEquipoMedicion(" ");
                            auditoriaSicov.setObservacion(observaciones);
                        }
                    } else {
                        if (test.getTipoPrueba().getTesttype() == 8) {
                            System.out.println("VALID PRUEBA GASES");
                            if (tramaAuditoria.equalsIgnoreCase("aplicoSensorial")) {
                                tramaAuditoria = "{\"tempRalenti\":\"".concat(String.valueOf(" ")).concat("\",").concat("\"tempCrucero\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"rpmRalenti\":\"").concat("\",").concat("\"rpmCrucero\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"CORalenti\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"COCrucero\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"CO2Ralenti\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"CO2Crucero\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"O2Ralenti\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"O2Crucero\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"HCRalenti\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"HCCrucero\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"NORalenti\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"NOCrucero\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"tempDiesel\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"rpmDiesel\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"ciclo1\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"ciclo2\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"ciclo3\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"ciclo4\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"resultadoValor\":\"").concat(String.valueOf(" ")).concat("\",");
                                tramaAuditoria = tramaAuditoria.concat("\"dilucion\":\"").concat("\",\"revisionVisual\":").concat("aplicoSensorial").concat("\"\"tablaAfectada\":\"medidas\",\"idRegistro\":\"").concat(String.valueOf(idPrueba)).concat("\"}");
                                String tramaDefectos = "";
                                String tramaTemporal = "";
                                String tramaTemporal2 = "";
                                for (Defxprueba defxprueba : test.getDefxpruebaList()) {
                                    tramaDefectos = tramaDefectos.concat(defxprueba.getDefectos().getCodigoSuperintendencia().concat("_"));
                                }
                                if (tramaDefectos.length() > 1) {
                                    tramaDefectos = tramaDefectos.substring(0, tramaDefectos.length() - 1);
                                    int posIyec = tramaAuditoria.indexOf("revisionVisual");
                                    tramaTemporal = tramaAuditoria.substring(0, posIyec + 16);
                                    tramaTemporal2 = tramaAuditoria.substring(posIyec + 32, tramaAuditoria.length());
                                    tramaTemporal = tramaTemporal.concat("\"").concat(tramaDefectos);
                                    tramaTemporal = tramaTemporal.concat("\",").concat(tramaTemporal2);
                                    tramaAuditoria = tramaTemporal;
                                }
                            }
                            System.out.println("POS ARROBA " + tramaAuditoria);
                            if (tramaAuditoria.equalsIgnoreCase("@@@@")) {
                                tramaAuditoria = "{\"tempRalenti\":\"".concat(String.valueOf(" ")).concat("\",").concat("\"tempCrucero\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"rpmRalenti\":\"").concat("\",").concat("\"rpmCrucero\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"CORalenti\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"COCrucero\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"CO2Ralenti\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"CO2Crucero\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"O2Ralenti\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"O2Crucero\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"HCRalenti\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"HCCrucero\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"NORalenti\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"NOCrucero\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"tempDiesel\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"rpmDiesel\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"ciclo1\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"ciclo2\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"ciclo3\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"ciclo4\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"resultadoValor\":\"").concat(String.valueOf(" ")).concat("\",");
                                tramaAuditoria = tramaAuditoria.concat("\"dilucion\":\"").concat("\",\"revisionVisual\":").concat("").concat("\"\",\"tablaAfectada\":\"medidas\",\"idRegistro\":\"").concat(String.valueOf(test.getIdPruebas())).concat("\"}");
                            }
                            if (test.getComentarioaborto() != null) {
                                if (test.getComentarioaborto().equalsIgnoreCase("DILUCION DE MUESTRA")) {
                                    int pos = tramaAuditoria.indexOf("dilucion");
                                    String tramaTemp = tramaAuditoria.substring(0, pos + 11);
                                    String tramaRest = tramaAuditoria.substring(pos + 11, tramaAuditoria.length());
                                    tramaAuditoria = tramaTemp.concat("X").concat(tramaRest);
                                }
                            }
                        }// fin de validacion
                        System.out.println("TRAMA ES " + tramaAuditoria);
                        auditoriaSicov.setTRAMA(tramaAuditoria);
                        System.out.println("SERIAL DE EQUIPO TOMADO PRUEBA " + test.getSerialEquipo());
                        auditoriaSicov.setSerialEquipoMedicion(serialEquipo);

                        if (test.getObservaciones() == null) {
                            auditoriaSicov.setObservacion(" ");
                        } else {
                            auditoriaSicov.setObservacion(test.getObservaciones());
                        }
                    }
                    auditoriaSicov.setIdRevision(test.getHojaPruebas().getTestsheet());
                    System.out.println("Esta es la fecha que deviene de la tabla pruebas " + test.getFechaFinal());
                    System.out.println(" ");
                    auditoriaSicov.setFechaEvento(test.getFechaFinal());
                    System.out.println("Esta es la fecha que toma la tbl Auditoria " + auditoriaSicov.getFechaEvento());
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(auditoriaSicov.getFechaEvento()); // Configuramos la fecha que se recibe
                    calendar.add(Calendar.MILLISECOND, 75859);
                    auditoriaSicov.setFechaRegistroBD(calendar.getTime());
                    auditoriaSicov.setUsuario(usuario.getNombreusuario());
                    auditoriaSicov.setIdentificacionUsuario(usuario.getCedula());
                    auditoriaSicov.setIdAuditoriaSICOV(idAud);
                    em.persist(auditoriaSicov);
                }
                em.getTransaction().commit();
            } catch (Exception ex) {
                System.out.println("ocurrio una exception al momento de reg tbl audi razon " + ex.getMessage());
                regIdAuditoria(indPrueba, revTec, false, null, null);
            }
            em.getTransaction().begin();
            em.flush();
            em.getTransaction().commit();
            System.out.println("reg trama sicov exitosamente ");

        }
    }

    private void btn_verificarActionPerformed(java.awt.event.ActionEvent evt) {
        Frame frame = (Frame) SwingUtilities.getWindowAncestor(this);
//        ControladorVerificar controladorVerificar = new ControladorVerificar();
        //Guardar las placas de un vehículo para la posterior fescritura en la base de datos.
        nrei = new NoReiniciable();
        placas = cam_placas.getText();//las placas del carro
        nrei.establecerPlacasBd(placas);//????

        //Captura de las placas del vehículo, nombre de usuario y contraseña...
        //placas = cam_placas.getText();
        nombre_usuario = cam_usuario.getText();//nombre de usuario y contraseña
        char p[] = cam_contraseña.getPassword();//
        pass = new String(p);//pasword

        //Traer la informacion del usuario
        System.out.println(" voy a buscar usuario ");
        Usuarios usuarioJPA = controladorVerificar.getUsuarioByNick(nombre_usuario, em);
        usr = usuarioJPA;
        if (em.getTransaction().isActive() == false)//si no hay una conexion activa se crea una, en caso de que ya haya una conexion creada no crea ninguna 
        {
            em.getTransaction().begin();
        }
        //em.getTransaction().begin();
        em.flush();
        em.getTransaction().commit();
        System.out.println(" pase de fusionamiento del contexto de persistencia ");
        int idHojaPruebaLocal = controladorVerificar.hojaPruebaMaxNoFin(placas, em);

        int aplicTrans = controladorVerificar.aplicTransito(idHojaPruebaLocal, em);
        System.out.println(" capturo hoja y demas logica ");
        if (idHojaPruebaLocal < 0) {
            JOptionPane.showMessageDialog(null, "Disculpe, este Vehiculo NO POSEE una hoja de prueba Autorizada .....");
            cam_contraseña.setText("");
            return;
        }
        final int revTec = controladorVerificar.esRevTecn(idHojaPruebaLocal, em);
        if (revTec == -1) {
            JOptionPane.showMessageDialog(null, "Disculpe, NO PUEDO CONTINUAR con la Prueba (FaltaInicioPin).");
            return;
        }
        Vehiculos tipo = controladorVerificar.getTipoVehiculo(placas, em);

        System.out.println(" c1");
        try {
            config = new BufferedReader(new FileReader(new File("configuracion.txt")));
            String line;
            System.out.println("entro a validar el tipo de pista a la que entro");
            while (!config.readLine().startsWith("[TIPOPISTA]")) {
            }
            if ((line = config.readLine()).startsWith("valida_tipo:"));
            valida_tipo = Integer.parseInt(line.substring(line.indexOf(" ") + 1, line.length()));

            if ((line = config.readLine()).startsWith("tipo_pista:"));
            tipoPista = line.substring(line.indexOf(" ") + 1, line.length());

        } catch (FileNotFoundException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {

        }

        int tipo1 = tipo.getTipoVehiculo().getCartype();
        System.out.println(" el tipo is " + tipo1);
        if (valida_tipo == 1) {
            if (tipo1 != 4 && tipo1 != 5 && tipo1 != 121 && tipo1 != 122) {
                JOptionPane.showMessageDialog(null, "DISCULPE; EL VEHICULO NO ES UNA MOTO");
                return;
            }
        }
        if (tipoPista.equalsIgnoreCase("Liviana")) {
            if (tipo1 == 3 && tipo1 == 7) {
                JOptionPane.showMessageDialog(null, "DISCULPE; EL VEHICULO NO ES UN CARRO LIVIANO");
                return;
            }
        }
        if (tipoPista.equalsIgnoreCase("Mixta")) {
            if (tipo1 == 5 && tipo1 == 4) {
                JOptionPane.showMessageDialog(null, "DISCULPE; EL VEHICULO NO ES UN CARRO Mixta");
                return;
            }
        }
        if (usuarioJPA == null) {
            JOptionPane.showMessageDialog(null, "DISCULPE; El Usuario no esta registrado en Repositorio de Datos");
            doClose(0);
            return;
        }
        if (usuarioJPA.getNickusuario().startsWith(".DT") == true) {
            JOptionPane.showMessageDialog(null, "DISCULPE; Los Directores Tecnicos no estan Autorizados para Efectuar Pruebas ");
            doClose(0);
            return;
        }
        if (!usuarioJPA.getContrasenia().equals(pass)) {
            JOptionPane.showMessageDialog(null, "DISCULPE; la Contraseña DIGITADA es incorrecta");
            cam_contraseña.setText("");
            return;
        }
        System.out.println(" c3");
        ipEquipo = UtilSicov.BusqIpAplPrueba();

        idUsuario = usuarioJPA.getGeuser();
        Cda cda = new Cda();
        try {
            Object[] arrayObjetos = UtilInfoServicioGases.getAtributosCDA(em);
            cda.setProveedorSicov(arrayObjetos[0].toString());
            cda.setUsuarioSicov(arrayObjetos[1].toString());
            cda.setPasswordSicov(arrayObjetos[2].toString());
            cda.setUrlServicioSicov(arrayObjetos[3].toString());
            cda.setUrlServicioEncript(arrayObjetos[4].toString());
            cda.setIdRunt(Integer.parseInt(arrayObjetos[5].toString()));
        } catch (SQLException ex) {
            Logger.getLogger(Frm_Placas.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Frm_Placas.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("estoy en  c4");
        EventoDTO eventoDTO = null;
        if (cda.getProveedorSicov().equalsIgnoreCase("INDRA")) {
            System.out.println("entre a indra");
            eventoDTO = new EventoDTO();
            eventoDTO.setIdPrueba(idPrueba);
            eventoDTO.setPlaca(placas);
            eventoDTO.setCodigoProveedor(cda.getUsuarioSicov());
            eventoDTO.setIdEvento(1);
            eventoDTO.setIdRunt(cda.getIdRunt().toString());
        }
        System.out.println("salgo de validar si es indra");

        //Validar que exista una hoja de prueba para el vehiculo no finalizada
        // HojaPruebasJpaController hpjc = new HojaPruebasJpaController();
        //Que es mas eficiente traer todas las pruebas o buscar segun el boton
        //VehiculosJpaController vjc = new VehiculosJpaController();
        if (nombreBoton.equals("Labrado")) {
            System.out.println("entro al boton de prueba de labrado ");
            pruebaLabrado(idHojaPruebaLocal, frame);
        }

        if (nombreBoton.equals("Inspeccion Sensorial")) {
            pruebaInspeccionSensorial(idHojaPruebaLocal, usuarioJPA, frame, revTec, cda, eventoDTO);
        }

        if (nombreBoton.equals("Desviacion")) {
            pruebaDesviacion(idHojaPruebaLocal, usuarioJPA, frame, revTec, cda, eventoDTO, aplicTrans);
        }

        if (nombreBoton.equals("Foto")) {
            pruebaFoto(idHojaPruebaLocal, usuarioJPA, frame);
        }

        if (nombreBoton.equals("Frenos")) {
            pruebaFrenos(idHojaPruebaLocal, usuarioJPA, frame, revTec, cda, eventoDTO, aplicTrans);
        }

        if (nombreBoton.equals("Gases")) {
            System.out.println("entra en la prueba de gases \n");
            pruebaGases(idHojaPruebaLocal, usuarioJPA, tipo, frame, revTec, cda, eventoDTO, aplicTrans);
        }

        if (nombreBoton.equals("Luces")) {
            pruebaLuces(idHojaPruebaLocal, usuarioJPA, frame, revTec, cda, eventoDTO, aplicTrans);
        }

        if (nombreBoton.equals("Ruido")) {
            pruebaRuido(idHojaPruebaLocal, usuarioJPA, frame, revTec, cda, eventoDTO);
        }
        System.out.println("nombre del boton: " + nombreBoton);
        if (nombreBoton.equals("Suspensión")) {
            System.out.println("entro a suspension");
            pruebaSuspension(idHojaPruebaLocal, frame, revTec, cda, eventoDTO, aplicTrans);
        }

        if (nombreBoton.equals("Taximetro")) {
            pruebaTaximetro(idHojaPruebaLocal, usuarioJPA, frame, revTec, cda, eventoDTO, aplicTrans);
        }

        if (nombreBoton.equals("Integracion")) {
            System.out.println("entro al boton de prueba de integracion ");
            pruebaIntegracion(idHojaPruebaLocal, usuarioJPA, frame, revTec, cda, eventoDTO, aplicTrans);
        }
        doClose(0);
    }

    /**
     * @autor ELKIN B
     * @param idHojaPruebaLocal
     * @param usuarioJPA
     * @param frame
     * @param revTec
     * @param cda
     * @param eventoDTO
     * @param aplicTrans
     */
    private void pruebaIntegracion(int idHojaPruebaLocal, Usuarios usuarioJPA, Frame frame, int revTec, Cda cda, EventoDTO eventoDTO, int aplicTrans) {

        System.out.println("-----------------------------------------------------");
        System.out.println("--------------realizando pruebaIntegracion------------");
        System.out.println("-----------------------------------------------------");
        String F = "", A = "", S = "";
        serialEquipo = "";

        try {//lee los datos de configuracion.txT depediendo de que variable este en "si" toma el serial del equipo
            F = UtilPropiedades.cargarPropiedad("frenos", "configuracion.txt");//LEE EL VALOR DE LA VARIABLE desviacion 
            A = UtilPropiedades.cargarPropiedad("desviacion", "configuracion.txt");//LEE EL VALOR DE LA VARIABLE suspension
            S = UtilPropiedades.cargarPropiedad("suspension", "configuracion.txt");//LEE EL VALOR DE LA VARIABLE frenos
            System.out.println("se realizara la prueba de \n Frenos: " + F + "\nDesviaacion: " + A + "\nSuspension: " + S);

        } catch (Exception e) {
            System.out.println(" error leyendo el archivo configuracion.txt");
        }
        try {
            idPruebaFreno = controladorVerificar.extraerIdPrueba(idHojaPruebaLocal, 5, em);//5 es prueba de Frenos
            idPruebaSuspension = controladorVerificar.extraerIdPrueba(idHojaPruebaLocal, 6, em);//6 es prueba de Suspension
            idPruebaDesviacion = controladorVerificar.extraerIdPrueba(idHojaPruebaLocal, 4, em);//4 es la prueba de Desviacion            
            //Crear el dialogo de integracion
            //08 Noviembre wdistinguir entre dialogo de pesados de 4x4 y de livianos
            Vehiculos v = controladorVerificar.getTipoVehiculo(placas, em);
            boolean ensenianza = v.getEsEnsenaza() > 0;

            if (v.getTipoVehiculo().getNombre().equalsIgnoreCase("Liviano") || v.getTipoVehiculo().getNombre().equalsIgnoreCase("Taxis_AplTaximetro") || v.getTipoVehiculo().getNombre().equalsIgnoreCase("Taxis")) {
//                PruebaDefaultDAO.escrTrans = "";

                if (revTec == 1) {
                    if (cda.getProveedorSicov().equalsIgnoreCase("INDRA")) {
                        ClienteSicov clienteIndra = new ClienteSicov();
                        eventoDTO.setNombrePrueba("FAS");
                        //envia los parametros para consultar el serial de los equipos dependiendo de la prueba 
                        serialEquipo = (F.equals("si")) ? serialEquipo.concat(UtilSicov.BusqSerialRegistrado("FRENO", valida_tipo).concat("_")) : serialEquipo;
                        serialEquipo = (A.equals("si")) ? serialEquipo.concat(UtilSicov.BusqSerialRegistrado("DESVIACION", valida_tipo).concat("_")) : serialEquipo;
                        serialEquipo = (S.equals("si")) ? serialEquipo.concat(UtilSicov.BusqSerialRegistrado("SUSPENSION", valida_tipo).concat("_")) : serialEquipo;
                        serialEquipo = StringUtils.removeEnd(serialEquipo, "_");//remuevo el _ que se encuentre al final de la trama
                        System.out.println("serial de prueba fas que voy a enviar :" + serialEquipo);
                        //serialEquipo = UtilSicov.BusqSerialRegistrado("TAXIMETRO",valida_tipo);
                        eventoDTO.setSerialEquipo(serialEquipo);//envia serial del equipo
                        Logger.getLogger(Frm_Placas.class.getName()).log(Level.INFO, null, "Serial: ".concat(serialEquipo));
                        System.out.println(" SERIAL  " + serialEquipo);
                        System.out.println(" FECHA  " + eventoDTO.getFecha());
                        Logger.getLogger(Frm_Placas.class.getName()).log(Level.INFO, null, "fecha: ".concat(eventoDTO.getFecha()));
                        RespuestaDTO respuesta = clienteIndra.crearEvento(eventoDTO, cda);
                        System.out.println("--------------------------------------------Respuesta Evento Verificado Integracion");
                        if (respuesta.getCodigoRespuesta().equals("0")) {
                            JOptionPane.showMessageDialog(null, "DISCULPE; No Pude iniciar el Evento para la Pruebas FAS");
                            doClose(0);
                            return;
                        }
                    }
                }

                DlgIntegradoLiviano dlgIntegracion = new DlgIntegradoLiviano(frame, idPruebaDesviacion, idPruebaSuspension, idPruebaFreno, idUsuario, idHojaPruebaLocal, ensenianza, aplicTrans, ipEquipo, v.getTipoVehiculo().getNombre(), v.getCarplate(), cam_usuario.getText());
                dlgIntegracion.setVisible(true);
                //segun eso configurar apropiadamente el dialogo para que realice solo
                //las pruebas que deba
                if (dlgIntegracion.isError_config()) {
                    doClose(0);
                    return;
                }
//                System.out.println("valor tomado en LIVIANO PRUEBAS INTEGRADAS  " + PruebaDefaultDAO.escrTrans);
                if (idPruebaFreno > 0) {
                    registarPruebaLog(placas, usuarioJPA, "Freno");
                    System.out.println("REGISTRO ESPERA CONFIRMACION TRANSACCION FRENOS ");
                    regIdAuditoria(idPruebaFreno, revTec, true, eventoDTO, cda);
                }
                //Suspension
                if (idPruebaSuspension > 0) {
                    System.out.println("REGISTRO ESPERA CONFIRMACION TRANSACCION SUSPENSION ");
                    regIdAuditoria(idPruebaSuspension, revTec, true, eventoDTO, cda);
                }
                //Desviacion
                if (idPruebaDesviacion > 0) {
                    registarPruebaLog(placas, usuarioJPA, "Desviacion");
                    System.out.println("REGISTRO ESPERA CONFIRMACION TRANSACCION DESVIACION ");
                    regIdAuditoria(idPruebaDesviacion, revTec, true, eventoDTO, cda);
                }
                //no iniciar el dialogo si no esta habilitada ni freno, ni desviacion, ni suspension
                if (idPruebaFreno < 0 && idPruebaSuspension < 0 && idPruebaDesviacion < 0) {
                    JOptionPane.showMessageDialog(null, "Disculpe; No esta HABILITADA    ninguna de las pruebas");
                    doClose(0);
                    return;
                }
                //Hace visible el dialogo               
            } else if (v.getTipoVehiculo().getNombre().equalsIgnoreCase("Pesado")) {
//                System.out.println("valor inicial  " + PruebaDefaultDAO.escrTrans);
                DlgIntegradoPesado dlgIntegracion = new DlgIntegradoPesado(frame, idPruebaDesviacion, idPruebaFreno, idUsuario, idHojaPruebaLocal, v.getNumeroejes(), ensenianza, aplicTrans, ipEquipo, v.getTipoVehiculo().getNombre(), v.getCarplate(), cam_usuario.getText());//Es necesario configurar bien el constructor
                //Hace visible el dialogo
                dlgIntegracion.setNumeroejes(v.getNumeroejes());//no seteaba apropiadamente el numero de ejes.
                dlgIntegracion.setVisible(true);
                if (dlgIntegracion.isError_config()) {
                    doClose(0);
                    return;
                }
                //Frenos
//                System.out.println("valor tomado en PESADOS PRUEBAS INTEGRADAS  " + PruebaDefaultDAO.escrTrans);
                if (idPruebaFreno > 0) {
                    registarPruebaLog(placas, usuarioJPA, "Freno");
//                    System.out.println("ESCANEADO SEUDO SERVIDOR SART 1.7  FRENOS  " + PruebaDefaultDAO.escrTrans);
                    regIdAuditoria(idPruebaFreno, revTec, true, eventoDTO, cda);
                }
                //Desviacion
                if (idPruebaDesviacion > 0) {
//                    System.out.println("ESCANEADO SEUDO SERVIDOR SART 1.7  DESVIACION  " + PruebaDefaultDAO.escrTrans);
                    regIdAuditoria(idPruebaDesviacion, revTec, true, eventoDTO, cda);
                }

                //no iniciar el dialogo si no esta habilitada ni freno, ni desviacion
                if (idPruebaFreno < 0 && idPruebaDesviacion < 0) {
                    JOptionPane.showMessageDialog(null, "DISCULPE No esta habilitada ninguna de las pruebas");
                    doClose(0);
                    return;
                }
            } else if (v.getTipoVehiculo().getNombre().equalsIgnoreCase("4x4")) {
//                PruebaDefaultDAO.escrTrans = "";
                DlgIntegrado4x4 dlgIntegracion = new DlgIntegrado4x4(frame, idPruebaDesviacion, idPruebaSuspension, idPruebaFreno, idUsuario, idHojaPruebaLocal, aplicTrans, ipEquipo, v.getTipoVehiculo().getNombre(), v.getCarplate(), cam_usuario.getText());//Es necesario configurar bien el constructor
                //segun eso configurar apropiadamente el dialogo para que realice solo 
                //las pruebas que deba
                dlgIntegracion.setNumeroejes(v.getNumeroejes());//no seteaba apropiadamente el numero de ejes.
                //Frenos
//                System.out.println("valor tomado en este codigo  " + PruebaDefaultDAO.escrTrans);
                if (idPruebaFreno > 0) {
                    registarPruebaLog(placas, usuarioJPA, "Freno");
                    regIdAuditoria(idPruebaFreno, revTec, true, eventoDTO, cda);

                }
                //Suspension
                if (idPruebaSuspension > 0) {
                    registarPruebaLog(placas, usuarioJPA, "Suspension");
                    regIdAuditoria(idPruebaSuspension, revTec, true, eventoDTO, cda);
                }

                //Desviacion
                if (idPruebaDesviacion > 0) {
                    registarPruebaLog(placas, usuarioJPA, "Desviacion");
                    regIdAuditoria(idPruebaDesviacion, revTec, true, eventoDTO, cda);
                }
                //no iniciar el dialogo si no esta habilitada ni freno, ni desviacion, ni suspension
                if (idPruebaFreno < 0 && idPruebaSuspension < 0 && idPruebaDesviacion < 0) {
                    JOptionPane.showMessageDialog(null, "No esta habilitada ninguna de las pruebas");
                    doClose(0);
                    return;
                }

                //Hace visible el dialogo
                dlgIntegracion.setVisible(true);

            } else {
                Mensajes.mensajeAdvertencia("Disculpe; Tipo de vehiculo invalido");
            }

        } catch (Exception e) {
            System.out.println("Error en el metodo :pruebaIntegracion() " + e.getLocalizedMessage() + e.getMessage());
        }

        doClose(0);
    }

    /**
     * @autor ELKIN B
     * @param idHojaPruebaLocal
     * @param usuarioJPA
     * @param frame
     * @param revTec
     * @param cda
     * @param eventoDTO
     * @param aplicTrans
     */
    private void pruebaTaximetro(int idHojaPruebaLocal, Usuarios usuarioJPA, Frame frame, int revTec, Cda cda, EventoDTO eventoDTO, int aplicTrans) {
        //humedad= encima de 30 relativa, temp ambiente 5-55
        System.out.println("----------------------------------------------------");
        System.out.println("--------------realizando pruebaTaximetro------------");
        System.out.println("----------------------------------------------------");
        try {
            idPrueba = controladorVerificar.extraerIdPrueba(idHojaPruebaLocal, 9, em);//9 es TAXIMETRO
//            idPrueba=2;
            if (idPrueba > 1) {
                controladorVerificar.clearContextoPrueba(idPrueba, em);
            }
            if (idPrueba < 0) {
                JOptionPane.showMessageDialog(null, "Este vehiculo no tiene autorizada la prueba");
                doClose(0);
                return;
            } else {//El vehiculo tiene autorizada la prueba
                registarPruebaLog(placas, usuarioJPA, "Taximetro");
                Vehiculos vehiculo = controladorVerificar.getTipoVehiculo(placas, em);
                System.out.println("voy a entrar en constructor taximetro   ");
                System.out.println("valor de aplicTrans    " + aplicTrans);
                if (revTec == 1) {
                    if (cda.getProveedorSicov().equalsIgnoreCase("INDRA")) {
                        ClienteSicov clienteIndra = new ClienteSicov();
                        eventoDTO.setNombrePrueba("Taximetro");
                        serialEquipo = UtilSicov.BusqSerialRegistrado("TAXIMETRO", valida_tipo);
//                        serialEquipo="1";                              
                        if (serialEquipo.equals("0")) {
                            Logger.getLogger(Frm_Placas.class.getName()).log(Level.WARNING, null, "NO PUEDE INICIALIZAR EVENTOS DE PLACA".concat(eventoDTO.getPlaca()).concat(" DE TIPO ").concat(eventoDTO.getNombrePrueba()));
                            JOptionPane.showMessageDialog(null, "DISCULPE; No Pude iniciar el Evento para la Pruebas TAXIMETRO,debido a: (Serial Equipo NO ENCONTRADO)");
                            doClose(0);
                            return;
                        }
                        String fecha = UtilSicov.askDate();
                        fecha = fecha.substring(0, fecha.length() - 2);
                        eventoDTO.setSerialEquipo(serialEquipo);
                        eventoDTO.setFecha(fecha);
                        RespuestaDTO respuesta = clienteIndra.crearEvento(eventoDTO, cda);
                        System.out.println("--------------------------------------------Respuesta Evento Verificado Taximetro");
                        if (respuesta.getCodigoRespuesta().equals("0")) {
                            JOptionPane.showMessageDialog(null, "DISCULPE; No Pude iniciar el Evento para la Pruebas FAS");
                            doClose(0);
                            return;
                        }
                        Logger.getLogger(Frm_Placas.class.getName()).log(Level.INFO, null, "Serial: ".concat(serialEquipo));
                        Logger.getLogger(Frm_Placas.class.getName()).log(Level.INFO, null, "fecha: ".concat(eventoDTO.getFecha()));

                        if (respuesta.getCodigoRespuesta().equals("0")) {
                            JOptionPane.showMessageDialog(null, "DISCULPE; No Pude iniciar el Evento para la Pruebas Taxímetro");
                            doClose(0);
                            return;
                        }
                    }
                }
//                vehiculo.getTipoVehiculo().setNombre("Taxis_AplTaximetro");
                if (vehiculo.getTipoVehiculo().getNombre().equalsIgnoreCase("Taxis_AplTaximetro")) {
                    System.out.println("idPrueba : " + idPrueba + " idUsuario : " + idUsuario + " idHojaPruebaLocal : " + idHojaPruebaLocal + " Nombrellanta  vehiculo " + vehiculo.getLlantas().getNombrellanta() + " aplicTrans " + aplicTrans);
                    DlgTaximetro dlgTaximetro = new DlgTaximetro(frame, true, idPrueba, idUsuario, idHojaPruebaLocal, vehiculo.getLlantas().getNombrellanta(), aplicTrans);
                    dlgTaximetro.setVisible(true);
                    regIdAuditoria(idPrueba, revTec, true, eventoDTO, cda);
                } else {
                    JOptionPane.showMessageDialog(null, "Este vehiculo no aplica para la Prueba Mecanizada  de Taximetro ..!");
                    doClose(0);
                    return;
                }
            }//end else vehiculo autorizada prueba

        } catch (Exception e) {
            System.out.println("Error en el metodo : pruebaTaximetro()" + e);
            System.out.println("Error " + e.getMessage());
        }
        doClose(0);
    }

    /**
     * @autor ELKIN B
     * @param idHojaPruebaLocal
     * @param frame
     * @param revTec
     * @param cda
     * @param eventoDTO
     * @param aplicTrans
     */
    private void pruebaSuspension(int idHojaPruebaLocal, Frame frame, int revTec, Cda cda, EventoDTO eventoDTO, int aplicTrans) {
        System.out.println("----------------------------------------------------");
        System.out.println("--------------realizando Suspension-----------------");
        System.out.println("----------------------------------------------------");
        try {
            idPrueba = controladorVerificar.extraerIdPrueba(idHojaPruebaLocal, 6, em);//6 es SUSPENSION
            if (idPrueba > 1) {
                controladorVerificar.clearContextoPrueba(idPrueba, em);
            }
            if (idPrueba < 0) {
                JOptionPane.showMessageDialog(null, "Este vehiculo no tiene autorizada la prueba");
                doClose(0);
                return;
            } else {//El vehiculo tiene autorizada la prueba 
//                PruebaDefaultDAO.escrTrans = "";
                Vehiculos v = controladorVerificar.getTipoVehiculo(placas, em);
                boolean ensenianza = v.getEsEnsenaza() > 0;
//                System.out.println("valor inicial  " + PruebaDefaultDAO.escrTrans);
                if (revTec == 1) {
                    if (cda.getProveedorSicov().equalsIgnoreCase("INDRA")) {
                        ClienteSicov clienteIndra = new ClienteSicov();
                        eventoDTO.setNombrePrueba("FAS");
                        serialEquipo = UtilSicov.BusqSerialRegistrado("SUSPENSION", valida_tipo);
                        if (serialEquipo.equals("0")) {
                            Logger.getLogger(Frm_Placas.class.getName()).log(Level.WARNING, null, "NO PUEDE INICIALIZAR EVENTOS DE PLACA".concat(eventoDTO.getPlaca()).concat(" DE TIPO ").concat(eventoDTO.getNombrePrueba()));
                            JOptionPane.showMessageDialog(null, "DISCULPE; No Pude iniciar el Evento para la Pruebas SUSPENSION,debido a: (Serial Equipo NO ENCONTRADO)");
                            doClose(0);
                            return;
                        }
                        String fecha = UtilSicov.askDate();
                        fecha = fecha.substring(0, fecha.length() - 2);
                        eventoDTO.setSerialEquipo(serialEquipo);
                        eventoDTO.setFecha(fecha);
                        RespuestaDTO respuesta = clienteIndra.crearEvento(eventoDTO, cda);
                        System.out.println("--------------------------------------------Respuesta Evento Verificado Suspension");
                        Logger.getLogger(Frm_Placas.class.getName()).log(Level.INFO, null, "Serial: ".concat(serialEquipo));
                        Logger.getLogger(Frm_Placas.class.getName()).log(Level.INFO, null, "fecha: ".concat(eventoDTO.getFecha()));
                        if (respuesta.getCodigoRespuesta().equals("0")) {
                            JOptionPane.showMessageDialog(null, "DISCULPE; No Pude iniciar el Evento para la Pruebas FAS");
                            doClose(0);
                            return;
                        }
                    }
                }
                switch (v.getTipoVehiculo().getNombre()) {
                    case "Liviano":
                        new DlgIntegradoLiviano(frame, 0, idPrueba, 0, idUsuario, idHojaPruebaLocal, ensenianza, aplicTrans, ipEquipo, v.getTipoVehiculo().getNombre(), v.getCarplate(), cam_usuario.getText()).setVisible(true);
                        System.out.println("VOY A REGISTAR TIMER DE TRANSACCION (LIVIANO) SUSPENSION ");
                        regIdAuditoria(idPrueba, revTec, true, eventoDTO, cda);
                        break;
                    case "Taxis_AplTaximetro":
                        new DlgIntegradoLiviano(frame, 0, idPrueba, 0, idUsuario, idHojaPruebaLocal, ensenianza, aplicTrans, ipEquipo, v.getTipoVehiculo().getNombre(), v.getCarplate(), cam_usuario.getText()).setVisible(true);

                        regIdAuditoria(idPrueba, revTec, true, eventoDTO, cda);

                        break;
                    case "Taxis":
                        new DlgIntegradoLiviano(frame, 0, idPrueba, 0, idUsuario, idHojaPruebaLocal, ensenianza, aplicTrans, ipEquipo, v.getTipoVehiculo().getNombre(), v.getCarplate(), cam_usuario.getText()).setVisible(true);

                        regIdAuditoria(idPrueba, revTec, true, eventoDTO, cda);

                        break;
                    case "4x4":
                        //new DlgIntegrado4x4(frame, 0, idPrueba, 0, idUsuario, idHojaPruebaLocal, aplicTrans, ipEquipo, v.getTipoVehiculo().getNombre(), v.getCarplate(), cam_usuario.getText()).setVisible(true);
                        new DlgIntegradoLiviano(frame, 0, idPrueba, 0, idUsuario, idHojaPruebaLocal, ensenianza, aplicTrans, ipEquipo, v.getTipoVehiculo().getNombre(), v.getCarplate(), cam_usuario.getText()).setVisible(true);

                        regIdAuditoria(idPrueba, revTec, true, eventoDTO, cda);

                        break;
                }
            }//end else vehiculo autorizada prueba

        } catch (Exception e) {
            System.out.println("Error en el metodo : pruebaSuspension()" + e.getLocalizedMessage() + e.getMessage());
        }
        doClose(0);
    }

    /**
     * @autor ELKIN B
     * @param idHojaPruebaLocal
     * @param usuarioJPA
     * @param frame
     * @param revTec
     * @param cda
     * @param eventoDTO
     * @param aplicTrans
     */
    private void pruebaRuido(int idHojaPruebaLocal, Usuarios usuarioJPA, Frame frame, int revTec, Cda cda, EventoDTO eventoDTO) {
        System.out.println("----------------------------------------------------");
        System.out.println("--------------realizando pruebaRuido----------------");
        System.out.println("----------------------------------------------------");
        try {
            idPrueba = controladorVerificar.extraerIdPrueba(idHojaPruebaLocal, 7, em);//7 es el sonometro
            if (idPrueba > 1) {
                controladorVerificar.clearContextoPrueba(idPrueba, em);
            }
            if (idPrueba < 0) {
                JOptionPane.showMessageDialog(null, "Este vehiculo no tiene autorizada la prueba");
                doClose(0);
                return;
            } else {
                try {
                    //El vehiculo tiene autorizada la prueba
                    registarPruebaLog(placas, usuarioJPA, "Ruido");
                    Vehiculos v = controladorVerificar.getTipoVehiculo(placas, em);
                    if (revTec == 1) {
                        if (cda.getProveedorSicov().equalsIgnoreCase("INDRA")) {
                            serialEquipo = UtilSicov.BusqSerialRegistrado("RUIDO", valida_tipo);
                            if (serialEquipo.equals("0")) {
//                                Logger.getLogger(Frm_Placas.class.getName()).log(Level.WARNING, null, "NO PUEDE INICIALIZAR EVENTOS DE PLACA".concat(eventoDTO.getPlaca()).concat(" DE TIPO ").concat(eventoDTO.getNombrePrueba())); 
                                JOptionPane.showMessageDialog(null, "DISCULPE; No Pude iniciar el Evento para la Pruebas RUIDO,debido a: (Serial Equipo NO ENCONTRADO)");
                                doClose(0);
                                return;
                            }
                            Logger.getLogger(Frm_Placas.class.getName()).log(Level.INFO, null, "Serial: ".concat(serialEquipo));
                        }
                    }
                    JDialogSonometro jDialogSonometro = new JDialogSonometro(frame, true, idPrueba, idUsuario, idHojaPruebaLocal, v.getTiposGasolina().getNombregasolina());
                    jDialogSonometro.setModal(true);
                    System.out.println("la logre poner modal xx");
                    jDialogSonometro.setVisible(true);
                    try {
                        Thread.sleep(800);
                    } catch (InterruptedException ex) {
                    }
                    System.out.println("VOy a reg idAuditoria, el id prueba es " + idPrueba);
                    boolean fall = false;
                    regIdAuditoriaRuido(revTec, true, eventoDTO ,cda);
                } catch (Exception ex) {
                    Logger.getLogger(Frm_Placas.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        } catch (Exception e) {
            System.out.println("Error en el metodo :pruebaRuido() " + e.getLocalizedMessage() + e.getMessage());
        }

        doClose(0);
    }

    /**
     * @autor ELKIN B
     * @param idHojaPruebaLocal
     * @param usuarioJPA
     * @param frame
     * @param revTec
     * @param cda
     * @param eventoDTO
     * @param aplicTrans
     */
    private void pruebaLuces(int idHojaPruebaLocal, Usuarios usuarioJPA, Frame frame, int revTec, Cda cda, EventoDTO eventoDTO, int aplicTrans) {
        System.out.println("-----------------------------------------------------");
        System.out.println("--------------realizando pruebaLuces-----------------");
        System.out.println("-----------------------------------------------------");
        try {
            idPrueba = controladorVerificar.extraerIdPrueba(idHojaPruebaLocal, 2, em);//cambiar por constante
            if (idPrueba > 1) {
                controladorVerificar.clearContextoPrueba(idPrueba, em);
            }
            if (idPrueba < 0) {
                JOptionPane.showMessageDialog(null, "Este vehiculo no tiene ASIGNADA una Revision Tecnomecanica");
                doClose(0);
                return;
            } else {
                registarPruebaLog(placas, usuarioJPA, "Luces");
                Vehiculos vehiculoVariable = controladorVerificar.getTipoVehiculo(placas, em);
                Properties properties = new Properties();
                String versionLuxometro = "";
                String ubicacionLuxometro = "";
                String verPruebas = "";
                try {
                    properties.load(new FileInputStream(new File("./propiedades.properties")));
                    versionLuxometro = properties.getProperty("VersionLuxometro");
                    if (properties.getProperty("ubicacionLuxometro") != null) {
                        ubicacionLuxometro = properties.getProperty("ubicacionLuxometro");
                    }
                    if (properties.getProperty("verMedidasLuxometro") != null) {
                        verPruebas = properties.getProperty("verMedidasLuxometro");
                    }
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Frm_Placas.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Frm_Placas.class.getName()).log(Level.SEVERE, null, ex);
                }

                //Propiedades.VersionLuxometro versionLuxometro = Propiedades.getInstance().getVersionLuxometro();
                String tipoLux = versionLuxometro;
                System.out.println("-------------------------------------------");
                System.out.println("------------------tipoLux-- : " + tipoLux);
                System.out.println("-------------------------------------------");

                //System.out.println("valor inicial  " + PruebaDefaultDAO.escrTrans);
                if (revTec == 1) {
                    if (cda.getProveedorSicov().equalsIgnoreCase("INDRA")) {
                        ClienteSicov clienteIndra = new ClienteSicov();
                        eventoDTO.setNombrePrueba("Luces");///
                        serialEquipo = UtilSicov.BusqSerialRegistrado("LUCES", valida_tipo);
                        if (serialEquipo.equals("0")) {
                            Logger.getLogger(Frm_Placas.class.getName()).log(Level.WARNING, null, "NO PUEDE INICIALIZAR EVENTOS DE PLACA".concat(eventoDTO.getPlaca()).concat(" DE TIPO ").concat(eventoDTO.getNombrePrueba()));
                            JOptionPane.showMessageDialog(null, "DISCULPE; No Pude iniciar el Evento para la Pruebas LUCES,debido a: (Serial Equipo NO ENCONTRADO)");
                            doClose(0);
                            return;
                        }
                        Logger.getLogger(Frm_Placas.class.getName()).log(Level.INFO, null, "Serial -- : ".concat(serialEquipo));
                        String fecha = UtilSicov.askDate();
                        fecha = fecha.substring(0, fecha.length() - 2);
                        eventoDTO.setSerialEquipo(serialEquipo);///
                        eventoDTO.setMensajeEvento("");
                        eventoDTO.setFecha(fecha);
                        RespuestaDTO respuesta = clienteIndra.crearEvento(eventoDTO, cda);
                        System.out.println("Respuesta de envio Evento Luces-------------------------------");
                        Logger.getLogger(Frm_Placas.class.getName()).log(Level.INFO, null, "Serial -- : ".concat(serialEquipo));
                        Logger.getLogger(Frm_Placas.class.getName()).log(Level.INFO, null, "fecha -- : ".concat(eventoDTO.getFecha()));
                        if (respuesta.getCodigoRespuesta().equals("0")) {
                            JOptionPane.showMessageDialog(null, "DISCULPE; No Pude iniciar el Evento para la Pruebas Luces");
                            doClose(0);
                            return;
                       }
                    }
                }
                if (null != versionLuxometro) {

                    if (versionLuxometro.equalsIgnoreCase("LUJAN")) {
                        if (
                            vehiculoVariable.getTipoVehiculo().getNombre().equalsIgnoreCase("Moto") 
                            || vehiculoVariable.getTipoVehiculo().getNombre().equalsIgnoreCase("Motocarro")
                            || vehiculoVariable.getTipoVehiculo().getNombre().equalsIgnoreCase("CICLOMOTOR")
                            || vehiculoVariable.getTipoVehiculo().getNombre().equalsIgnoreCase("TRICIMOTO")
                        ) {//Si es una moto entonces hacer prueba de moto
                            Long indPrueba = Long.parseLong((String.valueOf(idPrueba)));
                            JDialogLucesMotoLujan jDialogLucesMoto = new JDialogLucesMotoLujan(frame, true, indPrueba, idHojaPruebaLocal, idUsuario, aplicTrans, tipoLux, placas);
                            regIdAuditoria(idPrueba, revTec, true, eventoDTO, cda);
                            System.out.println("sali pruebas de luces xxx");
                            doClose(0);
                        }
                        if (vehiculoVariable.getTipoVehiculo().getNombre().equalsIgnoreCase("Liviano") || vehiculoVariable.getTipoVehiculo().getNombre().equalsIgnoreCase("Pesado") || vehiculoVariable.getTipoVehiculo().getNombre().equalsIgnoreCase("4x4") || vehiculoVariable.getTipoVehiculo().getNombre().equalsIgnoreCase("Taxis_AplTaximetro") || vehiculoVariable.getTipoVehiculo().getNombre().equalsIgnoreCase("Taxis")) {
                            System.out.println("estoy entrando a Jdialogo para implementar pruebas de luces xxx");
                            HiloPruebaLuxometroLujan.aplicTrans = aplicTrans;
                            JDialogLucesVehiculoLujan jDialogLuces = new JDialogLucesVehiculoLujan(frame, true, idPrueba, idUsuario, idHojaPruebaLocal, aplicTrans, placas);
                            regIdAuditoria(idPrueba, revTec, true, eventoDTO, cda);
                            doClose(0);
                        }
                    }

                    String tipo = vehiculoVariable.getTipoVehiculo().getNombre();
                    if (versionLuxometro.equalsIgnoreCase("SERIAL")) {
                        if (
                            tipo.equalsIgnoreCase("Moto") 
                            || tipo.equalsIgnoreCase("Motocarro")
                            || tipo.equalsIgnoreCase("TRICIMOTO")
                            || tipo.equalsIgnoreCase("CICLOMOTOR")
                        ) {//Si es una moto entonces hacer prueba de moto
                            Long indPrueba = Long.parseLong((String.valueOf(idPrueba)));
                            JDialogLucesMoto jDialogLucesMoto = new JDialogLucesMoto(frame, true, indPrueba, idHojaPruebaLocal, idUsuario, aplicTrans, tipoLux, tipo);
                            regIdAuditoria(idPrueba, revTec, true, eventoDTO, cda);
                            System.out.println("sali pruebas de luces xxx");
                            doClose(0);
                        } else if (
                            tipo.equalsIgnoreCase("Liviano") 
                            || tipo.equalsIgnoreCase("Pesado") 
                            || tipo.equalsIgnoreCase("4x4") 
                            || tipo.equalsIgnoreCase("Taxis_AplTaximetro") 
                            || tipo.equalsIgnoreCase("Taxis")
                        ) {
                            System.out.println("estoy entrando a Jdialogo para implementar pruebas de luces xxx");
                            HiloPruebaLuxometro.aplicTrans = aplicTrans;
                            System.out.println("ya sali de crear el aplicTransacción");
                            JDialogLuces jDialogLuces = new JDialogLuces(frame, true, idPrueba, idUsuario, idHojaPruebaLocal, aplicTrans, tipo);
                            System.out.println("se creo el jdialogluces");
                            regIdAuditoria(idPrueba, revTec, true, eventoDTO, cda);
                            System.out.println("salgo del regIdAuditoria");
                            doClose(0);
                        }
                    }
                    if (versionLuxometro.equalsIgnoreCase("GEMINI")) {
                        JDialogGemini jDialogGemini = new JDialogGemini(frame, true, idPrueba, idUsuario, idHojaPruebaLocal, placas);
                        regIdAuditoria(idPrueba, revTec, true, eventoDTO, cda);
                        doClose(0);
                    }
                    if (versionLuxometro.equalsIgnoreCase("CHINO")) {
                        new JDialogLuxometroChino(frame, true, idPrueba, idUsuario).setVisible(true);
                        doClose(0);
                    }
                    if (versionLuxometro.equalsIgnoreCase("MOON")) {
                        LecturaArchivoLuxometroMoon archivoLuxometroMoon = new LecturaArchivoLuxometroMoon(aplicTrans);
//                        LecturaArchivoLuxomMoonPru archivLuxo= new LecturaArchivoLuxomMoonPru();
                        int tipoVehiculo = 0;
                        if (
                            vehiculoVariable.getTipoVehiculo().getNombre().equalsIgnoreCase("Moto") 
                            || vehiculoVariable.getTipoVehiculo().getNombre().equalsIgnoreCase("Motocarro")
                            || vehiculoVariable.getTipoVehiculo().getNombre().equalsIgnoreCase("CICLOMOTOR")
                            || vehiculoVariable.getTipoVehiculo().getNombre().equalsIgnoreCase("TRICIMOTO")
                        ) {
                            tipoVehiculo = 1;
                            archivoLuxometroMoon.iniciarTomaMedidaLuxometro(placas, idHojaPruebaLocal, idPrueba, idUsuario, tipoVehiculo, ubicacionLuxometro);
                        } else {
                            tipoVehiculo = 2;
                            System.out.println("entre luxometro");
                            boolean verMedidas = verPruebas.equalsIgnoreCase("SI");
                            
                            FrmLuxometroCapelec luxometroCapelec = new FrmLuxometroCapelec("Moon", placas, idHojaPruebaLocal, idPrueba, idUsuario, ubicacionLuxometro, verMedidas);
                            
                            JDialog dialog = new JDialog(this, true); // Crear un JDialog
                            dialog.add(luxometroCapelec.getContentPane()); // Agregar el contenido del formulario FrmSeriales al JDialog
                            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); // Establecer la operación de cierre del JDialog
                            dialog.pack(); // Ajustar el tamaño del JDialog al tamaño preferido de su contenido
                            dialog.setTitle("Luxometro"); // Establecer el título del JDialog
                            regIdAuditoria(idPrueba, revTec, true, eventoDTO, cda);
                            doClose(0);
                            dialog.setVisible(true); // Hacer visible el JDialog
                        }

                        
//                        archivLuxo.iniciarTomaMedidaLuxometro(placas, idHojaPruebaLocal, idPrueba, idUsuario,tipoVehiculo);
                        regIdAuditoria(idPrueba, revTec, true, eventoDTO, cda);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                        }
                        //   regTblAuditoriaSicov(LecturaArchivoLuxometroMoon.tramaAuditoria, 2, 1, idPrueba, revTec, "E", " ", usr);
                        LecturaArchivoLuxometroMoon.tramaAuditoria = "";
                    }
                    if (versionLuxometro.equalsIgnoreCase("CAPELEC")) {
                        if (
                            vehiculoVariable.getTipoVehiculo().getNombre().equalsIgnoreCase("Moto") 
                            || vehiculoVariable.getTipoVehiculo().getNombre().equalsIgnoreCase("Motocarro")
                            || vehiculoVariable.getTipoVehiculo().getNombre().equalsIgnoreCase("CICLOMOTOR")
                            || vehiculoVariable.getTipoVehiculo().getNombre().equalsIgnoreCase("TRICIMOTO")
                        ) {//Si es una moto entonces hacer prueba de moto
                            JDialogLucesMotoCapelec jDialogLucesMoto = new JDialogLucesMotoCapelec(frame, true, idPrueba, idHojaPruebaLocal, idUsuario);
                            regIdAuditoria(idPrueba, revTec, true, eventoDTO, cda);
                            doClose(0);
                        } else if (vehiculoVariable.getTipoVehiculo().getNombre().equalsIgnoreCase("Liviano") || vehiculoVariable.getTipoVehiculo().getNombre().equalsIgnoreCase("Pesado") || vehiculoVariable.getTipoVehiculo().getNombre().equalsIgnoreCase("4x4") || vehiculoVariable.getTipoVehiculo().getNombre().equalsIgnoreCase("Taxis_AplTaximetro") || vehiculoVariable.getTipoVehiculo().getNombre().equalsIgnoreCase("Taxis")) {
                            System.out.println("estoy entrando a Jdialogo paraa implementar pruebas de luces como CAPELEC");
                            JDialogLucesCarros jDialogLuces = new JDialogLucesCarros(frame, true, idPrueba, idUsuario, idHojaPruebaLocal);
                            regIdAuditoria(idPrueba, revTec, true, eventoDTO, cda);
                            doClose(0);
                        }
                    }

                    if (versionLuxometro.equalsIgnoreCase("CAPELECV2")) {
                        System.out.println("esta es la ubicacion del capelec: "+ubicacionLuxometro);
                        boolean verMedidas = verPruebas.equalsIgnoreCase("SI");
                        FrmLuxometroCapelec luxometroCapelec = new FrmLuxometroCapelec("Capelec", placas, idHojaPruebaLocal, idPrueba, idUsuario, ubicacionLuxometro, verMedidas);
                            
                        JDialog dialog = new JDialog(this, true); // Crear un JDialog
                        dialog.add(luxometroCapelec.getContentPane()); // Agregar el contenido del formulario FrmSeriales al JDialog
                        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); // Establecer la operación de cierre del JDialog
                        dialog.pack(); // Ajustar el tamaño del JDialog al tamaño preferido de su contenido
                        dialog.setTitle("Luxometro"); // Establecer el título del JDialog
                        regIdAuditoria(idPrueba, revTec, true, eventoDTO, cda);
                        doClose(0);
                        dialog.setVisible(true); // Hacer visible el JDialog
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Error en el metodo :pruebaLuces() " + e.getLocalizedMessage() + e.getMessage());
        }
        doClose(0);
    }

    /**
     * @autor ELKIN B
     *
     * @param idHojaPruebaLocal
     * @param frame
     */
    private void pruebaLabrado(int idHojaPruebaLocal, Frame frame) {
        System.out.println("-----------------------------------------------------");
        System.out.println("--------------realizando Labrado----------------------");
        System.out.println("-----------------------------------------------------");

        try {
            idPrueba = controladorVerificar.extraerIdPrueba(idHojaPruebaLocal, 1, em);
            if (idPrueba > 1) {
                controladorVerificar.clearContextoPrueba(idPrueba, em);
            }
            Vehiculos vehiculo = controladorVerificar.getTipoVehiculo(placas, em);
            if (idPrueba < 0) {
                JOptionPane.showMessageDialog(null, "DISCULPE; a Este vehiculo ya se le cerro la Prueba de Inspeccion Sensorial ");
                doClose(0);
                return;
            }
            if (vehiculo.getTipoVehiculo().getNombre().equalsIgnoreCase("Moto")) {
                LabradoPresionMoto frm = new LabradoPresionMoto(frame, true, idVehiculoInt, idHojaPruebaLocal, idPrueba, idUsuario, vehiculo, em);
                doClose(0);
                frm.setVisible(true);

            }
            if (vehiculo.getTipoVehiculo().getNombre().equalsIgnoreCase("Liviano") || vehiculo.getTipoVehiculo().getNombre().equalsIgnoreCase("Pesado") || vehiculo.getTipoVehiculo().getNombre().equalsIgnoreCase("4x4") || vehiculo.getTipoVehiculo().getNombre().equalsIgnoreCase("Taxis_AplTaximetro") || vehiculo.getTipoVehiculo().getNombre().equalsIgnoreCase("Taxis")) {
                LabradoPresionLiviano frm = new LabradoPresionLiviano(frame, true, idVehiculoInt, idHojaPruebaLocal, idPrueba, idUsuario, vehiculo, em);
                doClose(0);
                frm.setVisible(true);
            }
            if (vehiculo.getTipoVehiculo().getNombre().equalsIgnoreCase("Motocarro")) {
                LabradoPresionMotoCarro frm = new LabradoPresionMotoCarro(frame, true, idVehiculoInt, idHojaPruebaLocal, idPrueba, idUsuario, vehiculo, em);
                doClose(0);
                frm.setVisible(true);
            }
        } catch (Exception e) {
            System.out.println("Error en el metodo Labrado(): " + e.getLocalizedMessage() + e.getMessage());
        }
    }

    /**
     * @autor ELKIN B
     *
     * @param idHojaPruebaLocal
     * @param usuarioJPA
     * @param frame
     * @param revTec
     * @param cda
     * @param eventoDTO
     * @param aplicTrans
     */
    private void pruebaFrenos(int idHojaPruebaLocal, Usuarios usuarioJPA, Frame frame, int revTec, Cda cda, EventoDTO eventoDTO, int aplicTrans) {
        System.out.println("-----------------------------------------------------");
        System.out.println("--------------realizando frenos-----------------------");
        System.out.println("-----------------------------------------------------");
        //CAMBIO FRENO DE ENSEÑANZA
        // boolean ensenianza = false;
        try {
            int idPruebaV = controladorVerificar.extraerIdPrueba(idHojaPruebaLocal, 1, em);

            try {
                if (controladorVerificar.getPruebaNoFinalizadaTipo(idPruebaV, 1, em) < 0) {
                    JOptionPane.showMessageDialog(null, "La inspeccion visual no ha sido terminada");
                    doClose(0);
                    return;
                }
                Query q = em.createQuery("SELECT * FROM Defxprueba dp WHERE dp.id_prueba = ?1 ORDER BY dp.id_prueba DESC ");
                q.setParameter(1, idPruebaV);
                List<Object[]> lstEscalar = q.getResultList();
                System.out.println(lstEscalar);
                //for (Defxprueba defxprueba : idPruebaV.getDefxpruebaList())
            } catch (Exception ex) {
                Logger.getLogger(Frm_Placas.class.getName()).log(Level.SEVERE, null, ex);
            }
            idPrueba = controladorVerificar.extraerIdPrueba(idHojaPruebaLocal, 5, em);//cambiar por constante
            if (idPrueba > 1) {
                controladorVerificar.clearContextoPrueba(idPrueba, em);
            }
            if (idPrueba < 0) {
                JOptionPane.showMessageDialog(null, "Este vehiculo no tiene autorizada la prueba");
                doClose(0);
                return;
            } else {
//                PruebaDefaultDAO.escrTrans = "";
                registarPruebaLog(placas, usuarioJPA, "Frenos");
//                System.out.println("valor inicial  " + PruebaDefaultDAO.escrTrans);
                Vehiculos vehiculoVariable = controladorVerificar.getTipoVehiculo(placas, em);
                boolean ensenianza = vehiculoVariable.getEsEnsenaza() > 0;
                //CAMBIO FRENO DE ENSEÑANZA
                System.out.println("c5");
                if (revTec == 1) {
                    if (cda.getProveedorSicov().equalsIgnoreCase("INDRA")) {
                        ClienteSicov clienteIndra = new ClienteSicov();
                        eventoDTO.setNombrePrueba("FAS");
                        serialEquipo = UtilSicov.BusqSerialRegistrado("FRENO", valida_tipo);
                        if (serialEquipo.equals("0")) {
                            Logger.getLogger(Frm_Placas.class.getName()).log(Level.WARNING, null, "NO PUEDE INICIALIZAR EVENTOS DE PLACA".concat(eventoDTO.getPlaca()).concat(" DE TIPO ").concat(eventoDTO.getNombrePrueba()));
                            JOptionPane.showMessageDialog(null, "DISCULPE; No Pude iniciar el Evento para la Pruebas FRENOS,debido a: (Serial Equipo NO ENCONTRADO)");
                            doClose(0);
                            return;
                        }
                        Logger.getLogger(Frm_Placas.class.getName()).log(Level.INFO, null, "Serial: ".concat(serialEquipo));
                        String fecha = UtilSicov.askDate();
                        fecha = fecha.substring(0, fecha.length() - 2);
                        eventoDTO.setSerialEquipo(serialEquipo);
                        eventoDTO.setFecha(fecha);
                        RespuestaDTO respuesta = clienteIndra.crearEvento(eventoDTO, cda);
                        System.out.println("--------------------------------------------Respuesta Evento Verificado Frenos");
                        Logger.getLogger(Frm_Placas.class.getName()).log(Level.INFO, null, "Serial: ".concat(serialEquipo));

                        Logger.getLogger(Frm_Placas.class.getName()).log(Level.INFO, null, "fecha: ".concat(eventoDTO.getFecha()));
                        if (respuesta.getCodigoRespuesta().equals("0")) {
                            JOptionPane.showMessageDialog(null, "DISCULPE; No Pude iniciar el Evento para la Pruebas FAS");
                            doClose(0);
                            return;
                        }
                    }
                }
                if (
                    vehiculoVariable.getTipoVehiculo().getNombre().equalsIgnoreCase("Moto")
                    || vehiculoVariable.getTipoVehiculo().getNombre().equalsIgnoreCase("CICLOMOTOR")
                ) {//Si es una moto entonces hacer prueba de moto
                    //TODO: Prueba de frenos con plancha o rodillos
                    boolean plancha = false;
                    String version = "v1";
                    try {
                        Properties properties = new Properties();
                        properties.load(new FileInputStream(new File("./propiedades.properties")));
                        plancha = Boolean.parseBoolean(properties.getProperty("plancha"));
                        version = properties.getProperty("planchaVersion", "v1");

                    } catch (Exception e) {
                        System.out.println("No se encontro la propiedad plancha " + e);
                    }
                    if (plancha) {
                        if (version.equals("v1")) {
                            Logger.getLogger(Frm_Placas.class.getName()).log(Level.WARNING, null, "entre frenos  dialogfrenmotos".concat(eventoDTO.getPlaca()));
                            dialogfrenmotos d = new dialogfrenmotos(frame, true, idPrueba, idUsuario, idHojaPruebaLocal, aplicTrans, ipEquipo);
                            d.setVisible(true);
                            doClose(0);
                            regIdAuditoria(idPrueba, revTec, true, eventoDTO, cda);
                        } else {
                            Logger.getLogger(Frm_Placas.class.getName()).log(Level.WARNING, null, "entre frenos  dialogfrenmotos2".concat(eventoDTO.getPlaca()));
                            dialogfrenmotos2 d = new dialogfrenmotos2(frame, true, idPrueba, idUsuario, idHojaPruebaLocal);
                            d.setVisible(true);
                            doClose(0);
                            regIdAuditoria(idPrueba, revTec, true, eventoDTO, cda);
                        }
                    } else {
                        DlgFrenoMoto dfm = new DlgFrenoMoto(frame, true, idPrueba, idUsuario, idHojaPruebaLocal, aplicTrans, ipEquipo, vehiculoVariable.getCarplate(), cam_usuario.getText());
                        dfm.setVisible(true);
                        doClose(0);
                        regIdAuditoria(idPrueba, revTec, true, eventoDTO, cda);
                    }
                } else if (vehiculoVariable.getTipoVehiculo().getNombre().equalsIgnoreCase("Liviano") || vehiculoVariable.getTipoVehiculo().getNombre().equalsIgnoreCase("Taxis_AplTaximetro") || vehiculoVariable.getTipoVehiculo().getNombre().equalsIgnoreCase("Taxis")) {
                    System.out.println(" c7");
                    DlgIntegradoLiviano dlgFrenLivianos = new DlgIntegradoLiviano(frame, 0, 0, idPrueba, idUsuario, idHojaPruebaLocal, ensenianza, aplicTrans, ipEquipo, vehiculoVariable.getTipoVehiculo().getNombre(), vehiculoVariable.getCarplate(), cam_usuario.getText());
                    dlgFrenLivianos.setVisible(true);
                    doClose(0);
                    System.out.println("VOY A REGISTAR TIMER DE TRANSACCION ");
                    System.out.println(" c8");
                    regIdAuditoria(idPrueba, revTec, true, eventoDTO, cda);

                }//end else de liviano y pesado
                else if (vehiculoVariable.getTipoVehiculo().getNombre().equalsIgnoreCase("Pesado")) {
                    DlgIntegradoPesado dlgFrenPesados = new DlgIntegradoPesado(frame, 0, idPrueba, idUsuario, idHojaPruebaLocal, vehiculoVariable.getNumeroejes(), ensenianza, aplicTrans, ipEquipo, vehiculoVariable.getTipoVehiculo().getNombre(), vehiculoVariable.getCarplate(), cam_usuario.getText());
                    dlgFrenPesados.setNumeroejes(vehiculoVariable.getNumeroejes());
                    dlgFrenPesados.setVisible(true);
                    doClose(0);
                    System.out.println("VOY A REGISTAR TIMER DE TRANSACCION ");
                    regIdAuditoria(idPrueba, revTec, true, eventoDTO, cda);

                } else if (vehiculoVariable.getTipoVehiculo().getNombre().equalsIgnoreCase("4x4")) {
                    DlgIntegrado4x4 dlgFren4x4 = new DlgIntegrado4x4(frame, 0, 0, idPrueba, idUsuario, idHojaPruebaLocal, aplicTrans, ipEquipo, vehiculoVariable.getTipoVehiculo().getNombre(), vehiculoVariable.getCarplate(), cam_usuario.getText());
                    dlgFren4x4.setVisible(true);
                    doClose(0);
                    System.out.println("VOY A REGISTAR TIMER DE TRANSACCION ");
                    regIdAuditoria(idPrueba, revTec, true, eventoDTO, cda);
                } else if (
                    vehiculoVariable.getTipoVehiculo().getNombre().equalsIgnoreCase("Motocarro")
                    || vehiculoVariable.getTipoVehiculo().getNombre().equalsIgnoreCase("TRICIMOTO")
                ) {
                    System.out.println(" PRUEBA DE FRENOS MOTOCARROS");
                    DlgIntegradoMotoCarro dlgFrenMotoCarro = new DlgIntegradoMotoCarro(frame, 0, 0, idPrueba, idUsuario, idHojaPruebaLocal, ensenianza, aplicTrans, ipEquipo, vehiculoVariable.getTipoVehiculo().getNombre(), vehiculoVariable.getCarplate(), cam_usuario.getText());
                    dlgFrenMotoCarro.setVisible(true);
                    doClose(0);
                    System.out.println("VOY A REGISTAR TIMER DE TRANSACCION ");
                    System.out.println(" c8");
                    regIdAuditoria(idPrueba, revTec, true, eventoDTO, cda);
                    // Mensajes.mensajeAdvertencia("No se encuentra modulo para motocarro");
                } else {
                    Mensajes.mensajeAdvertencia("Tipo de vehiculo invalido revise los datos");
                }
            }//end else de prueba encontrada

        } catch (Exception e) {
            System.out.println("Error en el metodo pruebaFrenos(): " + e);
        }

        doClose(0);
    }

    /**
     * @autor ELKIN B
     * @param idHojaPruebaLocal
     * @param usuarioJPA
     * @param frame
     */
    private void pruebaFoto(int idHojaPruebaLocal, Usuarios usuarioJPA, Frame frame) {

        System.out.println("-----------------------------------------------------");
        System.out.println("--------------realizando pruebaFoto-----------------");
        System.out.println("-----------------------------------------------------");
        try {
            idPrueba = controladorVerificar.extraerIdPrueba(idHojaPruebaLocal, 3, em);//3 es foro
            if (idPrueba > 1) {
                controladorVerificar.clearContextoPrueba(idPrueba, em);
            }
            if (idPrueba < 0) {
                JOptionPane.showMessageDialog(null, "Este vehiculo no tiene autorizada la prueba");
                doClose(0);
                return;
            } else {
                registarPruebaLog(placas, usuarioJPA, "Foto");
                CapturarFoto cf = new CapturarFoto(frame, true, idPrueba, idUsuario, idHojaPruebaLocal);
                doClose(0);
                //ClienteSicov.eventoPruebaSicov(idPrueba, EstadoEventosSicov.FINALIZADO, "", serialEquipo);
            }//end else vehiculo autorizada prueba

        } catch (Exception e) {
            Logger.getLogger(Frm_Placas.class.getName()).log(Level.SEVERE, null, e);
            Logger.getLogger(Frm_Placas.class.getName()).log(Level.WARNING, null, "Error en Foto");
            System.out.println("Error en el metodo : pruebaFoto()" + e.getLocalizedMessage() + e.getMessage());
        }

        doClose(0);
    }

    /**
     * @autor ELKIN B
     * @param idHojaPruebaLocal
     * @param usuarioJPA
     * @param frame
     * @param revTec
     * @param cda
     * @param eventoDTO
     */
    private void pruebaInspeccionSensorial(int idHojaPruebaLocal, Usuarios usuarioJPA, Frame frame, int revTec, Cda cda, EventoDTO eventoDTO) {
        System.out.println("-------------------------------------------------------------------");
        System.out.println("--------------realizando pruebaInspeccionSensorial-----------------");
        System.out.println("--------------------------------------------------------------------");
        try {
            idPrueba = controladorVerificar.extraerIdPrueba(idHojaPruebaLocal, 1, em);//cuatro es desviacion
            if (idPrueba > 1) {
                controladorVerificar.clearContextoPrueba(idPrueba, em);
            }
            if (idPrueba < 0) {
                JOptionPane.showMessageDialog(null, "DISCULPE; Este vehiculo no tiene autorizada la prueba");
                doClose(0);
                return;
            } else {//Existe una prueba comprobar que tipo de vehiculo es y abrir la respectiva prueba
                Vehiculos vehiculo = controladorVerificar.getTipoVehiculo(placas, em);
                boolean aplicTaximetro = vehiculo.getTipoVehiculo().getNombre().equalsIgnoreCase("Taxis_AplTaximetro");
                idVehiculoInt = vehiculo.getCar();
                boolean ensenianza = vehiculo.getEsEnsenaza() > 0;
                if (revTec == 1) {
                    if (cda.getProveedorSicov().equalsIgnoreCase("INDRA")) {
                        ClienteSicov clienteIndra = new ClienteSicov();
                        eventoDTO.setNombrePrueba("Visual");
                        eventoDTO.setIdEvento(1);

                        serialEquipo = UtilSicov.BusqSerialRegistrado("PROFUNDIMETRO", valida_tipo);
//                        serialEquipo = UtilSicov.BusqSerialRegistrado("PROFUNDIMETRO",1);

                        System.out.println("------------------------------------");
                        System.out.println("---Serial para la prueba de VISUAL: " + serialEquipo);
                        System.out.println("------------------------------------");

                        if (serialEquipo.equals("0")) {
                            Logger.getLogger(Frm_Placas.class.getName()).log(Level.WARNING, null, "NO PUEDE INICIALIZAR EVENTOS DE PLACA".concat(eventoDTO.getPlaca()).concat(" DE TIPO ").concat(eventoDTO.getNombrePrueba()));
                            JOptionPane.showMessageDialog(null, "DISCULPE; No Pude iniciar el Evento para la Pruebas Visual,debido a: (Serial Equipo NO ENCONTRADO)");
                            doClose(0);
                            return;
                        }
                        String fecha = UtilSicov.askDate();
                        fecha = fecha.substring(0, fecha.length() - 2);
                        eventoDTO.setFecha(fecha);
                        eventoDTO.setSerialEquipo(serialEquipo);

                        Logger.getLogger(Frm_Placas.class.getName()).log(Level.INFO, null, "Serial: ".concat(serialEquipo));
                        Logger.getLogger(Frm_Placas.class.getName()).log(Level.INFO, null, "fecha: ".concat(eventoDTO.getFecha()));
                        RespuestaDTO respuesta = clienteIndra.crearEvento(eventoDTO, cda);
                        System.out.println("--------------------------------------------Respuesta Evento Verificado Sensorial");
                        if (respuesta.getCodigoRespuesta().equals("0")) {
                            JOptionPane.showMessageDialog(null, "DISCULPE; No Pude iniciar el Evento para la Pruebas Visual");
                            doClose(0);
                            return;
                        }
                    }
                }
                if (vehiculo.getTipoVehiculo().getNombre().equalsIgnoreCase("Moto")) {
                    Frm_mo_SFrenosv2 validarMedidasLabrado = new Frm_mo_SFrenosv2();
                    if (validarMedidasLabrado.cargarMedidasLabradoDB(idPrueba).size() > 0) {
                        registarPruebaLog(placas, usuarioJPA, "Inspeccion Sensorial");
                        Frm_mo_IVisualv2 frm = new Frm_mo_IVisualv2(frame, true, idVehiculoInt, idHojaPruebaLocal, idPrueba, idUsuario, ensenianza, aplicTaximetro, em);
                        frm.setTitle("Inspección Sensorial para Motocicletas");
                        doClose(0);
                        frm.setVisible(true);
                    } else {
                        MensajesOut.messageWarningTime("La prueba NO tiene medidas de labrado", 5);
                        return;
                    }

                } else if (vehiculo.getTipoVehiculo().getNombre().equalsIgnoreCase("Liviano") || vehiculo.getTipoVehiculo().getNombre().equalsIgnoreCase("Pesado") || vehiculo.getTipoVehiculo().getNombre().equalsIgnoreCase("4x4") || vehiculo.getTipoVehiculo().getNombre().equalsIgnoreCase("Taxis_AplTaximetro") || vehiculo.getTipoVehiculo().getNombre().equalsIgnoreCase("Taxis")) {
                    System.out.println("---------------------------------------------- ingrese--------------------------------------");
                    Frm_Motorv2 Ventanavisual = new Frm_Motorv2();
                    System.out.println("-------cantidad:" + Ventanavisual.cargarMedidasLabradoDB(idPrueba).size());
                    if (Ventanavisual.cargarMedidasLabradoDB(idPrueba).size() > 0) {
                        registarPruebaLog(placas, usuarioJPA, "Inspeccion Sensorial");
                        Frm_InspeccionVisual frm = new Frm_InspeccionVisual(frame, true, idVehiculoInt, idHojaPruebaLocal, idPrueba, idUsuario, ensenianza, aplicTaximetro, vehiculo, em);
                        frm.setTitle("Inspección Sensorial para Vehículos Livianos y Pesados");
                        frm.setVisible(true);
                        doClose(0);
                    } else {
                        MensajesOut.messageWarningTime("La prueba NO tiene medidas de labrado", 5);
                        return;
                    }

                } else if (vehiculo.getTipoVehiculo().getNombre().equalsIgnoreCase("Motocarro")) {
                    registarPruebaLog(placas, usuarioJPA, "Inspeccion Sensorial");
                    Frm_moca_IVisualv2 dlg = new Frm_moca_IVisualv2(frame, true, idVehiculoInt, idHojaPruebaLocal, idPrueba, idUsuario, em);
                    dlg.setTitle("Inspección Sensorial para Motocarros");
                    doClose(0);
                    dlg.setVisible(true);
                } else if (vehiculo.getTipoVehiculo().getNombre().equalsIgnoreCase("Remolques")) {
                    registarPruebaLog(placas, usuarioJPA, "Inspeccion Sensorial");
                    Frm_re_IVisualv2 dlg = new Frm_re_IVisualv2(frame, true);
                    regTblAuditoriaSicov(null, 7, 1, idPrueba, revTec, "E", " ", usr);
                }
            }
            regIdAuditoria(idPrueba, revTec, true, eventoDTO, cda);

        } catch (Exception e) {
            System.out.println("  (): " + e.getLocalizedMessage() + e.getMessage());
        }
        doClose(0);
    }

    /**
     * @autor ELKIN B
     * @param idHojaPruebaLocal
     * @param usuarioJPA
     * @param frame
     * @param revTec
     * @param cda
     * @param eventoDTO
     * @param aplicTrans
     */
    private void pruebaDesviacion(int idHojaPruebaLocal, Usuarios usuarioJPA, Frame frame, int revTec, Cda cda, EventoDTO eventoDTO, int aplicTrans) {
        System.out.println("-----------------------------------------------------");
        System.out.println("--------------realizando Desviacion-----------------");
        System.out.println("-----------------------------------------------------");
        try {
            idPrueba = controladorVerificar.extraerIdPrueba(idHojaPruebaLocal, 4, em);//cuatro es desviacion
            if (idPrueba > 1) {
                controladorVerificar.clearContextoPrueba(idPrueba, em);
            }
            if (idPrueba < 0) {
                JOptionPane.showMessageDialog(null, "Este vehiculo no tiene autorizada la prueba");
                doClose(0);
                return;
            } else {//Existe una prueba comprobar que tipo de vehiculo es y abrir la respectiva prueba   
//                    PruebaDefaultDAO.escrTrans = "";
//                    System.out.println("valor inicial  " + PruebaDefaultDAO.escrTrans);
                Vehiculos vehiculo = controladorVerificar.getTipoVehiculo(placas, em);
                boolean ensenianza = vehiculo.getEsEnsenaza() > 0;
                if (revTec == 1) {
                    if (cda.getProveedorSicov().equalsIgnoreCase("INDRA")) {
                        ClienteSicov clienteIndra = new ClienteSicov();
                        eventoDTO.setNombrePrueba("FAS");
                        serialEquipo = UtilSicov.BusqSerialRegistrado("DESVIACION", valida_tipo);
                        if (serialEquipo.equals("0")) {
                            Logger.getLogger(Frm_Placas.class.getName()).log(Level.WARNING, null, "NO PUEDE INICIALIZAR EVENTOS DE PLACA".concat(eventoDTO.getPlaca()).concat(" DE TIPO ").concat(eventoDTO.getNombrePrueba()));
                            JOptionPane.showMessageDialog(null, "DISCULPE; No Pude iniciar el Evento para la Pruebas DESVIACION,debido a: (Serial Equipo NO ENCONTRADO)");
                            doClose(0);
                            return;
                        }
                        Logger.getLogger(Frm_Placas.class.getName()).log(Level.INFO, null, "Serial: ".concat(serialEquipo));
                        String fecha = UtilSicov.askDate();
                        fecha = fecha.substring(0, fecha.length() - 2);
                        eventoDTO.setSerialEquipo(serialEquipo);
                        Logger.getLogger(Frm_Placas.class.getName()).log(Level.INFO, null, "fecha: ".concat(eventoDTO.getFecha()));
                        RespuestaDTO respuesta = clienteIndra.crearEvento(eventoDTO, cda);
                        System.out.println("--------------------------------------------Respuesta Evento Verificado Desviacion");
                        if (respuesta.getCodigoRespuesta().equals("0")) {
                            JOptionPane.showMessageDialog(null, "DISCULPE; No Pude iniciar el Evento para la Pruebas FAS");
                            doClose(0);
                            return;
                        }
                    }
                }

                if (vehiculo.getTipoVehiculo().getNombre().equalsIgnoreCase("Liviano") || vehiculo.getTipoVehiculo().getNombre().equalsIgnoreCase("Taxis_AplTaximetro") || vehiculo.getTipoVehiculo().getNombre().equalsIgnoreCase("Taxis")) {
                    registarPruebaLog(placas, usuarioJPA, "Desviacion");
                    new DlgIntegradoLiviano(frame, idPrueba, 0, 0, idUsuario, idHojaPruebaLocal, ensenianza, aplicTrans, ipEquipo, vehiculo.getTipoVehiculo().getNombre(), vehiculo.getCarplate(), cam_usuario.getText()).setVisible(true);
                    regIdAuditoria(idPrueba, revTec, true, eventoDTO, cda);

                } else if (vehiculo.getTipoVehiculo().getNombre().equalsIgnoreCase("Pesado")) {
                    registarPruebaLog(placas, usuarioJPA, "Desviacion");
                    new DlgIntegradoPesado(frame, idPrueba, 0, idUsuario, idHojaPruebaLocal, vehiculo.getNumeroejes(), ensenianza, aplicTrans, ipEquipo, vehiculo.getTipoVehiculo().getNombre(), vehiculo.getCarplate(), cam_usuario.getText()).setVisible(true);
                    regIdAuditoria(idPrueba, revTec, true, eventoDTO, cda);
                } else if (vehiculo.getTipoVehiculo().getNombre().equalsIgnoreCase("4x4")) {
                    registarPruebaLog(placas, usuarioJPA, "Desviacion");
                    new DlgIntegrado4x4(frame, idPrueba, 0, 0, idUsuario, idHojaPruebaLocal, aplicTrans, ipEquipo, vehiculo.getTipoVehiculo().getNombre(), vehiculo.getCarplate(), cam_usuario.getText()).setVisible(true);
                    regIdAuditoria(idPrueba, revTec, true, eventoDTO, cda);
                } else {
                    Mensajes.mensajeAdvertencia("Tipo de vehiculo " + vehiculo.getTipoVehiculo() + " no realiza desviación");
                }
            }

        } catch (Exception e) {
            System.err.println("Error en el metodo : " + e.getLocalizedMessage() + e.getMessage());
        }

        doClose(0);

    }

    /**
     *
     * @autor ELKIN B
     * @param idHojaPruebaLocal
     * @param usuarioJPA
     * @param tipo
     * @param frame
     * @param revTec
     * @param cda
     * @param eventoDTO
     * @param aplicTrans
     */
    private void pruebaGases(int idHojaPruebaLocal, Usuarios usuarioJPA, Vehiculos tipo, Frame frame, int revTec, Cda cda, EventoDTO eventoDTO, int aplicTrans) {
        System.out.println("-----------------------------------------------------");
        System.out.println("--------------realizando prueba Gases-----------------");
        System.out.println("-----------------------------------------------------");

        try {
            idPrueba = controladorVerificar.extraerIdPrueba(idHojaPruebaLocal, 8, em);//cambiar por constante
            if (idPrueba > 1) {
                controladorVerificar.clearContextoPrueba(idPrueba, em);
            }

            if (idPrueba < 0) {
                JOptionPane.showMessageDialog(null, "Este vehiculo no tiene autorizada la prueba");
                doClose(0);
                return;
            }

            if ( tipo.getTiposGasolina().getFueltype() == 5
                    || tipo.getTiposGasolina().getFueltype() == 6) {
                JOptionPane.showMessageDialog(null, " Este Vehiculo esta Exento de Pruebas de Emisiones Contaminantes ..¡"
                        + " \n Razon: Tipo Combustible:  " + tipo.getTiposGasolina().getNombregasolina());
                doClose(0);
                return;
            }

            registarPruebaLog(placas, usuarioJPA, "Gases");
            String serialBanco = "";
            Vehiculos v = controladorVerificar.getTipoVehiculo(placas, em);

            if (!v.getTiposGasolina().getNombregasolina().equalsIgnoreCase("Diesel")) {

                boolean flag = combustibleGasolina(idHojaPruebaLocal, serialBanco, revTec, cda, eventoDTO, v, frame);
                if (!flag) {
                    return;
                }
            }

            if (    
                v.getTipoVehiculo().getNombre().equalsIgnoreCase("Moto") 
                || v.getTipoVehiculo().getNombre().equalsIgnoreCase("Motocarro")
                || v.getTipoVehiculo().getNombre().equalsIgnoreCase("CICLOMOTOR")
                || v.getTipoVehiculo().getNombre().equalsIgnoreCase("TRICIMOTO")
            ) {
                if (v.getTiposGasolina().getNombregasolina().equalsIgnoreCase("Diesel")) {
                    boolean flag = combustibleDieselMotoMotocarro(idHojaPruebaLocal, revTec, cda, eventoDTO, frame, aplicTrans);
                    if (!flag) {
                        return;
                    }
                } else {
                    WorkerCiclosMoto.aplicTrans = aplicTrans;
                    WorkerCiclosMoto.ipEquipo = ipEquipo;
                    JDialogMotosGases dlgMotos = new JDialogMotosGases(frame, true, idPrueba, idUsuario, idHojaPruebaLocal, this, placas, termoHigrometroArtisan);
                    dlgMotos.setVisible(true);
                }
                System.out.println(" voy apartar idAuditoria para motos ");
                regIdAuditoria(idPrueba, revTec, true, eventoDTO, cda);

            } else if (v.getTipoVehiculo().getNombre().equalsIgnoreCase("Liviano")
                    || v.getTipoVehiculo().getNombre().equalsIgnoreCase("Pesado")
                    || v.getTipoVehiculo().getNombre().equalsIgnoreCase("4x4")
                    || v.getTipoVehiculo().getNombre().equalsIgnoreCase("Taxis_AplTaximetro")
                    || v.getTipoVehiculo().getNombre().equalsIgnoreCase("Taxis")) {
                if (v.getTiposGasolina().getNombregasolina().equalsIgnoreCase("Diesel") || v.getTiposGasolina().getNombregasolina().equalsIgnoreCase("GAS NATURAL-DIESEL")) {
                    System.out.println("entro al segundo if de diesel");
                    boolean flag = combustibleDieselOtros(idHojaPruebaLocal, revTec, cda, eventoDTO, frame, aplicTrans);
                    if (!flag) {
                        return;
                    }
                } else {
                    try {
                        System.out.println("entro al try para crear el dialogovehiculos");
                        dlgVehiculo = new DialogoVehiculo();
                        JDialog d = new JDialog((JDialog) SwingUtilities.getWindowAncestor(frame), true);
                        d.setTitle("Parametros Tipo Gasolina..!");
                        dlgVehiculo.fillData(placas);
                        d.add(dlgVehiculo);
                        d.setSize(278, 305);
                        d.getRootPane().setDefaultButton(dlgVehiculo.getButtonContinuar());
                        d.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
                        d.setLocationRelativeTo(frame);
                        d.setVisible(true);
                        WorkerCruceroRalenti.ipEquipo = ipEquipo;
                        WorkerCruceroRalenti.aplicTrans = aplicTrans;
                        JDialogPruebaGasolina dlgPruebaGasolina = new JDialogPruebaGasolina(frame, true, idPrueba, idUsuario, idHojaPruebaLocal, this, placas, termoHigrometroArtisan);
                        dlgPruebaGasolina.setVisible(true);
                        regIdAuditoria(idPrueba, revTec, true, eventoDTO, cda);
                    } catch (SQLException | ClassNotFoundException | IOException ex) {
                        Mensajes.mostrarExcepcion(ex);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error en el metodo pruebaGases(): " + e.getLocalizedMessage() + e.getMessage());
        }

        doClose(0);
    }

    /**
     * @autor ELKIN B
     * @param v
     * @param frame
     */
    private void motoMotoCarros(Vehiculos v, Frame frame) {
        System.out.println("----------------------------------------------------");
        System.out.println("----------  vehiculosMotoMotoCarros  ---------------");
        System.out.println("----------------------------------------------------");
        try {
            dlgMotos = new DialogoMotos();
            JDialog d = new JDialog((JDialog) SwingUtilities.getWindowAncestor(frame), true);
            if (v.getTipoVehiculo().getNombre().equalsIgnoreCase("Moto")) {
                d.setTitle("Parametros Motos.");
            } else {
                d.setTitle("Parametros MotoCarro.");
            }
            dlgMotos.fillData(placas);
            d.add(dlgMotos);
            d.setSize(278, 600);
            d.getRootPane().setDefaultButton(dlgMotos.getButtonContinuar());
            d.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            d.setLocationRelativeTo(frame);
            d.setVisible(true);

        } catch (Exception e) {
            System.out.println("Error en el metodo : ()" + e.getMessage());
            System.out.println("Error en el metodo : ()" + e.getStackTrace());
        }

    }

    /**
     * @autor ELKIN B
     *
     * @param cda
     * @param eventoDTO
     * @param v
     * @return
     */
    private boolean envioEventoIndraGases(Cda cda, EventoDTO eventoDTO, Vehiculos v) {
        System.out.println("----------------------------------------------------");
        System.out.println("----------    envioEventoIndraGases          -----");
        System.out.println("----------------------------------------------------");
        try {
            ClienteSicov clienteIndra = new ClienteSicov();
            if (v.getTipoVehiculo().getCartype() == 4) {
                int nTiempo = dlgMotos.getNumeroTiempos();
                if (nTiempo == 2) {
                    eventoDTO.setNombrePrueba("Gases");
                    serialEquipo = UtilSicov.BusqSerialRegistrado("Gases2T", valida_tipo);
                } else {
                    eventoDTO.setNombrePrueba("Gases");
                    serialEquipo = UtilSicov.BusqSerialRegistrado("Gases", valida_tipo);
                }
            } else {
                serialEquipo = UtilSicov.BusqSerialRegistrado("Gases", valida_tipo);
                eventoDTO.setNombrePrueba("Gases");
            }

            if (serialEquipo.equals("0")) {
                Logger.getLogger(Frm_Placas.class.getName()).log(Level.WARNING, null, "NO PUEDE INICIALIZAR EVENTOS DE PLACA".concat(eventoDTO.getPlaca()).concat(" DE TIPO ").concat(eventoDTO.getNombrePrueba()));
                JOptionPane.showMessageDialog(null, "DISCULPE; No Pude iniciar el Evento para la Pruebas GASES,debido a: (Serial Equipo NO ENCONTRADO)");
                doClose(0);
                return false;
            }
            Logger.getLogger(Frm_Placas.class.getName()).log(Level.INFO, null, "Serial: ".concat(serialEquipo));
            String fecha = UtilSicov.askDate();
            fecha = fecha.substring(0, fecha.length() - 2);
            eventoDTO.setSerialEquipo(serialEquipo.replace(";", "_"));
            eventoDTO.setFecha(fecha);
            RespuestaDTO respuesta = clienteIndra.crearEvento(eventoDTO, cda);
            System.out.println("--------------------------------------------Respuesta Evento Verificado IndraGases");
            Logger.getLogger(Frm_Placas.class.getName()).log(Level.INFO, null, "Serial: ".concat(serialEquipo));
            Logger.getLogger(Frm_Placas.class.getName()).log(Level.INFO, null, "fecha: ".concat(eventoDTO.getFecha()));

            if (respuesta.getCodigoRespuesta().equals("0")) {
                JOptionPane.showMessageDialog(null, "DISCULPE; No Pude iniciar el Evento para la Pruebas Gases");
                doClose(0);
                return false;
            }
        } catch (Exception e) {
            System.out.println("Error en el metodo :envioEventoIndraGases()" + e.getMessage());
            System.out.println("Error en el metodo :envioEventoIndraGases()" + e.getStackTrace());
            return false;
        }

        return true;
    }

    /**
     *
     * @autor ELKIN B
     *
     * @param idHojaPruebaLocal
     * @param serialBanco
     * @param revTec
     * @param cda
     * @param eventoDTO
     * @param vehiculoVariable
     * @param frame
     * @return
     */
    private boolean combustibleGasolina(int idHojaPruebaLocal, String serialBanco, int revTec, Cda cda, EventoDTO eventoDTO, Vehiculos vehiculoVariable, Frame frame) {
        System.out.println("--------------------------------------------------");
        System.out.println("----------  Prueba Combustible Gasolina-----------");
        System.out.println("--------------------------------------------------");
        //JFM COMENTARIO GASES 
        try {
            // VALIDADOR DE RULES PARA VEHICULOS A GASOLINA                   
            if (
                vehiculoVariable.getTipoVehiculo().getNombre().equalsIgnoreCase("Moto") 
                || vehiculoVariable.getTipoVehiculo().getNombre().equalsIgnoreCase("Motocarro")
                || vehiculoVariable.getTipoVehiculo().getNombre().equalsIgnoreCase("CICLOMOTOR")
                || vehiculoVariable.getTipoVehiculo().getNombre().equalsIgnoreCase("TRICIMOTO")
            ) {
                motoMotoCarros(vehiculoVariable, frame);
            }
            System.out.println("valor de rev tec = " + revTec);
            if (revTec == 1) {
                System.out.println("entro a prueba de gases de indra");
                //si es indra realiza envio de evento
                if (cda.getProveedorSicov().equalsIgnoreCase("INDRA")) {
                    if (!envioEventoIndraGases(cda, eventoDTO, vehiculoVariable)) {
                        return false;
                    }//si si de una
                }
            }

            serialBanco = UtilFugas.obtenerSerialBanco();
            System.out.println("SERIAL  BANCO SART  1.7.3 " + serialBanco);
            RegVefCalibraciones r = new RegVefCalibraciones();

            if (
                vehiculoVariable.getTipoVehiculo().getNombre().equalsIgnoreCase("Moto") 
                || vehiculoVariable.getTipoVehiculo().getNombre().equalsIgnoreCase("Motocarro")
                || vehiculoVariable.getTipoVehiculo().getNombre().equalsIgnoreCase("CICLOMOTOR")
                || vehiculoVariable.getTipoVehiculo().getNombre().equalsIgnoreCase("TRICIMOTO")
            ) {
                System.out.println("--------------------------" + vehiculoVariable);
                boolean prueba = r.validAnalizador(serialBanco, dlgMotos.getNumeroTiempos());
                if (prueba == false) {
                    JOptionPane.showMessageDialog(null, "Disculpe. El banco con serial "+serialBanco+" no corresponde con los tiempos del motor del Vehiculo");
                    doClose(0);
                    return false;
                }
            }

            if (vehiculoVariable.getTipoVehiculo().getNombre().equalsIgnoreCase("Liviano")) {
                try {
                    (new EquipoController()).findIdEquipoBySerial(serialBanco);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Disculpe. El banco con serial: " + serialBanco + " no existe");
                    doClose(0);
                    return false;
                }
            }

            boolean necesitaFugas = r.necesitaFugas(serialBanco);
            System.out.println("------------");
            boolean necesitaCalibracion = r.necesitaCalibracionDosPuntos(serialBanco);

            System.out.println(" SÇBANCO CALIBRACION   " + necesitaCalibracion);
            System.out.println(" SÇBANCO FUGAS  " + necesitaFugas);

            if (necesitaFugas == true || necesitaCalibracion == true) {
                JOptionPane.showMessageDialog(null, "Por favor verifique que el equipo con serial " + serialBanco + " tenga prueba de Fugas y calibracion dos puntos");
                doClose(0);
                return false;
            } else {

            }

            idPrueba = controladorVerificar.extraerIdPrueba(idHojaPruebaLocal, 8, em);//cambiar por constante
            if (idPrueba > 1) {
                controladorVerificar.clearContextoPrueba(idPrueba, em);
            }
            if (idPrueba == -1) {
                JOptionPane.showMessageDialog(null, "Este vehiculo no tiene autorizada la prueba Gases ");
                doClose(0);
                return false;
            }
            if (idPrueba == -2) {
                JOptionPane.showMessageDialog(null, "Este vehiculo ha Superado el Numero de Pruebas Gases permitidas  ");
                doClose(0);
                return false;
            }

        } catch (Exception ex) {
            int e = 0;
            System.err.println("Error en el metodo : combustibleGasolina()" + ex.getLocalizedMessage() + ex.getMessage());
            Logger.getLogger(Frm_Placas.class.getName()).log(Level.SEVERE, null, ex);
            doClose(0);
            return false;
        }
        return true;
    }

    /**
     * @autor ELKIN B
     *
     * @param idHojaPruebaLocal
     * @param revTec
     * @param cda
     * @param eventoDTO
     * @param frame
     * @param aplicTrans
     * @return
     */
    private boolean combustibleDieselMotoMotocarro(int idHojaPruebaLocal, int revTec, Cda cda, EventoDTO eventoDTO, Frame frame, int aplicTrans) {
        System.out.println("--------------------------------------------------");
        System.out.println("------Combustible Diesel Moto y Motocarro---------");
        System.out.println("--------------------------------------------------");

        serialEquipo = UtilSicov.BusqSerialRegistrado("OPACIMETRO", valida_tipo);
        idPrueba = controladorVerificar.extraerIdPrueba(idHojaPruebaLocal, 8, em);//cambiar por constante
        try {
            System.out.println("entro al try");
            if (revTec == 1) {
                System.out.println("entro a if revTec==1");
                if (cda.getProveedorSicov().equalsIgnoreCase("INDRA")) {
                    System.out.println(" entro a if de indra en Combustible Diesel Moto y Motocarro ");
                    ClienteSicov clienteIndra = new ClienteSicov();
                    eventoDTO.setNombrePrueba("Gases");
                    if (serialEquipo.equals("0")) {
                        Logger.getLogger(Frm_Placas.class.getName()).log(Level.WARNING, null, "NO PUEDE INICIALIZAR EVENTOS DE PLACA".concat(eventoDTO.getPlaca()).concat(" DE TIPO ").concat(eventoDTO.getNombrePrueba()));
                        JOptionPane.showMessageDialog(null, "DISCULPE; No Pude iniciar el Evento para la Pruebas OPACIMETRO ,debido a: (Serial Equipo NO ENCONTRADO)");
                        doClose(0);
                        return false;
                    }
                    Logger.getLogger(Frm_Placas.class.getName()).log(Level.INFO, null, "Serial: ".concat(serialEquipo));
                    String fecha = UtilSicov.askDate();
                    fecha = fecha.substring(0, fecha.length() - 2);
                    eventoDTO.setSerialEquipo(serialEquipo.replace(";", "_"));
                    eventoDTO.setFecha(fecha);
                    RespuestaDTO respuesta = clienteIndra.crearEvento(eventoDTO, cda);
                    System.out.println("--------------------------------------------Respuesta Evento Verificado Gases");
                    Logger.getLogger(Frm_Placas.class.getName()).log(Level.INFO, null, "Serial: ".concat(serialEquipo));
                    Logger.getLogger(Frm_Placas.class.getName()).log(Level.INFO, null, "fecha: ".concat(eventoDTO.getFecha()));

                    if (respuesta.getCodigoRespuesta().equals("0")) {
                        JOptionPane.showMessageDialog(null, "DISCULPE; No Pude iniciar el Evento para la Pruebas Gases");
                        doClose(0);
                        return false;
                    }
                }
            }
            System.out.println("despues del if revtec");
            Long serialOpcimetro = UtilFugas.obtenerSerialOpacimetro();
            System.out.println("SERIAL DEL OPACIMETRO  " + serialOpcimetro);
            RegVefCalibraciones regvef = new RegVefCalibraciones();
            boolean necesitaLinealidad = regvef.necesitaLinealidad(serialOpcimetro);
            if (necesitaLinealidad == true) {
                JOptionPane.showMessageDialog(null, "DISCULPE; No Pude iniciar. El opacimetro con serial: " + serialOpcimetro + " necesita linealidad");
                return false;
            }
            WorkerCiclosDiesel.aplicTrans = aplicTrans;
            WorkerCiclosDiesel.ipEquipo = ipEquipo;
            WorkerCiclosDiesel.placas = cam_placas.getText();
            WorkerCiclosDiesel.serialEquipo = serialEquipo;

            System.out.println("placas " + cam_placas.getText());
            JDialogDiesel dlgDialogDiesel = new JDialogDiesel(frame, true, idPrueba, idUsuario, idHojaPruebaLocal);
            dlgDialogDiesel.setVisible(true);
            boolean fall = false;
            regIdAuditoria(idPrueba, revTec, true, eventoDTO, cda);
            WorkerCiclosDiesel.ipEquipo = "";
        } catch (SQLException | ClassNotFoundException | IOException ex) {
            Mensajes.mostrarExcepcion(ex);
            System.err.println("Error en el metodo : combustibleDieselMotoMotocarro()" + ex.getLocalizedMessage() + ex.getMessage());
        } catch (Exception ex) {
            Logger.getLogger(Frm_Placas.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Error en el metodo : combustibleDieselMotoMotocarro()" + ex.getLocalizedMessage() + ex.getMessage());
        }
        return true;
    }

    /**
     *
     * @param idHojaPruebaLocal
     * @param revTec
     * @param cda
     * @param eventoDTO
     * @param frame
     * @param aplicTrans
     * @return
     */
    private boolean combustibleDieselOtros(int idHojaPruebaLocal, int revTec, Cda cda, EventoDTO eventoDTO, Frame frame, int aplicTrans) {
        System.out.println("--------------------------------------------------");
        System.out.println("----------combustibleDieselOtros-------------------");
        System.out.println("--------------------------------------------------");

        idPrueba = controladorVerificar.extraerIdPrueba(idHojaPruebaLocal, 8, em);//cambiar por constante
        serialEquipo = UtilSicov.BusqSerialRegistrado("OPACIMETRO", valida_tipo);
        try {
            if (revTec == 1) {
                if (cda.getProveedorSicov().equalsIgnoreCase("INDRA")) {
                    ClienteSicov clienteIndra = new ClienteSicov();
                    eventoDTO.setNombrePrueba("Gases");
                    if (serialEquipo.equals("0")) {
                        Logger.getLogger(Frm_Placas.class.getName()).log(Level.WARNING, null, "NO PUEDE INICIALIZAR EVENTOS DE PLACA".concat(eventoDTO.getPlaca()).concat(" DE TIPO ").concat(eventoDTO.getNombrePrueba()));
                        JOptionPane.showMessageDialog(null, "DISCULPE; No Pude iniciar el Evento para la Pruebas OPACIMETRO ,debido a: (Serial Equipo NO ENCONTRADO)");
                        doClose(0);
                        return false;
                    }
                    Logger.getLogger(Frm_Placas.class.getName()).log(Level.INFO, null, "Serial: ".concat(serialEquipo));
                    String fecha = UtilSicov.askDate();
                    fecha = fecha.substring(0, fecha.length() - 2);
                    eventoDTO.setSerialEquipo(serialEquipo.replace(";", "_"));
                    eventoDTO.setFecha(fecha);
                    RespuestaDTO respuesta = clienteIndra.crearEvento(eventoDTO, cda);
                    System.out.println("--------------------------------------------Respuesta Evento Verificado Diesel Otros");
                    Logger.getLogger(Frm_Placas.class.getName()).log(Level.INFO, null, "Serial: ".concat(serialEquipo));
                    Logger.getLogger(Frm_Placas.class.getName()).log(Level.INFO, null, "fecha: ".concat(eventoDTO.getFecha()));

                    if (respuesta.getCodigoRespuesta().equals("0")) {
                        JOptionPane.showMessageDialog(null, "DISCULPE; No Pude iniciar el Evento para la Pruebas Gases");
                        doClose(0);
                        return false;
                    }
                }
            }
            Long serialOpcimetro = UtilFugas.obtenerSerialOpacimetro();
            System.out.println("SERIAL DEL OPACIMETRO  " + serialOpcimetro);
            RegVefCalibraciones regvef = new RegVefCalibraciones();
            boolean necesitaLinealidad = regvef.necesitaLinealidad(serialOpcimetro);////////////////////////////////////////////////////////////////////////////
            System.out.println("valor de linealidad :" + necesitaLinealidad);

            if (necesitaLinealidad == true) {
                return false;
            }
            WorkerCiclosDiesel.aplicTrans = aplicTrans;
            WorkerCiclosDiesel.ipEquipo = ipEquipo;
            WorkerCiclosDiesel.placas = cam_placas.getText();
            System.out.println("placas " + cam_placas.getText());
            WorkerCiclosDiesel.serialEquipo = serialEquipo;

            JDialogDiesel dlgDialogDiesel = new JDialogDiesel(frame, true, idPrueba, idUsuario, idHojaPruebaLocal);
            dlgDialogDiesel.setVisible(true);
            boolean fall = false;
            regIdAuditoria(idPrueba, revTec, true, eventoDTO, cda);
            WorkerCiclosDiesel.ipEquipo = "";

        } catch (SQLException | ClassNotFoundException | IOException ex) {
            Mensajes.mostrarExcepcion(ex);
            System.err.println("u   1    " + ex.getLocalizedMessage() + ex.getMessage());
        } catch (Exception ex) {
            System.err.println("Error en el metodo : combustibleDieselOtros()" + ex.getLocalizedMessage() + ex.getMessage());
            Logger.getLogger(Frm_Placas.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    private void btn_verificarMouseClicked(java.awt.event.MouseEvent evt) {

    }

    public void establecerNombreBoton(String nombreBoton) {
        this.nombreBoton = nombreBoton;
    }

    //Método para cerrar la ventana
    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }
    //Método para extraer el id de la prueba
    // Variables declaration - do not modify                     
    private javax.swing.JButton btn_verificar;
    private javax.swing.JPasswordField cam_contraseña;
    private javax.swing.JTextField cam_placas;
    private javax.swing.JTextField cam_usuario;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    // End of variables declaration                   
    private int returnStatus = 0;

    /**
     * Metodo que ayudará a establecer los diferentes booleanos de todas las
     * pruebas, dependiendo si esta autorizada una prueba o no.
     *
     * @param i
     * @param prueba
     * @return
     */
    private boolean comprobar(int i, boolean prueba) {
        if (i > -1) {
            prueba = true;
        }
        return prueba;
    }

    public void cerrar() {
        Window w = SwingUtilities.getWindowAncestor(this);
        w.dispose();
    }

    /**
     *
     * @param placas
     * @param usuarioJPA
     * @param nombreTipoPrueba
     */
    private static void registarPruebaLog(String placas, Usuarios usuarioJPA, String nombreTipoPrueba) {
        StringBuilder sb = new StringBuilder();
        sb.append("Usuario ").append(usuarioJPA.getNombreusuario()).append(" realiza");
        sb.append(" prueba de :").append(nombreTipoPrueba);
        sb.append("para vehiculo de placas: ").append(placas);
        log.info(sb);
    }

}
