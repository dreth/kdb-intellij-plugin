package org.kdb.studio.language;

import com.appian.intellij.k.psi.*;
import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.TokenType;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.formatter.common.AbstractBlock;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class QBlock extends AbstractBlock {

    private SpacingBuilder mySpacingBuilder;

    private CodeStyleSettings mySettings;

    private QBlock myParent;

    private Indent myIndent;

    static TokenSet LAMBDA_DEF = TokenSet.create(KTypes.OPEN_BRACE, KTypes.LAMBDA_PARAMS, KTypes.SEMICOLON);
    static TokenSet CONTROL_DEF = TokenSet.create(KTypes.CONTROL, KTypes.ARGS, KTypes.SEMICOLON);
    static TokenSet CONDITIONAL_DEF = TokenSet.create(KTypes.OPEN_BRACKET, KTypes.CLOSE_BRACKET, KTypes.SEMICOLON);
    static TokenSet SEMICOLON = TokenSet.create(KTypes.SEMICOLON);

    public QBlock(@Nullable QBlock parent, @NotNull ASTNode node, @Nullable Wrap wrap, @Nullable Alignment alignment, @Nullable Indent indent, @NotNull CodeStyleSettings settings, @NotNull SpacingBuilder parentSpacingBuilder) {
        super(node, wrap, alignment);
        myParent = parent;
        mySettings = settings;
        mySpacingBuilder = parentSpacingBuilder;
        myIndent = indent;
        if (isNodeOfType(node, KExpression.class) && isNodeOfType(node.getTreeParent(), KQSql.class)) {
            mySpacingBuilder = QFormattingModelBuilder.createQSQLExpressionSpacingBuilder(settings);
        }
        if (isNodeOfType(node, KGroupOrList.class)) {
            if (hasAnyOf(node, TokenSet.create(KTypes.EXPRESSION), n -> isNodeOfType(n.getLastChildNode(), KAssignment.class))) {
                mySpacingBuilder = QFormattingModelBuilder.createSemicolonSpacingBuilder(settings);
            } else {
                mySpacingBuilder = QFormattingModelBuilder.createDefaultSpacingBuilder(settings);
            }
        }
        if (isNodeOfType(node, KArgs.class) &&
                (isNodeOfType(myNode.getTreeParent(), KExpression.class)) &&
                (isNodeOfType(myNode.getTreeParent().getFirstChildNode(), KArgs.class))) {
            mySpacingBuilder = QFormattingModelBuilder.createConditionalSpacingBuilder(settings);
        }
        if (isNodeOfType(node, KAssignment.class) && isNodeOfType(node.getLastChildNode(), KExpression.class))
            if (isNodeOfType(node.getLastChildNode().getFirstChildNode(), KLambda.class)) {
                mySpacingBuilder = QFormattingModelBuilder.createFunctionalSpacingBuilder(settings);
            } else {
                mySpacingBuilder = QFormattingModelBuilder.createDefaultSpacingBuilder(settings);
        }

    }

    protected static boolean isNodeOfType(ASTNode node, Class<? extends PsiElement> type) {
        if (node != null && node.getPsi() != null) {
            return type.isInstance(node.getPsi());
        }
        return false;
    }

    protected static boolean isNodeOfElementType(ASTNode node, IElementType type) {
        if (node != null && node.getElementType() != null) {
            return type == node.getElementType();
        }
        return false;
    }

    protected boolean hasAnyOf(ASTNode node, TokenSet filter, Predicate<ASTNode> nodePredicate) {
        for (ASTNode astNode: node.getChildren(filter)) {
            if (nodePredicate.test(astNode)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Indent getIndent() {
        return myIndent;
    }

    @Override
    protected List<Block> buildChildren() {

        return ContainerUtil.mapNotNull(myNode.getChildren(null), node -> {
            if (isWhitespaceOrEmpty(node)) {
                return null;
            }
            return makeSubBlock(node);
        });

    }

    private Block makeSubBlock(@NotNull ASTNode childNode) {
        Indent indent = null;
        Wrap wrap = null;
        SpacingBuilder spacingBuilder = mySpacingBuilder;
        if (isNodeOfType(myNode, KFile.class)) {
            indent = Indent.getAbsoluteNoneIndent();
        }

        if (isNodeOfType(myNode, KLambda.class)) {
            if(isNodeOfType(childNode, KLambdaParams.class)) {
                wrap = Wrap.createWrap(WrapType.NONE, false);
            } else if (isNodeOfElementType(childNode, KTypes.EXPRESSION) &&
                    (!isNodeOfElementType(getTreePrev(childNode), KTypes.OPEN_BRACE) || !isNodeOfElementType(getTreeNext(childNode), KTypes.CLOSE_BRACE))) {
                wrap = Wrap.createWrap(WrapType.ALWAYS, true);
                if (!LAMBDA_DEF.contains(childNode.getElementType())) {
                    indent = Indent.getNormalIndent(false);
                }
            }
            if (isNodeOfElementType(childNode, KTypes.CLOSE_BRACE) && myNode.getChildren(SEMICOLON).length > 0) {
                wrap = Wrap.createWrap(WrapType.ALWAYS, true);
                if (!LAMBDA_DEF.contains(childNode.getElementType())) {
                    indent = Indent.getNormalIndent(false);
                }
            }
        }
        if (isNodeOfType(myNode, KControlStatement.class)) {
            if (isNodeOfElementType(childNode, KTypes.EXPRESSION)) {
                wrap = Wrap.createWrap(WrapType.ALWAYS, true);
            }
            if (!CONTROL_DEF.contains(childNode.getElementType())) {
                indent = Indent.getNormalIndent(false);
            }
        }
        if (isNodeOfType(myNode, KArgs.class) &&
                (isNodeOfType(myNode.getTreeParent(), KControlStatement.class)
                || ((isNodeOfType(myNode.getTreeParent(), KConditionalEvaluation.class) &&
                        isNodeOfType(myNode.getTreeParent().getTreeParent(), KExpression.class) &&
                        !isNodeOfType(myNode.getTreeParent().getTreeParent().getTreeParent(), KAssignment.class))))) {
            if (isNodeOfElementType(childNode, KTypes.EXPRESSION) && isNodeOfElementType(getTreePrev(childNode), KTypes.SEMICOLON)) {
                wrap = Wrap.createWrap(WrapType.ALWAYS, true);
            }
            if (isNodeOfElementType(childNode, KTypes.CLOSE_BRACKET)) {
                wrap = Wrap.createWrap(WrapType.ALWAYS, true);
            }
            if (isNodeOfElementType(childNode, KTypes.EXPRESSION) && isNodeOfElementType(getTreePrev(childNode), KTypes.OPEN_BRACKET)) {
                wrap = Wrap.createWrap(WrapType.NONE, false);
                indent = null;
            } else if (!CONTROL_DEF.contains(childNode.getElementType())) {
                indent = Indent.getNormalIndent(false);
            }
        }
        if (isNodeOfType(myNode, KArgs.class) &&
                (isNodeOfType(myNode.getTreeParent(), KExpression.class)) &&
                (isNodeOfType(myNode.getTreeParent().getTreeParent(), KConditionalEvaluation.class))) {
            if (isNodeOfElementType(childNode, KTypes.OPEN_BRACKET) || isNodeOfElementType(childNode, KTypes.CLOSE_BRACKET)) {
                wrap = Wrap.createWrap(WrapType.ALWAYS, true);
                indent = Indent.getNoneIndent();
            }
            if (isNodeOfElementType(childNode, KTypes.EXPRESSION)) {
                wrap = Wrap.createWrap(WrapType.ALWAYS, true);
            }
            if (!CONDITIONAL_DEF.contains(childNode.getElementType())) {
                indent = Indent.getNormalIndent(false);
            }

        }
        if (isNodeOfType(myNode, KConditionalEvaluation.class) && isNodeOfType(childNode, KArgs.class)) {
            wrap = Wrap.createWrap(WrapType.NONE, false);
//            spacingBuilder = conditionalSpacingBuilder;
        }
        if (isNodeOfElementType(myNode, KTypes.NEWLINE)) {
            wrap = Wrap.createWrap(WrapType.NONE, false);
            indent = Indent.getNoneIndent();
        }
        return new QBlock(this, childNode, wrap, myAlignment, indent, mySettings, spacingBuilder);
    }

    private ASTNode getTreePrev(ASTNode node) {
        ASTNode prev = node.getTreePrev();
        while (prev != null) {
            if (prev.getElementType() == TokenType.WHITE_SPACE) {
                prev = prev.getTreePrev();
            } else {
                break;
            }
        }
        return prev;
    }

    private ASTNode getTreeNext(ASTNode node) {
        ASTNode next = node.getTreeNext();
        while (next != null) {
            if (next.getElementType() == TokenType.WHITE_SPACE) {
                next = next.getTreeNext();
            } else {
                break;
            }
        }
        return next;
    }

    private static boolean isWhitespaceOrEmpty(ASTNode node) {
        return node.getElementType() == TokenType.WHITE_SPACE  || node.getTextLength() == 0;
    }

    @Nullable
    @Override
    public Spacing getSpacing(@Nullable Block child1, @NotNull Block child2) {
        return mySpacingBuilder.getSpacing(this, child1, child2);
    }

    @Override
    public boolean isLeaf() {
        return myNode.getFirstChildNode() == null;
    }
}
