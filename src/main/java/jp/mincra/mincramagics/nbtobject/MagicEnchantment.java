package jp.mincra.mincramagics.nbtobject;

public enum MagicEnchantment {
    ;

    public enum Enchantment {    // マナ消費量減
        MANA_AFFINITY("mana_affinity"),
        // クールダウン減
        MAGIC_EXPERT("magic_expert");
        private final String id;
        ID(String id) {
            this.id = id;
        }
        public String getId() {
            return id;
        }

    }

    private final Enchantment enchantment;
    private final int lvl;

    MagicEnchantment(Enchantment enchantment, int lvl) {
        this.enchantment = enchantment;
        this.lvl = lvl;
    }
}