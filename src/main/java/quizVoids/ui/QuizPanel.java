package quizVoids.ui;

import java.util.List;
import java.util.function.Consumer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Clipboard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.TypeHelper;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.helpers.input.ScrollInputProcessor;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;

import basemod.BaseMod;
import basemod.ModPanel;
import basemod.interfaces.PostUpdateSubscriber;
import basemod.interfaces.RenderSubscriber;
import quizVoids.QuizVoids;

public class QuizPanel{
    public static final String[] TEXT = null;
    public String title;
    public static String textField;
    public Hitbox yesHb;
    public Hitbox noHb;
    private static final int CONFIRM_W = 360;
    private static final int CONFIRM_H = 414;
    private static final int YES_W = 173;
    private static final int NO_W = 161;
    private static final int BUTTON_H = 74;
    private Color screenColor;
    private Color uiColor;
    private float animTimer;
    private float waitTimer;
    private static final float ANIM_TIME = 0.25f;
    public boolean shown;
    private static final float SCREEN_DARKNESS = 0.7f;
    public MainMenuScreen.CurScreen sourceScreen;
    public static QuizPanel quizPanel;
    public static List<String> cacheCmdUp;
    public static int maxCache;
    public static List<String> cacheCmdDown;

    public QuizPanel() {
        this.yesHb = new Hitbox(160.0f * Settings.scale, 70.0f * Settings.scale);
        this.noHb = new Hitbox(160.0f * Settings.scale, 70.0f * Settings.scale);
        this.screenColor = new Color(0.0f, 0.0f, 0.0f, 0.0f);
        this.uiColor = new Color(1.0f, 1.0f, 1.0f, 0.0f);
        this.animTimer = 0.0f;
        this.waitTimer = 0.0f;
        this.shown = false;
        this.sourceScreen = null;
        QuizPanel.quizPanel = this;
    }
    
    public void update() {
        if (Gdx.input.isKeyPressed(67) && !QuizPanel.textField.equals("") && this.waitTimer <= 0.0f) {
            QuizPanel.textField = QuizPanel.textField.substring(0, QuizPanel.textField.length() - 1);
            this.waitTimer = 0.09f;
        }
        if (Gdx.input.isKeyJustPressed(19) && QuizPanel.cacheCmdUp.size() > 0) {
            QuizPanel.cacheCmdDown.add(0, QuizPanel.cacheCmdUp.get(0));
            QuizPanel.textField = QuizPanel.cacheCmdUp.get(0);
            QuizPanel.cacheCmdUp.remove(0);
        }
        if (Gdx.input.isKeyJustPressed(20) && QuizPanel.cacheCmdDown.size() > 0) {
            QuizPanel.cacheCmdUp.add(0, QuizPanel.cacheCmdDown.get(0));
            QuizPanel.textField = QuizPanel.cacheCmdDown.get(0);
            QuizPanel.cacheCmdDown.remove(0);
        }
        if (this.waitTimer > 0.0f) {
            this.waitTimer -= Gdx.graphics.getDeltaTime();
        }
        if (this.shown) {
            if (this.animTimer != 0.0f) {
                this.animTimer -= Gdx.graphics.getDeltaTime();
                if (this.animTimer < 0.0f) {
                    this.animTimer = 0.0f;
                }
                this.screenColor.a = Interpolation.fade.apply(0.8f, 0.0f, this.animTimer * 1.0f / 0.25f);
                this.uiColor.a = Interpolation.fade.apply(1.0f, 0.0f, this.animTimer * 1.0f / 0.25f);
            }
            else {
                this.updateYes();
                this.updateNo();
                if (InputActionSet.confirm.isJustPressed()) {
                    this.confirm();
                }
                else if (InputHelper.pressedEscape) {
                    InputHelper.pressedEscape = false;
                    this.cancel();
                }
                else if (InputHelper.isPasteJustPressed()) {
                    final Clipboard clipBoard = Gdx.app.getClipboard();
                    final String pasteText = clipBoard.getContents();
                    QuizPanel.textField += pasteText;
                }
            }
        }
        else if (this.animTimer != 0.0f) {
            this.animTimer -= Gdx.graphics.getDeltaTime();
            if (this.animTimer < 0.0f) {
                this.animTimer = 0.0f;
            }
            this.screenColor.a = Interpolation.fade.apply(0.0f, 0.8f, this.animTimer * 1.0f / 0.25f);
            this.uiColor.a = Interpolation.fade.apply(0.0f, 1.0f, this.animTimer * 1.0f / 0.25f);
        }
    }
    
    private void updateYes() {
        this.yesHb.update();
        if (this.yesHb.justHovered) {
            CardCrawlGame.sound.play("UI_HOVER");
        }
        if (InputHelper.justClickedLeft && this.yesHb.hovered) {
            CardCrawlGame.sound.play("UI_CLICK_1");
            this.yesHb.clickStarted = true;
        }
        if (CInputActionSet.proceed.isJustPressed()) {
            CInputActionSet.proceed.unpress();
            this.yesHb.clicked = true;
        }
        if (this.yesHb.clicked) {
            this.yesHb.clicked = false;
            this.confirm();
        }
    }
    
    private void updateNo() {
        this.noHb.update();
        if (this.noHb.justHovered) {
            CardCrawlGame.sound.play("UI_HOVER");
        }
        if (InputHelper.justClickedLeft && this.noHb.hovered) {
            CardCrawlGame.sound.play("UI_CLICK_1");
            this.noHb.clickStarted = true;
        }
        if (CInputActionSet.cancel.isJustPressed()) {
            this.noHb.clicked = true;
        }
        if (this.noHb.clicked) {
            this.noHb.clicked = false;
            this.cancel();
        }
    }
    
    public void show() {
        Gdx.input.setInputProcessor((InputProcessor)new TypeHelper(true));
        this.yesHb.move(860.0f * Settings.scale, Settings.OPTION_Y - 118.0f * Settings.scale);
        this.noHb.move(1062.0f * Settings.scale, Settings.OPTION_Y - 118.0f * Settings.scale);
        this.shown = true;
        this.animTimer = 0.25f;
        QuizPanel.textField = "";
    }
    
    public void show(final MainMenuScreen.CurScreen sourceScreen) {
        this.show();
        this.sourceScreen = sourceScreen;
    }
    
    public static void execute() {
        QuizPanel.quizPanel.confirm();
    }
    
    public void confirm() {
        QuizPanel.textField = QuizPanel.textField.trim();
        if (!"".equals(QuizPanel.textField)) {
            QuizVoids.submission = QuizPanel.textField;
            if (QuizPanel.cacheCmdUp.size() == 0 || !QuizPanel.textField.equals(QuizPanel.cacheCmdUp.get(QuizPanel.cacheCmdUp.size() - 1))) {
                if (QuizPanel.cacheCmdUp.size() < QuizPanel.maxCache) {
                    QuizPanel.cacheCmdUp.add(0, QuizPanel.textField);
                }
                else {
                    QuizPanel.cacheCmdUp.remove(QuizPanel.cacheCmdUp.size() - 1);
                    QuizPanel.cacheCmdUp.add(0, QuizPanel.textField);
                }
            }
        }
        QuizPanel.textField = "";
    }
    
    public void cancel() {
        this.close();
    }
    
    public void close() {
        this.yesHb.move(-1000.0f, -1000.0f);
        this.noHb.move(-1000.0f, -1000.0f);
        this.shown = false;
        this.animTimer = 0.25f;
        Gdx.input.setInputProcessor((InputProcessor)new ScrollInputProcessor());
        if (this.sourceScreen == null) {
            CardCrawlGame.mainMenuScreen.screen = MainMenuScreen.CurScreen.CUSTOM;
        }
        else {
            CardCrawlGame.mainMenuScreen.screen = this.sourceScreen;
            this.sourceScreen = null;
        }
    }
    
    public static boolean isFull() {
        return false;
    }
    
    public void render(final SpriteBatch sb) {
        sb.setColor(this.screenColor);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0.0f, 0.0f, (float)Settings.WIDTH, (float)Settings.HEIGHT);
        sb.setColor(this.uiColor);
        sb.draw(ImageMaster.RENAME_BOX, 160.0f, Settings.OPTION_Y - 160.0f, 160.0f, 160.0f, 1320.0f, 320.0f, Settings.scale * 1.1f, Settings.scale * 1.1f, 0.0f, 0, 0, 320, 320, false, false);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.cardTitleFont_small_N, QuizPanel.textField, 422.0f * Settings.scale, Settings.OPTION_Y + 4.0f * Settings.scale, this.uiColor);
        if (!isFull()) {
            final float tmpAlpha = (MathUtils.cosDeg((float)(System.currentTimeMillis() / 3L % 360L)) + 1.25f) / 3.0f * this.uiColor.a;
            FontHelper.renderSmartText(sb, FontHelper.cardTitleFont_small_N, "_", 422.0f * Settings.scale + FontHelper.getSmartWidth(FontHelper.cardTitleFont_small_N, QuizPanel.textField, 1000000.0f, 0.0f), Settings.OPTION_Y + 4.0f * Settings.scale, 100000.0f, 0.0f, new Color(1.0f, 1.0f, 1.0f, tmpAlpha));
        }
        Color c = Settings.GOLD_COLOR.cpy();
        c.a = this.uiColor.a;
        FontHelper.renderFontCentered(sb, FontHelper.cardTitleFont_N, QuizPanel.TEXT[1], Settings.WIDTH / 2.0f, Settings.OPTION_Y + 126.0f * Settings.scale, c);
        if (this.yesHb.clickStarted) {
            sb.setColor(new Color(1.0f, 1.0f, 1.0f, this.uiColor.a * 0.9f));
            sb.draw(ImageMaster.OPTION_YES, Settings.WIDTH / 2.0f - 86.5f - 100.0f * Settings.scale, Settings.OPTION_Y - 37.0f - 120.0f * Settings.scale, 86.5f, 37.0f, 173.0f, 74.0f, Settings.scale, Settings.scale, 0.0f, 0, 0, 173, 74, false, false);
            sb.setColor(new Color(this.uiColor));
        }
        else {
            sb.draw(ImageMaster.OPTION_YES, Settings.WIDTH / 2.0f - 86.5f - 100.0f * Settings.scale, Settings.OPTION_Y - 37.0f - 120.0f * Settings.scale, 86.5f, 37.0f, 173.0f, 74.0f, Settings.scale, Settings.scale, 0.0f, 0, 0, 173, 74, false, false);
        }
        if (!this.yesHb.clickStarted && this.yesHb.hovered) {
            sb.setColor(new Color(1.0f, 1.0f, 1.0f, this.uiColor.a * 0.25f));
            sb.setBlendFunction(770, 1);
            sb.draw(ImageMaster.OPTION_YES, Settings.WIDTH / 2.0f - 86.5f - 100.0f * Settings.scale, Settings.OPTION_Y - 37.0f - 120.0f * Settings.scale, 86.5f, 37.0f, 173.0f, 74.0f, Settings.scale, Settings.scale, 0.0f, 0, 0, 173, 74, false, false);
            sb.setBlendFunction(770, 771);
            sb.setColor(this.uiColor);
        }
        if (this.yesHb.clickStarted) {
            c = Color.LIGHT_GRAY.cpy();
        }
        else if (this.yesHb.hovered) {
            c = Settings.CREAM_COLOR.cpy();
        }
        else {
            c = Settings.GOLD_COLOR.cpy();
        }
        c.a = this.uiColor.a;
        FontHelper.renderFontCentered(sb, FontHelper.cardTitleFont_small_N, QuizPanel.TEXT[2], Settings.WIDTH / 2.0f - 110.0f * Settings.scale, Settings.OPTION_Y - 118.0f * Settings.scale, c, 1.0f);
        sb.draw(ImageMaster.OPTION_NO, Settings.WIDTH / 2.0f - 80.5f + 106.0f * Settings.scale, Settings.OPTION_Y - 37.0f - 120.0f * Settings.scale, 80.5f, 37.0f, 161.0f, 74.0f, Settings.scale, Settings.scale, 0.0f, 0, 0, 161, 74, false, false);
        if (!this.noHb.clickStarted && this.noHb.hovered) {
            sb.setColor(new Color(1.0f, 1.0f, 1.0f, this.uiColor.a * 0.25f));
            sb.setBlendFunction(770, 1);
            sb.draw(ImageMaster.OPTION_NO, Settings.WIDTH / 2.0f - 80.5f + 106.0f * Settings.scale, Settings.OPTION_Y - 37.0f - 120.0f * Settings.scale, 80.5f, 37.0f, 161.0f, 74.0f, Settings.scale, Settings.scale, 0.0f, 0, 0, 161, 74, false, false);
            sb.setBlendFunction(770, 771);
            sb.setColor(this.uiColor);
        }
        if (this.noHb.clickStarted) {
            c = Color.LIGHT_GRAY.cpy();
        }
        else if (this.noHb.hovered) {
            c = Settings.CREAM_COLOR.cpy();
        }
        else {
            c = Settings.GOLD_COLOR.cpy();
        }
        c.a = this.uiColor.a;
        FontHelper.renderFontCentered(sb, FontHelper.cardTitleFont_small_N, QuizPanel.TEXT[3], Settings.WIDTH / 2.0f + 110.0f * Settings.scale, Settings.OPTION_Y - 118.0f * Settings.scale, c, 1.0f);
        if (this.shown) {
            if (Settings.isControllerMode) {
                sb.draw(CInputActionSet.proceed.getKeyImg(), 770.0f * Settings.scale - 32.0f, Settings.OPTION_Y - 32.0f - 140.0f * Settings.scale, 32.0f, 32.0f, 64.0f, 64.0f, Settings.scale, Settings.scale, 0.0f, 0, 0, 64, 64, false, false);
                sb.draw(CInputActionSet.cancel.getKeyImg(), 1150.0f * Settings.scale - 32.0f, Settings.OPTION_Y - 32.0f - 140.0f * Settings.scale, 32.0f, 32.0f, 64.0f, 64.0f, Settings.scale, Settings.scale, 0.0f, 0, 0, 64, 64, false, false);
            }
            this.yesHb.render(sb);
            this.noHb.render(sb);
            FontHelper.renderFontCentered(sb, FontHelper.eventBodyText, QuizPanel.TEXT[4], Settings.WIDTH / 2.0f, 100.0f * Settings.scale, new Color(1.0f, 0.3f, 0.3f, c.a));
        }
    }
}