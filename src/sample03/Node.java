package sample03;

import java.util.List;

public interface Node<T, E> {
	public List<E> getChildren();

	public T getParent();

	public boolean hasChildren();

	public String getLabel();
}
