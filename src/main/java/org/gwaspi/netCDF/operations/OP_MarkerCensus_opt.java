package org.gwaspi.netCDF.operations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.gwaspi.constants.cImport;
import org.gwaspi.constants.cImport.Annotation.GWASpi;
import org.gwaspi.constants.cNetCDF;
import org.gwaspi.constants.cNetCDF.Defaults.AlleleBytes;
import org.gwaspi.global.Text;
import org.gwaspi.model.MarkerKey;
import org.gwaspi.model.MatricesList;
import org.gwaspi.model.MatrixMetadata;
import org.gwaspi.model.Operation;
import org.gwaspi.model.OperationMetadata;
import org.gwaspi.model.OperationsList;
import org.gwaspi.model.SampleInfo;
import org.gwaspi.model.SampleInfoList;
import org.gwaspi.model.SampleKey;
import org.gwaspi.netCDF.markers.MarkerSet_opt;
import org.gwaspi.samples.SampleSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ucar.ma2.ArrayChar;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFileWriteable;

/**
 *
 * @author Fernando Muñiz Fernandez
 * IBE, Institute of Evolutionary Biology (UPF-CSIC)
 * CEXS-UPF-PRBB
 */
public class OP_MarkerCensus_opt implements MatrixOperation {

	private final Logger log = LoggerFactory.getLogger(OP_MarkerCensus_opt.class);

	private int rdMatrixId;
	private String censusName;
	private Operation sampleQAOP;
	private double sampleMissingRatio;
	private double sampleHetzygRatio;
	private Operation markerQAOP;
	private boolean discardMismatches;
	private double markerMissingRatio;
	private File phenoFile;

	public OP_MarkerCensus_opt(
			int rdMatrixId,
			String censusName,
			Operation sampleQAOP,
			double sampleMissingRatio,
			double sampleHetzygRatio,
			Operation markerQAOP,
			boolean discardMismatches,
			double markerMissingRatio,
			File phenoFile)
	{
		this.rdMatrixId = rdMatrixId;
		this.censusName = censusName;
		this.sampleQAOP = sampleQAOP;
		this.sampleMissingRatio = sampleMissingRatio;
		this.sampleHetzygRatio = sampleHetzygRatio;
		this.markerQAOP = markerQAOP;
		this.discardMismatches = discardMismatches;
		this.markerMissingRatio = markerMissingRatio;
		this.phenoFile = phenoFile;
	}

	public int processMatrix() throws IOException, InvalidRangeException {
		int resultOpId = Integer.MIN_VALUE;

//		Map wrMarkerSetCensusMap = new LinkedHashMap();
//		Map wrMarkerSetKnownAllelesMap = new LinkedHashMap();

		//<editor-fold defaultstate="expanded" desc="PICKING CLEAN MARKERS AND SAMPLES FROM QA">
		OperationMetadata markerQAMetadata = OperationsList.getOperationMetadata(markerQAOP.getId());
		NetcdfFile rdMarkerQANcFile = NetcdfFile.open(markerQAMetadata.getPathToMatrix());

		OperationMetadata sampleQAMetadata = OperationsList.getOperationMetadata(sampleQAOP.getId());
		NetcdfFile rdSampleQANcFile = NetcdfFile.open(sampleQAMetadata.getPathToMatrix());

		MarkerOperationSet rdQAMarkerSet = new MarkerOperationSet(markerQAMetadata.getStudyId(), markerQAMetadata.getOPId());
		SampleOperationSet rdQASampleSet = new SampleOperationSet(sampleQAMetadata.getStudyId(), sampleQAMetadata.getOPId());
		Map<MarkerKey, Object> rdQAMarkerSetMap = rdQAMarkerSet.getOpSetMap();
		Map<SampleKey, Object> rdQASampleSetMap = rdQASampleSet.getOpSetMap();
		Map<MarkerKey, Object> excludeMarkerSetMap = new LinkedHashMap<MarkerKey, Object>();
		Map<SampleKey, Object> excludeSampleSetMap = new LinkedHashMap<SampleKey, Object>();

		int totalSampleNb = rdQASampleSetMap.size();
		int totalMarkerNb = rdQAMarkerSetMap.size();

		// EXCLUDE MARKER BY MISMATCH STATE
		if (discardMismatches) {
			rdQAMarkerSetMap = rdQAMarkerSet.fillOpSetMapWithVariable(rdMarkerQANcFile, cNetCDF.Census.VAR_OP_MARKERS_MISMATCHSTATE);

			for (Map.Entry<MarkerKey, Object> entry : rdQAMarkerSetMap.entrySet()) {
				MarkerKey key = entry.getKey();
				Object value = entry.getValue();
				if (value.equals(cNetCDF.Defaults.DEFAULT_MISMATCH_YES)) {
					excludeMarkerSetMap.put(key, value);
				}
			}
		}

		// EXCLUDE MARKER BY MISSING RATIO
		rdQAMarkerSetMap = rdQAMarkerSet.fillOpSetMapWithVariable(rdMarkerQANcFile, cNetCDF.Census.VAR_OP_MARKERS_MISSINGRAT);

		for (Map.Entry<MarkerKey, Object> entry : rdQAMarkerSetMap.entrySet()) {
			MarkerKey key = entry.getKey();
			double value = (Double) entry.getValue();
			if (value > markerMissingRatio) {
				excludeMarkerSetMap.put(key, value);
			}
		}

		// EXCLUDE SAMPLE BY MISSING RATIO
		rdQASampleSetMap = rdQASampleSet.fillOpSetMapWithVariable(rdSampleQANcFile, cNetCDF.Census.VAR_OP_SAMPLES_MISSINGRAT);

		if (rdQASampleSetMap != null) {
			int brgl = 0;
			for (Map.Entry<SampleKey, Object> entry : rdQASampleSetMap.entrySet()) {
				SampleKey key = entry.getKey();
				double value = (Double) entry.getValue();
				if (value > sampleMissingRatio) {
					excludeSampleSetMap.put(key, value);
				}
				brgl++;
			}
		}

		// EXCLUDE SAMPLE BY HETEROZYGOSITY RATIO
		rdQASampleSetMap = rdQASampleSet.fillOpSetMapWithVariable(rdSampleQANcFile, cNetCDF.Census.VAR_OP_SAMPLES_HETZYRAT);

		if (rdQASampleSetMap != null) {
			int brgl = 0;
			for (Map.Entry<SampleKey, Object> entry : rdQASampleSetMap.entrySet()) {
				SampleKey key = entry.getKey();
				double value = (Double) entry.getValue();
				if (value > sampleHetzygRatio) {
					excludeSampleSetMap.put(key, value);
				}
				brgl++;
			}
		}

		if (rdQAMarkerSetMap != null) {
			rdQAMarkerSetMap.clear();
		}
		if (rdQASampleSetMap != null) {
			rdQASampleSetMap.clear();
		}
		rdSampleQANcFile.close();
		rdMarkerQANcFile.close();
		//</editor-fold>

		if (excludeSampleSetMap.size() < totalSampleNb
				&& excludeMarkerSetMap.size() < totalMarkerNb)
		{
			// CHECK IF THERE IS ANY DATA LEFT TO PROCESS AFTER PICKING

			//<editor-fold defaultstate="expanded" desc="PURGE Maps">
			MatrixMetadata rdMatrixMetadata = MatricesList.getMatrixMetadataById(rdMatrixId);

			NetcdfFile rdNcFile = NetcdfFile.open(rdMatrixMetadata.getPathToMatrix());

			MarkerSet_opt rdMarkerSet = new MarkerSet_opt(rdMatrixMetadata.getStudyId(), rdMatrixId);
			rdMarkerSet.initFullMarkerIdSetMap();
			rdMarkerSet.fillWith(cNetCDF.Defaults.DEFAULT_GT);

			Map<MarkerKey, Object> wrMarkerSetMap = new LinkedHashMap<MarkerKey, Object>();
			wrMarkerSetMap.putAll(rdMarkerSet.getMarkerIdSetMap());

			SampleSet rdSampleSet = new SampleSet(rdMatrixMetadata.getStudyId(), rdMatrixId);
			Map<SampleKey, Object> rdSampleSetMap = rdSampleSet.getSampleIdSetMap();
			Map<SampleKey, Object> wrSampleSetMap = new LinkedHashMap<SampleKey, Object>();
			for (SampleKey key : rdSampleSetMap.keySet()) {
				if (!excludeSampleSetMap.containsKey(key)) {
					wrSampleSetMap.put(key, cNetCDF.Defaults.DEFAULT_GT);
				}
			}
			//</editor-fold>

			NetcdfFileWriteable wrNcFile = null;
			try {
				// CREATE netCDF-3 FILE
				cNetCDF.Defaults.OPType opType = cNetCDF.Defaults.OPType.MARKER_CENSUS_BY_AFFECTION;

				String description = "Genotype frequency count -" + censusName + "- on " + rdMatrixMetadata.getMatrixFriendlyName();
				if (phenoFile != null) {
					description += "\nCase/Control status read from file: " + phenoFile.getPath();
					opType = cNetCDF.Defaults.OPType.MARKER_CENSUS_BY_PHENOTYPE;
				}
				OperationFactory wrOPHandler = new OperationFactory(rdMatrixMetadata.getStudyId(),
						"Genotypes freq. - " + censusName, // friendly name
						description + "\nSample missing ratio threshold: " + sampleMissingRatio + "\nSample heterozygosity ratio threshold: " + sampleHetzygRatio + "\nMarker missing ratio threshold: " + markerMissingRatio + "\nDiscard mismatching Markers: " + discardMismatches + "\nMarkers: " + wrMarkerSetMap.size() + "\nSamples: " + wrSampleSetMap.size(), // description
						wrMarkerSetMap.size(),
						wrSampleSetMap.size(),
						0,
						opType.toString(),
						rdMatrixMetadata.getMatrixId(), // Parent matrixId
						-1); // Parent operationId

				wrNcFile = wrOPHandler.getNetCDFHandler();
				try {
					wrNcFile.create();
				} catch (IOException ex) {
					log.error("Failed creating file: " + wrNcFile.getLocation(), ex);
				}

				//<editor-fold defaultstate="expanded" desc="METADATA WRITER">
				// MARKERSET MARKERID
				ArrayChar.D2 markersD2 = Utils.writeMapKeysToD2ArrayChar(wrMarkerSetMap, cNetCDF.Strides.STRIDE_MARKER_NAME);
				int[] markersOrig = new int[]{0, 0};
				try {
					wrNcFile.write(cNetCDF.Variables.VAR_OPSET, markersOrig, markersD2);
				} catch (IOException ex) {
					log.error("Failed writing file: " + wrNcFile.getLocation(), ex);
				} catch (InvalidRangeException ex) {
					log.error(null, ex);
				}

				// MARKERSET RSID
				rdMarkerSet.fillInitMapWithVariable(cNetCDF.Variables.VAR_MARKERS_RSID);
				for (Map.Entry<MarkerKey, Object> entry : wrMarkerSetMap.entrySet()) {
					MarkerKey key = entry.getKey();
					Object value = rdMarkerSet.getMarkerIdSetMap().get(key);
					entry.setValue(value);
				}
				Utils.saveCharMapValueToWrMatrix(wrNcFile, wrMarkerSetMap, cNetCDF.Variables.VAR_MARKERS_RSID, cNetCDF.Strides.STRIDE_MARKER_NAME);

				// WRITE SAMPLESET TO MATRIX FROM SAMPLES ARRAYLIST
				ArrayChar.D2 samplesD2 = org.gwaspi.netCDF.operations.Utils.writeMapKeysToD2ArrayChar(wrSampleSetMap, cNetCDF.Strides.STRIDE_SAMPLE_NAME);

				int[] sampleOrig = new int[]{0, 0};
				try {
					wrNcFile.write(cNetCDF.Variables.VAR_IMPLICITSET, sampleOrig, samplesD2);
				} catch (IOException ex) {
					log.error("Failed writing file: " + wrNcFile.getLocation(), ex);
				} catch (InvalidRangeException ex) {
					log.error(null, ex);
				}
				log.info("Done writing Sample Set to operation");
				//</editor-fold>

				//<editor-fold defaultstate="expanded" desc="PROCESSOR">
				//<editor-fold defaultstate="expanded" desc="GET SAMPLES INFO">
				Map<SampleKey, Object> samplesInfoMap; // XXX change to Collection<SampleInfo>?
				List<SampleInfo> sampleInfos = SampleInfoList.getAllSampleInfoFromDBByPoolID(rdMatrixMetadata.getStudyId());
				if (phenoFile == null) {
					samplesInfoMap = new LinkedHashMap<SampleKey, Object>();
					for (SampleInfo sampleInfo : sampleInfos) {
						SampleKey tempSampleKey = sampleInfo.getKey();
						if (wrSampleSetMap.containsKey(tempSampleKey)) {
							String sex = sampleInfo.getSexStr();
							String affection = sampleInfo.getAffectionStr();
							String[] info = new String[]{sex, affection};
							samplesInfoMap.put(tempSampleKey, info);

							//samplesInfoMap.put(tempSampleId, affection);
						}
					}
				} else {
					FileReader phenotypeFR = new FileReader(phenoFile); // Pheno file has SampleInfo format!
					BufferedReader phenotypeBR = new BufferedReader(phenotypeFR);
					samplesInfoMap = new LinkedHashMap<SampleKey, Object>();

					String header = phenotypeBR.readLine(); // ignore header block
					String l;
					while ((l = phenotypeBR.readLine()) != null) {

						String[] cVals = l.split(cImport.Separators.separators_CommaSpaceTab_rgxp);
						String[] info = new String[]{cVals[GWASpi.sex], cVals[GWASpi.affection]};
						samplesInfoMap.put(new SampleKey(cVals[GWASpi.sampleId], cVals[GWASpi.familyId]), info);

						//samplesInfoMap.put(cVals[0], cVals[1]);
					}
					// CHECK IF THERE ARE MISSING SAMPLES IN THE PHENO PHILE
					for (SampleKey sampleKey : wrSampleSetMap.keySet()) {
						if (!samplesInfoMap.containsKey(sampleKey)) {
							String sex = "0";
							String affection = "0";
							int count = 0;
							for (SampleInfo sampleInfo : sampleInfos) {
								SampleKey tmpSampleKey = sampleInfo.getKey();
								if (tmpSampleKey.equals(sampleKey)) {
									sex = sampleInfo.getSexStr();
									affection = sampleInfo.getAffectionStr();
									break;
								}
							}
							String[] info = new String[]{sex, affection};
							samplesInfoMap.put(sampleKey, info);
						}
					}

				}
				//</editor-fold>

				// Iterate through markerset, take it marker by marker
				rdMarkerSet.fillInitMapWithVariable(cNetCDF.Variables.VAR_MARKERS_CHR);
				// INIT wrSampleSetMap with indexing order and chromosome info
				if (rdMarkerSet.getMarkerIdSetMap() != null) {
					int idx = 0;
					for (Map.Entry<MarkerKey, Object> entry : rdMarkerSet.getMarkerIdSetMap().entrySet()) {
						MarkerKey key = entry.getKey();
						if (wrMarkerSetMap.containsKey(key)) {
							String chr = entry.getValue().toString();
							Object[] markerInfo = new Object[]{idx, chr};
							wrMarkerSetMap.put(key, markerInfo);
						}
						//rdMarkerIdSetIndex.put(key, idx);
						idx++;
					}

					rdMarkerSet.getMarkerIdSetMap().clear();
				}

				log.info(Text.All.processing);

				int countMarkers = 0;
				int chunkSize = Math.round((float)org.gwaspi.gui.StartGWASpi.maxProcessMarkers / 4);
				if (chunkSize > 500000) {
					chunkSize = 500000; // We want to keep things manageable for RAM
				}
				if (chunkSize < 10000 && org.gwaspi.gui.StartGWASpi.maxProcessMarkers > 10000) {
					chunkSize = 10000; // But keep Map size sensible
				}
				int countChunks = 0;

				Map<MarkerKey, Object> wrChunkedMarkerCensusMap = new LinkedHashMap<MarkerKey, Object>();
				Map<MarkerKey, Object> wrChunkedKnownAllelesMap = new LinkedHashMap<MarkerKey, Object>();
				for (Map.Entry<MarkerKey, Object> entry : wrMarkerSetMap.entrySet()) {
					MarkerKey markerKey = entry.getKey();
					if (countMarkers % chunkSize == 0) {

						if (countMarkers > 0) {

							//<editor-fold defaultstate="expanded" desc="CENSUS DATA WRITER">
							// KNOWN ALLELES
							Utils.saveCharChunkedMapToWrMatrix(wrNcFile,
									wrChunkedKnownAllelesMap,
									cNetCDF.Variables.VAR_ALLELES,
									cNetCDF.Strides.STRIDE_GT,
									countChunks * chunkSize);

							// ALL CENSUS
							int[] columns = new int[]{0, 1, 2, 3};
							Utils.saveIntChunkedMapD2ToWrMatrix(wrNcFile,
									wrChunkedMarkerCensusMap,
									columns,
									cNetCDF.Census.VAR_OP_MARKERS_CENSUSALL,
									countChunks * chunkSize);

							// CASE CENSUS
							columns = new int[]{4, 5, 6};
							Utils.saveIntChunkedMapD2ToWrMatrix(wrNcFile,
									wrChunkedMarkerCensusMap,
									columns,
									cNetCDF.Census.VAR_OP_MARKERS_CENSUSCASE,
									countChunks * chunkSize);

							// CONTROL CENSUS
							columns = new int[]{7, 8, 9};
							Utils.saveIntChunkedMapD2ToWrMatrix(wrNcFile,
									wrChunkedMarkerCensusMap,
									columns,
									cNetCDF.Census.VAR_OP_MARKERS_CENSUSCTRL,
									countChunks * chunkSize);

							// ALTERNATE HW CENSUS
							columns = new int[]{10, 11, 12};
							Utils.saveIntChunkedMapD2ToWrMatrix(wrNcFile,
									wrChunkedMarkerCensusMap,
									columns,
									cNetCDF.Census.VAR_OP_MARKERS_CENSUSHW,
									countChunks * chunkSize);
							//</editor-fold>

							countChunks++;
						}
						wrChunkedMarkerCensusMap = new LinkedHashMap<MarkerKey, Object>();
						wrChunkedKnownAllelesMap = new LinkedHashMap<MarkerKey, Object>();
						System.gc(); // Try to garbage collect here
					}
					wrChunkedMarkerCensusMap.put(markerKey, "");
					countMarkers++;

					Map<Byte, Float> knownAlleles = new LinkedHashMap<Byte, Float>();
					Map<Integer, Float> allSamplesGTsTable = new LinkedHashMap<Integer, Float>();
					Map<Integer, Float> caseSamplesGTsTable = new LinkedHashMap<Integer, Float>();
					Map<Integer, Float> ctrlSamplesGTsTable = new LinkedHashMap<Integer, Float>();
					Map<Integer, Float> hwSamplesGTsTable = new LinkedHashMap<Integer, Float>();
					Map<String, Integer> allSamplesContingencyTable = new LinkedHashMap<String, Integer>();
					Map<String, Integer> caseSamplesContingencyTable = new LinkedHashMap<String, Integer>();
					Map<String, Integer> ctrlSamplesContingencyTable = new LinkedHashMap<String, Integer>();
					Map<String, Integer> hwSamplesContingencyTable = new LinkedHashMap<String, Integer>();
					Integer missingCount = 0;

					// Get a sampleset-full of GTs
					//int markerNb = rdMarkerIdSetIndex.get(markerId);
					Object[] markerInfo = (Object[]) entry.getValue();
					int markerNb = Integer.parseInt(markerInfo[0].toString());
					String markerChr = markerInfo[1].toString();

					rdSampleSetMap = rdSampleSet.readAllSamplesGTsFromCurrentMarkerToMap(rdNcFile, rdSampleSetMap, markerNb);
					for (SampleKey sampleKey : wrSampleSetMap.keySet()) {
						String[] sampleInfo = (String[]) samplesInfoMap.get(sampleKey);

						//<editor-fold defaultstate="expanded" desc="THE DECIDER">
						CensusDecision decision = CensusDecision.getDecisionByChrAndSex(markerChr, sampleInfo[0]);

						float counter = 1;
//						if(decision == CensusDecision.CountMalesNonAutosomally){
//							counter = 0.5f;
//						}
						//</editor-fold>

						//<editor-fold defaultstate="expanded" desc="SUMMING SAMPLESET GENOTYPES">
						byte[] tempGT = (byte[]) rdSampleSetMap.get(sampleKey);
						// Gather alleles different from 0 into a list of known alleles and count the number of appearences
						if (tempGT[0] != AlleleBytes._0) {
							float tempCount = 0;
							if (knownAlleles.containsKey(tempGT[0])) {
								tempCount = knownAlleles.get(tempGT[0]);
							}
							knownAlleles.put(tempGT[0], tempCount + counter);
						}
						if (tempGT[1] != AlleleBytes._0) {
							float tempCount = 0;
							if (knownAlleles.containsKey(tempGT[1])) {
								tempCount = knownAlleles.get(tempGT[1]);
							}
							knownAlleles.put(tempGT[1], tempCount + counter);
						}
						if (tempGT[0] == AlleleBytes._0 && tempGT[1] == AlleleBytes._0) {
							missingCount++;
						}

						int intAllele1 = tempGT[0];
						int intAllele2 = tempGT[1];
						Integer intAlleleSum = intAllele1 + intAllele2; // 2 alleles per GT

						// CASE/CONTROL CENSUS
						float tempCount = 0;
						if (allSamplesGTsTable.containsKey(intAlleleSum)) {
							tempCount = allSamplesGTsTable.get(intAlleleSum);
						}
						allSamplesGTsTable.put(intAlleleSum, tempCount + counter);

						//if(affection.equals("2")){ // CASE
						if (sampleInfo[1].equals("2")) { // CASE
							tempCount = 0;
							if (caseSamplesGTsTable.containsKey(intAlleleSum)) {
								tempCount = caseSamplesGTsTable.get(intAlleleSum);
							}
							caseSamplesGTsTable.put(intAlleleSum, tempCount + counter);
						} else if (sampleInfo[1].equals("1")) { // CONTROL
							tempCount = 0;
							if (ctrlSamplesGTsTable.containsKey(intAlleleSum)) {
								tempCount = ctrlSamplesGTsTable.get(intAlleleSum);
							}
							ctrlSamplesGTsTable.put(intAlleleSum, tempCount + counter);

							// HARDY WEINBERG COUNTER
							if (hwSamplesGTsTable.containsKey(intAlleleSum)) {
								tempCount = hwSamplesGTsTable.get(intAlleleSum);
							}
							if (decision == CensusDecision.CountMalesNonAutosomally) {
								hwSamplesGTsTable.put(intAlleleSum, tempCount);
							}
							if (decision == CensusDecision.CountFemalesNonAutosomally) {
								hwSamplesGTsTable.put(intAlleleSum, tempCount);
							}
							if (decision == CensusDecision.CountAutosomally) {
								hwSamplesGTsTable.put(intAlleleSum, tempCount + counter);
							}
						}
						//</editor-fold>
					}

					// AFFECTION ALLELE CENSUS + MISMATCH STATE + MISSINGNESS
					if (knownAlleles.size() <= 2) {
						// Check if there are mismatches in alleles

						//<editor-fold defaultstate="expanded" desc="KNOW YOUR ALLELES">
						List<Integer> AAnumValsAL = new ArrayList<Integer>();
						List<Integer> AanumValsAL = new ArrayList<Integer>();
						List<Integer> aanumValsAL = new ArrayList<Integer>();

						Iterator<Byte> itKnAll = knownAlleles.keySet().iterator();
						if (knownAlleles.size() == 1) {
							// Homozygote (AA or aa)
							byte key = itKnAll.next();
							int intAllele1 = (int) key;
							AAnumValsAL.add(intAllele1); // Single A
							AAnumValsAL.add(intAllele1 * 2); // Double AA
						}
						if (knownAlleles.size() == 2) {
							// Heterezygote (AA, Aa or aa)
							byte key = itKnAll.next();
							int countA = Math.round(knownAlleles.get(key));
							int intAllele1 = (int) key;
							key = itKnAll.next();
							int countB = Math.round(knownAlleles.get(key));
							int intAllele2 = (int) key;

							if (countA >= countB) {
								// Finding out what allele is major and minor
								AAnumValsAL.add(intAllele1);
								AAnumValsAL.add(intAllele1 * 2);

								aanumValsAL.add(intAllele2);
								aanumValsAL.add(intAllele2 * 2);

								AanumValsAL.add(intAllele1 + intAllele2);
							} else {
								AAnumValsAL.add(intAllele2);
								AAnumValsAL.add(intAllele2 * 2);

								aanumValsAL.add(intAllele1);
								aanumValsAL.add(intAllele1 * 2);

								AanumValsAL.add(intAllele1 + intAllele2);
							}
						}
						//</editor-fold>

						//<editor-fold defaultstate="expanded" desc="CONTINGENCY ALL SAMPLES">
						for (Map.Entry<Integer, Float> samplesEntry : allSamplesGTsTable.entrySet()) {
							Integer key = samplesEntry.getKey();
							Integer value = Math.round(samplesEntry.getValue());

							if (AAnumValsAL.contains(key)) {
								// compare to all possible character values of AA
								// ALL CENSUS
								int tempCount = 0;
								if (allSamplesContingencyTable.containsKey("AA")) {
									tempCount = allSamplesContingencyTable.get("AA");
								}
								allSamplesContingencyTable.put("AA", tempCount + value);
							}
							if (AanumValsAL.contains(key)) {
								// compare to all possible character values of Aa
								// ALL CENSUS
								int tempCount = 0;
								if (allSamplesContingencyTable.containsKey("Aa")) {
									tempCount = allSamplesContingencyTable.get("Aa");
								}
								allSamplesContingencyTable.put("Aa", tempCount + value);
							}
							if (aanumValsAL.contains(key)) {
								// compare to all possible character values of aa
								// ALL CENSUS
								int tempCount = 0;
								if (allSamplesContingencyTable.containsKey("aa")) {
									tempCount = allSamplesContingencyTable.get("aa");
								}
								allSamplesContingencyTable.put("aa", tempCount + value);
							}
						}
						//</editor-fold>

						//<editor-fold defaultstate="expanded" desc="CONTINGENCY CASE SAMPLES">
						for (Map.Entry<Integer, Float> samplesEntry : caseSamplesGTsTable.entrySet()) {
							Integer key = samplesEntry.getKey();
							Integer value = Math.round(samplesEntry.getValue());

							if (AAnumValsAL.contains(key)) {
								// compare to all possible character values of AA
								// ALL CENSUS
								int tempCount = 0;
								if (caseSamplesContingencyTable.containsKey("AA")) {
									tempCount = caseSamplesContingencyTable.get("AA");
								}
								caseSamplesContingencyTable.put("AA", tempCount + value);
							}
							if (AanumValsAL.contains(key)) {
								// compare to all possible character values of Aa
								// ALL CENSUS
								int tempCount = 0;
								if (caseSamplesContingencyTable.containsKey("Aa")) {
									tempCount = caseSamplesContingencyTable.get("Aa");
								}
								caseSamplesContingencyTable.put("Aa", tempCount + value);
							}
							if (aanumValsAL.contains(key)) {
								// compare to all possible character values of aa
								// ALL CENSUS
								int tempCount = 0;
								if (caseSamplesContingencyTable.containsKey("aa")) {
									tempCount = caseSamplesContingencyTable.get("aa");
								}
								caseSamplesContingencyTable.put("aa", tempCount + value);
							}
						}
						//</editor-fold>

						//<editor-fold defaultstate="expanded" desc="CONTINGENCY CTRL SAMPLES">
						for (Map.Entry<Integer, Float> samplesEntry : ctrlSamplesGTsTable.entrySet()) {
							Integer key = samplesEntry.getKey();
							Integer value = Math.round(samplesEntry.getValue());

							if (AAnumValsAL.contains(key)) {
								// compare to all possible character values of AA
								// ALL CENSUS
								int tempCount = 0;
								if (ctrlSamplesContingencyTable.containsKey("AA")) {
									tempCount = ctrlSamplesContingencyTable.get("AA");
								}
								ctrlSamplesContingencyTable.put("AA", tempCount + value);
							}
							if (AanumValsAL.contains(key)) {
								// compare to all possible character values of Aa
								// ALL CENSUS
								int tempCount = 0;
								if (ctrlSamplesContingencyTable.containsKey("Aa")) {
									tempCount = ctrlSamplesContingencyTable.get("Aa");
								}
								ctrlSamplesContingencyTable.put("Aa", tempCount + value);
							}
							if (aanumValsAL.contains(key)) {
								// compare to all possible character values of aa
								// ALL CENSUS
								int tempCount = 0;
								if (ctrlSamplesContingencyTable.containsKey("aa")) {
									tempCount = ctrlSamplesContingencyTable.get("aa");
								}
								ctrlSamplesContingencyTable.put("aa", tempCount + value);
							}
						}
						//</editor-fold>

						//<editor-fold defaultstate="expanded" desc="CONTINGENCY HW SAMPLES">
						for (Map.Entry<Integer, Float> samplesEntry : hwSamplesGTsTable.entrySet()) {
							Integer key = samplesEntry.getKey();
							Integer value = Math.round(samplesEntry.getValue());

							if (AAnumValsAL.contains(key)) {
								// compare to all possible character values of AA
								// HW CENSUS
								int tempCount = 0;
								if (hwSamplesContingencyTable.containsKey("AA")) {
									tempCount = hwSamplesContingencyTable.get("AA");
								}
								hwSamplesContingencyTable.put("AA", tempCount + value);
							}
							if (AanumValsAL.contains(key)) {
								// compare to all possible character values of Aa
								// HW CENSUS
								int tempCount = 0;
								if (hwSamplesContingencyTable.containsKey("Aa")) {
									tempCount = hwSamplesContingencyTable.get("Aa");
								}
								hwSamplesContingencyTable.put("Aa", tempCount + value);
							}
							if (aanumValsAL.contains(key)) {
								// compare to all possible character values of aa
								// HW CENSUS
								int tempCount = 0;
								if (hwSamplesContingencyTable.containsKey("aa")) {
									tempCount = hwSamplesContingencyTable.get("aa");
								}
								hwSamplesContingencyTable.put("aa", tempCount + value);
							}
						}
						//</editor-fold>

						// CENSUS
						int obsAllAA = 0;
						int obsAllAa = 0;
						int obsAllaa = 0;
						int obsCaseAA = 0;
						int obsCaseAa = 0;
						int obsCaseaa = 0;
						int obsCntrlAA = 0;
						int obsCntrlAa = 0;
						int obsCntrlaa = 0;
						int obsHwAA = 0;
						int obsHwAa = 0;
						int obsHwaa = 0;
						if (allSamplesContingencyTable.containsKey("AA")) {
							obsAllAA = allSamplesContingencyTable.get("AA");
						}
						if (allSamplesContingencyTable.containsKey("Aa")) {
							obsAllAa = allSamplesContingencyTable.get("Aa");
						}
						if (allSamplesContingencyTable.containsKey("aa")) {
							obsAllaa = allSamplesContingencyTable.get("aa");
						}
						if (caseSamplesContingencyTable.containsKey("AA")) {
							obsCaseAA = caseSamplesContingencyTable.get("AA");
						}
						if (caseSamplesContingencyTable.containsKey("Aa")) {
							obsCaseAa = caseSamplesContingencyTable.get("Aa");
						}
						if (caseSamplesContingencyTable.containsKey("aa")) {
							obsCaseaa = caseSamplesContingencyTable.get("aa");
						}
						if (ctrlSamplesContingencyTable.containsKey("AA")) {
							obsCntrlAA = ctrlSamplesContingencyTable.get("AA");
						}
						if (ctrlSamplesContingencyTable.containsKey("Aa")) {
							obsCntrlAa = ctrlSamplesContingencyTable.get("Aa");
						}
						if (ctrlSamplesContingencyTable.containsKey("aa")) {
							obsCntrlaa = ctrlSamplesContingencyTable.get("aa");
						}
						if (hwSamplesContingencyTable.containsKey("AA")) {
							obsHwAA = hwSamplesContingencyTable.get("AA");
						}
						if (hwSamplesContingencyTable.containsKey("Aa")) {
							obsHwAa = hwSamplesContingencyTable.get("Aa");
						}
						if (hwSamplesContingencyTable.containsKey("aa")) {
							obsHwaa = hwSamplesContingencyTable.get("aa");
						}

						int[] census = new int[13];

						census[0] = obsAllAA; // all
						census[1] = obsAllAa; // all
						census[2] = obsAllaa; // all
						census[3] = missingCount; // all
						census[4] = obsCaseAA; // case
						census[5] = obsCaseAa; // case
						census[6] = obsCaseaa; // case
						census[7] = obsCntrlAA; // control
						census[8] = obsCntrlAa; // control
						census[9] = obsCntrlaa; // control
						census[10] = obsHwAA; // HW samples
						census[11] = obsHwAa; // HW samples
						census[12] = obsHwaa; // HW samples

						wrChunkedMarkerCensusMap.put(markerKey, census);

						StringBuilder sb = new StringBuilder();
						byte[] alleles = cNetCDF.Defaults.DEFAULT_GT;
						Iterator<Byte> knit = knownAlleles.keySet().iterator();
						if (knownAlleles.size() == 2) {
							Byte allele1 = knit.next();
							Byte allele2 = knit.next();
							alleles = new byte[]{allele1, allele2};
						}
						if (knownAlleles.size() == 1) {
							Byte allele1 = knit.next();
							alleles = new byte[]{allele1, allele1};
						}
						sb.append(new String(alleles));

						wrChunkedKnownAllelesMap.put(markerKey, sb.toString());
					} else {
						// MISMATCHES FOUND
						int[] census = new int[10];
						wrChunkedMarkerCensusMap.put(markerKey, census);
						wrChunkedKnownAllelesMap.put(markerKey, "00");
					}

					if (markerNb != 0 && markerNb % 100000 == 0) {
						log.info("Processed markers: {}", markerNb);
					}
				}
				//</editor-fold>

				//<editor-fold defaultstate="expanded" desc="LAST CENSUS DATA WRITER">
				// KNOWN ALLELES
				Utils.saveCharChunkedMapToWrMatrix(wrNcFile,
						wrChunkedKnownAllelesMap,
						cNetCDF.Variables.VAR_ALLELES,
						cNetCDF.Strides.STRIDE_GT,
						countChunks * chunkSize);

				// ALL CENSUS
				int[] columns = new int[]{0, 1, 2, 3};
				Utils.saveIntChunkedMapD2ToWrMatrix(wrNcFile,
						wrChunkedMarkerCensusMap,
						columns,
						cNetCDF.Census.VAR_OP_MARKERS_CENSUSALL,
						countChunks * chunkSize);

				// CASE CENSUS
				columns = new int[]{4, 5, 6};
				Utils.saveIntChunkedMapD2ToWrMatrix(wrNcFile,
						wrChunkedMarkerCensusMap,
						columns,
						cNetCDF.Census.VAR_OP_MARKERS_CENSUSCASE,
						countChunks * chunkSize);

				// CONTROL CENSUS
				columns = new int[]{7, 8, 9};
				Utils.saveIntChunkedMapD2ToWrMatrix(wrNcFile,
						wrChunkedMarkerCensusMap,
						columns,
						cNetCDF.Census.VAR_OP_MARKERS_CENSUSCTRL,
						countChunks * chunkSize);

				// ALTERNATE HW CENSUS
				columns = new int[]{10, 11, 12};
				Utils.saveIntChunkedMapD2ToWrMatrix(wrNcFile,
						wrChunkedMarkerCensusMap,
						columns,
						cNetCDF.Census.VAR_OP_MARKERS_CENSUSHW,
						countChunks * chunkSize);
				//</editor-fold>

				resultOpId = wrOPHandler.getResultOPId();
			} catch (InvalidRangeException ex) {
				log.error(null, ex);
			} catch (IOException ex) {
				log.error(null, ex);
			} finally {
				if (null != rdNcFile) {
					try {
						rdNcFile.close();
						wrNcFile.close();
					} catch (IOException ex) {
						log.warn("Cannot close file", ex);
					}
				}

				org.gwaspi.global.Utils.sysoutCompleted("Genotype Frequency Count");
			}
		} else {
			// NO DATA LEFT AFTER THRESHOLD FILTER PICKING
			log.warn(Text.Operation.warnNoDataLeftAfterPicking);
		}

		return resultOpId;
	}
}
