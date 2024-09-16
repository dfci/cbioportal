package org.cbioportal.model;

import java.io.Serializable;
import java.util.Set;

public class StudyAndSampleIds implements Serializable {
    private String studyId;
    private String studyName;
    private Set<String> sampleIds;

    public String getStudyId() {
        return studyId;
    }

    public void setStudyId(String studyId) {
        this.studyId = studyId;
    }

    public Set<String> getSampleIds() {
        return sampleIds;
    }

    public void setSampleIds(Set<String> sampleIds) {
        this.sampleIds = sampleIds;
    }

    public String getStudyName() {
        return studyName;
    }

    public void setStudyName(String studyName) {
        this.studyName = studyName;
    }
}