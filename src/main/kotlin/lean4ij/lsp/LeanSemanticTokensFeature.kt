package lean4ij.lsp

import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.PsiFile
import com.redhat.devtools.lsp4ij.client.features.LSPSemanticTokensFeature
import lean4ij.setting.Lean4Settings

/**
 * Semantic tokens feature for Lean language server.
 * This provides accurate semantic highlighting based on the language server's analysis,
 * replacing heuristic-based approaches with compiler-accurate information.
 *
 * Lean's language server provides semantic tokens for various token types like:
 * - namespace, type, class, enum, interface, struct, typeParameter
 * - parameter, variable, property, enumMember, event
 * - function, method, macro, keyword, modifier, comment
 * - string, number, regexp, operator
 *
 * ref: https://github.com/leanprover/lean4/blob/master/src/Lean/Server/FileWorker/RequestHandling.lean
 */
class LeanSemanticTokensFeature : LSPSemanticTokensFeature() {
    private val lean4Settings = service<Lean4Settings>()

    private val logger = Logger.getInstance(this::class.java)

    override fun isEnabled(file: PsiFile): Boolean {
        // Enable semantic tokens for all Lean files if the setting is enabled
        return lean4Settings.enableSemanticHighlighting
    }

    /**
     * Maps LSP semantic token types to IntelliJ text attributes.
     * This determines how each semantic token type will be highlighted in the editor.
     */
    override fun getTextAttributesKey(tokenType: String, tokenModifiers: List<String>, file: PsiFile): TextAttributesKey? {

        // Check modifiers first for more specific highlighting
        if (tokenModifiers.contains("definition") || tokenModifiers.contains("declaration")) {
            return when (tokenType) {
                "function", "method" -> DefaultLanguageHighlighterColors.FUNCTION_DECLARATION
                "class", "struct", "type" -> DefaultLanguageHighlighterColors.CLASS_NAME
                "variable", "parameter" -> DefaultLanguageHighlighterColors.LOCAL_VARIABLE
                else -> {
                    // Log the unknown token type for future reference and return null
                    logger.error("Unknown semantic token type: $tokenType with modifiers: $tokenModifiers")
                    null
                }
            }
        }

        // Map semantic token types to appropriate IntelliJ text attributes
        return when (tokenType) {
            "namespace" -> DefaultLanguageHighlighterColors.CLASS_REFERENCE
            "type", "class", "struct", "enum", "interface" -> DefaultLanguageHighlighterColors.CLASS_REFERENCE
            "typeParameter" -> DefaultLanguageHighlighterColors.PARAMETER

            "parameter" -> DefaultLanguageHighlighterColors.PARAMETER
            "variable" -> DefaultLanguageHighlighterColors.LOCAL_VARIABLE
            "property" -> DefaultLanguageHighlighterColors.INSTANCE_FIELD
            "enumMember" -> DefaultLanguageHighlighterColors.STATIC_FIELD

            "function" -> DefaultLanguageHighlighterColors.FUNCTION_CALL
            "method" -> DefaultLanguageHighlighterColors.FUNCTION_CALL
            "macro" -> DefaultLanguageHighlighterColors.METADATA

            "keyword" -> DefaultLanguageHighlighterColors.KEYWORD
            "modifier" -> DefaultLanguageHighlighterColors.KEYWORD
            "comment" -> DefaultLanguageHighlighterColors.LINE_COMMENT
            "string" -> DefaultLanguageHighlighterColors.STRING
            "number" -> DefaultLanguageHighlighterColors.NUMBER
            "operator" -> DefaultLanguageHighlighterColors.OPERATION_SIGN

            else -> {
                // Log the unknown token type for future reference and return null
                logger.error("Unknown semantic token type: $tokenType with modifiers: $tokenModifiers")
                null
            }
        }
    }
}

