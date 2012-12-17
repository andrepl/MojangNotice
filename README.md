MojangNotice
===

MojangNotice is a bukkit plugin that uses data from craftstats.com to notify players when Mojang's login and sessions servers go down.

It's capable of replacing the MOTD messages shown to players in the server list of their game clients, and also of running any arbitrary commands when the servers go down, or come back up, and periodically until they come back up. 

Commands
---
Note: 'mojangnotice' can be a abbreviated to 'mojang'.

/mojangnotice reload - reload the config file.
/mojangnotice check - force a status check now.
/mojangnotice set [setting] [value] - Change settings.
(settings that can be changed via command include: 'checkinterval', 'repeatinterval', 'overridemotd', and 'motd')

Permissions
---
    mojangnotice.reload: 
        default: op
        description: allowed to reload the config file.

    mojangnotice.check:
        default: op
        description: allowed force a status check

    mojangnotice.set.*:
        description: change mojangnotice config settings.
        default: op
        children:
            mojangnotice.set.motd: true
            mojangnotice.set.repeat-interval: true
            mojangnotice.set.check-interval: true
            mojangnotice.set.override-motd: true

Source
---
Source code is available on [github](https://github.com/andrepl/MojangNotice)

