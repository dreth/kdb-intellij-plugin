package org.kdb.studio.language;

import com.appian.intellij.k.KLanguage;
import com.appian.intellij.k.psi.KExpression;
import com.appian.intellij.k.psi.KTypes;
import com.intellij.formatting.*;
import com.intellij.json.JsonLanguage;
import com.intellij.json.formatter.JsonBlock;
import com.intellij.json.formatter.JsonCodeStyleSettings;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.formatter.DocumentBasedFormattingModel;
import com.intellij.psi.formatter.common.AbstractBlock;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kdb.studio.ui.ColorAndFontManager;

import java.util.Collections;
import java.util.List;

import static com.intellij.json.JsonElementTypes.*;
import static com.intellij.json.JsonElementTypes.COMMA;

public class QFormattingModelBuilder implements FormattingModelBuilder {

    static TokenSet PARAMS_LEFT = TokenSet.create(KTypes.OPEN_BRACKET, KTypes.USER_ID, KTypes.SEMICOLON, KTypes.EXPRESSION);
    static TokenSet PARAMS_RIGHT = TokenSet.create(KTypes.CLOSE_BRACKET, KTypes.USER_ID, KTypes.SEMICOLON, KTypes.EXPRESSION);
    static TokenSet USER_ID_OR_STRING = TokenSet.create(KTypes.USER_ID, KTypes.STRING);
    static TokenSet PRIMITIVE_VERB = TokenSet.create(KTypes.PRIMITIVE_VERB);
    static TokenSet PRIMITIVE_VERB_EXPRESSION = TokenSet.create(KTypes.USER_ID, KTypes.EXPRESSION, KTypes.NUMBER);

    static TokenSet PARENTS = TokenSet.create(KTypes.OPEN_PAREN, KTypes.CLOSE_PAREN);

    private ColorAndFontManager manager = ColorAndFontManager.getInstance();

    @NotNull
    @Override
    public FormattingModel createModel(PsiElement element, CodeStyleSettings settings) {
        if (!manager.getFormattingEnabled()) {
            PsiFile file = element.getContainingFile();
            return new DocumentBasedFormattingModel(new AbstractBlock(element.getNode(), Wrap.createWrap(WrapType.NONE, false), Alignment.createAlignment()) {
                protected List<Block> buildChildren() {
                    return Collections.emptyList();
                }

                public Spacing getSpacing(Block child1, @NotNull Block child2) {
                    return Spacing.getReadOnlySpacing();
                }

                public boolean isLeaf() {
                    return true;
                }
            }, element.getProject(), settings, file.getFileType(), file);
        }
        final QBlock block = new QBlock(null, element.getNode(), null, null, Indent.getNoneIndent(), settings,createDefaultSpacingBuilder(settings));
        return FormattingModelProvider.createFormattingModelForPsiFile(element.getContainingFile(), block, settings);
    }

    @Nullable
    @Override
    public TextRange getRangeAffectingIndent(PsiFile file, int offset, ASTNode elementAtOffset) {
        return null;
    }

    static SpacingBuilder createQSQLExpressionSpacingBuilder(CodeStyleSettings settings) {
        return new SpacingBuilder(settings, KLanguage.INSTANCE)
        .before(KTypes.NEWLINE).spacing(0,0,0,false,0)
        .betweenInside(KTypes.PRIMITIVE_VERB, KTypes.USER_ID, KTypes.EXPRESSION).spacing(1, 1, 0, false, 0)
        .betweenInside(KTypes.PRIMITIVE_VERB, KTypes.EXPRESSION, KTypes.EXPRESSION).spacing(1, 1, 0, false, 0);

    }

    static SpacingBuilder createConditionalEvaluationArgsSpacingBuilder(CodeStyleSettings settings) {
        return new SpacingBuilder(settings, KLanguage.INSTANCE)
            .before(KTypes.NEWLINE).spacing(0,0,0,false,0)
            .between(PARAMS_LEFT, PARAMS_RIGHT).spacing(0, 0, 0, false, 0);
    }

    static SpacingBuilder createSemicolonSpacingBuilder(CodeStyleSettings settings) {
        return baseSpacingBuilder(settings)
                .betweenInside(KTypes.COLON, KTypes.EXPRESSION, KTypes.ASSIGNMENT).spacing(1, 1, 0, true, 0)
                .between(KTypes.EXPRESSION, KTypes.SEMICOLON).spacing(0, 0, 0, false, 0)
                .between(KTypes.SEMICOLON, KTypes.EXPRESSION).spacing(1, 1, 0, true, 0);
    }

    static SpacingBuilder baseSpacingBuilder(CodeStyleSettings settings) {
        return new SpacingBuilder(settings, KLanguage.INSTANCE)
                .before(KTypes.NEWLINE).spacing(0,0,0,false,0)
                .betweenInside(PRIMITIVE_VERB, PRIMITIVE_VERB_EXPRESSION, KTypes.EXPRESSION).spacing(0, 0, 0, false, 0)
                .betweenInside(KTypes.EXPRESSION, KTypes.CLOSE_PAREN, KTypes.GROUP_OR_LIST).spacing(0, 0, 0, true, 0)
                .betweenInside(KTypes.CLOSE_PAREN, KTypes.EXPRESSION, KTypes.GROUP_OR_LIST).spacing(0, 0, 0, true, 0)
                .beforeInside(KTypes.COLON, KTypes.ASSIGNMENT).spacing(0, 0, 0, true, 0)
                .betweenInside(KTypes.COLON, KTypes.LAMBDA, KTypes.ASSIGNMENT).spacing(0, 0, 0, false, 0)
                .betweenInside(PARAMS_LEFT, PARAMS_RIGHT, KTypes.LAMBDA_PARAMS).spacing(0, 0, 0, false, 0)
                .between(KTypes.ARGS, KTypes.ASSIGNMENT).spacing(1, 1, 0, true, 0)
                .betweenInside(KTypes.Q_SQL_TEMPLATE, KTypes.EXPRESSION, KTypes.Q_SQL).spacing(1, 1, 0, true, 0)
                .betweenInside(KTypes.EXPRESSION, KTypes.Q_SQL_FROM, KTypes.Q_SQL).spacing(1, 1, 0, true, 0)
                .betweenInside(KTypes.Q_SQL_FROM, KTypes.EXPRESSION, KTypes.Q_SQL).spacing(1, 1, 0, true, 0)
                .between(KTypes.OPEN_BRACE, KTypes.LAMBDA_PARAMS).spacing(0, 0, 0, false, 0)
                .between(KTypes.USER_ID, USER_ID_OR_STRING).spacing(1, 1, 0, true, 0);

    }

    static SpacingBuilder createConditionalSpacingBuilder(CodeStyleSettings settings) {
        return baseSpacingBuilder(settings)
                .betweenInside(KTypes.COLON, KTypes.EXPRESSION, KTypes.ASSIGNMENT).spacing(1, 1, 0, true, 0)
                .between(KTypes.EXPRESSION, KTypes.SEMICOLON).spacing(0, 0, 0, false, 0)
                .betweenInside(KTypes.SEMICOLON, KTypes.EXPRESSION, KTypes.LAMBDA_PARAMS).spacing(0, 0, 0, false, 0)
                .between(KTypes.SEMICOLON, KTypes.EXPRESSION).spacing(0, 0, 0, true, 0);
    }

    static SpacingBuilder createFunctionalSpacingBuilder(CodeStyleSettings settings) {
        return baseSpacingBuilder(settings)
                .betweenInside(KTypes.COLON, KTypes.EXPRESSION, KTypes.ASSIGNMENT).spacing(0, 0, 0, false, 0)
                .between(KTypes.EXPRESSION, KTypes.SEMICOLON).spacing(0, 0, 0, false, 0)
                .between(KTypes.SEMICOLON, KTypes.EXPRESSION).spacing(0, 0, 0, true, 0)
                .betweenInside(KTypes.OPEN_BRACKET, KTypes.EXPRESSION, KTypes.ARGS).spacing(0, 0, 0, false, 0);
    }

    static SpacingBuilder createDefaultSpacingBuilder(CodeStyleSettings settings) {
        return createConditionalSpacingBuilder(settings)
                .betweenInside(KTypes.OPEN_BRACKET, KTypes.EXPRESSION, KTypes.ARGS).spacing(0, 0, 0, false, 0);
    }

    
}
