package com.czetsuya.pse;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * @author Edward P. Legaspi | czetsuya@gmail.com
 * @since 0.0.1
 * @version 0.0.1
 */
@Builder
@Data
@ToString
public class StockQuote {

	private String sector;
	private String subSector;
	private String symbol;
	private BigDecimal bid;
	private BigDecimal ask;
	private BigDecimal open;
	private BigDecimal high;
	private BigDecimal low;
	private BigDecimal close;
	private BigDecimal volume;
	private BigDecimal value;
	private BigDecimal foreignSellingOrBuying;

}
