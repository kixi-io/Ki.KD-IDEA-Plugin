# Ki.KD-IDEA-Plugin

IntelliJ IDEA plugin for Ki Data (KD) files.

## Features

- **File Type Recognition**: `.kd` files are recognized with a custom icon
- **Language Support**: KD registered as a language for future syntax highlighting and editing features

## Installation

### From JetBrains Marketplace (coming soon)

1. Open IntelliJ IDEA
2. Go to **Settings/Preferences** → **Plugins** → **Marketplace**
3. Search for "Ki Data"
4. Click **Install**

### From Disk

1. Download the plugin `.zip` file from [Releases](https://github.com/kixi-io/Ki.KD-IDEA-Plugin/releases)
2. Open IntelliJ IDEA
3. Go to **Settings/Preferences** → **Plugins** → ⚙️ → **Install Plugin from Disk...**
4. Select the downloaded `.zip` file

## Building from Source

```bash
./gradlew build
```

The plugin will be available at `build/distributions/Ki.KD-IDEA-Plugin-<version>.zip`

## Running/Debugging

To run a sandboxed IDE instance with the plugin installed:

```bash
./gradlew runIde
```

## About Ki Data (KD)

KD is a modern, readable document format for structured data. It features:

- **Rich Type System**: strings, numbers, dates, durations, versions, geo-coordinates, quantities with units, and more
- **Hierarchical Structure**: tags with values, attributes, annotations, and children
- **Unicode & Emoji Support**: full Unicode support including emoji in identifiers and values

### Example KD Document

```kd
# Configuration example
@version(1.0.0)
config {
    database {
        host = "localhost"
        port = 5432
        ssl = true
        timeout = 30s
    }

    server {
        name = "MyApp"
        location = .geo(37.7749, -122.4194)
        version = 2.1.0-beta
    }
}

# Recipe with quantities
recipe "Pasta" servings=4 {
    pasta 500g
    tomatoes 400g
    garlic 2
    olive_oil 30mℓ
    cooking_time 20min
}
```

Learn more at [Ki.Docs](https://github.com/kixi-io/Ki.Docs)

## Related Projects

- [Ki.Core-JVM](https://github.com/kixi-io/Ki.Core-JVM) - Core Ki types for JVM
- [Ki.KD-JVM](https://github.com/kixi-io/Ki.KD-JVM) - KD parser for JVM
- [Ki.Docs](https://github.com/kixi-io/Ki.Docs) - Ki documentation and specification

## License

Apache License 2.0

## Author

Daniel Leuck (dan@leuck.org)
