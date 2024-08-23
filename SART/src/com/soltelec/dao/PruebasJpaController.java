/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.dao;

import com.soltelec.dao.conexion.PersistenceController;
import com.soltelec.dao.exceptions.NonexistentEntityException;
import com.soltelec.dao.exceptions.PreexistingEntityException;
import com.soltelec.model.HojaPruebas;
import com.soltelec.model.Pruebas;
import com.soltelec.model.TipoPrueba;
import com.soltelec.model.Usuarios;
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
public class PruebasJpaController {

    public EntityManager getEntityManager() {
        return PersistenceController.getEntityManager();
    }

    public void create(Pruebas pruebas) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuarios usuarios = pruebas.getUsuarios();
            if (usuarios != null) {
                usuarios = em.getReference(usuarios.getClass(), usuarios.getGeuser());
                pruebas.setUsuarios(usuarios);
            }
            TipoPrueba tipoPrueba = pruebas.getTipoPrueba();
            if (tipoPrueba != null) {
                tipoPrueba = em.getReference(tipoPrueba.getClass(), tipoPrueba.getTesttype());
                pruebas.setTipoPrueba(tipoPrueba);
            }
            HojaPruebas hojaPruebas = pruebas.getHojaPruebas();
            if (hojaPruebas != null) {
                hojaPruebas = em.getReference(hojaPruebas.getClass(), hojaPruebas.getTestsheet());
                pruebas.setHojaPruebas(hojaPruebas);
            }
            em.persist(pruebas);
            if (usuarios != null) {
                usuarios.getPruebasCollection().add(pruebas);
                usuarios = em.merge(usuarios);
            }
            if (tipoPrueba != null) {
                tipoPrueba.getPruebasCollection().add(pruebas);
                tipoPrueba = em.merge(tipoPrueba);
            }
            if (hojaPruebas != null) {
                hojaPruebas.getPruebasCollection().add(pruebas);
                hojaPruebas = em.merge(hojaPruebas);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findPruebas(pruebas.getIdPruebas()) != null) {
                throw new PreexistingEntityException("Pruebas " + pruebas + " already exists.", ex);
            }
            throw ex;
        } finally {
           
        }
    }

    public void edit(Pruebas pruebas) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Pruebas persistentPruebas = em.find(Pruebas.class, pruebas.getIdPruebas());
            Usuarios usuariosOld = persistentPruebas.getUsuarios();
            Usuarios usuariosNew = pruebas.getUsuarios();
            TipoPrueba tipoPruebaOld = persistentPruebas.getTipoPrueba();
            TipoPrueba tipoPruebaNew = pruebas.getTipoPrueba();
            HojaPruebas hojaPruebasOld = persistentPruebas.getHojaPruebas();
            HojaPruebas hojaPruebasNew = pruebas.getHojaPruebas();
            if (usuariosNew != null) {
                usuariosNew = em.getReference(usuariosNew.getClass(), usuariosNew.getGeuser());
                pruebas.setUsuarios(usuariosNew);
            }
            if (tipoPruebaNew != null) {
                tipoPruebaNew = em.getReference(tipoPruebaNew.getClass(), tipoPruebaNew.getTesttype());
                pruebas.setTipoPrueba(tipoPruebaNew);
            }
            if (hojaPruebasNew != null) {
                hojaPruebasNew = em.getReference(hojaPruebasNew.getClass(), hojaPruebasNew.getTestsheet());
                pruebas.setHojaPruebas(hojaPruebasNew);
            }
            pruebas = em.merge(pruebas);
            if (usuariosOld != null && !usuariosOld.equals(usuariosNew)) {
                usuariosOld.getPruebasCollection().remove(pruebas);
                usuariosOld = em.merge(usuariosOld);
            }
            if (usuariosNew != null && !usuariosNew.equals(usuariosOld)) {
                usuariosNew.getPruebasCollection().add(pruebas);
                usuariosNew = em.merge(usuariosNew);
            }
            if (tipoPruebaOld != null && !tipoPruebaOld.equals(tipoPruebaNew)) {
                tipoPruebaOld.getPruebasCollection().remove(pruebas);
                tipoPruebaOld = em.merge(tipoPruebaOld);
            }
            if (tipoPruebaNew != null && !tipoPruebaNew.equals(tipoPruebaOld)) {
                tipoPruebaNew.getPruebasCollection().add(pruebas);
                tipoPruebaNew = em.merge(tipoPruebaNew);
            }
            if (hojaPruebasOld != null && !hojaPruebasOld.equals(hojaPruebasNew)) {
                hojaPruebasOld.getPruebasCollection().remove(pruebas);
                hojaPruebasOld = em.merge(hojaPruebasOld);
            }
            if (hojaPruebasNew != null && !hojaPruebasNew.equals(hojaPruebasOld)) {
                hojaPruebasNew.getPruebasCollection().add(pruebas);
                hojaPruebasNew = em.merge(hojaPruebasNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = pruebas.getIdPruebas();
                if (findPruebas(id) == null) {
                    throw new NonexistentEntityException("The pruebas with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Pruebas pruebas;
            try {
                pruebas = em.getReference(Pruebas.class, id);
                pruebas.getIdPruebas();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The pruebas with id " + id + " no longer exists.", enfe);
            }
            Usuarios usuarios = pruebas.getUsuarios();
            if (usuarios != null) {
                usuarios.getPruebasCollection().remove(pruebas);
                usuarios = em.merge(usuarios);
            }
            TipoPrueba tipoPrueba = pruebas.getTipoPrueba();
            if (tipoPrueba != null) {
                tipoPrueba.getPruebasCollection().remove(pruebas);
                tipoPrueba = em.merge(tipoPrueba);
            }
            HojaPruebas hojaPruebas = pruebas.getHojaPruebas();
            if (hojaPruebas != null) {
                hojaPruebas.getPruebasCollection().remove(pruebas);
                hojaPruebas = em.merge(hojaPruebas);
            }
            em.remove(pruebas);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Pruebas> findPruebasEntities() {
        return findPruebasEntities(true, -1, -1);
    }

    public List<Pruebas> findPruebasEntities(int maxResults, int firstResult) {
        return findPruebasEntities(false, maxResults, firstResult);
    }

    private List<Pruebas> findPruebasEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Pruebas.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Pruebas findPruebas(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Pruebas.class, id);
        } finally {
           
        }
    }

    public int getPruebasCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Pruebas> rt = cq.from(Pruebas.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    public List<Integer> listaPruebasMaxNoFin(long idHojaPruebas){
        EntityManager em = getEntityManager();
        Query q = em.createQuery("SELECT MAX(p.idPruebas) FROM Pruebas p WHERE p.finalizada = 'Y' AND p.hojaPruebasFor.testsheet = ?1 GROUP BY p.tipoPrueba");
        q.setParameter(1,idHojaPruebas);
        return q.getResultList();
    }

    public int getPruebaNoFinalizadaTipo(int idHojaPruebas, int tipoPrueba ){
        EntityManager em = getEntityManager();
        Query q = em.createQuery("SELECT MAX(p.idPruebas) FROM Pruebas p WHERE p.finalizada = 'N' AND p.hojaPruebas.testsheet = ?1 AND p.tipoPrueba.testtype = ?2");
        q.setParameter(1,idHojaPruebas);
        q.setParameter(2,tipoPrueba);
        int idPrueba = -1;
        try{
            idPrueba = ((Integer)q.getSingleResult());
            return idPrueba;
        }catch(NoResultException | NullPointerException ne){
            return -1;
        }
    }//end of method getPruebaNoFinalizada

    
}
