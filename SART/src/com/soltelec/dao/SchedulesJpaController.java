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
import com.soltelec.model.Taskhistory;
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
public class SchedulesJpaController {

    public EntityManager getEntityManager() {
        return PersistenceController.getEntityManager();
    }

    public void create(Schedules schedules) throws PreexistingEntityException, Exception {
        if (schedules.getTaskhistoryCollection() == null) {
            schedules.setTaskhistoryCollection(new ArrayList<Taskhistory>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Pendingtasks pendingtasks = schedules.getPendingtasks();
            if (pendingtasks != null) {
                pendingtasks = em.getReference(pendingtasks.getClass(), pendingtasks.getPendingtask());
                schedules.setPendingtasks(pendingtasks);
            }
            Tasks tasks = schedules.getTasks();
            if (tasks != null) {
                tasks = em.getReference(tasks.getClass(), tasks.getTask());
                schedules.setTasks(tasks);
            }
            Collection<Taskhistory> attachedTaskhistoryCollection = new ArrayList<>();
            for (Taskhistory taskhistoryCollectionTaskhistoryToAttach : schedules.getTaskhistoryCollection()) {
                taskhistoryCollectionTaskhistoryToAttach = em.getReference(taskhistoryCollectionTaskhistoryToAttach.getClass(), taskhistoryCollectionTaskhistoryToAttach.getTaskhistory());
                attachedTaskhistoryCollection.add(taskhistoryCollectionTaskhistoryToAttach);
            }
            schedules.setTaskhistoryCollection(attachedTaskhistoryCollection);
            em.persist(schedules);
            if (pendingtasks != null) {
                Schedules oldSchedulesOfPendingtasks = pendingtasks.getSchedules();
                if (oldSchedulesOfPendingtasks != null) {
                    oldSchedulesOfPendingtasks.setPendingtasks(null);
                    oldSchedulesOfPendingtasks = em.merge(oldSchedulesOfPendingtasks);
                }
                pendingtasks.setSchedules(schedules);
                pendingtasks = em.merge(pendingtasks);
            }
            if (tasks != null) {
                tasks.getSchedulesCollection().add(schedules);
                tasks = em.merge(tasks);
            }
            for (Taskhistory taskhistoryCollectionTaskhistory : schedules.getTaskhistoryCollection()) {
                Schedules oldSchedulesOfTaskhistoryCollectionTaskhistory = taskhistoryCollectionTaskhistory.getSchedules();
                taskhistoryCollectionTaskhistory.setSchedules(schedules);
                taskhistoryCollectionTaskhistory = em.merge(taskhistoryCollectionTaskhistory);
                if (oldSchedulesOfTaskhistoryCollectionTaskhistory != null) {
                    oldSchedulesOfTaskhistoryCollectionTaskhistory.getTaskhistoryCollection().remove(taskhistoryCollectionTaskhistory);
                    oldSchedulesOfTaskhistoryCollectionTaskhistory = em.merge(oldSchedulesOfTaskhistoryCollectionTaskhistory);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findSchedules(schedules.getSchedule()) != null) {
                throw new PreexistingEntityException("Schedules " + schedules + " already exists.", ex);
            }
            throw ex;
        } finally {
           
        }
    }

    public void edit(Schedules schedules) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Schedules persistentSchedules = em.find(Schedules.class, schedules.getSchedule());
            Pendingtasks pendingtasksOld = persistentSchedules.getPendingtasks();
            Pendingtasks pendingtasksNew = schedules.getPendingtasks();
            Tasks tasksOld = persistentSchedules.getTasks();
            Tasks tasksNew = schedules.getTasks();
            Collection<Taskhistory> taskhistoryCollectionOld = persistentSchedules.getTaskhistoryCollection();
            Collection<Taskhistory> taskhistoryCollectionNew = schedules.getTaskhistoryCollection();
            List<String> illegalOrphanMessages = null;
            if (pendingtasksOld != null && !pendingtasksOld.equals(pendingtasksNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("You must retain Pendingtasks " + pendingtasksOld + " since its schedules field is not nullable.");
            }
            for (Taskhistory taskhistoryCollectionOldTaskhistory : taskhistoryCollectionOld) {
                if (!taskhistoryCollectionNew.contains(taskhistoryCollectionOldTaskhistory)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain Taskhistory " + taskhistoryCollectionOldTaskhistory + " since its schedules field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (pendingtasksNew != null) {
                pendingtasksNew = em.getReference(pendingtasksNew.getClass(), pendingtasksNew.getPendingtask());
                schedules.setPendingtasks(pendingtasksNew);
            }
            if (tasksNew != null) {
                tasksNew = em.getReference(tasksNew.getClass(), tasksNew.getTask());
                schedules.setTasks(tasksNew);
            }
            Collection<Taskhistory> attachedTaskhistoryCollectionNew = new ArrayList<>();
            for (Taskhistory taskhistoryCollectionNewTaskhistoryToAttach : taskhistoryCollectionNew) {
                taskhistoryCollectionNewTaskhistoryToAttach = em.getReference(taskhistoryCollectionNewTaskhistoryToAttach.getClass(), taskhistoryCollectionNewTaskhistoryToAttach.getTaskhistory());
                attachedTaskhistoryCollectionNew.add(taskhistoryCollectionNewTaskhistoryToAttach);
            }
            taskhistoryCollectionNew = attachedTaskhistoryCollectionNew;
            schedules.setTaskhistoryCollection(taskhistoryCollectionNew);
            schedules = em.merge(schedules);
            if (pendingtasksNew != null && !pendingtasksNew.equals(pendingtasksOld)) {
                Schedules oldSchedulesOfPendingtasks = pendingtasksNew.getSchedules();
                if (oldSchedulesOfPendingtasks != null) {
                    oldSchedulesOfPendingtasks.setPendingtasks(null);
                    oldSchedulesOfPendingtasks = em.merge(oldSchedulesOfPendingtasks);
                }
                pendingtasksNew.setSchedules(schedules);
                pendingtasksNew = em.merge(pendingtasksNew);
            }
            if (tasksOld != null && !tasksOld.equals(tasksNew)) {
                tasksOld.getSchedulesCollection().remove(schedules);
                tasksOld = em.merge(tasksOld);
            }
            if (tasksNew != null && !tasksNew.equals(tasksOld)) {
                tasksNew.getSchedulesCollection().add(schedules);
                tasksNew = em.merge(tasksNew);
            }
            for (Taskhistory taskhistoryCollectionNewTaskhistory : taskhistoryCollectionNew) {
                if (!taskhistoryCollectionOld.contains(taskhistoryCollectionNewTaskhistory)) {
                    Schedules oldSchedulesOfTaskhistoryCollectionNewTaskhistory = taskhistoryCollectionNewTaskhistory.getSchedules();
                    taskhistoryCollectionNewTaskhistory.setSchedules(schedules);
                    taskhistoryCollectionNewTaskhistory = em.merge(taskhistoryCollectionNewTaskhistory);
                    if (oldSchedulesOfTaskhistoryCollectionNewTaskhistory != null && !oldSchedulesOfTaskhistoryCollectionNewTaskhistory.equals(schedules)) {
                        oldSchedulesOfTaskhistoryCollectionNewTaskhistory.getTaskhistoryCollection().remove(taskhistoryCollectionNewTaskhistory);
                        oldSchedulesOfTaskhistoryCollectionNewTaskhistory = em.merge(oldSchedulesOfTaskhistoryCollectionNewTaskhistory);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = schedules.getSchedule();
                if (findSchedules(id) == null) {
                    throw new NonexistentEntityException("The schedules with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Schedules schedules;
            try {
                schedules = em.getReference(Schedules.class, id);
                schedules.getSchedule();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The schedules with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Pendingtasks pendingtasksOrphanCheck = schedules.getPendingtasks();
            if (pendingtasksOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This Schedules (" + schedules + ") cannot be destroyed since the Pendingtasks " + pendingtasksOrphanCheck + " in its pendingtasks field has a non-nullable schedules field.");
            }
            Collection<Taskhistory> taskhistoryCollectionOrphanCheck = schedules.getTaskhistoryCollection();
            for (Taskhistory taskhistoryCollectionOrphanCheckTaskhistory : taskhistoryCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This Schedules (" + schedules + ") cannot be destroyed since the Taskhistory " + taskhistoryCollectionOrphanCheckTaskhistory + " in its taskhistoryCollection field has a non-nullable schedules field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Tasks tasks = schedules.getTasks();
            if (tasks != null) {
                tasks.getSchedulesCollection().remove(schedules);
                tasks = em.merge(tasks);
            }
            em.remove(schedules);
            em.getTransaction().commit();
        } finally {
            
        }
    }

    public List<Schedules> findSchedulesEntities() {
        return findSchedulesEntities(true, -1, -1);
    }

    public List<Schedules> findSchedulesEntities(int maxResults, int firstResult) {
        return findSchedulesEntities(false, maxResults, firstResult);
    }

    private List<Schedules> findSchedulesEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Schedules.class));
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

    public Schedules findSchedules(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Schedules.class, id);
        } finally {
            em.close();
        }
    }

    public int getSchedulesCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Schedules> rt = cq.from(Schedules.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
