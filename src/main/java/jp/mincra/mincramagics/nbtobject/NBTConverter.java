package jp.mincra.mincramagics.nbtobject;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;

public class NBTConverter {
    public String convertItemToJson(ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);
        JsonObject jsonObject = new JsonObject();
        for (String key : nbtItem.getKeys()) {
            JsonElement element = null;
            try {
                element = JsonParser.parseString(nbtItem.getString(key));
            } catch (Exception ignored) {
            }
            if (element == null) {
                element = new Gson().toJsonTree(nbtItem.getCompound(key).getKeys());
            }
            jsonObject.add(key, element);
        }
        return new Gson().toJson(jsonObject);
    }
}
