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

package org.shanoir.ng.subject;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.shanoir.ng.shared.error.FieldErrorMap;
import org.shanoir.ng.shared.event.ShanoirEventService;
import org.shanoir.ng.shared.exception.EntityNotFoundException;
import org.shanoir.ng.shared.exception.MicroServiceCommunicationException;
import org.shanoir.ng.shared.jackson.JacksonUtils;
import org.shanoir.ng.shared.service.MicroserviceRequestsService;
import org.shanoir.ng.study.service.StudyService;
import org.shanoir.ng.subject.controler.SubjectApiController;
import org.shanoir.ng.subject.dto.SimpleSubjectDTO;
import org.shanoir.ng.subject.dto.SubjectDTO;
import org.shanoir.ng.subject.dto.mapper.SubjectMapper;
import org.shanoir.ng.subject.model.Subject;
import org.shanoir.ng.subject.service.SubjectService;
import org.shanoir.ng.subject.service.SubjectUniqueConstraintManager;
import org.shanoir.ng.subjectstudy.dto.SubjectStudyDTO;
import org.shanoir.ng.utils.ModelsUtil;
import org.shanoir.ng.utils.usermock.WithMockKeycloakUser;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestTemplate;

/**
 * Unit tests for subject controller.
 *
 * @author msimon
 *
 */

@WebMvcTest(controllers = SubjectApiController.class)
@ContextConfiguration(classes = {SubjectApiController.class, RestTemplate.class, MicroserviceRequestsService.class})
@AutoConfigureMockMvc(addFilters = false)
public class SubjectApiControllerTest {

	private static final String REQUEST_PATH = "/subjects";
	private static final String REQUEST_PATH_WITH_ID = REQUEST_PATH + "/1";

	@Autowired
	private MockMvc mvc;

	@MockBean
	private SubjectService subjectServiceMock;
	
	@MockBean
	private StudyService studyService;

	@MockBean
	private SubjectMapper subjectMapperMock;

	@MockBean
	private RabbitTemplate rabbitTemplate;

	@MockBean
	private SubjectUniqueConstraintManager uniqueConstraintManager;

	@MockBean
	private ShanoirEventService eventService;

	@BeforeEach
	public void setup() throws EntityNotFoundException, MicroServiceCommunicationException {
		given(subjectMapperMock.subjectsToSubjectDTOs(Mockito.anyList()))
		.willReturn(Arrays.asList(new SubjectDTO()));
		doNothing().when(subjectServiceMock).deleteById(1L);
		given(subjectServiceMock.findAll()).willReturn(Arrays.asList(new Subject()));
		given(subjectServiceMock.findById(1L)).willReturn(new Subject());
		Subject subject = new Subject();
		subject.setId(Long.valueOf(123));
		given(subjectServiceMock.create(Mockito.any(Subject.class))).willReturn(subject );
		given(uniqueConstraintManager.validate(Mockito.any(Subject.class))).willReturn(new FieldErrorMap());
	}

	@Test
	@WithMockKeycloakUser(id = 12, username = "test", authorities = { "ROLE_ADMIN" })
	public void deleteSubjectTest() throws Exception {
		mvc.perform(MockMvcRequestBuilders.delete(REQUEST_PATH_WITH_ID).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());
	}

	@Test
	public void findSubjectByIdTest() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get(REQUEST_PATH_WITH_ID).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void findSubjectsTest() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get(REQUEST_PATH).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockKeycloakUser(id = 12, username = "test", authorities = { "ROLE_ADMIN" })
	public void saveNewSubjectTest() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post(REQUEST_PATH).accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(JacksonUtils.serialize(ModelsUtil.createSubject())))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockKeycloakUser(id = 12, username = "test", authorities = { "ROLE_ADMIN" })
	public void updateSubjectTest() throws Exception {
		Subject subject = ModelsUtil.createSubject();
		subject.setId(1L);
		mvc.perform(MockMvcRequestBuilders.put(REQUEST_PATH_WITH_ID).accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(JacksonUtils.serialize(subject)))
				.andExpect(status().isNoContent());
	}

	@Test
	@WithMockKeycloakUser(id = 12, username = "test", authorities = { "ROLE_ADMIN" })
	public void testFindSubjectsByStudyId() throws Exception {
		SimpleSubjectDTO subject = new SimpleSubjectDTO();
		subject.setName("AA");
		subject.setId(2L);
		subject.setSubjectStudy(new SubjectStudyDTO());

		SimpleSubjectDTO subject2 = new SimpleSubjectDTO();
		subject2.setName("BB");
		subject2.setId(1L);
		subject2.setSubjectStudy(new SubjectStudyDTO());
		
		List<SimpleSubjectDTO> list = new ArrayList<SimpleSubjectDTO>();
		list.add(subject2);
		list.add(subject);
		
		given(subjectServiceMock.findAllSubjectsOfStudyId(1L)).willReturn(list);

		mvc.perform(MockMvcRequestBuilders.get(REQUEST_PATH + "/1/allSubjects").param("preclinical", "null").accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(JacksonUtils.serialize(subject)))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockKeycloakUser(id = 12, username = "test", authorities = { "ROLE_ADMIN" })
	public void testFindSubjectsByStudyIdPreclinical() throws Exception {
		SimpleSubjectDTO subject = new SimpleSubjectDTO();
		subject.setName("AA");
		subject.setId(2L);
		subject.setSubjectStudy(new SubjectStudyDTO());

		SimpleSubjectDTO subject2 = new SimpleSubjectDTO();
		subject2.setName("BB");
		subject2.setId(1L);
		subject2.setSubjectStudy(new SubjectStudyDTO());

		
		List<SimpleSubjectDTO> list = new ArrayList<SimpleSubjectDTO>();
		list.add(subject2);
		list.add(subject);
		
		given(subjectServiceMock.findAllSubjectsOfStudyId(1L)).willReturn(list);
		
		mvc.perform(MockMvcRequestBuilders.get(REQUEST_PATH + "/1/allSubjects").param("preclinical", "null").accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(JacksonUtils.serialize(subject)))
				.andExpect(status().isOk());
	}

}
