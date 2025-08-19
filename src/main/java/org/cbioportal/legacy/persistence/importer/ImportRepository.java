package org.cbioportal.legacy.persistence.importer;

import java.util.List;
import org.cbioportal.legacy.model.importer.ImportLog;
import org.cbioportal.legacy.model.importer.ImportStudy;

public interface ImportRepository {
  ImportStudy getStudy(String study);

  List<String> getUsersForStudy(String study);

  List<ImportLog> getAllLogsForStudy(String study, String logType);

  ImportLog getLog(String logType, String study, String id);

  List<ImportStudy> getAllStudies();

  void addImportLog(ImportLog importLog);

  Integer getLastId();

  void updateStudyAsValidating(String study);

  void updateStudyAsImporting(String study);
}
