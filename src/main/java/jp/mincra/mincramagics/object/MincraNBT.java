package jp.mincra.mincramagics.object;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MincraNBT {
    @SerializedName("Materials")
    @Expose
    public List<Material> materials;
    @SerializedName("MagicEnchantments")
    @Expose
    public List<MagicEnchantment> magicEnchantments;
    @SerializedName("MaterialFilters")
    @Expose
    public List<MaterialFilter> materialFilters;

    public MincraNBT() {
        magicEnchantments = new ArrayList<>();
        materials = new ArrayList<>();
        materialFilters = new ArrayList<>();
    }
}
