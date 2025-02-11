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
 * Spectroscopy dataset.
 * 
 * @author msimon
 *
 */
@Entity
public class SpectDataset extends Dataset {

	/**
	 * UID 
	 */
	private static final long serialVersionUID = -4934855853599640771L;
	
	/** Spect Dataset Nature. */
	private Integer spectDatasetNature;

	public SpectDataset() {

	}

	public SpectDataset(Dataset other) {
		super(other);
		if (((SpectDataset) other).getSpectDatasetNature() != null) {
			this.spectDatasetNature = ((SpectDataset) other).getSpectDatasetNature().getId();
		} else {
			this.spectDatasetNature = null;
		}
	}

	/**
	 * @return the spectDatasetNature
	 */
	public SpectDatasetNature getSpectDatasetNature() {
		return SpectDatasetNature.getNature(spectDatasetNature);
	}

	/**
	 * @param spectDatasetNature
	 *            the spectDatasetNature to set
	 */
	public void setSpectDatasetNature(SpectDatasetNature spectDatasetNature) {
		if (spectDatasetNature == null) {
			this.spectDatasetNature = null;
		} else {
			this.spectDatasetNature = spectDatasetNature.getId();
		}
	}

	@Override
	public DatasetType getType() {
		return DatasetType.Spect;
	}

}
