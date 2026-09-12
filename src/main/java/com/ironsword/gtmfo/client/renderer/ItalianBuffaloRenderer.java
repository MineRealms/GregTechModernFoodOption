package com.ironsword.gtmfo.client.renderer;

import com.ironsword.gtmfo.GregTechModernFoodOption;
import com.ironsword.gtmfo.common.entity.ItalianBuffaloEntity;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class ItalianBuffaloRenderer extends MobRenderer<ItalianBuffaloEntity, CowModel<ItalianBuffaloEntity>> {

    private static final ResourceLocation TEXTURE = GregTechModernFoodOption.id("textures/entity/italian_buffalo/italian_buffalo.png");

    public ItalianBuffaloRenderer(EntityRendererProvider.Context context) {
        super(context, new CowModel<>(context.bakeLayer(ModelLayers.COW)), 0.7F);
    }

    @Override
    public ResourceLocation getTextureLocation(ItalianBuffaloEntity entity) {
        return TEXTURE;
    }
}
