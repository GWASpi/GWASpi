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

package org.gwaspi.reports;

import org.gwaspi.model.DataSetKey;
import org.gwaspi.model.OperationKey;
import org.gwaspi.operations.AbstractOperationParams;

/**
 * Parameters for the {@link OutputQASamples}.
 */
public class QASamplesOutputParams extends AbstractOperationParams {

	private final boolean newReport;

	public QASamplesOutputParams(OperationKey sampleQAOpKey, boolean newReport) {
		super(null, new DataSetKey(sampleQAOpKey), null);

		this.newReport = newReport;
	}

	/** @deprecated */
	public OperationKey getSampleQAOpKey() {
		return getParent().getOperationParent();
	}

	public boolean isNewReport() {
		return newReport;
	}

	@Override
	protected String getNameDefault() {
		return "Output to file for QA Samples: " + getParent().toString();
	}
}
