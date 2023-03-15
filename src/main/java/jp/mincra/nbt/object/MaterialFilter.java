package jp.mincra.nbt.object;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MaterialFilter {
    @SerializedName("slot")
    @Expose
    public String slot;
}
