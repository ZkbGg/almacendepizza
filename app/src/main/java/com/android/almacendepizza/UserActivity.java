package com.android.almacendepizza;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UserActivity extends AppCompatActivity {

    EditText username,password,repassword;
    Button btnSignUp,btnSignIn;
    DBHelper myDB;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        repassword = (EditText) findViewById(R.id.repassword);

        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);

        try (DBHelper dbHelper = myDB = new DBHelper(this)) {
        }

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = username.getText().toString();
                String pass = password.getText().toString();
                String repass = repassword.getText().toString();

                if (user.equals("") || pass.equals("") || repass.equals("")) {
                    Toast.makeText(UserActivity.this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
                } else {
                    if (pass.equals(repass)) {
                        Boolean usercheckResult = myDB.checkusername(user);
                        if (usercheckResult == false) {
                            Boolean regResult = myDB.insertData(user, pass);
                            if (regResult == true) {
                                Toast.makeText(UserActivity.this, "Registro exitoso!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                                startActivity(intent);
                            }
                            else
                            {
                                Toast.makeText(UserActivity.this, "Registro fallido!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(UserActivity.this, "El usuario ya existe! \n Ingresa otro usuario", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(UserActivity.this, "Password no coincide.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
            }
        });
    }

}