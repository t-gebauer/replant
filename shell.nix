{ pkgs ? import <nixpkgs> {} }:

with pkgs;
let
  tree-sitter-kotlin = fetchFromGitHub {
    owner = "fwcd";
    repo = "tree-sitter-kotlin";
    rev = "0.2.3";
    sha256 = "03nz3nbda6dickqgfkqm2337sjlr1akwyi5j6sihny0yvm43m5p5";
  };
in
mkShell {
  buildInputs = [
    nodejs
    python3
    kotlin
  ];
  LIB_KOTLIN_TREE_SITTER = tree-sitter-kotlin;
}
