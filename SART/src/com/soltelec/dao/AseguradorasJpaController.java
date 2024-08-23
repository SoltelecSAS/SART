/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.dao;

import com.soltelec.dao.conexion.PersistenceController;
import com.soltelec.dao.exceptions.IllegalOrphanException;
import com.soltelec.dao.exceptions.NonexistentEntityException;
import com.soltelec.dao.exceptions.PreexistingEntityException;
import com.soltelec.model.Aseguradoras;
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
public class AseguradorasJpaController {
    
    public EntityManager getEntityManager() {
        return PersistenceController.getEntityManager();
    }

    public void create(Aseguradoras aseguradoras) throws PreexistingEntityException, Exception {
        if (aseguradoras.getVehiculosCollection() == null) {
            aseguradoras.setVehiculosCollection(new ArrayList<Vehiculos>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Vehiculos> attachedVehiculosCollection = new ArrayList<>();
            for (Vehiculos vehiculosCollectionVehiculosToAttach : aseguradoras.getVehiculosCollection()) {
                vehiculosCollectionVehiculosToAttach = em.getReference(vehiculosCollectionVehiculosToAttach.getClass(), vehiculosCollectionVehiculosToAttach.getCar());
                attachedVehiculosCollection.add(vehiculosCollectionVehiculosToAttach);
            }
            aseguradoras.setVehiculosCollection(attachedVehiculosCollection);
            em.persist(aseguradoras);
            for (Vehiculos vehiculosCollectionVehiculos : aseguradoras.getVehiculosCollection()) {
                Aseguradoras oldAseguradorasOfVehiculosCollectionVehiculos = vehiculosCollectionVehiculos.getAseguradoras();
                vehiculosCollectionVehiculos.setAseguradoras(aseguradoras);
                vehiculosCollectionVehiculos = em.merge(vehiculosCollectionVehiculos);
                if (oldAseguradorasOfVehiculosCollectionVehiculos != null) {
                    oldAseguradorasOfVehiculosCollectionVehiculos.getVehiculosCollection().remove(vehiculosCollectionVehiculos);
                    oldAseguradorasOfVehiculosCollectionVehiculos = em.merge(oldAseguradorasOfVehiculosCollectionVehiculos);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findAseguradoras(aseguradoras.getInsuring()) != null) {
                throw new PreexistingEntityException("Aseguradoras " + aseguradoras + " already exists.", ex);
            }
            throw ex;
        } finally {
            
        }
    }

    public void edit(Aseguradoras aseguradoras) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Aseguradoras persistentAseguradoras = em.find(Aseguradoras.class, aseguradoras.getInsuring());
            Collection<Vehiculos> vehiculosCollectionOld = persistentAseguradoras.getVehiculosCollection();
            Collection<Vehiculos> vehiculosCollectionNew = aseguradoras.getVehiculosCollection();
            List<String> illegalOrphanMessages = null;
            for (Vehiculos vehiculosCollectionOldVehiculos : vehiculosCollectionOld) {
                if (!vehiculosCollectionNew.contains(vehiculosCollectionOldVehiculos)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain Vehiculos " + vehiculosCollectionOldVehiculos + " since its aseguradoras field is not nullable.");
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
            aseguradoras.setVehiculosCollection(vehiculosCollectionNew);
            aseguradoras = em.merge(aseguradoras);
            for (Vehiculos vehiculosCollectionNewVehiculos : vehiculosCollectionNew) {
                if (!vehiculosCollectionOld.contains(vehiculosCollectionNewVehiculos)) {
                    Aseguradoras oldAseguradorasOfVehiculosCollectionNewVehiculos = vehiculosCollectionNewVehiculos.getAseguradoras();
                    vehiculosCollectionNewVehiculos.setAseguradoras(aseguradoras);
                    vehiculosCollectionNewVehiculos = em.merge(vehiculosCollectionNewVehiculos);
                    if (oldAseguradorasOfVehiculosCollectionNewVehiculos != null && !oldAseguradorasOfVehiculosCollectionNewVehiculos.equals(aseguradoras)) {
                        oldAseguradorasOfVehiculosCollectionNewVehiculos.getVehiculosCollection().remove(vehiculosCollectionNewVehiculos);
                        oldAseguradorasOfVehiculosCollectionNewVehiculos = em.merge(oldAseguradorasOfVehiculosCollectionNewVehiculos);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = aseguradoras.getInsuring();
                if (findAseguradoras(id) == null) {
                    throw new NonexistentEntityException("The aseguradoras with id " + id + " no longer exists.");
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
            Aseguradoras aseguradoras;
            try {
                aseguradoras = em.getReference(Aseguradoras.class, id);
                aseguradoras.getInsuring();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The aseguradoras with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Vehiculos> vehiculosCollectionOrphanCheck = aseguradoras.getVehiculosCollection();
            for (Vehiculos vehiculosCollectionOrphanCheckVehiculos : vehiculosCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This Aseguradoras (" + aseguradoras + ") cannot be destroyed since the Vehiculos " + vehiculosCollectionOrphanCheckVehiculos + " in its vehiculosCollection field has a non-nullable aseguradoras field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(aseguradoras);
            em.getTransaction().commit();
        } finally {
           
        }
    }

    public List<Aseguradoras> findAseguradorasEntities() {
        return findAseguradorasEntities(true, -1, -1);
    }

    public List<Aseguradoras> findAseguradorasEntities(int maxResults, int firstResult) {
        return findAseguradorasEntities(false, maxResults, firstResult);
    }

    private List<Aseguradoras> findAseguradorasEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Aseguradoras.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            
        }
    }

    public Aseguradoras findAseguradoras(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Aseguradoras.class, id);
        } finally {
           
        }
    }

    public int getAseguradorasCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Aseguradoras> rt = cq.from(Aseguradoras.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
           
        }
    }

}
