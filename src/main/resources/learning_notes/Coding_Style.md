# Coding Style for Forge Mods

https://mcforge.readthedocs.io/en/1.18.x/gettingstarted/structuring/


## Package

- Pick a unique name
- Usually a website/url, such as `com.example`
- Append your modid afterwards
- eg: `com.fred.exploregalore`

## The `mods.toml` File

- TOML format
- Can put multiple mods into your file structure, 
  and then simply indicate each one in this file
  
- The version will usually say, `${file.jarVersion}`.
  during development, the version in the mods page will say `NONE`, 
  however, Forge will take care of it during deployment.
  
## The Mod File (`ExploreGalore.java`)
- The *entry point* to the mod
- Use the `@Mod` annotation to tell the Forge Mod Loader that this
  is where it should look for to initialize your mod
  
## File Structure
- Use many subpackages
- Keep common (code that runs on both server & client), and client-only
  code separate
  
- eg: Items, Blocks, Block Entities would go in `common`

## Class Naming
- Append `Block`, `Item`, `Entity`, etc. to each respective implementation
- Eg: For an item called "PowerBand"  
  - Put inside `item` package
  - Name its class `PowerBandItem`

