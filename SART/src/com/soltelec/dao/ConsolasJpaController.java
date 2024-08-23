/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.dao;

import com.soltelec.dao.conexion.PersistenceController;
import com.soltelec.dao.exceptions.NonexistentEntityException;
import com.soltelec.dao.exceptions.PreexistingEntityException;
import com.soltelec.model.Consolas;
import com.soltelec.model.ConsolasPK;
import com.soltelec.model.Roads;
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
public class ConsolasJpaController {

    public EntityManager getEntityManager() {
        return PersistenceController.getEntityManager();
    }

    public void create(Consolas consolas) throws PreexistingEntityException, Exception {
        if (consolas.getConsolasPK() == null) {
            consolas.setConsolasPK(new ConsolasPK());
        }
        consolas.getConsolasPK().setRoad(consolas.getRoads().getRoad());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Roads roads = consolas.getRoads();
            if (roads != null) {
                roads = em.getReference(roads.getClass(), roads.getRoad());
                consolas.setRoads(roads);
            }
            em.persist(consolas);
            if (roads != null) {
                roads.getConsolasCollection().add(consolas);
                roads = em.merge(roads);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findConsolas(consolas.getConsolasPK()) != null) {
                throw new PreexistingEntityException("Consolas " + consolas + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Consolas consolas) throws NonexistentEntityException, Exception {
        consolas.getConsolasPK().setRoad(consolas.getRoads().getRoad());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Consolas persistentConsolas = em.find(Consolas.class, consolas.getConsolasPK());
            Roads roadsOld = persistentConsolas.getRoads();
            Roads roadsNew = consolas.getRoads();
            if (roadsNew != null) {
                roadsNew = em.getReference(roadsNew.getClass(), roadsNew.getRoad());
                consolas.setRoads(roadsNew);
            }
            consolas = em.merge(consolas);
            if (roadsOld != null && !roadsOld.equals(roadsNew)) {
                roadsOld.getConsolasCollection().remove(consolas);
                roadsOld = em.merge(roadsOld);
            }
            if (roadsNew != null && !roadsNew.equals(roadsOld)) {
                roadsNew.getConsolasCollection().add(consolas);
                roadsNew = em.merge(roadsNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                ConsolasPK id = consolas.getConsolasPK();
                if (findConsolas(id) == null) {
                    throw new NonexistentEntityException("The consolas with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(ConsolasPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Consolas consolas;
            try {
                consolas = em.getReference(Consolas.class, id);
                consolas.getConsolasPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The consolas with id " + id + " no longer exists.", enfe);
            }
            Roads roads = consolas.getRoads();
            if (roads != null) {
                roads.getConsolasCollection().remove(consolas);
                roads = em.merge(roads);
            }
            em.remove(consolas);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Consolas> findConsolasEntities() {
        return findConsolasEntities(true, -1, -1);
    }

    public List<Consolas> findConsolasEntities(int maxResults, int firstResult) {
        return findConsolasEntities(false, maxResults, firstResult);
    }

    private List<Consolas> findConsolasEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Consolas.class));
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

    public Consolas findConsolas(ConsolasPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Consolas.class, id);
        } finally {
            em.close();
        }
    }

    public int getConsolasCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Consolas> rt = cq.from(Consolas.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
