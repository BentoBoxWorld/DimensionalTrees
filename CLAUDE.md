# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

DimensionalTrees is a BentoBox addon for Spigot/Paper Minecraft servers. When trees grow in the Nether or End dimensions, this addon replaces their blocks (logs, leaves) with dimension-appropriate materials configured by the server admin.

## Build Commands

```bash
# Build the plugin JAR
mvn clean package

# Run all tests
mvn test

# Run a single test class
mvn test -Dtest=ClassName

# Run a single test method
mvn test -Dtest=ClassName#methodName
```

The built JAR lands in `target/`. CI builds append a build number; master branch builds strip `-SNAPSHOT`.

## Architecture

This is a small, focused addon with four main classes:

- **`DimensionalTrees`** — Main addon class (extends BentoBox `Addon`). Entry point. Loads `Settings`, registers the `AdminCommand` and `TreeGrowEvent` listener during `onEnable()`. Config reload via `onReload()`.

- **`Settings`** — BentoBox `ConfigObject` backed by `config.yml`. Holds the replacement block types (logs/leaves) for End and Nether, the list of tree sapling types to process, and enable/disable flags per dimension.

- **`TreeGrowEvent`** — The core listener. Handles Bukkit's `StructureGrowEvent`. Checks if the sapling is in the configured tree types list, determines which dimension (Nether vs End), then iterates the grown blocks and swaps logs/leaves to the configured materials.

- **`AdminCommand` / `DTReloadCommand`** — BentoBox composite command pattern. Parent command (`/dtrees`, `/dt`) with a `reload` subcommand.

## BentoBox Framework Patterns

- Addons extend `world.bentobox.bentobox.api.addons.Addon` and use its lifecycle hooks (`onLoad`, `onEnable`, `onDisable`, `onReload`).
- Configuration uses `@ConfigEntry` annotations on `@ConfigObject` classes; BentoBox serializes/deserializes these automatically.
- Commands extend `CompositeCommand`; subcommands register themselves to a parent in their constructor.
- Locale/i18n strings live in `src/main/resources/locales/`. Keys are referenced via `user.getTranslation("key")`.

## Dependencies

- **Spigot API 1.21.3** — Minecraft server API (provided scope)
- **BentoBox 2.7.1** — Addon framework (provided scope)
- **Java 17**
