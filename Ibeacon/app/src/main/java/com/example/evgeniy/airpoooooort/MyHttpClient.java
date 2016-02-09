package com.example.evgeniy.airpoooooort;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.*;
import cz.msebera.android.httpclient.Header;

/**
 * Created by Evgeniy on 02.02.2016.
 */
public class MyHttpClient {

    private static final String TAG = "MyHttpClient";
    private static final String SERVER = "http://85.143.221.58:8000/";
    private TextView tvContent;
    private ImageView image;

    public MyHttpClient() {
    }

    public void setTv(TextView tvContent) {
        this.tvContent = tvContent;
    }

    public void setImage(ImageView image) {
        this.image = image;
    }

    public void getRequest(String id) { // id - "major:minor" beacons
        AsyncHttpClient client = new AsyncHttpClient(); // создаём новый асинкхронный хттп клиент.
        client.get(SERVER + id, new AsyncHttpResponseHandler() { // отправляем гет запрос и получаем:

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) { // если запрос прошёл успешно: статус код, заголовки и тело. Надо бы дописать там проверочные параметры и всё такое, но это уже детали. Если мы згнаем, что в ответе придёт картинка, то можно смело парсить её.
                String allHeaders = "";
                for (Header header : headers)
                    allHeaders += header.getName() + " : " + header.getValue() + "\n"; // просто все хедеры в 1 переменну для логирования
                Bitmap bmp = BitmapFactory.decodeByteArray(response, 0, response.length); // вот тут мы и декодим байт массив в битмап
                image.setImageBitmap(bmp); // сетится картинка из битмапов и сразу выводиться на главные экран.
                tvContent.setText("statusCode: " + statusCode); // ставим статус код. Можно заменять на что угодно.
                Log.d(TAG, "statusCode: " + statusCode + "\n"
                        + "header[] : " + allHeaders + "\n");
                /*
                Можно даже несколько запросов слать. в 1 получаем картинку,
                 в другом текст и тому подобное.
                 */
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                /*
                По хорошему, это тоже хендлить надо. Он спросит про это, мол а если всё упало или сервер недоступен, то что?
                то выводит, что недоступен пока что. Можешь картинку присобачить.
                 */
                tvContent.setText("statusCode: " + statusCode +"\n GET FAILURE"); // STUB
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }

}
