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

	FINANCIALS("Financials"), //
	INDUSTRIAL("Industrial"), //
	HOLDING("HoldingFirms"), //
	PROPERTY("Property"), //
	SERVICES("Services"), //
	MINING("Mining&Oil"), //
	PREFERRED("Preferred"), //
	PHILDEPOSITARY("PhilDepositaryReceipts"), //
	WARRANTS("Warrants"), //
	SMALLMEDIUMEMERGING("SmallMedium&Emerging"), //
	EXCHANGETRADEDFUNDS("ExchangeTradedFunds"), //
	DOLLARDENOMINATED("DollarDenominatedSecurities");

	private String sectorName;

	private SectorEnum(String sectorName) {
		this.sectorName = sectorName;
	}

	public static boolean contains(String param) {

		param = StringUtils.deleteWhitespace(param);
		param = StringUtils.remove(param, ".");
		param = StringUtils.remove(param, ",");

		final String finalParam = param;

		return Stream.of(SectorEnum.values())
				.anyMatch(e -> e.sectorName.equalsIgnoreCase(StringUtils.deleteWhitespace(finalParam)));
	}

	public static SectorEnum findSector(String param) {

		param = StringUtils.deleteWhitespace(param);
		param = StringUtils.remove(param, ".");
		param = StringUtils.remove(param, ",");

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
