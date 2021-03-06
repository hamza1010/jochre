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
package com.joliciel.jochre.search.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.joliciel.jochre.utils.JochreException;

/**
 * Any properties read from the config file.
 * 
 * @author Assaf Urieli
 *
 */
public class JochreSearchProperties {
	private static final Logger LOG = LoggerFactory.getLogger(JochreSearchProperties.class);
	private static JochreSearchProperties instance;
	private Properties properties;
	private ServletContext servletContext;
	private Locale locale;

	public static JochreSearchProperties getInstance(ServletContext servletContext) {
		if (instance == null) {
			instance = new JochreSearchProperties(servletContext);
		}
		return instance;
	}

	public static void purgeInstance() {
		LOG.debug("purgeInstance");
		instance = null;
	}

	private JochreSearchProperties(ServletContext servletContext) {
		try {
			this.servletContext = servletContext;
			String cfhPropertiesPath = "/WEB-INF/jochre.properties";
			String realPath = servletContext.getRealPath(cfhPropertiesPath);
			LOG.info("Loading new JochreSearchProperties from " + realPath);
			properties = new Properties();
			FileInputStream inputStream = new FileInputStream(realPath);
			properties.load(inputStream);
			for (Object keyObj : properties.keySet()) {
				String key = (String) keyObj;
				LOG.info(key + "=" + properties.getProperty(key));
			}
		} catch (IOException e) {
			LOG.error("Failed to construct" + this.getClass().getSimpleName(), e);
			throw new RuntimeException(e);
		}

	}

	public Properties getProperties() {
		return properties;
	}

	public String getIndexDirPath() {
		String indexDirPath = this.properties.getProperty("index.dir");
		if (indexDirPath == null)
			throw new JochreException("Missing property: index.dir");
		return indexDirPath;
	}

	public String getContentDirPath() {
		String contentDirPath = this.properties.getProperty("content.dir");
		if (contentDirPath == null)
			throw new JochreException("Missing property: content.dir");
		return contentDirPath;
	}

	/**
	 * Return a path to the database properties, if the file exists, otherwise
	 * null.
	 */
	public String getDatabasePropertiesPath() {
		String jdbcPropertiesPath = "/WEB-INF/jdbc.properties";
		String realPath = servletContext.getRealPath(jdbcPropertiesPath);

		File dataSourcePropsFile = new File(realPath);
		if (!dataSourcePropsFile.exists())
			return null;

		return realPath;
	}

	public String getLexiconPath() {
		return this.properties.getProperty("lexicon");
	}

	public Locale getLocale() {
		if (locale == null) {
			String language = this.properties.getProperty("language");
			if (language == null)
				throw new JochreException("Missing property: language");
			locale = Locale.forLanguageTag(language);
		}
		return locale;
	}

}
