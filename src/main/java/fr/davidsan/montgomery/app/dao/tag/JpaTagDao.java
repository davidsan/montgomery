package fr.davidsan.montgomery.app.dao.tag;

import fr.davidsan.montgomery.app.dao.JpaDao;
import fr.davidsan.montgomery.app.entity.Tag;

public class JpaTagDao extends JpaDao<Tag, Long> implements TagDao {

	public JpaTagDao() {
		super(Tag.class);
	}

	 
}
