# Assignment 2: Weather Grid Game

## Overview
This assignment is an updated version of Assignment 1 in which I have included Weather Data Streaming to make Grid Based Game. The main features of the game are effect of Weather in Gameplay Mechanics, proper use of Design Patterns, Lambdas and Streams and having computer as your opponent (Single Player Mode).

## Game Description
A 60-second competitive game where:
- **Player:** The player is gonna control the Cat and Bird to collect Fish and Seeds respectively.
- **Computer:** The computer controls the dog to collect Bones.
- **Weather conditions:** The weather effects the movement cost and gameplay of the animal
- **Winner:** Cats needs to collect Fish, Bird needs to collect Seeds, and Dog needs to collect Bones through they are gonna score points. The winner is determined by the highest score out of Two players. 

---

## Running the Game

### Prerequisites:
- Java 17 or higher
- Internet connection (for weather data)

### Compilation:
```bash
javac *.java
```

### Execution:
```bash
java Main
```

### Controls:
1. Click Cat or Bird to select
2. Click adjacent cell to move
3. Dog moves automatically
4. Collect food to score points
5. Highest score at 60s wins
6. Click "RESTART" to play again

---

## Design Patterns Implementation

### 1. **Observer Pattern**
**Location**: `WeatherObserver`, `WeatherManager`, `WeatherEventManager`

**Purpose**: Decouples weather data source from game state reactions

**Implementation**:
```java
@FunctionalInterface
public interface WeatherObserver {
    void onWeatherChange(double rain, double temp, double windX, double windY, boolean storm);
}
```

**Why This Pattern**:
- **Loose Coupling**: Game components don't need to poll weather data
- **Scalability**: We can scale it for unlimited observers without modifyiing WeatherManager
- **Real-time Updates**: The weather changes automatically
- **Separation of Concerns**: Weather data collection is independent from weather reactions

**Example Usage**:
```java
weatherManager.addObserver((rain, temp, windX, windY, storm) -> {
    this.isStorm = storm;
    // React to weather changes
});
```

### 2. **Strategy Pattern**
**Location**: `MovementStrategy`, `MovementCostStrategy`

**Purpose**: Enables flexible, runtime-configurable behavior for movement and cost calculation

**Implementation**:
```java
@FunctionalInterface
public interface MovementStrategy {
    Optional<Cell> chooseMove(Actor actor, List<Cell> adjacent, Grid grid, double windX, double windY);
}

@FunctionalInterface
public interface MovementCostStrategy {
    double calculateCost(Cell cell, double rain, double temp);
}
```

**Why This Pattern**:
- **Runtime Flexibility**: Each actor have their own movement strategy
- **Easy Extension**: We can add new strategies without updating existing one
- **Testability**: The strategies can be tested independently
- **Weather Integration**: The movement costs are dynamically adapted to the weather condition

**Example Usage**:
```java
movementCostStrategy = (cell, rain, temp) -> {
    double cost = 1.0;
    if (cell.getCellType().getName().equals("Water") && rain > 0.5) {
        cost *= 3.0;  // Flooded water is harder to traverse
    }
    if (temp > 0.7) cost *= 0.5;   // Heat makes movement easier
    else if (temp < 0.3) cost *= 1.5; // Cold slows movement
    return cost;
};
```

### 3. **Decorator Pattern**
**Location**: `CellDecorator`, `FloodDecorator`, `HeatWaveDecorator`, `ChillDecorator`

**Purpose**: Dynamically adds weather effects to cells without modifying base cell classes

**Implementation**:
```java
public abstract class CellDecorator extends Cell {
    protected Cell decoratedCell;
    protected DoubleUnaryOperator costModifier;
    
    public double getMovementCost(double weatherValue) {
        return costModifier.applyAsDouble(decoratedCell.getMovementCost(weatherValue));
    }
}
```

**Why This Pattern**:
- **Single Responsibility**: The base cells are simple whereas decorators add complexity
- **Composition Over Inheritance**: The weather effects are stacked dynamically
- **Runtime Modification**: The cells lose or gain specialties based on the weather effects conditions.
- **Visual Feedback**: Each decorator provides distinct visual tinting

**Example**:
```java
Cell decorated = baseCell;
if (rain > 0.5) decorated = new FloodDecorator(decorated);
if (temp > 0.7) decorated = new HeatWaveDecorator(decorated);
```

### 4. **Consumer Pattern**
**Location**: `Actor implements Consumer<Boolean>`

**Purpose**: Enables actors to react to storm events uniformly

**Implementation**:
```java
@Override
public void accept(Boolean isStorm) {
    visibility = isStorm ? 0.3f : 1.0f; // Reduce visibility in storm
}
```

**Why This Pattern**:
- **Functional Programming**: Actors become event consumers
- **Uniform Interface**: All actors respond to storms consistently
- **Stream Integration**: Can use `actors.forEach(actor -> actor.accept(isStorm))`

---

## Lambdas and Streams Implementation

### 1. **Weather Data Streaming**
**Location**: `WeatherManager.updateWeather()`

**Sophisticated Stream Pipeline**:
```java
Map<String, Double> newWeather = reader.lines()
    .map(this::parseLine)                    // Transform lines to WeatherRecords
    .filter(Objects::nonNull)                // Remove invalid records
    .filter(WeatherRecord::isRecent)         // Keep only recent data (<60s)
    .collect(Collectors.groupingBy(          // Group by attribute (rain/temp/etc)
        rec -> rec.attribute,
        Collectors.averagingDouble(rec -> rec.value)  // Average multiple readings
    ));
```

**Why This Approach**:
- **Declarative**: This expresses what to do rather than how to do
- **Composable**: Each of the operations are independent of each other and reuable
- **Lazy Evaluation**: Only the required Data is processed
- **Data Aggregation**: This automatically averages the weather readings based on their attributes
- **Functional Purity**: There is no side effect in the transformation stage

### 2. **Actor Management with Streams**
**Location**: `Stage` class

**Example - Finding Actors**:
```java
Actor dog = actors.stream()
    .filter(a -> a.getActorType().equals("Dog"))
    .findFirst()
    .orElse(null);
```

**Example - Food Collection**:
```java
bones.sort((b1, b2) -> {
    Cell dogCell = dog.getLocation();
    double d1 = Math.sqrt(Math.pow(b1.x - dogCell.x, 2) + Math.pow(b1.y - dogCell.y, 2));
    double d2 = Math.sqrt(Math.pow(b2.x - dogCell.x, 2) + Math.pow(b2.y - dogCell.y, 2));
    return Double.compare(d1, d2);
});
```

**Why Streams Over Loops**:
- **Readability**: The readability and intent is clearly than the manual loops
- **Maintainability**: It helps to write less repeatative codes
- **Optimization**: Java Virtual Machine can optimize stream operations

### 3. **Functional Interfaces for Game Logic**

**MovementCostStrategy as Lambda**:
```java
movementCostStrategy = (cell, rain, temp) -> {
    double cost = 1.0;
    if (cell.getCellType().getName().equals("Water") && rain > 0.5) cost *= 3.0;
    if (temp > 0.7) cost *= 0.5;
    else if (temp < 0.3) cost *= 1.5;
    return cost;
};
```

**Observer as Lambda**:
```java
weatherManager.addObserver((rain, temp, windX, windY, storm) -> {
    this.isStorm = storm;
    // React immediately to weather changes
});
```

**Benefits**:
- **Inline Definition**: The logics are defined where it is used
- **Type Inference**: The compiler infers the types automatically
- **Closure Support**: this.isStorm captures the surrounding context

---

## Uniqueness and Creativity

### 1. **Competitive AI System**
**Innovation**: Computer-controlled Dog with sophisticated pathfinding

**Features**:
- **Smart Target Selection**: Prioritizes accessible bones over blocked ones
- **Weighted Pathfinding**: Evaluates moves based on distance improvement
- **Obstacle Avoidance**: Detects and routes around Cat/Bird
- **Opportunistic Eating**: Grabs nearby food while pursuing distant targets
- **Anti-Stuck Mechanism**: Random movement when cornered

**Code Highlight**:
```java
private void moveDogToNearestBone() {
    // Calculates reachability scores for all bones
    // Penalizes blocked targets (+1000 to score)
    // Uses weighted scoring for move evaluation
    // Adds randomness to avoid local minima
}
```

### 2. **Timed Competitive Gameplay**
**Innovation**: 60-second timer creates urgency and competition

**Features**:
- Real-time countdown display
- Red warning when ≤10 seconds remain
- Automatic game over with winner determination
- Score tracking for Player vs Computer
- One-click restart functionality

### 3. **Dynamic Weather Integration**
**Innovation**: Real-time weather affects gameplay mechanics

**Weather Effects**:
- **Flooding** (rain > 0.5): Movement cost ×2-3
- **Heat Wave** (temp > 0.7): Movement cost ×0.5
- **Cold** (temp < 0.3): Movement cost ×1.5
- **Storm** (combined conditions): Reduced visibility

**Visual Feedback**:
- Color-coded weather warnings in UI
- Cell tinting based on weather effects
- Real-time weather attribute display
- Active effects list with symbols

### 4. **Enhanced Food System**
**Innovation**: Dynamic food spawning with multi-point detection

**Features**:
- Spawns 3 new items after each consumption
- Multiple detection points ensure reliable eating
- Type-specific food (Bone/Fish/Seed) for each animal
- Statistics tracking by food type

### 5. **Comprehensive UI/UX**
**Innovation**: Information-rich interface with real-time updates

**Features**:
- Timer with color-coded urgency
- Score display with win status
- Cell information on hover
- Movement cost display
- Active weather effects list
- Food statistics breakdown
- Selected actor highlighting
- Restart button on game over

---

## Technical Highlights

### 1. **Concurrent Programming** 
- Scheduled executor service for weather updates (5s interval)
- Scheduled executor service for Dog AI (1s interval)
- Graceful shutdown of background threads
- Thread-safe weather data updates

### 2. **Error Handling**
- Connection timeouts (2 seconds)
- Failed attempt tracking
- Graceful degradation to default weather
- User-friendly error messages

### 3. **Performance Optimizations**
- Lazy stream evaluation
- Efficient collision detection
- Cached weather data
- Optimized rendering pipeline

---

## How Weather Streaming Works

### Data Flow:
1. **Client.java** demonstrates basic weather server connection
2. **WeatherManager** connects to `http://13.238.167.130/weather`
3. **Stream Processing**: Lines → WeatherRecords → Filtering → Grouping → Averaging
4. **Observer Notification**: All registered observers receive updates
5. **Game Reactions**: 
   - Grid applies cell decorators
   - Movement costs recalculated
   - Visual effects updated
   - Storm visibility changes

### Weather Record Structure:
```
timestamp attribute x y value
1234567890 rain 0 0 0.75
1234567890 temp 0 0 0.82
```

### Processing Pipeline:
```
HTTP Stream → Lines → Parse → Filter Recent → Group by Attribute → Average → Notify Observers
```

---

## Design Insights

### Why These Patterns Matter:

1. **Observer Pattern**: Enables reactive programming - the game world automatically responds to weather changes without tight coupling. This is crucial for real-time systems where state changes must propagate efficiently.

2. **Strategy Pattern**: Separates algorithm from context. Weather conditions can change strategies at runtime without modifying actor classes. This exemplifies the Open/Closed Principle.

3. **Decorator Pattern**: Provides unlimited flexibility in combining weather effects. A cell can be simultaneously flooded AND frozen, with costs multiplicatively stacked. This demonstrates composition over inheritance.

4. **Lambdas & Streams**: Transform imperative loops into declarative data pipelines. The weather processing stream clearly expresses intent: "Get recent data, group by type, average values." This is far more maintainable than nested loops with mutable state.

### Real-World Applications:
- **Game Development**: AI decision-making, event systems
- **Data Processing**: ETL pipelines, real-time analytics
- **UI Frameworks**: Reactive updates, state management
- **Microservices**: Event-driven architectures

---

## Improvements Over Assignment 1

### Assignment 1 Features:
- Static grid with manual actor control
- Basic inheritance hierarchy
- Simple food collection
- No time pressure

### Assignment 2 Additions:
- ✅ Real-time weather streaming integration
- ✅ Observer pattern for weather events
- ✅ Strategy pattern for movement/costs
- ✅ Decorator pattern for cell effects
- ✅ Autonomous AI opponent
- ✅ Timed competitive gameplay
- ✅ Dynamic weather effects on mechanics
- ✅ Sophisticated lambda/stream usage
- ✅ Concurrent background processing
- ✅ Comprehensive UI with real-time info
- ✅ Game restart functionality

---
