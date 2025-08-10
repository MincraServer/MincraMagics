package jp.mincra.mincramagics.oraxen.mechanic.magicstaff;

/**
 * TriggerType when player uses the material actively.
 * Note that this class is different from MaterialSlot, as the former is used for skill activation, and the latter is used for slots.
 * For instance, the latter may contain a passive slot, but the former does not.
 */
public enum TriggerType {
    LEFT, RIGHT, SWAP, DROP
}
