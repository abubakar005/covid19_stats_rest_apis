package covid19.statistics.reports;

import com.fasterxml.jackson.databind.ObjectMapper;
import covid19.statistics.reports.dto.AuthenticationRequest;
import covid19.statistics.reports.dto.AuthenticationResponse;
import covid19.statistics.reports.service.Covid19ReportsService;
import covid19.statistics.reports.service.MyUserDetailsService;
import covid19.statistics.reports.util.JwtUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@WebMvcTest
class ReportsApplicationTests {

	public static Logger LOG = LoggerFactory.getLogger(ReportsApplicationTests.class);

	private String LOCAL_URL = "http://localhost:8080";
	private int STATUS_CODE = 200;
	private String USER_NAME = "admin";
	private String USER_PASSWORD = "complexpassword$";
	public static String JWT_TOKEN = "Bearer ";
	private RestTemplate restTemplate = new RestTemplate();

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	@MockBean
	private Covid19ReportsService covid19ReportsService;

	@Autowired
	@MockBean
	private MyUserDetailsService myUserDetailsService;

	@MockBean
	private JwtUtil jwtUtil;


	@Test
	void getJwt() {

		AuthenticationRequest request = new AuthenticationRequest();
		request.setUsername(USER_NAME);
		request.setPassword(USER_PASSWORD);

		try {
			MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/jwt")
					.content(new ObjectMapper().writeValueAsBytes(request))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andReturn();
		} catch (Exception e) {
			LOG.error("error :{}", e);
		}
	}

	@Test
	@Order(2)
	void getActiveUsers() {
		try {
			MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/active-users")
					.header("Authorization",JWT_TOKEN)
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isUnauthorized())
					.andReturn();
		} catch (Exception e) {
			LOG.error("error :{}" , e);
			Assertions.assertEquals(true, false);
		}
	}

	@Test
	@Order(3)
	void totalCasesToday() {
		try {
			MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/covid19/today/cases")
					.header("Authorization", JWT_TOKEN)
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isUnauthorized())
					.andReturn();
		} catch (Exception e) {
			LOG.error("error :{}" , e);
			Assertions.assertEquals(true, false);
		}
	}

	public RestTemplate restTemplate() {

		final RestTemplate restTemplate = new RestTemplate();

		List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
		messageConverters.add(converter);
		restTemplate.setMessageConverters(messageConverters);

		return restTemplate;
	}

}
