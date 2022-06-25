# Handling Information and Storage

## Networking

- Forge generally uses the `Netty` framework to aid in communication
    - `SimpleImpl` is one option for beginners
- 2 goals in network communication:
    1. Making sure the client view correctly represents what is stored/calculated server-side.
       - Showing that a flower at X, Y, Z just grew
    2. Making sure the server receives and responds to any client changes, like player inputs/key presses. 
- Most common way to accomplish client-server communication is to pass messages back and forth in some structured format. 


## Named Binary Tag (NBT)

- Most common way to pass information in Minecraft
- Used to store information on the disk (serialization?)

### NBT Types

- 14 different types
  - Most common is `CompoundTag`, used within `Entity`/`BlockEntity` when writing to disk
- Implement the `Tag` interface
  - Contains methods to read/write to a file, and copying. 
- Includes `EndTag`, `ByteTag`, `ShortTag`, ... (refer to wiki/source code)
- Most common are: 
  - `CompoundTag`: Holds an object composed of other tags (just like Java object, where it can store primitives, other objects, itself).
  - `ListTag`: Holds a list of a specific tag. 
  
### Usages

- `CompoundTag`: Use methods like `put`, `putInt`, `getInt`, etc., to store values within them in a map, via a key. 
- `ListTag`: Use just like `ArrayList` - add, remove, set, get, ....

## FriendlyByteBuf

- Byte array to synchronize information across a network.
- Like a queue, FIFO. 
- !! TODO, REFER TO WIKI !!

## Sending Packets

- !! TODO, REFER TO WIKI !!

## SimpleChannel

- Packet system revolving around `SimpleChannel` class
- The easiest way to send data between client/server
- !! TODO, REFER TO WIKI !!

## Networking with Entities

- Entities are advanced objects, and so have additional systems to synchronize entity data. 
- !! TODO, REFER TO WIKI !!

## DynamicOps

- Part of Mojang's `DataFixerUpper` serialization library
- Used alongside `codec`s to convert Java objects to a serialized format
  - Codecs describe *how* the Java object is to be serialized
  - DynamicOps describe the *format* the object is to be serialized to

### Built-In DynamicOps

#### `JsonOps`
- Used to serialize/deserialize JSON data
- Instances of `DynamicOps<JsonElement>`
- Can be used with Codecs to serialize/deserialize objects from assets and datapacks
- 2 public instances of `JsonOps`:
  -  `JsonOps.INSTANCE` and `JsonOps.Compressed`. 
    - Compressed data is represented as a single string. Never used in vanilla Minecraft. 
  
#### `NbtOps`
- Used to serialize/deseralize NBT data
- Instances of `DynamicOps<Tag>`
- Can also be used for serializing:
  - Data into packets to send across networks
  - Persistent data for entities and similar objects
- Only 1 public instance: `NbtOps.INSTANCE`.

### Using DynamicOps

- We combine `Codec<SomeClassType>` with `DynamicOps<SomeSerializedFormat>` to convert objects to a serialized form and back. Refer to the 'Codecs' section for details. 

- We can convert between serialized formats like `Tag` and `JsonElement` using `DynamicOps#convertTo` instance method:

```java
// converting Tag to JsonElement
// DynamicOps#convertTo is NbtOps.Instance.convertTo() in this case
JsonElement someJsonElement = NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE, someTag);

// converting JsonElement to Tag
// DynamicOps#convertTo is JsonOps.Instance.convertTo() in this case
Tag someTag = JsonOps.INSTANCE.convertTo(NbtOps.INSTANCE, someJsonElement);
```

- Note: exceptions may be thrown if the format is incompatible
  - eg: Converting lists of numbers (does this mean `Number` class or generic number?), due to NBT's strong typing and implementation. 
  
## Codecs

- Serialization tool from Mojang's DataFixerUpper library
  - Used with `DynamicOps` to serialize objects with different formats (JSON or NBT)
- DynamicOps describes the FORMAT the object is to be serialized to
- Codec describes the MANNER/METHOD in which the object is to be serialized
  - A single Codec can be used to serialize an object to any format for which a DynamicOps exists.
  - Together, Codecs and DynamicOps form an abstraction layer over data serialization. 
  
### Using Codecs

- Goal: convert a Java object to a JsonElement/Tag (serialization) and back (deserialization). 
- Given: `Codec<SomeJavaType>` and `DynamicOps<SomeSerializedType>`
  - We want to convert an instance of `SomeJavaType` to an instance of `SomeSerializedType`
- Methods needed:
  - Serialization: `Codec#encodeStart`
  - Deserialization: `Codec#parse`
  - These methods:
    - Take a `DynamicOps` instance
    - Take an instance of the object we want to serialize/deserialize
    - Return a `DataResult`

Example:
```java
// let someCodec be a Codec<SomeJavaType>
// let someJavaObject be an instance of SomeJavaType
// let someTag and someJsonElement be instances of Tag and JsonElement, respectively

// serialize some java object to Tag
DataResult<Tag> result = someCodec.encodeStart(NBTOps.INSTANCE, someJavaObject);

// deserialize some Tag instance back to a proper java object
DataResult<SomeJavaType> result = someCodec.parse(NBTOps.INSTANCE, someTag);

// serialize some java object to a JsonElement
DataResult<JsonElement> result = someCodec.encodeStart(JsonOps.INSTANCE, someJavaObject);

// deserialize a JsonElement back to a proper java object
DataResult<SomeJavaType> result = someCodec.parse(JsonOps.INSTANCE, someJsonElement);
```

- `DataResult`:
  - Holds the converted instance OR error data, depending on success
  - `DataResult#result` returns a Java `Optional` object containing the converted object if successful
  - `DataResult#resultOrPartial` executes a given function if the conversion was successful, AND returns an `Optional` object. Useful for error logging. 
  
Example:
```java
// deserialize something from json
someCodec.parse(JsonOps.INSTANCE, someJsonElement)
	.resultOrPartial(errorMessage -> doSomethingIfBadData(errorMessage))
	.ifPresent(someJavaObject -> doSomethingIfGoodData(someJavaObject))
```
- Note: `ifPresent` is method from `Optional` class that executes the provided function if the contained object exists. 

### Built-in Codecs

#### Primitive Codecs
- The `Codec` class contains some built-in instances for primitive types, like BOOL, INT, DOUBLE, STRING, INT_STREAM (serializes to list of numbers), EMPTY (null objects), etc. 

#### Other Built-in Codecs
- Vanilla minecraft also has many other built-in Codecs for commonly-serialized objects. 
- Generally a static instance of `Codec<SomeClass>` found inside the class the codec is serializing (i.e., `SomeClass`).
  - Ex: `ResourceLocation.CODEC`, for `Codec<ResourceLocation>`, or `BlockPos.CODEC` for `Codec<BlockPos>`

```java
// Found inside the ResourceLocation class
public class ResourceLocation implements Comparable<ResourceLocation> {
   public static final Codec<ResourceLocation> CODEC = Codec.STRING.comapFlatMap(ResourceLocation::read, ResourceLocation::toString).stable();
}
```

- Each vanilla `Registry` acts as the Codec for type of object the registry contains
  - `Registry.BLOCK` is itself a `Codec<Block>`
- `ForgeRegistrie`s, however, don't currently implement Codecs, so one must implement them themselves if so desired. 
  - Special note: `CompoundTag.CODEC` can be used to serialize CompoundTags into a JSON file. However, it CANNOT safely deserialize numbers from JSON due to the strong typing of `ListTag`, and the way the `NBTOps` deserializer reads numeric values. 
  
### Creating Codecs

Problem: Given the following class, how can we *deserialize* a JSON file to an instance of this class?

**The Java Class:**
```java
public class ExampleCodecClass {

    private final int someInt;
    private final Item item;
    private final List<BlockPos> blockPositions;

    public ExampleCodecClass(int someInt, Item item, List<BlockPos> blockPositions) {...}

    public int getSomeInt() { return this.someInt; }
    public Item getItem() { return this.item; }
    public List<BlockPos> getBlockPositions() { return this.blockPositions; }
}
```
**The Corresponding JSON File:**
```json
{
	"some_int": 42,
	"item": "minecraft:gold_ingot",
	"block_positions":
	[
		[0,0,0],
		[10,20,-100]
	]
}
```
Answer:

1. We create a Codec for `ExampleCodecClass` (`Codec<ExampleCodecClass>`) by piecing together existing codecs for its fields:
    - `Codec<Integer>`, in which we can use `Codec.INT`
    - `Codec<Item>`, in which we can use `Registry.ITEM`
    - `Codec<List<BlockPos>>`, in which we can use (and modify) `BlockPos.CODEC`
2. We use `Codec#listOf` instance method to generate a codec for a list from an existing codec:
    - Codecs created using `listOf` serialize to list-like objects, like [] JSON arrays, or ListTags. 
    - Deserializing a list in this manner(?) produces an *immutable* list. 
    - `XMap` can be used to produce a mutable list. 
```java
// BlockPos.CODEC is a Codec<BlockPos>
Codec<List<BlockPos>> = BlockPos.CODEC.listOf();
```

3. We use `RecordCodecBuilder` to handle explicitly named fields (i.e., someInt, item, blockPositions in our above example). 
```java
public static final Codec<ExampleCodecClass> = RecordCodecBuilder.create(instance -> instance.group(
		Codec.INT.fieldOf("some_int").forGetter(ExampleCodecClass::getSomeInt),
		Registry.ITEM.fieldOf("item").forGetter(ExampleCodecClass::getItem),
		BlockPos.CODEC.listOf().fieldOf("block_positions").forGetter(ExampleCodecClass::getBlockPositions)
	).apply(instance, ExampleCodecClass::new));
```
- Each line in the group specifies a codec instance for the corresponding field type, the field name in the serialized object, and the corresponding getter method. 
    - There is a limitation of 16 different fields. For those curious, it is because `instance` is of type `Instance`, which inherits from `Kind1`, which accepts up to 16 generic types - take a look!
- We wrap up the builder using `apply`, providing a factory (such as a constructor) for the class we want to deserialize to. 

#### Optional and Default Values in Record Fields
- In our above example code for `RecordCodecBuilder`, we have implemented it in a way such that each field *MUST* be inside the serialized object (JsonObject/CompoundTag) or else the codec will fail to deserialize it. 
- If we want for certain fields to be optional, we can replace `fieldOf()` with:
    - `someCodec.optionalFieldOf("field_name")` - creates a field for an `Optional` object. A non-present field in the JSON/NBT will result in an empty `Optional`. Likewise, an empty `Optional` object will be omitted from serialization (not written to JSON/NBT). 
    - `someCodec.optionalFieldOf("field_name", someDefaultValue)` - creates an optional field that will be given `someDefaultValue` if not present in the JSON/NBT. Likewise, if the field object equals `someDefaultValue`, the field will not be serialized to the JSON/NBT.
- Note: optional fields fail SILENTLY! If the field contains bad data/fails to serialize, the error is silently caught, and the field will serialize to the DEFAULT value!

#### Boxing Values as Objects
- What if we need to serialize a single value as a single-field object?
- We can skip `RecordCodecBuilder` and use `fieldOf` by itself:
```java
public static final Codec<Integer> BOXED_INT_CODEC = Codec.INT.fieldOf("value").codec();

JsonElement value = BOXED_INT_CODEC.encodeStart(JsonOps.INSTANCE, 5).result().get();
```
Which serializes to:
```java
{"value":5}
```

### Useful Codec Properties/Functions

#### Unit
- Syntax: `Codec.unit(defaultValue)` (static)
  - Creates a Codec that *always* deserializes to the provided `defaultValue`, regardless of input. The Codec serializes to *nothing*. 
  
#### Pair
- Syntax: `Codec.pair(codecA, codecB)` (static)
  - Generates a `Codec<Pair<A, B>>` from the two provided codecs. 
  - `Pair<A,B>` is a DFU class. 
- Valid arguments: Codecs that serialize to objects with *explicit* fields.
  - Eg, Codecs created using `RecordCodecBuilder` or `fieldOf`.
  - Additionally, Codecs that serialize to nothing (like unit codecs) are also valid (They essentially act as objects with *no* fields). 

Ex: Create a codec with a `value` and `name` field. 
```java
public static final Codec<Pair<Integer,String>> PAIR_CODEC = Codec.pair(
	Codec.INT.fieldOf("value").codec(),
	Codec.STRING.fieldOf("name").codec());

JsonElement encodedPair = PAIR_CODEC.encodeStart(JsonOps.INSTANCE, Pair.of(5, "cheese").result().get();
```
Serializes to:
```java
{
	"value": 5,
	"name": "cheese"
}
```
- Note: Codecs that serialize to objects with undefined fields such as `unboundedMap` may cause unpredictable/undefined behaviour when used with Pairs. They should be first be boxed via `fieldOf` when used in a Pair codec. 

#### Either 
- Syntax: `Codec.either(codecA, codecB)`
- Takes 2 codecs, generates a `Codec<Either<A, B>>`
- When de/serializing, it first attempts to use the FIRST Codec. If and only if this fails, it will attempt to use the SECOND codec. 
  - If this 2nd conversion fails, the returned DataResult will contain the error data from the *SECOND* Codec's conversion attempt. 
  
#### Numeric Ranges
- Syntax (static methods):
  - `Codec.intRange(min, max)`
  - `Codec.floatRange(min, max)`
  - `Codec.doubleRange(min, max)`
- Generates Codecs for int/float/doubles for which only values within the specified range will de/serialize successfully (else fail). 

#### Maps
- Use case:
  - We want to serialize a `Map` type (like `HashMap`), where there is indefinitely many key-value pairs, and we don't know the keys ahead of time. 
- Solution: create a `Codec<Map<KEY, VALUE>>` using the `Codec.unboundedMap` static method.
  - Takes as arguments a Key and Value Codecs:
  - `public static final Codec<Map<String, BlockPos>> = Codec.unboundedMap(Codec.STRING, BlockPos.CODEC);`
- Serialized result: `JsonObject`s or `CompoundTag`s
  - Fields are key-value pairs inside the map
  - Keys are used as the field names
  - Values are used as the values for the corresponding field
- Limitation(s): `unboundedMap` ONLY supports key codecs that serialize to Strings
  - Eg: Codec for `ResourceLocation`
  - If a Map has non string-like keys, the Map must be serialized as a **list** of key-value pairs instead. 

#### Equivalent Types: `xmap`
- Problem: We have two types: `Amalgam` and `Box`, such that any `Amalgam` instance can be converted to a `Box` instance. 
  - We have already implemented a `Codec<Amalgam>`, but not `Codec<Box>`
  - If the two classes are so similar, how can we avoid writing repetitive code?
- Solution: `Codec#xmap`
  - Use on a `Codec<Amalgam>` instance to generate a second codec for a fundamentally equivalent type to the first Codec's type.
  - Takes as parameters the class conversion methods `toOtherClass`:
  - `public static final Codec<Box> = Amalgam.CODEC.xmap(Amalgam::toBox, Box::toAmalgam);`
  - Note that the created `Codec<Box>` will serialize to the exact same format as `Codec<Amalgam>`. 
  
#### Partially Equivalent Types: `flatComapMap,`, `comapFlatMap`, and `flatXMap`
- Problem: We have two types/classes, A, and B, where every instance of A can be converted to B, but not the other way around.
  - Ex: `ResourceLocation` -> `String` is valid, but not vice versa
  - `xmap` would not work here, as when converting B to A, we would get a runtime exception, when in reality we *want* a failed `DataResult`. 
  
- Solution: Use 3 other possible instance methods in `Codec`

| A -> B ALWAYS possible? | B -> A ALWAYS possible? | Method of `codecA` to use to create `codecB` |
| --- | --- | --- |
| Y | Y | `codecA.xmap` |
| Y | N | `codecA.flatComapMap` |
| N | Y | `codecA.comapFlatMap` |  
| N | N | `codecA.flatXmap` |

#### Registry Dispatch Codecs
- Used to define a registry of Codecs, and indicate which Codec should be used for deserialization based on a type field found inside the JSON file. 
  - Commonly used for deserializing world data
- Example steps for a class `Thing`:

1. `Thing` must(?) be an abstract class. 
2. Create a `ThingType` interface. 
    - `ThingType` must have a method `Codec<Thing> codec()`, which supplies a `Codec<Thing>`.
    - `Thing` subclasses must define a method that supplies a `ThingType`
3. Create a Map/Registry of `ThingType`s, and register a `ThingType` for each `Thing` codec (subclasses) we want to have. 
4. Create a `Codec<ThingType>`, or have the `ThingType` registry implement `Codec`. 
5. Create a master Codec `Codec<Thing>` by invoking `Codec#dispatch` on our `ThingType` Codec. The arguments are:
    - A field name for the ID of the sub-codec (such as "type" in example below)
    - A function to retrieve a `ThingType` from a `Thing`
    - A function to retrieve a `Codec<Thing>` from a `ThingType`
  
- We can now use the `Codec<Thing>` to create `Thing` fields in other Codecs whose represented classes use `Thing` objects. 

Example using `ExampleCodecClass`: 
- Note that the field name for the ID is "type"
- We assume that `ExampleCodecClass` extends `Thing`
- In this case, the serialized format becomes (in JSON):

```json
"some_thing":
{
	"type": "ourmod:exampleclass",
	"some_int": 42,
	"item": "minecraft:gold_ingot",
	"block_positions":
	[
		[0,0,0],
		[10,20,-100]
	]
}
```

Concrete examples in Minecraft Source Code:
- `RuleTest` and `RuleTestType`
- `BlockPlacer` and `BlockPlacerType`
- `ConfiguredDecorator` and `FeatureDecorator`