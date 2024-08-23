/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.dao;

import com.soltelec.dao.conexion.PersistenceController;
import com.soltelec.dao.exceptions.IllegalOrphanException;
import com.soltelec.dao.exceptions.NonexistentEntityException;
import com.soltelec.dao.exceptions.PreexistingEntityException;
import com.soltelec.model.Medidas;
import com.soltelec.model.Permisibles;
import com.soltelec.model.TiposMedida;
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
public class TiposMedidaJpaController {

    public EntityManager getEntityManager() {
        return PersistenceController.getEntityManager();
    }

    public void create(TiposMedida tiposMedida) throws PreexistingEntityException, Exception {
        if (tiposMedida.getPermisiblesCollection() == null) {
            tiposMedida.setPermisiblesCollection(new ArrayList<Permisibles>());
        }
        if (tiposMedida.getMedidasCollection() == null) {
            tiposMedida.setMedidasCollection(new ArrayList<Medidas>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Permisibles> attachedPermisiblesCollection = new ArrayList<>();
            for (Permisibles permisiblesCollectionPermisiblesToAttach : tiposMedida.getPermisiblesCollection()) {
                permisiblesCollectionPermisiblesToAttach = em.getReference(permisiblesCollectionPermisiblesToAttach.getClass(), permisiblesCollectionPermisiblesToAttach.getIdpermisible());
                attachedPermisiblesCollection.add(permisiblesCollectionPermisiblesToAttach);
            }
            tiposMedida.setPermisiblesCollection(attachedPermisiblesCollection);
            Collection<Medidas> attachedMedidasCollection = new ArrayList<>();
            for (Medidas medidasCollectionMedidasToAttach : tiposMedida.getMedidasCollection()) {
                medidasCollectionMedidasToAttach = em.getReference(medidasCollectionMedidasToAttach.getClass(), medidasCollectionMedidasToAttach.getMeasure());
                attachedMedidasCollection.add(medidasCollectionMedidasToAttach);
            }
            tiposMedida.setMedidasCollection(attachedMedidasCollection);
            em.persist(tiposMedida);
            for (Permisibles permisiblesCollectionPermisibles : tiposMedida.getPermisiblesCollection()) {
                TiposMedida oldTiposMedidaOfPermisiblesCollectionPermisibles = permisiblesCollectionPermisibles.getTiposMedida();
                permisiblesCollectionPermisibles.setTiposMedida(tiposMedida);
                permisiblesCollectionPermisibles = em.merge(permisiblesCollectionPermisibles);
                if (oldTiposMedidaOfPermisiblesCollectionPermisibles != null) {
                    oldTiposMedidaOfPermisiblesCollectionPermisibles.getPermisiblesCollection().remove(permisiblesCollectionPermisibles);
                    oldTiposMedidaOfPermisiblesCollectionPermisibles = em.merge(oldTiposMedidaOfPermisiblesCollectionPermisibles);
                }
            }
            for (Medidas medidasCollectionMedidas : tiposMedida.getMedidasCollection()) {
                TiposMedida oldTiposMedidaOfMedidasCollectionMedidas = medidasCollectionMedidas.getTiposMedida();
                medidasCollectionMedidas.setTiposMedida(tiposMedida);
                medidasCollectionMedidas = em.merge(medidasCollectionMedidas);
                if (oldTiposMedidaOfMedidasCollectionMedidas != null) {
                    oldTiposMedidaOfMedidasCollectionMedidas.getMedidasCollection().remove(medidasCollectionMedidas);
                    oldTiposMedidaOfMedidasCollectionMedidas = em.merge(oldTiposMedidaOfMedidasCollectionMedidas);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findTiposMedida(tiposMedida.getMeasuretype()) != null) {
                throw new PreexistingEntityException("TiposMedida " + tiposMedida + " already exists.", ex);
            }
            throw ex;
        } finally {
          
        }
    }

    public void edit(TiposMedida tiposMedida) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TiposMedida persistentTiposMedida = em.find(TiposMedida.class, tiposMedida.getMeasuretype());
            Collection<Permisibles> permisiblesCollectionOld = persistentTiposMedida.getPermisiblesCollection();
            Collection<Permisibles> permisiblesCollectionNew = tiposMedida.getPermisiblesCollection();
            Collection<Medidas> medidasCollectionOld = persistentTiposMedida.getMedidasCollection();
            Collection<Medidas> medidasCollectionNew = tiposMedida.getMedidasCollection();
            List<String> illegalOrphanMessages = null;
            for (Medidas medidasCollectionOldMedidas : medidasCollectionOld) {
                if (!medidasCollectionNew.contains(medidasCollectionOldMedidas)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain Medidas " + medidasCollectionOldMedidas + " since its tiposMedida field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Permisibles> attachedPermisiblesCollectionNew = new ArrayList<>();
            for (Permisibles permisiblesCollectionNewPermisiblesToAttach : permisiblesCollectionNew) {
                permisiblesCollectionNewPermisiblesToAttach = em.getReference(permisiblesCollectionNewPermisiblesToAttach.getClass(), permisiblesCollectionNewPermisiblesToAttach.getIdpermisible());
                attachedPermisiblesCollectionNew.add(permisiblesCollectionNewPermisiblesToAttach);
            }
            permisiblesCollectionNew = attachedPermisiblesCollectionNew;
            tiposMedida.setPermisiblesCollection(permisiblesCollectionNew);
            Collection<Medidas> attachedMedidasCollectionNew = new ArrayList<>();
            for (Medidas medidasCollectionNewMedidasToAttach : medidasCollectionNew) {
                medidasCollectionNewMedidasToAttach = em.getReference(medidasCollectionNewMedidasToAttach.getClass(), medidasCollectionNewMedidasToAttach.getMeasure());
                attachedMedidasCollectionNew.add(medidasCollectionNewMedidasToAttach);
            }
            medidasCollectionNew = attachedMedidasCollectionNew;
            tiposMedida.setMedidasCollection(medidasCollectionNew);
            tiposMedida = em.merge(tiposMedida);
            for (Permisibles permisiblesCollectionOldPermisibles : permisiblesCollectionOld) {
                if (!permisiblesCollectionNew.contains(permisiblesCollectionOldPermisibles)) {
                    permisiblesCollectionOldPermisibles.setTiposMedida(null);
                    permisiblesCollectionOldPermisibles = em.merge(permisiblesCollectionOldPermisibles);
                }
            }
            for (Permisibles permisiblesCollectionNewPermisibles : permisiblesCollectionNew) {
                if (!permisiblesCollectionOld.contains(permisiblesCollectionNewPermisibles)) {
                    TiposMedida oldTiposMedidaOfPermisiblesCollectionNewPermisibles = permisiblesCollectionNewPermisibles.getTiposMedida();
                    permisiblesCollectionNewPermisibles.setTiposMedida(tiposMedida);
                    permisiblesCollectionNewPermisibles = em.merge(permisiblesCollectionNewPermisibles);
                    if (oldTiposMedidaOfPermisiblesCollectionNewPermisibles != null && !oldTiposMedidaOfPermisiblesCollectionNewPermisibles.equals(tiposMedida)) {
                        oldTiposMedidaOfPermisiblesCollectionNewPermisibles.getPermisiblesCollection().remove(permisiblesCollectionNewPermisibles);
                        oldTiposMedidaOfPermisiblesCollectionNewPermisibles = em.merge(oldTiposMedidaOfPermisiblesCollectionNewPermisibles);
                    }
                }
            }
            for (Medidas medidasCollectionNewMedidas : medidasCollectionNew) {
                if (!medidasCollectionOld.contains(medidasCollectionNewMedidas)) {
                    TiposMedida oldTiposMedidaOfMedidasCollectionNewMedidas = medidasCollectionNewMedidas.getTiposMedida();
                    medidasCollectionNewMedidas.setTiposMedida(tiposMedida);
                    medidasCollectionNewMedidas = em.merge(medidasCollectionNewMedidas);
                    if (oldTiposMedidaOfMedidasCollectionNewMedidas != null && !oldTiposMedidaOfMedidasCollectionNewMedidas.equals(tiposMedida)) {
                        oldTiposMedidaOfMedidasCollectionNewMedidas.getMedidasCollection().remove(medidasCollectionNewMedidas);
                        oldTiposMedidaOfMedidasCollectionNewMedidas = em.merge(oldTiposMedidaOfMedidasCollectionNewMedidas);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = tiposMedida.getMeasuretype();
                if (findTiposMedida(id) == null) {
                    throw new NonexistentEntityException("The tiposMedida with id " + id + " no longer exists.");
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
            TiposMedida tiposMedida;
            try {
                tiposMedida = em.getReference(TiposMedida.class, id);
                tiposMedida.getMeasuretype();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The tiposMedida with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Medidas> medidasCollectionOrphanCheck = tiposMedida.getMedidasCollection();
            for (Medidas medidasCollectionOrphanCheckMedidas : medidasCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This TiposMedida (" + tiposMedida + ") cannot be destroyed since the Medidas " + medidasCollectionOrphanCheckMedidas + " in its medidasCollection field has a non-nullable tiposMedida field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Permisibles> permisiblesCollection = tiposMedida.getPermisiblesCollection();
            for (Permisibles permisiblesCollectionPermisibles : permisiblesCollection) {
                permisiblesCollectionPermisibles.setTiposMedida(null);
                permisiblesCollectionPermisibles = em.merge(permisiblesCollectionPermisibles);
            }
            em.remove(tiposMedida);
            em.getTransaction().commit();
        } finally {
           
        }
    }

    public List<TiposMedida> findTiposMedidaEntities() {
        return findTiposMedidaEntities(true, -1, -1);
    }

    public List<TiposMedida> findTiposMedidaEntities(int maxResults, int firstResult) {
        return findTiposMedidaEntities(false, maxResults, firstResult);
    }

    private List<TiposMedida> findTiposMedidaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(TiposMedida.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
           
        }
    }

    public TiposMedida findTiposMedida(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(TiposMedida.class, id);
        } finally {
           
        }
    }

    public int getTiposMedidaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<TiposMedida> rt = cq.from(TiposMedida.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
          
        }
    }

}
