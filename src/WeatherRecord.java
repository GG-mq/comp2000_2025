public class WeatherRecord {
    public final long   timestamp;
    public final String attribute; 
    public final int    x;
    public final int    y;
    public final double value;
    public WeatherRecord(long timestamp, String attribute,int x, int y, double value) {
        this.timestamp = timestamp;
        this.attribute = attribute;
        this.x = x;
        this.y = y;
        this.value = value;
    }
    public boolean isRecent() {
        return System.currentTimeMillis() / 1000L - timestamp < 60;
    }
    public double getValue() {
        return value;
    }
    public String getAttribute() {
        return attribute;
    }
}
