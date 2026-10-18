# Graph Report - controle_remoto  (2026-10-18)

## Corpus Check
- 26 files · ~6,171 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 17 file(s) not represented in the graph (top: .xml 8, (none) 2, .properties 2)

## Summary
- 332 nodes · 606 edges · 22 communities (8 shown, 14 thin omitted)
- Extraction: 99% EXTRACTED · 1% INFERRED · 0% AMBIGUOUS · INFERRED: 7 edges (avg confidence: 0.86)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `7738091f`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- RemoteControls.kt
- RemoteKey
- RokuApp
- AppsScreen.kt
- RemoteApp
- RemoteViewModel.kt
- RokuDevice
- EcpClient
- EcpClientTest.kt
- KeyboardScreen.kt
- TvScreen.kt
- RokuControl
- .buildMagicPacket
- Tab

## God Nodes (most connected - your core abstractions)
1. `RemoteKey` - 40 edges
2. `EcpClient` - 21 edges
3. `RemoteViewModel` - 18 edges
4. `RokuDevice` - 17 edges
5. `RokuRepository` - 14 edges
6. `RokuApp` - 13 edges
7. `RemoteApp()` - 13 edges
8. `TvChannel` - 11 edges
9. `RokuControl` - 11 edges
10. `EcpClientTest` - 10 edges

## Surprising Connections (you probably didn't know these)
- `RemoteApp()` --calls--> `AppsScreen()`  [INFERRED]
  app/src/main/java/com/nathalia/rokuremote/ui/RemoteApp.kt → app/src/main/java/com/nathalia/rokuremote/ui/AppsScreen.kt
- `RemoteApp()` --calls--> `ConnectScreen()`  [INFERRED]
  app/src/main/java/com/nathalia/rokuremote/ui/RemoteApp.kt → app/src/main/java/com/nathalia/rokuremote/ui/ConnectScreen.kt
- `RemoteApp()` --calls--> `KeyboardScreen()`  [INFERRED]
  app/src/main/java/com/nathalia/rokuremote/ui/RemoteApp.kt → app/src/main/java/com/nathalia/rokuremote/ui/KeyboardScreen.kt
- `RemoteApp()` --calls--> `RemoteControls()`  [INFERRED]
  app/src/main/java/com/nathalia/rokuremote/ui/RemoteApp.kt → app/src/main/java/com/nathalia/rokuremote/ui/RemoteControls.kt
- `RemoteApp()` --calls--> `TvScreen()`  [INFERRED]
  app/src/main/java/com/nathalia/rokuremote/ui/RemoteApp.kt → app/src/main/java/com/nathalia/rokuremote/ui/TvScreen.kt

## Import Cycles
- None detected.

## Communities (22 total, 14 thin omitted)

### Community 0 - "RemoteControls.kt"
Cohesion: 0.10
Nodes (6): DPadRing(), EdgeKey(), PillKey(), RemoteControls(), RoundKey(), VolumeRocker()

### Community 1 - "RemoteKey"
Cohesion: 0.07
Nodes (28): RemoteKey, BACK, BACKSPACE, CHANNEL_DOWN, CHANNEL_UP, DOWN, ENTER, FORWARD (+20 more)

### Community 2 - "RokuApp"
Cohesion: 0.11
Nodes (11): Connected, Connecting, ConnectionState, Disconnected, Error, RokuApp, TvChannel, RokuXml (+3 more)

### Community 4 - "AppsScreen.kt"
Cohesion: 0.11
Nodes (3): AppCell(), AppsScreen(), loadIcon()

### Community 5 - "RemoteApp"
Cohesion: 0.13
Nodes (5): MainActivity, InputChip(), NavItem(), RemoteApp(), RokuRemoteTheme()

### Community 7 - "RokuDevice"
Cohesion: 0.23
Nodes (3): RokuRepository, RokuDevice, SsdpDiscovery

### Community 13 - "RokuControl"
Cohesion: 0.15
Nodes (12): Arquitetura, Assinatura, Build, Funcionalidades, Instalacao, Licenca, Limitacoes, Matriz de capacidades (+4 more)

### Community 17 - "Tab"
Cohesion: 0.40
Nodes (5): Tab, APPS, KEYBOARD, REMOTE, TV

## Knowledge Gaps
- **33 isolated node(s):** `Disconnected`, `Error`, `HOME`, `BACK`, `UP` (+28 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 152 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **14 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `RemoteKey` connect `RemoteKey` to `RemoteControls.kt`, `RemoteApp.kt`, `RemoteViewModel.kt`, `RokuDevice`, `EcpClient`, `EcpClient.kt`, `EcpClientTest.kt`, `KeyboardScreen.kt`?**
  _High betweenness centrality (0.389) - this node is a cross-community bridge._
- **What connects `Disconnected`, `Error`, `HOME` to the rest of the system?**
  _33 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `RemoteControls.kt` be split into smaller, more focused modules?**
  _Cohesion score 0.1032258064516129 - nodes in this community are weakly interconnected._
- **Why does `EcpClient` connect `EcpClient` to `EcpClient.kt`, `EcpClientTest.kt`, `RokuDevice`?**
  _High betweenness centrality (0.087) - this node is a cross-community bridge._
- **Should `RemoteKey` be split into smaller, more focused modules?**
  _Cohesion score 0.06896551724137931 - nodes in this community are weakly interconnected._
- **Why does `Pre-requisitos da TV` connect `RokuControl` to `EcpClient`?**
  _High betweenness centrality (0.068) - this node is a cross-community bridge._
- **Should `RokuApp` be split into smaller, more focused modules?**
  _Cohesion score 0.1111111111111111 - nodes in this community are weakly interconnected._