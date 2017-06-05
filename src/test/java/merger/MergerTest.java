package merger;

import org.junit.Test;

import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class MergerTest {
    @Test
    public void testInsertDetached() throws Exception {
        Price price = price(1L, 2L, 1);
        Price price1 = price(5L, 6L, 2);
        Price incoming = price(3L, 4L, 3);
        List<Price> result = Merger.merge(asList(price, price1), singletonList(incoming));

        assertThat(result, hasSize(3));
        assertThat(result, contains(price, incoming, price1));
    }

    @Test
    public void insertLeftOverlappingLeftWithSamePrice() throws Exception {
        Price price = price(1L, 3L, 2);
        Price price1 = price(7L, 8L, 2);
        Price incoming = price(2L, 5L, 2);
        List<Price> result = Merger.merge(asList(price, price1), singletonList(incoming));

        assertThat(result, hasSize(2));
        assertThat(result, contains(price(1L, 5L, 2), price1));
    }

    @Test
    public void insertRightOverlappingWithSamePrice() throws Exception {
        Price price = price(1L, 3L, 2);
        Price price1 = price(7L, 8L, 2);
        Price incoming = price(5L, 7L, 2);
        List<Price> result = Merger.merge(asList(price, price1), singletonList(incoming));

        assertThat(result, hasSize(2));
        assertThat(result, contains(price, price(5, 8, 2)));
    }

    @Test
    public void insertBothOverlappingWithSamePrice() throws Exception {
        Price price = price(1L, 3L, 2);
        Price price1 = price(5L, 8L, 2);
        Price incoming = price(2L, 7L, 2);
        List<Price> result = Merger.merge(asList(price, price1), singletonList(incoming));

        assertThat(result, hasSize(1));
        assertThat(result, contains(price(1, 8, 2)));
    }

    @Test
    public void insertRightOverlappingNewPrice() throws Exception {
        Price price = price(1L, 3L, 2);
        Price price1 = price(5L, 9L, 2);
        Price incoming = price(7L, 10L, 3);
        List<Price> result = Merger.merge(asList(price, price1), singletonList(incoming));

        assertThat(result, hasSize(3));
        assertThat(result, contains(price, price(5, 7, 2), incoming));
    }

    @Test
    public void insertLeftOverlappingNewPrice() throws Exception {
        Price price = price(1L, 3L, 2);
        Price price1 = price(7L, 10L, 2);
        Price incoming = price(2L, 4L, 3);
        List<Price> result = Merger.merge(asList(price, price1), singletonList(incoming));

        assertThat(result, hasSize(3));
        assertThat(result, contains(price(1, 2, 2), incoming, price1));
    }

    @Test
    public void insertBothOverlappingNewPrice() throws Exception {
        Price price = price(1L, 3L, 2);
        Price price1 = price(5L, 8L, 2);
        Price incoming = price(2L, 7L, 3);
        List<Price> result = Merger.merge(asList(price, price1), singletonList(incoming));

        assertThat(result, hasSize(3));
        assertThat(result, contains(price(1, 2, 2), incoming, price(7, 8, 2)));
    }

    @Test
    public void insertInTheMiddleNewPrice() throws Exception {
        Price price = price(1, 8, 2);
        Price incoming = price(3, 4, 3);
        List<Price> result = Merger.merge(singletonList(price), singletonList(incoming));

        assertThat(result, hasSize(3));
        assertThat(result, contains(price(1, 3, 2), incoming, price(4, 8, 2)));
    }

    @Test
    public void absorbOneOld() throws Exception {
        Price price = price(3, 4, 2);
        Price incoming = price(1, 8, 3);
        List<Price> result = Merger.merge(singletonList(price), singletonList(incoming));

        assertThat(result, hasSize(1));
        assertThat(result, contains(incoming));
    }

    @Test
    public void absorbThreeOldOnes() throws Exception {
        Price incoming = price(1, 9, 3);
        List<Price> result = Merger.merge(asList(
                price(1, 2, 2),
                price(1, 2, 2),
                price(1, 2, 2)
        ), singletonList(incoming));
        assertThat(result, hasSize(1));
        assertThat(result, contains(incoming));
    }

    @Test
    public void differentCodes() throws Exception {
        Price price11 = price(2, 4, 1);
        Price incoming12 = price(3, 5, 1);

        Price price21 = price(1, 4, 2, "2");
        Price incoming22 = price(4, 5, 2, "2");
        List<Price> result = Merger.merge(asList(price11, price21), asList(incoming12, incoming22));

        assertThat(result, hasSize(2));
        assertThat(result, contains(price(2, 5, 1), price(1, 5, 2, "2")));
    }

    private Price price(long from, long to, int value) {
        return price(from, to, value, "1");
    }

    private Price price(long from, long to, int value, String productCode) {
        return new Price(productCode, new Date(from), new Date(to), value);
    }
}