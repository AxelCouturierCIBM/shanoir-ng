/**
 * Shanoir NG - Import, manage and share neuroimaging data
 * Copyright (C) 2009-2019 Inria - https://www.inria.fr/
 * Contact us on https://project.inria.fr/shanoir/
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/gpl-3.0.html
 */

//package org.shanoir.ng.manufacturermodel;
//
//import static org.junit.Assertions.assertEquals;
//
//import java.io.IOException;
//
//import org.apache.http.client.ClientProtocolException;
//import org.junit.jupiter.api.Test;
//import org.shanoir.ng.manufacturermodel.model.ManufacturerModel;
//import org.shanoir.ng.utils.KeycloakControllerTestIT;
//import org.shanoir.ng.utils.ModelsUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.test.context.ActiveProfiles;
//
///**
// * Integration tests for manufacturer model controller.
// *
// * @author msimon
// *
// */
//
//@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
//@ActiveProfiles("dev")
//public class ManufacturerModelApiControllerTestIT extends KeycloakControllerTestIT {
//
//	private static final String REQUEST_PATH = "/manufacturermodels";
//	private static final String REQUEST_PATH_WITH_ID = REQUEST_PATH + "/1";
//
//	@Autowired
//	private TestRestTemplate restTemplate;
//
//	@Test
//	public void findManufacturerModelByIdProtected() {
//		final ResponseEntity<String> response = restTemplate.getForEntity(REQUEST_PATH_WITH_ID, String.class);
//		assertEquals(HttpStatus.FOUND, response.getStatusCode());
//	}
//
//	@Test
//	public void findManufacturerModelByIdWithLogin() throws ClientProtocolException, IOException {
//		final HttpEntity<String> entity = new HttpEntity<String>(getHeadersWithToken(true));
//
//		final ResponseEntity<String> response = restTemplate.exchange(REQUEST_PATH_WITH_ID, HttpMethod.GET, entity,
//				String.class);
//		assertEquals(HttpStatus.OK, response.getStatusCode());
//	}
//
//	@Test
//	public void findManufacturerModelsProtected() {
//		final ResponseEntity<String> response = restTemplate.getForEntity(REQUEST_PATH, String.class);
//		assertEquals(HttpStatus.FOUND, response.getStatusCode());
//	}
//
//	@Test
//	public void findManufacturerModelsWithLogin() throws ClientProtocolException, IOException {
//		final HttpEntity<String> entity = new HttpEntity<String>(getHeadersWithToken(true));
//
//		final ResponseEntity<String> response = restTemplate.exchange(REQUEST_PATH, HttpMethod.GET, entity,
//				String.class);
//		assertEquals(HttpStatus.OK, response.getStatusCode());
//	}
//
//	@Test
//	public void findManufacturerModelsWithBadRole() {
//		final HttpEntity<ManufacturerModel> entity = new HttpEntity<ManufacturerModel>(null,
//				getHeadersWithToken(false));
//
//		final ResponseEntity<String> response = restTemplate.exchange(REQUEST_PATH, HttpMethod.GET, entity,
//				String.class);
//		assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
//	}
//
//	@Test
//	public void saveNewManufacturerModelProtected() {
//		final ResponseEntity<String> response = restTemplate.postForEntity(REQUEST_PATH, new ManufacturerModel(),
//				String.class);
//		assertEquals(HttpStatus.FOUND, response.getStatusCode());
//	}
//
//	@Test
//	public void updateManufacturerModelProtected() {
//		final HttpEntity<ManufacturerModel> entity = new HttpEntity<>(ModelsUtil.createManufacturerModel());
//
//		final ResponseEntity<String> response = restTemplate.exchange(REQUEST_PATH_WITH_ID, HttpMethod.PUT, entity,
//				String.class);
//		assertEquals(HttpStatus.FOUND, response.getStatusCode());
//	}
//
//	@Test
//	public void updateManufacturerModelWithLogin() throws ClientProtocolException, IOException {
//		final HttpEntity<ManufacturerModel> entity = new HttpEntity<>(ModelsUtil.createManufacturerModel(),
//				getHeadersWithToken(true));
//
//		final ResponseEntity<String> response = restTemplate.exchange(REQUEST_PATH_WITH_ID, HttpMethod.PUT, entity,
//				String.class);
//		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
//	}
//
//}
