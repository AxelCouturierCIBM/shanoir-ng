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

package org.shanoir.ng.study.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;


/**
 * Study service.
 *
 */
@Service
public interface RelatedDatasetService {

	@PreAuthorize("hasAnyRole('ADMIN', 'EXPERT', 'USER')")
	String addCenterAndCopyDatasetToStudy(String datasetIds, String studyId, String centerIds);

}
