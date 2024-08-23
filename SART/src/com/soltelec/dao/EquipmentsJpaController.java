/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.dao;

import com.soltelec.dao.conexion.PersistenceController;
import com.soltelec.dao.exceptions.NonexistentEntityException;
import com.soltelec.dao.exceptions.PreexistingEntityException;
import com.soltelec.model.Equipments;
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
public class EquipmentsJpaController {

    public EntityManager getEntityManager() {
        return PersistenceController.getEntityManager();
    }

    public void create(Equipments equipments) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(equipments);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findEquipments(equipments.getEquipment()) != null) {
                throw new PreexistingEntityException("Equipments " + equipments + " already exists.", ex);
            }
            throw ex;
        } finally {
            
        }
    }

    public void edit(Equipments equipments) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            equipments = em.merge(equipments);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = equipments.getEquipment();
                if (findEquipments(id) == null) {
                    throw new NonexistentEntityException("The equipments with id " + id + " no longer exists.");
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
            Equipments equipments;
            try {
                equipments = em.getReference(Equipments.class, id);
                equipments.getEquipment();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The equipments with id " + id + " no longer exists.", enfe);
            }
            em.remove(equipments);
            em.getTransaction().commit();
        } finally {
          
        }
    }

    public List<Equipments> findEquipmentsEntities() {
        return findEquipmentsEntities(true, -1, -1);
    }

    public List<Equipments> findEquipmentsEntities(int maxResults, int firstResult) {
        return findEquipmentsEntities(false, maxResults, firstResult);
    }

    private List<Equipments> findEquipmentsEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Equipments.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
          
        }
    }

    public Equipments findEquipments(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Equipments.class, id);
        } finally {
           
        }
    }

    public int getEquipmentsCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Equipments> rt = cq.from(Equipments.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
           
        }
    }

}
