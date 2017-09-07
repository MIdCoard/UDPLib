package team.unstudio.udpl.config;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.file.YamlConfiguration;

import team.unstudio.udpl.config.serialization.ConfigurationSerializationHelper;

/**
 * 配置文件处理器
 */
public abstract class ConfigurationHandler{

	private final File file;
	private final Map<String,Object> defaults = new HashMap<>();
	private boolean cache = false;
	
	public ConfigurationHandler(File file) {
		this.file = file;
	}
	
	private void loadDefaults(){
		defaults.clear();
		
		for(Field f:getClass().getDeclaredFields()){
			int modifiers = f.getModifiers();
			if(Modifier.isFinal(modifiers)||Modifier.isStatic(modifiers)||Modifier.isTransient(modifiers))
				continue;
			
			f.setAccessible(true);
			
			ConfigItem anno= f.getDeclaredAnnotation(ConfigItem.class);
			if(anno==null) continue;
			
			try {
				defaults.put(anno.value(),f.get(this));
			} catch (IllegalArgumentException | IllegalAccessException e) {}
		}
	}
	
	/**
	 * 重载配置文件
	 * @return
	 */
	public boolean reload(){
		if(!cache){
			loadDefaults();
			cache=true;
		}
			
		YamlConfiguration config = ConfigurationHelper.loadConfiguration(file);
		if(config == null)
			return false;

		for (Field f : getClass().getDeclaredFields()) {
			int modifiers = f.getModifiers();
			if (Modifier.isFinal(modifiers) || Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers))
				continue;

			f.setAccessible(true);

			ConfigItem anno = f.getDeclaredAnnotation(ConfigItem.class);
			if (anno == null)
				continue;

			try {
				Object object = ConfigurationSerializationHelper.deserialize(config, anno.value());
				if (object != null)
					f.set(this, object);
				else
					f.set(this, defaults.get(anno.value()));
			} catch (IllegalArgumentException | IllegalAccessException e) {
			}
		}

		return true;
	}
	
	/**
	 * 保存配置文件
	 * @return
	 */
	public boolean save(){
		try{
			YamlConfiguration config = ConfigurationHelper.loadConfiguration(file);
			
			for(Field f:getClass().getDeclaredFields()){
				int modifiers = f.getModifiers();
				if(Modifier.isFinal(modifiers)||Modifier.isStatic(modifiers)||Modifier.isTransient(modifiers))
					continue;
				
				f.setAccessible(true);
				
				ConfigItem anno = f.getDeclaredAnnotation(ConfigItem.class);
				if(anno==null) continue;
				
				try {
					ConfigurationSerializationHelper.serialize(config, anno.value(), f.get(this));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					ConfigurationSerializationHelper.serialize(config, anno.value(), defaults.get(anno.value()));
				}
			}
			
			config.save(file);
			
			return true;
		}catch(IOException e){
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 注解一个配置项
	 * value为在配置文件中的key
	 * 可以为注解的配置项设置一个默认值
	 */
	@Retention(RUNTIME)
	@Target(FIELD)
	public static @interface ConfigItem{
		String value();
	}
}