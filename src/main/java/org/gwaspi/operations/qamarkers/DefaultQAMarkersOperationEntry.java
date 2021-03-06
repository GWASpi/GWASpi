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

package org.gwaspi.operations.qamarkers;

import java.io.Serializable;
import org.gwaspi.model.MarkerKey;
import org.gwaspi.operations.AbstractOperationDataEntry;

public class DefaultQAMarkersOperationEntry extends AbstractOperationDataEntry<MarkerKey>
		implements QAMarkersOperationEntry, Serializable
{
	private static final long serialVersionUID = 1L;

	private final boolean mismatchState;
	private final byte majorAllele;
	private final double majorAlleleFrequency;
	private final byte minorAllele;
	private final double minorAlleleFrequency;
	private final int missingCount;
	private final double missingRatio;
	private final int[] alleleCounts;
	private final int[] genotypeCounts;

	public DefaultQAMarkersOperationEntry(
			MarkerKey key,
			int index,
			boolean mismatchState,
			byte majorAllele,
			double majorAlleleFrequency,
			byte minorAllele,
			double minorAlleleFrequency,
			int missingCount,
			double missingRatio,
			int[] alleleCounts,
			int[] genotypeCounts)
	{
		super(key, index);

		this.missingRatio = missingRatio;
		this.mismatchState = mismatchState;
		this.majorAllele = majorAllele;
		this.majorAlleleFrequency = majorAlleleFrequency;
		this.minorAllele = minorAllele;
		this.minorAlleleFrequency = minorAlleleFrequency;
		this.missingCount = missingCount;
		this.alleleCounts = alleleCounts;
		this.genotypeCounts = genotypeCounts;
	}

	@Override
	public double getMissingRatio() {
		return missingRatio;
	}

	@Override
	public boolean getMismatchState() {
		return mismatchState;
	}

	@Override
	public byte getMajorAllele() {
		return majorAllele;
	}

	@Override
	public double getMajorAlleleFrequency() {
		return majorAlleleFrequency;
	}

	@Override
	public byte getMinorAllele() {
		return minorAllele;
	}

	@Override
	public double getMinorAlleleFrequency() {
		return minorAlleleFrequency;
	}

	@Override
	public int getAlleleAA() {
		return
				getGenotypeCounts()[GenotypeCounts._AA.ordinal()]
				+ getGenotypeCounts()[GenotypeCounts._A0.ordinal()]
				+ getGenotypeCounts()[GenotypeCounts._0A.ordinal()];
	}

	@Override
	public int getAlleleAa() {
		return
				getGenotypeCounts()[GenotypeCounts._Aa.ordinal()]
				+ getGenotypeCounts()[GenotypeCounts._aA.ordinal()];
	}

	@Override
	public int getAlleleaa() {
		return
				getGenotypeCounts()[GenotypeCounts._aa.ordinal()]
				+ getGenotypeCounts()[GenotypeCounts._a0.ordinal()]
				+ getGenotypeCounts()[GenotypeCounts._0a.ordinal()];
	}

	@Override
	public int getMissingCount() {
		return missingCount;
	}

	@Override
	public int[] getAlleleCounts() {
		return alleleCounts;
	}

	@Override
	public int[] getGenotypeCounts() {
		return genotypeCounts;
	}
}
