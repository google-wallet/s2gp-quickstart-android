/*
 * Copyright Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.checkout.android.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.wallet.AutoResolvableVoidResult;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.CreateWalletObjectsRequest;
import com.google.android.gms.wallet.LoyaltyWalletObject;
import com.google.android.gms.wallet.OfferWalletObject;
import com.google.android.gms.wallet.GiftCardWalletObject;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;
import com.google.android.gms.wallet.WalletObjectsClient;
import com.google.android.gms.wallet.wobs.LabelValue;
import com.google.android.gms.wallet.wobs.LabelValueRow;
import com.google.android.gms.wallet.wobs.LoyaltyPoints;
import com.google.android.gms.wallet.wobs.LoyaltyPointsBalance;
import com.google.android.gms.wallet.wobs.TextModuleData;
import com.google.android.gms.wallet.wobs.UriData;
import com.google.android.gms.wallet.wobs.WalletObjectMessage;
import com.google.android.gms.wallet.wobs.WalletObjectsConstants;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity
        implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MainActivity";
    public static final int SAVE_TO_ANDROID = 888;

    private String ISSUER_ID;
    private String LOYALTY_CLASS_ID;
    private String LOYALTY_OBJECT_ID;
    private String GIFTCARD_CLASS_ID;
    private String GIFTCARD_OBJECT_ID;
    private String OFFER_CLASS_ID;
    private String OFFER_OBJECT_ID;
    private String SUCCESS_RESPONSE_TEXT;
    private String CANCELED_RESPONSE_TEXT;
    private String ERROR_PREFIX_TEXT;

    public static final Scope WOB =
            new Scope("https://www.googleapis.com/auth/wallet_object.issuer");

    private WalletObjectsClient walletObjectsClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ISSUER_ID = getResources().getString(R.string.ISSUER_ID);
        LOYALTY_CLASS_ID = getResources().getString(R.string.LOYALTY_CLASS_ID);
        LOYALTY_OBJECT_ID= getResources().getString(R.string.LOYALTY_OBJECT_ID);
        GIFTCARD_CLASS_ID= getResources().getString(R.string.GIFTCARD_CLASS_ID);
        GIFTCARD_OBJECT_ID= getResources().getString(R.string.GIFTCARD_OBJECT_ID);
        OFFER_CLASS_ID= getResources().getString(R.string.OFFER_CLASS_ID);
        OFFER_OBJECT_ID= getResources().getString(R.string.OFFER_OBJECT_ID);
        SUCCESS_RESPONSE_TEXT= getResources().getString(R.string.SUCCESS_RESPONSE_TEXT);
        CANCELED_RESPONSE_TEXT= getResources().getString(R.string.CANCELED_RESPONSE_TEXT);
        ERROR_PREFIX_TEXT = getResources().getString(R.string.ERROR_PREFIX_TEXT);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.w(TAG, "onConnectionFailed: " + result);
    }

    public void saveToAndroid(View view) {
        LoyaltyWalletObject wob = generateLoyaltyWalletObject();
        CreateWalletObjectsRequest request = new CreateWalletObjectsRequest(wob);
        Wallet.WalletOptions walletOptions = new Wallet.WalletOptions.Builder()
                .setTheme(WalletConstants.THEME_LIGHT)
                .setEnvironment(WalletConstants.ENVIRONMENT_PRODUCTION)
                .build();

        walletObjectsClient = Wallet.getWalletObjectsClient(this, walletOptions);
        Task<AutoResolvableVoidResult> task = walletObjectsClient.createWalletObjects(request);
        AutoResolveHelper.resolveTask(task, this, SAVE_TO_ANDROID);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        EditText textBox = (EditText) findViewById(R.id.s2wResponse);
        switch (requestCode) {
            case SAVE_TO_ANDROID:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        textBox.setText(SUCCESS_RESPONSE_TEXT);
                        break;
                    case Activity.RESULT_CANCELED:
                        textBox.setText(CANCELED_RESPONSE_TEXT);
                        break;
                    default:
                        int errorCode =
                                data.getIntExtra(
                                        WalletConstants.EXTRA_ERROR_CODE, -1);
                        textBox.setText(ERROR_PREFIX_TEXT + errorCode);
                        break;
                }
        }
    }

    public LoyaltyWalletObject generateLoyaltyWalletObject() {
        LoyaltyPoints points = LoyaltyPoints.newBuilder()
                .setLabel("Points")
                .setType("points")
                .setBalance(LoyaltyPointsBalance.newBuilder().setString("500").build()).build();
        List textModulesData = new ArrayList();
        TextModuleData textModuleData = new TextModuleData("Jane's Baconrista Rewards",
                "Save more at your local Mountain View store Jane." +
                        " You get 1 bacon fat latte for every 5 coffees purchased." +
                        " Also just for you, 10% off all pastries in the Mountain View store.");
        textModulesData.add(textModuleData);
        List uris = new ArrayList();
        UriData uri1 = new UriData("http://www.baconrista.com/myaccount?id=1234567890",
                "My Baconrista Account");
        uris.add(uri1);
        List imageUris = new ArrayList();
        List row0cols = new ArrayList();
        LabelValue row0col0 = new LabelValue("Next Reward in", "2 coffees");
        LabelValue row0col1 = new LabelValue("Member Since", "01/15/2013");
        row0cols.add(row0col0);
        row0cols.add(row0col1);
        List row1cols = new ArrayList();
        LabelValue row1col0 = new LabelValue("Local Store", "Mountain View");
        row1cols.add(row1col0);
        List rows = new ArrayList();
        LabelValueRow row0 = LabelValueRow.newBuilder().setHexBackgroundColor("#922635")
                .setHexFontColor("#F8EDC1").addColumns(row0cols).build();
        LabelValueRow row1 = LabelValueRow.newBuilder().setHexBackgroundColor("#922635")
                .setHexFontColor("#F8EDC1").addColumns(row1cols).build();
        rows.add(row0);
        rows.add(row1);
        List messages = new ArrayList();
        WalletObjectMessage message = WalletObjectMessage.newBuilder()
                .setHeader("Hi Jane!")
                .setBody("Thanks for joining our program. Show this message to " +
                        "our barista for your first free coffee on us!")
                .setActionUri(new UriData("http://baconrista.com", ""))
                .build();
        messages.add(message);
        LatLng location = new LatLng(37.422601, -122.085286);
        List locations = new ArrayList();
        locations.add(location);
        LoyaltyWalletObject wob = LoyaltyWalletObject.newBuilder()
                .setClassId(ISSUER_ID +"." + LOYALTY_CLASS_ID)
                .setId(ISSUER_ID +"." + LOYALTY_OBJECT_ID)
                .setState(WalletObjectsConstants.State.ACTIVE)
                .setAccountId("1234567890")
                .setIssuerName("Baconrista")
                .setProgramName("Baconrista Rewards")
                .build();
        return wob;
    }

    public GiftCardWalletObject generateGiftCardWalletObject() {
        List messages = new ArrayList();
        WalletObjectMessage message = WalletObjectMessage.newBuilder()
                .setHeader("Hi Jane!")
                .setBody("Thanks for saving your gift card.")
                .setActionUri(new UriData("http://baconrista.com", ""))
                .build();
        messages.add(message);
        List linkModulesData = new ArrayList();
        UriData uri1 = new UriData("http://www.baconrista.com/mybalance?id=1234567890",
                "My Baconrista Gift Card Purchases");
        linkModulesData.add(uri1);
        List textModulesData = new ArrayList();
        TextModuleData textModuleData = new TextModuleData("Jane's Baconrista Gift Card",
                "Use your gift card when you purchase your next bacon fat latte!");
        textModulesData.add(textModuleData);
        return GiftCardWalletObject.newBuilder()
                .setId(ISSUER_ID + "." + GIFTCARD_OBJECT_ID)
                .setClassId(ISSUER_ID + "." + GIFTCARD_CLASS_ID)
                .setState(WalletObjectsConstants.State.ACTIVE)
                .setCardNumber("1234567890")
                .setPin("1234")
                .setTitle("This is my GiftCard")
                .setBalanceMicros(1000000)
                .setBalanceCurrencyCode("USD")
                .setBalanceUpdateTime(1425943338)
                .setEventNumber("1")
                .setCardIdentifier("Identifier")
                .setIssuerName("Baconristafoo")
                .setBarcodeType("qrCode")
                .setBarcodeValue("28343E3")
                .setBarcodeAlternateText("12345")
                .setBarcodeLabel("User Id")
                .addMessages(messages)
                .addLinksModuleDataUris(linkModulesData)
                .addTextModulesData(textModulesData)
                .build();
    }

    public OfferWalletObject generateOfferWalletObject() {
        OfferWalletObject wob = OfferWalletObject.newBuilder()
                .setId(ISSUER_ID + "." + OFFER_OBJECT_ID)
                .setClassId(ISSUER_ID + "." + OFFER_CLASS_ID)
                .setBarcodeAlternateText("378233762830")
                .setBarcodeLabel("Promotion Code")
                .setBarcodeType("qrCode")
                .setBarcodeValue("378233762830")
                .setState(WalletObjectsConstants.State.ACTIVE)
                .setIssuerName("Baconrista")
                .setTitle("Activ8 Name")
                .build();
        return wob;
    }
}