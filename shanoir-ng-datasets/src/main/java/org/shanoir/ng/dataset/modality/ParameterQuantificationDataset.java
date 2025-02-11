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

package org.shanoir.ng.dataset.modality;

import jakarta.persistence.Entity;

import org.shanoir.ng.dataset.model.Dataset;
import org.shanoir.ng.dataset.model.DatasetType;

/**
 * Parameter quantification dataset.
 * 
 * @author msimon
 *
 */
@Entity
public class ParameterQuantificationDataset extends Dataset {

	/**
	 * UID
	 */
	private static final long serialVersionUID = 7649321017486497468L;

	public ParameterQuantificationDataset() {

	}

	public ParameterQuantificationDataset(Dataset other) {
		super(other);
		if (((ParameterQuantificationDataset) other).getParameterQuantificationDatasetNature() != null) {
			this.parameterQuantificationDatasetNature = ((ParameterQuantificationDataset) other).getParameterQuantificationDatasetNature().getId();
		} else {
			this.parameterQuantificationDatasetNature = null;
		}
	}

	/** Parameter Quantification Dataset Nature. */
	private Integer parameterQuantificationDatasetNature;

	/**
	 * @return the parameterQuantificationDatasetNature
	 */
	public ParameterQuantificationDatasetNature getParameterQuantificationDatasetNature() {
		return ParameterQuantificationDatasetNature.getNature(parameterQuantificationDatasetNature);
	}

	/**
	 * @param parameterQuantificationDatasetNature
	 *            the parameterQuantificationDatasetNature to set
	 */
	public void setParameterQuantificationDatasetNature(
			ParameterQuantificationDatasetNature parameterQuantificationDatasetNature) {
		if (parameterQuantificationDatasetNature == null) {
			this.parameterQuantificationDatasetNature = null;
		} else {
			this.parameterQuantificationDatasetNature = parameterQuantificationDatasetNature.getId();
		}
	}

	@Override
	public DatasetType getType() {
		return DatasetType.ParameterQuantification;
	}

}
