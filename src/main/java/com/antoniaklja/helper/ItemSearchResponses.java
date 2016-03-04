package com.antoniaklja.helper;

import com.antoniaklja.generated.Item;
import com.antoniaklja.generated.ItemAttributes;
import com.antoniaklja.generated.ItemSearchResponse;
import com.antoniaklja.generated.Items;

import java.util.*;

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

    public static List<String> getItemsTitlesAndPrices(ItemSearchResponse response) {
        checkNotNull(response);

        List<Items> allItems = response.getItems();
        if (allItems != null) {
            List<String> result = new ArrayList<String>();

            for (Items items : allItems) {
                for (Item item : items.getItem()) {
                    ItemAttributes itemAttributes = item.getItemAttributes();
                    if (itemAttributes.getListPrice() != null) {
                        result.add("Title " + itemAttributes.getTitle() + ". Price " + itemAttributes.getListPrice().getFormattedPrice());
                    }
                }
            }

            return result;
        }


        return emptyList();
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
