package de.kytodragon.live_edit.editing.gui.loot_tables;

import de.kytodragon.live_edit.editing.MyLootCondition;
import de.kytodragon.live_edit.editing.MyLootCondition.Condition;
import de.kytodragon.live_edit.editing.gui.components.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class LootConditionInput extends VerticalList {

    private final ListSelectBox type;
    private final FloatInput base_chance;
    private final FloatInput additional_chance;

    private final ItemComponent tool_item;
    private final TagComponent tool_tag;
    private final ListSelectBox biome_id;
    private final ListSelectBox entity_type;
    private final ListSelectBox entity_tag;
    private final ListSelectBox blocks; // TODO use separate component?
    private final ListSelectBox tool_action;

    private final IntegerInput slime_size_min;
    private final IntegerInput slime_size_max;

    private final LootConditionInput inverted;
    private final LootConditionsInput alternatives;

    // TODO

    public LootConditionInput(int x, int y) {
        this(x, y, false);
    }

    private LootConditionInput(int x, int y, boolean from_inverted) {
        super(x, y);

        List<String> types = Arrays.stream(Condition.values()).filter(cond -> !from_inverted || cond != Condition.INVERTED_CONDITION).map(Enum::name).toList();
        type = new ListSelectBox(0, 0, 160, types, this::setType);
        addChild(type);

        TextComponent base_chance_label = new TextComponent(0, 0, "Base Chance:");
        base_chance = new FloatInput(70, 0, 50, 12);
        addChild(new ComponentGroup(0, 0, base_chance_label, base_chance));

        TextComponent additional_chance_label = new TextComponent(0, 0, "Additional Chance:");
        additional_chance = new FloatInput(95, 0, 50, 12);
        addChild(new ComponentGroup(0, 0, additional_chance_label, additional_chance));

        tool_item = new ItemComponent(0, 0);
        tool_item.only_one_item = true;
        addChild(tool_item);
        tool_tag = new TagComponent(0, 0);
        addChild(tool_tag);
        biome_id = new ListSelectBox(0, 0, 160, ForgeRegistries.BIOMES.getKeys(), null);
        addChild(biome_id);
        entity_type = new ListSelectBox(0, 0, 160, ForgeRegistries.ENTITY_TYPES.getKeys(), null);
        addChild(entity_type);
        List<String> entity_tags = ForgeRegistries.ENTITY_TYPES.tags().getTagNames().map(TagKey::location).map(ResourceLocation::toString).toList();
        entity_tag = new ListSelectBox(0, 0, 160, entity_tags, null);
        addChild(entity_tag);
        blocks = new ListSelectBox(0, 0, 160, ForgeRegistries.BLOCKS.getKeys(), null);
        addChild(blocks);
        // This line makes sure all the static variables in ToolActions are initalized and therefore all tool-actions are in the global list.
        Objects.requireNonNull(ToolActions.AXE_DIG.name());
        tool_action = new ListSelectBox(0, 0, 160, ToolAction.getActions().stream().map(ToolAction::name).toList(), null);
        addChild(tool_action);

        TextComponent slime_size_label = new TextComponent(0, 0, "Slime size:");
        slime_size_min = new IntegerInput(50, 0, 20, 12);
        slime_size_max = new IntegerInput(75, 0, 20, 12);
        addChild(new ComponentGroup(0, 0, slime_size_label, slime_size_min, slime_size_max));

        if (!from_inverted) {
            inverted = new LootConditionInput(10, 0, true);
            addChild(inverted);
        } else {
            inverted = null;
        }

        alternatives = new LootConditionsInput(0, 0);
        addChild(alternatives);

        // TODO
        //public String block_state_name;
        //public String block_state_value;
        //public List<Float> fortune_chances;

        setType(types.get(0));
    }

    private void setType(String type_name) {
        Condition type = Condition.valueOf(type_name);
        setVisiblityBasedOnType(type);
    }

    private void setVisiblityBasedOnType(Condition type) {

        base_chance.parent.is_visible = false;
        additional_chance.parent.is_visible = false;
        tool_item.is_visible = false;
        tool_tag.is_visible = false;
        biome_id.is_visible = false;
        entity_type.is_visible = false;
        entity_tag.is_visible = false;
        blocks.is_visible = false;
        tool_action.is_visible = false;
        slime_size_min.parent.is_visible = false;
        if (inverted != null)
            inverted.is_visible = false;
        alternatives.is_visible = false;

        if (type == Condition.RANDOM) {
            base_chance.parent.is_visible = true;
        } else if (type == Condition.RANDOM_WITH_LOOTING) {
            base_chance.parent.is_visible = true;
            additional_chance.parent.is_visible = true;
        } else if (type == Condition.KILLED_BY_ENTITY_OF_TYPE) {
            entity_type.is_visible = true;
        } else if (type == Condition.KILLED_BY_ENTITY_IN_TAG) {
            entity_tag.is_visible = true;
        } else if (type == Condition.SLIME_SIZE) {
            slime_size_min.parent.is_visible = true;
        } else if (type == Condition.INVERTED_CONDITION) {
            Objects.requireNonNull(inverted);
            inverted.is_visible = true;
        } else if (type == Condition.IS_IN_BIOME) {
            biome_id.is_visible = true;
        } else if (type == Condition.ALTERNATIVES) {
            alternatives.is_visible = true;
        } else if (type == Condition.BLOCK_STATE) {
            blocks.is_visible = true;
            // TODO block state name & value
        } else if (type == Condition.FORTUNE) {
            // TODO fortune chances per level
        } else if (type == Condition.MATCH_TOOL_ID) {
            tool_item.is_visible = true;
        } else if (type == Condition.MATCH_TOOL_TAG) {
            tool_tag.is_visible = true;
        } else if (type == Condition.MATCH_TOOL_ACTION) {
            tool_action.is_visible = true;
        }

        this.propagate_size_change = true;
    }

    public void setLootCondition(MyLootCondition condition) {
        type.setValue(condition.type.name());
        base_chance.setValue(condition.base_chance);
        additional_chance.setValue(condition.additional_chance);

        if (condition.type == Condition.KILLED_BY_ENTITY_OF_TYPE) {
            entity_type.setValue(condition.id.toString());
        } else if (condition.type == Condition.KILLED_BY_ENTITY_IN_TAG) {
            entity_tag.setValue(condition.id.toString());
        } else if (condition.type == Condition.SLIME_SIZE) {
            slime_size_min.setValue(condition.slime_size_min);
            slime_size_max.setValue(condition.slime_size_max);
        } else if (condition.type == Condition.INVERTED_CONDITION) {
            Objects.requireNonNull(inverted);
            inverted.setLootCondition(condition.inverted);
        } else if (condition.type == Condition.IS_IN_BIOME) {
            biome_id.setValue(condition.id.toString());
        } else if (condition.type == Condition.ALTERNATIVES) {
            alternatives.setLootConditions(condition.alternatives);
        } else if (condition.type == Condition.BLOCK_STATE) {
            blocks.setValue(condition.id.toString());
            // TODO block state name & value
        } else if (condition.type == Condition.FORTUNE) {
            // TODO fortune chances per level
        } else if (condition.type == Condition.MATCH_TOOL_ID) {
            tool_item.setItemId(condition.id);
        } else if (condition.type == Condition.MATCH_TOOL_TAG) {
            tool_tag.setTagId(condition.id);
        } else if (condition.type == Condition.MATCH_TOOL_ACTION) {
            tool_action.setValue(condition.id.getPath());
        }

        setVisiblityBasedOnType(condition.type);
    }

    public MyLootCondition getLootCondition() {
        MyLootCondition condition = new MyLootCondition();
        condition.type = Condition.valueOf(type.getValue());
        condition.base_chance = base_chance.getValue();
        condition.additional_chance = additional_chance.getValue();

        if (condition.type == Condition.KILLED_BY_ENTITY_OF_TYPE) {
            condition.id = ResourceLocation.of(entity_type.getValue(), ':');
        } else if (condition.type == Condition.KILLED_BY_ENTITY_IN_TAG) {
            condition.id = ResourceLocation.of(entity_tag.getValue(), ':');
        } else if (condition.type == Condition.SLIME_SIZE) {
            condition.slime_size_min = slime_size_min.getValue();
            condition.slime_size_max = slime_size_max.getValue();
        } else if (condition.type == Condition.INVERTED_CONDITION) {
            Objects.requireNonNull(inverted);
            condition.inverted = inverted.getLootCondition();
        } else if (condition.type == Condition.IS_IN_BIOME) {
            condition.id = ResourceLocation.of(biome_id.getValue(), ':');
        } else if (condition.type == Condition.ALTERNATIVES) {
            alternatives.setLootConditions(condition.alternatives);
        } else if (condition.type == Condition.BLOCK_STATE) {
            condition.id = ResourceLocation.of(blocks.getValue(), ':');
            // TODO block state name & value
        } else if (condition.type == Condition.FORTUNE) {
            // TODO fortune chances per level
        } else if (condition.type == Condition.MATCH_TOOL_ID) {
            condition.id = tool_item.getItemId();
        } else if (condition.type == Condition.MATCH_TOOL_TAG) {
            condition.id = tool_tag.getTagId();
        } else if (condition.type == Condition.MATCH_TOOL_ACTION) {
            condition.id = new ResourceLocation("forge", tool_action.getValue());
        }

        return condition;
    }
}
