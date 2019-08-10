package com.example.pavilion.chatdemo;

/**
 * Created by pavilion on 26-01-2018.
 */

public class MsgOk {
    private String content, username;

    public MsgOk(){
    }

    public MsgOk(String content, String username ){
        this.content=content;
        this.username=username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
