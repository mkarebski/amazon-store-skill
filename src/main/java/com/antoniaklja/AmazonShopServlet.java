package com.antoniaklja;

import com.amazon.speech.speechlet.servlet.SpeechletServlet;

public class AmazonShopServlet extends SpeechletServlet {

    public AmazonShopServlet() {
        this.setSpeechlet(new AmazonShopSpeechlet());
    }
}
