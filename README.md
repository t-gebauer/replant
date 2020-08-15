# replant
Kotlin to TypeScript data-structure transpiler

Uses [tree-sitter](https://github.com/tree-sitter/tree-sitter) via [Node.js bindings](https://github.com/tree-sitter/node-tree-sitter) with [Kotlin grammar](https://github.com/fwcd/tree-sitter-kotlin)

## Status

Prototype - proof of concept

## Goals

- Simplicity (usage of the tree-sitter AST is even easier than using regular expressions?)
- Resistant to syntax changes
- Easy to adapt to different source (and output?) languages

## Example

Kotlin input:
```kotlin
data class Apple(
    var id: Long?,
    val color: String,
    val size: Int
)
```
Generated TypeScript output:
```typescript
export class Apple {
  id: number | null
  color: string
  size: number
}
```

## Comparison to alternative implementations

### Regular Expressions

Pros:
- Simple

Cons:
- Prone to unforseen syntax changes

### Annotation Processor

Pros:
- Integrated into compilation via [kapt](https://kotlinlang.org/docs/reference/kapt.html), no extra setup (but not with IntelliJ's build system)

Cons:
- Integrated into compilaton, can't be run on it's own
- `kapt` the Kotlin annotation processing tool is quite slow
- The Java annotation processing API is not easy to use with Kotlin sources

