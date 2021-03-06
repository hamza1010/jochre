///////////////////////////////////////////////////////////////////////////////
//Copyright (C) 2016 Joliciel Informatique
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
package com.joliciel.jochre.search.lexicon;

import com.joliciel.jochre.search.SearchServiceLocator;

public class LexiconServiceLocator {
	private static LexiconServiceLocator instance;
	private LexiconServiceImpl lexiconService;
	@SuppressWarnings("unused")
	private SearchServiceLocator searchServiceLocator;

	public LexiconServiceLocator(SearchServiceLocator searchServiceLocator) {
		this.searchServiceLocator = searchServiceLocator;
	}

	public static LexiconServiceLocator getInstance(SearchServiceLocator searchServiceLocator) {
		if (instance==null) {
			instance = new LexiconServiceLocator(searchServiceLocator);
		}
		return instance;
	}
	
	public LexiconService getLexiconService() {
		if (lexiconService==null) {
			lexiconService = new LexiconServiceImpl();
		}
		return lexiconService;
	}
}
