package org.cbioportal.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.cbioportal.model.importer.ImportLog;
import org.cbioportal.model.importer.ImportStudy;
import org.cbioportal.model.User;
import org.cbioportal.service.importer.ImportService;
import org.cbioportal.web.config.annotation.InternalApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@InternalApi
@RestController
@Validated
@Tag(name = "Import", description = " ")
public class ImportController {
	private final CloseableHttpClient httpClient = HttpClients.createDefault();

	@Autowired
	ImportService importService;

	@RequestMapping(value = "/api/logs/{logType}/{studyId}/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "Get the specified log file")
	@ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ImportLog.class)))
	public ResponseEntity<ImportLog> getLog(@PathVariable("logType") String logType,
			@PathVariable("studyId") String studyId, @PathVariable("id") String id, Authentication authentication) {
		HttpGet request = new HttpGet("http://importer:8080/log/" + studyId + "/" + id);
		setUserIdHeader(authentication, request);

		try (CloseableHttpResponse response = httpClient.execute(request)) {
			HttpStatus status = HttpStatus.resolve(response.getStatusLine().getStatusCode());
			ObjectMapper mapper = new ObjectMapper();
			ImportLog importLog = mapper.readValue(EntityUtils.toString(response.getEntity()), ImportLog.class);
			return new ResponseEntity<>(importLog, status == null ? HttpStatus.INTERNAL_SERVER_ERROR : status);
		} catch (Exception ex) {
			ex.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/api/logs/{logType}/{studyId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "Get the specified log file")
	@ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ImportLog.class))))
	public ResponseEntity<List<ImportLog>> getAllLogsForStudy(@PathVariable("logType") String logType,
			@PathVariable("studyId") String studyId, Authentication authentication) {
		HttpGet request = new HttpGet("http://importer:8080/logs/" + studyId + "/" + logType);
		setUserIdHeader(authentication, request);

		try (CloseableHttpResponse response = httpClient.execute(request)) {
			HttpStatus status = HttpStatus.resolve(response.getStatusLine().getStatusCode());
			ObjectMapper mapper = new ObjectMapper();
			List<ImportLog> importLogs = Arrays
					.asList(mapper.readValue(EntityUtils.toString(response.getEntity()), ImportLog[].class));
			return new ResponseEntity<>(importLogs, status == null ? HttpStatus.INTERNAL_SERVER_ERROR : status);
		} catch (Exception ex) {
			ex.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@RequestMapping(value = "/api/importer/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "Get a list of all studies in the importer")
	@ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ImportStudy.class))))
	public ResponseEntity<List<ImportStudy>> getAllImporterStudies(Authentication authentication) {
		HttpGet request = new HttpGet("http://importer:8080/studies");
		setUserIdHeader(authentication, request);

		try (CloseableHttpResponse response = httpClient.execute(request)) {
			HttpStatus status = HttpStatus.resolve(response.getStatusLine().getStatusCode());
			ObjectMapper mapper = new ObjectMapper();
			List<ImportStudy> studies = Arrays
					.asList(mapper.readValue(EntityUtils.toString(response.getEntity()), ImportStudy[].class));
			return new ResponseEntity<>(studies, status == null ? HttpStatus.INTERNAL_SERVER_ERROR : status);
		} catch (Exception ex) {
			ex.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/api/importer/{studyId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "Get study details")
	@ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ImportStudy.class)))
	public ResponseEntity<ImportStudy> getImporterStudy(@PathVariable("studyId") String studyId,
			Authentication authentication) {
		HttpGet request = new HttpGet("http://importer:8080/studies/" + studyId);
		setUserIdHeader(authentication, request);

		try (CloseableHttpResponse response = httpClient.execute(request)) {
			HttpStatus status = HttpStatus.resolve(response.getStatusLine().getStatusCode());
			ObjectMapper mapper = new ObjectMapper();
			ImportStudy importLog = mapper.readValue(EntityUtils.toString(response.getEntity()), ImportStudy.class);
			return new ResponseEntity<>(importLog, status == null ? HttpStatus.INTERNAL_SERVER_ERROR : status);
		} catch (Exception ex) {
			ex.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/api/importer/{studyId}/import", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "Run a trial import of the studyId")
	@ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = String.class)))
	public ResponseEntity<String> runTrialImport(@PathVariable("studyId") String studyId, Authentication authentication)
			throws IOException, InterruptedException {
		String username = getUserName(authentication);
		HttpGet request = new HttpGet("http://importer:8080/importer/" + studyId + "/" + username + "/import");
		setUserIdHeader(authentication, request);

		try (CloseableHttpResponse response = httpClient.execute(request)) {
			HttpStatus status = HttpStatus.resolve(response.getStatusLine().getStatusCode());
			return new ResponseEntity<>(status == null ? HttpStatus.INTERNAL_SERVER_ERROR : status);
		} catch (Exception ex) {
			ex.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/api/importer/{studyId}/validate", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "Run a trial validation of the studyId")
	@ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = String.class)))
	public ResponseEntity<String> runTrialValidation(@PathVariable("studyId") String studyId,
			Authentication authentication) throws IOException, InterruptedException {
		String username = getUserName(authentication);
		HttpGet request = new HttpGet("http://importer:8080/importer/" + studyId + "/" + username + "/validate");
		setUserIdHeader(authentication, request);

		try (CloseableHttpResponse response = httpClient.execute(request)) {
			HttpStatus status = HttpStatus.resolve(response.getStatusLine().getStatusCode());
			return new ResponseEntity<>(status == null ? HttpStatus.INTERNAL_SERVER_ERROR : status);
		} catch (Exception ex) {
			ex.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private String getUserName(Authentication authentication) {
		String username = authentication == null ? "no_auth" : authentication.getName();
		User user = importService.getUser(username);
		return user == null ? username : user.getName().replace(" ", "_");
	}

	private void setUserIdHeader(Authentication authentication, HttpGet request) {
		//TODO: This will need to be updated to support more than just the SAML2 Principal method
//		String id = ((Saml2AuthenticatedPrincipal) authentication.getPrincipal()).getFirstAttribute("username");
		String id = "JE011";

		request.setHeader("requesterId", id);
	}
}