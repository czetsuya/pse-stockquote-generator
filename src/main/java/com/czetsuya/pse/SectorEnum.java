package com.czetsuya.pse;

import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;

/**
 * @author Edward P. Legaspi | czetsuya@gmail.com
 * @since 0.0.1
 * @version 0.0.1
 */
@Getter
public enum SectorEnum {

	FINANCIALS("Financials", "^FINANCIAL", "Financials", true), //
	INDUSTRIAL("Industrial", "^INDUSTRIAL", "Industrials", true), //
	HOLDING("HoldingFirms", "^HOLDING", "Holding Firms", true), //
	PROPERTY("Property", "^PROPERTY", "Property", true), //
	SERVICES("Services", "^SERVICE", "Services", true), //
	MINING("Mining&Oil", "^MINING-OIL", "Mining & Oil", true), //
	PREFERRED("Preferred", "^PREFERRED", "Preferred", false), //
	PHILDEPOSITARY("Phil.DepositaryReceipts", "^PHILDEPO", "Phil Depositary", false), //
	WARRANTS("Warrants", "^WARRANTS", "Warrants", false), //
	SMALLMEDIUMEMERGING("Small,Medium&Emerging", "^SMALLMEDIUMEMERGING", "Small, Medium & Emerging", false), //
	EXCHANGETRADEDFUNDS("ExchangeTradedFunds", "^EXCHANGETRADED", "Exchange Traded Funds", false), //
	DOLLARDENOMINATED("DollarDenominatedSecurities", "^DOLLAR", "Dollar Denominated", false), //
	PSEI("PSEI", "^PSEI", "PSEi", false), //
	ALLSHARES("ALLSHARES", "^ALLSHARES", "All", false);

	private String sectorName;
	private String csvName;
	private String sectorSummaryName;
	private boolean toProcess;

	private SectorEnum(String sectorName, String csvName, String sectorSummaryName, boolean toProcess) {
		this.sectorName = sectorName;
		this.csvName = csvName;
		this.sectorSummaryName = sectorSummaryName;
		this.toProcess = toProcess;
	}

	public static boolean contains(String param) {

		param = StringUtils.deleteWhitespace(param);
		final String finalParam = param;

		return Stream.of(SectorEnum.values())
				.anyMatch(e -> e.sectorName.equalsIgnoreCase(StringUtils.deleteWhitespace(finalParam)));
	}

	public static SectorEnum findSector(String param) {

		param = StringUtils.deleteWhitespace(param);

		final String finalParam = param;

		Optional<SectorEnum> result = Stream.of(SectorEnum.values())
				.filter(e -> e.sectorName.equalsIgnoreCase(finalParam)).findAny();

		if (result.isPresent()) {
			return result.get();
		}

		return SectorEnum.DOLLARDENOMINATED;
	}

	public String formatSectorName() {
		return StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(getSectorName()), " ");
	}

	public static SectorEnum findSectorFromSummary(String param) {

		Optional<SectorEnum> result = Stream.of(SectorEnum.values())
				.filter(e -> e.sectorSummaryName.equalsIgnoreCase(param)).findAny();
		if (result.isPresent()) {
			return result.get();
		}

		return null;
	}

	public static boolean containsSectorFromSummary(String param) {
		return Stream.of(SectorEnum.values()).anyMatch(e -> e.sectorSummaryName.equalsIgnoreCase(param));
	}
}
