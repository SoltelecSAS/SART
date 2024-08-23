/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.dao;

import com.soltelec.dao.conexion.PersistenceController;
import com.soltelec.dao.exceptions.IllegalOrphanException;
import com.soltelec.dao.exceptions.NonexistentEntityException;
import com.soltelec.model.Consolas;
import com.soltelec.model.Roads;
import java.util.ArrayList;
import java.util.Collection;
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
public class RoadsJpaController {

    public EntityManager getEntityManager() {
        return PersistenceController.getEntityManager();
    }

    public void create(Roads roads) {
        if (roads.getConsolasCollection() == null) {
            roads.setConsolasCollection(new ArrayList<Consolas>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Consolas> attachedConsolasCollection = new ArrayList<>();
            for (Consolas consolasCollectionConsolasToAttach : roads.getConsolasCollection()) {
                consolasCollectionConsolasToAttach = em.getReference(consolasCollectionConsolasToAttach.getClass(), consolasCollectionConsolasToAttach.getConsolasPK());
                attachedConsolasCollection.add(consolasCollectionConsolasToAttach);
            }
            roads.setConsolasCollection(attachedConsolasCollection);
            em.persist(roads);
            for (Consolas consolasCollectionConsolas : roads.getConsolasCollection()) {
                Roads oldRoadsOfConsolasCollectionConsolas = consolasCollectionConsolas.getRoads();
                consolasCollectionConsolas.setRoads(roads);
                consolasCollectionConsolas = em.merge(consolasCollectionConsolas);
                if (oldRoadsOfConsolasCollectionConsolas != null) {
                    oldRoadsOfConsolasCollectionConsolas.getConsolasCollection().remove(consolasCollectionConsolas);
                    oldRoadsOfConsolasCollectionConsolas = em.merge(oldRoadsOfConsolasCollectionConsolas);
                }
            }
            em.getTransaction().commit();
        } finally {
            
        }
    }

    public void edit(Roads roads) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Roads persistentRoads = em.find(Roads.class, roads.getRoad());
            Collection<Consolas> consolasCollectionOld = persistentRoads.getConsolasCollection();
            Collection<Consolas> consolasCollectionNew = roads.getConsolasCollection();
            List<String> illegalOrphanMessages = null;
            for (Consolas consolasCollectionOldConsolas : consolasCollectionOld) {
                if (!consolasCollectionNew.contains(consolasCollectionOldConsolas)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain Consolas " + consolasCollectionOldConsolas + " since its roads field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Consolas> attachedConsolasCollectionNew = new ArrayList<>();
            for (Consolas consolasCollectionNewConsolasToAttach : consolasCollectionNew) {
                consolasCollectionNewConsolasToAttach = em.getReference(consolasCollectionNewConsolasToAttach.getClass(), consolasCollectionNewConsolasToAttach.getConsolasPK());
                attachedConsolasCollectionNew.add(consolasCollectionNewConsolasToAttach);
            }
            consolasCollectionNew = attachedConsolasCollectionNew;
            roads.setConsolasCollection(consolasCollectionNew);
            roads = em.merge(roads);
            for (Consolas consolasCollectionNewConsolas : consolasCollectionNew) {
                if (!consolasCollectionOld.contains(consolasCollectionNewConsolas)) {
                    Roads oldRoadsOfConsolasCollectionNewConsolas = consolasCollectionNewConsolas.getRoads();
                    consolasCollectionNewConsolas.setRoads(roads);
                    consolasCollectionNewConsolas = em.merge(consolasCollectionNewConsolas);
                    if (oldRoadsOfConsolasCollectionNewConsolas != null && !oldRoadsOfConsolasCollectionNewConsolas.equals(roads)) {
                        oldRoadsOfConsolasCollectionNewConsolas.getConsolasCollection().remove(consolasCollectionNewConsolas);
                        oldRoadsOfConsolasCollectionNewConsolas = em.merge(oldRoadsOfConsolasCollectionNewConsolas);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = roads.getRoad();
                if (findRoads(id) == null) {
                    throw new NonexistentEntityException("The roads with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Roads roads;
            try {
                roads = em.getReference(Roads.class, id);
                roads.getRoad();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The roads with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Consolas> consolasCollectionOrphanCheck = roads.getConsolasCollection();
            for (Consolas consolasCollectionOrphanCheckConsolas : consolasCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This Roads (" + roads + ") cannot be destroyed since the Consolas " + consolasCollectionOrphanCheckConsolas + " in its consolasCollection field has a non-nullable roads field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(roads);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Roads> findRoadsEntities() {
        return findRoadsEntities(true, -1, -1);
    }

    public List<Roads> findRoadsEntities(int maxResults, int firstResult) {
        return findRoadsEntities(false, maxResults, firstResult);
    }

    private List<Roads> findRoadsEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Roads.class));
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

    public Roads findRoads(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Roads.class, id);
        } finally {
            em.close();
        }
    }

    public int getRoadsCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Roads> rt = cq.from(Roads.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
