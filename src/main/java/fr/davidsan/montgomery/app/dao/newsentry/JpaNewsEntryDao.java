package fr.davidsan.montgomery.app.dao.newsentry;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import fr.davidsan.montgomery.app.dao.JpaDao;
import fr.davidsan.montgomery.app.entity.NewsEntry;
import fr.davidsan.montgomery.app.entity.User;

/**
 * JPA Implementation of a {@link NewsEntryDao}.
 * 
 */
public class JpaNewsEntryDao extends JpaDao<NewsEntry, Long> implements
		NewsEntryDao {

	public JpaNewsEntryDao() {
		super(NewsEntry.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public List<NewsEntry> findAll() {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT n FROM NewsEntry n ");
		sb.append("ORDER BY date DESC");
		return this.getEntityManager().createQuery(sb.toString())
				.setMaxResults(100).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<NewsEntry> findByAuthor(User user) {

		return this
				.getEntityManager()
				.createQuery(
						"SELECT n FROM NewsEntry n WHERE n.author LIKE :user")
				.setParameter("user", user).setMaxResults(100).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<NewsEntry> findNearest(Double distance, Double geolat,
			Double geolon) {

		Double minLat = geolat - (distance / 69);
		Double maxLat = geolat + (distance / 69);
		Double minLon = geolon - distance
				/ Math.abs(Math.cos(Math.toRadians(geolat)) * 69);
		Double maxLon = geolon + distance
				/ Math.abs(Math.cos(Math.toRadians(geolat)) * 69);

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT n FROM NewsEntry n ");
		sb.append("WHERE n.geolat >= :minLat ");
		sb.append("AND n.geolat <= :maxLat ");
		sb.append("AND n.geolon >= :minLon ");
		sb.append("AND n.geolon <= :maxLon ");
		sb.append("ORDER BY date DESC");
		return this.getEntityManager().createQuery(sb.toString())
				.setParameter("minLat", minLat).setParameter("maxLat", maxLat)
				.setParameter("minLon", minLon).setParameter("maxLon", maxLon)
				.setMaxResults(100).getResultList();
	}

}
