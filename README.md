# ModDisabler
[日本語](README.ja.md)  
A mod that disables specific mods.

## Configuration
Configure it in `config/moddisabler.txt`.
```
<Mod ID> <Mod file name (regular expression)>
```

## Example
### Avoiding JEI conflicts in an EMI + TMRV environment
`config/moddisabler.txt`
```
jei jei-1\.21\.1-neoforge-[\d.]+\.jar
```
