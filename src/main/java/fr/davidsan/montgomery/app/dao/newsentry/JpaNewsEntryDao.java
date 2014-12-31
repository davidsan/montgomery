package fr.davidsan.montgomery.app.dao.newsentry;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

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

		return this.getEntityManager().createQuery(
			    "SELECT n FROM NewsEntry n WHERE n.author LIKE :user")
			    .setParameter("user", user)
			    .setMaxResults(100)
			    .getResultList();
	}

}
