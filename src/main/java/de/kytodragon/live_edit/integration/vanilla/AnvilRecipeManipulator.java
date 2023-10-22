package de.kytodragon.live_edit.integration.vanilla;

public class AnvilRecipeManipulator {

    // TODO See JEIs AnvilRecipeMaker
    // Repair-Materials can be queried via Item.isValidRepairItem and are hardcoded in e.g. ArmorMaterials.repairIngredient.
    // Enchantment.canEnchant can be used to determine, if a enchantment is compatible with a certain item.
    // Item.isEnchantable tells you, if an item accepts enchantments in the first place.
    // Anvil-Recipes can be added, changed or deleted via ForgeHooks.onAnvilChange
    // We would need a custom packet to update the client with new recipes
}
