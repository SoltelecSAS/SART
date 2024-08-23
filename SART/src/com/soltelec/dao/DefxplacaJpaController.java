/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.dao;

import com.soltelec.dao.conexion.PersistenceController;
import com.soltelec.dao.exceptions.NonexistentEntityException;
import com.soltelec.dao.exceptions.PreexistingEntityException;
import com.soltelec.model.Defectos;
import com.soltelec.model.Defxplaca;
import com.soltelec.model.DefxplacaPK;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author Administrador
 */
public class DefxplacaJpaController {

    public EntityManager getEntityManager() {
        return PersistenceController.getEntityManager();
    }

    public void create(Defxplaca defxplaca) throws PreexistingEntityException, Exception {
        if (defxplaca.getDefxplacaPK() == null) {
            defxplaca.setDefxplacaPK(new DefxplacaPK());
        }
        defxplaca.getDefxplacaPK().setIdDefecto(defxplaca.getDefectos().getCardefault());
        defxplaca.getDefxplacaPK().setIdhojapruebafor(defxplaca.getHojaPruebas().getTestsheet());
        defxplaca.getDefxplacaPK().setIdVehiculoFor(defxplaca.getVehiculos().getCar());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Defectos defectos = defxplaca.getDefectos();
            if (defectos != null) {
                defectos = em.getReference(defectos.getClass(), defectos.getCardefault());
                defxplaca.setDefectos(defectos);
            }
            em.persist(defxplaca);
            if (defectos != null) {
                defectos.getDefxplacaCollection().add(defxplaca);
                defectos = em.merge(defectos);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findDefxplaca(defxplaca.getDefxplacaPK()) != null) {
                throw new PreexistingEntityException("Defxplaca " + defxplaca + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Defxplaca defxplaca) throws NonexistentEntityException, Exception {
        defxplaca.getDefxplacaPK().setIdDefecto(defxplaca.getDefectos().getCardefault());
        defxplaca.getDefxplacaPK().setIdhojapruebafor(defxplaca.getHojaPruebas().getTestsheet());
        defxplaca.getDefxplacaPK().setIdVehiculoFor(defxplaca.getVehiculos().getCar());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Defxplaca persistentDefxplaca = em.find(Defxplaca.class, defxplaca.getDefxplacaPK());
            Defectos defectosOld = persistentDefxplaca.getDefectos();
            Defectos defectosNew = defxplaca.getDefectos();
            if (defectosNew != null) {
                defectosNew = em.getReference(defectosNew.getClass(), defectosNew.getCardefault());
                defxplaca.setDefectos(defectosNew);
            }
            defxplaca = em.merge(defxplaca);
            if (defectosOld != null && !defectosOld.equals(defectosNew)) {
                defectosOld.getDefxplacaCollection().remove(defxplaca);
                defectosOld = em.merge(defectosOld);
            }
            if (defectosNew != null && !defectosNew.equals(defectosOld)) {
                defectosNew.getDefxplacaCollection().add(defxplaca);
                defectosNew = em.merge(defectosNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                DefxplacaPK id = defxplaca.getDefxplacaPK();
                if (findDefxplaca(id) == null) {
                    throw new NonexistentEntityException("The defxplaca with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(DefxplacaPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Defxplaca defxplaca;
            try {
                defxplaca = em.getReference(Defxplaca.class, id);
                defxplaca.getDefxplacaPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The defxplaca with id " + id + " no longer exists.", enfe);
            }
            Defectos defectos = defxplaca.getDefectos();
            if (defectos != null) {
                defectos.getDefxplacaCollection().remove(defxplaca);
                defectos = em.merge(defectos);
            }
            em.remove(defxplaca);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Defxplaca> findDefxplacaEntities() {
        return findDefxplacaEntities(true, -1, -1);
    }

    public List<Defxplaca> findDefxplacaEntities(int maxResults, int firstResult) {
        return findDefxplacaEntities(false, maxResults, firstResult);
    }

    private List<Defxplaca> findDefxplacaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Defxplaca.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Defxplaca findDefxplaca(DefxplacaPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Defxplaca.class, id);
        } finally {
            em.close();
        }
    }

    public int getDefxplacaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Defxplaca> rt = cq.from(Defxplaca.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
