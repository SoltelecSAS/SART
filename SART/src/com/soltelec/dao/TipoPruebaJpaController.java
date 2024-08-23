/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.dao;

import com.soltelec.dao.conexion.PersistenceController;
import com.soltelec.dao.exceptions.IllegalOrphanException;
import com.soltelec.dao.exceptions.NonexistentEntityException;
import com.soltelec.dao.exceptions.PreexistingEntityException;
import com.soltelec.model.Pruebas;
import com.soltelec.model.TipoPrueba;
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
public class TipoPruebaJpaController {

    public EntityManager getEntityManager() {
        return PersistenceController.getEntityManager();
    }

    public void create(TipoPrueba tipoPrueba) throws PreexistingEntityException, Exception {
        if (tipoPrueba.getPruebasCollection() == null) {
            tipoPrueba.setPruebasCollection(new ArrayList<Pruebas>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Pruebas> attachedPruebasCollection = new ArrayList<>();
            for (Pruebas pruebasCollectionPruebasToAttach : tipoPrueba.getPruebasCollection()) {
                pruebasCollectionPruebasToAttach = em.getReference(pruebasCollectionPruebasToAttach.getClass(), pruebasCollectionPruebasToAttach.getIdPruebas());
                attachedPruebasCollection.add(pruebasCollectionPruebasToAttach);
            }
            tipoPrueba.setPruebasCollection(attachedPruebasCollection);
            em.persist(tipoPrueba);
            for (Pruebas pruebasCollectionPruebas : tipoPrueba.getPruebasCollection()) {
                TipoPrueba oldTipoPruebaOfPruebasCollectionPruebas = pruebasCollectionPruebas.getTipoPrueba();
                pruebasCollectionPruebas.setTipoPrueba(tipoPrueba);
                pruebasCollectionPruebas = em.merge(pruebasCollectionPruebas);
                if (oldTipoPruebaOfPruebasCollectionPruebas != null) {
                    oldTipoPruebaOfPruebasCollectionPruebas.getPruebasCollection().remove(pruebasCollectionPruebas);
                    oldTipoPruebaOfPruebasCollectionPruebas = em.merge(oldTipoPruebaOfPruebasCollectionPruebas);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findTipoPrueba(tipoPrueba.getTesttype()) != null) {
                throw new PreexistingEntityException("TipoPrueba " + tipoPrueba + " already exists.", ex);
            }
            throw ex;
        } finally {
           
        }
    }

    public void edit(TipoPrueba tipoPrueba) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TipoPrueba persistentTipoPrueba = em.find(TipoPrueba.class, tipoPrueba.getTesttype());
            Collection<Pruebas> pruebasCollectionOld = persistentTipoPrueba.getPruebasCollection();
            Collection<Pruebas> pruebasCollectionNew = tipoPrueba.getPruebasCollection();
            List<String> illegalOrphanMessages = null;
            for (Pruebas pruebasCollectionOldPruebas : pruebasCollectionOld) {
                if (!pruebasCollectionNew.contains(pruebasCollectionOldPruebas)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain Pruebas " + pruebasCollectionOldPruebas + " since its tipoPrueba field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Pruebas> attachedPruebasCollectionNew = new ArrayList<>();
            for (Pruebas pruebasCollectionNewPruebasToAttach : pruebasCollectionNew) {
                pruebasCollectionNewPruebasToAttach = em.getReference(pruebasCollectionNewPruebasToAttach.getClass(), pruebasCollectionNewPruebasToAttach.getIdPruebas());
                attachedPruebasCollectionNew.add(pruebasCollectionNewPruebasToAttach);
            }
            pruebasCollectionNew = attachedPruebasCollectionNew;
            tipoPrueba.setPruebasCollection(pruebasCollectionNew);
            tipoPrueba = em.merge(tipoPrueba);
            for (Pruebas pruebasCollectionNewPruebas : pruebasCollectionNew) {
                if (!pruebasCollectionOld.contains(pruebasCollectionNewPruebas)) {
                    TipoPrueba oldTipoPruebaOfPruebasCollectionNewPruebas = pruebasCollectionNewPruebas.getTipoPrueba();
                    pruebasCollectionNewPruebas.setTipoPrueba(tipoPrueba);
                    pruebasCollectionNewPruebas = em.merge(pruebasCollectionNewPruebas);
                    if (oldTipoPruebaOfPruebasCollectionNewPruebas != null && !oldTipoPruebaOfPruebasCollectionNewPruebas.equals(tipoPrueba)) {
                        oldTipoPruebaOfPruebasCollectionNewPruebas.getPruebasCollection().remove(pruebasCollectionNewPruebas);
                        oldTipoPruebaOfPruebasCollectionNewPruebas = em.merge(oldTipoPruebaOfPruebasCollectionNewPruebas);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = tipoPrueba.getTesttype();
                if (findTipoPrueba(id) == null) {
                    throw new NonexistentEntityException("The tipoPrueba with id " + id + " no longer exists.");
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
            TipoPrueba tipoPrueba;
            try {
                tipoPrueba = em.getReference(TipoPrueba.class, id);
                tipoPrueba.getTesttype();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The tipoPrueba with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Pruebas> pruebasCollectionOrphanCheck = tipoPrueba.getPruebasCollection();
            for (Pruebas pruebasCollectionOrphanCheckPruebas : pruebasCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This TipoPrueba (" + tipoPrueba + ") cannot be destroyed since the Pruebas " + pruebasCollectionOrphanCheckPruebas + " in its pruebasCollection field has a non-nullable tipoPrueba field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(tipoPrueba);
            em.getTransaction().commit();
        } finally {
           
        }
    }

    public List<TipoPrueba> findTipoPruebaEntities() {
        return findTipoPruebaEntities(true, -1, -1);
    }

    public List<TipoPrueba> findTipoPruebaEntities(int maxResults, int firstResult) {
        return findTipoPruebaEntities(false, maxResults, firstResult);
    }

    private List<TipoPrueba> findTipoPruebaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(TipoPrueba.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
           
        }
    }

    public TipoPrueba findTipoPrueba(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(TipoPrueba.class, id);
        } finally {
          
        }
    }

    public int getTipoPruebaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<TipoPrueba> rt = cq.from(TipoPrueba.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
           
        }
    }

}
