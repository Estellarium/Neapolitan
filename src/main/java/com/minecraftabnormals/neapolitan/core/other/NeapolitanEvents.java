package com.minecraftabnormals.neapolitan.core.other;

import java.util.List;

import com.minecraftabnormals.neapolitan.common.entity.goals.AvoidBlockGoal;
import com.minecraftabnormals.neapolitan.core.Neapolitan;
import com.minecraftabnormals.neapolitan.core.NeapolitanConfig;
import com.minecraftabnormals.neapolitan.core.registry.NeapolitanBlocks;
import com.minecraftabnormals.neapolitan.core.registry.NeapolitanEffects;
import com.minecraftabnormals.neapolitan.core.registry.NeapolitanItems;
import com.teamabnormals.abnormals_core.core.utils.TradeUtils;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.merchant.villager.VillagerTrades.ITrade;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DrinkHelper;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteractSpecific;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = Neapolitan.MODID)
public class NeapolitanEvents {

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof CreeperEntity) {
            CreeperEntity creeper = (CreeperEntity) event.getEntity();
            creeper.goalSelector.addGoal(3, new AvoidBlockGoal<>(creeper, 6, 1.0D, 1.2D));
        }
    }

    @SubscribeEvent
    public static void onEntityInteract(EntityInteractSpecific event) {
        ItemStack stack = event.getItemStack();
        Entity entity = event.getTarget();
        Hand hand = event.getHand();
        PlayerEntity player = event.getPlayer();

        if (NeapolitanConfig.COMMON.milkingWithGlassBottles.get() && entity.getType().isContained(NeapolitanTags.EntityTypes.MILKABLE)) {
            boolean notChild = entity instanceof LivingEntity ? !((LivingEntity) entity).isChild() : true;
            if (stack.getItem() == Items.GLASS_BOTTLE && notChild) {
                player.playSound(SoundEvents.ENTITY_COW_MILK, 1.0F, 1.0F);
                ItemStack itemstack1 = DrinkHelper.func_241445_a_(stack, event.getPlayer(), NeapolitanItems.MILK_BOTTLE.get().getDefaultInstance());
                player.swingArm(hand);
                player.setHeldItem(hand, itemstack1);
            }
        }
    }

    @SubscribeEvent
    public static void onExplosion(ExplosionEvent.Detonate event) {
        if (event.getExplosion().getExplosivePlacedBy() instanceof CreeperEntity) {
            CreeperEntity creeper = (CreeperEntity) event.getExplosion().getExplosivePlacedBy();
            if (event.getWorld().getBlockState(creeper.getPosition()).getBlock() == NeapolitanBlocks.STRAWBERRY_BUSH.get()) {
                for (Entity entity : event.getAffectedEntities()) {
                    if (entity instanceof LivingEntity) {
                        LivingEntity livingEntity = ((LivingEntity) entity);
                        livingEntity.heal(5.0F);
                    }
                }

                event.getAffectedEntities().clear();
                event.getAffectedBlocks().clear();
            }
        }
    }

    @SubscribeEvent
    public static void onPotionAdded(PotionEvent.PotionApplicableEvent event) {
        if (event.getEntityLiving().getActivePotionEffect(NeapolitanEffects.VANILLA_SCENT.get()) != null) {
            if (event.getPotionEffect().getPotion() != NeapolitanEffects.VANILLA_SCENT.get()) {
                event.setResult(Result.DENY);
            }
        }
    }

    @SubscribeEvent
    public static void onVillagerTrades(VillagerTradesEvent event) {
        VillagerProfession type = event.getType();
        Int2ObjectMap<List<ITrade>> trades = event.getTrades();

        List<ITrade> novice = trades.get(1);
        List<ITrade> journeyman = trades.get(3);
        List<ITrade> expert = trades.get(4);
        List<ITrade> master = trades.get(5);

        if (type == VillagerProfession.FARMER) {
            novice.add(new TradeUtils.EmeraldsForItemsTrade(NeapolitanItems.STRAWBERRIES.get(), 24, 1, 16, 2));

            journeyman.add(new TradeUtils.ItemsForEmeraldsTrade(NeapolitanItems.STRAWBERRY_SCONES.get(), 3, 12, 12, 10));

            expert.add(new TradeUtils.ItemsForEmeraldsTrade(NeapolitanItems.VANILLA_CAKE.get(), 3, 1, 12, 15));
            expert.add(new TradeUtils.ItemsForEmeraldsTrade(NeapolitanItems.CHOCOLATE_CAKE.get(), 3, 1, 12, 15));
            expert.add(new TradeUtils.ItemsForEmeraldsTrade(NeapolitanItems.STRAWBERRY_CAKE.get(), 3, 1, 12, 15));
        }
        if (type == VillagerProfession.BUTCHER) {
            master.add(new TradeUtils.EmeraldsForItemsTrade(NeapolitanItems.DRIED_VANILLA_PODS.get(), 16, 1, 16, 30));

        }
    }

    @SubscribeEvent
    public static void onWandererTradesEvent(WandererTradesEvent event) {
        event.getGenericTrades().add(new TradeUtils.ItemsForEmeraldsTrade(NeapolitanItems.VANILLA_PODS.get(), 1, 3, 4, 1));
        event.getRareTrades().add(new TradeUtils.ItemsForEmeraldsTrade(NeapolitanItems.WHITE_STRAWBERRIES.get(), 4, 8, 2, 1));
    }
}
