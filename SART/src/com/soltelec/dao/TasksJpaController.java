/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.dao;

import com.soltelec.dao.conexion.PersistenceController;
import com.soltelec.dao.exceptions.IllegalOrphanException;
import com.soltelec.dao.exceptions.NonexistentEntityException;
import com.soltelec.dao.exceptions.PreexistingEntityException;
import com.soltelec.model.Schedules;
import com.soltelec.model.Tasks;
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
public class TasksJpaController {

    public EntityManager getEntityManager() {
        return PersistenceController.getEntityManager();
    }

    public void create(Tasks tasks) throws PreexistingEntityException, Exception {
        if (tasks.getSchedulesCollection() == null) {
            tasks.setSchedulesCollection(new ArrayList<Schedules>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Schedules> attachedSchedulesCollection = new ArrayList<>();
            for (Schedules schedulesCollectionSchedulesToAttach : tasks.getSchedulesCollection()) {
                schedulesCollectionSchedulesToAttach = em.getReference(schedulesCollectionSchedulesToAttach.getClass(), schedulesCollectionSchedulesToAttach.getSchedule());
                attachedSchedulesCollection.add(schedulesCollectionSchedulesToAttach);
            }
            tasks.setSchedulesCollection(attachedSchedulesCollection);
            em.persist(tasks);
            for (Schedules schedulesCollectionSchedules : tasks.getSchedulesCollection()) {
                Tasks oldTasksOfSchedulesCollectionSchedules = schedulesCollectionSchedules.getTasks();
                schedulesCollectionSchedules.setTasks(tasks);
                schedulesCollectionSchedules = em.merge(schedulesCollectionSchedules);
                if (oldTasksOfSchedulesCollectionSchedules != null) {
                    oldTasksOfSchedulesCollectionSchedules.getSchedulesCollection().remove(schedulesCollectionSchedules);
                    oldTasksOfSchedulesCollectionSchedules = em.merge(oldTasksOfSchedulesCollectionSchedules);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findTasks(tasks.getTask()) != null) {
                throw new PreexistingEntityException("Tasks " + tasks + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Tasks tasks) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Tasks persistentTasks = em.find(Tasks.class, tasks.getTask());
            Collection<Schedules> schedulesCollectionOld = persistentTasks.getSchedulesCollection();
            Collection<Schedules> schedulesCollectionNew = tasks.getSchedulesCollection();
            List<String> illegalOrphanMessages = null;
            for (Schedules schedulesCollectionOldSchedules : schedulesCollectionOld) {
                if (!schedulesCollectionNew.contains(schedulesCollectionOldSchedules)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain Schedules " + schedulesCollectionOldSchedules + " since its tasks field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Schedules> attachedSchedulesCollectionNew = new ArrayList<>();
            for (Schedules schedulesCollectionNewSchedulesToAttach : schedulesCollectionNew) {
                schedulesCollectionNewSchedulesToAttach = em.getReference(schedulesCollectionNewSchedulesToAttach.getClass(), schedulesCollectionNewSchedulesToAttach.getSchedule());
                attachedSchedulesCollectionNew.add(schedulesCollectionNewSchedulesToAttach);
            }
            schedulesCollectionNew = attachedSchedulesCollectionNew;
            tasks.setSchedulesCollection(schedulesCollectionNew);
            tasks = em.merge(tasks);
            for (Schedules schedulesCollectionNewSchedules : schedulesCollectionNew) {
                if (!schedulesCollectionOld.contains(schedulesCollectionNewSchedules)) {
                    Tasks oldTasksOfSchedulesCollectionNewSchedules = schedulesCollectionNewSchedules.getTasks();
                    schedulesCollectionNewSchedules.setTasks(tasks);
                    schedulesCollectionNewSchedules = em.merge(schedulesCollectionNewSchedules);
                    if (oldTasksOfSchedulesCollectionNewSchedules != null && !oldTasksOfSchedulesCollectionNewSchedules.equals(tasks)) {
                        oldTasksOfSchedulesCollectionNewSchedules.getSchedulesCollection().remove(schedulesCollectionNewSchedules);
                        oldTasksOfSchedulesCollectionNewSchedules = em.merge(oldTasksOfSchedulesCollectionNewSchedules);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = tasks.getTask();
                if (findTasks(id) == null) {
                    throw new NonexistentEntityException("The tasks with id " + id + " no longer exists.");
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
            Tasks tasks;
            try {
                tasks = em.getReference(Tasks.class, id);
                tasks.getTask();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The tasks with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Schedules> schedulesCollectionOrphanCheck = tasks.getSchedulesCollection();
            for (Schedules schedulesCollectionOrphanCheckSchedules : schedulesCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This Tasks (" + tasks + ") cannot be destroyed since the Schedules " + schedulesCollectionOrphanCheckSchedules + " in its schedulesCollection field has a non-nullable tasks field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(tasks);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Tasks> findTasksEntities() {
        return findTasksEntities(true, -1, -1);
    }

    public List<Tasks> findTasksEntities(int maxResults, int firstResult) {
        return findTasksEntities(false, maxResults, firstResult);
    }

    private List<Tasks> findTasksEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Tasks.class));
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

    public Tasks findTasks(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Tasks.class, id);
        } finally {
            em.close();
        }
    }

    public int getTasksCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Tasks> rt = cq.from(Tasks.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
