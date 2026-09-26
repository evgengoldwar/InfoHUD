package com.gtnewhorizons.infohud.hud.tags;

final class Expressions {

    static final class EvalException extends RuntimeException {

        final boolean unknown;

        EvalException(boolean unknown) {
            super(null, null, false, false);
            this.unknown = unknown;
        }
    }

    private final String text;
    private int pos;

    private Expressions(String text) {
        this.text = text;
    }

    static double evaluate(String text) {
        Expressions parser = new Expressions(text);
        double value = parser.parseSum();
        parser.skipSpaces();
        if (parser.pos < text.length()) throw new EvalException(true);
        return value;
    }

    private double parseSum() {
        double value = parseProduct();
        while (true) {
            skipSpaces();
            if (eat('+')) {
                value += parseProduct();
            } else if (eat('-')) {
                value -= parseProduct();
            } else {
                return value;
            }
        }
    }

    private double parseProduct() {
        double value = parseFactor();
        while (true) {
            skipSpaces();
            if (eat('*')) {
                value *= parseFactor();
            } else if (eat('/')) {
                double divisor = parseFactor();
                if (divisor == 0) throw new EvalException(false);
                value /= divisor;
            } else if (eat('%')) {
                double divisor = parseFactor();
                if (divisor == 0) throw new EvalException(false);
                value %= divisor;
            } else {
                return value;
            }
        }
    }

    private double parseFactor() {
        skipSpaces();

        if (eat('-')) return -parseFactor();
        if (eat('+')) return parseFactor();

        if (eat('(')) {
            double value = parseSum();
            skipSpaces();
            if (!eat(')')) throw new EvalException(true);
            return value;
        }

        int start = pos;
        if (pos < text.length() && (Character.isDigit(text.charAt(pos)) || text.charAt(pos) == '.')) {
            while (pos < text.length() && (Character.isDigit(text.charAt(pos)) || text.charAt(pos) == '.')) pos++;
            try {
                return Double.parseDouble(text.substring(start, pos));
            } catch (NumberFormatException e) {
                throw new EvalException(true);
            }
        }

        while (pos < text.length() && (Character.isLetterOrDigit(text.charAt(pos)) || text.charAt(pos) == '_')) {
            pos++;
        }
        if (start == pos) throw new EvalException(true);

        InfoTag tag = TagRegistry.get(text.substring(start, pos));
        if (tag == null) throw new EvalException(true);

        String value = tag.getValue();
        Double number = value == null ? null : LineTemplate.parseNumber(value);
        if (number == null) throw new EvalException(false);
        return number;
    }

    private boolean eat(char c) {
        if (pos < text.length() && text.charAt(pos) == c) {
            pos++;
            return true;
        }
        return false;
    }

    private void skipSpaces() {
        while (pos < text.length() && text.charAt(pos) == ' ') pos++;
    }
}
