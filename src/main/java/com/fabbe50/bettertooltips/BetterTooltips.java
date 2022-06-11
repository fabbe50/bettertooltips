package com.fabbe50.bettertooltips;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Identifier;

import java.util.Collection;

public class BetterTooltips implements ModInitializer {
    @Override
    public void onInitialize() {
        ServerPlayNetworking.registerGlobalReceiver(Constants.ITEM_TAG_PACKET_ID, (server, player, handler, buf, responseSender) -> {
            ItemStack stack = buf.readItemStack();
            Collection<Identifier> tags = ItemTags.getTagGroup().getTagsFor(stack.getItem());
            PacketByteBuf newBuf = PacketByteBufs.create();
            StringBuilder builder = new StringBuilder();
            for (Identifier tag : tags) {
                builder.append(tag.toString()).append("\n");
            }
            newBuf.writeByteArray(builder.toString().getBytes());
            ServerPlayNetworking.send(player, Constants.ITEM_TAG_PACKET_ID, newBuf);
        });
        ServerPlayNetworking.registerGlobalReceiver(Constants.FOOD_LEVEL_PACKET_ID, (server, player, handler, buf, responseSender) -> {
            ItemStack stack = buf.readItemStack();
            if (stack.isFood()) {
                FoodComponent component = stack.getItem().getFoodComponent();
                if (component != null) {
                    PacketByteBuf newBuf = PacketByteBufs.create();
                    newBuf.writeInt(component.getHunger());
                    ServerPlayNetworking.send(player, Constants.FOOD_LEVEL_PACKET_ID, newBuf);
                }
            } else {
                PacketByteBuf newBuf = PacketByteBufs.create();
                newBuf.writeInt(0);
//                ServerPlayNetworking.send(player, Constants.FOOD_LEVEL_PACKET_ID, newBuf);
            }
        });
    }
}
