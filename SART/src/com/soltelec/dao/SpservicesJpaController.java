/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.dao;

import com.soltelec.dao.conexion.PersistenceController;
import com.soltelec.dao.exceptions.NonexistentEntityException;
import com.soltelec.dao.exceptions.PreexistingEntityException;
import com.soltelec.model.Spservices;
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
public class SpservicesJpaController {

    public EntityManager getEntityManager() {
        return PersistenceController.getEntityManager();
    }

    public void create(Spservices spservices) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(spservices);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findSpservices(spservices.getSpservice()) != null) {
                throw new PreexistingEntityException("Spservices " + spservices + " already exists.", ex);
            }
            throw ex;
        } finally {
            
        }
    }

    public void edit(Spservices spservices) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            spservices = em.merge(spservices);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = spservices.getSpservice();
                if (findSpservices(id) == null) {
                    throw new NonexistentEntityException("The spservices with id " + id + " no longer exists.");
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
            Spservices spservices;
            try {
                spservices = em.getReference(Spservices.class, id);
                spservices.getSpservice();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The spservices with id " + id + " no longer exists.", enfe);
            }
            em.remove(spservices);
            em.getTransaction().commit();
        } finally {
           
        }
    }

    public List<Spservices> findSpservicesEntities() {
        return findSpservicesEntities(true, -1, -1);
    }

    public List<Spservices> findSpservicesEntities(int maxResults, int firstResult) {
        return findSpservicesEntities(false, maxResults, firstResult);
    }

    private List<Spservices> findSpservicesEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Spservices.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
         
        }
    }

    public Spservices findSpservices(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Spservices.class, id);
        } finally {
          
        }
    }

    public int getSpservicesCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Spservices> rt = cq.from(Spservices.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
          
        }
    }

}
