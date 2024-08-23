/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.dao;

import com.soltelec.dao.conexion.PersistenceController;
import com.soltelec.dao.exceptions.IllegalOrphanException;
import com.soltelec.dao.exceptions.NonexistentEntityException;
import com.soltelec.dao.exceptions.PreexistingEntityException;
import com.soltelec.model.Servicios;
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
public class ServiciosJpaController {

    public EntityManager getEntityManager() {
        return PersistenceController.getEntityManager();
    }

    public void create(Servicios servicios) throws PreexistingEntityException, Exception {
        if (servicios.getVehiculosCollection() == null) {
            servicios.setVehiculosCollection(new ArrayList<Vehiculos>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Vehiculos> attachedVehiculosCollection = new ArrayList<>();
            for (Vehiculos vehiculosCollectionVehiculosToAttach : servicios.getVehiculosCollection()) {
                vehiculosCollectionVehiculosToAttach = em.getReference(vehiculosCollectionVehiculosToAttach.getClass(), vehiculosCollectionVehiculosToAttach.getCar());
                attachedVehiculosCollection.add(vehiculosCollectionVehiculosToAttach);
            }
            servicios.setVehiculosCollection(attachedVehiculosCollection);
            em.persist(servicios);
            for (Vehiculos vehiculosCollectionVehiculos : servicios.getVehiculosCollection()) {
                Servicios oldServiciosOfVehiculosCollectionVehiculos = vehiculosCollectionVehiculos.getServicios();
                vehiculosCollectionVehiculos.setServicios(servicios);
                vehiculosCollectionVehiculos = em.merge(vehiculosCollectionVehiculos);
                if (oldServiciosOfVehiculosCollectionVehiculos != null) {
                    oldServiciosOfVehiculosCollectionVehiculos.getVehiculosCollection().remove(vehiculosCollectionVehiculos);
                    oldServiciosOfVehiculosCollectionVehiculos = em.merge(oldServiciosOfVehiculosCollectionVehiculos);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findServicios(servicios.getService()) != null) {
                throw new PreexistingEntityException("Servicios " + servicios + " already exists.", ex);
            }
            throw ex;
        } finally {
           
        }
    }

    public void edit(Servicios servicios) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Servicios persistentServicios = em.find(Servicios.class, servicios.getService());
            Collection<Vehiculos> vehiculosCollectionOld = persistentServicios.getVehiculosCollection();
            Collection<Vehiculos> vehiculosCollectionNew = servicios.getVehiculosCollection();
            List<String> illegalOrphanMessages = null;
            for (Vehiculos vehiculosCollectionOldVehiculos : vehiculosCollectionOld) {
                if (!vehiculosCollectionNew.contains(vehiculosCollectionOldVehiculos)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain Vehiculos " + vehiculosCollectionOldVehiculos + " since its servicios field is not nullable.");
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
            servicios.setVehiculosCollection(vehiculosCollectionNew);
            servicios = em.merge(servicios);
            for (Vehiculos vehiculosCollectionNewVehiculos : vehiculosCollectionNew) {
                if (!vehiculosCollectionOld.contains(vehiculosCollectionNewVehiculos)) {
                    Servicios oldServiciosOfVehiculosCollectionNewVehiculos = vehiculosCollectionNewVehiculos.getServicios();
                    vehiculosCollectionNewVehiculos.setServicios(servicios);
                    vehiculosCollectionNewVehiculos = em.merge(vehiculosCollectionNewVehiculos);
                    if (oldServiciosOfVehiculosCollectionNewVehiculos != null && !oldServiciosOfVehiculosCollectionNewVehiculos.equals(servicios)) {
                        oldServiciosOfVehiculosCollectionNewVehiculos.getVehiculosCollection().remove(vehiculosCollectionNewVehiculos);
                        oldServiciosOfVehiculosCollectionNewVehiculos = em.merge(oldServiciosOfVehiculosCollectionNewVehiculos);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = servicios.getService();
                if (findServicios(id) == null) {
                    throw new NonexistentEntityException("The servicios with id " + id + " no longer exists.");
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
            Servicios servicios;
            try {
                servicios = em.getReference(Servicios.class, id);
                servicios.getService();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The servicios with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Vehiculos> vehiculosCollectionOrphanCheck = servicios.getVehiculosCollection();
            for (Vehiculos vehiculosCollectionOrphanCheckVehiculos : vehiculosCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This Servicios (" + servicios + ") cannot be destroyed since the Vehiculos " + vehiculosCollectionOrphanCheckVehiculos + " in its vehiculosCollection field has a non-nullable servicios field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(servicios);
            em.getTransaction().commit();
        } finally {
           
        }
    }

    public List<Servicios> findServiciosEntities() {
        return findServiciosEntities(true, -1, -1);
    }

    public List<Servicios> findServiciosEntities(int maxResults, int firstResult) {
        return findServiciosEntities(false, maxResults, firstResult);
    }

    private List<Servicios> findServiciosEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Servicios.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
          
        }
    }

    public Servicios findServicios(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Servicios.class, id);
        } finally {
           
        }
    }

    public int getServiciosCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Servicios> rt = cq.from(Servicios.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
           
        }
    }

}
