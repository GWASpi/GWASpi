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

package org.gwaspi.operations.markercensus;

import java.io.Serializable;
import org.gwaspi.global.Extractor;
import org.gwaspi.model.Census;
import org.gwaspi.model.CensusFull;
import org.gwaspi.model.MarkerKey;
import org.gwaspi.operations.OperationDataEntry;
import org.gwaspi.operations.hardyweinberg.HardyWeinbergOperationEntry.Category;

public interface MarkerCensusOperationEntry extends OperationDataEntry<MarkerKey> {

	class KnownAllelesExtractor implements Extractor<MarkerCensusOperationEntry, byte[]>,
			Serializable
	{
		private static final long serialVersionUID = 1L;

		@Override
		public byte[] extract(MarkerCensusOperationEntry from) {
			return from.getKnownAlleles();
		}
	};
	Extractor<MarkerCensusOperationEntry, byte[]> TO_KNOWN_ALLELES
			= new KnownAllelesExtractor();

	class CensusFullExtractor implements Extractor<MarkerCensusOperationEntry, CensusFull>,
			Serializable
	{
		private static final long serialVersionUID = 1L;

		@Override
		public CensusFull extract(MarkerCensusOperationEntry from) {
			return from.getCensus();
		}
	};
	Extractor<MarkerCensusOperationEntry, CensusFull> TO_CENSUS_FULL
			= new CensusFullExtractor();

	class CensusExtractor implements Extractor<MarkerCensusOperationEntry, Census>, Serializable {

		private static final long serialVersionUID = 1L;

		private final Category category;

		public CensusExtractor(final Category category) {

			this.category = category;
		}

		@Override
		public Census extract(MarkerCensusOperationEntry from) {
			return from.getCensus().getCategoryCensus().get(category);
		}
	};

	// - Variables.VAR_OPSET: [Collection<MarkerKey>]
	// - Variables.VAR_MARKERS_RSID: [Collection<String>]
	// - Variables.VAR_IMPLICITSET: [Collection<SampleKey>]
	// - Variables.VAR_ALLELES: known alleles [Collection<char[]>]
	// - Census.VAR_OP_MARKERS_CENSUSALL: marker census - all [Collection<Census.all<== int[]>>]
	// - Census.VAR_OP_MARKERS_CENSUSCASE: marker census - case [Collection<Census.case>]
	// - Census.VAR_OP_MARKERS_CENSUSCTRL: marker census - control [Collection<Census.control>]
	// - Census.VAR_OP_MARKERS_CENSUSHW: marker census - alternate hardy-weinberg [Collection<Census.altHW>]

	byte[] getKnownAlleles();

	/**
	 * @return all census info:
	 *   int[4]: allele-AA, allele-Aa, allele-aa, missing-count for each marker in this operation
	 * NetCDF variable: Census.VAR_OP_MARKERS_CENSUSALL
	 */
//	int[] getCensusAll();
	CensusFull getCensus();
}
