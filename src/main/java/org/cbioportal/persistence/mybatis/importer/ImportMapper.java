package org.cbioportal.persistence.mybatis.importer;

import org.cbioportal.model.importer.ImportLog;
import org.cbioportal.model.importer.ImportStudy;

import java.util.List;

public interface ImportMapper {
    ImportStudy getStudy(String study);
    List<String> getUsersForStudy(String study);
    ImportLog getLog(String logType, String study, String id);
    List<ImportStudy> getAllStudies();
    List<ImportLog> getAllLogsForStudy(String study, String logType);
    void addImportLog(ImportLog importLog);
    Integer getLastId();
    void updateStudyAsValidating(String study);
    void updateStudyAsImporting(String study);
}