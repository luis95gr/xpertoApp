package com.example.luis9.xpertp;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.luis9.xpertp.Helo.conexionHeloBluetooth;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.worldgn.connector.Connector;
import com.worldgn.connector.DeviceItem;
import com.worldgn.connector.ScanCallBack;


import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Timer;

public class conduccion extends FragmentActivity implements OnMapReadyCallback,TextToSpeech.OnInitListener, ScanCallBack {

    //VARIABLES
    SupportMapFragment mapFragment;
    SharedPreferences spLogin;
    private GoogleMap mMap;
    Chronometer chronometer;
    ProgressBar progressBar;
    LocationManager locationManager,locationManager2;
    TextView textMidiendo,textUltimaMedicion,textBle,textDis;
    Date date;
    SimpleDateFormat sdfHour;
    SimpleDateFormat sdfDate;
    String stringDate,stringHour;
    LinearLayout linearLayoutIniciar,linearLayoutPausar,linearLayoutReanudar;
    boolean booleanStart = true,booleanChronometer,booleanSonido = true;
    CountDownTimer countDownTimer, countDownTimerHr,countDownTimerBp,countDownTimerBr,countDownTimerMF;
    String stringEmail,stringPass,stringID;
    int conduccion = 1;
    String steps;
    PowerManager powerManager;
    PowerManager.WakeLock wakeLock;
    WifiManager wifiManager;
    WifiManager.WifiLock wifiLock;
    TextToSpeech mTts;
    Vibrator vibrator;
    private long timeWhenStopped = 0;
    Handler handlerBp,handlerBr,handlerHr,handlerMood,handlerFatiga,handlerReconect,handlerApagar,handlerVolverMedir;
    DeviceItem deviceItem = null;
    int intCount;
    BluetoothAdapter bluetoothAdapter;
    //
    //VELOCIDAD VARIABLES
    static final int REQUEST_LOCATION = 1;
    LocationListener locationListener,locationListener2;
    double speed;
    String stringSpeed = "0";
    DecimalFormat decimalFormat = new DecimalFormat("##");
    //DIAGNOSTICO VARIABLES
    double doubleMaxHr,doubleMinHr;
    String stringDiagnosticHr,stringDiagnosticBr,stringDiagnosticBp;
    int intNormalBrmin,intNormalBrmax,intNormalBpmax,intNormalBpmin;
    //HELO VARIABLES
    IntentFilter intentFilter;
    MeasurementReceiver heloMeasurementReceiver;
    //
    String ip = "meddataa.sytes.net/registrovar/index.php?";
    //
    //VARIABLES DE INTERNET
    boolean booleanBpMeasure, booleanBrMeasure,booleanMoodMeasure,booleanFatigueMeasure,booleanHrMeasure= false;
    int contBp, contBr, contMood, contFatigue,contHr = 0;
    String stringBpmaxSaved,stringBpminSaved,stringBrSaved,stringMoodSaved,stringFatigueSaved,stringHrSaved;
    String stringDateBpSaved, stringDateBrSaved, stringDateMoodSaved, stringDateFatigueSaved,stringDateHrSaved;
    String stringHourBpSaved, stringHourBrSaved, stringHourMoodSaved, stringHourFatigueSaved,stringHourHrSaved;
    String max,min,br,fatigue,hr,mood;
    SharedPreferences spMeasuresSaved;
    Intent intentService;
    boolean booleanServiceStarted;
    Snackbar snackbar;
    NotificationManager notificationManager;
    //FINALIZAR VIAJE VARIABLES
    ArrayList<Integer> intPBr,intPHr,intPBpmax,intPBpmin;
    int elementBr,elementHr,elementBpmax,elementBpmin = 0;
    ArrayList<String> stringPFatigue,stringPMood;
    ArrayList<Double> doublePSpeed;
    double elementSpeed = 0;
    StringTokenizer stringTokenizer;
    String tokenH,tokenM,tokenS,tiempo;
    double promedioBr,promedioHr,promedioBpmax,promedioBpmin,promedioSpeed;
    int contadorFatigaCansado,contadorFatigaNormal,contadorFatigaMuyCansado,contadorMoodEmocionado,contadorMoodDeprimido,contadorMoodCalmado;
    Iterator<Integer> iteratorBr,iteratorHr,iteratorBpmax,iteratorBpmin;
    Iterator<String> iteratorFatiga,iteratorMood;
    Iterator<Double> iteratorSpeed;

    //
    public final String BROADCAST_ACTION_BP_MEASUREMENT = "com.worldgn.connector.BP_MEASUREMENT";
    public final String BROADCAST_ACTION_MEASUREMENT_WRITE_FAILURE = "com.worldgn.connector.MEASURE_WRITE_FAILURE";
    public final String BROADCAST_ACTION_HR_MEASUREMENT = "com.worldgn.connector.HR_MEASUREMENT";
    public final String BROADCAST_ACTION_BR_MEASUREMENT = "com.worldgn.connector.BR_MEASUREMENT";
    public final String BROADCAST_ACTION_FATIGUE_MEASUREMENT = "com.worldgn.connector.FATIGUE_MEASUREMENT";
    public final String BROADCAST_ACTION_MOOD_MEASUREMENT = "com.worldgn.connector.MOOD_MEASUREMENT";
    public final String BROADCAST_ACTION_STEPS_MEASUREMENT = "com.worldgn.connector.STEPS_MEASUREMENT";
    public final String BROADCAST_ACTION_HELO_DISCONNECTED = "com.worldgn.connector.ACTION_HELO_DISCONNECTED";
    public final String BROADCAST_ACTION_HELO_CONNECTED = "com.worldgn.connector.ACTION_HELO_CONNECTED";
    public final String BROADCAST_ACTION_HELO_BONDED = "com.worldgn.connector.ACTION_HELO_BONDED";
    //
    public final String INTENT_KEY_HR_MEASUREMENT = "HR_MEASUREMENT";
    public final String INTENT_KEY_BR_MEASUREMENT = "BR_MEASUREMENT";
    public final String INTENT_KEY_BP_MEASUREMENT_MAX = "BP_MEASUREMENT_MAX";
    public final String INTENT_KEY_BP_MEASUREMENT_MIN = "BP_MEASUREMENT_MIN";
    public final String INTENT_KEY_MOOD_MEASUREMENT = "MOOD_MEASUREMENT";
    public final String INTENT_KEY_FATIGUE_MEASUREMENT = "FATIGUE_MEASUREMENT";
    public final String INTENT_MEASUREMENT_WRITE_FAILURE = "MEASUREMENT_WRITE_FAILURE";
    public  final String INTENT_KEY_STEPS_MEASUREMENT = "STEPS_MEASUREMENT";
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conduccion);
        //MENU
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);
        //
        //SHARED
        spLogin = getSharedPreferences("login", MODE_PRIVATE);
        spMeasuresSaved = PreferenceManager.getDefaultSharedPreferences(this);
        stringEmail = spLogin.getString("email",null);
        stringPass = spLogin.getString("email",null);
        stringID = spLogin.getString("id",null);
        //CAST
        chronometer = findViewById(R.id.chronometer);
        progressBar = findViewById(R.id.progressBar);
        textMidiendo = findViewById(R.id.textMidiendo);
        textUltimaMedicion = findViewById(R.id.textUltimaMedicion);
        linearLayoutIniciar = findViewById(R.id.linearIniciar);
        linearLayoutPausar = findViewById(R.id.linearPausar);
        linearLayoutReanudar = findViewById(R.id.linearReanudar);
        textBle = findViewById(R.id.textBle);
        textDis = findViewById(R.id.txtDis);
        powerManager = (PowerManager)getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"tag");
        wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL,"tag");
        mTts = new TextToSpeech(this, this);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //PROMEDIOS CAST
        intPBr = new ArrayList<>();
        intPHr = new ArrayList<>();
        intPBpmax = new ArrayList<>();
        intPBpmin = new ArrayList<>();
        stringPFatigue = new ArrayList<>();
        stringPMood = new ArrayList<>();
        doublePSpeed = new ArrayList<>();
        iteratorHr = intPHr.iterator();
        iteratorBpmax = intPBpmax.iterator();
        iteratorBpmin = intPBpmin.iterator();
        iteratorBr = intPBr.iterator();
        iteratorMood = stringPMood.iterator();
        iteratorFatiga = stringPFatigue.iterator();
        iteratorSpeed = doublePSpeed.iterator();
        //COUNTDOWN CAST
        //HANDLERS CAST
        handlerBp = new Handler();
        handlerHr = new Handler();
        handlerFatiga = new Handler();
        handlerMood = new Handler();
        handlerBr = new Handler();
        handlerReconect = new Handler();
        handlerApagar = new Handler();
        handlerVolverMedir = new Handler();
        //HELO CAST
        intentFilter = new IntentFilter();
        heloMeasurementReceiver = new MeasurementReceiver();
        intentFilter.addAction(BROADCAST_ACTION_STEPS_MEASUREMENT);
        intentFilter.addAction(BROADCAST_ACTION_BP_MEASUREMENT);
        intentFilter.addAction(BROADCAST_ACTION_BR_MEASUREMENT);
        intentFilter.addAction(BROADCAST_ACTION_FATIGUE_MEASUREMENT);
        intentFilter.addAction(BROADCAST_ACTION_MOOD_MEASUREMENT);
        intentFilter.addAction(BROADCAST_ACTION_HR_MEASUREMENT);
        intentFilter.addAction(BROADCAST_ACTION_MEASUREMENT_WRITE_FAILURE);
        intentFilter.addAction(BROADCAST_ACTION_HELO_DISCONNECTED);
        intentFilter.addAction(BROADCAST_ACTION_HELO_CONNECTED);
        intentFilter.addAction(BROADCAST_ACTION_HELO_BONDED);
        intentFilter.addAction("enviado");
        //INTERNET CAST
        //
        //CHECK CONNECTION
        Connector.getInstance().getStepsData();

        //MAPS
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //
        //VELOCIDAD
        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                location.getLatitude();
                speed = location.getSpeed()*3.6;
                stringSpeed = decimalFormat.format(location.getSpeed()*3.6);
                if (stringSpeed.equalsIgnoreCase("null")){
                    stringSpeed = "0";
                }
                textDis.setText(stringSpeed + " km/hr");
                //
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                Geocoder geocoder = new Geocoder(getApplicationContext());
                try {
                    List<Address> addresses =
                            geocoder.getFromLocation(latitude, longitude, 1);
                    String result = addresses.get(0).getLocality() + ":";
                    result += addresses.get(0).getCountryName();
                    LatLng latLng = new LatLng(latitude, longitude);
                    mMap.addMarker(new MarkerOptions().position(latLng).title(result));
                    mMap.setMaxZoomPreference(20);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f));
                } catch (IOException e){
                    e.printStackTrace();
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
        //

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(heloMeasurementReceiver, intentFilter);
        Connector.getInstance().stopStepsHRDynamicMeasurement();
    }

    public void iniciar(View view){
        wakeLock.acquire();
        wifiLock.acquire();
        booleanStart = true;
        Connector.getInstance().measureMF();
        textMidiendo.setText(R.string.FatigaSin);
        VolleyPetition("http://meddataa.sytes.net/registrovar/index.php?correo=" + stringEmail + "&usuario=" + stringID +
                "&var=" + "INICIO" + "&valor=" + "INICIO" + "&fecha=" + dates() + "&hora=" + hour() + "&velocidad=" + "INICIO"
                + "&diagnostico=" +"INICIO");
        //BUTTONS
        linearLayoutIniciar.setVisibility(View.INVISIBLE);
        linearLayoutPausar.setVisibility(View.VISIBLE);
        //
        startcountDownTimer();
        startcountDownTimerHr();
        chronometer();
    }

    public void terminar(View view){
        wakeLock.release();
        wifiLock.release();
        booleanStart = false;
        linearLayoutPausar.setVisibility(View.INVISIBLE);
        linearLayoutReanudar.setVisibility(View.INVISIBLE);
        linearLayoutIniciar.setVisibility(View.VISIBLE);
        //
        countDownTimer.cancel();
        chronometerCancel();
        //
        Toast.makeText(this, "VIAJE TERMINADO", Toast.LENGTH_SHORT).show();
        VolleyPetition("http://meddataa.sytes.net/registrovar/index.php?correo=" + stringEmail + "&usuario=" + stringID +
                "&var=" + "FIN" + "&valor=" + "FIN" + "&fecha=" + dates() + "&hora=" + hour() + "&velocidad=" + "FIN"
                + "&diagnostico=" +"FIN");
        //TIEMPO
        /*stringTokenizer = new StringTokenizer(chronometer.getText().toString(),":");
        tokenH = stringTokenizer.nextToken();
        tokenM = stringTokenizer.nextToken();
        tokenS = stringTokenizer.nextToken();*/
        tiempo = chronometer.getText().toString();
        //PROMEDIOS
        if (!doublePSpeed.isEmpty()){
            while (iteratorSpeed.hasNext()){
                elementSpeed += iteratorSpeed.next();
            }
            if (elementSpeed == 0){
                promedioSpeed = 0;
            } else promedioSpeed = elementSpeed / doublePSpeed.size();
        }

        //
        startActivity(new Intent(conduccion.this, PopUp.class));
        //
    }

    public void pausar(View view){
        linearLayoutReanudar.setVisibility(View.VISIBLE);
        linearLayoutPausar.setVisibility(View.INVISIBLE);
        booleanStart = false;
        countDownTimer.cancel();
        timeWhenStopped = chronometer.getBase() - SystemClock.elapsedRealtime();
        chronometer.stop();
        VolleyPetition("http://meddataa.sytes.net/registrovar/index.php?correo=" + stringEmail + "&usuario=" + stringID +
                "&var=" + "PAUSADO" + "&valor=" + "PAUSADO" + "&fecha=" + dates() + "&hora=" + hour() + "&velocidad=" + "PAUSADO"
                + "&diagnostico=" +"PAUSADO");
    }

    public void reanudar(View view){
        linearLayoutPausar.setVisibility(View.VISIBLE);
        linearLayoutReanudar.setVisibility(View.INVISIBLE);
        booleanStart = true;
        Connector.getInstance().measureMF();
        chronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
        chronometer.start();
        startcountDownTimer();
        startcountDownTimerHr();
        VolleyPetition("http://meddataa.sytes.net/registrovar/index.php?correo=" + stringEmail + "&usuario=" + stringID +
                "&var=" + "REANUDADO" + "&valor=" + "REANUDADO" + "&fecha=" + dates() + "&hora=" + hour() + "&velocidad=" + "REANUDADO"
                + "&diagnostico=" +"REANUDADO");
    }

    //////////////////////MEDICIONES TIMERS////////////////////////////////////////////
    public void startcountDownTimer() {
        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                progressBar.setMax(100);
                long pr = (millisUntilFinished / 1000) * 100000 / 60000;
                int progress = (int) pr;
                progressBar.setProgress(progress);
            }

            @Override
            public void onFinish() {
                startcountDownTimer();
            }
        }.start();
    }
    //COUNTDOWN MEASURES
    public void startcountDownTimerHr(){
        intCount = 1;
        countDownTimerHr = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (!booleanStart) countDownTimerHr.cancel();
            }
            @Override
            public void onFinish() {
                Connector.getInstance().measureHR();
                //TEXTS
                textMidiendo.setText(R.string.RitmoSin);
                startcountDownTimerBp();
            }
        }.start();
    }
    public void startcountDownTimerBp(){
        intCount = 2;
        countDownTimerBp = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (!booleanStart) countDownTimerBp.cancel();
            }
            @Override
            public void onFinish() {
                Connector.getInstance().measureBP();
                //TEXTS
                textMidiendo.setText(R.string.PresionSin);
                startcountDownTimerBr();
            }
        }.start();
    }
    public void startcountDownTimerBr(){
        intCount = 3;
        countDownTimerBr = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (!booleanStart) countDownTimerBr.cancel();
            }
            @Override
            public void onFinish() {
                Connector.getInstance().measureBr();
                //TEXTS
                textMidiendo.setText(R.string.RespiracionSin);
                startcountDownTimerMF();
            }
        }.start();
    }
    public void startcountDownTimerMF(){
        intCount = 4;
        countDownTimerMF = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (!booleanStart) countDownTimerMF.cancel();
            }
            @Override
            public void onFinish() {
                Connector.getInstance().measureMF();
                //TEXTS
                textMidiendo.setText(R.string.FatigaSin);
                startcountDownTimerHr();
            }
        }.start();
    }

    //


    public class MeasurementReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, final Intent intent) {
            if (intent != null && intent.getAction() != null) {
                //PRESION
                if (intent.getAction().equals(BROADCAST_ACTION_BP_MEASUREMENT)) {
                    booleanBpMeasure = true;
                    max = intent.getStringExtra(INTENT_KEY_BP_MEASUREMENT_MAX);
                    min = intent.getStringExtra(INTENT_KEY_BP_MEASUREMENT_MIN);
                    //
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textUltimaMedicion.setText("Presión: " + max + "/" + min);
                        }
                    });
                    //
                    //PROMEDIO
                    intPBpmax.add(Integer.parseInt(max));
                    intPBpmin.add(Integer.parseInt(min));
                    doublePSpeed.add(Double.parseDouble(stringSpeed));
                    //
                    //
                    if (internet()) {
                        booleanBpMeasure = false;
                        VolleyPetition("http://meddataa.sytes.net/registrovar/index.php?correo=" + stringEmail + "&usuario=" + stringID +
                                "&var=" + "BPmax" + "&valor=" + max + "&fecha=" + dates() + "&hora=" + hour() + "&velocidad=" + stringSpeed
                                + "&diagnostico=" + bpDiagnostic(Integer.parseInt(max), Integer.parseInt(min)));
                        //
                        VolleyPetition("http://meddataa.sytes.net/registrovar/index.php?correo=" + stringEmail + "&usuario=" + stringID + "&var=" +
                                "BPmin" + "&valor=" + min + "&fecha=" + dates() + "&hora=" + hour() + "&velocidad=" + stringSpeed
                                + "&diagnostico=" + bpDiagnostic(Integer.parseInt(max), Integer.parseInt(min)));
                    } else guardarDatos();
                    //SPEAK
                    if (booleanSonido) {
                        mTts.speak("Tu presion es " + bpDiagnostic(Integer.parseInt(max), Integer.parseInt(min)),
                                0, null, "bp");
                    }
                    //
                    //RESPIRACION
                } else if (intent.getAction().equals(BROADCAST_ACTION_BR_MEASUREMENT)) {
                    booleanBrMeasure = true;
                    br = intent.getStringExtra(INTENT_KEY_BR_MEASUREMENT);
                    //
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textUltimaMedicion.setText("Respiración: " + br);
                        }
                    });
                    //
                    //PROMEDIO
                    intPBr.add(Integer.parseInt(br));
                    doublePSpeed.add(Double.parseDouble(stringSpeed));
                    //
                    if (internet()) {
                        booleanBrMeasure = false;
                        VolleyPetition("http://meddataa.sytes.net/registrovar/index.php?correo=" + stringEmail + "&usuario=" + stringID + "&var=" +
                                "BR" + "&valor=" + br + "&fecha=" + dates() + "&hora=" + hour() + "&velocidad=" + stringSpeed + "&diagnostico=" +
                                brDiagnostic(Integer.parseInt(br)));
                    } else guardarDatos();
                    //SPEAK
                    if (booleanSonido) {
                        mTts.speak("Tu respiracion es " + brDiagnostic(Integer.parseInt(br)), 0, null, "br");
                    }
                    //
                    //FATIGA Y ANIMO
                } else if (intent.getAction().equals(BROADCAST_ACTION_FATIGUE_MEASUREMENT)) {
                    booleanFatigueMeasure = true;
                    fatigue = intent.getStringExtra(INTENT_KEY_FATIGUE_MEASUREMENT);
                    //
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textUltimaMedicion.setText("Fatiga: " + fatigue);
                        }
                    });
                    //
                    //PROMEDIO
                    stringPFatigue.add(fatigue);
                    doublePSpeed.add(Double.parseDouble(stringSpeed));
                    //
                    if (internet()) {
                        booleanFatigueMeasure = false;
                        VolleyPetition("http://meddataa.sytes.net/registrovar/index.php?correo=" + stringEmail + "&usuario=" + stringID +
                                "&var=" + "Fatiga" + "&valor=" + fatigue + "&fecha=" + dates() + "&hora=" + hour() + "&velocidad=" + stringSpeed
                                + "&diagnostico=" + "-");
                    } else guardarDatos();
                    //
                    //SPEAK
                    if (booleanSonido) {
                        if (fatigue.equalsIgnoreCase("tired")) {
                            mTts.speak("Tu nivel de fatiga es cansado ", 0, null, "fatiga");
                        } else mTts.speak("Tu nivel de fatiga es normal ", 0, null, "fatiga");
                    }
                    //
                } else if (intent.getAction().equals(BROADCAST_ACTION_MOOD_MEASUREMENT)) {
                    booleanMoodMeasure = true;
                    mood = intent.getStringExtra(INTENT_KEY_MOOD_MEASUREMENT);
                    //
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textUltimaMedicion.setText("Animo: " + mood);

                        }
                    });
                    //PROMEDIO
                    stringPMood.add(mood);
                    doublePSpeed.add(Double.parseDouble(stringSpeed));
                    //
                    //RECOVER DATE AND HOUR
                    if (internet()) {
                        booleanMoodMeasure = false;
                        VolleyPetition("http://meddataa.sytes.net/registrovar/index.php?correo=" + stringEmail + "&usuario=" + stringID + "&var=" +
                                "Mood" + "&valor=" + mood + "&fecha=" + dates() + "&hora=" + hour() + "&velocidad=" + stringSpeed + "&diagnostico=" + "-");
                    } else guardarDatos();
                    //
                } else if (intent.getAction().equals(BROADCAST_ACTION_HR_MEASUREMENT)) {
                    booleanHrMeasure = true;
                    hr = intent.getStringExtra(INTENT_KEY_HR_MEASUREMENT);
                    //
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textUltimaMedicion.setText("Ritmo: " + hr);
                        }
                    });
                    //PROMEDIO
                    intPHr.add(Integer.parseInt(hr));
                    doublePSpeed.add(Double.parseDouble(stringSpeed));
                    //
                    if (internet()) {
                        booleanHrMeasure = false;
                        VolleyPetition("http://meddataa.sytes.net/registrovar/index.php?correo=" + stringEmail + "&usuario=" + stringID + "&var=" +
                                "HR" + "&valor=" + hr + "&fecha=" + dates() + "&hora=" + hour() + "&velocidad=" + stringSpeed + "&diagnostico=" +
                                hrDiagnostic(Integer.parseInt(hr)));
                    } else guardarDatos();
                    //SPEAK
                    if (booleanSonido){
                        mTts.speak("Tu ritmo cardiaco es " + hrDiagnostic(Integer.parseInt(hr)), 0, null, "hr");
                    }
                    //
                } else if (intent.getAction().equals(BROADCAST_ACTION_MEASUREMENT_WRITE_FAILURE)) {
                    String message = intent.getStringExtra(INTENT_MEASUREMENT_WRITE_FAILURE);
                    Toast.makeText(conduccion.this, message, Toast.LENGTH_LONG).show();
                    //
                } else if (intent.getAction().equals(BROADCAST_ACTION_STEPS_MEASUREMENT)) {
                    steps = intent.getStringExtra(INTENT_KEY_STEPS_MEASUREMENT);
                    if (!steps.isEmpty()){
                        textBle.setText(R.string.Conectado);
                        textBle.setTextColor(Color.parseColor("#90EE90"));
                    }
                } else if (intent.getAction().equals(BROADCAST_ACTION_HELO_DISCONNECTED)){
                    textBle.setText(getString(R.string.tConexionDesconectado));
                    textBle.setTextColor(Color.parseColor("#F44336"));
                    mTts.speak(getString(R.string.DispositivoDesconectado),0,null,"desconectado");
                    vibrator.vibrate(800);
                    countDownTimer.cancel();
                    timeWhenStopped = chronometer.getBase() - SystemClock.elapsedRealtime();
                    chronometer.stop();
                    switch (intCount){
                        case 1: countDownTimerHr.cancel(); break;
                        case 2: countDownTimerBp.cancel(); break;
                        case 3: countDownTimerBr.cancel(); break;
                        case 4: countDownTimerMF.cancel(); break;
                    }
                    //bluetoothAdapter.disable();
                    reconectar();
                    //
                    VolleyPetition("http://meddataa.sytes.net/registrovar/index.php?correo=" + stringEmail + "&usuario=" + stringID +
                            "&var=" + "DESCONEXION" + "&valor=" + "DESCONEXION" + "&fecha=" + dates() + "&hora=" + hour() + "&velocidad=" + "DESCONEXION"
                            + "&diagnostico=" +"DESCONEXION");
                    //
                } else if (intent.getAction().equals(BROADCAST_ACTION_HELO_CONNECTED)){
                    textBle.setText(R.string.Conectado);
                    textBle.setTextColor(Color.parseColor("#90EE90"));
                    startcountDownTimer();
                    startcountDownTimerHr();
                    handlerVolverMedir.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Connector.getInstance().measureMF();
                        }
                    },600);
                    chronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
                    chronometer.start();
                }else if (intent.getAction().equals(BROADCAST_ACTION_HELO_BONDED)){
                    Toast.makeText(context, "BONDED", Toast.LENGTH_LONG).show();
                }
                else if (intent.getAction().equals("enviado")){
                    Log.i("service123","envio mensaje");
                    Notification terminar = new Notification.Builder(getApplicationContext())
                            .setContentTitle("Administrador de cargas")
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentText("Datos guardados en la base de datos")
                            .setAutoCancel(true).build();
                    notificationManager.notify(1,terminar);
                    booleanServiceStarted = false;
                    contBp = contBr = contMood = contFatigue = contHr = 0;
                    SharedPreferences.Editor spMeasuresSavedEditor = spMeasuresSaved.edit();
                    spMeasuresSavedEditor.clear();
                    spMeasuresSavedEditor.apply();
                }
            }
        }
    }
    public void guardarDatos () {

        intentService = new Intent(conduccion.this,serviceInternet.class);
        startService(intentService);
        //
        Notification start = new Notification.Builder(this)
                .setContentTitle("Administrador de cargas")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText("Esperando conexión a internet para subir datos")
                .setAutoCancel(true).build();
        notificationManager.notify(0,start);

        SharedPreferences.Editor spMeasuresSavedEditor = spMeasuresSaved.edit();
        if (booleanBpMeasure) {
            snackbar = Snackbar.make(findViewById(R.id.conduccionL), "Presion guardada en el dispositivo", Snackbar.LENGTH_SHORT);
            snackbar.show();
            booleanBpMeasure = false;
            contBp++;
            //DATE AND HOUR SAVE
            stringDateBpSaved = dates();
            stringHourBpSaved= hour();
            //
            stringBpmaxSaved = "BPmax," + max + "," + stringDateBpSaved + "," + stringHourBpSaved + "," + stringSpeed + "," +
                    bpDiagnostic(Integer.parseInt(max), Integer.parseInt(min));
            stringBpminSaved = "BPmin," + min + "," + stringDateBpSaved + "," + stringHourBpSaved + "," + stringSpeed + "," +
                    bpDiagnostic(Integer.parseInt(max), Integer.parseInt(min));
            //
            //ENVIAR INFO A SHARED //
            spMeasuresSavedEditor.putInt("contBp",contBp);
            spMeasuresSavedEditor.putString("stringBpmaxSaved"+contBp, stringBpmaxSaved);
            spMeasuresSavedEditor.putString("stringBpminSaved"+contBp, stringBpminSaved);
            //
            spMeasuresSavedEditor.apply();
        }
        if (booleanBrMeasure){
            snackbar = Snackbar.make(findViewById(R.id.conduccionL), "Respiracion guardada en el dispositivo", Snackbar.LENGTH_SHORT);
            snackbar.show();
            booleanBrMeasure = false;
            contBr++;
            //DATE AND HOUR SAVE
            stringDateBrSaved = dates();
            stringHourBrSaved = hour();
            //
            stringBrSaved = "BR," + br + "," + stringDateBrSaved + "," + stringHourBrSaved + "," + stringSpeed + "," +
            brDiagnostic(Integer.parseInt(br));
            //ENVIAR INFO A SHARED //
            spMeasuresSavedEditor.putInt("contBr",contBr);
            spMeasuresSavedEditor.putString("stringBrSaved"+contBr, stringBrSaved);
            //
            spMeasuresSavedEditor.apply();
        }
        if (booleanMoodMeasure){
            snackbar = Snackbar.make(findViewById(R.id.conduccionL), "Humor guardado en el dispositivo", Snackbar.LENGTH_SHORT);
            snackbar.show();
            booleanMoodMeasure = false;
            contMood++;
            //DATE AND HOUR SAVE
            stringDateMoodSaved = dates();
            stringHourMoodSaved = hour();
            //
            stringMoodSaved = "Mood," + mood + "," + stringDateMoodSaved + "," + stringHourMoodSaved + "," + stringSpeed + "," + "MOOD";
            // ENVIAR INFO A SHARED //
            spMeasuresSavedEditor.putInt("contMood",contMood);
            spMeasuresSavedEditor.putString("stringMoodSaved"+contMood,stringMoodSaved);
            //
            spMeasuresSavedEditor.apply();
        }
        if (booleanFatigueMeasure){
            snackbar = Snackbar.make(findViewById(R.id.conduccionL), "Fatiga guardada en el dispositivo", Snackbar.LENGTH_SHORT);
            snackbar.show();
            booleanFatigueMeasure = false;
            contFatigue++;
            //DATE AND HOUR SAVE
            stringDateFatigueSaved = dates();
            stringHourFatigueSaved = hour();
            //
            stringFatigueSaved = "Fatiga," + fatigue + "," + stringDateFatigueSaved + "," + stringHourFatigueSaved + "," + stringSpeed + "," + "FATIGA";
            // ENVIAR INFO A SHARED //
            spMeasuresSavedEditor.putInt("contFatigue",contFatigue);
            spMeasuresSavedEditor.putString("stringFatigueSaved"+contFatigue,stringFatigueSaved);
            //
            spMeasuresSavedEditor.apply();
        }
        if (booleanHrMeasure){
            snackbar = Snackbar.make(findViewById(R.id.conduccionL), "Ritmo guardado en el dispositivo", Snackbar.LENGTH_SHORT);
            snackbar.show();
            booleanHrMeasure = false;
            contHr++;
            //DATE AND HOUR SAVE
            stringDateHrSaved = dates();
            stringHourHrSaved = hour();
            //
            stringHrSaved = "HR," + hr + "," + stringDateHrSaved + "," + stringHourHrSaved + "," + stringSpeed +
                    "," + hrDiagnostic(Integer.parseInt(hr));
            // ENVIAR INFO A SHARED //
            spMeasuresSavedEditor.putInt("contHr",contHr);
            spMeasuresSavedEditor.putString("stringHrSaved"+contHr,stringHrSaved);
            //
            spMeasuresSavedEditor.apply();

        }
    }


    //DIAGNOSTICOS
    ///HR
    protected String hrDiagnostic(int intMeasureHr){
        //StringdiagnosticHr = "Normal";
        if (spLogin.getString("gender","No gender").equalsIgnoreCase("Masculino")) {
            doubleMaxHr = 203.7 / (1 + Math.exp(0.033 * (age() - 104.3)));
        }
        if (spLogin.getString("gender","No gender").equalsIgnoreCase("Femenino")) {
            doubleMaxHr = 190.2 / (1 + Math.exp(0.0453 * (age() - 107.5)));
        }
        doubleMinHr = -5.4*((double)(spLogin.getInt("exInt",1)))+69.2;
        //
        //OUT OF RANGE
        if (intMeasureHr < doubleMinHr || intMeasureHr > doubleMaxHr){
            if (intMeasureHr < doubleMinHr){
                stringDiagnosticHr = "BRADICARDIA";
            }
            if (intMeasureHr > doubleMaxHr){
                stringDiagnosticHr = "TAQUICARDIA";
            }
        }
        //NORMAL RANGE
        if (intMeasureHr <= doubleMaxHr-90 && intMeasureHr >= doubleMinHr+10 ){
            stringDiagnosticHr = "VALOR NORMAL";
        }
        //BRADICARDIA
        if (intMeasureHr > doubleMinHr && intMeasureHr < doubleMinHr+10){
            stringDiagnosticHr = "LIGERA BRADICARDIA";
        }
        //TAQUICARDIA
        if (intMeasureHr < doubleMaxHr && intMeasureHr > doubleMaxHr-90){
            stringDiagnosticHr = "LIGERA TAQUICARDIA";
        }
        //
        return stringDiagnosticHr;
    }
    //
    //BR
    protected String brDiagnostic(int intMeasureBr){
        if (age() >= 18){
            intNormalBrmin = 12;
            intNormalBrmax = 20;
        }
        //HIGH NORMAL BREATH RATE
        if (intMeasureBr <= intNormalBrmax && intMeasureBr >= intNormalBrmax-1){
            stringDiagnosticBr = "FRECUENCIA RESPIRATORIA ALTA NORMAL";
        }
        //NORMAL BREATH RATE
        if (intMeasureBr <= intNormalBrmax-2 && intMeasureBr >= intNormalBrmin+2){
            stringDiagnosticBr = "FRECUENCIA RESPIRATORIA NORMAL";
        }
        //LOW NORMAL BREATH RATE
        if (intMeasureBr <= intNormalBrmin+1 && intMeasureBr >= intNormalBrmin){
            stringDiagnosticBr = "FRECUENCIA RESPIRATORIA BAJA NORMAL";
        }
        //TACHYPNEA
        if (intMeasureBr > intNormalBrmax && intMeasureBr <= intNormalBrmax+5) {
            stringDiagnosticBr = "TAQUIPNEA LIGERA";
        }
        if (intMeasureBr >= intNormalBrmax+6){
            stringDiagnosticBr = "TAQUIPNEA SEVERA";
        }
        //BRADIPNEA
        if (intMeasureBr < intNormalBrmin && intMeasureBr >= intNormalBrmin-5){
            stringDiagnosticBr = "BRADIPNEA LIGERA";
        }
        if (intMeasureBr <= intNormalBrmin-6){
            stringDiagnosticBr = "BRADIPNEA SEVERA";
        }
        //
        return stringDiagnosticBr;
    }
    //
    //BP
    protected String bpDiagnostic(int intMeasureBpmax,int intMeasureBpmin){
        //CHECK BP SYSTOLIC VALUES
        if(20 <= age() && age()<= 24) {
            intNormalBpmax = 120;
            intNormalBpmin =79;
        }
        if (25 <= age() && age()<= 29){
            intNormalBpmax = 121;
            intNormalBpmin =80;
        }
        if (30 <= age() && age()<= 34){
            intNormalBpmax = 122;
            intNormalBpmin =81;
        }
        if (35 <= age() && age()<= 39){
            intNormalBpmax = 123;
            intNormalBpmin =82;
        }
        if (40 <= age() && age()<= 44){
            intNormalBpmax = 125;
            intNormalBpmin =83;
        }
        if (45 <= age() && age()<= 49){
            intNormalBpmax = 127;
            intNormalBpmin =84;
        }
        if (50 <= age() && age()<= 54){
            intNormalBpmax = 129;
            intNormalBpmin =85;
        }
        if (55 <= age() && age()<= 59){
            intNormalBpmax = 131;
            intNormalBpmin =86;
        }
        /////NORMAL VALUE//////////
        if(intMeasureBpmax >= intNormalBpmax-29 && intMeasureBpmax <= intNormalBpmax+19 &&
                intMeasureBpmin >= intNormalBpmin-19 && intMeasureBpmin <= intNormalBpmin+9) {
            //HIGH NORMAL VALUE
            if (intMeasureBpmax > intNormalBpmax && intMeasureBpmin > intNormalBpmin){
                stringDiagnosticBp = "ALTA PRESION NORMAL";
            }
            //NORMAL VALUE
            if (intMeasureBpmax == intNormalBpmax && intMeasureBpmin == intNormalBpmin){
                stringDiagnosticBp = "PRESION NORMAL";
            }
            //LOW NORMAL VALUE
            if (intMeasureBpmax < intNormalBpmax && intMeasureBpmin < intNormalBpmin){
                stringDiagnosticBp = "BAJA PRESION NORMAL";
            }
            if (intMeasureBpmax > intNormalBpmax && intMeasureBpmin < intNormalBpmin){
                if (intMeasureBpmax - intNormalBpmax > intNormalBpmin - intMeasureBpmin){
                    stringDiagnosticBp = "ALTA PRESION NORMAL";
                } else stringDiagnosticBp = "BAJA PRESION NORMAL";

            }
        }
        //////LOW VALUE///////
        if (intMeasureBpmax <= intNormalBpmax-30 || intMeasureBpmin <= intNormalBpmin-20){
            //DANGEROUSLY LOW VALUE
            if (intMeasureBpmax <= intNormalBpmax-70 || intMeasureBpmin <= intNormalBpmin-47){
                stringDiagnosticBp = "PRESION PELIGROSAMENTE BAJA";
            }
            //TOO LOW VALUE
            if (intMeasureBpmax <= intNormalBpmax-60 && intMeasureBpmax >= intNormalBpmax-69 ||
                    intMeasureBpmin <= intNormalBpmin-40 && intMeasureBpmin >= intNormalBpmin-46){
                stringDiagnosticBp = "PRESION MUY BAJA";
            }
            //BORDER LOW VALUE
            if (intMeasureBpmax <= intNormalBpmax-30 && intMeasureBpmax >= intNormalBpmax-59 ||
                    intMeasureBpmin <= intNormalBpmin-20 && intMeasureBpmin >= intNormalBpmin-39){
                stringDiagnosticBp = "LIMITE DE PRESION BAJA";
            }
        }
        //////HIGH VALUE/////
        if (intMeasureBpmax >= intNormalBpmax+20 && intMeasureBpmin >= intNormalBpmin+10){
            //STAGE 1
            if (intMeasureBpmax < intNormalBpmax+40 && intMeasureBpmax >= intNormalBpmax+20 ||
                    intMeasureBpmin <= intNormalBpmin+19 && intMeasureBpmin >= intNormalBpmin+10){
                stringDiagnosticBp = "HIPERTENSION: NIVEL 1";
            }
            //STAGE 2
            if (intMeasureBpmax < intNormalBpmax+60 && intMeasureBpmax >= intNormalBpmax+40 ||
                    intMeasureBpmin <= intNormalBpmin+29 && intMeasureBpmin >= intNormalBpmin+20){
                stringDiagnosticBp = "HIPERTENSION: NIVEL 2";
            }
            //STAGE 3
            if (intMeasureBpmax < intNormalBpmax+90 && intMeasureBpmax >= intNormalBpmax+60 ||
                    intMeasureBpmin <= intNormalBpmin+39 && intMeasureBpmin >= intNormalBpmin+30){
                stringDiagnosticBp = "HIPERTENSION NIVEL 3";
            }
            //STAGE 4
            if (intMeasureBpmax >= intNormalBpmax+90 || intMeasureBpmin >= intNormalBpmin+40){
                stringDiagnosticBp = "HIPERTENSION NIVEL 4";
            }
        }
        //ISOLATED VALUES
        //HIGH ISOLATED SYSTOLIC
        if (intMeasureBpmax >= intNormalBpmax+20 && intMeasureBpmax <= intNormalBpmax+39 &&
                intMeasureBpmin>= intNormalBpmin-19 &&intMeasureBpmin<= intNormalBpmin+9){
            stringDiagnosticBp = "HIPERTENSIÓN SISTOLICA AISLADA NIVEL 1";
        }
        if (intMeasureBpmax >= intNormalBpmax+40 && intMeasureBpmax <= intNormalBpmax+59 &&
                intMeasureBpmin>= intNormalBpmin-19 &&intMeasureBpmin<= intNormalBpmin+9){
            stringDiagnosticBp = "HIPERTENSION SISTOLICA AISLADA NIVEL 2";
        }
        if (intMeasureBpmax >= intNormalBpmax+60 && intMeasureBpmax <= intNormalBpmax+89 &&
                intMeasureBpmin>= intNormalBpmin-19 &&intMeasureBpmin<= intNormalBpmin+9){
            stringDiagnosticBp = "HIPERTENSION SISTOLICA AISLADA NIVEL 3";
        }
        if (intMeasureBpmax >= intNormalBpmax+90 && intMeasureBpmin>= intNormalBpmin-19 && intMeasureBpmin<= intNormalBpmin+9){
            stringDiagnosticBp = "HIPERTENSION SISTOLICA AISLADA NIVEL 4";
        }
        //LOW ISOLATED SYSTOLIC
        if (intMeasureBpmax <= intNormalBpmax-30 && intMeasureBpmax >= intNormalBpmax-59 &&
                intMeasureBpmin>= intNormalBpmin-19 &&intMeasureBpmin<= intNormalBpmin+9){
            stringDiagnosticBp = "LIMITE DE HIPOTENSION SISTOLICA AISLADA";
        }
        if (intMeasureBpmax <= intNormalBpmax-60 && intMeasureBpmax >= intNormalBpmax-69 &&
                intMeasureBpmin>= intNormalBpmin-19 &&intMeasureBpmin<= intNormalBpmin+9){
            stringDiagnosticBp = "HIPOTENSION SISTOLICA AISLADA MUY BAJA";
        }
        if (intMeasureBpmax <= intNormalBpmax-70  && intMeasureBpmin>= intNormalBpmin-19 &&
                intMeasureBpmin<= intNormalBpmin+9){
            stringDiagnosticBp = "HIPOTENSION SISTOLICA PELIGROSA";
        }
        //
        return stringDiagnosticBp;
    }
    //PETICIONES
    private void VolleyPetition(String URL) {
        Log.i("url", "" + URL);
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Snackbar snackbar = Snackbar.make(findViewById(R.id.conduccionL), "Guardado", Snackbar.LENGTH_LONG);
                snackbar.show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(conduccion.this, "Error!" , Toast.LENGTH_LONG).show();

            }
        });
        queue.add(stringRequest);
    }
    //

    //METODOS UTILES
    protected boolean internet() {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }


    //MENU 3 DOTS
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_dot_conduccion,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.acercaNos:
                Toast.makeText(this, "Xperto App", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.sonidoA:
                if (!booleanSonido){
                    booleanSonido = true;
                    Toast.makeText(this, R.string.SonidoActivado, Toast.LENGTH_SHORT).show();
                }
                else {
                    booleanSonido = false;
                    Toast.makeText(this, R.string.SonidoDesactivado, Toast.LENGTH_SHORT).show();
                }

            default:
                return super.onOptionsItemSelected(item);
        }

    }
    //END MENU 3 DOTS
    @Override
    public void onInit(int status) {
        mTts.setLanguage(Locale.getDefault());
    }
    public String dates(){
        date = Calendar.getInstance().getTime();
        sdfDate = new SimpleDateFormat("yyyy.MM.dd");
        stringDate = sdfDate.format(date);
        return stringDate;
    }
    public String hour(){
        date = Calendar.getInstance().getTime();
        sdfHour = new SimpleDateFormat("kk:mm:ss");
        stringHour = sdfHour.format(date);
        return stringHour;
    }
    public void chronometer(){
        chronometer.setFormat("H:MM:SS");
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long time = SystemClock.elapsedRealtime() - chronometer.getBase();
                int h = (int) (time / 3600000);
                int m = (int) (time - h * 3600000) / 60000;
                int s = (int) (time - h * 3600000 - m * 60000) / 1000;
                String t = (h < 10 ? "0" + h : h) + ":" + (m < 10 ? "0" + m : m) + ":" + (s < 10 ? "0" + s : s);
                chronometer.setText(t);
            }
        });
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
    }
    public void chronometerCancel(){
        chronometer.stop();
    }
    public int age() {
        //YEAR,MONTH,DAY
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy");
        String stringYear = sdfDate.format(date);
        sdfDate = new SimpleDateFormat("MM");
        String stringMonth = sdfDate.format(date);
        sdfDate = new SimpleDateFormat("dd");
        String stringDay = sdfDate.format(date);
        int intYearActual = Integer.parseInt(stringYear);
        int intMonthActual = Integer.parseInt(stringMonth);
        int intDayActual = Integer.parseInt(stringDay);
        //
        //BIRTH
        String stringBirth = spLogin.getString("birth","Birth");
        StringTokenizer tokenBirth = new StringTokenizer(stringBirth, "-");
        //TOKENS
        String tokenYear = tokenBirth.nextToken();
        String tokenMonth = tokenBirth.nextToken();
        String tokenDay = tokenBirth.nextToken();
        int year = Integer.parseInt(tokenYear);
        int month = Integer.parseInt(tokenMonth);
        int day = Integer.parseInt(tokenDay);
        //
        //CALCULATE AGE
        int ageYears = intYearActual - year;
        //
        if (intMonthActual-month < 0) {
            ageYears = ageYears-1;
            if(intDayActual-day< 0){
                ageYears = ageYears-1;
            }
        }

        return ageYears;
    }
    public void reconectar(){
        if (booleanSonido) {
            mTts.speak("Reconectando dispositivo", 0, null, "reconectando");
        }
        //bluetoothAdapter.enable();
        /*
        handlerApagar.postDelayed(new Runnable() {
            @Override
            public void run() {
                Connector.getInstance().scan(scanCallBack);
                Log.i("Reconectar","Scan called");
            }
        },600);*/
        Connector.getInstance().scan(this);
        Log.i("Reconectar","Scan called");
    }

    @Override
    public void onScanStarted() {
        handlerReconect.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i("Reconectar","Scan started");
                mTts.speak("Buscando el dispositivo",0,null,"buscando");
                textBle.setText(getString(R.string.Escaneando));
                textBle.setTextColor(Color.parseColor("#FFB74D"));
            }
        },700);
    }

    @Override
    public void onScanFinished() {
        Log.i("Reconectar","Scan finished");
    }

    @Override
    public void onLedeviceFound(final DeviceItem deviceItem) {
        Log.i("Reconectar","Device found");
        textBle.setText(getString(R.string.Encontrado));
        textBle.setTextColor(Color.parseColor("#FFB74D"));
        mTts.speak("Dispositivo encontrado",0,null,"encontrado");
        this.deviceItem = deviceItem;
        handlerReconect.postDelayed(new Runnable() {
            @Override
            public void run() {
                Connector.getInstance().connect(deviceItem);
            }
        },700);
    }

    @Override
    public void onPairedDeviceNotFound() {
        mTts.speak("No se encuentra el dispositivo",0,null,"noencontrado");
    }



    //
    @Override
    protected void onDestroy() {
        super.onDestroy();
       unregisterReceiver(heloMeasurementReceiver);
       mTts.shutdown();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }

}
