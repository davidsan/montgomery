package fr.davidsan.montgomery.app.dao;

import java.util.ArrayList;
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
		ArrayList<User> l = new ArrayList<User>();
		User userUser = new User("user", "user@user.com",
				this.passwordEncoder.encode("user"));
		userUser.addRole("user");
		userUser.setGeolat(48.858093);
		userUser.setGeolon(2.294694);
		userUser = this.userDao.save(userUser);
		l.add(userUser);

		User adminUser = new User("admin", "admin@admin.com",
				this.passwordEncoder.encode("admin"));
		adminUser.addRole("user");
		adminUser.addRole("admin");
		adminUser.setGeolat(48.860294);
		adminUser.setGeolon(2.338629);
		adminUser = this.userDao.save(adminUser);
		l.add(adminUser);

		l.add(userDao.save(generateFakeUser("eiffel", 48.858093, 2.294694)));
		l.add(userDao.save(generateFakeUser("louvre", 48.860294, 2.338629)));
		l.add(userDao.save(generateFakeUser("orsay", 48.859962, 2.326561)));
		l.add(userDao.save(generateFakeUser("versailles", 48.801407, 2.130122)));
		l.add(userDao.save(generateFakeUser("montmartre", 48.887691, 2.340607)));
		l.add(userDao.save(generateFakeUser("triomphe", 48.873756, 2.294946)));
		l.add(userDao.save(generateFakeUser("bhv", 48.856373, 2.353016)));
		l.add(userDao.save(generateFakeUser("lux", 48.846870, 2.337170)));
		l.add(userDao.save(generateFakeUser("nice", 43.675819, 7.289429)));
		l.add(userDao.save(generateFakeUser("amiens", 49.894066, 2.295753)));
		l.add(userDao.save(generateFakeUser("newyork", 40.714270, -74.005970)));

		long timestamp = System.currentTimeMillis() - 1000 * 1000 * 60 * 60 * 24;
		for (int i = 0; i < 1000; i++) {
			NewsEntry newsEntry = new NewsEntry();
			newsEntry.setContent("This is example content " + i);
			newsEntry.setDate(new Date(timestamp));
			newsEntry.setAuthor(l.get((int) (Math.random() * l.size())));
			newsEntry.setGeolat(newsEntry.getAuthor().getGeolat());
			newsEntry.setGeolon(newsEntry.getAuthor().getGeolon());
			this.newsEntryDao.save(newsEntry);
			timestamp += 1000;
		}
		NewsEntry newsEntry = new NewsEntry();
		newsEntry.setContent("A message from NYC");
		newsEntry.setDate(new Date(System.currentTimeMillis()));
		newsEntry.setAuthor(l.get(l.size()-1));
		newsEntry.setGeolat(newsEntry.getAuthor().getGeolat());
		newsEntry.setGeolon(newsEntry.getAuthor().getGeolon());
		this.newsEntryDao.save(newsEntry);
	}

	private User generateFakeUser(String name, Double geolat, Double geolon) {
		User user = new User(name, name + "@" + name + ".com",
				this.passwordEncoder.encode(name));
		user.setGeolat(geolat);
		user.setGeolon(geolon);
		user.addRole("user");
		return user;
	}

}