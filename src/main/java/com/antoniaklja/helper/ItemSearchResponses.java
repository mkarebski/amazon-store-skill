package com.antoniaklja.helper;

import com.antoniaklja.generated.Item;
import com.antoniaklja.generated.ItemSearchResponse;
import com.antoniaklja.generated.Items;

import java.util.List;

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
            return items.getItem();
        }
        return emptyList();
    }
}
