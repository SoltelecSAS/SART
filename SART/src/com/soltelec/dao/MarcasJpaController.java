/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.dao;

import com.soltelec.dao.conexion.PersistenceController;
import com.soltelec.dao.exceptions.IllegalOrphanException;
import com.soltelec.dao.exceptions.NonexistentEntityException;
import com.soltelec.dao.exceptions.PreexistingEntityException;
import com.soltelec.model.LineasVehiculos;
import com.soltelec.model.Marcas;
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
public class MarcasJpaController {

    public EntityManager getEntityManager() {
        return PersistenceController.getEntityManager();
    }

    public void create(Marcas marcas) throws PreexistingEntityException, Exception {
        if (marcas.getVehiculosCollection() == null) {
            marcas.setVehiculosCollection(new ArrayList<Vehiculos>());
        }
        if (marcas.getLineasVehiculosCollection() == null) {
            marcas.setLineasVehiculosCollection(new ArrayList<LineasVehiculos>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Vehiculos> attachedVehiculosCollection = new ArrayList<>();
            for (Vehiculos vehiculosCollectionVehiculosToAttach : marcas.getVehiculosCollection()) {
                vehiculosCollectionVehiculosToAttach = em.getReference(vehiculosCollectionVehiculosToAttach.getClass(), vehiculosCollectionVehiculosToAttach.getCar());
                attachedVehiculosCollection.add(vehiculosCollectionVehiculosToAttach);
            }
            marcas.setVehiculosCollection(attachedVehiculosCollection);
            Collection<LineasVehiculos> attachedLineasVehiculosCollection = new ArrayList<>();
            for (LineasVehiculos lineasVehiculosCollectionLineasVehiculosToAttach : marcas.getLineasVehiculosCollection()) {
                lineasVehiculosCollectionLineasVehiculosToAttach = em.getReference(lineasVehiculosCollectionLineasVehiculosToAttach.getClass(), lineasVehiculosCollectionLineasVehiculosToAttach.getCarline());
                attachedLineasVehiculosCollection.add(lineasVehiculosCollectionLineasVehiculosToAttach);
            }
            marcas.setLineasVehiculosCollection(attachedLineasVehiculosCollection);
            em.persist(marcas);
            for (Vehiculos vehiculosCollectionVehiculos : marcas.getVehiculosCollection()) {
                Marcas oldMarcasOfVehiculosCollectionVehiculos = vehiculosCollectionVehiculos.getMarcas();
                vehiculosCollectionVehiculos.setMarcas(marcas);
                vehiculosCollectionVehiculos = em.merge(vehiculosCollectionVehiculos);
                if (oldMarcasOfVehiculosCollectionVehiculos != null) {
                    oldMarcasOfVehiculosCollectionVehiculos.getVehiculosCollection().remove(vehiculosCollectionVehiculos);
                    oldMarcasOfVehiculosCollectionVehiculos = em.merge(oldMarcasOfVehiculosCollectionVehiculos);
                }
            }
            for (LineasVehiculos lineasVehiculosCollectionLineasVehiculos : marcas.getLineasVehiculosCollection()) {
                Marcas oldMarcasOfLineasVehiculosCollectionLineasVehiculos = lineasVehiculosCollectionLineasVehiculos.getMarcas();
                lineasVehiculosCollectionLineasVehiculos.setMarcas(marcas);
                lineasVehiculosCollectionLineasVehiculos = em.merge(lineasVehiculosCollectionLineasVehiculos);
                if (oldMarcasOfLineasVehiculosCollectionLineasVehiculos != null) {
                    oldMarcasOfLineasVehiculosCollectionLineasVehiculos.getLineasVehiculosCollection().remove(lineasVehiculosCollectionLineasVehiculos);
                    oldMarcasOfLineasVehiculosCollectionLineasVehiculos = em.merge(oldMarcasOfLineasVehiculosCollectionLineasVehiculos);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findMarcas(marcas.getCarmark()) != null) {
                throw new PreexistingEntityException("Marcas " + marcas + " already exists.", ex);
            }
            throw ex;
        } finally {
           
        }
    }

    public void edit(Marcas marcas) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Marcas persistentMarcas = em.find(Marcas.class, marcas.getCarmark());
            Collection<Vehiculos> vehiculosCollectionOld = persistentMarcas.getVehiculosCollection();
            Collection<Vehiculos> vehiculosCollectionNew = marcas.getVehiculosCollection();
            Collection<LineasVehiculos> lineasVehiculosCollectionOld = persistentMarcas.getLineasVehiculosCollection();
            Collection<LineasVehiculos> lineasVehiculosCollectionNew = marcas.getLineasVehiculosCollection();
            List<String> illegalOrphanMessages = null;
            for (LineasVehiculos lineasVehiculosCollectionOldLineasVehiculos : lineasVehiculosCollectionOld) {
                if (!lineasVehiculosCollectionNew.contains(lineasVehiculosCollectionOldLineasVehiculos)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain LineasVehiculos " + lineasVehiculosCollectionOldLineasVehiculos + " since its marcas field is not nullable.");
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
            marcas.setVehiculosCollection(vehiculosCollectionNew);
            Collection<LineasVehiculos> attachedLineasVehiculosCollectionNew = new ArrayList<>();
            for (LineasVehiculos lineasVehiculosCollectionNewLineasVehiculosToAttach : lineasVehiculosCollectionNew) {
                lineasVehiculosCollectionNewLineasVehiculosToAttach = em.getReference(lineasVehiculosCollectionNewLineasVehiculosToAttach.getClass(), lineasVehiculosCollectionNewLineasVehiculosToAttach.getCarline());
                attachedLineasVehiculosCollectionNew.add(lineasVehiculosCollectionNewLineasVehiculosToAttach);
            }
            lineasVehiculosCollectionNew = attachedLineasVehiculosCollectionNew;
            marcas.setLineasVehiculosCollection(lineasVehiculosCollectionNew);
            marcas = em.merge(marcas);
            for (Vehiculos vehiculosCollectionOldVehiculos : vehiculosCollectionOld) {
                if (!vehiculosCollectionNew.contains(vehiculosCollectionOldVehiculos)) {
                    vehiculosCollectionOldVehiculos.setMarcas(null);
                    vehiculosCollectionOldVehiculos = em.merge(vehiculosCollectionOldVehiculos);
                }
            }
            for (Vehiculos vehiculosCollectionNewVehiculos : vehiculosCollectionNew) {
                if (!vehiculosCollectionOld.contains(vehiculosCollectionNewVehiculos)) {
                    Marcas oldMarcasOfVehiculosCollectionNewVehiculos = vehiculosCollectionNewVehiculos.getMarcas();
                    vehiculosCollectionNewVehiculos.setMarcas(marcas);
                    vehiculosCollectionNewVehiculos = em.merge(vehiculosCollectionNewVehiculos);
                    if (oldMarcasOfVehiculosCollectionNewVehiculos != null && !oldMarcasOfVehiculosCollectionNewVehiculos.equals(marcas)) {
                        oldMarcasOfVehiculosCollectionNewVehiculos.getVehiculosCollection().remove(vehiculosCollectionNewVehiculos);
                        oldMarcasOfVehiculosCollectionNewVehiculos = em.merge(oldMarcasOfVehiculosCollectionNewVehiculos);
                    }
                }
            }
            for (LineasVehiculos lineasVehiculosCollectionNewLineasVehiculos : lineasVehiculosCollectionNew) {
                if (!lineasVehiculosCollectionOld.contains(lineasVehiculosCollectionNewLineasVehiculos)) {
                    Marcas oldMarcasOfLineasVehiculosCollectionNewLineasVehiculos = lineasVehiculosCollectionNewLineasVehiculos.getMarcas();
                    lineasVehiculosCollectionNewLineasVehiculos.setMarcas(marcas);
                    lineasVehiculosCollectionNewLineasVehiculos = em.merge(lineasVehiculosCollectionNewLineasVehiculos);
                    if (oldMarcasOfLineasVehiculosCollectionNewLineasVehiculos != null && !oldMarcasOfLineasVehiculosCollectionNewLineasVehiculos.equals(marcas)) {
                        oldMarcasOfLineasVehiculosCollectionNewLineasVehiculos.getLineasVehiculosCollection().remove(lineasVehiculosCollectionNewLineasVehiculos);
                        oldMarcasOfLineasVehiculosCollectionNewLineasVehiculos = em.merge(oldMarcasOfLineasVehiculosCollectionNewLineasVehiculos);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = marcas.getCarmark();
                if (findMarcas(id) == null) {
                    throw new NonexistentEntityException("The marcas with id " + id + " no longer exists.");
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
            Marcas marcas;
            try {
                marcas = em.getReference(Marcas.class, id);
                marcas.getCarmark();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The marcas with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<LineasVehiculos> lineasVehiculosCollectionOrphanCheck = marcas.getLineasVehiculosCollection();
            for (LineasVehiculos lineasVehiculosCollectionOrphanCheckLineasVehiculos : lineasVehiculosCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This Marcas (" + marcas + ") cannot be destroyed since the LineasVehiculos " + lineasVehiculosCollectionOrphanCheckLineasVehiculos + " in its lineasVehiculosCollection field has a non-nullable marcas field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Vehiculos> vehiculosCollection = marcas.getVehiculosCollection();
            for (Vehiculos vehiculosCollectionVehiculos : vehiculosCollection) {
                vehiculosCollectionVehiculos.setMarcas(null);
                vehiculosCollectionVehiculos = em.merge(vehiculosCollectionVehiculos);
            }
            em.remove(marcas);
            em.getTransaction().commit();
        } finally {
          
        }
    }

    public List<Marcas> findMarcasEntities() {
        return findMarcasEntities(true, -1, -1);
    }

    public List<Marcas> findMarcasEntities(int maxResults, int firstResult) {
        return findMarcasEntities(false, maxResults, firstResult);
    }

    private List<Marcas> findMarcasEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Marcas.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
           
        }
    }

    public Marcas findMarcas(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Marcas.class, id);
        } finally {
           
        }
    }

    public int getMarcasCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Marcas> rt = cq.from(Marcas.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
          
        }
    }

}
