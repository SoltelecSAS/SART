/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.dao;

import com.soltelec.dao.conexion.PersistenceController;
import com.soltelec.dao.exceptions.NonexistentEntityException;
import com.soltelec.dao.exceptions.PreexistingEntityException;
import com.soltelec.model.Certificados;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author Administrador
 */
public class CertificadosJpaController {

    public EntityManager getEntityManager() {
        return PersistenceController.getEntityManager();
    }

    public void create(Certificados certificados) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(certificados);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findCertificados(certificados.getCertificate()) != null) {
                throw new PreexistingEntityException("Certificados " + certificados + " already exists.", ex);
            }
            throw ex;
        } finally {
            
        }
    }

    public void edit(Certificados certificados) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            certificados = em.merge(certificados);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = certificados.getCertificate();
                if (findCertificados(id) == null) {
                    throw new NonexistentEntityException("The certificados with id " + id + " no longer exists.");
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
            Certificados certificados;
            try {
                certificados = em.getReference(Certificados.class, id);
                certificados.getCertificate();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The certificados with id " + id + " no longer exists.", enfe);
            }
            em.remove(certificados);
            em.getTransaction().commit();
        } finally {
            
        }
    }

    public List<Certificados> findCertificadosEntities() {
        return findCertificadosEntities(true, -1, -1);
    }

    public List<Certificados> findCertificadosEntities(int maxResults, int firstResult) {
        return findCertificadosEntities(false, maxResults, firstResult);
    }

    private List<Certificados> findCertificadosEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Certificados.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
          
        }
    }

    public Certificados findCertificados(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Certificados.class, id);
        } finally {
            
        }
    }

    public int getCertificadosCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Certificados> rt = cq.from(Certificados.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
           
        }
    }

}
