package team.unstudio.udpl.core.nms.v1_11_R1;

import java.util.Map;

import org.bukkit.craftbukkit.v1_11_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;

import net.minecraft.server.v1_11_R1.NBTTagCompound;
import team.unstudio.udpl.api.nms.NMSManager;

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
		return NMSManager.getNMSNBT().toMap(((CraftEntity)entity).getHandle().e(new NBTTagCompound()));
	}

	@Override
	public team.unstudio.udpl.api.nms.NMSEntity setNBT(Map<String, Object> map) throws Exception {
		((CraftEntity)entity).getHandle().f((NBTTagCompound)NMSManager.getNMSNBT().toNBT(map));
		return this;
	}

}
