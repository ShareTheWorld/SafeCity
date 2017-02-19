package com.city.safe.dao;

import java.util.List;

/**
 * Created by user on 16-12-10.
 */

public interface IEntityDb<T>{
    public boolean insert(T entity);
    public boolean delete(String id[]);
    public boolean update(T entity);
    public List<T> select(String id[]);
    public T select(String id);
    public List<T> selectByKey(String key);
    public List<T> select();
}
