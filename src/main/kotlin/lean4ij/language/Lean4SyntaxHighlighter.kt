package lean4ij.language

import com.google.common.io.Resources
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.lexer.Lexer
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors.BAD_CHARACTER
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.elementType
import lean4ij.setting.Lean4Settings
import lean4ij.language.psi.TokenType
import lean4ij.language.psi.TokenType.WHITE_SPACE
import java.nio.charset.StandardCharsets


/**
 * TODO use customized textAttributes
 */
class Lean4SyntaxHighlighter : SyntaxHighlighterBase() {
    // Text Attributes
    val KEYWORD_ATTR: TextAttributesKey = createTextAttributesKey("LEAN_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
    val KEYWORD_MODIFIER_ATTR: TextAttributesKey = createTextAttributesKey("LEAN_KEYWORD_MODIFIER", DefaultLanguageHighlighterColors.KEYWORD)
    val KEYWORD_IN_PROOF_ATTR: TextAttributesKey = createTextAttributesKey("LEAN_KEYWORD_IN_PROOF", DefaultLanguageHighlighterColors.KEYWORD)
    val COMMENT_ATTR: TextAttributesKey = createTextAttributesKey("LEAN_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
    val BLOCK_COMMENT_ATTR: TextAttributesKey = createTextAttributesKey("LEAN_BLOCK_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT)
    val DOC_COMMENT_ATTR: TextAttributesKey = createTextAttributesKey("LEAN_DOC_COMMENT", DefaultLanguageHighlighterColors.DOC_COMMENT)
    val STRING_ATTR: TextAttributesKey = createTextAttributesKey("LEAN_STRING", DefaultLanguageHighlighterColors.STRING)
    val NUMBER_ATTR: TextAttributesKey = createTextAttributesKey("LEAN_NUMBER", DefaultLanguageHighlighterColors.NUMBER)
    val IDENTIFIER_ATTR: TextAttributesKey = createTextAttributesKey("LEAN_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER)
    val OPERATOR_ATTR: TextAttributesKey = createTextAttributesKey("LEAN_OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN)
    val PARENTHESES_ATTR: TextAttributesKey = createTextAttributesKey("LEAN_PARENTHESES", DefaultLanguageHighlighterColors.PARENTHESES)
    val BRACKETS_ATTR: TextAttributesKey = createTextAttributesKey("LEAN_BRACKETS", DefaultLanguageHighlighterColors.BRACKETS)
    val BRACES_ATTR: TextAttributesKey = createTextAttributesKey("LEAN_BRACES", DefaultLanguageHighlighterColors.BRACES)
    val COMMA_ATTR: TextAttributesKey = createTextAttributesKey("LEAN_COMMA", DefaultLanguageHighlighterColors.COMMA)
    val DOT_ATTR: TextAttributesKey = createTextAttributesKey("LEAN_DOT", DefaultLanguageHighlighterColors.DOT)
    val SEMICOLON_ATTR: TextAttributesKey = createTextAttributesKey("LEAN_SEMICOLON", DefaultLanguageHighlighterColors.SEMICOLON)
    val SORRY_ATTR: TextAttributesKey = createTextAttributesKey("LEAN_SORRY", DefaultLanguageHighlighterColors.INVALID_STRING_ESCAPE)
    val TYPE_ATTR: TextAttributesKey = createTextAttributesKey("LEAN_TYPE", DefaultLanguageHighlighterColors.CLASS_NAME)

    // Key Arrays
    val BAD_CHAR_KEYS: Array<TextAttributesKey> = arrayOf(BAD_CHARACTER)
    val KEYWORD_KEYS: Array<TextAttributesKey> = arrayOf(KEYWORD_ATTR)
    val KEYWORD_MODIFIER_KEYS: Array<TextAttributesKey> = arrayOf(KEYWORD_MODIFIER_ATTR)
    val KEYWORD_IN_PROOF_KEYS: Array<TextAttributesKey> = arrayOf(KEYWORD_IN_PROOF_ATTR)
    val COMMENT_KEYS: Array<TextAttributesKey> = arrayOf(COMMENT_ATTR)
    val BLOCK_COMMENT_KEYS: Array<TextAttributesKey> = arrayOf(BLOCK_COMMENT_ATTR)
    val DOC_COMMENT_KEYS: Array<TextAttributesKey> = arrayOf(DOC_COMMENT_ATTR)
    val STRING_KEYS: Array<TextAttributesKey> = arrayOf(STRING_ATTR)
    val NUMBER_KEYS: Array<TextAttributesKey> = arrayOf(NUMBER_ATTR)
    val IDENTIFIER_KEYS: Array<TextAttributesKey> = arrayOf(IDENTIFIER_ATTR)
    val OPERATOR_KEYS: Array<TextAttributesKey> = arrayOf(OPERATOR_ATTR)
    val PARENTHESES_KEYS: Array<TextAttributesKey> = arrayOf(PARENTHESES_ATTR)
    val BRACKETS_KEYS: Array<TextAttributesKey> = arrayOf(BRACKETS_ATTR)
    val BRACES_KEYS: Array<TextAttributesKey> = arrayOf(BRACES_ATTR)
    val COMMA_KEYS: Array<TextAttributesKey> = arrayOf(COMMA_ATTR)
    val DOT_KEYS: Array<TextAttributesKey> = arrayOf(DOT_ATTR)
    val SEMICOLON_KEYS: Array<TextAttributesKey> = arrayOf(SEMICOLON_ATTR)
    val SORRY_KEYS: Array<TextAttributesKey> = arrayOf(SORRY_ATTR)
    val TYPE_KEYS: Array<TextAttributesKey> = arrayOf(TYPE_ATTR)


    override fun getHighlightingLexer(): Lexer {
        return Lean4LexerAdapter()
    }

    override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> {
        if (tokenType == null) {
            return emptyArray()
        }
        
        return when (tokenType) {
            // Keywords
            TokenType.KEYWORD_COMMAND1,
            TokenType.KEYWORD_COMMAND2,
            TokenType.KEYWORD_COMMAND3,
            TokenType.KEYWORD_COMMAND4,
            TokenType.KEYWORD_COMMAND5,
            TokenType.KEYWORD_COMMAND_PREFIX -> KEYWORD_KEYS
            
            TokenType.KEYWORD_MODIFIER -> KEYWORD_MODIFIER_KEYS
            TokenType.KEYWORD_COMMAND6 -> KEYWORD_IN_PROOF_KEYS
            TokenType.KEYWORD_SORRY -> SORRY_KEYS
            
            // Types
            TokenType.DEFAULT_TYPE -> TYPE_KEYS
            
            // Comments
            TokenType.LINE_COMMENT -> COMMENT_KEYS
            TokenType.BLOCK_COMMENT -> BLOCK_COMMENT_KEYS
            TokenType.DOC_COMMENT -> DOC_COMMENT_KEYS
            
            // Strings
            TokenType.STRING -> STRING_KEYS
            
            // Numbers
            TokenType.NUMBER,
            TokenType.NEGATIVE_NUMBER -> NUMBER_KEYS
            
            // Identifiers
            TokenType.IDENTIFIER -> IDENTIFIER_KEYS
            
            // Operators and Symbols
            TokenType.ASSIGN,
            TokenType.EQUAL,
            TokenType.COLON,
            TokenType.AT,
            TokenType.STAR,
            TokenType.FOR_ALL,
            TokenType.MISC_COMPARISON_SYM,
            TokenType.MISC_PLUS_SYM,
            TokenType.MISC_MULTIPLY_SYM,
            TokenType.MISC_EXPONENT_SYM,
            TokenType.MISC_ARROW_SYM -> OPERATOR_KEYS
            
            // Parentheses
            TokenType.LEFT_PAREN,
            TokenType.RIGHT_PAREN -> PARENTHESES_KEYS
            
            // Brackets
            TokenType.LEFT_BRACKET,
            TokenType.RIGHT_BRACKET,
            TokenType.LEFT_UNI_BRACKET,
            TokenType.RIGHT_UNI_BRACKET -> BRACKETS_KEYS
            
            // Braces
            TokenType.LEFT_BRACE,
            TokenType.RIGHT_BRACE -> BRACES_KEYS
            
            // Punctuation
            TokenType.COMMA -> COMMA_KEYS
            TokenType.DOT -> DOT_KEYS
            
            // Attributes
            TokenType.ATTRIBUTE_START,
            TokenType.ATTRIBUTE -> KEYWORD_KEYS
            
            // Placeholder
            TokenType.PLACEHOLDER -> IDENTIFIER_KEYS
            
            // Template
            TokenType.TEMPLATE_TRIGGER -> KEYWORD_KEYS
            
            // Other
            TokenType.OTHER -> IDENTIFIER_KEYS
            
            else -> emptyArray()
        }
    }
}

class Lean4SyntaxHighlighterFactory : SyntaxHighlighterFactory() {
    override fun getSyntaxHighlighter(project: Project?, virtualFile: VirtualFile?): SyntaxHighlighter {
        return Lean4SyntaxHighlighter()
    }
}

/**
 * ref: https://plugins.jetbrains.com/docs/intellij/syntax-highlighting-and-error-highlighting.html
 * TODO use customized text attributes
 */
class Lean4Annotator : Annotator {
    private val lean4Settings = service<Lean4Settings>()

    companion object {
        val tactics = getAllTactics()

        private fun getAllTactics(): Map<String, String> {
            val tactics = mutableMapOf<String, String>()
            val resource = javaClass.classLoader.getResource("tactics.txt")?:return emptyMap()
            for (line in Resources.readLines(resource, StandardCharsets.UTF_8)) {
                if (line.startsWith("--")) {
                    continue
                }
                val (key, value) = line.split(" ")
                tactics[key] = value
            }
            return tactics.toMap()
        }
    }

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element.parent is Lean4Definition) {
            if (!lean4Settings.enableHeuristicDefinition) return
            if (element.node.elementType == TokenType.IDENTIFIER || element.node.elementType == TokenType.DOT) {
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(element.textRange).textAttributes(DefaultLanguageHighlighterColors.FUNCTION_DECLARATION).create();
            }
        } else if (element.parent is Lean4Attributes) {
            if (!lean4Settings.enableHeuristicAttributes) return
            // check the parent rather than the element itself for skipping comments
            if (element.node.elementType == TokenType.IDENTIFIER) {
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(element.textRange).textAttributes(DefaultLanguageHighlighterColors.METADATA).create();
            }
            if (element.node.elementType == TokenType.ATTRIBUTE) {
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(element.textRange).textAttributes(DefaultLanguageHighlighterColors.KEYWORD).create();
            }
        } else if (element.node.elementType == TokenType.IDENTIFIER) {
            if (isField(element)) {
                if (!lean4Settings.enableHeuristicField) return
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(element.textRange).textAttributes(DefaultLanguageHighlighterColors.INSTANCE_FIELD).create();
            } else if (startsWithUppercase(element.text)) {
                if (!lean4Settings.enableHeuristicType) return
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(element.textRange).textAttributes(DefaultLanguageHighlighterColors.CLASS_NAME).create();
            } else if (isAllSymbols(element.text)) {
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(element.textRange).textAttributes(DefaultLanguageHighlighterColors.OPERATION_SIGN).create();
            } else {
                if (!lean4Settings.enableHeuristicTactic) return
                if (tactics.containsKey(element.text)) {
                    holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                        .range(element.textRange).textAttributes(DefaultLanguageHighlighterColors.FUNCTION_CALL).create();
                }
            }
        }
    }

    private fun startsWithUppercase(text: String): Boolean {
        if (text.isEmpty()) return false
        val firstChar = text.first()
        // Check for uppercase English letters (A-Z)
        if (firstChar in 'A'..'Z') return true
        // Check for uppercase Greek letters
        // Greek uppercase: Α-Ω (U+0391 to U+03A9)
        if (firstChar in '\u0391'..'\u03A9') return true
        return false
    }

    private fun isAllSymbols(text: String): Boolean {
        if (text.isEmpty()) return false

        // Define symbol sets based on Lean4 token types
        val comparisonSymbols = setOf(
            '∉','∋','∌','⊆','⊈','⊂','⊄','⊊','∝','∊','∍','∥','∦','∷','∺','∻','∽','∾','≁','≃','≄','≅','≆','≇','≈','≉','≊','≋','≌','≍','≎','≐','≑','≒','≓','≔','≕','≖','≗','≘','≙','≚','≛','≜','≝','≞','≟','≣','≦','≧','≨','≩','≪','≫','≬','≭','≮','≯','≰','≱','≲','≳','≴','≵','≶','≷','≸','≹','≺','≻','≼','≽','≾','≿','⊀','⊁','⊃','⊅','⊇','⊉','⊋','⊏','⊐','⊑','⊒','⊜','⊩','⊬','⊮','⊰','⊱','⊲','⊳','⊴','⊵','⊶','⊷','⋍','⋐','⋑','⋕','⋖','⋗','⋘','⋙','⋚','⋛','⋜','⋝','⋞','⋟','⋠','⋡','⋢','⋣','⋤','⋥','⋦','⋧','⋨','⋩','⋪','⋫','⋬','⋭','⋲','⋳','⋴','⋵','⋶','⋷','⋸','⋹','⋺','⋻','⋼','⋽','⋾','⋿','⟈','⟉','⟒','⦷','⧀','⧁','⧡','⧣','⧤','⧥','⩦','⩧','⩪','⩫','⩬','⩭','⩮','⩯','⩰','⩱','⩲','⩳','⩴','⩵','⩶','⩷','⩸','⩹','⩺','⩻','⩼','⩽','⩾','⩿','⪀','⪁','⪂','⪃','⪄','⪅','⪆','⪇','⪈','⪉','⪊','⪋','⪌','⪍','⪎','⪏','⪐','⪑','⪒','⪓','⪔','⪕','⪖','⪗','⪘','⪙','⪚','⪛','⪜','⪝','⪞','⪟','⪠','⪡','⪢','⪣','⪤','⪥','⪦','⪧','⪨','⪩','⪪','⪫','⪬','⪭','⪮','⪯','⪰','⪱','⪲','⪳','⪴','⪵','⪶','⪷','⪸','⪹','⪺','⪻','⪼','⪽','⪾','⪿','⫀','⫁','⫂','⫃','⫄','⫅','⫆','⫇','⫈','⫉','⫊','⫋','⫌','⫍','⫎','⫏','⫐','⫑','⫒','⫓','⫔','⫕','⫖','⫗','⫘','⫙','⫷','⫸','⫹','⫺','⊢','⊣','⟂'
        )
        val plusSymbols = setOf(
            '⊕','⊖','⊞','⊟','∪','∨','⊔','±','∓','∔','∸','≂','≏','⊎','⊽','⋎','⋓','⧺','⧻','⨈','⨢','⨣','⨤','⨥','⨦','⨧','⨨','⨩','⨪','⨫','⨬','⨭','⨮','⨹','⨺','⩁','⩂','⩅','⩊','⩌','⩏','⩐','⩒','⩔','⩖','⩗','⩛','⩝','⩡','⩢','⩣'
        )
        val multiplySymbols = setOf(
            '∘','∩','∧','⊗','⊘','⊙','⊚','⊛','⊠','⊡','⊓','∗','∙','∤','⅋','≀','⊼','⋄','⋆','⋇','⋉','⋊','⋋','⋌','⋏','⋒','⟑','⦸','⦼','⦾','⦿','⧶','⧷','⨇','⨰','⨱','⨲','⨳','⨴','⨵','⨶','⨷','⨸','⨻','⨼','⨽','⩀','⩃','⩄','⩋','⩍','⩎','⩑','⩓','⩕','⩘','⩚','⩜','⩞','⩟','⩠','⫛','⊍','▷','⨝','⟕','⟖','⟗'
        )
        val exponentSymbols = setOf(
            '↑','↓','⇵','⟰','⟱','⤈','⤉','⤊','⤋','⤒','⤓','⥉','⥌','⥍','⥏','⥑','⥔','⥕','⥘','⥙','⥜','⥝','⥠','⥡','⥣','⥥','⥮','⥯','￪','￬'
        )
        val arrowSymbols = setOf(
            '←','→','↔','↚','↛','↞','↠','↢','↣','↦','↤','↮','⇎','⇍','⇏','⇐','⇒','⇔','⇴','⇶','⇷','⇸','⇹','⇺','⇻','⇼','⇽','⇾','⇿','⟵','⟶','⟷','⟹','⟺','⟻','⟼','⟽','⟾','⟿','⤀','⤁','⤂','⤃','⤄','⤅','⤆','⤇','⤌','⤍','⤎','⤏','⤐','⤑','⤔','⤕','⤖','⤗','⤘','⤝','⤞','⤟','⤠','⥄','⥅','⥆','⥇','⥈','⥊','⥋','⥎','⥐','⥒','⥓','⥖','⥗','⥚','⥛','⥞','⥟','⥢','⥤','⥦','⥧','⥨','⥩','⥪','⥫','⥬','⥭','⥰','⧴','⬱','⬰','⬲','⬳','⬴','⬵','⬶','⬷','⬸','⬹','⬺','⬻','⬼','⬽','⬾','⬿','⭀','⭁','⭂','⭃','⭄','⭇','⭈','⭉','⭊','⭋','⭌','￩','￫','⇜','⇝','↜','↝','↩','↪','↫','↬','↼','↽','⇀','⇁','⇄','⇆','⇇','⇉','⇋','⇌','⇚','⇛','⇠','⇢'
        )

        // Common ASCII symbols
        val asciiSymbols = setOf(
            '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '-', '+', '=',
            '[', ']', '{', '}', '\\', '|', ';', ':', '\'', '"', '<', '>',
            ',', '.', '/', '?', '~', '`'
        )

        // Check if all characters are symbols
        return text.all { char ->
            char in comparisonSymbols ||
            char in plusSymbols ||
            char in multiplySymbols ||
            char in exponentSymbols ||
            char in arrowSymbols ||
            char in asciiSymbols
        }
    }

    private fun isField(element: PsiElement): Boolean {
        // quite loose check
        return prevSiblingIsNewLine(element) /*&& nextSiblingIsAssign(element)*/
    }

    private fun prevSiblingIsNewLine(element: PsiElement): Boolean {
        val prevElement = element.prevSibling?:return false
        return prevElement.elementType == WHITE_SPACE && prevElement.text.contains('\n')
    }

    private fun nextSiblingIsAssign(element: PsiElement): Boolean {
        var nextValidElement : PsiElement? = element.nextSibling
        while (!isValid(nextValidElement)) {
            nextValidElement = nextValidElement?.nextSibling
        }
        val elementType = nextValidElement?.node?.elementType
        return elementType == TokenType.ASSIGN || elementType == TokenType.COLON
    }

    private fun isValid(element: PsiElement?): Boolean {
        return element?.node?.elementType != WHITE_SPACE && element?.node?.elementType != TokenType.PLACEHOLDER;
    }
}
