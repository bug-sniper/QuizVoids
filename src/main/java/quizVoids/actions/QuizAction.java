package quizVoids.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.core.Settings.GameLanguage;

public class QuizAction extends AbstractGameAction {
    
    public QuizAction() {
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
        GameLanguage language = Settings.language;
        String languageAbbreviation = getLanguageAbbreviation(language);
        isDone = true;
    }
}
