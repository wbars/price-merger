package merger;

import java.util.*;

import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;

public class Merger {
    public static List<Price> merge(List<Price> old, List<Price> incoming) {
        Map<String, List<Price>> oldByProducts = groupByProductCodes(old);
        Map<String, List<Price>> incomingByProducts = groupByProductCodes(incoming);

        List<Price> result = new ArrayList<>();
        for (String productCode : oldByProducts.keySet()) {
            result.addAll(mergeSameProductsPrices(
                    oldByProducts.get(productCode),
                    incomingByProducts.getOrDefault(productCode, emptyList())
            ));
            incomingByProducts.remove(productCode);
        }
        incomingByProducts.values().forEach(result::addAll);
        return result;
    }

    private static Map<String, List<Price>> groupByProductCodes(List<Price> prices) {
        return prices.stream()
                .collect(groupingBy(Price::getProductCode));
    }

    private static List<Price> mergeSameProductsPrices(List<Price> old, List<Price> incoming) {
        old.sort(comparing(Price::getFrom));
        incoming.sort(comparing(Price::getFrom));

        Stack<Price> prices = new Stack<>();
        ListIterator<Price> oldIt = old.listIterator();
        ListIterator<Price> incomingIt = incoming.listIterator();
        while (oldIt.hasNext() && incomingIt.hasNext()) {
            if (old.get(oldIt.nextIndex()).compareTo(incoming.get(incomingIt.nextIndex())) < 0)
                addWithMerge(oldIt.next(), prices, false);
            else addWithMerge(incomingIt.next(), prices, true);
        }
        oldIt.forEachRemaining(p -> addWithMerge(p, prices, false));
        incomingIt.forEachRemaining(p -> addWithMerge(p, prices, true));
        return new ArrayList<>(prices);
    }

    private static void addWithMerge(Price price, Stack<Price> prices, boolean incomingPrice) {
        if (prices.isEmpty()) {
            prices.add(price);
            return;
        }

        if (prices.peek().getTo().compareTo(price.getFrom()) < 0) {
            prices.add(price);
            return;
        }

        Price pop = prices.pop();
        if (pop.getValue() == price.getValue()) {
            prices.add(merge(pop, price));
            return;
        }

        if (incomingPrice) {
            prices.add(rightRemainder(pop, price));
            prices.add(price);
            if (price.getTo().compareTo(pop.getTo()) < 0) prices.add(leftRemainder(price, pop));
        } else {
            prices.add(pop);
            if (price.getTo().compareTo(pop.getTo()) > 0) prices.add(leftRemainder(pop, price));
        }


    }

    private static Price rightRemainder(Price first, Price second) {
        return new Price(first.getProductCode(), first.getFrom(), second.getFrom(), first.getValue());
    }

    private static Price leftRemainder(Price first, Price second) {
        return new Price(first.getProductCode(), first.getTo(), second.getTo(), second.getValue());
    }

    private static Price merge(Price first, Price second) {
        return new Price(first.getProductCode(), first.getFrom(), second.getTo(), second.getValue());
    }
}
