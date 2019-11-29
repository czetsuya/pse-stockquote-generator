package com.czetsuya.pse;

import java.io.IOException;
import java.util.List;

/**
 * @author Edward P. Legaspi | czetsuya@gmail.com
 * @since 0.0.1
 * @version 0.0.1
 */
public class App {

	private static final String DEFAULT_FILENAME = "stockQuotes_11282019.pdf";

	public static void main(String[] args) {

		try {
			App app = new App();

			if (args.length == 0) {
				app.execute(DEFAULT_FILENAME);

			} else {
				app.execute(args[0]);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void execute(String filename) throws IOException {

		StockQuotePdfReader stockQuotePdfReader = new StockQuotePdfReader();
		List<String> lines = stockQuotePdfReader.readDoc(filename);

		StockQuoteExtractor stockQuoteExtractor = new StockQuoteExtractor();
		List<StockQuote> result = stockQuoteExtractor.extract(lines);

		// write to a csv file
	}
}
