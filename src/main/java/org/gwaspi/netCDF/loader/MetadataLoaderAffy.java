package org.gwaspi.netCDF.loader;

import org.gwaspi.constants.cImport.Annotation.Affymetrix_GenomeWide6;
import org.gwaspi.constants.cNetCDF;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author Fernando Muñiz Fernandez
 * IBE, Institute of Evolutionary Biology (UPF-CSIC)
 * CEXS-UPF-PRBB
 */
public class MetadataLoaderAffy {

	private String annotationPath;
	private String format;
	private int studyId;

	private enum Bases {

		A, C, T, G;
	}
	private static String tabulator = cNetCDF.Defaults.TMP_SEPARATOR;

	public MetadataLoaderAffy(String _annotationPath, String _format, int _studyId) throws FileNotFoundException {

		annotationPath = _annotationPath;
		studyId = _studyId;
		format = _format;

	}

	// ACCESSORS
	public Map<String, Object> getSortedMarkerSetWithMetaData() throws IOException {
		String startTime = org.gwaspi.global.Utils.getMediumDateTimeAsString();

		TreeMap tempTM = parseAnnotationBRFile(annotationPath); // affyId, rsId,chr,pseudo-autosomal,pos, strand, alleles, plus-alleles

		org.gwaspi.global.Utils.sysoutStart("initilaizing marker info");
		System.out.println(org.gwaspi.global.Text.All.processing);

		Map<String, Object> markerMetadataLHM = new LinkedHashMap<String, Object>();
		for (Iterator it = tempTM.keySet().iterator(); it.hasNext();) {
			// keyValues = chr;pseudo-autosomal1;pseudo-autosomal2;pos;markerId"
			String key = it.next().toString();
			String[] keyValues = key.split(cNetCDF.Defaults.TMP_SEPARATOR);
			int pos;
			try {
				pos = Integer.parseInt(keyValues[3]);
			} catch (Exception ex) {
				pos = 0;
			}

			//valValues = rsId;strand;alleles
			String[] valValues = tempTM.get(key).toString().split(cNetCDF.Defaults.TMP_SEPARATOR);
			valValues = fixRsId(keyValues, valValues);
			keyValues = fixChrData(keyValues);

			Object[] markerInfo = new Object[8];
			markerInfo[0] = keyValues[4]; // 0 => affyid
			markerInfo[1] = valValues[0]; // 1 => rsId
			markerInfo[2] = keyValues[0]; // 2 => chr
			markerInfo[3] = keyValues[1]; // 3 => pseudo-autosomal1
			markerInfo[4] = keyValues[2]; // 4 => pseudo-autosomal2
			markerInfo[5] = pos; // 5 => pos
			markerInfo[6] = valValues[1]; // 6 => strand
			markerInfo[7] = valValues[2]; // 7 => alleles

			markerMetadataLHM.put(keyValues[4], markerInfo);
		}

		String description = "Generated sorted MarkerIdSet LHM sorted by chromosome and position";
		logAsWhole(startTime, annotationPath, description, studyId);
		return markerMetadataLHM;
	}

	public static TreeMap parseAnnotationBRFile(String path) throws IOException {
		FileReader fr = new FileReader(path);
		BufferedReader inputAnnotationBr = new BufferedReader(fr);
		TreeMap sortedMetadataTM = new TreeMap(new ComparatorChrAutPosMarkerIdAsc());

		String header = "";
		while (!header.startsWith("\"Probe Set ID") && header != null) {
			header = inputAnnotationBr.readLine();
		}

		// Duplicate SNPs to be removed
		SNPBlacklist SNPblackList = new SNPBlacklist();

		String l;
		int count = 0;
		while ((l = inputAnnotationBr.readLine()) != null) {

			String[] affy6Vals = l.split("\",\"");
			Affymetrix_GenomeWide6.init(path);
			String affyId = affy6Vals[Affymetrix_GenomeWide6.markerId].replace("\"", "");
			String chr = affy6Vals[Affymetrix_GenomeWide6.chr].replace("\"", "");
			String inFinalList = affy6Vals[Affymetrix_GenomeWide6.in_final_list].replace("\"", "");

			if (!affyId.startsWith("AFFX-") && !inFinalList.equals("NO") && !chr.equals("---") && !SNPblackList.getAffyIdBlacklist().contains(affyId)) {
				// chr;pseudo-autosomal1;pseudo-autosomal2;pos;markerId"
				StringBuilder sbKey = new StringBuilder(chr); // 0 => chr
				sbKey.append(cNetCDF.Defaults.TMP_SEPARATOR);
				sbKey.append(affy6Vals[Affymetrix_GenomeWide6.pseudo_a1]); // 1 => pseudo-autosomal1
				sbKey.append(cNetCDF.Defaults.TMP_SEPARATOR);
				sbKey.append(affy6Vals[Affymetrix_GenomeWide6.pseudo_a2]); // 2 => pseudo-autosomal2
				sbKey.append(cNetCDF.Defaults.TMP_SEPARATOR);
				sbKey.append(affy6Vals[Affymetrix_GenomeWide6.pos]); // 3 => pos
				sbKey.append(cNetCDF.Defaults.TMP_SEPARATOR);
				sbKey.append(affyId); // 4 => markerId

				// rsId;strand;alleles
				StringBuilder sbVal = new StringBuilder(affy6Vals[Affymetrix_GenomeWide6.rsId]); // 0 => rsId
				sbVal.append(cNetCDF.Defaults.TMP_SEPARATOR);
				sbVal.append(affy6Vals[Affymetrix_GenomeWide6.strand]); // 1 => strand
				sbVal.append(cNetCDF.Defaults.TMP_SEPARATOR);
				sbVal.append(affy6Vals[Affymetrix_GenomeWide6.alleleA]).append(affy6Vals[Affymetrix_GenomeWide6.alleleB]); // 2 => alleles

				sortedMetadataTM.put(sbKey.toString(), sbVal.toString());
			}
			count++;
			if (count == 1) {
				System.out.println(org.gwaspi.global.Text.All.processing);
			} else if (count % 100000 == 0) {
				System.out.println("Parsed annotation lines: " + count);
			}
		}
		System.out.println("Parsed annotation lines: " + count);
		return sortedMetadataTM;
	}

	public static String[] fixRsId(String[] keyValues, String[] valValues) throws IOException {
		// CHECK IF RSID EXISTS, USE AFFYID IF NOT

		// valValues rsId;strand;alleles
		// keyValues chr;pseudo-autosomal1;pseudo-autosomal2;pos;markerId
		if (valValues[0].equals("---")) {
			valValues[0] = keyValues[4];
		}
		return valValues;
	}

	public static String[] fixChrData(String[] keyValues) throws IOException {
		// CHECK FOR PSEUDO-AUTOSOMAL FLAG => XY

		//keyValues chr;pseudo-autosomal1;pseudo-autosomal2;pos;markerId
		if (keyValues[1].equals("1")) {
			keyValues[0] = "XY";
		}
		if (keyValues[2].equals("1")) {
			keyValues[0] = "XY";
		}
		return keyValues;
	}

	// METHODS
	private static void logAsWhole(String startTime, String dirPath, String description, int studyId) throws IOException {
		// LOG OPERATION IN STUDY HISTORY
		StringBuilder operation = new StringBuilder("\nLoaded Annotation metadata in path " + dirPath + ".\n");
		operation.append("Start Time: ").append(startTime).append("\n");
		operation.append("End Time: ").append(org.gwaspi.global.Utils.getMediumDateTimeAsString()).append(".\n");
		operation.append("Description: ").append(description).append(".\n");
		org.gwaspi.global.Utils.logOperationInStudyDesc(operation.toString(), studyId);
		////////////////////////////////
	}
}
