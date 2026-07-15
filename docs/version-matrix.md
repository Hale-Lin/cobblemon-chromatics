# Version matrix

| Component | Locked version | Notes |
| --- | --- | --- |
| Minecraft Java Edition | 1.21.1 | Do not upgrade independently |
| Cobblemon | 1.7.3 | Fabric build for Minecraft 1.21.1 |
| Java | 21 | Required for Minecraft 1.21.1 |
| Fabric Loader | 0.17.2 | Cobblemon 1.7.3 minimum requirement |
| Fabric API | 0.116.13+1.21.1 | Project baseline |
| Yarn mappings | 1.21.1+build.3 | Compile-time mappings |

Any dependency added to the client or server pack must explicitly support Minecraft 1.21.1 and Fabric. A version that only says "1.21" is not accepted without a launch test.
