package io.unauthed.use.svc.movie;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.fasterxml.jackson.annotation.JsonView;

@CrossOrigin
@RepositoryRestResource
public interface MovieRepository extends MongoRepository<Movie, String> {

	@RestResource(exported = false)
	@Query(fields = "{ 'title' : 1, 'user' : 1}")
	Optional<Movie> findByTitleIgnoreCase(String name);

	@JsonView(MovieResponse.ListFilter.class)
	@RestResource(path = "filter", rel = "filter")
	Page<Movie> findByUserAndUpdated(String user, Date updated, Pageable pageable);

	@JsonView(MovieResponse.ListFilter.class)
	Page<Movie> findAllBy(TextCriteria criteria, Pageable page);

	@JsonView(MovieResponse.ListFilter.class)
	List<Movie> findAllByOrderByScoreDesc(TextCriteria criteria);

}