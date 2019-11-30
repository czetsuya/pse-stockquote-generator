package com.czetsuya.pse.utils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import com.czetsuya.pse.utils.BigDecimalAverageCollector.BigDecimalAccumulator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author Edward P. Legaspi | czetsuya@gmail.com
 * @since 0.0.1
 * @version 0.0.1
 */
public class BigDecimalAverageCollector implements Collector<BigDecimal, BigDecimalAccumulator, BigDecimal> {

	@Override
	public Supplier<BigDecimalAccumulator> supplier() {
		return BigDecimalAccumulator::new;
	}

	@Override
	public BiConsumer<BigDecimalAccumulator, BigDecimal> accumulator() {
		return BigDecimalAccumulator::add;
	}

	@Override
	public BinaryOperator<BigDecimalAccumulator> combiner() {
		return BigDecimalAccumulator::combine;
	}

	@Override
	public Function<BigDecimalAccumulator, BigDecimal> finisher() {
		return BigDecimalAccumulator::getAverage;
	}

	@Override
	public Set<Characteristics> characteristics() {
		return Collections.emptySet();
	}

	@NoArgsConstructor
	@AllArgsConstructor
	static class BigDecimalAccumulator {

		@Getter
		private BigDecimal sum = BigDecimal.ZERO;

		@Getter
		private BigDecimal count = BigDecimal.ZERO;

		BigDecimal getAverage() {
			return BigDecimal.ZERO.compareTo(count) == 0 ? BigDecimal.ZERO
					: sum.divide(count, 2, BigDecimal.ROUND_HALF_UP);
		}

		BigDecimalAccumulator combine(BigDecimalAccumulator another) {
			return new BigDecimalAccumulator(sum.add(another.getSum()), count.add(another.getCount()));
		}

		void add(BigDecimal successRate) {
			count = count.add(BigDecimal.ONE);
			sum = sum.add(successRate);
		}
	}

}