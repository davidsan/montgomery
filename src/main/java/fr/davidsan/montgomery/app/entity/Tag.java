package fr.davidsan.montgomery.app.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@javax.persistence.Entity
@Table(name = "tag")
public class Tag implements Entity {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String name;

	private Set<NewsEntry> newsEntries = new HashSet<NewsEntry>();

	protected Tag() {
	}

	public Tag(String name) {
		this.name = name;
	}

	@Id
	@GeneratedValue
	@Column(name = "tag_id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "tag_name", nullable = false, length = 255)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "tags")
	public Set<NewsEntry> getNewsEntries() {
		return newsEntries;
	}

	public void setNewsEntries(Set<NewsEntry> newsEntries) {
		this.newsEntries = newsEntries;
	}

}
