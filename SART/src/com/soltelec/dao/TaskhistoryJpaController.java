/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.dao;

import com.soltelec.dao.conexion.PersistenceController;
import com.soltelec.dao.exceptions.NonexistentEntityException;
import com.soltelec.dao.exceptions.PreexistingEntityException;
import com.soltelec.model.Schedules;
import com.soltelec.model.Taskhistory;
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
public class TaskhistoryJpaController {

    public EntityManager getEntityManager() {
        return PersistenceController.getEntityManager();
    }

    public void create(Taskhistory taskhistory) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Schedules schedules = taskhistory.getSchedules();
            if (schedules != null) {
                schedules = em.getReference(schedules.getClass(), schedules.getSchedule());
                taskhistory.setSchedules(schedules);
            }
            em.persist(taskhistory);
            if (schedules != null) {
                schedules.getTaskhistoryCollection().add(taskhistory);
                schedules = em.merge(schedules);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findTaskhistory(taskhistory.getTaskhistory()) != null) {
                throw new PreexistingEntityException("Taskhistory " + taskhistory + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Taskhistory taskhistory) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Taskhistory persistentTaskhistory = em.find(Taskhistory.class, taskhistory.getTaskhistory());
            Schedules schedulesOld = persistentTaskhistory.getSchedules();
            Schedules schedulesNew = taskhistory.getSchedules();
            if (schedulesNew != null) {
                schedulesNew = em.getReference(schedulesNew.getClass(), schedulesNew.getSchedule());
                taskhistory.setSchedules(schedulesNew);
            }
            taskhistory = em.merge(taskhistory);
            if (schedulesOld != null && !schedulesOld.equals(schedulesNew)) {
                schedulesOld.getTaskhistoryCollection().remove(taskhistory);
                schedulesOld = em.merge(schedulesOld);
            }
            if (schedulesNew != null && !schedulesNew.equals(schedulesOld)) {
                schedulesNew.getTaskhistoryCollection().add(taskhistory);
                schedulesNew = em.merge(schedulesNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = taskhistory.getTaskhistory();
                if (findTaskhistory(id) == null) {
                    throw new NonexistentEntityException("The taskhistory with id " + id + " no longer exists.");
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
            Taskhistory taskhistory;
            try {
                taskhistory = em.getReference(Taskhistory.class, id);
                taskhistory.getTaskhistory();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The taskhistory with id " + id + " no longer exists.", enfe);
            }
            Schedules schedules = taskhistory.getSchedules();
            if (schedules != null) {
                schedules.getTaskhistoryCollection().remove(taskhistory);
                schedules = em.merge(schedules);
            }
            em.remove(taskhistory);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Taskhistory> findTaskhistoryEntities() {
        return findTaskhistoryEntities(true, -1, -1);
    }

    public List<Taskhistory> findTaskhistoryEntities(int maxResults, int firstResult) {
        return findTaskhistoryEntities(false, maxResults, firstResult);
    }

    private List<Taskhistory> findTaskhistoryEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Taskhistory.class));
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

    public Taskhistory findTaskhistory(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Taskhistory.class, id);
        } finally {
            em.close();
        }
    }

    public int getTaskhistoryCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Taskhistory> rt = cq.from(Taskhistory.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
