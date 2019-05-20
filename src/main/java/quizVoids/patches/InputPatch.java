package quizVoids.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.helpers.TypeHelper;
import com.megacrit.cardcrawl.helpers.input.InputAction;

import quizVoids.QuizVoids;
import quizVoids.ui.QuizPanel;

//Patch to redirect initializations of LoseEnergyAction to QuizAction

public class InputPatch
{
    @SpirePatch(clz = TypeHelper.class, method = "keyTyped")
    public static class TypeHelperPatch
    {
        public static SpireReturn<Boolean> Prefix(final TypeHelper typeHelper, final char character) {
            if (QuizVoids.inQuiz) {
                if (character == '\n' || character == '\r') {
                    QuizPanel.submit();
                }
                else if (character >= ' ' && character <= '~') {
                    QuizPanel.textField += character;
                }
                return SpireReturn.Return(false);
            }
            return SpireReturn.Continue();
        }
    }
    
    @SpirePatch(clz = InputAction.class, method = "isJustPressed")
    public static class InputActionPatch
    {
        public static SpireReturn Prefix(final InputAction inputAction) {
            if (QuizVoids.inQuiz) {
                return SpireReturn.Return((Object)false);
            }
            return SpireReturn.Continue();
        }
    }
}
