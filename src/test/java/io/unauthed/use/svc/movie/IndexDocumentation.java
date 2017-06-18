package io.unauthed.use.svc.movie;

 import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;
import java.util.UUID;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IndexDocumentation {

	private static final String API_ROOT = "/api/v1";
	private static final String MOVIES_RESOURCE = API_ROOT + "/movies";

	@Rule
	public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private WebApplicationContext context;

	private MockMvc mockMvc;

	@Before
	public void setUp() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
				.apply(documentationConfiguration(this.restDocumentation)).alwaysDo(document("{method-name}/{step}/"))
				.build();
	}

	@Test
	public void index() throws Exception {
		this.mockMvc.perform(get(API_ROOT).accept(MediaTypes.HAL_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("_links.movies", is(notNullValue())));
	}

	@Test
	public void creatingAMovie() throws JsonProcessingException, Exception {
		String movieLocation = createMovie();
		getMovie(movieLocation);
	}

	String createMovie() throws Exception {
		Movie movie = new Movie(UUID.randomUUID().toString(), "Movie title", "Movie description", new Date());

		String movieLocation = this.mockMvc
				.perform(post(MOVIES_RESOURCE).contentType(MediaTypes.HAL_JSON)
				.content(objectMapper.writeValueAsString(movie)))
				.andExpect(status().isCreated()).andExpect(header().string("Location", notNullValue())).andReturn()
				.getResponse().getHeader("Location");
		return movieLocation;
	}

	MvcResult getMovie(String movieLocation) throws Exception {
		return this.mockMvc.perform(get(movieLocation)).andExpect(status().isOk())
				.andExpect(jsonPath("title", is(notNullValue()))).andExpect(jsonPath("description", is(notNullValue())))
				.andReturn();
	}
}