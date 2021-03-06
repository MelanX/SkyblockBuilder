package de.melanx.skyblockbuilder.data;

import de.melanx.skyblockbuilder.util.TemplateLoader;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;

import javax.annotation.Nonnull;

public class TemplateData extends WorldSavedData {

    private static final String NAME = "skyblock_template";

    private final Template template;

    public TemplateData(Template template) {
        super(NAME);
        this.template = template;
        this.markDirty();
    }

    public static TemplateData get(ServerWorld world) {
        DimensionSavedDataManager storage = world.getServer().getOverworld().getSavedData();
        Template template = TemplateLoader.getTemplate();
        return storage.getOrCreate(() -> new TemplateData(template), NAME);
    }

    @Override
    public void read(@Nonnull CompoundNBT nbt) {
        this.template.read(nbt);
    }

    @Nonnull
    @Override
    public CompoundNBT write(@Nonnull CompoundNBT nbt) {
        return this.template.writeToNBT(nbt);
    }

    public void refreshTemplate() {
        this.template.read(TemplateLoader.getTemplate().writeToNBT(new CompoundNBT()));
        this.markDirty();
    }

    public Template getTemplate() {
        // TODO 1.17 delete because it's only because of a bug in 1.5.1-1.5.3
        if (this.template.blocks.isEmpty() && !TemplateLoader.getTemplate().blocks.isEmpty()) {
            this.refreshTemplate();
        }

        return this.template;
    }
}
