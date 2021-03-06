package fr.davidsan.montgomery.app.dao;

import java.util.List;

import fr.davidsan.montgomery.app.entity.Entity;

public interface Dao<T extends Entity, I> {

	List<T> findAll();

	T find(I id);

	T save(T newsEntry);

	void delete(I id);

}