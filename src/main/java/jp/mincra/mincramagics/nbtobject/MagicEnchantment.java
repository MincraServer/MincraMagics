package jp.mincra.mincramagics.nbtobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MagicEnchantment {
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("lvl")
    @Expose
    public Integer lvl;

}