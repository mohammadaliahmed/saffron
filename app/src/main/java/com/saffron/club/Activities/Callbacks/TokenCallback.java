package com.saffron.club.Activities.Callbacks;

import com.stripe.android.model.Token;

public interface TokenCallback {
    /**
     * Error callback method.
     * @param error the error that occurred.
     */
    void onError(Exception error);

    /**
     * Success callback method.
     * @param token the {@link Token} that was found or created.
     */
    void onSuccess(Token token);

}
