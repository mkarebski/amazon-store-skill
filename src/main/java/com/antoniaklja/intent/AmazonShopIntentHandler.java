package com.antoniaklja.intent;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.antoniaklja.client.ProductsAdvertisingClient;
import com.antoniaklja.generated.Item;
import com.antoniaklja.generated.ItemLink;
import com.antoniaklja.generated.ItemLinks;
import com.antoniaklja.helper.ProductAdvertisingConstants;
import com.antoniaklja.service.ProductsAdvertisingService;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AmazonShopIntentHandler {

    public static final String ASK_AMAZON_SHOP_INTENT = "AskAmazonShopIntent";
    public static final String SEARCH_KEYWORD_AMAZON_SHOP_INTENT = "SearchKeywordAmazonShopIntent";
    public static final String SEARCH_CATEGORY_AMAZON_SHOP_INTENT = "SearchCategoryAmazonShopIntent";
    public static final String SEARCH_KEYWORD_AND_CATEGORY_AMAZON_SHOP_INTENT = "SearchKeywordAndCategoryAmazonShopIntent";
    public static final String AMAZON_YES_INTENT = "AMAZON.YesIntent";
    public static final String AMAZON_NO_INTENT = "AMAZON.NoIntent";
    public static final String AMAZON_HELP_INTENT = "AMAZON.HelpIntent";

    private ProductsAdvertisingClient client;
    private ProductsAdvertisingService service;

    private LastActionState lastState = LastActionState.INITIALIZED;
    private List<Item> products = new LinkedList<Item>();
    private Map<String, String> titlesAndLinks = new HashMap<String, String>();

    public AmazonShopIntentHandler() {
        client = new ProductsAdvertisingClient(
                ProductAdvertisingConstants.ENDPOINT,
                ProductAdvertisingConstants.AWS_ACCESS_KEY_ID,
                ProductAdvertisingConstants.AWS_SECRET_KEY,
                ProductAdvertisingConstants.AWS_ASSOCIATES_KEY
        );
        service = new ProductsAdvertisingService(client);
    }

    public SpeechletResponse handleIntent(Intent intent) throws SpeechletException {

        String intentName = (intent != null) ? intent.getName() : null;

        if (SEARCH_KEYWORD_AMAZON_SHOP_INTENT.equals(intentName)) {
            return handleSearchKeyword(intent);
        } else if (SEARCH_KEYWORD_AND_CATEGORY_AMAZON_SHOP_INTENT.equals(intentName)) {
            return handleSearchKeywordAndCategory(intent);
        } else if (ASK_AMAZON_SHOP_INTENT.equals(intentName)) {
            return handleAskAmazonShopIntent();
        } else if (AMAZON_YES_INTENT.equals(intentName)) {
            return handleYes();
        } else if (AMAZON_NO_INTENT.equals(intentName)) {
            return handleNo();
        } else if (AMAZON_HELP_INTENT.equals(intentName)) {
            return getHelpResponse();
        } else {
            throw new SpeechletException("Invalid Intent");
        }

    }

    private SpeechletResponse handleSearchKeyword(Intent intent) {

        Map<String, Slot> slots = intent.getSlots();
        Slot item = slots.get("item");

        products = service.findProducts(item.getValue());

        int itemSize = products.size();

        String speechText = String.format("I found %d products, would you like to know the names?", itemSize);

        SimpleCard card = new SimpleCard();
        card.setTitle("i found some products");
        card.setContent(speechText);

        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(speech);

        lastState = LastActionState.PRODUCTS_FOUND;
        return SpeechletResponse.newAskResponse(speech, reprompt, card);
    }

    private SpeechletResponse handleSearchKeywordAndCategory(Intent intent) {
        Map<String, Slot> slots = intent.getSlots();
        Slot item = slots.get("item");
        Slot category = slots.get("category");

        products = service.findProducts(item.getValue(), category.getValue());

        int itemSize = products.size();

        String speechText = String.format("I found %d products, would you like to know the names?", itemSize);

        SimpleCard card = new SimpleCard();
        card.setTitle("i found some products");
        card.setContent(speechText);

        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(speech);

        lastState = LastActionState.PRODUCTS_FOUND;
        return SpeechletResponse.newAskResponse(speech, reprompt, card);
    }

    private SpeechletResponse handleAskAmazonShopIntent() {

        lastState = LastActionState.ASKED_FOR_CATEGORY;
        String speechText = "Please tell me category or keyword";

        SimpleCard card = new SimpleCard();
        card.setTitle("Give me more information");
        card.setContent(speechText);

        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(speech);

        return SpeechletResponse.newAskResponse(speech, reprompt, card);
    }

    private SpeechletResponse handleNo() {
        products = new LinkedList<Item>();
        lastState = LastActionState.INITIALIZED;

        String speechText = String.format("So thank you for conversation");

        SimpleCard card = new SimpleCard();
        card.setTitle("Finish conversation");
        card.setContent(speechText);

        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        return SpeechletResponse.newTellResponse(speech, card);
    }

    private SpeechletResponse handleYes() {
        StringBuffer sb = new StringBuffer();
        if (lastState.equals(LastActionState.PRODUCTS_FOUND)) {
            for (Item product : products) {
                String title = product.getItemAttributes().getTitle();
                ItemLinks itemLinksAttr = product.getItemLinks();
                if (itemLinksAttr != null) {
                    List<ItemLink> itemLinks = itemLinksAttr.getItemLink();
                    if (itemLinks.size() > 0) {
                        titlesAndLinks.put(title, itemLinks.get(0).getURL());
                    }
                }
                sb.append(title);
                sb.append(", ");
            }

            String speechText = String.format("Product names are : %s", sb.toString());

            SimpleCard card = new SimpleCard();
            card.setTitle("Products listing");
            card.setContent(speechText);

            PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
            speech.setText(speechText);

            return SpeechletResponse.newTellResponse(speech, card);
        }

        String speechText = String.format("I don't understand");

        SimpleCard card = new SimpleCard();
        card.setTitle("What?");
        card.setContent(speechText);

        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        return SpeechletResponse.newTellResponse(speech, card);
    }

    public SpeechletResponse handleWelcomeRequest() {
        lastState = LastActionState.DIALOG_STARTED;
        return getWelcomeResponse();
    }

    private SpeechletResponse getHelpResponse() {
        lastState = LastActionState.NEED_HELP;
        String speechText = "You can find products on amazon web store!";

        SimpleCard card = new SimpleCard();
        card.setTitle("Some help");
        card.setContent(speechText);

        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(speech);

        return SpeechletResponse.newAskResponse(speech, reprompt, card);
    }

    private SpeechletResponse getWelcomeResponse() {
        String speechText = "Welcome, here you can look for products on amazon web store";

        SimpleCard card = new SimpleCard();
        card.setTitle("Welcome");
        card.setContent(speechText);

        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(speech);

        return SpeechletResponse.newAskResponse(speech, reprompt, card);
    }

}
