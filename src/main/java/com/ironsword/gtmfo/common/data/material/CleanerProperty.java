package com.ironsword.gtmfo.common.data.material;

import com.gregtechceu.gtceu.api.data.chemical.material.properties.IMaterialProperty;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.MaterialProperties;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;

public class CleanerProperty implements IMaterialProperty {

    public static final PropertyKey<CleanerProperty> CLEANER = new PropertyKey<>("gtfo_cleaner",
            CleanerProperty.class);

    private final int cleaningPower;

    public CleanerProperty(int cleaningPower) {
        this.cleaningPower = cleaningPower;
    }

    public CleanerProperty() {
        this(1);
    }

    public int getCleaningPower() {
        return cleaningPower;
    }

    @Override
    public void verifyProperty(MaterialProperties properties) {
        properties.ensureSet(PropertyKey.FLUID, true);
    }
}
