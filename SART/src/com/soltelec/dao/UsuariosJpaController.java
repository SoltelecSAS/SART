/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.soltelec.dao;

import com.soltelec.dao.conexion.PersistenceController;
import com.soltelec.dao.exceptions.IllegalOrphanException;
import com.soltelec.dao.exceptions.NonexistentEntityException;
import com.soltelec.dao.exceptions.PreexistingEntityException;
import com.soltelec.model.Calibraciones;
import com.soltelec.model.HojaPruebas;
import com.soltelec.model.Pruebas;
import com.soltelec.model.Usuarios;
import com.soltelec.model.Vehiculos;
import java.io.Serializable;
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
public class UsuariosJpaController implements Serializable {

    public EntityManager getEntityManager() {
        return PersistenceController.getEntityManager();
    }

    public void create(Usuarios usuarios) throws PreexistingEntityException, Exception {
        if (usuarios.getVehiculosCollection() == null) {
            usuarios.setVehiculosCollection(new ArrayList<Vehiculos>());
        }
        if (usuarios.getHojaPruebasCollection() == null) {
            usuarios.setHojaPruebasCollection(new ArrayList<HojaPruebas>());
        }
        if (usuarios.getPruebasCollection() == null) {
            usuarios.setPruebasCollection(new ArrayList<Pruebas>());
        }
        if (usuarios.getCalibracionesList() == null) {
            usuarios.setCalibracionesList(new ArrayList<Calibraciones>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Vehiculos> attachedVehiculosCollection = new ArrayList<>();
            for (Vehiculos vehiculosCollectionVehiculosToAttach : usuarios.getVehiculosCollection()) {
                vehiculosCollectionVehiculosToAttach = em.getReference(vehiculosCollectionVehiculosToAttach.getClass(), vehiculosCollectionVehiculosToAttach.getCar());
                attachedVehiculosCollection.add(vehiculosCollectionVehiculosToAttach);
            }
            usuarios.setVehiculosCollection(attachedVehiculosCollection);
            Collection<HojaPruebas> attachedHojaPruebasCollection = new ArrayList<>();
            for (HojaPruebas hojaPruebasCollectionHojaPruebasToAttach : usuarios.getHojaPruebasCollection()) {
                hojaPruebasCollectionHojaPruebasToAttach = em.getReference(hojaPruebasCollectionHojaPruebasToAttach.getClass(), hojaPruebasCollectionHojaPruebasToAttach.getTestsheet());
                attachedHojaPruebasCollection.add(hojaPruebasCollectionHojaPruebasToAttach);
            }
            usuarios.setHojaPruebasCollection(attachedHojaPruebasCollection);
            Collection<Pruebas> attachedPruebasCollection = new ArrayList<>();
            for (Pruebas pruebasCollectionPruebasToAttach : usuarios.getPruebasCollection()) {
                pruebasCollectionPruebasToAttach = em.getReference(pruebasCollectionPruebasToAttach.getClass(), pruebasCollectionPruebasToAttach.getIdPruebas());
                attachedPruebasCollection.add(pruebasCollectionPruebasToAttach);
            }
            usuarios.setPruebasCollection(attachedPruebasCollection);
            List<Calibraciones> attachedCalibracionesList = new ArrayList<>();
            for (Calibraciones calibracionesListCalibracionesToAttach : usuarios.getCalibracionesList()) {
                calibracionesListCalibracionesToAttach = em.getReference(calibracionesListCalibracionesToAttach.getClass(), calibracionesListCalibracionesToAttach.getCalibration());
                attachedCalibracionesList.add(calibracionesListCalibracionesToAttach);
            }
            usuarios.setCalibracionesList(attachedCalibracionesList);
            em.persist(usuarios);
            for (Vehiculos vehiculosCollectionVehiculos : usuarios.getVehiculosCollection()) {
                Usuarios oldUsuariosOfVehiculosCollectionVehiculos = vehiculosCollectionVehiculos.getUsuarios();
                vehiculosCollectionVehiculos.setUsuarios(usuarios);
                vehiculosCollectionVehiculos = em.merge(vehiculosCollectionVehiculos);
                if (oldUsuariosOfVehiculosCollectionVehiculos != null) {
                    oldUsuariosOfVehiculosCollectionVehiculos.getVehiculosCollection().remove(vehiculosCollectionVehiculos);
                    oldUsuariosOfVehiculosCollectionVehiculos = em.merge(oldUsuariosOfVehiculosCollectionVehiculos);
                }
            }
            for (HojaPruebas hojaPruebasCollectionHojaPruebas : usuarios.getHojaPruebasCollection()) {
                Usuarios oldUsuariosOfHojaPruebasCollectionHojaPruebas = hojaPruebasCollectionHojaPruebas.getUsuarios();
                hojaPruebasCollectionHojaPruebas.setUsuarios(usuarios);
                hojaPruebasCollectionHojaPruebas = em.merge(hojaPruebasCollectionHojaPruebas);
                if (oldUsuariosOfHojaPruebasCollectionHojaPruebas != null) {
                    oldUsuariosOfHojaPruebasCollectionHojaPruebas.getHojaPruebasCollection().remove(hojaPruebasCollectionHojaPruebas);
                    oldUsuariosOfHojaPruebasCollectionHojaPruebas = em.merge(oldUsuariosOfHojaPruebasCollectionHojaPruebas);
                }
            }
            for (Pruebas pruebasCollectionPruebas : usuarios.getPruebasCollection()) {
                Usuarios oldUsuariosOfPruebasCollectionPruebas = pruebasCollectionPruebas.getUsuarios();
                pruebasCollectionPruebas.setUsuarios(usuarios);
                pruebasCollectionPruebas = em.merge(pruebasCollectionPruebas);
                if (oldUsuariosOfPruebasCollectionPruebas != null) {
                    oldUsuariosOfPruebasCollectionPruebas.getPruebasCollection().remove(pruebasCollectionPruebas);
                    oldUsuariosOfPruebasCollectionPruebas = em.merge(oldUsuariosOfPruebasCollectionPruebas);
                }
            }
            for (Calibraciones calibracionesListCalibraciones : usuarios.getCalibracionesList()) {
                Usuarios oldGeuserOfCalibracionesListCalibraciones = calibracionesListCalibraciones.getGeuser();
                calibracionesListCalibraciones.setGeuser(usuarios);
                calibracionesListCalibraciones = em.merge(calibracionesListCalibraciones);
                if (oldGeuserOfCalibracionesListCalibraciones != null) {
                    oldGeuserOfCalibracionesListCalibraciones.getCalibracionesList().remove(calibracionesListCalibraciones);
                    oldGeuserOfCalibracionesListCalibraciones = em.merge(oldGeuserOfCalibracionesListCalibraciones);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findUsuarios(usuarios.getGeuser()) != null) {
                throw new PreexistingEntityException("Usuarios " + usuarios + " already exists.", ex);
            }
            throw ex;
        } finally {

        }
    }

    public void edit(Usuarios usuarios) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuarios persistentUsuarios = em.find(Usuarios.class, usuarios.getGeuser());
            Collection<Vehiculos> vehiculosCollectionOld = persistentUsuarios.getVehiculosCollection();
            Collection<Vehiculos> vehiculosCollectionNew = usuarios.getVehiculosCollection();
            Collection<HojaPruebas> hojaPruebasCollectionOld = persistentUsuarios.getHojaPruebasCollection();
            Collection<HojaPruebas> hojaPruebasCollectionNew = usuarios.getHojaPruebasCollection();
            Collection<Pruebas> pruebasCollectionOld = persistentUsuarios.getPruebasCollection();
            Collection<Pruebas> pruebasCollectionNew = usuarios.getPruebasCollection();
            List<Calibraciones> calibracionesListOld = persistentUsuarios.getCalibracionesList();
            List<Calibraciones> calibracionesListNew = usuarios.getCalibracionesList();
            List<String> illegalOrphanMessages = null;
            for (Vehiculos vehiculosCollectionOldVehiculos : vehiculosCollectionOld) {
                if (!vehiculosCollectionNew.contains(vehiculosCollectionOldVehiculos)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain Vehiculos " + vehiculosCollectionOldVehiculos + " since its usuarios field is not nullable.");
                }
            }
            for (HojaPruebas hojaPruebasCollectionOldHojaPruebas : hojaPruebasCollectionOld) {
                if (!hojaPruebasCollectionNew.contains(hojaPruebasCollectionOldHojaPruebas)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain HojaPruebas " + hojaPruebasCollectionOldHojaPruebas + " since its usuarios field is not nullable.");
                }
            }
            for (Calibraciones calibracionesListOldCalibraciones : calibracionesListOld) {
                if (!calibracionesListNew.contains(calibracionesListOldCalibraciones)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain Calibraciones " + calibracionesListOldCalibraciones + " since its geuser field is not nullable.");
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
            usuarios.setVehiculosCollection(vehiculosCollectionNew);
            Collection<HojaPruebas> attachedHojaPruebasCollectionNew = new ArrayList<>();
            for (HojaPruebas hojaPruebasCollectionNewHojaPruebasToAttach : hojaPruebasCollectionNew) {
                hojaPruebasCollectionNewHojaPruebasToAttach = em.getReference(hojaPruebasCollectionNewHojaPruebasToAttach.getClass(), hojaPruebasCollectionNewHojaPruebasToAttach.getTestsheet());
                attachedHojaPruebasCollectionNew.add(hojaPruebasCollectionNewHojaPruebasToAttach);
            }
            hojaPruebasCollectionNew = attachedHojaPruebasCollectionNew;
            usuarios.setHojaPruebasCollection(hojaPruebasCollectionNew);
            Collection<Pruebas> attachedPruebasCollectionNew = new ArrayList<>();
            for (Pruebas pruebasCollectionNewPruebasToAttach : pruebasCollectionNew) {
                pruebasCollectionNewPruebasToAttach = em.getReference(pruebasCollectionNewPruebasToAttach.getClass(), pruebasCollectionNewPruebasToAttach.getIdPruebas());
                attachedPruebasCollectionNew.add(pruebasCollectionNewPruebasToAttach);
            }
            pruebasCollectionNew = attachedPruebasCollectionNew;
            usuarios.setPruebasCollection(pruebasCollectionNew);
            List<Calibraciones> attachedCalibracionesListNew = new ArrayList<>();
            for (Calibraciones calibracionesListNewCalibracionesToAttach : calibracionesListNew) {
                calibracionesListNewCalibracionesToAttach = em.getReference(calibracionesListNewCalibracionesToAttach.getClass(), calibracionesListNewCalibracionesToAttach.getCalibration());
                attachedCalibracionesListNew.add(calibracionesListNewCalibracionesToAttach);
            }
            calibracionesListNew = attachedCalibracionesListNew;
            usuarios.setCalibracionesList(calibracionesListNew);
            usuarios = em.merge(usuarios);
            for (Vehiculos vehiculosCollectionNewVehiculos : vehiculosCollectionNew) {
                if (!vehiculosCollectionOld.contains(vehiculosCollectionNewVehiculos)) {
                    Usuarios oldUsuariosOfVehiculosCollectionNewVehiculos = vehiculosCollectionNewVehiculos.getUsuarios();
                    vehiculosCollectionNewVehiculos.setUsuarios(usuarios);
                    vehiculosCollectionNewVehiculos = em.merge(vehiculosCollectionNewVehiculos);
                    if (oldUsuariosOfVehiculosCollectionNewVehiculos != null && !oldUsuariosOfVehiculosCollectionNewVehiculos.equals(usuarios)) {
                        oldUsuariosOfVehiculosCollectionNewVehiculos.getVehiculosCollection().remove(vehiculosCollectionNewVehiculos);
                        oldUsuariosOfVehiculosCollectionNewVehiculos = em.merge(oldUsuariosOfVehiculosCollectionNewVehiculos);
                    }
                }
            }
            for (HojaPruebas hojaPruebasCollectionNewHojaPruebas : hojaPruebasCollectionNew) {
                if (!hojaPruebasCollectionOld.contains(hojaPruebasCollectionNewHojaPruebas)) {
                    Usuarios oldUsuariosOfHojaPruebasCollectionNewHojaPruebas = hojaPruebasCollectionNewHojaPruebas.getUsuarios();
                    hojaPruebasCollectionNewHojaPruebas.setUsuarios(usuarios);
                    hojaPruebasCollectionNewHojaPruebas = em.merge(hojaPruebasCollectionNewHojaPruebas);
                    if (oldUsuariosOfHojaPruebasCollectionNewHojaPruebas != null && !oldUsuariosOfHojaPruebasCollectionNewHojaPruebas.equals(usuarios)) {
                        oldUsuariosOfHojaPruebasCollectionNewHojaPruebas.getHojaPruebasCollection().remove(hojaPruebasCollectionNewHojaPruebas);
                        oldUsuariosOfHojaPruebasCollectionNewHojaPruebas = em.merge(oldUsuariosOfHojaPruebasCollectionNewHojaPruebas);
                    }
                }
            }
            for (Pruebas pruebasCollectionOldPruebas : pruebasCollectionOld) {
                if (!pruebasCollectionNew.contains(pruebasCollectionOldPruebas)) {
                    pruebasCollectionOldPruebas.setUsuarios(null);
                    pruebasCollectionOldPruebas = em.merge(pruebasCollectionOldPruebas);
                }
            }
            for (Pruebas pruebasCollectionNewPruebas : pruebasCollectionNew) {
                if (!pruebasCollectionOld.contains(pruebasCollectionNewPruebas)) {
                    Usuarios oldUsuariosOfPruebasCollectionNewPruebas = pruebasCollectionNewPruebas.getUsuarios();
                    pruebasCollectionNewPruebas.setUsuarios(usuarios);
                    pruebasCollectionNewPruebas = em.merge(pruebasCollectionNewPruebas);
                    if (oldUsuariosOfPruebasCollectionNewPruebas != null && !oldUsuariosOfPruebasCollectionNewPruebas.equals(usuarios)) {
                        oldUsuariosOfPruebasCollectionNewPruebas.getPruebasCollection().remove(pruebasCollectionNewPruebas);
                        oldUsuariosOfPruebasCollectionNewPruebas = em.merge(oldUsuariosOfPruebasCollectionNewPruebas);
                    }
                }
            }
            for (Calibraciones calibracionesListNewCalibraciones : calibracionesListNew) {
                if (!calibracionesListOld.contains(calibracionesListNewCalibraciones)) {
                    Usuarios oldGeuserOfCalibracionesListNewCalibraciones = calibracionesListNewCalibraciones.getGeuser();
                    calibracionesListNewCalibraciones.setGeuser(usuarios);
                    calibracionesListNewCalibraciones = em.merge(calibracionesListNewCalibraciones);
                    if (oldGeuserOfCalibracionesListNewCalibraciones != null && !oldGeuserOfCalibracionesListNewCalibraciones.equals(usuarios)) {
                        oldGeuserOfCalibracionesListNewCalibraciones.getCalibracionesList().remove(calibracionesListNewCalibraciones);
                        oldGeuserOfCalibracionesListNewCalibraciones = em.merge(oldGeuserOfCalibracionesListNewCalibraciones);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = usuarios.getGeuser();
                if (findUsuarios(id) == null) {
                    throw new NonexistentEntityException("The usuarios with id " + id + " no longer exists.");
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
            Usuarios usuarios;
            try {
                usuarios = em.getReference(Usuarios.class, id);
                usuarios.getGeuser();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The usuarios with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Vehiculos> vehiculosCollectionOrphanCheck = usuarios.getVehiculosCollection();
            for (Vehiculos vehiculosCollectionOrphanCheckVehiculos : vehiculosCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This Usuarios (" + usuarios + ") cannot be destroyed since the Vehiculos " + vehiculosCollectionOrphanCheckVehiculos + " in its vehiculosCollection field has a non-nullable usuarios field.");
            }
            Collection<HojaPruebas> hojaPruebasCollectionOrphanCheck = usuarios.getHojaPruebasCollection();
            for (HojaPruebas hojaPruebasCollectionOrphanCheckHojaPruebas : hojaPruebasCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This Usuarios (" + usuarios + ") cannot be destroyed since the HojaPruebas " + hojaPruebasCollectionOrphanCheckHojaPruebas + " in its hojaPruebasCollection field has a non-nullable usuarios field.");
            }
            List<Calibraciones> calibracionesListOrphanCheck = usuarios.getCalibracionesList();
            for (Calibraciones calibracionesListOrphanCheckCalibraciones : calibracionesListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This Usuarios (" + usuarios + ") cannot be destroyed since the Calibraciones " + calibracionesListOrphanCheckCalibraciones + " in its calibracionesList field has a non-nullable geuser field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Pruebas> pruebasCollection = usuarios.getPruebasCollection();
            for (Pruebas pruebasCollectionPruebas : pruebasCollection) {
                pruebasCollectionPruebas.setUsuarios(null);
                pruebasCollectionPruebas = em.merge(pruebasCollectionPruebas);
            }
            em.remove(usuarios);
            em.getTransaction().commit();
        } finally {

        }
    }

    public List<Usuarios> findUsuariosEntities() {
        return findUsuariosEntities(true, -1, -1);
    }

    public List<Usuarios> findUsuariosEntities(int maxResults, int firstResult) {
        return findUsuariosEntities(false, maxResults, firstResult);
    }

    private List<Usuarios> findUsuariosEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Usuarios.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {

        }
    }

    public Usuarios findUsuarios(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Usuarios.class, id);
        } finally {

        }
    }

    public int getUsuariosCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Usuarios> rt = cq.from(Usuarios.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {

        }
    }

    public Usuarios getUsuarioByNick(String nick) {
        EntityManager em = getEntityManager();

        Query q = em.createNamedQuery("Usuarios.findByNickusuario");
        q.setParameter("nickusuario", nick);
        Usuarios u = null;
        try {
            u = (Usuarios) q.getSingleResult();
        } catch (NoResultException nre) {
            u = null;
        }
        return u;
    }

    public Usuarios getPassWord(String pass) {
         List results;
        Usuarios user = null;
        EntityManager em = getEntityManager();
        Query queryPass = em.createQuery("SELECT u FROM Usuarios u WHERE u.contrasenia = :contrasena");
        queryPass.setParameter("contrasena", pass);
        results = queryPass.getResultList();
        if(results.size()>0){
            user = (Usuarios) results.get(0);
        }
        
        return user;
    }

}
