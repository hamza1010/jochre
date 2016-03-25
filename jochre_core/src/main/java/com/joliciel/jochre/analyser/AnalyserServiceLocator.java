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
package com.joliciel.jochre.analyser;

import com.joliciel.jochre.JochreServiceLocator;

public class AnalyserServiceLocator {
	AnalyserServiceImpl analyserService = null;
	private JochreServiceLocator jochreServiceLocator;
	
	public AnalyserServiceLocator(JochreServiceLocator jochreServiceLocator) {
		this.jochreServiceLocator = jochreServiceLocator;
	}
	
	public AnalyserService getAnalyserService() {
		if (analyserService==null) {
			analyserService = new AnalyserServiceImpl();
			analyserService.setObjectCache(this.jochreServiceLocator.getObjectCache());
			analyserService.setGraphicsService(this.getJochreServiceLocator().getGraphicsServiceLocator().getGraphicsService());
			analyserService.setLetterGuesserService(this.jochreServiceLocator.getLetterGuesserServiceLocator().getLetterGuesserService());
			analyserService.setBoundaryService(this.jochreServiceLocator.getBoundaryServiceLocator().getBoundaryService());
		}
		return analyserService;
	}
	

	public JochreServiceLocator getJochreServiceLocator() {
		return jochreServiceLocator;
	}
	
	
}