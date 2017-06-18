package io.unauthed.use.svc.movie;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;
import java.util.UUID;

import javax.servlet.RequestDispatcher;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiDocumentation {

	private static final String API_ROOT = "/api/v1";
	private static final String MOVIES_RESOURCE = API_ROOT + "/movies";

	@Rule
	public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

	@Autowired
	private MovieRepository movieRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private WebApplicationContext context;

	private MockMvc mockMvc;

	@Before
	public void setUp() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
				.apply(documentationConfiguration(this.restDocumentation)).build();
	}

	@Test
	public void errorExample() throws Exception {
		this.mockMvc
				.perform(get("/error").requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 400)
						.requestAttr(RequestDispatcher.ERROR_REQUEST_URI, MOVIES_RESOURCE).requestAttr(
								RequestDispatcher.ERROR_MESSAGE,
								"The movie 'http://localhost" + MOVIES_RESOURCE + "/123' does not exist"))
				.andDo(print()).andExpect(status().isBadRequest()).andExpect(jsonPath("error", is("Bad Request")))
				.andExpect(jsonPath("timestamp", is(notNullValue()))).andExpect(jsonPath("status", is(400)))
				.andExpect(jsonPath("path", is(notNullValue())))
				.andDo(document("error-example",
						responseFields(
								fieldWithPath("error").description("The HTTP error that occurred, e.g. `Bad Request`"),
								fieldWithPath("message").description("A description of the cause of the error"),
								fieldWithPath("path").description("The path to which the request was made"),
								fieldWithPath("status").description("The HTTP status code, e.g. `400`"),
								fieldWithPath("timestamp")
										.description("The time, in milliseconds, at which the error occurred"))));
	}

	@Test
	public void indexExample() throws Exception {
		this.mockMvc.perform(get(API_ROOT)).andExpect(status().isOk())
				.andDo(document("index-example",
						links(linkWithRel("movies").description("The <<resources-movies,Movies resource>>"),
								linkWithRel("profile").description("The ALPS profile for the service")),
						responseFields(subsectionWithPath("_links")
								.description("<<resources-index-links,Links>> to other resources")))); // TODO

	}

	@Test
	public void moviesListExample() throws Exception {
		this.movieRepository.deleteAll();
		this.movieRepository.save(createMovie("Example name one", "Example description one"));
		this.movieRepository.save(createMovie("Example name two", "Example description two"));
		this.movieRepository.save(createMovie("Example name three", "Example description three"));

		this.mockMvc.perform(get(MOVIES_RESOURCE)).andExpect(status().isOk())
				.andDo(document("movies-list-example",
						links(linkWithRel("self").description("Canonical link for this resource"),
								linkWithRel("profile").description("The ALPS profile for this resource"),
								linkWithRel("search").description("The search for this resource")),
						responseFields(
								subsectionWithPath("page").description("The page object for paging and sorting"),
								subsectionWithPath("_embedded.movies").description("An array of <<resources-movie, Movie resources>>"),
								subsectionWithPath("_links")
										.description("<<resources-tags-list-links, Links>> to other resources")))); // TODO
	}

	@Test
	public void moviesCreateExample() throws Exception {
		Movie movie = createMovie("Example name", "Example description");

		this.mockMvc
				.perform(post(MOVIES_RESOURCE).contentType(MediaTypes.HAL_JSON)
						.content(this.objectMapper.writeValueAsString(movie)))
				.andExpect(status().isCreated())
				.andDo(document("movies-create-example",
						requestFields(
								fieldWithPath("id").description("The id of the movie"),
								fieldWithPath("user").description("The user of the movie"),
								fieldWithPath("title").description("The title of the movie"),
								fieldWithPath("description").description("The description of the movie"),
								fieldWithPath("created").description("The created date of the movie"),
								fieldWithPath("updated").description("The updated date of the movie"))));
	}

	@Test
	public void movieGetExample() throws Exception {
		Movie movie = createMovie("Example title", "Example description");

		String movieLocation = this.mockMvc
				.perform(post(MOVIES_RESOURCE).contentType(MediaTypes.HAL_JSON)
						.content(this.objectMapper.writeValueAsString(movie)))
				.andExpect(status().isCreated()).andReturn().getResponse().getHeader("Location");

		this.mockMvc.perform(get(movieLocation)).andExpect(status().isOk())
				.andExpect(jsonPath("user", is(movie.getUser())))
				.andExpect(jsonPath("title", is(movie.getTitle())))
				.andExpect(jsonPath("description", is(movie.getDescription())))
				.andExpect(jsonPath("created", notNullValue()))
				.andExpect(jsonPath("updated", notNullValue()))
				.andExpect(jsonPath("_links.self.href", is(movieLocation))).andDo(print())
				.andDo(document("movie-get-example",
						links(linkWithRel("self").description("Canonical link for this <<resources-movie,movie>>"),
								linkWithRel("movie").description("This <<resources-movie,movie>>")),
									responseFields(
									fieldWithPath("user").description("The user of the movie"),
									fieldWithPath("title").description("The title of the movie"),
									fieldWithPath("description").description("The description of the movie"),
									fieldWithPath("created").description("The created date of the movie"),
									fieldWithPath("updated").description("The updated date of the movie"),
									subsectionWithPath("_links").description("<<resources-movie-links,Links>> to other resources"))));
	}

	@Test
	public void movieUpdateExample() throws Exception {
		Movie movie = createMovie("Example title", "Example description");

		String movieLocation = this.mockMvc
				.perform(post(MOVIES_RESOURCE).contentType(MediaTypes.HAL_JSON)
						.content(this.objectMapper.writeValueAsString(movie)))
				.andExpect(status().isCreated()).andReturn().getResponse().getHeader("Location");

		this.mockMvc.perform(get(movieLocation)).andExpect(status().isOk())
				.andExpect(jsonPath("user", is(movie.getUser()))).andExpect(jsonPath("title", is(movie.getTitle())))
				.andExpect(jsonPath("description", is(movie.getDescription())))
				.andExpect(jsonPath("created", notNullValue()))
				.andExpect(jsonPath("updated", notNullValue()))
				.andExpect(jsonPath("_links.self.href", is(movieLocation)));

		movie.setTitle("Example title updated");

		this.mockMvc
				.perform(patch(movieLocation).contentType(MediaTypes.HAL_JSON)
						.content(this.objectMapper.writeValueAsString(movie)))
				.andExpect(status().isNoContent())
				.andDo(document("movie-update-example",
						requestFields(
								fieldWithPath("id").description("The id of the movie").type(JsonFieldType.STRING).optional(),
								fieldWithPath("user").description("The user of the movie").type(JsonFieldType.STRING).optional(),
								fieldWithPath("title").description("The title of the movie").type(JsonFieldType.STRING).optional(),
								fieldWithPath("description").description("The description of the movie").type(JsonFieldType.STRING).optional(),
								fieldWithPath("created").description("The created date of the movie").type(JsonFieldType.NUMBER).optional(),
								fieldWithPath("updated").description("The updated date of the movie").type(JsonFieldType.NUMBER).optional())));
	}

	private Movie createMovie(String title, String description) {
		return new Movie(UUID.randomUUID().toString(), title, description, new Date());
	}

}