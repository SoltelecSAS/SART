/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.dao;

import com.soltelec.dao.conexion.PersistenceController;
import com.soltelec.dao.exceptions.IllegalOrphanException;
import com.soltelec.dao.exceptions.NonexistentEntityException;
import com.soltelec.dao.exceptions.PreexistingEntityException;
import com.soltelec.model.Pendingtasks;
import com.soltelec.model.Schedules;
import java.util.ArrayList;
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
public class PendingtasksJpaController {

    public EntityManager getEntityManager() {
        return PersistenceController.getEntityManager();
    }

    public void create(Pendingtasks pendingtasks) throws IllegalOrphanException, PreexistingEntityException, Exception {
        List<String> illegalOrphanMessages = null;
        Schedules schedulesOrphanCheck = pendingtasks.getSchedules();
        if (schedulesOrphanCheck != null) {
            Pendingtasks oldPendingtasksOfSchedules = schedulesOrphanCheck.getPendingtasks();
            if (oldPendingtasksOfSchedules != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("The Schedules " + schedulesOrphanCheck + " already has an item of type Pendingtasks whose schedules column cannot be null. Please make another selection for the schedules field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Schedules schedules = pendingtasks.getSchedules();
            if (schedules != null) {
                schedules = em.getReference(schedules.getClass(), schedules.getSchedule());
                pendingtasks.setSchedules(schedules);
            }
            em.persist(pendingtasks);
            if (schedules != null) {
                schedules.setPendingtasks(pendingtasks);
                schedules = em.merge(schedules);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findPendingtasks(pendingtasks.getPendingtask()) != null) {
                throw new PreexistingEntityException("Pendingtasks " + pendingtasks + " already exists.", ex);
            }
            throw ex;
        } finally {
            
        }
    }

    public void edit(Pendingtasks pendingtasks) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Pendingtasks persistentPendingtasks = em.find(Pendingtasks.class, pendingtasks.getPendingtask());
            Schedules schedulesOld = persistentPendingtasks.getSchedules();
            Schedules schedulesNew = pendingtasks.getSchedules();
            List<String> illegalOrphanMessages = null;
            if (schedulesNew != null && !schedulesNew.equals(schedulesOld)) {
                Pendingtasks oldPendingtasksOfSchedules = schedulesNew.getPendingtasks();
                if (oldPendingtasksOfSchedules != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("The Schedules " + schedulesNew + " already has an item of type Pendingtasks whose schedules column cannot be null. Please make another selection for the schedules field.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (schedulesNew != null) {
                schedulesNew = em.getReference(schedulesNew.getClass(), schedulesNew.getSchedule());
                pendingtasks.setSchedules(schedulesNew);
            }
            pendingtasks = em.merge(pendingtasks);
            if (schedulesOld != null && !schedulesOld.equals(schedulesNew)) {
                schedulesOld.setPendingtasks(null);
                schedulesOld = em.merge(schedulesOld);
            }
            if (schedulesNew != null && !schedulesNew.equals(schedulesOld)) {
                schedulesNew.setPendingtasks(pendingtasks);
                schedulesNew = em.merge(schedulesNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = pendingtasks.getPendingtask();
                if (findPendingtasks(id) == null) {
                    throw new NonexistentEntityException("The pendingtasks with id " + id + " no longer exists.");
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
            Pendingtasks pendingtasks;
            try {
                pendingtasks = em.getReference(Pendingtasks.class, id);
                pendingtasks.getPendingtask();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The pendingtasks with id " + id + " no longer exists.", enfe);
            }
            Schedules schedules = pendingtasks.getSchedules();
            if (schedules != null) {
                schedules.setPendingtasks(null);
                schedules = em.merge(schedules);
            }
            em.remove(pendingtasks);
            em.getTransaction().commit();
        } finally {
            
        }
    }

    public List<Pendingtasks> findPendingtasksEntities() {
        return findPendingtasksEntities(true, -1, -1);
    }

    public List<Pendingtasks> findPendingtasksEntities(int maxResults, int firstResult) {
        return findPendingtasksEntities(false, maxResults, firstResult);
    }

    private List<Pendingtasks> findPendingtasksEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Pendingtasks.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
          
        }
    }

    public Pendingtasks findPendingtasks(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Pendingtasks.class, id);
        } finally {
          
        }
    }

    public int getPendingtasksCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Pendingtasks> rt = cq.from(Pendingtasks.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
           
        }
    }

}
