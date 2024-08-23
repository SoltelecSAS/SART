/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.dao;

import com.soltelec.dao.conexion.PersistenceController;
import com.soltelec.dao.exceptions.IllegalOrphanException;
import com.soltelec.dao.exceptions.NonexistentEntityException;
import com.soltelec.dao.exceptions.PreexistingEntityException;
import com.soltelec.model.HojaPruebas;
import com.soltelec.model.Propietarios;
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
public class PropietariosJpaController {

    public EntityManager getEntityManager() {
        return PersistenceController.getEntityManager();
    }

    public void create(Propietarios propietarios) throws PreexistingEntityException, Exception {
        if (propietarios.getVehiculosCollection() == null) {
            propietarios.setVehiculosCollection(new ArrayList<Vehiculos>());
        }
        if (propietarios.getHojaPruebasCollection() == null) {
            propietarios.setHojaPruebasCollection(new ArrayList<HojaPruebas>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Vehiculos> attachedVehiculosCollection = new ArrayList<>();
            for (Vehiculos vehiculosCollectionVehiculosToAttach : propietarios.getVehiculosCollection()) {
                vehiculosCollectionVehiculosToAttach = em.getReference(vehiculosCollectionVehiculosToAttach.getClass(), vehiculosCollectionVehiculosToAttach.getCar());
                attachedVehiculosCollection.add(vehiculosCollectionVehiculosToAttach);
            }
            propietarios.setVehiculosCollection(attachedVehiculosCollection);
            Collection<HojaPruebas> attachedHojaPruebasCollection = new ArrayList<>();
            for (HojaPruebas hojaPruebasCollectionHojaPruebasToAttach : propietarios.getHojaPruebasCollection()) {
                hojaPruebasCollectionHojaPruebasToAttach = em.getReference(hojaPruebasCollectionHojaPruebasToAttach.getClass(), hojaPruebasCollectionHojaPruebasToAttach.getTestsheet());
                attachedHojaPruebasCollection.add(hojaPruebasCollectionHojaPruebasToAttach);
            }
            propietarios.setHojaPruebasCollection(attachedHojaPruebasCollection);
            em.persist(propietarios);
            for (Vehiculos vehiculosCollectionVehiculos : propietarios.getVehiculosCollection()) {
                Propietarios oldPropietariosOfVehiculosCollectionVehiculos = vehiculosCollectionVehiculos.getPropietarios();
                vehiculosCollectionVehiculos.setPropietarios(propietarios);
                vehiculosCollectionVehiculos = em.merge(vehiculosCollectionVehiculos);
                if (oldPropietariosOfVehiculosCollectionVehiculos != null) {
                    oldPropietariosOfVehiculosCollectionVehiculos.getVehiculosCollection().remove(vehiculosCollectionVehiculos);
                    oldPropietariosOfVehiculosCollectionVehiculos = em.merge(oldPropietariosOfVehiculosCollectionVehiculos);
                }
            }
            for (HojaPruebas hojaPruebasCollectionHojaPruebas : propietarios.getHojaPruebasCollection()) {
                Propietarios oldPropietariosOfHojaPruebasCollectionHojaPruebas = hojaPruebasCollectionHojaPruebas.getPropietarios();
                hojaPruebasCollectionHojaPruebas.setPropietarios(propietarios);
                hojaPruebasCollectionHojaPruebas = em.merge(hojaPruebasCollectionHojaPruebas);
                if (oldPropietariosOfHojaPruebasCollectionHojaPruebas != null) {
                    oldPropietariosOfHojaPruebasCollectionHojaPruebas.getHojaPruebasCollection().remove(hojaPruebasCollectionHojaPruebas);
                    oldPropietariosOfHojaPruebasCollectionHojaPruebas = em.merge(oldPropietariosOfHojaPruebasCollectionHojaPruebas);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findPropietarios(propietarios.getCarowner()) != null) {
                throw new PreexistingEntityException("Propietarios " + propietarios + " already exists.", ex);
            }
            throw ex;
        } finally {
           
        }
    }

    public void edit(Propietarios propietarios) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Propietarios persistentPropietarios = em.find(Propietarios.class, propietarios.getCarowner());
            Collection<Vehiculos> vehiculosCollectionOld = persistentPropietarios.getVehiculosCollection();
            Collection<Vehiculos> vehiculosCollectionNew = propietarios.getVehiculosCollection();
            Collection<HojaPruebas> hojaPruebasCollectionOld = persistentPropietarios.getHojaPruebasCollection();
            Collection<HojaPruebas> hojaPruebasCollectionNew = propietarios.getHojaPruebasCollection();
            List<String> illegalOrphanMessages = null;
            for (Vehiculos vehiculosCollectionOldVehiculos : vehiculosCollectionOld) {
                if (!vehiculosCollectionNew.contains(vehiculosCollectionOldVehiculos)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain Vehiculos " + vehiculosCollectionOldVehiculos + " since its propietarios field is not nullable.");
                }
            }
            for (HojaPruebas hojaPruebasCollectionOldHojaPruebas : hojaPruebasCollectionOld) {
                if (!hojaPruebasCollectionNew.contains(hojaPruebasCollectionOldHojaPruebas)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain HojaPruebas " + hojaPruebasCollectionOldHojaPruebas + " since its propietarios field is not nullable.");
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
            propietarios.setVehiculosCollection(vehiculosCollectionNew);
            Collection<HojaPruebas> attachedHojaPruebasCollectionNew = new ArrayList<>();
            for (HojaPruebas hojaPruebasCollectionNewHojaPruebasToAttach : hojaPruebasCollectionNew) {
                hojaPruebasCollectionNewHojaPruebasToAttach = em.getReference(hojaPruebasCollectionNewHojaPruebasToAttach.getClass(), hojaPruebasCollectionNewHojaPruebasToAttach.getTestsheet());
                attachedHojaPruebasCollectionNew.add(hojaPruebasCollectionNewHojaPruebasToAttach);
            }
            hojaPruebasCollectionNew = attachedHojaPruebasCollectionNew;
            propietarios.setHojaPruebasCollection(hojaPruebasCollectionNew);
            propietarios = em.merge(propietarios);
            for (Vehiculos vehiculosCollectionNewVehiculos : vehiculosCollectionNew) {
                if (!vehiculosCollectionOld.contains(vehiculosCollectionNewVehiculos)) {
                    Propietarios oldPropietariosOfVehiculosCollectionNewVehiculos = vehiculosCollectionNewVehiculos.getPropietarios();
                    vehiculosCollectionNewVehiculos.setPropietarios(propietarios);
                    vehiculosCollectionNewVehiculos = em.merge(vehiculosCollectionNewVehiculos);
                    if (oldPropietariosOfVehiculosCollectionNewVehiculos != null && !oldPropietariosOfVehiculosCollectionNewVehiculos.equals(propietarios)) {
                        oldPropietariosOfVehiculosCollectionNewVehiculos.getVehiculosCollection().remove(vehiculosCollectionNewVehiculos);
                        oldPropietariosOfVehiculosCollectionNewVehiculos = em.merge(oldPropietariosOfVehiculosCollectionNewVehiculos);
                    }
                }
            }
            for (HojaPruebas hojaPruebasCollectionNewHojaPruebas : hojaPruebasCollectionNew) {
                if (!hojaPruebasCollectionOld.contains(hojaPruebasCollectionNewHojaPruebas)) {
                    Propietarios oldPropietariosOfHojaPruebasCollectionNewHojaPruebas = hojaPruebasCollectionNewHojaPruebas.getPropietarios();
                    hojaPruebasCollectionNewHojaPruebas.setPropietarios(propietarios);
                    hojaPruebasCollectionNewHojaPruebas = em.merge(hojaPruebasCollectionNewHojaPruebas);
                    if (oldPropietariosOfHojaPruebasCollectionNewHojaPruebas != null && !oldPropietariosOfHojaPruebasCollectionNewHojaPruebas.equals(propietarios)) {
                        oldPropietariosOfHojaPruebasCollectionNewHojaPruebas.getHojaPruebasCollection().remove(hojaPruebasCollectionNewHojaPruebas);
                        oldPropietariosOfHojaPruebasCollectionNewHojaPruebas = em.merge(oldPropietariosOfHojaPruebasCollectionNewHojaPruebas);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = propietarios.getCarowner();
                if (findPropietarios(id) == null) {
                    throw new NonexistentEntityException("The propietarios with id " + id + " no longer exists.");
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
            Propietarios propietarios;
            try {
                propietarios = em.getReference(Propietarios.class, id);
                propietarios.getCarowner();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The propietarios with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Vehiculos> vehiculosCollectionOrphanCheck = propietarios.getVehiculosCollection();
            for (Vehiculos vehiculosCollectionOrphanCheckVehiculos : vehiculosCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This Propietarios (" + propietarios + ") cannot be destroyed since the Vehiculos " + vehiculosCollectionOrphanCheckVehiculos + " in its vehiculosCollection field has a non-nullable propietarios field.");
            }
            Collection<HojaPruebas> hojaPruebasCollectionOrphanCheck = propietarios.getHojaPruebasCollection();
            for (HojaPruebas hojaPruebasCollectionOrphanCheckHojaPruebas : hojaPruebasCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This Propietarios (" + propietarios + ") cannot be destroyed since the HojaPruebas " + hojaPruebasCollectionOrphanCheckHojaPruebas + " in its hojaPruebasCollection field has a non-nullable propietarios field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(propietarios);
            em.getTransaction().commit();
        } finally {
          
        }
    }

    public List<Propietarios> findPropietariosEntities() {
        return findPropietariosEntities(true, -1, -1);
    }

    public List<Propietarios> findPropietariosEntities(int maxResults, int firstResult) {
        return findPropietariosEntities(false, maxResults, firstResult);
    }

    private List<Propietarios> findPropietariosEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Propietarios.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
          
        }
    }

    public Propietarios findPropietarios(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Propietarios.class, id);
        } finally {
         
        }
    }

    public int getPropietariosCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Propietarios> rt = cq.from(Propietarios.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
           
        }
    }

}
