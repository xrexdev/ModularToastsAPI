package com.fishguy129.modulartoasts.util;

import java.text.DecimalFormat;

public final class MoneyFormat {
    private MoneyFormat() {}

    private static final DecimalFormat COMMAS = new DecimalFormat("#,###");

    public static String format(long amount, String symbol) {
        return symbol + COMMAS.format(amount);
    }
}