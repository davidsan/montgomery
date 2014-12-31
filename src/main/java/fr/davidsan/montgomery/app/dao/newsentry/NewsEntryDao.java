package fr.davidsan.montgomery.app.dao.newsentry;

import java.util.List;

import fr.davidsan.montgomery.app.dao.Dao;
import fr.davidsan.montgomery.app.entity.NewsEntry;
import fr.davidsan.montgomery.app.entity.User;

/**
 * Definition of a Data Access Object that can perform CRUD Operations for
 * {@link NewsEntry}s.
 * 
 */
public interface NewsEntryDao extends Dao<NewsEntry, Long> {

	List<NewsEntry> findByAuthor(User author);

	List<NewsEntry> findNearest(Double distance, Double geolat, Double geolon);
	
}