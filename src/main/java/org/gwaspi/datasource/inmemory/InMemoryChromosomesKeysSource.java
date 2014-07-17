/*
 * Copyright (C) 2014 Universitat Pompeu Fabra
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

package org.gwaspi.datasource.inmemory;

import java.io.IOException;
import java.util.List;
import org.gwaspi.model.ChromosomeKey;
import org.gwaspi.model.ChromosomeKeyFactory;
import org.gwaspi.model.ChromosomesKeysSource;
import org.gwaspi.model.KeyFactory;
import org.gwaspi.model.MatrixKey;

public class InMemoryChromosomesKeysSource extends AbstractInMemoryKeysSource<ChromosomeKey> implements ChromosomesKeysSource {

	private ChromosomesKeysSource originSource;

	private InMemoryChromosomesKeysSource(MatrixKey origin, List<ChromosomeKey> items, List<Integer> originalIndices) {
		super(origin, items, originalIndices);

		this.originSource = null;
	}

	public static ChromosomesKeysSource createForMatrix(MatrixKey origin, List<ChromosomeKey> items) throws IOException {
		return new InMemoryChromosomesKeysSource(origin, items, null);
	}

	public static ChromosomesKeysSource createForOperation(MatrixKey origin, List<ChromosomeKey> items, List<Integer> originalIndices) throws IOException {
		return new InMemoryChromosomesKeysSource(origin, items, originalIndices);
	}

	@Override
	public ChromosomesKeysSource getOrigSource() throws IOException {

		if (originSource == null) {
			if (getOrigin() == null) {
				originSource = this;
			} else {
				originSource = getOrigDataSetSource().getChromosomesKeysSource();
			}
		}

		return originSource;
	}

	@Override
	protected KeyFactory<ChromosomeKey> createKeyFactory() {
		return new ChromosomeKeyFactory();
	}
}
