package fr.davidsan.montgomery.app.dao;

import java.util.Date;

import org.springframework.security.crypto.password.PasswordEncoder;

import fr.davidsan.montgomery.app.dao.newsentry.NewsEntryDao;
import fr.davidsan.montgomery.app.dao.user.UserDao;
import fr.davidsan.montgomery.app.entity.NewsEntry;
import fr.davidsan.montgomery.app.entity.User;

public class DataBaseInitializer {

	private NewsEntryDao newsEntryDao;

	private UserDao userDao;

	private PasswordEncoder passwordEncoder;

	protected DataBaseInitializer() {
		/* Default constructor for reflection instantiation */
	}

	public DataBaseInitializer(UserDao userDao, NewsEntryDao newsEntryDao,
			PasswordEncoder passwordEncoder) {
		this.userDao = userDao;
		this.newsEntryDao = newsEntryDao;
		this.passwordEncoder = passwordEncoder;
	}

	public void initDataBase() {
		User userUser = new User("user", this.passwordEncoder.encode("user"));
		userUser.addRole("user");
		userUser= this.userDao.save(userUser);

		User adminUser = new User("admin", this.passwordEncoder.encode("admin"));
		adminUser.addRole("user");
		adminUser.addRole("admin");
		adminUser=this.userDao.save(adminUser);

		long timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 24;
		for (int i = 0; i < 10; i++) {
			NewsEntry newsEntry = new NewsEntry();
			newsEntry.setContent("This is example content " + i);
			newsEntry.setDate(new Date(timestamp));
			newsEntry.setAuthor(userUser);
			this.newsEntryDao.save(newsEntry);
			timestamp += 1000 * 60 * 60;
		}
	}

}