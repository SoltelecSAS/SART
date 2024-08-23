/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.soltelec.eventos.dao;

import com.soltelec.eventos.exceptions.NonexistentEntityException;
import com.soltelec.eventos.model.EventoCda;
import com.soltelec.eventos.conexion.PersistenceController;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author SOLTELEC
 */
public class EventoCdaJpaController {

    private EntityManager getEntityManager() {
        return PersistenceController.getEntityManager();
    }

    public void create(EventoCda eventoCda) {
        EntityManager em = null;
        try {          
            em = getEntityManager();         
            em.getTransaction().begin();
            em.persist(eventoCda);
            em.getTransaction().commit();
        } finally {    
            //em.close();
        }
    }

    public void edit(EventoCda eventoCda) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            eventoCda = em.merge(eventoCda);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = eventoCda.getIdEvento();
                if (findEventoCda(id) == null) {
                    throw new NonexistentEntityException("The eventoCda with id " + id + " no longer exists.");
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
            EventoCda eventoCda;
            try {
                eventoCda = em.getReference(EventoCda.class, id);
                eventoCda.getIdEvento();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The eventoCda with id " + id + " no longer exists.", enfe);
            }
            em.remove(eventoCda);
            em.getTransaction().commit();
        } finally {           
        }
    }

    public List<EventoCda> findEventoCdaEntities() {
        return findEventoCdaEntities(true, -1, -1);
    }

    public List<EventoCda> findEventoCdaEntities(int maxResults, int firstResult) {
        return findEventoCdaEntities(false, maxResults, firstResult);
    }

    private List<EventoCda> findEventoCdaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(EventoCda.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
           
        }
    }

    public EventoCda findEventoCda(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(EventoCda.class, id);
        } finally {
           
        }
    }

    public int getEventoCdaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<EventoCda> rt = cq.from(EventoCda.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
           
        }
    }

    public List<EventoCda> findEventoCda1() {
        EntityManager em = getEntityManager();
        //Query query = em.createQuery("SELECT c FROM Vehiculos AS c,  Marcas m WHERE c.marcas = m");
        Query query = em.createQuery("SELECT c FROM EventoCda AS c");
        return query.getResultList();
    }

    public List<EventoCda> findEventoCdaByMultiParameters(String[] valores, String campos[], String condicion[]) {
        EntityManager em = getEntityManager();
        String cadena = "";
        for (int i = 0; i < campos.length; i++) {
            if (i != 0) {
                cadena += " and ";
            }
            cadena += "i." + campos[i] + " " + condicion[i] + " '" + valores[i] + "'";
        }
        System.out.println("SQL: " + cadena);
        Query query = em.createQuery("SELECT i FROM EventoCda i WHERE " + cadena);
        return query.getResultList();
    }
    
    public List<EventoCda> findEventoCda_porParametros(String mat, String campo, String com) {
        EntityManager em = getEntityManager();

        String msql = "SELECT i FROM EventoCda i WHERE i." + campo + " " + com + " '" + mat + "'";

        Query query = em.createQuery(msql);

        return query.getResultList();
    }
}
