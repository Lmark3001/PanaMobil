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
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private EditText field_ip;
    private EditText field_port;

    private Button button_1;
    private TextView result_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        field_ip = findViewById(R.id.field_ip);     //ищем в дизайне и назначаем на переменной
        field_port = findViewById(R.id.field_port);
        button_1 = findViewById(R.id.button_1);
        result_info = findViewById(R.id.result_info);


        //////обработчики событий
        button_1.setOnClickListener(new View.OnClickListener(){   // дклает колбэк при нажатии на элемент, выделяем память под обьект
            @Override
            public void onClick(View view) {
                if(field_ip.getText().toString().trim().equals("")){
                   Toast.makeText(MainActivity.this,"192.168.0.118", Toast.LENGTH_LONG).show();
                    field_ip.setText("192.168.0.118");
                }
                if(field_port.getText().toString().trim().equals("")){
                    Toast.makeText(MainActivity.this,"8888", Toast.LENGTH_LONG).show();
                    field_port.setText("8888");
                }
                else {
                    String ip = field_ip.getText().toString();
                    String port = field_port.getText().toString();
                    //////////TODO: сделал до сюда.......

                    new GetData().execute(field_ip,field_port); // Запускаем в бэкграунде методы из УРЛ дата
                }
            }
        });
    }
    private class GetData extends AsyncTask<String, String, String>{         // Наследуем в этот класс, что он выполняется в бэкграунде
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
                e.printStackTrace()

                ;
            }

        }


    }

    }
