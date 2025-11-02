import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
public class ItemManager<T extends Item> {
    protected List<T> items;
    public ItemManager() {
        this.items = new ArrayList<>();
    }
    public void addItem(T item) {
        items.add(item);
    }
    public void removeItem(T item) {
        items.remove(item);
    }
    public List<T> getItemsAt(Point location) {
        return items.stream()
                    .filter(item -> item.contains(location))
                    .collect(Collectors.toList());
    }
    public void paintAll(Graphics g) {
        items.forEach(item -> item.paint(g));
    }
    public int getItemCount() {
        return items.size();
    }
}
