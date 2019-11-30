package com.czetsuya.pse;

import java.math.BigDecimal;
import java.math.RoundingMode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Edward P. Legaspi | czetsuya@gmail.com
 * @since 0.0.1
 * @version 0.0.1
 */
@Builder
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
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

	public void addAsk(BigDecimal val) {
		if (ask == null) {
			ask = BigDecimal.ZERO;
		}

		ask = ask.add(val);
	}

	public void addBid(BigDecimal val) {
		if (bid == null) {
			bid = BigDecimal.ZERO;
		}

		bid = bid.add(val);
	}

	public void addOpen(BigDecimal val) {
		if (open == null) {
			open = BigDecimal.ZERO;
		}

		open = open.add(val);
	}

	public void addHigh(BigDecimal val) {
		if (high == null) {
			high = BigDecimal.ZERO;
		}

		high = high.add(val);
	}

	public void addLow(BigDecimal val) {
		if (low == null) {
			low = BigDecimal.ZERO;
		}

		low = low.add(val);
	}

	public void addClose(BigDecimal val) {
		if (close == null) {
			close = BigDecimal.ZERO;
		}

		close = close.add(val);
	}

	public void addVolume(BigDecimal val) {
		if (volume == null) {
			volume = BigDecimal.ZERO;
		}

		volume = volume.add(val);
	}

	public void addValue(BigDecimal val) {
		if (value == null) {
			value = BigDecimal.ZERO;
		}

		value = value.add(val);
	}

	public void addForeign(BigDecimal val) {
		if (foreignSellingOrBuying == null) {
			foreignSellingOrBuying = BigDecimal.ZERO;
		}

		foreignSellingOrBuying = foreignSellingOrBuying.add(val);
	}

	public void addStockQuote(StockQuote e) {

		addAsk(e.getAsk());
		addBid(e.getBid());
		addClose(e.getClose());
		addForeign(e.getForeignSellingOrBuying());
		addHigh(e.getHigh());
		addLow(e.getLow());
		addOpen(e.getOpen());
		addValue(e.getValue());
		addVolume(e.getVolume());
	}

	public void computeAverage(int size) {

		setAsk(getAsk().divide(new BigDecimal(size), RoundingMode.HALF_DOWN));
		setBid(getBid().divide(new BigDecimal(size), RoundingMode.HALF_DOWN));
		setClose(getClose().divide(new BigDecimal(size), RoundingMode.HALF_DOWN));
		setForeignSellingOrBuying(getForeignSellingOrBuying().divide(new BigDecimal(size), RoundingMode.HALF_DOWN));
		setHigh(getHigh().divide(new BigDecimal(size), RoundingMode.HALF_DOWN));
		setLow(getLow().divide(new BigDecimal(size), RoundingMode.HALF_DOWN));
		setOpen(getOpen().divide(new BigDecimal(size), RoundingMode.HALF_DOWN));
		setValue(getValue().divide(new BigDecimal(size), RoundingMode.HALF_DOWN));
		setVolume(getVolume().divide(new BigDecimal(size), RoundingMode.HALF_DOWN));
	}

}