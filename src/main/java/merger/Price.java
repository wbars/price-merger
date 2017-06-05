package merger;

import java.util.Date;

public class Price implements Comparable<Price> {
    private final String productCode;
    private final Date from;
    private final Date to;
    private final long value;

    public Price(String productCode, Date from, Date to, long value) {
        if (from.compareTo(to) > 0) throw new IllegalArgumentException();
        this.productCode = productCode;
        this.from = from;
        this.to = to;
        this.value = value;
    }

    public String getProductCode() {
        return productCode;
    }

    public Date getFrom() {
        return from;
    }

    public Date getTo() {
        return to;
    }

    public long getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Price price = (Price) o;

        if (value != price.value) return false;
        if (!productCode.equals(price.productCode)) return false;
        if (!from.equals(price.from)) return false;
        return to.equals(price.to);
    }

    @Override
    public int hashCode() {
        int result = 0;
        result = 31 * result + productCode.hashCode();
        result = 31 * result + from.hashCode();
        result = 31 * result + to.hashCode();
        result = 31 * result + (int) (value ^ (value >>> 32));
        return result;
    }

    @Override
    public int compareTo(Price o) {
        return from.compareTo(o.from);
    }
}
