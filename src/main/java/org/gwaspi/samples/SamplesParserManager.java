package org.gwaspi.samples;

import org.gwaspi.constants.cDBSamples;
import org.gwaspi.constants.cImport;
import org.gwaspi.constants.cImport.Annotation.GWASpi;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.gwaspi.model.MatricesList;
import org.gwaspi.model.MatrixMetadata;
import org.gwaspi.model.SampleInfo;
import org.gwaspi.model.SampleInfoList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ucar.ma2.InvalidRangeException;

/**
 *
 * @author Fernando Muñiz Fernandez
 * IBE, Institute of Evolutionary Biology (UPF-CSIC)
 * CEXS-UPF-PRBB
 */
public class SamplesParserManager {

	private static final Logger log
			= LoggerFactory.getLogger(SamplesParserManager.class);

	private SamplesParserManager() {
	}

	//<editor-fold defaultstate="collapsed" desc="DB SAMPLE INFO PROVIDERS">
	public static Set<Object> getDBAffectionStates(int matrixId) {
		Set<Object> resultHS = new HashSet<Object>();
		try {
			MatrixMetadata rdMatrixMetadata = MatricesList.getMatrixMetadataById(matrixId);
			log.info("Getting Sample Affection info for: {}",
					rdMatrixMetadata.getMatrixFriendlyName());
//			NetcdfFile rdNcFile = NetcdfFile.open(rdMatrixMetadata.getPathToMatrix());
			SampleSet rdSampleSet = new SampleSet(rdMatrixMetadata.getStudyId(), matrixId);
			Map<String, Object> rdSampleSetMap = rdSampleSet.getSampleIdSetMap();
			for (String key : rdSampleSetMap.keySet()) {
				List<SampleInfo> sampleInfos = SampleInfoList.getCurrentSampleInfoFromDB(key.toString(), rdMatrixMetadata.getStudyId());
				if (sampleInfos != null) {
					resultHS.add(sampleInfos.get(0).getAffection());
				}
			}
		} catch (InvalidRangeException ex) {
			log.error(null, ex);
		} catch (IOException ex) {
			log.error(null, ex);
		}
		return resultHS;
	}
	//</editor-fold>

	//<editor-fold defaultstate="collapsed" desc="FILE SAMPLE INFO SCANNERS">
	private static final SamplesParser gwaspiSamplesParser = new GwaspiSamplesParser();
	public static Map<String, Object> scanGwaspiSampleInfo(String sampleInfoPath) throws IOException {
		return gwaspiSamplesParser.scanSampleInfo(sampleInfoPath);
	}

	private static final SamplesParser plinkStandardSamplesParser = new PlinkStandardSamplesParser();
	public static Map<String, Object> scanPlinkStandardSampleInfo(String pedPath) throws IOException {
		return plinkStandardSamplesParser.scanSampleInfo(pedPath);
	}

	private static final SamplesParser plinkFAMSamplesParser = new PlinkFAMSamplesParser();
	public static Map<String, Object> scanPlinkFAMSampleInfo(String famPath) throws IOException {
		return plinkFAMSamplesParser.scanSampleInfo(famPath);
	}

	private static final SamplesParser illuminaLGENSamplesParser = new IlluminaLGENSamplesParser();
	public static Map<String, Object> scanIlluminaLGENSampleInfo(String lgenDir) throws IOException {
		return illuminaLGENSamplesParser.scanSampleInfo(lgenDir);
	}

	private static final SamplesParser beagleSamplesParser = new BeagleSamplesParser();
	public static Map<String, Object> scanBeagleSampleInfo(String beaglePath) throws IOException {
		return beagleSamplesParser.scanSampleInfo(beaglePath);
	}

	private static final SamplesParser hapmapSamplesParser = new HapmapSamplesParser();
	public static Map<String, Object> scanHapmapSampleInfo(String hapmapPath) throws IOException {
		return hapmapSamplesParser.scanSampleInfo(hapmapPath);
	}

	private static final SamplesParser hgdp1SamplesParser = new HGDP1SamplesParser();
	public static Map<String, Object> scanHGDP1SampleInfo(String hgdpPath) throws IOException {
		return hgdp1SamplesParser.scanSampleInfo(hgdpPath);
	}

	private static final SamplesParser affymetrixSamplesParser = new AffymetrixSamplesParser();
	public static Map<String, Object> scanAffymetrixSampleInfo(String genotypesPath) throws IOException {
		return affymetrixSamplesParser.scanSampleInfo(genotypesPath);
	}

	private static final SamplesParser sequenomSamplesParser = new SequenomSamplesParser();
	public static Map<String, Object> scanSequenomSampleInfo(String genotypePath) throws IOException {
		return sequenomSamplesParser.scanSampleInfo(genotypePath);
	}

	public static Set<String> scanSampleInfoAffectionStates(String sampleInfoPath) throws IOException {
		Set<String> resultHS = new HashSet<String>();

		File sampleFile = new File(sampleInfoPath);
		FileReader inputFileReader = new FileReader(sampleFile);
		BufferedReader inputBufferReader = new BufferedReader(inputFileReader);

		String header = inputBufferReader.readLine(); // ignore header block
		String l;
		while ((l = inputBufferReader.readLine()) != null) {
			String[] cVals = l.split(cImport.Separators.separators_CommaSpaceTab_rgxp);
			resultHS.add(cVals[GWASpi.affection]);
		}

		inputFileReader.close();

		return resultHS;
	}
	//</editor-fold>
}
