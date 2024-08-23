/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.dao;

import com.soltelec.dao.conexion.PersistenceController;
import com.soltelec.dao.exceptions.IllegalOrphanException;
import com.soltelec.dao.exceptions.NonexistentEntityException;
import com.soltelec.model.Defectos;
import com.soltelec.model.Grupos;
import com.soltelec.model.SubGrupos;
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
public class SubGruposJpaController {

    public EntityManager getEntityManager() {
        return PersistenceController.getEntityManager();
    }

    public void create(SubGrupos subGrupos) {
        if (subGrupos.getDefectosCollection() == null) {
            subGrupos.setDefectosCollection(new ArrayList<Defectos>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Grupos grupos = subGrupos.getGrupos();
            if (grupos != null) {
                grupos = em.getReference(grupos.getClass(), grupos.getDefgroup());
                subGrupos.setGrupos(grupos);
            }
            Collection<Defectos> attachedDefectosCollection = new ArrayList<>();
            for (Defectos defectosCollectionDefectosToAttach : subGrupos.getDefectosCollection()) {
                defectosCollectionDefectosToAttach = em.getReference(defectosCollectionDefectosToAttach.getClass(), defectosCollectionDefectosToAttach.getCardefault());
                attachedDefectosCollection.add(defectosCollectionDefectosToAttach);
            }
            subGrupos.setDefectosCollection(attachedDefectosCollection);
            em.persist(subGrupos);
            if (grupos != null) {
                grupos.getSubGruposCollection().add(subGrupos);
                grupos = em.merge(grupos);
            }
            for (Defectos defectosCollectionDefectos : subGrupos.getDefectosCollection()) {
                SubGrupos oldSubGruposOfDefectosCollectionDefectos = defectosCollectionDefectos.getSubGrupos();
                defectosCollectionDefectos.setSubGrupos(subGrupos);
                defectosCollectionDefectos = em.merge(defectosCollectionDefectos);
                if (oldSubGruposOfDefectosCollectionDefectos != null) {
                    oldSubGruposOfDefectosCollectionDefectos.getDefectosCollection().remove(defectosCollectionDefectos);
                    oldSubGruposOfDefectosCollectionDefectos = em.merge(oldSubGruposOfDefectosCollectionDefectos);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(SubGrupos subGrupos) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            SubGrupos persistentSubGrupos = em.find(SubGrupos.class, subGrupos.getScdefgroupsub());
            Grupos gruposOld = persistentSubGrupos.getGrupos();
            Grupos gruposNew = subGrupos.getGrupos();
            Collection<Defectos> defectosCollectionOld = persistentSubGrupos.getDefectosCollection();
            Collection<Defectos> defectosCollectionNew = subGrupos.getDefectosCollection();
            List<String> illegalOrphanMessages = null;
            for (Defectos defectosCollectionOldDefectos : defectosCollectionOld) {
                if (!defectosCollectionNew.contains(defectosCollectionOldDefectos)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain Defectos " + defectosCollectionOldDefectos + " since its subGrupos field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (gruposNew != null) {
                gruposNew = em.getReference(gruposNew.getClass(), gruposNew.getDefgroup());
                subGrupos.setGrupos(gruposNew);
            }
            Collection<Defectos> attachedDefectosCollectionNew = new ArrayList<>();
            for (Defectos defectosCollectionNewDefectosToAttach : defectosCollectionNew) {
                defectosCollectionNewDefectosToAttach = em.getReference(defectosCollectionNewDefectosToAttach.getClass(), defectosCollectionNewDefectosToAttach.getCardefault());
                attachedDefectosCollectionNew.add(defectosCollectionNewDefectosToAttach);
            }
            defectosCollectionNew = attachedDefectosCollectionNew;
            subGrupos.setDefectosCollection(defectosCollectionNew);
            subGrupos = em.merge(subGrupos);
            if (gruposOld != null && !gruposOld.equals(gruposNew)) {
                gruposOld.getSubGruposCollection().remove(subGrupos);
                gruposOld = em.merge(gruposOld);
            }
            if (gruposNew != null && !gruposNew.equals(gruposOld)) {
                gruposNew.getSubGruposCollection().add(subGrupos);
                gruposNew = em.merge(gruposNew);
            }
            for (Defectos defectosCollectionNewDefectos : defectosCollectionNew) {
                if (!defectosCollectionOld.contains(defectosCollectionNewDefectos)) {
                    SubGrupos oldSubGruposOfDefectosCollectionNewDefectos = defectosCollectionNewDefectos.getSubGrupos();
                    defectosCollectionNewDefectos.setSubGrupos(subGrupos);
                    defectosCollectionNewDefectos = em.merge(defectosCollectionNewDefectos);
                    if (oldSubGruposOfDefectosCollectionNewDefectos != null && !oldSubGruposOfDefectosCollectionNewDefectos.equals(subGrupos)) {
                        oldSubGruposOfDefectosCollectionNewDefectos.getDefectosCollection().remove(defectosCollectionNewDefectos);
                        oldSubGruposOfDefectosCollectionNewDefectos = em.merge(oldSubGruposOfDefectosCollectionNewDefectos);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = subGrupos.getScdefgroupsub();
                if (findSubGrupos(id) == null) {
                    throw new NonexistentEntityException("The subGrupos with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            SubGrupos subGrupos;
            try {
                subGrupos = em.getReference(SubGrupos.class, id);
                subGrupos.getScdefgroupsub();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The subGrupos with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Defectos> defectosCollectionOrphanCheck = subGrupos.getDefectosCollection();
            for (Defectos defectosCollectionOrphanCheckDefectos : defectosCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This SubGrupos (" + subGrupos + ") cannot be destroyed since the Defectos " + defectosCollectionOrphanCheckDefectos + " in its defectosCollection field has a non-nullable subGrupos field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Grupos grupos = subGrupos.getGrupos();
            if (grupos != null) {
                grupos.getSubGruposCollection().remove(subGrupos);
                grupos = em.merge(grupos);
            }
            em.remove(subGrupos);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<SubGrupos> findSubGruposEntities() {
        return findSubGruposEntities(true, -1, -1);
    }

    public List<SubGrupos> findSubGruposEntities(int maxResults, int firstResult) {
        return findSubGruposEntities(false, maxResults, firstResult);
    }

    private List<SubGrupos> findSubGruposEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(SubGrupos.class));
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

    public SubGrupos findSubGrupos(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(SubGrupos.class, id);
        } finally {
            em.close();
        }
    }

    public int getSubGruposCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<SubGrupos> rt = cq.from(SubGrupos.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
