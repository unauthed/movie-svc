package io.unauthed.use.svc.movie;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterConvertEvent;
import org.springframework.stereotype.Component;

@Component
public class MovieEventListener extends AbstractMongoEventListener<Movie> {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public void onAfterConvert(AfterConvertEvent<Movie> event) {
		super.onAfterConvert(event);

		Movie movie = event.getSource();

		movie.setVotes(movie.getReviews().size());
		movie.setRatings(sumRatings(movie) / movie.getVotes());
	}

	private Float sumRatings(Movie movie) {

		if (movie.getReviews().isEmpty()) {
			return new Float(0F);
		}

		TypedAggregation<Movie> aggregation = newAggregation(Movie.class, //
				match(where("id").is(movie.getId())), //
				unwind("reviews"), //
				group().sum("reviews.rating").as("ratings"), //
				project("ratings"));

		RatingsResult ratingsResult = mongoTemplate.aggregate(aggregation, RatingsResult.class).getUniqueMappedResult();

		return roundFloat(ratingsResult.ratings, 1);
	}

	static Float roundFloat(Float value, int decimalPlace) {
		BigDecimal bigDecimal = new BigDecimal(Float.toString(value));
		return bigDecimal.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP).floatValue();
	}

	class RatingsResult {
		float ratings;
	}
}