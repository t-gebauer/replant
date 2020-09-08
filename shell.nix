{ pkgs ? import <nixpkgs> {} }:
with pkgs;

mkShell {
  buildInputs = [
    nodejs
    python3
    nodePackages.node-gyp
    kotlin # Validate (compile) kotlin code
  ];
}
