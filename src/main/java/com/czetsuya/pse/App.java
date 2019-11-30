package com.czetsuya.pse;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.DateUtils;

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
				app.execute(DEFAULT_FILENAME, new Date());

			} else if (args.length == 1) {
				System.out.println("Usage App <stockQuoteDaily.pdf> <MMddyyyy>");

			} else {
				app.execute(args[0], DateUtils.parseDate(args[1], com.czetsuya.pse.utils.DateUtils.DATE_FORMAT_IO));
			}

		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}

	public void execute(String filename, Date date) throws IOException {

		StockQuotePdfReader stockQuotePdfReader = new StockQuotePdfReader();
		List<String> lines = stockQuotePdfReader.readDoc(filename);

		StockQuoteExtractor stockQuoteExtractor = new StockQuoteExtractor();
		List<StockQuote> result = stockQuoteExtractor.extract(lines);

		Map<String, Map<String, List<StockQuote>>> x = result.stream()
				.collect(Collectors.groupingBy(StockQuote::getSector, Collectors.groupingBy(StockQuote::getSubSector)));

		System.out.println("\n");
		for (Entry<String, Map<String, List<StockQuote>>> entry1 : x.entrySet()) {
			for (Entry<String, List<StockQuote>> entry2 : entry1.getValue().entrySet()) {
				for (StockQuote sq : entry2.getValue()) {
					System.out.println(entry1.getKey() + "." + entry2.getKey() + "=" + sq.getSymbol() + " "
							+ sq.getVolume() + " " + sq.getValue());
				}
			}
		}

		// write to a csv file
		NumberFormat formatter = NumberFormat.getCurrencyInstance();

		System.out.println("\nVolume Summary");
		result.stream()
				.collect(Collectors.groupingBy(StockQuote::getSector,
						Collectors.reducing(BigDecimal.ZERO, StockQuote::getVolume, BigDecimal::add)))
				.forEach((subSector, subSectorTotal) -> System.out
						.println(subSector + " " + formatter.format(subSectorTotal)));

		System.out.println("\nValue Summary");
		result.stream()
				.collect(Collectors.groupingBy(StockQuote::getSector,
						Collectors.reducing(BigDecimal.ZERO, StockQuote::getValue, BigDecimal::add)))
				.forEach((subSector, subSectorTotal) -> System.out
						.println(subSector + " " + formatter.format(subSectorTotal)));

		// Map<String, List<StockQuote>> x =
		// result.stream().sorted(Comparator.comparing(StockQuote::getSubSector))
		// .collect(Collectors.groupingBy(StockQuote::getSubSector));

		// Map<String, List<StockQuote>> y =
		// x.entrySet().stream().sorted(Map.Entry.comparingByKey())
		// .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) ->
		// e1, LinkedHashMap::new));
		// y.entrySet().forEach(e->System.out.println(e.getKey()));

		System.out.println("\nPrinting the stockQuotes");
		CsvStockWriter csvStockWriter = new CsvStockWriter();
		csvStockWriter.write(date, result);

		System.out.println("Finish");
	}
}
