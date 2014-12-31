package fr.davidsan.montgomery.app.dao.mailentry;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.transaction.annotation.Transactional;

import fr.davidsan.montgomery.app.dao.JpaDao;
import fr.davidsan.montgomery.app.entity.MailEntry;

public class JpaMailEntryDao extends JpaDao<MailEntry, Long> implements
		MailEntryDao {

	public JpaMailEntryDao() {
		super(MailEntry.class);
	}

	@Override
	@Transactional(readOnly = true)
	public List<MailEntry> findAll() {
		// order by date
		final CriteriaBuilder builder = this.getEntityManager()
				.getCriteriaBuilder();
		final CriteriaQuery<MailEntry> criteriaQuery = builder
				.createQuery(MailEntry.class);

		Root<MailEntry> root = criteriaQuery.from(MailEntry.class);
		criteriaQuery.orderBy(builder.desc(root.get("date")));

		TypedQuery<MailEntry> typedQuery = this.getEntityManager().createQuery(
				criteriaQuery);
		return typedQuery.getResultList();
	}

}
