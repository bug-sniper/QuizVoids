package quizVoids.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.status.VoidCard;

import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import quizVoids.actions.QuizAction;

//Patch to redirect initializations of LoseEnergyAction to QuizAction

@SpirePatch(
        clz=VoidCard.class,
        method="triggerWhenDrawn"
)
public class ChanneledCardDiscardPatch {
    public static int count = 1;
    public static ExprEditor Instrument() {
        return new ExprEditor() {
            @Override
            public void edit(MethodCall m) throws CannotCompileException {
                if (m.getMethodName().equals("addToBottom") && count == 1){
                    m.replace("$_ = $proceed(new " + QuizAction.class.getName() + "());");
                    count += 1;
                }
            }
        };
    }
}
