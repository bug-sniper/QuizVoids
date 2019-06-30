package quizVoids.actions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.core.Settings.GameLanguage;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.random.Random;

import quizVoids.QuizVoids;

public class QuizAction extends AbstractGameAction {
    
    public QuizAction() {
        System.out.println("In QuizAction init");
        GameLanguage language = Settings.language;
        String languageAbbreviation = getLanguageAbbreviation(language);
        URL listURL = null;
        try {
            listURL = new URL("https://wikimedia.org/api/rest_v1/metrics/pageviews/top/" +
            languageAbbreviation +
            ".wikipedia/all-access/2015/10/all-days");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        
        URLConnection con;
        InputStreamReader reader = null;
        try {
            con = listURL.openConnection();
            reader = new InputStreamReader(con.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
//        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonContainer = jsonParser.parse(reader);
        JsonObject item = jsonContainer.getAsJsonObject().get("items").getAsJsonArray().get(0).getAsJsonObject();
        ArrayList<String> titles = new ArrayList<String>();
        for (JsonElement article:item.get("articles").getAsJsonArray()){
            titles.add(article.getAsString());
        }
        String selectedTitle = titles.get(AbstractDungeon.cardRandomRng.random(0, titles.size()));
        URL catURL = null;
        try {
            catURL = new URL("https://" +
            languageAbbreviation +
            "en.wikipedia.org/w/api.php?action=query&format=json&titles=" +
            selectedTitle +
            "&prop=categories");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        
        try {
            con = catURL.openConnection();
            reader = new InputStreamReader(con.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        jsonContainer = jsonParser.parse(reader);
        String pageId = jsonContainer.getAsJsonObject().get("clcontinue").getAsString().split("\\|")[0];
        JsonArray categoriesJsonArray = jsonContainer.getAsJsonObject().get("query").getAsJsonObject().get("pages").getAsJsonObject().get(pageId).getAsJsonObject().get("categories").getAsJsonArray();
        ArrayList<String> categoryList = new ArrayList<String>();
        
        String n1 = normalized(selectedTitle);
        for (JsonElement category:categoriesJsonArray){
            String n2 = normalized(category.getAsString());
            if (n1.contains(n2) || n2.contains(n1)){
                continue;
            }
            categoryList.add(category.getAsString());
        }
    }
    
    private static String normalized(String input){
        String ret = input;
        String punct = " -/\\.:'\"()";
        for (char c:punct.toCharArray()){
            ret = ret.replace(String.valueOf(c), "");
        }
        ret = ret.toLowerCase();
        return ret;
    }
    
    private static String getLanguageAbbreviation(GameLanguage language){
        switch (language){
        case ENG: return "en";
        case DUT: return "nl"; 
        case EPO: return "eo";
        case PTB: return "pt"; 
        case ZHS: return "zh";
        case ZHT: return "zh";
        case FRA: return "fr";
        case DEU: return "de";
        case GRE: return "el";
        case IND: return "id";
        case ITA: return "it";
        case JPN: return "ja";
        case KOR: return "ko";
        case NOR: return "no";
        case POL: return "pl";
        case RUS: return "ru";
        case SPA: return "es";
        case SRP: return "sr";
        case SRB: return "sr";
        case THA: return "th";
        case TUR: return "tr";
        case UKR: return "uk";
        case WWW: return "en";
        default: return "en";
        }
    }
    
    private static String getDemonstrative(GameLanguage language, String nextWord){
        switch (language){
        case ENG: return "en";
        case DUT: return "deze"; 
        case EPO: return "ĉi tiu";
        case PTB: return "este"; 
        case ZHS: return "这个";
        case ZHT: return "這個";
        case FRA: return "ce(s)/cette";
        case DEU: return "dieses";
        case GRE: return "τοῦτο";
        case IND: return "ini";
        case ITA: return "Questo/Questa";
        case JPN: return "この";
        case KOR: return "이";
        case NOR: return "dette";
        case POL: return "ten";
        case RUS: return "этот";
        case SPA: return "este";
        case SRP: return "ove";
        case SRB: return "ovo";
        case THA: return "นี่";
        case TUR: return "bu";
        case UKR: return "цей";
        case WWW: return "this";
        default: return "this";
        }
    }
    
    @Override
    public void update() {
        System.out.println("In QuizAction.update");
        if(QuizVoids.submission != null){
            isDone = true;
        } else {
            QuizVoids.quizPanel.show();
        }
    }
}
