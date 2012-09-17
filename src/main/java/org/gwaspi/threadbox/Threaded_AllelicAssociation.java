package org.gwaspi.threadbox;

import org.gwaspi.constants.cNetCDF;
import org.gwaspi.model.GWASpiExplorerNodes;
import org.gwaspi.model.OperationsList;
import org.gwaspi.netCDF.operations.GWASinOneGOParams;
import org.gwaspi.netCDF.operations.OperationMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gwaspi.reports.OutputAllelicAssociation;

/**
 *
 * @author Fernando Muñiz Fernandez
 * IBE, Institute of Evolutionary Biology (UPF-CSIC)
 * CEXS-UPF-PRBB
 */
public class Threaded_AllelicAssociation extends CommonRunnable {

	private int matrixId;
	private int censusOpId;
	private int hwOpId;
	private GWASinOneGOParams gwasParams;

	public Threaded_AllelicAssociation(
			String threadName,
			String timeStamp,
			int matrixId,
			int censusOpId,
			int hwOpId,
			GWASinOneGOParams gwasParams)
	{
		super(threadName, timeStamp, "Allelic Association Study");

		this.matrixId = matrixId;
		this.censusOpId = censusOpId;
		this.hwOpId = hwOpId;
		this.gwasParams = gwasParams;

		startInternal("Allelic Association Test");
	}

	protected Logger createLog() {
		return LoggerFactory.getLogger(Threaded_AllelicAssociation.class);
	}

	protected void runInternal(SwingWorkerItem thisSwi) throws Exception {

		OperationsList opList = new OperationsList(matrixId);
		int markersQAOpId = opList.getIdOfLastOperationTypeOccurance(cNetCDF.Defaults.OPType.MARKER_QA);

		if (!gwasParams.isDiscardMarkerByMisRat()) {
			gwasParams.setDiscardMarkerMisRatVal(1);
		}
		if (!gwasParams.isDiscardMarkerByHetzyRat()) {
			gwasParams.setDiscardMarkerHetzyRatVal(1);
		}
		if (!gwasParams.isDiscardSampleByMisRat()) {
			gwasParams.setDiscardSampleMisRatVal(1);
		}
		if (!gwasParams.isDiscardSampleByHetzyRat()) {
			gwasParams.setDiscardSampleHetzyRatVal(1);
		}

		// ALLELIC ALLELICTEST (needs newMatrixId, censusOpId, pickedMarkerSet, pickedSampleSet)

		OperationMetadata markerQAMetadata = new OperationMetadata(markersQAOpId);

		if (gwasParams.isDiscardMarkerHWCalc()) {
			gwasParams.setDiscardMarkerHWTreshold((double) 0.05 / markerQAMetadata.getOpSetSize());
		}

		if (thisSwi.getQueueState().equals(QueueStates.PROCESSING)) {
			int assocOpId = org.gwaspi.netCDF.operations.OperationManager.performCleanAllelicTests(matrixId,
					censusOpId,
					hwOpId, gwasParams.getDiscardMarkerHWTreshold());
			GWASpiExplorerNodes.insertSubOperationUnderOperationNode(censusOpId, assocOpId);

			// Make Reports (needs newMatrixId, QAopId, AssocOpId)
			if (assocOpId != Integer.MIN_VALUE) {
				OutputAllelicAssociation.writeReportsForAssociationData(assocOpId);
				GWASpiExplorerNodes.insertReportsUnderOperationNode(assocOpId);
			}
		}
	}
}
