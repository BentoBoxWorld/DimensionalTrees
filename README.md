# 🌲 DimensionalTrees Add-on for BentoBox
[![Build Status](https://ci.codemc.org/buildStatus/icon?job=BentoBoxWorld/DimensionalTrees)](https://ci.codemc.org/job/BentoBoxWorld/job/DimensionalTrees/)

## 🔍 What is DimensionalTrees?

**DimensionalTrees** is a BentoBox add-on that makes trees grown in the **Nether** and **End** dimensions look like they belong there. Instead of regular wood and leaves, trees that grow from saplings in these dimensions are built from blocks native to the dimension — crimson and glowstone in the Nether, purpur and end stone in the End.

Configurable per dimension, fully customizable block types, and works with any BentoBox game mode.

---

## 🚀 Getting Started

1. Place the **DimensionalTrees** `.jar` into your BentoBox `addons` folder.
2. Restart your server.
3. The addon will create an `addons/DimensionalTrees/config.yml` file.
4. Edit `config.yml` to set the block types and tree species you want to transform.
5. Restart the server to apply your changes (or use `/dtrees reload`).

---

## ⚙️ Configuration

The `config.yml` controls which blocks are used in each dimension and which tree types are affected.

### Block Replacements

```yaml
dimensionaltrees:
  blocks:
    end:
      logs: purpur_block     # Replaces log blocks in the End
      leaves: end_stone      # Replaces leaf blocks in the End
    nether:
      logs: gravel           # Replaces log blocks in the Nether
      leaves: glowstone      # Replaces leaf blocks in the Nether
```

Any valid Minecraft material name can be used. If an invalid material is set, the tree will not grow and an error is logged.

### Tree Types

Only the saplings listed here will be transformed. Comment out any types you want to leave as normal trees.

```yaml
    tree_types:
      - oak
      #- spruce
      - acacia
      #- dark_oak
      #- jungle
      - birch
```

### Options

```yaml
    options:
      enable_addon: true    # Master switch for the addon
      end_trees: true       # Enable/disable End dimension tree replacement
      nether_trees: true    # Enable/disable Nether dimension tree replacement
      sendlog: true         # Log debug messages (e.g. invalid block warnings)
```

---

## 🛡️ Permissions

```
dimensionaltrees.admin.*          # Access to all DimensionalTrees admin commands
```

---

## 💬 Commands

The admin command is available as `/dtrees`, `/dimensionaltrees`, or `/dt`.

| Command          | Description                                            |
|------------------|--------------------------------------------------------|
| `/dtrees reload` | Reloads the `config.yml` without restarting the server |

---

## 🐛 Bugs and Feature Requests

Please submit issues at [GitHub Issues](https://github.com/BentoBoxWorld/DimensionalTrees/issues) or ask in the BentoBox Discord.
