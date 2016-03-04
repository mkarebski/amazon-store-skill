package com.antoniaklja.alexa;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.*;
import com.antoniaklja.intent.AmazonShopIntentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AmazonShopSpeechlet implements Speechlet {

    private static final Logger log = LoggerFactory.getLogger(AmazonShopSpeechlet.class);

    private final AmazonShopIntentHandler intentHandler;

    public AmazonShopSpeechlet() {
        this.intentHandler = new AmazonShopIntentHandler();
    }

    public void onSessionStarted(final SessionStartedRequest request, final Session session)
            throws SpeechletException {
        log.info("onSessionStarted requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
        // any initialization logic goes here
    }

    public SpeechletResponse onLaunch(final LaunchRequest request, final Session session)
            throws SpeechletException {
        log.info("onLaunch requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
        return intentHandler.handleWelcomeRequest();
    }

    public SpeechletResponse onIntent(final IntentRequest request, final Session session)
            throws SpeechletException {
        log.info("onIntent requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());

        Intent intent = request.getIntent();

        return intentHandler.handleIntent(intent);
    }

    public void onSessionEnded(final SessionEndedRequest request, final Session session)
            throws SpeechletException {
        log.info("onSessionEnded requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
        // any cleanup logic goes here
    }
}
