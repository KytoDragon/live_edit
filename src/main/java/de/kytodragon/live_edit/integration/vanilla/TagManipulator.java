package de.kytodragon.live_edit.integration.vanilla;

import de.kytodragon.live_edit.recipe.GeneralManipulationData;
import de.kytodragon.live_edit.recipe.IRecipeManipulator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.*;
import java.util.stream.Collectors;

public class TagManipulator extends IRecipeManipulator<ResourceLocation, Tag<Item>, VanillaIntegration> {

    @Override
    public ResourceLocation getKey(Tag<Item> tag) {
        return tag.key.location();
    }

    @Override
    public Tag<Item> manipulate(Tag<Item> tag, GeneralManipulationData data) {
        if (data.itemsToReplace.keySet().stream().anyMatch(item -> tag.content.contains(item))) {
            Tag<Item> new_tag = new Tag<>();
            new_tag.key = tag.key;
            new_tag.content = tag.content.stream().map(item -> data.itemsToReplace.getOrDefault(item, item)).collect(Collectors.toSet());
            return new_tag;
        }
        return tag;
    }

    @Override
    public Collection<Tag<Item>> getCurrentRecipes() {
        return integration.forge_tag_manager.getTagNames().map(this::createTag).toList();
    }

    @Override
    public Optional<Tag<Item>> getRecipe(ResourceLocation key) {
        return integration.forge_tag_manager.getTagNames().filter(tag -> tag.location().equals(key)).map(this::createTag).findAny();
    }

    @Override
    public void prepareReload(Collection<Tag<Item>> tags) {
        integration.addNewTags(tags);
    }

    private Tag<Item> createTag(TagKey<Item> key) {
        Tag<Item> new_tag = new Tag<>();
        new_tag.key = key;
        new_tag.content = integration.forge_tag_manager.getTag(key).stream().collect(Collectors.toSet());
        return new_tag;
    }
}
