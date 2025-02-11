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

import { Option } from "../shared/select/select.component";
import { allOfEnum } from "../utils/app.utils";

export enum ProcessedDatasetType {

    RECONSTRUCTEDDATASET = 'RECONSTRUCTEDDATASET',
    NONRECONSTRUCTEDDATASET = 'NONRECONSTRUCTEDDATASET',
    EXECUTION_RESULT = 'EXECUTION_RESULT',
    UNDEFINED = 'UNDEFINED'

} export namespace ProcessedDatasetType {

    export function all(): Array<ProcessedDatasetType> {
        return allOfEnum<ProcessedDatasetType>(ProcessedDatasetType);
    }

    export function getLabel(type: ProcessedDatasetType): string {
        switch(type) {
            case ProcessedDatasetType.NONRECONSTRUCTEDDATASET:
                return 'Non-reconstructed'
            case ProcessedDatasetType.RECONSTRUCTEDDATASET:
                return 'Reconstructed'
            case ProcessedDatasetType.EXECUTION_RESULT:
                return 'Execution result'
            default:
                return 'Undefined'
        }
    }

    export var options: Option<ProcessedDatasetType>[] = all().map(prop => new Option<ProcessedDatasetType>(prop, getLabel(prop)));
}
