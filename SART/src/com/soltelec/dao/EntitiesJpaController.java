/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.dao;

import com.soltelec.dao.conexion.PersistenceController;
import com.soltelec.dao.exceptions.IllegalOrphanException;
import com.soltelec.dao.exceptions.NonexistentEntityException;
import com.soltelec.dao.exceptions.PreexistingEntityException;
import com.soltelec.model.Entities;
import com.soltelec.model.Permissions;
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
public class EntitiesJpaController {

    public EntityManager getEntityManager() {
        return PersistenceController.getEntityManager();
    }

    public void create(Entities entities) throws PreexistingEntityException, Exception {
        if (entities.getPermissionsCollection() == null) {
            entities.setPermissionsCollection(new ArrayList<Permissions>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Permissions> attachedPermissionsCollection = new ArrayList<>();
            for (Permissions permissionsCollectionPermissionsToAttach : entities.getPermissionsCollection()) {
                permissionsCollectionPermissionsToAttach = em.getReference(permissionsCollectionPermissionsToAttach.getClass(), permissionsCollectionPermissionsToAttach.getPermission());
                attachedPermissionsCollection.add(permissionsCollectionPermissionsToAttach);
            }
            entities.setPermissionsCollection(attachedPermissionsCollection);
            em.persist(entities);
            for (Permissions permissionsCollectionPermissions : entities.getPermissionsCollection()) {
                Entities oldEntitiesOfPermissionsCollectionPermissions = permissionsCollectionPermissions.getEntities();
                permissionsCollectionPermissions.setEntities(entities);
                permissionsCollectionPermissions = em.merge(permissionsCollectionPermissions);
                if (oldEntitiesOfPermissionsCollectionPermissions != null) {
                    oldEntitiesOfPermissionsCollectionPermissions.getPermissionsCollection().remove(permissionsCollectionPermissions);
                    oldEntitiesOfPermissionsCollectionPermissions = em.merge(oldEntitiesOfPermissionsCollectionPermissions);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findEntities(entities.getEntity()) != null) {
                throw new PreexistingEntityException("Entities " + entities + " already exists.", ex);
            }
            throw ex;
        } finally {
           
        }
    }

    public void edit(Entities entities) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Entities persistentEntities = em.find(Entities.class, entities.getEntity());
            Collection<Permissions> permissionsCollectionOld = persistentEntities.getPermissionsCollection();
            Collection<Permissions> permissionsCollectionNew = entities.getPermissionsCollection();
            List<String> illegalOrphanMessages = null;
            for (Permissions permissionsCollectionOldPermissions : permissionsCollectionOld) {
                if (!permissionsCollectionNew.contains(permissionsCollectionOldPermissions)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain Permissions " + permissionsCollectionOldPermissions + " since its entities field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Permissions> attachedPermissionsCollectionNew = new ArrayList<>();
            for (Permissions permissionsCollectionNewPermissionsToAttach : permissionsCollectionNew) {
                permissionsCollectionNewPermissionsToAttach = em.getReference(permissionsCollectionNewPermissionsToAttach.getClass(), permissionsCollectionNewPermissionsToAttach.getPermission());
                attachedPermissionsCollectionNew.add(permissionsCollectionNewPermissionsToAttach);
            }
            permissionsCollectionNew = attachedPermissionsCollectionNew;
            entities.setPermissionsCollection(permissionsCollectionNew);
            entities = em.merge(entities);
            for (Permissions permissionsCollectionNewPermissions : permissionsCollectionNew) {
                if (!permissionsCollectionOld.contains(permissionsCollectionNewPermissions)) {
                    Entities oldEntitiesOfPermissionsCollectionNewPermissions = permissionsCollectionNewPermissions.getEntities();
                    permissionsCollectionNewPermissions.setEntities(entities);
                    permissionsCollectionNewPermissions = em.merge(permissionsCollectionNewPermissions);
                    if (oldEntitiesOfPermissionsCollectionNewPermissions != null && !oldEntitiesOfPermissionsCollectionNewPermissions.equals(entities)) {
                        oldEntitiesOfPermissionsCollectionNewPermissions.getPermissionsCollection().remove(permissionsCollectionNewPermissions);
                        oldEntitiesOfPermissionsCollectionNewPermissions = em.merge(oldEntitiesOfPermissionsCollectionNewPermissions);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = entities.getEntity();
                if (findEntities(id) == null) {
                    throw new NonexistentEntityException("The entities with id " + id + " no longer exists.");
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
            Entities entities;
            try {
                entities = em.getReference(Entities.class, id);
                entities.getEntity();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The entities with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Permissions> permissionsCollectionOrphanCheck = entities.getPermissionsCollection();
            for (Permissions permissionsCollectionOrphanCheckPermissions : permissionsCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This Entities (" + entities + ") cannot be destroyed since the Permissions " + permissionsCollectionOrphanCheckPermissions + " in its permissionsCollection field has a non-nullable entities field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(entities);
            em.getTransaction().commit();
        } finally {
           
        }
    }

    public List<Entities> findEntitiesEntities() {
        return findEntitiesEntities(true, -1, -1);
    }

    public List<Entities> findEntitiesEntities(int maxResults, int firstResult) {
        return findEntitiesEntities(false, maxResults, firstResult);
    }

    private List<Entities> findEntitiesEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Entities.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
          
        }
    }

    public Entities findEntities(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Entities.class, id);
        } finally {
           
        }
    }

    public int getEntitiesCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Entities> rt = cq.from(Entities.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {     }
    }

}
