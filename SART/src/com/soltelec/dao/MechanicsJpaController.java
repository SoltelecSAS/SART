/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.dao;

import com.soltelec.dao.conexion.PersistenceController;
import com.soltelec.dao.exceptions.NonexistentEntityException;
import com.soltelec.dao.exceptions.PreexistingEntityException;
import com.soltelec.model.Mechanics;
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
public class MechanicsJpaController {

    public EntityManager getEntityManager() {
        return PersistenceController.getEntityManager();
    }

    public void create(Mechanics mechanics) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(mechanics);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findMechanics(mechanics.getMechanic()) != null) {
                throw new PreexistingEntityException("Mechanics " + mechanics + " already exists.", ex);
            }
            throw ex;
        } finally {
            
        }
    }

    public void edit(Mechanics mechanics) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            mechanics = em.merge(mechanics);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = mechanics.getMechanic();
                if (findMechanics(id) == null) {
                    throw new NonexistentEntityException("The mechanics with id " + id + " no longer exists.");
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
            Mechanics mechanics;
            try {
                mechanics = em.getReference(Mechanics.class, id);
                mechanics.getMechanic();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The mechanics with id " + id + " no longer exists.", enfe);
            }
            em.remove(mechanics);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Mechanics> findMechanicsEntities() {
        return findMechanicsEntities(true, -1, -1);
    }

    public List<Mechanics> findMechanicsEntities(int maxResults, int firstResult) {
        return findMechanicsEntities(false, maxResults, firstResult);
    }

    private List<Mechanics> findMechanicsEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Mechanics.class));
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

    public Mechanics findMechanics(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Mechanics.class, id);
        } finally {
            em.close();
        }
    }

    public int getMechanicsCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Mechanics> rt = cq.from(Mechanics.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
