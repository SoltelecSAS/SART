/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.dao;

import com.soltelec.dao.conexion.PersistenceController;
import com.soltelec.dao.exceptions.IllegalOrphanException;
import com.soltelec.dao.exceptions.NonexistentEntityException;
import com.soltelec.dao.exceptions.PreexistingEntityException;
import com.soltelec.model.TiposGasolina;
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
public class TiposGasolinaJpaController {

    public EntityManager getEntityManager() {
        return PersistenceController.getEntityManager();
    }

    public void create(TiposGasolina tiposGasolina) throws PreexistingEntityException, Exception {
        if (tiposGasolina.getVehiculosCollection() == null) {
            tiposGasolina.setVehiculosCollection(new ArrayList<Vehiculos>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Vehiculos> attachedVehiculosCollection = new ArrayList<>();
            for (Vehiculos vehiculosCollectionVehiculosToAttach : tiposGasolina.getVehiculosCollection()) {
                vehiculosCollectionVehiculosToAttach = em.getReference(vehiculosCollectionVehiculosToAttach.getClass(), vehiculosCollectionVehiculosToAttach.getCar());
                attachedVehiculosCollection.add(vehiculosCollectionVehiculosToAttach);
            }
            tiposGasolina.setVehiculosCollection(attachedVehiculosCollection);
            em.persist(tiposGasolina);
            for (Vehiculos vehiculosCollectionVehiculos : tiposGasolina.getVehiculosCollection()) {
                TiposGasolina oldTiposGasolinaOfVehiculosCollectionVehiculos = vehiculosCollectionVehiculos.getTiposGasolina();
                vehiculosCollectionVehiculos.setTiposGasolina(tiposGasolina);
                vehiculosCollectionVehiculos = em.merge(vehiculosCollectionVehiculos);
                if (oldTiposGasolinaOfVehiculosCollectionVehiculos != null) {
                    oldTiposGasolinaOfVehiculosCollectionVehiculos.getVehiculosCollection().remove(vehiculosCollectionVehiculos);
                    oldTiposGasolinaOfVehiculosCollectionVehiculos = em.merge(oldTiposGasolinaOfVehiculosCollectionVehiculos);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findTiposGasolina(tiposGasolina.getFueltype()) != null) {
                throw new PreexistingEntityException("TiposGasolina " + tiposGasolina + " already exists.", ex);
            }
            throw ex;
        } finally {
           
        }
    }

    public void edit(TiposGasolina tiposGasolina) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TiposGasolina persistentTiposGasolina = em.find(TiposGasolina.class, tiposGasolina.getFueltype());
            Collection<Vehiculos> vehiculosCollectionOld = persistentTiposGasolina.getVehiculosCollection();
            Collection<Vehiculos> vehiculosCollectionNew = tiposGasolina.getVehiculosCollection();
            List<String> illegalOrphanMessages = null;
            for (Vehiculos vehiculosCollectionOldVehiculos : vehiculosCollectionOld) {
                if (!vehiculosCollectionNew.contains(vehiculosCollectionOldVehiculos)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain Vehiculos " + vehiculosCollectionOldVehiculos + " since its tiposGasolina field is not nullable.");
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
            tiposGasolina.setVehiculosCollection(vehiculosCollectionNew);
            tiposGasolina = em.merge(tiposGasolina);
            for (Vehiculos vehiculosCollectionNewVehiculos : vehiculosCollectionNew) {
                if (!vehiculosCollectionOld.contains(vehiculosCollectionNewVehiculos)) {
                    TiposGasolina oldTiposGasolinaOfVehiculosCollectionNewVehiculos = vehiculosCollectionNewVehiculos.getTiposGasolina();
                    vehiculosCollectionNewVehiculos.setTiposGasolina(tiposGasolina);
                    vehiculosCollectionNewVehiculos = em.merge(vehiculosCollectionNewVehiculos);
                    if (oldTiposGasolinaOfVehiculosCollectionNewVehiculos != null && !oldTiposGasolinaOfVehiculosCollectionNewVehiculos.equals(tiposGasolina)) {
                        oldTiposGasolinaOfVehiculosCollectionNewVehiculos.getVehiculosCollection().remove(vehiculosCollectionNewVehiculos);
                        oldTiposGasolinaOfVehiculosCollectionNewVehiculos = em.merge(oldTiposGasolinaOfVehiculosCollectionNewVehiculos);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = tiposGasolina.getFueltype();
                if (findTiposGasolina(id) == null) {
                    throw new NonexistentEntityException("The tiposGasolina with id " + id + " no longer exists.");
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
            TiposGasolina tiposGasolina;
            try {
                tiposGasolina = em.getReference(TiposGasolina.class, id);
                tiposGasolina.getFueltype();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The tiposGasolina with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Vehiculos> vehiculosCollectionOrphanCheck = tiposGasolina.getVehiculosCollection();
            for (Vehiculos vehiculosCollectionOrphanCheckVehiculos : vehiculosCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This TiposGasolina (" + tiposGasolina + ") cannot be destroyed since the Vehiculos " + vehiculosCollectionOrphanCheckVehiculos + " in its vehiculosCollection field has a non-nullable tiposGasolina field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(tiposGasolina);
            em.getTransaction().commit();
        } finally {
            
        }
    }

    public List<TiposGasolina> findTiposGasolinaEntities() {
        return findTiposGasolinaEntities(true, -1, -1);
    }

    public List<TiposGasolina> findTiposGasolinaEntities(int maxResults, int firstResult) {
        return findTiposGasolinaEntities(false, maxResults, firstResult);
    }

    private List<TiposGasolina> findTiposGasolinaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(TiposGasolina.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
          
        }
    }

    public TiposGasolina findTiposGasolina(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(TiposGasolina.class, id);
        } finally {
          
        }
    }

    public int getTiposGasolinaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<TiposGasolina> rt = cq.from(TiposGasolina.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
          
        }
    }

}
