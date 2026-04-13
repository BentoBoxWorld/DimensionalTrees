# 🌲 DimensionalTrees Add-on for BentoBox
[![Build Status](https://ci.codemc.org/buildStatus/icon?job=BentoBoxWorld/DimensionalTrees)](https://ci.codemc.org/job/BentoBoxWorld/job/DimensionalTrees/)

## 🔍 What is DimensionalTrees?

**DimensionalTrees** is a BentoBox add-on that makes trees grown in the **Nether** and **End** dimensions look like they belong there. Instead of regular wood and leaves, trees that grow from saplings in these dimensions are built from blocks native to the dimension — gravel and glowstone in the Nether, purpur and end stone in the End.

Fully customizable per dimension, with support for **per-tree-type overrides**, **per-gamemode overrides**, and **weighted random block mixing**. Works with any BentoBox game mode.

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

### Options

These are the top-level switches. Set them first before customizing block replacements.

```yaml
dimensionaltrees:
  options:
    enable_addon: true    # Master switch — set to false to disable the addon entirely
    end_trees: true       # Enable/disable End dimension tree replacement
    nether_trees: true    # Enable/disable Nether dimension tree replacement
    sendlog: true         # Log debug info to the console (e.g. invalid material warnings)

    # Tree types to transform. Comment out any species to leave them as normal trees.
    tree_types:
      - oak
      - spruce
      - acacia
      - dark_oak
      - jungle
      - birch
```

### Block Replacements

Each `logs` and `leaves` entry is a **map of material name → weight (%)**.

```yaml
dimensionaltrees:
  blocks:
    end:
      logs:
        purpur_block: 100    # 100% purpur block for log replacements in the End
      leaves:
        end_stone: 100       # 100% end stone for leaf replacements in the End
    nether:
      logs:
        gravel: 100          # 100% gravel for log replacements in the Nether
      leaves:
        glowstone: 100       # 100% glowstone for leaf replacements in the Nether
```

Any valid Minecraft material name can be used. If an invalid material is configured, a warning is logged and the block is skipped.

#### Weighted Block Mixing

You can mix multiple materials with percentage weights. Each block in the grown tree is independently randomised using these weights.

```yaml
    nether:
      logs:
        gravel: 70           # 70% of log blocks become gravel
        netherrack: 30       # 30% of log blocks become netherrack
      leaves:
        glowstone: 60
        soul_sand: 40
```

Weight rules:
- **Exactly 100%** — each material appears at its stated probability.
- **More than 100%** — weights are scaled proportionally; no AIR is added. A warning is logged.
- **Less than 100%** — the remainder is filled with AIR (blocks are removed). A warning is logged.

### Per-Tree-Type Overrides

Override the global block replacements for individual tree species. Useful when you want oak trees to look different from acacia trees in the same dimension.

Priority: **per-tree** > per-gamemode > global default.

```yaml
    nether:
      logs:
        gravel: 100            # global default for all Nether logs
      per-tree:
        logs:
          acacia:
            netherrack: 80
            gravel: 20         # acacia logs use a different mix
        leaves:
          acacia:
            soul_sand: 100     # acacia leaves use soul sand
```

Supported tree type keys: `oak`, `spruce`, `birch`, `jungle`, `acacia`, `dark_oak`.

### Per-Gamemode Overrides

If you run multiple BentoBox gamemodes (e.g. BSkyBlock and CaveBlock) on the same server, you can configure different block replacements per gamemode. Per-gamemode overrides fall back to the global default when a gamemode is not listed.

Priority: per-tree > **per-gamemode** > global default.

```yaml
    nether:
      logs:
        gravel: 100            # global default
      per-gamemode:
        logs:
          CaveBlock:
            obsidian: 100      # CaveBlock islands use obsidian for Nether logs
          AcidIsland:
            netherrack: 100
        leaves:
          CaveBlock:
            nether_wart_block: 60
            glowstone: 40
```

The gamemode key must exactly match the addon name as registered in BentoBox (e.g. `BSkyBlock`, `CaveBlock`, `AcidIsland`).

### Full Priority Order

When a tree grows, the replacement block for each log or leaf is resolved in this order:

1. **Per-tree override** for this specific tree species (highest priority)
2. **Per-gamemode override** for the gamemode that owns the world
3. **Global default** for the dimension (lowest priority)

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
