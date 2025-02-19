package de.kytodragon.live_edit.recipe;

import de.kytodragon.live_edit.editing.*;
import de.kytodragon.live_edit.editing.MyLootCondition.Condition;
import de.kytodragon.live_edit.editing.MyLootFunction.Function;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.*;
import net.minecraft.world.level.storage.loot.functions.*;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.*;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.common.loot.CanToolPerformAction;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Map;

public class LootTableConverter {

    public static MyLootTable convertLootTable(LootTable table) {
        MyLootTable result = new MyLootTable();
        result.id = table.getLootTableId();
        result.pools = new ArrayList<>();
        result.requiredParams = new ArrayList<>();
        result.optionalParams = new ArrayList<>();
        for (LootPool pool : table.pools) {
            result.pools.add(convertPool(pool));
        }
        for (LootContextParam<?> param : table.getParamSet().getAllowed()) {
            if (table.getParamSet().getRequired().contains(param)) {
                result.requiredParams.add(param.getName());
            } else {
                result.optionalParams.add(param.getName());
            }
        }
        result.functions = new ArrayList<>();
        for (LootItemFunction function : table.functions) {
            result.functions.add(convertFunction(function));
        }

        return result;
    }

    private static MyLootPool convertPool(LootPool pool) {
        MyLootPool result = new MyLootPool();
        result.entries = new ArrayList<>();
        result.conditions = new ArrayList<>();
        result.functions = new ArrayList<>();
        for (LootPoolEntryContainer entry : pool.entries) {
            result.entries.add(convertEntry(entry));
        }
        for (LootItemCondition condition : pool.conditions) {
            result.conditions.add(convertCondition(condition));
        }
        for (LootItemFunction function : pool.functions) {
            result.functions.add(convertFunction(function));
        }
        FloatPair rolls = convertNumberGenerator(pool.getRolls());
        result.rolls_min = (int)rolls.min;
        result.rolls_max = (int)rolls.max;
        FloatPair bonusRolls = convertNumberGenerator(pool.getBonusRolls());
        result.bonus_rolls_min = (int)bonusRolls.min;
        result.bonus_rolls_max = (int)bonusRolls.max;

        return result;
    }

    private static MyLootCondition convertCondition(LootItemCondition condition) {
        MyLootCondition result = new MyLootCondition();
        if (condition.getType() == LootItemConditions.KILLED_BY_PLAYER) {
            result.type = Condition.KILLED_BY_PLAYER;

        } else if (condition.getType() == LootItemConditions.RANDOM_CHANCE) {
            result.type = Condition.RANDOM;
            result.base_chance = ((LootItemRandomChanceCondition)condition).probability;

        } else if (condition.getType() == LootItemConditions.RANDOM_CHANCE_WITH_LOOTING) {
            result.type = Condition.RANDOM_WITH_LOOTING;
            result.base_chance = ((LootItemRandomChanceWithLootingCondition)condition).percent;
            result.additional_chance = ((LootItemRandomChanceWithLootingCondition)condition).lootingMultiplier;

        } else if (condition.getType() == LootItemConditions.ENTITY_PROPERTIES) {
            LootContext.EntityTarget target = ((LootItemEntityPropertyCondition)condition).entityTarget;
            EntityPredicate predicate = ((LootItemEntityPropertyCondition) condition).predicate;
            int num_predicates = 0;
            if (predicate.distanceToPlayer != DistancePredicate.ANY)
                num_predicates++;
            if (predicate.effects != MobEffectsPredicate.ANY)
                num_predicates++;
            if (predicate.entityType != EntityTypePredicate.ANY)
                num_predicates++;
            if (predicate.equipment != EntityEquipmentPredicate.ANY)
                num_predicates++;
            if (predicate.flags != EntityFlagsPredicate.ANY)
                num_predicates++;
            if (predicate.location != LocationPredicate.ANY)
                num_predicates++;
            if (predicate.nbt != NbtPredicate.ANY)
                num_predicates++;
            if (predicate.passenger != EntityPredicate.ANY)
                num_predicates++;
            if (predicate.vehicle != EntityPredicate.ANY)
                num_predicates++;
            if (predicate.targetedEntity != EntityPredicate.ANY)
                num_predicates++;
            if (predicate.subPredicate != EntitySubPredicate.ANY)
                num_predicates++;
            if (predicate.steppingOnLocation != LocationPredicate.ANY)
                num_predicates++;
            if (predicate.team != null) {
                throw new UnsupportedOperationException("Entity loot condition with team");
            }
            if (num_predicates > 1) {
                throw new UnsupportedOperationException("multiple entity loot conditions");
            }
            if (num_predicates == 0) {
                result.type = Condition.DESTROYED_BY_ENTITY;
            } else if (target == LootContext.EntityTarget.THIS && predicate.flags != EntityFlagsPredicate.ANY) {
                EntityFlagsPredicate flags = predicate.flags;
                if (flags.isBaby != null || flags.isCrouching != null
                    || flags.isSprinting != null || flags.isSwimming != null
                    || flags.isOnFire != Boolean.TRUE) {
                    throw new UnsupportedOperationException("unsupported entity flag condition");
                }
                result.type = Condition.ENTITY_IS_ON_FIRE;
            } else if (predicate.subPredicate != EntitySubPredicate.ANY) {
                if (predicate.subPredicate.type() == EntitySubPredicate.Types.SLIME) {
                    SlimePredicate slime = (SlimePredicate) predicate.subPredicate;
                    result.type = Condition.SLIME_SIZE;
                    Integer minValue = slime.size.getMin();
                    if (minValue != null)
                        result.slime_size_min = minValue.intValue();
                    Integer maxValue = slime.size.getMax();
                    if (maxValue != null)
                        result.slime_size_max = maxValue.intValue();

                } else if (predicate.subPredicate.type() == EntitySubPredicate.Types.FISHING_HOOK) {
                    result.type = Condition.FISHING_IN_OPEN_WATER;

                } else {
                    throw new UnsupportedOperationException("unsupported entity flag condition");
                }

            } else if (predicate.entityType != EntityTypePredicate.ANY) {
                if (predicate.entityType.serializeToJson().getAsString().startsWith("#")) {
                    result.type = Condition.KILLED_BY_ENTITY_IN_TAG;
                    EntityTypePredicate.TagPredicate type = (EntityTypePredicate.TagPredicate) predicate.entityType;
                    result.id = type.tag.location();
                } else {
                    result.type = Condition.KILLED_BY_ENTITY_OF_TYPE;
                    EntityTypePredicate.TypePredicate type = (EntityTypePredicate.TypePredicate) predicate.entityType;
                    result.id = EntityType.getKey(type.type);
                }

            } else {
                throw new UnsupportedOperationException("unsupported entity flag condition");
            }

        } else if (condition.getType() == LootItemConditions.INVERTED) {
            result.type = Condition.INVERTED_CONDITION;
            result.inverted = convertCondition(((InvertedLootItemCondition)condition).term);

        } else if (condition.getType() == LootItemConditions.ANY_OF) {
            result.type = Condition.ALTERNATIVES;
            result.alternatives = new ArrayList<>();
            for (LootItemCondition alternative : ((AnyOfCondition)condition).terms) {
                result.alternatives.add(convertCondition(alternative));
            }

        } else if (condition.getType() == LootItemConditions.DAMAGE_SOURCE_PROPERTIES) {
            DamageSourceCondition damageSourceCondition = (DamageSourceCondition) condition;
            DamageSourcePredicate damageSource = damageSourceCondition.predicate;
            if (damageSource.tags.size() != 1) {
                throw new UnsupportedOperationException("multiple damage source conditions");
            }
            if (damageSource.tags.get(0).tag.location().equals(DamageTypes.LIGHTNING_BOLT.location())) {
                result.type = Condition.KILLED_BY_LIGHTNING;
            } else {
                throw new UnsupportedOperationException("unsupported damage source condition");
            }

        } else if (condition.getType() == LootItemConditions.LOCATION_CHECK) {
            LocationCheck check = (LocationCheck) condition;
            if (!check.offset.equals(BlockPos.ZERO)) {
                throw new UnsupportedOperationException("relative location check");
            }
            if (check.predicate.x != MinMaxBounds.Doubles.ANY || check.predicate.y != MinMaxBounds.Doubles.ANY
                || check.predicate.z != MinMaxBounds.Doubles.ANY || check.predicate.structure != null
                || check.predicate.dimension != null || check.predicate.smokey != null
                || check.predicate.light != LightPredicate.ANY || check.predicate.block != BlockPredicate.ANY
                || check.predicate.fluid != FluidPredicate.ANY || check.predicate.biome == null) {
                throw new UnsupportedOperationException("unsupported location check condition");
            }

            result.type = Condition.IS_IN_BIOME;
            result.id = check.predicate.biome.location();

        } else if (condition.getType() == LootItemConditions.SURVIVES_EXPLOSION) {
            result.type = Condition.SURVIVES_EXPLOSION;

        } else if (condition.getType() == LootItemConditions.BLOCK_STATE_PROPERTY) {
            LootItemBlockStatePropertyCondition blockState = (LootItemBlockStatePropertyCondition)condition;
            if (blockState.properties.properties.size() == 1
                && blockState.properties.properties.get(0) instanceof StatePropertiesPredicate.ExactPropertyMatcher matcher) {
                result.type = Condition.BLOCK_STATE;
                result.id = ForgeRegistries.BLOCKS.getKey(blockState.block);
                result.block_state_name = matcher.getName();
                result.block_state_value = matcher.value;
            } else {
                throw new UnsupportedOperationException("unsupported block property condition");
            }

        } else if (condition.getType() == LootItemConditions.MATCH_TOOL) {
            ItemPredicate item = ((MatchTool)condition).predicate;
            int num_predicates = 0;
            if (item.items != null)
                num_predicates++;
            if (item.tag != null)
                num_predicates++;
            if (item.count != MinMaxBounds.Ints.ANY)
                num_predicates++;
            if (item.durability != MinMaxBounds.Ints.ANY)
                num_predicates++;
            if (item.enchantments != EnchantmentPredicate.NONE)
                num_predicates++;
            if (item.storedEnchantments != EnchantmentPredicate.NONE)
                num_predicates++;
            if (item.potion != null)
                num_predicates++;
            if (item.nbt != NbtPredicate.ANY)
                num_predicates++;
            if (num_predicates != 1)
                throw new UnsupportedOperationException("unsupported tool condition");

            if (item.items != null && item.items.size() == 1) {
                result.type = Condition.MATCH_TOOL_ID;
                result.id = ForgeRegistries.ITEMS.getKey(item.items.stream().findAny().get().asItem());
            } else if (item.tag != null) {
                result.type = Condition.MATCH_TOOL_TAG;
                result.id = item.tag.location();
            } else if (item.enchantments != EnchantmentPredicate.NONE && item.enchantments.length == 1) {
                EnchantmentPredicate enchantment = item.enchantments[0];
                if (enchantment.enchantment == Enchantments.SILK_TOUCH && enchantment.level.getMin() != null
                    && enchantment.level.getMin().intValue() == 1 && enchantment.level.getMax() == null) {
                    result.type = Condition.SILK_TOUCH;
                } else {
                    throw new UnsupportedOperationException("unsupported tool condition");
                }
            } else {
                throw new UnsupportedOperationException("unsupported tool condition");
            }

        } else if (condition.getType() == LootItemConditions.TABLE_BONUS) {
            BonusLevelTableCondition bonus = (BonusLevelTableCondition) condition;
            if (bonus.enchantment == Enchantments.BLOCK_FORTUNE) {
                if (bonus.values.length > 5) {
                    throw new UnsupportedOperationException("unsupported fortune level: " + (bonus.values.length - 1));
                }
                result.type = Condition.FORTUNE;
                result.fortune_chances = new ArrayList<>();
                for (float f : bonus.values) {
                    result.fortune_chances.add(f);
                }
            } else {
                throw new UnsupportedOperationException("unsupported bonus condition");
            }

        } else if (condition.getType() == CanToolPerformAction.LOOT_CONDITION_TYPE) {
            result.type = Condition.MATCH_TOOL_ACTION;
            result.id = new ResourceLocation("forge", ((CanToolPerformAction)condition).action.name());

        } else {
            throw new UnsupportedOperationException("unknown loot condition type");
        }
        return result;
    }

    private static MyLootEntry convertEntry(LootPoolEntryContainer entry) {

        MyLootEntry result = new MyLootEntry();
        result.type = entry.getType();
        result.conditions = new ArrayList<>();
        for (LootItemCondition condition : entry.conditions) {
            result.conditions.add(convertCondition(condition));
        }

        if (entry instanceof CompositeEntryBase composite) {
            result.children = new ArrayList<>();
            for (LootPoolEntryContainer child : composite.children) {
                result.children.add(convertEntry(child));
            }

        } else if (entry instanceof LootPoolSingletonContainer singleton) {
            result.functions = new ArrayList<>();
            result.weight = singleton.weight;
            result.quality = singleton.quality;
            for (LootItemFunction function : singleton.functions) {
                result.functions.add(convertFunction(function));
            }
            if (result.type == LootPoolEntries.ITEM) {
                result.id = ForgeRegistries.ITEMS.getKey(((LootItem)singleton).item);
            } else if (result.type == LootPoolEntries.TAG) {
                result.id = ((TagEntry)singleton).tag.location();
                result.drop_all_items_from_tag = !((TagEntry)singleton).expand;
            } else if (result.type == LootPoolEntries.EMPTY) {
            } else if (result.type == LootPoolEntries.REFERENCE) {
                result.id = ((LootTableReference)singleton).name;
            } else {
                throw new UnsupportedOperationException("unknown loot entry type");
            }

        } else {
            throw new UnsupportedOperationException("unknown loot entry type");
        }

        return result;
    }

    @SuppressWarnings("DataFlowIssue")
    private static MyLootFunction convertFunction(LootItemFunction function) {
        MyLootFunction result = new MyLootFunction();

        if (function instanceof LootItemConditionalFunction conditional) {
            result.conditions = new ArrayList<>();
            for (LootItemCondition condition : conditional.predicates) {
                result.conditions.add(convertCondition(condition));
            }
        }

        if (function.getType() == LootItemFunctions.SET_COUNT) {
            SetItemCountFunction countFunc = (SetItemCountFunction) function;
            result.type = Function.SET_COUNT;
            FloatPair count = convertNumberGenerator(countFunc.value);
            result.min_count = count.min;
            result.max_count = count.max;
            result.add_count = countFunc.add;

        } else if (function.getType() == LootItemFunctions.LOOTING_ENCHANT) {
            LootingEnchantFunction looting = (LootingEnchantFunction) function;
            result.type = Function.LOOTING;
            FloatPair count = convertNumberGenerator(looting.value);
            result.min_count = count.min;
            result.max_count = count.max;
            result.limit = looting.limit;

        } else if (function.getType() == LootItemFunctions.SET_POTION) {
            SetPotionFunction potion = (SetPotionFunction) function;
            result.type = Function.POTION_EFFECT;
            result.id = ForgeRegistries.POTIONS.getKey(potion.potion);

        } else if (function.getType() == LootItemFunctions.FURNACE_SMELT) {
            result.type = Function.FURNACE_SMELT;

        } else if (function.getType() == LootItemFunctions.SET_DAMAGE) {
            SetItemDamageFunction damage = (SetItemDamageFunction) function;
            result.type = Function.DAMAGE;
            FloatPair count = convertNumberGenerator(damage.damage);
            result.min_count = count.min;
            result.max_count = count.max;
            result.add_count = damage.add;

        } else if (function.getType() == LootItemFunctions.ENCHANT_RANDOMLY) {
            EnchantRandomlyFunction enchant = (EnchantRandomlyFunction) function;
            if (enchant.enchantments.isEmpty()) {
                result.type = Function.ENCHANT_RANDOMLY;
            } else {
                result.type = Function.ENCHANT_RANDOMLY_WITH_LIST;
                result.ids = new ArrayList<>(enchant.enchantments.size());
                for (Enchantment e : enchant.enchantments) {
                    result.ids.add(ForgeRegistries.ENCHANTMENTS.getKey(e));
                }
            }

        } else if (function.getType() == LootItemFunctions.SET_INSTRUMENT) {
            SetInstrumentFunction instrument = (SetInstrumentFunction) function;
            result.type = Function.INSTRUMENT;
            result.id = instrument.options.location();

        } else if (function.getType() == LootItemFunctions.SET_STEW_EFFECT) {
            SetStewEffectFunction stew = (SetStewEffectFunction) function;
            result.type = Function.STEW_EFFECT;
            result.ids = new ArrayList<>(stew.effectDurationMap.size());
            result.stew_duration_min = new ArrayList<>(stew.effectDurationMap.size());
            result.stew_duration_max = new ArrayList<>(stew.effectDurationMap.size());
            for (Map.Entry<MobEffect, NumberProvider> effect : stew.effectDurationMap.entrySet()) {
                result.ids.add(ForgeRegistries.MOB_EFFECTS.getKey(effect.getKey()));
                FloatPair duration = convertNumberGenerator(effect.getValue());
                result.stew_duration_min.add(duration.min);
                result.stew_duration_max.add(duration.max);
            }

        } else if (function.getType() == LootItemFunctions.ENCHANT_WITH_LEVELS) {
            EnchantWithLevelsFunction enchant = (EnchantWithLevelsFunction) function;
            result.type = Function.ENCHANT_WITH_LEVELS;
            FloatPair levels = convertNumberGenerator(enchant.levels);
            result.min_count = levels.min;
            result.max_count = levels.max;
            result.treasure_enchant = enchant.treasure;

        } else if (function.getType() == LootItemFunctions.APPLY_BONUS) {
            ApplyBonusCount bonus = (ApplyBonusCount) function;
            if (bonus.enchantment == Enchantments.BLOCK_FORTUNE) {
                if (bonus.formula.getType() == ApplyBonusCount.OreDrops.TYPE) {
                    result.type = Function.FORTUNE_ORES;
                } else if (bonus.formula.getType() == ApplyBonusCount.UniformBonusCount.TYPE) {
                    ApplyBonusCount.UniformBonusCount uniform = (ApplyBonusCount.UniformBonusCount) bonus.formula;
                    result.type = Function.FORTUNE_UNIFORM;
                    result.limit = uniform.bonusMultiplier;
                } else if (bonus.formula.getType() == ApplyBonusCount.BinomialWithBonusCount.TYPE) {
                    ApplyBonusCount.BinomialWithBonusCount binomial = (ApplyBonusCount.BinomialWithBonusCount) bonus.formula;
                    result.type = Function.FORTUNE_BINOMIAL;
                    result.limit = binomial.extraRounds;
                    result.max_count = binomial.probability;
                } else {
                    throw new UnsupportedOperationException("unknown bonus loot formula");
                }
            } else {
                throw new UnsupportedOperationException("unknown bonus loot enchantment");
            }

        } else if (function.getType() == LootItemFunctions.EXPLOSION_DECAY) {
            result.type = Function.EXPLOSION_DECAY;

        } else if (function.getType() == LootItemFunctions.LIMIT_COUNT) {
            LimitCount limit = (LimitCount) function;
            result.type = Function.LIMIT_COUNT;
            if (limit.limiter.min != null) {
                FloatPair limiterMin = convertNumberGenerator(limit.limiter.min);
                if (limiterMin.min != limiterMin.max) {
                    throw new UnsupportedOperationException("unknown limit count modifier");
                }
                result.min_count = limiterMin.min;
            }
            if (limit.limiter.max != null) {
                FloatPair limiterMax = convertNumberGenerator(limit.limiter.max);
                if (limiterMax.min != limiterMax.max) {
                    throw new UnsupportedOperationException("unknown limit count modifier");
                }
                result.max_count = limiterMax.max;
            }

        } else {
            throw new UnsupportedOperationException("unknown loot function");
        }

        return result;
    }

    private record FloatPair(float min, float max) {}

    private static FloatPair convertNumberGenerator(NumberProvider number) {

        float min;
        float max;
        if (number instanceof UniformGenerator uniform) {
            if (uniform.min instanceof ConstantValue constMin) {
                min = (int)constMin.getFloat(null);
            } else
                throw new UnsupportedOperationException("unknown number generator in uniform");
            if (uniform.max instanceof ConstantValue constMax) {
                max = (int)constMax.getFloat(null);
            } else
                throw new UnsupportedOperationException("unknown number generator in uniform");
        } else if (number instanceof ConstantValue constVal) {
            min = constVal.getFloat(null);
            max = min;
        } else {
            throw new UnsupportedOperationException("unknown number generator");
        }
        return new FloatPair(min, max);
    }
}
