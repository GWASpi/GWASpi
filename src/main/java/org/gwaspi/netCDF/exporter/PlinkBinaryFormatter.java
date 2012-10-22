package org.gwaspi.netCDF.exporter;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.gwaspi.constants.cExport;
import org.gwaspi.constants.cNetCDF;
import org.gwaspi.constants.cNetCDF.Defaults.OPType;
import org.gwaspi.model.MatrixMetadata;
import org.gwaspi.model.Operation;
import org.gwaspi.model.OperationsList;
import org.gwaspi.model.SampleInfo;
import org.gwaspi.netCDF.markers.MarkerSet_opt;
import org.gwaspi.reports.GatherQAMarkersData;
import org.gwaspi.samples.SampleSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ucar.nc2.NetcdfFile;

/**
 *
 * @author Fernando Muñiz Fernandez
 * IBE, Institute of Evolutionary Biology (UPF-CSIC)
 * CEXS-UPF-PRBB
 */
public class PlinkBinaryFormatter implements Formatter {

	private final Logger log = LoggerFactory.getLogger(PlinkBinaryFormatter.class);

	public boolean export(
			String exportPath,
			MatrixMetadata rdMatrixMetadata,
			MarkerSet_opt rdMarkerSet,
			SampleSet rdSampleSet,
			Map<String, Object> rdSampleSetMap,
			String phenotype)
			throws IOException
	{
		File exportDir = new File(exportPath);
		if (!exportDir.exists() || !exportDir.isDirectory()) {
			return false;
		}

		boolean result = false;
		NetcdfFile rdNcFile = NetcdfFile.open(rdMatrixMetadata.getPathToMatrix());
		String sep = cExport.separator_PLINK;

		//<editor-fold defaultstate="collapsed" desc="BIM FILE">
		String filePath = exportDir.getPath() + "/" + rdMatrixMetadata.getMatrixFriendlyName() + ".bim";
		FileWriter mapFW = new FileWriter(filePath);
		BufferedWriter mapBW = new BufferedWriter(mapFW);

		//BIM files
		//     chromosome (1-22, X(23), Y(24), XY(25), MT(26) or 0 if unplaced)
		//     rs# or snp identifier
		//     Genetic distance (morgans)
		//     Base-pair position (bp units)
		//     Allele 1
		//     Allele 2

		//PURGE MARKERSET
		rdMarkerSet.initFullMarkerIdSetMap();
		rdMarkerSet.fillWith("");

		//MARKERSET CHROMOSOME
		rdMarkerSet.fillInitMapWithVariable(cNetCDF.Variables.VAR_MARKERS_CHR);

		//MARKERSET RSID
		rdMarkerSet.appendVariableToMarkerSetMapValue(cNetCDF.Variables.VAR_MARKERS_RSID, sep);

		//DEFAULT GENETIC DISTANCE = 0
		for (Map.Entry<String, Object> entry : rdMarkerSet.getMarkerIdSetMap().entrySet()) {
			StringBuilder value = new StringBuilder(entry.getValue().toString());
			value.append(sep);
			value.append("0");
			entry.setValue(value.toString());
		}

		//MARKERSET POSITION
		rdMarkerSet.appendVariableToMarkerSetMapValue(cNetCDF.Variables.VAR_MARKERS_POS, sep);

		//ALLELES
		List<Operation> operations = OperationsList.getOperationsList(rdMatrixMetadata.getMatrixId());
		int markersQAOpId = OperationsList.getIdOfLastOperationTypeOccurance(operations, OPType.MARKER_QA);

		Map<String, Object> minorAllelesMap = GatherQAMarkersData.loadMarkerQAMinorAlleles(markersQAOpId);
		Map<String, Object> majorAllelesMap = GatherQAMarkersData.loadMarkerQAMajorAlleles(markersQAOpId);
		Map<String, Object> minorAlleleFreqMap = GatherQAMarkersData.loadMarkerQAMinorAlleleFrequency(markersQAOpId);
		for (Map.Entry<String, Object> entry : rdMarkerSet.getMarkerIdSetMap().entrySet()) {
			String key = entry.getKey();
			String minorAllele = minorAllelesMap.get(key).toString();
			String majorAllele = majorAllelesMap.get(key).toString();
			Double minAlleleFreq = (Double) minorAlleleFreqMap.get(key);

			if (minAlleleFreq == 0.5) { // IF BOTH ALLELES ARE EQUALLY COMMON, USE ALPHABETICAL ORDER
				String tmpMinorAllele = majorAllele;
				majorAllele = minorAllele;
				minorAllele = tmpMinorAllele;

				majorAllelesMap.put(key, majorAllele);
				minorAllelesMap.put(key, minorAllele);
			}

			Object values = entry.getValue();
			mapBW.append(values.toString());
			mapBW.append(sep);
			mapBW.append(minorAllele);
			mapBW.append(sep);
			mapBW.append(majorAllele);
			mapBW.append("\n");
		}


		mapBW.close();
		mapFW.close();
		org.gwaspi.global.Utils.sysoutCompleted("Completed exporting BIM file to " + filePath);

		//</editor-fold>

		//<editor-fold defaultstate="collapsed/expanded" desc="BED FILE">

		// THIS SHOULD BE MULTIPLE OF SAMPLE SET LENGTH
		int nbOfSamples = rdSampleSet.getSampleSetSize();
		int bytesPerSampleSet = ((int) Math.ceil((double) nbOfSamples / 8)) * 2;
		int nbOfMarkers = rdMarkerSet.getMarkerSetSize();
		int nbRowsPerChunk = Math.round(org.gwaspi.gui.StartGWASpi.maxProcessMarkers / nbOfSamples);
		if (nbRowsPerChunk > nbOfMarkers) {
			nbRowsPerChunk = nbOfMarkers;
		}
		int byteChunkSize = bytesPerSampleSet * nbRowsPerChunk;

		// Create an output stream to the file.
		filePath = exportDir.getPath() + "/" + rdMatrixMetadata.getMatrixFriendlyName() + ".bed";
		File bedFW = new File(filePath);
		FileOutputStream file_output = new FileOutputStream(bedFW);
		DataOutputStream data_out = new DataOutputStream(file_output);

//          |----magic number---|  |---mode--|  |----------genotype data-----------|
//          01101100 00011011   00000001   11011100 00001111 11100111
//                                              (SNP-major)

		data_out.writeByte(108);      //magic number
		data_out.writeByte(27);        //magic number
		data_out.writeByte(1);          //mode SNP-major


		int markerNb = 0;
		int byteCount = 0;
		byte[] wrBytes = new byte[byteChunkSize];
		// ITERATE THROUGH ALL MARKERS, ONE SAMPLESET AT A TIME
		for (String markerId : rdMarkerSet.getMarkerIdSetMap().keySet()) {
			String tmpMinorAllele = minorAllelesMap.get(markerId).toString();
			String tmpMajorAllele = majorAllelesMap.get(markerId).toString();

			// GET SAMPLESET FOR CURRENT MARKER
			Map<String, Object> remainingSampleSet = rdSampleSet.readAllSamplesGTsFromCurrentMarkerToMap(rdNcFile, rdSampleSetMap, markerNb);
			rdSampleSetMap = remainingSampleSet; // FIXME This line should most likely be removed, because further down this is used again ... check out!

			for (Iterator<String> rdSampleIds = remainingSampleSet.keySet().iterator(); rdSampleIds.hasNext();) {
				// ONE BYTE AT A TIME (4 SAMPLES)
				StringBuilder tetraGTs = new StringBuilder("");
				for (int i = 0; i < 4; i++) {
					if (rdSampleIds.hasNext()) {
						String sampleId = rdSampleIds.next();
						byte[] tempGT = (byte[]) remainingSampleSet.get(sampleId);
						byte[] translatedByte = translateTo00011011Byte(tempGT, tmpMinorAllele, tmpMajorAllele);
						tetraGTs.insert(0, translatedByte[0]); //REVERSE ORDER, AS PER PLINK SPECS http://pngu.mgh.harvard.edu/~purcell/plink/binary.shtml
						tetraGTs.insert(0, translatedByte[1]);
					}
				}

				int number = Integer.parseInt(tetraGTs.toString(), 2);
				byte[] tetraGT = new byte[]{(byte) number};

				System.arraycopy(tetraGT,
						0,
						wrBytes,
						byteCount,
						1);
				byteCount++;

				if (byteCount == byteChunkSize) {
					// WRITE TO FILE
					data_out.write(wrBytes, 0, byteChunkSize);

					// INIT NEW CHUNK
					wrBytes = new byte[byteChunkSize];
					byteCount = 0;
				}
			}

			markerNb++;
		}

		// WRITE LAST BITES TO FILE
		data_out.write(wrBytes, 0, byteCount);

		// Close file when finished with it..
		file_output.close();

		org.gwaspi.global.Utils.sysoutCompleted("Completed exporting BED file to " + filePath);
		//</editor-fold>

		//<editor-fold defaultstate="collapsed" desc="FAM FILE">
		filePath = exportDir.getPath() + "/" + rdMatrixMetadata.getMatrixFriendlyName() + ".fam";
		FileWriter tfamFW = new FileWriter(filePath);
		BufferedWriter tfamBW = new BufferedWriter(tfamFW);

		// Iterate through all samples
		int sampleNb = 0;
		for (String sampleId : rdSampleSetMap.keySet()) {
			SampleInfo sampleInfo = Utils.getCurrentSampleFormattedInfo(sampleId, rdMatrixMetadata.getStudyId());

			String familyId = sampleInfo.getFamilyId();
			String fatherId = sampleInfo.getFatherId();
			String motherId = sampleInfo.getMotherId();
			String sex = sampleInfo.getSexStr();
			String affection = sampleInfo.getAffectionStr();

			// FAM files
			// Family ID
			// Individual ID
			// Paternal ID
			// Maternal ID
			// Sex (1=male; 2=female; other=unknown)
			// Affection

			StringBuilder line = new StringBuilder();
			line.append(familyId);
			line.append(sep);
			line.append(sampleId);
			line.append(sep);
			line.append(fatherId);
			line.append(sep);
			line.append(motherId);
			line.append(sep);
			line.append(sex);
			line.append(sep);
			line.append(affection);


			tfamBW.append(line);
			tfamBW.append("\n");
			tfamBW.flush();

			sampleNb++;
			if (sampleNb % 100 == 0) {
				log.info("Samples exported: {}", sampleNb);
			}

		}
		tfamBW.close();
		tfamFW.close();
		org.gwaspi.global.Utils.sysoutCompleted("Completed exporting FAM file to " + filePath);
		//</editor-fold>

		return result;
	}

	private static byte[] translateTo00011011Byte(byte[] tempGT, String tmpMinorAllele, String tmpMajorAllele) {
		byte[] result;
		if (tempGT[0] == 48
				|| tempGT[1] == 48) {
			// SOME MISSING ALLELES => SET ALL TO MISSING
			result = new byte[]{1, 0};
		} else {
			String allele1 = new String(new byte[]{tempGT[0]});
			String allele2 = new String(new byte[]{tempGT[1]});

			if (allele1.equals(tmpMinorAllele)) {
				if (allele2.equals(tmpMinorAllele)) {
					// HOMOZYGOUS FOR MINOR ALLELE
					result = new byte[]{0, 0};
				} else {
					// HETEROZYGOUS
					result = new byte[]{0, 1};
				}
			} else {
				if (allele2.equals(tmpMajorAllele)) {
					// HOMOZYGOUS FOR MAJOR ALLELE
					result = new byte[]{1, 1};
				} else {
					// HETEROZYGOUS
					result = new byte[]{0, 1};
				}
			}
		}
		return result;
	}
}
