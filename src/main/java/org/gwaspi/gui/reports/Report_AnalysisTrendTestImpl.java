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

package org.gwaspi.gui.reports;

import org.gwaspi.constants.NetCDFConstants.Defaults.OPType;
import org.gwaspi.model.OperationKey;

public final class Report_AnalysisTrendTestImpl extends Report_Analysis {

	public Report_AnalysisTrendTestImpl(final OperationKey operationKey, final String analysisFileName, final Integer nRows) {
		super(operationKey, analysisFileName, nRows);
	}

	@Override
	protected OPType getAssociationTestType() {
		return OPType.TRENDTEST;
	}
}
