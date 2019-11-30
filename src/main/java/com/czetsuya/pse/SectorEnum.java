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

	FINANCIALS("Financials", "^FINANCIAL"), //
	INDUSTRIAL("Industrial", "^INDUSTRIAL"), //
	HOLDING("HoldingFirms", "^HOLDING"), //
	PROPERTY("Property", "^PROPERTY"), //
	SERVICES("Services", "^SERVICE"), //
	MINING("Mining&Oil", "^MINING-OIL"), //
	PREFERRED("Preferred", "^PREFERRED"), //
	PHILDEPOSITARY("Phil.DepositaryReceipts", "^PHILDEPO"), //
	WARRANTS("Warrants", "^WARRANTS"), //
	SMALLMEDIUMEMERGING("Small,Medium&Emerging", "^SMALLMEDIUMEMERGING"), //
	EXCHANGETRADEDFUNDS("ExchangeTradedFunds", "^EXCHANGETRADED"), //
	DOLLARDENOMINATED("DollarDenominatedSecurities", "^DOLLAR");

	private String sectorName;
	private String csvName;

	private SectorEnum(String sectorName, String csvName) {
		this.sectorName = sectorName;
		this.csvName = csvName;
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
}
