/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.dao;


import com.soltelec.dao.conexion.PersistenceController;
import com.soltelec.dao.exceptions.NonexistentEntityException;
import com.soltelec.dao.exceptions.PreexistingEntityException;
import com.soltelec.model.Defectos;
import com.soltelec.model.Defxprueba;
import com.soltelec.model.DefxpruebaPK;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author Gerenciadesarrollo
 */
public class DefxpruebaJpaController {

    public EntityManager getEntityManager() {
        return PersistenceController.getEntityManager();
    }

    public void create(Defxprueba defxprueba) throws Exception {
        if (defxprueba.getDefxpruebaPK() == null) {
            defxprueba.setDefxpruebaPK(new DefxpruebaPK());
        }
        defxprueba.getDefxpruebaPK().setIdDefecto(defxprueba.getDefectos().getCardefault());
        defxprueba.getDefxpruebaPK().setIdPrueba(defxprueba.getPruebas().getIdPruebas());
        //defxprueba.getDefxpruebaPK().setIdHojapruebaFor(defxprueba.getHojaPruebas().getTestsheet());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Defectos defectos = defxprueba.getDefectos();
            if (defectos != null) {
                defectos = em.getReference(defectos.getClass(), defectos.getCardefault());
                defxprueba.setDefectos(defectos);
            }
            em.persist(defxprueba);
            if (defectos != null) {
                defectos.getDefxpruebaCollection().add(defxprueba);
                defectos = em.merge(defectos);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findDefxprueba(defxprueba.getDefxpruebaPK()) != null) {
                throw new PreexistingEntityException("Defxprueba " + defxprueba + " already exists.", ex);
            }
            throw ex;
        } finally {
           
        }
    }

    public void edit(Defxprueba defxprueba) throws NonexistentEntityException, Exception {
        defxprueba.getDefxpruebaPK().setIdDefecto(defxprueba.getDefectos().getCardefault());
        defxprueba.getDefxpruebaPK().setIdPrueba(defxprueba.getPruebas().getIdPruebas());
        //defxprueba.getDefxpruebaPK().setIdHojapruebaFor(defxprueba.getHojaPruebas().getTestsheet());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Defxprueba persistentDefxprueba = em.find(Defxprueba.class, defxprueba.getDefxpruebaPK());
            Defectos defectosOld = persistentDefxprueba.getDefectos();
            Defectos defectosNew = defxprueba.getDefectos();
            if (defectosNew != null) {
                defectosNew = em.getReference(defectosNew.getClass(), defectosNew.getCardefault());
                defxprueba.setDefectos(defectosNew);
            }
            defxprueba = em.merge(defxprueba);
            if (defectosOld != null && !defectosOld.equals(defectosNew)) {
                defectosOld.getDefxpruebaCollection().remove(defxprueba);
                defectosOld = em.merge(defectosOld);
            }
            if (defectosNew != null && !defectosNew.equals(defectosOld)) {
                defectosNew.getDefxpruebaCollection().add(defxprueba);
                defectosNew = em.merge(defectosNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                DefxpruebaPK id = defxprueba.getDefxpruebaPK();
                if (findDefxprueba(id) == null) {
                    throw new NonexistentEntityException("The defxprueba with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
           
        }
    }

    public void destroy(DefxpruebaPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Defxprueba defxprueba;
            try {
                defxprueba = em.getReference(Defxprueba.class, id);
                defxprueba.getDefxpruebaPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The defxprueba with id " + id + " no longer exists.", enfe);
            }
            Defectos defectos = defxprueba.getDefectos();
            if (defectos != null) {
                defectos.getDefxpruebaCollection().remove(defxprueba);
                defectos = em.merge(defectos);
            }
            em.remove(defxprueba);
            em.getTransaction().commit();
        } finally {
            
        }
    }

    public List<Defxprueba> findDefxpruebaEntities() {
        return findDefxpruebaEntities(true, -1, -1);
    }

    public List<Defxprueba> findDefxpruebaEntities(int maxResults, int firstResult) {
        return findDefxpruebaEntities(false, maxResults, firstResult);
    }

    private List<Defxprueba> findDefxpruebaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Defxprueba.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
           
        }
    }

    public Defxprueba findDefxprueba(DefxpruebaPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Defxprueba.class, id);
        } finally {
           
        }
    }

    public int getDefxpruebaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Defxprueba> rt = cq.from(Defxprueba.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
           
        }
    }



    public void insertarDefx(int def, int pru) {

        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            String query = "insert into Defxprueba values("+def+" , "+pru+",null)";
            em.createNativeQuery(query).executeUpdate();
            em.getTransaction().commit();
        } finally {
         
        }
    }


 //   public void finalizarDefx2(int def, int pru) {
    public void finalizarDefx(int us, int idp, String com) {
        int user = us;
        int pru = idp;
        String c = com;
        EntityManager em = getEntityManager();
        try {

                em.getTransaction().begin();
    em.createNativeQuery("UPDATE Pruebas SET usuario_for = ?, Abortada = 'Y', Finalizada = 'Y', Comentario_aborto = ? WHERE Id_Pruebas = ?")
      .setParameter(1, user)
            .setParameter(2, com)    
      .setParameter(3, pru)
      .executeUpdate();
        em.getTransaction().commit();
        } finally {
           
        }
    }



 //   public void finalizarDefx2(int def, int pru) {
    public void finalizarDefx2(int us, int idp) {
        int user = us;
        int pru = idp;
        EntityManager em = getEntityManager();
        try {

                em.getTransaction().begin();
    em.createNativeQuery("UPDATE Pruebas SET usuario_for = ?, Autorizada = 'N', Finalizada = 'Y' WHERE Id_Pruebas = ?")
      .setParameter(1, user)
      .setParameter(2, pru)      
      .executeUpdate();
        em.getTransaction().commit();
        } finally {
           
        }
    }






    public static void main(String args[]) {
        DefxpruebaJpaController defx = new DefxpruebaJpaController();
        defx.finalizarDefx2(1,14);
    }

}
