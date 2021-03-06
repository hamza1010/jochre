///////////////////////////////////////////////////////////////////////////////
//Copyright (C) 2014 Assaf Urieli
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
package com.joliciel.jochre.search.alto;

import java.awt.Rectangle;
import java.util.Set;

import com.joliciel.jochre.search.JochreToken;

public interface AltoString extends JochreToken {
	/**
	 * Style indicating emphasis through individual character separation.
	 */
	public static final String SEP_EMPH_STYLE = "SEP_EMPH";

	public String getContent();

	public void setContent(String content);

	public AltoTextLine getTextLine();

	/**
	 * Alternative possibilities for the current string.
	 */
	public Set<String> getAlternatives();

	public void setAlternatives(Set<String> alternatives);

	/**
	 * The confidence assigned by the OCR model to this string, from 0 to 1.
	 */
	public double getConfidence();

	public void setConfidence(double confidence);

	/**
	 * Is this string a hyphen within a hyphenated word?
	 */
	public boolean isHyphen();

	public void setHyphen(boolean hyphen);

	/**
	 * Is this string the first half of a hyphenated word which crosses an
	 * end-of-line.
	 */
	public boolean isHyphenStart();

	public void setHyphenStart(boolean hyphenStart);

	/**
	 * Is this string the second half of a hyphenated word which corsses an
	 * end-of-line.
	 */
	public boolean isHyphenEnd();

	public void setHyphenEnd(boolean hyphenEnd);

	/**
	 * The full hyphenated word, or null if string is not part of hyphenated
	 * word.
	 */
	public String getHyphenatedContent();

	public void setHyphenatedContent(String hyphenatedContent);

	/**
	 * The index of this string within the current TextLine.
	 */
	@Override
	public int getIndex();

	public void setIndex(int index);

	/**
	 * The starting position of this string's character span, where 0 is
	 * considered to be the start of the current page.
	 */
	@Override
	public abstract int getSpanStart();

	@Override
	public abstract void setSpanStart(int spanStart);

	/**
	 * The position just after the last character in this string.
	 */
	@Override
	public abstract int getSpanEnd();

	@Override
	public abstract void setSpanEnd(int spanEnd);

	/**
	 * Does this string represent white space?
	 */
	public boolean isWhiteSpace();

	/**
	 * Does this string represent punctuation?
	 */
	@Override
	public boolean isPunctuation();

	public void setRectangle(Rectangle rectangle);

	/**
	 * Merge a given AltoString with the next string in the line or the first
	 * string on the following line (if it's the last in the line), removing any
	 * intervening whitespaces.
	 * 
	 * @return true if merge occurred, false otherwise (whitespace)
	 */
	public boolean mergeWithNext();

	public String getStyle();

	public void setStyle(String style);
}
