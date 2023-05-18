# Tgc-System
   **Information**
- [Bug Report](https://github.com/Timongcraft/Tgc-System/issues/new?labels=bug&projects=&template=bug_report.yml&title=%5BBug%5D%3A+)s and [Feature Request](https://github.com/Timongcraft/Tgc-System/issues/new?labels=enhancement&projects=&template=feature_request.yml&title=%5BFeature+Request%5D%3A+)s are welcome, but I maybe won't implement them!
- You can toggle most features look at the config below
- Version 1.4.2 and higher: Vanilla teams prefix/suffix is only updated while the player joins for performance reason!
- Version 1.4.1 and below: Vanilla teams can't be used at all if you use the statuses feature!

[![Modrinth](https://raw.githubusercontent.com/Timongcraft/Tgc-System/master/modrinth.png)](https://modrinth.com/plugin/tgc-system)

-----

# Features
- SpawnElytra
- Statuses
- AntiChatReporting (by overriding the default msg and say command and sending chat messages as server)
- A reply command
- A colorcodes command, so you can check the colorcodes if you forgot them
- Join & quit message
- Custom message on join
- Message with the current status on join
- Sound/Highlight your player name in chat
- A permission system
- A maintenance system
- A auto save system
- A team chat
- A resource pack system
- A alert/broadcast command
- A speed, walkspeed and flyspeed command
- A reboot command that reboots server after a specified amount of minutes with a reason
- A plugin command to disable plugins
- Time in chat messages with formatting
- A configs reload command for the plugin
- Block commands & prefixes

-----

# Config

<details>
    <summary><b>default</b></summary>

````
#Do not edit or things might break!
version: 1.6

prefix:
  pluginPrefix: '§7[§1System§7] §f'
  alertPrefix: '§7[§4Alert§7] §f'
  teamChatPrefix: '§7[§4TeamChat§7] §r'
  teamChatPrefixInChat: '#'

#You can use color codes and "%Player%" will be replaced with the player name
joinQuitMessage:
  enabled: true
  joinMessage: "§2[§a+§2] §7%Player%"
  quitMessage: "§4[§c-§4] §7%Player%"

onJoin:
  enabled: false
  #Here you also can use color codes, %prefix% will be replaced with the plugins prefix and
  #%alertPrefix% will be replaced with the alert Prefix
  message: "%prefix%Edit in /plugins/Tgc-System/config.yml"
  #If a player joins and has a status set he will be shown a message with the players status
  status: false

#This makes chat reporting impossible my overriding the msg command and sending all chat messages as the server
#But also includes features like a sound/highlighting when your name is in a message
chatSystem:
  enabled: true
  noLinks: false
  #Configure if the Timestamp should be displayed in the chat (only for player messages)
  timeStampInChat:
    enabled: false
    format: 'HH:mm'
    #https://www.php.net/manual/en/timezones
    timeZone: 'America/New_York'

#The permission manager command and the permission handling
permissionSystem:
  enabled: true

#Set a status in front of your name
#This uses teams for the tab list so if you have another plugin
#for that you can't use this
statuses:
  enabled: false
  characterLimit: 10

spawnElytra:
  enabled: false
  spawnRadius: 20
  worldName: world
  boost:
    enabled: true
    multiplyValue: 2

#Set a resource pack for the server but if you use this players with the tgc-system.team permission
#don't get the resource pack event if it is forced, and they can enable it in game with /resourcepack
resourcePack:
  url: ''
  hash: ''
  promt: ''
  force: false
  #The time after which the player gets kicked if he hasn't loaded the pack in seconds (force must be true for this)
  maxLoadTime: 10

#You can create multiple motds and everytime someone loads/refreshes his multiplayer menu that player will see a random motd
#Use \n for the second line
motds:
  enabled: false
  #This means that the real motd will only be show to ip addresses that players one the servers have
  #So if someone random has the ip the default "A Minecraft Server" Motd will be shown and the max player
  #count will be 20 so it is harder to find the server with a scanner
  hiddenMode: false
  list: []

maintenance:
  motd: "§cServer is in Maintenance"
  kickMessage: "This server is now in maintenance mode"
  #You can add a 64x64 png file to the Plugin directory and that will be set as you maintenance icon
  #Must be called maintenance-icon.png
  icon: false

#Auto save your world if your hoster doesn't have that functionality
autoSave:
  enabled: false
  time: '1h'

#This makes to so if you right-click on wheat, potatoes, carrots, beetroots and cocoa beans
easyHarvest:
  enabled: false

#Checks for new updates with the modrinth api
newUpdateNotifications:
  console: true

#These commands are blocked e.g. plugins
blockedCommands: []


#These prefixes are blocked e.g. 'bukkit:'
blockedPrefixes: []
````
</details>

<details>
    <summary><b>example</b></summary>

````
#Do not edit or things might break!
version: 1.6

prefix:
  pluginPrefix: '§7[§1System§7] §f'
  alertPrefix: '§7[§4Alert§7] §f'
  teamChatPrefix: '§7[§4TeamChat§7] §r'
  teamChatPrefixInChat: '#'

#You can use color codes and "%Player%" will be replaced with the player name
joinQuitMessage:
  enabled: true
  joinMessage: "§2[§a+§2] §7%Player%"
  quitMessage: "§4[§c-§4] §7%Player%"

onJoin:
  enabled: false
  #Here you also can use color codes, %prefix% will be replaced with the plugins prefix and
  #%alertPrefix% will be replaced with the alert Prefix
  message: "%prefix%Edit in /plugins/Tgc-System/config.yml"
  #If a player joins and has a status set he will be shown a message with the players status
  status: true

#This makes chat reporting impossible my overriding the msg command and sending all chat messages as the server
#But also includes features like a sound/highlighting when your name is in a message
chatSystem:
  enabled: true
  noLinks: true
  #Configure if the Timestamp should be displayed in the chat (only for player messages)
  timeStampInChat:
    enabled: false
    format: 'HH:mm'
    #https://www.php.net/manual/en/timezones
    timeZone: 'America/New_York'

#The permission manager command and the permission handling
permissionSystem:
  enabled: true

#Set a status in front of your name
#This uses teams for the tab list so if you have another plugin
#for that you can't use this
statuses:
  enabled: true
  characterLimit: 15

spawnElytra:
  enabled: true
  spawnRadius: 20
  worldName: world
  boost:
    enabled: true
    multiplyValue: 3

#Set a resource pack for the server but if you use this players with the tgc-system.team permission
#don't get the resource pack event if it is forced, and they can enable it in game with /resourcepack
resourcePack:
  url: ''
  hash: ''
  promt: ''
  force: false
  #The time after which the player gets kicked if he hasn't loaded the pack in seconds (force must be true for this)
  maxLoadTime: 10

#You can create multiple motds and everytime someone loads/refreshes his multiplayer menu that player will see a random motd
#Use \n for the second line
motds:
  enabled: true
  #This means that the real motd will only be show to ip addresses that players one the servers have
  #So if someone random has the ip the default "A Minecraft Server" Motd will be shown and the max player
  #count will be 20 so it is harder to find the server with a scanner
  hiddenMode: true
  list: ["Example1 1. Line\nExample1 2. Line", "Example2 1. Line\nExample2 2. Line"]

maintenance:
  motd: "§cServer is in Maintenance"
  kickMessage: "This server is now in maintenance mode"
  #You can add a 64x64 png file to the Plugin directory and that will be set as you maintenance icon
  icon: true

#Auto save your world if your hoster doesn't have that functionality
autoSave:
  enabled: true
  time: '1h'

#This makes to so if you right-click on wheat, potatoes, carrots, beetroots and cocoa beans
easyHarvest:
  enabled: true

#Checks for new updates with the modrinth api
newUpdateNotifications:
  console: true

#These commands are blocked e.g. plugins
#Do 'blockedCommands: []' if it should be empty
blockedCommands:
  - '?'
  - 'about'
  - 'help'
  - 'icanhasbukkit'
  - 'pl'
  - 'plugins'
  - 'me'
  - 'tm'
  - 'teammsg'
  - 'ver'
  - 'version'

#These prefixes are blocked e.g. 'bukkit:'
#Do 'blockedPrefix: []' if it should be empty
blockedPrefix:
  - 'bukkit:'
  - 'minecraft:'
  - 'sbs-system:'
````
</details>

-----

# Dependencies

- [CommandAPI](https://github.com/JorelAli/CommandAPI) (MIT License)