package com.czetsuya.pse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.text.WordUtils;

public class StockQuoteExtractor {

	public List<StockQuote> extract(List<String> lines) {

		return extractSectorGroup(lines);
	}

	private List<StockQuote> extractSectorGroup(List<String> lines) {

		SectorEnum currentSector = SectorEnum.FINANCIALS;
		String subSector = "";
		List<StockQuote> result = new ArrayList<>();

		int lineProcessorIndex = -1;
		for (int i = 0; i < lines.size(); i++) {
			String currentLine = lines.get(i);

			if (SectorEnum.contains(currentLine)) {
				currentSector = SectorEnum.findSector(currentLine);
				System.out.println("\nProcessing sector=" + currentSector + " line ===> " + currentLine);
				lineProcessorIndex = i + 1;

			} else {
				if (currentLine.startsWith("****")) {
					subSector = StringUtils.substringsBetween(currentLine, "****", "****")[0];
					if (lineProcessorIndex == -1) {
						lineProcessorIndex = i + 1;

					} else {
						result.addAll(
								extractStockQuote(currentSector, subSector, lines.subList(lineProcessorIndex, i)));
						lineProcessorIndex = i + 1;
					}

					System.out.println("=======>" + currentSector.formatSectorName() + " - " + subSector);

				} else if (currentLine.toLowerCase().contains("sector total")) {
					result.addAll(extractStockQuote(currentSector, subSector, lines.subList(lineProcessorIndex, i)));

				} else if (currentLine.toLowerCase()
						.contains(currentSector.getSectorName().toLowerCase().concat(" total"))) {
					result.addAll(extractStockQuote(currentSector, subSector, lines.subList(lineProcessorIndex, i)));
				}
			}
		}

		return result;
	}

	private List<StockQuote> extractStockQuote(SectorEnum currentSector, String subSector, List<String> lines) {

		List<StockQuote> result = new ArrayList<>();

		for (String line : lines) {
			if (StringUtils.isBlank(line) || line.contains("sector total")) {
				continue;
			}

			StockQuote sq = extractQuoteFromLine(line);
			if (sq == null) {
				continue;
			}
			sq.setSector(WordUtils.capitalizeFully(currentSector.formatSectorName()));
			sq.setSubSector(WordUtils.capitalizeFully(subSector.trim()));

			result.add(sq);
		}

		return result;
	}

	private StockQuote extractQuoteFromLine(String line) {

		System.out.println("processing line=" + line);

		if (line.contains("SMC FB PREF")) {
			int i = 0;
		}

		String[] splittedLine = StringUtils.split(line, " ");
		int stockIndex = 0;
		for (int i = splittedLine.length - 1; i > 0; i--) {
			// remove the , character in string
			// check if the column contains "-", it means an empty number field
			// why not just put 0?
			String cellValue = splittedLine[i];
			if (StringUtils.isNotEmpty(cellValue)) {
				cellValue = StringUtils.remove(splittedLine[i], ",");
				cellValue = StringUtils.remove(cellValue, "(");
				cellValue = StringUtils.remove(cellValue, ")");
				if (!cellValue.equals("-") && !NumberUtils.isCreatable(cellValue)) {
					stockIndex = i;
					break;
				}
			}
		}

		List<String> stockData = Arrays.asList(splittedLine).subList(stockIndex, splittedLine.length);

		return buildStockQuote(stockData);
	}

	private StockQuote buildStockQuote(List<String> line) {

		line = line.stream().map(StockQuoteExtractor::removeAllChars).collect(Collectors.toList());

		return StockQuote.builder().symbol(line.get(0)) //
				.bid(parseNumber(line.get(1))) //
				.ask(parseNumber(line.get(2))) //
				.open(parseNumber(line.get(3))) //
				.high(parseNumber(line.get(4))) //
				.low(parseNumber(line.get(5))) //
				.close(parseNumber(line.get(6))) //
				.volume(parseNumber(line.get(7))) //
				.value(parseNumber(line.get(8))) //
				.foreignSellingOrBuying(parseNumber(line.get(9))) //
				.build(); //
	}

	private static BigDecimal parseNumber(String input) {

		BigDecimal result = new BigDecimal(0);
		try {
			return StringUtils.isBlank(input) ? new BigDecimal(0) : new BigDecimal(input);

		} catch (NumberFormatException e) {
			System.out.println(e.getMessage());
		}

		return result;
	}

	private static String removeAllChars(String str) {

		String forbidden = ",-";
		for (int i = 0; i < forbidden.length(); i++) {
			char c = forbidden.charAt(i);
			str = StringUtils.remove(str, c);
		}

		// if contains * then negate
		if (str.contains("(") && str.contains(")")) {
			str = StringUtils.substringBetween(str, "(", ")");
			str = "-" + str;
		}

		return str;
	}

	public SectorEnum getParentSector(List<String> lines, int currentRow) {

		SectorEnum currentSector;
		String previousLine = lines.get(currentRow - 1);
		if (SectorEnum.contains(previousLine)) {
			currentSector = SectorEnum.findSector(previousLine);

		} else {
			currentSector = getParentSector(lines, currentRow - 1);
		}

		return currentSector;
	}

}
