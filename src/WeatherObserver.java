@FunctionalInterface
public interface WeatherObserver {
    /** Called every time the weather data changes */
    void onWeatherChange(double rain, double temp,
                         double windX, double windY,
                         boolean storm);
}
