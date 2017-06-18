package io.unauthed.use.svc.movie;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.Future;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.mongodb.core.index.Indexed;

public class Review implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final int MAX_LENGTH_DESCRIPTION = 3000;

	@Indexed
	@NotEmpty
	private String user;

	@Size(max = MAX_LENGTH_DESCRIPTION)
	private String description;

	@Range(min = 0, max = 10)
	private Float rating = 0F;

	@Future
	private Date created;

	@Future
	private Date updated;

	Review() {
		// json
	}

	public Review(String user, String description, Float rating, Date created, Date updated) {
		this.user = user;
		this.description = description;
		this.rating = rating;
		this.created = created;
		this.updated = updated;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Float getRating() {
		return rating;
	}

	public void setRating(Float rating) {
		this.rating = rating;
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

	@Override
	public String toString() {
		return String.format("Review [user=%s, description=%s, rating=%s, created=%s, updated=%s]", user, description,
				rating, created, updated);
	}

}