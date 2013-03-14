package org.gwaspi.netCDF.operations;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import org.gwaspi.constants.cImport.ImportFormat;
import org.gwaspi.constants.cNetCDF;
import org.gwaspi.constants.cNetCDF.Defaults.GenotypeEncoding;
import org.gwaspi.global.Text;
import org.gwaspi.model.MarkerKey;
import org.gwaspi.model.MatricesList;
import org.gwaspi.model.MatrixMetadata;
import org.gwaspi.model.SampleKey;
import org.gwaspi.netCDF.loader.ComparatorChrAutPosMarkerIdAsc;
import org.gwaspi.netCDF.markers.MarkerSet_opt;
import org.gwaspi.netCDF.matrices.MatrixFactory;
import org.gwaspi.samples.SampleSet;
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
public class MatrixMerge {

	private final Logger log = LoggerFactory.getLogger(MatrixMerge.class);

	private int studyId;
	private int rdMatrix1Id;
	private int rdMatrix2Id;
	private int wrMatrixId;
	private String wrMatrixFriendlyName;
	private String wrMatrixDescription;
	private MatrixMetadata rdMatrix1Metadata;
	private MatrixMetadata rdMatrix2Metadata;
	private MatrixMetadata wrMatrixMetadata;
	private MarkerSet_opt rdMarkerSet1;
	private MarkerSet_opt rdMarkerSet2;
	private MarkerSet_opt wrMarkerSet;
	private SampleSet rdSampleSet1;
	private SampleSet rdSampleSet2;
	private SampleSet wrSampleSet;
	/**
	 * Whether to merge all, or only the marked samples
	 * TODO the second part of the previous sentence needs revising
	 */
	private boolean all;

	/**
	 * This constructor to join 2 Matrices.
	 * The SampleSet from the 1st Matrix will be used in the result Matrix.
	 * No new Samples from the 2nd Matrix will be added.
	 * Markers from the 2nd Matrix will be mingled as per their chromosome
	 * and position to the MarkersSet from the 1st Matrix.
	 * Duplicate Markers from the 2nd Matrix will overwrite Markers in the 1st Matrix
	 */
	public MatrixMerge(
			int studyId,
			int rdMatrix1Id,
			int rdMatrix2Id,
			String wrMatrixFriendlyName,
			String wrMatrixDescription,
			boolean all)
			throws IOException, InvalidRangeException
	{
		// INIT EXTRACTOR OBJECTS
		this.wrMatrixId = Integer.MIN_VALUE;
		this.wrMatrixMetadata = null;
		this.wrMarkerSet = null;
		this.wrSampleSet = null;

		this.studyId = studyId;

		this.rdMatrix1Id = rdMatrix1Id;
		this.rdMatrix2Id = rdMatrix2Id;

		this.rdMatrix1Metadata = MatricesList.getMatrixMetadataById(this.rdMatrix1Id);
		this.rdMatrix2Metadata = MatricesList.getMatrixMetadataById(this.rdMatrix2Id);

		this.rdMarkerSet1 = new MarkerSet_opt(this.studyId, this.rdMatrix1Id);
		this.rdMarkerSet2 = new MarkerSet_opt(this.studyId, this.rdMatrix2Id);

		this.rdSampleSet1 = new SampleSet(this.studyId, this.rdMatrix1Id);
		this.rdSampleSet2 = new SampleSet(this.studyId, this.rdMatrix2Id);

		this.wrMatrixFriendlyName = wrMatrixFriendlyName;
		this.wrMatrixDescription = wrMatrixDescription;

		this.all = all;
	}

	public int mingleMarkersKeepSamplesConstant() throws IOException, InvalidRangeException {
		int resultMatrixId = Integer.MIN_VALUE;

		NetcdfFile rdNcFile1 = NetcdfFile.open(rdMatrix1Metadata.getPathToMatrix());
		NetcdfFile rdNcFile2 = NetcdfFile.open(rdMatrix2Metadata.getPathToMatrix());

		Map<MarkerKey, Object> wrComboSortedMarkerSetMap = mingleAndSortMarkerSet();

		// RETRIEVE CHROMOSOMES INFO
		Map<MarkerKey, Object> chrSetMap = org.gwaspi.netCDF.matrices.Utils.aggregateChromosomeInfo(wrComboSortedMarkerSetMap, 0, 1);

		// Get combo SampleSet with position[] (wrPos, rdMatrixNb, rdPos)
		Map<SampleKey, Object> rdSampleSetMap1 = rdSampleSet1.getSampleIdSetMap();
		Map<SampleKey, Object> rdSampleSetMap2 = rdSampleSet2.getSampleIdSetMap();
		Map<SampleKey, Object> wrSampleSetMap = (all
				? getComboSampleSetWithIndicesArray(rdSampleSetMap1, rdSampleSetMap2)
				: getSampleSetWithIndicesMap(rdSampleSetMap1, rdSampleSetMap2));
		Map<SampleKey, Object> theSamples = (all ? wrSampleSetMap : rdSampleSetMap1);

		final int numSamples = (all
				? wrSampleSetMap.size() // Comboed SampleSet
				: rdMatrix1Metadata.getSampleSetSize()); // Keep rdMatrix1Metadata from Matrix1. SampleSet is constant
		final String humanReadableMethodName = (all ? Text.Trafo.mergeAll : Text.Trafo.mergeMarkersOnly);
		final String methodDescription = (all ? Text.Trafo.mergeMethodMergeAll : Text.Trafo.mergeMethodMarkerJoin);

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
					chrSetMap.size(),
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
			int[] markersOrig = new int[]{0, 0};
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
			org.gwaspi.netCDF.operations.Utils.saveCharMapKeyToWrMatrix(wrNcFile, chrSetMap, cNetCDF.Variables.VAR_CHR_IN_MATRIX, 8);
			// Number of marker per chromosome & max pos for each chromosome
			int[] columns = new int[]{0, 1, 2, 3};
			org.gwaspi.netCDF.operations.Utils.saveIntMapD2ToWrMatrix(wrNcFile, chrSetMap, columns, cNetCDF.Variables.VAR_CHR_INFO);

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

			//<editor-fold defaultstate="expanded" desc="GENOTYPES WRITER">
			if (all) {
				// Get SampleId index from each Matrix
				// Iterate through wrSampleSetMap
				int wrSampleIndex = 0;
				for (Map.Entry<SampleKey, Object> entry : wrSampleSetMap.entrySet()) {
					SampleKey sampleKey = entry.getKey();
					int[] rdSampleIndices = (int[]) entry.getValue(); // position[rdPos matrix 1, rdPos matrix 2]

					// Read from Matrix1
					rdMarkerSet1.fillWith(cNetCDF.Defaults.DEFAULT_GT);
					if (rdSampleSet1.getSampleIdSetMap().containsKey(sampleKey)) {
						rdMarkerSet1.fillGTsForCurrentSampleIntoInitMap(rdSampleIndices[0]);
					}

					// Read from Matrix2
					rdMarkerSet2.fillWith(cNetCDF.Defaults.DEFAULT_GT);

					if (rdSampleSet2.getSampleIdSetMap().containsKey(sampleKey)) {
						rdMarkerSet2.fillGTsForCurrentSampleIntoInitMap(rdSampleIndices[1]);
					}

					// Fill wrSortedMingledMarkerMap with matrix 1+2 Genotypes
					for (Map.Entry<MarkerKey, Object> markerEntry : wrComboSortedMarkerSetMap.entrySet()) {
						MarkerKey markerKey = markerEntry.getKey();
						byte[] genotype = cNetCDF.Defaults.DEFAULT_GT;
						if (rdSampleSetMap1.containsKey(sampleKey) && rdMarkerSet1.getMarkerIdSetMap().containsKey(markerKey)) {
							genotype = (byte[]) rdMarkerSet1.getMarkerIdSetMap().get(markerKey);
						}
						if (rdSampleSetMap2.containsKey(sampleKey) && rdMarkerSet2.getMarkerIdSetMap().containsKey(markerKey)) {
							genotype = (byte[]) rdMarkerSet2.getMarkerIdSetMap().get(markerKey);
						}

						markerEntry.setValue(genotype);
					}

					// Write wrMarkerIdSetMap to A3 ArrayChar and save to wrMatrix
					Utils.saveSingleSampleGTsToMatrix(wrNcFile, wrComboSortedMarkerSetMap, wrSampleIndex);
					wrSampleIndex++;
				}
			} else {
				// Get SampleId index from each Matrix
				// Iterate through wrSampleSetMap
				for (Object value : wrSampleSetMap.values()) {
					int[] sampleIndices = (int[]) value; // position[rdPos matrix 1, rdPos matrix 2]

					// Read from Matrix1
					rdMarkerSet1.fillWith(cNetCDF.Defaults.DEFAULT_GT);
					rdMarkerSet1.fillGTsForCurrentSampleIntoInitMap(sampleIndices[0]);

					// Read from Matrix2
					rdMarkerSet2.fillWith(cNetCDF.Defaults.DEFAULT_GT);
					rdMarkerSet2.fillGTsForCurrentSampleIntoInitMap(sampleIndices[1]);

					// Fill wrSortedMingledMarkerMap with matrix 1+2 Genotypes
					for (Map.Entry<MarkerKey, Object> markerEntry : wrComboSortedMarkerSetMap.entrySet()) {
						MarkerKey markerKey = markerEntry.getKey();
						byte[] genotype = cNetCDF.Defaults.DEFAULT_GT;
						if (rdMarkerSet1.getMarkerIdSetMap().containsKey(markerKey)) {
							genotype = (byte[]) rdMarkerSet1.getMarkerIdSetMap().get(markerKey);
						}
						if (rdMarkerSet2.getMarkerIdSetMap().containsKey(markerKey)) {
							genotype = (byte[]) rdMarkerSet2.getMarkerIdSetMap().get(markerKey);
						}

						markerEntry.setValue(genotype);
					}

					// Write wrMarkerIdSetMap to A3 ArrayChar and save to wrMatrix
					Utils.saveSingleSampleGTsToMatrix(wrNcFile, wrComboSortedMarkerSetMap, sampleIndices[0]);
				}
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
		//</editor-fold>

		return resultMatrixId;
	}

	private Map<MarkerKey, Object> mingleAndSortMarkerSet() {

		// GET 1st MATRIX Map WITH CHR AND POS
		Map<MarkerKey, Object> workMap = new LinkedHashMap<MarkerKey, Object>();
		rdMarkerSet1.initFullMarkerIdSetMap();
		rdMarkerSet2.initFullMarkerIdSetMap();
		rdMarkerSet1.fillWith("");
		rdMarkerSet1.fillInitMapWithVariable(cNetCDF.Variables.VAR_MARKERS_CHR);
		workMap.putAll(rdMarkerSet1.getMarkerIdSetMap());
		rdMarkerSet1.fillWith("");
		rdMarkerSet1.fillInitMapWithVariable(cNetCDF.Variables.VAR_MARKERS_POS);
		for (Map.Entry<MarkerKey, Object> entry : workMap.entrySet()) {
			MarkerKey markerKey = entry.getKey();
			String chr = entry.getValue().toString();
			String pos = rdMarkerSet1.getMarkerIdSetMap().get(markerKey).toString();
			StringBuilder sbKey = new StringBuilder(chr);
			sbKey.append(cNetCDF.Defaults.TMP_SEPARATOR);
			sbKey.append(pos);
			sbKey.append(cNetCDF.Defaults.TMP_SEPARATOR);
			sbKey.append(markerKey.getMarkerId());
			entry.setValue(sbKey.toString());
		}
		if (rdMarkerSet1.getMarkerIdSetMap() != null) {
			rdMarkerSet1.getMarkerIdSetMap().clear();
		}

		// GET 2nd MATRIX Map WITH CHR AND POS
		Map<MarkerKey, Object> workMap2 = new LinkedHashMap<MarkerKey, Object>();
		rdMarkerSet2.fillWith("");
		rdMarkerSet2.fillInitMapWithVariable(cNetCDF.Variables.VAR_MARKERS_CHR);
		workMap2.putAll(rdMarkerSet2.getMarkerIdSetMap());
		rdMarkerSet2.fillWith("");
		rdMarkerSet2.fillInitMapWithVariable(cNetCDF.Variables.VAR_MARKERS_POS);
		for (Map.Entry<MarkerKey, Object> entry : workMap2.entrySet()) {
			MarkerKey markerKey = entry.getKey();
			String chr = entry.getValue().toString();
			String pos = rdMarkerSet2.getMarkerIdSetMap().get(markerKey).toString();
			StringBuilder sbKey = new StringBuilder(chr);
			sbKey.append(cNetCDF.Defaults.TMP_SEPARATOR);
			sbKey.append(pos);
			sbKey.append(cNetCDF.Defaults.TMP_SEPARATOR);
			sbKey.append(markerKey.getMarkerId());
			entry.setValue(sbKey.toString());
		}
		if (rdMarkerSet2.getMarkerIdSetMap() != null) {
			rdMarkerSet2.getMarkerIdSetMap().clear();
		}

		workMap.putAll(workMap2);

		// SORT MERGED Map
		SortedMap<String, MarkerKey> sortedMetadataTM = new TreeMap<String, MarkerKey>(new ComparatorChrAutPosMarkerIdAsc());
		for (Map.Entry<MarkerKey, Object> entry : workMap.entrySet()) {
			MarkerKey key = entry.getKey();
			String value = entry.getValue().toString();
			sortedMetadataTM.put(value, key);
		}
		if (workMap != null) {
			workMap.clear();
		}

		// PACKAGE IN AN Map
		for (Map.Entry<String, MarkerKey> entry : sortedMetadataTM.entrySet()) {
			String key = entry.getKey();
			String[] keyValues = key.split(cNetCDF.Defaults.TMP_SEPARATOR);
			Object[] markerInfo = new Object[2];
			markerInfo[0] = keyValues[0]; // => chr
			markerInfo[1] = Integer.parseInt(keyValues[1]); // => pos

			MarkerKey markerKey = entry.getValue();
			workMap.put(markerKey, markerInfo);
		}

		return workMap;
	}

	static Map<SampleKey, Object> getComboSampleSetWithIndicesArray(Map<SampleKey, Object> sampleSetMap1, Map<SampleKey, Object> sampleSetMap2) {
		Map<SampleKey, Object> resultMap = new LinkedHashMap<SampleKey, Object>();

		int wrPos = 0;
		int rdPos = 0;
		for (SampleKey key : sampleSetMap1.keySet()) {
			int[] position = new int[]{1, rdPos, wrPos}; // rdMatrixNb, rdPos, wrPos
			resultMap.put(key, position);
			wrPos++;
			rdPos++;
		}

		rdPos = 0;
		for (SampleKey key : sampleSetMap2.keySet()) {
			int[] position;
			// IF SAMPLE ALLREADY EXISTS IN MATRIX1 SUBSTITUTE VALUES WITH MATRIX2
			if (resultMap.containsKey(key)) {
				position = (int[]) resultMap.get(key);
				position[0] = 2; // rdMatrixNb
				position[1] = rdPos; // rdPos
			} else {
				position = new int[]{2, rdPos, wrPos}; // rdMatrixNb, rdPos, wrPos
			}

			resultMap.put(key, position);
			wrPos++;
			rdPos++;
		}

		return resultMap;
	}

	private static Map<SampleKey, Object> getSampleSetWithIndicesMap(Map<SampleKey, Object> sampleSetMap1, Map<SampleKey, Object> sampleSetMap2) {
		Map<SampleKey, Object> resultMap = new LinkedHashMap<SampleKey, Object>();

		int rdPos = 0;
		for (SampleKey key : sampleSetMap1.keySet()) {
			int[] position = new int[]{rdPos, 0}; // rdPos matrix 1
			resultMap.put(key, position);
			rdPos++;
		}

		rdPos = 0;
		for (SampleKey key : sampleSetMap2.keySet()) {
			// IF SAMPLE ALLREADY EXISTS IN MATRIX1 SUBSTITUTE VALUES WITH MATRIX2
			if (resultMap.containsKey(key)) {
				int[] position = (int[]) resultMap.get(key);
				position[1] = rdPos; // rdPos matrix 2
				resultMap.put(key, position);
			}

			rdPos++;
		}

		return resultMap;
	}

	private double[] checkForMismatches(int wrMatrixId) throws IOException, InvalidRangeException {
		double[] result = new double[2];

		wrMatrixMetadata = MatricesList.getMatrixMetadataById(wrMatrixId);
		wrSampleSet = new SampleSet(wrMatrixMetadata.getStudyId(), wrMatrixId);
		wrMarkerSet = new MarkerSet_opt(wrMatrixMetadata.getStudyId(), wrMatrixId);
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