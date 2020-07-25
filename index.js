const Parser = require('tree-sitter')
const Kotlin = require('tree-sitter-kotlin')
const fs = require('fs')
const { log } = require('console')

const parser = new Parser()
parser.setLanguage(Kotlin)


const sourceCode = fs.readFileSync("test/Apple.kt").toString()
const tree = parser.parse(sourceCode)

log("Complete tree:")
log(tree.rootNode.toString())
log()

const cursor = tree.walk()

cursor.gotoFirstChild()

function walkUntil(type) {
    let didWalk = true
    while (didWalk && cursor.currentNode.type !== type) {
        didWalk = cursor.gotoNextSibling()
    }
}

// Find class declaration
walkUntil('class_declaration')
cursor.gotoFirstChild()

// Find class name
walkUntil('type_identifier')
const className = cursor.currentNode.text
log("Class name:", className)

// Find primary constructor
walkUntil('primary_constructor')
const constructorNode = cursor.currentNode
log(constructorNode.toString())

// For each constructor parameter
cursor.gotoFirstChild()
walkUntil('class_parameter')
// TODO ...
