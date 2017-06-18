package io.unauthed.use.svc.movie;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.TextScore;

import com.fasterxml.jackson.annotation.JsonView;

@Document
public class Movie implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final int MAX_LENGTH_TITLE = 100;
	public static final int MAX_LENGTH_DESCRIPTION = 5000;

	@Id
	private String id;

	@JsonView(MovieResponse.ListFilter.class)
	@Indexed
	@NotEmpty
	private String user;

	@JsonView(MovieResponse.ListFilter.class)
	@TextIndexed(weight = 3)
	@NotEmpty
	@Size(max = MAX_LENGTH_TITLE)
	private String title;

	@JsonView(MovieResponse.ListFilterWithDescription.class)
	@TextIndexed(weight = 2)
	@Size(max = MAX_LENGTH_DESCRIPTION)
	private String description;

	@TextScore
	private Float score = 0F;

	@Future
	private Date created;

	@Future
	private Date updated;

	@NotNull
	private List<Review> reviews = new ArrayList<Review>();;

	@ReadOnlyProperty
	private Integer votes = 0;

	@ReadOnlyProperty
	private Float ratings = 0F;

	Movie() {
		// json
	}

	public Movie(String user, String title, String description, Date created) {
		this.user = user;
		this.title = title;
		this.description = description;
		this.created = this.updated = created;
	}

	@PersistenceConstructor
	Movie(String user, String title, String description, Date created, Date updated, List<Review> reviews) {
		this.user = user;
		this.title = title;
		this.description = description;
		this.created = created;
		this.updated = updated;
		this.reviews = reviews;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Float getScore() {
		return score;
	}

	public void setScore(Float score) {
		this.score = score;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public List<Review> getReviews() {
		return reviews;
	}

	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}

	public Integer getVotes() {
		return votes;
	}

	public void setVotes(Integer votes) {
		this.votes = votes;
	}

	public Float getRatings() {
		return ratings;
	}

	public void setRatings(Float ratings) {
		this.ratings = ratings;
	}

	@Override
	public String toString() {
		return String.format(
				"Movie [id=%s, user=%s, title=%s, description=%s, score=%s, created=%s, updated=%s, reviews=%s, votes=%s, ratings=%s]",
				id, user, title, description, score, created, updated, reviews, votes, ratings);
	}

}