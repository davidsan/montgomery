package fr.davidsan.montgomery.app.rest.resources;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import fr.davidsan.montgomery.app.dao.mailentry.MailEntryDao;
import fr.davidsan.montgomery.app.entity.MailEntry;

@Component
@Path("/mail")
public class MailEntryResource {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private MailEntryDao mailEntryDao;

	@Autowired
	private ObjectMapper mapper;

	@GET
	@PreAuthorize("hasRole('admin')")
	@Produces(MediaType.APPLICATION_JSON)
	public List<MailEntry> list() throws JsonGenerationException,
			JsonMappingException, IOException {
		this.logger.info("list()");
		List<MailEntry> allEntries = this.mailEntryDao.findAll();
		return allEntries;
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public MailEntry create(MailEntry mailEntry) {
		this.logger.info("create(): " + mailEntry);
		return this.mailEntryDao.save(mailEntry);
	}

}