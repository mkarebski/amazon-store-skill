package com.antoniaklja.service;

import com.antoniaklja.client.ProductsAdvertisingClient;
import com.antoniaklja.generated.Item;
import com.antoniaklja.generated.ItemSearchResponse;
import com.google.common.base.Optional;

import java.util.List;

import static com.antoniaklja.helper.ItemSearchResponses.extractItemsFrom;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This class is responsible for finding products on Amazon Web Store.
 */
public class ProductsAdvertisingService {

    private ProductsAdvertisingClient client;

    public ProductsAdvertisingService(ProductsAdvertisingClient client) {
        this.client = checkNotNull(client);
    }

    public List<Item> findProducts(String keywords) {
        ItemSearchResponse response = client.find(
                keywords,
                Optional.<String>absent(),
                Optional.<Integer>absent(),
                Optional.<Integer>absent()
        );
        return extractItemsFrom(response);
    }

    public List<Item> findProducts(String keywords, String category) {
        ItemSearchResponse response = client.find(
                keywords,
                Optional.of(category),
                Optional.<Integer>absent(),
                Optional.<Integer>absent()
        );
        return extractItemsFrom(response);
    }

    public List<Item> findProducts(String keywords, String category, Integer minPrice, Integer maxPrice) {
        ItemSearchResponse response = client.find(
                keywords,
                Optional.of(category),
                Optional.of(minPrice),
                Optional.of(maxPrice));
        return extractItemsFrom(response);
    }

}
