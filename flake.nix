{
  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-25.11";
  };

  outputs = { nixpkgs, ... }: let
    forAllSystems = function:
      nixpkgs.lib.genAttrs [
        "x86_64-linux"
        "aarch64-linux"
        "x86_64-darwin"
        "aarch64-darwin"
      ] (system:
        function (import nixpkgs {
          inherit system;
        }));
  in {
    devShells = forAllSystems (pkgs: {
      jdk25 = pkgs.mkShellNoCC {
        buildInputs = with pkgs; [
          jdk25_headless
        ];
      };
    });
  };
}
