package com.antoniaklja.service;

import com.antoniaklja.client.ProductsAdvertisingClient;
import com.antoniaklja.generated.Item;
import com.antoniaklja.generated.ItemAttributes;
import com.antoniaklja.generated.Price;
import com.antoniaklja.helper.ProductAdvertisingConstants;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertNotNull;

@Ignore
public class ProductsServiceTest {

    private ProductsAdvertisingClient client;
    private ProductsAdvertisingService service;

    @Before
    public void setUp() throws Exception {
        client = new ProductsAdvertisingClient(
                ProductAdvertisingConstants.ENDPOINT,
                ProductAdvertisingConstants.AWS_ACCESS_KEY_ID,
                ProductAdvertisingConstants.AWS_SECRET_KEY,
                ProductAdvertisingConstants.AWS_ASSOCIATES_KEY
        );
        service = new ProductsAdvertisingService(client);
    }

    @Test
    public void shouldFindProducts() {
        // given
        String keywords = "hacker book";

        // when
        List<Item> items = service.findProducts(keywords);

        // then
        assertNotNull(items);
        print(items);
    }

    @Test
    public void shouldFindProductsWithCategory() {
        // given
        String keywords = "car";
        String category = "Toys";

        // when
        List<Item> items = service.findProducts(keywords, category);

        // then
        assertNotNull(items);
        print(items);
    }

    @Test
    public void shouldFindProductsWithCategoryAndMinPriceAndMaxPrice() {
        // given
        String keywords = "gloves";
        String category = "Apparel";
        Integer minPrice = 5000;
        Integer maxPrice = 150000;

        // when
        List<Item> items = service.findProducts(keywords, category, minPrice, maxPrice);

        // then
        assertNotNull(items);
        print(items);
    }

    private void print(List<Item> items) {
        System.out.println("\n");
        for (Item item : items) {
            ItemAttributes attributes = item.getItemAttributes();
            String title = attributes.getTitle();
            Price listPrice = attributes.getListPrice();
            String price = "";
            if (listPrice != null) {
                price = listPrice.getFormattedPrice();
            }
            System.out.println(title + " " + price);
        }
        System.out.println("\n");
    }

}