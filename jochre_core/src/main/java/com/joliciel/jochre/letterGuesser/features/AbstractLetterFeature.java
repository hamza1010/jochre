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
package com.joliciel.jochre.letterGuesser.features;

import com.joliciel.jochre.letterGuesser.LetterGuesserContext;
import com.joliciel.talismane.machineLearning.features.AbstractCachableFeature;

/**
 * An Abstract base class for features, basically defining feature equality.
 * @author Assaf Urieli
 *
 */
public abstract class AbstractLetterFeature<Y> extends AbstractCachableFeature<LetterGuesserContext,Y> implements LetterFeature<Y> {
	
}