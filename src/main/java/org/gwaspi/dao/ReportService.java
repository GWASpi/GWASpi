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

package org.gwaspi.dao;

import java.io.IOException;
import java.util.List;
import org.gwaspi.constants.NetCDFConstants.Defaults.OPType;
import org.gwaspi.model.DataSetKey;
import org.gwaspi.model.OperationMetadata;
import org.gwaspi.model.Report;
import org.gwaspi.model.ReportKey;

public interface ReportService {

	Report getReport(ReportKey reportKey) throws IOException;

	List<Report> getReports(DataSetKey parentKey) throws IOException;

	List<Report> getReports(DataSetKey parentKey, OPType reportType) throws IOException;

	String getReportNamePrefix(OperationMetadata op); // TODO move somewhere else

	void insertReport(Report report) throws IOException;

	void deleteReports(final DataSetKey parent) throws IOException;
}
