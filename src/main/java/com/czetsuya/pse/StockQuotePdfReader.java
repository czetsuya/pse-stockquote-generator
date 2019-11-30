package com.czetsuya.pse;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 * @author Edward P. Legaspi | czetsuya@gmail.com
 * @since 0.0.1
 * @version 0.0.1
 */
public class StockQuotePdfReader {

	public List<String> readDoc(String filename) throws IOException {

		List<String> result = new ArrayList<>();

		InputStream is = new FileInputStream(filename);

		PDDocument document = PDDocument.load(is);
		AccessPermission ap = document.getCurrentAccessPermission();
		if (!ap.canExtractContent()) {
			throw new IOException("You do not have permission to extract text");
		}

		PDFTextStripper stripper = new PDFTextStripper();

		// This example uses sorting, but in some cases it is more useful to switch it
		// off,
		// e.g. in some files with columns where the PDF content stream respects the
		// column order.
		stripper.setSortByPosition(true);

		for (int p = 1; p <= document.getNumberOfPages(); ++p) {
			// Set the page interval to extract. If you don't, then all pages would be
			// extracted.
			stripper.setStartPage(p);
			stripper.setEndPage(p);

			String text = stripper.getText(document);

			result.addAll(readPage(text));
		}

		document.close();
		is.close();

		return result;
	}

	public List<String> readPage(String text) throws IOException {

		List<String> result = new ArrayList<>();
		BufferedReader reader = new BufferedReader(new StringReader(text));
		reader.lines().skip(3).filter(e -> !e.toLowerCase().contains("name symbol")
				&& !e.equalsIgnoreCase("buying/(selling),") && !e.equalsIgnoreCase("net foreign")).forEach(result::add);

		return result;
	}
}
