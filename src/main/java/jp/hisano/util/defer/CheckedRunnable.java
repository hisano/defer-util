package jp.hisano.util.defer;

public interface CheckedRunnable {
	void run() throws Exception;
}