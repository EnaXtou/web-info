package cz.plsi.webInfo.shared.dataStore.entities;

import java.util.List;

public interface EntityCommon {

	public abstract long count();

	public abstract boolean exists();

	public abstract List<? extends EntityCommon> getAll();

}