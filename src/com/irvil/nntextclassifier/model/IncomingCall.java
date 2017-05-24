package com.irvil.nntextclassifier.model;

public class IncomingCall {
  private final String text;
  private final Module module;
  private final Handler handler;

  public IncomingCall(String text, Module module, Handler handler) {
    this.text = text;
    this.module = module;
    this.handler = handler;
  }

  public IncomingCall(String text) {
    this(text, null, null);
  }

  public String getText() {
    return text;
  }

  public Module getModule() {
    return module;
  }

  public Handler getHandler() {
    return handler;
  }

  @Override
  public String toString() {
    return text + " (Module: " + module + ", Handler: " + handler + ")";
  }
}