package org.gwaspi.threadbox;

import org.gwaspi.model.GWASpiExplorerNodes;
import org.gwaspi.netCDF.operations.MatrixMergeMarkers_opt;
import org.gwaspi.netCDF.operations.OP_QAMarkers_opt;
import org.gwaspi.netCDF.operations.OP_QASamples_opt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Fernando Muñiz Fernandez
 * IBE, Institute of Evolutionary Biology (UPF-CSIC)
 * CEXS-UPF-PRBB
 */
public class Threaded_MergeMatricesAddMarkers extends CommonRunnable {

	private int studyId;
	private int parentMatrixId1;
	private int parentMatrixId2;
	private String newMatrixName;
	private String description;

	public Threaded_MergeMatricesAddMarkers(
			String timeStamp,
			int studyId,
			int parentMatrixId1,
			int parentMatrixId2,
			String newMatrixName,
			String description)
	{
		super("Merge Matrices", timeStamp, "Merging Data", "Merging Matrices");

		this.studyId = studyId;
		this.parentMatrixId1 = parentMatrixId1;
		this.parentMatrixId2 = parentMatrixId2;
		this.newMatrixName = newMatrixName;
		this.description = description;
	}

	protected Logger createLog() {
		return LoggerFactory.getLogger(Threaded_MergeMatricesAddMarkers.class);
	}

	protected void runInternal(SwingWorkerItem thisSwi) throws Exception {

		if (thisSwi.getQueueState().equals(QueueState.PROCESSING)) {
			MatrixMergeMarkers_opt jointedMatrix = new MatrixMergeMarkers_opt(studyId,
					parentMatrixId1,
					parentMatrixId2,
					newMatrixName,
					description);

			int resultMatrixId = jointedMatrix.mingleMarkersKeepSamplesConstant();
			GWASpiExplorerNodes.insertMatrixNode(studyId, resultMatrixId);

			if (!thisSwi.getQueueState().equals(QueueState.PROCESSING)) {
				return;
			}
			int sampleQAOpId = new OP_QASamples_opt(resultMatrixId).processMatrix();
			GWASpiExplorerNodes.insertOperationUnderMatrixNode(resultMatrixId, sampleQAOpId);
			org.gwaspi.reports.OutputQASamples.writeReportsForQASamplesData(sampleQAOpId, true);
			GWASpiExplorerNodes.insertReportsUnderOperationNode(sampleQAOpId);

			if (!thisSwi.getQueueState().equals(QueueState.PROCESSING)) {
				return;
			}
			int markersQAOpId = new OP_QAMarkers_opt(resultMatrixId).processMatrix();
			GWASpiExplorerNodes.insertOperationUnderMatrixNode(resultMatrixId, markersQAOpId);
			org.gwaspi.reports.OutputQAMarkers.writeReportsForQAMarkersData(markersQAOpId);
			GWASpiExplorerNodes.insertReportsUnderOperationNode(markersQAOpId);
			MultiOperations.printCompleted("Matrix Quality Control");
		}
	}
}
