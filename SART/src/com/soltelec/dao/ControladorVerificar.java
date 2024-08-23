/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.soltelec.dao;

import com.soltelec.ci2.ClienteCi2;
import com.soltelec.ci2.Pin;
import com.soltelec.ci2.RespuestaDTO;
import com.soltelec.ci2.Mensajes;
 

import com.soltelec.dao.conexion.PersistenceController;
import com.soltelec.indra.ClienteIndra;
import com.soltelec.model.Cda;
import com.soltelec.model.HojaPruebas;
import com.soltelec.model.Pruebas;
import com.soltelec.model.Reinspeccion;
import com.soltelec.model.TipoPrueba;
import com.soltelec.model.Usuarios;
import com.soltelec.model.Vehiculos;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.swing.JOptionPane;
import org.soltelec.pruebasgases.motocicletas.CallableInicioMotos;
import org.soltelec.pruebasgases.motocicletas.CallablePruebaMotos;
import  org.soltelec.util.RegistrarMedidas;
import org.soltelec.util.RegistrarMedidas;

/**
 * Clase para optimizar la consulta de la ultima hoja de prueba no finalizada
 * poner todas las consultas aqui de manera que se use solamente un entity
 * manager
 *
 *
 * @author Gerencia Desarrollo de Soluciones Tecnologicas
 */
public class ControladorVerificar {

    public static HojaPruebas hp;

    public EntityManager getEntityManager() {
        return PersistenceController.getEntityManager();
    }

    public void clearContextoPrueba(Integer idPrueba, EntityManager em) {
        Pruebas pr = em.find(Pruebas.class, idPrueba);
        if (pr.getTipoPrueba().getTesttype() != 1) {
            if (pr.getObservaciones() != null) {
                if (pr.getObservaciones().length() > 0) {
                    if (em.getTransaction().isActive() == false) {
                        em.getTransaction().begin();
                    }
                    pr.setObservaciones(" ");
                    pr.setComentarioaborto(" ");
                    pr.setAutorizada("N");
                    em.merge(pr);
                    em.getTransaction().commit();
                }
            }
        }
    }

    public Usuarios getUsuarioByNick(String nick, EntityManager em) {
        Query q = em.createNamedQuery("Usuarios.findByNickusuario");
        q.setParameter("nickusuario", nick);
        Usuarios u = null;
        try {
            u = (Usuarios) q.getSingleResult();
        } catch (NoResultException nre) {
            u = null;
        }
        return u;
    }//end of method

    public int hojaPruebaMaxNoFin(String laPlaca, EntityManager em) {
        em.clear();
        em.getTransaction().begin();
        em.flush();
        em.getTransaction().commit();
        Query q = em.createQuery("SELECT MAX(hp.testsheet) FROM HojaPruebas hp WHERE hp.finalizada = 'N' AND hp.vehiculos.carplate = :placa ");
        q.setParameter("placa", laPlaca);
        int idHoja = -1;
        try {
            idHoja = ((Integer) q.getSingleResult());
        } catch (NoResultException | NullPointerException nre) {
            idHoja = -1;
        }
        return idHoja;
    }//end of method

    public int extraerIdPrueba(int idHojaPrueba, int tipoPrueba, EntityManager em)  {       
        int idPrueba=-1;       
        try {
            idPrueba = getPruebaNoFinalizadaTipo(idHojaPrueba, tipoPrueba, em);
        } catch (Exception ex) {
            Logger.getLogger(ControladorVerificar.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("id de la prueba cuando se busca suspension: "+ idPrueba  );
        return idPrueba;
    }

    public Vehiculos getTipoVehiculo(String placa, EntityManager em) {
        Query q = em.createNamedQuery("Vehiculos.findByCarplate");
        q.setParameter("carplate", placa);
        Vehiculos v = (Vehiculos) q.getSingleResult();
        em.getTransaction().begin();
        em.flush();
        em.refresh(v);
        em.getTransaction().commit();
        return v;
    }

    public int esRevTecn(Integer idHojaPrueba, EntityManager em) {
        Query q = em.createNamedQuery("HojaPruebas.findByTestsheet");
        q.setParameter("testsheet", idHojaPrueba);
        List<HojaPruebas> lstHp = q.getResultList();
        Cda ctxCDA;
        ctxCDA = em.find(Cda.class, 1);
        if (lstHp.size() > 0) {
            HojaPruebas hp = lstHp.get(0);
            if (hp.getPreventiva().equalsIgnoreCase("Y")) {
                return 2;
            } else {
                System.out.println("VALIDANDO DI ES INDRA");
                if (ctxCDA.getProveedorSicov().equalsIgnoreCase("INDRA")) {
                    ClienteIndra indra = new ClienteIndra();
                    indra.setUrlSicov(ctxCDA.getUrlServicioSicov());
                    System.out.println("voy a entrar a GETINFOPIN");
                    com.soltelec.indra.RespuestaDTO pinRespuesta = indra.getInfoPin(hp.getVehiculos().getCarplate(), String.valueOf(ctxCDA.getIdRunt()));
                    System.out.println("SALI DE GETINFOPIN");
                    return Integer.parseInt(pinRespuesta.getCodigoRespuesta());
                }
                System.out.println("NO ES INDRA");
                
                if (hp.getEstadoSICOV().equalsIgnoreCase("REGISTRADA")) {                   
                    
                    RespuestaDTO respuestaDTO= null;
                    try {
                        Pin pin = new Pin();                       
                        pin.setClave(ctxCDA.getPasswordSicov());
                        pin.setP_placa(hp.getVehiculos().getCarplate());                        
                        pin.setUsuario(ctxCDA.getUsuarioSicov());              
                        ClienteCi2 clienteCi2 = new ClienteCi2(ctxCDA.getUrlServicioSicov());                        
                        if (hp.getReinspeccionList().size() > 0) {
                             pin.setP_tipo_rtm("2");
                             pin.setP_pin(hp.getPin());
                            respuestaDTO = clienteCi2.utilizarPin(pin);                           
                        } else {
                            pin.setP_tipo_rtm("1");
                            respuestaDTO = clienteCi2.consultarPinPlaca(pin);                            
                        }                        
                                               
                        if (respuestaDTO == null) {
                            Mensajes.mensajeAdvertencia("Disculpe, No he Tenido COMUNICACION con el Servidor CI2 en este momento ..! \n Intente dentro de un Minuto si el problema persiste COMUNIQUESE con la Mesa de Ayuda");
                            Mensajes.mensajeAdvertencia("Por Favor, se RECOMIENDA NO continuar con PRUEBAS de Revision TecnoMecanica..!");
                            return -1;
                        }
                        
                        if (pin.getP_tipo_rtm().equalsIgnoreCase("1")) {
                            if (respuestaDTO.getCodigoRespuesta().equalsIgnoreCase("2006")) {
                                String temp = respuestaDTO.getMensajeRespuesta().split("@")[1];
                                respuestaDTO.setMensajeRespuesta(temp.replace("|TD", ""));
                                String ctxPing = respuestaDTO.getMensajeRespuesta();
                                System.out.println(ctxPing);                               
                                pin.setP_pin(ctxPing);
                                respuestaDTO = clienteCi2.utilizarPin(pin);
                                if (respuestaDTO.getCodigoRespuesta().equals("0000")) {
                                    em.getTransaction().begin();
                                    HojaPruebas ctxHP = em.find(HojaPruebas.class, hp.getTestsheet());
                                    ctxHP.setPin(ctxPing);
                                    ctxHP.setEstadoSICOV("Iniciado");
                                    em.merge(ctxHP);
                                    em.getTransaction().commit();
                                    Mensajes.mensajeCorrecto("Pin Iniciado con EXITO ..¡ ");
                                    return 1;
                                } else {
                                    Mensajes.mensajeAdvertencia("Disculpe, No he podido ASOCIAR el Pin debido a :" + respuestaDTO.getMensajeRespuesta());
                                    return -1;
                                }                               
                            } else {
                                Mensajes.mensajeAdvertencia("Disculpe, No he podido ASOCIAR el Pin debido a :" + respuestaDTO.getMensajeRespuesta());
                                return -1;
                            }
                        }
                        if (pin.getP_tipo_rtm().equalsIgnoreCase("2")) {                           
                            if (respuestaDTO.getCodigoRespuesta().equals("0000")) { //ok
                                 em.getTransaction().begin();
                                HojaPruebas ctxHP = em.find(HojaPruebas.class, hp.getTestsheet());                               
                                ctxHP.setEstadoSICOV("Iniciado");
                                em.merge(ctxHP);
                                em.getTransaction().commit();
                                Mensajes.mensajeCorrecto(" Inicializado el PIN para Reinspeccion con EXITO ..¡ ");
                                return 1;                                
                            } else {
                                Mensajes.mensajeAdvertencia("Disculpe, No he podido ASOCIAR el Pin Reinspeccion debido a :" + respuestaDTO.getMensajeRespuesta());
                                return -1;
                            }
                        }
                    } catch (Throwable ne) {
                        Mensajes.mensajeAdvertencia("error " + ne.getMessage());
                    }

                }
                return 1;
            }           
        }else{
             return 0;
        }
    }

    public int aplicTransito(Integer idHojaPrueba, EntityManager em) 
    {
        Query qDA = em.createNativeQuery("SELECT cont_test,cont_cda from cda ");
        Object[] arrayObjetos = (Object[]) qDA.getResultList().iterator().next();
        return (Integer) arrayObjetos[0];
    }
 
    public String exisTestLab(int idPrueba, EntityManager em) {
        try {
            String comLabrado = null;
            Query q = em.createQuery("SELECT p.observaciones FROM Pruebas p WHERE p.idPruebas = ?1");
            q.setParameter(1, idPrueba);
            comLabrado = ((String) q.getSingleResult());
            if (comLabrado.indexOf("$") > 0 && comLabrado.indexOf("&") > 0) {
                return comLabrado;
            } else {
                return "0";
            }
        } catch (NoResultException | NullPointerException ne) {
            return "0";
        }
    }//end of method getPruebaNoFinalizada

    public String exisObs(int idPrueba, EntityManager em) {
        try {
            String comObs = null;
            Query q = em.createQuery("SELECT p.observaciones FROM Pruebas p WHERE p.idPruebas = ?1");
            q.setParameter(1, idPrueba);
            comObs = ((String) q.getSingleResult());
            String[] obs = comObs.split("obs");
            if (obs.length > 1) {
                return obs[1];
            } else {
                return " ";
            }
        } catch (NoResultException | NullPointerException ne) {
            return "";
        }
    }//end of method getPruebaNoFinalizada

    public String exisObsCrud(int idPrueba, EntityManager em) {
        try {
            String comObs = null;
            Query q = em.createQuery("SELECT p.observaciones FROM Pruebas p WHERE p.idPruebas = ?1");
            q.setParameter(1, idPrueba);
            comObs = ((String) q.getSingleResult());
            if (comObs != null) {
                return comObs;
            } else {
                return " ";
            }
        } catch (NoResultException | NullPointerException ne) {
            return "0";
        }
    }//end of method getPruebaNoFinalizada

    public int getPruebaNoFinalizadaTipo(int idHojaPruebas, int tipoPrueba, EntityManager em) throws Exception
    {
        System.out.println("----------------------------------------------------");
        System.out.println("-----------getPruebaNoFinalizadaTipo----------------");
        System.out.println("----------------------------------------------------");

        int idPrueba = 0;
        
        List<Object[]> lstEscalar = consultaDB(em, idHojaPruebas, tipoPrueba);

        try 
        {
            if (lstEscalar != null) 
            {
                String abortada = null;
                Object[] result = lstEscalar.get(0);
                idPrueba = (Integer) result[0];
                abortada = (String) result[1];
                String finalizada = (String) result[3];
                Integer usuario = (Integer) result[2];
                CallableInicioMotos.lecturaCondicionesAnormales = "";
                CallablePruebaMotos.condicionTemperatura = 0;
                if (finalizada.equalsIgnoreCase("Y")) {
                    return -1;
                }
                if (abortada.equalsIgnoreCase("Y")) {
                    HojaPruebas hp = em.find(HojaPruebas.class, idHojaPruebas);
                    if (hp.getReinspeccionList().size() > 0) {
                        Reinspeccion rH = hp.getReinspeccionList().get(0);
                        List<Pruebas> lstPruebas = rH.getPruebaList();
                        int cntPrueba = 0;
                        for (Pruebas pr : lstPruebas) {
                            if (pr.getTipoPrueba().getTesttype() == tipoPrueba) {
                                cntPrueba++;
                            }
                        }
                        if (cntPrueba > 4) {
                            return -2;
                        }
                    } else {
                        if (lstEscalar.size() > 4) {
                            return -2;
                        }
                    }
                    em.getTransaction().begin();
                    Pruebas prAbr = em.find(Pruebas.class, idPrueba);
                    prAbr.setFinalizada("X");
                    em.merge(prAbr);
                    Pruebas prueba = new Pruebas();
                    Calendar fecha = Calendar.getInstance();
                    prueba.setFechaprueba(fecha.getTime());
                    prueba.setFechaFinal(fecha.getTime());
                    Usuarios user = em.find(Usuarios.class, usuario);
                    prueba.setUsuarios(user);
                    prueba.setAbortada("N");
                    prueba.setAprobada("N");
                    prueba.setFinalizada("N");
                    prueba.setAutorizada("N");
                    TipoPrueba tipo = em.find(TipoPrueba.class, 8);

                    prueba.setTipoPrueba(tipo);
                    prueba.setHojaPruebas(hp);
                    em.persist(prueba);
                    em.flush();
                    em.getTransaction().commit();
                    if (hp.getReinspeccionList().size() > 0) {
                        Reinspeccion rH = hp.getReinspeccionList().get(0);
                        String strInsercion = "INSERT INTO reinspxprueba(id_reinspeccion,id_prueba_for) VALUES (" + rH.getId() + "," + prueba.getIdPruebas() + ")";
                        try {
                            RegistrarMedidas reg = new RegistrarMedidas();
                            Connection cn = reg.getConnection();
                            PreparedStatement insXP = cn.prepareStatement(strInsercion);
                            insXP.executeUpdate();
                        } catch (SQLException ex) {
                            Logger.getLogger(ControladorVerificar.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    return prueba.getIdPruebas();
                } else {
                    return idPrueba;
                }

            }else{
                System.err.println("-----No se tegistra informacion en la db");
                JOptionPane.showMessageDialog(null, "No se tegistra informacion en la db");
            }

        } catch (NoResultException | NullPointerException ne) {
            return -1;
        }

        return -1;
    }//end of method getPruebaNoFinalizada

    /**
     * 
     * 
     * @param em
     * @param idHojaPruebas
     * @param tipoPrueba
     * @return 
     */
    private List<Object[]> consultaDB(EntityManager em,int idHojaPruebas, int tipoPrueba)
    {
        try 
        {
            System.out.println("----------------------------------------------------");
            System.out.println("-------------------consultaDB-----------------------");
            System.out.println("----------------------------------------------------");

            Query q = em.createQuery("SELECT p.idPruebas,p.abortada, p.usuarios.geuser,p.finalizada FROM Pruebas p WHERE p.autorizada='N' AND p.hojaPruebas.testsheet = ?1 AND p.tipoPrueba.testtype = ?2  ORDER BY p.idPruebas DESC ");
            System.out.println("id hoja de pruebas que envio: " + idHojaPruebas + " tipo de prueba que envio: " + tipoPrueba);
            q.setParameter(1, idHojaPruebas);
            q.setParameter(2, tipoPrueba);
            List<Object[]> lstEscalar = q.getResultList();
            System.out.println("Longitud de la lista : "+ lstEscalar.size() );
            return lstEscalar;
        } catch (Exception e) 
        {
            System.err.println("idPrueba : "+ idHojaPruebas + " tipoPrueba : " + tipoPrueba);
            System.err.println("Error en el metodo: consultaDB()"+e.getMessage() + e.getLocalizedMessage());
        }
        return null;
    }
    
}
