# Tgc-System
   **Information**
- Bug Reports and Feature Request are welcome, but I maybe won't implement them!
- You can toggle most features look at the config below

# Features
- SpawnElytra
- Statuses
- AntiChatReporting (by overriding the default msg command and sending chat messages as server)
- A reply command
- A colorcodes command, so you can check the colorcodes if you forgot one
- Join & quit message
- Message on join
- Show status on join
- Sound/Highlighting your player name is typed in chat
- A permission system
- A maintenance system
- A auto save system
- A resource pack system
- A alert/broadcast command
- A fly speed command
- A reboot command that reboots server after a specified amount of minutes with a reason
- A plugin command to disable plugins
- Time in chat messages with formatting
- A configs reload command for the plugin
- Block commands & prefixes

# Config

<details>
    <summary><b>default</b></summary>

````
#Do not edit or things might break!
version: 1.4

prefix:
  pluginPrefix: '§7[§1System§7] §f'
  alertPrefix: '§7[§4Alert§7] §f'

#You can use color codes and "%Player%" will be replaced with the player name
joinQuitMessage:
  enabled: true
  joinMessage: "§2[§a+§2] §7%Player%"
  quitMessage: "§4[§c-§4] §7%Player%"

onJoin:
  enabled: false
  #Here you also can use color codes and "%AlertPrefix%" will be replaced with the alert Prefix
  message: "Edit in /plugins/Tgc-System/config.yml"
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

#You can create multiple motds and everytime someone loads/refreshes his multiplayer menu that player will see a random motd
#Use \n for the second line
motds:
  enabled: false
  list: []

maintenance:
  motd: "§4Server is in Maintenance"
  kickMessage: "This server is now in maintenance mode"
  #You can add a 64x64 png file to the Plugin directory and that will be set as you maintenance icon
  icon: false

#Auto save your world if your hoster doesn't have that functionality
autoSave:
  enabled: false
  time: '1h'

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
version: 1.4

prefix:
  pluginPrefix: '§7[§1System§7] §f'
  alertPrefix: '§7[§4Alert§7] §f'

#You can use color codes and "%Player%" will be replaced with the player name
joinQuitMessage:
  enabled: true
  joinMessage: "§2[§a+§2] §7%Player%"
  quitMessage: "§4[§c-§4] §7%Player%"

onJoin:
  enabled: false
  #Here you also can use color codes and "%AlertPrefix%" will be replaced with the alert Prefix
  message: "Edit in /plugins/Tgc-System/config.yml"
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

#You can create multiple motds and everytime someone loads/refreshes his multiplayer menu that player will see a random motd
#Use \n for the second line
motds:
  enabled: true
  list: ["Example1 1. Line\nExample1 2. Line", "Example2 1. Line\nExample2 2. Line"]

maintenance:
  motd: "§4Server is in Maintenance"
  kickMessage: "This server is now in maintenance mode"
  #You can add a 64x64 png file to the Plugin directory and that will be set as you maintenance icon
  icon: true

#Auto save your world if your hoster doesn't have that functionality
autoSave:
  enabled: true
  time: '1h'

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