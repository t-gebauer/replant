const Parser = require('tree-sitter')
const Kotlin = require('tree-sitter-kotlin')
const fs = require('fs')
const { log } = require('console')

const parser = new Parser()
parser.setLanguage(Kotlin)

const sourceCode = fs.readFileSync("test/Apple.kt").toString()
const tree = parser.parse(sourceCode)

class Parameter {
    /**
     * @param {string} mutableType
     * @param {string} identifier
     * @param {string} type
     * @param {string} isNullable
     */
    constructor(mutableType, identifier, type, isNullable) {
        this.mutableType = mutableType;
        this.identifier = identifier;
        this.type = type;
        this.isNullable = isNullable;
    }
}

/**
 *  @param {string} type
 */
function mapType(type) {
    switch (type) {
        case 'Byte':
        case 'Short':
        case 'Int':
        case 'Long':
        case 'Float':
        case 'Double':
            return 'number'
        case 'String':
            return 'string'
    }
}

/**
 * @param {Parser.SyntaxNode} node
 */
function createParameterFromNode(node) {
    const firstChildNode = node.children[0]
    if (firstChildNode.type === 'val' || firstChildNode.type === 'var') {
        const mutableType = firstChildNode.text
        const identifierNode = node.children[1]
        const identifier = identifierNode.text
        let typeNode = node.children[3]
        const isNullable = typeNode.type === 'nullable_type'
        if (isNullable) {
            typeNode = typeNode.firstChild
        }
        const type = typeNode.text
        return new Parameter(mutableType, identifier, mapType(type), isNullable)
    }
}

const classNode = tree.rootNode.children.find(it => it.type === 'class_declaration')
const className = classNode.children.find(it => it.type === 'type_identifier').text
const constructorNode = classNode.children.find(it => it.type === 'primary_constructor')
const parameters = constructorNode.children
    .filter(it => it.type === 'class_parameter')
    .map(it => createParameterFromNode(it))

/**
 * @param {string} className
 * @param {Parameter[]} parameters
 */
function generateTypeScript(className, parameters) {
    return `export class ${className} {\n`
        + parameters
            .map(it => {
                const typeString = it.isNullable ? `${it.type} | null` : it.type
                return `  ${it.identifier}: ${typeString}`
            })
            .join("\n")
        + "\n}\n"
}

const tsOutput = generateTypeScript(className, parameters)
log(tsOutput)
fs.writeFileSync("out/apple.ts", tsOutput)
