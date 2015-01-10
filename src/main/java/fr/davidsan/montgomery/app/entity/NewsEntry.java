package fr.davidsan.montgomery.app.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.codehaus.jackson.map.annotate.JsonView;

import fr.davidsan.montgomery.app.JsonViews;

/**
 * JPA Annotated Pojo that represents a news entry.
 * 
 */
@javax.persistence.Entity
@Table(name = "newsentry")
public class NewsEntry implements Entity {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Date date;

	private String content;

	private User author;

	private Double geolat;

	private Double geolon;

	private Set<Tag> tags = new HashSet<Tag>();

	public NewsEntry() {
		this.date = new Date();
	}

	@Id
	@GeneratedValue
	@Column(name = "newsentry_id", unique = true, nullable = false)
	@JsonView(JsonViews.User.class)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "newsentry_date", unique = true, nullable = false)
	@JsonView(JsonViews.User.class)
	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Column(name = "newsentry_content", unique = true, nullable = false)
	@JsonView(JsonViews.User.class)
	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "newsentry_author")
	@JsonView(JsonViews.User.class)
	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	@Column(name = "newsentry_geolat", unique = false, nullable = false)
	@JsonView(JsonViews.User.class)
	public Double getGeolat() {
		return geolat;
	}

	public void setGeolat(Double geolat) {
		this.geolat = geolat;
	}

	@Column(name = "newsentry_geolon", unique = false, nullable = false)
	@JsonView(JsonViews.User.class)
	public Double getGeolon() {
		return geolon;
	}

	public void setGeolon(Double geolon) {
		this.geolon = geolon;
	}

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "newsentry_tag", joinColumns = { @JoinColumn(name = "newsentry_id", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "tag_id", nullable = false, updatable = false) })
	public Set<Tag> getTags() {
		return tags;
	}

	public void setTags(Set<Tag> tags) {
		this.tags = tags;
	}

	public void addTags(Tag tag) {
		this.tags.add(tag);
	}

	@Override
	public String toString() {
		return String.format("NewsEntry[%d, %s]", this.id, this.content);
	}

}