# sever-core

简体中文 | [English](./README.en.md)

这是一个用来管理和拓展 SEATiDE 服务器的主插件，今后所有的拓展将在此插件中进行，或者作为此插件的子集进行。

## 当前已完成的实现

### 自动释放 ECS

当服务器空闲达到指定的时间以后，备份并释放当前的抢占式实例。

```yml
backupScript: ~
maxEmptyTime: ~
adminUsername: ~
adminPassword: ~
```

- (***string***, *选填*) `backupScript` — （绝对路径）指向备份脚本的路径。如果不填写就不备份。
- (***int***, *必填*) `maxEmptyTime` — （单位：秒）允许服务器空闲的最长时间，超过则释放。
- (***string***, *必填*) `adminUsername` — 后端 API 中的管理员用户名。
- (***string***, *必填*) `adminPassword` — 后端 API 中的管理员密码。

## License

MIT