package com.smart.helper;

public class Message {
    
    private String content;
    private String greet;
    private String type;

    public String getGreet() {
        return greet;
    }

    public Message(String content, String greet, String type) {
        this.content = content;
        this.greet = greet;
        this.type = type;
    }

    public void setGreet(String greet) {
        this.greet = greet;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Message [content=" + content + ", greet=" + greet + ", type=" + type + "]";
    }

    
    
}
