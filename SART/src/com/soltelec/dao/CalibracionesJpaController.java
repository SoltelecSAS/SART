/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.dao;

import com.soltelec.dao.conexion.PersistenceController;
import com.soltelec.dao.exceptions.NonexistentEntityException;
import com.soltelec.model.Calibraciones;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.soltelec.model.TipoCalibracion;
import com.soltelec.model.Usuarios;
import com.soltelec.model.Equipos;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author Dany
 */
public class CalibracionesJpaController implements Serializable {

    public EntityManager getEntityManager() {
        return PersistenceController.getEntityManager();
    }

    public void create(Calibraciones calibraciones) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TipoCalibracion idTipoCalibracion = calibraciones.getIdTipoCalibracion();
            if (idTipoCalibracion != null) {
                idTipoCalibracion = em.getReference(idTipoCalibracion.getClass(), idTipoCalibracion.getIdTipoCalibracion());
                calibraciones.setIdTipoCalibracion(idTipoCalibracion);
            }
            Usuarios geuser = calibraciones.getGeuser();
            if (geuser != null) {
                geuser = em.getReference(geuser.getClass(), geuser.getGeuser());
                calibraciones.setGeuser(geuser);
            }
            Equipos idEquipo = calibraciones.getIdEquipo();
            if (idEquipo != null) {
                idEquipo = em.getReference(idEquipo.getClass(), idEquipo.getIdEquipo());
                calibraciones.setIdEquipo(idEquipo);
            }
            em.persist(calibraciones);
            if (idTipoCalibracion != null) {
                idTipoCalibracion.getCalibracionesList().add(calibraciones);
                idTipoCalibracion = em.merge(idTipoCalibracion);
            }
            
            if (geuser != null) {
                geuser.getCalibracionesList().add(calibraciones);
                geuser = em.merge(geuser);
            }
            
            if (idEquipo != null) {
                idEquipo.getCalibracionesList().add(calibraciones);
                idEquipo = em.merge(idEquipo);
            }
            em.getTransaction().commit();
        } finally {
           
        }
    }

    public void edit(Calibraciones calibraciones) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Calibraciones persistentCalibraciones = em.find(Calibraciones.class, calibraciones.getCalibration());
            TipoCalibracion idTipoCalibracionOld = persistentCalibraciones.getIdTipoCalibracion();
            TipoCalibracion idTipoCalibracionNew = calibraciones.getIdTipoCalibracion();
            Usuarios geuserOld = persistentCalibraciones.getGeuser();
            Usuarios geuserNew = calibraciones.getGeuser();
            Equipos idEquipoOld = persistentCalibraciones.getIdEquipo();
            Equipos idEquipoNew = calibraciones.getIdEquipo();
            if (idTipoCalibracionNew != null) {
                idTipoCalibracionNew = em.getReference(idTipoCalibracionNew.getClass(), idTipoCalibracionNew.getIdTipoCalibracion());
                calibraciones.setIdTipoCalibracion(idTipoCalibracionNew);
            }
            if (geuserNew != null) {
                geuserNew = em.getReference(geuserNew.getClass(), geuserNew.getGeuser());
                calibraciones.setGeuser(geuserNew);
            }
            if (idEquipoNew != null) {
                idEquipoNew = em.getReference(idEquipoNew.getClass(), idEquipoNew.getIdEquipo());
                calibraciones.setIdEquipo(idEquipoNew);
            }
            calibraciones = em.merge(calibraciones);
            if (idTipoCalibracionOld != null && !idTipoCalibracionOld.equals(idTipoCalibracionNew)) {
                idTipoCalibracionOld.getCalibracionesList().remove(calibraciones);
                idTipoCalibracionOld = em.merge(idTipoCalibracionOld);
            }
            if (idTipoCalibracionNew != null && !idTipoCalibracionNew.equals(idTipoCalibracionOld)) {
                idTipoCalibracionNew.getCalibracionesList().add(calibraciones);
                idTipoCalibracionNew = em.merge(idTipoCalibracionNew);
            }
            if (geuserOld != null && !geuserOld.equals(geuserNew)) {
                geuserOld.getCalibracionesList().remove(calibraciones);
                geuserOld = em.merge(geuserOld);
            }
            if (geuserNew != null && !geuserNew.equals(geuserOld)) {
                geuserNew.getCalibracionesList().add(calibraciones);
                geuserNew = em.merge(geuserNew);
            }
            if (idEquipoOld != null && !idEquipoOld.equals(idEquipoNew)) {
                idEquipoOld.getCalibracionesList().remove(calibraciones);
                idEquipoOld = em.merge(idEquipoOld);
            }
            if (idEquipoNew != null && !idEquipoNew.equals(idEquipoOld)) {
                idEquipoNew.getCalibracionesList().add(calibraciones);
                idEquipoNew = em.merge(idEquipoNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = calibraciones.getCalibration();
                if (findCalibraciones(id) == null) {
                    throw new NonexistentEntityException("The calibraciones with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Calibraciones calibraciones;
            try {
                calibraciones = em.getReference(Calibraciones.class, id);
                calibraciones.getCalibration();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The calibraciones with id " + id + " no longer exists.", enfe);
            }
            TipoCalibracion idTipoCalibracion = calibraciones.getIdTipoCalibracion();
            if (idTipoCalibracion != null) {
                idTipoCalibracion.getCalibracionesList().remove(calibraciones);
                idTipoCalibracion = em.merge(idTipoCalibracion);
            }
            Usuarios geuser = calibraciones.getGeuser();
            if (geuser != null) {
                geuser.getCalibracionesList().remove(calibraciones);
                geuser = em.merge(geuser);
            }
            Equipos idEquipo = calibraciones.getIdEquipo();
            if (idEquipo != null) {
                idEquipo.getCalibracionesList().remove(calibraciones);
                idEquipo = em.merge(idEquipo);
            }
            em.remove(calibraciones);
            em.getTransaction().commit();
        } finally {
            
        }
    }

    public List<Calibraciones> findCalibracionesEntities() {
        return findCalibracionesEntities(true, -1, -1);
    }

    public List<Calibraciones> findCalibracionesEntities(int maxResults, int firstResult) {
        return findCalibracionesEntities(false, maxResults, firstResult);
    }

    private List<Calibraciones> findCalibracionesEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Calibraciones.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            
        }
    }

    public Calibraciones findCalibraciones(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Calibraciones.class, id);
        } finally {
           
        }
    }

    public int getCalibracionesCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Calibraciones> rt = cq.from(Calibraciones.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            
        }
    }
    
}
