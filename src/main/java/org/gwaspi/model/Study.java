package org.gwaspi.model;

import org.gwaspi.constants.cDBGWASpi;
import org.gwaspi.constants.cDBMatrix;
import org.gwaspi.database.DbManager;
import org.gwaspi.global.ServiceLocator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Fernando Muñiz Fernandez
 * IBE, Institute of Evolutionary Biology (UPF-CSIC)
 * CEXS-UPF-PRBB
 */
public class Study {

	private static final Logger log
			= LoggerFactory.getLogger(Study.class);

	private int id = Integer.MIN_VALUE; // id INTEGER generated by default as identity
	private String name = ""; // name VARCHAR(64)
	private String description = ""; // study_description LONG VARCHAR
	private String studyType = ""; // study_type VARCHAR(255)
	private String validity = ""; // validity SMALLINT
	private Date creationDate; // creation_date TIMESTAMP (2009-05-13 17:22:10.984)
	private List<Integer> studyMatrices = new ArrayList<Integer>();

	/**
	 * This will init the Study object requested from the DB
	 */
	public Study(int _studyId) throws IOException {
		List<Map<String, Object>> rs = getStudy(_studyId);

		// PREVENT PHANTOM-DB READS EXCEPTIONS
		if (!rs.isEmpty() && rs.get(0).size() == cDBGWASpi.T_CREATE_STUDIES.length) {
			id = Integer.parseInt(rs.get(0).get("id").toString());
			name = rs.get(0).get("name").toString();
			description = rs.get(0).get("study_description").toString();
			studyType = rs.get(0).get("study_type").toString();
			validity = rs.get(0).get("validity").toString();
			creationDate = org.gwaspi.global.Utils.stringToDate(rs.get(0).get("creation_date").toString(), "yyyy-MM-dd hh:mm:ss.SSS");

			studyMatrices = getStudyMatricesId(_studyId);
		}
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getStudyType() {
		return studyType;
	}

	public String getValidity() {
		return validity;
	}

	public String getDescription() {
		return description;
	}

	private static List<Map<String, Object>> getStudy(int studyId) throws IOException {
		List<Map<String, Object>> rs = null;
		String dbName = cDBGWASpi.DB_DATACENTER;
		DbManager studyDbManager = ServiceLocator.getDbManager(dbName);
		try {
			rs = studyDbManager.executeSelectStatement("SELECT * FROM " + cDBGWASpi.SCH_APP + "." + cDBGWASpi.T_STUDIES + " WHERE id=" + studyId + "  WITH RR");
		} catch (Exception ex) {
			log.error(null, ex);
		}

		return rs;
	}

	private static List<Integer> getStudyMatricesId(int studyId) throws IOException {
		List<Integer> studyMatricesList = new ArrayList<Integer>();
		List<Map<String, Object>> rs = null;
		String dbName = cDBGWASpi.DB_DATACENTER;
		DbManager studyDbManager = ServiceLocator.getDbManager(dbName);
		try {
			rs = studyDbManager.executeSelectStatement("SELECT * FROM " + cDBGWASpi.SCH_MATRICES + "." + cDBMatrix.T_MATRICES + " WHERE " + cDBMatrix.f_STUDYID + "=" + studyId + "  WITH RR");
		} catch (Exception ex) {
			log.error(null, ex);
		}

		int rowcount = rs.size();
		if (rowcount > 0) {
			for (int i = rowcount - 1; i >= 0; i--) // loop through rows of result set
			{
				int currentMatrixId = (Integer) rs.get(i).get(cDBMatrix.f_ID);
				studyMatricesList.add(currentMatrixId);
			}
		}

		return studyMatricesList;
	}
}
