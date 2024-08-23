/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.dao;

import com.soltelec.dao.conexion.PersistenceController;
import com.soltelec.dao.exceptions.NonexistentEntityException;
import com.soltelec.model.Defectos;
import com.soltelec.model.Permisibles;
import com.soltelec.model.TiposMedida;
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
public class PermisiblesJpaController {

    public EntityManager getEntityManager() {
        return PersistenceController.getEntityManager();
    }

    public void create(Permisibles permisibles) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TiposMedida tiposMedida = permisibles.getTiposMedida();
            if (tiposMedida != null) {
                tiposMedida = em.getReference(tiposMedida.getClass(), tiposMedida.getMeasuretype());
                permisibles.setTiposMedida(tiposMedida);
            }
            Defectos defectos = permisibles.getDefectos();
            if (defectos != null) {
                defectos = em.getReference(defectos.getClass(), defectos.getCardefault());
                permisibles.setDefectos(defectos);
            }
            em.persist(permisibles);
            if (tiposMedida != null) {
                tiposMedida.getPermisiblesCollection().add(permisibles);
                tiposMedida = em.merge(tiposMedida);
            }
            if (defectos != null) {
                defectos.getPermisiblesCollection().add(permisibles);
                defectos = em.merge(defectos);
            }
            em.getTransaction().commit();
        } finally {
            
        }
    }

    public void edit(Permisibles permisibles) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Permisibles persistentPermisibles = em.find(Permisibles.class, permisibles.getIdpermisible());
            TiposMedida tiposMedidaOld = persistentPermisibles.getTiposMedida();
            TiposMedida tiposMedidaNew = permisibles.getTiposMedida();
            Defectos defectosOld = persistentPermisibles.getDefectos();
            Defectos defectosNew = permisibles.getDefectos();
            if (tiposMedidaNew != null) {
                tiposMedidaNew = em.getReference(tiposMedidaNew.getClass(), tiposMedidaNew.getMeasuretype());
                permisibles.setTiposMedida(tiposMedidaNew);
            }
            if (defectosNew != null) {
                defectosNew = em.getReference(defectosNew.getClass(), defectosNew.getCardefault());
                permisibles.setDefectos(defectosNew);
            }
            permisibles = em.merge(permisibles);
            if (tiposMedidaOld != null && !tiposMedidaOld.equals(tiposMedidaNew)) {
                tiposMedidaOld.getPermisiblesCollection().remove(permisibles);
                tiposMedidaOld = em.merge(tiposMedidaOld);
            }
            if (tiposMedidaNew != null && !tiposMedidaNew.equals(tiposMedidaOld)) {
                tiposMedidaNew.getPermisiblesCollection().add(permisibles);
                tiposMedidaNew = em.merge(tiposMedidaNew);
            }
            if (defectosOld != null && !defectosOld.equals(defectosNew)) {
                defectosOld.getPermisiblesCollection().remove(permisibles);
                defectosOld = em.merge(defectosOld);
            }
            if (defectosNew != null && !defectosNew.equals(defectosOld)) {
                defectosNew.getPermisiblesCollection().add(permisibles);
                defectosNew = em.merge(defectosNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = permisibles.getIdpermisible();
                if (findPermisibles(id) == null) {
                    throw new NonexistentEntityException("The permisibles with id " + id + " no longer exists.");
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
            Permisibles permisibles;
            try {
                permisibles = em.getReference(Permisibles.class, id);
                permisibles.getIdpermisible();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The permisibles with id " + id + " no longer exists.", enfe);
            }
            TiposMedida tiposMedida = permisibles.getTiposMedida();
            if (tiposMedida != null) {
                tiposMedida.getPermisiblesCollection().remove(permisibles);
                tiposMedida = em.merge(tiposMedida);
            }
            Defectos defectos = permisibles.getDefectos();
            if (defectos != null) {
                defectos.getPermisiblesCollection().remove(permisibles);
                defectos = em.merge(defectos);
            }
            em.remove(permisibles);
            em.getTransaction().commit();
        } finally {
            
        }
    }

    public List<Permisibles> findPermisiblesEntities() {
        return findPermisiblesEntities(true, -1, -1);
    }

    public List<Permisibles> findPermisiblesEntities(int maxResults, int firstResult) {
        return findPermisiblesEntities(false, maxResults, firstResult);
    }

    private List<Permisibles> findPermisiblesEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Permisibles.class));
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

    public Permisibles findPermisibles(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Permisibles.class, id);
        } finally {
            em.close();
        }
    }

    public int getPermisiblesCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Permisibles> rt = cq.from(Permisibles.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
