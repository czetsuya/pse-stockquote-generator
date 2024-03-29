package com.czetsuya.pse;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.text.WordUtils;

/**
 * @author Edward P. Legaspi | czetsuya@gmail.com
 * @version 0.0.1
 * @since 0.0.1
 */
public class StockQuoteExtractor {

  public List<StockQuote> extract(List<String> lines) {

    return extractSectorGroup(lines);
  }

  private String getSubSector(String currentLine) {
    return StringUtils.substringsBetween(currentLine, "****", "****")[0];
  }

  private List<StockQuote> extractSectorGroup(List<String> lines) {

    SectorEnum currentSector = SectorEnum.FINANCIALS;
    String subSector = "";
    List<StockQuote> result = new ArrayList<>();

    int lineProcessorIndex = -1;
    int state = 0;

    for (int i = 0; i < lines.size(); i++) {
      String currentLine = lines.get(i);

      switch (state) {
        case 0:
          if (SectorEnum.contains(currentLine)) {
            currentSector = SectorEnum.findSector(currentLine);
            lineProcessorIndex = i + 1;

            System.out.println("\n===>Processing sector " + currentSector.getSectorName());

            if (lines.get(i + 1).contains("****")) {
              state = 1;

            } else {
              state = 2;
            }

          } else if (currentLine.startsWith("****")) {
            subSector = getSubSector(currentLine);
            lineProcessorIndex = i + 1;
            state = 1;

          } else if (StringUtils.deleteWhitespace(currentLine).toLowerCase().contains("sectoralsummary")) {
            lineProcessorIndex = i + 2;
            state = 3;

          } else if (currentLine.toLowerCase().startsWith("foreign buying")) {
            System.out.println("\n===>Foreign buying=" + currentLine.substring(currentLine.indexOf("Php") + 4));

          } else if (currentLine.toLowerCase().startsWith("foreign selling")) {
            state = 100;
            System.out
                .println("\n===>Foreign selling=" + currentLine.substring(currentLine.indexOf("Php") + 4));

          }
          break;

        case 1:
          if (currentLine.startsWith("****")) {
            result.addAll(extractStockQuote(currentSector, subSector, lines.subList(lineProcessorIndex, i)));
            subSector = getSubSector(currentLine);
            System.out.println("\n===>Processing sub sector " + subSector);
            lineProcessorIndex = i + 1;

          } else if (currentLine.toLowerCase().contains("sector total")) {
            result.addAll(extractStockQuote(currentSector, subSector, lines.subList(lineProcessorIndex, i)));
            state = 0;
          }
          break;

        case 2:
          if (currentLine.startsWith("****")) {
            result.addAll(extractStockQuote(currentSector, subSector, lines.subList(lineProcessorIndex, i)));
            subSector = getSubSector(currentLine);
            System.out.println("\n===>Processing sub sector " + subSector);
            lineProcessorIndex = i + 1;
            state = 1;

          } else if (currentLine.toLowerCase().contains("total")
              && currentLine.toLowerCase().contains("volume")) {
            result.addAll(extractStockQuote(currentSector, subSector, lines.subList(lineProcessorIndex, i)));
            state = 0;
          }
          break;

        case 3:
          if (currentLine.toLowerCase().contains("grand total")) {
            result.addAll(extractSectorQuote(lines.subList(lineProcessorIndex, i)));
            state = 0;
          }
          break;
        case 100:
          break;
        default:
          break;
      }

    }

    return result;
  }

  private List<StockQuote> extractSectorQuote(List<String> lines) {

    List<StockQuote> result = new ArrayList<>();
    lines.forEach(line -> {
      String[] lineArr = StringUtils.split(line, " ");

      if (SectorEnum.containsSectorFromSummary(lineArr[0])) {

        SectorEnum sector = SectorEnum.findSectorFromSummary(lineArr[0]);

        StockQuote sq;

        if (sector == SectorEnum.PSEI || sector == SectorEnum.ALLSHARES) {
          sq = StockQuote.builder() //
              .symbol(sector.getCsvName()) //
              .open(parseNumber(lineArr[1])) //
              .high(parseNumber(lineArr[2])) //
              .low(parseNumber(lineArr[3])) //
              .close(parseNumber(lineArr[4])) //
              .percentChange(parseNumber(lineArr[5])) //
              .build();

        } else {
          sq = StockQuote.builder() //
              .symbol(sector.getCsvName()) //
              .open(parseNumber(lineArr[1])) //
              .high(parseNumber(lineArr[2])) //
              .low(parseNumber(lineArr[3])) //
              .close(parseNumber(lineArr[4])) //
              .percentChange(parseNumber(lineArr[5])) //
              .volume(parseNumber(lineArr[7])) //
              .value(parseNumber(lineArr[8])) //
              .build();
        }

        sq.setSector(sector.getCsvName());
        sq.setSubSector(sector.getCsvName());

        result.add(sq);
      }
    });

    return result;
  }

  @SuppressWarnings("unused")
  private BigDecimal average(List<StockQuote> stockQuotes, Function<StockQuote, BigDecimal> mapper) {

    BigDecimal sum = stockQuotes.stream().collect(Collectors.reducing(BigDecimal.ZERO, mapper, BigDecimal::add));

    return sum.divide(new BigDecimal(stockQuotes.size()), RoundingMode.HALF_DOWN);
  }

  @SuppressWarnings("unused")
  private StockQuote computeAverage(List<StockQuote> stockQuotes) {

    StockQuote sq = new StockQuote();
    stockQuotes.stream().forEach(e -> {
      sq.addStockQuote(e);
      sq.computeAverage(stockQuotes.size());
    });

    return sq;
  }

  private StockQuote addStockQuote(List<StockQuote> stockQuotes) {

    StockQuote sq = new StockQuote();
    stockQuotes.stream().forEach(e -> {
      sq.addStockQuote(e);
    });

    return sq;
  }

  @SuppressWarnings("unused")
  private void computeSector(List<StockQuote> stockQuotes) {

    List<StockQuote> sectorQuotes = new ArrayList<>();

    StockQuote pseiQuote = new StockQuote();
    pseiQuote.setSector("^PSEI");
    pseiQuote.setSubSector("^PSEI");
    pseiQuote.setSymbol("^PSEI");

    Map<String, List<StockQuote>> groupedSq = stockQuotes.stream()
        .collect(Collectors.groupingBy(StockQuote::getSector));

    groupedSq.entrySet().forEach(e -> {
      SectorEnum sectorEnum = SectorEnum.findSector(e.getKey());
      if (sectorEnum.isToProcess()) {
        StockQuote sq = addStockQuote(e.getValue());
        sq.setSector(sectorEnum.getSectorName());
        sq.setSubSector(sectorEnum.getSectorName());
        sq.setSymbol(sectorEnum.getCsvName());
        sectorQuotes.add(sq);

        pseiQuote.addStockQuote(sq);
      }
    });

    stockQuotes.addAll(sectorQuotes);
    stockQuotes.add(pseiQuote);
  }

  private List<StockQuote> extractStockQuote(SectorEnum currentSector, String subSector, List<String> lines) {

    List<StockQuote> result = new ArrayList<>();

    for (String line : lines) {
      if (StringUtils.isBlank(line) || line.contains("sector total")) {
        continue;
      }

      Optional<StockQuote> sqNullable = extractQuoteFromLine(line);
      if (sqNullable.isPresent()) {
        StockQuote sq = sqNullable.get();
        sq.setSector(WordUtils.capitalizeFully(currentSector.formatSectorName()));
        sq.setSubSector(WordUtils.capitalizeFully(subSector.trim()));

        result.add(sq);
      }
    }

    return result;
  }

  private Optional<StockQuote> extractQuoteFromLine(String line) {

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

  private Optional<StockQuote> buildStockQuote(List<String> line) {

    try {
      return Optional.ofNullable(StockQuote.builder().symbol(line.get(0)) //
          .bid(parseNumber(line.get(1))) //
          .ask(parseNumber(line.get(2))) //
          .open(parseNumber(line.get(3))) //
          .high(parseNumber(line.get(4))) //
          .low(parseNumber(line.get(5))) //
          .close(parseNumber(line.get(6))) //
          .volume(parseNumber(line.get(7))) //
          .value(parseNumber(line.get(8))) //
          .build()); //

    } catch (Exception e) {
      System.out.println("Line" + line + "\r\nFail: " + e.getMessage());
      //throw new RuntimeException("Breaking updates from PSE. Contact the developer of this app.");
      return Optional.empty();
    }
  }

  private static BigDecimal parseNumber(String input) {

    if (StringUtils.isBlank(input) || input.equals("-")) {
      return BigDecimal.ZERO;
    }

    BigDecimal result = new BigDecimal(0);
    try {
      input = removeAllChars(input);
      return new BigDecimal(input);

    } catch (NumberFormatException e) {
      System.out.println(e.getMessage());
    }

    return result;
  }

  private static String removeAllChars(String str) {

    String forbidden = ",";
    for (int i = 0; i < forbidden.length(); i++) {
      char c = forbidden.charAt(i);
      str = StringUtils.remove(str, c);
    }

    if (str.equals("-")) {
      str = "";
    }

    // if contains * then negate
    str = com.czetsuya.pse.utils.StringUtils.negate(str);

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
