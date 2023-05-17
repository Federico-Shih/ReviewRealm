package ar.edu.itba.paw.persistence.helpers;

public enum QueryOperator {
    AND("AND"),
    OR("OR"),
    NOT("NOT"),
    EMPTY("");

    private final String operator;

    QueryOperator(String operator) {
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }
}
