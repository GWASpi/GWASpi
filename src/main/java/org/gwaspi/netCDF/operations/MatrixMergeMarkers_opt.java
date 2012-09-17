package org.gwaspi.netCDF.operations;

import org.gwaspi.constants.cDBGWASpi;
import org.gwaspi.constants.cDBMatrix;
import org.gwaspi.constants.cNetCDF;
import org.gwaspi.database.DbManager;
import org.gwaspi.global.ServiceLocator;
import org.gwaspi.global.Text;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import org.gwaspi.netCDF.loader.ComparatorChrAutPosMarkerIdAsc;
import org.gwaspi.netCDF.markers.MarkerSet_opt;
import org.gwaspi.netCDF.matrices.MatrixFactory;
import org.gwaspi.netCDF.matrices.MatrixMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gwaspi.samples.SampleSet;
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
public class MatrixMergeMarkers_opt {

	private final Logger log = LoggerFactory.getLogger(MatrixMergeMarkers_opt.class);

	private int studyId;
	private int rdMatrix1Id;
	private int rdMatrix2Id;
	private int wrMatrixId;
	private NetcdfFile rdNcFile1;
	private NetcdfFile rdNcFile2;
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
	 * This constructor to join 2 Matrices.
	 * The SampleSet from the 1st Matrix will be used in the result Matrix.
	 * No new Samples from the 2nd Matrix will be added.
	 * Markers from the 2nd Matrix will be mingled as per their chromosome
	 * and position to the MarkersSet from the 1st Matrix.
	 * Duplicate Markers from the 2nd Matrix will overwrite Markers in the 1st Matrix
	 */
	public MatrixMergeMarkers_opt(
			int studyId,
			int rdMatrix1Id,
			int rdMatrix2Id,
			String wrMatrixFriendlyName,
			String wrMatrixDescription)
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

		this.rdMatrix1Metadata = new MatrixMetadata(this.rdMatrix1Id);
		this.rdMatrix2Metadata = new MatrixMetadata(this.rdMatrix2Id);

		this.rdMarkerSet1 = new MarkerSet_opt(this.studyId, this.rdMatrix1Id);
		this.rdMarkerSet2 = new MarkerSet_opt(this.studyId, this.rdMatrix2Id);

		this.rdSampleSet1 = new SampleSet(this.studyId, this.rdMatrix1Id);
		this.rdSampleSet2 = new SampleSet(this.studyId, this.rdMatrix2Id);

		this.wrMatrixFriendlyName = wrMatrixFriendlyName;
		this.wrMatrixDescription = wrMatrixDescription;

		this.rdNcFile1 = NetcdfFile.open(this.rdMatrix1Metadata.getPathToMatrix());
		this.rdNcFile2 = NetcdfFile.open(this.rdMatrix2Metadata.getPathToMatrix());
	}

	public int mingleMarkersKeepSamplesConstant() {
		int resultMatrixId = Integer.MIN_VALUE;

		Map<String, Object> wrComboSortedMarkerSetLHM = mingleAndSortMarkerSet();

		// RETRIEVE CHROMOSOMES INFO
		Map<String, Object> chrSetLHM = org.gwaspi.netCDF.matrices.Utils.aggregateChromosomeInfo(wrComboSortedMarkerSetLHM, 0, 1);

		//<editor-fold defaultstate="collapsed" desc="CREATE MATRIX">
		try {
			// CREATE netCDF-3 FILE
			int hasDictionary = 0;
			if (rdMatrix1Metadata.getHasDictionray() == rdMatrix2Metadata.getHasDictionray()) {
				hasDictionary = rdMatrix1Metadata.getHasDictionray();
			}
			String gtEncoding = cNetCDF.Defaults.GenotypeEncoding.UNKNOWN.toString();
			if (rdMatrix1Metadata.getGenotypeEncoding().equals(rdMatrix2Metadata.getGenotypeEncoding())) {
				gtEncoding = rdMatrix1Metadata.getGenotypeEncoding();
			}
			String technology = cNetCDF.Defaults.GenotypeEncoding.UNKNOWN.toString();
			if (rdMatrix1Metadata.getTechnology().equals(rdMatrix2Metadata.getTechnology())) {
				technology = rdMatrix1Metadata.getTechnology();
			}

			StringBuilder descSB = new StringBuilder(Text.Matrix.descriptionHeader1);
			descSB.append(org.gwaspi.global.Utils.getShortDateTimeAsString());
			descSB.append("\n");
			descSB.append("Markers: ").append(wrComboSortedMarkerSetLHM.size()).append(", Samples: ").append(rdMatrix1Metadata.getSampleSetSize());
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
			descSB.append(Text.Trafo.mergeMarkersOnly);
			descSB.append(":\n");
			descSB.append(Text.Trafo.mergeMethodMarkerJoin);

			MatrixFactory wrMatrixHandler = new MatrixFactory(studyId,
					technology, // technology
					wrMatrixFriendlyName,
					wrMatrixDescription + "\n\n" + descSB.toString(), // description
					rdMatrix1Metadata.getStrand(),
					hasDictionary, // has dictionary?
					rdMatrix1Metadata.getSampleSetSize(), // Keep rdMatrix1Metadata from Matrix1. SampleSet is constant
					wrComboSortedMarkerSetLHM.size(), // Use comboed wrSortedMingledMarkerLHM as MarkerSet
					chrSetLHM.size(),
					gtEncoding, // GT encoding
					rdMatrix1Id, // Parent matrixId 1
					rdMatrix2Id); // Parent matrixId 2

			resultMatrixId = wrMatrixHandler.getResultMatrixId();

			NetcdfFileWriteable wrNcFile = wrMatrixHandler.getNetCDFHandler();
			try {
				wrNcFile.create();
			} catch (IOException ex) {
				log.error("Failed creating file " + wrNcFile.getLocation(), ex);
			}
			//log.trace("Done creating netCDF handle in MatrixSampleJoin: " + org.gwaspi.global.Utils.getMediumDateTimeAsString());

			//<editor-fold defaultstate="collapsed" desc="METADATA WRITER">
			// SAMPLESET
			// Keep rdSampleSetLHM1 from Matrix1 constant
			Map<String, Object> rdSampleSetLHM1 = rdSampleSet1.getSampleIdSetLHM();
			ArrayChar.D2 samplesD2 = Utils.writeLHMKeysToD2ArrayChar(rdSampleSetLHM1, cNetCDF.Strides.STRIDE_SAMPLE_NAME);

			int[] sampleOrig = new int[]{0, 0};
			try {
				wrNcFile.write(cNetCDF.Variables.VAR_SAMPLESET, sampleOrig, samplesD2);
			} catch (IOException ex) {
				log.error("Failed writing file", ex);
			} catch (InvalidRangeException ex) {
				log.error(null, ex);
			}
			log.info("Done writing SampleSet to matrix at {}", org.gwaspi.global.Utils.getMediumDateTimeAsString()); // FIXME log system already adds time

			// MARKERSET MARKERID
			ArrayChar.D2 markersD2 = Utils.writeLHMKeysToD2ArrayChar(wrComboSortedMarkerSetLHM, cNetCDF.Strides.STRIDE_MARKER_NAME);
			int[] markersOrig = new int[]{0, 0};
			try {
				wrNcFile.write(cNetCDF.Variables.VAR_MARKERSET, markersOrig, markersD2);
			} catch (IOException ex) {
				log.error("Failed writing file", ex);
			} catch (InvalidRangeException ex) {
				log.error(null, ex);
			}

			// WRITE CHROMOSOME METADATA FROM ANNOTATION FILE
			markersD2 = org.gwaspi.netCDF.operations.Utils.writeLHMValueItemToD2ArrayChar(wrComboSortedMarkerSetLHM, 0, cNetCDF.Strides.STRIDE_CHR);

			try {
				wrNcFile.write(cNetCDF.Variables.VAR_MARKERS_CHR, markersOrig, markersD2);
			} catch (IOException ex) {
				log.error("Failed writing file", ex);
			} catch (InvalidRangeException ex) {
				log.error(null, ex);
			}
			log.info("Done writing chromosomes to matrix at {}", org.gwaspi.global.Utils.getMediumDateTimeAsString()); // FIXME log system already adds time

			// Set of chromosomes found in matrix along with number of markersinfo
			org.gwaspi.netCDF.operations.Utils.saveCharLHMKeyToWrMatrix(wrNcFile, chrSetLHM, cNetCDF.Variables.VAR_CHR_IN_MATRIX, 8);
			// Number of marker per chromosome & max pos for each chromosome
			int[] columns = new int[]{0, 1, 2, 3};
			org.gwaspi.netCDF.operations.Utils.saveIntLHMD2ToWrMatrix(wrNcFile, chrSetLHM, columns, cNetCDF.Variables.VAR_CHR_INFO);

			// WRITE POSITION METADATA FROM ANNOTATION FILE
			ArrayInt.D1 markersPosD1 = org.gwaspi.netCDF.operations.Utils.writeLHMValueItemToD1ArrayInt(wrComboSortedMarkerSetLHM, 1);
			int[] posOrig = new int[1];
			try {
				wrNcFile.write(cNetCDF.Variables.VAR_MARKERS_POS, posOrig, markersPosD1);
			} catch (IOException ex) {
				log.error("Failed writing file", ex);
			} catch (InvalidRangeException ex) {
				log.error(null, ex);
			}
			log.info("Done writing positions to matrix at {}", org.gwaspi.global.Utils.getMediumDateTimeAsString()); // FIXME log system already adds time

			//<editor-fold defaultstate="collapsed" desc="GATHER METADATA FROM BOTH MATRICES">
			rdMarkerSet1.initFullMarkerIdSetLHM();
			rdMarkerSet2.initFullMarkerIdSetLHM();

			//<editor-fold defaultstate="collapsed" desc="MARKERSET RSID">
			rdMarkerSet1.fillInitLHMWithVariable(cNetCDF.Variables.VAR_MARKERS_RSID);
			wrComboSortedMarkerSetLHM.putAll(rdMarkerSet1.getMarkerIdSetLHM());
			rdMarkerSet2.fillInitLHMWithVariable(cNetCDF.Variables.VAR_MARKERS_RSID);
			wrComboSortedMarkerSetLHM.putAll(rdMarkerSet2.getMarkerIdSetLHM());

			Utils.saveCharLHMValueToWrMatrix(wrNcFile, wrComboSortedMarkerSetLHM, cNetCDF.Variables.VAR_MARKERS_RSID, cNetCDF.Strides.STRIDE_MARKER_NAME);
			//</editor-fold>

			//<editor-fold defaultstate="collapsed" desc="MARKERSET DICTIONARY ALLELES">
			Attribute hasDictionary1 = rdNcFile1.findGlobalAttribute(cNetCDF.Attributes.GLOB_HAS_DICTIONARY);
			Attribute hasDictionary2 = rdNcFile2.findGlobalAttribute(cNetCDF.Attributes.GLOB_HAS_DICTIONARY);
			if ((Integer) hasDictionary1.getNumericValue() == 1
					&& (Integer) hasDictionary2.getNumericValue() == 1)
			{
				rdMarkerSet1.fillInitLHMWithVariable(cNetCDF.Variables.VAR_MARKERS_BASES_DICT);
				wrComboSortedMarkerSetLHM.putAll(rdMarkerSet1.getMarkerIdSetLHM());
				rdMarkerSet2.fillInitLHMWithVariable(cNetCDF.Variables.VAR_MARKERS_BASES_DICT);
				wrComboSortedMarkerSetLHM.putAll(rdMarkerSet2.getMarkerIdSetLHM());

				Utils.saveCharLHMValueToWrMatrix(wrNcFile, wrComboSortedMarkerSetLHM, cNetCDF.Variables.VAR_MARKERS_BASES_DICT, cNetCDF.Strides.STRIDE_GT);
			}
			//</editor-fold>

			//<editor-fold defaultstate="collapsed/expanded" desc="GENOTYPE STRAND">
			rdMarkerSet1.fillInitLHMWithVariable(cNetCDF.Variables.VAR_GT_STRAND);
			wrComboSortedMarkerSetLHM.putAll(rdMarkerSet1.getMarkerIdSetLHM());
			rdMarkerSet2.fillInitLHMWithVariable(cNetCDF.Variables.VAR_GT_STRAND);
			wrComboSortedMarkerSetLHM.putAll(rdMarkerSet2.getMarkerIdSetLHM());

			Utils.saveCharLHMValueToWrMatrix(wrNcFile, wrComboSortedMarkerSetLHM, cNetCDF.Variables.VAR_GT_STRAND, 3);
			//</editor-fold>
			//</editor-fold>
			//</editor-fold>

			//<editor-fold defaultstate="collapsed" desc="GENOTYPES WRITER">
			// Get SampleId index from each Matrix
			Map<String, Object> rdSampleSetLHM2 = rdSampleSet2.getSampleIdSetLHM();
			Map<String, Object> wrSampleSetLHM = getSampleSetWithIndicesLHM(rdSampleSetLHM1, rdSampleSetLHM2);

			// Iterate through wrSampleSetLHM
			for (Object value : wrSampleSetLHM.values()) {
				int[] sampleIndices = (int[]) value; // position[rdPos matrix 1, rdPos matrix 2]

				// Read from Matrix1
				rdMarkerSet1.fillWith(cNetCDF.Defaults.DEFAULT_GT);
				rdMarkerSet1.fillGTsForCurrentSampleIntoInitLHM(sampleIndices[0]);

				// Read from Matrix2
				rdMarkerSet2.fillWith(cNetCDF.Defaults.DEFAULT_GT);
				rdMarkerSet2.fillGTsForCurrentSampleIntoInitLHM(sampleIndices[1]);

				// Fill wrSortedMingledMarkerLHM with matrix 1+2 Genotypes
				for (Map.Entry<String, Object> entry : wrComboSortedMarkerSetLHM.entrySet()) {
					String markerId = entry.getKey();
					byte[] genotype = cNetCDF.Defaults.DEFAULT_GT;
					if (rdMarkerSet1.getMarkerIdSetLHM().containsKey(markerId)) {
						genotype = (byte[]) rdMarkerSet1.getMarkerIdSetLHM().get(markerId);
					}
					if (rdMarkerSet2.getMarkerIdSetLHM().containsKey(markerId)) {
						genotype = (byte[]) rdMarkerSet2.getMarkerIdSetLHM().get(markerId);
					}

					entry.setValue(genotype);
				}

				// Write wrMarkerIdSetLHM to A3 ArrayChar and save to wrMatrix
				Utils.saveSingleSampleGTsToMatrix(wrNcFile, wrComboSortedMarkerSetLHM, sampleIndices[0]);
			}
			//</editor-fold>

			// CLOSE THE FILE AND BY THIS, MAKE IT READ-ONLY
			try {
				// GENOTYPE ENCODING
				ArrayChar.D2 guessedGTCodeAC = new ArrayChar.D2(1, 8);
				Index index = guessedGTCodeAC.getIndex();
				guessedGTCodeAC.setString(index.set(0, 0), rdMatrix1Metadata.getGenotypeEncoding());
				int[] origin = new int[]{0, 0};
				wrNcFile.write(cNetCDF.Variables.GLOB_GTENCODING, origin, guessedGTCodeAC);

				descSB.append("\nGenotype encoding: ");
				descSB.append(rdMatrix1Metadata.getGenotypeEncoding());
				DbManager db = ServiceLocator.getDbManager(cDBGWASpi.DB_DATACENTER);
				db.updateTable(cDBGWASpi.SCH_MATRICES,
						cDBMatrix.T_MATRICES,
						new String[]{cDBMatrix.f_DESCRIPTION},
						new Object[]{descSB.toString()},
						new String[]{cDBMatrix.f_ID},
						new Object[]{resultMatrixId});

				wrNcFile.close();
				rdNcFile1.close();
				rdNcFile2.close();

				// CHECK FOR MISMATCHES
				if (rdMatrix1Metadata.getGenotypeEncoding().equals(cNetCDF.Defaults.GenotypeEncoding.ACGT0.toString())
						|| rdMatrix1Metadata.getGenotypeEncoding().equals(cNetCDF.Defaults.GenotypeEncoding.O1234.toString()))
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

	private Map<String, Object> mingleAndSortMarkerSet() {

		// GET 1st MATRIX LHM WITH CHR AND POS
		Map<String, Object> workLHM = new LinkedHashMap<String, Object>();
		rdMarkerSet1.initFullMarkerIdSetLHM();
		rdMarkerSet2.initFullMarkerIdSetLHM();
		rdMarkerSet1.fillWith("");
		rdMarkerSet1.fillInitLHMWithVariable(cNetCDF.Variables.VAR_MARKERS_CHR);
		workLHM.putAll(rdMarkerSet1.getMarkerIdSetLHM());
		rdMarkerSet1.fillWith("");
		rdMarkerSet1.fillInitLHMWithVariable(cNetCDF.Variables.VAR_MARKERS_POS);
		for (Map.Entry<String, Object> entry : workLHM.entrySet()) {
			String markerId = entry.getKey();
			String chr = entry.getValue().toString();
			String pos = rdMarkerSet1.getMarkerIdSetLHM().get(markerId).toString();
			StringBuilder sbKey = new StringBuilder(chr);
			sbKey.append(cNetCDF.Defaults.TMP_SEPARATOR);
			sbKey.append(pos);
			sbKey.append(cNetCDF.Defaults.TMP_SEPARATOR);
			sbKey.append(markerId);
			entry.setValue(sbKey.toString());
		}
		if (rdMarkerSet1.getMarkerIdSetLHM() != null) {
			rdMarkerSet1.getMarkerIdSetLHM().clear();
		}

		// GET 2nd MATRIX LHM WITH CHR AND POS
		Map<String, Object> workLHM2 = new LinkedHashMap<String, Object>();
		rdMarkerSet2.fillWith("");
		rdMarkerSet2.fillInitLHMWithVariable(cNetCDF.Variables.VAR_MARKERS_CHR);
		workLHM2.putAll(rdMarkerSet2.getMarkerIdSetLHM());
		rdMarkerSet2.fillWith("");
		rdMarkerSet2.fillInitLHMWithVariable(cNetCDF.Variables.VAR_MARKERS_POS);
		for (Map.Entry<String, Object> entry : workLHM2.entrySet()) {
			String markerId = entry.getKey();
			String chr = entry.getValue().toString();
			String pos = rdMarkerSet2.getMarkerIdSetLHM().get(markerId).toString();
			StringBuilder sbKey = new StringBuilder(chr);
			sbKey.append(cNetCDF.Defaults.TMP_SEPARATOR);
			sbKey.append(pos);
			sbKey.append(cNetCDF.Defaults.TMP_SEPARATOR);
			sbKey.append(markerId);
			entry.setValue(sbKey.toString());
		}
		if (rdMarkerSet2.getMarkerIdSetLHM() != null) {
			rdMarkerSet2.getMarkerIdSetLHM().clear();
		}

		workLHM.putAll(workLHM2);

		// SORT MERGED LHM
		SortedMap<String, String> sortedMetadataTM = new TreeMap<String, String>(new ComparatorChrAutPosMarkerIdAsc());
		for (Map.Entry<String, Object> entry : workLHM.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue().toString();
			sortedMetadataTM.put((String)value, key);
		}
		if (workLHM != null) {
			workLHM.clear();
		}

		// PACKAGE IN AN LHM
		for (Map.Entry<String, String> entry : sortedMetadataTM.entrySet()) {
			String key = entry.getKey();
			String[] keyValues = key.split(cNetCDF.Defaults.TMP_SEPARATOR);
			Object[] markerInfo = new Object[2];
			markerInfo[0] = keyValues[0];  //=> chr
			markerInfo[1] = Integer.parseInt(keyValues[1]);  // => pos

			String markerId = entry.getValue();
			workLHM.put(markerId, markerInfo);
		}

		return workLHM;
	}

	private static Map<String, Object> getSampleSetWithIndicesLHM(Map<String, Object> sampleSetLHM1, Map<String, Object> sampleSetLHM2) {
		Map<String, Object> resultLHM = new LinkedHashMap<String, Object>();

		int rdPos = 0;
		for (String key : sampleSetLHM1.keySet()) {
			int[] position = new int[]{rdPos, 0}; // rdPos matrix 1
			resultLHM.put(key, position);
			rdPos++;
		}

		rdPos = 0;
		for (String key : sampleSetLHM2.keySet()) {
			// IF SAMPLE ALLREADY EXISTS IN MATRIX1 SUBSTITUTE VALUES WITH MATRIX2
			if (resultLHM.containsKey(key)) {
				int[] position = (int[]) resultLHM.get(key);
				position[1] = rdPos; //rdPos matrix 2
				resultLHM.put(key, position);
			}

			rdPos++;
		}

		return resultLHM;
	}

	private double[] checkForMismatches(int wrMatrixId) throws IOException, InvalidRangeException {
		double[] result = new double[2];

		wrMatrixMetadata = new MatrixMetadata(wrMatrixId);
		wrSampleSet = new SampleSet(wrMatrixMetadata.getStudyId(), wrMatrixId);
		wrMarkerSet = new MarkerSet_opt(wrMatrixMetadata.getStudyId(), wrMatrixId);
		wrMarkerSet.initFullMarkerIdSetLHM();
		Map<String, Object> wrSampleSetLHM = wrSampleSet.getSampleIdSetLHM();

		NetcdfFile rdNcFile = NetcdfFile.open(wrMatrixMetadata.getPathToMatrix());

		// Iterate through markerset, take it marker by marker
		int markerNb = 0;
		double mismatchCount = 0;

		// Iterate through markerSet
		for (String markerId : wrMarkerSet.getMarkerIdSetLHM().keySet()) {
			Map<Character, Object> knownAlleles = new LinkedHashMap<Character, Object>();

			// Get a sampleset-full of GTs
			wrSampleSetLHM = wrSampleSet.readAllSamplesGTsFromCurrentMarkerToLHM(rdNcFile, wrSampleSetLHM, markerNb);

			// Iterate through sampleSet
			for (Object value : wrSampleSetLHM.values()) {
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
				log.info("Checking markers for mismatches: {} at {}", markerNb, org.gwaspi.global.Utils.getMediumDateTimeAsString()); // FIXME log system already adds time
			}
		}

		double mismatchRatio = mismatchCount / wrSampleSet.getSampleSetSize();
		result[0] = mismatchCount;
		result[1] = mismatchRatio;

		return result;
	}
}
