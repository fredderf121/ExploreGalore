package fred.exploregalore;

import fred.exploregalore.core.BlockList;
import fred.exploregalore.core.ItemList;
import net.fabricmc.api.ModInitializer;


public class ExploreGalore implements ModInitializer {

	public static final String MOD_ID = "exploregalore";



	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		ItemList.initalizeAndRegister();
		BlockList.initalizeAndRegister();




	}


}
