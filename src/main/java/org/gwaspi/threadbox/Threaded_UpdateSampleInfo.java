package org.gwaspi.threadbox;

import org.gwaspi.constants.cDBGWASpi;
import org.gwaspi.database.DbManager;
import org.gwaspi.global.ServiceLocator;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.gwaspi.model.Study;
import org.gwaspi.model.StudyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gwaspi.samples.SamplesParserManager;

/**
 *
 * @author Fernando Muñiz Fernandez
 * IBE, Institute of Evolutionary Biology (UPF-CSIC)
 * CEXS-UPF-PRBB
 */
public class Threaded_UpdateSampleInfo extends CommonRunnable {

	private File sampleInfoFile;
	private int poolId;

	public Threaded_UpdateSampleInfo(
			int poolId,
			File sampleInfoFile)
	{
		super(
				"Update Sample Info",
				"Sample Info Update",
				"Update Sample Info on Study ID: " + poolId,
				"Sample Info Update");

		this.poolId = poolId;
		this.sampleInfoFile = sampleInfoFile;
	}

	protected Logger createLog() {
		return LoggerFactory.getLogger(Threaded_UpdateSampleInfo.class);
	}

	protected void runInternal(SwingWorkerItem thisSwi) throws Exception {

		Map<String, Object> sampleInfoMap = SamplesParserManager.scanGwaspiSampleInfo(sampleInfoFile.getPath());
		List<String> updatedSamplesAL = org.gwaspi.samples.InsertSampleInfo.processData(poolId, sampleInfoMap);

		// DO NOT! Write new reports of SAMPLE QA
//		OperationsList opList = new OperationsList(matrix.getMatrixId());
//		List<Operation> opAL = opList.operationsListAL;
//		int qaOpId = Integer.MIN_VALUE;
//		for (int i = 0; i < opAL.size(); i++) {
//			if (opAL.get(i).getOperationType().equals(OPType.SAMPLE_QA)) {
//				qaOpId = opAL.get(i).getOperationId();
//			}
//		}
//		if (qaOpId != Integer.MIN_VALUE) {
//			org.gwaspi.reports.OutputQASamples.writeReportsForQASamplesData(qaOpId, false);
//		}

		Study study = StudyList.getStudy(poolId);

		StringBuilder oldDesc = new StringBuilder(study.getDescription());
		oldDesc.append("\n* Sample Info updated from: ");
		oldDesc.append(sampleInfoFile.getPath());
		oldDesc.append(" (");
		oldDesc.append(org.gwaspi.global.Utils.getShortDateTimeAsString());
		oldDesc.append(") *");
		saveDescription(oldDesc.toString(), poolId);
	}

	private void saveDescription(String description, int studyId) {
		try {
			org.gwaspi.global.Utils.logBlockInStudyDesc(description, studyId);

			DbManager db = ServiceLocator.getDbManager(cDBGWASpi.DB_DATACENTER);
			db.updateTable(cDBGWASpi.SCH_APP,
					cDBGWASpi.T_STUDIES,
					new String[]{cDBGWASpi.f_STUDY_DESCRIPTION},
					new Object[]{description},
					new String[]{cDBGWASpi.f_ID},
					new Object[]{studyId});
		} catch (IOException ex) {
			getLog().error(null, ex);
		}
	}
}
