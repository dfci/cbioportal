package org.cbioportal.model;

import java.util.LinkedHashSet;

public class StudyOverlap {
    private String studyId;
    private LinkedHashSet<String> overlappingStudyIds;
    private LinkedHashSet<String> overlappingStudyNames;

    public StudyOverlap(){}

    public StudyOverlap(String studyId) {
        this.studyId = studyId;
        overlappingStudyIds = new LinkedHashSet<>();
        overlappingStudyNames = new LinkedHashSet<>();
    }

    public String getStudyId() {
        return studyId;
    }

    public void setStudyId(String studyId) {
        this.studyId = studyId;
    }

    public LinkedHashSet<String> getOverlappingStudyIds() {
        return overlappingStudyIds;
    }

    public void setOverlappingStudyIds(LinkedHashSet<String> overlappingStudyIds) {
        this.overlappingStudyIds = overlappingStudyIds;
    }

    public LinkedHashSet<String> getOverlappingStudyNames() {
        return overlappingStudyNames;
    }

    public void setOverlappingStudyNames(LinkedHashSet<String> overlappingStudyNames) {
        this.overlappingStudyNames = overlappingStudyNames;
    }
}