package com.fabbe50.bettertooltips;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BetterTooltipsClient implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("bettertooltips");

	private Item targetedItem;
	private String[] tags = new String[0];

	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(Constants.ITEM_TAG_PACKET_ID, (client, handler, buf, responseSender) -> {
			tags = new String(buf.readByteArray()).split("\n");
		});

		ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
			if (MinecraftClient.getInstance().options.advancedItemTooltips && MinecraftClient.getInstance().world != null) {
				if (!stack.isEmpty()) {
					if (tags.length > 0 && targetedItem != null && stack.getItem() == targetedItem) {
						boolean flag = false;
						for (String tag : tags) {
							if (!tag.isEmpty()) {
								if (!flag) {
									lines.add(Text.of("Tags:").copyContentOnly().setStyle(Style.EMPTY.withColor(Formatting.GRAY).withUnderline(true)));
									flag = true;
								}
								Text tagText = Text.of(tag);
								lines.add(tagText.copyContentOnly().setStyle(tagText.getStyle().withColor(Formatting.GRAY)));
							}
						}
					} else {
						PacketByteBuf buf = PacketByteBufs.create();
						buf.writeItemStack(stack);
						targetedItem = stack.getItem();
						ClientPlayNetworking.send(Constants.ITEM_TAG_PACKET_ID, buf);
					}
				}
			}
			if (!MinecraftClient.getInstance().options.advancedItemTooltips && stack.isDamageable()) {
				lines.add(Text.of("Durability: " + (stack.getMaxDamage() - stack.getDamage()) + "/" + stack.getMaxDamage()));
			}
			if (stack.isFood()) {
				FoodComponent foodComponent = stack.getItem().getFoodComponent();
				if (foodComponent != null) {
					lines.add(1, Text.of("Hunger: " + foodComponent.getHunger()).copyContentOnly().setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
					lines.add(2, Text.of("Saturation: " + (foodComponent.getHunger() * foodComponent.getSaturationModifier())).copyContentOnly().setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
				}
			}
		});
	}
}
