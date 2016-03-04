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
import com.antoniaklja.helper.ProductAdvertisingConstants;
import com.antoniaklja.service.ProductsAdvertisingService;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AmazonShopIntentHandler {

    public static final String ASK_AMAZON_SHOP_INTENT = "AskAmazonShopIntent";
    public static final String SEARCH_KEYWORD_AMAZON_SHOP_INTENT = "SearchKeywordAmazonShopIntent";
    public static final String SEARCH_KEYWORD_AND_CATEGORY_AMAZON_SHOP_INTENT = "SearchKeywordAndCategoryAmazonShopIntent";
    public static final String SEARCH_SIZE = "SearchSizeAmazonShopIntent";
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
        lastState = LastActionState.INITIALIZED;

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
        } else if (SEARCH_SIZE.equals(intentName)) {
            return handleSearchSize(intent);
        } else if (AMAZON_YES_INTENT.equals(intentName)) {
            return handleYes();
        } else if (AMAZON_NO_INTENT.equals(intentName)) {
            return handleNo();
        } else if (AMAZON_HELP_INTENT.equals(intentName)) {
            return getHelpResponse();
        } else {
            return handleInvalidIntent();
        }
    }

    private SpeechletResponse handleSearchSize(Intent intent) {
        if (lastState.equals(LastActionState.ASKED_FOR_SEARCH_SIZE)) {

            Map<String, Slot> slots = intent.getSlots();
            Slot slotNumber = slots.get("number");
            Integer number = Integer.valueOf(slotNumber.getValue());

            StringBuilder sb = new StringBuilder();
            int i = 1;
            for (int j = 0; j < number; j++) {
                Item item = products.get(j);
                String title = item.getItemAttributes().getTitle();
                sb.append(i + "" + title);
                sb.append("\n");
                i++;
            }

            String speechText = String.format("Here you are your products : %s", sb.toString());

            SimpleCard card = new SimpleCard();
            card.setTitle("Products listing");
            card.setContent(speechText);

            PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
            speech.setText(speechText);

            lastState = LastActionState.ASKED_FOR_PRUCTS_NAMES;
            return SpeechletResponse.newTellResponse(speech, card);
        }

        return handleInvalidIntent();
    }

    private SpeechletResponse handleInvalidIntent() {
        String speechText = "I don't understand you, please say again.";

        SimpleCard card = new SimpleCard();
        card.setTitle("Problem");
        card.setContent(speechText);

        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(speech);

        lastState = LastActionState.NEED_HELP;
        return SpeechletResponse.newAskResponse(speech, reprompt, card);
    }

    private SpeechletResponse handleSearchKeyword(Intent intent) {

        Map<String, Slot> slots = intent.getSlots();
        Slot item = slots.get("item");

        products = service.findProducts(item.getValue());

        int itemSize = products.size();

        String speechText = String.format("I found %d products, would you like to know more?", itemSize);

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

        String speechText = String.format("I found %d products, would you like to know more?", itemSize);

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

        String speechText = "Please tell me keyword or category and keyword";

        SimpleCard card = new SimpleCard();
        card.setTitle("Give me more information");
        card.setContent(speechText);

        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(speech);

        lastState = LastActionState.ASKED_FOR_CATEGORY;
        return SpeechletResponse.newAskResponse(speech, reprompt, card);
    }

    private SpeechletResponse handleNo() {
        products = new LinkedList<Item>();

        String speechText = String.format("So thank you for conversation");

        SimpleCard card = new SimpleCard();
        card.setTitle("Finish conversation");
        card.setContent(speechText);

        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        lastState = LastActionState.INITIALIZED;
        return SpeechletResponse.newTellResponse(speech, card);
    }

    private SpeechletResponse handleYes() {
        if (lastState.equals(LastActionState.PRODUCTS_FOUND)) {
            String speechText = "How many?";

            SimpleCard card = new SimpleCard();
            card.setTitle("Search size");
            card.setContent(speechText);

            PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
            speech.setText(speechText);

            Reprompt reprompt = new Reprompt();
            reprompt.setOutputSpeech(speech);

            lastState = LastActionState.ASKED_FOR_SEARCH_SIZE;
            return SpeechletResponse.newAskResponse(speech, reprompt, card);
        }

        return handleInvalidIntent();
    }

    public SpeechletResponse handleWelcomeRequest() {
        lastState = LastActionState.DIALOG_STARTED;
        return getWelcomeResponse();
    }

    private SpeechletResponse getHelpResponse() {
        String speechText = "You can find products on amazon web store!";

        SimpleCard card = new SimpleCard();
        card.setTitle("Some help");
        card.setContent(speechText);

        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(speech);

        lastState = LastActionState.NEED_HELP;
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

        lastState = LastActionState.INITIALIZED;
        return SpeechletResponse.newAskResponse(speech, reprompt, card);
    }

}
