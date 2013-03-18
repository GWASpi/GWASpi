package org.gwaspi.netCDF.operations;

import java.util.Map;
import org.gwaspi.constants.cNetCDF;
import org.gwaspi.constants.cNetCDF.Defaults.OPType;
import org.gwaspi.model.MarkerKey;
import org.gwaspi.model.Operation;
import org.gwaspi.statistics.Associations;
import org.gwaspi.statistics.Pvalue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ucar.nc2.NetcdfFileWriteable;

/**
 *
 * @author Fernando Muñiz Fernandez
 * IBE, Institute of Evolutionary Biology (UPF-CSIC)
 * CEXS-UPF-PRBB
 */
public class OP_AssociationTests extends AbstractTestMatrixOperation {

	private final Logger log
			= LoggerFactory.getLogger(OP_AssociationTests.class);

	/**
	 * Whether we are to perform allelic or genotypic association tests.
	 */
	private final boolean allelic;

	public OP_AssociationTests(
			int rdMatrixId,
			Operation markerCensusOP,
			Operation hwOP,
			double hwThreshold,
			boolean allelic)
	{
		super(
			rdMatrixId,
			markerCensusOP,
			hwOP,
			hwThreshold,
			(allelic ? "Allelic" : "Genotypic") + " Association Test",
			allelic ? OPType.ALLELICTEST : OPType.GENOTYPICTEST);

		this.allelic = allelic;
	}

	/**
	 * Performs the Allelic or Genotypic Association Tests.
	 * @param wrNcFile
	 * @param wrCaseMarkerIdSetMap
	 * @param wrCtrlMarkerSet
	 */
	@Override
	protected void performTest(NetcdfFileWriteable wrNcFile, Map<MarkerKey, Object> wrCaseMarkerIdSetMap, Map<MarkerKey, Object> wrCtrlMarkerSet) {
		// Iterate through markerset
		int markerNb = 0;
		for (Map.Entry<MarkerKey, Object> entry : wrCaseMarkerIdSetMap.entrySet()) {
			MarkerKey markerKey = entry.getKey();

			int[] caseCntgTable = (int[]) entry.getValue();
			int[] ctrlCntgTable = (int[]) wrCtrlMarkerSet.get(markerKey);

			// INIT VALUES
			int caseAA = caseCntgTable[0];
			int caseAa = caseCntgTable[1];
			int caseaa = caseCntgTable[2];
			int caseTot = caseAA + caseaa + caseAa;

			int ctrlAA = ctrlCntgTable[0];
			int ctrlAa = ctrlCntgTable[1];
			int ctrlaa = ctrlCntgTable[2];
			int ctrlTot = ctrlAA + ctrlaa + ctrlAa;

			Double[] store;
			if (allelic) {
				// allelic test
				int sampleNb = caseTot + ctrlTot;

				double allelicT = Associations.calculateAllelicAssociationChiSquare(
						sampleNb,
						caseAA,
						caseAa,
						caseaa,
						caseTot,
						ctrlAA,
						ctrlAa,
						ctrlaa,
						ctrlTot);
				double allelicPval = Pvalue.calculatePvalueFromChiSqr(allelicT, 1);

				double allelicOR = Associations.calculateAllelicAssociationOR(
						caseAA,
						caseAa,
						caseaa,
						ctrlAA,
						ctrlAa,
						ctrlaa);

				store = new Double[3];
				store[0] = allelicT;
				store[1] = allelicPval;
				store[2] = allelicOR;
			} else {
				// genotypic test
				double gntypT = Associations.calculateGenotypicAssociationChiSquare(
						caseAA,
						caseAa,
						caseaa,
						caseTot,
						ctrlAA,
						ctrlAa,
						ctrlaa,
						ctrlTot);
				double gntypPval = Pvalue.calculatePvalueFromChiSqr(gntypT, 2);
				double[] gntypOR = Associations.calculateGenotypicAssociationOR(
						caseAA,
						caseAa,
						caseaa,
						ctrlAA,
						ctrlAa,
						ctrlaa);

				store = new Double[4];
				store[0] = gntypT;
				store[1] = gntypPval;
				store[2] = gntypOR[0];
				store[3] = gntypOR[1];
			}
			wrCaseMarkerIdSetMap.put(markerKey, store); // Re-use Map to store P-value and stuff

			markerNb++;
			if (markerNb % 100000 == 0) {
				log.info("Processed {} markers", markerNb);
			}
		}

		//<editor-fold defaultstate="expanded" desc="ALLELICTEST DATA WRITER">
		int[] boxes;
		String variableName;
		if (allelic) {
			boxes = new int[] {0, 1, 2};
			variableName = cNetCDF.Association.VAR_OP_MARKERS_ASAllelicAssociationTPOR;
		} else {
			boxes = new int[] {0, 1, 2, 3};
			variableName = cNetCDF.Association.VAR_OP_MARKERS_ASGenotypicAssociationTP2OR;
		}
		Utils.saveDoubleMapD2ToWrMatrix(wrNcFile, wrCaseMarkerIdSetMap, boxes, variableName);
		//</editor-fold>
	}
}
