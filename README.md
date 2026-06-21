# ByeolPass

ByeolPass is a Java password security tool that provides password generation and password auditing through both a CLI and a JavaFX GUI.

## Features

* Generate one or multiple passwords
* Custom password length
* Character restrictions
* Raw output mode
* File export
* Password strength audit
* Weak pattern detection
* Suggestions for improvement

## Requirements

* Java 17+
* OpenJFX

## Installation

```bash
git clone https://github.com/Sebabacou/ByeolPass.git
cd ByeolPass
sudo apt update
sudo apt install openjdk-17-jdk openjfx
mkdir -p bin
```

## Compile

### CLI only

```bash
javac -d bin src/Main.java src/cli/*.java src/command/*.java src/exception/*.java src/password/*.java
```

### CLI + GUI

```bash
javac --module-path /usr/share/openjfx/lib --add-modules javafx.controls,javafx.graphics -d bin src/Main.java src/cli/*.java src/command/*.java src/exception/*.java src/password/*.java src/gui/*.java
```

## Run

### CLI

```bash
java -cp bin Main help
```

### GUI

```bash
java --module-path /usr/share/openjfx/lib --add-modules javafx.controls,javafx.graphics -cp bin gui.GuiMain
```

## Examples

```bash
java -cp bin Main generate
java -cp bin Main generate --len:16 --number:3
java -cp bin Main generate --raw
java -cp bin Main audit Password123!
java -cp bin Main audit --file:passwords.txt
```
