/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.dao;

import com.soltelec.dao.conexion.PersistenceController;
import com.soltelec.dao.exceptions.NonexistentEntityException;
import com.soltelec.dao.exceptions.PreexistingEntityException;
import com.soltelec.model.Grupos;
import com.soltelec.model.Ttpxdgp;
import com.soltelec.model.TtpxdgpPK;
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
public class TtpxdgpJpaController {

    public EntityManager getEntityManager() {
        return PersistenceController.getEntityManager();
    }

    public void create(Ttpxdgp ttpxdgp) throws PreexistingEntityException, Exception {
        if (ttpxdgp.getTtpxdgpPK() == null) {
            ttpxdgp.setTtpxdgpPK(new TtpxdgpPK());
        }
        ttpxdgp.getTtpxdgpPK().setDefgroup(ttpxdgp.getGrupos().getDefgroup());
        ttpxdgp.getTtpxdgpPK().setTesttype(ttpxdgp.getTipoPrueba().getTesttype());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Grupos grupos = ttpxdgp.getGrupos();
            if (grupos != null) {
                grupos = em.getReference(grupos.getClass(), grupos.getDefgroup());
                ttpxdgp.setGrupos(grupos);
            }
            em.persist(ttpxdgp);
            if (grupos != null) {
                grupos.getTtpxdgpCollection().add(ttpxdgp);
                grupos = em.merge(grupos);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findTtpxdgp(ttpxdgp.getTtpxdgpPK()) != null) {
                throw new PreexistingEntityException("Ttpxdgp " + ttpxdgp + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Ttpxdgp ttpxdgp) throws NonexistentEntityException, Exception {
        ttpxdgp.getTtpxdgpPK().setDefgroup(ttpxdgp.getGrupos().getDefgroup());
        ttpxdgp.getTtpxdgpPK().setTesttype(ttpxdgp.getTipoPrueba().getTesttype());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Ttpxdgp persistentTtpxdgp = em.find(Ttpxdgp.class, ttpxdgp.getTtpxdgpPK());
            Grupos gruposOld = persistentTtpxdgp.getGrupos();
            Grupos gruposNew = ttpxdgp.getGrupos();
            if (gruposNew != null) {
                gruposNew = em.getReference(gruposNew.getClass(), gruposNew.getDefgroup());
                ttpxdgp.setGrupos(gruposNew);
            }
            ttpxdgp = em.merge(ttpxdgp);
            if (gruposOld != null && !gruposOld.equals(gruposNew)) {
                gruposOld.getTtpxdgpCollection().remove(ttpxdgp);
                gruposOld = em.merge(gruposOld);
            }
            if (gruposNew != null && !gruposNew.equals(gruposOld)) {
                gruposNew.getTtpxdgpCollection().add(ttpxdgp);
                gruposNew = em.merge(gruposNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                TtpxdgpPK id = ttpxdgp.getTtpxdgpPK();
                if (findTtpxdgp(id) == null) {
                    throw new NonexistentEntityException("The ttpxdgp with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(TtpxdgpPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Ttpxdgp ttpxdgp;
            try {
                ttpxdgp = em.getReference(Ttpxdgp.class, id);
                ttpxdgp.getTtpxdgpPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The ttpxdgp with id " + id + " no longer exists.", enfe);
            }
            Grupos grupos = ttpxdgp.getGrupos();
            if (grupos != null) {
                grupos.getTtpxdgpCollection().remove(ttpxdgp);
                grupos = em.merge(grupos);
            }
            em.remove(ttpxdgp);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Ttpxdgp> findTtpxdgpEntities() {
        return findTtpxdgpEntities(true, -1, -1);
    }

    public List<Ttpxdgp> findTtpxdgpEntities(int maxResults, int firstResult) {
        return findTtpxdgpEntities(false, maxResults, firstResult);
    }

    private List<Ttpxdgp> findTtpxdgpEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Ttpxdgp.class));
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

    public Ttpxdgp findTtpxdgp(TtpxdgpPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Ttpxdgp.class, id);
        } finally {
            em.close();
        }
    }

    public int getTtpxdgpCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Ttpxdgp> rt = cq.from(Ttpxdgp.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
