package com.xeiam.xchange.service;

import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author timmolter
 */
public class WebSocketEventProducer extends WebSocketClient {

  private final Logger logger = LoggerFactory.getLogger(WebSocketEventProducer.class);

  private final ExchangeEventListener exchangeEventListener;

  /**
   * Constructor
   * 
   * @param url
   * @param exchangeEventProducer
   * @throws URISyntaxException
   */
  public WebSocketEventProducer(String url, ExchangeEventListener exchangeEventListener) throws URISyntaxException {

    super(new URI(url), new Draft_10());
    this.exchangeEventListener = exchangeEventListener;

  }

  @Override
  public void onOpen(ServerHandshake handshakedata) {

    // System.out.println("opened connection");
    // if you pan to refuse connection based on ip or httpfields overload: onWebsocketHandshakeReceivedAsClient

    logger.debug("onOpen");
    ExchangeEvent exchangeEvent = new JsonWrappedExchangeEvent(ExchangeEventType.CONNECT, "connected");
    exchangeEventListener.handleEvent(exchangeEvent);
  }

  @Override
  public void onMessage(String message) {

    // System.out.println("received: " + message);
    // send( "you said: " + message );

    // logger.debug(onMessage);
    ExchangeEvent exchangeEvent = new DefaultExchangeEvent(ExchangeEventType.MESSAGE, message);
    exchangeEventListener.handleEvent(exchangeEvent);
  }

  @Override
  public void onClose(int code, String reason, boolean remote) {

    // The codecodes are documented in class org.java_websocket.framing.CloseFrame
    // System.out.println("Connection closed by " + (remote ? "remote peer" : "us"));
    // System.out.println("reason= " + reason);

    logger.debug("onClose");
    ExchangeEvent exchangeEvent = new JsonWrappedExchangeEvent(ExchangeEventType.DISCONNECT, "disconnected");
    exchangeEventListener.handleEvent(exchangeEvent);
  }

  @Override
  public void onError(Exception ex) {

    // ex.printStackTrace();
    // if the error is fatal then onClose will be called additionally

    logger.error("onError: {}", ex.getMessage(), ex);
    ExchangeEvent exchangeEvent = new JsonWrappedExchangeEvent(ExchangeEventType.ERROR, ex.getMessage());
    exchangeEventListener.handleEvent(exchangeEvent);
  }

}