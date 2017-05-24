package com.irvil.nntextclassifier.model;

public class IncomingCall {
  private final String text;
  private final Module module;
  private final Handler handler;
  private final Category category;

  public IncomingCall(String text, Module module, Handler handler, Category category) {
    this.text = text;
    this.module = module;
    this.handler = handler;
    this.category = category;
  }

  public IncomingCall(String text) {
    this(text, null, null, null);
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

  public Category getCategory() {
    return category;
  }

  @Override
  public String toString() {
    return text + " (Module: " + module + ", Category: " + category + ", Handler: " + handler + ")";
  }
}