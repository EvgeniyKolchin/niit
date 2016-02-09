package com.example.evgeniy.airpoooooort;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Nearable;
import com.estimote.sdk.Region;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

	private static final String NO_BEACONS = "Подойдите к любому стенду.";
    private BeaconManager beaconManager;
    private static final String TAG = "MainActivity"; //тег для лога
    private static final UUID ESTIMOTE_PROXIMITY_UUID = UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"); //дефолтный уид наших беконов для поиска
    private static final Region ALL_ESTIMOTE_BEACONS = new Region("rid", ESTIMOTE_PROXIMITY_UUID, null, null); //он же, только в регионе
    private String findedBeaconKey = ""; //глобальная переменная, для сохранения мажор:минор текущего бекона
    private TextView tvContent; //наш ящик для вывода "Мама я нашёл бекон" и прочее
    private MyHttpClient myHttpClient;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // метод для вызова метода суперкласса
        setContentView(R.layout.activity_main); // хз, скорее всего для установки что это активити главная НАДО ПОЧИТАТЬ!
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); // создаём новый тулбар, та фиговина с сообщением. Можешь удалить если захочешь.
        setSupportActionBar(toolbar); // то же про тул бар фиговина
        image = (ImageView) findViewById(R.id.image);
        tvContent = (TextView) findViewById(R.id.tvContent); // создаём новый ящик для вывода "мама я нашёл бекон"
        tvContent.setText(NO_BEACONS); // Устанавливаем дефолтное значения

        myHttpClient = new MyHttpClient();
        myHttpClient.setTv(tvContent);
        myHttpClient.setImage(image);

        beaconManager = new BeaconManager(getApplicationContext()); // создаём новый менеджер беконов

        beaconManager.setRangingListener(new BeaconManager.RangingListener() { // создаём и устанавливаеим новый рейндж листнер который:
            @Override
            public void onBeaconsDiscovered(Region region, final List beacons) { // на каждый тик делает:
                if (!beacons.isEmpty()) {  // если он нашёл хоть 1 бекон, то лист не будет пустым
					Beacon nearestBeacon = (Beacon) beacons.get(0); // устанавлиаем первый бекон, как с самым сильным сигналом
					for(int i = 1; i < beacons.size(); i++) { // если есть другие беконы, то:
						if (nearestBeacon.getRssi() < ((Beacon)beacons.get(i)).getRssi()) { // getRssi() - Received Signal Strength Indication at the moment of scanning. если другой бекон с сигналом сильнее нашего, то:
							nearestBeacon = (Beacon) beacons.get(i);  //  текущий сигнал становиться "нашим" самым сильным
						}
					}
                    placesNearBeacon(nearestBeacon ); // вызываем нашу функцию, которая установит ID бекона. НАДО ПЕРЕПИСАТЬ ФУНКЦИЮ!
                } else { // если всё же лист пуст, то он не нашёл беконов:
                    noBeacon(); // вызовем  нашу функцию, которая будет писать на главный экран, что беконов нет
                }
            }
        });

        beaconManager.setNearableListener(new BeaconManager.NearableListener() { // листнер, который ищет близлежайщие беконы. Так у нас и не заработала
            @Override
            public void onNearablesDiscovered(List nearables) {
                Log.d(TAG, "Discovered nearables: " + nearables);
            }
        });


        beaconManager.connect(new BeaconManager.ServiceReadyCallback() { // делаем конекшен:
            @Override public void onServiceReady() { // как будет сервис беконов готов, то:
                beaconManager.startRanging(ALL_ESTIMOTE_BEACONS); // запускаем поиск по дефолтным беконам. Если много ID беконов, скорее всего надо писать ещё beaconManager.startRanging(RANGE_BEACONS); с другим рейнджем
                beaconManager.startNearableDiscovery(); // запускаем поиск близлежайщих.
            }
        });
    }

    private void noBeacon() {
        tvContent.setText(NO_BEACONS); // выводим, что нет беконов.
    }

    private void placesNearBeacon(Beacon beacon) { // НАДО ПЕРЕПИСАТЬ ФУНКЦИЮ! Она не отправляет HTTP, а должна отправлять!
        String beaconKey = String.format("%d:%d", beacon.getMajor(), beacon.getMinor()); // делает локальный ключ "сильнейшего" бекона
        if(beaconKey.equals(findedBeaconKey)) { // если он равен глобальному, то это один и тот же бекон
            return;
        }
        findedBeaconKey = beaconKey; // наш сильнейший бекон становиться глобальным
        //tvContent.setText("beaconkey=" + beaconKey); // выводим ТЕСТОВУЮ информацию. Надо заменить на ответ от сервера
        Log.d(TAG, "beaconKey=" + beaconKey); // логируем, ключ бекона
        Log.d(TAG, "beacon=" + beacon.toString()); // логируем сам бекон
        myHttpClient.getRequest(beaconKey);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // не знаю и не хочу разбираться, скорее всего как-то с тулбаром свящзаня, я хз
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // не знаю и не хочу разбираться, скорее всего как-то с тулбаром свящзаня, я хз
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
}
