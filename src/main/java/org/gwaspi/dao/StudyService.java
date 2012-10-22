package org.gwaspi.dao;

import java.io.IOException;
import java.util.List;
import org.gwaspi.database.DbManager;
import org.gwaspi.model.Study;

public interface StudyService {

	Study getById(int studyId) throws IOException;

	List<Study> getAll() throws IOException;

	Object[][] getAllAsTable() throws IOException;

	String createStudyManagementTable(DbManager db, Object[] insertValues);

	void insertNewStudy(String studyName, String description);

	void deleteStudy(int studyId, boolean deleteReports) throws IOException;

	String createStudyLogFile(Integer studyId) throws IOException;
}