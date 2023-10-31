package org.example;

import net.sourceforge.tess4j.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class RecoText {
    static Tesseract ts = new Tesseract();
    public String recoText(BufferedImage BufferedImage) {

        // Kelimeleri algılayan uygulamanın genel ayarları
        ts.setDatapath(Main.tessdata);
        ts.setTessVariable("tessedit_char_whitelist"," abcçdefghijklmnopqrstuvwxyzABCÇDEFGĞHIİJKLMNOÖPQRSTUÜVWXYZ-.=,'?!&/{}[]*_%+^'<>0123456789");

        // Algıladıgı kelimeler
        List<Word> words = ts.getWords(BufferedImage,ITessAPI.TessPageIteratorLevel.RIL_BLOCK);

        StringBuilder result = new StringBuilder("");
        result.append(words);

        String extractedText = null;
        if(words.size()>0){
            String output = result.toString();
            int startIndex = output.indexOf("Text:") + 6;
            int endIndex = output.indexOf(", Confidence:");
            extractedText = output.substring(startIndex,endIndex).trim();
            words=null;
        }
        return extractedText;
    }

}

