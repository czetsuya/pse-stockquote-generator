package com.czetsuya.pse;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.time.DateUtils;

/**
 * <p>
 * This utility will parse all the stock quotes in the same directory where this
 * application is run too.
 * </p>
 * <p>
 * It will output the csv stock quote format file in the same directory.
 * </p>
 * 
 * @author Edward P. Legaspi | czetsuya@gmail.com
 * @since 0.0.1
 * @version 0.0.1
 */
public class App {

	public static void main(String[] args) {

		try {
			App app = new App();

			List<File> files = app.init();

			app.processFiles(files);

		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Fin");
	}

	private void processFiles(List<File> files) {

		files.forEach(t -> {
			try {
				execute(t);

			} catch (IOException | ParseException e) {
				e.printStackTrace();
			}
		});
	}

	private List<File> init() {

		String workingDir = System.getProperty("user.dir");

		File dir = new File(workingDir);
		FileFilter fileFilter = new WildcardFileFilter("*.pdf");
		File[] files = dir.listFiles(fileFilter);

		return Arrays.asList(files);
	}

	public void execute(File file) throws IOException, ParseException {

		String fileName = file.getName();
		fileName = FilenameUtils.removeExtension(fileName);
		String strDate = fileName.split("_")[1];
		Date date = DateUtils.parseDate(strDate, com.czetsuya.pse.utils.DateUtils.DATE_FORMAT_IO);

		StockQuotePdfReader stockQuotePdfReader = new StockQuotePdfReader();
		List<String> lines = stockQuotePdfReader.readDoc(file.getCanonicalPath());

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
		result.stream().filter(e -> !Objects.isNull(e.getVolume()))
				.collect(Collectors.groupingBy(StockQuote::getSector,
						Collectors.reducing(BigDecimal.ZERO, StockQuote::getVolume, BigDecimal::add)))
				.forEach((subSector, subSectorTotal) -> System.out
						.println(subSector + " " + formatter.format(subSectorTotal)));

		System.out.println("\nValue Summary");
		result.stream().filter(e -> !Objects.isNull(e.getValue()))
				.collect(Collectors.groupingBy(StockQuote::getSector,
						Collectors.reducing(BigDecimal.ZERO, StockQuote::getValue, BigDecimal::add)))
				.forEach((subSector, subSectorTotal) -> System.out
						.println(subSector + " " + formatter.format(subSectorTotal)));

		System.out.println("\nPrinting the stockQuotes");
		CsvStockWriter csvStockWriter = new CsvStockWriter();
		csvStockWriter.write(date, result);
	}
}
