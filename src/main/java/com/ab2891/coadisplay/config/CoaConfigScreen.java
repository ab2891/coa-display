package com.ab2891.coadisplay.config;

import com.ab2891.coadisplay.CoaDisplayClient;
import com.coadisplay.config.CoaConfig;
import com.coadisplay.config.CoaConfigManager;
import dev.isxander.yacl3.api.ButtonOption;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

@Environment(EnvType.CLIENT)
public final class CoaConfigScreen {
    private CoaConfigScreen() {
    }

    public static Screen build(Screen parent) {
        CoaConfig defaults = new CoaConfig();
        CoaConfig live = CoaDisplayClient.CONFIG;

        return YetAnotherConfigLib.createBuilder()
                .title(Component.literal("Crown of Avarice HUD"))
                .category(ConfigCategory.createBuilder()
                        .name(Component.literal("General"))
                        .group(OptionGroup.createBuilder()
                                .name(Component.literal("HUD"))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.literal("Enabled"))
                                        .description(OptionDescription.of(Component.literal("Show crown progress on HUD.")))
                                        .binding(defaults.enabled, () -> live.enabled, v -> live.enabled = v)
                                        .controller(BooleanControllerBuilder::create)
                                        .build())
                                .option(Option.<Integer>createBuilder()
                                        .name(Component.literal("X"))
                                        .binding(defaults.x, () -> live.x, v -> live.x = v)
                                        .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, 500).step(1))
                                        .build())
                                .option(Option.<Integer>createBuilder()
                                        .name(Component.literal("Y"))
                                        .binding(defaults.y, () -> live.y, v -> live.y = v)
                                        .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, 300).step(1))
                                        .build())
                                .option(ButtonOption.createBuilder()
                                        .name(Component.literal("Move HUD\u2026"))
                                        .description(OptionDescription.of(Component.literal("Drag the HUD text to position it.")))
                                        .action((yaclScreen, button) -> Minecraft.getInstance().setScreen(new HudMoveScreen(yaclScreen)))
                                        .build())
                                .build())
                        .build())
                .save(() -> CoaConfigManager.save(live))
                .build()
                .generateScreen(parent);
    }
}
