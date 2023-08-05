package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private EditText user_field;
    private Button button_1;
    private TextView result_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user_field = findViewById(R.id.user_field);     //ищем в дизайне и назначаем на переменной
        button_1 = findViewById(R.id.button_1);
        result_info = findViewById(R.id.result_info);


        //////обработчики событий
        button_1.setOnClickListener(new View.OnClickListener(){   // дклает колбэк при нажатии на элемент, выделяем память под обьект
            @Override
            public void onClick(View view) {
                if(user_field.getText().toString().trim().equals("")){
                   Toast.makeText(MainActivity.this,R.string.no_user_input, Toast.LENGTH_LONG).show();
                    user_field.setText("192.168.0.118");
                }
                else {
                    String city = user_field.getText().toString();
                    String key = "7448490b32ba7215d482c530708fd677";
                    String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + key +"&units=metric&lang=ru";

                    new GetURLData().execute(url); // Запускаем в бэкграунде методы из УРЛ дата
                }
            }
        });
    }
    private class GetURLData extends AsyncTask<String, String, String>{         // Наследуем в этот класс, что он выполняется в бэкграунде
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            result_info.setText("Ожидайте...");
        }

        @SuppressLint("SuspiciousIndentation")
        @Override
        protected String doInBackground(String... strings) {                    // то что выпоняем в бэкграунде.
            HttpsURLConnection connection = null;                               // cоздаем обьект
            BufferedReader reader = null;                                       // буфер для чтения ответа


            try {
                URL url = new URL(strings[0]);                                    // фомируем урл из полученого аргумента из doInBackground
                connection = (HttpsURLConnection)url.openConnection();              // открываем соединение и возврашаем приведенный результат в перерменную
                connection.connect();                                               //соединяемся  (объект HttpsURLConnection connection)
                InputStream stream = connection.getInputStream();                    // создаем поток для считывания как для файла
                reader =  new BufferedReader(new InputStreamReader(stream));          // с помощью buffer reader мы можем более детально считать всю информацию
                                                                                    // в конструктор идет поток инпурт стрим с HttpsURLConnection connection
                StringBuffer buffer = new StringBuffer();                           //нужен стринг, удобно получать с помощью стринг баффер
                String line = "";

                while ((line = reader.readLine())!=null)                            // читаем по линии пока не конец
                    buffer.append(line).append("\n");                               // добавляем в конец линии считанную линию и перехо на новую строку

                return buffer.toString();                                           //возвращаем принятое

            } catch (MalformedURLException e) {  //в случае уродливого УРЛ бросаем исключкния
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(connection !=null)  {connection.disconnect();}    //проверяем и закрываем соединения
                    try {
                        if(reader!=null)
                           reader.close();             /// закрываем считывание
                    } catch (IOException e) {
                       e.printStackTrace();
                    }
                }
            return null;
            }
        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result){   //Выводим результат
            super.onPostExecute(result);
            try {
                JSONObject json_weather_pack = new JSONObject(result);  // Загоняем как JSON полученныЙ результат
              // ToDO: поставить проверку на адекватность пакета
                // ToDo: пораскидать значения по столюцам
                //

                result_info.setText("Т" +  json_weather_pack.getJSONObject("main").getDouble("temp") + "\n" +
                                    "T ощ. " +  json_weather_pack.getJSONObject("main").getDouble("feels_like") + "\n" +
                                    "Влажность" +  json_weather_pack.getJSONObject("main").getDouble("humidity") + "\n" +
                                    "Ветер " +  json_weather_pack.getJSONObject("wind").getDouble("speed"));

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


    }

    }
