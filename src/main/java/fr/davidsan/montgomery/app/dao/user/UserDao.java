package fr.davidsan.montgomery.app.dao.user;

import org.springframework.security.core.userdetails.UserDetailsService;

import fr.davidsan.montgomery.app.dao.Dao;
import fr.davidsan.montgomery.app.entity.User;

public interface UserDao extends Dao<User, Long>, UserDetailsService {

	User findByName(String name);

	User findByEmail(String email);
	
}