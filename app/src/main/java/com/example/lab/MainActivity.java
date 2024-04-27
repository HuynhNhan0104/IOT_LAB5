package com.example.lab;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.Charset;


public class MainActivity extends AppCompatActivity {
    MQTTHelper mqqtHelper;
    TextView txtTemp , txtHumi;
    LabeledSwitch pumpButton, ledButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Log.d("creating MQTT", "starting");
        createMQTTHelper();
        txtTemp = findViewById(R.id.textTemp);
        txtHumi = findViewById(R.id.textHumi);
        pumpButton = findViewById(R.id.buttonPump);
        ledButton = findViewById(R.id.buttonLed);
        pumpButton.setOnToggledListener(new OnToggledListener(){
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if(isOn){
                    sendDataMQTT("NhanHuynh/feeds/pump","1");
                }
                else{
                    sendDataMQTT("NhanHuynh/feeds/pump","0");
                }
            }
        });

        ledButton.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if(isOn){
                    sendDataMQTT("NhanHuynh/feeds/led","1");
                }
                else{
                    sendDataMQTT("NhanHuynh/feeds/led","0");
                }
            }
        });


    }
    public void sendDataMQTT(String topic, String value){
        MqttMessage msg = new MqttMessage();
        msg.setId(1234);
        msg.setQos(0);
        msg.setRetained(false);

        byte[] b = value.getBytes(Charset.forName("UTF-8"));
        msg.setPayload(b);

        try {
            mqqtHelper.mqttAndroidClient.publish(topic, msg);
        }catch (MqttException e){
        }
    }

    public void createMQTTHelper(){
        mqqtHelper = new MQTTHelper(this);
        mqqtHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("TEST", topic + " : " + message.toString());
                if(topic.contains("temp")){
                    txtTemp.setText(message.toString() + " C");
                }
                if (topic.contains("humi")){
                    txtHumi.setText(message.toString() + " %");

                }
                if (topic.contains("led")){
                    if(message.toString().equals("1")){
                        ledButton.setOn(true);
                    }
                    else {
                        ledButton.setOn(false);
                    }
                }
                if (topic.contains("pump")){
                    if(message.toString().equals("1")){
                        pumpButton.setOn(true);
                    }
                    else {
                        pumpButton.setOn(false);
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }



}