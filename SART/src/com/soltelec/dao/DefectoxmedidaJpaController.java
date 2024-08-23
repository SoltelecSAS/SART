/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.dao;

import com.soltelec.dao.conexion.PersistenceController;
import com.soltelec.dao.exceptions.NonexistentEntityException;
import com.soltelec.dao.exceptions.PreexistingEntityException;
import com.soltelec.model.Defectos;
import com.soltelec.model.Defectoxmedida;
import com.soltelec.model.DefectoxmedidaPK;
import com.soltelec.model.Medidas;
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
public class DefectoxmedidaJpaController {

    public EntityManager getEntityManager() {
        return PersistenceController.getEntityManager();
    }

    public void create(Defectoxmedida defectoxmedida) throws PreexistingEntityException, Exception {
        if (defectoxmedida.getDefectoxmedidaPK() == null) {
            defectoxmedida.setDefectoxmedidaPK(new DefectoxmedidaPK());
        }
        defectoxmedida.getDefectoxmedidaPK().setCardefault(defectoxmedida.getDefectos().getCardefault());
        defectoxmedida.getDefectoxmedidaPK().setMeasure(defectoxmedida.getMedidas().getMeasure());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Medidas medidas = defectoxmedida.getMedidas();
            if (medidas != null) {
                medidas = em.getReference(medidas.getClass(), medidas.getMeasure());
                defectoxmedida.setMedidas(medidas);
            }
            Defectos defectos = defectoxmedida.getDefectos();
            if (defectos != null) {
                defectos = em.getReference(defectos.getClass(), defectos.getCardefault());
                defectoxmedida.setDefectos(defectos);
            }
            em.persist(defectoxmedida);
            if (medidas != null) {
                medidas.getDefectoxmedidaCollection().add(defectoxmedida);
                medidas = em.merge(medidas);
            }
            if (defectos != null) {
                defectos.getDefectoxmedidaCollection().add(defectoxmedida);
                defectos = em.merge(defectos);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findDefectoxmedida(defectoxmedida.getDefectoxmedidaPK()) != null) {
                throw new PreexistingEntityException("Defectoxmedida " + defectoxmedida + " already exists.", ex);
            }
            throw ex;
        } finally {
           
        }
    }

    public void edit(Defectoxmedida defectoxmedida) throws NonexistentEntityException, Exception {
        defectoxmedida.getDefectoxmedidaPK().setCardefault(defectoxmedida.getDefectos().getCardefault());
        defectoxmedida.getDefectoxmedidaPK().setMeasure(defectoxmedida.getMedidas().getMeasure());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Defectoxmedida persistentDefectoxmedida = em.find(Defectoxmedida.class, defectoxmedida.getDefectoxmedidaPK());
            Medidas medidasOld = persistentDefectoxmedida.getMedidas();
            Medidas medidasNew = defectoxmedida.getMedidas();
            Defectos defectosOld = persistentDefectoxmedida.getDefectos();
            Defectos defectosNew = defectoxmedida.getDefectos();
            if (medidasNew != null) {
                medidasNew = em.getReference(medidasNew.getClass(), medidasNew.getMeasure());
                defectoxmedida.setMedidas(medidasNew);
            }
            if (defectosNew != null) {
                defectosNew = em.getReference(defectosNew.getClass(), defectosNew.getCardefault());
                defectoxmedida.setDefectos(defectosNew);
            }
            defectoxmedida = em.merge(defectoxmedida);
            if (medidasOld != null && !medidasOld.equals(medidasNew)) {
                medidasOld.getDefectoxmedidaCollection().remove(defectoxmedida);
                medidasOld = em.merge(medidasOld);
            }
            if (medidasNew != null && !medidasNew.equals(medidasOld)) {
                medidasNew.getDefectoxmedidaCollection().add(defectoxmedida);
                medidasNew = em.merge(medidasNew);
            }
            if (defectosOld != null && !defectosOld.equals(defectosNew)) {
                defectosOld.getDefectoxmedidaCollection().remove(defectoxmedida);
                defectosOld = em.merge(defectosOld);
            }
            if (defectosNew != null && !defectosNew.equals(defectosOld)) {
                defectosNew.getDefectoxmedidaCollection().add(defectoxmedida);
                defectosNew = em.merge(defectosNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                DefectoxmedidaPK id = defectoxmedida.getDefectoxmedidaPK();
                if (findDefectoxmedida(id) == null) {
                    throw new NonexistentEntityException("The defectoxmedida with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
           
        }
    }

    public void destroy(DefectoxmedidaPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Defectoxmedida defectoxmedida;
            try {
                defectoxmedida = em.getReference(Defectoxmedida.class, id);
                defectoxmedida.getDefectoxmedidaPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The defectoxmedida with id " + id + " no longer exists.", enfe);
            }
            Medidas medidas = defectoxmedida.getMedidas();
            if (medidas != null) {
                medidas.getDefectoxmedidaCollection().remove(defectoxmedida);
                medidas = em.merge(medidas);
            }
            Defectos defectos = defectoxmedida.getDefectos();
            if (defectos != null) {
                defectos.getDefectoxmedidaCollection().remove(defectoxmedida);
                defectos = em.merge(defectos);
            }
            em.remove(defectoxmedida);
            em.getTransaction().commit();
        } finally {
           
        }
    }

    public List<Defectoxmedida> findDefectoxmedidaEntities() {
        return findDefectoxmedidaEntities(true, -1, -1);
    }

    public List<Defectoxmedida> findDefectoxmedidaEntities(int maxResults, int firstResult) {
        return findDefectoxmedidaEntities(false, maxResults, firstResult);
    }

    private List<Defectoxmedida> findDefectoxmedidaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Defectoxmedida.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
           
        }
    }

    public Defectoxmedida findDefectoxmedida(DefectoxmedidaPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Defectoxmedida.class, id);
        } finally {
           
        }
    }

    public int getDefectoxmedidaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Defectoxmedida> rt = cq.from(Defectoxmedida.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            
        }
    }

}
