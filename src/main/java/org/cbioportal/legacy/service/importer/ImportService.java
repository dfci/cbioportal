package org.cbioportal.legacy.service.importer;

import java.util.List;
import org.cbioportal.legacy.model.User;
import org.cbioportal.model.importer.ImportLog;
import org.cbioportal.model.importer.ImportStudy;

public interface ImportService {
  ImportStudy getStudy(String study);

  ImportLog getLog(String log, String study, String id);

  List<ImportStudy> getAllStudies();

  List<ImportLog> getAllLogsForStudy(String study, String logType);

  void addImportLog(ImportLog importLog);

  Integer getLastId();

  void updateStudyAsValidating(String study);

  void updateStudyAsImporting(String study);

  User getUser(String username);
}
