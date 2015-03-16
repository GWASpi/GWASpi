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

import org.gwaspi.operations.AbstractOperation;
import org.gwaspi.operations.OperationParams;

public abstract class AbstractOutputOperation<P extends OperationParams>
		extends AbstractOperation<P, Object>
{
	private final P params;

	protected AbstractOutputOperation(P params) {

		this.params = params;
	}

	@Override
	public P getParams() {
		return params;
	}
}
