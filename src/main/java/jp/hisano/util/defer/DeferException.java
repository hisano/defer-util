package jp.hisano.util.defer;

public final class DeferException extends RuntimeException {
	DeferException(Throwable cause) {
		super(cause);
	}
}
