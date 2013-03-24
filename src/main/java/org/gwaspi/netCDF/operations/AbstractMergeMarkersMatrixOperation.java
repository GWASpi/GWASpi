package org.gwaspi.netCDF.operations;

import java.io.IOException;
import java.util.Map;
import org.gwaspi.constants.cImport.ImportFormat;
import org.gwaspi.constants.cNetCDF;
import org.gwaspi.constants.cNetCDF.Defaults.GenotypeEncoding;
import org.gwaspi.global.Text;
import org.gwaspi.model.MarkerKey;
import org.gwaspi.model.MatricesList;
import org.gwaspi.model.SampleKey;
import org.gwaspi.netCDF.matrices.MatrixFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ucar.ma2.ArrayChar;
import ucar.ma2.ArrayInt;
import ucar.ma2.Index;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFileWriteable;

/**
 *
 * @author Fernando Muñiz Fernandez
 * IBE, Institute of Evolutionary Biology (UPF-CSIC)
 * CEXS-UPF-PRBB
 */
public abstract class AbstractMergeMarkersMatrixOperation extends AbstractMergeMatrixOperation {

	private final Logger log = LoggerFactory.getLogger(AbstractMergeMarkersMatrixOperation.class);

	public AbstractMergeMarkersMatrixOperation(
			int studyId,
			int rdMatrix1Id,
			int rdMatrix2Id,
			String wrMatrixFriendlyName,
			String wrMatrixDescription)
			throws IOException, InvalidRangeException
	{
		super(
				studyId,
				rdMatrix1Id,
				rdMatrix2Id,
				wrMatrixFriendlyName,
				wrMatrixDescription);
	}

	/**
	 * Mingles markers and keeps samples constant.
	 */
	protected int mergeMatrices(
			Map<SampleKey, Object> rdSampleSetMap1,
			Map<SampleKey, Object> rdSampleSetMap2,
			Map<SampleKey, Object> wrSampleSetMap,
			Map<SampleKey, Object> theSamples,
			final int numSamples,
			final String humanReadableMethodName,
			final String methodDescription)
			throws IOException, InvalidRangeException
	{
		int resultMatrixId = Integer.MIN_VALUE;

		NetcdfFile rdNcFile1 = NetcdfFile.open(rdMatrix1Metadata.getPathToMatrix());
		NetcdfFile rdNcFile2 = NetcdfFile.open(rdMatrix2Metadata.getPathToMatrix());

		Map<MarkerKey, Object> wrComboSortedMarkerSetMap = mingleAndSortMarkerSet();

		// RETRIEVE CHROMOSOMES INFO
		Map<MarkerKey, Object> chrInfo = org.gwaspi.netCDF.matrices.Utils.aggregateChromosomeInfo(wrComboSortedMarkerSetMap, 0, 1);

		//<editor-fold defaultstate="expanded" desc="CREATE MATRIX">
		try {
			// CREATE netCDF-3 FILE
			boolean hasDictionary = false;
			if (rdMatrix1Metadata.getHasDictionray() == rdMatrix2Metadata.getHasDictionray()) {
				hasDictionary = rdMatrix1Metadata.getHasDictionray();
			}
			GenotypeEncoding gtEncoding = GenotypeEncoding.UNKNOWN;
			if (rdMatrix1Metadata.getGenotypeEncoding().equals(rdMatrix2Metadata.getGenotypeEncoding())) {
				gtEncoding = rdMatrix1Metadata.getGenotypeEncoding();
			}
			ImportFormat technology = ImportFormat.UNKNOWN;
			if (rdMatrix1Metadata.getTechnology().equals(rdMatrix2Metadata.getTechnology())) {
				technology = rdMatrix1Metadata.getTechnology();
			}

			StringBuilder descSB = new StringBuilder(Text.Matrix.descriptionHeader1);
			descSB.append(org.gwaspi.global.Utils.getShortDateTimeAsString());
			descSB.append("\n");
			descSB.append("Markers: ").append(wrComboSortedMarkerSetMap.size()).append(", Samples: ").append(numSamples);
			descSB.append("\n");
			descSB.append(Text.Trafo.mergedFrom);
			descSB.append("\nMX-");
			descSB.append(rdMatrix1Metadata.getMatrixId());
			descSB.append(" - ");
			descSB.append(rdMatrix1Metadata.getMatrixFriendlyName());
			descSB.append("\nMX-");
			descSB.append(rdMatrix2Metadata.getMatrixId());
			descSB.append(" - ");
			descSB.append(rdMatrix2Metadata.getMatrixFriendlyName());
			descSB.append("\n\n");
			descSB.append("Merge Method - ");
			descSB.append(humanReadableMethodName);
			descSB.append(":\n");
			descSB.append(methodDescription);

			MatrixFactory wrMatrixHandler = new MatrixFactory(
					studyId,
					technology, // technology
					wrMatrixFriendlyName,
					wrMatrixDescription + "\n\n" + descSB.toString(), // description
					gtEncoding, // GT encoding
					rdMatrix1Metadata.getStrand(),
					hasDictionary, // has dictionary?
					numSamples,
					wrComboSortedMarkerSetMap.size(), // Use comboed wrSortedMingledMarkerMap as MarkerSet
					chrInfo.size(),
					rdMatrix1Id, // Parent matrixId 1
					rdMatrix2Id); // Parent matrixId 2

			NetcdfFileWriteable wrNcFile = wrMatrixHandler.getNetCDFHandler();
			try {
				wrNcFile.create();
			} catch (IOException ex) {
				log.error("Failed creating file " + wrNcFile.getLocation(), ex);
			}
			//log.trace("Done creating netCDF handle in MatrixSampleJoin: " + org.gwaspi.global.Utils.getMediumDateTimeAsString());

			//<editor-fold defaultstate="expanded" desc="METADATA WRITER">
			// SAMPLESET
			ArrayChar.D2 samplesD2 = Utils.writeMapKeysToD2ArrayChar(theSamples, cNetCDF.Strides.STRIDE_SAMPLE_NAME);

			int[] sampleOrig = new int[]{0, 0};
			try {
				wrNcFile.write(cNetCDF.Variables.VAR_SAMPLESET, sampleOrig, samplesD2);
			} catch (IOException ex) {
				log.error("Failed writing file", ex);
			} catch (InvalidRangeException ex) {
				log.error(null, ex);
			}
			log.info("Done writing SampleSet to matrix");

			// MARKERSET MARKERID
			ArrayChar.D2 markersD2 = Utils.writeMapKeysToD2ArrayChar(wrComboSortedMarkerSetMap, cNetCDF.Strides.STRIDE_MARKER_NAME);
			int[] markersOrig = new int[] {0, 0};
			try {
				wrNcFile.write(cNetCDF.Variables.VAR_MARKERSET, markersOrig, markersD2);
			} catch (IOException ex) {
				log.error("Failed writing file", ex);
			} catch (InvalidRangeException ex) {
				log.error(null, ex);
			}

			// WRITE CHROMOSOME METADATA FROM ANNOTATION FILE
			markersD2 = org.gwaspi.netCDF.operations.Utils.writeMapValueItemToD2ArrayChar(wrComboSortedMarkerSetMap, 0, cNetCDF.Strides.STRIDE_CHR);

			try {
				wrNcFile.write(cNetCDF.Variables.VAR_MARKERS_CHR, markersOrig, markersD2);
			} catch (IOException ex) {
				log.error("Failed writing file", ex);
			} catch (InvalidRangeException ex) {
				log.error(null, ex);
			}
			log.info("Done writing chromosomes to matrix");

			// Set of chromosomes found in matrix along with number of markersinfo
			org.gwaspi.netCDF.operations.Utils.saveCharMapKeyToWrMatrix(wrNcFile, chrInfo, cNetCDF.Variables.VAR_CHR_IN_MATRIX, 8);
			// Number of marker per chromosome & max pos for each chromosome
			int[] columns = new int[] {0, 1, 2, 3};
			org.gwaspi.netCDF.operations.Utils.saveIntMapD2ToWrMatrix(wrNcFile, chrInfo, columns, cNetCDF.Variables.VAR_CHR_INFO);

			// WRITE POSITION METADATA FROM ANNOTATION FILE
			ArrayInt.D1 markersPosD1 = org.gwaspi.netCDF.operations.Utils.writeMapValueItemToD1ArrayInt(wrComboSortedMarkerSetMap, 1);
			int[] posOrig = new int[1];
			try {
				wrNcFile.write(cNetCDF.Variables.VAR_MARKERS_POS, posOrig, markersPosD1);
			} catch (IOException ex) {
				log.error("Failed writing file", ex);
			} catch (InvalidRangeException ex) {
				log.error(null, ex);
			}
			log.info("Done writing positions to matrix");

			//<editor-fold defaultstate="expanded" desc="GATHER METADATA FROM BOTH MATRICES">
			rdMarkerSet1.initFullMarkerIdSetMap();
			rdMarkerSet2.initFullMarkerIdSetMap();

			//<editor-fold defaultstate="expanded" desc="MARKERSET RSID">
			rdMarkerSet1.fillInitMapWithVariable(cNetCDF.Variables.VAR_MARKERS_RSID);
			wrComboSortedMarkerSetMap.putAll(rdMarkerSet1.getMarkerIdSetMap());
			rdMarkerSet2.fillInitMapWithVariable(cNetCDF.Variables.VAR_MARKERS_RSID);
			wrComboSortedMarkerSetMap.putAll(rdMarkerSet2.getMarkerIdSetMap());

			Utils.saveCharMapValueToWrMatrix(wrNcFile, wrComboSortedMarkerSetMap, cNetCDF.Variables.VAR_MARKERS_RSID, cNetCDF.Strides.STRIDE_MARKER_NAME);
			//</editor-fold>

			//<editor-fold defaultstate="expanded" desc="MARKERSET DICTIONARY ALLELES">
			Attribute hasDictionary1 = rdNcFile1.findGlobalAttribute(cNetCDF.Attributes.GLOB_HAS_DICTIONARY);
			Attribute hasDictionary2 = rdNcFile2.findGlobalAttribute(cNetCDF.Attributes.GLOB_HAS_DICTIONARY);
			if ((Integer) hasDictionary1.getNumericValue() == 1
					&& (Integer) hasDictionary2.getNumericValue() == 1)
			{
				rdMarkerSet1.fillInitMapWithVariable(cNetCDF.Variables.VAR_MARKERS_BASES_DICT);
				wrComboSortedMarkerSetMap.putAll(rdMarkerSet1.getMarkerIdSetMap());
				rdMarkerSet2.fillInitMapWithVariable(cNetCDF.Variables.VAR_MARKERS_BASES_DICT);
				wrComboSortedMarkerSetMap.putAll(rdMarkerSet2.getMarkerIdSetMap());

				Utils.saveCharMapValueToWrMatrix(wrNcFile, wrComboSortedMarkerSetMap, cNetCDF.Variables.VAR_MARKERS_BASES_DICT, cNetCDF.Strides.STRIDE_GT);
			}
			//</editor-fold>

			//<editor-fold defaultstate="expanded" desc="GENOTYPE STRAND">
			rdMarkerSet1.fillInitMapWithVariable(cNetCDF.Variables.VAR_GT_STRAND);
			wrComboSortedMarkerSetMap.putAll(rdMarkerSet1.getMarkerIdSetMap());
			rdMarkerSet2.fillInitMapWithVariable(cNetCDF.Variables.VAR_GT_STRAND);
			wrComboSortedMarkerSetMap.putAll(rdMarkerSet2.getMarkerIdSetMap());

			Utils.saveCharMapValueToWrMatrix(wrNcFile, wrComboSortedMarkerSetMap, cNetCDF.Variables.VAR_GT_STRAND, 3);
			//</editor-fold>
			//</editor-fold>
			//</editor-fold>

			writeGenotypes(wrNcFile, wrSampleSetMap, wrComboSortedMarkerSetMap, rdSampleSetMap1, rdSampleSetMap2);

			// CLOSE THE FILE AND BY THIS, MAKE IT READ-ONLY
			try {
				// GENOTYPE ENCODING
				ArrayChar.D2 guessedGTCodeAC = new ArrayChar.D2(1, 8);
				Index index = guessedGTCodeAC.getIndex();
				guessedGTCodeAC.setString(index.set(0, 0), rdMatrix1Metadata.getGenotypeEncoding().toString());
				int[] origin = new int[]{0, 0};
				wrNcFile.write(cNetCDF.Variables.GLOB_GTENCODING, origin, guessedGTCodeAC);

				descSB.append("\nGenotype encoding: ");
				descSB.append(rdMatrix1Metadata.getGenotypeEncoding());
				MatricesList.saveMatrixDescription(
						resultMatrixId,
						descSB.toString());

				resultMatrixId = wrMatrixHandler.getResultMatrixId();

				wrNcFile.close();
				rdNcFile1.close();
				rdNcFile2.close();

				// CHECK FOR MISMATCHES
				if (rdMatrix1Metadata.getGenotypeEncoding().equals(GenotypeEncoding.ACGT0)
						|| rdMatrix1Metadata.getGenotypeEncoding().equals(GenotypeEncoding.O1234))
				{
					double[] mismatchState = checkForMismatches(wrMatrixHandler.getResultMatrixId()); // mismatchCount, mismatchRatio
					if (mismatchState[1] > 0.01) {
						log.warn("");
						log.warn("Mismatch ratio is bigger than 1% ({}%)!", (mismatchState[1] * 100));
						log.warn("There might be an issue with strand positioning of your genotypes!");
						log.warn("");
						//resultMatrixId = new int[] {wrMatrixHandler.getResultMatrixId(),-4};  // The threshold of acceptable mismatching genotypes has been crossed
					}
				}
			} catch (IOException ex) {
				log.error("Failed creating file " + wrNcFile.getLocation(), ex);
			}

			org.gwaspi.global.Utils.sysoutCompleted("extraction to new Matrix");
		} catch (InvalidRangeException ex) {
			log.error(null, ex);
		} catch (IOException ex) {
			log.error(null, ex);
		}
		//</editor-fold>

		return resultMatrixId;
	}

	protected abstract void writeGenotypes(
			NetcdfFileWriteable wrNcFile,
			Map<SampleKey, Object> wrSampleSetMap,
			Map<MarkerKey, Object> wrComboSortedMarkerSetMap,
			Map<SampleKey, Object> rdSampleSetMap1,
			Map<SampleKey, Object> rdSampleSetMap2)
			throws InvalidRangeException, IOException;
}