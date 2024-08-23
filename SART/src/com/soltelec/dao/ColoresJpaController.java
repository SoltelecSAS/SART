/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.dao;

import com.soltelec.dao.conexion.PersistenceController;
import com.soltelec.dao.exceptions.IllegalOrphanException;
import com.soltelec.dao.exceptions.NonexistentEntityException;
import com.soltelec.model.Colores;
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
 * @author GerenciaDesarrollo
 */
public class ColoresJpaController {

    public EntityManager getEntityManager() {
        return PersistenceController.getEntityManager();
    }

    public void create(Colores colores) {
        if (colores.getVehiculosCollection() == null) {
            colores.setVehiculosCollection(new ArrayList<Vehiculos>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Vehiculos> attachedVehiculosCollection = new ArrayList<>();
            for (Vehiculos vehiculosCollectionVehiculosToAttach : colores.getVehiculosCollection()) {
                vehiculosCollectionVehiculosToAttach = em.getReference(vehiculosCollectionVehiculosToAttach.getClass(), vehiculosCollectionVehiculosToAttach.getCar());
                attachedVehiculosCollection.add(vehiculosCollectionVehiculosToAttach);
            }
            colores.setVehiculosCollection(attachedVehiculosCollection);
            em.persist(colores);
            for (Vehiculos vehiculosCollectionVehiculos : colores.getVehiculosCollection()) {
                Colores oldColoresOfVehiculosCollectionVehiculos = vehiculosCollectionVehiculos.getColores();
                vehiculosCollectionVehiculos.setColores(colores);
                vehiculosCollectionVehiculos = em.merge(vehiculosCollectionVehiculos);
                if (oldColoresOfVehiculosCollectionVehiculos != null) {
                    oldColoresOfVehiculosCollectionVehiculos.getVehiculosCollection().remove(vehiculosCollectionVehiculos);
                    oldColoresOfVehiculosCollectionVehiculos = em.merge(oldColoresOfVehiculosCollectionVehiculos);
                }
            }
            em.getTransaction().commit();
        } finally {
            
        }
    }

    public void edit(Colores colores) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Colores persistentColores = em.find(Colores.class, colores.getColor());
            Collection<Vehiculos> vehiculosCollectionOld = persistentColores.getVehiculosCollection();
            Collection<Vehiculos> vehiculosCollectionNew = colores.getVehiculosCollection();
            List<String> illegalOrphanMessages = null;
            for (Vehiculos vehiculosCollectionOldVehiculos : vehiculosCollectionOld) {
                if (!vehiculosCollectionNew.contains(vehiculosCollectionOldVehiculos)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain Vehiculos " + vehiculosCollectionOldVehiculos + " since its colores field is not nullable.");
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
            colores.setVehiculosCollection(vehiculosCollectionNew);
            colores = em.merge(colores);
            for (Vehiculos vehiculosCollectionNewVehiculos : vehiculosCollectionNew) {
                if (!vehiculosCollectionOld.contains(vehiculosCollectionNewVehiculos)) {
                    Colores oldColoresOfVehiculosCollectionNewVehiculos = vehiculosCollectionNewVehiculos.getColores();
                    vehiculosCollectionNewVehiculos.setColores(colores);
                    vehiculosCollectionNewVehiculos = em.merge(vehiculosCollectionNewVehiculos);
                    if (oldColoresOfVehiculosCollectionNewVehiculos != null && !oldColoresOfVehiculosCollectionNewVehiculos.equals(colores)) {
                        oldColoresOfVehiculosCollectionNewVehiculos.getVehiculosCollection().remove(vehiculosCollectionNewVehiculos);
                        oldColoresOfVehiculosCollectionNewVehiculos = em.merge(oldColoresOfVehiculosCollectionNewVehiculos);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = colores.getColor();
                if (findColores(id) == null) {
                    throw new NonexistentEntityException("The colores with id " + id + " no longer exists.");
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
            Colores colores;
            try {
                colores = em.getReference(Colores.class, id);
                colores.getColor();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The colores with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Vehiculos> vehiculosCollectionOrphanCheck = colores.getVehiculosCollection();
            for (Vehiculos vehiculosCollectionOrphanCheckVehiculos : vehiculosCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This Colores (" + colores + ") cannot be destroyed since the Vehiculos " + vehiculosCollectionOrphanCheckVehiculos + " in its vehiculosCollection field has a non-nullable colores field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(colores);
            em.getTransaction().commit();
        } finally {
            
        }
    }

    public List<Colores> findColoresEntities() {
        return findColoresEntities(true, -1, -1);
    }

    public List<Colores> findColoresEntities(int maxResults, int firstResult) {
        return findColoresEntities(false, maxResults, firstResult);
    }

    private List<Colores> findColoresEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Colores.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
           
        }
    }

    public Colores findColores(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Colores.class, id);
        } finally {
           
        }
    }

    public int getColoresCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Colores> rt = cq.from(Colores.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
           
        }
    }

}
