# sever-core

This is a plugin specially designed for SEATiDE Minecraft Server. Any of our need in the area of Bukkit Plugin will be implemented in this repository or as its subset.

## Implementations

### ECS Auto Deletion

Automatically delete the ECS instance if there keeps being no online players for ***X*** seconds.

```yml
backupScript: ~
maxEmptyTime: ~
```

- (***string***, *optional*) `backupScript` — (Use absolute path) Path to the backup script. Skip if not set.
- (***int***, *required*) `maxEmptyTime` — (In seconds) The maximum time allowed to have no players online.

## License

MIT