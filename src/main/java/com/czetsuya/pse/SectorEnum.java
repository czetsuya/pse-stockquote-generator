package com.czetsuya.pse;

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
	MINING("Mining&Oil");

	private String sectorName;

	private SectorEnum(String sectorName) {
		this.sectorName = sectorName;
	}

	public static boolean contains(String param) {
		return Stream.of(SectorEnum.values())
				.filter(e -> e.sectorName.toLowerCase().equals(StringUtils.deleteWhitespace(param).toLowerCase()))
				.findAny().isPresent();
	}

	public static SectorEnum findSector(String param) {
		return Stream.of(SectorEnum.values())
				.filter(e -> e.sectorName.toLowerCase().equals(StringUtils.deleteWhitespace(param).toLowerCase()))
				.findAny().get();
	}

	public String formatSectorName() {
		return StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(getSectorName()), " ");
	}
}
