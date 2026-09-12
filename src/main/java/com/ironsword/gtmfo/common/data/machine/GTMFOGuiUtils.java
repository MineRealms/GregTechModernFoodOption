package com.ironsword.gtmfo.common.data.machine;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.gui.editor.EditableMachineUI;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.SimpleTieredMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;

import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.widget.ImageWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;

import net.minecraft.resources.ResourceLocation;

/**
 * GTFO machines show the GTFO logo in the corner of their GUI (original {@code GTFOSimpleMachineMetaTileEntity}).
 */
public class GTMFOGuiUtils {

    public static final ResourceTexture GTFO_LOGO = new ResourceTexture("gtceu:textures/gui/icon/gtfo_logo.png");

    /** Wraps GTCEu's simple machine UI and adds the logo in the top-right corner. */
    public static EditableMachineUI withLogo(ResourceLocation path, GTRecipeType recipeType) {
        EditableMachineUI base = SimpleTieredMachine.EDITABLE_UI_CREATOR.apply(path, recipeType);
        return new EditableMachineUI("simple_logo", path, () -> {
            WidgetGroup group = base.createDefault();
            group.addWidget(new ImageWidget(group.getSize().width - 20, 2, 18, 18, GTFO_LOGO));
            return group;
        }, base::setupUI);
    }
}
