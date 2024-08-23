/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.dao;

import com.soltelec.dao.conexion.PersistenceController;
import com.soltelec.dao.exceptions.NonexistentEntityException;
import com.soltelec.dao.exceptions.PreexistingEntityException;
import com.soltelec.model.Dbshadows;
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
public class DbshadowsJpaController {

    public EntityManager getEntityManager() {
        return PersistenceController.getEntityManager();
    }

    public void create(Dbshadows dbshadows) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(dbshadows);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findDbshadows(dbshadows.getDbshadow()) != null) {
                throw new PreexistingEntityException("Dbshadows " + dbshadows + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Dbshadows dbshadows) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            dbshadows = em.merge(dbshadows);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = dbshadows.getDbshadow();
                if (findDbshadows(id) == null) {
                    throw new NonexistentEntityException("The dbshadows with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Dbshadows dbshadows;
            try {
                dbshadows = em.getReference(Dbshadows.class, id);
                dbshadows.getDbshadow();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The dbshadows with id " + id + " no longer exists.", enfe);
            }
            em.remove(dbshadows);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Dbshadows> findDbshadowsEntities() {
        return findDbshadowsEntities(true, -1, -1);
    }

    public List<Dbshadows> findDbshadowsEntities(int maxResults, int firstResult) {
        return findDbshadowsEntities(false, maxResults, firstResult);
    }

    private List<Dbshadows> findDbshadowsEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Dbshadows.class));
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

    public Dbshadows findDbshadows(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Dbshadows.class, id);
        } finally {
            em.close();
        }
    }

    public int getDbshadowsCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Dbshadows> rt = cq.from(Dbshadows.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
