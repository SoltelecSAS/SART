/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.dao;

import com.soltelec.dao.conexion.PersistenceController;
import com.soltelec.dao.exceptions.IllegalOrphanException;
import com.soltelec.dao.exceptions.NonexistentEntityException;
import com.soltelec.model.Defectoxmedida;
import com.soltelec.model.Medidas;
import com.soltelec.model.TiposMedida;
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
public class MedidasJpaController {

    public EntityManager getEntityManager() {
        return PersistenceController.getEntityManager();
    }

    public void create(Medidas medidas) {
        if (medidas.getDefectoxmedidaCollection() == null) {
            medidas.setDefectoxmedidaCollection(new ArrayList<Defectoxmedida>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TiposMedida tiposMedida = medidas.getTiposMedida();
            if (tiposMedida != null) {
                tiposMedida = em.getReference(tiposMedida.getClass(), tiposMedida.getMeasuretype());
                medidas.setTiposMedida(tiposMedida);
            }
            Collection<Defectoxmedida> attachedDefectoxmedidaCollection = new ArrayList<>();
            for (Defectoxmedida defectoxmedidaCollectionDefectoxmedidaToAttach : medidas.getDefectoxmedidaCollection()) {
                defectoxmedidaCollectionDefectoxmedidaToAttach = em.getReference(defectoxmedidaCollectionDefectoxmedidaToAttach.getClass(), defectoxmedidaCollectionDefectoxmedidaToAttach.getDefectoxmedidaPK());
                attachedDefectoxmedidaCollection.add(defectoxmedidaCollectionDefectoxmedidaToAttach);
            }
            medidas.setDefectoxmedidaCollection(attachedDefectoxmedidaCollection);
            em.persist(medidas);
            if (tiposMedida != null) {
                tiposMedida.getMedidasCollection().add(medidas);
                tiposMedida = em.merge(tiposMedida);
            }
            for (Defectoxmedida defectoxmedidaCollectionDefectoxmedida : medidas.getDefectoxmedidaCollection()) {
                Medidas oldMedidasOfDefectoxmedidaCollectionDefectoxmedida = defectoxmedidaCollectionDefectoxmedida.getMedidas();
                defectoxmedidaCollectionDefectoxmedida.setMedidas(medidas);
                defectoxmedidaCollectionDefectoxmedida = em.merge(defectoxmedidaCollectionDefectoxmedida);
                if (oldMedidasOfDefectoxmedidaCollectionDefectoxmedida != null) {
                    oldMedidasOfDefectoxmedidaCollectionDefectoxmedida.getDefectoxmedidaCollection().remove(defectoxmedidaCollectionDefectoxmedida);
                    oldMedidasOfDefectoxmedidaCollectionDefectoxmedida = em.merge(oldMedidasOfDefectoxmedidaCollectionDefectoxmedida);
                }
            }
            em.getTransaction().commit();
        } finally {
           
        }
    }

    public void edit(Medidas medidas) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Medidas persistentMedidas = em.find(Medidas.class, medidas.getMeasure());
            TiposMedida tiposMedidaOld = persistentMedidas.getTiposMedida();
            TiposMedida tiposMedidaNew = medidas.getTiposMedida();
            Collection<Defectoxmedida> defectoxmedidaCollectionOld = persistentMedidas.getDefectoxmedidaCollection();
            Collection<Defectoxmedida> defectoxmedidaCollectionNew = medidas.getDefectoxmedidaCollection();
            List<String> illegalOrphanMessages = null;
            for (Defectoxmedida defectoxmedidaCollectionOldDefectoxmedida : defectoxmedidaCollectionOld) {
                if (!defectoxmedidaCollectionNew.contains(defectoxmedidaCollectionOldDefectoxmedida)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain Defectoxmedida " + defectoxmedidaCollectionOldDefectoxmedida + " since its medidas field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (tiposMedidaNew != null) {
                tiposMedidaNew = em.getReference(tiposMedidaNew.getClass(), tiposMedidaNew.getMeasuretype());
                medidas.setTiposMedida(tiposMedidaNew);
            }
            Collection<Defectoxmedida> attachedDefectoxmedidaCollectionNew = new ArrayList<>();
            for (Defectoxmedida defectoxmedidaCollectionNewDefectoxmedidaToAttach : defectoxmedidaCollectionNew) {
                defectoxmedidaCollectionNewDefectoxmedidaToAttach = em.getReference(defectoxmedidaCollectionNewDefectoxmedidaToAttach.getClass(), defectoxmedidaCollectionNewDefectoxmedidaToAttach.getDefectoxmedidaPK());
                attachedDefectoxmedidaCollectionNew.add(defectoxmedidaCollectionNewDefectoxmedidaToAttach);
            }
            defectoxmedidaCollectionNew = attachedDefectoxmedidaCollectionNew;
            medidas.setDefectoxmedidaCollection(defectoxmedidaCollectionNew);
            medidas = em.merge(medidas);
            if (tiposMedidaOld != null && !tiposMedidaOld.equals(tiposMedidaNew)) {
                tiposMedidaOld.getMedidasCollection().remove(medidas);
                tiposMedidaOld = em.merge(tiposMedidaOld);
            }
            if (tiposMedidaNew != null && !tiposMedidaNew.equals(tiposMedidaOld)) {
                tiposMedidaNew.getMedidasCollection().add(medidas);
                tiposMedidaNew = em.merge(tiposMedidaNew);
            }
            for (Defectoxmedida defectoxmedidaCollectionNewDefectoxmedida : defectoxmedidaCollectionNew) {
                if (!defectoxmedidaCollectionOld.contains(defectoxmedidaCollectionNewDefectoxmedida)) {
                    Medidas oldMedidasOfDefectoxmedidaCollectionNewDefectoxmedida = defectoxmedidaCollectionNewDefectoxmedida.getMedidas();
                    defectoxmedidaCollectionNewDefectoxmedida.setMedidas(medidas);
                    defectoxmedidaCollectionNewDefectoxmedida = em.merge(defectoxmedidaCollectionNewDefectoxmedida);
                    if (oldMedidasOfDefectoxmedidaCollectionNewDefectoxmedida != null && !oldMedidasOfDefectoxmedidaCollectionNewDefectoxmedida.equals(medidas)) {
                        oldMedidasOfDefectoxmedidaCollectionNewDefectoxmedida.getDefectoxmedidaCollection().remove(defectoxmedidaCollectionNewDefectoxmedida);
                        oldMedidasOfDefectoxmedidaCollectionNewDefectoxmedida = em.merge(oldMedidasOfDefectoxmedidaCollectionNewDefectoxmedida);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = medidas.getMeasure();
                if (findMedidas(id) == null) {
                    throw new NonexistentEntityException("The medidas with id " + id + " no longer exists.");
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
            Medidas medidas;
            try {
                medidas = em.getReference(Medidas.class, id);
                medidas.getMeasure();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The medidas with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Defectoxmedida> defectoxmedidaCollectionOrphanCheck = medidas.getDefectoxmedidaCollection();
            for (Defectoxmedida defectoxmedidaCollectionOrphanCheckDefectoxmedida : defectoxmedidaCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This Medidas (" + medidas + ") cannot be destroyed since the Defectoxmedida " + defectoxmedidaCollectionOrphanCheckDefectoxmedida + " in its defectoxmedidaCollection field has a non-nullable medidas field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            TiposMedida tiposMedida = medidas.getTiposMedida();
            if (tiposMedida != null) {
                tiposMedida.getMedidasCollection().remove(medidas);
                tiposMedida = em.merge(tiposMedida);
            }
            em.remove(medidas);
            em.getTransaction().commit();
        } finally {
           
        }
    }

    public List<Medidas> findMedidasEntities() {
        return findMedidasEntities(true, -1, -1);
    }

    public List<Medidas> findMedidasEntities(int maxResults, int firstResult) {
        return findMedidasEntities(false, maxResults, firstResult);
    }

    private List<Medidas> findMedidasEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Medidas.class));
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

    public Medidas findMedidas(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Medidas.class, id);
        } finally {
            em.close();
        }
    }

    public int getMedidasCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Medidas> rt = cq.from(Medidas.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
