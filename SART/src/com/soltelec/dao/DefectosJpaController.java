/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.dao;

import com.soltelec.dao.conexion.PersistenceController;
import com.soltelec.dao.exceptions.IllegalOrphanException;
import com.soltelec.dao.exceptions.NonexistentEntityException;
import com.soltelec.dao.exceptions.PreexistingEntityException;
import com.soltelec.model.Defectos;
import com.soltelec.model.Defectoxmedida;
import com.soltelec.model.Defxplaca;
import com.soltelec.model.Defxprueba;
import com.soltelec.model.Grupos;
import com.soltelec.model.Permisibles;
import com.soltelec.model.SubGrupos;
import com.soltelec.model.Vehiculos;
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
public class DefectosJpaController {

    public EntityManager getEntityManager() {
        return PersistenceController.getEntityManager();
    }

    public void create(Defectos defectos) throws PreexistingEntityException, Exception {
        if (defectos.getDefectoxmedidaCollection() == null) {
            defectos.setDefectoxmedidaCollection(new ArrayList<Defectoxmedida>());
        }
        if (defectos.getPermisiblesCollection() == null) {
            defectos.setPermisiblesCollection(new ArrayList<Permisibles>());
        }
        if (defectos.getDefxpruebaCollection() == null) {
            defectos.setDefxpruebaCollection(new ArrayList<Defxprueba>());
        }
        if (defectos.getDefxplacaCollection() == null) {
            defectos.setDefxplacaCollection(new ArrayList<Defxplaca>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Grupos grupos = null;
            if (grupos != null) {
                grupos = em.getReference(grupos.getClass(), grupos.getDefgroup());
                //defectos.setGrupos(grupos);
            }
            SubGrupos subGrupos = defectos.getSubGrupos();
            if (subGrupos != null) {
                subGrupos = em.getReference(subGrupos.getClass(), subGrupos.getScdefgroupsub());
                defectos.setSubGrupos(subGrupos);
            }
            Collection<Defectoxmedida> attachedDefectoxmedidaCollection = new ArrayList<>();
            for (Defectoxmedida defectoxmedidaCollectionDefectoxmedidaToAttach : defectos.getDefectoxmedidaCollection()) {
                defectoxmedidaCollectionDefectoxmedidaToAttach = em.getReference(defectoxmedidaCollectionDefectoxmedidaToAttach.getClass(), defectoxmedidaCollectionDefectoxmedidaToAttach.getDefectoxmedidaPK());
                attachedDefectoxmedidaCollection.add(defectoxmedidaCollectionDefectoxmedidaToAttach);
            }
            defectos.setDefectoxmedidaCollection(attachedDefectoxmedidaCollection);
            Collection<Permisibles> attachedPermisiblesCollection = new ArrayList<>();
            for (Permisibles permisiblesCollectionPermisiblesToAttach : defectos.getPermisiblesCollection()) {
                permisiblesCollectionPermisiblesToAttach = em.getReference(permisiblesCollectionPermisiblesToAttach.getClass(), permisiblesCollectionPermisiblesToAttach.getIdpermisible());
                attachedPermisiblesCollection.add(permisiblesCollectionPermisiblesToAttach);
            }
            defectos.setPermisiblesCollection(attachedPermisiblesCollection);
            Collection<Defxprueba> attachedDefxpruebaCollection = new ArrayList<>();
            for (Defxprueba defxpruebaCollectionDefxpruebaToAttach : defectos.getDefxpruebaCollection()) {
                defxpruebaCollectionDefxpruebaToAttach = em.getReference(defxpruebaCollectionDefxpruebaToAttach.getClass(), defxpruebaCollectionDefxpruebaToAttach.getDefxpruebaPK());
                attachedDefxpruebaCollection.add(defxpruebaCollectionDefxpruebaToAttach);
            }
            defectos.setDefxpruebaCollection(attachedDefxpruebaCollection);
            Collection<Defxplaca> attachedDefxplacaCollection = new ArrayList<>();
            for (Defxplaca defxplacaCollectionDefxplacaToAttach : defectos.getDefxplacaCollection()) {
                defxplacaCollectionDefxplacaToAttach = em.getReference(defxplacaCollectionDefxplacaToAttach.getClass(), defxplacaCollectionDefxplacaToAttach.getDefxplacaPK());
                attachedDefxplacaCollection.add(defxplacaCollectionDefxplacaToAttach);
            }
            defectos.setDefxplacaCollection(attachedDefxplacaCollection);
            em.persist(defectos);
            if (grupos != null) {
                //grupos.getDefectosCollection().add(defectos);
                grupos = em.merge(grupos);
            }
            if (subGrupos != null) {
                subGrupos.getDefectosCollection().add(defectos);
                subGrupos = em.merge(subGrupos);
            }
            for (Defectoxmedida defectoxmedidaCollectionDefectoxmedida : defectos.getDefectoxmedidaCollection()) {
                Defectos oldDefectosOfDefectoxmedidaCollectionDefectoxmedida = defectoxmedidaCollectionDefectoxmedida.getDefectos();
                defectoxmedidaCollectionDefectoxmedida.setDefectos(defectos);
                defectoxmedidaCollectionDefectoxmedida = em.merge(defectoxmedidaCollectionDefectoxmedida);
                if (oldDefectosOfDefectoxmedidaCollectionDefectoxmedida != null) {
                    oldDefectosOfDefectoxmedidaCollectionDefectoxmedida.getDefectoxmedidaCollection().remove(defectoxmedidaCollectionDefectoxmedida);
                    oldDefectosOfDefectoxmedidaCollectionDefectoxmedida = em.merge(oldDefectosOfDefectoxmedidaCollectionDefectoxmedida);
                }
            }
            for (Permisibles permisiblesCollectionPermisibles : defectos.getPermisiblesCollection()) {
                Defectos oldDefectosOfPermisiblesCollectionPermisibles = permisiblesCollectionPermisibles.getDefectos();
                permisiblesCollectionPermisibles.setDefectos(defectos);
                permisiblesCollectionPermisibles = em.merge(permisiblesCollectionPermisibles);
                if (oldDefectosOfPermisiblesCollectionPermisibles != null) {
                    oldDefectosOfPermisiblesCollectionPermisibles.getPermisiblesCollection().remove(permisiblesCollectionPermisibles);
                    oldDefectosOfPermisiblesCollectionPermisibles = em.merge(oldDefectosOfPermisiblesCollectionPermisibles);
                }
            }
            for (Defxprueba defxpruebaCollectionDefxprueba : defectos.getDefxpruebaCollection()) {
                Defectos oldDefectosOfDefxpruebaCollectionDefxprueba = defxpruebaCollectionDefxprueba.getDefectos();
                defxpruebaCollectionDefxprueba.setDefectos(defectos);
                defxpruebaCollectionDefxprueba = em.merge(defxpruebaCollectionDefxprueba);
                if (oldDefectosOfDefxpruebaCollectionDefxprueba != null) {
                    oldDefectosOfDefxpruebaCollectionDefxprueba.getDefxpruebaCollection().remove(defxpruebaCollectionDefxprueba);
                    oldDefectosOfDefxpruebaCollectionDefxprueba = em.merge(oldDefectosOfDefxpruebaCollectionDefxprueba);
                }
            }
            for (Defxplaca defxplacaCollectionDefxplaca : defectos.getDefxplacaCollection()) {
                Defectos oldDefectosOfDefxplacaCollectionDefxplaca = defxplacaCollectionDefxplaca.getDefectos();
                defxplacaCollectionDefxplaca.setDefectos(defectos);
                defxplacaCollectionDefxplaca = em.merge(defxplacaCollectionDefxplaca);
                if (oldDefectosOfDefxplacaCollectionDefxplaca != null) {
                    oldDefectosOfDefxplacaCollectionDefxplaca.getDefxplacaCollection().remove(defxplacaCollectionDefxplaca);
                    oldDefectosOfDefxplacaCollectionDefxplaca = em.merge(oldDefectosOfDefxplacaCollectionDefxplaca);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findDefectos(defectos.getCardefault()) != null) {
                throw new PreexistingEntityException("Defectos " + defectos + " already exists.", ex);
            }
            throw ex;
        } finally {
           
        }
    }

    public void edit(Defectos defectos) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Defectos persistentDefectos = em.find(Defectos.class, defectos.getCardefault());
            Grupos gruposOld = null;
            Grupos gruposNew = null;
            SubGrupos subGruposOld = persistentDefectos.getSubGrupos();
            SubGrupos subGruposNew = defectos.getSubGrupos();
            Collection<Defectoxmedida> defectoxmedidaCollectionOld = persistentDefectos.getDefectoxmedidaCollection();
            Collection<Defectoxmedida> defectoxmedidaCollectionNew = defectos.getDefectoxmedidaCollection();
            Collection<Permisibles> permisiblesCollectionOld = persistentDefectos.getPermisiblesCollection();
            Collection<Permisibles> permisiblesCollectionNew = defectos.getPermisiblesCollection();
            Collection<Defxprueba> defxpruebaCollectionOld = persistentDefectos.getDefxpruebaCollection();
            Collection<Defxprueba> defxpruebaCollectionNew = defectos.getDefxpruebaCollection();
            Collection<Defxplaca> defxplacaCollectionOld = persistentDefectos.getDefxplacaCollection();
            Collection<Defxplaca> defxplacaCollectionNew = defectos.getDefxplacaCollection();
            List<String> illegalOrphanMessages = null;
            for (Defectoxmedida defectoxmedidaCollectionOldDefectoxmedida : defectoxmedidaCollectionOld) {
                if (!defectoxmedidaCollectionNew.contains(defectoxmedidaCollectionOldDefectoxmedida)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain Defectoxmedida " + defectoxmedidaCollectionOldDefectoxmedida + " since its defectos field is not nullable.");
                }
            }
            for (Permisibles permisiblesCollectionOldPermisibles : permisiblesCollectionOld) {
                if (!permisiblesCollectionNew.contains(permisiblesCollectionOldPermisibles)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain Permisibles " + permisiblesCollectionOldPermisibles + " since its defectos field is not nullable.");
                }
            }
            for (Defxprueba defxpruebaCollectionOldDefxprueba : defxpruebaCollectionOld) {
                if (!defxpruebaCollectionNew.contains(defxpruebaCollectionOldDefxprueba)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain Defxprueba " + defxpruebaCollectionOldDefxprueba + " since its defectos field is not nullable.");
                }
            }
            for (Defxplaca defxplacaCollectionOldDefxplaca : defxplacaCollectionOld) {
                if (!defxplacaCollectionNew.contains(defxplacaCollectionOldDefxplaca)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain Defxplaca " + defxplacaCollectionOldDefxplaca + " since its defectos field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (gruposNew != null) {
                gruposNew = em.getReference(gruposNew.getClass(), gruposNew.getDefgroup());
                //defectos.setGrupos(gruposNew);
            }
            if (subGruposNew != null) {
                subGruposNew = em.getReference(subGruposNew.getClass(), subGruposNew.getScdefgroupsub());
                defectos.setSubGrupos(subGruposNew);
            }
            Collection<Defectoxmedida> attachedDefectoxmedidaCollectionNew = new ArrayList<>();
            for (Defectoxmedida defectoxmedidaCollectionNewDefectoxmedidaToAttach : defectoxmedidaCollectionNew) {
                defectoxmedidaCollectionNewDefectoxmedidaToAttach = em.getReference(defectoxmedidaCollectionNewDefectoxmedidaToAttach.getClass(), defectoxmedidaCollectionNewDefectoxmedidaToAttach.getDefectoxmedidaPK());
                attachedDefectoxmedidaCollectionNew.add(defectoxmedidaCollectionNewDefectoxmedidaToAttach);
            }
            defectoxmedidaCollectionNew = attachedDefectoxmedidaCollectionNew;
            defectos.setDefectoxmedidaCollection(defectoxmedidaCollectionNew);
            Collection<Permisibles> attachedPermisiblesCollectionNew = new ArrayList<>();
            for (Permisibles permisiblesCollectionNewPermisiblesToAttach : permisiblesCollectionNew) {
                permisiblesCollectionNewPermisiblesToAttach = em.getReference(permisiblesCollectionNewPermisiblesToAttach.getClass(), permisiblesCollectionNewPermisiblesToAttach.getIdpermisible());
                attachedPermisiblesCollectionNew.add(permisiblesCollectionNewPermisiblesToAttach);
            }
            permisiblesCollectionNew = attachedPermisiblesCollectionNew;
            defectos.setPermisiblesCollection(permisiblesCollectionNew);
            Collection<Defxprueba> attachedDefxpruebaCollectionNew = new ArrayList<>();
            for (Defxprueba defxpruebaCollectionNewDefxpruebaToAttach : defxpruebaCollectionNew) {
                defxpruebaCollectionNewDefxpruebaToAttach = em.getReference(defxpruebaCollectionNewDefxpruebaToAttach.getClass(), defxpruebaCollectionNewDefxpruebaToAttach.getDefxpruebaPK());
                attachedDefxpruebaCollectionNew.add(defxpruebaCollectionNewDefxpruebaToAttach);
            }
            defxpruebaCollectionNew = attachedDefxpruebaCollectionNew;
            defectos.setDefxpruebaCollection(defxpruebaCollectionNew);
            Collection<Defxplaca> attachedDefxplacaCollectionNew = new ArrayList<>();
            for (Defxplaca defxplacaCollectionNewDefxplacaToAttach : defxplacaCollectionNew) {
                defxplacaCollectionNewDefxplacaToAttach = em.getReference(defxplacaCollectionNewDefxplacaToAttach.getClass(), defxplacaCollectionNewDefxplacaToAttach.getDefxplacaPK());
                attachedDefxplacaCollectionNew.add(defxplacaCollectionNewDefxplacaToAttach);
            }
            defxplacaCollectionNew = attachedDefxplacaCollectionNew;
            defectos.setDefxplacaCollection(defxplacaCollectionNew);
            defectos = em.merge(defectos);
            if (gruposOld != null && !gruposOld.equals(gruposNew)) {
              //  gruposOld.getDefectosCollection().remove(defectos);
                gruposOld = em.merge(gruposOld);
            }
            if (gruposNew != null && !gruposNew.equals(gruposOld)) {
               // gruposNew.getDefectosCollection().add(defectos);
                gruposNew = em.merge(gruposNew);
            }
            if (subGruposOld != null && !subGruposOld.equals(subGruposNew)) {
                subGruposOld.getDefectosCollection().remove(defectos);
                subGruposOld = em.merge(subGruposOld);
            }
            if (subGruposNew != null && !subGruposNew.equals(subGruposOld)) {
                subGruposNew.getDefectosCollection().add(defectos);
                subGruposNew = em.merge(subGruposNew);
            }
            for (Defectoxmedida defectoxmedidaCollectionNewDefectoxmedida : defectoxmedidaCollectionNew) {
                if (!defectoxmedidaCollectionOld.contains(defectoxmedidaCollectionNewDefectoxmedida)) {
                    Defectos oldDefectosOfDefectoxmedidaCollectionNewDefectoxmedida = defectoxmedidaCollectionNewDefectoxmedida.getDefectos();
                    defectoxmedidaCollectionNewDefectoxmedida.setDefectos(defectos);
                    defectoxmedidaCollectionNewDefectoxmedida = em.merge(defectoxmedidaCollectionNewDefectoxmedida);
                    if (oldDefectosOfDefectoxmedidaCollectionNewDefectoxmedida != null && !oldDefectosOfDefectoxmedidaCollectionNewDefectoxmedida.equals(defectos)) {
                        oldDefectosOfDefectoxmedidaCollectionNewDefectoxmedida.getDefectoxmedidaCollection().remove(defectoxmedidaCollectionNewDefectoxmedida);
                        oldDefectosOfDefectoxmedidaCollectionNewDefectoxmedida = em.merge(oldDefectosOfDefectoxmedidaCollectionNewDefectoxmedida);
                    }
                }
            }
            for (Permisibles permisiblesCollectionNewPermisibles : permisiblesCollectionNew) {
                if (!permisiblesCollectionOld.contains(permisiblesCollectionNewPermisibles)) {
                    Defectos oldDefectosOfPermisiblesCollectionNewPermisibles = permisiblesCollectionNewPermisibles.getDefectos();
                    permisiblesCollectionNewPermisibles.setDefectos(defectos);
                    permisiblesCollectionNewPermisibles = em.merge(permisiblesCollectionNewPermisibles);
                    if (oldDefectosOfPermisiblesCollectionNewPermisibles != null && !oldDefectosOfPermisiblesCollectionNewPermisibles.equals(defectos)) {
                        oldDefectosOfPermisiblesCollectionNewPermisibles.getPermisiblesCollection().remove(permisiblesCollectionNewPermisibles);
                        oldDefectosOfPermisiblesCollectionNewPermisibles = em.merge(oldDefectosOfPermisiblesCollectionNewPermisibles);
                    }
                }
            }
            for (Defxprueba defxpruebaCollectionNewDefxprueba : defxpruebaCollectionNew) {
                if (!defxpruebaCollectionOld.contains(defxpruebaCollectionNewDefxprueba)) {
                    Defectos oldDefectosOfDefxpruebaCollectionNewDefxprueba = defxpruebaCollectionNewDefxprueba.getDefectos();
                    defxpruebaCollectionNewDefxprueba.setDefectos(defectos);
                    defxpruebaCollectionNewDefxprueba = em.merge(defxpruebaCollectionNewDefxprueba);
                    if (oldDefectosOfDefxpruebaCollectionNewDefxprueba != null && !oldDefectosOfDefxpruebaCollectionNewDefxprueba.equals(defectos)) {
                        oldDefectosOfDefxpruebaCollectionNewDefxprueba.getDefxpruebaCollection().remove(defxpruebaCollectionNewDefxprueba);
                        oldDefectosOfDefxpruebaCollectionNewDefxprueba = em.merge(oldDefectosOfDefxpruebaCollectionNewDefxprueba);
                    }
                }
            }
            for (Defxplaca defxplacaCollectionNewDefxplaca : defxplacaCollectionNew) {
                if (!defxplacaCollectionOld.contains(defxplacaCollectionNewDefxplaca)) {
                    Defectos oldDefectosOfDefxplacaCollectionNewDefxplaca = defxplacaCollectionNewDefxplaca.getDefectos();
                    defxplacaCollectionNewDefxplaca.setDefectos(defectos);
                    defxplacaCollectionNewDefxplaca = em.merge(defxplacaCollectionNewDefxplaca);
                    if (oldDefectosOfDefxplacaCollectionNewDefxplaca != null && !oldDefectosOfDefxplacaCollectionNewDefxplaca.equals(defectos)) {
                        oldDefectosOfDefxplacaCollectionNewDefxplaca.getDefxplacaCollection().remove(defxplacaCollectionNewDefxplaca);
                        oldDefectosOfDefxplacaCollectionNewDefxplaca = em.merge(oldDefectosOfDefxplacaCollectionNewDefxplaca);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = defectos.getCardefault();
                if (findDefectos(id) == null) {
                    throw new NonexistentEntityException("The defectos with id " + id + " no longer exists.");
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
            Defectos defectos;
            try {
                defectos = em.getReference(Defectos.class, id);
                defectos.getCardefault();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The defectos with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Defectoxmedida> defectoxmedidaCollectionOrphanCheck = defectos.getDefectoxmedidaCollection();
            for (Defectoxmedida defectoxmedidaCollectionOrphanCheckDefectoxmedida : defectoxmedidaCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This Defectos (" + defectos + ") cannot be destroyed since the Defectoxmedida " + defectoxmedidaCollectionOrphanCheckDefectoxmedida + " in its defectoxmedidaCollection field has a non-nullable defectos field.");
            }
            Collection<Permisibles> permisiblesCollectionOrphanCheck = defectos.getPermisiblesCollection();
            for (Permisibles permisiblesCollectionOrphanCheckPermisibles : permisiblesCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This Defectos (" + defectos + ") cannot be destroyed since the Permisibles " + permisiblesCollectionOrphanCheckPermisibles + " in its permisiblesCollection field has a non-nullable defectos field.");
            }
            Collection<Defxprueba> defxpruebaCollectionOrphanCheck = defectos.getDefxpruebaCollection();
            for (Defxprueba defxpruebaCollectionOrphanCheckDefxprueba : defxpruebaCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This Defectos (" + defectos + ") cannot be destroyed since the Defxprueba " + defxpruebaCollectionOrphanCheckDefxprueba + " in its defxpruebaCollection field has a non-nullable defectos field.");
            }
            Collection<Defxplaca> defxplacaCollectionOrphanCheck = defectos.getDefxplacaCollection();
            for (Defxplaca defxplacaCollectionOrphanCheckDefxplaca : defxplacaCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This Defectos (" + defectos + ") cannot be destroyed since the Defxplaca " + defxplacaCollectionOrphanCheckDefxplaca + " in its defxplacaCollection field has a non-nullable defectos field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Grupos grupos = null;
            if (grupos != null) {
               // grupos.getDefectosCollection().remove(defectos);
                grupos = em.merge(grupos);
            }
            SubGrupos subGrupos = defectos.getSubGrupos();
            if (subGrupos != null) {
                subGrupos.getDefectosCollection().remove(defectos);
                subGrupos = em.merge(subGrupos);
            }
            em.remove(defectos);
            em.getTransaction().commit();
        } finally {
            
        }
    }

    public List<Defectos> findDefectosEntities() {
        return findDefectosEntities(true, -1, -1);
    }

    public List<Defectos> findDefectosEntities(int maxResults, int firstResult) {
        return findDefectosEntities(false, maxResults, firstResult);
    }

    private List<Defectos> findDefectosEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Defectos.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
           
        }
    }

    public Defectos findDefectos(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Defectos.class, id);
        } finally {
           
        }
    }

    public int getDefectosCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Defectos> rt = cq.from(Defectos.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            
        }
    }

}
