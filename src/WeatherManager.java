import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class WeatherManager {
    private final Map<String, Double> currentWeather = new HashMap<>();
    private final List<WeatherObserver> observers = new ArrayList<>();

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private int failedAttempts = 0;
    private static final int MAX_FAILED_ATTEMPTS = 3;

    public WeatherManager() {
        // Initialize with default weather values
        currentWeather.put("rain", 0.3);
        currentWeather.put("temp", 0.5);
        currentWeather.put("windx", 0.0);
        currentWeather.put("windy", 0.0);
        
        // Schedule first fetch asynchronously (don't block constructor)
        scheduler.schedule(this::updateWeather, 0, TimeUnit.SECONDS);
        // then every 5 seconds
        scheduler.scheduleAtFixedRate(this::updateWeather, 5, 5, TimeUnit.SECONDS);
    }

    /** Public entry point – called from Stage */
    public void updateWeather() {
        try {
            URL url = new URL("http://13.238.167.130/weather");
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(2000); // 2 second timeout
            connection.setReadTimeout(2000);
            
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            Map<String, Double> newWeather = reader.lines()
                    .map(this::parseLine)
                    .filter(Objects::nonNull)
                    .filter(WeatherRecord::isRecent)
                    .collect(Collectors.groupingBy(
                            rec -> rec.attribute,
                            Collectors.averagingDouble(rec -> rec.value)
                    ));

            if (!newWeather.isEmpty()) {
                currentWeather.clear();
                currentWeather.putAll(newWeather);
                failedAttempts = 0; // Reset on success
                notifyObservers();
            }
            
            reader.close();

        } catch (Exception e) {
            failedAttempts++;
            if (failedAttempts <= MAX_FAILED_ATTEMPTS) {
                System.err.println("Weather update failed (attempt " + failedAttempts + "): " + e.getMessage());
                System.err.println("Using default weather values. Game continues normally.");
            }
            // After MAX_FAILED_ATTEMPTS, stop logging to reduce console spam
            
            // Continue with default/last known values
            notifyObservers();
        }
    }

    /** Convert one line from the server into a WeatherRecord */
    private WeatherRecord parseLine(String line) {
        String[] p = line.split(" ");
        if (p.length != 5) return null;
        try {
            return new WeatherRecord(
                    Long.parseLong(p[0]),   // timestamp
                    p[1],                   // attribute
                    Integer.parseInt(p[2]), // x
                    Integer.parseInt(p[3]), // y
                    Double.parseDouble(p[4])// value
            );
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public double get(String attribute) {
        return currentWeather.getOrDefault(attribute, 0.5);
    }

    public void addObserver(WeatherObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers() {
        double rain = get("rain");
        boolean storm = rain > 0.7;
        observers.forEach(obs -> obs.onWeatherChange(
                rain,
                get("temp"),
                get("windx"),
                get("windy"),
                storm));
    }

    /** Stop the background thread when the program exits */
    public void shutdown() {
        scheduler.shutdownNow();
    }
}
