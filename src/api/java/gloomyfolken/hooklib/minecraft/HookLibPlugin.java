package gloomyfolken.hooklib.minecraft;

import cpw.mods.fml.relauncher.*;

import java.lang.reflect.Field;
import java.util.Map;

public class HookLibPlugin implements IFMLLoadingPlugin {
	
	private static boolean obf;
	private static boolean checked;
	
	public static boolean getObfuscated() {
		if (!checked) {
			try {
				Field deobfField = CoreModManager.class.getDeclaredField("deobfuscatedEnvironment");
				deobfField.setAccessible(true);
				obf = !deobfField.getBoolean(null);
				FMLRelaunchLog.info("[HOOKLIB] " + " Obfuscated: " + obf);
			} catch (Exception e) {
				e.printStackTrace();
			}
			checked = true;
		}
		return obf;
	}
	
	// 1.6.x only
	public String[] getLibraryRequestClass() {
		return null;
	}
	
	@Override
	public String[] getASMTransformerClass() {
		return new String[] { PrimaryClassTransformer.class.getName() };
	}
	
	@Override
	public String getModContainerClass() {
		return null;
	}
	
	@Override
	public String getSetupClass() {
		return null;
	}
	
	@Override
	public void injectData(Map<String, Object> data) {}
	
	// 1.7.x only
	public String getAccessTransformerClass() {
		return null;
	}
}
