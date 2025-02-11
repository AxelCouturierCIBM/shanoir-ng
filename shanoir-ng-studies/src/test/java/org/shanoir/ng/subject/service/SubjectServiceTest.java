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

package org.shanoir.ng.subject.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.BDDMockito.given;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.shanoir.ng.shared.event.ShanoirEvent;
import org.shanoir.ng.shared.event.ShanoirEventService;
import org.shanoir.ng.shared.exception.EntityNotFoundException;
import org.shanoir.ng.shared.exception.MicroServiceCommunicationException;
import org.shanoir.ng.shared.exception.RestServiceException;
import org.shanoir.ng.shared.exception.ShanoirException;
import org.shanoir.ng.studyexamination.StudyExaminationRepository;
import org.shanoir.ng.subject.dto.mapper.SubjectMapper;
import org.shanoir.ng.subject.model.HemisphericDominance;
import org.shanoir.ng.subject.model.ImagedObjectCategory;
import org.shanoir.ng.subject.model.PseudonymusHashValues;
import org.shanoir.ng.subject.model.Sex;
import org.shanoir.ng.subject.model.Subject;
import org.shanoir.ng.subject.model.UserPersonalCommentSubject;
import org.shanoir.ng.subject.repository.SubjectRepository;
import org.shanoir.ng.utils.ModelsUtil;
import org.shanoir.ng.utils.usermock.WithMockKeycloakUser;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * Subject service test.
 *
 * @author msimon
 *
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class SubjectServiceTest {

	private static final Long SUBJECT_ID = 1L;

	@Mock
	private SubjectRepository subjectRepository;

	@Mock
	private RabbitTemplate rabbitTemplate;
	
	@Mock
	private SubjectMapper subjectMapperMock;

	@InjectMocks
	private SubjectServiceImpl subjectService;
	
	@Mock
	private ObjectMapper objectMapper;

	
	@Mock
	private StudyExaminationRepository studyExaminationRepository;

	@BeforeEach
	public void setup() {
		given(subjectRepository.findAll()).willReturn(Arrays.asList(ModelsUtil.createSubject()));
		given(subjectRepository.findById(SUBJECT_ID)).willReturn(Optional.of(ModelsUtil.createSubject()));
		//given(subjectRepository.save(Mockito.any(Subject.class))).willReturn(ModelsUtil.createSubject());
		given(subjectRepository.save(Mockito.any(Subject.class))).willReturn(createSubjectTosave());
	}

	@Test
	@WithMockKeycloakUser(id = 3, username = "jlouis", authorities = { "ROLE_ADMIN" })
	public void deleteByIdTest() throws EntityNotFoundException {
		subjectService.deleteById(SUBJECT_ID);

		Mockito.verify(subjectRepository, Mockito.times(1)).deleteById(Mockito.anyLong());
	}

	@Test
	public void findAllTest() {
		final List<Subject> subjects = subjectService.findAll();
		Assertions.assertNotNull(subjects);
		Assertions.assertTrue(subjects.size() == 1);

		Mockito.verify(subjectRepository, Mockito.times(1)).findAll();
	}

	@Test
	public void findByIdTest() {
		final Subject subject = subjectService.findById(SUBJECT_ID);
		Assertions.assertNotNull(subject);
		Assertions.assertTrue(ModelsUtil.SUBJECT_NAME.equals(subject.getName()));

		Mockito.verify(subjectRepository, Mockito.times(1)).findById(Mockito.anyLong());
	}

	@Test
	public void saveTest() throws MicroServiceCommunicationException {
		subjectService.create(createSubjectTosave());
		Mockito.verify(subjectRepository, Mockito.times(1)).save(Mockito.any(Subject.class));
	}

	@Test
	public void updateTest() throws RestServiceException, ShanoirException {
		final Subject updatedSubject = subjectService.update(createSubjectToUpdate());
		Assertions.assertNotNull(updatedSubject);
		Assertions.assertTrue(Sex.F.equals(updatedSubject.getSex()));

		Mockito.verify(subjectRepository, Mockito.times(1)).save(Mockito.any(Subject.class));
	}

	@Test
	public void updateTestChangeName() throws EntityNotFoundException, MicroServiceCommunicationException, RestServiceException {
		try {
			Subject updated = createSubjectToUpdate();
			updated.setName("new name");
			subjectService.update(updated);
		} catch (ShanoirException exception) {
			assertEquals(HttpStatus.FORBIDDEN.value(), exception.getErrorCode());
			assertEquals("You cannot update subject common name.", exception.getMessage());
			return;
		}
		fail();
	}

	private Subject createSubjectToUpdate() {
		final Subject subject = new Subject();
		subject.setId(SUBJECT_ID);
		subject.setSex(Sex.F);
		subject.setName(ModelsUtil.SUBJECT_NAME);
		return subject;
	}

	private Subject createSubjectTosave() {
		final Subject subject = new Subject();
		//subject.setName("Toto");
		//subject.setId(1L);
		subject.setBirthDate(Instant.ofEpochMilli(1392122691000L).atZone(ZoneId.systemDefault()).toLocalDate());
		subject.setIdentifier("Titi");

		subject.setImagedObjectCategory(ImagedObjectCategory.PHANTOM);
		subject.setLanguageHemisphericDominance(HemisphericDominance.Left);
		subject.setManualHemisphericDominance(HemisphericDominance.Left);
		PseudonymusHashValues pseudonymusHashValues= new PseudonymusHashValues();
		pseudonymusHashValues.setBirthDateHash("uihuizdhuih");
		subject.setPseudonymusHashValues(pseudonymusHashValues);
		subject.setSex(Sex.F);
		UserPersonalCommentSubject userPersonalCommentList1= new UserPersonalCommentSubject();
		userPersonalCommentList1.setComment("comment1");
		UserPersonalCommentSubject userPersonalCommentList2= new UserPersonalCommentSubject();
		userPersonalCommentList1.setComment("comment2");
		List<UserPersonalCommentSubject> listSubjectComments = new ArrayList<>();
		listSubjectComments.add(userPersonalCommentList1);
		listSubjectComments.add(userPersonalCommentList2);
		subject.setUserPersonalCommentList(listSubjectComments);
		return subject;
	}
}