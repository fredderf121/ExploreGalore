# Valhesia-Structures Minecraft Mod Study for Forge 1.18

## Author: Fred

This document explores the formatting of the Valhesia-Structures mod for Forge 1.18.

As it is for my own learning, there is a specific focus for *structure generation*. 

## 1. Starting File: `ValhesiaStructures.java`

`com.stal111.valhelsia_structures.core.ValhesiaStructures.java`

#### General notes:
- Keep a global `MOD_ID` string here for whenever you need it in other classes (e.g. for registries)
- For larger mods and for better organization, use separate classes for registration of recipes, 
  items, blocks, entities, etc., and then call the respective method inside the mod class.
  (as opposed to writing everything in one class)
- This class uses methods from `valhesia_core` - mainly just registry & config helper methods.

#### Constructor `ValhesiaStructures()` Summary

1. Obtain a reference of the Forge Mod Event Bus to tell Forge what things your mod will add.
   - `IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();`
2. Tell Forge to run physical-side specific (`Dist`) code using `DistExecutor#safeRunWhenOn`
   - `DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientSetup::new);`
3. Register your mod objects (items, recipes, structures, entities) using deferred registration
   - `ModStructures.STRUCTURES.register(eventBus);`
   - Note: `STRUCTURES` is a `DeferredRegister` object
4. Tell the event bus to listen in to your common setup code (`addListener`)
5. Load data from the config file, and apply changes
6. Register the class itself to the Forge Event Bus

- They also add a creative item tab, and a logger for debugging. 

    
## 2. Initialization of Structures

### 2.1 `com.stal111.valhelsia_structures.core.init.ModStructures`
- Called from `ValhesiaStructures$ValhesiaStructures`
    -  `ModStructures.STRUCTURES.register(eventBus);`
- Style note: Java initializes any static fields BEFORE an arbitrary static method from the class is called. Here, it is `register(eventBus)` from above. Therefore, any of the registration code I describe below is also called. I'm not sure how standard it is to do this, but the first time I saw this, it was a little confusing as there was no explicit method called to initialize these fields. 

1. Create a deferred registry for `Structure<?>` objects that (I think) 'appends' your registered objects to the main Forge Registry `ForgeRegistries.STRUCTURE_FEATURES`. 
2. Register all your structures to the deferred registry
    - `public static final RegistryObject<CastleStructure> CASTLE = register(CastleStructure.create(JigsawConfiguration.CODEC));`
    - Use a helper `register` function
    - The deferred registry needs the name of the structure, and a supplier that provides an instance (the SAME instance) to the structure object
      - `STRUCTURES.register(structure.getName(), () -> structure);`
    - Append your structure instances to a master list for use later
    
#### Important Notes That Will Be Discussed Later

- Upon creating the structure object, it is constructed using a `JigsawConfiguration.CODEC` parameter
- There is also a `ModStructures#setupStructures` static method, called by `com.stal111.valhelsia_structures.common.CommonSetup#setup`


## 3. Common Setup
`com.stal111.valhelsia_structures.common.CommonSetup#setup`
- Called from `ValhesiaStructures$ValhesiaStructures`
1. Receives a `FMLCommonSetupEvent` instance
  - Recall: This event is called BEFORE Server/Client setup events, and AFTER Register events are fired. Can be used for registering capabilities, like energy storage, and inventory. Also used for modifying objects from other mods (and minecraft base code itself), as (I think?) they are guaranteed to exist at this point. 
2. Tell Forge what code is to be run during `FMLCommonSetupEvent`
   - Registering blocks that are flammable and compostable.
    - `FlammableRegistry.register();`
   - Setting up the structures and its features (to be discussed later)
    - `ModStructures.setupStructures();
       ModStructureFeatures.registerStructureFeatures();`
3. 

**Side Note: Something I don't quite understand**
- For some reason, registering flammable/compostable objects is enqueued/deferred via `FMLCommonSetupEvent$enqueueWork`, while registering what can be flint-and-steeled/extinguished is executed 'normally'. I have swapped around the different registration methods, bringing them in/out of the queue, and I experienced no errors. I don't understand why certain work is deferred. 
    - Something to do with `net.minecraftforge.fml.DeferredWorkQueue`?
    - Answer from a dev on Discord: It's because Forge loads mods in parallel using multiple threads, but certain parts of vanilla Minecraft aren't thread-safe. By deferring those parts, they get written to on the main thread sequentially instead of in parallel.


## 4. A Structure Object: `ForgeStructure` (as in tool-smith forge)

- Created in `com.stal111.valhelsia_structures.core.init.ModStructures`
- `com.stal111.valhelsia_structures.common.world.structures.ForgeStructure` has the following inheritance chain:
    - `ForgeStructure` 
    - extends `AbstractValhesiaStructure` 
    - extends `ValhesiaJigsawStructure<JigsawConfiguration>`
        - extends `StructureFeature<JigsawConfiguration>` (from Minecraft)
          - extends `ForgeRegistryEntry<StructureFeature<?>>` (from Forge)
          - implements `IForgeStructureFeature` (from Forge)
        - implements `IValhesiaStructure`
    
- Additionally, we have the Minecraft `JigsawConfiguration` class:
    - `JigsawConfiguration`
    - implements `FeatureConfiguration` (from Minecraft)
    
In the following few sections, I will go through each class, and try to decipher what they do. Before we do this, however, we must delve into one important topic: `Codec`s. 



### 4.1.1. `FeatureConfiguration`
- Basic template for describing a feature. 
    - A feature, abstractly, is something that modifies what the Minecraft world looks like, AFTER(?) the base world is generated. 
        - "Base world" refers to what the world looks like after the terrain shape is generated. 
        - Examples include trees, caves, structures (like shipwrecks/mine-shafts), drip-stone, and canyons. Features from the Nether and End are also included, like End Cities, fossils, and (interestingly) "underwater magma". 
- Contains one static variable `NONE`, which is a static instance of a `NoneFeatureConfiguration` class. 
    - I would assume it is used for features that are constant, and only have one implementation (hardcoded). Not too sure what this means, as features such as Swamp Huts (witch huts), Ice Spikes, and Desert Wells seem to use this configuration. 
- `NoneFeatureConfiguration` implements `FeatureConfiguration`
    - Contains a static instance `INSTANCE`
    - Like the name suggests, it has no configurations, so the codec `CODEC` simply is a unit codec - deserializes to `INSTANCE`, serializes to nothing
    
### 4.1.2 `JigsawConfiguration` implements `FeatureConfiguration`
- Used for configuring "Jigsaw Structures" - larger structures like villages or strongholds, 


# UNFINISHED - I may have found better tutorials, and I will be working on those instead. 