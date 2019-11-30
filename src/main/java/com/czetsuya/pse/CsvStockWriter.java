package com.czetsuya.pse;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.time.DateFormatUtils;

import com.czetsuya.pse.utils.DateUtils;

/**
 * @author Edward P. Legaspi | czetsuya@gmail.com
 * @since 0.0.1
 * @version 0.0.1
 */
public class CsvStockWriter {

	public void write(Date date, List<StockQuote> stockQuotes) throws IOException {

		FileWriter fw = new FileWriter(
				"stockQuotes_" + DateFormatUtils.format(date, DateUtils.DATE_FORMAT_IO) + ".csv");

		final String strDate = DateFormatUtils.format(date, DateUtils.DATE_FORMAT);
		try (CSVPrinter csvPrinter = new CSVPrinter(fw, CSVFormat.DEFAULT)) {
			stockQuotes.forEach(e -> {
				try {
					csvPrinter.printRecord(e.getSymbol(), strDate, e.getOpen(), e.getHigh(), e.getLow(), e.getClose(),
							e.getVolume(), e.getForeignSellingOrBuying());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			});
		}
	}
}
