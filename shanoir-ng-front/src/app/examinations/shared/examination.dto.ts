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
import { Injectable } from '@angular/core';

import { Center } from '../../centers/shared/center.model';
import { CenterService } from '../../centers/shared/center.service';
import { IdName } from '../../shared/models/id-name.model';
import { Study } from '../../studies/shared/study.model';
import { StudyService } from '../../studies/shared/study.service';
import { Subject } from '../../subjects/shared/subject.model';
import { Examination } from './examination.model';
import { InstrumentBasedAssessment } from "../instrument-assessment/instrument.model"
import {UnitOfMeasure} from "../../enum/unitofmeasure.enum";

@Injectable()
export class ExaminationDTOService {

    constructor(
        private studyService: StudyService,
        private centerService: CenterService) {}

    /**
     * Convert from a DTO to an Entity
     * Warning : DO NOT USE THIS IN A LOOP, use toEntityList instead
     * @param result can be used to get an immediate temporary result without async data
     */
    public toEntity(dto: ExaminationDTO, result?: Examination): Promise<Examination> {
        if (!result) result = new Examination();
        ExaminationDTOService.mapSyncFields(dto, result);
        let promises: Promise<any>[] = [];
        if (dto.studyId) promises.push(this.studyService.get(dto.studyId).then(study => result.study = study));
        if (dto.centerId) promises.push(this.centerService.get(dto.centerId).then(center => result.center = center));
        return Promise.all(promises).then(() => result);
    }

    /**
     * Convert from a DTO list to an Entity list
     * @param result can be used to get an immediate temporary result without async data
     */
    public toEntityList(dtos: ExaminationDTO[], result?: Examination[]): Promise<Examination[]> {
        if (!result) result = [];
        if (dtos) {
            for (let dto of dtos ? dtos : []) {
                let entity = new Examination();
                ExaminationDTOService.mapSyncFields(dto, entity);
                result.push(entity);
            }
        }
        return Promise.all([
            this.studyService.getStudiesNames(),
            this.centerService.getCentersNames(),
        ]).then(([studies, centers]: [IdName[], IdName[]]) => {
            for (let entity of result) {
                if (entity.study) entity.study = studies.find(study => study.id == entity.study.id);
                if (entity.center) entity.center = centers.find(center => center.id == entity.center.id);
            }
            return result;
        });
    }

    static mapSyncFields(dto: ExaminationDTO, entity: Examination): Examination {
        entity.id = dto.id;
        entity.examinationDate = new Date(dto.examinationDate);
        entity.comment = dto.comment;
        entity.source = dto.source;
        entity.copies = dto.copies;
        entity.note = dto.note;
        entity.subjectWeight = dto.subjectWeight;
        entity.weightUnitOfMeasure = dto.weightUnitOfMeasure;
        entity.preclinical = dto.preclinical;
        entity.extraDataFilePathList = dto.extraDataFilePathList;
        entity.instrumentBasedAssessmentList = dto.instrumentBasedAssessmentList;
        if (dto.studyId) {
            entity.study = new Study();
            entity.study.id = dto.studyId;
        }
        if (dto.centerId) {
            entity.center = new Center();
            entity.center.id = dto.centerId;
        }
        if (dto.subject) {
            entity.subject = new Subject();
            entity.subject.id = dto.subject.id;
            entity.subject.name = dto.subject.name;
        }
        return entity;
    }
}

export class ExaminationDTO {
    id: number;
    centerId: number;
	comment: string;
    examinationDate: Date;
    note: string;
    studyId: number;
    subject: IdName;
    subjectWeight: number;
    weightUnitOfMeasure: UnitOfMeasure;
    preclinical: boolean;
    instrumentBasedAssessmentList: InstrumentBasedAssessment[];
    extraDataFilePathList: string[] = [];
    copies: number[];
    source: number;
    constructor(examination?: Examination) {
        if (examination) {
            this.id = examination.id;
            this.centerId = examination.center ? examination.center.id : null;
            this.comment = examination.comment;
            this.examinationDate = examination.examinationDate;
            this.note = examination.note;
            this.studyId = examination.study ? examination.study.id : null;
            this.subject = examination.subject ? new IdName(examination.subject.id, examination.subject.name) : null;
            this.subjectWeight = examination.subjectWeight;
            this.weightUnitOfMeasure = examination.weightUnitOfMeasure;
            this.preclinical = examination.preclinical;
            this.extraDataFilePathList = examination.extraDataFilePathList;
            this.instrumentBasedAssessmentList = examination.instrumentBasedAssessmentList;
            this.source = examination.source;
            this.copies = examination.copies;
        }
    }
}
