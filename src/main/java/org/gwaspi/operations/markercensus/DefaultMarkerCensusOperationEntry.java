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

import org.gwaspi.model.CensusFull;
import org.gwaspi.model.MarkerKey;
import org.gwaspi.operations.AbstractOperationDataEntry;

public class DefaultMarkerCensusOperationEntry extends AbstractOperationDataEntry<MarkerKey> implements MarkerCensusOperationEntry {

	private final byte[] knownAlleles;
	private final CensusFull census;

	public DefaultMarkerCensusOperationEntry(MarkerKey key, int index, byte[] knownAlleles, CensusFull census) {
		super(key, index);

		this.knownAlleles = knownAlleles;
		this.census = census;
	}

	@Override
	public byte[] getKnownAlleles() {
		return knownAlleles;
	}

	@Override
	public CensusFull getCensus() {
		return census;
	}
}
