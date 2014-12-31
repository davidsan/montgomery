package fr.davidsan.montgomery.app.entity;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonView;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import fr.davidsan.montgomery.app.JsonViews;

@javax.persistence.Entity
@Table(name = "`users`")
// because Postgres contains an user table
public class User implements Entity, UserDetails {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@JsonView(JsonViews.User.class)
	private Long id;

	@Column(unique = true, length = 16, nullable = false)
	@JsonView(JsonViews.User.class)
	private String name;

	@Column(length = 80, nullable = false)
	@JsonView(JsonViews.Admin.class)
	private String password;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "users_roles")
	@JsonView(JsonViews.Admin.class)
	private Set<String> roles = new HashSet<String>();

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "author")
	@JsonIgnore
	private Set<NewsEntry> newsEntries;
 
	protected User() {
		/* Reflection instantiation */
	}

	public User(String name, String passwordHash) {
		this.name = name;
		this.password = passwordHash;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<String> getRoles() {
		return this.roles;
	}

	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}

	public void addRole(String role) {
		this.roles.add(role);
	}

	public Set<NewsEntry> getNewsEntries() {
		return newsEntries;
	}

	public void setNewsEntries(Set<NewsEntry> newsEntries) {
		this.newsEntries = newsEntries;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@JsonView(JsonViews.Admin.class)
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Set<String> roles = this.getRoles();

		if (roles == null) {
			return Collections.emptyList();
		}

		Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
		for (String role : roles) {
			authorities.add(new SimpleGrantedAuthority(role));
		}

		return authorities;
	}

	@JsonView(JsonViews.Admin.class)
	@Override
	public String getUsername() {
		return this.name;
	}

	@JsonView(JsonViews.Admin.class)
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@JsonView(JsonViews.Admin.class)
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@JsonView(JsonViews.Admin.class)
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@JsonView(JsonViews.Admin.class)
	@Override
	public boolean isEnabled() {
		return true;
	}

}