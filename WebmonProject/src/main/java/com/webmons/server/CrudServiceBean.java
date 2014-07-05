package com.webmons.server;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
@Local(CrudService.class)
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class CrudServiceBean implements CrudService {
   
    @PersistenceContext
    EntityManager em;

	@Override
	public <T> T create(T t) {
        this.em.persist(t);
        this.em.flush();
        this.em.refresh(t);
        return t;
	}

	@Override
	public <T> T find(Class<T> type, Object id) {
		return this.em.find(type, id);
	}

	@Override
	public <T> T update(T t) {
		return this.em.merge(t);
	}

	@Override
	public <T> void delete(Class<T> type, Object id) {
		Object ref = this.em.getReference(type, id);
	       this.em.remove(ref);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> findWithNamedQuery(String queryName) {
		return this.em.createNamedQuery(queryName).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> findWithNamedQuery(String queryName, int resultLimit) {
        return this.em.createNamedQuery(queryName).
                setMaxResults(resultLimit).
                getResultList();   

	}

	@Override
	public <T> List<T> findWithNamedQuery(String namedQueryName,
			Map<String, Object> parameters) {
		return findWithNamedQuery(namedQueryName, parameters, 0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> findWithNamedQuery(String namedQueryName,
			Map<String, Object> parameters, int resultLimit) {
        Set<Entry<String, Object>> rawParameters = parameters.entrySet();
        Query query = this.em.createNamedQuery(namedQueryName);
        if(resultLimit > 0)
            query.setMaxResults(resultLimit);
        for (Entry<String, Object> entry : rawParameters) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> findWithQuery(String queryName) {
		return this.em.createQuery(queryName).getResultList();  
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> findWithQuery(String queryName, int resultLimit) {
        return this.em.createQuery(queryName).
                setMaxResults(resultLimit).
                getResultList();   
	}

	@Override
	public <T> List<T> findWithQuery(String namedQueryName,
			Map<String, Object> parameters) {
		return findWithQuery(namedQueryName, parameters, 0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> findWithQuery(String namedQueryName,
			Map<String, Object> parameters, int resultLimit) {
        Set<Entry<String, Object>> rawParameters = parameters.entrySet();
        Query query = this.em.createQuery(namedQueryName);
        if(resultLimit > 0)
            query.setMaxResults(resultLimit);
        for (Entry<String, Object> entry : rawParameters) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        return query.getResultList();
	}
    

}
