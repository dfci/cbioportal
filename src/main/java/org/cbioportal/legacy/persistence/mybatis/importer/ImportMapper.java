package org.cbioportal.legacy.persistence.mybatis.importer;

import java.util.List;
import org.cbioportal.legacy.model.importer.ImportLog;
import org.cbioportal.legacy.model.importer.ImportStudy;

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
