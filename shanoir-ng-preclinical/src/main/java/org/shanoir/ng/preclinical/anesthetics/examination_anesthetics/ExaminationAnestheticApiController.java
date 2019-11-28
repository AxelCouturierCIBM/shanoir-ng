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

package org.shanoir.ng.preclinical.anesthetics.examination_anesthetics;

import java.util.List;

import org.shanoir.ng.preclinical.anesthetics.anesthetic.Anesthetic;
import org.shanoir.ng.preclinical.anesthetics.anesthetic.AnestheticService;
import org.shanoir.ng.shared.error.FieldErrorMap;
import org.shanoir.ng.shared.exception.ErrorDetails;
import org.shanoir.ng.shared.exception.ErrorModel;
import org.shanoir.ng.shared.exception.RestServiceException;
import org.shanoir.ng.shared.exception.ShanoirException;
import org.shanoir.ng.shared.validation.EditableOnlyByValidator;
import org.shanoir.ng.shared.validation.UniqueValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.annotations.ApiParam;

@Controller
public class ExaminationAnestheticApiController implements ExaminationAnestheticApi {

	private static final Logger LOG = LoggerFactory.getLogger(ExaminationAnestheticApiController.class);

	@Autowired
	private ExaminationAnestheticService examAnestheticsService;

	@Autowired
	private AnestheticService anestheticsService;

	public ResponseEntity<ExaminationAnesthetic> addExaminationAnesthetic(
			@ApiParam(value = "examination id", required = true) @PathVariable("id") Long id,
			@ApiParam(value = "anesthetic to add to examination", required = true) @RequestBody ExaminationAnesthetic examAnesthetic,
			BindingResult result) throws RestServiceException {

		final FieldErrorMap accessErrors = this.getCreationRightsErrors(examAnesthetic);
		final FieldErrorMap hibernateErrors = new FieldErrorMap(result);
		final FieldErrorMap uniqueErrors = this.getUniqueConstraintErrors(examAnesthetic);
		/* Merge errors. */
		final FieldErrorMap errors = new FieldErrorMap(accessErrors, hibernateErrors, uniqueErrors);
		if (!errors.isEmpty()) {
			throw new RestServiceException(
					new ErrorModel(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Bad arguments", new ErrorDetails(errors)));
		}

		// Guarantees it is a creation, not an update
		examAnesthetic.setId(null);
		// Set the examination id
		examAnesthetic.setExaminationId(id);

		/* Save examination anesthetic in db. */
		try {
			final ExaminationAnesthetic createdExamAnesthetic = examAnestheticsService.save(examAnesthetic);
			return new ResponseEntity<ExaminationAnesthetic>(createdExamAnesthetic, HttpStatus.OK);
		} catch (ShanoirException e) {
			throw new RestServiceException(e,
					new ErrorModel(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Bad arguments", null));
		}
	}

	public ResponseEntity<Void> deleteExaminationAnesthetic(
			@ApiParam(value = "Examination id", required = true) @PathVariable("id") Long id,
			@ApiParam(value = "Examination anesthetic id to delete", required = true) @PathVariable("eaid") Long eaid)
			throws RestServiceException {

		if (examAnestheticsService.findById(eaid) == null) {
			return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
		}
		try {
			examAnestheticsService.deleteById(eaid);
		} catch (ShanoirException e) {
			return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
		}
		return new ResponseEntity<Void>(HttpStatus.OK);

	}

	public ResponseEntity<ExaminationAnesthetic> getExaminationAnestheticById(
			@ApiParam(value = "examination id", required = true) @PathVariable("id") Long id,
			@ApiParam(value = "ID of examination id that needs to be fetched", required = true) @PathVariable("eaid") Long eaid)
			throws RestServiceException {
		final ExaminationAnesthetic examAnesthetic = examAnestheticsService.findById(eaid);
		if (examAnesthetic == null) {
			return new ResponseEntity<ExaminationAnesthetic>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<ExaminationAnesthetic>(examAnesthetic, HttpStatus.OK);
	}

	public ResponseEntity<List<ExaminationAnesthetic>> getExaminationAnesthetics(
			@ApiParam(value = "examination id", required = true) @PathVariable("id") Long id)
			throws RestServiceException {
		final List<ExaminationAnesthetic> examAnesthetics = examAnestheticsService.findByExaminationId(id);
		return new ResponseEntity<List<ExaminationAnesthetic>>(examAnesthetics, HttpStatus.OK);
	}

	public ResponseEntity<Void> updateExaminationAnesthetic(
			@ApiParam(value = "examination id", required = true) @PathVariable("id") Long id,
			@ApiParam(value = "ID of examination anesthetic that needs to be updated", required = true) @PathVariable("eaid") Long eaid,
			@ApiParam(value = "Examination anesthetic object that needs to be updated", required = true) @RequestBody ExaminationAnesthetic examAnesthetic,
			final BindingResult result) throws RestServiceException {

		examAnesthetic.setId(eaid);
		final FieldErrorMap accessErrors = this.getUpdateRightsErrors(examAnesthetic);
		final FieldErrorMap hibernateErrors = new FieldErrorMap(result);
		final FieldErrorMap uniqueErrors = this.getUniqueConstraintErrors(examAnesthetic);
		/* Merge errors. */
		final FieldErrorMap errors = new FieldErrorMap(accessErrors, hibernateErrors, uniqueErrors);
		if (!errors.isEmpty()) {
			throw new RestServiceException(
					new ErrorModel(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Bad arguments", new ErrorDetails(errors)));
		}

		try {
			examAnestheticsService.update(examAnesthetic);
		} catch (ShanoirException e) {
			LOG.error("Error while trying to update examination anesthetic " + eaid + " : ", e);
			throw new RestServiceException(e,
					new ErrorModel(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Bad arguments", null));
		}
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	public ResponseEntity<List<ExaminationAnesthetic>> getExaminationAnestheticsByAnesthetic(
			@ApiParam(value = "anesthetic id", required = true) @PathVariable("id") Long id)
			throws RestServiceException {
		Anesthetic anesthetic = anestheticsService.findById(id);
		if (anesthetic == null) {
			return new ResponseEntity<List<ExaminationAnesthetic>>(HttpStatus.NOT_FOUND);
		} else {
			final List<ExaminationAnesthetic> subexaminations = examAnestheticsService.findByAnesthetic(anesthetic);
			if (subexaminations.isEmpty()) {
				return new ResponseEntity<List<ExaminationAnesthetic>>(HttpStatus.NO_CONTENT);
			}
			return new ResponseEntity<List<ExaminationAnesthetic>>(subexaminations, HttpStatus.OK);
		}
	}

	private FieldErrorMap getUpdateRightsErrors(final ExaminationAnesthetic examAnesthetic) {
		final ExaminationAnesthetic previousStateExamAnesthetic = examAnestheticsService
				.findById(examAnesthetic.getId());
		final FieldErrorMap accessErrors = new EditableOnlyByValidator<ExaminationAnesthetic>()
				.validate(previousStateExamAnesthetic, examAnesthetic);
		return accessErrors;
	}

	private FieldErrorMap getCreationRightsErrors(final ExaminationAnesthetic examAnesthetics) {
		return new EditableOnlyByValidator<ExaminationAnesthetic>().validate(examAnesthetics);
	}

	private FieldErrorMap getUniqueConstraintErrors(final ExaminationAnesthetic examAnesthetic) {
		final UniqueValidator<ExaminationAnesthetic> uniqueValidator = new UniqueValidator<ExaminationAnesthetic>(
				examAnestheticsService);
		final FieldErrorMap uniqueErrors = uniqueValidator.validate(examAnesthetic);
		return uniqueErrors;
	}

}
