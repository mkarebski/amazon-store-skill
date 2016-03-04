package com.antoniaklja.client;

import com.antoniaklja.generated.ItemSearchResponse;
import com.antoniaklja.helper.SignedRequestsHelper;
import com.google.common.base.Optional;
import com.jcabi.http.Request;
import com.jcabi.http.Response;
import com.jcabi.http.request.JdkRequest;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.valueOf;

/**
 * This class is responsible for communication with Amazon Product Advertising API.
 *
 * @see {http://docs.aws.amazon.com/AWSECommerceService/latest/DG/Welcome.html#arch}
 */
public class ProductsAdvertisingClient {

    private SignedRequestsHelper signedRequestsHelper;
    private String endpoint;
    private String accessKeyId;
    private String secretKey;
    private String associateTag;

    public ProductsAdvertisingClient(String endpoint, String accessKeyId, String secretKey, String associateTag) {
        try {
            this.endpoint = checkNotNull(endpoint);
            this.accessKeyId = checkNotNull(accessKeyId);
            this.secretKey = checkNotNull(secretKey);
            this.associateTag = checkNotNull(associateTag);
            this.signedRequestsHelper = SignedRequestsHelper.getInstance(endpoint, accessKeyId, secretKey);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot create Product Advertising Client!", e);
        }
    }

    public ItemSearchResponse find(String keywords, Optional<String> category, Optional<Integer> minPrice, Optional<Integer> maxPrice) {
        checkNotNull(keywords, "Expected non null keywords");
        checkArgument(!keywords.isEmpty(), "Expected non empty keywords");

        try {
            Map<String, String> params = constructParams(keywords, category, minPrice, maxPrice);

            String requestUrl = signedRequestsHelper.sign(params);
            Request request = new JdkRequest(requestUrl);
            Response response = request.fetch();

            JAXBContext jaxbContext = JAXBContext.newInstance(ItemSearchResponse.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            ItemSearchResponse itemSearchResponse = (ItemSearchResponse) unmarshaller.unmarshal(new StringReader(response.body()));
            return itemSearchResponse;
        } catch (Exception e) {
            throw new IllegalStateException("Cannot perform request!", e);
        }
    }

    private Map<String, String> constructParams(String keywords, Optional<String> category, Optional<Integer> minPrice, Optional<Integer> maxPrice) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("Service", "AWSECommerceService");
        params.put("Operation", "ItemSearch");
        params.put("AWSAccessKeyId", accessKeyId);
        params.put("AssociateTag", associateTag);
        params.put("SearchIndex", "All");
        params.put("Condition", "New");
        params.put("ResponseGroup", "Large");
        params.put("Availability", "Available ");
        params.put("Keywords", keywords);

        if (category.isPresent()) { // you have to specify category if you want filter by max/min price
            params.put("SearchIndex", category.get());
            if (minPrice.isPresent()) {
                params.put("MinimumPrice", valueOf(minPrice));
            }
            if (maxPrice.isPresent()) {
                params.put("MaximumPrice", valueOf(maxPrice));
            }
//                params.put("Sort", "price");
        }
        return params;
    }
}
