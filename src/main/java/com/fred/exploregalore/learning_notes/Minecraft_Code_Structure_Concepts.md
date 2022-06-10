# Minecraft Code Structure Concepts

## Minecraft Sides

https://mcforge.readthedocs.io/en/1.18.x/concepts/sides/

### Physical Sides

- **Physical Client**: Runs when the user launches Minecraft from launcher
  - Threads, processes, services that run during game's graphical, interactable
    lifetime.
    
- **Physical Server, or Dedicated Server**: Runs whenever the `minecraft_server.jar` is launched.
  - NO playable GUI.
    
### Logical Sides

- **Logical Server**: Game logic
  - Mob spawning, weather, health, AI, etc.
  - Present in physical server, but can also run inside a physical client
    together with a logical client (single player world)
  - ALWAYS runs in a thread called `Server Thread`
    
- ** Logical client**: Accepts input from player, sends the info to logical server
  - Receives info from logical server and renders the info to the screen
  - Runs in the `Render Thread`, and other threads may be used for audio / chunk rendering
    
### Minecraft Forge codebase usage
- Physical side represented by enum called `Dist`
- Logical side represented by enum called `LogicalSide`


### Performing Side-Specific Operations
- I.e., if you only want code to run on server/client

- `Level#isClientSide`
  - True if the code is currently running on logical client (false if logical server)
  - Encapsulate your  game logic / mechanics code in this check
    
- `DistExecutor`
  - Common mistake is to call methods that are only present in the client OR server, such as
    `Minecraft.getInstance()...`. This crashes the physical server, as the method does not 
    exist in the server jar. 
  - Resolve using methods from `DistExecutor`, which provide ways to run different methods
    on different physical sides
    
### Thread Groups
- Alternative to check which logical side you are on

### Common Mistakes
- **Always send information across logical sides with NETWORK PACKETS**
  - Common mistake when accessing static fields (race conditions + threading issues)
  

## Resources

- Data used by game, NOT stored in code (i.e., data file)
- 2 primary resource systems:
  - **assets**: for logical client; visuals, textures, models, localizations
  - **data**: for logical server; recipes, loot tables, etc. 
  - *Resource packs* control *assets*
  - *Datapacks* control *data*
  - Standard to store them in `src/main/resources` directory
  - If there are multiple packs, the game merges them
    - Generally, the top packs override the lower ones
    - They can also be merged contentwise
  - **Naming:** snake case path and filenames (lowercase and '_')
  
### `ResourceLocation`

- What Minecraft uses to identify resources.
  - 2 parts: *namespace* AND *path*
  - Together, points to resource found at `assets/<namespace>/<ctx>/<path>`
  - Also a `ctx` context specific path fragment that indicates how the resource is used
  
- Read from a string as `<namespace>:<path>`
  - If namespace + colon is left out, it defaults to "minecraft" namespace
  - Mod should put resources into a namespace with same name as `modid`
    - e.g., `assets/exploregalore`, and `data/exploregalore`, and `ResourceLocation` would point to `exploregalore:<path>`
  - Not mandatory, and sometimes one can use different namespaces if makes logical sense, 
    but generally it is recommended
    - An example use is if you want to override a recipe from another mod (you would use that mod's namespace)
- The `ResourceLocation` is also used for identifying other objects, like registries. 


## Registries

- Process of taking objects of a mod (items, blocks, sounds, etc.) and "letting the game know about them"
- Kind of like a map that assigns values to keys
  - Eg: Forge uses registries with `ResourceLocation` keys to register objects
  - `ResourceLocation` acts as the registry "name" for objects
  - Use getters/setters; setter can only be called ONCE
  
- ALL object types have their own unique registry
  - Refer to `ForgeRegistries` for the complete list
  - Each registry name in each list is unique, but a name can be used multiple times in DIFFERENT registries
  - e.g., `exploregalore:wand` can be used in both `Block` and `Item` registries
  
  
### Methods of Registering

- Two methods: `DeferredRegister` class and `RegistryEvent$Register` lifecycle event

#### `DeferredRegister `

- Newer, documented way
- Allows use of static initializers
- Maintains a list of suppliers for entries, and then registers the objects from those suppliers
  when the `RegistryEvent$Register` occurs
  
```java
private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);

public static final RegistryObject<Block> ROCK_BLOCK = BLOCKS.register("rock", () -> new Block(BlockBehaviour.Properties.of(Material.STONE)));

public ExampleMod() {
    BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
}
```

#### `Register` Events

- More flexible alternative
- Fired AFTER mod constructor, BEFORE loading configs
- Use the event `RegistryEvent$Register<T>`, `T` is the type of object registered
- `#getRegistry` method returns the actual register, to which you can register your objects
  using `#register` or `registerAll`
  
Ex: (handler is registered on *mod event bus*)
```java
@SubscribeEvent
public void registerBlocks(RegistryEvent.Register<Block> event) {
    event.getRegistry().registerAll(new Block(...), new Block(...), ...);
}
```

- Final note: Some classes can't be registered, like BlockEntities. Instead, you register
  the `BlockEntityType`, (i.e. <...Type> classes). These function as factories that 
  create the containing type on demand
- Use the `*Type$Builder` method to create the class
- Ex: `REGISTER` is a variable of type `DeferredRegister<BlockEntityType>`
```java
public static final RegistryObject<BlockEntityType<ExampleBlockEntity>> EXAMPLE_BLOCK_ENTITY = REGISTER.register(
    "example_block_entity", () -> BlockEntityType.Builder.of(ExampleBlockEntity::new, EXAMPLE_BLOCK.get()).build(null)
);
```

- Do NOT store registered objects in fields when they are created/registered
  - ALWAYS newly create & register them whenever the `RegistryEvent$Register` event is fired
  - Allows for dynamic loading, unloading of mods in future versions of Forge. 
  - ALWAYS reference registered objects through `RegistryObject`, or `@ObjectHolder`
  
### Using RegistryObjects

- Used to retrieve references to registered objects once they are available
- Used by `DeferredRegister` to return a reference to registered objects
- Usage:
  - Call `RegistryObject#of`
  - Provide `ResourceLocation` and `IForgeRegistry` of registrable object
  - Store in public static final
  - Call `#get` to retrieve it
  
```java
public static final RegistryObject<Item> BOW = RegistryObject.of(new ResourceLocation("minecraft:bow"), ForgeRegistries.ITEMS);

// assume that ManaType is a valid registry, and 'neomagicae:coffeinum' is a valid object within that registry
public static final RegistryObject<ManaType> COFFEINUM = RegistryObject.of(new ResourceLocation("neomagicae", "coffeinum"), () -> ManaType.class); 
```

### `@ObjectHolder`

- Refer to docs
  https://mcforge.readthedocs.io/en/1.18.x/concepts/registries/
  
### Creating Custom Registries

- Refer to docs
- Use `RegistryBuilder`
- Must implement some interfaces / extend classes


## Mod Lifecycle

- Various events are fired during the mod-loading process on the mod-specific event bus
  - Many actions performed, like registering objects, data generation, or communicating with other mods
- Register **event listeners** using:
  - `@EventBusSubscriber(bus = Bus.MOD)`
  - Inside the mod constructor.
  
Ex: The annotation method:
```java
@Mod.EventBusSubscriber(modid = "mymod", bus = Mod.EventBusSubscriber.Bus.MOD)
public class MyModEventSubscriber {
    @SubscribeEvent
    static void onCommonSetup(FMLCommonSetupEvent event) { ... }
}
```
Ex: the constructor method
```java
@Mod("mymod")
public class MyMod {
    public MyMod() {
        FMLModLoadingContext.get().getModEventBus().addListener(this::onCommonSetup);
    } 

    private void onCommonSetup(FMLCommonSetupEvent event) { ... }
}
```

- Read warning on docs for thread-safety & process of writing deferred code

### Registry Events

- Fired after mod instance construction
  - `RegistryEvent$NewRegistry` event
      - Allows for registering own custom registries using `RegistryBuilder`
  - `RegistryEvent$Register<?>` event
      - Registering objects into registries
      - One event fired for each different registry
  - Both are fired synchronously during mod loading
  
### Data Generation
- Use `GatherDataEvent` to register you mod's data providers to associated data generator
- Synchronous

### Common Setup
- `FMLCommonSetupEvent` for actions common to both physical client AND server
- Eg: registering capabilities

### Sided Setups
- Fired on respective sides
- `FMLClientSetupEvent` or `FMLDedicatedServerSetupEvent`
- Eg: registering client-side key bindings

### InterModComms
- Messages sent to other mods for cross-compatibility
- Refer to docs

### Other Events
- `FMLConstructModEvent`, fired AFTER mod instance construction, BEFORE `RegistryEvent`s
- `FMLLoadCompleteEvent`, fired AFTER `InterModComms` events; when mod loading process is complete. 