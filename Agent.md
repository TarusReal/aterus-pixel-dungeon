# Aterus Pixel Dungeon - Agent Guide

## Project Overview
Aterus Pixel Dungeon is an open-source traditional roguelike dungeon crawler based on Shattered Pixel Dungeon source code. It's a Java-based game using LibGDX framework that compiles for Android, iOS, and Desktop platforms.

**Key Information:**
- **Language**: Java (LibGDX framework)
- **License**: GPLv3
- **Build System**: Gradle
- **Package**: `com.shatteredpixel.shatteredpixeldungeon`
- **Main Game Class**: `ShatteredPixelDungeon.java`

## Project Structure

### Root Directory
```
aterus-pixel-dungeon/
├── core/                    # Main game logic (cross-platform)
├── desktop/                 # Desktop-specific code
├── android/                 # Android-specific code  
├── ios/                     # iOS-specific code
├── services/                # External services (updates, news)
├── docs/                    # Documentation
└── build.gradle             # Root build configuration
```

### Core Module Structure (`core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/`)

#### **Main Game Classes**
- `ShatteredPixelDungeon.java` - Main game entry point, handles initialization and scene management
- `Dungeon.java` - Core game state management (32KB, 1087 lines)
- `Assets.java` - Asset management and loading (18KB)
- `SPDSettings.java` - Game settings and preferences
- `Statistics.java` - Game statistics tracking
- `Badges.java` - Achievement system (41KB)
- `GamesInProgress.java` - Save game management

#### **Actor System** (`actors/`)
- `Char.java` - Base character class (49KB, 1407 lines) - **FUNDAMENTAL**
- `Actor.java` - Actor system for turn-based gameplay (9KB)
- `hero/` - Player character classes and abilities
- `mobs/` - Enemy entities
- `buffs/` - Status effects and buffs
- `blobs/` - Area effects (gas, fire, etc.)

#### **Level System** (`levels/`)
- `Level.java` - Base level class (49KB, 1640 lines) - **FUNDAMENTAL**
- `RegularLevel.java` - Standard level generation
- Specific level types: `SewerLevel.java`, `PrisonLevel.java`, `CavesLevel.java`, `CityLevel.java`, `HallsLevel.java`
- Boss levels: `SewerBossLevel.java`, `PrisonBossLevel.java`, etc.
- `builders/` - Level generation algorithms
- `painters/` - Level decoration and room placement
- `rooms/` - Room types and generation
- `traps/` - Trap types and mechanics
- `features/` - Terrain features (doors, chasms, etc.)

#### **Item System** (`items/`)
- `Item.java` - Base item class (18KB)
- `Generator.java` - Item generation and distribution (36KB)
- `Heap.java` - Item piles on ground
- Subdirectories:
  - `armor/` - Armor equipment
  - `weapon/` - Weapons and ammunition
  - `wands/` - Magical wands
  - `potions/` - Potions
  - `scrolls/` - Magical scrolls
  - `rings/` - Magical rings
  - `artifacts/` - Special artifacts
  - `bags/` - Inventory bags
  - `bombs/` - Explosive items
  - `food/` - Food and consumables
  - `keys/` - Keys and quest items
  - `trinkets/` - Small accessories
  - `spells/` - Magical spells
  - `stones/` - Runestones

#### **Scene System** (`scenes/`)
- `PixelScene.java` - Base scene class
- `GameScene.java` - Main gameplay scene (52KB)
- `TitleScene.java` - Main menu
- `InterlevelScene.java` - Level transitions
- `HeroSelectScene.java` - Character creation
- `AlchemyScene.java` - Alchemy system
- `JournalScene.java` - Journal and catalog
- Other UI scenes: `AboutScene.java`, `RankingsScene.java`, etc.

#### **UI System** (`ui/`)
- UI components and windows
- `windows/` - Dialog windows and UI panels

#### **Visual System**
- `sprites/` - Character and item sprites
- `tiles/` - Dungeon tile rendering
- `effects/` - Visual effects

#### **Other Systems**
- `plants/` - Plant mechanics
- `journal/` - Journal and tracking systems
- `mechanics/` - Game mechanics (shadow casting, etc.)
- `messages/` - Localization and text
- `utils/` - Utility classes

## Important Classes and Patterns

### **Core Architecture Patterns**

#### **1. Actor System**
- All game entities extend `Actor` for turn-based processing
- `Char` is the base for all living entities (player, mobs, NPCs)
- Turn order managed through `Actor.process()`

#### **2. Level Generation**
- Levels use builder/painter pattern
- `RegularLevel` uses `Builder` for room placement and `Painter` for decoration
- Each level type has specific generation rules

#### **3. Item System**
- All items extend `Item` base class
- Items use factory pattern via `Generator.java`
- Categories: equipment, consumables, quest items

#### **4. Buff System**
- Status effects extend `Buff` class
- Applied to `Char` instances
- Automatic duration management

#### **5. Scene Management**
- UI uses scene-based architecture
- `PixelScene` base class for all scenes
- Scene transitions handled by `ShatteredPixelDungeon`

### **Key File Locations**

#### **Configuration**
- `build.gradle` - Build settings, version info, dependencies
- `gradle.properties` - Gradle configuration
- `settings.gradle` - Module configuration

#### **Assets**
- `core/src/main/assets/` - Game assets (images, data files)
- `core/src/main/assets/messages/` - Localization files
- `core/src/main/assets/interfaces/` - UI graphics

#### **Platform-Specific**
- `desktop/src/main/` - Desktop launcher and configuration
- `android/src/main/` - Android manifest and resources
- `ios/` - iOS-specific configuration

## Common Development Tasks

### **Adding New Items**

**Step-by-Step Implementation:**

```java
// 1. Create the item class in items/weapon/melee/
public class CustomSword extends MeleeWeapon {
    {
        image = ItemSpriteSheet.CUSTOM_SWORD; // Add to sprite sheet
        tier = 3;                            // Weapon tier (1-5)
        ACC = 1.5f;                         // Accuracy modifier
        DLY = 0.8f;                         // Attack speed
    }
    
    @Override
    public int max(int lvl) {
        return 4*(tier+1) +    // base damage
               lvl*(tier+1);   // level scaling
    }
    
    @Override
    public String info() {
        return Messages.get(this, "desc");
    }
}
```

**2. Add to Generator.java:**
```java
// In Generator.Category class
public enum Category {
    WEAPON,
    ARMOR,
    // ... existing categories
    CUSTOM_SWORD(1); // Add spawn weight
    
    // Add to constructor and methods
}
```

**3. Add sprite reference:**
```java
// In ItemSpriteSheet.java
public static final int CUSTOM_SWORD = 0;
```

**4. Add localization:**
```properties
# In items.properties
customsword.name=Custom Sword
customsword.desc=A powerful custom weapon with unique properties.
customsword.stats_desc=This sword deals _%d-%d_ damage and attacks _%1.2f_ times per second.
```

### **Adding New Mobs**

**Complete Implementation Example:**

```java
// 1. Create mob class in actors/mobs/
public class CustomMob extends Mob {
    {
        spriteClass = CustomMobSprite.class;
        
        HP = HT = 50;                    // Health points
        defenseSkill = 15;               // Evasion
        baseSpeed = 1.0f;               // Movement speed
        EXP = 8;                        // Experience reward
        maxLvl = 15;                    // Max level for scaling
        
        loot = Gold.class;              // Drop type
        lootChance = 0.2f;              // Drop chance
    }
    
    @Override
    public int damageRoll() {
        return Random.NormalIntRange(8, 16);
    }
    
    @Override
    public int attackSkill(Char target) {
        return 20;
    }
    
    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 4);
    }
    
    @Override
    public boolean act() {
        // Custom AI behavior
        if (state == HUNTING) {
            // Custom hunting logic
            if (enemySeen && distance(enemy) > 3) {
                // Ranged attack behavior
                return rangedAttack();
            }
        }
        return super.act();
    }
    
    private boolean rangedAttack() {
        // Implement ranged attack
        Ballistica bolt = new Ballistica(pos, enemy.pos, Ballistica.PROJECTILE);
        CustomProjectile projectile = new CustomProjectile();
        projectile.cast(this, enemy.pos);
        return true;
    }
}
```

**2. Create sprite class:**
```java
public class CustomMobSprite extends MobSprite {
    public CustomMobSprite() {
        texture(Assets.Sprites.CUSTOM_MOB);
        
        // Define animation frames
        idle = new Animation(1, true);
        idle.frames(frames, 0, 0, 0, 1);
        
        run = new Animation(12, true);
        run.frames(frames, 2, 3, 4, 5);
        
        attack = new Animation(15, false);
        attack.frames(frames, 6, 7, 8);
        
        die = new Animation(8, false);
        die.frames(frames, 9, 10, 11, 12);
        
        play(idle);
    }
}
```

**3. Add to level generation:**
```java
// In appropriate level's Builder class
@Override
protected Mob createMob() {
    if (Random.Int(10) == 0) {
        return new CustomMob(); // 10% spawn chance
    }
    return super.createMob();
}
```

### **Adding New Levels**

**Level Implementation Template:**

```java
public class CustomLevel extends RegularLevel {
    {
        color1 = new Color(0x53, 0x4f, 0x4e, 0xff);
        color2 = new Color(0xb2, 0x9d, 0x8e, 0xff);
        viewDistance = 8;
    }
    
    @Override
    protected List<Room> specialRooms() {
        List<Room> rooms = new ArrayList<>();
        rooms.add(new CustomRoom());
        return rooms;
    }
    
    @Override
    protected Painter painter() {
        return new CustomPainter();
    }
    
    @Override
    protected Builder builder() {
        return new CustomBuilder();
    }
    
    @Override
    public String tilesTex() {
        return Assets.Environment.TILES_CUSTOM;
    }
    
    @Override
    public String waterTex() {
        return Assets.Environment.WATER_CUSTOM;
    }
}
```

**Custom Builder Example:**
```java
public class CustomBuilder extends RegularBuilder {
    @Override
    protected void buildLevel() {
        super.buildLevel();
        
        // Add custom features
        for (Room room : rooms) {
            if (room instanceof CustomRoom) {
                placeCustomFeatures(room);
            }
        }
    }
    
    private void placeCustomFeatures(Room room) {
        // Add special terrain or objects
        Point center = room.center();
        map[center.x][center.y] = Terrain.CUSTOM_FEATURE;
    }
}
```

### **Modifying UI**

**Adding New Window Example:**

```java
public class WndCustom extends Window {
    private static final int WIDTH = 120;
    private static final int HEIGHT = 160;
    
    public WndCustom() {
        super(0, 0, Chrome.get(Chrome.Type.WINDOW));
        
        // Title
        RenderedTextBlock title = PixelScene.renderText(Messages.get(this, "title"), 9);
        title.hardlight(Window.TITLE_COLOR);
        title.setPos((WIDTH - title.width()) / 2, 6);
        add(title);
        
        // Content area
        CustomComponent content = new CustomComponent();
        content.setRect(0, title.bottom() + 2, WIDTH, HEIGHT - title.bottom() - 20);
        add(content);
        
        // Close button
        RedButton btnClose = new RedButton(Messages.get(this, "close")) {
            @Override
            protected void onClick() {
                hide();
            }
        };
        btnClose.setRect(0, HEIGHT - 20, WIDTH, 18);
        add(btnClose);
        
        resize(WIDTH, HEIGHT);
    }
}
```

**Adding Scene Navigation:**
```java
// In GameScene.java
public static void showCustomWindow() {
    GameScene.scene().add(new WndCustom());
}

// Or scene transition
public static void showCustomScene() {
    ShatteredPixelDungeon.switchScene(CustomScene.class);
}
```

## Advanced Implementation Examples

### **Custom Buff System**

**Complete Buff Implementation:**

```java
public class CustomBuff extends Buff {
    private static final float DURATION = 20f;
    private float timer;
    private int powerLevel;
    
    {
        type = Buff.buffType.POSITIVE; // POSITIVE, NEGATIVE, or NEUTRAL
        announced = true;              // Show message when applied
    }
    
    @Override
    public boolean act() {
        timer -= TICK;
        if (timer <= 0) {
            detach();
            return true;
        }
        
        // Apply effect each turn
        if (target instanceof Hero) {
            ((Hero) target).heal(powerLevel);
            CellEmitter.center(target.pos).burst(HealingParticle.FACTORY, 1);
        }
        
        spend(TICK);
        return true;
    }
    
    @Override
    public int icon() {
        return BuffIndicator.CUSTOM_BUFF; // Add to BuffIndicator.java
    }
    
    @Override
    public void fx(boolean on) {
        if (on) {
            target.sprite.add(Char.State.LEVITATING);
        } else {
            target.sprite.remove(Char.State.LEVITATING);
        }
    }
    
    @Override
    public String toString() {
        return Messages.get(this, "name");
    }
    
    @Override
    public String desc() {
        return Messages.get(this, "desc", dispTurns(timer));
    }
    
    public void setPower(int power) {
        this.powerLevel = power;
        this.timer = DURATION * (1 + power * 0.5f);
    }
    
    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put("timer", timer);
        bundle.put("power", powerLevel);
    }
    
    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        timer = bundle.getFloat("timer");
        powerLevel = bundle.getInt("power");
    }
}
```

**Applying Buffs:**
```java
// In item usage
@Override
public void execute(Hero hero, String action) {
    super.execute(hero, action);
    if (action.equals(AC_USE)) {
        Buff.affect(hero, CustomBuff.class).setPower(2);
        hero.spend(TIME_TO_USE);
        hero.busy();
    }
}

// In mob attacks
@Override
public int attackProc(Char enemy, int damage) {
    damage = super.attackProc(enemy, damage);
    if (Random.Int(10) == 0) {
        Buff.affect(enemy, CustomBuff.class).setPower(1);
    }
    return damage;
}
```

### **Event System Integration**

**Handling Game Events:**

```java
// Listen for level changes
public class CustomListener {
    public static void init() {
        Dungeon.addListener(new Dungeon.Listener() {
            @Override
            public void onLevelChanged(int depth) {
                // Custom logic for level transitions
                if (depth == 5) {
                    GLog.w(Messages.get(CustomListener.class, "warning"));
                }
            }
            
            @Override
            public void onMobDeath(Mob mob) {
                // Custom logic for mob deaths
                if (mob instanceof CustomMob) {
                    Statistics.customMobsKilled++;
                    Badges.validateCustomAchievement();
                }
            }
            
            @Override
            public void onItemCollected(Item item) {
                // Custom logic for item collection
                if (item instanceof CustomItem) {
                    Statistics.customItemsFound++;
                }
            }
        });
    }
}
```

**Custom Achievement System:**
```java
// In Badges.java
public enum Badge {
    // ... existing badges
    CUSTOM_ACHIEVEMENT(1),
    CUSTOM_MASTERY(2);
    
    public static boolean validateCustomAchievement() {
        if (!isUnlocked(Badge.CUSTOM_ACHIEVEMENT) && 
            Statistics.customMobsKilled >= 10) {
            Badge badge = Badge.CUSTOM_ACHIEVEMENT;
            displayBadge(badge);
            return true;
        }
        return false;
    }
}

// In Statistics.java
public static int customMobsKilled = 0;
public static int customItemsFound = 0;
```

### **Custom Projectile System**

**Projectile Implementation:**

```java
public class CustomProjectile extends Item {
    {
        image = ItemSpriteSheet.CUSTOM_PROJECTILE;
        stackable = true;
    }
    
    @Override
    protected void onThrow(int cell) {
        Char enemy = Actor.findChar(cell);
        if (enemy != null) {
            cast(curUser, enemy.pos);
        } else {
            super.onThrow(cell);
        }
    }
    
    public void cast(Hero user, int target) {
        Ballistica bolt = new Ballistica(user.pos, target, Ballistica.PROJECTILE);
        int cell = bolt.collisionPos;
        
        // Visual effect
        user.sprite.parent.add(
            new Beam.Custom(user.sprite.center(), 
                          DungeonTilemap.tileCenterToWorld(cell), 
                          Effects.get(CUSTOM_BEAM_COLOR))
        );
        
        // Damage at collision point
        if (Actor.findChar(cell) != null) {
            Char enemy = Actor.findChar(cell);
            enemy.damage(damageRoll(), this);
            
            // Apply custom effect
            Buff.affect(enemy, CustomBuff.class).setPower(1);
        }
        
        // Area effect
        for (int n : PathFinder.NEIGHBORS8) {
            int aoeCell = cell + n;
            if (Level.insideMap(aoeCell) && Actor.findChar(aoeCell) != null) {
                Char aoeTarget = Actor.findChar(aoeCell);
                aoeTarget.damage(damageRoll() / 2, this);
            }
        }
        
        // Sound effect
        Sample.INSTANCE.play(Assets.Sounds.ZAP);
    }
    
    public int damageRoll() {
        return Random.NormalIntRange(6, 12);
    }
}
```

### **Custom Room Implementation**

**Room Type Example:**

```java
public class CustomRoom extends Room {
    {
        type = Type.SPECIAL;
        setSize(7, 7);
    }
    
    @Override
    public void paint(Level level) {
        Painter.fill(level, this, Terrain.WALL);
        Painter.fill(level, this, 1, Terrain.EMPTY);
        
        // Add custom features
        Point center = center();
        level.set(center.x, center.y, Terrain.PEDESTAL);
        
        // Place custom item
        level.drop(new CustomItem(), center).type = Heap.Type.CHEST;
        
        // Add decorative elements
        for (Point p : getPoints()) {
            if (p.x == left || p.x == right || p.y == top || p.y == bottom) {
                level.set(p.x, p.y, Random.oneOf( Terrain.STATUE, Terrain.EMPTY_DECO ));
            }
        }
        
        // Place custom mob
        CustomMob mob = new CustomMob();
        mob.pos = level.pointToCell(center());
        level.mobs.add(mob);
    }
    
    @Override
    public boolean canConnect(Room r) {
        return r.type == Type.STANDARD || r.type == Type.TUNNEL;
    }
    
    @Override
    public int minWidth() {
        return 7;
    }
    
    @Override
    public int minHeight() {
        return 7;
    }
}
```

## Build Commands

```bash
# Desktop development
./gradlew desktop:debug

# Desktop release build
./gradlew desktop:release

# Android debug build
./gradlew android:debug

# Clean all builds
./gradlew clean
```

## Important Constants and Configuration

### **Version Information** (in `build.gradle`)
- `appName` - Game display name
- `appPackageName` - Package identifier
- `appVersionCode` - Internal version number
- `appVersionName` - Display version

### **Game Constants** (in various classes)
- Level depth constants in `Dungeon.java`
- Item probabilities in `Generator.java`
- Buff durations in respective buff classes

## Development Notes

### **Code Style**
- Follows Java conventions
- Extensive use of inner classes
- Heavy inheritance hierarchy
- Static factory methods common

### **Performance Considerations**
- Object pooling used for frequently created objects
- Asset loading managed through `Assets.java`
- Turn-based structure helps with performance

### **Localization**
- Uses `.properties` files for translations
- Messages accessed through `Messages.get()`
- Supports multiple languages

### **Save System**
- Uses `Bundle` class for serialization
- Game state saved in `Dungeon.java`
- Separate files for different game aspects

#### **Bundle Implementation Examples**

**When to Use Bundle:**
- Game state persistence (level changes, app closing)
- Save game creation
- Version compatibility handling

**How to Implement Bundle for Custom Classes:**

```java
// 1. For custom items - implement storeInBundle and restoreFromBundle
public class CustomItem extends Item {
    private int customValue;
    private String customName;
    
    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle(bundle);
        bundle.put("custom_value", customValue);
        bundle.put("custom_name", customName);
    }
    
    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle(bundle);
        customValue = bundle.getInt("custom_value");
        customName = bundle.getString("custom_name");
    }
}
```

**2. For custom mobs:**
```java
public class CustomMob extends Mob {
    private boolean specialState;
    private int powerLevel;
    
    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle(bundle);
        bundle.put("special_state", specialState);
        bundle.put("power_level", powerLevel);
    }
    
    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle(bundle);
        specialState = bundle.getBoolean("special_state");
        powerLevel = bundle.getInt("power_level");
    }
}
```

**3. For custom buffs:**
```java
public class CustomBuff extends Buff {
    private float duration;
    private int strength;
    
    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle(bundle);
        bundle.put("duration", duration);
        bundle.put("strength", strength);
    }
    
    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle(bundle);
        duration = bundle.getFloat("duration");
        strength = bundle.getInt("strength");
    }
}
```

**4. When to Trigger Saves:**
- After level transitions: `Dungeon.saveLevel()`
- After important item pickups: `Dungeon.saveAll()`
- Before boss fights: `Dungeon.saveLevel()`
- On app pause: `GamesInProgress.saveGame()`

**5. Save Game Locations:**
- Main save: `Dungeon.saveAll()` - saves complete game state
- Level save: `Dungeon.saveLevel()` - saves current level only
- Auto-save: Triggered every few turns automatically

## Common Issues and Solutions

### **Memory Management**
- Be careful with sprite loading/unloading
- Use object pools for temporary effects
- Dispose of LibGDX resources properly

### **Platform Differences**
- Desktop uses different input handling than mobile
- File system access varies by platform
- Screen density handling differs

### **Performance**
- Level generation can be expensive
- Large numbers of active actors slow down turns
- Complex visual effects impact framerate

## Testing

The project uses standard Java testing patterns. Test files are typically located in the same package structure under `src/test/`.

## External Dependencies

- **LibGDX** - Core game framework
- **Android SDK** - Android development
- **RoboVM** - iOS development (legacy)

## License and Distribution

This project is licensed under GPLv3. Any modifications must also be open-source under the same license.
