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
public class LineasVehiculosJpaController {

    public EntityManager getEntityManager() {
        return PersistenceController.getEntityManager();
    }

    public void create(LineasVehiculos lineasVehiculos) throws PreexistingEntityException, Exception {
        if (lineasVehiculos.getVehiculosCollection() == null) {
            lineasVehiculos.setVehiculosCollection(new ArrayList<Vehiculos>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Marcas marcas = lineasVehiculos.getMarcas();
            if (marcas != null) {
                marcas = em.getReference(marcas.getClass(), marcas.getCarmark());
                lineasVehiculos.setMarcas(marcas);
            }
            Collection<Vehiculos> attachedVehiculosCollection = new ArrayList<>();
            for (Vehiculos vehiculosCollectionVehiculosToAttach : lineasVehiculos.getVehiculosCollection()) {
                vehiculosCollectionVehiculosToAttach = em.getReference(vehiculosCollectionVehiculosToAttach.getClass(), vehiculosCollectionVehiculosToAttach.getCar());
                attachedVehiculosCollection.add(vehiculosCollectionVehiculosToAttach);
            }
            lineasVehiculos.setVehiculosCollection(attachedVehiculosCollection);
            em.persist(lineasVehiculos);
            if (marcas != null) {
                marcas.getLineasVehiculosCollection().add(lineasVehiculos);
                marcas = em.merge(marcas);
            }
            for (Vehiculos vehiculosCollectionVehiculos : lineasVehiculos.getVehiculosCollection()) {
                LineasVehiculos oldLineasVehiculosOfVehiculosCollectionVehiculos = vehiculosCollectionVehiculos.getLineasVehiculos();
                vehiculosCollectionVehiculos.setLineasVehiculos(lineasVehiculos);
                vehiculosCollectionVehiculos = em.merge(vehiculosCollectionVehiculos);
                if (oldLineasVehiculosOfVehiculosCollectionVehiculos != null) {
                    oldLineasVehiculosOfVehiculosCollectionVehiculos.getVehiculosCollection().remove(vehiculosCollectionVehiculos);
                    oldLineasVehiculosOfVehiculosCollectionVehiculos = em.merge(oldLineasVehiculosOfVehiculosCollectionVehiculos);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findLineasVehiculos(lineasVehiculos.getCarline()) != null) {
                throw new PreexistingEntityException("LineasVehiculos " + lineasVehiculos + " already exists.", ex);
            }
            throw ex;
        } finally {
            
        }
    }

    public void edit(LineasVehiculos lineasVehiculos) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            LineasVehiculos persistentLineasVehiculos = em.find(LineasVehiculos.class, lineasVehiculos.getCarline());
            Marcas marcasOld = persistentLineasVehiculos.getMarcas();
            Marcas marcasNew = lineasVehiculos.getMarcas();
            Collection<Vehiculos> vehiculosCollectionOld = persistentLineasVehiculos.getVehiculosCollection();
            Collection<Vehiculos> vehiculosCollectionNew = lineasVehiculos.getVehiculosCollection();
            List<String> illegalOrphanMessages = null;
            for (Vehiculos vehiculosCollectionOldVehiculos : vehiculosCollectionOld) {
                if (!vehiculosCollectionNew.contains(vehiculosCollectionOldVehiculos)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain Vehiculos " + vehiculosCollectionOldVehiculos + " since its lineasVehiculos field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (marcasNew != null) {
                marcasNew = em.getReference(marcasNew.getClass(), marcasNew.getCarmark());
                lineasVehiculos.setMarcas(marcasNew);
            }
            Collection<Vehiculos> attachedVehiculosCollectionNew = new ArrayList<>();
            for (Vehiculos vehiculosCollectionNewVehiculosToAttach : vehiculosCollectionNew) {
                vehiculosCollectionNewVehiculosToAttach = em.getReference(vehiculosCollectionNewVehiculosToAttach.getClass(), vehiculosCollectionNewVehiculosToAttach.getCar());
                attachedVehiculosCollectionNew.add(vehiculosCollectionNewVehiculosToAttach);
            }
            vehiculosCollectionNew = attachedVehiculosCollectionNew;
            lineasVehiculos.setVehiculosCollection(vehiculosCollectionNew);
            lineasVehiculos = em.merge(lineasVehiculos);
            if (marcasOld != null && !marcasOld.equals(marcasNew)) {
                marcasOld.getLineasVehiculosCollection().remove(lineasVehiculos);
                marcasOld = em.merge(marcasOld);
            }
            if (marcasNew != null && !marcasNew.equals(marcasOld)) {
                marcasNew.getLineasVehiculosCollection().add(lineasVehiculos);
                marcasNew = em.merge(marcasNew);
            }
            for (Vehiculos vehiculosCollectionNewVehiculos : vehiculosCollectionNew) {
                if (!vehiculosCollectionOld.contains(vehiculosCollectionNewVehiculos)) {
                    LineasVehiculos oldLineasVehiculosOfVehiculosCollectionNewVehiculos = vehiculosCollectionNewVehiculos.getLineasVehiculos();
                    vehiculosCollectionNewVehiculos.setLineasVehiculos(lineasVehiculos);
                    vehiculosCollectionNewVehiculos = em.merge(vehiculosCollectionNewVehiculos);
                    if (oldLineasVehiculosOfVehiculosCollectionNewVehiculos != null && !oldLineasVehiculosOfVehiculosCollectionNewVehiculos.equals(lineasVehiculos)) {
                        oldLineasVehiculosOfVehiculosCollectionNewVehiculos.getVehiculosCollection().remove(vehiculosCollectionNewVehiculos);
                        oldLineasVehiculosOfVehiculosCollectionNewVehiculos = em.merge(oldLineasVehiculosOfVehiculosCollectionNewVehiculos);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = lineasVehiculos.getCarline();
                if (findLineasVehiculos(id) == null) {
                    throw new NonexistentEntityException("The lineasVehiculos with id " + id + " no longer exists.");
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
            LineasVehiculos lineasVehiculos;
            try {
                lineasVehiculos = em.getReference(LineasVehiculos.class, id);
                lineasVehiculos.getCarline();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The lineasVehiculos with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Vehiculos> vehiculosCollectionOrphanCheck = lineasVehiculos.getVehiculosCollection();
            for (Vehiculos vehiculosCollectionOrphanCheckVehiculos : vehiculosCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This LineasVehiculos (" + lineasVehiculos + ") cannot be destroyed since the Vehiculos " + vehiculosCollectionOrphanCheckVehiculos + " in its vehiculosCollection field has a non-nullable lineasVehiculos field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Marcas marcas = lineasVehiculos.getMarcas();
            if (marcas != null) {
                marcas.getLineasVehiculosCollection().remove(lineasVehiculos);
                marcas = em.merge(marcas);
            }
            em.remove(lineasVehiculos);
            em.getTransaction().commit();
        } finally {
           
        }
    }

    public List<LineasVehiculos> findLineasVehiculosEntities() {
        return findLineasVehiculosEntities(true, -1, -1);
    }

    public List<LineasVehiculos> findLineasVehiculosEntities(int maxResults, int firstResult) {
        return findLineasVehiculosEntities(false, maxResults, firstResult);
    }

    private List<LineasVehiculos> findLineasVehiculosEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(LineasVehiculos.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
          
        }
    }

    public LineasVehiculos findLineasVehiculos(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(LineasVehiculos.class, id);
        } finally {
          
        }
    }

    public int getLineasVehiculosCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<LineasVehiculos> rt = cq.from(LineasVehiculos.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
          
        }
    }

}
