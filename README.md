# replant
Kotlin to TypeScript data-structure transpiler

Uses [tree-sitter](https://github.com/tree-sitter/tree-sitter) via [Node.js bindings](https://github.com/tree-sitter/node-tree-sitter) with [Kotlin grammar](https://github.com/fwcd/tree-sitter-kotlin)

## Status

Prototype - proof of concept

## Goals

- The focus lies on transpiling data classes or [POJO](https://en.wikipedia.org/wiki/Plain_old_Java_object)s. It is not planned to try to convert any logic.
- Simplicity (usage of the tree-sitter AST is even easier than using regular expressions?)
- Resistant to syntax changes (compared to regular expressions)
- Easy to adapt to different source (and output?) languages

## Examples

### Data class
Kotlin input:
```kotlin
data class Apple(
    var id: Long?,
    val color: String,
    val size: Int
)
```

Generated TypeScript output:
(At least that is what is planned, we are not there yet)
```typescript
export class Apple {
  readonly id: number | null
  readonly color: string
  readonly size: number

  constructor (...)

  copy(...): Apple
}
```

### Enums

... tbd ...

## Development

This project currently uses [shadow-cljs](http://shadow-cljs.org/) to compile ClojureScript. This makes it very easy to add npm dependencies and requires nearly zero configuration.

1. (Optionally) `shadow-cljs server start` Speeds up the following commands
2. `shadow-cljs watch :test` to compile the :test target (and recompile on changes)

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

