/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.dao;

import com.soltelec.dao.conexion.PersistenceController;
import com.soltelec.dao.exceptions.NonexistentEntityException;
import com.soltelec.dao.exceptions.PreexistingEntityException;
import com.soltelec.model.Roadtypes;
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
public class RoadtypesJpaController {

    public EntityManager getEntityManager() {
        return PersistenceController.getEntityManager();
    }

    public void create(Roadtypes roadtypes) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(roadtypes);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findRoadtypes(roadtypes.getIroad()) != null) {
                throw new PreexistingEntityException("Roadtypes " + roadtypes + " already exists.", ex);
            }
            throw ex;
        } finally {
            
        }
    }

    public void edit(Roadtypes roadtypes) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            roadtypes = em.merge(roadtypes);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = roadtypes.getIroad();
                if (findRoadtypes(id) == null) {
                    throw new NonexistentEntityException("The roadtypes with id " + id + " no longer exists.");
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
            Roadtypes roadtypes;
            try {
                roadtypes = em.getReference(Roadtypes.class, id);
                roadtypes.getIroad();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The roadtypes with id " + id + " no longer exists.", enfe);
            }
            em.remove(roadtypes);
            em.getTransaction().commit();
        } finally {
            
        }
    }

    public List<Roadtypes> findRoadtypesEntities() {
        return findRoadtypesEntities(true, -1, -1);
    }

    public List<Roadtypes> findRoadtypesEntities(int maxResults, int firstResult) {
        return findRoadtypesEntities(false, maxResults, firstResult);
    }

    private List<Roadtypes> findRoadtypesEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Roadtypes.class));
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

    public Roadtypes findRoadtypes(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Roadtypes.class, id);
        } finally {
            em.close();
        }
    }

    public int getRoadtypesCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Roadtypes> rt = cq.from(Roadtypes.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
