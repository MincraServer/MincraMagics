package jp.mincra.mincramagics.nbt;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Material {
    // TODO: change public to private
    @SerializedName("slot")
    @Expose
    public String slot;
    @SerializedName("id")
    @Expose
    public String id;

    public Material(String slot, String id) {
        this.slot = slot;
        this.id = id;
    }

    public String slot() {
        return slot;
    }

    public String id() {
        return id;
    }
}