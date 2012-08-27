package org.gwaspi.threadbox;

import org.gwaspi.constants.cNetCDF;
import org.gwaspi.global.Text;
import java.io.File;
import java.util.Map;
import java.util.Set;
import org.gwaspi.model.GWASpiExplorerNodes;
import org.gwaspi.model.OperationsList;
import org.gwaspi.netCDF.operations.GWASinOneGOParams;
import org.gwaspi.netCDF.operations.OperationManager;
import org.gwaspi.netCDF.operations.OperationMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gwaspi.reports.OutputAllelicAssociation;
import org.gwaspi.reports.OutputGenotypicAssociation;
import org.gwaspi.reports.OutputTrendTest;
import org.gwaspi.samples.SamplesParser;

/**
 *
 * @author Fernando Muñiz Fernandez
 * IBE, Institute of Evolutionary Biology (UPF-CSIC)
 * CEXS-UPF-PRBB
 */
public class Threaded_GWAS extends CommonRunnable {

	private int matrixId;
	private File phenotypeFile;
	private GWASinOneGOParams gwasParams;

	public Threaded_GWAS(String threadName,
			String timeStamp,
			int matrixId,
			File phenotypeFile,
			GWASinOneGOParams gwasParams)
	{
		super(threadName, timeStamp, "GWAS");

		this.matrixId = matrixId;
		this.phenotypeFile = phenotypeFile;
		this.gwasParams = gwasParams;

		startInternal(getTaskDescription());
	}

	protected Logger createLog() {
		return LoggerFactory.getLogger(Threaded_GWAS.class);
	}

	protected void runInternal(SwingWorkerItem thisSwi) throws Exception {

		OperationsList opList = new OperationsList(matrixId);
		int sampleQAOpId = opList.getIdOfLastOperationTypeOccurance(org.gwaspi.constants.cNetCDF.Defaults.OPType.SAMPLE_QA);
		int markersQAOpId = opList.getIdOfLastOperationTypeOccurance(org.gwaspi.constants.cNetCDF.Defaults.OPType.MARKER_QA);

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

		//<editor-fold defaultstate="collapsed" desc="PRE-GWAS PROCESS">
		// GENOTYPE FREQ.
		int censusOpId = Integer.MIN_VALUE;
		if (thisSwi.getQueueState().equals(org.gwaspi.threadbox.QueueStates.PROCESSING)) {

			if (phenotypeFile != null && phenotypeFile.exists() && phenotypeFile.isFile()) { //BY EXTERNAL PHENOTYPE FILE

				Set<String> affectionStates = SamplesParser.scanSampleInfoAffectionStates(phenotypeFile.getPath()); //use Sample Info file affection state

				if (affectionStates.contains("1") && affectionStates.contains("2")) {
					System.out.println("Updating Sample Info in DB");
					Map<String, Object> sampleInfoLHM = SamplesParser.scanGwaspiSampleInfo(phenotypeFile.getPath());
					org.gwaspi.samples.InsertSampleInfo.processData(matrixId, sampleInfoLHM);

					censusOpId = OperationManager.censusCleanMatrixMarkersByPhenotypeFile(matrixId,
							sampleQAOpId,
							markersQAOpId, gwasParams.getDiscardMarkerMisRatVal(), gwasParams.isDiscardGTMismatches(), gwasParams.getDiscardSampleMisRatVal(), gwasParams.getDiscardSampleHetzyRatVal(),
							new StringBuilder().append(gwasParams.getFriendlyName()).append(" using ").append(phenotypeFile.getName()).toString(),
							phenotypeFile);

					org.gwaspi.global.Utils.sysoutCompleted("Genotype Frequency Count");
				} else {
					System.out.println(Text.Operation.warnAffectionMissing);
				}
			} else { // BY DB AFFECTION
				Set<Object> affectionStates = SamplesParser.getDBAffectionStates(matrixId); //use Sample Info file affection state
				if (affectionStates.contains("1") && affectionStates.contains("2")) {
					censusOpId = OperationManager.censusCleanMatrixMarkers(matrixId,
							sampleQAOpId,
							markersQAOpId, gwasParams.getDiscardMarkerMisRatVal(), gwasParams.isDiscardGTMismatches(), gwasParams.getDiscardSampleMisRatVal(), gwasParams.getDiscardSampleHetzyRatVal(),
							new StringBuilder().append(gwasParams.getFriendlyName()).append(" using ").append(cNetCDF.Defaults.DEFAULT_AFFECTION).toString());

					org.gwaspi.global.Utils.sysoutCompleted("Genotype Frequency Count");
				} else {
					System.out.println(Text.Operation.warnAffectionMissing);
				}
			}

			GWASpiExplorerNodes.insertOperationUnderMatrixNode(matrixId, censusOpId);
		}

		// HW ON GENOTYPE FREQ.
		int hwOpId = Integer.MIN_VALUE;
		if (thisSwi.getQueueState().equals(org.gwaspi.threadbox.QueueStates.PROCESSING)
				&& censusOpId != Integer.MIN_VALUE) {
			hwOpId = org.gwaspi.netCDF.operations.OperationManager.performHardyWeinberg(censusOpId, cNetCDF.Defaults.DEFAULT_AFFECTION);
			GWASpiExplorerNodes.insertSubOperationUnderOperationNode(censusOpId, hwOpId);
		}
		//</editor-fold>

		//<editor-fold defaultstate="collapsed" desc="GWAS TESTS & REPORTS">

		// ALLELIC TEST (needs newMatrixId, censusOpId, pickedMarkerSet, pickedSampleSet)
		if (gwasParams.isPerformAllelicTests()
				&& thisSwi.getQueueState().equals(org.gwaspi.threadbox.QueueStates.PROCESSING)
				&& censusOpId != Integer.MIN_VALUE
				&& hwOpId != Integer.MIN_VALUE) {

			OperationMetadata markerQAMetadata = new OperationMetadata(markersQAOpId);
			int qaMarkerSetSize = markerQAMetadata.getOpSetSize();

			if (	gwasParams.isDiscardMarkerHWCalc()) {
				gwasParams.setDiscardMarkerHWTreshold((double) 0.05 / qaMarkerSetSize);
			}

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

		// GENOTYPIC TEST (needs newMatrixId, censusOpId, pickedMarkerSet, pickedSampleSet)
		if (gwasParams.isPerformGenotypicTests()
				&& thisSwi.getQueueState().equals(org.gwaspi.threadbox.QueueStates.PROCESSING)
				&& censusOpId != Integer.MIN_VALUE
				&& hwOpId != Integer.MIN_VALUE) {

			OperationMetadata markerQAMetadata = new OperationMetadata(markersQAOpId);
			int qaMarkerSetSize = markerQAMetadata.getOpSetSize();

			if (	gwasParams.isDiscardMarkerHWCalc()) {
				gwasParams.setDiscardMarkerHWTreshold((double) 0.05 / qaMarkerSetSize);
			}

			int assocOpId = org.gwaspi.netCDF.operations.OperationManager.performCleanGenotypicTests(matrixId,
					censusOpId,
					hwOpId, gwasParams.getDiscardMarkerHWTreshold());
			GWASpiExplorerNodes.insertSubOperationUnderOperationNode(censusOpId, assocOpId);

			// Make Reports (needs newMatrixId, QAopId, AssocOpId)
			if (assocOpId != Integer.MIN_VALUE) {
				OutputGenotypicAssociation.writeReportsForAssociationData(assocOpId);
				GWASpiExplorerNodes.insertReportsUnderOperationNode(assocOpId);
			}
		}

		// TREND TESTS (needs newMatrixId, censusOpId, pickedMarkerSet, pickedSampleSet)
		if (gwasParams.isPerformTrendTests()
				&& thisSwi.getQueueState().equals(org.gwaspi.threadbox.QueueStates.PROCESSING)
				&& censusOpId != Integer.MIN_VALUE
				&& hwOpId != Integer.MIN_VALUE) {

			OperationMetadata markerQAMetadata = new OperationMetadata(markersQAOpId);
			int qaMarkerSetSize = markerQAMetadata.getOpSetSize();

			if (	gwasParams.isDiscardMarkerHWCalc()) {
				gwasParams.setDiscardMarkerHWTreshold((double) 0.05 / qaMarkerSetSize);
			}

			int trendOpId = org.gwaspi.netCDF.operations.OperationManager.performCleanTrendTests(matrixId,
					censusOpId,
					hwOpId, gwasParams.getDiscardMarkerHWTreshold());
			GWASpiExplorerNodes.insertSubOperationUnderOperationNode(censusOpId, trendOpId);

			// Make Reports (needs newMatrixId, QAopId, AssocOpId)
			if (trendOpId != Integer.MIN_VALUE) {
				OutputTrendTest.writeReportsForTrendTestData(trendOpId);
				GWASpiExplorerNodes.insertReportsUnderOperationNode(trendOpId);
			}
		}
		//</editor-fold>
	}
}
