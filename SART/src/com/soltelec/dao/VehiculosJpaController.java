/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.dao;

import com.soltelec.dao.conexion.PersistenceController;
import com.soltelec.dao.exceptions.IllegalOrphanException;
import com.soltelec.dao.exceptions.NonexistentEntityException;
import com.soltelec.model.Aseguradoras;
import com.soltelec.model.ClasesVehiculo;
import com.soltelec.model.Colores;
import com.soltelec.model.HojaPruebas;
import com.soltelec.model.LineasVehiculos;
import com.soltelec.model.Llantas;
import com.soltelec.model.Marcas;
import com.soltelec.model.Propietarios;
import com.soltelec.model.Servicios;
import com.soltelec.model.TipoVehiculo;
import com.soltelec.model.TiposGasolina;
import com.soltelec.model.Usuarios;
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
public class VehiculosJpaController {

    public EntityManager getEntityManager() {
        return PersistenceController.getEntityManager();
    }

    public void create(Vehiculos vehiculos) {
        if (vehiculos.getHojaPruebasCollection() == null) {
            vehiculos.setHojaPruebasCollection(new ArrayList<HojaPruebas>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Llantas llantas = vehiculos.getLlantas();
            if (llantas != null) {
                llantas = em.getReference(llantas.getClass(), llantas.getWheel());
                vehiculos.setLlantas(llantas);
            }
            Servicios servicios = vehiculos.getServicios();
            if (servicios != null) {
                servicios = em.getReference(servicios.getClass(), servicios.getService());
                vehiculos.setServicios(servicios);
            }
            Aseguradoras aseguradoras = vehiculos.getAseguradoras();
            if (aseguradoras != null) {
                aseguradoras = em.getReference(aseguradoras.getClass(), aseguradoras.getInsuring());
                vehiculos.setAseguradoras(aseguradoras);
            }
            Usuarios usuarios = vehiculos.getUsuarios();
            if (usuarios != null) {
                usuarios = em.getReference(usuarios.getClass(), usuarios.getGeuser());
                vehiculos.setUsuarios(usuarios);
            }
            TiposGasolina tiposGasolina = vehiculos.getTiposGasolina();
            if (tiposGasolina != null) {
                tiposGasolina = em.getReference(tiposGasolina.getClass(), tiposGasolina.getFueltype());
                vehiculos.setTiposGasolina(tiposGasolina);
            }
            Colores colores = vehiculos.getColores();
            if (colores != null) {
                colores = em.getReference(colores.getClass(), colores.getColor());
                vehiculos.setColores(colores);
            }
            ClasesVehiculo clasesVehiculo = vehiculos.getClasesVehiculo();
            if (clasesVehiculo != null) {
                clasesVehiculo = em.getReference(clasesVehiculo.getClass(), clasesVehiculo.getClass1());
                vehiculos.setClasesVehiculo(clasesVehiculo);
            }
            TipoVehiculo tipoVehiculo = vehiculos.getTipoVehiculo();
            if (tipoVehiculo != null) {
                tipoVehiculo = em.getReference(tipoVehiculo.getClass(), tipoVehiculo.getCartype());
                vehiculos.setTipoVehiculo(tipoVehiculo);
            }
            Propietarios propietarios = vehiculos.getPropietarios();
            if (propietarios != null) {
                propietarios = em.getReference(propietarios.getClass(), propietarios.getCarowner());
                vehiculos.setPropietarios(propietarios);
            }
            Marcas marcas = vehiculos.getMarcas();
            if (marcas != null) {
                marcas = em.getReference(marcas.getClass(), marcas.getCarmark());
                vehiculos.setMarcas(marcas);
            }
            LineasVehiculos lineasVehiculos = vehiculos.getLineasVehiculos();
            if (lineasVehiculos != null) {
                lineasVehiculos = em.getReference(lineasVehiculos.getClass(), lineasVehiculos.getCarline());
                vehiculos.setLineasVehiculos(lineasVehiculos);
            }
            Collection<HojaPruebas> attachedHojaPruebasCollection = new ArrayList<>();
            for (HojaPruebas hojaPruebasCollectionHojaPruebasToAttach : vehiculos.getHojaPruebasCollection()) {
                hojaPruebasCollectionHojaPruebasToAttach = em.getReference(hojaPruebasCollectionHojaPruebasToAttach.getClass(), hojaPruebasCollectionHojaPruebasToAttach.getTestsheet());
                attachedHojaPruebasCollection.add(hojaPruebasCollectionHojaPruebasToAttach);
            }
            vehiculos.setHojaPruebasCollection(attachedHojaPruebasCollection);
            em.persist(vehiculos);
            if (llantas != null) {
                llantas.getVehiculosCollection().add(vehiculos);
                llantas = em.merge(llantas);
            }
            if (servicios != null) {
                servicios.getVehiculosCollection().add(vehiculos);
                servicios = em.merge(servicios);
            }
            if (aseguradoras != null) {
                aseguradoras.getVehiculosCollection().add(vehiculos);
                aseguradoras = em.merge(aseguradoras);
            }
            if (usuarios != null) {
                usuarios.getVehiculosCollection().add(vehiculos);
                usuarios = em.merge(usuarios);
            }
            if (tiposGasolina != null) {
                tiposGasolina.getVehiculosCollection().add(vehiculos);
                tiposGasolina = em.merge(tiposGasolina);
            }
            if (colores != null) {
                colores.getVehiculosCollection().add(vehiculos);
                colores = em.merge(colores);
            }
            if (clasesVehiculo != null) {
                clasesVehiculo.getVehiculosCollection().add(vehiculos);
                clasesVehiculo = em.merge(clasesVehiculo);
            }
            if (tipoVehiculo != null) {
                tipoVehiculo.getVehiculosCollection().add(vehiculos);
                tipoVehiculo = em.merge(tipoVehiculo);
            }
            if (propietarios != null) {
                propietarios.getVehiculosCollection().add(vehiculos);
                propietarios = em.merge(propietarios);
            }
            if (marcas != null) {
                marcas.getVehiculosCollection().add(vehiculos);
                marcas = em.merge(marcas);
            }
            if (lineasVehiculos != null) {
                lineasVehiculos.getVehiculosCollection().add(vehiculos);
                lineasVehiculos = em.merge(lineasVehiculos);
            }
            for (HojaPruebas hojaPruebasCollectionHojaPruebas : vehiculos.getHojaPruebasCollection()) {
                Vehiculos oldVehiculosOfHojaPruebasCollectionHojaPruebas = hojaPruebasCollectionHojaPruebas.getVehiculos();
                hojaPruebasCollectionHojaPruebas.setVehiculos(vehiculos);
                hojaPruebasCollectionHojaPruebas = em.merge(hojaPruebasCollectionHojaPruebas);
                if (oldVehiculosOfHojaPruebasCollectionHojaPruebas != null) {
                    oldVehiculosOfHojaPruebasCollectionHojaPruebas.getHojaPruebasCollection().remove(hojaPruebasCollectionHojaPruebas);
                    oldVehiculosOfHojaPruebasCollectionHojaPruebas = em.merge(oldVehiculosOfHojaPruebasCollectionHojaPruebas);
                }
            }
            em.getTransaction().commit();
        } finally {
           
        }
    }

    public void edit(Vehiculos vehiculos) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Vehiculos persistentVehiculos = em.find(Vehiculos.class, vehiculos.getCar());
            Llantas llantasOld = persistentVehiculos.getLlantas();
            Llantas llantasNew = vehiculos.getLlantas();
            Servicios serviciosOld = persistentVehiculos.getServicios();
            Servicios serviciosNew = vehiculos.getServicios();
            Aseguradoras aseguradorasOld = persistentVehiculos.getAseguradoras();
            Aseguradoras aseguradorasNew = vehiculos.getAseguradoras();
            Usuarios usuariosOld = persistentVehiculos.getUsuarios();
            Usuarios usuariosNew = vehiculos.getUsuarios();
            TiposGasolina tiposGasolinaOld = persistentVehiculos.getTiposGasolina();
            TiposGasolina tiposGasolinaNew = vehiculos.getTiposGasolina();
            Colores coloresOld = persistentVehiculos.getColores();
            Colores coloresNew = vehiculos.getColores();
            ClasesVehiculo clasesVehiculoOld = persistentVehiculos.getClasesVehiculo();
            ClasesVehiculo clasesVehiculoNew = vehiculos.getClasesVehiculo();
            TipoVehiculo tipoVehiculoOld = persistentVehiculos.getTipoVehiculo();
            TipoVehiculo tipoVehiculoNew = vehiculos.getTipoVehiculo();
            Propietarios propietariosOld = persistentVehiculos.getPropietarios();
            Propietarios propietariosNew = vehiculos.getPropietarios();
            Marcas marcasOld = persistentVehiculos.getMarcas();
            Marcas marcasNew = vehiculos.getMarcas();
            LineasVehiculos lineasVehiculosOld = persistentVehiculos.getLineasVehiculos();
            LineasVehiculos lineasVehiculosNew = vehiculos.getLineasVehiculos();
            Collection<HojaPruebas> hojaPruebasCollectionOld = persistentVehiculos.getHojaPruebasCollection();
            Collection<HojaPruebas> hojaPruebasCollectionNew = vehiculos.getHojaPruebasCollection();
            List<String> illegalOrphanMessages = null;
            for (HojaPruebas hojaPruebasCollectionOldHojaPruebas : hojaPruebasCollectionOld) {
                if (!hojaPruebasCollectionNew.contains(hojaPruebasCollectionOldHojaPruebas)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain HojaPruebas " + hojaPruebasCollectionOldHojaPruebas + " since its vehiculos field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (llantasNew != null) {
                llantasNew = em.getReference(llantasNew.getClass(), llantasNew.getWheel());
                vehiculos.setLlantas(llantasNew);
            }
            if (serviciosNew != null) {
                serviciosNew = em.getReference(serviciosNew.getClass(), serviciosNew.getService());
                vehiculos.setServicios(serviciosNew);
            }
            if (aseguradorasNew != null) {
                aseguradorasNew = em.getReference(aseguradorasNew.getClass(), aseguradorasNew.getInsuring());
                vehiculos.setAseguradoras(aseguradorasNew);
            }
            if (usuariosNew != null) {
                usuariosNew = em.getReference(usuariosNew.getClass(), usuariosNew.getGeuser());
                vehiculos.setUsuarios(usuariosNew);
            }
            if (tiposGasolinaNew != null) {
                tiposGasolinaNew = em.getReference(tiposGasolinaNew.getClass(), tiposGasolinaNew.getFueltype());
                vehiculos.setTiposGasolina(tiposGasolinaNew);
            }
            if (coloresNew != null) {
                coloresNew = em.getReference(coloresNew.getClass(), coloresNew.getColor());
                vehiculos.setColores(coloresNew);
            }
            if (clasesVehiculoNew != null) {
                clasesVehiculoNew = em.getReference(clasesVehiculoNew.getClass(), clasesVehiculoNew.getClass1());
                vehiculos.setClasesVehiculo(clasesVehiculoNew);
            }
            if (tipoVehiculoNew != null) {
                tipoVehiculoNew = em.getReference(tipoVehiculoNew.getClass(), tipoVehiculoNew.getCartype());
                vehiculos.setTipoVehiculo(tipoVehiculoNew);
            }
            if (propietariosNew != null) {
                propietariosNew = em.getReference(propietariosNew.getClass(), propietariosNew.getCarowner());
                vehiculos.setPropietarios(propietariosNew);
            }
            if (marcasNew != null) {
                marcasNew = em.getReference(marcasNew.getClass(), marcasNew.getCarmark());
                vehiculos.setMarcas(marcasNew);
            }
            if (lineasVehiculosNew != null) {
                lineasVehiculosNew = em.getReference(lineasVehiculosNew.getClass(), lineasVehiculosNew.getCarline());
                vehiculos.setLineasVehiculos(lineasVehiculosNew);
            }
            Collection<HojaPruebas> attachedHojaPruebasCollectionNew = new ArrayList<>();
            for (HojaPruebas hojaPruebasCollectionNewHojaPruebasToAttach : hojaPruebasCollectionNew) {
                hojaPruebasCollectionNewHojaPruebasToAttach = em.getReference(hojaPruebasCollectionNewHojaPruebasToAttach.getClass(), hojaPruebasCollectionNewHojaPruebasToAttach.getTestsheet());
                attachedHojaPruebasCollectionNew.add(hojaPruebasCollectionNewHojaPruebasToAttach);
            }
            hojaPruebasCollectionNew = attachedHojaPruebasCollectionNew;
            vehiculos.setHojaPruebasCollection(hojaPruebasCollectionNew);
            vehiculos = em.merge(vehiculos);
            if (llantasOld != null && !llantasOld.equals(llantasNew)) {
                llantasOld.getVehiculosCollection().remove(vehiculos);
                llantasOld = em.merge(llantasOld);
            }
            if (llantasNew != null && !llantasNew.equals(llantasOld)) {
                llantasNew.getVehiculosCollection().add(vehiculos);
                llantasNew = em.merge(llantasNew);
            }
            if (serviciosOld != null && !serviciosOld.equals(serviciosNew)) {
                serviciosOld.getVehiculosCollection().remove(vehiculos);
                serviciosOld = em.merge(serviciosOld);
            }
            if (serviciosNew != null && !serviciosNew.equals(serviciosOld)) {
                serviciosNew.getVehiculosCollection().add(vehiculos);
                serviciosNew = em.merge(serviciosNew);
            }
            if (aseguradorasOld != null && !aseguradorasOld.equals(aseguradorasNew)) {
                aseguradorasOld.getVehiculosCollection().remove(vehiculos);
                aseguradorasOld = em.merge(aseguradorasOld);
            }
            if (aseguradorasNew != null && !aseguradorasNew.equals(aseguradorasOld)) {
                aseguradorasNew.getVehiculosCollection().add(vehiculos);
                aseguradorasNew = em.merge(aseguradorasNew);
            }
            if (usuariosOld != null && !usuariosOld.equals(usuariosNew)) {
                usuariosOld.getVehiculosCollection().remove(vehiculos);
                usuariosOld = em.merge(usuariosOld);
            }
            if (usuariosNew != null && !usuariosNew.equals(usuariosOld)) {
                usuariosNew.getVehiculosCollection().add(vehiculos);
                usuariosNew = em.merge(usuariosNew);
            }
            if (tiposGasolinaOld != null && !tiposGasolinaOld.equals(tiposGasolinaNew)) {
                tiposGasolinaOld.getVehiculosCollection().remove(vehiculos);
                tiposGasolinaOld = em.merge(tiposGasolinaOld);
            }
            if (tiposGasolinaNew != null && !tiposGasolinaNew.equals(tiposGasolinaOld)) {
                tiposGasolinaNew.getVehiculosCollection().add(vehiculos);
                tiposGasolinaNew = em.merge(tiposGasolinaNew);
            }
            if (coloresOld != null && !coloresOld.equals(coloresNew)) {
                coloresOld.getVehiculosCollection().remove(vehiculos);
                coloresOld = em.merge(coloresOld);
            }
            if (coloresNew != null && !coloresNew.equals(coloresOld)) {
                coloresNew.getVehiculosCollection().add(vehiculos);
                coloresNew = em.merge(coloresNew);
            }
            if (clasesVehiculoOld != null && !clasesVehiculoOld.equals(clasesVehiculoNew)) {
                clasesVehiculoOld.getVehiculosCollection().remove(vehiculos);
                clasesVehiculoOld = em.merge(clasesVehiculoOld);
            }
            if (clasesVehiculoNew != null && !clasesVehiculoNew.equals(clasesVehiculoOld)) {
                clasesVehiculoNew.getVehiculosCollection().add(vehiculos);
                clasesVehiculoNew = em.merge(clasesVehiculoNew);
            }
            if (tipoVehiculoOld != null && !tipoVehiculoOld.equals(tipoVehiculoNew)) {
                tipoVehiculoOld.getVehiculosCollection().remove(vehiculos);
                tipoVehiculoOld = em.merge(tipoVehiculoOld);
            }
            if (tipoVehiculoNew != null && !tipoVehiculoNew.equals(tipoVehiculoOld)) {
                tipoVehiculoNew.getVehiculosCollection().add(vehiculos);
                tipoVehiculoNew = em.merge(tipoVehiculoNew);
            }
            if (propietariosOld != null && !propietariosOld.equals(propietariosNew)) {
                propietariosOld.getVehiculosCollection().remove(vehiculos);
                propietariosOld = em.merge(propietariosOld);
            }
            if (propietariosNew != null && !propietariosNew.equals(propietariosOld)) {
                propietariosNew.getVehiculosCollection().add(vehiculos);
                propietariosNew = em.merge(propietariosNew);
            }
            if (marcasOld != null && !marcasOld.equals(marcasNew)) {
                marcasOld.getVehiculosCollection().remove(vehiculos);
                marcasOld = em.merge(marcasOld);
            }
            if (marcasNew != null && !marcasNew.equals(marcasOld)) {
                marcasNew.getVehiculosCollection().add(vehiculos);
                marcasNew = em.merge(marcasNew);
            }
            if (lineasVehiculosOld != null && !lineasVehiculosOld.equals(lineasVehiculosNew)) {
                lineasVehiculosOld.getVehiculosCollection().remove(vehiculos);
                lineasVehiculosOld = em.merge(lineasVehiculosOld);
            }
            if (lineasVehiculosNew != null && !lineasVehiculosNew.equals(lineasVehiculosOld)) {
                lineasVehiculosNew.getVehiculosCollection().add(vehiculos);
                lineasVehiculosNew = em.merge(lineasVehiculosNew);
            }
            for (HojaPruebas hojaPruebasCollectionNewHojaPruebas : hojaPruebasCollectionNew) {
                if (!hojaPruebasCollectionOld.contains(hojaPruebasCollectionNewHojaPruebas)) {
                    Vehiculos oldVehiculosOfHojaPruebasCollectionNewHojaPruebas = hojaPruebasCollectionNewHojaPruebas.getVehiculos();
                    hojaPruebasCollectionNewHojaPruebas.setVehiculos(vehiculos);
                    hojaPruebasCollectionNewHojaPruebas = em.merge(hojaPruebasCollectionNewHojaPruebas);
                    if (oldVehiculosOfHojaPruebasCollectionNewHojaPruebas != null && !oldVehiculosOfHojaPruebasCollectionNewHojaPruebas.equals(vehiculos)) {
                        oldVehiculosOfHojaPruebasCollectionNewHojaPruebas.getHojaPruebasCollection().remove(hojaPruebasCollectionNewHojaPruebas);
                        oldVehiculosOfHojaPruebasCollectionNewHojaPruebas = em.merge(oldVehiculosOfHojaPruebasCollectionNewHojaPruebas);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = vehiculos.getCar();
                if (findVehiculos(id) == null) {
                    throw new NonexistentEntityException("The vehiculos with id " + id + " no longer exists.");
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
            Vehiculos vehiculos;
            try {
                vehiculos = em.getReference(Vehiculos.class, id);
                vehiculos.getCar();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The vehiculos with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<HojaPruebas> hojaPruebasCollectionOrphanCheck = vehiculos.getHojaPruebasCollection();
            for (HojaPruebas hojaPruebasCollectionOrphanCheckHojaPruebas : hojaPruebasCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This Vehiculos (" + vehiculos + ") cannot be destroyed since the HojaPruebas " + hojaPruebasCollectionOrphanCheckHojaPruebas + " in its hojaPruebasCollection field has a non-nullable vehiculos field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Llantas llantas = vehiculos.getLlantas();
            if (llantas != null) {
                llantas.getVehiculosCollection().remove(vehiculos);
                llantas = em.merge(llantas);
            }
            Servicios servicios = vehiculos.getServicios();
            if (servicios != null) {
                servicios.getVehiculosCollection().remove(vehiculos);
                servicios = em.merge(servicios);
            }
            Aseguradoras aseguradoras = vehiculos.getAseguradoras();
            if (aseguradoras != null) {
                aseguradoras.getVehiculosCollection().remove(vehiculos);
                aseguradoras = em.merge(aseguradoras);
            }
            Usuarios usuarios = vehiculos.getUsuarios();
            if (usuarios != null) {
                usuarios.getVehiculosCollection().remove(vehiculos);
                usuarios = em.merge(usuarios);
            }
            TiposGasolina tiposGasolina = vehiculos.getTiposGasolina();
            if (tiposGasolina != null) {
                tiposGasolina.getVehiculosCollection().remove(vehiculos);
                tiposGasolina = em.merge(tiposGasolina);
            }
            Colores colores = vehiculos.getColores();
            if (colores != null) {
                colores.getVehiculosCollection().remove(vehiculos);
                colores = em.merge(colores);
            }
            ClasesVehiculo clasesVehiculo = vehiculos.getClasesVehiculo();
            if (clasesVehiculo != null) {
                clasesVehiculo.getVehiculosCollection().remove(vehiculos);
                clasesVehiculo = em.merge(clasesVehiculo);
            }
            TipoVehiculo tipoVehiculo = vehiculos.getTipoVehiculo();
            if (tipoVehiculo != null) {
                tipoVehiculo.getVehiculosCollection().remove(vehiculos);
                tipoVehiculo = em.merge(tipoVehiculo);
            }
            Propietarios propietarios = vehiculos.getPropietarios();
            if (propietarios != null) {
                propietarios.getVehiculosCollection().remove(vehiculos);
                propietarios = em.merge(propietarios);
            }
            Marcas marcas = vehiculos.getMarcas();
            if (marcas != null) {
                marcas.getVehiculosCollection().remove(vehiculos);
                marcas = em.merge(marcas);
            }
            LineasVehiculos lineasVehiculos = vehiculos.getLineasVehiculos();
            if (lineasVehiculos != null) {
                lineasVehiculos.getVehiculosCollection().remove(vehiculos);
                lineasVehiculos = em.merge(lineasVehiculos);
            }
            em.remove(vehiculos);
            em.getTransaction().commit();
        } finally {
           
        }
    }

    public List<Vehiculos> findVehiculosEntities() {
        return findVehiculosEntities(true, -1, -1);
    }

    public List<Vehiculos> findVehiculosEntities(int maxResults, int firstResult) {
        return findVehiculosEntities(false, maxResults, firstResult);
    }

    private List<Vehiculos> findVehiculosEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Vehiculos.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
        
        }
    }

    public Vehiculos findVehiculos(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Vehiculos.class, id);
        } finally {
           
        }
    }

    public int getVehiculosCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Vehiculos> rt = cq.from(Vehiculos.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
           
        }
    }

    public Vehiculos getTipoVehiculo(String placa){
        EntityManager em = getEntityManager();
        Query q = em.createNamedQuery("Vehiculos.findByCarplate");
        q.setParameter("carplate",placa);
        Vehiculos v = (Vehiculos)q.getSingleResult();
        return v;
    }

}
