{ pkgs ? import <nixpkgs> {} }:
with pkgs;

mkShell {
  buildInputs = [
    nodejs
    python3
    nodePackages.node-gyp
    kotlin # Validate (compile) kotlin code
    inotify-tools # React to file changes
    clojure-lsp
  ];
}
