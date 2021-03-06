/*
 * Copyright (C) 2013 Universitat Pompeu Fabra
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.gwaspi.netCDF.loader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import org.gwaspi.constants.ImportConstants;
import org.gwaspi.constants.ImportConstants.Annotation.Beagle_Standard;
import org.gwaspi.constants.NetCDFConstants;
import org.gwaspi.constants.NetCDFConstants.Defaults.StrandType;
import org.gwaspi.model.MarkerMetadata;
import org.gwaspi.model.StudyKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetadataLoaderBeagle implements MetadataLoader {

	private final Logger log
			= LoggerFactory.getLogger(MetadataLoaderBeagle.class);

	@Override
	public boolean isHasStrandInfo() {
		return false;
	}

	@Override
	public StrandType getFixedStrandFlag() {
		return null;
	}

	@Override
	public void loadMarkers(DataSetDestination samplesReceiver, GenotypesLoadDescription loadDescription) throws IOException {
		loadMarkers(
				samplesReceiver,
				loadDescription.getAnnotationFilePath(),
				loadDescription.getChromosome(),
				loadDescription.getStudyKey());
	}

	private void loadMarkers(DataSetDestination samplesReceiver, String markerFilePath, String chr, StudyKey studyKey) throws IOException {

		String startTime = org.gwaspi.global.Utils.getMediumDateTimeAsString();

		log.info("read and pre-parse raw marker info");
		SortedMap<String, String> tempTM = parseAndSortMarkerFile(markerFilePath, chr); // chr, markerId, genetic distance, position

		log.info("parse and fixup raw marker info");
		for (Map.Entry<String, String> entry : tempTM.entrySet()) {
			// chr;pos;markerId
			String[] keyValues = entry.getKey().split(NetCDFConstants.Defaults.TMP_SEPARATOR);
			int pos = MetadataLoaderPlink.fixPosIfRequired(keyValues[1]);

			// rsId;alleles
			String[] valValues = entry.getValue().split(NetCDFConstants.Defaults.TMP_SEPARATOR);

			MarkerMetadata markerInfo = new MarkerMetadata(
					keyValues[2], // markerid
					valValues[0], // rsId
					fixChrData(keyValues[0]), // chr
					pos, // pos
					valValues[1]); // alleles

			samplesReceiver.addMarkerMetadata(markerInfo);
		}

		String description = "Generated sorted MarkerIdSet Map sorted by chromosome and position";
		MetadataLoaderPlink.logAsWhole(log, startTime, markerFilePath, description, studyKey.getId());
	}

	private SortedMap<String, String> parseAndSortMarkerFile(String markerFilePath, String chr) throws IOException {
		FileReader fr = new FileReader(markerFilePath);
		BufferedReader inputMapBR = new BufferedReader(fr);
		SortedMap<String, String> sortedMetadataTM = new TreeMap<String, String>(new ComparatorChrAutPosMarkerIdAsc());

		String l;
		int count = 0;
		while ((l = inputMapBR.readLine()) != null) {
			String[] markerVals = l.split(ImportConstants.Separators.separators_SpaceTab_rgxp);
			String markerId = markerVals[Beagle_Standard.rsId].trim();
			String rsId = "";
			if (markerId.startsWith("rs")) {
				rsId = markerId;
			}
			String pos = markerVals[Beagle_Standard.pos].trim();

			// chr;pos;markerId
			final StringBuilder sbKey = new StringBuilder();
			sbKey
					.append(chr)
					.append(NetCDFConstants.Defaults.TMP_SEPARATOR)
					.append(pos)
					.append(NetCDFConstants.Defaults.TMP_SEPARATOR)
					.append(markerId);

			// rsId;alleles
			final StringBuilder sbVal = new StringBuilder();
			sbVal
					.append(rsId) // 0 => rsId
					.append(NetCDFConstants.Defaults.TMP_SEPARATOR)
					.append(markerVals[Beagle_Standard.allele1].trim())
					.append(markerVals[Beagle_Standard.allele2].trim()); // 1 => alleles

			sortedMetadataTM.put(sbKey.toString(), sbVal.toString());

			count++;

			if ((count == 1) || (count % 100000 == 0)) {
				log.info("read and pre-parse marker metadat from file(s); lines: {}", count);
			}
		}
		log.info("read and pre-parse marker metadat from file(s); lines: {}", count);

		inputMapBR.close();

		return sortedMetadataTM;
	}

	static String fixChrData(String chr) {

		String chrFixed = chr;

		if (chrFixed.equals("23")) {
			chrFixed = "X";
		}
		if (chrFixed.equals("24")) {
			chrFixed = "Y";
		}
		if (chrFixed.equals("25")) {
			chrFixed = "XY";
		}
		if (chrFixed.equals("26")) {
			chrFixed = "MT";
		}

		return chrFixed;
	}
}
