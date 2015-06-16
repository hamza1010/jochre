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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;

import com.joliciel.jochre.search.JochreIndexSearcher;
import com.joliciel.jochre.search.JochreQuery;
import com.joliciel.jochre.search.SearchService;
import com.joliciel.jochre.search.SearchServiceLocator;
import com.joliciel.jochre.search.highlight.HighlightManager;
import com.joliciel.jochre.search.highlight.HighlightService;
import com.joliciel.jochre.search.highlight.HighlightServiceLocator;
import com.joliciel.jochre.search.highlight.Highlighter;
import com.joliciel.jochre.search.highlight.ImageSnippet;
import com.joliciel.jochre.search.highlight.Snippet;
import com.joliciel.talismane.utils.LogUtils;

/**
 * Restful web service for Jochre search.
 * @author Assaf Urieli
 *
 */
public class JochreSearchServlet extends HttpServlet {
	private static final Log LOG = LogFactory.getLog(JochreSearchServlet.class);
	private static final long serialVersionUID = 1L;
	
	private JochreIndexSearcher searcher = null;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse response)
			throws ServletException, IOException {
		this.doGet(req, response);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			response.addHeader("Access-Control-Allow-Origin", "*");
			response.setCharacterEncoding("UTF-8");
			
			JochreSearchProperties props = JochreSearchProperties.getInstance(this.getServletContext());
			
			String command = req.getParameter("command");
			if (command==null) {
				command="search";
			}
			
			if (command.equals("purge")) {
				JochreSearchProperties.purgeInstance();
				searcher = null;
				return;
			}
			
			Map<String,String> argMap = new HashMap<String, String>();
			@SuppressWarnings("rawtypes")
			Enumeration params = req.getParameterNames();
			while (params.hasMoreElements()) {
				String paramName = (String) params.nextElement();
				String value = req.getParameter(paramName);
				argMap.put(paramName, value);
			}
			
			SearchServiceLocator searchServiceLocator = SearchServiceLocator.getInstance();
			SearchService searchService = searchServiceLocator.getSearchService();
	
			double minWeight = 0;
			int titleSnippetCount = 1;
			int snippetCount = 3;
			int snippetSize = 80;
			boolean includeText = false;
			boolean includeGraphics = false;
			String snippetJson = null;
			Set<Integer> docIds = null;
			
			Set<String> handledArgs = new HashSet<String>();
			for (Entry<String, String> argEntry : argMap.entrySet()) {
				String argName = argEntry.getKey();
				String argValue = argEntry.getValue();
				argValue = URLDecoder.decode(argValue, "UTF-8");
				LOG.debug(argName + ": " + argValue);
				
				boolean handled = true;
				if (argName.equals("minWeight")) {
					minWeight = Double.parseDouble(argValue);
				} else if (argName.equals("titleSnippetCount")) {
					titleSnippetCount = Integer.parseInt(argValue);
				} else if (argName.equals("snippetCount")) {
					snippetCount = Integer.parseInt(argValue);
				} else if (argName.equals("snippetSize")) {
					snippetSize = Integer.parseInt(argValue);
				} else if (argName.equals("includeText")) {
					includeText = argValue.equalsIgnoreCase("true");
				} else if (argName.equals("includeGraphics")) {
					includeGraphics = argValue.equalsIgnoreCase("true");
				} else if (argName.equals("snippet")) {
					snippetJson = argValue;
				} else if (argName.equalsIgnoreCase("docIds")) {
					if (argValue.length()>0) {
						String[] idArray = argValue.split(",");
						docIds = new HashSet<Integer>();
						for (String id : idArray)
							docIds.add(Integer.parseInt(id));
					}
				} else {
					handled = false;
				}
				if (handled) {
					handledArgs.add(argName);
				}
			}
			for (String argName : handledArgs) argMap.remove(argName);
	
			if (searcher==null) {
				String indexDirPath = props.getIndexDirPath();
				File indexDir = new File(indexDirPath);
				LOG.info("Index dir: " + indexDir.getAbsolutePath());
				searcher = searchService.getJochreIndexSearcher(indexDir);
			}
			
			if (command.equals("search")) {		
				response.setContentType("text/plain;charset=UTF-8");
				PrintWriter out = response.getWriter();
				JochreQuery query = searchService.getJochreQuery(argMap);
				searcher.search(query, out);
				out.flush();
			} else if (command.equals("highlight") || command.equals("snippets")) {
				response.setContentType("text/plain;charset=UTF-8");
				PrintWriter out = response.getWriter();
				JochreQuery query = searchService.getJochreQuery(argMap);
				if (docIds==null)
					throw new RuntimeException("Command " + command + " requires docIds");
				
				HighlightServiceLocator highlightServiceLocator = HighlightServiceLocator.getInstance(searchServiceLocator);
				HighlightService highlightService = highlightServiceLocator.getHighlightService();
				Highlighter highlighter = highlightService.getHighlighter(query, searcher.getIndexSearcher());
				HighlightManager highlightManager = highlightService.getHighlightManager(searcher.getIndexSearcher());
				highlightManager.setDecimalPlaces(query.getDecimalPlaces());
				highlightManager.setMinWeight(minWeight);
				highlightManager.setIncludeText(includeText);
				highlightManager.setIncludeGraphics(includeGraphics);
				highlightManager.setTitleSnippetCount(titleSnippetCount);
				highlightManager.setSnippetCount(snippetCount);
				highlightManager.setSnippetSize(snippetSize);
	
				Set<String> fields = new HashSet<String>();
				fields.add("text");
				
				if (command.equals("highlight"))
					highlightManager.highlight(highlighter, docIds, fields, out);
				else
					highlightManager.findSnippets(highlighter, docIds, fields, out);
				out.flush();
			} else if (command.equals("imageSnippet")) {
				String mimeType="image/png";
				response.setContentType(mimeType);
				if (snippetJson==null)
					throw new RuntimeException("Command " + command + " requires a snippet");
				Snippet snippet = new Snippet(snippetJson);
				
				if (LOG.isDebugEnabled()) {
					Document doc = searcher.getIndexSearcher().doc(snippet.getDocId());
					LOG.debug("Snippet in " + doc.get("id") + ", path: " + doc.get("path"));
				}
				
				HighlightServiceLocator highlightServiceLocator = HighlightServiceLocator.getInstance(searchServiceLocator);
				HighlightService highlightService = highlightServiceLocator.getHighlightService();
				HighlightManager highlightManager = highlightService.getHighlightManager(searcher.getIndexSearcher());
				ImageSnippet imageSnippet = highlightManager.getImageSnippet(snippet);
				OutputStream os = response.getOutputStream();
				ImageOutputStream ios = ImageIO.createImageOutputStream(os);
				BufferedImage image = imageSnippet.getImage();
				ImageReader imageReader = ImageIO.getImageReadersByMIMEType(mimeType).next();
				ImageWriter imageWriter = ImageIO.getImageWriter(imageReader);
				imageWriter.setOutput(ios);
				imageWriter.write(image);
				ios.flush();
			} else {
				throw new RuntimeException("Unknown command: " + command);
			}
		} catch (RuntimeException e) {
			LogUtils.logError(LOG, e);
			throw e;
		}
	}
}