package com.antoniaklja.intent;

import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.antoniaklja.client.ProductsAdvertisingClient;
import com.antoniaklja.generated.Item;
import com.antoniaklja.helper.ProductAdvertisingConstants;
import com.antoniaklja.service.ProductsAdvertisingService;

import java.util.List;

public class AmazonShopIntentHandler {

    private ProductsAdvertisingClient client;
    private ProductsAdvertisingService service;

    public AmazonShopIntentHandler() {
        client = new ProductsAdvertisingClient(
                ProductAdvertisingConstants.ENDPOINT,
                ProductAdvertisingConstants.AWS_ACCESS_KEY_ID,
                ProductAdvertisingConstants.AWS_SECRET_KEY,
                ProductAdvertisingConstants.AWS_ASSOCIATES_KEY
        );
        service = new ProductsAdvertisingService(client);
    }

    public SpeechletResponse handleIntent(String intentName) throws SpeechletException {

        if ("AmazonShopIntent".equals(intentName)) {
            return handleAmazonShopIntent();
        } else if ("AMAZON.HelpIntent".equals(intentName)) {
            return getHelpResponse();
        } else {
            throw new SpeechletException("Invalid Intent");
        }

    }

    public SpeechletResponse handleWelcomeRequest() {
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

        return SpeechletResponse.newAskResponse(speech, reprompt, card);
    }

    private SpeechletResponse handleAmazonShopIntent() {
        List<Item> products = service.findProducts("hacker book");
        int itemCount = products.size();

        String speechText = String.format("I found %d products", itemCount);

        SimpleCard card = new SimpleCard();
        card.setTitle("Response");
        card.setContent(speechText);

        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        return SpeechletResponse.newTellResponse(speech, card);
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
