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
package com.joliciel.jochre.output;

import java.io.StringWriter;
import java.util.List;
import java.util.ArrayList;

import mockit.NonStrict;
import mockit.NonStrictExpectations;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import com.joliciel.jochre.doc.JochreDocument;
import com.joliciel.jochre.doc.JochrePage;
import com.joliciel.jochre.graphics.GroupOfShapes;
import com.joliciel.jochre.graphics.JochreImage;
import com.joliciel.jochre.graphics.Paragraph;
import com.joliciel.jochre.graphics.RowOfShapes;
import com.joliciel.jochre.graphics.Shape;
import com.joliciel.jochre.output.TextFormat;
import com.joliciel.jochre.output.TextGetterImpl;

import static org.junit.Assert.*;

public class TextGetterImplTest {
    private static final Log LOG = LogFactory.getLog(TextGetterImplTest.class);

    @Test
    public void testGetText(@NonStrict final JochreDocument doc,
    		@NonStrict final JochrePage page,
    		@NonStrict final JochreImage jochreImage,
    		@NonStrict final Paragraph paragraph,
    		@NonStrict final RowOfShapes row,
    		@NonStrict final GroupOfShapes group
    		) {
    	final List<Paragraph> paragraphs = new ArrayList<Paragraph>();
    	paragraphs.add(paragraph);
    	final List<RowOfShapes> rows = new ArrayList<RowOfShapes>();
    	rows.add(row);
    	final List<GroupOfShapes> groups = new ArrayList<GroupOfShapes>();
    	groups.add(group);
    	
		new NonStrictExpectations() {
			Shape shape1, shape2, shape3, shape4, shape5, shape6, shape7, shape8, shape9;
			{
         	jochreImage.getPage(); returns(page);
        	page.getDocument(); returns(doc);
        	doc.isLeftToRight(); returns(false);
        	jochreImage.getParagraphs(); returns(paragraphs);
        	paragraph.getRows(); returns(rows);
        	row.getGroups(); returns(groups);
        	List<Shape> shapes = new ArrayList<Shape>();
        	shapes.add(shape1);
        	shapes.add(shape2);
        	shapes.add(shape3);
        	shapes.add(shape4);
        	shapes.add(shape5);
        	shapes.add(shape6);
        	shapes.add(shape7);
        	shapes.add(shape8);
        	shapes.add(shape9);
        	
        	group.getShapes(); returns(shapes);
        	group.getXHeight(); returns(10);
        	shape1.getLetter(); returns(",");
        	shape2.getLetter(); returns(",");
        	shape3.getLetter(); returns("|אַ");
        	shape4.getLetter(); returns("אַ|");
        	shape5.getLetter(); returns("|m");
        	shape6.getLetter(); returns("m|");
        	shape7.getLetter(); returns("|ש");
        	shape8.getLetter(); returns("ע|");
        	shape9.getLetter(); returns(",");
     	
        }};
        
        StringWriter writer = new StringWriter();
		TextGetterImpl textGetter = new TextGetterImpl(writer, TextFormat.PLAIN);
		textGetter.onImageComplete(jochreImage);
		String result = writer.toString();
		LOG.debug(result);
		assertEquals("„אַm|שע|, \n", result);
    }
    
    @Test
    public void testGetTextFontSizes(@NonStrict final JochreDocument doc,
    		@NonStrict final JochrePage page,
    		@NonStrict final JochreImage jochreImage,
    		@NonStrict final Paragraph paragraph,
    		@NonStrict final RowOfShapes row) {
      	final List<Paragraph> paragraphs = new ArrayList<Paragraph>();
     	paragraphs.add(paragraph);
    	final List<RowOfShapes> rows = new ArrayList<RowOfShapes>();
    	rows.add(row);
  
		new NonStrictExpectations() {
			GroupOfShapes group1, group2, group3, group4;
			Shape shape1, shape2, shape3, shape4;
			{
				jochreImage.getPage(); returns(page);
	        	page.getDocument(); returns(doc);
	        	doc.isLeftToRight(); returns(true);
	        	jochreImage.getParagraphs(); returns(paragraphs);
	        	paragraph.getRows(); returns(rows);
	        	List<GroupOfShapes> groups = new ArrayList<GroupOfShapes>();
	        	groups.add(group1);
	        	groups.add(group2);
	        	groups.add(group3);
	        	groups.add(group4);
	        	
	        	row.getGroups(); returns(groups);
	        	
	        	List<Shape> shapes1 = new ArrayList<Shape>();
	        	shapes1.add(shape1);
	        	group1.getShapes(); returns(shapes1);
	        	
	        	List<Shape> shapes2 = new ArrayList<Shape>();
	        	shapes2.add(shape2);
	        	group2.getShapes(); returns(shapes2);
	        	
	        	List<Shape> shapes3 = new ArrayList<Shape>();
	        	shapes3.add(shape3);
	        	group3.getShapes(); returns(shapes3);
	        	
	        	List<Shape> shapes4 = new ArrayList<Shape>();
	        	shapes4.add(shape4);
	        	group4.getShapes(); returns(shapes4);
	        	group1.getXHeight(); returns(10);
	        	group2.getXHeight(); returns(20);
	        	group3.getXHeight(); returns(10);
	        	group4.getXHeight(); returns(5);
	        	
	        	shape1.getLetter(); returns("A");
	        	shape2.getLetter(); returns("B");
	        	shape3.getLetter(); returns("C");
	        	shape4.getLetter(); returns("D");
	        	shape1.getXHeight(); returns(10);
	        	shape2.getXHeight(); returns(20);
	        	shape3.getXHeight(); returns(10);
	        	shape4.getXHeight(); returns(5);
        }};
        
        StringWriter writer = new StringWriter();
		TextGetterImpl textGetter = new TextGetterImpl(writer, TextFormat.XHTML);
		textGetter.onImageComplete(jochreImage);
		String result = writer.toString();
		LOG.debug(result);
		assertEquals("<p dir=\"rtl\">A <big>B </big>C <small>D </small></p>", result);
    }
    
	public void testAppendBidiText() {
	    StringWriter writer = new StringWriter();
		TextGetterImpl textGetter = new TextGetterImpl(writer, TextFormat.XHTML);
		String text = "איך מײן אַז ס'איז 5.01 פּראָצענט, אָדער ebyam אַפֿילו %5.02.";
		textGetter.appendBidiText(text, writer);
		String result = writer.toString();
		LOG.debug(result);
		assertEquals("איך מײן אַז ס'איז 10.5 פּראָצענט, אָדער maybe אַפֿילו 20.5%.", result);
	}

}
