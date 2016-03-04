package com.antoniaklja.helper;

import com.antoniaklja.generated.*;

import java.util.*;
import java.util.Collections;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.emptyList;

/**
 * Utility class for operations related to Amazon Product Advertising API data model.
 */
public class ItemSearchResponses {

    public static List<Item> extractItemsFrom(ItemSearchResponse response) {
        checkNotNull(response);

        List<Items> allItems = response.getItems();
        if (allItems == null) {
            return emptyList();
        }

        for (Items items : allItems) {
            Iterator<Item> itemIterator = items.getItem().iterator();
            while (itemIterator.hasNext()) {
                Item item = itemIterator.next();
                ItemAttributes itemAttributes = item.getItemAttributes();
                if (itemAttributes.getListPrice() != null) {
                    itemIterator.remove();
                }
            }
        }
        return allItems.get(0).getItem();
    }

    public static String extractCardDataFor(Item item) {
        checkNotNull(item);

        ItemAttributes itemAttributes = item.getItemAttributes();
        if (itemAttributes.getListPrice() != null) {
            return String.format("Title %s costs %s \\n %s \\n",
                    itemAttributes.getTitle(),
                    itemAttributes.getListPrice().getFormattedPrice(),
                    extractLinkFrom(item));
        }
        return "";
    }

    private static String extractLinkFrom(Item item) {
        ItemLinks itemLinks = item.getItemLinks();
        if (itemLinks != null) {
            List<ItemLink> itemLink = itemLinks.getItemLink();
            if (itemLink.size() > 0) {
                return itemLink.get(0).getURL();
            }
        }
        return "";
    }

    public static String extractTitleAndPrice(Item item) {
        checkNotNull(item);

        ItemAttributes itemAttributes = item.getItemAttributes();
        if (itemAttributes.getListPrice() != null) {
            return String.format("Title %s costs %s", itemAttributes.getTitle(), itemAttributes.getListPrice().getFormattedPrice());
        }

        return "";
    }

}
