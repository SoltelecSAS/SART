/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.dao;

import com.soltelec.dao.conexion.PersistenceController;
import com.soltelec.dao.exceptions.IllegalOrphanException;
import com.soltelec.dao.exceptions.NonexistentEntityException;
import com.soltelec.dao.exceptions.PreexistingEntityException;
import com.soltelec.model.TipoVehiculo;
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
public class TipoVehiculoJpaController {

    public EntityManager getEntityManager() {
        return PersistenceController.getEntityManager();
    }

    public void create(TipoVehiculo tipoVehiculo) throws PreexistingEntityException, Exception {
        if (tipoVehiculo.getVehiculosCollection() == null) {
            tipoVehiculo.setVehiculosCollection(new ArrayList<Vehiculos>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Vehiculos> attachedVehiculosCollection = new ArrayList<>();
            for (Vehiculos vehiculosCollectionVehiculosToAttach : tipoVehiculo.getVehiculosCollection()) {
                vehiculosCollectionVehiculosToAttach = em.getReference(vehiculosCollectionVehiculosToAttach.getClass(), vehiculosCollectionVehiculosToAttach.getCar());
                attachedVehiculosCollection.add(vehiculosCollectionVehiculosToAttach);
            }
            tipoVehiculo.setVehiculosCollection(attachedVehiculosCollection);
            em.persist(tipoVehiculo);
            for (Vehiculos vehiculosCollectionVehiculos : tipoVehiculo.getVehiculosCollection()) {
                TipoVehiculo oldTipoVehiculoOfVehiculosCollectionVehiculos = vehiculosCollectionVehiculos.getTipoVehiculo();
                vehiculosCollectionVehiculos.setTipoVehiculo(tipoVehiculo);
                vehiculosCollectionVehiculos = em.merge(vehiculosCollectionVehiculos);
                if (oldTipoVehiculoOfVehiculosCollectionVehiculos != null) {
                    oldTipoVehiculoOfVehiculosCollectionVehiculos.getVehiculosCollection().remove(vehiculosCollectionVehiculos);
                    oldTipoVehiculoOfVehiculosCollectionVehiculos = em.merge(oldTipoVehiculoOfVehiculosCollectionVehiculos);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findTipoVehiculo(tipoVehiculo.getCartype()) != null) {
                throw new PreexistingEntityException("TipoVehiculo " + tipoVehiculo + " already exists.", ex);
            }
            throw ex;
        } finally {
          
        }
    }

    public void edit(TipoVehiculo tipoVehiculo) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TipoVehiculo persistentTipoVehiculo = em.find(TipoVehiculo.class, tipoVehiculo.getCartype());
            Collection<Vehiculos> vehiculosCollectionOld = persistentTipoVehiculo.getVehiculosCollection();
            Collection<Vehiculos> vehiculosCollectionNew = tipoVehiculo.getVehiculosCollection();
            List<String> illegalOrphanMessages = null;
            for (Vehiculos vehiculosCollectionOldVehiculos : vehiculosCollectionOld) {
                if (!vehiculosCollectionNew.contains(vehiculosCollectionOldVehiculos)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain Vehiculos " + vehiculosCollectionOldVehiculos + " since its tipoVehiculo field is not nullable.");
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
            tipoVehiculo.setVehiculosCollection(vehiculosCollectionNew);
            tipoVehiculo = em.merge(tipoVehiculo);
            for (Vehiculos vehiculosCollectionNewVehiculos : vehiculosCollectionNew) {
                if (!vehiculosCollectionOld.contains(vehiculosCollectionNewVehiculos)) {
                    TipoVehiculo oldTipoVehiculoOfVehiculosCollectionNewVehiculos = vehiculosCollectionNewVehiculos.getTipoVehiculo();
                    vehiculosCollectionNewVehiculos.setTipoVehiculo(tipoVehiculo);
                    vehiculosCollectionNewVehiculos = em.merge(vehiculosCollectionNewVehiculos);
                    if (oldTipoVehiculoOfVehiculosCollectionNewVehiculos != null && !oldTipoVehiculoOfVehiculosCollectionNewVehiculos.equals(tipoVehiculo)) {
                        oldTipoVehiculoOfVehiculosCollectionNewVehiculos.getVehiculosCollection().remove(vehiculosCollectionNewVehiculos);
                        oldTipoVehiculoOfVehiculosCollectionNewVehiculos = em.merge(oldTipoVehiculoOfVehiculosCollectionNewVehiculos);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = tipoVehiculo.getCartype();
                if (findTipoVehiculo(id) == null) {
                    throw new NonexistentEntityException("The tipoVehiculo with id " + id + " no longer exists.");
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
            TipoVehiculo tipoVehiculo;
            try {
                tipoVehiculo = em.getReference(TipoVehiculo.class, id);
                tipoVehiculo.getCartype();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The tipoVehiculo with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Vehiculos> vehiculosCollectionOrphanCheck = tipoVehiculo.getVehiculosCollection();
            for (Vehiculos vehiculosCollectionOrphanCheckVehiculos : vehiculosCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This TipoVehiculo (" + tipoVehiculo + ") cannot be destroyed since the Vehiculos " + vehiculosCollectionOrphanCheckVehiculos + " in its vehiculosCollection field has a non-nullable tipoVehiculo field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(tipoVehiculo);
            em.getTransaction().commit();
        } finally {
           
        }
    }

    public List<TipoVehiculo> findTipoVehiculoEntities() {
        return findTipoVehiculoEntities(true, -1, -1);
    }

    public List<TipoVehiculo> findTipoVehiculoEntities(int maxResults, int firstResult) {
        return findTipoVehiculoEntities(false, maxResults, firstResult);
    }

    private List<TipoVehiculo> findTipoVehiculoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(TipoVehiculo.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
          
        }
    }

    public TipoVehiculo findTipoVehiculo(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(TipoVehiculo.class, id);
        } finally {
           
        }
    }

    public int getTipoVehiculoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<TipoVehiculo> rt = cq.from(TipoVehiculo.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
           
        }
    }

}
