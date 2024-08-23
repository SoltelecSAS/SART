/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.dao;

import com.soltelec.dao.conexion.PersistenceController;
import com.soltelec.dao.exceptions.IllegalOrphanException;
import com.soltelec.dao.exceptions.NonexistentEntityException;
import com.soltelec.dao.exceptions.PreexistingEntityException;
import com.soltelec.model.ClasesVehiculo;
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
public class ClasesVehiculoJpaController {

    public EntityManager getEntityManager() {
        return PersistenceController.getEntityManager();
    }

    public void create(ClasesVehiculo clasesVehiculo) throws PreexistingEntityException, Exception {
        if (clasesVehiculo.getVehiculosCollection() == null) {
            clasesVehiculo.setVehiculosCollection(new ArrayList<Vehiculos>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Vehiculos> attachedVehiculosCollection = new ArrayList<>();
            for (Vehiculos vehiculosCollectionVehiculosToAttach : clasesVehiculo.getVehiculosCollection()) {
                vehiculosCollectionVehiculosToAttach = em.getReference(vehiculosCollectionVehiculosToAttach.getClass(), vehiculosCollectionVehiculosToAttach.getCar());
                attachedVehiculosCollection.add(vehiculosCollectionVehiculosToAttach);
            }
            clasesVehiculo.setVehiculosCollection(attachedVehiculosCollection);
            em.persist(clasesVehiculo);
            for (Vehiculos vehiculosCollectionVehiculos : clasesVehiculo.getVehiculosCollection()) {
                ClasesVehiculo oldClasesVehiculoOfVehiculosCollectionVehiculos = vehiculosCollectionVehiculos.getClasesVehiculo();
                vehiculosCollectionVehiculos.setClasesVehiculo(clasesVehiculo);
                vehiculosCollectionVehiculos = em.merge(vehiculosCollectionVehiculos);
                if (oldClasesVehiculoOfVehiculosCollectionVehiculos != null) {
                    oldClasesVehiculoOfVehiculosCollectionVehiculos.getVehiculosCollection().remove(vehiculosCollectionVehiculos);
                    oldClasesVehiculoOfVehiculosCollectionVehiculos = em.merge(oldClasesVehiculoOfVehiculosCollectionVehiculos);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findClasesVehiculo(clasesVehiculo.getClass1()) != null) {
                throw new PreexistingEntityException("ClasesVehiculo " + clasesVehiculo + " already exists.", ex);
            }
            throw ex;
        } finally {
            
        }
    }

    public void edit(ClasesVehiculo clasesVehiculo) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ClasesVehiculo persistentClasesVehiculo = em.find(ClasesVehiculo.class, clasesVehiculo.getClass1());
            Collection<Vehiculos> vehiculosCollectionOld = persistentClasesVehiculo.getVehiculosCollection();
            Collection<Vehiculos> vehiculosCollectionNew = clasesVehiculo.getVehiculosCollection();
            List<String> illegalOrphanMessages = null;
            for (Vehiculos vehiculosCollectionOldVehiculos : vehiculosCollectionOld) {
                if (!vehiculosCollectionNew.contains(vehiculosCollectionOldVehiculos)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain Vehiculos " + vehiculosCollectionOldVehiculos + " since its clasesVehiculo field is not nullable.");
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
            clasesVehiculo.setVehiculosCollection(vehiculosCollectionNew);
            clasesVehiculo = em.merge(clasesVehiculo);
            for (Vehiculos vehiculosCollectionNewVehiculos : vehiculosCollectionNew) {
                if (!vehiculosCollectionOld.contains(vehiculosCollectionNewVehiculos)) {
                    ClasesVehiculo oldClasesVehiculoOfVehiculosCollectionNewVehiculos = vehiculosCollectionNewVehiculos.getClasesVehiculo();
                    vehiculosCollectionNewVehiculos.setClasesVehiculo(clasesVehiculo);
                    vehiculosCollectionNewVehiculos = em.merge(vehiculosCollectionNewVehiculos);
                    if (oldClasesVehiculoOfVehiculosCollectionNewVehiculos != null && !oldClasesVehiculoOfVehiculosCollectionNewVehiculos.equals(clasesVehiculo)) {
                        oldClasesVehiculoOfVehiculosCollectionNewVehiculos.getVehiculosCollection().remove(vehiculosCollectionNewVehiculos);
                        oldClasesVehiculoOfVehiculosCollectionNewVehiculos = em.merge(oldClasesVehiculoOfVehiculosCollectionNewVehiculos);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = clasesVehiculo.getClass1();
                if (findClasesVehiculo(id) == null) {
                    throw new NonexistentEntityException("The clasesVehiculo with id " + id + " no longer exists.");
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
            ClasesVehiculo clasesVehiculo;
            try {
                clasesVehiculo = em.getReference(ClasesVehiculo.class, id);
                clasesVehiculo.getClass1();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The clasesVehiculo with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Vehiculos> vehiculosCollectionOrphanCheck = clasesVehiculo.getVehiculosCollection();
            for (Vehiculos vehiculosCollectionOrphanCheckVehiculos : vehiculosCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This ClasesVehiculo (" + clasesVehiculo + ") cannot be destroyed since the Vehiculos " + vehiculosCollectionOrphanCheckVehiculos + " in its vehiculosCollection field has a non-nullable clasesVehiculo field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(clasesVehiculo);
            em.getTransaction().commit();
        } finally {
            
        }
    }

    public List<ClasesVehiculo> findClasesVehiculoEntities() {
        return findClasesVehiculoEntities(true, -1, -1);
    }

    public List<ClasesVehiculo> findClasesVehiculoEntities(int maxResults, int firstResult) {
        return findClasesVehiculoEntities(false, maxResults, firstResult);
    }

    private List<ClasesVehiculo> findClasesVehiculoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(ClasesVehiculo.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            
        }
    }

    public ClasesVehiculo findClasesVehiculo(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(ClasesVehiculo.class, id);
        } finally {
           
        }
    }

    public int getClasesVehiculoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<ClasesVehiculo> rt = cq.from(ClasesVehiculo.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            
        }
    }

}
