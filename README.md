# all-in-one

The things you can do with the mod :
+ Added a scoreboard criterion named `minedCount` to stat players mined counts
+ Added chain blocks
  + Use command `/chain start` to start chain block
  + Use command `/chain stop` to stop chain block
  + Use command `/chain max <int>` to modify max blocks that can be chained 
  + If you started chain block, you need to sneaking to chain blocks
+ Added cs commands
  + Use `/c` to spectator mode
  + Use `/s` to survive mode
  + Use `/whitelist <boolean>` to open/close whitelist (default close)
  + Use `/whitelist list` to list whitelist players
  + Use `/whitelist add <player>` to add player to whitelist
  + Use `/whitelist remove <player>` to remove player to whitelist
+ Added infinity and mending enchantment can be merged on one bow
+ Command `/seed` no longer requires permission
+ The server whitelist matches `player names` instead of `uuid` now
+ Added plan commands
  + Used `/plan add <content>` to add a plan
  + Used `/plan remove <index>` to remove a plan
  + Used `/plan modify <index> <content>` to modify the plan's content
  + Used `/plan complete <index> [false]` to complete[incomplete] a plan
  + Used `/plan list <all/completed/unfinished>` to list all/completed/unfinished plans
