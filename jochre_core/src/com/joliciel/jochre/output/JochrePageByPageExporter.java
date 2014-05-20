package com.joliciel.jochre.output;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.joliciel.jochre.doc.DocumentObserver;
import com.joliciel.jochre.doc.JochreDocument;
import com.joliciel.jochre.doc.JochrePage;
import com.joliciel.jochre.graphics.JochreImage;
import com.joliciel.talismane.utils.LogUtils;

import freemarker.cache.NullCacheStorage;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
* Outputs to Jochre's lossless XML format on a page-by-page basis, along with the image.
**/
class JochrePageByPageExporter implements DocumentObserver {
	private static final Log LOG = LogFactory.getLog(JochrePageByPageExporter.class);
	private static String SUFFIX = "png";
	private Template template;
	private File outDir;
	private String baseName;

	JochreImage jochreImage = null;
	
	public JochrePageByPageExporter(File outDir, String baseName) {
		super();
		try {
			this.outDir = outDir;
			outDir.mkdirs();
			this.baseName = baseName;
			Configuration cfg = new Configuration();
			cfg.setCacheStorage(new NullCacheStorage());
			cfg.setObjectWrapper(new DefaultObjectWrapper());
	
			Reader templateReader = new BufferedReader(new InputStreamReader(JochrePageByPageExporter.class.getResourceAsStream("jochre.ftl")));
			this.template = new Template("freemarkerTemplate", templateReader, cfg);
		} catch (IOException ioe) {
			LogUtils.logError(LOG, ioe);
			throw new RuntimeException(ioe);
		}
	}
	
	@Override
	public void onImageStart(JochreImage jochreImage) {
		this.jochreImage = jochreImage;
		File outputFile = new File(outDir, this.getImageBaseName(jochreImage) + "." + SUFFIX);
		try {
			outputFile.delete();
			ImageIO.write(jochreImage.getOriginalImage(),SUFFIX,outputFile);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onDocumentStart(JochreDocument jochreDocument) {
	}

	@Override
	public void onPageStart(JochrePage jochrePage) {
	}

	@Override
	public void onImageComplete(JochreImage jochreImage) {
		try {
			File outputFile = new File(outDir, this.getImageBaseName(jochreImage) + ".xml");
			outputFile.delete();
			Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile, true),"UTF8"));
			Map<String,Object> model = new HashMap<String, Object>();
			model.put("image", jochreImage);
			template.process(model, writer);
			writer.flush();
		} catch (TemplateException te) {
			LogUtils.logError(LOG, te);
			throw new RuntimeException(te);
		} catch (IOException ioe) {
			LogUtils.logError(LOG, ioe);
			throw new RuntimeException(ioe);
		}
	}
	
	String getImageBaseName(JochreImage jochreImage) {
		String imageBaseName = baseName + "_" + String.format("%04d", jochreImage.getPage().getIndex());
		if (jochreImage.getPage().getImages().size()>1)
			imageBaseName += "_" + String.format("%02d", jochreImage.getIndex());
		return imageBaseName;
	}


	@Override
	public void onPageComplete(JochrePage jochrePage) {
	}

	@Override
	public void onDocumentComplete(JochreDocument jochreDocument) {
	}
}
