package com.saffron.club.Activities.CartManagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.saffron.club.R;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;

import org.jetbrains.annotations.NotNull;

import androidx.appcompat.app.AppCompatActivity;

public class Pay extends AppCompatActivity {
    private String cvc;
    private String cardName;
    private String cardExpYear;
    private String cardExpMonth;
    private String cardNumber;
    private TextView amount;
    private CardInputWidget mCardInputWidget;
    private static PayPalConfiguration config;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        config = new PayPalConfiguration()

                // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
                // or live (ENVIRONMENT_PRODUCTION)
                .environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK)

                .clientId("AcLW5HGkgFvX5uHcqFU1QBLw-SVUAYhjlYX8c95J-vpTEdPapd1W3Jx0");

        mCardInputWidget = findViewById(R.id.card_input_widget);
        amount = findViewById(R.id.amount);
        Button pay = findViewById(R.id.btn_pay);
        Intent intent = getIntent();
        amount.setText("$ "+intent.getStringExtra("amount"));

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Card cardToSave = mCardInputWidget.getCard();
                if (mCardInputWidget.getCard() != null) {
                    cvc = mCardInputWidget.getCard().getCvc();
                    cardNumber = mCardInputWidget.getCard().getNumber();
                    cardName = mCardInputWidget.getCard().getName();
                    cardExpYear = mCardInputWidget.getCard().getExpYear().toString();
                    cardExpMonth = mCardInputWidget.getCard().getExpMonth().toString();

                }


                if (cardToSave == null) {
//                Toast.makeText(this@MainActivity, "Invalid Card Data!", Toast.LENGTH_SHORT).show()
                    Toast.makeText(getApplicationContext(), "Enter Card Info", Toast.LENGTH_SHORT).show();
                } else {
                    Stripe stripe = new Stripe(getApplicationContext(), "pk_test_rgOrs6HLfdfkb6qR8xSrGvfI00Nv21Q0KG");
                    stripe.createCardToken(cardToSave, new ApiResultCallback<Token>() {
                        @Override
                        public void onSuccess(Token token) {
                            Toast.makeText(getApplicationContext(), "Card Authenticated", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(@NotNull Exception e) {
                            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });
    }
}