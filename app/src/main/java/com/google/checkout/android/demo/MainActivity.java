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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.wallet.CreateWalletObjectsRequest;
import com.google.android.gms.wallet.GiftCardWalletObject;
import com.google.android.gms.wallet.LoyaltyWalletObject;
import com.google.android.gms.wallet.OfferWalletObject;
//import com.google.android.gms.wallet.GiftCardWalletObject;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;
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


public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    public static final int SAVE_TO_WALLET = 888;

    public static final String ISSUER_ID = "2967745143867465930";

    public static final Scope WOB = new Scope("https://www.googleapis.com/auth/wallet_object.issuer");

    private GoogleApiClient googleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        googleApiClient = createGoogleApiClient();

    }


    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }


    private GoogleApiClient createGoogleApiClient() {
        return new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wallet.API, new Wallet.WalletOptions.Builder()
                        .setEnvironment(WalletConstants.ENVIRONMENT_SANDBOX)
                        .setTheme(WalletConstants.THEME_HOLO_DARK).build()).build();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection has been interrupted.
        // Disable any UI components that depend on Google APIs
        // until onConnected() is called.
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
    }

    public void saveToWallet(View view) {

//        LoyaltyWalletObject wob = generateLoyaltyWalletObject();

//        GiftCardWalletObject wob = generateGiftCardWalletObject();

        OfferWalletObject wob = generateOfferWalletObject();

        CreateWalletObjectsRequest request = new CreateWalletObjectsRequest(wob);
        request.toString();
        Wallet.WalletObjects.createWalletObjects(googleApiClient, request, SAVE_TO_WALLET);

    }

    //gradle installRelease
    //setup signature. create signing config.
    //production keystore

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        EditText textBox = (EditText) findViewById(R.id.s2wResponse);

        switch (requestCode) {
            case SAVE_TO_WALLET:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        textBox.setText("saved");
                        break;
                    case Activity.RESULT_CANCELED:
                        textBox.setText("canceled");
                        break;
                    default:
                        int errorCode =
                                data.getIntExtra(
                                        WalletConstants.EXTRA_ERROR_CODE, -1);
                        textBox.setText("failed error code: " + errorCode);
                        break;
                }
        }
    }

    public LoyaltyWalletObject generateLoyaltyWalletObject() {

        /* new */
        //2967745143867465930

        // Define Points
        LoyaltyPoints points = LoyaltyPoints.newBuilder()
                .setLabel("Points")
                .setType("points")
                .setBalance(LoyaltyPointsBalance.newBuilder().setString("500").build()).build();

        // Define Text Module Data
        List textModulesData = new ArrayList();
        TextModuleData textModuleData = new TextModuleData("Jane's Baconrista Rewards", "Save more at your local Mountain View store Jane.  You get 1 bacon fat latte for every 5 coffees purchased.  Also just for you, 10% off all pastries in the Mountain View store.");
        textModulesData.add(textModuleData);

        // Define Links Module Data
        List uris = new ArrayList();
        UriData uri1 = new UriData("http://www.baconrista.com/myaccount?id=1234567890", "My Baconrista Account");
        uris.add(uri1);

        List imageUris = new ArrayList();
        // If imageURI is not accessible, 404 occurs.
        //        UriData uri2 = new UriData("http://examplesite/images/exampleimage2.jpg", "Image Description");
        //        imageUris.add(uri2);

        // Define Info Module
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

        // Define general messages
        List messages = new ArrayList();
        WalletObjectMessage message = WalletObjectMessage.newBuilder()
                .setHeader("Hi Jane!")
                .setBody("Thanks for joining our program. Show this message to " +
                        "our barista for your first free coffee on us!")
                        // If imageURI is not accessible, 404 occurs.
//                        .setImageUri(
//                                new UriData("http://examplesite/images/exampleimage1.jpg",""))
                .setActionUri(new UriData("http://baconrista.com", ""))
                .build();
        messages.add(message);

        // Define Geolocations

        LatLng location = new LatLng(37.422601, -122.085286);

        List locations = new ArrayList();
        locations.add(location);

        LoyaltyWalletObject wob = LoyaltyWalletObject
                .newBuilder()
                .setClassId("2967745143867465930.01_LoyaltyClass")
                .setId("2967745143867465930.01_LoyaltyObjectId_11")
                .setState(WalletObjectsConstants.State.ACTIVE)
                .setAccountId("1234567890")
                .setAccountName("Jane Doe")
                .setIssuerName("Baconrista")
                .setProgramName("Baconrista Rewards")
                .setBarcodeType("qrCode")
                .setBarcodeValue("28343E3")
                .setBarcodeAlternateText("12345")
                .setBarcodeLabel("User Id")
                .setLoyaltyPoints(points)
                .addTextModulesData(textModulesData)
                .addLinksModuleDataUris(uris)
                .setInfoModuleDataHexFontColor("#F8EDC1")
                .setInfoModuleDataHexBackgroundColor("#442905")
                .setInfoModuleDataShowLastUpdateTime(true)
                .addInfoModuleDataLabelValueRows(rows)
                .addImageModuleDataMainImageUris(imageUris)
                .addMessages(messages)
                .addLocations(locations)
                .build();

        wob = LoyaltyWalletObject.newBuilder()
                .setClassId("2967745143867465930.01_LoyaltyClass")
                .setId("2967745143867465930.01_LoyaltyObjectId_11")
                .setState(WalletObjectsConstants.State.ACTIVE)
                .setAccountId("1234567890")
                .setIssuerName("Baconrista")
                .setProgramName("Baconrista Rewards")
                .build();


        return wob;


    }

    public GiftCardWalletObject generateGiftCardWalletObject() {


        // Define Messages Data
        List messages = new ArrayList();
        WalletObjectMessage message = WalletObjectMessage.newBuilder()
                .setHeader("Hi Jane!")
                .setBody("Thanks for saving your gift card.")
                .setActionUri(new UriData("http://baconrista.com", ""))
                .build();
        messages.add(message);

        // Define Links Module Data
        List linkModulesData = new ArrayList();
        UriData uri1 = new UriData("http://www.baconrista.com/mybalance?id=1234567890", "My Baconrista Gift Card Purchases");
        linkModulesData.add(uri1);

        // Define Text Module Data
        List textModulesData = new ArrayList();
        TextModuleData textModuleData = new TextModuleData("Jane's Baconrista Gift Card", "Use your gift card when you purchase your next bacon fat latte!");
        textModulesData.add(textModuleData);

        // Build and Return New Gift Card Object
        return GiftCardWalletObject.newBuilder()
                .setId("2967745143867465930.GiftCardObjectBarcodeTextTest4")
                .setClassId("2967745143867465930.GiftCardClass1501161237")
                .setState(WalletObjectsConstants.State.ACTIVE)
                .setCardNumber("1234567890")
                .setPin("1234")
                .setTitle("This is my GiftCard")
                .setBalanceMicros(1000000)
                .setBalanceCurrencyCode("USD")
                .setBalanceUpdateTime(1425943338)
                .setEventNumber("1")
                .setCardIdentifier("Identifier")
                .setIssuerName("Baconrista")
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
                .setId("2967745143867465930.OfferObjectActiv8")
                .setClassId("2967745143867465930.OfferClassTest")
                .setBarcodeAlternateText("378233762830")
                .setBarcodeLabel("Promotion Code")
                .setBarcodeType("qrCode")
                .setBarcodeValue("378233762830")
                .setState(WalletObjectsConstants.State.ACTIVE)
                .setIssuerName("Baconrista")
                .setTitle("Activ8 Name")
                .build();
        String s = wob.toString();

        return wob;
    }
}