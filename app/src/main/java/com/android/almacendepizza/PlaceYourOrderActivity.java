package com.android.almacendepizza;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.android.almacendepizza.adapters.PlaceYourOrderAdapter;
import com.android.almacendepizza.model.Menu;
import com.android.almacendepizza.model.RestaurantModel;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

public class PlaceYourOrderActivity extends AppCompatActivity {

    private EditText inputName, inputAddress, inputCity, inputState, inputZip,inputCardNumber, inputCardExpiry, inputCardPin ;
    private RecyclerView cartItemsRecyclerView;
    private TextView tvSubtotalAmount, tvDeliveryChargeAmount, tvDeliveryCharge, tvTotalAmount, buttonPlaceYourOrder;
    private SwitchCompat switchDelivery;
    private boolean isDeliveryOn;
    private PlaceYourOrderAdapter placeYourOrderAdapter;
    private static final int SCAN_RESULT = 100;
    private TextView fecha;
    private TextView tarjeta;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_your_order);

        init();
        RestaurantModel restaurantModel = getIntent().getParcelableExtra("RestaurantModel");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(restaurantModel.getName());
        actionBar.setSubtitle(restaurantModel.getAddress());
        actionBar.setDisplayHomeAsUpEnabled(true);

        tarjeta = (TextView) findViewById(R.id.tarjeta);
        fecha = (TextView) findViewById(R.id.Fecha);
        inputName = findViewById(R.id.inputName);
        inputAddress = findViewById(R.id.inputAddress);
        inputCity = findViewById(R.id.inputCity);
        inputState = findViewById(R.id.inputState);
        inputZip = findViewById(R.id.inputZip);
        inputCardPin = findViewById(R.id.inputCardPin);
        tvSubtotalAmount = findViewById(R.id.tvSubtotalAmount);
        tvDeliveryChargeAmount = findViewById(R.id.tvDeliveryChargeAmount);
        tvDeliveryCharge = findViewById(R.id.tvDeliveryCharge);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        buttonPlaceYourOrder = findViewById(R.id.buttonPlaceYourOrder);
        switchDelivery = findViewById(R.id.switchDelivery);

        cartItemsRecyclerView = findViewById(R.id.cartItemsRecyclerView);

        buttonPlaceYourOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlaceOrderButtonClick(restaurantModel);
            }
        });

        switchDelivery.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    inputAddress.setVisibility(View.VISIBLE);
                    inputCity.setVisibility(View.VISIBLE);
                    inputState.setVisibility(View.VISIBLE);
                    inputZip.setVisibility(View.VISIBLE);
                    tvDeliveryChargeAmount.setVisibility(View.VISIBLE);
                    tvDeliveryCharge.setVisibility(View.VISIBLE);
                    isDeliveryOn = true;
                    calculateTotalAmount(restaurantModel);
                } else {
                    inputAddress.setVisibility(View.GONE);
                    inputCity.setVisibility(View.GONE);
                    inputState.setVisibility(View.GONE);
                    inputZip.setVisibility(View.GONE);
                    tvDeliveryChargeAmount.setVisibility(View.GONE);
                    tvDeliveryCharge.setVisibility(View.GONE);
                    isDeliveryOn = false;
                    calculateTotalAmount(restaurantModel);
                }
            }
        });
        initRecyclerView(restaurantModel);
        calculateTotalAmount(restaurantModel);
    }
    public void scanearTarjeta(View view) {
        Intent intent = new Intent(this, CardIOActivity.class);
        intent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY,true);
        intent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV,false);
        intent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE,false);
        startActivityForResult(intent,SCAN_RESULT);
    }
    private void init(){
        tarjeta = (TextView) findViewById(R.id.tarjeta);
        fecha = (TextView) findViewById(R.id.Fecha);
                        }

            @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SCAN_RESULT){
            if(data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT))
            {
                CreditCard scanresult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);
                tarjeta.setText(scanresult.getRedactedCardNumber());

                if(scanresult.isExpiryValid()){
                    String mes = String.valueOf(scanresult.expiryMonth);
                    String an = String.valueOf(scanresult.expiryYear);
                    fecha.setText(mes + "/"+an);

                }
            }
        }
                if(requestCode == 1000) {
                setResult(Activity.RESULT_OK);
                finish();
                }
                super.onActivityResult(requestCode, resultCode, data);
            }



    private void calculateTotalAmount(RestaurantModel restaurantModel) {
        float subTotalAmount = 0f;

        for(Menu m : restaurantModel.getMenus()) {
            subTotalAmount += m.getPrice() * m.getTotalInCart();
        }

        tvSubtotalAmount.setText("$"+String.format("%.2f", subTotalAmount));
        if(isDeliveryOn) {
            tvDeliveryChargeAmount.setText("$"+String.format("%.2f", restaurantModel.getDelivery_charge()));
            subTotalAmount += restaurantModel.getDelivery_charge();
        }
        tvTotalAmount.setText("$"+String.format("%.2f", subTotalAmount));
    }

    private void onPlaceOrderButtonClick(RestaurantModel restaurantModel) {
        if(TextUtils.isEmpty(inputName.getText().toString())) {
            inputName.setError("Por favor ingresa un nombre ");
            return;
        } else if(isDeliveryOn && TextUtils.isEmpty(inputAddress.getText().toString())) {
            inputAddress.setError("Por favor ingresa una direccion ");
            return;
        }else if(isDeliveryOn && TextUtils.isEmpty(inputCity.getText().toString())) {
            inputCity.setError("Por favor ingresa tu ciudad ");
            return;
        }else if(isDeliveryOn && TextUtils.isEmpty(inputState.getText().toString())) {
            inputState.setError("Por favor ingresa tu codigo postal ");
            return;
        }else if( TextUtils.isEmpty(inputCardPin.getText().toString())) {
            inputCardPin.setError("Por favor ingresa el cvv ");
            return;
        }
        //start success activity..
        Intent i = new Intent(PlaceYourOrderActivity.this, OrderSucceessActivity.class);
        i.putExtra("RestaurantModel", restaurantModel);
        startActivityForResult(i, 1000);
    }

    private void initRecyclerView(RestaurantModel restaurantModel) {
        cartItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        placeYourOrderAdapter = new PlaceYourOrderAdapter(restaurantModel.getMenus());
        cartItemsRecyclerView.setAdapter(placeYourOrderAdapter);
    }

    //@Override
    //protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        //if(requestCode == 1000) {
            //setResult(Activity.RESULT_OK);
            //finish();
        //}
        //super.onActivityResult(requestCode, resultCode, data);
    //}

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home :
                finish();
            default:
                //do nothing
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        finish();
    }
}