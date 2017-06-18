package io.unauthed.use.svc.movie;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class Application implements ApplicationRunner {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	@Autowired
	private MovieRepository movieRepository;

	@Autowired
	private ObjectMapper objectMapper;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	/*
	 * Initialise the mongodb with test data Pass 'skipSeed' as command line
	 * argument to skip seeding mongodb
	 * 
	 * @see
	 * org.springframework.boot.ApplicationRunner#run(org.springframework.boot.
	 * ApplicationArguments)
	 */
	@Override
	public void run(ApplicationArguments args) throws Exception {

		if (args.containsOption("skipSeed") || movieRepository.count() > 0) {
			log.info("Skip seeding the database.");
		} else {
			log.info("Seeding the database with IMDB movie data.");

			String json = StreamUtils.copyToString(new ClassPathResource("imdb.json").getInputStream(),
					Charset.forName("UTF-8"));

			// fastest version
			@SuppressWarnings("unchecked")
			List<Map<String, String>> list = Arrays.asList(objectMapper.readValue(json, Map[].class));

			Date today = new Date();
			String user = "8662568a-cd71-4393-a6ae-24e1a9f1f7fe";
			List<Movie> movies = new ArrayList<>();

			for (Map<String, String> map : list) {
				Review reviewOne = new Review(user, "review one description", this.randomRating(), today, today);
				Review reviewTwo = new Review(UUID.randomUUID().toString(), "review two description",
						this.randomRating(), today, today);
				Review reviewThree = new Review(UUID.randomUUID().toString(), "review three description",
						this.randomRating(), today, today);

				Movie movie = new Movie(user, map.get("title"), map.get("description"), today);
				movie.getReviews().add(reviewOne);
				movie.getReviews().add(reviewTwo);
				movie.getReviews().add(reviewThree);

				movies.add(movie);
			}

			movieRepository.save(movies);
		}
	}

	private Float randomRating() {
		Float rating = randomFloat(1F, 5F);
		return MovieEventListener.roundFloat(rating, 1);
	}

	static Float randomFloat(Float lowerLimit, Float upperLimit) {
		return lowerLimit + new Random().nextFloat() * (upperLimit - lowerLimit);
	}
}
