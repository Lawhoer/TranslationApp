package org.example;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Translate {
    public String translate(String args) {
        String inputText = args;
        String outputText = null;
        String outputLanguage = Main.dil; //     Türkçe dil kodu

        try {
            // API anahtarınızı aşağıdaki URL'de kullanın
            URL url = new URL("https://translation.googleapis.com/language/translate/v2?key="+Main.googleKey);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            // API'ye gönderilecek JSON formatındaki isteği oluşturun
            String jsonInputString = "{\"q\":\"" + inputText + "\",\"target\":\"" + outputLanguage + "\"}";

            // İstek gövdesine JSON formatındaki isteği yazın
            con.getOutputStream().write(jsonInputString.getBytes(StandardCharsets.UTF_8));
            con.disconnect();

            // Yanıtı alın ve okuyun
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

            // JSON formatındaki yanıttan çıktı metnini alın
            int startIndex = response.indexOf("translatedText") + 18;
            int endIndex = response.indexOf("\"", startIndex);
            outputText = response.substring(startIndex, endIndex);

            outputText = outputText.replace("&#39;", "'");

        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
        return outputText;
    }

}
