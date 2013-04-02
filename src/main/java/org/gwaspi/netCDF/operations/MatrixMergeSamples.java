package org.gwaspi.netCDF.operations;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.gwaspi.constants.cImport.ImportFormat;
import org.gwaspi.constants.cNetCDF;
import org.gwaspi.constants.cNetCDF.Defaults.GenotypeEncoding;
import org.gwaspi.global.Text;
import org.gwaspi.model.MarkerKey;
import org.gwaspi.model.MatricesList;
import org.gwaspi.model.MatrixMetadata;
import org.gwaspi.model.SampleKey;
import org.gwaspi.netCDF.markers.MarkerSet;
import org.gwaspi.netCDF.matrices.MatrixFactory;
import org.gwaspi.samples.SampleSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ucar.ma2.ArrayChar;
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
public class MatrixMergeSamples {

	private final Logger log = LoggerFactory.getLogger(MatrixMergeSamples.class);

	private int studyId;
	private int rdMatrix1Id;
	private int rdMatrix2Id;
	private int wrMatrixId;
	private String wrMatrixFriendlyName;
	private String wrMatrixDescription;
	private MatrixMetadata rdMatrix1Metadata;
	private MatrixMetadata rdMatrix2Metadata;
	private MatrixMetadata wrMatrixMetadata;
	private MarkerSet rdwrMarkerSet1;
	private MarkerSet rdMarkerSet2;
	private MarkerSet wrMarkerSet;
	private SampleSet rdSampleSet1;
	private SampleSet rdSampleSet2;
	private SampleSet wrSampleSet;

	/**
	 * This constructor to join 2 Matrices.
	 * The MarkerSet from the 1st Matrix will be used in the result Matrix.
	 * No new Markers from the 2nd Matrix will be added.
	 * Samples from the 2nd Matrix will be appended to the end of the SampleSet from the 1st Matrix.
	 * Duplicate Samples from the 2nd Matrix will overwrite Samples in the 1st Matrix
	 */
	public MatrixMergeSamples(
			int studyId, // XXX this is unused, confusing!
			int rdMatrix1Id,
			int rdMatrix2Id,
			String wrMatrixFriendlyName,
			String wrMatrixDescription)
			throws IOException, InvalidRangeException
	{
		// INIT EXTRACTOR OBJECTS
		this.rdMatrix1Id = rdMatrix1Id;
		this.rdMatrix2Id = rdMatrix2Id;

		this.rdMatrix1Metadata = MatricesList.getMatrixMetadataById(this.rdMatrix1Id);
		this.rdMatrix2Metadata = MatricesList.getMatrixMetadataById(this.rdMatrix2Id);
		this.studyId = this.rdMatrix1Metadata.getStudyId();

		this.rdMatrix1Metadata = MatricesList.getMatrixMetadataById(this.rdMatrix1Id);
		this.rdMatrix2Metadata = MatricesList.getMatrixMetadataById(this.rdMatrix2Id);

		this.rdwrMarkerSet1 = new MarkerSet(this.rdMatrix1Metadata.getStudyId(), this.rdMatrix1Id);
		this.rdMarkerSet2 = new MarkerSet(this.rdMatrix2Metadata.getStudyId(), this.rdMatrix2Id);

		this.rdSampleSet1 = new SampleSet(this.rdMatrix1Metadata.getStudyId(), this.rdMatrix1Id);
		this.rdSampleSet2 = new SampleSet(this.rdMatrix2Metadata.getStudyId(), this.rdMatrix2Id);

		this.wrMatrixFriendlyName = wrMatrixFriendlyName;
		this.wrMatrixDescription = wrMatrixDescription;
	}

	public int appendSamplesKeepMarkersConstant() throws IOException, InvalidRangeException {
		int resultMatrixId = Integer.MIN_VALUE;

		NetcdfFile rdNcFile1 = NetcdfFile.open(rdMatrix1Metadata.getPathToMatrix());
		NetcdfFile rdNcFile2 = NetcdfFile.open(rdMatrix2Metadata.getPathToMatrix());

		// Get combo SampleSet with position[] (wrPos, rdMatrixNb, rdPos)
		Map<SampleKey, Object> rdSampleSetMap1 = rdSampleSet1.getSampleIdSetMap();
		Map<SampleKey, Object> rdSampleSetMap2 = rdSampleSet2.getSampleIdSetMap();
		Map<SampleKey, Object> wrComboSampleSetMap = MatrixMerge.getComboSampleSetWithIndicesArray(rdSampleSetMap1, rdSampleSetMap2);
		Map<SampleKey, Object> theSamples = wrComboSampleSetMap;

		// Use comboed wrComboSampleSetMap as SampleSet
		final int numSamples = theSamples.size();
		final String humanReadableMethodName = Text.Trafo.mergeSamplesOnly;
		final String methodDescription = Text.Trafo.mergeMethodSampleJoin;

		rdwrMarkerSet1.initFullMarkerIdSetMap();
		rdMarkerSet2.initFullMarkerIdSetMap();

		// RETRIEVE CHROMOSOMES INFO
//		rdwrMarkerSet1.fillMarkerSetMapWithChrAndPos();
//		wrMarkerIdSetMap = rdMarkerSet.replaceWithValuesFrom(wrMarkerIdSetMap, rdMarkerSet.markerIdSetMap);
//		rdChrInfoSetMap = org.gwaspi.netCDF.matrices.Utils.aggregateChromosomeInfo(wrMarkerIdSetMap, 0, 1);

		Map<MarkerKey, Object> rdChrInfoSetMap = rdwrMarkerSet1.getChrInfoSetMap();

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
			descSB.append("Markers: ").append(rdSampleSetMap1.size()).append(", Samples: ").append(numSamples);
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
					rdwrMarkerSet1.getMarkerSetSize(), // Keep rdwrMarkerIdSetMap1 from Matrix1. MarkerSet is constant
					rdChrInfoSetMap.size(),
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

			// Keep rdwrMarkerIdSetMap1 from Matrix1 constant
			// MARKERSET MARKERID
			ArrayChar.D2 markersD2 = Utils.writeMapKeysToD2ArrayChar(rdwrMarkerSet1.getMarkerIdSetMap(), cNetCDF.Strides.STRIDE_MARKER_NAME);
			int[] markersOrig = new int[]{0, 0};
			try {
				wrNcFile.write(cNetCDF.Variables.VAR_MARKERSET, markersOrig, markersD2);
			} catch (IOException ex) {
				log.error("Failed writing file", ex);
			} catch (InvalidRangeException ex) {
				log.error(null, ex);
			}

			// MARKERSET RSID
			rdwrMarkerSet1.fillInitMapWithVariable(cNetCDF.Variables.VAR_MARKERS_RSID);
			Utils.saveCharMapValueToWrMatrix(wrNcFile, rdwrMarkerSet1.getMarkerIdSetMap(), cNetCDF.Variables.VAR_MARKERS_RSID, cNetCDF.Strides.STRIDE_MARKER_NAME);

			// MARKERSET CHROMOSOME
			rdwrMarkerSet1.fillInitMapWithVariable(cNetCDF.Variables.VAR_MARKERS_CHR);
			Utils.saveCharMapValueToWrMatrix(wrNcFile, rdwrMarkerSet1.getMarkerIdSetMap(), cNetCDF.Variables.VAR_MARKERS_CHR, cNetCDF.Strides.STRIDE_CHR);

			// Set of chromosomes found in matrix along with number of markersinfo
			org.gwaspi.netCDF.operations.Utils.saveCharMapKeyToWrMatrix(wrNcFile, rdChrInfoSetMap, cNetCDF.Variables.VAR_CHR_IN_MATRIX, 8);
			// Number of marker per chromosome & max pos for each chromosome
			int[] columns = new int[]{0, 1, 2, 3};
			org.gwaspi.netCDF.operations.Utils.saveIntMapD2ToWrMatrix(wrNcFile, rdChrInfoSetMap, columns, cNetCDF.Variables.VAR_CHR_INFO);

			// MARKERSET POSITION
			rdwrMarkerSet1.fillInitMapWithVariable(cNetCDF.Variables.VAR_MARKERS_POS);
			//Utils.saveCharMapValueToWrMatrix(wrNcFile, rdwrMarkerIdSetMap1, cNetCDF.Variables.VAR_MARKERS_POS, cNetCDF.Strides.STRIDE_POS);
			Utils.saveIntMapD1ToWrMatrix(wrNcFile, rdwrMarkerSet1.getMarkerIdSetMap(), cNetCDF.Variables.VAR_MARKERS_POS);

			// MARKERSET DICTIONARY ALLELES
			Attribute hasDictionary1 = rdNcFile1.findGlobalAttribute(cNetCDF.Attributes.GLOB_HAS_DICTIONARY);
			if ((Integer) hasDictionary1.getNumericValue() == 1) {
				rdwrMarkerSet1.fillInitMapWithVariable(cNetCDF.Variables.VAR_MARKERS_BASES_DICT);
				Utils.saveCharMapValueToWrMatrix(wrNcFile, rdwrMarkerSet1.getMarkerIdSetMap(), cNetCDF.Variables.VAR_MARKERS_BASES_DICT, cNetCDF.Strides.STRIDE_GT);
			}

			//<editor-fold defaultstate="expanded" desc="GENOTYPE STRAND">
			rdwrMarkerSet1.fillInitMapWithVariable(cNetCDF.Variables.VAR_GT_STRAND);
			Utils.saveCharMapValueToWrMatrix(wrNcFile, rdwrMarkerSet1.getMarkerIdSetMap(), cNetCDF.Variables.VAR_GT_STRAND, 3);
			//</editor-fold>
			//</editor-fold>

			//<editor-fold defaultstate="expanded" desc="GENOTYPES WRITER">
			rdMarkerSet2.initFullMarkerIdSetMap();

			// Iterate through wrSampleSetMap, use item position to read correct sample GTs into rdMarkerIdSetMap.
			for (Object value : wrComboSampleSetMap.values()) {             //Next SampleId
				int[] sampleIndices = (int[]) value; //Next position[rdMatrixNb, rdPos, wrPos] to read/write

				// Iterate through wrMarkerIdSetMap, get the correct GT from rdMarkerIdSetMap
				if (sampleIndices[0] == 1) { // Read from Matrix1
					rdwrMarkerSet1.fillWith(cNetCDF.Defaults.DEFAULT_GT);
					rdwrMarkerSet1.fillGTsForCurrentSampleIntoInitMap(sampleIndices[1]);
				}
				if (sampleIndices[0] == 2) { // Read from Matrix2
					rdwrMarkerSet1.fillWith(cNetCDF.Defaults.DEFAULT_GT);
					rdMarkerSet2.fillGTsForCurrentSampleIntoInitMap(sampleIndices[1]);
					for (Map.Entry<MarkerKey, Object> entry : rdwrMarkerSet1.getMarkerIdSetMap().entrySet()) {
						MarkerKey key = entry.getKey();
						if (rdMarkerSet2.getMarkerIdSetMap().containsKey(key)) {
							Object markerValue = rdMarkerSet2.getMarkerIdSetMap().get(key);
							entry.setValue(markerValue);
						}
					}
					//rdwrMarkerIdSetMap1 = rdMarkerSet2.replaceWithValuesFrom(rdwrMarkerIdSetMap1, rdMarkerIdSetMap2);
				}

				// Write wrMarkerIdSetMap to A3 ArrayChar and save to wrMatrix
				Utils.saveSingleSampleGTsToMatrix(wrNcFile, rdwrMarkerSet1.getMarkerIdSetMap(), sampleIndices[2]);
			}
			//</editor-fold>

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
					double[] mismatchState = checkForMismatches(wrMatrixHandler.getResultMatrixId()); //mismatchCount, mismatchRatio
					if (mismatchState[1] > 0.01) {
						log.warn("");
						log.warn("Mismatch ratio is bigger than 1% ({}%)!", (mismatchState[1] * 100));
						log.warn("There might be an issue with strand positioning of your genotypes!");
						log.warn("");
						//resultMatrixId = new int[]{wrMatrixHandler.getResultMatrixId(),-4};  //The threshold of acceptable mismatching genotypes has been crossed
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

		return resultMatrixId;
	}

	private double[] checkForMismatches(int wrMatrixId) throws IOException, InvalidRangeException {
		double[] result = new double[2];

		wrMatrixMetadata = MatricesList.getMatrixMetadataById(wrMatrixId);
		wrSampleSet = new SampleSet(wrMatrixMetadata.getStudyId(), wrMatrixId);
		wrMarkerSet = new MarkerSet(wrMatrixMetadata.getStudyId(), wrMatrixId);
		wrMarkerSet.initFullMarkerIdSetMap();
		Map<SampleKey, Object> wrSampleSetMap = wrSampleSet.getSampleIdSetMap();

		NetcdfFile rdNcFile = NetcdfFile.open(wrMatrixMetadata.getPathToMatrix());

		// Iterate through markerset, take it marker by marker
		int markerNb = 0;
		double mismatchCount = 0;

		// Iterate through markerSet
		for (MarkerKey markerKey : wrMarkerSet.getMarkerIdSetMap().keySet()) {
			Map<Character, Object> knownAlleles = new LinkedHashMap<Character, Object>();

			// Get a sampleset-full of GTs
			wrSampleSetMap = wrSampleSet.readAllSamplesGTsFromCurrentMarkerToMap(rdNcFile, wrSampleSetMap, markerNb);

			// Iterate through sampleSet
			for (Object value : wrSampleSetMap.values()) {
				char[] tempGT = value.toString().toCharArray();

				// Gather alleles different from 0 into a list of known alleles and count the number of appearences
				if (tempGT[0] != '0') {
					int tempCount = 0;
					if (knownAlleles.containsKey(tempGT[0])) {
						tempCount = (Integer) knownAlleles.get(tempGT[0]);
					}
					knownAlleles.put(tempGT[0], tempCount + 1);
				}
				if (tempGT[1] != '0') {
					int tempCount = 0;
					if (knownAlleles.containsKey(tempGT[1])) {
						tempCount = (Integer) knownAlleles.get(tempGT[1]);
					}
					knownAlleles.put(tempGT[1], tempCount + 1);
				}
			}

			if (knownAlleles.size() > 2) {
				mismatchCount++;
			}

			markerNb++;
			if (markerNb % 100000 == 0) {
				log.info("Checking markers for mismatches: {}", markerNb);
			}
		}

		double mismatchRatio = mismatchCount / wrSampleSet.getSampleSetSize();
		result[0] = mismatchCount;
		result[1] = mismatchRatio;

		return result;
	}
}