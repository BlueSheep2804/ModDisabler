# ModDisabler
特定のModを無効化するMod。

## 設定
`config/moddisabler.txt`で設定できます。
```
<Mod ID> <Modのファイル名(正規表現)>
```

## 例
### EMI + TMRV環境下でのJEIの競合を回避
`config/moddisabler.txt`
```
jei jei-1\.21\.1-neoforge-[\d.]+\.jar
```
