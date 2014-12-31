package fr.davidsan.montgomery.app.rest.resources;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import fr.davidsan.montgomery.app.JsonViews;
import fr.davidsan.montgomery.app.dao.newsentry.NewsEntryDao;
import fr.davidsan.montgomery.app.dao.user.UserDao;
import fr.davidsan.montgomery.app.entity.NewsEntry;
import fr.davidsan.montgomery.app.entity.User;

@Component
@Path("/news")
public class NewsEntryResource {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private NewsEntryDao newsEntryDao;

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
		List<NewsEntry> allEntries = this.newsEntryDao.findAll();

		return viewWriter.writeValueAsString(allEntries);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{id}")
	public String read(@PathParam("id") Long id)
			throws JsonGenerationException, JsonMappingException, IOException {
		this.logger.info("read(id)");

		NewsEntry newsEntry = this.newsEntryDao.find(id);
		if (newsEntry == null) {
			throw new WebApplicationException(404);
		}
		ObjectWriter viewWriter;
		if (this.isAdmin()) {
			viewWriter = this.mapper.writerWithView(JsonViews.Admin.class);
		} else {
			viewWriter = this.mapper.writerWithView(JsonViews.User.class);
		}

		return viewWriter.writeValueAsString(newsEntry);
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String create(NewsEntry newsEntry) throws JsonGenerationException,
			JsonMappingException, IOException {
		this.logger.info("create(): " + newsEntry);
		User user = getAuthenticatedUser();
		if (user == null) {
			throw new WebApplicationException(401);
		}
		newsEntry.setAuthor(user);
		newsEntry = this.newsEntryDao.save(newsEntry);

		return this.mapper.writerWithView(JsonViews.User.class)
				.writeValueAsString(newsEntry);
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("{id}")
	public String update(@PathParam("id") Long id, NewsEntry newsEntry)
			throws JsonGenerationException, JsonMappingException, IOException {
		this.logger.info("update(): " + newsEntry);
		if (!this.isAdmin()) {
			// allow update only if he is the author
			User user = getAuthenticatedUser();
			if (user == null || user.getId() != id) {
				throw new WebApplicationException(401);
			}
		}
		NewsEntry transientNewsEntry = this.newsEntryDao.find(id);
		if (transientNewsEntry == null) {
			throw new WebApplicationException(404);
		}
		transientNewsEntry.setContent(newsEntry.getContent());
		transientNewsEntry = this.newsEntryDao.save(transientNewsEntry);
		return this.mapper.writerWithView(JsonViews.User.class)
				.writeValueAsString(transientNewsEntry);
	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{id}")
	@PreAuthorize("hasRole('admin')")
	public void delete(@PathParam("id") Long id) {
		this.logger.info("delete(id)");
		this.newsEntryDao.delete(id);
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