package com.webmons.server;

import java.util.List;
import java.util.Map;

public interface CrudService {
    public <T>  T create(T t);
    public <T>  T find(Class<T> type,Object id);
    public  <T> T update(T t);
    public <T> void delete(Class<T> type,Object id);
    public <T> List<T> findWithNamedQuery(String queryName);
    public <T> List<T> findWithNamedQuery(String queryName,int resultLimit);
    public <T> List<T> findWithNamedQuery(String namedQueryName, Map<String, Object> parameters);
    public <T> List<T> findWithNamedQuery(String namedQueryName, Map<String, Object> parameters,int resultLimit);
    public <T> List<T> findWithQuery(String queryName);
    public <T> List<T> findWithQuery(String queryName,int resultLimit);
    public <T> List<T> findWithQuery(String namedQueryName, Map<String, Object> parameters);
    public <T> List<T> findWithQuery(String namedQueryName, Map<String, Object> parameters,int resultLimit);
}
