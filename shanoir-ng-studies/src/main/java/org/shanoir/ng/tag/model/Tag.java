package org.shanoir.ng.tag.model;

import java.util.List;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.shanoir.ng.shared.hateoas.HalEntity;
import org.shanoir.ng.study.model.Study;
import org.shanoir.ng.subjectstudy.model.SubjectStudyTag;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Tag extends HalEntity {

	private static final long serialVersionUID = 1L;

	private String name;

	private String color;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "study_id")
	private Study study;
	
	@JsonIgnore
	@OneToMany(mappedBy = "tag", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private List<SubjectStudyTag> subjectStudyTags;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the color
	 */
	public String getColor() {
		return color;
	}

	/**
	 * @param color the color to set
	 */
	public void setColor(String color) {
		this.color = color;
	}

	/**
	 * @return the study
	 */
	public Study getStudy() {
		return study;
	}

	/**
	 * @param study the study to set
	 */
	public void setStudy(Study study) {
		this.study = study;
	}

	public List<SubjectStudyTag> getSubjectStudyTags() {
		return subjectStudyTags;
	}

	public void setSubjectStudyTags(List<SubjectStudyTag> subjectStudyTags) {
		if (this.subjectStudyTags != null) {
			this.subjectStudyTags.clear();
			if (subjectStudyTags != null) {
				this.subjectStudyTags.addAll(subjectStudyTags);
			}			
		} else {
			this.subjectStudyTags = subjectStudyTags;
		}
	}
}
