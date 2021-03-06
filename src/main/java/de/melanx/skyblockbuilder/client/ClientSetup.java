package de.melanx.skyblockbuilder.client;

import de.melanx.skyblockbuilder.Registration;
import de.melanx.skyblockbuilder.util.TemplateLoader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeWorldTypeScreens;

@OnlyIn(Dist.CLIENT)
public class ClientSetup {

    public static void clientSetup() {
        ForgeWorldTypeScreens.registerFactory(Registration.customSkyblock, (parent, settings) -> new ScreenCustomizeSkyblock(parent, TemplateLoader.getTemplate()));
    }
}
