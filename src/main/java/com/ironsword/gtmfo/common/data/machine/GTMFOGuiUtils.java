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
    public static final ResourceTexture GTFO_LOGO_XMAS = new ResourceTexture("gtceu:textures/gui/icon/gtfo_logo_xmas.png");

    /** Original {@code GTFOSimpleMachineMetaTileEntity}: the christmas logo is used during the winter event. */
    private static ResourceTexture currentLogo() {
        return com.gregtechceu.gtceu.api.GTValues.XMAS.getAsBoolean() ? GTFO_LOGO_XMAS : GTFO_LOGO;
    }

    /** Wraps GTCEu's simple machine UI and adds the logo in the top-right corner. */
    public static EditableMachineUI withLogo(ResourceLocation path, GTRecipeType recipeType) {
        EditableMachineUI base = SimpleTieredMachine.EDITABLE_UI_CREATOR.apply(path, recipeType);
        return new EditableMachineUI("simple_logo", path, () -> {
            WidgetGroup group = base.createDefault();
            group.addWidget(new ImageWidget(group.getSize().width - 20, 2, 18, 18, currentLogo()));
            return group;
        }, base::setupUI);
    }
}
