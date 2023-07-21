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

package org.shanoir.ng.studycard.service;

import java.util.List;

import org.shanoir.ng.shared.exception.EntityNotFoundException;
import org.shanoir.ng.shared.exception.MicroServiceCommunicationException;
import org.shanoir.ng.studycard.model.Card;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

public interface CardService<T extends Card> {

	void deleteById(Long id) throws EntityNotFoundException, MicroServiceCommunicationException;

	@PreAuthorize("hasRole('ADMIN') or (hasRole('EXPERT') and @datasetSecurityService.hasRightOnStudy(#card.getStudyId(), 'CAN_ADMINISTRATE'))")
	T save(T card) throws MicroServiceCommunicationException;
	
	@PreAuthorize("hasRole('ADMIN') or (hasRole('EXPERT') and @datasetSecurityService.hasUpdateRightOnCard(#card, 'CAN_ADMINISTRATE'))")
	T update(T card) throws EntityNotFoundException, MicroServiceCommunicationException;
	
	@PreAuthorize("hasAnyRole('ADMIN', 'EXPERT', 'USER')")
	@PostAuthorize("hasRole('ADMIN') or @datasetSecurityService.filterCardList(returnObject, 'CAN_SEE_ALL')")
	List<T> findAll();
	
	// MK: had to move down annotation to -ServiceImpls as Spring Sec throws a duplicate
	// annotation exception, because of 2 interface hierarchy, that is not well managed.
	T findById(Long id);
	
	/**
	 * Find template by its id.
	 *
	 * @param id template id.
	 * @return a template or null.
	 */
	T findByName(String name);

	@PreAuthorize("hasAnyRole('ADMIN', 'EXPERT', 'USER')")
	@PostAuthorize("hasRole('ADMIN') or @datasetSecurityService.filterCardList(returnObject, 'CAN_SEE_ALL')")
	List<T> search(List<Long> studyIdList);
	
	@PreAuthorize("hasAnyRole('ADMIN', 'EXPERT', 'USER')")
	@PostAuthorize("hasRole('ADMIN') or @datasetSecurityService.filterCardList(returnObject, 'CAN_SEE_ALL')")
	List<T> findByStudy (Long studyId);

}
