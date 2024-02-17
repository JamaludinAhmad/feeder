package com.example.feeder_importer.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Getter @Setter
public class Akun {

//    @Autowired
//    private Http http;

    private String username;
    private String pass;


    private String token;
    private boolean isLogin;

    private String act = "";

    public void login() throws JsonProcessingException {
        JsonNode jsonNode = check();
        if(jsonNode.get("error_code").asInt() == 12){
            isLogin = false;
            this.token = null;
            return;
        }
        this.token = jsonNode.get("data").get("token").asText();
        isLogin = true;
    }

    public void generateToken() throws JsonProcessingException {
        JsonNode jsonNode = check();
        this.token = jsonNode.get("data").get("token").asText();
    }

    public JsonNode check() throws JsonProcessingException {
        Http http = new Http();
        JSONObject respon = http.post("""
                    {
                    "act" : "GetToken",
                    "username" : "%s",
                    "password" : "%s"
                    }
                    
                """.formatted(this.username, this.pass));
        ObjectMapper obj = new ObjectMapper();
        return obj.readTree(respon.toString());
    }

    public void setAct(String act){
        this.act = act;
    }

    public JSONObject post() throws JsonProcessingException {
        Http http = new Http();
        JSONObject respon = http.post("""
                    {
                        "token" : "%s",
                        %s
                    }
                """.formatted(this.token, this.act));
        if(respon.getInt("error_code") != 0){
            generateToken();
            System.out.println(this.token);
            System.out.println("generate token");
            System.out.println("disin");
            JSONObject a = http.post("""
                    {
                        "token" : "%s",
                        %s
                    }
                """.formatted(this.token, this.act));
            return a;
        }
        return respon;
    }

}
