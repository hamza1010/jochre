///////////////////////////////////////////////////////////////////////////////
//Copyright (C) 2012 Assaf Urieli
//
//This file is part of Jochre.
//
//Jochre is free software: you can redistribute it and/or modify
//it under the terms of the GNU Affero General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//Jochre is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU Affero General Public License for more details.
//
//You should have received a copy of the GNU Affero General Public License
//along with Jochre.  If not, see <http://www.gnu.org/licenses/>.
//////////////////////////////////////////////////////////////////////////////
package com.joliciel.jochre.boundaries;

import java.util.List;

import com.joliciel.jochre.graphics.GroupOfShapes;

public interface BoundaryDetector {
	/**
	 * Find the n most likely shape sequences forming this group of shapes, after
	 * splitting and merging have been attempted.
	 */
	public List<ShapeSequence> findBoundaries(GroupOfShapes group);

	public abstract void setMaxDistanceRatioForMerge(double maxDistanceRatioForMerge);

	public abstract double getMaxDistanceRatioForMerge();

	public abstract void setMaxWidthRatioForMerge(double maxWidthRatioForMerge);

	public abstract double getMaxWidthRatioForMerge();

	public abstract void setMinWidthRatioForSplit(double minWidthRatioForSplit);

	public abstract double getMinWidthRatioForSplit();
}
