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

package org.gwaspi.operations.genotypicassociationtest;

import java.io.IOException;
import java.util.Map;
import org.gwaspi.constants.NetCDFConstants;
import org.gwaspi.global.Text;
import org.gwaspi.model.DataSetKey;
import org.gwaspi.model.OperationKey;
import org.gwaspi.operations.OperationTypeInfo;
import org.gwaspi.operations.DefaultOperationTypeInfo;
import org.gwaspi.operations.trendtest.AbstractTestOperationFactory;

public class GenotypicAssociationTestsOperationFactory
		extends AbstractTestOperationFactory<GenotypicAssociationTestsOperationDataSet, AssociationTestOperationParams>
{
	static final OperationTypeInfo OPERATION_TYPE_INFO
			= new DefaultOperationTypeInfo(
					false,
					Text.Operation.genoAssocTest,
					Text.Operation.genoAssocTest, // TODO We need a more elaborate description of this operation!
					NetCDFConstants.Defaults.OPType.GENOTYPICTEST,
					true,
					false);

	public GenotypicAssociationTestsOperationFactory() {
		super(GenotypicAssociationTestOperation.class, OPERATION_TYPE_INFO);
	}

	@Override
	protected GenotypicAssociationTestsOperationDataSet generateReadOperationDataSetNetCdf(
			OperationKey operationKey, DataSetKey parent, Map<String, Object> properties)
			throws IOException
	{

		return new NetCdfGenotypicAssociationTestsOperationDataSet(
				parent.getOrigin(), parent, operationKey);
	}

	@Override
	protected GenotypicAssociationTestsOperationDataSet generateSpecificWriteOperationDataSetMemory(
			DataSetKey parent, Map<String, Object> properties)
			throws IOException
	{
		return new InMemoryGenotypicAssociationTestsOperationDataSet(
				parent.getOrigin(), parent);
	}
}
