package team.unstudio.udpl.core.nms.common;

import java.lang.reflect.Method;
import java.util.Map;

import org.bukkit.entity.Entity;

import team.unstudio.udpl.api.nms.NMSManager;
import team.unstudio.udpl.api.nms.ReflectionUtils;

public class NMSEntity implements team.unstudio.udpl.api.nms.NMSEntity{

	private final Entity entity;
	
	public NMSEntity(Entity entity) {
		this.entity = entity;
	}

	@Override
	public Entity getEntity() {
		return entity;
	}

	@Override
	public Map<String, Object> getNBT() throws Exception {
		Class<?> NBTTagCompound = ReflectionUtils.PackageType.MINECRAFT_SERVER.getClass("NBTTagCompound");
		Object nbt = NBTTagCompound.newInstance();
		Class<?> Entity = ReflectionUtils.PackageType.MINECRAFT_SERVER.getClass("Entity");
		Method getHandle = entity.getClass().getDeclaredMethod("getHandle");
		getHandle.setAccessible(true);
		Method e = Entity.getDeclaredMethod("e", NBTTagCompound);
		e.setAccessible(true);
		e.invoke(getHandle.invoke(entity),nbt);
		return NMSManager.getNMSNBT().toMap(nbt);
	}

	@Override
	public team.unstudio.udpl.api.nms.NMSEntity setNBT(Map<String, Object> map) throws Exception {
		Class<?> NBTTagCompound = ReflectionUtils.PackageType.MINECRAFT_SERVER.getClass("NBTTagCompound");
		Object nbt = NMSManager.getNMSNBT().toNBT(map);
		Class<?> Entity = ReflectionUtils.PackageType.MINECRAFT_SERVER.getClass("Entity");
		Method getHandle = entity.getClass().getDeclaredMethod("getHandle");
		getHandle.setAccessible(true);
		Method f = Entity.getDeclaredMethod("f", NBTTagCompound);
		f.setAccessible(true);
		f.invoke(getHandle.invoke(entity),nbt);
		return this;
	}

}
