package net.studymongolian.todochimee;

import android.content.Context;
import android.util.AttributeSet;

import net.studymongolian.mongollibrary.Key;
import net.studymongolian.mongollibrary.KeyBackspace;
import net.studymongolian.mongollibrary.KeyImage;
import net.studymongolian.mongollibrary.KeyKeyboardChooser;
import net.studymongolian.mongollibrary.KeyText;
import net.studymongolian.mongollibrary.Keyboard;
import net.studymongolian.mongollibrary.MongolCode;
import net.studymongolian.mongollibrary.PopupKeyCandidate;

import java.util.ArrayList;
import java.util.List;

public class KeyboardTodo extends Keyboard {

    // name to use in the keyboard popup chooser
    private static final String DEFAULT_DISPLAY_NAME = "ᠺᠣᠮᠫᠢᠦ᠋ᠲ᠋ᠧᠷ";

    // Row 1
    protected KeyText mKeyQ;
    protected KeyText mKeyW;
    protected KeyText mKeyE;
    protected KeyText mKeyR;
    protected KeyText mKeyT;
    protected KeyText mKeyY;
    protected KeyText mKeyU;
    protected KeyText mKeyI;
    protected KeyText mKeyO;
    protected KeyText mKeyP;

    // Row 2
    protected KeyText mKeyA;
    protected KeyText mKeyS;
    protected KeyText mKeyD;
    protected KeyText mKeyF;
    protected KeyText mKeyG;
    protected KeyText mKeyH;
    protected KeyText mKeyJ;
    protected KeyText mKeyK;
    protected KeyText mKeyL;
    protected KeyText mKeyNg;

    // Row 3
    protected KeyText mKeyZ;
    protected KeyText mKeyX;
    protected KeyText mKeyC;
    protected KeyText mKeyV;
    protected KeyText mKeyB;
    protected KeyText mKeyN;
    protected KeyText mKeyM;
    protected KeyText mKeyLongVowel;
    protected KeyBackspace mKeyBackspace;

    // Row 4
    protected KeyKeyboardChooser mKeyKeyboard;
    protected KeyText mKeyQuote;
    protected KeyText mKeyComma;
    protected KeyText mKeySpace;
    protected KeyText mKeyPeriod;
    protected KeyText mKeyQuestion;
    protected KeyImage mKeyReturn;

    private static final String KEY_G_PUNCT_SUB = "+";
    private static final String KEY_H_PUNCT_SUB = "$";
    private static final String KEY_J_PUNCT_SUB = "";
    private static final String KEY_K_PUNCT_SUB = "";
    private static final String KEY_L_PUNCT_SUB = "";
    private static final String KEY_NG_PUNCT_SUB = "";
    private static final String KEY_ZWJ_PUNCT_SUB = "\\";
    private static final String KEY_X_PUNCT_SUB = "";
    private static final String KEY_C_PUNCT_SUB = ".";
    private static final String KEY_V_PUNCT_SUB = "";
    private static final String KEY_B_PUNCT_SUB = "~";

    private static final String NEWLINE = "\n";
    private static final String KEY_SPACE_SUB_DISPLAY = " ";

    public KeyboardTodo(Context context) {
        super(context);
        init(context);
    }

    public KeyboardTodo(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public KeyboardTodo(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    protected void init(Context context) {

        // keyboard layout

        // | Q | W | E | R | T | Y | U | I | O | P |  Row 1
        // | A | S | D | F | G | H | J | K | L | NG|  Row 2
        // | Z | X | C | V | B | N | M |long | del |  Row 3
        // |  kb | ! | , |   space   | : | ? | ret |  Row 4

        // actual layout work is done by Keyboard superclass's onLayout
        mNumberOfKeysInRow = new int[]{10, 10, 9, 7}; // 36 keys total
        // the key weights for each row should sum to 1
        float r3 = 0.10625f;
        mKeyWeights = new float[]{
                0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f,     // row 0
                0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f,     // row 1
                r3, r3, r3, r3, r3, r3, r3, r3, 0.15f,                          // row 2
                0.15f, 0.1f, 0.1f, 0.3f, 0.1f, 0.1f, 0.15f};                    // row 3

        // Make sure that the total keys added to this ViewGroup below equals
        // the mNumberOfKeysInRow and mKeyWeights array totals above.

        instantiateKeys(context);
        setKeyValues();
        setNonChangingKeyValues();
        setKeyImages();
        setListeners();
        addKeysToKeyboard();
        applyThemeToKeys();
    }

    private void instantiateKeys(Context context) {

        // Row 1
        mKeyQ = new KeyText(context);
        mKeyW = new KeyText(context);
        mKeyE = new KeyText(context);
        mKeyR = new KeyText(context);
        mKeyT = new KeyText(context);
        mKeyY = new KeyText(context);
        mKeyU = new KeyText(context);
        mKeyI = new KeyText(context);
        mKeyO = new KeyText(context);
        mKeyP = new KeyText(context);

        // Row 2
        mKeyA = new KeyText(context);
        mKeyS = new KeyText(context);
        mKeyD = new KeyText(context);
        mKeyF = new KeyText(context);
        mKeyG = new KeyText(context);
        mKeyH = new KeyText(context);
        mKeyJ = new KeyText(context);
        mKeyK = new KeyText(context);
        mKeyL = new KeyText(context);
        mKeyNg = new KeyText(context);

        // Row 3
        mKeyZ = new KeyText(context);
        mKeyX = new KeyText(context);
        mKeyC = new KeyText(context);
        mKeyV = new KeyText(context);
        mKeyB = new KeyText(context);
        mKeyN = new KeyText(context);
        mKeyM = new KeyText(context);
        mKeyLongVowel = new KeyText(context);
        mKeyBackspace = new KeyBackspace(context);

        // Row 4
        mKeyKeyboard = new KeyKeyboardChooser(context);
        mKeyQuote = new KeyText(context);
        mKeyComma = new KeyText(context);
        mKeySpace = new KeyText(context);
        mKeyPeriod = new KeyText(context);
        mKeyQuestion = new KeyText(context);
        mKeyReturn = new KeyImage(context);
    }

    private void setKeyValues() {

        rotatePrimaryText();

        // Row 1

        mKeyQ.setText(MongolCode.Uni.TODO_TSA);
        mKeyQ.setSubText(MongolCode.Uni.TODO_CHA);

        mKeyW.setText(MongolCode.Uni.TODO_WA);
        mKeyW.setSubText("");

        mKeyE.setText(MongolCode.Uni.TODO_E);
        mKeyE.setSubText("");

        mKeyR.setText(MongolCode.Uni.RA);
        mKeyR.setSubText("");

        mKeyT.setText(MongolCode.Uni.TODO_TA);
        mKeyT.setSubText("");

        mKeyY.setText(MongolCode.Uni.TODO_YA);
        mKeyY.setSubText("");

        mKeyU.setText(MongolCode.Uni.TODO_UE);
        mKeyU.setSubText("");

        mKeyI.setText(MongolCode.Uni.TODO_I);
        mKeyI.setSubText("");

        mKeyO.setText(MongolCode.Uni.TODO_OE);
        mKeyO.setSubText("");

        mKeyP.setText(MongolCode.Uni.TODO_PA);
        mKeyP.setSubText("");

        // Row 2
        mKeyA.setText(MongolCode.Uni.A);
        mKeyA.setSubText("");

        mKeyS.setText(MongolCode.Uni.SA);
        mKeyS.setSubText("");

        mKeyD.setText(MongolCode.Uni.TODO_DA);
        mKeyD.setSubText("");

        mKeyF.setText(MongolCode.Uni.WA);
        mKeyF.setSubText("");

        mKeyG.setText(MongolCode.Uni.TODO_GA);
        mKeyG.setSubText(MongolCode.Uni.TODO_GAA);

        mKeyH.setText(MongolCode.Uni.TODO_QA);
        mKeyH.setSubText(MongolCode.Uni.TODO_HAA);

        mKeyJ.setText(MongolCode.Uni.TODO_JA);
        mKeyJ.setSubText(MongolCode.Uni.TODO_JIA);

        mKeyK.setText(MongolCode.Uni.TODO_KA);
        mKeyK.setSubText("");

        mKeyL.setText(MongolCode.Uni.LA);
        mKeyL.setSubText(MongolCode.Uni.LHA);

        mKeyNg.setText(MongolCode.Uni.TODO_ANG);
        mKeyNg.setSubText("");

        // Row 3

        mKeyZ.setText(MongolCode.Uni.CHA);
        mKeyZ.setSubText(MongolCode.Uni.TODO_DZA);

        mKeyX.setText(MongolCode.Uni.SHA);
        mKeyX.setSubText("");

        mKeyC.setText(MongolCode.Uni.TODO_O);
        mKeyC.setSubText("");

        mKeyV.setText(MongolCode.Uni.TODO_U);
        mKeyV.setSubText("");

        mKeyB.setText(MongolCode.Uni.TODO_BA);
        mKeyB.setSubText("");

        mKeyN.setText(MongolCode.Uni.NA);
        mKeyN.setSubText(MongolCode.Uni.TODO_NIA);

        mKeyM.setText(MongolCode.Uni.TODO_MA);
        mKeyM.setSubText("");

        mKeyLongVowel.setText(MongolCode.Uni.TODO_LONG_VOWEL_SIGN);
        mKeyLongVowel.setSubText(MongolCode.Uni.MONGOLIAN_NIRUGU);
    }

    private void rotatePrimaryText() {
        mKeyQ.setIsRotatedPrimaryText(true);
        mKeyW.setIsRotatedPrimaryText(true);
        mKeyE.setIsRotatedPrimaryText(true);
        mKeyR.setIsRotatedPrimaryText(true);
        mKeyT.setIsRotatedPrimaryText(true);
        mKeyY.setIsRotatedPrimaryText(true);
        mKeyU.setIsRotatedPrimaryText(true);
        mKeyI.setIsRotatedPrimaryText(true);
        mKeyO.setIsRotatedPrimaryText(true);
        mKeyP.setIsRotatedPrimaryText(true);
        mKeyH.setIsRotatedPrimaryText(true);
    }

    private void setPuncuationKeyValues() {

        dontRotatePrimaryTextForSelectKeys();

        // Row 1

        mKeyQ.setText("1");
        mKeyQ.setSubText("");

        mKeyW.setText("2");
        mKeyW.setSubText("");

        mKeyE.setText("3");
        mKeyE.setSubText("");

        mKeyR.setText("4");
        mKeyR.setSubText("");

        mKeyT.setText("5");
        mKeyT.setSubText("");

        mKeyY.setText("6");
        mKeyY.setSubText("");

        mKeyU.setText("7");
        mKeyU.setSubText("");

        mKeyI.setText("8");
        mKeyI.setSubText("");

        mKeyO.setText("9");
        mKeyO.setSubText("");

        mKeyP.setText("0");
        mKeyP.setSubText("");

        // Row 2

        mKeyA.setText(MongolCode.Uni.VERTICAL_LEFT_PARENTHESIS);
        mKeyA.setSubText(MongolCode.Uni.VERTICAL_LEFT_SQUARE_BRACKET);

        mKeyS.setText(MongolCode.Uni.VERTICAL_RIGHT_PARENTHESIS);
        mKeyS.setSubText(MongolCode.Uni.VERTICAL_RIGHT_SQUARE_BRACKET);

        mKeyD.setText(MongolCode.Uni.VERTICAL_LEFT_DOUBLE_ANGLE_BRACKET);
        mKeyD.setSubText(MongolCode.Uni.VERTICAL_LEFT_ANGLE_BRACKET);

        mKeyF.setText(MongolCode.Uni.VERTICAL_RIGHT_DOUBLE_ANGLE_BRACKET);
        mKeyF.setSubText(MongolCode.Uni.VERTICAL_RIGHT_ANGLE_BRACKET);

        mKeyG.setText("=");
        mKeyG.setSubText(KEY_G_PUNCT_SUB);

        mKeyH.setText("¥");
        mKeyH.setSubText(KEY_H_PUNCT_SUB);

        mKeyJ.setText("'");
        mKeyJ.setSubText(KEY_J_PUNCT_SUB);

        mKeyK.setText("\"");
        mKeyK.setSubText(KEY_K_PUNCT_SUB);

        mKeyL.setText("#");
        mKeyL.setSubText(KEY_L_PUNCT_SUB);

        mKeyNg.setText("|");
        mKeyNg.setSubText(KEY_NG_PUNCT_SUB);

        // Row 3

        mKeyZ.setText(MongolCode.Uni.REFERENCE_MARK);
        mKeyZ.setSubText(MongolCode.Uni.MONGOLIAN_FOUR_DOTS);

        mKeyX.setText(MongolCode.Uni.MONGOLIAN_BIRGA,
                MongolCode.Uni.MONGOLIAN_BIRGA);
        mKeyX.setSubText(KEY_X_PUNCT_SUB);

        mKeyC.setText(MongolCode.Uni.MIDDLE_DOT);
        mKeyC.setSubText(KEY_C_PUNCT_SUB);

        mKeyV.setText(MongolCode.Uni.MONGOLIAN_ELLIPSIS);
        mKeyV.setSubText(KEY_V_PUNCT_SUB);

        mKeyB.setText(MongolCode.Uni.VERTICAL_EM_DASH);
        mKeyB.setSubText(KEY_B_PUNCT_SUB);

        mKeyN.setText(MongolCode.Uni.MONGOLIAN_COLON);
        mKeyN.setSubText(MongolCode.Uni.VERTICAL_COMMA);

        mKeyM.setText(MongolCode.Uni.QUESTION_EXCLAMATION_MARK);
        mKeyM.setSubText("");

        mKeyLongVowel.setText("/");
        mKeyLongVowel.setSubText(KEY_ZWJ_PUNCT_SUB);
    }

    private void dontRotatePrimaryTextForSelectKeys() {
        mKeyQ.setIsRotatedPrimaryText(false);
        mKeyW.setIsRotatedPrimaryText(false);
        mKeyE.setIsRotatedPrimaryText(false);
        mKeyR.setIsRotatedPrimaryText(false);
        mKeyT.setIsRotatedPrimaryText(false);
        mKeyY.setIsRotatedPrimaryText(false);
        mKeyU.setIsRotatedPrimaryText(false);
        mKeyI.setIsRotatedPrimaryText(false);
        mKeyO.setIsRotatedPrimaryText(false);
        mKeyP.setIsRotatedPrimaryText(false);
        mKeyH.setIsRotatedPrimaryText(false);
    }

    private void setNonChangingKeyValues() {
        mKeyQuote.setText(MongolCode.Uni.VERTICAL_LEFT_WHITE_CORNER_BRACKET);
        mKeyQuote.setSubText(MongolCode.Uni.VERTICAL_RIGHT_WHITE_CORNER_BRACKET);

        mKeyComma.setText(MongolCode.Uni.VERTICAL_COMMA);
        mKeyComma.setSubText(MongolCode.Uni.VERTICAL_SEMICOLON);

        mKeySpace.setText(" ");
        if (hasCandidatesView()) {
            mKeySpace.setSubText(KEY_SPACE_SUB_DISPLAY);
        }

        mKeyPeriod.setText(MongolCode.Uni.VERTICAL_IDEOGRAPHIC_FULL_STOP);
        mKeyPeriod.setSubText(MongolCode.Uni.MIDDLE_DOT);

        mKeyQuestion.setText(MongolCode.Uni.VERTICAL_QUESTION_MARK);
        mKeyQuestion.setSubText(MongolCode.Uni.VERTICAL_EXCLAMATION_MARK);

        mKeyReturn.setText(NEWLINE);
    }

    private void setKeyImages() {
        mKeyBackspace.setImage(getBackspaceImage());
        mKeyKeyboard.setImage(getKeyboardImage());
        mKeyReturn.setImage(getReturnImage());
    }

    private void setListeners() {

        // Row 1
        mKeyQ.setKeyListener(this);
        mKeyW.setKeyListener(this);
        mKeyE.setKeyListener(this);
        mKeyR.setKeyListener(this);
        mKeyT.setKeyListener(this);
        mKeyY.setKeyListener(this);
        mKeyU.setKeyListener(this);
        mKeyI.setKeyListener(this);
        mKeyO.setKeyListener(this);
        mKeyP.setKeyListener(this);

        // Row 2
        mKeyA.setKeyListener(this);
        mKeyS.setKeyListener(this);
        mKeyD.setKeyListener(this);
        mKeyF.setKeyListener(this);
        mKeyG.setKeyListener(this);
        mKeyH.setKeyListener(this);
        mKeyJ.setKeyListener(this);
        mKeyK.setKeyListener(this);
        mKeyL.setKeyListener(this);
        mKeyNg.setKeyListener(this);

        // Row 3
        mKeyZ.setKeyListener(this);
        mKeyX.setKeyListener(this);
        mKeyC.setKeyListener(this);
        mKeyV.setKeyListener(this);
        mKeyB.setKeyListener(this);
        mKeyN.setKeyListener(this);
        mKeyM.setKeyListener(this);
        mKeyLongVowel.setKeyListener(this);
        mKeyBackspace.setKeyListener(this);

        // Row 4
        mKeyKeyboard.setKeyListener(this);
        mKeyQuote.setKeyListener(this);
        mKeyComma.setKeyListener(this);
        mKeySpace.setKeyListener(this);
        mKeyPeriod.setKeyListener(this);
        mKeyQuestion.setKeyListener(this);
        mKeyReturn.setKeyListener(this);
    }

    private void addKeysToKeyboard() {

        // Row 1
        addView(mKeyQ);
        addView(mKeyW);
        addView(mKeyE);
        addView(mKeyR);
        addView(mKeyT);
        addView(mKeyY);
        addView(mKeyU);
        addView(mKeyI);
        addView(mKeyO);
        addView(mKeyP);

        // Row 2
        addView(mKeyA);
        addView(mKeyS);
        addView(mKeyD);
        addView(mKeyF);
        addView(mKeyG);
        addView(mKeyH);
        addView(mKeyJ);
        addView(mKeyK);
        addView(mKeyL);
        addView(mKeyNg);

        // Row 3
        addView(mKeyZ);
        addView(mKeyX);
        addView(mKeyC);
        addView(mKeyV);
        addView(mKeyB);
        addView(mKeyN);
        addView(mKeyM);
        addView(mKeyLongVowel);
        addView(mKeyBackspace);

        // Row 4
        addView(mKeyKeyboard);
        addView(mKeyQuote);
        addView(mKeyComma);
        addView(mKeySpace);
        addView(mKeyPeriod);
        addView(mKeyQuestion);
        addView(mKeyReturn);
    }

    public List<PopupKeyCandidate> getPopupCandidates(Key key) {
        // get the appropriate candidates based on the key pressed
        if (key == mKeyQ) {
            return getCandidatesForQ();
        } else if (key == mKeyW) {
            return getCandidatesForW();
        } else if (key == mKeyE) {
            return getCandidatesForE();
        } else if (key == mKeyR) {
            return getCandidatesForR();
        } else if (key == mKeyT) {
            return getCandidatesForT();
        } else if (key == mKeyY) {
            return getCandidatesForY();
        } else if (key == mKeyU) {
            return getCandidatesForU();
        } else if (key == mKeyI) {
            return getCandidatesForI();
        } else if (key == mKeyO) {
            return getCandidatesForO();
        } else if (key == mKeyP) {
            return getCandidatesForP();
        } else if (key == mKeyA) {
            return getCandidatesForA();
        } else if (key == mKeyS) {
            return getCandidatesForS();
        } else if (key == mKeyD) {
            return getCandidatesForD();
        } else if (key == mKeyF) {
            return getCandidatesForF();
        } else if (key == mKeyG) {
            return getCandidatesForG();
        } else if (key == mKeyH) {
            return getCandidatesForH();
        } else if (key == mKeyJ) {
            return getCandidatesForJ();
        } else if (key == mKeyK) {
            return getCandidatesForK();
        } else if (key == mKeyL) {
            return getCandidatesForL();
        } else if (key == mKeyNg) {
            return getCandidatesForNG();
        } else if (key == mKeyZ) {
            return getCandidatesForZ();
        } else if (key == mKeyX) {
            return getCandidatesForX();
        } else if (key == mKeyC) {
            return getCandidatesForC();
        } else if (key == mKeyV) {
            return getCandidatesForV();
        } else if (key == mKeyB) {
            return getCandidatesForB();
        } else if (key == mKeyN) {
            return getCandidatesForN();
        } else if (key == mKeyM) {
            return getCandidatesForM();
        } else if (key == mKeyLongVowel) {
            return getCandidatesForLongVowel();
        } else if (key == mKeyKeyboard) {
            return getCandidatesForKeyboardKey();
        } else if (key == mKeyQuote) {
            return getCandidatesForQuote();
        } else if (key == mKeyComma) {
            return getCandidatesForComma();
        } else if (key == mKeySpace) {
            return getCandidatesForSpace();
        } else if (key == mKeyPeriod) {
            return getCandidatesForPeriod();
        } else if (key == mKeyQuestion) {
            return getCandidatesForQuestion();
        }

        return null;
    }

    private List<PopupKeyCandidate> getCandidatesForQ() {
        List<PopupKeyCandidate> candidates = new ArrayList<>();
        if (mIsShowingPunctuation) {
            return candidates;
        }
        candidates.add(new PopupKeyCandidate(
                "" + MongolCode.Uni.TODO_CHA));
        return candidates;
    }

    private List<PopupKeyCandidate> getCandidatesForW() {
        List<PopupKeyCandidate> candidates = new ArrayList<>();
        if (mIsShowingPunctuation) {
            return candidates;
        }
        return candidates;
    }

    private List<PopupKeyCandidate> getCandidatesForE() {
        List<PopupKeyCandidate> candidates = new ArrayList<>();
        if (mIsShowingPunctuation) {
            return candidates;
        }

        if (!isIsolateOrInitial()) {
            PopupKeyCandidate medial_E_FVS1 = new PopupKeyCandidate(
                    "" + MongolCode.Uni.TODO_E + MongolCode.Uni.FVS1,
                    "" + MongolCode.Uni.ZWJ + MongolCode.Uni.TODO_E + MongolCode.Uni.FVS1 + MongolCode.Uni.ZWJ,
                    "" + MongolCode.Uni.TODO_E + MongolCode.Uni.FVS1 + MongolCode.Uni.ZWJ);
            candidates.add(medial_E_FVS1);
        }

        return candidates;
    }

    private List<PopupKeyCandidate> getCandidatesForR() {
        List<PopupKeyCandidate> candidates = new ArrayList<>();
        if (mIsShowingPunctuation) {
            return candidates;
        }

        return candidates;
    }

    private List<PopupKeyCandidate> getCandidatesForT() {
        List<PopupKeyCandidate> candidates = new ArrayList<>();
        if (mIsShowingPunctuation) {
            return candidates;
        }

        return candidates;
    }

    private List<PopupKeyCandidate> getCandidatesForY() {
        List<PopupKeyCandidate> candidates = new ArrayList<>();
        if (mIsShowingPunctuation) {
            return candidates;
        }

        return candidates;
    }

    private List<PopupKeyCandidate> getCandidatesForU() {
        List<PopupKeyCandidate> candidates = new ArrayList<>();
        if (mIsShowingPunctuation) {
            return candidates;
        }

        if (isIsolateOrInitial()) {
            PopupKeyCandidate isolate_UE_FVS1 = new PopupKeyCandidate(
                    "" + MongolCode.Uni.TODO_UE + MongolCode.Uni.FVS1);
            candidates.add(isolate_UE_FVS1);
        } else {
            PopupKeyCandidate medial_UE_FVS1 = new PopupKeyCandidate(
                    "" + MongolCode.Uni.TODO_UE + MongolCode.Uni.FVS1,
                    "" + MongolCode.Uni.ZWJ + MongolCode.Uni.TODO_UE + MongolCode.Uni.FVS1 + MongolCode.Uni.ZWJ,
                    "" + MongolCode.Uni.TODO_UE + MongolCode.Uni.FVS1 + MongolCode.Uni.ZWJ);
            candidates.add(medial_UE_FVS1);
        }

        return candidates;
    }

    private List<PopupKeyCandidate> getCandidatesForI() {
        List<PopupKeyCandidate> candidates = new ArrayList<>();
        if (mIsShowingPunctuation) {
            return candidates;
        }

        if (!isIsolateOrInitial()) {
            PopupKeyCandidate medial_I_FVS1 = new PopupKeyCandidate(
                    "" + MongolCode.Uni.TODO_I + MongolCode.Uni.FVS1,
                    "" + MongolCode.Uni.ZWJ + MongolCode.Uni.TODO_I + MongolCode.Uni.FVS1 + MongolCode.Uni.ZWJ,
                    "" + MongolCode.Uni.TODO_I + MongolCode.Uni.FVS1 + MongolCode.Uni.ZWJ);
            candidates.add(medial_I_FVS1);
        }

        return candidates;
    }

    private List<PopupKeyCandidate> getCandidatesForO() {
        List<PopupKeyCandidate> candidates = new ArrayList<>();
        if (mIsShowingPunctuation) {
            return candidates;
        }

        if (!isIsolateOrInitial()) {
            PopupKeyCandidate medial_OE_FVS1 = new PopupKeyCandidate(
                    "" + MongolCode.Uni.TODO_OE + MongolCode.Uni.FVS1,
                    "" + MongolCode.Uni.ZWJ + MongolCode.Uni.TODO_OE + MongolCode.Uni.FVS1 + MongolCode.Uni.ZWJ,
                    "" + MongolCode.Uni.TODO_OE + MongolCode.Uni.FVS1 + MongolCode.Uni.ZWJ);
            candidates.add(medial_OE_FVS1);
        }

        return candidates;
    }

    private List<PopupKeyCandidate> getCandidatesForP() {
        List<PopupKeyCandidate> candidates = new ArrayList<>();
        if (mIsShowingPunctuation) {
            return candidates;
        }
        return candidates;
    }

    private List<PopupKeyCandidate> getCandidatesForA() {
        List<PopupKeyCandidate> candidates = new ArrayList<>();
        if (mIsShowingPunctuation) {
            candidates.add(new PopupKeyCandidate(
                    "" + MongolCode.Uni.VERTICAL_LEFT_SQUARE_BRACKET));
            return candidates;
        }

        if (isIsolateOrInitial()) {
            PopupKeyCandidate a_fvs1_isolate = new PopupKeyCandidate(
                    "" + MongolCode.Uni.A + MongolCode.Uni.FVS1);
            candidates.add(a_fvs1_isolate);
        } else {
            PopupKeyCandidate a_fvs1_final = new PopupKeyCandidate(
                    "" + MongolCode.Uni.A + MongolCode.Uni.FVS1,
                    "" + MongolCode.Uni.ZWJ + MongolCode.Uni.A + MongolCode.Uni.FVS1);
            candidates.add(a_fvs1_final);
        }

        return candidates;
    }

    private List<PopupKeyCandidate> getCandidatesForS() {
        List<PopupKeyCandidate> candidates = new ArrayList<>();
        if (mIsShowingPunctuation) {
            candidates.add(new PopupKeyCandidate(
                    "" + MongolCode.Uni.VERTICAL_RIGHT_SQUARE_BRACKET));
            return candidates;
        }
        return candidates;
    }

    private List<PopupKeyCandidate> getCandidatesForD() {
        List<PopupKeyCandidate> candidates = new ArrayList<>();
        if (mIsShowingPunctuation) {
            candidates.add(new PopupKeyCandidate(
                    "" + MongolCode.Uni.VERTICAL_LEFT_ANGLE_BRACKET));
            return candidates;
        }

        return candidates;
    }

    private List<PopupKeyCandidate> getCandidatesForF() {
        List<PopupKeyCandidate> candidates = new ArrayList<>();
        if (mIsShowingPunctuation) {
            candidates.add(new PopupKeyCandidate(
                    "" + MongolCode.Uni.VERTICAL_RIGHT_ANGLE_BRACKET));
            return candidates;
        }
        return candidates;
    }

    private List<PopupKeyCandidate> getCandidatesForG() {
        List<PopupKeyCandidate> candidates = new ArrayList<>();
        if (mIsShowingPunctuation) {
            candidates.add(new PopupKeyCandidate("+"));
            candidates.add(new PopupKeyCandidate("-"));
            candidates.add(new PopupKeyCandidate("×"));
            candidates.add(new PopupKeyCandidate("÷"));
            candidates.add(new PopupKeyCandidate("≠"));
            candidates.add(new PopupKeyCandidate("≈"));
            return candidates;
        }

        PopupKeyCandidate gaa = new PopupKeyCandidate(
                "" + MongolCode.Uni.TODO_GAA,
                "" + MongolCode.Uni.TODO_GAA);
        candidates.add(gaa);

        if (!isIsolateOrInitial()) {
            PopupKeyCandidate ga_fvs1_medial = new PopupKeyCandidate(
                    "" + MongolCode.Uni.TODO_GA + MongolCode.Uni.FVS1,
                    "" + MongolCode.Uni.ZWJ + MongolCode.Uni.TODO_GA + MongolCode.Uni.FVS1 + MongolCode.Uni.ZWJ,
                    "" + MongolCode.Uni.TODO_GA + MongolCode.Uni.FVS1 + MongolCode.Uni.ZWJ);
            candidates.add(ga_fvs1_medial);
        }

        return candidates;
    }

    private List<PopupKeyCandidate> getCandidatesForH() {
        List<PopupKeyCandidate> candidates = new ArrayList<>();
        if (mIsShowingPunctuation) {
            candidates.add(new PopupKeyCandidate("$"));
            candidates.add(new PopupKeyCandidate("₮"));
            return candidates;
        }

        PopupKeyCandidate haa = new PopupKeyCandidate(
                "" + MongolCode.Uni.TODO_HAA,
                "" + MongolCode.Uni.TODO_HAA);
        candidates.add(haa);

        if (isIsolateOrInitial()) {
            PopupKeyCandidate qa_fvs1_initial = new PopupKeyCandidate(
                    "" + MongolCode.Uni.TODO_QA + MongolCode.Uni.FVS1,
                    "" + MongolCode.Uni.TODO_QA + MongolCode.Uni.FVS1 + MongolCode.Uni.ZWJ,
                    "" + MongolCode.Uni.TODO_QA + MongolCode.Uni.FVS1 + MongolCode.Uni.ZWJ);
            candidates.add(qa_fvs1_initial);
        } else {
            PopupKeyCandidate qa_fvs1_medial = new PopupKeyCandidate(
                    "" + MongolCode.Uni.TODO_QA + MongolCode.Uni.FVS1,
                    "" + MongolCode.Uni.ZWJ + MongolCode.Uni.TODO_QA + MongolCode.Uni.FVS1 + MongolCode.Uni.ZWJ,
                    "" + MongolCode.Uni.TODO_QA + MongolCode.Uni.FVS1 + MongolCode.Uni.ZWJ);
            candidates.add(qa_fvs1_medial);
        }

        return candidates;
    }

    private List<PopupKeyCandidate> getCandidatesForJ() {
        List<PopupKeyCandidate> candidates = new ArrayList<>();
        if (mIsShowingPunctuation) {
            return candidates;
        }
        PopupKeyCandidate jia = new PopupKeyCandidate(
                "" + MongolCode.Uni.TODO_JIA,
                "" + MongolCode.Uni.TODO_JIA);
        candidates.add(jia);
        return candidates;
    }

    private List<PopupKeyCandidate> getCandidatesForK() {
        List<PopupKeyCandidate> candidates = new ArrayList<>();
        if (mIsShowingPunctuation) {
            return candidates;
        }
        return candidates;
    }

    private List<PopupKeyCandidate> getCandidatesForL() {
        List<PopupKeyCandidate> candidates = new ArrayList<>();
        if (mIsShowingPunctuation) {
            return candidates;
        }
        PopupKeyCandidate lha = new PopupKeyCandidate(
                "" + MongolCode.Uni.LHA,
                "" + MongolCode.Uni.LHA);
        candidates.add(lha);
        return candidates;
    }

    private List<PopupKeyCandidate> getCandidatesForNG() {
        List<PopupKeyCandidate> candidates = new ArrayList<>();
        if (mIsShowingPunctuation) {
            return candidates;
        }
        return candidates;
    }

    private List<PopupKeyCandidate> getCandidatesForZ() {
        List<PopupKeyCandidate> candidates = new ArrayList<>();
        if (mIsShowingPunctuation) {
            candidates.add(new PopupKeyCandidate(
                    "" + MongolCode.Uni.MONGOLIAN_FOUR_DOTS,
                    "" + MongolCode.Uni.MONGOLIAN_FOUR_DOTS));
            candidates.add(new PopupKeyCandidate("*"));
            return candidates;
        }
        PopupKeyCandidate dza = new PopupKeyCandidate(
                "" + MongolCode.Uni.TODO_DZA,
                "" + MongolCode.Uni.TODO_DZA);
        candidates.add(dza);
        return candidates;
    }

    private List<PopupKeyCandidate> getCandidatesForX() {
        List<PopupKeyCandidate> candidates = new ArrayList<>();
        if (mIsShowingPunctuation) {
            return candidates;
        }
        return candidates;
    }

    private List<PopupKeyCandidate> getCandidatesForC() {
        List<PopupKeyCandidate> candidates = new ArrayList<>();
        if (mIsShowingPunctuation) {
            candidates.add(new PopupKeyCandidate(KEY_C_PUNCT_SUB));
            return candidates;
        }

        if (!isIsolateOrInitial()) {
            PopupKeyCandidate medial_O_FVS1 = new PopupKeyCandidate(
                    "" + MongolCode.Uni.TODO_O + MongolCode.Uni.FVS1,
                    "" + MongolCode.Uni.ZWJ + MongolCode.Uni.TODO_O + MongolCode.Uni.FVS1 + MongolCode.Uni.ZWJ,
                    "" + MongolCode.Uni.TODO_O + MongolCode.Uni.FVS1 + MongolCode.Uni.ZWJ);
            candidates.add(medial_O_FVS1);
        }

        return candidates;
    }

    private List<PopupKeyCandidate> getCandidatesForV() {
        List<PopupKeyCandidate> candidates = new ArrayList<>();
        if (mIsShowingPunctuation) {
            return candidates;
        }

        if (isIsolateOrInitial()) {
            PopupKeyCandidate isolate_U_FVS1 = new PopupKeyCandidate(
                    "" + MongolCode.Uni.TODO_U + MongolCode.Uni.FVS1);
            candidates.add(isolate_U_FVS1);
        } else {
            PopupKeyCandidate medial_U_FVS1 = new PopupKeyCandidate(
                    "" + MongolCode.Uni.TODO_U + MongolCode.Uni.FVS1,
                    "" + MongolCode.Uni.ZWJ + MongolCode.Uni.TODO_U + MongolCode.Uni.FVS1 + MongolCode.Uni.ZWJ,
                    "" + MongolCode.Uni.TODO_U + MongolCode.Uni.FVS1 + MongolCode.Uni.ZWJ);
            PopupKeyCandidate medial_U_FVS2 = new PopupKeyCandidate(
                    "" + MongolCode.Uni.TODO_U + MongolCode.Uni.FVS2,
                    "" + MongolCode.Uni.ZWJ + MongolCode.Uni.TODO_U + MongolCode.Uni.FVS2 + MongolCode.Uni.ZWJ,
                    "" + MongolCode.Uni.TODO_U + MongolCode.Uni.FVS2 + MongolCode.Uni.ZWJ);
            PopupKeyCandidate final_U_FVS1 = new PopupKeyCandidate(
                    "" + MongolCode.Uni.TODO_U + MongolCode.Uni.FVS1,
                    "" + MongolCode.Uni.ZWJ + MongolCode.Uni.TODO_U + MongolCode.Uni.FVS1);
            candidates.add(medial_U_FVS1);
            candidates.add(medial_U_FVS2);
            candidates.add(final_U_FVS1);
        }

        return candidates;
    }

    private List<PopupKeyCandidate> getCandidatesForB() {
        List<PopupKeyCandidate> candidates = new ArrayList<>();
        if (mIsShowingPunctuation) {
            candidates.add(new PopupKeyCandidate(KEY_B_PUNCT_SUB));
            return candidates;
        }

        return candidates;
    }

    private List<PopupKeyCandidate> getCandidatesForN() {
        List<PopupKeyCandidate> candidates = new ArrayList<>();
        if (mIsShowingPunctuation) {
            candidates.add(new PopupKeyCandidate(
                    "" + MongolCode.Uni.VERTICAL_COMMA));
            return candidates;
        }

        PopupKeyCandidate nia = new PopupKeyCandidate(
                "" + MongolCode.Uni.TODO_NIA,
                "" + MongolCode.Uni.TODO_NIA);
        candidates.add(nia);
        return candidates;
    }

    private List<PopupKeyCandidate> getCandidatesForM() {
        List<PopupKeyCandidate> candidates = new ArrayList<>();
        if (mIsShowingPunctuation) {
            return candidates;
        }
        return candidates;
    }

    private List<PopupKeyCandidate> getCandidatesForLongVowel() {
        List<PopupKeyCandidate> candidates = new ArrayList<>();
        if (mIsShowingPunctuation) {
            candidates.add(new PopupKeyCandidate(KEY_ZWJ_PUNCT_SUB));
            return candidates;
        }

        PopupKeyCandidate nirugu = new PopupKeyCandidate(
                "" + MongolCode.Uni.MONGOLIAN_NIRUGU);
        candidates.add(nirugu);

        return candidates;
    }

    private List<PopupKeyCandidate> getCandidatesForQuote() {
        List<PopupKeyCandidate> candidates = new ArrayList<>();
        candidates.add(new PopupKeyCandidate(MongolCode.Uni.VERTICAL_RIGHT_WHITE_CORNER_BRACKET));
        return candidates;
    }

    private List<PopupKeyCandidate> getCandidatesForComma() {
        List<PopupKeyCandidate> candidates = new ArrayList<>();
        candidates.add(new PopupKeyCandidate(MongolCode.Uni.VERTICAL_IDEOGRAPHIC_COMMA));
        candidates.add(new PopupKeyCandidate(MongolCode.Uni.VERTICAL_SEMICOLON));
        candidates.add(new PopupKeyCandidate(MongolCode.Uni.MONGOLIAN_COLON));
        candidates.add(new PopupKeyCandidate(MongolCode.Uni.VERTICAL_COLON));
        return candidates;
    }

    private List<PopupKeyCandidate> getCandidatesForSpace() {
        List<PopupKeyCandidate> candidates = new ArrayList<>();
        PopupKeyCandidate nnbs = new PopupKeyCandidate(
                "" + MongolCode.Uni.NNBS,
                KEY_SPACE_SUB_DISPLAY,
                " ");
        candidates.add(nnbs);
        return candidates;
    }

    private List<PopupKeyCandidate> getCandidatesForPeriod() {
        List<PopupKeyCandidate> candidates = new ArrayList<>();
        candidates.add(new PopupKeyCandidate(MongolCode.Uni.MIDDLE_DOT));
        return candidates;
    }

    private List<PopupKeyCandidate> getCandidatesForQuestion() {
        List<PopupKeyCandidate> candidates = new ArrayList<>();
        candidates.add(new PopupKeyCandidate(MongolCode.Uni.VERTICAL_EXCLAMATION_MARK));
        candidates.add(new PopupKeyCandidate(MongolCode.Uni.DOUBLE_EXCLAMATION_MARK));
        candidates.add(new PopupKeyCandidate(MongolCode.Uni.QUESTION_EXCLAMATION_MARK));
        candidates.add(new PopupKeyCandidate(MongolCode.Uni.EXCLAMATION_QUESTION_MARK));
        return candidates;
    }

    @Override
    public String getDisplayName() {
        if (mDisplayName == null)
            return DEFAULT_DISPLAY_NAME;
        return mDisplayName;
    }

    @Override
    public void onKeyboardKeyClick() {
        mIsShowingPunctuation = !mIsShowingPunctuation;
        if (mIsShowingPunctuation) {
            setPuncuationKeyValues();
        } else {
            setKeyValues();
        }
    }

    @Override
    public void onNewKeyboardChosen(int xPosition) {
        super.onNewKeyboardChosen(xPosition);
    }
}
