/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.dao;

import com.soltelec.dao.conexion.PersistenceController;
import com.soltelec.dao.exceptions.IllegalOrphanException;
import com.soltelec.dao.exceptions.NonexistentEntityException;
import com.soltelec.model.HojaPruebas;
import com.soltelec.model.Propietarios;
import com.soltelec.model.Pruebas;
import com.soltelec.model.Usuarios;
import com.soltelec.model.Vehiculos;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author GerenciaDesarrollo
 */
public class HojaPruebasJpaController {

    public EntityManager getEntityManager() {
        return PersistenceController.getEntityManager();
    }

    public void create(HojaPruebas hojaPruebas) {
        if (hojaPruebas.getPruebasCollection() == null) {
            hojaPruebas.setPruebasCollection(new ArrayList<Pruebas>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Vehiculos vehiculos = hojaPruebas.getVehiculos();
            if (vehiculos != null) {
                vehiculos = em.getReference(vehiculos.getClass(), vehiculos.getCar());
                hojaPruebas.setVehiculos(vehiculos);
            }
            Usuarios usuarios = hojaPruebas.getUsuarios();
            if (usuarios != null) {
                usuarios = em.getReference(usuarios.getClass(), usuarios.getGeuser());
                hojaPruebas.setUsuarios(usuarios);
            }
            Propietarios propietarios = hojaPruebas.getPropietarios();
            if (propietarios != null) {
                propietarios = em.getReference(propietarios.getClass(), propietarios.getCarowner());
                hojaPruebas.setPropietarios(propietarios);
            }
            Collection<Pruebas> attachedPruebasCollection = new ArrayList<>();
            for (Pruebas pruebasCollectionPruebasToAttach : hojaPruebas.getPruebasCollection()) {
                pruebasCollectionPruebasToAttach = em.getReference(pruebasCollectionPruebasToAttach.getClass(), pruebasCollectionPruebasToAttach.getIdPruebas());
                attachedPruebasCollection.add(pruebasCollectionPruebasToAttach);
            }
            hojaPruebas.setPruebasCollection(attachedPruebasCollection);
            em.persist(hojaPruebas);
            if (vehiculos != null) {
                vehiculos.getHojaPruebasCollection().add(hojaPruebas);
                vehiculos = em.merge(vehiculos);
            }
            if (usuarios != null) {
                usuarios.getHojaPruebasCollection().add(hojaPruebas);
                usuarios = em.merge(usuarios);
            }
            if (propietarios != null) {
                propietarios.getHojaPruebasCollection().add(hojaPruebas);
                propietarios = em.merge(propietarios);
            }
            for (Pruebas pruebasCollectionPruebas : hojaPruebas.getPruebasCollection()) {
                HojaPruebas oldHojaPruebasOfPruebasCollectionPruebas = pruebasCollectionPruebas.getHojaPruebas();
                pruebasCollectionPruebas.setHojaPruebas(hojaPruebas);
                pruebasCollectionPruebas = em.merge(pruebasCollectionPruebas);
                if (oldHojaPruebasOfPruebasCollectionPruebas != null) {
                    oldHojaPruebasOfPruebasCollectionPruebas.getPruebasCollection().remove(pruebasCollectionPruebas);
                    oldHojaPruebasOfPruebasCollectionPruebas = em.merge(oldHojaPruebasOfPruebasCollectionPruebas);
                }
            }
            em.getTransaction().commit();
        } finally {
            
        }
    }

    public void edit(HojaPruebas hojaPruebas) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            HojaPruebas persistentHojaPruebas = em.find(HojaPruebas.class, hojaPruebas.getTestsheet());
            Vehiculos vehiculosOld = persistentHojaPruebas.getVehiculos();
            Vehiculos vehiculosNew = hojaPruebas.getVehiculos();
            Usuarios usuariosOld = persistentHojaPruebas.getUsuarios();
            Usuarios usuariosNew = hojaPruebas.getUsuarios();
            Propietarios propietariosOld = persistentHojaPruebas.getPropietarios();
            Propietarios propietariosNew = hojaPruebas.getPropietarios();
            Collection<Pruebas> pruebasCollectionOld = persistentHojaPruebas.getPruebasCollection();
            Collection<Pruebas> pruebasCollectionNew = hojaPruebas.getPruebasCollection();
            List<String> illegalOrphanMessages = null;
            for (Pruebas pruebasCollectionOldPruebas : pruebasCollectionOld) {
                if (!pruebasCollectionNew.contains(pruebasCollectionOldPruebas)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain Pruebas " + pruebasCollectionOldPruebas + " since its hojaPruebas field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (vehiculosNew != null) {
                vehiculosNew = em.getReference(vehiculosNew.getClass(), vehiculosNew.getCar());
                hojaPruebas.setVehiculos(vehiculosNew);
            }
            if (usuariosNew != null) {
                usuariosNew = em.getReference(usuariosNew.getClass(), usuariosNew.getGeuser());
                hojaPruebas.setUsuarios(usuariosNew);
            }
            if (propietariosNew != null) {
                propietariosNew = em.getReference(propietariosNew.getClass(), propietariosNew.getCarowner());
                hojaPruebas.setPropietarios(propietariosNew);
            }
            Collection<Pruebas> attachedPruebasCollectionNew = new ArrayList<>();
            for (Pruebas pruebasCollectionNewPruebasToAttach : pruebasCollectionNew) {
                pruebasCollectionNewPruebasToAttach = em.getReference(pruebasCollectionNewPruebasToAttach.getClass(), pruebasCollectionNewPruebasToAttach.getIdPruebas());
                attachedPruebasCollectionNew.add(pruebasCollectionNewPruebasToAttach);
            }
            pruebasCollectionNew = attachedPruebasCollectionNew;
            hojaPruebas.setPruebasCollection(pruebasCollectionNew);
            hojaPruebas = em.merge(hojaPruebas);
            if (vehiculosOld != null && !vehiculosOld.equals(vehiculosNew)) {
                vehiculosOld.getHojaPruebasCollection().remove(hojaPruebas);
                vehiculosOld = em.merge(vehiculosOld);
            }
            if (vehiculosNew != null && !vehiculosNew.equals(vehiculosOld)) {
                vehiculosNew.getHojaPruebasCollection().add(hojaPruebas);
                vehiculosNew = em.merge(vehiculosNew);
            }
            if (usuariosOld != null && !usuariosOld.equals(usuariosNew)) {
                usuariosOld.getHojaPruebasCollection().remove(hojaPruebas);
                usuariosOld = em.merge(usuariosOld);
            }
            if (usuariosNew != null && !usuariosNew.equals(usuariosOld)) {
                usuariosNew.getHojaPruebasCollection().add(hojaPruebas);
                usuariosNew = em.merge(usuariosNew);
            }
            if (propietariosOld != null && !propietariosOld.equals(propietariosNew)) {
                propietariosOld.getHojaPruebasCollection().remove(hojaPruebas);
                propietariosOld = em.merge(propietariosOld);
            }
            if (propietariosNew != null && !propietariosNew.equals(propietariosOld)) {
                propietariosNew.getHojaPruebasCollection().add(hojaPruebas);
                propietariosNew = em.merge(propietariosNew);
            }
            for (Pruebas pruebasCollectionNewPruebas : pruebasCollectionNew) {
                if (!pruebasCollectionOld.contains(pruebasCollectionNewPruebas)) {
                    HojaPruebas oldHojaPruebasOfPruebasCollectionNewPruebas = pruebasCollectionNewPruebas.getHojaPruebas();
                    pruebasCollectionNewPruebas.setHojaPruebas(hojaPruebas);
                    pruebasCollectionNewPruebas = em.merge(pruebasCollectionNewPruebas);
                    if (oldHojaPruebasOfPruebasCollectionNewPruebas != null && !oldHojaPruebasOfPruebasCollectionNewPruebas.equals(hojaPruebas)) {
                        oldHojaPruebasOfPruebasCollectionNewPruebas.getPruebasCollection().remove(pruebasCollectionNewPruebas);
                        oldHojaPruebasOfPruebasCollectionNewPruebas = em.merge(oldHojaPruebasOfPruebasCollectionNewPruebas);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = hojaPruebas.getTestsheet();
                if (findHojaPruebas(id) == null) {
                    throw new NonexistentEntityException("The hojaPruebas with id " + id + " no longer exists.");
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
            HojaPruebas hojaPruebas;
            try {
                hojaPruebas = em.getReference(HojaPruebas.class, id);
                hojaPruebas.getTestsheet();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The hojaPruebas with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Pruebas> pruebasCollectionOrphanCheck = hojaPruebas.getPruebasCollection();
            for (Pruebas pruebasCollectionOrphanCheckPruebas : pruebasCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This HojaPruebas (" + hojaPruebas + ") cannot be destroyed since the Pruebas " + pruebasCollectionOrphanCheckPruebas + " in its pruebasCollection field has a non-nullable hojaPruebas field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Vehiculos vehiculos = hojaPruebas.getVehiculos();
            if (vehiculos != null) {
                vehiculos.getHojaPruebasCollection().remove(hojaPruebas);
                vehiculos = em.merge(vehiculos);
            }
            Usuarios usuarios = hojaPruebas.getUsuarios();
            if (usuarios != null) {
                usuarios.getHojaPruebasCollection().remove(hojaPruebas);
                usuarios = em.merge(usuarios);
            }
            Propietarios propietarios = hojaPruebas.getPropietarios();
            if (propietarios != null) {
                propietarios.getHojaPruebasCollection().remove(hojaPruebas);
                propietarios = em.merge(propietarios);
            }
            em.remove(hojaPruebas);
            em.getTransaction().commit();
        } finally {
           
        }
    }

    public List<HojaPruebas> findHojaPruebasEntities() {
        return findHojaPruebasEntities(true, -1, -1);
    }

    public List<HojaPruebas> findHojaPruebasEntities(int maxResults, int firstResult) {
        return findHojaPruebasEntities(false, maxResults, firstResult);
    }

    private List<HojaPruebas> findHojaPruebasEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(HojaPruebas.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
           
        }
    }

    public HojaPruebas findHojaPruebas(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(HojaPruebas.class, id);
        } finally {
           
        }
    }

    public int getHojaPruebasCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<HojaPruebas> rt = cq.from(HojaPruebas.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
           
        }
    }

    //Seleccionar la maxima id de hoja de prueba para esas placas que no este finalizada
     public int hojaPruebaMaxNoFin(String laPlaca){
        EntityManager em = getEntityManager();
        Query q = em.createQuery("SELECT MAX(hp.testsheet) FROM HojaPruebas hp WHERE hp.finalizada = 'N' AND hp.vehiculos.carplate = :placa ");
        q.setParameter("placa", laPlaca);
        int idHoja = -1;
        try{
             idHoja = ((Integer)q.getSingleResult());
         }catch(NoResultException | NullPointerException nre){
            idHoja = -1;
         }
        return idHoja;
    }

}
