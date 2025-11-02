import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
public class WeatherEventManager {
    private List<Consumer<Boolean>> stormListeners = new ArrayList<>();
    private boolean isStormActive = false;
    public void addStormListener(Consumer<Boolean> listener) {
        stormListeners.add(listener);
    }
    public void updateStorm(boolean isStorm) {
        if (isStorm != isStormActive) {
            isStormActive = isStorm;
            stormListeners.forEach(listener -> listener.accept(isStorm));
        }
    }
    public boolean isStormActive() { return isStormActive; }
}
