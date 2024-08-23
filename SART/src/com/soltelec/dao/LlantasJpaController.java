/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.dao;

import com.soltelec.dao.conexion.PersistenceController;
import com.soltelec.dao.exceptions.IllegalOrphanException;
import com.soltelec.dao.exceptions.NonexistentEntityException;
import com.soltelec.dao.exceptions.PreexistingEntityException;
import com.soltelec.model.Llantas;
import com.soltelec.model.Vehiculos;
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
public class LlantasJpaController {

    public EntityManager getEntityManager() {
        return PersistenceController.getEntityManager();
    }

    public void create(Llantas llantas) throws PreexistingEntityException, Exception {
        if (llantas.getVehiculosCollection() == null) {
            llantas.setVehiculosCollection(new ArrayList<Vehiculos>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Vehiculos> attachedVehiculosCollection = new ArrayList<>();
            for (Vehiculos vehiculosCollectionVehiculosToAttach : llantas.getVehiculosCollection()) {
                vehiculosCollectionVehiculosToAttach = em.getReference(vehiculosCollectionVehiculosToAttach.getClass(), vehiculosCollectionVehiculosToAttach.getCar());
                attachedVehiculosCollection.add(vehiculosCollectionVehiculosToAttach);
            }
            llantas.setVehiculosCollection(attachedVehiculosCollection);
            em.persist(llantas);
            for (Vehiculos vehiculosCollectionVehiculos : llantas.getVehiculosCollection()) {
                Llantas oldLlantasOfVehiculosCollectionVehiculos = vehiculosCollectionVehiculos.getLlantas();
                vehiculosCollectionVehiculos.setLlantas(llantas);
                vehiculosCollectionVehiculos = em.merge(vehiculosCollectionVehiculos);
                if (oldLlantasOfVehiculosCollectionVehiculos != null) {
                    oldLlantasOfVehiculosCollectionVehiculos.getVehiculosCollection().remove(vehiculosCollectionVehiculos);
                    oldLlantasOfVehiculosCollectionVehiculos = em.merge(oldLlantasOfVehiculosCollectionVehiculos);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findLlantas(llantas.getWheel()) != null) {
                throw new PreexistingEntityException("Llantas " + llantas + " already exists.", ex);
            }
            throw ex;
        } finally {
            
        }
    }

    public void edit(Llantas llantas) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Llantas persistentLlantas = em.find(Llantas.class, llantas.getWheel());
            Collection<Vehiculos> vehiculosCollectionOld = persistentLlantas.getVehiculosCollection();
            Collection<Vehiculos> vehiculosCollectionNew = llantas.getVehiculosCollection();
            List<String> illegalOrphanMessages = null;
            for (Vehiculos vehiculosCollectionOldVehiculos : vehiculosCollectionOld) {
                if (!vehiculosCollectionNew.contains(vehiculosCollectionOldVehiculos)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain Vehiculos " + vehiculosCollectionOldVehiculos + " since its llantas field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Vehiculos> attachedVehiculosCollectionNew = new ArrayList<>();
            for (Vehiculos vehiculosCollectionNewVehiculosToAttach : vehiculosCollectionNew) {
                vehiculosCollectionNewVehiculosToAttach = em.getReference(vehiculosCollectionNewVehiculosToAttach.getClass(), vehiculosCollectionNewVehiculosToAttach.getCar());
                attachedVehiculosCollectionNew.add(vehiculosCollectionNewVehiculosToAttach);
            }
            vehiculosCollectionNew = attachedVehiculosCollectionNew;
            llantas.setVehiculosCollection(vehiculosCollectionNew);
            llantas = em.merge(llantas);
            for (Vehiculos vehiculosCollectionNewVehiculos : vehiculosCollectionNew) {
                if (!vehiculosCollectionOld.contains(vehiculosCollectionNewVehiculos)) {
                    Llantas oldLlantasOfVehiculosCollectionNewVehiculos = vehiculosCollectionNewVehiculos.getLlantas();
                    vehiculosCollectionNewVehiculos.setLlantas(llantas);
                    vehiculosCollectionNewVehiculos = em.merge(vehiculosCollectionNewVehiculos);
                    if (oldLlantasOfVehiculosCollectionNewVehiculos != null && !oldLlantasOfVehiculosCollectionNewVehiculos.equals(llantas)) {
                        oldLlantasOfVehiculosCollectionNewVehiculos.getVehiculosCollection().remove(vehiculosCollectionNewVehiculos);
                        oldLlantasOfVehiculosCollectionNewVehiculos = em.merge(oldLlantasOfVehiculosCollectionNewVehiculos);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = llantas.getWheel();
                if (findLlantas(id) == null) {
                    throw new NonexistentEntityException("The llantas with id " + id + " no longer exists.");
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
            Llantas llantas;
            try {
                llantas = em.getReference(Llantas.class, id);
                llantas.getWheel();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The llantas with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Vehiculos> vehiculosCollectionOrphanCheck = llantas.getVehiculosCollection();
            for (Vehiculos vehiculosCollectionOrphanCheckVehiculos : vehiculosCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This Llantas (" + llantas + ") cannot be destroyed since the Vehiculos " + vehiculosCollectionOrphanCheckVehiculos + " in its vehiculosCollection field has a non-nullable llantas field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(llantas);
            em.getTransaction().commit();
        } finally {
           
        }
    }

    public List<Llantas> findLlantasEntities() {
        return findLlantasEntities(true, -1, -1);
    }

    public List<Llantas> findLlantasEntities(int maxResults, int firstResult) {
        return findLlantasEntities(false, maxResults, firstResult);
    }

    private List<Llantas> findLlantasEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Llantas.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
           
        }
    }

    public Llantas findLlantas(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Llantas.class, id);
        } finally {
           
        }
    }

    public int getLlantasCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Llantas> rt = cq.from(Llantas.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
           
        }
    }

}
