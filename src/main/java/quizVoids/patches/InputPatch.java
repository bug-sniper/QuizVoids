package quizVoids.patches;

import com.badlogic.gdx.Input.Keys;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.TypeHelper;
import com.megacrit.cardcrawl.helpers.input.InputAction;

import quizVoids.QuizVoids;
import quizVoids.actions.QuizAction;
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
                    QuizVoids.quizPanel.confirm();
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
                if (inputAction.getKey() == Keys.ESCAPE){
                    QuizVoids.quizPanel.close();
                }
                return SpireReturn.Return(false);
            }
            if (inputAction.getKey() == Keys.L) {
                //TODO: Remove this when testing is done
                AbstractDungeon.actionManager.addToBottom(new QuizAction());
            }
            return SpireReturn.Continue();
        }
    }
}
