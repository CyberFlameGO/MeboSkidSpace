package secondlife.network.practice.utilties;

public interface TtlHandler<E> {

	void onExpire(E element);

	long getTimestamp(E element);

}
