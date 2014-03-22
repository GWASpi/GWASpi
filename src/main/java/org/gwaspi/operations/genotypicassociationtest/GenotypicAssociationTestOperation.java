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

package org.gwaspi.operations.genotypicassociationtest;

import org.gwaspi.global.Text;
import org.gwaspi.netCDF.operations.DefaultOperationTypeInfo;
import org.gwaspi.netCDF.operations.OP_AssociationTests;
import org.gwaspi.netCDF.operations.OperationFactory;
import org.gwaspi.netCDF.operations.OperationTypeInfo;

public class GenotypicAssociationTestOperation extends OP_AssociationTests {

	private static final OperationTypeInfo OPERATION_TYPE_INFO
			= new DefaultOperationTypeInfo(
					false,
					Text.Operation.genoAssocTest,
					Text.Operation.genoAssocTest); // TODO We need a more elaborate description of this operation!
	static {
		// NOTE When converting to OSGi, this would be done in bundle init,
		//   or by annotations.
		OperationFactory.registerOperationTypeInfo(
				GenotypicAssociationTestOperation.class,
				OPERATION_TYPE_INFO);
	}

	public GenotypicAssociationTestOperation(final AssociationTestOperationParams params) {
		super(params);
	}
}