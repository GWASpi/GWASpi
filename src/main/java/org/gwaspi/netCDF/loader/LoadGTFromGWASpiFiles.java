package org.gwaspi.netCDF.loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.gwaspi.constants.cDBGWASpi;
import org.gwaspi.constants.cDBMatrix;
import org.gwaspi.constants.cImport;
import org.gwaspi.constants.cImport.ImportFormat;
import org.gwaspi.constants.cNetCDF;
import org.gwaspi.constants.cNetCDF.Defaults.GenotypeEncoding;
import org.gwaspi.constants.cNetCDF.Defaults.StrandType;
import org.gwaspi.database.DbManager;
import org.gwaspi.global.Config;
import org.gwaspi.global.ServiceLocator;
import org.gwaspi.global.Text;
import org.gwaspi.gui.utils.Dialogs;
import org.gwaspi.model.MatricesList;
import org.gwaspi.model.MatrixMetadata;
import org.gwaspi.netCDF.markers.MarkerSet_opt;
import org.gwaspi.netCDF.matrices.MatrixFactory;
import org.gwaspi.samples.SampleSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ucar.ma2.ArrayChar;
import ucar.ma2.ArrayInt;
import ucar.ma2.Index;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.NetcdfFileWriteable;

/**
 * Hapmap genotypes loader
 * Can load a single file or multiple files, as long as they belong to a single population (CEU, YRI, JPT...)
 * Imports Hapmap genotype files as found on
 * http://hapmap.ncbi.nlm.nih.gov/downloads/genotypes/?N=D
 *
 * @author Fernando Muñiz Fernandez
 * IBE, Institute of Evolutionary Biology (UPF-CSIC)
 * CEXS-UPF-PRBB
 */
public final class LoadGTFromGWASpiFiles implements GenotypesLoader {

	private final Logger log
			= LoggerFactory.getLogger(LoadGTFromGWASpiFiles.class);

	public LoadGTFromGWASpiFiles() {
	}

	@Override
	public ImportFormat getFormat() {
		return ImportFormat.GWASpi;
	}

	@Override
	public StrandType getMatrixStrand() {
		return null; // unused
	}

	@Override
	public boolean isHasDictionary() {
		return false;
	}

	@Override
	public int getMarkersD2ItemNb() {
		throw new UnsupportedOperationException("Not supported yet."); // FIXME
	}

	@Override
	public String getMarkersD2Variables() {
		throw new UnsupportedOperationException("Not supported yet."); // FIXME
	}

	@Override
	public int processData(GenotypesLoadDescription loadDescription, Map<String, Object> sampleInfo)
			throws IOException, InvalidRangeException, InterruptedException
	{
		int result = Integer.MIN_VALUE;

		if (new File(loadDescription.getGtDirPath()).exists()) {
		SampleSet matrixSampleSet = new SampleSet(loadDescription.getStudyId(), "");
		Map<String, Object> matrixSampleSetMap = matrixSampleSet.getSampleIdSetMap(loadDescription.getGtDirPath());

		boolean testExcessSamplesInMatrix = false;
		boolean testExcessSamplesInFile = false;
		for (String key : matrixSampleSetMap.keySet()) {
			if (!sampleInfo.containsKey(key)) {
				testExcessSamplesInMatrix = true;
				break;
			}
		}

		for (String key : sampleInfo.keySet()) {
			if (!matrixSampleSetMap.containsKey(key)) {
				testExcessSamplesInFile = true;
				break;
			}
		}

		if (testExcessSamplesInFile) {
			log.info("There were Samples in the Sample Info file that are not present in the genotypes file.\n" + Text.App.appName + " will attempt to ignore them...");
		}
		if (testExcessSamplesInMatrix) {
			log.info("Warning!\nSome Samples in the imported genotypes are not described in the Sample Info file!\nData will not be imported!");
		}

		MatrixMetadata importMatrixMetadata = MatricesList.getMatrixMetadata(loadDescription.getGtDirPath(), loadDescription.getStudyId(), loadDescription.getFriendlyName());

		// CREATE netCDF-3 FILE
		StringBuilder descSB = new StringBuilder(Text.Matrix.descriptionHeader1);
		descSB.append(org.gwaspi.global.Utils.getShortDateTimeAsString());
		if (!loadDescription.getDescription().isEmpty()) {
			descSB.append("\nDescription: ");
			descSB.append(loadDescription.getDescription());
			descSB.append("\n");
		}
//		descSB.append("\nStrand: ");
//		descSB.append(strand);
//		descSB.append("\nGenotype encoding: ");
//		descSB.append(importMatrixMetadata.getGenotypeEncoding());
		descSB.append("\n");
		descSB.append("Technology: ");
		descSB.append(importMatrixMetadata.getTechnology());
		descSB.append("\n");
		descSB.append("Markers: " + importMatrixMetadata.getMarkerSetSize() + ", Samples: " + importMatrixMetadata.getSampleSetSize());
		descSB.append("\n");
		descSB.append(Text.Matrix.descriptionHeader2);
		descSB.append(loadDescription.getFormat());
		descSB.append("\n");
		descSB.append(Text.Matrix.descriptionHeader3);
		descSB.append("\n");
		descSB.append(loadDescription.getGtDirPath());
		descSB.append(" (Matrix file)\n");
		descSB.append(loadDescription.getSampleFilePath());
		descSB.append(" (Sample Info file)\n");

		if (importMatrixMetadata.getGwaspiDBVersion().equals(Config.getConfigValue(Config.PROPERTY_CURRENT_GWASPIDB_VERSION, "2.0.2"))) { //COMPARE DATABASE VERSIONS
			if (!testExcessSamplesInMatrix) {
				DbManager dBManager = ServiceLocator.getDbManager(cDBGWASpi.DB_DATACENTER);
				MatricesList.insertMatrixMetadata(
						dBManager,
						loadDescription.getStudyId(),
						loadDescription.getFriendlyName(),
						importMatrixMetadata.getMatrixNetCDFName(),
						importMatrixMetadata.getGenotypeEncoding(),
						Integer.MIN_VALUE,
						Integer.MIN_VALUE,
						"",
						descSB.toString(), // description
						0);
			}
			copyMatrixToGenotypesFolder(loadDescription.getStudyId(), loadDescription.getGtDirPath(), importMatrixMetadata.getMatrixNetCDFName());
		} else {
			generateNewGWASpiDBversionMatrix(loadDescription, importMatrixMetadata);
		}

		importMatrixMetadata = MatricesList.getMatrixMetadataByNetCDFname(importMatrixMetadata.getMatrixNetCDFName());

		result = importMatrixMetadata.getMatrixId();
		}

		return result;
	}

	private int generateNewGWASpiDBversionMatrix(GenotypesLoadDescription loadDescription, MatrixMetadata importMatrixMetadata)
			throws IOException, InvalidRangeException, InterruptedException
	{
		int result = Integer.MIN_VALUE;
		String startTime = org.gwaspi.global.Utils.getMediumDateTimeAsString();

		//<editor-fold defaultstate="collapsed/expanded" desc="CREATE MARKERSET & NETCDF">
		MarkerSet_opt rdMarkerSet = new MarkerSet_opt(importMatrixMetadata.getStudyId(), loadDescription.getGtDirPath(), importMatrixMetadata.getMatrixNetCDFName());
		rdMarkerSet.initFullMarkerIdSetMap();
		rdMarkerSet.fillMarkerSetMapWithChrAndPos();
		Map<String, Object> rdMarkerSetMap = rdMarkerSet.getMarkerIdSetMap();

		SampleSet rdSampleSet = new SampleSet(importMatrixMetadata.getStudyId(), loadDescription.getGtDirPath(), importMatrixMetadata.getMatrixNetCDFName());
		Map<String, Object> rdSampleSetMap = rdSampleSet.getSampleIdSetMap();

		log.info("Done initializing sorted MarkerSetMap");

		// RETRIEVE CHROMOSOMES INFO
		Map<String, Object> chrSetMap = org.gwaspi.netCDF.matrices.Utils.aggregateChromosomeInfo(rdMarkerSetMap, 0, 1);

		MatrixFactory matrixFactory = new MatrixFactory(
				loadDescription.getStudyId(),
				loadDescription.getFormat(),
				loadDescription.getFriendlyName(),
				importMatrixMetadata.getDescription(), //description
				importMatrixMetadata.getGenotypeEncoding(),
				importMatrixMetadata.getStrand(),
				isHasDictionary(),
				rdSampleSetMap.size(),
				rdMarkerSetMap.size(),
				chrSetMap.size(),
				loadDescription.getGtDirPath());

		NetcdfFileWriteable ncfile = matrixFactory.getNetCDFHandler();

		// create the file
		try {
			ncfile.create();
		} catch (IOException ex) {
			log.error("Failed creating file " + ncfile.getLocation(), ex);
		}
		//log.info("Done creating netCDF handle ");
		//</editor-fold>

		//<editor-fold defaultstate="collapsed" desc="WRITE MATRIX METADATA">
		// WRITE SAMPLESET TO MATRIX FROM SAMPLES LIST
		ArrayChar.D2 samplesD2 = org.gwaspi.netCDF.operations.Utils.writeMapKeysToD2ArrayChar(rdSampleSetMap, cNetCDF.Strides.STRIDE_SAMPLE_NAME);

		int[] sampleOrig = new int[]{0, 0};
		try {
			ncfile.write(cNetCDF.Variables.VAR_SAMPLESET, sampleOrig, samplesD2);
		} catch (IOException ex) {
			log.error("Failed writing file", ex);
		} catch (InvalidRangeException ex) {
			log.error(null, ex);
		}
		samplesD2 = null;
		log.info("Done writing SampleSet to matrix");


		// WRITE CHROMOSOME METADATA FROM ANNOTATION FILE
		//Chromosome location for each marker
		ArrayChar.D2 markersD2 = org.gwaspi.netCDF.operations.Utils.writeMapValueItemToD2ArrayChar(rdMarkerSetMap, 0, cNetCDF.Strides.STRIDE_CHR);
		int[] markersOrig = new int[]{0, 0};
		try {
			ncfile.write(cNetCDF.Variables.VAR_MARKERS_CHR, markersOrig, markersD2);
		} catch (IOException ex) {
			log.error("Failed writing file", ex);
		} catch (InvalidRangeException ex) {
			log.error(null, ex);
		}
		log.info("Done writing chromosomes to matrix");

		// Set of chromosomes found in matrix along with number of markersinfo
		org.gwaspi.netCDF.operations.Utils.saveCharMapKeyToWrMatrix(ncfile, chrSetMap, cNetCDF.Variables.VAR_CHR_IN_MATRIX, 8);

		// Number of marker per chromosome & max pos for each chromosome
		int[] columns = new int[]{0, 1, 2, 3};
		org.gwaspi.netCDF.operations.Utils.saveIntMapD2ToWrMatrix(ncfile, chrSetMap, columns, cNetCDF.Variables.VAR_CHR_INFO);


		// WRITE POSITION METADATA FROM ANNOTATION FILE
		ArrayInt.D1 markersPosD1 = org.gwaspi.netCDF.operations.Utils.writeMapValueItemToD1ArrayInt(rdMarkerSetMap, 1);
		int[] posOrig = new int[1];
		try {
			ncfile.write(cNetCDF.Variables.VAR_MARKERS_POS, posOrig, markersPosD1);
		} catch (IOException ex) {
			log.error("Failed writing file", ex);
		} catch (InvalidRangeException ex) {
			log.error(null, ex);
		}
		log.info("Done writing positions to matrix");


		//WRITE RSID & MARKERID METADATA FROM METADATAMap
		rdMarkerSet.fillInitMapWithVariable(cNetCDF.Variables.VAR_MARKERS_RSID);
		markersD2 = org.gwaspi.netCDF.operations.Utils.writeMapKeysToD2ArrayChar(rdMarkerSetMap, cNetCDF.Strides.STRIDE_MARKER_NAME);
		try {
			ncfile.write(cNetCDF.Variables.VAR_MARKERSET, markersOrig, markersD2);
		} catch (IOException ex) {
			log.error("Failed writing file", ex);
		} catch (InvalidRangeException ex) {
			log.error(null, ex);
		}
		markersD2 = org.gwaspi.netCDF.operations.Utils.writeMapValueToD2ArrayChar(rdMarkerSetMap, cNetCDF.Strides.STRIDE_MARKER_NAME);
		try {
			ncfile.write(cNetCDF.Variables.VAR_MARKERS_RSID, markersOrig, markersD2);
		} catch (IOException ex) {
			log.error("Failed writing file", ex);
		} catch (InvalidRangeException ex) {
			log.error(null, ex);
		}
		log.info("Done writing MarkerId and RsId to matrix");


		//WRITE GT STRAND FROM ANNOTATION FILE
		rdMarkerSet.fillInitMapWithVariable(cNetCDF.Variables.VAR_GT_STRAND);
		markersD2 = org.gwaspi.netCDF.operations.Utils.writeMapValueToD2ArrayChar(rdMarkerSetMap, cNetCDF.Strides.STRIDE_STRAND);
		int[] gtOrig = new int[]{0, 0};
		try {
			ncfile.write(cNetCDF.Variables.VAR_GT_STRAND, gtOrig, markersD2);
		} catch (IOException ex) {
			log.error("Failed writing file", ex);
		} catch (InvalidRangeException ex) {
			log.error(null, ex);
		}
		markersD2 = null;
		log.info("Done writing strand info to matrix");


		// </editor-fold>

		//<editor-fold defaultstate="collapsed" desc="GENOTYPES WRITER">

		//Iterate through rdSampleSetMap, use item position to read correct sample GTs into rdMarkerIdSetMap.
		log.info(Text.All.processing);
		int sampleWrIndex = 0;
		for (int i = 0; i < rdSampleSetMap.size(); i++) {
			rdMarkerSet.fillGTsForCurrentSampleIntoInitMap(sampleWrIndex);

			//Write MarkerIdSetMap to A3 ArrayChar and save to wrMatrix
			org.gwaspi.netCDF.operations.Utils.saveSingleSampleGTsToMatrix(ncfile, rdMarkerSet.getMarkerIdSetMap(), sampleWrIndex);
			if (sampleWrIndex % 100 == 0) {
				log.info("Samples copied: " + sampleWrIndex);
			}
			sampleWrIndex++;
		}
		//</editor-fold>



		// CLOSE THE FILE AND BY THIS, MAKE IT READ-ONLY
		GenotypeEncoding guessedGTCode = GenotypeEncoding.UNKNOWN;
		try {
			//GUESS GENOTYPE ENCODING
			if (guessedGTCode.equals(cNetCDF.Defaults.GenotypeEncoding.UNKNOWN)) {
				guessedGTCode = Utils.detectGTEncoding(rdMarkerSet.getMarkerIdSetMap());
			} else if (guessedGTCode.equals(cNetCDF.Defaults.GenotypeEncoding.O12)) {
				guessedGTCode = Utils.detectGTEncoding(rdMarkerSet.getMarkerIdSetMap());
			}

			ArrayChar.D2 guessedGTCodeAC = new ArrayChar.D2(1, 8);
			Index index = guessedGTCodeAC.getIndex();
			guessedGTCodeAC.setString(index.set(0, 0), guessedGTCode.toString().trim());
			int[] origin = new int[]{0, 0};
			ncfile.write(cNetCDF.Variables.GLOB_GTENCODING, origin, guessedGTCodeAC);

			StringBuilder descSB = new StringBuilder(loadDescription.getDescription());
			descSB.append("Genotype encoding: ");
			descSB.append(guessedGTCode);
			DbManager db = ServiceLocator.getDbManager(cDBGWASpi.DB_DATACENTER);
			db.updateTable(cDBGWASpi.SCH_MATRICES,
					cDBMatrix.T_MATRICES,
					new String[]{cDBMatrix.f_DESCRIPTION},
					new Object[]{descSB.toString()},
					new String[]{cDBMatrix.f_ID},
					new Object[]{matrixFactory.getMatrixMetaData().getMatrixId()});

			//CLOSE FILE
			ncfile.close();
			result = matrixFactory.getMatrixMetaData().getMatrixId();
		} catch (IOException ex) {
			log.error("Failed creating file " + ncfile.getLocation(), ex);
		}

		org.gwaspi.global.Utils.sysoutCompleted("writing Genotypes to Matrix");
		return result;
	}
	//</editor-fold>

	//<editor-fold defaultstate="collapsed" desc="HELPER METHODS">
	private Map<String, Object> getSampleIds(File hapmapGTFile) throws IOException {

		Map<String, Object> uniqueSamples = new LinkedHashMap<String, Object>();

		FileReader fr = new FileReader(hapmapGTFile.getPath());
		BufferedReader inputAnnotationBr = new BufferedReader(fr);
		String header = inputAnnotationBr.readLine();

		String l;
		String[] hapmapVals = header.split(cImport.Separators.separators_SpaceTab_rgxp);

		for (int i = cImport.SampleInfo.sampleId; i < hapmapVals.length; i++) {
			uniqueSamples.put(hapmapVals[i], "");
		}

		return uniqueSamples;
	}

	private void copyMatrixToGenotypesFolder(int studyId, String importMatrixPath, String newMatrixCDFName) {
		try {
			String genotypesFolder = Config.getConfigValue(Config.PROPERTY_GENOTYPES_DIR, "");
			File pathToStudy = new File(genotypesFolder + "/STUDY_" + studyId);
			if (!pathToStudy.exists()) {
				org.gwaspi.global.Utils.createFolder(genotypesFolder, "/STUDY_" + studyId);
			}

			File origFile = new File(importMatrixPath);
			File newFile = new File(pathToStudy + "/" + newMatrixCDFName + ".nc");
			if (origFile.exists()) {
				org.gwaspi.global.Utils.copyFile(origFile, newFile);
			}
		} catch (IOException ex) {
			Dialogs.showWarningDialogue("A table saving error has occurred");
			log.error("A table saving error has occurred", ex);
		} catch (Exception ex) {
			Dialogs.showWarningDialogue("A table saving error has occurred");
			log.error("A table saving error has occurred", ex);
		}
	}
	//</editor-fold>
}
