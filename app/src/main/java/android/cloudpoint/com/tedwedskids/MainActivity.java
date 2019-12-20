package android.cloudpoint.com.tedwedskids;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    Button sendMessages, customSmsBtn;
    EditText customSmsTxt;
    final String phoneNo = "+26774319137";
    final String textMessage = "Thank You for celebrating our wedding with us";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }

        // initialise objects
        customSmsTxt = findViewById(R.id.customSmsTxt);
        customSmsBtn = findViewById(R.id.customSmsBtn);
        sendMessages = findViewById(R.id.sendMessages);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(Api.baseUrl).addConverterFactory(GsonConverterFactory.create()).build();

        Api api = retrofit.create(Api.class);

        final Call<List<Guest>> call = api.getGuests();

        // Sending the Hard coded Thank you Messages
        sendMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queueCall(call);
            }
        }); // sendMessages

        customSmsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = customSmsTxt.getText().toString();
                sendCustomMessage(call, text);
                customSmsTxt.setText("");
                Toast.makeText(getApplicationContext(), "Sent!", Toast.LENGTH_LONG).show();
            }
        });

    } //onCreate

    private void sendCustomMessage(Call<List<Guest>> call, final String text) {
        call.enqueue(new Callback<List<Guest>>() {
            @Override
            public void onResponse(Call<List<Guest>> call, Response<List<Guest>> response) {
                List<Guest> guests = response.body();

                for (Guest guest : guests) {
                    if (guest.getName() != null && guest.getSurname() != null && guest.getNumber() != null) {
                        Log.d("Debugging: ", "Getting Data From the API");

                        String message = "Hi, " + guest.getName() + "." + text;
                        sendTextMessage(message, guest.getNumber());
                        Toast.makeText(getApplicationContext(), "Sending...", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Guest>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                System.out.println(".............." + t.getMessage());
            }
        });
    }

    private void queueCall(Call<List<Guest>> call) {
        call.enqueue(new Callback<List<Guest>>() {
            @Override
            public void onResponse(Call<List<Guest>> call, Response<List<Guest>> response) {
                List<Guest> guests = response.body();

                for (Guest guest : guests) {
                    if (guest.getName() != null && guest.getSurname() != null && guest.getNumber() != null) {
                        Log.d("Debugging: ", "Getting Data From the API");

                        createMessage(guest);
                    } else {
                        Log.d("Number: ", "Response is empty");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Guest>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                System.out.println(".............." + t.getMessage());
            }
        });
    }

    private void createMessage(Guest guest) {
        final String message = "Hi, " + guest.getName() + ". Thank You for celebrating our wedding with us.";
        final String number = guest.getNumber();
        sendTextMessage(message, number);
    }

    protected void sendTextMessage(String message, String number) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(number, null, message, null, null);
        Toast.makeText(this, "Sent!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "SMS Permissions Granted.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "SMS Permissions Denied.", Toast.LENGTH_LONG).show();
                }
            }
        }
    } //onRequestPermissionsResult
}
