package fr.davidsan.montgomery.app.rest.resources;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import fr.davidsan.montgomery.app.JsonViews;
import fr.davidsan.montgomery.app.dao.user.UserDao;
import fr.davidsan.montgomery.app.entity.User;
import fr.davidsan.montgomery.app.rest.TokenUtils;
import fr.davidsan.montgomery.app.transfer.TokenTransfer;
import fr.davidsan.montgomery.app.transfer.UserTransfer;
import fr.davidsan.montgomery.app.transfer.in.GeoTransfer;

@Component
@Path("/user")
public class UserResource {

	@Autowired
	private UserDetailsService userService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	@Qualifier("authenticationManager")
	private AuthenticationManager authManager;

	/**
	 * Retrieves the currently logged in user.
	 * 
	 * @return A transfer containing the username and the roles.
	 */
	@GET
	@Path("self")
	@Produces(MediaType.APPLICATION_JSON)
	public UserTransfer getUser() {
		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();
		Object principal = authentication.getPrincipal();
		if (principal instanceof String
				&& ((String) principal).equals("anonymousUser")) {
			throw new WebApplicationException(401);
		}
		UserDetails userDetails = (UserDetails) principal;

		return new UserTransfer(userDetails.getUsername(),
				this.createRoleMap(userDetails));
	}

	/**
	 * Retrieves the currently logged in user. (DEBUG)
	 * 
	 * @return All informations about the currently logged in user
	 */
	@GET
	@Path("self/debug")
	@Produces(MediaType.APPLICATION_JSON)
	public User getUserDetailled() {
		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();
		Object principal = authentication.getPrincipal();
		if (principal instanceof String
				&& ((String) principal).equals("anonymousUser")) {
			throw new WebApplicationException(401);
		}
		UserDetails userDetails = (UserDetails) principal;

		return userDao.findByName(userDetails.getUsername());
	}

	/**
	 * Authenticates a user and creates an authentication token.
	 * 
	 * @param username
	 *            The name of the user.
	 * @param password
	 *            The password of the user.
	 * @return A transfer containing the authentication token.
	 */
	@Path("authenticate")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public TokenTransfer authenticate(@FormParam("username") String username,
			@FormParam("password") String password) {
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
				username, password);
		Authentication authentication = this.authManager
				.authenticate(authenticationToken);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		/*
		 * Reload user as password of authentication principal will be null
		 * after authorization and password is needed for token generation
		 */
		UserDetails userDetails = this.userService.loadUserByUsername(username);

		return new TokenTransfer(TokenUtils.createToken(userDetails));
	}

	private Map<String, Boolean> createRoleMap(UserDetails userDetails) {
		Map<String, Boolean> roles = new HashMap<String, Boolean>();
		for (GrantedAuthority authority : userDetails.getAuthorities()) {
			roles.put(authority.getAuthority(), Boolean.TRUE);
		}

		return roles;
	}

	/**
	 * UserResource
	 */

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private UserDao userDao;

	@Autowired
	private ObjectMapper mapper;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String list() throws JsonGenerationException, JsonMappingException,
			IOException {
		this.logger.info("list()");

		ObjectWriter viewWriter;
		if (this.isAdmin()) {
			viewWriter = this.mapper.writerWithView(JsonViews.Admin.class);
		} else {
			viewWriter = this.mapper.writerWithView(JsonViews.User.class);
		}
		List<User> allUsers = this.userDao.findAll();

		return viewWriter.writeValueAsString(allUsers);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{id}")
	public String read(@PathParam("id") Long id)
			throws JsonGenerationException, JsonMappingException, IOException {
		this.logger.info("read(id)");

		User user = this.userDao.find(id);
		if (user == null) {
			throw new WebApplicationException(404);
		}

		ObjectWriter viewWriter;
		if (this.isAdmin()) {
			viewWriter = this.mapper.writerWithView(JsonViews.Admin.class);
		} else {
			viewWriter = this.mapper.writerWithView(JsonViews.User.class);
		}

		return viewWriter.writeValueAsString(user);
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String create(User user) throws JsonGenerationException,
			JsonMappingException, IOException {
		this.logger.info("create(): " + user);

		ObjectWriter viewWriter;
		if (this.isAdmin()) {
			viewWriter = this.mapper.writerWithView(JsonViews.Admin.class);
		} else {
			viewWriter = this.mapper.writerWithView(JsonViews.User.class);
		}
		user.setPassword(this.passwordEncoder.encode(user.getPassword()));
		user.addRole("user");
		User newUser = this.userDao.save(user);
		return viewWriter.writeValueAsString(newUser);
	}

	/**
	 * GeoResource
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{id}/geo")
	public GeoTransfer readGeo(@PathParam("id") Long id)
			throws JsonGenerationException, JsonMappingException, IOException {
		this.logger.info("readGeo(id)");

		User user = this.userDao.find(id);
		if (user == null) {
			throw new WebApplicationException(404);
		}
		return new GeoTransfer(user.getId(), user.getGeolat(), user.getGeolon());
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("{id}/geo")
	public GeoTransfer updateGeo(GeoTransfer geo) {
		this.logger.info("updateGeo(): " + geo);

		User authenticatedUser = this.getAuthenticatedUser();
		if (authenticatedUser == null
				|| authenticatedUser.getId() != geo.getId()) {
			throw new WebApplicationException(401);
		}
		User transientUser = this.userDao.find(geo.getId());
		if (transientUser == null) {
			throw new WebApplicationException(404);
		}
		transientUser.setGeolat(geo.getGeolat());
		transientUser.setGeolon(geo.getGeolon());
		this.userDao.save(transientUser);
		return geo;
	}

	private boolean isAdmin() {
		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();
		Object principal = authentication.getPrincipal();
		if (principal instanceof String
				&& ((String) principal).equals("anonymousUser")) {
			return false;
		}
		UserDetails userDetails = (UserDetails) principal;

		for (GrantedAuthority authority : userDetails.getAuthorities()) {
			if (authority.toString().equals("admin")) {
				return true;
			}
		}

		return false;
	}

	private User getAuthenticatedUser() {
		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();

		Object principal = authentication.getPrincipal();
		if (principal instanceof String
				&& ((String) principal).equals("anonymousUser")) {
			return null;
		}
		UserDetails userDetails = (UserDetails) principal;

		return userDao.findByName(userDetails.getUsername());
	}
}