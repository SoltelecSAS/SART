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
import com.soltelec.model.Ttpxdgp;
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
public class GruposJpaController {

    public EntityManager getEntityManager() {
        return PersistenceController.getEntityManager();
    }

    public void create(Grupos grupos) {
        if (grupos.getTtpxdgpCollection() == null) {
            grupos.setTtpxdgpCollection(new ArrayList<Ttpxdgp>());
        }
        if (grupos.getSubGruposCollection() == null) {
            grupos.setSubGruposCollection(new ArrayList<SubGrupos>());
        }
       /* if (grupos.getDefectosCollection() == null) {
            grupos.setDefectosCollection(new ArrayList<Defectos>());
        }*/
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Ttpxdgp> attachedTtpxdgpCollection = new ArrayList<>();
            for (Ttpxdgp ttpxdgpCollectionTtpxdgpToAttach : grupos.getTtpxdgpCollection()) {
                ttpxdgpCollectionTtpxdgpToAttach = em.getReference(ttpxdgpCollectionTtpxdgpToAttach.getClass(), ttpxdgpCollectionTtpxdgpToAttach.getTtpxdgpPK());
                attachedTtpxdgpCollection.add(ttpxdgpCollectionTtpxdgpToAttach);
            }
            grupos.setTtpxdgpCollection(attachedTtpxdgpCollection);
            Collection<SubGrupos> attachedSubGruposCollection = new ArrayList<>();
            for (SubGrupos subGruposCollectionSubGruposToAttach : grupos.getSubGruposCollection()) {
                subGruposCollectionSubGruposToAttach = em.getReference(subGruposCollectionSubGruposToAttach.getClass(), subGruposCollectionSubGruposToAttach.getScdefgroupsub());
                attachedSubGruposCollection.add(subGruposCollectionSubGruposToAttach);
            }
            grupos.setSubGruposCollection(attachedSubGruposCollection);
            Collection<Defectos> attachedDefectosCollection = new ArrayList<>();
           /* for (Defectos defectosCollectionDefectosToAttach : grupos.getDefectosCollection()) {
                defectosCollectionDefectosToAttach = em.getReference(defectosCollectionDefectosToAttach.getClass(), defectosCollectionDefectosToAttach.getCardefault());
                attachedDefectosCollection.add(defectosCollectionDefectosToAttach);
            }*/
          //  grupos.setDefectosCollection(attachedDefectosCollection);
            em.persist(grupos);
            for (Ttpxdgp ttpxdgpCollectionTtpxdgp : grupos.getTtpxdgpCollection()) {
                Grupos oldGruposOfTtpxdgpCollectionTtpxdgp = ttpxdgpCollectionTtpxdgp.getGrupos();
                ttpxdgpCollectionTtpxdgp.setGrupos(grupos);
                ttpxdgpCollectionTtpxdgp = em.merge(ttpxdgpCollectionTtpxdgp);
                if (oldGruposOfTtpxdgpCollectionTtpxdgp != null) {
                    oldGruposOfTtpxdgpCollectionTtpxdgp.getTtpxdgpCollection().remove(ttpxdgpCollectionTtpxdgp);
                    oldGruposOfTtpxdgpCollectionTtpxdgp = em.merge(oldGruposOfTtpxdgpCollectionTtpxdgp);
                }
            }
            for (SubGrupos subGruposCollectionSubGrupos : grupos.getSubGruposCollection()) {
                Grupos oldGruposOfSubGruposCollectionSubGrupos = subGruposCollectionSubGrupos.getGrupos();
                subGruposCollectionSubGrupos.setGrupos(grupos);
                subGruposCollectionSubGrupos = em.merge(subGruposCollectionSubGrupos);
                if (oldGruposOfSubGruposCollectionSubGrupos != null) {
                    oldGruposOfSubGruposCollectionSubGrupos.getSubGruposCollection().remove(subGruposCollectionSubGrupos);
                    oldGruposOfSubGruposCollectionSubGrupos = em.merge(oldGruposOfSubGruposCollectionSubGrupos);
                }
            }
           /* for (Defectos defectosCollectionDefectos : grupos.getDefectosCollection()) {
                Grupos oldGruposOfDefectosCollectionDefectos =null;
                //defectosCollectionDefectos.setGrupos(grupos);
                defectosCollectionDefectos = em.merge(defectosCollectionDefectos);
                if (oldGruposOfDefectosCollectionDefectos != null) {
                    //oldGruposOfDefectosCollectionDefectos.getDefectosCollection().remove(defectosCollectionDefectos);
                    oldGruposOfDefectosCollectionDefectos = em.merge(oldGruposOfDefectosCollectionDefectos);
                }
            }*/
            em.getTransaction().commit();
        } finally {
            
        }
    }

    public void edit(Grupos grupos) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Grupos persistentGrupos = em.find(Grupos.class, grupos.getDefgroup());
            Collection<Ttpxdgp> ttpxdgpCollectionOld = persistentGrupos.getTtpxdgpCollection();
            Collection<Ttpxdgp> ttpxdgpCollectionNew = grupos.getTtpxdgpCollection();
            Collection<SubGrupos> subGruposCollectionOld = persistentGrupos.getSubGruposCollection();
            Collection<SubGrupos> subGruposCollectionNew = grupos.getSubGruposCollection();
            Collection<Defectos> defectosCollectionOld = null;
            Collection<Defectos> defectosCollectionNew = null;
            List<String> illegalOrphanMessages = null;
            for (Ttpxdgp ttpxdgpCollectionOldTtpxdgp : ttpxdgpCollectionOld) {
                if (!ttpxdgpCollectionNew.contains(ttpxdgpCollectionOldTtpxdgp)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain Ttpxdgp " + ttpxdgpCollectionOldTtpxdgp + " since its grupos field is not nullable.");
                }
            }
            for (Defectos defectosCollectionOldDefectos : defectosCollectionOld) {
                if (!defectosCollectionNew.contains(defectosCollectionOldDefectos)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain Defectos " + defectosCollectionOldDefectos + " since its grupos field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Ttpxdgp> attachedTtpxdgpCollectionNew = new ArrayList<>();
            for (Ttpxdgp ttpxdgpCollectionNewTtpxdgpToAttach : ttpxdgpCollectionNew) {
                ttpxdgpCollectionNewTtpxdgpToAttach = em.getReference(ttpxdgpCollectionNewTtpxdgpToAttach.getClass(), ttpxdgpCollectionNewTtpxdgpToAttach.getTtpxdgpPK());
                attachedTtpxdgpCollectionNew.add(ttpxdgpCollectionNewTtpxdgpToAttach);
            }
            ttpxdgpCollectionNew = attachedTtpxdgpCollectionNew;
            grupos.setTtpxdgpCollection(ttpxdgpCollectionNew);
            Collection<SubGrupos> attachedSubGruposCollectionNew = new ArrayList<>();
            for (SubGrupos subGruposCollectionNewSubGruposToAttach : subGruposCollectionNew) {
                subGruposCollectionNewSubGruposToAttach = em.getReference(subGruposCollectionNewSubGruposToAttach.getClass(), subGruposCollectionNewSubGruposToAttach.getScdefgroupsub());
                attachedSubGruposCollectionNew.add(subGruposCollectionNewSubGruposToAttach);
            }
            subGruposCollectionNew = attachedSubGruposCollectionNew;
            grupos.setSubGruposCollection(subGruposCollectionNew);
            Collection<Defectos> attachedDefectosCollectionNew = new ArrayList<>();
            for (Defectos defectosCollectionNewDefectosToAttach : defectosCollectionNew) {
                defectosCollectionNewDefectosToAttach = em.getReference(defectosCollectionNewDefectosToAttach.getClass(), defectosCollectionNewDefectosToAttach.getCardefault());
                attachedDefectosCollectionNew.add(defectosCollectionNewDefectosToAttach);
            }
            defectosCollectionNew = attachedDefectosCollectionNew;
           // grupos.setDefectosCollection(defectosCollectionNew);
            grupos = em.merge(grupos);
            for (Ttpxdgp ttpxdgpCollectionNewTtpxdgp : ttpxdgpCollectionNew) {
                if (!ttpxdgpCollectionOld.contains(ttpxdgpCollectionNewTtpxdgp)) {
                    Grupos oldGruposOfTtpxdgpCollectionNewTtpxdgp = ttpxdgpCollectionNewTtpxdgp.getGrupos();
                    ttpxdgpCollectionNewTtpxdgp.setGrupos(grupos);
                    ttpxdgpCollectionNewTtpxdgp = em.merge(ttpxdgpCollectionNewTtpxdgp);
                    if (oldGruposOfTtpxdgpCollectionNewTtpxdgp != null && !oldGruposOfTtpxdgpCollectionNewTtpxdgp.equals(grupos)) {
                        oldGruposOfTtpxdgpCollectionNewTtpxdgp.getTtpxdgpCollection().remove(ttpxdgpCollectionNewTtpxdgp);
                        oldGruposOfTtpxdgpCollectionNewTtpxdgp = em.merge(oldGruposOfTtpxdgpCollectionNewTtpxdgp);
                    }
                }
            }
            for (SubGrupos subGruposCollectionOldSubGrupos : subGruposCollectionOld) {
                if (!subGruposCollectionNew.contains(subGruposCollectionOldSubGrupos)) {
                    subGruposCollectionOldSubGrupos.setGrupos(null);
                    subGruposCollectionOldSubGrupos = em.merge(subGruposCollectionOldSubGrupos);
                }
            }
            for (SubGrupos subGruposCollectionNewSubGrupos : subGruposCollectionNew) {
                if (!subGruposCollectionOld.contains(subGruposCollectionNewSubGrupos)) {
                    Grupos oldGruposOfSubGruposCollectionNewSubGrupos = subGruposCollectionNewSubGrupos.getGrupos();
                    subGruposCollectionNewSubGrupos.setGrupos(grupos);
                    subGruposCollectionNewSubGrupos = em.merge(subGruposCollectionNewSubGrupos);
                    if (oldGruposOfSubGruposCollectionNewSubGrupos != null && !oldGruposOfSubGruposCollectionNewSubGrupos.equals(grupos)) {
                        oldGruposOfSubGruposCollectionNewSubGrupos.getSubGruposCollection().remove(subGruposCollectionNewSubGrupos);
                        oldGruposOfSubGruposCollectionNewSubGrupos = em.merge(oldGruposOfSubGruposCollectionNewSubGrupos);
                    }
                }
            }
            for (Defectos defectosCollectionNewDefectos : defectosCollectionNew) {
                if (!defectosCollectionOld.contains(defectosCollectionNewDefectos)) {
                    Grupos oldGruposOfDefectosCollectionNewDefectos = null;
                   // defectosCollectionNewDefectos.setGrupos(grupos);
                    defectosCollectionNewDefectos = em.merge(defectosCollectionNewDefectos);
                    if (oldGruposOfDefectosCollectionNewDefectos != null && !oldGruposOfDefectosCollectionNewDefectos.equals(grupos)) {
                       // oldGruposOfDefectosCollectionNewDefectos.getDefectosCollection().remove(defectosCollectionNewDefectos);
                        oldGruposOfDefectosCollectionNewDefectos = em.merge(oldGruposOfDefectosCollectionNewDefectos);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = grupos.getDefgroup();
                if (findGrupos(id) == null) {
                    throw new NonexistentEntityException("The grupos with id " + id + " no longer exists.");
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
            Grupos grupos;
            try {
                grupos = em.getReference(Grupos.class, id);
                grupos.getDefgroup();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The grupos with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Ttpxdgp> ttpxdgpCollectionOrphanCheck = grupos.getTtpxdgpCollection();
            for (Ttpxdgp ttpxdgpCollectionOrphanCheckTtpxdgp : ttpxdgpCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This Grupos (" + grupos + ") cannot be destroyed since the Ttpxdgp " + ttpxdgpCollectionOrphanCheckTtpxdgp + " in its ttpxdgpCollection field has a non-nullable grupos field.");
            }
            Collection<Defectos> defectosCollectionOrphanCheck = null;
            for (Defectos defectosCollectionOrphanCheckDefectos : defectosCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This Grupos (" + grupos + ") cannot be destroyed since the Defectos " + defectosCollectionOrphanCheckDefectos + " in its defectosCollection field has a non-nullable grupos field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<SubGrupos> subGruposCollection = grupos.getSubGruposCollection();
            for (SubGrupos subGruposCollectionSubGrupos : subGruposCollection) {
                subGruposCollectionSubGrupos.setGrupos(null);
                subGruposCollectionSubGrupos = em.merge(subGruposCollectionSubGrupos);
            }
            em.remove(grupos);
            em.getTransaction().commit();
        } finally {
           
        }
    }

    public List<Grupos> findGruposEntities() {
        return findGruposEntities(true, -1, -1);
    }

    public List<Grupos> findGruposEntities(int maxResults, int firstResult) {
        return findGruposEntities(false, maxResults, firstResult);
    }

    private List<Grupos> findGruposEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Grupos.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
           
        }
    }

    public Grupos findGrupos(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Grupos.class, id);
        } finally {
           
        }
    }

    public int getGruposCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Grupos> rt = cq.from(Grupos.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
         
        }
    }

}
